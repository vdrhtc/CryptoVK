package view.messaging;

import data.ImageOperator;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.messaging.MessageModel;
import view.View;

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
		plug.setMinWidth(50);
		ImageOperator.clipImage(senderPhoto);

		messageContainer.getChildren().addAll(message, date);
		HBox.setHgrow(message, Priority.ALWAYS);

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
			senderName.setText(model.getSender().getFullName());
			senderPhoto.setImage(ImageOperator.getIconFrom(model.getSender().getPhotoURL()));
			if (!model.isRead())
				messageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), true);
			else
				messageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), false);
			return true;
		}
		return false;
	}

	private Separator buildVBorder() {
		Separator vBorder = new Separator(Orientation.VERTICAL);
		vBorder.getStyleClass().add("message-border");
		return vBorder;
	}

	private MessageModel model;
	private HBox root = new HBox();
	private Label date = new Label();
	private Label message = new Label();
	private VBox messageContainer = new VBox();
	private Pane plug = new Pane();
	private Label senderName = new Label();
	private ImageView senderPhoto = new ImageView();

	@Override
	public Parent getRoot() {
		return root;
	}

	@Override
	public ViewName getName() {
		return ViewName.MESSAGE_VIEW;
	}

}
