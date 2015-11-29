package controller;

import java.util.logging.Logger;

import data.ReadStatesDatabase.ChatReadState;
import http.ConnectionOperator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
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
		addReadStateListener();
		addReadButtonListener();
	}
	
	private void addReadStateListener() {
		controlled.getReadStateProperty().addListener(new ChangeListener<ChatReadState>() {
			public void changed(ObservableValue<? extends ChatReadState> observable, ChatReadState oldValue,
					ChatReadState newValue) {
				readStateWithIdProperty.setValue(new ChatReadStateWithId(controlled.getModel().getChatId(), newValue));
			}
		});
	}
	
	private void addReadButtonListener() {
		controlled.getRead().setOnMousePressed((MouseEvent e) -> {
			controlled.getRoot().pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
		});
		controlled.getRead().setOnAction((ActionEvent e) -> {
			if (controlled.getModel().getLastMessage().isIncoming()) {
				Thread t = new Thread(() -> {
					CO.readChat(controlled.getModel().getChatId(),
							controlled.getModel().getLastMessage().getId());
				});
				t.start();
				controlled.setReadState(ChatReadState.READ);
				controlled.getModel().setReadState(ChatReadState.READ);
				readStateWithIdProperty
						.setValue(new ChatReadStateWithId(controlled.getModel().getChatId(), ChatReadState.READ));
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
	private ObjectProperty<ChatReadStateWithId> readStateWithIdProperty = new SimpleObjectProperty<>();
	private ConnectionOperator CO = new ConnectionOperator(1000);

	public ObjectProperty<ChatReadStateWithId> getReadStateWithIdProperty() {
		return readStateWithIdProperty;
	}
	
	@SuppressWarnings("unused")
	private Logger log = Logger.getAnonymousLogger();

}
