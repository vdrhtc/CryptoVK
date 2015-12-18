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
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import model.Document;

public class DocumentView extends Label {

	public static final double MAX_DOCUMENT_WIDTH = 200;

	public DocumentView(Document document, Boolean editable) {
		super(document.getDisplayedName());

		this.getStyleClass().add("document-label");
		Double expectedWidth = (double) Toolkit.getToolkit().getFontLoader()
				.computeStringWidth(document.getDisplayedName(), this.getFont());
		this.setPrefWidth(expectedWidth > MAX_DOCUMENT_WIDTH ? MAX_DOCUMENT_WIDTH : expectedWidth + 7 * 2); 
		this.setMinWidth(Region.USE_PREF_SIZE);
		MenuItem saveAs = new MenuItem("Save As...");
		saveAs.getStyleClass().add("image-context-menu-item");
		saveAs.setOnAction((ActionEvent a) -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Save As");
			chooser.setInitialFileName(document.getName()+"."+document.getExtension());
			File file = chooser.showSaveDialog(this.getScene().getWindow());
			if (file != null) {
				try {
					FileOutputStream outputStream = new FileOutputStream(file);
					InputStream inputStream = document.getUrl().openStream();
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = inputStream.read(bytes)) != -1)
						outputStream.write(bytes, 0, read);
					inputStream.close();
					outputStream.close();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		MenuItem remove = new MenuItem("Remove");
		remove.getStyleClass().add("image-context-menu-item");
		remove.setOnAction((ActionEvent a) -> {
			removalRequested.setValue(true);;
		});
		
		MenuItem open = new MenuItem("Open");
		open.getStyleClass().add("image-context-menu-item");
		open.setOnAction((ActionEvent e) -> {

			Thread thread = new Thread(() -> {
				try {
					File documentFile = null;

					URL documentUrl = document.getUrl();
					File appDataTmp = new File(System.getProperty("java.io.tmpdir") + "/concrypt");
					appDataTmp.mkdir();
					documentFile = 	new File(System.getProperty("java.io.tmpdir") + "/concrypt/vk_"
									+ document.getStringRepresentation() + "." + document.getExtension());
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
		
		
		ContextMenu contextMenu = new ContextMenu(open);
		if (editable)
			contextMenu.getItems().add(remove);
		else 
			contextMenu.getItems().add(saveAs);
		this.setContextMenu(contextMenu);

		this.setOnMouseClicked((MouseEvent e) -> {
			e.consume();
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
