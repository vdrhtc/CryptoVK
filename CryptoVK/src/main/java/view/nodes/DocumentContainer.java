package view.nodes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import model.Document;

public class DocumentContainer {

	public DocumentContainer(Boolean isMessageIncoming) {
		this.root = new FlowPane(Orientation.HORIZONTAL);
		this.root.getStyleClass().add(isMessageIncoming ? "document-container-incoming" : "document-container");
	}

	public void addDocument(Document document) {
		Label documentView = new Label(document.getDisplayedName());
		documentView.getStyleClass().add("document-label");

		MenuItem open = new MenuItem("Open");
		open.getStyleClass().add("image-context-menu-item");
		open.setOnAction((ActionEvent e) -> {

			Thread thread = new Thread(() -> {
				try {
					File documentFile = null;

					URL documentUrl = document.getUrl();
					documentFile = File.createTempFile("vk_document", document.getName()+"."+document.getExtension());
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
		documentView.setContextMenu(new ContextMenu(open));
		
		this.root.getChildren().add(documentView);
	}

	public Pane getRoot() {
		return root;
	}

	private FlowPane root;

	public void clear() {
		root.getChildren().clear();
	}
}
