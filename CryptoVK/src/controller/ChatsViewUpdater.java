package controller;

import view.ChatsView;

public class ChatsViewUpdater extends LongPollUpdater {
	
	public ChatsViewUpdater(ChatsView updated) {
		super(updated.getModel(), updated);
	}

	public String getName() {
		return "Chats updater";
	}
	
}
