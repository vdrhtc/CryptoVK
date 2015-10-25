package view.preview;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.ImageOperator;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.preview.ChatPreviewModel;
import view.View;

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
			icon.setImage(getIcon(model));
			setLastMessageReadState(model);
			lastMessage.setText(model.getLastMessage());
			lastSenderPhoto.setImage(getLastSenderPhoto(model));
			ImageOperator.clipImage(lastSenderPhoto);
		}
	}

	private void initRoot() {

		lastMessageContainer.getStyleClass().add("chat-entry-last-message-container");
		metaInfoContainer.getStyleClass().add("chat-entry-meta-info-container");
		lastMessage.getStyleClass().add("chat-entry-message");
		title.getStyleClass().add("chat-entry-title");
		icon.getStyleClass().add("chat-entry-icon");
		root.getStyleClass().add("chat-entry-hbox");
		
		ImageOperator.clipImage(icon);
		metaInfoContainer.getChildren().addAll(title, date);
		lastMessageContainer.getChildren().addAll(lastSenderPhoto, lastMessage);
		root.getChildren().addAll(icon, metaInfoContainer, lastMessageContainer);
		HBox.setHgrow(lastMessageContainer, Priority.ALWAYS);
	}

	private void setLastMessageReadState(ChatPreviewModel model) {
		if (!model.isRead())
			this.lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), true);
		else
			this.lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), false);
	}


	private Image getLastSenderPhoto(ChatPreviewModel model) {
		Image im = new Image(model.getLastMessageSender().getPhotoURL(), icon.getImage().getWidth() * 0.66,
				icon.getImage().getHeight() * 0.66, true, true);
		return im;
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

	private static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.OFF);
	}

	@Override
	public HBox getRoot() {
		return this.root;
	}

	public ChatPreviewModel getCurrentLoadedModel() {
		return currentLoadedModel;
	}

	@Override
	public ViewName getName() {
		return ViewName.CHAT_PREVIEW;
	}

}
