package view.messaging;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Updated;
import model.messaging.ChatsModel;
import view.View;

public class ChatsView implements View, Updated {

	public ChatsView() {
		initRoot();
	}

	private void initRoot() {
		
		root.getStyleClass().add("chats-root");
		header.getStyleClass().add("chats-header");
		chatNamesContainer.getStyleClass().add("chats-names-container");

		back.setCancelButton(true);
		header.getChildren().addAll(back, title);
		root.setTop(new VBox(header, chatNamesContainer));
	
	}


	public void setCurrentViewedChat(ChatView CV) {
		this.root.setCenter(CV.getRoot());
	}
	
	public HashMap<Integer, ChatView> getViewedChats() {
		return viewedChats;
	}

	@Override
	public void update() {
		for(ChatView CV : viewedChats.values()) {
			CV.update();
		}
	}

	private HBox header = new HBox();
	private Label title = new Label("Чаты");
	private Button back = new Button("Назад");
	private BorderPane root = new BorderPane();
	private ChatsModel model = new ChatsModel();
	private FlowPane chatNamesContainer = new FlowPane();
	private HashMap<Integer, ChatView> viewedChats = new HashMap<>();
	private BooleanProperty canBeUpdated = new SimpleBooleanProperty();

	private static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.ALL);
	}
	
	public Button getBackButton() {
		return back;
	}

	@Override
	public Pane getRoot() {
		return root;
	}

	@Override
	public ViewName getName() {
		return ViewName.CHATS_VIEW;
	}

	public ChatsModel getModel() {
		return model;
	}

	@Override
	public void getLock() {
		model.getLock();
	}

	@Override
	public void releaseLock() {
		model.releaseLock();
	}

	public BooleanProperty canBeUpdated() {
		return canBeUpdated;
	}

	public FlowPane getChatNamesContainer() {
		return chatNamesContainer;
	}

}
