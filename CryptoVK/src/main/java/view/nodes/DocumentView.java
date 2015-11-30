package view.nodes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.sun.javafx.tk.Toolkit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Document;

public class DocumentView extends Label {

	public static final double MAX_DOCUMENT_WIDTH = 400;

	public DocumentView(Document document) {
		super(document.getDisplayedName());

		this.getStyleClass().add("document-label");
		Double expectedWidth = (double) Toolkit.getToolkit().getFontLoader()
				.computeStringWidth(document.getDisplayedName(), this.getFont());
		this.setPrefWidth(expectedWidth > MAX_DOCUMENT_WIDTH ? MAX_DOCUMENT_WIDTH : expectedWidth + 7 * 2); // Padding
																											// from
																											// css
		MenuItem open = new MenuItem("Open");
		open.getStyleClass().add("image-context-menu-item");
		open.setOnAction((ActionEvent e) -> {

			Thread thread = new Thread(() -> {
				try {
					File documentFile = null;

					URL documentUrl = document.getUrl();
					documentFile = File.createTempFile("vk_document",
							document.getName() + "." + document.getExtension());
					InputStream inputStream = documentUrl.openStream();
					OutputStream outputStream = new FileOutputStream(documentFile);

					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = inputStream.read(bytes)) != -1)
						outputStream.write(bytes, 0, read);
					inputStream.close();
					outputStream.close();

					Desktop.getDesktop().open(documentFile);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			thread.start();
		});
		this.setContextMenu(new ContextMenu(open));

		this.setOnMouseClicked((MouseEvent e) -> {
			if (e.getButton().equals(MouseButton.MIDDLE))
				removalRequested.setValue(true);
			else if (e.getButton().equals(MouseButton.PRIMARY))
				if (e.getClickCount() > 1)
					removalRequested.setValue(true);
		});
	}

	private BooleanProperty removalRequested = new SimpleBooleanProperty(false);

	public BooleanProperty getRemovalRequested() {
		return removalRequested;
	}

}
