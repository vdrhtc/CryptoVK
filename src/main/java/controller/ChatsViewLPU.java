package controller;

import view.ChatsView;

public class ChatsViewLPU extends LongPollUpdater {
	
	public ChatsViewLPU(ChatsView updated) {
		super(updated.getModel(), updated);
	}

	public String getName() {
		return "CU";
	}
	
}
