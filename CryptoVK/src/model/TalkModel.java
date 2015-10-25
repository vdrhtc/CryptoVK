package model;

import org.json.JSONObject;

import http.ConnectionOperator;

public class TalkModel extends ChatModel {


	public TalkModel(ChatModel superModel) {
		super(superModel.getChatId(), superModel.getChatTitle(), superModel.getChatIconURL(),
				superModel.getLoadedMessages(), superModel.getServerMessageCount());
	}

	@Override
	public JSONObject formAndSendRequest(int count, int offset) {
		return ConnectionOperator.getChatHistory(0, getChatId(), count, offset);
	}

	@Override
	public TalkModel clone() {
		return new TalkModel(super.clone());
	}

	public int getInterlocutorId() {
		return 0;
	}
}
