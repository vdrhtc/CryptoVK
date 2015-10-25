package controller;

import view.ChatsPreview;

public class ChatsPreviewUpdater extends LongPollUpdater {

	
	public ChatsPreviewUpdater(ChatsPreview updated) {
		super(updated.getModel(), updated);
	}
	
	public String getName() {
		return "Preview updater";
	}

}
