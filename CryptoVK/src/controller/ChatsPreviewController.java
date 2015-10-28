package controller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import http.ConnectionOperator;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import model.ChatPreviewModel;
import model.ChatsPreviewModel;
import model.DialogPreviewModel;
import model.TalkPreviewModel;
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
		if (controlled.getPreviewsCount() > 0) // TODO: WTF??
			return;

		loadNextModels(ChatsPreviewModel.PRE_LOADED_ENTRIES);
		;
		loadNextPreviews(ChatsPreview.CHATS_PER_PAGE);
		controlled.canBeUpdated().setValue(true);

	}

	public void loadNextPreviews(int count) {
		ArrayList<Node> toAppend = new ArrayList<>();
		int oldChatEntriesCount = controlled.getPreviewsCount();
		for (int i = oldChatEntriesCount; i < oldChatEntriesCount + count; i++) {
			controlled.setPreviewsCount(controlled.getPreviewsCount() + 1);
			toAppend.add(controlled.buildHBorder());
			ChatPreviewController newController = new ChatPreviewController(controlled.getModel().getChats().get(i));
			newController.getControlled().getRoot().prefHeightProperty()
					.bind(controlled.getRoot().heightProperty().divide(ChatsPreview.CHATS_PER_PAGE));

			controlled.getPreviews().add(newController.getControlled());
			toAppend.add(newController.getControlled().getRoot());

		}
		controlled.getChatsLayout().getChildren().addAll(toAppend);
	}

	public void loadNextModels(int count) {
		JSONArray chatsJSONs = ConnectionOperator.getDialogs(count, controlled.getModel().getChats().size());
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

	private ChatsPreview controlled;
	private ChatsPreviewLPU updater;
	private LoaderService loader = new LoaderService();

	public ChatsPreview getControlled() {
		return controlled;
	}

	private class LoaderService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			Task<Void> loadNextModels = new Task<Void>() {
				@Override
				protected Void call() {
					controlled.getModel().getLock();
					if (controlled.getPreviewsCount() + ChatsPreview.LOAD_NEW_COUNT >= controlled.getModel()
							.getChats().size())
						loadNextModels(ChatsPreview.LOAD_NEW_COUNT);
					controlled.getModel().releaseLock();
					return null;
				}
			};
			loadNextModels.setOnSucceeded(chatPreviewsLoader);
			return loadNextModels;
		}

		private EventHandler<WorkerStateEvent> chatPreviewsLoader = new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

				Platform.runLater(() -> {
					controlled.getModel().getLock();
					loadNextPreviews(ChatsPreview.LOAD_NEW_COUNT);
					controlled.getModel().releaseLock();

					controlled.getProgressBar().setProgress(1);
					controlled.getStatusMessage().setText("Ready");
				});
			}
		};
	}

}
