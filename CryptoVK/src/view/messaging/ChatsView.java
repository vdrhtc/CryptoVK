package view.messaging;

import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import view.SwitchableView;
import controller.ChatsViewController;

public class ChatsView implements SwitchableView {
	
	public ChatsView() {
		this.controller = new ChatsViewController(this);
		this.controller.addBackButtonListener(back);
		
		this.back.setCancelButton(true);
		
		this.header.getChildren().addAll(back, title);
		this.root.getChildren().addAll(header, chatNamesContainer, inputTray);
		
	}
	
	
	public void addChatView(ChatView newChatView) {
		viewedChats.add(newChatView);
	}
	
	
	private VBox root = new VBox();
	private HBox header = new HBox();
	private Label title = new Label("Чаты");
	private Button back = new Button("Назад");
	private HBox chatNamesContainer = new HBox();
	private TextField inputTray = new TextField();
	private ChatsViewController controller;
	private ArrayList<ChatView> viewedChats = new ArrayList<>();
	

	@Override
	public Pane getRoot() {
		return root;
	}

	@Override
	public ViewName getName() {
		return ViewName.CHATS_HOLDER;
	}

	@Override
	public void getReadyForSwitch() {
		// TODO Auto-generated method stub
	}

	@Override
	public ViewName redirectTo() {
		return ViewName.CHATS_HOLDER;
	}


}
