package controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import data.ReadStatesDatabase;
import data.ReadStatesDatabase.ReadState;
import http.ConnectionOperator;
import http.Uploader;
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
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Attachment;
import model.ChatModel;
import view.ChatView;
import view.View.ViewName;

public class ChatViewController implements Controller {

	public ChatViewController(ChatModel CM) {

		CM.loadMessages(ChatModel.INIT_LOAD_COUNT, 0);
		this.controlled = new ChatView(CM);
		this.uploader = new Uploader();
		addScrollPaneListener();
		addTextChangeListener();
		addEnterListenerAndRequestFocus();
		addSchorcutLocalizer();
		addReadStateListener();
		addUploadButtonListener();
		addReadButtonListener();
		controlled.getMessagesContainer().setOnScroll(scrollHandler);
	}

	private void addUploadButtonListener() {
		controlled.getUploadButton().setOnAction((ActionEvent a) -> {

			FileChooser fc = new FileChooser();
			List<File> selectedFiles = fc.showOpenMultipleDialog(controlled.getUploadButton().getScene().getWindow());
			if (selectedFiles != null) {
				for (File selectedFile : selectedFiles) {
					Task<Attachment> uploadTask = new Task<Attachment>() {
						protected Attachment call() throws Exception {
							Thread.currentThread().setName("File uploader");
							return uploader.upload(selectedFile);
						}

						protected void succeeded() {
							Attachment attachment = getValue();
							controlled.getAttachmentsContainer().addAttachment(attachment);
							controlled.getAttachments().add(attachment);
						}
					};
					Thread t = new Thread(uploadTask);
					t.start();
				}
			}
		});
	}

	@Override
	public void prepareViewForSwitch(Object... params) {
		if (!controlled.getActive()) {
			controlled.getModel().getLock();
			controlled.setActive(true);
			if (controlled.getReadStateProperty().getValue() == ReadState.UNREAD) {
				controlled.getReadStateProperty().set(ReadState.VIEWED);
				controlled.getModel().setReadState(ReadState.VIEWED);
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

	public void addSchorcutLocalizer() {
		controlled.getInputTray().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.isControlDown() && !event.getText().equals("")) {
					if (event.getText().equals("с")) {
						controlled.getInputTray()
								.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), "c", KeyCode.C,
										event.isShiftDown(), event.isControlDown(), event.isAltDown(),
										event.isMetaDown()));
						event.consume();
					} else if (event.getText().equals("м")) {
						controlled.getInputTray()
								.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), "v", KeyCode.V,
										event.isShiftDown(), event.isControlDown(), event.isAltDown(),
										event.isMetaDown()));
						event.consume();
					} else if (event.getText().equals("ф")) {
						controlled.getInputTray()
								.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), "a", KeyCode.A,
										event.isShiftDown(), event.isControlDown(), event.isAltDown(),
										event.isMetaDown()));
						event.consume();
					} else if (event.getText().equals("ч")) {
						controlled.getInputTray()
								.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), "x", KeyCode.X,
										event.isShiftDown(), event.isControlDown(), event.isAltDown(),
										event.isMetaDown()));
						event.consume();
					} else if (event.getText().equals("я")) {
						controlled.getInputTray()
								.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), "z", KeyCode.Z,
										event.isShiftDown(), event.isControlDown(), event.isAltDown(),
										event.isMetaDown()));
						event.consume();
					}

				}
			}
		});
	}

	public void addEnterListenerAndRequestFocus() {

		controlled.getInputTray().sceneProperty().addListener(new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
				if (newValue != null)
					controlled.getInputTray().requestFocus();
			}
		});

		controlled.getInputTray().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (eventJustFiltered) {
					eventJustFiltered = false;
					return;
				}

				if (event.getCode().equals(KeyCode.ENTER) && !event.isShiftDown()) {

					controlled.getModel().getLock();
					ArrayList<Attachment> aTTs = controlled.getAttachments();
					String message = controlled.getInputTray().getText();
					int chatId = controlled.getModel().getChatId();
					int interlocutorId = controlled.getModel().getInterlocutorId();

					String[] attachmentStrings = new String[aTTs.size()];
					for (Attachment a : controlled.getAttachments())
						attachmentStrings[aTTs.indexOf(a)] = a.toString();

					Thread t = new Thread(() -> {
						Thread.currentThread().setName("Message sender");
						try {
							CO.sendMessage(chatId, interlocutorId, message, attachmentStrings);
							ReadStatesDatabase.clear(controlled.getModel().getChatId());
						} catch (UnsupportedEncodingException e) {
							Platform.runLater(() -> {
								controlled.getInputTray().setText("Failed to encode URL! Unsupported characters!");
							});
						}
					});
					t.start();
					
					controlled.getAttachmentsContainer().clear();
					controlled.getAttachments().clear();
					controlled.getInputTray().clear();
					controlled.getModel().setReadState(ReadState.READ);
					controlled.getReadStateProperty().setValue(ReadState.READ);
					controlled.getModel().releaseLock();
					event.consume();

				} else if (event.getCode().equals(KeyCode.ENTER) && event.isShiftDown()) {
					eventJustFiltered = true;
					controlled.getInputTray()
							.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), event.getText(),
									event.getCode(), false, event.isControlDown(), event.isAltDown(),
									event.isMetaDown()));
					event.consume();
				}
			}
		});

	}

	public void addReadStateListener() {
		controlled.getReadStateProperty().addListener(new ChangeListener<ReadState>() {

			@Override
			public void changed(ObservableValue<? extends ReadState> observable, ReadState oldValue,
					ReadState newValue) {

				readStateWithIdProperty.setValue(new ReadStateWithId(controlled.getModel().getChatId(), newValue));
				controlled.getChatNameLabel().setReadState(newValue);
			}
		});
	}
	
	private void addReadButtonListener() {
		controlled.getReadButton().setOnAction((ActionEvent a) -> {
			readMessages();
		});
	}

	public void readMessages() {
		if (!controlled.getModel().getLoadedMessages().get(0).isIncoming())
			return;
		
		controlled.getModel().getLock();
		Thread t = new Thread(() -> {
			CO.readChat(controlled.getModel().getChatId(),
					controlled.getModel().getLoadedMessages().get(0).getId());
		});
		t.start();
		controlled.getModel().setReadState(ReadState.READ);
		ReadStatesDatabase.clear(controlled.getModel().getChatId());
		controlled.getReadStateProperty().setValue(ReadState.READ);
		controlled.getModel().releaseLock();
	}

	public void addTextChangeListener() {
		TextArea IT = controlled.getInputTray();
		controlled.getInputTray().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, String t, String t1) {
				Text text = (Text) IT.lookup(".text");
				IT.setPrefHeight(text.getBoundsInParent().getHeight() + 10);
			}
		});
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
			if(newVvalue >= 1)
				scrollLockOn = false;
			else
				scrollLockOn = true;

		}
	};

	private ChatView controlled;
	private boolean scrollLockOn = false;
	private ConnectionOperator CO = new ConnectionOperator(1000);
	private Uploader uploader;
	private ObjectProperty<ReadStateWithId> readStateWithIdProperty = new SimpleObjectProperty<>();

	private boolean eventJustFiltered = false;
	private LoaderService loader = new LoaderService();

	public ObjectProperty<ReadStateWithId> getReadStateWithIdProperty() {
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
