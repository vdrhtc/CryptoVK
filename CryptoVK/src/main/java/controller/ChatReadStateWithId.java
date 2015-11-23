package controller;
import data.ReadStatesDatabase.ChatReadState;

public class ChatReadStateWithId {

	public ChatReadStateWithId(int chatId, ChatReadState RS) {
		this.chatId = chatId;
		this.RS = RS;
	}

	private int chatId;
	private ChatReadState RS;

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public ChatReadState getRS() {
		return RS;
	}

	public void setRS(ChatReadState rS) {
		RS = rS;
	}

}
