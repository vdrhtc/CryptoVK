package model.preview;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.VKPerson;

import org.json.JSONObject;

public class ChatPreviewModel {
	
	public ChatPreviewModel() {
		
	}

	public ChatPreviewModel(int chatId, Boolean read, String title,
			long lastMessageId, String[] chatIconURL, String lastMessage,
			Date lastMessageDate, VKPerson lastMessageSender,
			ArrayList<VKPerson> interlocutors) {
		super();
		this.chatId = chatId;
		this.read = read;
		this.title = title;
		this.lastMessageId = lastMessageId;
		this.chatIconURL = chatIconURL;
		this.lastMessage = lastMessage;
		this.lastMessageDate = lastMessageDate;
		this.lastMessageSender = lastMessageSender;
		this.interlocutors = interlocutors;
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
		lastMessageId = content.getLong("id");
	}

	private void extractLastMessageAndSenderAndReadState(JSONObject content) {
		read = content.getInt("read_state") == 1 ? true : false;
		lastMessage = content.getString("body");
		if (content.getInt("out") !=1 )
			lastMessageSender = VKPerson.getKnownPerson(content.getInt("user_id"));
		else
			lastMessageSender = VKPerson.getOwner();
	}

	private void extractDate(JSONObject content) {
		this.lastMessageDate = new Date(content.getLong("date") * 1000);
	}
	
	public void update(JSONObject content) {
		read = content.getInt("read_state") == 1 ? true : false;
		
		if(lastMessageId != content.getLong("id")) {
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
	

	@Override
	public String toString() {
		return "ChatEntry [title=" + getTitle() + ", read=" + read
				+ ", lastMessageDate=" + lastMessageDate + "]";
	}


	private int chatId;
	private Boolean read;
	private String title;
	private long lastMessageId = -1;
	private String[] chatIconURL;
	private String lastMessage;
	private Date lastMessageDate;
	private VKPerson lastMessageSender;
	private ArrayList<VKPerson> interlocutors = new ArrayList<>();

	private static Logger log = Logger.getAnonymousLogger();
	static {
		getLog().setLevel(Level.SEVERE);
	}
	
	public Boolean isRead() {
		return read;
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
		if (lastMessageDate == null) {
			if (other.lastMessageDate != null)
				return false;
		} else if (!lastMessageDate.equals(other.lastMessageDate))
			return false;
		if (read == null) {
			if (other.read != null)
				return false;
		} else if (!read.equals(other.read))
			return false;
		return true;
	}
	
	@Override
	public ChatPreviewModel clone() {
		return new ChatPreviewModel(chatId,read, title, lastMessageId, chatIconURL,
				lastMessage, lastMessageDate, lastMessageSender, interlocutors);
	}

	public String getTitle() { 
		return title; 
	}

	public String getLastMessageDate() {
		return DateFormat.getInstance().format(lastMessageDate);
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

}
