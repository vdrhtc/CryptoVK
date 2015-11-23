package controller;

import java.io.File;
import java.util.List;

import http.Uploader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Attachment;
import view.nodes.ChatFooter;

public class ChatFooterController {

	public ChatFooterController(ChatFooter controlled) {
		this.controlled = controlled;
		addTextChangeListener();
		addSchorcutLocalizer();
		addUploadButtonListener();
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

	public void addFocusRequester() {
		controlled.getInputTray().sceneProperty().addListener(new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
				if (newValue != null)
					controlled.getInputTray().requestFocus();
			}
		});
	}

	public void addTextChangeListener() {
		TextArea IT = controlled.getInputTray();
		IT.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, String t, String t1) {
				Text text = (Text) IT.lookup(".text");
				IT.setPrefHeight(text.getBoundsInParent().getHeight() + 10);
				IT.setPrefRowCount((int)(IT.getPrefHeight()/text.getFont().getSize()));
				ScrollBar scrollBarv = (ScrollBar)IT.lookup(".scroll-bar:vertical");
				scrollBarv.setDisable(true);
			}
		});
		IT.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				Text text = (Text) IT.lookup(".text");
				IT.setPrefHeight(text.getBoundsInParent().getHeight() + 10);
				IT.setPrefRowCount((int)(IT.getPrefHeight()/text.getFont().getSize()));
				ScrollBar scrollBarv = (ScrollBar)IT.lookup(".scroll-bar:vertical");
				scrollBarv.setDisable(true);
			}
		});
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

	private ChatFooter controlled;
	private Uploader uploader = new Uploader();
}
