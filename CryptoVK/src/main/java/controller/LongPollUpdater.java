package controller;

import org.json.JSONArray;
import org.json.JSONObject;

import http.ConnectionOperator;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Updated;

public class LongPollUpdater extends Service<Void> {

	public LongPollUpdater(Updated model, Updated view) {
		this.updatedModel = model;
		this.updatedView = view;
	}

	public String getName() {
		return "";
	}

	private boolean needsUpdating(JSONObject updates) {

		JSONArray updatesList = updates.getJSONArray("updates");
		if (updatesList.length() == 0)
			return false;

		for (int i = 0; i < updatesList.length(); i++) {
			int updateType = updatesList.getJSONArray(i).getInt(0);
			if (!(updateType == 9 || updateType == 8 || updateType == 1 || updateType == 2 || updateType == 3
					|| updateType == 80))
				return true;
		}
		return false;
	}

	@Override
	protected Task<Void> createTask() {
		Task<Void> updaterTask = new Task<Void>() {

			@Override
			protected Void call() throws InterruptedException {
				Thread.currentThread().setName(getName());
				JSONObject longPollServerData = auxCO.getLongPollServer();

				while (!isCancelled()) {
					if (isWorking.getValue()) {

						JSONObject updates = CO.getUpdates(longPollServerData.getString("server"),
								longPollServerData.getString("key"), longPollServerData.getLong("ts"));

						longPollServerData.put("ts", updates.getLong("ts"));
						if (needsUpdating(updates)) {

							updatedModel.getLock("");
							updatedModel.update(updates);
							updatedModel.releaseLock("");

							isWorking.setValue(false);
							Platform.runLater(() -> {
								updatedView.getLock("Updater");
								updatedView.update();
								updatedView.releaseLock("Updater");
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

	private Updated updatedModel;
	private Updated updatedView;
	private ConnectionOperator CO = new ConnectionOperator(30000);
	private ConnectionOperator auxCO = new ConnectionOperator(1000);

	private BooleanProperty isWorking = new SimpleBooleanProperty();

}
