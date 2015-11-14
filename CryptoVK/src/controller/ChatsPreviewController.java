package controller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import data.DataOperator;
import http.ConnectionOperator;
import javafx.application.Platform;
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

	@Override
	public void prepareViewForSwitch(Object... params) {
		if (controlled.getChatsLayout().getChildren().size() > 0)
			return;

		controlled.getLastSeenOnline().setText(
				"You were last seen online on " + DataOperator.formatDate(VKPerson.getOwner().getLastSeenOnline()));
		loadNextModels(ChatsPreviewModel.PRE_LOADED_ENTRIES);
		loadNextPreviews(ChatsPreview.CHATS_PER_PAGE);
		controlled.canBeUpdated().setValue(true);

	}

	public void loadNextPreviews(int count) {
		ArrayList<Node> toAppend = new ArrayList<>();
		int oldChatEntriesCount = controlled.getChatsLayout().getChildren().size()/2;
		for (int i = oldChatEntriesCount; i < oldChatEntriesCount + count; i++) {
			ChatPreviewController newController = new ChatPreviewController(controlled.getModel().getChats().get(i));
			newController.getControlled().getRoot().prefHeightProperty()
					.bind(controlled.getRoot().heightProperty().divide(ChatsPreview.CHATS_PER_PAGE));

			controlled.getPreviews().add(newController.getControlled());
			toAppend.add(newController.getControlled().getRoot());
			toAppend.add(controlled.buildHBorder());

		}
		controlled.getChatsLayout().getChildren().addAll(toAppend);
	}

	public void loadNextModels(int count) {
		JSONArray chatsJSONs = CO.getDialogs(count, controlled.getModel().getChats().size());
		int offset = controlled.getModel().getChats().size();
		for (int i = offset; i < offset + count; i++) {
			JSONObject content = chatsJSONs.getJSONObject(i - offset).getJSONObject("message");
			ChatPreviewModel entry;
			if (ChatPreviewModel.isDialog(content))
				entry = new DialogPreviewModel();
			else
				entry = new TalkPreviewModel();

			entry.loadContent(content);
			controlled.getModel().getChats().add(entry);
		}
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

	public ChatPreview getPreviewById(int chatId) {
		for (ChatPreview CP : this.controlled.getPreviews()) {
			if (CP.getCurrentLoadedModel().getChatId() == chatId)
				return CP;
		}
		return null;
	}

	private ChatsPreview controlled;
	private ChatsPreviewLPU updater;
	private ConnectionOperator CO = ChatsPreviewModel.getConnectionOperator();
	private LoaderService loader = new LoaderService();

	public ChatsPreview getControlled() {
		return controlled;
	}

	private class LoaderService extends Service<Void> {
		protected Task<Void> createTask() {
			Task<Void> loadNextModels = new Task<Void>() {
				protected Void call() {
					controlled.getModel().getLock();
					if (controlled.getChatsLayout().getChildren().size()/2 + ChatsPreview.LOAD_NEW_COUNT >= controlled
							.getModel().getChats().size())
						loadNextModels(ChatsPreview.LOAD_NEW_COUNT);
					controlled.getModel().releaseLock();

					Platform.runLater(() -> {
						controlled.getModel().getLock();
						loadNextPreviews(ChatsPreview.LOAD_NEW_COUNT);
						controlled.getModel().releaseLock();

						controlled.getProgressBar().setProgress(1);
						controlled.getStatusMessage().setText("Ready");
					});
					return null;
				}
			};
			return loadNextModels;
		}
	}
}
