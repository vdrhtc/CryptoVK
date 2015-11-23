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
			Long chatId = null;
			if (updatesList.getJSONArray(i).getInt(0) <= 4) {
				message = CO.getMesageById(updatesList.getJSONArray(i).getInt(1));
				chatId = message.optInt("chat_id") == 0 ? message.getLong("user_id") : message.getLong("chat_id");
			} else if (updatesList.getJSONArray(i).getInt(0) == 6 || updatesList.getJSONArray(i).getInt(0) == 7) {
				chatId = updatesList.getJSONArray(i).getLong(1);
				if (chatId > 2000000000)
					chatId = chatId-2000000000;
			} else {
				continue;
			}

			ChatModel CM = chatModels.get(chatId.intValue());
			if (CM != null) {
				System.out.println("Updating " + CM.toString());
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
