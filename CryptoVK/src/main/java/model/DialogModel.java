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
		return ChatsModel.getConnectionOperator().getChatHistory(interlocutor.getId(), interlocutor.getId().longValue(), count, offset);
	}

	private VKPerson interlocutor;

	public Long getInterlocutorId() {
		return getChatId();
	}

}
