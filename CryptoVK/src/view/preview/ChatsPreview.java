package view.preview;

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
import model.Updated;
import model.preview.ChatsPreviewModel;
import view.View;

public class ChatsPreview implements Updated, View {

	public static final int LOAD_NEW_COUNT = 4;
	public static final int CHATS_PER_PAGE = 10;
	public static final ViewName NAME = ViewName.CHATS_PREVIEW;

	public ChatsPreview() {
		
		initRoot();
		
	}
	
	private void initRoot() {
		

		this.header.getStyleClass().add("chats-header");
		this.statusMessage.setId("chats-status-message");
		this.statusBar.getStyleClass().add("chats-status-bar");
		this.chatsContainer.getStyleClass().add("chats-container");
		
		this.root = new BorderPane();
		
		this.root.getStyleClass().add("chats-root");
				
		this.accessExpirationInfo.setText("Доступ без пароля до "+DataOperator.getLastTokenExpirationDate());
		this.statusBar.getChildren().addAll(statusMessage, progressBar, accessExpirationInfo);
		
		this.root.setCenter(chatsContainer);
		this.root.setTop(header);
		this.root.setBottom(statusBar);
	}

	@Override
	public Pane getRoot() {
		return root;
	}

	public Integer getPreviewsCount() {
		return chatPreviewsCount;
	}

	public void setPreviewsCount(Integer chatEntriesCount) {
		this.chatPreviewsCount = chatEntriesCount;
	}

	public Separator buildHBorder() {
		Separator hBorder = new Separator(Orientation.HORIZONTAL);
		hBorder.getStyleClass().add("chats-border");
		return hBorder;
	}

	public void update() {
		for (int i = 0; i < previews.size(); i++) 
			previews.get(i).loadModel(model.getChats().get(i));
	}
	
	private BorderPane root;
	private HBox statusBar = new HBox(); 
	private Integer chatPreviewsCount = 0;
	private VBox chatsLayout = new VBox();
	private Label header = new Label("Чаты");
	private Label statusMessage = new Label("Ready");
	private ProgressBar progressBar = new ProgressBar(0);
	private ChatsPreviewModel model = new ChatsPreviewModel();
	private ArrayList<ChatPreview> previews = new ArrayList<>();
	private ScrollPane chatsContainer = new ScrollPane(chatsLayout);
	private BooleanProperty canBeUpdated = new SimpleBooleanProperty(false);

	public ArrayList<ChatPreview> getPreviews(){
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
