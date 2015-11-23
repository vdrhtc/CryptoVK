package controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import data.ReadStatesDatabase;
import data.ReadStatesDatabase.ChatReadState;
import http.ConnectionOperator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import model.Attachment;
import model.ChatModel;
import view.ChatView;
import view.View.ViewName;

public class ChatViewController implements Controller {

	public ChatViewController(ChatModel CM) {

		CM.loadMessages(ChatModel.INIT_LOAD_COUNT, 0);
		this.controlled = new ChatView(CM);
		new ChatFooterController(controlled.getFooter());
		addScrollPaneListener();
		addEnterListenerAndRequestFocus();
		addReadStateListener();
		addReadButtonListener();
		addPostponeButtonListener();
		controlled.getMessagesContainer().setOnScroll(scrollHandler);
	}


	@Override
	public void prepareViewForSwitch(Object... params) {
		if (!controlled.getActive()) {
			controlled.getModel().getLock();
			controlled.setActive(true);
			if (controlled.getReadStateProperty().getValue() == ChatReadState.UNREAD) {
				controlled.getReadStateProperty().set(ChatReadState.VIEWED);
				controlled.getModel().setReadState(ChatReadState.VIEWED);
			}
			controlled.update();
			controlled.getModel().releaseLock();
		} else
			controlled.setActive(false);
	}

	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}

	

	public void addEnterListenerAndRequestFocus() {

		controlled.getFooter().getInputTray().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (eventJustFiltered) {
					eventJustFiltered = false;
					return;
				}

				if (event.getCode().equals(KeyCode.ENTER) && !event.isShiftDown()) {

					controlled.getModel().getLock();
					ArrayList<Attachment> aTTs = controlled.getFooter().getAttachments();
					String message = controlled.getFooter().getInputTray().getText();
					Long chatId = controlled.getModel().getChatId();
					Long interlocutorId = controlled.getModel().getInterlocutorId();

					String[] attachmentStrings = new String[aTTs.size()];
					for (Attachment a : controlled.getFooter().getAttachments())
						attachmentStrings[aTTs.indexOf(a)] = a.toString();

					Thread t = new Thread(() -> {
						Thread.currentThread().setName("Message sender");
						try {
							CO.sendMessage(chatId, interlocutorId, message, attachmentStrings);
							ReadStatesDatabase.clear(controlled.getModel().getChatId());
						} catch (UnsupportedEncodingException e) {
							Platform.runLater(() -> {
								controlled.getFooter().getInputTray().setText("Failed to encode URL! Unsupported characters!");
							});
						}
					});
					t.start();

					controlled.getFooter().getAttachmentsContainer().clear();
					controlled.getFooter().getAttachments().clear();
					controlled.getFooter().getInputTray().clear();
					controlled.getModel().setReadState(ChatReadState.READ);
					controlled.getReadStateProperty().setValue(ChatReadState.READ);
					controlled.getModel().releaseLock();
					event.consume();

				} else if (event.getCode().equals(KeyCode.ENTER) && event.isShiftDown()) {
					eventJustFiltered = true;
					controlled.getFooter().getInputTray()
							.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), event.getText(),
									event.getCode(), false, event.isControlDown(), event.isAltDown(),
									event.isMetaDown()));
					event.consume();
				}
			}
		});
	}

	public void addReadStateListener() {
		controlled.getReadStateProperty().addListener(new ChangeListener<ChatReadState>() {
			public void changed(ObservableValue<? extends ChatReadState> observable, ChatReadState oldValue,
					ChatReadState newValue) {
				readStateWithIdProperty.setValue(new ChatReadStateWithId(controlled.getModel().getChatId(), newValue));
				controlled.getChatNameLabel().setReadState(newValue);
				controlled.getFooter().setReadState(newValue);
			}
		});
	}

	private void addPostponeButtonListener() {
		controlled.getFooter().getPostponeButton().setOnAction((ActionEvent a) -> {
			controlled.getModel().getLock();
			if (controlled.getModel().getReadState() == ChatReadState.POSTPONED) {
				controlled.getModel().setReadState(ChatReadState.VIEWED);
				controlled.getReadStateProperty().setValue(ChatReadState.VIEWED);
			} else if (controlled.getModel().getReadState() == ChatReadState.VIEWED) {
				controlled.getModel().setReadState(ChatReadState.POSTPONED);
				controlled.getReadStateProperty().setValue(ChatReadState.POSTPONED);
			}
			controlled.getModel().releaseLock();
		});
	}

	private void addReadButtonListener() {
		controlled.getFooter().getReadButton().setOnAction((ActionEvent a) -> {
			readMessages();
		});
	}

	public void readMessages() {
		if (!controlled.getModel().getLoadedMessages().get(0).isIncoming())
			return;

		controlled.getModel().getLock();
		Thread t = new Thread(() -> {
			CO.readChat(controlled.getModel().getChatId(), controlled.getModel().getLoadedMessages().get(0).getId());
		});
		t.start();
		controlled.getModel().setReadState(ChatReadState.READ);
		ReadStatesDatabase.clear(controlled.getModel().getChatId());
		controlled.getReadStateProperty().setValue(ChatReadState.READ);
		controlled.getModel().releaseLock();
	}



	public void addScrollPaneListener() {

		ScrollPane SP = controlled.getMessagesContainer();
		VBox contents = controlled.getMessagesLayout();

		contents.heightProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				if (!scrollLockOn)
					SP.setVvalue(SP.getVmax());
			}
		});

	}

	public EventHandler<ScrollEvent> scrollHandler = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {

			double min = controlled.getMessagesContainer().getVmin();
			double relation = controlled.getMessageHeight() / controlled.getMessagesLayout().getHeight();
			double newVvalue = controlled.getMessagesContainer().getVvalue()
					- Math.signum(event.getDeltaY()) * relation;

			if (newVvalue <= min && event.getDeltaY() > 0
					&& (loader.getState() == State.SUCCEEDED || loader.getState() == State.READY)) {

				loader.reset();
				loader.start();

			} else
				controlled.getMessagesContainer().setVvalue(newVvalue);
			if (newVvalue >= 1)
				scrollLockOn = false;
			else
				scrollLockOn = true;

		}
	};

	private ChatView controlled;
	private boolean scrollLockOn = false;
	private ConnectionOperator CO = new ConnectionOperator(1000);
	private ObjectProperty<ChatReadStateWithId> readStateWithIdProperty = new SimpleObjectProperty<>();

	private boolean eventJustFiltered = false;
	private LoaderService loader = new LoaderService();

	public ObjectProperty<ChatReadStateWithId> getReadStateWithIdProperty() {
		return readStateWithIdProperty;
	}

	private class LoaderService extends Service<Void> {
		protected Task<Void> createTask() {
			Task<Void> loadMoreChatEntries = new Task<Void>() {
				protected Void call() {
					controlled.getModel().getLock();
					try {
						controlled.getModel().loadMessages(ChatModel.LOAD_NEW_COUNT,
								controlled.getModel().getLoadedMessages().size());
					} catch (Exception e) {
						e.printStackTrace();
					}
					controlled.getModel().releaseLock();

					Platform.runLater(() -> {
						controlled.getModel().getLock();
						controlled.loadModel();
						controlled.getModel().releaseLock();
					});
					return null;
				}
			};
			return loadMoreChatEntries;
		}
	}

	@Override
	public ChatView getControlled() {
		return controlled;
	}

}
