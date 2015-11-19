package model;

import org.json.JSONObject;

public class DialogModel extends ChatModel {

	public DialogModel(ChatModel superModel, VKPerson interlocutor) {
		super(superModel.getChatId(), superModel.getChatTitle(), superModel.getChatIconURL(),
				superModel.getLoadedMessages(), superModel.getServerMessageCount(), superModel.getReadState());
		this.interlocutor = interlocutor;
	}

	@Override
	public JSONObject formAndSendRequest(int count, int offset) {
		return ChatsModel.getConnectionOperator().getChatHistory(interlocutor.getId(), interlocutor.getId(), count, offset);
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
