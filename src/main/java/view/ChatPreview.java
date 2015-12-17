package view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.ImageOperator;
import data.ReadStatesDatabase.ChatReadState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.ChatPreviewModel;

public class ChatPreview implements View {

	public ChatPreview(ChatPreviewModel model) {
		this.loadModel(model);
		this.initRoot();
	}
	
	// TODO Think about the structure of references 
	public void loadModel(ChatPreviewModel model) {
		if (model.isInvalidated()) { 
			this.currentModel = model;
			date.setText(model.getLastMessageDateString());
			title.setText(model.getTitle());
			getIcon(model);
			setReadState(model.getReadState());
			lastMessage.setText(model.getLastMessage().getText());
			lastSenderName.setText(model.getLastMessageSender().getFirstName());
			getLastSenderPhoto(model);
			unreadCounter.setText(model.getUnreadMessagesCount().toString());
			model.setInvalidated(false);
		}
	}

	private void initRoot() {

		readButton.getStyleClass().addAll("chat-preview-button", "chat-preview-read-button");
		lastMessageContainer.getStyleClass().add("chat-entry-last-message-container");
		metaInfoContainer.getStyleClass().add("chat-entry-meta-info-container");
		lastSenderName.getStyleClass().add("chat-entry-last-sender-name");
		lastSenderPhoto.getStyleClass().add("chat-entry-last-sender-photo");
		leftContainer.getStyleClass().add("chat-entry-left-container");
		lastMessage.getStyleClass().add("chat-entry-message");
		title.getStyleClass().add("chat-entry-title");
		icon.getStyleClass().add("chat-entry-icon");
		root.getStyleClass().add("chat-entry-hbox");
		date.getStyleClass().add("chat-entry-date");
		unreadImage.getStyleClass().add("chat-entry-unread-icon");
		unreadCounter.getStyleClass().add("chat-entry-unread-counter");
		
		metaInfoContainer.getChildren().addAll(title, date);
		leftContainer.getChildren().addAll(metaInfoContainer, readButton);
		unreadImage.setFitHeight(20);
		unreadImage.setFitWidth(20);
		unreadCounterContainer.getChildren().addAll(unreadCounter, unreadImage);
		unreadCounterContainer.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(unreadCounterContainer, Priority.ALWAYS);
		lastMessageContainer.getChildren().addAll(lastSenderPhoto, new VBox(lastSenderName, lastMessage), unreadCounterContainer);
		root.getChildren().addAll(icon, leftContainer, lastMessageContainer);
		HBox.setHgrow(lastMessageContainer, Priority.ALWAYS);
	}

	private void getLastSenderPhoto(ChatPreviewModel model) {
		ImageOperator.asyncLoadSmallIcon(lastSenderPhoto, model.getLastMessageSender().getPhotoURL());
	}

	private void getIcon(ChatPreviewModel model) {
		ImageOperator.asyncLoadLargeIcon(icon, model.getChatIconURL().toArray(new String[0]));
	}

	public void setReadState(ChatReadState chatReadState) {
		currentModel.setReadState(chatReadState);
		unreadCounter.setText(currentModel.getUnreadMessagesCount().toString());
		if (chatReadState == ChatReadState.UNREAD) 
			unreadCounterContainer.setVisible(true);
		else
			unreadCounterContainer.setVisible(false);
			
		readStateProperty.set(chatReadState);
		switch (chatReadState) {
		case READ:
			setMessageContainerPseusoClass(false, false);
			leftContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("postponed"), false);
			break;
		case UNREAD:
			setMessageContainerPseusoClass(true, false);
			leftContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("postponed"), false);
			break;
		case VIEWED:
			setMessageContainerPseusoClass(false, true);
			leftContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("postponed"), false);
			break;
		case POSTPONED:
			setMessageContainerPseusoClass(false, true);
			leftContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("postponed"), true);
			break;
		}
	}

	private void setMessageContainerPseusoClass(boolean unread, boolean viewed) {
		lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), unread);
		lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("viewed"), viewed);
	}

	private HBox root = new HBox();
	private Label date = new Label();
	private Label title = new Label();
	private Button readButton = new Button();
	private Label lastMessage = new Label();
	private ImageView icon = new ImageView();
	private Label lastSenderName = new Label();
	private VBox metaInfoContainer = new VBox();
	private ChatPreviewModel currentModel;
	HBox unreadCounterContainer = new HBox();
	private HBox lastMessageContainer = new HBox();
	private ImageView lastSenderPhoto = new ImageView();
	private Label unreadCounter = new Label();
	private ImageView unreadImage = new ImageView();
	private ObjectProperty<ChatReadState> readStateProperty = new SimpleObjectProperty<>();
	private HBox leftContainer = new HBox();

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ChatPreview.class);

	public ObjectProperty<ChatReadState> getReadStateProperty() {
		return readStateProperty;
	}

	@Override
	public HBox getRoot() {
		return this.root;
	}

	public ChatPreviewModel getModel() {
		return currentModel;
	}

	@Override
	public ViewName getName() {
		return ViewName.CHAT_PREVIEW;
	}

	public Button getRead() {
		return readButton;
	}
	
	public HBox getLeftContainer() {
		return leftContainer ;
	}

}
