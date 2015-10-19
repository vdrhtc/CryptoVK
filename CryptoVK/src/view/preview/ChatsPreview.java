package view.preview;

import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.preview.ChatsPreviewModel;
import view.SwitchableView;
import controller.ChatsPreviewController;
import data.DataOperator;

public class ChatsPreview implements SwitchableView {

	public static final int LOAD_NEW_COUNT = 10;
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
		
		this.controller = new ChatsPreviewController(this);

	}

	@Override
	public Pane getRoot() {
		return root;
	}

	public void loadNewEntries(int count) {
		ArrayList<Node> toAppend = new ArrayList<>();
		int oldChatEntriesCount = chatEntriesCount;
		for (int i = oldChatEntriesCount; i < oldChatEntriesCount+count; i++) {
			chatEntriesCount++;
			toAppend.add(buildHBorder());
			ChatPreview newEntry = new ChatPreview(model.getChats().get(i), chatsContainer.heightProperty());
			entries.add(newEntry);
			toAppend.add(newEntry.getRoot());

//			progressBar.setProgress(1.0/LOAD_NEW_COUNT*(i-oldChatEntriesCount));
//			System.out.println(progressBar.getProgress());
		}
		chatsLayout.getChildren().addAll(toAppend);
	}

	public Integer getChatEntriesCount() {
		return chatEntriesCount;
	}

	private Separator buildHBorder() {
		Separator hBorder = new Separator(Orientation.HORIZONTAL);
		hBorder.getStyleClass().add("chats-border");
		return hBorder;
	}

	public void update() {
		for (int i = 0; i < entries.size(); i++) 
			entries.get(i).update(model.getChats().get(i));
	}
	
	private BorderPane root;
	private HBox statusBar = new HBox(); 
	private Integer chatEntriesCount = 0;
	private VBox chatsLayout = new VBox();
	private Label header = new Label("Чаты");
	private ChatsPreviewController controller;
	private Label statusMessage = new Label("Ready");
	private ProgressBar progressBar = new ProgressBar(0);
	private ChatsPreviewModel model = new ChatsPreviewModel();
	private ArrayList<ChatPreview> entries = new ArrayList<>();
	private ScrollPane chatsContainer = new ScrollPane(chatsLayout);
	private BooleanProperty canBeUpdated = new SimpleBooleanProperty(false);


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

	@Override
	public void getReadyForSwitch(Object... params) {
		
		if(chatEntriesCount > 0)
			return;
			
		model.initializeEntries();
		loadNewEntries(CHATS_PER_PAGE);
		canBeUpdated.setValue(true);
	}

	@Override
	public ViewName redirectTo() {
		return getName();
	}
	
	public ScrollPane getChatsContainer() {
		return chatsContainer;
	}
	
	public VBox getChatsLayout() {
		return chatsLayout;
	}
	
	public double getEntryHeight() {
		return this.root.getHeight()/CHATS_PER_PAGE;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}
	
	public ChatsPreviewModel getModel() {
		return model;
	}


}
