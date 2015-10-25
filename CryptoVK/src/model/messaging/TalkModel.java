package model.messaging;

import java.util.ArrayList;

import org.json.JSONObject;

import http.ConnectionOperator;
import model.preview.ChatPreviewModel;
import view.messaging.ChatView;

public class TalkModel extends ChatModel {

	public TalkModel(ChatPreviewModel sourcePreviewModel) {
		super(sourcePreviewModel);
		this.loadMessages(ChatView.INIT_LOAD_COUNT, 0);
	}

	public TalkModel(int chatId, String chatTitle, String[] chatIconURL, ArrayList<MessageModel> loadedMessages,
			Integer serverMessageCount) {
		super(chatId, chatTitle, chatIconURL, loadedMessages, serverMessageCount);
	}


	@Override
	public JSONObject formAndSendRequest(int count, int offset) {
		return ConnectionOperator.getChatHistory(0, getChatId(), count, offset);
	}

	@Override
	public TalkModel clone() {
		return new TalkModel(getChatId(), getChatTitle(), getChatIconURL(), getLoadedMessages(),
				getServerMessageCount());
	}

	public int getInterlocutorId() {
		return 0;
	}
}
