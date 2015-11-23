package model;

import java.util.ArrayList;

import org.json.JSONObject;

import data.ReadStatesDatabase.ChatReadState;

public class DialogPreviewModel extends ChatPreviewModel {

	public DialogPreviewModel() {
	}

	public DialogPreviewModel(Long chatId, ChatReadState chatReadState, String title, ArrayList<String> chatIconURL,
			MessageModel lastMessage, ArrayList<VKPerson> interlocutors) {
		super(chatId, chatReadState, title, chatIconURL, lastMessage, interlocutors);
	}

	@Override
	public void loadContent(JSONObject content) {
		extractInterlocutor(content);
		extractDialogTitle(this.getInterlocutors().get(0));
		super.loadContent(content);

	}

	public void extractInterlocutor(JSONObject content) {
		if (getInterlocutors().size() == 0)
			getInterlocutors().add(VKPerson.getKnownPerson(content.getInt("user_id")));
	}

	private void extractDialogTitle(VKPerson interlocutor) {
		title = interlocutor.getFirstName() + " " + interlocutor.getLastName();
	}

	@Override
	public boolean isContentCorresponding(JSONObject content) {
		if (content.optInt("chat_id") == 0 && content.getInt("user_id") == getChatId())
			return true;
		return false;
	}

	@Override
	public DialogPreviewModel clone() {
		return new DialogPreviewModel(getChatId(), getReadState(), getTitle(), getChatIconURL(), getLastMessage(),
				getInterlocutors());
	}

}
