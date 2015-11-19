package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import data.ReadStatesDatabase;
import data.ReadStatesDatabase.ReadState;

public class ChatPreviewModel {

	public ChatPreviewModel() {
	}

	public ChatPreviewModel(int chatId, ReadState readState, String title, ArrayList<String> chatIconURL,
			MessageModel lastMessage, ArrayList<VKPerson> interlocutors) {
		this.chatId = chatId;
		this.lastMessage = lastMessage;
		this.title = title;
		this.chatIconURL = chatIconURL;
		this.interlocutors = interlocutors;
		this.setReadState(readState);
	}

	public void loadContent(JSONObject content) {
		lastMessage = new MessageModel(content);
		chatId = lastMessage.getChatId();

		setOrRecallReadState(content.getInt("read_state") == 1 ? ReadState.READ
				: content.getInt("out") == 1 ? ReadState.VIEWED : ReadState.UNREAD);
		extractChatIcon();
	}

	public void update(JSONObject content) {
		setOrRecallReadState(content.getInt("read_state") == 1 ? ReadState.READ
				: content.getInt("out") == 1 ? ReadState.VIEWED : ReadState.UNREAD);

		if (lastMessage.getId() != content.getLong("id")) {
			System.out.println("Updating " + toString());
			this.loadContent(content);
		}
	}

	private void setOrRecallReadState(ReadState RS) {
		JSONObject state = ReadStatesDatabase.optChatJSON(chatId);
		if (state == null)
			setReadState(RS);
		else if (state.getInt("lastMessageId") != lastMessage.getId())
			setReadState(RS);
		else if (RS == ReadState.READ)
			setReadState(ReadState.READ);
		else
			setReadState(ReadState.valueOf(state.getString("readState")));
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

	protected int chatId;
	protected ReadState RS;
	protected String title;
	protected MessageModel lastMessage;
	protected ArrayList<String> chatIconURL = new ArrayList<>();
	protected ArrayList<VKPerson> interlocutors = new ArrayList<>();

	protected static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.ALL);
	}

	@Override
	public ChatPreviewModel clone() {
		return new ChatPreviewModel(getChatId(), getReadState(), getTitle(), getChatIconURL(), getLastMessage(),
				getInterlocutors());
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

	public int getChatId() {
		return chatId;
	}


	public ReadState getReadState() {
		return RS;
	}

	public void setReadState(ReadState RS) {
		this.RS = RS;
		ReadStatesDatabase.put(chatId, lastMessage.getId(), getReadState());
	}
	
}
