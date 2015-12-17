package view.nodes;

import data.ImageOperator;
import data.ReadStatesDatabase.ChatReadState;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.VKPerson;

public class ChatFooter extends HBox {

	public ChatFooter(ChatReadState chatReadState, String... chatIconUrl) {
		postponeButton.getStyleClass().addAll("chat-postpone-button", "chat-button");
		uploadButton.getStyleClass().addAll("chat-upload-button", "chat-button");
		readButton.getStyleClass().addAll("chat-read-button", "chat-button");
		inputTray.getStyleClass().add("chat-input-tray");
		this.getStyleClass().add("chat-footer");
		
		
		getIcon(chatIconUrl);
		getOwnerIcon(VKPerson.getOwner().getPhotoURL());
		VBox editorArea = new VBox(inputTray, getAttachmentsContainer());
		editorArea.setMaxWidth(Double.MAX_VALUE);
		getChildren().addAll(ownerIcon, new VBox(readButton, postponeButton), editorArea,
				new VBox(uploadButton), icon);
		setReadState(chatReadState);
		inputTray.setPrefHeight(50);
		inputTray.setMinHeight(50);
		HBox.setHgrow(editorArea, Priority.ALWAYS);
		HBox.setHgrow(inputTray, Priority.ALWAYS);
	}
	

	public TextArea getInputTray() {
		return inputTray;
	}

	private void getIcon(String... urls) {
		ImageOperator.asyncLoadLargeIcon(icon, urls);
	}
	
	private void getOwnerIcon(String url) {
		ImageOperator.asyncLoadLargeIcon(ownerIcon, url);
	}
	
	public void setReadState(ChatReadState chatReadState) {
		switch (chatReadState) {
		case READ:
			setPseudoClass(false, false, false);
			break;
		case UNREAD:
			setPseudoClass(true, false, false);
			break;
		case VIEWED:
			setPseudoClass(false, true, false);
			break;
		case POSTPONED:
			setPseudoClass(false, false, true);
			break;
		}
	}

	private void setPseudoClass(boolean unread, boolean viewed, boolean postponed) {
		pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), unread);
		pseudoClassStateChanged(PseudoClass.getPseudoClass("viewed"), viewed);
		pseudoClassStateChanged(PseudoClass.getPseudoClass("postponed"), postponed);
	}
	
	private Button readButton = new Button();
	private ImageView icon = new ImageView();
	private Button uploadButton = new Button();
	private TextArea inputTray = new TextArea();
	private Button postponeButton = new Button();
	private ImageView ownerIcon = new ImageView();
	private AttachmentsContainer attachmentsContainer = new AttachmentsContainer(false, true);
	

	public AttachmentsContainer getAttachmentsContainer() {
		return attachmentsContainer;
	}
	public Button getUploadButton() {
		return uploadButton;
	}

	public Button getPostponeButton() {
		return postponeButton;
	}

	public Button getReadButton() {
		return readButton;
	}
}
