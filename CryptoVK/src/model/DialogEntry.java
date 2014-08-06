package model;

import org.json.JSONObject;

public class DialogEntry extends ChatEntry {

	
	@Override
	public void loadContent(JSONObject content) {
		extractDialogTitle(content);
		extractDialogId(content);
		extractDialogIcon();
		super.loadContent(content);

	}
	
	private void extractDialogTitle(JSONObject content) {
		VKPerson interlocutor = VKPerson.getKnownPerson(content.getInt("user_id"));
		getInterlocutors().add(interlocutor);
		getLog().info(interlocutor.toString());
		setTitle(interlocutor.getFirstName() + " "
				+ interlocutor.getLastName());
	}
	
	private void extractDialogId(JSONObject content) {
		setChatId(content.getInt("user_id"));
	}
	
	private void extractDialogIcon() {
		setChatIconURL(getInterlocutors().get(0).getPhotoURL());
	}
	
	
	@Override
	public boolean isContentCorresponding(JSONObject content) {
		if (isDialog(content) && content.getInt("user_id") == getChatId())
			return true;
		return false;
	}
	
}
