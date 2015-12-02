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


	public ChatReadState getRS() {
		return RS;
	}
	
	@Override
	public String toString() {
		return "[chatId=" + chatId + ", RS=" + RS + "]";
	}
}
