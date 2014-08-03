package model;

import http.ConnectionOperator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatEntry {

	public enum ChatType {
		DIALOG, TALK
	}
	
	private String deletedImageURL = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcRdEp0TNPk3cGnvN0IBtMZsw9-td381BgxHGoqEuiy8Afgn9qtc";
	
	public ChatEntry() {
		
	}
	
	public ChatEntry(int chatId, Boolean read, Date lastMessageDate) {
		this.chatId = chatId;
		this.read = read;
		this.lastMessageDate = lastMessageDate;
	}
	
	public static ChatEntry getChatEntry(JSONObject content) {
		return content.optInt("chat_id") == 0 ? new DialogEntry() : new TalkEntry();
	}

	public void loadContent(JSONObject content) {
		log.info(content.toString());
		extractChatType(content);
		if (this.type == ChatType.DIALOG) {
			extractDialogId(content);
			extractDialogTitle(content);
			extractDialogIcon();
		} else {
			extractTalkTitle(content);
			extractTalkId(content);
			JSONObject chatInfo = extractTalkInterlocutors(content);
			extractTalkIcon(chatInfo);
		}
		extractDate(content);
		extractLastMessageAndSenderAndReadState(content);
	}
	
	private void extractTalkId(JSONObject content) {
		this.chatId = content.getInt("chat_id");
	}

	private void extractDialogId(JSONObject content) {
		this.chatId = content.getInt("user_id");
	}

	private void extractLastMessageAndSenderAndReadState(JSONObject content) {
		read = content.getInt("read_state") == 1 ? true : false;
		lastMessage = content.getString("body");
		if (content.getInt("out") !=1 )
			lastMessageSender = VKPerson.getKnownPerson(content.getInt("user_id"));
		else
			lastMessageSender = VKPerson.getOwner();
	}
	
	private void extractDialogTitle(JSONObject content) {
		VKPerson interlocutor = VKPerson.getKnownPerson(content.getInt("user_id"));
		this.interlocutors.add(interlocutor);
		log.info(interlocutor.toString());
		this.title = interlocutor.getFirstName() + " "
				+ interlocutor.getLastName();
	}
	
	private void extractTalkTitle(JSONObject content) {
		this.title = content.getString("title");
	}

	private void extractChatType(JSONObject content) {
		if (content.optInt("chat_id") == 0)
			this.type = ChatType.DIALOG;
		else
			this.type = ChatType.TALK;
	}

	private void extractDate(JSONObject content) {
		this.lastMessageDate = new Date(content.getLong("date") * 1000);
	}
	
	private void extractDialogIcon() {
		this.chatIconURL = interlocutors.get(0).getPhotoURL();
	}

	private void extractTalkIcon(JSONObject chatInfo) {
		if (chatInfo.optString("photo_50").equals("")) {
			this.chatIconURL = this.interlocutors.get(
					new Random().nextInt(this.interlocutors.size()))
					.getPhotoURL();
		} 
		else 
			this.chatIconURL = chatInfo.getString("photo_50");
	}

	private JSONObject extractTalkInterlocutors(JSONObject content) {
		JSONObject chatInfo = ConnectionOperator.getChat(content
				.getInt("chat_id"));
		if (chatInfo == null) {
			chatInfo = new JSONObject();
			chatInfo.put("photo_50", deletedImageURL);
			this.title.concat(" [Потрачено]");
			return chatInfo;
		}
		JSONArray interlocutors = chatInfo.getJSONArray("users");
		for (int i = 0; i < interlocutors.length(); i++) {
			this.interlocutors
					.add(new VKPerson(interlocutors.getJSONObject(i)));
		}
		return chatInfo;
	}
	
	public void update(JSONObject content) {
		read = content.getInt("read_state") == 1 ? true : false;
		if(!lastMessageDate.equals(new Date(content.getLong("date")*1000))) {
			System.out.println("Updating "+toString());
			this.loadContent(content);
		}
	}
	
	public boolean isContentCorresponding(JSONObject content) {
		ChatType type = content.optInt("chat_id") == 0 ? ChatType.DIALOG : ChatType.TALK;
		if (type == this.type) {
			int id = type == ChatType.DIALOG ? content.getInt("user_id") : content.getInt("chat_id");
			if(id == this.chatId)
				return true;
		}
		return false;
	}
	

	@Override
	public String toString() {
		return "ChatEntry [title=" + title + ", read=" + read
				+ ", lastMessageDate=" + lastMessageDate + "]";
	}


	private int chatId;
	private String title;
	private Boolean read;
	private ChatType type;
	private String chatIconURL;
	private String lastMessage;
	private Date lastMessageDate;
	private VKPerson lastMessageSender;
	private ArrayList<VKPerson> interlocutors = new ArrayList<>();

	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.SEVERE);
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
		if (!(obj instanceof ChatEntry))
			return false;
		ChatEntry other = (ChatEntry) obj;
		if (chatId != other.chatId)
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
	public ChatEntry clone() {
		return new ChatEntry(chatId, read, lastMessageDate);
	}
	

	public String getTitle() { 
		return title; 
	}

	public String getLastMessageDate() {
		return DateFormat.getInstance().format(lastMessageDate);
	}

	public String getChatIconURL() {
		return chatIconURL;
	}
	
	public String getLastMessage() {
		return lastMessage;
	}
	
	public VKPerson getLastMessageSender() {
		return lastMessageSender;
	}

}
