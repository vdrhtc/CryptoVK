package controller;

import java.util.logging.Logger;

import javafx.css.PseudoClass;
import javafx.event.EventHandler;
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
		
		this.controlled.getLeftContainer().setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				controlled.getRead().pseudoClassStateChanged(PseudoClass.getPseudoClass("parent-hover"), true);
			}
		});
		this.controlled.getLeftContainer().setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				controlled.getRead().pseudoClassStateChanged(PseudoClass.getPseudoClass("parent-hover"), false);
			}
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
