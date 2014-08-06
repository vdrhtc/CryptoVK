package model;

import http.ConnectionOperator;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class Chats {

	public final int PRE_LOADED_ENTRIES = 20;
	private Lock lock = new ReentrantLock();

	public void initializeEntries() {
		JSONArray chatsJSONs = ConnectionOperator.getDialogs(
				PRE_LOADED_ENTRIES, 0);
		for (int i = 0; i < PRE_LOADED_ENTRIES; i++) {
			JSONObject content = chatsJSONs.getJSONObject(i).getJSONObject(
					"message");
			ChatEntry entry = ChatEntry.getChatEntry(content);
			entry.loadContent(content);
			getChats().add(entry);
		}
	}

	public void loadNewEntries(int count) {
		JSONArray chatsJSONs = ConnectionOperator.getDialogs(count, getChats()
				.size());
		int offset = getChats().size();
		for (int i = offset; i < offset + count; i++) {
			JSONObject content = chatsJSONs.getJSONObject(i - offset)
					.getJSONObject("message");
			ChatEntry entry = ChatEntry.getChatEntry(content);
			entry.loadContent(content);
			getChats().add(entry);
		}
	}

	public void updateEntries() {
		JSONArray chatsJSONs = ConnectionOperator.getDialogs(getChats().size(),
				0);
		int i = 0;
		updateOrder(chatsJSONs);
		for (ChatEntry e : chats) {
			e.update(chatsJSONs.getJSONObject(i).getJSONObject("message"));
			i++;
		}
	}

	private void updateOrder(JSONArray chatsJSONs) {
		for (int i = 0; i < chatsJSONs.length(); i++) {
			boolean matchFound = false;
			for (int j = 0; j < chats.size(); j++) 
				if (chats.get(j).isContentCorresponding(
						chatsJSONs.getJSONObject(i).getJSONObject("message"))) {
					matchFound = true;
					if (j != i) {
						chats.add(i, chats.remove(j));
						break;
					}
				}
			if (!matchFound)
				chats.add(0, new ChatEntry());
		}
	}

	public ArrayList<ChatEntry> getChats() {
		return chats;
	}

	public void getNextChats(int offset, int count) {
		if (offset + count >= getChats().size())
			loadNewEntries(count);
	}

	public void releaseLock() {
		log.info("Releasing lock: "+ Thread.currentThread().getName());
		lock.unlock();
		
	}
	
	public void getLock() {
		log.info("Getting lock: "+ Thread.currentThread().getName());
		lock.lock();
		log.info("Got lock"+Thread.currentThread().getName());
	}

	private ArrayList<ChatEntry> chats = new ArrayList<>();
	
	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.ALL);
	}
}
