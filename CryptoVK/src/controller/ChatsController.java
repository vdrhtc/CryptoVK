package controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import view.chats.ChatsView;

public class ChatsController {

	public ChatsController(ChatsView CV) {
		this.controlled = CV;
		this.updater = new ChatsUpdater(controlled.getModel());
		this.updater.isWorkingProperty().addListener(
				(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) -> {
						Platform.runLater(() -> {
							controlled.getModel().lock.lock(); 
							if (!newValue)
								controlled.update();
							controlled.getModel().lock.unlock(); 
							updater.isWorkingProperty().setValue(true);
							});
					}
		);
		this.controlled.getReadyForUpdates().addListener((ObservableValue<? extends Boolean> observable,
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

	private ChatsView controlled;
	private ChatsUpdater updater;
	private LoadService loader = new LoadService();
	
	
	private class LoadService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			Task<Void> loadMoreChatEntries = new Task<Void>() {
				@Override
				protected Void call() {
					controlled.getModel().lock.lock(); 
					controlled.getModel().getNextChats(controlled.getChatEntriesCount(), ChatsView.LOAD_NEW_COUNT);
					controlled.getModel().lock.unlock();; 
					return null;
				}
			};
			loadMoreChatEntries.setOnSucceeded(entryLoadResultHandler);
			return loadMoreChatEntries;
		}

		private EventHandler<WorkerStateEvent> entryLoadResultHandler = new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				controlled.getModel().lock.lock(); 
				controlled.appendNewEntries(ChatsView.LOAD_NEW_COUNT);
				controlled.getModel().lock.unlock(); 
				
				controlled.getProgressBar().setProgress(1);
				controlled.getStatusMessage().setText("Ready");
			}
		};
	}
}
