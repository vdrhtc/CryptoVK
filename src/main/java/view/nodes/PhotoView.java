package view.nodes;

import java.io.File;

import data.ImageOperator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Photo;

public class PhotoView extends ImageView {

	public PhotoView(Photo model, MenuItem saveAll) {
		super();

		MenuItem open = new MenuItem("Open");
		open.getStyleClass().add("image-context-menu-item");
		open.setOnAction((ActionEvent e) -> {
			try {
				ImageOperator.saveImageFromUrl(model.getLargestResolutionUrl(),
						File.createTempFile("vk_photo", ".jpg"), true);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});

		contextMenu.getItems().addAll(open, saveAll);

		this.setOnContextMenuRequested((ContextMenuEvent e) -> {
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
			e.consume();
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
