package view;

import javafx.application.Application;
import javafx.stage.Stage;
import view.SwitchableView.ViewName;
import view.messaging.ChatsView;
import view.preview.ChatsPreview;
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
		VS.switchToView(ViewName.AUTHORIZE_VIEW, (Object[]) null);
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
