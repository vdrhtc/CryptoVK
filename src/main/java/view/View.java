package view;

import javafx.scene.layout.Pane;

public interface View {
	public enum ViewName {
		AUTHORIZE_VIEW, CHATS_PREVIEW, CHAT_PREVIEW, CHATS_VIEW, CHAT_VIEW, MESSAGE_VIEW
	}

	public ViewName getName();
	public Pane getRoot();
}
