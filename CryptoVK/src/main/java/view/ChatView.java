package view;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.ImageOperator;
import data.ReadStatesDatabase.ReadState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Attachment;
import model.ChatModel;
import model.MessageModel;
import model.Updated;
import model.VKPerson;
import view.nodes.AttachmentsContainer;
import view.nodes.ChatNameLabel;

public class ChatView implements View, Updated {

	public ChatView(ChatModel model) {

		this.model = model;
		loadModel();
		initRoot();
	}

	private void initRoot() {

		postponeButton.getStyleClass().addAll("chat-postpone-button", "chat-button");
		uploadButton.getStyleClass().addAll("chat-upload-button", "chat-button");
		readButton.getStyleClass().addAll("chat-read-button", "chat-button");
		messagesContainer.getStyleClass().add("messages-container");
		inputTray.getStyleClass().add("chat-input-tray");
		footer.getStyleClass().add("chat-footer");

		getIcon(model.getChatIconURL().toArray(new String[0]));
		getOwnerIcon(VKPerson.getOwner().getPhotoURL());
		footer.getChildren().addAll(ownerIcon, new VBox(readButton, postponeButton), new VBox(inputTray, getAttachmentsContainer()),
				new VBox(uploadButton), icon);
		messagesContainer.setContent(messagesLayout);
		messagesContainer.setVvalue(messagesContainer.getVmax());
		root.setCenter(messagesContainer);
		root.setBottom(new VBox(footer, totalMessagesCounter));
		HBox.setHgrow(inputTray, Priority.ALWAYS);
	}

	public void loadModel() {

		totalMessagesCounter.setText("Total messages count: " + model.getServerMessageCount().toString());
		viewedMessagesCount = model.getServerMessageCount();

		readStateProperty.setValue(model.getReadState());
		for (MessageModel m : model.getLoadedMessages().subList(messagesLayout.getChildren().size(),
				model.getLoadedMessages().size())) {
			if (m.getReadState() == ReadState.UNREAD)
				m.setReadState(ReadState.VIEWED);
			MessageView MV = new MessageView(m);
			loadedMessageViews.add(MV);
			messagesLayout.getChildren().add(0, MV.getRoot());
		}
	}

	@Override
	public void update(Object... params) {

		int newMessagesCount = model.getServerMessageCount() - viewedMessagesCount;

		if (newMessagesCount > 0) {

			totalMessagesCounter.setText("Total messages count: " + model.getServerMessageCount().toString());
			viewedMessagesCount = model.getServerMessageCount();

			if (active) {
				model.setReadState(ReadState.VIEWED);
				readStateProperty.set(ReadState.VIEWED);
			} else {
				model.setReadState(ReadState.UNREAD);
				readStateProperty.set(ReadState.UNREAD);
			}
		} else if (model.getLoadedMessages().get(0).getReadState() == ReadState.READ) {
			model.setReadState(ReadState.READ);
			readStateProperty.set(ReadState.READ);
		}

		for (int i = 0; i < model.getLoadedMessages().size(); i++) {
			if (i < newMessagesCount) {
				MessageModel nextModel = model.getLoadedMessages().get(i);

				if (nextModel.getReadState() == ReadState.UNREAD && active)
					nextModel.setReadState(ReadState.VIEWED);

				MessageView newMessageView = new MessageView(nextModel);
				messagesLayout.getChildren().add(messagesLayout.getChildren().size() - i, newMessageView.getRoot());
				messagesLayout.getChildren().remove(0);
				loadedMessageViews.add(i, newMessageView);
				loadedMessageViews.remove(loadedMessageViews.size() - 1);

			} else {
				if (model.getLoadedMessages().get(i).getReadState() == ReadState.UNREAD && active)
					model.getLoadedMessages().get(i).setReadState(ReadState.VIEWED);

				if (loadedMessageViews.get(i).loadModel(model.getLoadedMessages().get(i)))
					messagesLayout.getChildren().set(messagesLayout.getChildren().size() - 1 - i,
							loadedMessageViews.get(i).getRoot());
			}
		}
	}

	public double getMessageHeight() {
		return messagesLayout.getHeight() / messagesLayout.getChildren().size();
	}

	public TextArea getInputTray() {
		return inputTray;
	}

	private void getIcon(String... urls) {
		ImageOperator.asyncLoadImage(icon, urls);
	}
	
	private void getOwnerIcon(String url) {
		ImageOperator.asyncLoadImage(ownerIcon, url);
	}

	private Button uploadButton = new Button();
	private Button readButton = new Button();
	private Button postponeButton = new Button();
	private ChatModel model;
	private int viewedMessagesCount;
	private ChatNameLabel chatNameLabel;
	private ArrayList<Attachment> attachments = new ArrayList<>();
	private AttachmentsContainer attachmentsContainer = new AttachmentsContainer(false);
	private Boolean active = false;
	private HBox footer = new HBox();
	private VBox messagesLayout = new VBox();
	private ImageView icon = new ImageView();
	private BorderPane root = new BorderPane();
	private TextArea inputTray = new TextArea();
	private ImageView ownerIcon = new ImageView();
	private Label totalMessagesCounter = new Label();
	private ScrollPane messagesContainer = new ScrollPane();
	private ArrayList<MessageView> loadedMessageViews = new ArrayList<>();
	private ObjectProperty<ReadState> readStateProperty = new SimpleObjectProperty<>();

	private static Logger log = Logger.getAnonymousLogger();

	static {
		log.setLevel(Level.WARNING);
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

	public void setChatNameLabel(ChatNameLabel label) {
		this.chatNameLabel = label;
	}

	public ChatNameLabel getChatNameLabel() {
		return chatNameLabel;
	}

	public AttachmentsContainer getAttachmentsContainer() {
		return attachmentsContainer;
	}

	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}

	public ObjectProperty<ReadState> getReadStateProperty() {
		return readStateProperty;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
		chatNameLabel.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), active);
	}

	@Override
	public Pane getRoot() {
		return root;
	}

	public ScrollPane getMessagesContainer() {
		return messagesContainer;
	}

	public VBox getMessagesLayout() {
		return messagesLayout;
	}

	public ChatModel getModel() {
		return model;
	}

	@Override
	public void getLock() {
		model.getLock();
	}

	@Override
	public void releaseLock() {
		model.releaseLock();
	}


	@Override
	public ViewName getName() {
		return ViewName.CHAT_VIEW;
	}

}
