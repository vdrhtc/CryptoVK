package view.messaging;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.messaging.MessageModel;
import view.View;

public class MessageView implements View {

	public MessageView() {
		
	}
	
	
	private MessageModel model;
	private HBox root = new HBox();
	private Label date = new Label();
	private Label message = new Label();
	
	@Override
	public Parent getRoot() {
		return root;
	}
	
}
