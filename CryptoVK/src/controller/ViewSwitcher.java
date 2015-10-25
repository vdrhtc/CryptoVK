package controller;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.View.ViewName;

public class ViewSwitcher {

	private ViewSwitcher() {

	}

	public void setViews(Stage stage, Controller... views) {

		ViewSwitcher.stage = stage;

		for (Controller v : views) {
			this.controllers.put(v.getControlled().getName(), v);

			Scene scene = new Scene(new StackPane());
			// TODO: Сделать для каждого свои стайлшиты

			scene.getStylesheets().addAll("view/preview/chatPreviewStyle.css", "view/preview/chatsPreviewStyle.css",
					"view/messaging/messageViewStyle.css", "view/messaging/chatViewStyle.css",
					"view/messaging/chatsViewStyle.css");

			this.scenes.put(v.getControlled().getName(), scene);

		}
	}

	public void switchToView(ViewName name, Object... params) {
		ViewName redirectName = controllers.get(name).redirectTo();

		controllers.get(redirectName).prepareViewForSwitch(params);
		Scene scene = scenes.get(redirectName);
		scene.setRoot(controllers.get(redirectName).getControlled().getRoot());

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
	private static ViewSwitcher INSTANCE;
	private HashMap<ViewName, Controller> controllers = new HashMap<>();
	private HashMap<ViewName, Scene> scenes = new HashMap<>();
}
