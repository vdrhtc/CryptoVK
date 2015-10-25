package controller;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.messaging.ChatModel;
import model.preview.ChatPreviewModel;
import view.View.ViewName;
import view.messaging.ChatsView;

public class ChatsController implements Controller {

	public ChatsController() {
		this.controlled = new ChatsView();
		
		addBackButtonListener(controlled.getBackButton());
		this.updater = new ChatsUpdater(controlled);
		this.controlled.canBeUpdated()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					if (newValue)
						updater.isWorkingProperty().setValue(true);
					updater.start();
				});
	}

	@Override
	public void prepareViewForSwitch(Object... params) {
		ChatPreviewModel chatPreviewModel = (ChatPreviewModel) params[0];
		Integer chatId = chatPreviewModel.getChatId();

		if (!controlled.getViewedChats().containsKey(chatId)) {

			ChatModel fullModel = chatPreviewModel.buildFullModel();
			ChatController newChatController = new ChatController(fullModel);

			controlled.getModel().getChatModels().add(fullModel);
			controlled.getChatNamesContainer().getChildren().add(new Label(fullModel.getChatTitle()));
			controlled.getViewedChats().put(chatId, newChatController.getControlled());
			controlled.setCurrentViewedChat(newChatController.getControlled());
		}

		else {
			controlled.setCurrentViewedChat(controlled.getViewedChats().get(chatId));
		}
		controlled.canBeUpdated().setValue(true);
	}
	
	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}
	
	private void addBackButtonListener(Button back) {
		back.setOnAction((ActionEvent e) -> {
			ViewSwitcher.getInstance().switchToView(ViewName.CHATS_PREVIEW, (Object[]) null);
		});
	}

	
	private ChatsView controlled;
	private ChatsUpdater updater;

	public ChatsView getControlled() {
		return controlled;
	}

	
}
