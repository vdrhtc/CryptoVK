package model;

import org.json.JSONObject;

import http.ConnectionOperator;

public class DialogModel extends ChatModel {

	public DialogModel(ChatModel superModel, VKPerson interlocutor) {
		super(superModel.getChatId(), superModel.getChatTitle(), superModel.getChatIconURL(),
				superModel.getLoadedMessages(), superModel.getServerMessageCount());
		this.interlocutor = interlocutor;
	}

	@Override
	public JSONObject formAndSendRequest(int count, int offset) {
		return ConnectionOperator.getChatHistory(interlocutor.getId(), interlocutor.getId(), count, offset);
	}

	private VKPerson interlocutor;

	@Override
	public DialogModel clone() {
		return new DialogModel(super.clone(), interlocutor);
	}

	public int getInterlocutorId() {
		return getChatId();
	}

}
