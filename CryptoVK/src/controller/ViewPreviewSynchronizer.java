package controller;


import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.ChatPreviewModel;
import view.ChatsPreview;
import view.ChatsView;

public class ViewPreviewSynchronizer extends Service<Void>{

	public ViewPreviewSynchronizer(ChatsPreview CPV, ChatsView CV) {
		this.CPV = CPV;
		this.CV = CV;
	}
	
	@Override
	protected Task<Void> createTask() {
		Task<Void> synchronizeReadStates = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				Thread.currentThread().setName("Read states synchronizer");
				while(!isCancelled()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
					
					Platform.runLater(() -> {
						CPV.getLock();
						for (ChatPreviewModel CPM : CPV.getModel().getChats()) {
							
						}
					});
				}
				
				
				return null;
			}
		};
		return synchronizeReadStates;
	}
	
	private ChatsPreview CPV;
	private ChatsView CV;

}
