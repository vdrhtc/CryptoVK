package view.nodes;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

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

	public PhotoView(Photo model) {
		super();

		MenuItem open = new MenuItem("Open");
		open.getStyleClass().add("image-context-menu-item");
		open.setOnAction((ActionEvent e) -> {

			Thread thread = new Thread(() -> {
				try {
					BufferedImage fullImage;
					File fullImageFile = null;

					fullImage = ImageIO.read(new URL(model.getLargestResolutionUrl()));
					fullImageFile = File.createTempFile("vk_photo", ".jpg");
					Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
					ImageWriter writer = (ImageWriter) iter.next();
					ImageWriteParam iwp = writer.getDefaultWriteParam();
					iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					iwp.setCompressionQuality((float) 0.9);
					FileImageOutputStream output = new FileImageOutputStream(fullImageFile);
					writer.setOutput(output);
					IIOImage image = new IIOImage(fullImage, null, null);
					writer.write(null, image, iwp);
					Desktop.getDesktop().open(fullImageFile);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			thread.start();
		});
		ContextMenu imageMenu = new ContextMenu(open);
		this.setOnContextMenuRequested((ContextMenuEvent e) -> {
			imageMenu.show(this, e.getScreenX(), e.getScreenY());
		});
		
		this.setOnMouseClicked((MouseEvent e) -> {
			if (e.getButton().equals(MouseButton.MIDDLE))
				removalRequested.setValue(true);
			else if (e.getButton().equals(MouseButton.PRIMARY))
				if (e.getClickCount() > 1)
					removalRequested.setValue(true);
		});

		ImageOperator.asyncLoadImage(this, model.getPreviewURL());
	}
	
	private BooleanProperty removalRequested = new SimpleBooleanProperty(false);
	
	public BooleanProperty getRemovalRequested() {
		return removalRequested;
	}
	
}
