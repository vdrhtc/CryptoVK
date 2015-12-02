package model;

import org.json.JSONObject;

public class DialogPreviewModel extends ChatPreviewModel {


	@Override
	public void loadContent(JSONObject content) {
		extractInterlocutor(content);
		extractDialogTitle(this.getInterlocutors().get(0));
		super.loadContent(content);

	}

	public void extractInterlocutor(JSONObject content) {
		if (getInterlocutors().size() == 0)
			getInterlocutors().add(VKPerson.getKnownPerson(content.getInt("user_id")));
	}

	private void extractDialogTitle(VKPerson interlocutor) {
		title = interlocutor.getFirstName() + " " + interlocutor.getLastName();
	}

	@Override
	public boolean isContentCorresponding(JSONObject content) {
		if (content.optInt("chat_id") == 0 && content.getInt("user_id") == getChatId())
			return true;
		return false;
	}

}
