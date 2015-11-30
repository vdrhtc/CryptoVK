package model;

import java.util.ArrayList;
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

		ArrayList<Long> alreadyUpdatedChatIds = new ArrayList<>();
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
			if (alreadyUpdatedChatIds.contains(chatId))
				continue;

			ChatModel CM = chatModels.get(chatId);
			if (CM != null) {
				CM.getLock("ChatModel.update");
				CM.update();
				System.out.println("Updated " + CM.toString());
				CM.releaseLock("ChatModel.update");
			}
			alreadyUpdatedChatIds.add(chatId);
		}
	}

	private HashMap<Long, ChatModel> chatModels = new HashMap<>();

	public HashMap<Long, ChatModel> getChatModels() {
		return chatModels;
	}

	@Override
	public void getLock(String takerName) {
		log.info("Getting lock: "+Thread.currentThread().getName()+" "+takerName);
		lock.lock();
		log.info("Got lock: "+Thread.currentThread().getName()+" "+takerName);
	}
	
	@Override
	public void releaseLock(String takerName) {
		lock.unlock();
		log.info("Releasing lock: "+Thread.currentThread().getName()+" "+takerName);

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
