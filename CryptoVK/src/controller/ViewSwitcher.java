package controller;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.SwitchableView;
import view.SwitchableView.ViewName;

public class ViewSwitcher {

	private ViewSwitcher() {
		
	}
	
	public void setViews(Stage stage, SwitchableView... views) {

		this.stage = stage;
		
		for (SwitchableView v : views) {
			this.views.put(v.getName(), v);
			this.scenes.put(v.getName(), new Scene(new StackPane()));
		}
	}
	
	public void switchToView(ViewName name) {
		ViewName redirectName = views.get(name).redirectTo();
		
		views.get(redirectName).getReadyForSwitch();
		Scene scene = scenes.get(redirectName);
		scene.setRoot(views.get(redirectName).getRoot());
		this.activeView = redirectName;
		
		scene.getStylesheets().addAll("view/chats/chatPreviewStyle.css", "view/chats/chatsPreviewStyle.css");
		
		stage.setScene(scene);
		stage.show();
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
	
	private static Stage stage;
	private static ViewSwitcher INSTANCE;
	private ViewName activeView;
	private HashMap<ViewName, SwitchableView> views = new HashMap<>();
	private HashMap<ViewName, Scene> scenes = new HashMap<>();
}
