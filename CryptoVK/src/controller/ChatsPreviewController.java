package controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import view.preview.ChatsPreview;

public class ChatsPreviewController {

	public ChatsPreviewController(ChatsPreview CPV) {
		this.controlled = CPV;
		this.controlled.getChatsContainer().setOnScroll(scrollHandler);
		
		this.updater = new ChatsPreviewUpdater(controlled);
		
		this.controlled.canBeUpdated().addListener((ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) -> {
			if (newValue)
				updater.isWorkingProperty().setValue(true);
				updater.start();});
	}
	


	public EventHandler<ScrollEvent> scrollHandler = new EventHandler<ScrollEvent>() {
		
		@Override
		public void handle(ScrollEvent event) {
			double max = controlled.getChatsContainer().getVmax();
			double min = controlled.getChatsContainer().getVmin();
			double delta = controlled.getEntryHeight()
					/ controlled.getChatsLayout().getHeight();
			double newVvalue = controlled.getChatsContainer().getVvalue()
					- Math.signum(event.getDeltaY()) * delta;

			if (newVvalue >= max
					&& event.getDeltaY() < 0
					&& (loader.getState() == State.SUCCEEDED || loader
							.getState() == State.READY)) {

				loader.reset();
				controlled.getProgressBar().progressProperty().set(-1);
				controlled.getStatusMessage().setText("Loading...");
				loader.start();
			}

			if (newVvalue >= max || newVvalue <= min)
				controlled.getChatsContainer().setVvalue(
						newVvalue >= max ? max : min);
			else
				controlled.getChatsContainer().setVvalue(newVvalue);
		}
	};

	private ChatsPreview controlled;
	private ChatsPreviewUpdater updater;
	private LoaderService loader = new LoaderService();
	
	
	private class LoaderService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			Task<Void> loadMoreChatEntries = new Task<Void>() {
				@Override
				protected Void call() {
					controlled.getModel().getLock(); 
					try {
					controlled.getModel().getNextChats(controlled.getChatEntriesCount(), ChatsPreview.LOAD_NEW_COUNT);
					} catch (Exception e ){
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
					controlled.getProgressBar().setProgress(0);
					controlled.getModel().getLock(); 
					controlled.loadNewEntries(ChatsPreview.LOAD_NEW_COUNT);
					controlled.getModel().releaseLock(); 
					
					controlled.getProgressBar().setProgress(1);
					controlled.getStatusMessage().setText("Ready");
					});
			}
		};
	}
}
