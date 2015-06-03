package model.messaging;

import java.util.ArrayList;

import model.VKPerson;
import model.preview.ChatPreviewModel;

public class ChatModel {
	
	public ChatModel(ChatPreviewModel sourcePreviewModel) {
		this.chatId = sourcePreviewModel.getChatId();
		this.chatIconURL = sourcePreviewModel.getChatIconURL();
		this.interlocutors = sourcePreviewModel.getInterlocutors();
	}

	private int chatId;
	private String[] chatIconURL;
	private ArrayList<VKPerson> interlocutors = new ArrayList<>();

	private ArrayList<MessageModel> loadedMessages = new ArrayList<>();

	
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

	public ArrayList<VKPerson> getInterlocutors() {
		return interlocutors;
	}

	public void setInterlocutors(ArrayList<VKPerson> interlocutors) {
		this.interlocutors = interlocutors;
	}

	public ArrayList<MessageModel> getLoadedMessages() {
		return loadedMessages;
	}

	public void setLoadedMessages(ArrayList<MessageModel> loadedMessages) {
		this.loadedMessages = loadedMessages;
	}


}
