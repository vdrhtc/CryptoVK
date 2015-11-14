package controller;

import controller.ChatViewController.ReadStateWithId;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import view.ChatPreview;

public class ViewPreviewSynchronizer {

	public ViewPreviewSynchronizer(ChatsPreviewController CPVC, ChatsViewController CVC) {
		this.CPVC = CPVC;
		this.CVC = CVC;
		addChatViewReadStateListener();
	}
	
	private void addChatViewReadStateListener() {
		CVC.getReadStateWithIdProperty().addListener(new ChangeListener<ReadStateWithId>() {

			@Override
			public void changed(ObservableValue<? extends ReadStateWithId> observable, ReadStateWithId oldValue,
					ReadStateWithId newValue) {
				CPVC.getControlled().getModel().getLock();
				ChatPreview CP = CPVC.getPreviewById(newValue.getChatId());
				CP.setReadState(newValue.getRS());
				CP.getCurrentLoadedModel().setReadState(newValue.getRS());
				CPVC.getControlled().getModel().releaseLock();

			}
		});
	}

	private ChatsViewController CVC;
	private ChatsPreviewController CPVC;
 
}
 