package model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class TalkPreviewModel extends ChatPreviewModel {

	public TalkPreviewModel() {
		
	}

	public TalkPreviewModel(ChatPreviewModel superModel) {
		super(superModel.getChatId(), superModel.getReadState(), superModel.getTitle(), superModel.getLastMessageId(),
				superModel.getChatIconURL(), superModel.getLastMessage(), superModel.getLastMessageDate(),
				superModel.getLastMessageSender(), superModel.getInterlocutors());
	}

	private String deletedImageURL = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcRdEp0TNPk3cGnvN0IBtMZsw9-td381BgxHGoqEuiy8Afgn9qtc";

	@Override
	public void loadContent(JSONObject content) {
		extractTalkTitle(content);
		extractTalkId(content);
		extractTalkInterlocutorsAndIcon(content);
		super.loadContent(content);
	}

	private void extractTalkInterlocutorsAndIcon(JSONObject content) {
		this.setInterlocutors(new ArrayList<VKPerson>());
		JSONObject chatInfo = ChatsPreviewModel.getConnectionOperator().getChat(content.getInt("chat_id"));
		if (chatInfo.optInt("left") == 1) {
			getChatIconURL().add(deletedImageURL);
			getTitle().concat(" [Потрачено] ");
		} else if (!(chatInfo.optString("photo_50").equals(""))) {
			if (getChatIconURL().size() > 0)
				getChatIconURL().set(0, chatInfo.getString("photo_50"));
			else
				getChatIconURL().add(chatInfo.getString("photo_50"));
		}
		JSONArray interlocutors = chatInfo.getJSONArray("users");
		for (int i = 0; i < interlocutors.length(); i++) {
			addInterlocutor(interlocutors.getJSONObject(i));
		}
	}
	
	public void addInterlocutor(JSONObject user) {
		getInterlocutors().add(new VKPerson(user));
	}


	private void extractTalkId(JSONObject content) {
		this.setChatId(content.getInt("chat_id"));
	}

	private void extractTalkTitle(JSONObject content) {
		this.setTitle(content.getString("title"));
	}

	@Override
	public TalkPreviewModel clone() {
		return new TalkPreviewModel(super.clone());
	}

	@Override
	public boolean isContentCorresponding(JSONObject content) {
		if (!isDialog(content) && content.getInt("chat_id") == getChatId())
			return true;
		return false;
	}
}
