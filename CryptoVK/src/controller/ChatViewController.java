package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import view.SwitchableView.ViewName;
import view.messaging.ChatsView;


public class ChatViewController {

	public ChatViewController(ChatsView CV) {
		this.controlled = CV;
	}
	

	public void addBackButtonListener(Button back) {
		back.setOnAction((ActionEvent e) -> {
			ViewSwitcher.getInstance().switchToView(ViewName.CHATS_PREVIEW, null);
		});
	}
	
	private ChatsView controlled;

}
