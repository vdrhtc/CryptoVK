package model;

import org.json.JSONObject;

public class TalkModel extends ChatModel {


	public TalkModel(ChatModel superModel) {
		super(superModel.getChatId(), superModel.getChatTitle(), superModel.getChatIconURL(),
				superModel.getLoadedMessages(), superModel.getServerMessageCount(), superModel.getReadState());
	}

	@Override
	public JSONObject formAndSendRequest(int count, int offset) {
		return ChatsModel.getConnectionOperator().getChatHistory(0, getChatId(), count, offset);
	}

	@Override
	public TalkModel clone() {
		return new TalkModel(super.clone());
	}
	
	@Override
	public Long getInterlocutorId() {
		return Long.valueOf("0");
	}
}
