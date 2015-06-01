package controller;

import java.util.HashMap;

import javafx.scene.Scene;
import view.SwitchableView;
import view.SwitchableView.ViewName;

public class ViewSwitcher {

	private ViewSwitcher() {
		
	}
	
	public void setViews(Scene scene, SwitchableView... views) {
		this.scene = scene;
		for (SwitchableView v : views) {
			v.setViewSwitcher(this);
			this.views.put(v.getName(), v);
		}
	}
	
	public void switchToView(ViewName name) {
		ViewName redirectName = views.get(name).redirectTo();
		if(activeView != null)
			views.get(activeView).getRoot().getChildren().clear();
		views.get(redirectName).prepareModel();
		scene.setRoot(views.get(redirectName).buildRoot());
		this.activeView = redirectName;
	}
	
	public SwitchableView getView(ViewName name) {
		return views.get(name);
	}
	
	
	public static ViewSwitcher getInstance() {
		if (INSTANCE == null) {
			ViewSwitcher.INSTANCE = new ViewSwitcher();
			return INSTANCE;
		} else
			return INSTANCE;
	}
	
	private static ViewSwitcher INSTANCE;
	private ViewName activeView;
	private Scene scene;
	private HashMap<ViewName, SwitchableView> views = new HashMap<>();
}
