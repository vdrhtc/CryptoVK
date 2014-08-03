package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.SwitchableView.ViewName;
import view.chats.ChatsView;
import controller.ViewSwitcher;

public class Launcher extends Application {

	public static ViewSwitcher VS;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		Scene scene = new Scene(new StackPane());

		AuthorizeView AV = new AuthorizeView();
		ChatsView CV = new ChatsView();
		VS = new ViewSwitcher(scene, AV, CV);
		VS.switchToView(ViewName.AUTHORIZE_VIEW);
		

		scene.getStylesheets().addAll("view/chats/chatsEntryViewStyle.css", "view/chats/chatsViewStyle.css");
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
