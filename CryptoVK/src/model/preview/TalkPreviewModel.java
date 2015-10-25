package model.preview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import http.ConnectionOperator;
import model.VKPerson;
import model.messaging.TalkModel;

public class TalkPreviewModel extends ChatPreviewModel {
	
	public TalkPreviewModel() {	}

	public TalkPreviewModel(int chatId, Boolean read, String title, long lastMessageId, String[] chatIconURL,
			String lastMessage, Date lastMessageDate, VKPerson lastMessageSender, ArrayList<VKPerson> interlocutors) {
		super(chatId, read, title, lastMessageId, chatIconURL, lastMessage, lastMessageDate, lastMessageSender, interlocutors);
	}


	private String deletedImageURL = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcRdEp0TNPk3cGnvN0IBtMZsw9-td381BgxHGoqEuiy8Afgn9qtc";

	
	@Override
	public void loadContent(JSONObject content) {
		extractTalkTitle(content);
		extractTalkId(content);
		JSONObject chatInfo = extractTalkInterlocutors(content);
		extractTalkIcon(chatInfo);
		super.loadContent(content);
	}
	
	@Override
	public TalkModel buildFullModel() {
		return new TalkModel(this);
	}
	
	
	private JSONObject extractTalkInterlocutors(JSONObject content) {
		this.setInterlocutors(new ArrayList<VKPerson>());
		JSONObject chatInfo = ConnectionOperator.getChat(content
				.getInt("chat_id"));
		if (chatInfo.optInt("left")==1) {
			chatInfo = new JSONObject();
			chatInfo.put("photo_50", deletedImageURL);
			getTitle().concat(" [Потрачено] ");
			return chatInfo;
		}
		JSONArray interlocutors = chatInfo.getJSONArray("users");
		for (int i = 0; i < interlocutors.length(); i++) {
			this.getInterlocutors()
					.add(new VKPerson(interlocutors.getJSONObject(i)));
		}
		return chatInfo;
	}
	

	private void extractTalkIcon(JSONObject chatInfo) {
		if (chatInfo.optString("photo_50").equals("")) {
			int min = Math.min(4, getInterlocutors().size());
			String[] urls = new String[min];
			List<Integer> indices = new ArrayList<>();
			for(int i = 0; i < getInterlocutors().size(); i++)
				indices.add(i);
			java.util.Collections.shuffle(indices, new Random(System.currentTimeMillis()));
			for(int i = 0; i < min; i++)
				urls[i] = getInterlocutors().get(indices.get(i)).getPhotoURL();
			setChatIconURL(urls);
		} 
		else 
			this.setChatIconURL(chatInfo.getString("photo_50"));
	}

	
	private void extractTalkId(JSONObject content) {
		this.setChatId(content.getInt("chat_id"));
	}
	
	private void extractTalkTitle(JSONObject content) {
		this.setTitle(content.getString("title"));
	}
	
	@Override
	public TalkPreviewModel clone() {
		
		return new TalkPreviewModel(getChatId(),getRead(), getTitle(), getLastMessageId(), getChatIconURL(),
				getLastMessage(), getLastMessageDate(), getLastMessageSender(), getInterlocutors());
		
	}

	@Override
	public boolean isContentCorresponding(JSONObject content) {
		if (!isDialog(content) && content.getInt("chat_id") == getChatId())
			return true;
		return false;
	}
}
