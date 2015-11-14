package view;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.IIOException;

import data.ImageOperator;
import data.ReadStatesDatabase.ReadState;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
			icon.setImage(getIcon(model));
			setReadState(model.getReadState());
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

	private Image getLastSenderPhoto(ChatPreviewModel model) {
		Image im = ImageOperator.getLastSenderPhotoFrom(model.getLastMessageSender().getPhotoURL());
		return im;
	}

	private Image getIcon(ChatPreviewModel model) {
		Image im;
		try {
			im = ImageOperator.getIconFrom(model.getChatIconURL().toArray(new String[0]));
		} catch (IIOException e) {
			log.warning("Couldn't load the icon for " + this.currentLoadedModel.getChatId()
					+ "! Retrying in .5 seconds...");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			im = getIcon(model);
		}
		return im;
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
	private Label lastMessage = new Label();
	private ImageView icon = new ImageView();
	private VBox metaInfoContainer = new VBox();
	private ChatPreviewModel currentLoadedModel;
	private HBox lastMessageContainer = new HBox();
	private ImageView lastSenderPhoto = new ImageView();

	private static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.WARNING);
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
