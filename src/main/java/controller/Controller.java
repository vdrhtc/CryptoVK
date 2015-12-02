package controller;

import view.View;
import view.View.ViewName;

public interface Controller {
	
	public void prepareViewForSwitch(Object... params);
	public View getControlled();
	public ViewName redirectTo();
}
