package controller;
import data.ReadStatesDatabase.ReadState;

public class ReadStateWithId {

	public ReadStateWithId(int chatId, ReadState RS) {
		this.chatId = chatId;
		this.RS = RS;
	}

	private int chatId;
	private ReadState RS;

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public ReadState getRS() {
		return RS;
	}

	public void setRS(ReadState rS) {
		RS = rS;
	}

}
