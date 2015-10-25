package controller;

import view.messaging.ChatsView;

public class ChatsUpdater extends LongPollUpdater {
	
	public ChatsUpdater(ChatsView updated) {
		super(updated.getModel(), updated);
	}

	public String getName() {
		return "Chats updater";
	}
	
}
