package controller;

import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import model.ChatModel;
import model.ChatPreviewModel;
import model.DialogModel;
import model.TalkModel;
import view.ChatPreview;
import view.ChatsView;
import view.View.ViewName;
import view.nodes.ChatNameLabel;

public class ChatsViewController implements Controller {

	public ChatsViewController() {
		this.controlled = new ChatsView();
		
		addBackButtonListener(controlled.getBackButton());
		this.updater = new ChatsViewLPU(controlled);
		this.controlled.canBeUpdated()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					if (newValue)
						updater.isWorkingProperty().setValue(true);
					updater.start();
				});
	}

	@Override
	public void prepareViewForSwitch(Object... params) {
		ChatPreview preview = (ChatPreview) params[0];
		ChatPreviewModel previewModel = preview.getModel();
		Integer chatId = previewModel.getChatId();

		if (!controlled.getViewedChats().containsKey(chatId)) {

			ChatModel fullModel = new ChatModel(previewModel.getChatId(), previewModel.getChatIconURL(),
					previewModel.getTitle(), previewModel.getReadState());
			if (previewModel.getInterlocutors().size() == 1)
				fullModel = new DialogModel(fullModel, previewModel.getInterlocutors().get(0));
			else 
				fullModel = new TalkModel(fullModel);

			ChatViewController newChatController = new ChatViewController(fullModel);
			addReadStateChangeListener(newChatController);

			controlled.getModel().getLock();
			controlled.getModel().getChatModels().put(fullModel.getChatId(), fullModel);
			controlled.releaseLock();

			ChatNameLabel newNameLabel = new ChatNameLabel(fullModel);
			controlled.getChatNamesContainer().getChildren().add(newNameLabel);
			newChatController.getControlled().setChatNameLabel(newNameLabel);

			controlled.getViewedChats().put(chatId, newChatController.getControlled());
			controllers.put(chatId, newChatController);

			newChatController.prepareViewForSwitch((Object[]) null);
			if (activeChatController != null)
				activeChatController.prepareViewForSwitch((Object[]) null);
			controlled.setCurrentViewedChat(newChatController.getControlled());
			activeChatController = controllers.get(chatId);
		}

		else {
			controllers.get(chatId).prepareViewForSwitch((Object[]) null);
			if (activeChatController != null)
				activeChatController.prepareViewForSwitch((Object[]) null);
			controlled.setCurrentViewedChat(controlled.getViewedChats().get(chatId));
			activeChatController = controllers.get(chatId);
		}
		controlled.canBeUpdated().setValue(true);
	}

	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}

	private void addBackButtonListener(Button back) {
		back.setOnAction((ActionEvent e) -> {
			activeChatController.prepareViewForSwitch((Object[]) null);
			activeChatController = null;
			ViewSwitcher.getInstance().switchToView(ViewName.CHATS_PREVIEW, (Object[]) null);
		});
	}

	private void addReadStateChangeListener(ChatViewController CVC) {
		CVC.getReadStateWithIdProperty().addListener(new ChangeListener<ReadStateWithId>() {
			@Override
			public void changed(ObservableValue<? extends ReadStateWithId> observable, ReadStateWithId oldValue,
					ReadStateWithId newValue) {
				changedReadStatesWithIds.add(newValue);
			}
		});
	}

	private ChatsView controlled;
	private HashMap<Integer, ChatViewController> controllers = new HashMap<>();
	private ChatsViewLPU updater;
	private ChatViewController activeChatController;
	private ObservableList<ReadStateWithId> changedReadStatesWithIds = FXCollections.observableArrayList();
	
	public HashMap<Integer, ChatViewController> getControllers() {
		return controllers;
	}

	public ObservableList<ReadStateWithId> getReadStateWithIdProperty() {
		return changedReadStatesWithIds;
	}

	public ChatsView getControlled() {
		return controlled;
	}
}
