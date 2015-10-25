package controller;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import view.View.ViewName;
import view.preview.ChatsPreview;

public class ChatsPreviewController implements Controller {

	public ChatsPreviewController() {

		this.controlled = new ChatsPreview();
		this.controlled.getChatsContainer().setOnScroll(scrollHandler);

		this.updater = new ChatsPreviewUpdater(controlled);

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

		controlled.getModel().initializeEntries();
		loadNewEntries(ChatsPreview.CHATS_PER_PAGE);
		controlled.canBeUpdated().setValue(true);

	}

	public void loadNewEntries(int count) {
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

	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}

	public EventHandler<ScrollEvent> scrollHandler = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {
			double max = controlled.getChatsContainer().getVmax();
			double min = controlled.getChatsContainer().getVmin();
			double delta = 1.0/ChatsPreview.CHATS_PER_PAGE;
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
	private ChatsPreviewUpdater updater;
	private LoaderService loader = new LoaderService();

	public ChatsPreview getControlled() {
		return controlled;
	}

	private class LoaderService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			Task<Void> loadMoreChatEntries = new Task<Void>() {
				@Override
				protected Void call() {
					controlled.getModel().getLock();
					try {
						controlled.getModel().getNextChats(controlled.getPreviewsCount(), ChatsPreview.LOAD_NEW_COUNT);
					} catch (Exception e) {
						e.printStackTrace();
					}
					controlled.getModel().releaseLock();
					return null;
				}
			};
			loadMoreChatEntries.setOnSucceeded(entryLoadResultHandler);
			return loadMoreChatEntries;
		}

		private EventHandler<WorkerStateEvent> entryLoadResultHandler = new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

				Platform.runLater(() -> {
					controlled.getModel().getLock();
					loadNewEntries(ChatsPreview.LOAD_NEW_COUNT);
					controlled.getModel().releaseLock();

					controlled.getProgressBar().setProgress(1);
					controlled.getStatusMessage().setText("Ready");
				});
			}
		};
	}

}
