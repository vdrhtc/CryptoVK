package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatModel implements Updated {
	
	public static final int LOAD_NEW_COUNT = 4;
	public static final int INIT_LOAD_COUNT = 10;
	
	private Lock lock = new ReentrantLock();

	
	public ChatModel(int chatId, String[] chatIconUrl, String chatTitle) {
		this.chatId = chatId;
		this.chatIconURL = chatIconUrl;
		this.chatTitle = chatTitle;
	}
	
	public ChatModel(int chatId, String chatTitle, String[] chatIconURL,
			ArrayList<MessageModel> loadedMessages, Integer serverMessageCount) {
		this.chatId = chatId;
		this.chatTitle = chatTitle;
		this.chatIconURL = chatIconURL;
		this.loadedMessages = loadedMessages;
		this.serverMessageCount = serverMessageCount;
	}
	
	public void loadMessages(int count, int offset) {
		
		JSONObject chatHistory = formAndSendRequest(count, offset);
		serverMessageCount = chatHistory.getInt("count");

		JSONArray messageContents = chatHistory.getJSONArray("items");

		for (int i = 0; i < messageContents.length(); i++)
			this.getLoadedMessages().add(new MessageModel(messageContents.getJSONObject(i)));
	}
	
	public JSONObject formAndSendRequest(int count, int offset) {
		return null;
	}
	
	@Override
	public void update() {
		int currentLoadedMessagesCount = loadedMessages.size();
		JSONObject newChatHistory = formAndSendRequest(currentLoadedMessagesCount, 0);
		serverMessageCount = newChatHistory.getInt("count");
		for (int i = 0; i< currentLoadedMessagesCount; i++) 
			loadedMessages.set(i, new MessageModel(newChatHistory.getJSONArray("items").getJSONObject(i)));
	}
	
	
	public void getLock() {
		lock.lock();
	}
	
	public void releaseLock() {
		lock.unlock();
	}

	private int chatId;
	private String chatTitle;
	private String[] chatIconURL;
	private Integer serverMessageCount;
	private ArrayList<MessageModel> loadedMessages = new ArrayList<>();


	
	public Integer getServerMessageCount () {
		return serverMessageCount;
	}
	
	public void setServerMessageCount (int serverMessageCount) {
		this.serverMessageCount = serverMessageCount;
	}
	
	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public String[] getChatIconURL() {
		return chatIconURL;
	}

	public void setChatIconURL(String[] chatIconURL) {
		this.chatIconURL = chatIconURL;
	}

	public ArrayList<MessageModel> getLoadedMessages() {
		return loadedMessages;
	}

	public void setLoadedMessages(ArrayList<MessageModel> loadedMessages) {
		this.loadedMessages = loadedMessages;
	}


	public String getChatTitle() {
		return chatTitle;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(chatIconURL);
		result = prime * result + chatId;
		result = prime * result + ((chatTitle == null) ? 0 : chatTitle.hashCode());
		result = prime * result + ((loadedMessages == null) ? 0 : loadedMessages.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ChatModel)) {
			return false;
		}
		ChatModel other = (ChatModel) obj;
		if (!Arrays.equals(chatIconURL, other.chatIconURL)) {
			return false;
		}
		if (chatId != other.chatId) {
			return false;
		}
		if (chatTitle == null) {
			if (other.chatTitle != null) {
				return false;
			}
		} else if (!chatTitle.equals(other.chatTitle)) {
			return false;
		}
		if (loadedMessages == null) {
			if (other.loadedMessages != null) {
				return false;
			}
		} else if (!loadedMessages.equals(other.loadedMessages)) {
			return false;
		}
		return true;
	}

	@Override
	public ChatModel clone() {
		return new ChatModel(chatId, chatTitle, chatIconURL, loadedMessages, serverMessageCount);
	}

	public int getInterlocutorId() {
		return 0;
	}





}
