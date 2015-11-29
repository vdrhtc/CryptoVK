package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import data.ReadStatesDatabase;
import data.ReadStatesDatabase.ChatReadState;

public class ChatPreviewModel {

	public ChatPreviewModel() {
	}

	public ChatPreviewModel(Long chatId, ChatReadState chatReadState, String title, ArrayList<String> chatIconURL,
			MessageModel lastMessage, ArrayList<VKPerson> interlocutors) {
		this.chatId = chatId;
		this.lastMessage = lastMessage;
		this.title = title;
		this.chatIconURL = chatIconURL;
		this.interlocutors = interlocutors;
		this.setReadState(chatReadState);
	}

	public void loadContent(JSONObject content) {
		invalidated = true;
		lastMessage = new MessageModel(content);
		chatId = lastMessage.getChatId();
		setOrRecallReadState(content.getInt("read_state") == 1 ? ChatReadState.READ
				: content.getInt("out") == 1 ? ChatReadState.VIEWED : ChatReadState.UNREAD);
		extractChatIcon();
	}

	public void update(JSONObject content) {
		setOrRecallReadState(content.getInt("read_state") == 1 ? ChatReadState.READ
				: content.getInt("out") == 1 ? ChatReadState.VIEWED : ChatReadState.UNREAD);

		if (lastMessage.getId() != content.getLong("id")) {
			this.loadContent(content);
			System.out.println("Updated " + toString());
		}
	}

	private void setOrRecallReadState(ChatReadState RS) {
		JSONObject state = ReadStatesDatabase.optChatJSON(chatId);
		if (state == null)
			setReadState(RS);
		else if (state.getInt("lastMessageId") != lastMessage.getId())
			setReadState(RS);
		else if (RS == ChatReadState.READ)
			setReadState(ChatReadState.READ);
		else
			setReadState(ChatReadState.valueOf(state.getString("readState")));
	}

	public boolean isContentCorresponding(JSONObject content) {return false;}


	private void extractChatIcon() {
		if (chatIconURL.size() > 0)
			return;

		int min = Math.min(4, getInterlocutors().size());
		ArrayList<String> urls = new ArrayList<>(min);
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < getInterlocutors().size(); i++)
			indices.add(i);
		java.util.Collections.shuffle(indices, new Random(System.currentTimeMillis()));
		for (int i = 0; i < min; i++)
			urls.add(getInterlocutors().get(indices.get(i)).getPhotoURL());
		chatIconURL = urls;
	}

	protected Long chatId;
	protected ChatReadState RS;
	protected String title;
	protected MessageModel lastMessage;
	protected ArrayList<String> chatIconURL = new ArrayList<>();
	protected ArrayList<VKPerson> interlocutors = new ArrayList<>();
	protected Boolean invalidated;

	protected static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.ALL);
	}

	@Override
	public ChatPreviewModel clone() {
		return new ChatPreviewModel(getChatId(), getReadState(), getTitle(), getChatIconURL(), getLastMessage(),
				getInterlocutors());
	}
	
	public void setInvalited(Boolean value) {
		invalidated = value;
	}

	public Boolean isInvalidated() {
		return invalidated;
	}
	
	public String getTitle() {
		return title;
	}

	public String getLastMessageDateString() {
		return lastMessage.getDate();
	}

	public ArrayList<String> getChatIconURL() {
		return chatIconURL;
	}

	public MessageModel getLastMessage() {
		return lastMessage;
	}

	public VKPerson getLastMessageSender() {
		return lastMessage.getSender();
	}

	public ArrayList<VKPerson> getInterlocutors() {
		return interlocutors;
	}

	@Override
	public String toString() {
		return "ChatEntry [title=" + getTitle() + ", read=" + getReadState() + ", lastMessageDate="
				+ lastMessage.getDate() + "]";
	}

	public Long getChatId() {
		return chatId;
	}

	public ChatReadState getReadState() {
		return RS;
	}

	public void setReadState(ChatReadState RS) {
		this.RS = RS;
		ReadStatesDatabase.putChat(chatId, lastMessage.getId(), !lastMessage.isIncoming(), getReadState());
	}
	
}
