package controller;

import http.ConnectionOperator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.preview.ChatsPreviewModel;

import org.json.JSONObject;

import view.preview.ChatsPreview;

public class ChatsPreviewUpdater extends Service<Void> {

	public ChatsPreviewUpdater(ChatsPreview updated) {
		this.updatedModel = updated.getModel();
		this.updatedView = updated;
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
							
							updatedModel.getLock();
							updatedModel.updateEntries();
							updatedModel.releaseLock();

							isWorking.setValue(false);
							Platform.runLater(() -> {
								updatedModel.getLock(); 
								updatedView.update();
								updatedModel.releaseLock(); 
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

	private ChatsPreviewModel updatedModel;
	private ChatsPreview updatedView;

	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.ALL);
	}
}
