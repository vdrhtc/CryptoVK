package controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import controller.ChatViewController.ReadStateWithId;
import javafx.collections.ListChangeListener;
import view.ChatPreview;

public class ViewPreviewSynchronizer {

	public ViewPreviewSynchronizer(ChatsPreviewController CPVC, ChatsViewController CVC) {
		this.CPVC = CPVC;
		this.CVC = CVC;
		addChatViewReadStateListener();
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
						CP.getCurrentLoadedModel().setReadState(RSId.getRS());
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
