package controller;

import java.io.UnsupportedEncodingException;

import http.ConnectionOperator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.ChatModel;
import model.ChatPreviewModel;
import view.ChatView;
import view.View.ViewName;

public class ChatViewController implements Controller {


	public ChatViewController(ChatModel CM, ChatPreviewModel PM) {

		CM.loadMessages(ChatModel.INIT_LOAD_COUNT, 0);
		this.controlled = new ChatView(CM);
		this.preview = PM;
		addScrollPaneListener();
		addTextChangeListener();
		addEnterListener();
		addSchorcutLocalizer();
		controlled.getMessagesContainer().setOnScroll(scrollHandler);
	}

	@Override
	public void prepareViewForSwitch(Object... params) {

	}

	@Override
	public ViewName redirectTo() {
		return controlled.getName();
	}

	public void addSchorcutLocalizer() {
		controlled.getInputTray().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
//				System.out.println(event.getText());
				if (event.isControlDown() && !event.getText().equals("")) {
					if (event.getText().equals("с")) {
						controlled.getInputTray()
						.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter() , "c",
								KeyCode.C, event.isShiftDown(), event.isControlDown(), event.isAltDown(),
								event.isMetaDown()));
						event.consume();
					} else if (event.getText().equals("м")) {
						controlled.getInputTray()
						.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), "v",
								KeyCode.V, event.isShiftDown(), event.isControlDown(), event.isAltDown(),
								event.isMetaDown()));
						event.consume();
					} else if (event.getText().equals("ф")) {
						controlled.getInputTray()
						.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), "a",
								KeyCode.A, event.isShiftDown(), event.isControlDown(), event.isAltDown(),
								event.isMetaDown()));
						event.consume();
					} else if (event.getText().equals("ч")) {
						controlled.getInputTray()
						.fireEvent(new KeyEvent(event.getEventType(), event.getCharacter(), "x",
								KeyCode.X, event.isShiftDown(), event.isControlDown(), event.isAltDown(),
								event.isMetaDown()));
						event.consume();
					}

				}
			}
		});
	}

	public void addEnterListener() {

		controlled.getInputTray().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (eventJustFiltered) {
					eventJustFiltered = false;
					return;
				}

				if (event.getCode().equals(KeyCode.ENTER) && !event.isShiftDown()) {
					try {
						ConnectionOperator.sendMessage(controlled.getModel().getChatId(),
								controlled.getModel().getInterlocutorId(), controlled.getInputTray().getText());
						controlled.getInputTray().clear();
						event.consume();
					} catch (UnsupportedEncodingException e) {
						controlled.getInputTray().setText("Failed to encode URL! Unsupported characters!");
					}
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addTextChangeListener() {
		TextArea IT = controlled.getInputTray();
		controlled.getInputTray().textProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				Text text = (Text) IT.lookup(".text");
				IT.setPrefHeight(text.getBoundsInParent().getHeight() + 10);
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addScrollPaneListener() {

		ScrollPane SP = controlled.getMessagesContainer();
		VBox contents = controlled.getMessagesLayout();

		contents.heightProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (controlled.getКостыльДляПрокрутки()) {
					SP.setVvalue(SP.getVmax());
					controlled.setКостыльДляПрокрутки(false);
				}
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

		}
	};

	private ChatView controlled;
	private ChatPreviewModel preview;

	private boolean eventJustFiltered = false;
	private LoaderService loader = new LoaderService();

	private class LoaderService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			Task<Void> loadMoreChatEntries = new Task<Void>() {
				@Override
				protected Void call() {
					controlled.getModel().getLock();
					try {
						controlled.getModel().loadMessages(ChatModel.LOAD_NEW_COUNT,
								controlled.getModel().getLoadedMessages().size());
					} catch (Exception e) {
						e.printStackTrace();
					}
					controlled.getModel().releaseLock();
					return null;
				}
			};
			loadMoreChatEntries.setOnSucceeded(entryLoadResultHandler);
			return loadMoreChatEntries;
		}

		private EventHandler<WorkerStateEvent> entryLoadResultHandler = new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

				Platform.runLater(() -> {
					controlled.getModel().getLock();
					controlled.loadModel();
					controlled.getModel().releaseLock();

				});
			}
		};
	}

	@Override
	public ChatView getControlled() {
		return controlled;
	}

}
