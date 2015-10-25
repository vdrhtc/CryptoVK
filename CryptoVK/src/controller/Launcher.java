package controller;

import java.util.Properties;

import javafx.application.Application;
import javafx.stage.Stage;
import view.View.ViewName;

public class Launcher extends Application {

	public static ViewSwitcher VS;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		Properties props = System.getProperties();
		props.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s, %2$s [%1$tc]%n");
		
		AuthorizeViewController AVC = new AuthorizeViewController();
		ChatsPreviewController CPVC = new ChatsPreviewController();
		ChatsController CVC = new ChatsController();
		VS = ViewSwitcher.getInstance();
		VS.setViews(primaryStage, AVC, CPVC, CVC);
		VS.switchToView(ViewName.AUTHORIZE_VIEW, (Object[]) null);
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
