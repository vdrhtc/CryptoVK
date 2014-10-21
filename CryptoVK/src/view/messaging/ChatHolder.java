package view.messaging;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import view.SwitchableView;
import controller.ChatHolderController;
import controller.ViewSwitcher;

public class ChatHolder implements SwitchableView {
	
	public ChatHolder() {
		this.controller = new ChatHolderController(this);
	}

	
	public void addChatView(ChatView newChatView) {
		viewedChats.add(newChatView);
	}
	
	private Pane root;
	private ChatHolderController controller;
	private ArrayList<ChatView> viewedChats = new ArrayList<>();
	
	
	
	@Override
	public Pane getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pane buildRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ViewName getName() {
		return ViewName.CHAT_HOLDER;
	}

	@Override
	public void setViewSwitcher(ViewSwitcher VS) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareModel() {
		// TODO Auto-generated method stub
	}

	@Override
	public ViewName redirectTo() {
		return ViewName.CHAT_HOLDER;
	}

}
