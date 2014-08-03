package controller;

import java.util.HashMap;

import javafx.scene.Scene;
import view.SwitchableView;
import view.SwitchableView.ViewName;

public class ViewSwitcher {

	public ViewSwitcher(Scene scene, SwitchableView... views) {
		this.scene = scene;
		for (SwitchableView v : views) {
			v.setViewSwitcher(this);
			this.views.put(v.getName(), v);
		}
	}
	
	public void switchToView(ViewName name) {
		ViewName realName = views.get(name).redirectTo();
		if(activeView != null)
			views.get(activeView).getRoot().getChildren().clear();
		views.get(realName).prepareModel();
		scene.setRoot(views.get(realName).buildRoot());
		this.activeView = realName;
	}
	
	public SwitchableView getView(ViewName name) {
		return views.get(name);
	}
	
	private ViewName activeView;
	private Scene scene;
	private HashMap<ViewName, SwitchableView> views = new HashMap<>();
}
