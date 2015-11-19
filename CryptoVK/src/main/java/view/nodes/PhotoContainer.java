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
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import model.Photo;

public class PhotoContainer {

	public PhotoContainer(Boolean incomingMessage) {
		this.root = new FlowPane(Orientation.HORIZONTAL);
		this.root.getStyleClass().add(incomingMessage ? "photo-container-incoming" : "photo-container");
		this.root.setPrefWidth(0);
	}

	public void addImage(Photo attachmentPhoto) {
		
		double fullWidth = attachmentPhoto.getWidth();
		double fullHeight = attachmentPhoto.getHeight();
		double addedWidth = 15 + fullWidth > fullHeight ? 150 : fullWidth / fullHeight * 150;

		if (root.getPrefWidth() < 600)
			root.setPrefWidth(addedWidth + root.getPrefWidth());

		addActionableImageView(attachmentPhoto);
	}

	public void clear() {
		this.root.getChildren().clear();
		this.root.setPrefWidth(0);
	}

	public Pane getRoot() {
		return root;
	}

	private void addActionableImageView(Photo photo) {
		ImageView actionableImage = new ImageView();
		ImageOperator.asyncLoadImage(actionableImage, photo.getPreviewURL());

		MenuItem open = new MenuItem("Open");
		open.getStyleClass().add("image-context-menu-item");
		open.setOnAction((ActionEvent e) -> {

			Thread thread = new Thread(() -> {
				try {
					BufferedImage fullImage;
					File fullImageFile = null;

					fullImage = ImageIO.read(new URL(photo.getLargestResolutionUrl()));
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
		actionableImage.setOnContextMenuRequested((ContextMenuEvent e) -> {
			imageMenu.show(actionableImage, e.getScreenX(), e.getScreenY());
		});

		root.getChildren().add(actionableImage);
	}

	private FlowPane root;
}
