package controller;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import view.View.ViewName;

public class ViewSwitcher {

	private ViewSwitcher() {
	}

	public void setViews(Stage stage, Controller... views) {

		ViewSwitcher.stage = stage;
		stage.show();
		for (Controller v : views) {
			this.controllers.put(v.getControlled().getName(), v);

//			this.scenes.put(v.getControlled().getName(), scene);

		}
		
		scene.getStylesheets().addAll("view/chatPreviewStyle.css", "view/chatsPreviewStyle.css",
				"view/messageViewStyle.css", "view/chatViewStyle.css", "view/chatsViewStyle.css",
				"view/nodes/attachmentsContainerStyle.css");

	}

	public void switchToView(ViewName name, Object... params) {
		
		ViewName redirectName = controllers.get(name).redirectTo();

		controllers.get(redirectName).prepareViewForSwitch(params);
		
		Pane root = controllers.get(redirectName).getControlled().getRoot();
		scene.setRoot(root);
		stage.setScene(scene);
		stage.show();
	}

	public Controller getController(ViewName name) {
		return controllers.get(name);
	}

	public static ViewSwitcher getInstance() {
		if (INSTANCE == null) {
			ViewSwitcher.INSTANCE = new ViewSwitcher();
			return INSTANCE;
		} else
			return INSTANCE;
	}

	private static Stage stage;
	private Scene scene = new Scene(new BorderPane());
	private static ViewSwitcher INSTANCE;
	private HashMap<ViewName, Controller> controllers = new HashMap<>();
//	private HashMap<ViewName, Scene> scenes = new HashMap<>();
}
