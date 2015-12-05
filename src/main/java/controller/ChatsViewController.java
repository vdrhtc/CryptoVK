package controller;

import java.util.HashMap;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.ChatModel;
import model.ChatPreviewModel;
import model.DialogModel;
import model.TalkModel;
import view.ChatPreview;
import view.ChatView;
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
		Long chatId = previewModel.getChatId();

		if (!controlled.getViewedChats().containsKey(chatId)) {

			ChatModel fullModel = new ChatModel(previewModel.getChatId(), previewModel.getChatIconURL(),
					previewModel.getTitle(), previewModel.getReadState());
			if (previewModel.getInterlocutors().size() == 1)
				fullModel = new DialogModel(fullModel, previewModel.getInterlocutors().get(0));
			else
				fullModel = new TalkModel(fullModel);

			ChatViewController newChatController = new ChatViewController(fullModel);
			addReadStateChangeListener(newChatController);

			controlled.getModel().getLock("ChatsViewController.initView");
			controlled.getModel().getChatModels().put(fullModel.getChatId(), fullModel);

			ChatNameLabel newNameLabel = new ChatNameLabel(fullModel);
			addChatNameLabelMouseListener(newNameLabel);
			addChatNameLabelContextMenu(newNameLabel);
			controlled.getChatNamesContainer().getChildren().add(newNameLabel);
			newChatController.getControlled().setChatNameLabel(newNameLabel);

			controlled.getViewedChats().put(chatId, newChatController.getControlled());
			controllers.put(chatId, newChatController);

			newChatController.prepareViewForSwitch((Object[]) null);
			if (activeChatController != null)
				activeChatController.prepareViewForSwitch((Object[]) null);
			controlled.setCurrentViewedChat(newChatController.getControlled());
			activeChatController = controllers.get(chatId);

			controlled.releaseLock("ChatsViewController.initView");
		} else {
			switchChatTo(chatId);
		}
		controlled.canBeUpdated().setValue(true);
	}

	public void switchChatTo(Long chatId) {
		controllers.get(chatId).prepareViewForSwitch((Object[]) null);
		if (activeChatController != null)
			activeChatController.prepareViewForSwitch((Object[]) null);
		controlled.setCurrentViewedChat(controlled.getViewedChats().get(chatId));
		activeChatController = controllers.get(chatId);
	}

	public void removeChat(Long chatId) {
		controlled.getModel().getLock("ChatsViewController.removeChat");
		ChatView removed = controlled.getViewedChats().remove(chatId);
		if (removed == null)
			return;
		Integer index = controlled.getChatNamesContainer().getChildren().indexOf(removed.getChatNameLabel());
		controlled.getChatNamesContainer().getChildren().remove(removed.getChatNameLabel());
		Integer newSize = controlled.getChatNamesContainer().getChildren().size();
		if (newSize > 0) {
			index = index == newSize ? newSize - 1 : index;
			Long nextId = ((ChatNameLabel) controlled.getChatNamesContainer().getChildren().get(index)).getChatId();
			switchChatTo(nextId);
		} else
			returnToPreview();
		controllers.remove(chatId);
		controlled.getModel().releaseLock("ChatsViewController.removeChat");

	}

	private void addBackButtonListener(Button back) {
		back.setOnAction((ActionEvent e) -> {
			returnToPreview();
		});
	}

	public void returnToPreview() {
		activeChatController.prepareViewForSwitch((Object[]) null);
		activeChatController = null;
		ViewSwitcher.getInstance().switchToView(ViewName.CHATS_PREVIEW, (Object[]) null);
	}

	private void addChatNameLabelMouseListener(ChatNameLabel label) {
		label.setOnMouseClicked((MouseEvent e) -> {
			if (e.getButton().equals(MouseButton.MIDDLE))
				removeChat(label.getChatId());
			if (e.getButton().equals(MouseButton.PRIMARY))
				if (!label.getChatId().equals(activeChatController.getControlled().getModel().getChatId()))
					switchChatTo(label.getChatId());
			if (e.getClickCount() > 1)
				removeChat(label.getChatId());
		});
	}

	private void addChatNameLabelContextMenu(ChatNameLabel label) {
		ContextMenu menu = new ContextMenu();
		MenuItem close = new MenuItem("Close");
		close.setOnAction((ActionEvent e) -> {
			removeChat(label.getChatId());
		});
		close.getStyleClass().add("name-label-context-menu-item");

		MenuItem closeOthers = new MenuItem("Close Others");
		closeOthers.setOnAction((ActionEvent e) -> {
			@SuppressWarnings("unchecked")
			Set<Long> keys = ((HashMap<Long, ChatViewController>)controllers.clone()).keySet();
			for (Long chatId : keys)
				if (!chatId.equals(label.getChatId()))
					removeChat(chatId);
		});
		closeOthers.getStyleClass().add("name-label-context-menu-item");

		MenuItem closeAll = new MenuItem("Close All");
		closeAll.setOnAction((ActionEvent e) -> {
			@SuppressWarnings("unchecked")
			Set<Long> keys = ((HashMap<Long, ChatViewController>)controllers.clone()).keySet();
			for (Long chatId : keys)
				removeChat(chatId);
		});
		closeAll.getStyleClass().add("name-label-context-menu-item");
		menu.getItems().addAll(close, closeOthers, new SeparatorMenuItem(), closeAll);
		label.setContextMenu(menu);
	}

	private void addReadStateChangeListener(ChatViewController CVC) {
		CVC.getReadStateWithIdProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable observable) {
				ChatReadStateWithId newValue = CVC.getReadStateWithIdProperty().getValue();
				changedReadStatesWithIds.add(newValue);
			}
		});
	}

	private ChatsView controlled;
	private HashMap<Long, ChatViewController> controllers = new HashMap<>();
	private ChatsViewLPU updater;
	private ChatViewController activeChatController;
	private ObservableList<ChatReadStateWithId> changedReadStatesWithIds = FXCollections.observableArrayList();

	public HashMap<Long, ChatViewController> getControllers() {
		return controllers;
	}

	public ObservableList<ChatReadStateWithId> getReadStateWithIdProperty() {
		return changedReadStatesWithIds;
	}

	public ChatsView getControlled() {
		return controlled;
	}

	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}
}
