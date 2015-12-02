package view.nodes;

import data.ReadStatesDatabase.ChatReadState;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import model.ChatModel;

public class ChatNameLabel extends Label {

	public ChatNameLabel(ChatModel fullModel) {
		super();
		this.chatId = fullModel.getChatId();
		setText(fullModel.getChatTitle());
		setReadState(fullModel.getReadState());
		getStyleClass().add("chats-chat-name");
	}
	
	public void setReadState(ChatReadState chatReadState) {
		switch (chatReadState) {
		case READ:
			setPseudoClass(false, false, false);
			break;
		case UNREAD:
			setPseudoClass(true, false, false);
			break;
		case VIEWED:
			setPseudoClass(false, true, false);
			break;
		case POSTPONED:
			setPseudoClass(false, false, true);
			break;
		}
	}

	private void setPseudoClass(boolean unread, boolean viewed, boolean postponed) {
		pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), unread);
		pseudoClassStateChanged(PseudoClass.getPseudoClass("viewed"), viewed);
		pseudoClassStateChanged(PseudoClass.getPseudoClass("postponed"), postponed);
	}
	
	private Long chatId;
	
	public Long getChatId() {
		return chatId;
	}
}
