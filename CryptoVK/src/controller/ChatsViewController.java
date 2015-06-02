package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import view.SwitchableView.ViewName;
import view.messaging.ChatsView;


public class ChatsViewController {

	public ChatsViewController(ChatsView CV) {
		this.controlled = CV;
	}
	

	public void addBackButtonListener(Button back) {
		back.setOnAction((ActionEvent e)->{
			ViewSwitcher.getInstance().switchToView(ViewName.CHATS_PREVIEW);
		});
	}
	
	private ChatsView controlled;

}
