package view;


public interface SwitchableView extends View {
	
	public enum ViewName {
		AUTHORIZE_VIEW, CHATS_PREVIEW, CHATS_HOLDER
	}

	public ViewName getName();
	public void getReadyForSwitch();
	public ViewName redirectTo();
}
