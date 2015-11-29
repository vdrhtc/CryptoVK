package view;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.ImageOperator;
import data.ReadStatesDatabase.MessageReadState;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.MessageModel;
import view.nodes.AttachmentsContainer;

public class MessageView implements View {

	public MessageView() {

	}

	public MessageView(MessageModel model) {
		this.loadModel(model);
		this.initRoot();
	}

	private void initRoot() {

		message.getStyleClass().add("message-message");
		date.getStyleClass().add("message-date");
		senderName.getStyleClass().add("message-sender-name");
		plug.setMinWidth(50);

		messageContainer.getChildren().addAll(senderName, message, date);
		MenuItem copy = new MenuItem("Copy text");
		copy.setOnAction((ActionEvent e) -> {
			ClipboardContent content = new ClipboardContent();
			content.put(DataFormat.PLAIN_TEXT, message.getText());
			Clipboard.getSystemClipboard().setContent(content);
		});
		message.setContextMenu(new ContextMenu(copy));
		
		HBox.setHgrow(message, Priority.ALWAYS);

		if (attachementsContainer != null)
			messageContainer.getChildren().add(2, attachementsContainer);

		if (model.isIncoming()) {
			root.getStyleClass().add("message-hbox-incoming");
			messageContainer.getStyleClass().add("message-container-incoming");

			root.getChildren().addAll(plug, messageContainer, buildVBorder(), senderPhoto);
		} else {
			root.getStyleClass().add("message-hbox");
			messageContainer.getStyleClass().add("message-container");

			root.getChildren().addAll(senderPhoto, buildVBorder(), messageContainer, plug);
		}
		
		
	}

	public boolean loadModel(MessageModel model) {

		if (!model.equals(this.model)) {
			this.model = model.clone();
			date.setText(model.getDate());
			message.setText(model.getText());
			senderName.setText(model.getSender().getFirstName());
			getIcon(model);
			setReadState(model.getReadState());

			if (model.getAttachments().size() > 0)
				attachementsContainer = new AttachmentsContainer(model.getAttachments(), model.isIncoming());
			return true;
		}
		return false;
	}

	private void getIcon(MessageModel model) {
		ImageOperator.asyncLoadImage(senderPhoto, model.getSender().getPhotoURL());
	}

	private Separator buildVBorder() {
		Separator vBorder = new Separator(Orientation.VERTICAL);
		vBorder.getStyleClass().add("message-border");
		return vBorder;
	}

	public void setReadState(MessageReadState RS) {
		switch (RS) {
		case READ:
			setMessageContainerPseudoClass(false, false);
			break;
		case UNREAD:
			setMessageContainerPseudoClass(true, false);
			break;
		case VIEWED:
			setMessageContainerPseudoClass(false, true);
			break;
		}
	}

	private void setMessageContainerPseudoClass(boolean unread, boolean viewed) {
		messageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), unread);
		messageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("viewed"), viewed);
	}

	private MessageModel model;
	private HBox root = new HBox();
	private Label date = new Label();
	private Label message = new Label();
	private VBox messageContainer = new VBox();
	private Pane plug = new Pane();
	private Label senderName = new Label();
	private ImageView senderPhoto = new ImageView();
	private AttachmentsContainer attachementsContainer;

	private static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.WARNING);
	}

	@Override
	public Pane getRoot() {
		return root;
	}

	@Override
	public ViewName getName() {
		return ViewName.MESSAGE_VIEW;
	}

}
