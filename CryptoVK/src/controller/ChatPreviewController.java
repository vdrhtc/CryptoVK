package controller;


import java.util.logging.Logger;

import javafx.scene.input.MouseEvent;
import model.ChatPreviewModel;
import view.ChatPreview;
import view.View.ViewName;

public class ChatPreviewController implements Controller {
	
	public ChatPreviewController(ChatPreviewModel CPM) {
		
		this.controlled = new ChatPreview(CPM);
		this.controlled.getRoot().setOnMouseClicked((MouseEvent event) -> {
			ViewSwitcher.getInstance().switchToView(ViewName.CHATS_VIEW, controlled);
		});
	}
	
	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}
	
	@Override
	public void prepareViewForSwitch(Object... params) {
		
	}

	
	public ChatPreview getControlled() {
		return controlled;
	}
	
	private ChatPreview controlled;
		
	@SuppressWarnings("unused")
	private Logger log = Logger.getAnonymousLogger();

}
