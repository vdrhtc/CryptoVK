package controller;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.ChatModel;
import model.ChatPreviewModel;
import model.DialogModel;
import model.TalkModel;
import view.ChatsView;
import view.View.ViewName;

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
		ChatPreviewModel previewModel = (ChatPreviewModel) params[0];
		Integer chatId = previewModel.getChatId();

		if (!controlled.getViewedChats().containsKey(chatId)) {

			ChatModel fullModel = new ChatModel(previewModel.getChatId(), previewModel.getChatIconURL(),
					previewModel.getTitle());
			if (previewModel.getInterlocutors().size() == 1)
				fullModel = new DialogModel(fullModel, previewModel.getInterlocutors().get(0));
			else
				fullModel = new TalkModel(fullModel);

			ChatViewController newChatController = new ChatViewController(fullModel, previewModel);

			controlled.getModel().getLock();
			controlled.getModel().getChatModels().add(fullModel);
			controlled.releaseLock();
			
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
	private ChatsViewLPU updater;

	public ChatsView getControlled() {
		return controlled;
	}

}
