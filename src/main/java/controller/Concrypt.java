package controller;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import javafx.application.Application;
import javafx.stage.Stage;
import view.View.ViewName;

public class Concrypt extends Application {

	public static ViewSwitcher VS;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		Thread.currentThread().setName("JFX");
		
		AuthorizeViewController AVC = new AuthorizeViewController();
		ChatsPreviewController CPVC = new ChatsPreviewController();
		ChatsViewController CVC = new ChatsViewController();
		new ViewPreviewSynchronizer(CPVC, CVC);
		VS = ViewSwitcher.getInstance();
		VS.setViews(primaryStage, AVC, CPVC, CVC);
		VS.switchToView(ViewName.AUTHORIZE_VIEW, (Object[]) null);
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
