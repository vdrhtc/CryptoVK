package view;

import javafx.application.Application;
import javafx.stage.Stage;
import view.SwitchableView.ViewName;
import view.chats.ChatsPreview;
import view.messaging.ChatsView;
import controller.ViewSwitcher;

public class Launcher extends Application {

	public static ViewSwitcher VS;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		AuthorizeView AV = new AuthorizeView();
		ChatsPreview CPV = new ChatsPreview();
		ChatsView CV = new ChatsView();
		VS = ViewSwitcher.getInstance();
		VS.setViews(primaryStage, AV, CPV, CV);
		VS.switchToView(ViewName.AUTHORIZE_VIEW);
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
