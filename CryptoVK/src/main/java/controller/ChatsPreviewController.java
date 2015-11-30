package controller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import data.DataOperator;
import data.ReadStatesDatabase;
import http.ConnectionOperator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import model.ChatPreviewModel;
import model.ChatsPreviewModel;
import model.DialogPreviewModel;
import model.TalkPreviewModel;
import model.VKPerson;
import view.ChatPreview;
import view.ChatsPreview;
import view.View.ViewName;

public class ChatsPreviewController implements Controller {

	public ChatsPreviewController() {

		this.controlled = new ChatsPreview();
		this.controlled.getChatsContainer().setOnScroll(scrollHandler);

		this.updater = new ChatsPreviewLPU(controlled);

		this.controlled.canBeUpdated()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					if (newValue)
						updater.isWorkingProperty().setValue(true);
					updater.start();
				});
	}

	private void addReadStateChangeListener(ChatPreviewController CPVC) {
		CPVC.getReadStateWithIdProperty().addListener(new ChangeListener<ChatReadStateWithId>() {
			public void changed(ObservableValue<? extends ChatReadStateWithId> observable, ChatReadStateWithId oldValue,
					ChatReadStateWithId newValue) {
				// System.out.println("==== Preview read state changed, " +
				// newValue.toString());
				readStateWithIdProperty.setValue(newValue);
				controlled.getUnreadMessagesCounter().setText(ReadStatesDatabase.getUnreadCounter().toString());
			}
		});
	}

	@Override
	public void prepareViewForSwitch(Object... params) {
		if (controlled.getChatsLayout().getChildren().size() == 0) {
			controlled.getLastSeenOnline().setText(
					"You were last seen online on " + DataOperator.formatDate(VKPerson.getOwner().getLastSeenOnline()));
			loadNextModels(ChatsPreviewModel.PRE_LOADED_ENTRIES);
			loadNextPreviews(ChatsPreview.CHATS_PER_PAGE);
			controlled.getUnreadMessagesCounter().setText(controlled.getModel().getUnreadMessagesCount().toString());
			controlled.canBeUpdated().setValue(true);
		}
	}

	public void loadNextModels(int count) {
		JSONArray chatsJSONs = CO.getDialogs(count, controlled.getModel().getChats().size());
		count = chatsJSONs.length() < count ? chatsJSONs.length() : count;
		int offset = controlled.getModel().getChats().size();
		for (int i = offset; i < offset + count; i++) {
			JSONObject content = chatsJSONs.getJSONObject(i - offset).getJSONObject("message");
			ChatPreviewModel entry;
			if (content.optInt("chat_id") == 0)
				entry = new DialogPreviewModel();
			else
				entry = new TalkPreviewModel();

			entry.loadContent(content);
			controlled.getModel().getChats().add(entry);
		}
	}

	public void loadNextPreviews(int count) {
		ArrayList<Node> toAppend = new ArrayList<>();
		int oldChatEntriesCount = controlled.getChatsLayout().getChildren().size() / 2;
		int totalLoadedModelsCount = controlled.getModel().getChats().size();
		count = oldChatEntriesCount + count > totalLoadedModelsCount ? totalLoadedModelsCount - oldChatEntriesCount
				: count;
		for (int i = oldChatEntriesCount; i < oldChatEntriesCount + count; i++) {
			ChatPreviewController newController = new ChatPreviewController(controlled.getModel().getChats().get(i));
			addReadStateChangeListener(newController);

			controlled.getPreviews().add(newController.getControlled());

			toAppend.add(newController.getControlled().getRoot());
			toAppend.add(controlled.buildHBorder());

		}
		controlled.getChatsLayout().getChildren().addAll(toAppend);
	}

	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}

	public EventHandler<ScrollEvent> scrollHandler = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {
			double max = controlled.getChatsContainer().getVmax();
			double min = controlled.getChatsContainer().getVmin();
			double delta = 1.0 / ChatsPreview.CHATS_PER_PAGE;
			double newVvalue = controlled.getChatsContainer().getVvalue() - Math.signum(event.getDeltaY()) * delta;

			if (newVvalue >= max && event.getDeltaY() < 0
					&& (loader.getState() == State.SUCCEEDED || loader.getState() == State.READY)) {

				loader.reset();
				controlled.getProgressBar().progressProperty().set(-1);
				controlled.getStatusMessage().setText("Loading...");
				loader.start();
			}

			if (newVvalue >= max || newVvalue <= min)
				controlled.getChatsContainer().setVvalue(newVvalue >= max ? max : min);
			else
				controlled.getChatsContainer().setVvalue(newVvalue);
		}
	};

	public ChatPreview getPreviewById(Long chatId) {
		for (ChatPreview CP : this.controlled.getPreviews()) {
			System.out.println("Checking chat " + CP.getModel().getChatId());
			if (CP.getModel().getChatId().equals(chatId))
				return CP;
		}
		System.out.println("Not found chat " + chatId);
		return null;
	}

	private ChatsPreview controlled;
	private ChatsPreviewLPU updater;
	private ObjectProperty<ChatReadStateWithId> readStateWithIdProperty = new SimpleObjectProperty<>();
	private ConnectionOperator CO = ChatsPreviewModel.getConnectionOperator();
	private LoaderService loader = new LoaderService();

	public ChatsPreview getControlled() {
		return controlled;
	}

	public ObjectProperty<ChatReadStateWithId> getReadStateWithIdProperty() {
		return readStateWithIdProperty;
	}

	private class LoaderService extends Service<Void> {
		protected Task<Void> createTask() {
			Task<Void> loadNextModels = new Task<Void>() {
				protected Void call() {
					Thread.currentThread().setName("Preview scroll loader");
					controlled.getModel().getLock("");
					if (controlled.getChatsLayout().getChildren().size() / 2 + ChatsPreview.LOAD_NEW_COUNT >= controlled
							.getModel().getChats().size())
						loadNextModels(ChatsPreview.LOAD_NEW_COUNT);
					controlled.getModel().releaseLock("");

					Platform.runLater(() -> {
						controlled.getModel().getLock("ChatsPreviewController.handleScroll");
						loadNextPreviews(ChatsPreview.LOAD_NEW_COUNT);
						controlled.getModel().releaseLock("ChatsPreviewController.handleScroll");

						controlled.getProgressBar().setProgress(1);
						controlled.getStatusMessage().setText("Ready");
					});
					return null;
				}

				@Override
				protected void failed() {
					getException().printStackTrace();
				}
			};
			return loadNextModels;
		}
	}
}
