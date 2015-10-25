package controller;

import javafx.beans.value.ObservableValue;
import view.AuthorizeView;
import view.View.ViewName;

public class AuthorizeViewController implements Controller {
	public AuthorizeViewController() {
		this.controlled = new AuthorizeView();
		addLocationListener();
	}
	
	@Override
	public void prepareViewForSwitch(Object... params) {
		return;
	}
	
	@Override
	public ViewName redirectTo() {
		return controlled.tokenAvailableAndValid() ? ViewName.CHATS_PREVIEW : controlled.getName();
	}
	
	private void addLocationListener() {
		controlled.getBrowserLocationProperty()
		.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
			controlled.getAddressBar().setText(newValue);
			if(controlled.getTokenFromURL(newValue))
				ViewSwitcher.getInstance().switchToView(ViewName.CHATS_PREVIEW, (Object) null);
		});
	}
	
	public AuthorizeView getControlled() {
		return controlled;
	}


	private AuthorizeView controlled;

}
