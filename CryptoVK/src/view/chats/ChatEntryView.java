package view.chats;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.ImageOperator;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import model.ChatEntry;
import view.View;

public class ChatEntryView implements View {


	public ChatEntryView(ChatEntry model, ReadOnlyDoubleProperty parentHeight) {
		
		this.synchronizeWithModel(model);

		this.metaInfoContainer.getStyleClass().add(
				"chat-entry-meta-info-container");
		this.lastMessageContainer.getStyleClass().add(
				"chat-entry-last-message-container");
		this.title.getStyleClass().add("chat-entry-title");
		this.lastMessage.getStyleClass().add("chat-entry-message");
		this.icon.getStyleClass().add("chat-entry-icon");
		this.initRoot(parentHeight);
	}

	private void synchronizeWithModel(ChatEntry model) {
		this.quasiModel = model.clone();
		date.setText(model.getLastMessageDate());
		title.setText(model.getTitle());
		icon.setImage(getIcon(model));
		setLastMessageReadState(model);
		lastMessage.setText(model.getLastMessage());
		lastSenderPhoto.setImage(getLastSenderPhoto(model));
		clipLastSenderPhoto();
	}

	private void setLastMessageReadState(ChatEntry model) {
		if(!model.isRead())
			this.lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), true);
		else
			this.lastMessageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"),	 false);
	}

	private void clipLastSenderPhoto() {
		Rectangle rR = new Rectangle(0, 0, lastSenderPhoto.getImage().getWidth(),lastSenderPhoto.getImage().getHeight());
		rR.setArcHeight(10);
		rR.setArcWidth(10);
		lastSenderPhoto.setClip(rR);
	}

	private Image getLastSenderPhoto(ChatEntry model) {
		Image im = new Image(model.getLastMessageSender().getPhotoURL(), icon
				.getImage().getWidth() * 0.66, icon.getImage().getHeight()*0.66,
				true, true);
		return im;
	}

	private void initRoot(ReadOnlyDoubleProperty parentHeiht) {
		root.getStyleClass().add("chat-entry-hbox");
		root.prefHeightProperty().bind(
				parentHeiht.divide(ChatsView.CHATS_PER_PAGE));
	}

	@Override
	public Pane buildRoot() {
		metaInfoContainer.getChildren().addAll(title, date);
		lastMessageContainer.getChildren().addAll(lastSenderPhoto, lastMessage);
		root.getChildren()
				.addAll(icon, metaInfoContainer, lastMessageContainer);
		HBox.setHgrow(lastMessageContainer, Priority.ALWAYS);
		return root;
	}

	private Image getIcon(ChatEntry model) {
		return ImageOperator.getIconFrom(model.getChatIconURLs());
	}

	private ChatEntry quasiModel;
	private HBox root = new HBox();
	private Label date = new Label();
	private Label title = new Label();
	private Label lastMessage = new Label();
	private ImageView icon = new ImageView();
	private ImageView lastSenderPhoto = new ImageView();
	private VBox metaInfoContainer = new VBox();
	private HBox lastMessageContainer = new HBox();

	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.OFF);
	}

	@Override
	public Pane getRoot() {
		return root;
	}

	public void update(ChatEntry model) {
		if (!this.quasiModel.equals(model))
			synchronizeWithModel(model);
	}

}
