package model.preview;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.VKPerson;
import model.messaging.ChatModel;

import org.json.JSONObject;

public class ChatPreviewModel {
	
	public ChatPreviewModel() {	}

	public ChatPreviewModel(int chatId, Boolean read, String title,
			long lastMessageId, String[] chatIconURL, String lastMessage,
			Date lastMessageDate, VKPerson lastMessageSender,
			ArrayList<VKPerson> interlocutors) {
		super();
		this.setChatId(chatId);
		this.setRead(read);
		this.setTitle(title);
		this.setLastMessageId(lastMessageId);
		this.setChatIconURL(chatIconURL);
		this.setLastMessage(lastMessage);
		this.setLastMessageDate(lastMessageDate);
		this.setLastMessageSender(lastMessageSender);
		this.setInterlocutors(interlocutors);
	}
	
	public static ChatPreviewModel getChatEntry(JSONObject content) {
		return  isDialog(content) ? new DialogPreviewModel() : new TalkPreviewModel();
	}

	public void loadContent(JSONObject content) {
		getLog().info(content.toString());
		extractDate(content);
		extractLastMessageId(content);
		extractLastMessageAndSenderAndReadState(content);
	}

	private void extractLastMessageId(JSONObject content) {
		setLastMessageId(content.getLong("id"));
	}

	private void extractLastMessageAndSenderAndReadState(JSONObject content) {
		setRead(content.getInt("read_state") == 1 ? true : false);
		setLastMessage(content.getString("body"));
		if (content.getInt("out") !=1 )
			setLastMessageSender(VKPerson.getKnownPerson(content.getInt("user_id")));
		else
			setLastMessageSender(VKPerson.getOwner());
	}

	private void extractDate(JSONObject content) {
		this.setLastMessageDate(new Date(content.getLong("date") * 1000));
	}
	
	public void update(JSONObject content) {
		setRead(content.getInt("read_state") == 1 ? true : false);
		
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
	
	public ChatModel buildFullModel() {
		return new ChatModel(this);
	}
	
	@Override
	public String toString() {
		return "ChatEntry [title=" + getTitle() + ", read=" + getRead()
				+ ", lastMessageDate=" + getLastMessageDate() + "]";
	}

	private int chatId;
	private Boolean read;
	private String title;
	private String lastMessage;
	private Date lastMessageDate;
	private String[] chatIconURL;
	private long lastMessageId = -1;
	private VKPerson lastMessageSender;
	private ArrayList<VKPerson> interlocutors = new ArrayList<>();

	private static Logger log = Logger.getAnonymousLogger();
	static {
		getLog().setLevel(Level.SEVERE);
	}
	
	public Boolean isRead() {
		return getRead();
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
		if (getRead() == null) {
			if (other.getRead() != null)
				return false;
		} else if (!getRead().equals(other.getRead()))
			return false;
		return true;
	}
	
	@Override
	public ChatPreviewModel clone() {
		return new ChatPreviewModel(getChatId(),getRead(), getTitle(), getLastMessageId(), getChatIconURL(),
				getLastMessage(), getLastMessageDate(), getLastMessageSender(), getInterlocutors());
	}

	public String getTitle() { 
		return title; 
	}

	public String getLastMessageDateString() {
		return DateFormat.getInstance().format(getLastMessageDate());
	}

	public String[] getChatIconURL() {
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

	public void setChatIconURL(String... chatIconURL) {
		this.chatIconURL = chatIconURL;
	}

	protected Boolean getRead() {
		return read;
	}

	private void setRead(Boolean read) {
		this.read = read;
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
