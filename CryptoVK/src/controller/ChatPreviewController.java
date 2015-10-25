package controller;


import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import model.ChatPreviewModel;
import view.ChatPreview;
import view.View.ViewName;

public class ChatPreviewController implements Controller {
	
	public ChatPreviewController(ChatPreviewModel CPM) {
		
		this.controlled = new ChatPreview(CPM);
		this.controlled.getRoot().setOnMouseClicked(clickHandler);
	}
	
	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}
	
	@Override
	public void prepareViewForSwitch(Object... params) {
		// TODO Auto-generated method stub
		
	}
	private EventHandler<MouseEvent> clickHandler  = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			ViewSwitcher.getInstance().switchToView(ViewName.CHATS_VIEW, controlled.getCurrentLoadedModel());
		}
		
	};
	
	public ChatPreview getControlled() {
		return controlled;
	}
	
	private ChatPreview controlled;
		
	@SuppressWarnings("unused")
	private Logger log = Logger.getAnonymousLogger();

}
