package view.messaging;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.messaging.ChatModel;
import model.preview.ChatPreviewModel;
import view.SwitchableView;
import controller.ChatViewController;

public class ChatsView implements SwitchableView {
	
	public ChatsView() {

		this.controller = new ChatViewController(this);
		this.controller.addBackButtonListener(back);
		
		this.back.setCancelButton(true);
		
		this.header.getChildren().addAll(back, title);
		
		VBox chatHolderDummy = new VBox();
		this.root.getChildren().addAll(header, chatNamesContainer, chatHolderDummy);
		
	}
	
	
	@Override
	public void getReadyForSwitch(Object... params) {

		ChatModel model = (ChatModel) params[0];
		
		if(!viewedChats.containsKey(model)) {
			
			ChatView newChat = new ChatView(model);
			
			chatNamesContainer.getChildren().add(new Label(model.getTitle()));
			viewedChats.put(model, newChat);
			setCurrentViewedChat(newChat);
		}
		
		else {
			setCurrentViewedChat(viewedChats.get(model));
		}
	}
	
	
	private void setCurrentViewedChat(ChatView CV) {
		this.root.getChildren().set(2, CV.getRoot());
	}
	
	private VBox root = new VBox();
	private HBox header = new HBox();
	private ChatViewController controller;
	private Label title = new Label("Чаты");
	private Button back = new Button("Назад");
	private HBox chatNamesContainer = new HBox();
	private HashMap<ChatModel, ChatView> viewedChats = new HashMap<>();
	

	@Override
	public Pane getRoot() {
		return root;
	}

	@Override
	public ViewName getName() {
		return ViewName.CHATS_VIEW;
	}


	@Override
	public ViewName redirectTo() {
		return ViewName.CHATS_VIEW;
	}


}
