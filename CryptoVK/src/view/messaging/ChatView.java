package view.messaging;

import java.util.ArrayList;

import data.ImageOperator;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Updated;
import model.VKPerson;
import model.messaging.ChatModel;
import model.messaging.MessageModel;
import view.View;

public class ChatView implements View, Updated {

	public static final int LOAD_NEW_COUNT = 4;
	public static final int INIT_LOAD_COUNT = 10;

	public ChatView(ChatModel model) {
		this.model = model;
		loadModel();
		initRoot();
	}

	private void initRoot() {

		messagesContainer.getStyleClass().add("chats-container");
		footer.getStyleClass().add("chat-footer");
		inputTray.getStyleClass().add("chat-input-tray");

		icon.setImage(ImageOperator.getIconFrom(model.getChatIconURL()));
		ImageOperator.clipImage(icon);
		ownerIcon.setImage(ImageOperator.getIconFrom(VKPerson.getOwner().getPhotoURL()));
		ImageOperator.clipImage(ownerIcon);
		footer.getChildren().addAll(ownerIcon, new VBox(inputTray), icon);
		messagesContainer.setContent(messagesLayout);
		messagesContainer.setVvalue(messagesContainer.getVmax());

		root.setTop(totalMessagesCounter);
		root.setCenter(messagesContainer);
		root.setBottom(footer);
		HBox.setHgrow(inputTray, Priority.ALWAYS);
		VBox.setVgrow(inputTray, Priority.ALWAYS);
	}

	public void loadModel() {
		totalMessagesCounter.setText(model.getServerMessageCount().toString());
		for (MessageModel m : model.getLoadedMessages().subList(messagesLayout.getChildren().size(),
				model.getLoadedMessages().size())) {
			MessageView MV = new MessageView(m);
			loadedMessageViews.add(MV);
			messagesLayout.getChildren().add(0, MV.getRoot());
		}
	}

	@Override
	public void update() {
		
		int newMessagesCount = model.getServerMessageCount() - new Integer(totalMessagesCounter.getText());
		
		for (int i = 0; i < model.getLoadedMessages().size(); i++) {
			if (i < newMessagesCount) {
				MessageView MV = new MessageView(model.getLoadedMessages().get(i));
				messagesLayout.getChildren().add(messagesLayout.getChildren().size() - i, MV.getRoot());
				messagesLayout.getChildren().remove(0);
				loadedMessageViews.add(i, new MessageView(model.getLoadedMessages().get(i)));
				loadedMessageViews.remove(loadedMessageViews.size() - 1);
				
			} else if (loadedMessageViews.get(i).loadModel(model.getLoadedMessages().get(i)))
				messagesLayout.getChildren().set(messagesLayout.getChildren().size() - i - 1,
						loadedMessageViews.get(i).getRoot());
		}

		totalMessagesCounter.setText(model.getServerMessageCount().toString());
		if (messagesContainer.getVvalue() > 0.9)
			костыльДляПрокрутки = true;
	}

	public double getMessageHeight() {
		return messagesLayout.getHeight() / messagesLayout.getChildren().size();
	}

	public TextArea getInputTray() {
		return inputTray;
	}

	private ChatModel model;
	private HBox footer = new HBox();
	private Label totalMessagesCounter = new Label();
	private VBox messagesLayout = new VBox();
	private ImageView icon = new ImageView();
	private BorderPane root = new BorderPane();
	private ImageView ownerIcon = new ImageView();
	private TextArea inputTray = new TextArea();
	private ScrollPane messagesContainer = new ScrollPane();
	private ArrayList<MessageView> loadedMessageViews = new ArrayList<>();
	private Boolean костыльДляПрокрутки = true;

	@Override
	public Parent getRoot() {
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

	}

	@Override
	public void releaseLock() {

	}

	public Boolean getКостыльДляПрокрутки() {
		return костыльДляПрокрутки;
	}

	public void setКостыльДляПрокрутки(Boolean костыльДляПрокрутки) {
		this.костыльДляПрокрутки = костыльДляПрокрутки;
	}

	@Override
	public ViewName getName() {
		return ViewName.CHAT_VIEW;
	}
}
