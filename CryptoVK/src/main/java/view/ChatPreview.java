package view;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.ImageOperator;
import data.ReadStatesDatabase.ReadState;
import javafx.css.PseudoClass;
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

	public void loadModel(ChatPreviewModel model) {
		if (!model.equals(currentLoadedModel)) {

			this.currentLoadedModel = model.clone();
			date.setText(model.getLastMessageDateString());
			title.setText(model.getTitle());
			getIcon(model);
			setReadState(model.getReadState());
			lastMessage.setText(model.getLastMessage().getText());
			getLastSenderPhoto(model);
//			ImageOperator.clipImage(lastSenderPhoto);
		}
	}

	private void initRoot() {

		lastMessageContainer.getStyleClass().add("chat-entry-last-message-container");
		metaInfoContainer.getStyleClass().add("chat-entry-meta-info-container");
		lastMessage.getStyleClass().add("chat-entry-message");
		title.getStyleClass().add("chat-entry-title");
		icon.getStyleClass().add("chat-entry-icon");
		root.getStyleClass().add("chat-entry-hbox");
		lastSenderPhoto.getStyleClass().add("chat-entry-last-sender-photo");
		read.getStyleClass().addAll("chat-preview-button", "chat-preview-read-button");
		leftContainer.getStyleClass().add("chat-entry-left-container");

		metaInfoContainer.getChildren().addAll(title, date);
		leftContainer.getChildren().addAll(metaInfoContainer, read);
		lastMessageContainer.getChildren().addAll(lastSenderPhoto, lastMessage);
		root.getChildren().addAll(icon, leftContainer, lastMessageContainer);
		HBox.setHgrow(lastMessageContainer, Priority.ALWAYS);
	}

	private void getLastSenderPhoto(ChatPreviewModel model) {
		ImageOperator.asyncLoadSmallImage(lastSenderPhoto, model.getLastMessageSender().getPhotoURL());
	}

	private void getIcon(ChatPreviewModel model) {
		ImageOperator.asyncLoadImage(icon, model.getChatIconURL().toArray(new String[0]));//(model.getChatIconURL().toArray(new String[0]));
	}

	public void setReadState(ReadState RS) {
		switch (RS) {
		case READ:
			setMessageContainerPseusoClass(false, false, false);
			break;
		case UNREAD:
			setMessageContainerPseusoClass(true, false, false);
			break;
		case VIEWED:
			setMessageContainerPseusoClass(false, true, false);
			break;
		case POSTPONED:
			setMessageContainerPseusoClass(false, false, true);
			break;
		}
	}

	private void setMessageContainerPseusoClass(boolean unread, boolean viewed, boolean postponed) {
		lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), unread);
		lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("viewed"), viewed);
		lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("posponed"), postponed);
	}

	private HBox root = new HBox();
	private Label date = new Label();
	private Label title = new Label();
	private Button read = new Button();
	private Label lastMessage = new Label();
	private ImageView icon = new ImageView();
	private VBox metaInfoContainer = new VBox();
	private ChatPreviewModel currentLoadedModel;
	private HBox lastMessageContainer = new HBox();
	private ImageView lastSenderPhoto = new ImageView();
	private HBox leftContainer = new HBox();

	private static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.WARNING);
	}

	@Override
	public HBox getRoot() {
		return this.root;
	}

	public ChatPreviewModel getModel() {
		return currentLoadedModel;
	}

	@Override
	public ViewName getName() {
		return ViewName.CHAT_PREVIEW;
	}

	public Button getRead() {
		return read;
	}
	
	public HBox getLeftContainer() {
		return leftContainer ;
	}

}
