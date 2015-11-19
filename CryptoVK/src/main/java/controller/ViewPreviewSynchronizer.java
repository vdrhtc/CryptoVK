package controller;

import java.util.logging.Level;
import java.util.logging.Logger;

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
		CPVC.getReadStateWithIdProperty().addListener(new ChangeListener<ReadStateWithId>() {

			@Override
			public void changed(ObservableValue<? extends ReadStateWithId> observable, ReadStateWithId oldValue,
					ReadStateWithId RSId) {
				log.info("Synchronizing view read state, chatId: "+RSId.getChatId());
				ChatViewController chatController = CVC.getControllers().get(RSId.getChatId());
				if (chatController != null)
					chatController.readMessages();
				
			}
		});
	}

	private void addChatViewReadStateListener() {
		CVC.getReadStateWithIdProperty().addListener(new ListChangeListener<ReadStateWithId>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ReadStateWithId> c) {
				c.next();
				if (c.wasAdded())
					for (ReadStateWithId RSId : c.getAddedSubList()) {
						log.info("Synchronizing preview read state, chatId: "+RSId.getChatId());
						CPVC.getControlled().getModel().getLock();
						ChatPreview CP = CPVC.getPreviewById(RSId.getChatId());
						CP.setReadState(RSId.getRS());
						CP.getModel().setReadState(RSId.getRS());
						CPVC.getControlled().getModel().releaseLock();
						c.getList().remove(RSId);
					}
			}
			
		});
	}

	private ChatsViewController CVC;
	private ChatsPreviewController CPVC;
	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.ALL);
	}
}
