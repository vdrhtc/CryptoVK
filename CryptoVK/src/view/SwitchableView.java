package view;

import java.util.ArrayList;

public interface SwitchableView extends View {
	
	public enum ViewName {
		AUTHORIZE_VIEW, CHATS_PREVIEW, CHATS_VIEW
	}

	public ViewName getName();
	public void getReadyForSwitch(Object... params);
	public ViewName redirectTo();
}
