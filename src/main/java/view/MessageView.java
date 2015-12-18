package view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
		senderName.getStyleClass().add("message-sender-name");
		plug.setMinWidth(50);

		message.setMinHeight(Region.USE_PREF_SIZE);
		
		if (message.getText().equals(""))
			message.setPrefHeight(0);
		
		messageContainer.getChildren().addAll(senderName, message, date);
		MenuItem copy = new MenuItem("Copy text");
		copy.setOnAction((ActionEvent e) -> {
			ClipboardContent content = new ClipboardContent();
			content.put(DataFormat.PLAIN_TEXT, message.getText());
			Clipboard.getSystemClipboard().setContent(content);
		});
		message.setContextMenu(new ContextMenu(copy));
		
		HBox.setHgrow(message, Priority.ALWAYS);
		
		if (attachementsContainer != null) {
			messageContainer.getChildren().add(2, attachementsContainer);
//			attachementsContainer.setStyle("-fx-border-color:black");
			HBox.setHgrow(attachementsContainer, Priority.ALWAYS);
		}
		
		if (model.isIncoming()) {
			date.getStyleClass().add("message-date-incoming");
			root.getStyleClass().add("message-hbox-incoming");
			messageContainer.getStyleClass().add("message-container-incoming");

			root.getChildren().addAll(plug, messageContainer, buildVBorder(), senderIcon);
		} else {
			date.getStyleClass().add("message-date");
			root.getStyleClass().add("message-hbox");
			messageContainer.getStyleClass().add("message-container");

			root.getChildren().addAll(senderIcon, buildVBorder(), messageContainer, plug);
		}
		root.setOnMousePressed((MouseEvent e) -> {
			root.setStyle("-fx-background-color: derive( #DAE1E8, 30%)");
		});
		root.setOnMouseReleased((MouseEvent e) -> {
			root.setStyle("-fx-background-color: transparent");
		});
	}

	public boolean loadModel(MessageModel model) {
		if (!model.equals(this.model)) {
			this.model = model.clone();
			date.setText(model.getDate());
			message.setText(model.getText());
			senderName.setText(model.getSender().getFirstName());
			senderName.setOnMouseEntered((MouseEvent e) -> {
				senderName.setText(model.getSender().getFirstName() + " " + model.getSender().getLastName());
			});
			senderName.setOnMouseExited((MouseEvent e) -> {
				senderName.setText(model.getSender().getFirstName());
			});
			getIcon(model);
 			setReadState(model.getReadState());

			if (model.getAttachments().size() > 0)
				attachementsContainer = new AttachmentsContainer(model.getAttachments(), model.isIncoming(), false);
			return true;
		}
		return false;
	}

	private void getIcon(MessageModel model) {
		ImageOperator.asyncLoadLargeIcon(senderIcon, model.getSender().getPhotoURL());
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
	private Pane plug = new Pane();
	private HBox root = new HBox();
	private Label date = new Label();
	private Label message = new Label();
	private Label senderName = new Label();
	private VBox messageContainer = new VBox();
	private ImageView senderIcon = new ImageView();
	private AttachmentsContainer attachementsContainer;

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public Pane getRoot() {
		return root;
	}

	@Override
	public ViewName getName() {
		return ViewName.MESSAGE_VIEW;
	}

}
