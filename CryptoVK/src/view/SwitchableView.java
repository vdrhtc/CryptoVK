package view;

import controller.ViewSwitcher;

public interface SwitchableView extends View {
	
	public enum ViewName {
		AUTHORIZE_VIEW, CHATS_VIEW, CHAT_HOLDER
	}

	public ViewName getName();
	public void setViewSwitcher(ViewSwitcher VS);
	public void prepareModel();
	public ViewName redirectTo();
}
