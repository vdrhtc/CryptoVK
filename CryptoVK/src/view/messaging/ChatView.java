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

	public static final int LOAD_NEW_COUNT = 10;

	
	public ChatView(ChatModel model) {
		initRoot();
		this.model = model;
		loadMessages();
	}
	
	private void initRoot() {
		this.root = new VBox();
		this.footer.getChildren().addAll(inputTray, sendButton);
		this.root.getChildren().addAll(messagesContainer, footer);
	}
	
	private void loadMessages() {
		model.loadNewMessages(LOAD_NEW_COUNT);
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
