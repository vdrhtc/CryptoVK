package view.preview;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import model.preview.ChatPreviewModel;
import view.View;
import controller.ChatPreviewController;
import data.ImageOperator;

public class ChatPreview implements View {


	public ChatPreview(ChatPreviewModel model, ReadOnlyDoubleProperty parentHeight) {
		
		this.updateModel(model);
		this.controller = new ChatPreviewController(this);
	}

	private void setLastMessageReadState(ChatPreviewModel model) {
		if(!model.isRead())
			this.lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), true);
		else
			this.lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"),	 false);
	}

	private void clipLastSenderPhoto() {
		Rectangle rR = new Rectangle(0, 0, lastSenderPhoto.getImage().getWidth(), lastSenderPhoto.getImage().getHeight());
		rR.setArcHeight(10);
		rR.setArcWidth(10);
		lastSenderPhoto.setClip(rR);
	}

	private Image getLastSenderPhoto(ChatPreviewModel model) {
		Image im = new Image(model.getLastMessageSender().getPhotoURL(), icon
				.getImage().getWidth() * 0.66, icon.getImage().getHeight()*0.66,
				true, true);
		return im;
	}

	private void initRoot(ReadOnlyDoubleProperty parentHeiht) {
		

		this.metaInfoContainer.getStyleClass().add(
				"chat-entry-meta-info-container");
		this.lastMessageContainer.getStyleClass().add(
				"chat-entry-last-message-container");
		this.title.getStyleClass().add("chat-entry-title");
		this.lastMessage.getStyleClass().add("chat-entry-message");
		this.icon.getStyleClass().add("chat-entry-icon");
		
		root.getStyleClass().add("chat-entry-hbox");
		root.prefHeightProperty().bind(
				parentHeiht.divide(ChatsPreview.CHATS_PER_PAGE));
		metaInfoContainer.getChildren().addAll(title, date);
		lastMessageContainer.getChildren().addAll(lastSenderPhoto, lastMessage);
		root.getChildren()
				.addAll(icon, metaInfoContainer, lastMessageContainer);
		HBox.setHgrow(lastMessageContainer, Priority.ALWAYS);
		
	}

	private Image getIcon(ChatPreviewModel model) {
		return ImageOperator.getIconFrom(model.getChatIconURL());
	}

	private HBox root = new HBox();
	private Label date = new Label();
	private Label title = new Label();
	private Label lastMessage = new Label();
	private ImageView icon = new ImageView();
	private VBox metaInfoContainer = new VBox();
	private ChatPreviewModel currentLoadedModel;
	private HBox lastMessageContainer = new HBox();
	private ImageView lastSenderPhoto = new ImageView();
	
	private ChatPreviewController controller;

	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.OFF);
	}

	public void updateModel(ChatPreviewModel model) {
		if (!this.currentLoadedModel.equals(model)) {
			
			this.currentLoadedModel = model.clone();
			date.setText(model.getLastMessageDateString());
			title.setText(model.getTitle());
			icon.setImage(getIcon(model));
			setLastMessageReadState(model);
			lastMessage.setText(model.getLastMessage());
			lastSenderPhoto.setImage(getLastSenderPhoto(model));
			clipLastSenderPhoto();
		}
	}

	@Override
	public HBox getRoot() {
		return this.root;
	}

	public ChatPreviewModel getCurrentLoadedModel() {
		return currentLoadedModel;
	}


}
