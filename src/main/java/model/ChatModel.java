package model;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.ReadStatesDatabase;
import data.ReadStatesDatabase.ChatReadState;

public class ChatModel implements Updated {
	
	public static final int LOAD_NEW_COUNT = 4;
	public static final int INIT_LOAD_COUNT = 10;
	
	private Lock lock = new ReentrantLock();

	
	public ChatModel(Long chatId, ArrayList<String> chatIconUrl, String chatTitle, ChatReadState RS) {
		this.chatId = chatId;
		this.chatIconURL = chatIconUrl;
		this.chatTitle = chatTitle;
		this.RS = RS;
	}
	
	public ChatModel(Long chatId, String chatTitle, ArrayList<String> chatIconURL,
			ArrayList<MessageModel> loadedMessages, Integer serverMessageCount, ChatReadState RS) {
		this.RS = RS;
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
	public void update(Object... params) {
		int currentLoadedMessagesCount = loadedMessages.size();
		JSONObject newChatHistory = formAndSendRequest(currentLoadedMessagesCount, 0);
		serverMessageCount = newChatHistory.getInt("count");
		for (int i = 0; i< currentLoadedMessagesCount; i++) 
			loadedMessages.set(i, new MessageModel(newChatHistory.getJSONArray("items").getJSONObject(i)));
	}
	
	@Override
	public void getLock(String takerName) {
		log.info("Getting lock: "+takerName);
		lock.lock();
		log.info("Got lock: "+takerName);
	}
	
	@Override
	public void releaseLock(String takerName) {
		lock.unlock();
		log.info("Releasing lock: "+takerName);

	}

	private Long chatId;
	private String chatTitle;
	private ArrayList<String> chatIconURL;
	private Integer serverMessageCount;
	private ArrayList<MessageModel> loadedMessages = new ArrayList<>();
	private ChatReadState RS;
	
	private static Logger log = LoggerFactory.getLogger(ChatModel.class);

	public ArrayList<String> getChatIconURL() {
		return chatIconURL;
	}

	public void setChatIconURL(ArrayList<String> chatIconURL) {
		this.chatIconURL = chatIconURL;
	}
	
	public Integer getServerMessageCount () {
		return serverMessageCount;
	}
	
	public void setServerMessageCount (int serverMessageCount) {
		this.serverMessageCount = serverMessageCount;
	}
	
	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
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


	public Long getInterlocutorId() {
		return null;
	}

	public void setReadState(ChatReadState RS) { 
		this.RS = RS;
		ReadStatesDatabase.putChat(chatId, loadedMessages.get(0).getId(), !loadedMessages.get(0).isIncoming(), RS);
	}

	public ChatReadState getReadState() {
		return RS;
	}
	
	@Override
	public String toString() {
		return "ChatModel [chatId=" + chatId + ", chatTitle=" + chatTitle + ", RS=" + RS + "]";
	}
}
