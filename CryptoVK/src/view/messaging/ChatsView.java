package view.messaging;

import java.util.HashMap;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
	public void getReadyForSwitch(Object param) {

		ChatPreviewModel previewModel = (ChatPreviewModel) param;
		
		if(!viewedChats.containsKey(previewModel)) {
			
			ChatView newChat = new ChatView(previewModel);
			
			chatNamesContainer.getChildren().add(new Label(previewModel.getTitle()));
			viewedChats.put(previewModel, newChat);
			setCurrentViewedChat(newChat);
		}
		
		else {
			setCurrentViewedChat(viewedChats.get(previewModel));
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
	private HashMap<ChatPreviewModel, ChatView> viewedChats = new HashMap<>();
	

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
