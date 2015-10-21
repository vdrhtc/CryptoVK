package model.messaging;

import http.ConnectionOperator;
import model.VKPerson;
import model.preview.ChatPreviewModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class DialogModel extends ChatModel {

	public DialogModel(ChatPreviewModel sourcePreviewModel) {
		super(sourcePreviewModel);
		this.interlocutor = sourcePreviewModel.getInterlocutors().get(0);
		this.loadNewMessages(10);
	}
	
	@Override
	public void loadNewMessages(int count) {
				
		JSONArray messageContents = ConnectionOperator.getChatHistory(interlocutor.getId(), interlocutor.getId(), count, 0);
		for (int i=0; i<messageContents.length();i++) {
			this.getLoadedMessages().add(new MessageModel(messageContents.getJSONObject(i)));
		}
	}

	
	private VKPerson interlocutor;

}
