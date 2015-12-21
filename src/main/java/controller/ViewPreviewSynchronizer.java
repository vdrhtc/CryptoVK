package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.ReadStatesDatabase.ChatReadState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import view.ChatPreview;

public class ViewPreviewSynchronizer {

	public ViewPreviewSynchronizer(ChatsPreviewController CPVC, ChatsViewController CVC) {
		this.CPVC = CPVC;
		this.CVC = CVC;
		addChatViewReadStateListener();
		addChatPreviewReadStateListener();
	}

	private void addChatPreviewReadStateListener() {
		CPVC.getReadStateWithIdProperty().addListener(new ChangeListener<ChatReadStateWithId>() {

			@Override
			public void changed(ObservableValue<? extends ChatReadStateWithId> observable, ChatReadStateWithId oldValue,
					ChatReadStateWithId RSId) {
				ChatViewController chatController = CVC.getControllers().get(RSId.getChatId());
				if (chatController != null && RSId.getRS() == ChatReadState.READ
						&& chatController.getControlled().getReadStateProperty().get() != ChatReadState.READ) {
					log.info("Synchronizing view read state, " + RSId.toString());
					chatController.readMessages();
				}
			}
		});
	}

	private void addChatViewReadStateListener() {
		CVC.getReadStateWithIdProperty().addListener(new ListChangeListener<ChatReadStateWithId>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ChatReadStateWithId> c) {
				c.next();
				if (c.wasAdded())
					for (ChatReadStateWithId RSId : c.getAddedSubList()) {
						log.info("Synchronizing preview read state, " + RSId.toString());
						ChatPreview CP = CPVC.getPreviewById(RSId.getChatId());
						CP.setReadState(RSId.getRS());
						c.getList().remove(RSId);
					}
			}

		});
	}

	private ChatsViewController CVC;
	private ChatsPreviewController CPVC;
	private static Logger log = LoggerFactory.getLogger(ViewPreviewSynchronizer.class);
}
