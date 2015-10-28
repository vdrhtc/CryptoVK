package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import data.DataOperator;
import data.ReadStatesDatabase;
import model.MessageModel.ReadState;

public class ChatPreviewModel {
	
	public ChatPreviewModel() {	}

	public ChatPreviewModel(int chatId, ReadState readState, String title,
			long lastMessageId, ArrayList<String> chatIconURL, String lastMessage,
			Date lastMessageDate, VKPerson lastMessageSender,
			ArrayList<VKPerson> interlocutors) {
		this.setChatId(chatId);
		this.setReadState(readState);
		this.setTitle(title);
		this.setLastMessageId(lastMessageId);
		this.setChatIconURL(chatIconURL);
		this.setLastMessage(lastMessage);
		this.setLastMessageDate(lastMessageDate);
		this.setLastMessageSender(lastMessageSender);
		this.setInterlocutors(interlocutors);
	}
	
	public void loadContent(JSONObject content) {
		getLog().info(content.toString());
		extractDate(content);
		extractLastMessageId(content);
		extractLastMessageAndSenderAndReadState(content);
		extractChatIcon();
	}

	private void extractLastMessageId(JSONObject content) {
		setLastMessageId(content.getLong("id"));
	}

	private void extractLastMessageAndSenderAndReadState(JSONObject content) {
		setReadState(content.getInt("read_state") == 1 ? true : false);
		setLastMessage(content.getString("body"));
		if (content.getInt("out") !=1 )
			setLastMessageSender(VKPerson.getKnownPerson(content.getInt("user_id")));
		else
			setLastMessageSender(VKPerson.getOwner());
	}

	private void extractDate(JSONObject content) {
		this.setLastMessageDate(new Date(content.getLong("date") * 1000));
	}
	
	public void addInterlocutor(JSONObject user) {
		interlocutors.add(new VKPerson(user));
	}
	
	public void update(JSONObject content) {
		setReadState(content.getInt("read_state") == 1 ? true : false);
		
		if(getLastMessageId() != content.getLong("id")) {
			System.out.println("Updating "+toString());
			this.loadContent(content);
		}
	}
	
	public boolean isContentCorresponding(JSONObject content) {
		return true;
	}
	
	public static boolean isDialog(JSONObject content) {
		return content.optInt("chat_id") == 0 ? true : false;
	}
	
	private void extractChatIcon() {
		if(chatIconURL.size() > 0)
			return;
		
		int min = Math.min(4, getInterlocutors().size());
		ArrayList<String> urls = new ArrayList<>(min);
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < getInterlocutors().size(); i++)
			indices.add(i);
		java.util.Collections.shuffle(indices, new Random(System.currentTimeMillis()));
		for (int i = 0; i < min; i++)
			urls.add(getInterlocutors().get(indices.get(i)).getPhotoURL());
		setChatIconURL(urls);
	}
	
	
	@Override
	public String toString() {
		return "ChatEntry [title=" + getTitle() + ", read=" + getReadState()
				+ ", lastMessageDate=" + getLastMessageDate() + "]";
	}

	private int chatId;
	private ReadState RS;
	private String title;
	private String lastMessage;
	private Date lastMessageDate;
	private long lastMessageId = -1;
	private VKPerson lastMessageSender;
	private ArrayList<String> chatIconURL = new ArrayList<>();
	private ArrayList<VKPerson> interlocutors = new ArrayList<>();

	private static Logger log = Logger.getAnonymousLogger();
	static {
		getLog().setLevel(Level.SEVERE);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ChatPreviewModel))
			return false;
		ChatPreviewModel other = (ChatPreviewModel) obj;
		if (getChatId() != other.getChatId())
			return false;
		if (getLastMessageDate() == null) {
			if (other.getLastMessageDate() != null)
				return false;
		} else if (!getLastMessageDate().equals(other.getLastMessageDate()))
			return false;
		if (getReadState() == null) {
			if (other.getReadState() != null)
				return false;
		} else if (!getReadState().equals(other.getReadState()))
			return false;
		return true;
	}
	
	@Override
	public ChatPreviewModel clone() {
		return new ChatPreviewModel(getChatId(),getReadState(), getTitle(), getLastMessageId(), getChatIconURL(),
				getLastMessage(), getLastMessageDate(), getLastMessageSender(), getInterlocutors());
	}

	public String getTitle() { 
		return title; 
	}

	public String getLastMessageDateString() {
		return DataOperator.formatDate(lastMessageDate);
	}

	public ArrayList<String> getChatIconURL() {
		return chatIconURL;
	}
	
	public String getLastMessage() {
		return lastMessage;
	}
	
	public VKPerson getLastMessageSender() {
		return lastMessageSender;
	}

	public ArrayList<VKPerson> getInterlocutors() {
		return interlocutors;
	}

	public void setInterlocutors(ArrayList<VKPerson> interlocutors) {
		this.interlocutors = interlocutors;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		ChatPreviewModel.log = log;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public void setChatIconURL(ArrayList<String> chatIconURL) {
		this.chatIconURL = chatIconURL;
	}

	public ReadState getReadState() {
		return RS;
	}

	public void setReadState(ReadState RS) {
		this.RS = RS;
	}
	
	public void setReadState(Boolean read) {
		if (read==false) {
			JSONObject state = ReadStatesDatabase.get(chatId);
			if (state!=null)
				if (state.getInt("lastMessageId") == lastMessageId)
					setReadState(ReadState.valueOf(state.getString("readState")));
				else
					setReadState(ReadState.UNREAD);
		}
	}

	protected long getLastMessageId() {
		return lastMessageId;
	}

	private void setLastMessageId(long lastMessageId) {
		this.lastMessageId = lastMessageId;
	}

	private void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	protected Date getLastMessageDate() {
		return lastMessageDate;
	}

	private void setLastMessageDate(Date lastMessageDate) {
		this.lastMessageDate = lastMessageDate;
	}

	private void setLastMessageSender(VKPerson lastMessageSender) {
		this.lastMessageSender = lastMessageSender;
	}

}
