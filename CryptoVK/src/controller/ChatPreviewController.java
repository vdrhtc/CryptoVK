package controller;


import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import view.SwitchableView.ViewName;
import view.preview.ChatPreview;

public class ChatPreviewController {
	
	public ChatPreviewController(ChatPreview controlled) {
		
		this.controlled = controlled;
		this.controlled.getRoot().setOnMouseClicked(clickHandler);
	}
	
	private EventHandler<MouseEvent> clickHandler  = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			ViewSwitcher.getInstance().switchToView(ViewName.CHATS_VIEW, controlled.getCurrentLoadedModel());
		}
		
	};
	
	private ChatPreview controlled;
		
	private Logger log = Logger.getAnonymousLogger();
}
