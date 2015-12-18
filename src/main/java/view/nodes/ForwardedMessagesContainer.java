package view.nodes;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.Attachment;
import model.MessageModel;
import view.MessageView;

public class ForwardedMessagesContainer {

	public ForwardedMessagesContainer(Boolean editable) {
		this.editable = editable;
		this.root = new ScrollPane();
		this.root.setContent(layout);
		this.root.setPrefHeight(0);
		this.root.setMinHeight(0);
		this.root.setMaxHeight(500);
		this.root.getStyleClass().add("forwarded-messages-container");
		this.layout.setPrefHeight(0);
		this.layout.setAlignment(Pos.CENTER);
//		this.layout.setStyle("-fx-border-color:black;");
		this.root.minHeightProperty().bind(demandedHeightProperty);
		this.root.minWidthProperty().bind(demandedWidthProperty);
	}

	public void addMessage(MessageModel message) {
		if (messages.size() == 0) {
			this.layout.heightProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					Platform.runLater(() -> {
						if ((Double) newValue < 500)
							demandedHeightProperty.setValue((double) newValue + 5);
						else
							demandedHeightProperty.setValue(500);
					});
				}
			});
		}
		messages.add(message);
		layout.getChildren().add(new MessageView(message).getRoot());

	}

	public void clear() {
		layout.getChildren().clear();
		messages.clear();
	}

	private ScrollPane root;
	private Boolean editable;
	private DoubleProperty demandedHeightProperty = new SimpleDoubleProperty();
	private DoubleProperty demandedWidthProperty = new SimpleDoubleProperty();
	private ArrayList<Attachment> messages = new ArrayList<>();
	private VBox layout = new VBox();

	public ScrollPane getRoot() {
		return root;
	}

	public ArrayList<Attachment> getAttachments() {
		return messages;
	}
}
