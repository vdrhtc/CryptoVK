package controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import view.chats.ChatsView;
import http.ConnectionOperator;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.chats.Chats;

public class ChatsUpdater extends Service<Void> {

	public ChatsUpdater(ChatsView updated) {
		this.updated = updated.getModel();
		this.updated2 = updated;
	}

	@Override
	protected Task<Void> createTask() {
		Task<Void> updaterTask = new Task<Void>() {

			@Override
			protected Void call() throws InterruptedException {
				Thread.currentThread().setName("Updater");
				JSONObject longPollServerData = ConnectionOperator
						.getLongPollServer();

				while (!isCancelled()) {
					if (isWorking.getValue()) {

						JSONObject updates = ConnectionOperator.getUpdates(
								longPollServerData.getString("server"),
								longPollServerData.getString("key"),
								longPollServerData.getLong("ts"));
						
						if (updates.getJSONArray("updates").length() != 0) {
							longPollServerData.put("ts", updates.getLong("ts"));
							
							updated.getLock();
							updated.updateEntries();
							updated.releaseLock();

							isWorking.setValue(false);
							Platform.runLater(() -> {
								updated.getLock(); 
								updated2.update();
								updated.releaseLock(); 
								isWorking.setValue(true);
								});
						}
					}
				}
				return null;
			}
		};
		updaterTask.setOnFailed(t -> {
			exceptionProperty().get().printStackTrace();
			this.restart();
		});
		return updaterTask;
	}

	public BooleanProperty isWorkingProperty() {
		return isWorking;
	}

	private BooleanProperty isWorking = new SimpleBooleanProperty();

	private Chats updated;
	private ChatsView updated2;

	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.ALL);
	}
}
