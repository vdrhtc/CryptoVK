package model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class TalkPreviewModel extends ChatPreviewModel {

	private String deletedImageURL = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcRdEp0TNPk3cGnvN0IBtMZsw9-td381BgxHGoqEuiy8Afgn9qtc";

	@Override
	public void loadContent(JSONObject content) {
		JSONObject messageContent = content.getJSONObject("message");
		extractTalkTitle(messageContent);
		extractTalkId(messageContent);
		extractTalkInterlocutorsAndIcon(messageContent);
		super.loadContent(content);
	}

	private void extractTalkInterlocutorsAndIcon(JSONObject content) {
		if (interlocutors.size() > 0)
			return;
		this.interlocutors = new ArrayList<VKPerson>();
		JSONObject chatInfo = ChatsPreviewModel.getConnectionOperator().getChat(content.getInt("chat_id"));
		if (chatInfo.optInt("left") == 1) {
			getChatIconURL().add(deletedImageURL);
			getTitle().concat(" [Wasted] ");
		} else if (!(chatInfo.optString("photo_100").equals(""))) {
			if (getChatIconURL().size() > 0)
				getChatIconURL().set(0, chatInfo.getString("photo_100"));
			else
				getChatIconURL().add(chatInfo.getString("photo_100"));
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
		this.chatId = content.getLong("chat_id");
	}

	private void extractTalkTitle(JSONObject content) {
		this.title = content.getString("title");
	}

	@Override
	public boolean isContentCorresponding(JSONObject content) {
		if (!(content.optInt("chat_id")==0) && content.getInt("chat_id") == getChatId())
			return true;
		return false;
	}
}
