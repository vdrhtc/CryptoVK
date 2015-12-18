package view;

import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.ChatsPreviewModel;
import model.Updated;

public class ChatsPreview implements Updated, View {

	public static final int LOAD_NEW_COUNT = 5;
	public static final int CHATS_PER_PAGE = 10;
	public static final ViewName NAME = ViewName.CHATS_PREVIEW;

	public ChatsPreview() {
		this.model = new ChatsPreviewModel();
		initRoot();
	}

	private void initRoot() {
		this.header.getStyleClass().add("chats-preview-header");
		this.title.getStyleClass().add("chats-preview-title");
		this.statusMessage.setId("chats-status-message");
		this.statusBar.getStyleClass().add("chats-status-bar");
		this.chatsContainer.getStyleClass().add("chats-container");
		this.root.getStyleClass().add("chats-root");
		this.lastSeenOnline.getStyleClass().add("chats-preview-last-seen");
		this.unreadMessagesCounter.getStyleClass().add("chats-preview-counter");
		this.countersContainer.getStyleClass().add("chats-counters-container");
		this.unreadImage.getStyleClass().add("chats-unread-icon");
		
		this.unreadImage.setFitHeight(50);
		this.unreadImage.setFitWidth(50);
		
		this.countersContainer.getChildren().addAll(unreadMessagesCounter, unreadImage);
		this.statusBar.getChildren().addAll(statusMessage, progressBar);
		header.getChildren().addAll(new VBox(title, lastSeenOnline), countersContainer);
		HBox.setHgrow(countersContainer, Priority.ALWAYS);
		this.root.setCenter(chatsContainer);
		this.root.setTop(header);
		this.root.setBottom(statusBar);

	}

	@Override
	public Pane getRoot() {
		return root;
	}

	public Separator buildHBorder() {
		Separator hBorder = new Separator(Orientation.HORIZONTAL);
		hBorder.getStyleClass().add("chats-border");
		return hBorder;
	}

	public void update(Object... params) {
		for (int i = 0; i < previews.size(); i++)

			previews.get(i).loadModel(model.getChats().get(i));
	}

	private ChatsPreviewModel model;
	private HBox header = new HBox();
	private HBox statusBar = new HBox();
	private VBox chatsLayout = new VBox();
	private Label title = new Label("Chats");
	private BorderPane root = new BorderPane();
	private Label lastSeenOnline = new Label();
	private HBox countersContainer = new HBox();
	private Label statusMessage = new Label("Ready");
	private Label unreadMessagesCounter = new Label();
	private ProgressBar progressBar = new ProgressBar(0);
	private ArrayList<ChatPreview> previews = new ArrayList<>();
	private ScrollPane chatsContainer = new ScrollPane(chatsLayout);
	private BooleanProperty canBeUpdated = new SimpleBooleanProperty(false);
	private ImageView unreadImage = new ImageView(
			new Image(ClassLoader.class.getResourceAsStream("/assets/unread.png")));

	public ImageView getUnreadImage() {
		return unreadImage;
	}
	
	public Label getUnreadMessagesCounter() {
		return unreadMessagesCounter;
	}

	public Label getLastSeenOnline() {
		return lastSeenOnline;
	}

	public ArrayList<ChatPreview> getPreviews() {
		return previews;
	}

	public BooleanProperty canBeUpdated() {
		return canBeUpdated;
	}

	public Label getStatusMessage() {
		return statusMessage;
	}

	@Override
	public ViewName getName() {
		return ChatsPreview.NAME;
	}

	public ScrollPane getChatsContainer() {
		return chatsContainer;
	}

	public VBox getChatsLayout() {
		return chatsLayout;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public ChatsPreviewModel getModel() {
		return model;
	}

	@Override
	public void getLock(String takerName) {
		model.getLock(takerName);
	}

	@Override
	public void releaseLock(String takerName) {
		model.releaseLock(takerName);
	}

}
