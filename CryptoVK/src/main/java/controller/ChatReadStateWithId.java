package controller;
import data.ReadStatesDatabase.ChatReadState;

public class ChatReadStateWithId {

	public ChatReadStateWithId(Long chatId, ChatReadState RS) {
		this.chatId = chatId;
		this.RS = RS;
	}

	private Long chatId;
	private ChatReadState RS;

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public ChatReadState getRS() {
		return RS;
	}

	public void setRS(ChatReadState rS) {
		RS = rS;
	}

}
