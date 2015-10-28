package model;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import model.MessageModel.ReadState;

public class DialogPreviewModel extends ChatPreviewModel {

	public DialogPreviewModel() {}
	
	public DialogPreviewModel(int chatId, ReadState readState, String title, long lastMessageId, ArrayList<String> chatIconURL,
			String lastMessage, Date lastMessageDate, VKPerson lastMessageSender, ArrayList<VKPerson> interlocutors) {
		super(chatId, readState, title, lastMessageId, chatIconURL, lastMessage, lastMessageDate, lastMessageSender, interlocutors);
	}

	@Override
	public void loadContent(JSONObject content) {
		extractDialogTitle(content);
		extractDialogId(content);
		super.loadContent(content);

	}
	
	
	private void extractDialogTitle(JSONObject content) {
		VKPerson interlocutor = VKPerson.getKnownPerson(content.getInt("user_id"));
		getInterlocutors().add(interlocutor);
		getLog().info(interlocutor.toString());
		setTitle(interlocutor.getFirstName() + " "
				+ interlocutor.getLastName());
	}
	
	private void extractDialogId(JSONObject content) {
		setChatId(content.getInt("user_id"));
	}
	
	
	
	@Override
	public boolean isContentCorresponding(JSONObject content) {
		if (isDialog(content) && content.getInt("user_id") == getChatId())
			return true;
		return false;
	}
	
	@Override
	public DialogPreviewModel clone() {
		
		return new DialogPreviewModel(getChatId(),getReadState(), getTitle(), getLastMessageId(), getChatIconURL(),
				getLastMessage(), getLastMessageDate(), getLastMessageSender(), getInterlocutors());
		
	}

	
}
