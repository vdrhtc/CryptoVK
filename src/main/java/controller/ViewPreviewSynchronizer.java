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
				log.info("Synchronizing view read state, "+RSId.toString());
				ChatViewController chatController = CVC.getControllers().get(RSId.getChatId());
				if (chatController != null && RSId.getRS()==ChatReadState.READ)
					chatController.readMessages();
				
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
						log.info("Synchronizing preview read state, "+RSId.toString());
						ChatPreview CP = CPVC.getPreviewById(RSId.getChatId());
						Thread t = new Thread(()-> {
							Thread.currentThread().setName("PMS");;
							CPVC.getControlled().getModel().getLock("ViewPreviewSynchronizer");
							CP.getModel().setReadState(RSId.getRS());
							CPVC.getControlled().getModel().releaseLock("ViewPreviewSynchronizer");
						});
						t.start();
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
