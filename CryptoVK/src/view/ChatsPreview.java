package view;

import java.util.ArrayList;

import data.DataOperator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.ChatsPreviewModel;
import model.Updated;

public class ChatsPreview implements Updated, View {

	public static final int LOAD_NEW_COUNT = 4;
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

		this.accessExpirationInfo.setText("Token valid until " + DataOperator.getLastTokenExpirationDate());
		this.statusBar.getChildren().addAll(statusMessage, progressBar, accessExpirationInfo);

		header.getChildren().addAll(new VBox(title, lastSeenOnline));
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
	private Label statusMessage = new Label("Ready");
	private ProgressBar progressBar = new ProgressBar(0);
	private ArrayList<ChatPreview> previews = new ArrayList<>();
	private ScrollPane chatsContainer = new ScrollPane(chatsLayout);
	private BooleanProperty canBeUpdated = new SimpleBooleanProperty(false);

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

	private Label accessExpirationInfo = new Label();

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
	public void getLock() {
		model.getLock();
	}

	@Override
	public void releaseLock() {
		model.releaseLock();
	}

}
