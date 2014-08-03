package model;

import http.ConnectionOperator;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;

public class Chats {
	
	public final int PRE_LOADED_ENTRIES = 20;
	public Lock lock = new ReentrantLock();
	
	public void initializeEntries() {
		JSONArray chatsJSONs = ConnectionOperator.getDialogs(PRE_LOADED_ENTRIES, 0);
		for (int i = 0; i<PRE_LOADED_ENTRIES; i++) {
			ChatEntry entry = new ChatEntry();
			entry.loadContent(chatsJSONs.getJSONObject(i).getJSONObject("message"));
			getChats().add(entry);
		}
	}
	
	public void loadNewEntries(int count) {
		JSONArray chatsJSONs = ConnectionOperator.getDialogs(count, getChats().size());
		int offset = getChats().size();
		for (int i = offset; i<offset+count; i++) {
			ChatEntry entry = new ChatEntry();
			entry.loadContent(chatsJSONs.getJSONObject(i-offset).getJSONObject("message"));
			getChats().add(entry);
		}
	}
	
	public void updateEntries() {
		JSONArray chatsJSONs = ConnectionOperator.getDialogs(getChats().size(), 0);
		int i = 0;
		updateOrder(chatsJSONs);
		for (ChatEntry e : chats) {
			e.update(chatsJSONs.getJSONObject(i).getJSONObject("message"));
			i++;
		}
	}
	
	
	private void updateOrder(JSONArray chatsJSONs) {
		for (int i = 0; i<chatsJSONs.length(); i++) 
			for (int j = 0; j<chats.size(); j++) 
				if (chats.get(j).isContentCorresponding(chatsJSONs.getJSONObject(i).getJSONObject("message")) && j != i)
					chats.add(i, chats.remove(j));
	}

	public ArrayList<ChatEntry> getChats() {
		return chats;
	}
	
	public void getNextChats(int offset, int count) {
		if(offset+count >= getChats().size())
			loadNewEntries(count);
	}
	

	private ArrayList<ChatEntry> chats = new ArrayList<>();



}
