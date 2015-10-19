package model.messaging;

import http.ConnectionOperator;
import model.VKPerson;
import model.preview.ChatPreviewModel;

import org.json.JSONArray;

public class DialogModel extends ChatModel {

	public DialogModel(ChatPreviewModel sourcePreviewModel) {
		super(sourcePreviewModel);
		this.interlocutor = sourcePreviewModel.getInterlocutors().get(0);
		this.loadNewMessages(10);
	}
	
	@Override
	public void loadNewMessages(int count) {
		
		JSONArray messages = ConnectionOperator.getChatHistory(interlocutor.getId(), interlocutor.getId(), count, 0);
		System.out.println(messages.toString());
		MessageModel startMessage = new MessageModel(messages.getJSONObject(0));
	}

	
	private VKPerson interlocutor;

}
