package view.messaging;

import java.util.ArrayList;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.messaging.ChatModel;
import model.preview.ChatPreviewModel;
import view.View;

public class ChatView implements View {

	public ChatView(ChatPreviewModel sourcePreviewModel) {
		initRoot();
		this.model = new ChatModel(sourcePreviewModel);
		loadMessages();
	}
	
	private void initRoot() {
		this.root = new VBox();
		this.footer.getChildren().addAll(inputTray, sendButton);
		this.root.getChildren().addAll(messagesContainer, footer);
	}
	
	private void loadMessages() {
		
	}

	private VBox root;
	private ChatModel model;
	private HBox footer = new HBox();
	private Button sendButton = new Button();
	private TextField inputTray = new TextField();
	private ScrollPane messagesContainer = new ScrollPane();
	private ArrayList<MessageView> messageViews = new ArrayList<>();
	
	@Override
	public Parent getRoot() {
		return root;
	}
	
	
}
