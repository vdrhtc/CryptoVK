package model.preview;

import model.VKPerson;
import model.messaging.DialogModel;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

public class DialogPreviewModel extends ChatPreviewModel {

	public DialogPreviewModel() {}
	
	public DialogPreviewModel(int chatId, Boolean read, String title, long lastMessageId, String[] chatIconURL,
			String lastMessage, Date lastMessageDate, VKPerson lastMessageSender, ArrayList<VKPerson> interlocutors) {
		super(chatId, read, title, lastMessageId, chatIconURL, lastMessage, lastMessageDate, lastMessageSender, interlocutors);
	}

	@Override
	public void loadContent(JSONObject content) {
		extractDialogTitle(content);
		extractDialogId(content);
		extractDialogIcon();
		super.loadContent(content);

	}
	
	@Override
	public DialogModel buildFullModel() {
		return new DialogModel(this);
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
	
	private void extractDialogIcon() {
		setChatIconURL(getInterlocutors().get(0).getPhotoURL());
	}
	
	
	@Override
	public boolean isContentCorresponding(JSONObject content) {
		if (isDialog(content) && content.getInt("user_id") == getChatId())
			return true;
		return false;
	}
	
	@Override
	public DialogPreviewModel clone() {
		
		return new DialogPreviewModel(getChatId(),getRead(), getTitle(), getLastMessageId(), getChatIconURL(),
				getLastMessage(), getLastMessageDate(), getLastMessageSender(), getInterlocutors());
		
	}

	
}
