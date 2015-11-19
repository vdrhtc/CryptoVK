package controller;

import view.ChatsPreview;

public class ChatsPreviewLPU extends LongPollUpdater {

	
	public ChatsPreviewLPU(ChatsPreview updated) {
		super(updated.getModel(), updated);
	}
	
	public String getName() {
		return "Preview updater";
	}

}
