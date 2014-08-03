package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Chats;

public class ChatsUpdater extends Service<Void> {

	public ChatsUpdater(Chats updated) {
		this.updated = updated;
	}
	
	@Override
	protected Task<Void> createTask() {
		Task<Void> updaterTask = new Task<Void>() {

			@Override
			protected Void call() throws InterruptedException {
				Thread.currentThread().setName("Updater");
				while(!isCancelled()) {
					if(isWorking.getValue()) {
						updated.lock.lock();
						updated.updateEntries();
						updated.lock.unlock();
						
						isWorking.setValue(false);
						Thread.sleep(5000);
					}
				}
				return null;
			}
		};
		updaterTask.setOnFailed(t -> {exceptionProperty().get().printStackTrace(); this.restart();});
		return updaterTask;
	}
	
	
	
	public BooleanProperty isWorkingProperty() {
		return isWorking;
	}
	
	private BooleanProperty isWorking = new SimpleBooleanProperty();

	private Chats updated;
}
