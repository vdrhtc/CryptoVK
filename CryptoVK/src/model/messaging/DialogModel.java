package model.messaging;

import java.util.ArrayList;

import org.json.JSONObject;

import http.ConnectionOperator;
import model.VKPerson;
import model.preview.ChatPreviewModel;
import view.messaging.ChatView;

public class DialogModel extends ChatModel {

	public DialogModel(ChatPreviewModel sourcePreviewModel) {
		super(sourcePreviewModel);
		this.interlocutor = sourcePreviewModel.getInterlocutors().get(0);
		this.loadMessages(ChatView.INIT_LOAD_COUNT, 0);
	}

	public DialogModel(int chatId, String chatTitle, String[] chatIconURL,
			ArrayList<MessageModel> loadedMessages, Integer serverMessageCount, VKPerson interlocutor) {
		super(chatId, chatTitle, chatIconURL, loadedMessages, serverMessageCount);
		this.interlocutor = interlocutor;
	}

	
	@Override
	public JSONObject formAndSendRequest(int count, int offset){
		return ConnectionOperator.getChatHistory(interlocutor.getId(), interlocutor.getId(), count,
				offset);
	}
	

	private VKPerson interlocutor;

	@Override
	public DialogModel clone() {
		return new DialogModel(getChatId(), getChatTitle(), getChatIconURL(), getLoadedMessages(), getServerMessageCount(),
				interlocutor);
	}
	
	public int getInterlocutorId() {
		return getChatId();
	}
	
}
