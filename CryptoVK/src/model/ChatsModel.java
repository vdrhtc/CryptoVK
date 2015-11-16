package model;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import http.ConnectionOperator;

public class ChatsModel implements Updated {

	@Override
	public void update(Object... params) {

		JSONObject updates = (JSONObject) params[0];
		JSONArray updatesList = updates.getJSONArray("updates");

		for (int i = 0; i < updatesList.length(); i++) {
			JSONObject message = null;
			if (updatesList.getJSONArray(i).getInt(0) <= 4) {
				message = CO.getMesageById(updatesList.getJSONArray(i).getInt(1));
			} else if (updatesList.getJSONArray(i).getInt(0) == 6 || updatesList.getJSONArray(i).getInt(0) == 7) {
				message = CO.getMesageById(updatesList.getJSONArray(i).getInt(2));

			} else {
				return;
			}
			Integer chatId = message.optInt("chat_id") == 0 ? message.getInt("user_id") : message.getInt("chat_id");

			ChatModel CM = chatModels.get(chatId);
			if (CM != null) {
				CM.getLock();
				CM.update();
				CM.releaseLock();
			}
		}
	}

	private HashMap<Integer, ChatModel> chatModels = new HashMap<>();

	public HashMap<Integer, ChatModel> getChatModels() {
		return chatModels;
	}

	@Override
	public void getLock() {
		log.info("Waiting for lock: " + Thread.currentThread().getName());
		lock.lock();
		log.info("Got lock: " + Thread.currentThread().getName());

	}

	@Override
	public void releaseLock() {
		log.info("Releasing lock: " + Thread.currentThread().getName());
		lock.unlock();

	}

	private Lock lock = new ReentrantLock();
	private static ConnectionOperator CO = new ConnectionOperator(1000);

	private static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.ALL);
	}

	public static ConnectionOperator getConnectionOperator() {
		return CO;
	}

}
