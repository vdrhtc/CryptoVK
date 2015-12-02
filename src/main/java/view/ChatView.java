package view;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.ReadStatesDatabase.ChatReadState;
import data.ReadStatesDatabase.MessageReadState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.ChatModel;
import model.MessageModel;
import model.Updated;
import view.nodes.ChatFooter;
import view.nodes.ChatNameLabel;

public class ChatView implements View, Updated {

	public ChatView(ChatModel model) {

		this.model = model;
		loadModel();
		initRoot();
	}

	private void initRoot() {

		messagesContainer.getStyleClass().add("messages-container");

		footer = new ChatFooter(readStateProperty.getValue(), model.getChatIconURL().toArray(new String[0]));
		messagesContainer.setContent(messagesLayout);
		messagesContainer.setVvalue(messagesContainer.getVmax());
		root.setCenter(messagesContainer);
		root.setBottom(new VBox(footer, totalMessagesCounter));
	}

	public void loadModel() {

		totalMessagesCounter.setText("Total messages count: " + model.getServerMessageCount().toString());
		viewedMessagesCount = model.getServerMessageCount();

		readStateProperty.setValue(model.getReadState());
		for (MessageModel m : model.getLoadedMessages().subList(messagesLayout.getChildren().size(),
				model.getLoadedMessages().size())) {
			if (m.getReadState() == MessageReadState.UNREAD)
				m.setReadState(MessageReadState.VIEWED);
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
				model.setReadState(ChatReadState.VIEWED);
				readStateProperty.set(ChatReadState.VIEWED);
			} else {
				model.setReadState(ChatReadState.UNREAD);
				readStateProperty.set(ChatReadState.UNREAD);
			}
		} else if (model.getLoadedMessages().get(0).getReadState() == MessageReadState.READ) {
			model.setReadState(ChatReadState.READ);
			readStateProperty.set(ChatReadState.READ);
		}

		for (int i = 0; i < model.getLoadedMessages().size(); i++) {
			if (i < newMessagesCount) {
				MessageModel nextModel = model.getLoadedMessages().get(i);

				if (nextModel.getReadState() == MessageReadState.UNREAD && active)
					nextModel.setReadState(MessageReadState.VIEWED);

				MessageView newMessageView = new MessageView(nextModel);
				messagesLayout.getChildren().add(messagesLayout.getChildren().size() - i, newMessageView.getRoot());
				messagesLayout.getChildren().remove(0);
				loadedMessageViews.add(i, newMessageView);
				loadedMessageViews.remove(loadedMessageViews.size() - 1);

			} else {
				if (model.getLoadedMessages().get(i).getReadState() == MessageReadState.UNREAD && active)
					model.getLoadedMessages().get(i).setReadState(MessageReadState.VIEWED);

				if (loadedMessageViews.get(i).loadModel(model.getLoadedMessages().get(i)))
					messagesLayout.getChildren().set(messagesLayout.getChildren().size() - 1 - i,
							loadedMessageViews.get(i).getRoot());
			}
		}
	}

	public double getMessageHeight() {
		return messagesLayout.getHeight() / messagesLayout.getChildren().size();
	}

	private ChatModel model;
	private Boolean active = false;
	private int viewedMessagesCount;
	private ChatNameLabel chatNameLabel;
	private VBox messagesLayout = new VBox();
	private BorderPane root = new BorderPane();
	private ChatFooter footer;
	private Label totalMessagesCounter = new Label();
	private ScrollPane messagesContainer = new ScrollPane();
	private ArrayList<MessageView> loadedMessageViews = new ArrayList<>();
	private ObjectProperty<ChatReadState> readStateProperty = new SimpleObjectProperty<>();

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public ChatFooter getFooter() {
		return footer;
	}

	public void setChatNameLabel(ChatNameLabel label) {
		this.chatNameLabel = label;
	}

	public ChatNameLabel getChatNameLabel() {
		return chatNameLabel;
	}

	public ObjectProperty<ChatReadState> getReadStateProperty() {
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
	public void getLock(String takerName) {
		model.getLock(takerName);
	}

	@Override
	public void releaseLock(String takerName) {
		model.releaseLock(takerName);
	}

	@Override
	public ViewName getName() {
		return ViewName.CHAT_VIEW;
	}

}
