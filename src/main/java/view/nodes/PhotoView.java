package view.nodes;

import java.io.File;

import data.ImageOperator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import model.Photo;

public class PhotoView extends ImageView {

	public PhotoView(Photo model, MenuItem saveAll, Boolean editable) {
		super();

		MenuItem open = new MenuItem("Open");
		open.getStyleClass().add("image-context-menu-item");
		open.setOnAction((ActionEvent e) -> {
			try {
				File appDataTmp = new File(System.getProperty("java.io.tmpdir") + "/concrypt");
				appDataTmp.mkdir();
				ImageOperator.saveImageFromUrl(model.getLargestResolutionUrl(),
						new File(System.getProperty("java.io.tmpdir") + "/concrypt/vk_"
								+ model.getStringRepresentation() + ".jpg"),
						true);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		MenuItem saveAs = new MenuItem("Save As...");
		saveAs.getStyleClass().add("image-context-menu-item");
		saveAs.setOnAction((ActionEvent a) -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Save As");
			chooser.setInitialFileName(model.toString() + ".jpg");
			File file = chooser.showSaveDialog(this.getScene().getWindow());
			if (file != null) {
				ImageOperator.saveImageFromUrl(model.getLargestResolutionUrl(), file, false);
			}
		});
		MenuItem remove = new MenuItem("Remove");
		remove.getStyleClass().add("image-context-menu-item");
		remove.setOnAction((ActionEvent a) -> {
			removalRequested.setValue(true);
		});

		contextMenu.getItems().add(open);
		if (editable)
			contextMenu.getItems().add(remove);
		else
			contextMenu.getItems().addAll(saveAs, new SeparatorMenuItem(), saveAll);

		this.setOnContextMenuRequested((ContextMenuEvent e) -> {
			e.consume();
			contextMenu.show(this, e.getScreenX(), e.getScreenY());
		});

		this.setOnMouseClicked((MouseEvent e) -> {
			e.consume();
			if (e.getButton().equals(MouseButton.MIDDLE))
				removalRequested.setValue(true);
			else if (e.getButton().equals(MouseButton.PRIMARY))
				if (e.getClickCount() > 1)
					removalRequested.setValue(true);
		});
		this.setOnMousePressed((MouseEvent e) -> {
			contextMenu.hide();
		});

		ImageOperator.asyncLoadImage(this, model.getPreviewURL());
	}

	private ContextMenu contextMenu = new ContextMenu();
	private BooleanProperty removalRequested = new SimpleBooleanProperty(false);

	public BooleanProperty getRemovalRequested() {
		return removalRequested;
	}

}
