package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.SwitchableView.ViewName;
import view.chats.ChatsPreview;
import view.messaging.ChatsHolder;
import controller.ViewSwitcher;

public class Launcher extends Application {

	public static ViewSwitcher VS;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		Scene scene = new Scene(new StackPane());

		AuthorizeView AV = new AuthorizeView();
		ChatsPreview CPV = new ChatsPreview();
		ChatsHolder CH = new ChatsHolder();
		VS = ViewSwitcher.getInstance();
		VS.setViews(scene, AV, CPV);
		VS.switchToView(ViewName.AUTHORIZE_VIEW);
		

		scene.getStylesheets().addAll("view/chats/chatPreviewStyle.css", "view/chats/chatsPreviewStyle.css");
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
