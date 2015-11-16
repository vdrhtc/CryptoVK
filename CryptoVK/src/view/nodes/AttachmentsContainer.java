package view.nodes;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.json.JSONObject;

import data.ImageOperator;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.Photo;

public class AttachmentsContainer extends VBox {

	public AttachmentsContainer() {
		this.getChildren().add(photoContainer);
	}

	public AttachmentsContainer(ArrayList<JSONObject> attachments) {
		photoContainer.getStyleClass().add("photo-container");
		photoContainer.setPrefWidth(0);

		for (JSONObject attachment : attachments) {
			switch (attachment.getString("type")) {
			case "photo":
				Photo attachmentPhoto = new Photo(attachment.getJSONObject("photo"));
				double fullWidth = attachmentPhoto.getWidth();
				double fullHeight = attachmentPhoto.getHeight();
				double addedWidth = 15 + fullWidth > fullHeight ? 150 : fullWidth / fullHeight * 150;

				if (photoContainer.getPrefWidth() < 600)
					photoContainer.setPrefWidth(addedWidth + photoContainer.getPrefWidth());

				addActionableImageViewToContainer(attachmentPhoto);

				break;
			default:
				break;
			}
		}
		getChildren().add(photoContainer);
	}

	private void addActionableImageViewToContainer(Photo attachmentPhoto) {
		photoContainer.getChildren().add(
				createActionableImageView(attachmentPhoto.getPreviewURL(), attachmentPhoto.getLargestResolutionUrl()));
	}

	private ImageView createActionableImageView(String iconUrl, String uri) {
		ImageView actionableImage = new ImageView();
		ImageOperator.asyncLoadImage(actionableImage, iconUrl);

		MenuItem open = new MenuItem("Open");
		open.getStyleClass().add("image-context-menu-item");
		open.setOnAction((ActionEvent e) -> {

			Thread thread = new Thread(() -> {
				try {
					BufferedImage fullImage;
					File fullImageFile = null;

					fullImage = ImageIO.read(new URL(uri));
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
		return actionableImage;
	}

	public void addImage(Photo photoAttachment) {
		ImageView newPhotoAttachment = new ImageView();
		ImageOperator.asyncLoadImage(newPhotoAttachment, photoAttachment.getPreviewURL());
		photoContainer.getChildren().add(newPhotoAttachment);
	}

	public void clear() {
		getChildren().clear();
		photoContainer = new FlowPane(Orientation.HORIZONTAL);
		getChildren().add(photoContainer);

	}

	private FlowPane photoContainer = new FlowPane(Orientation.HORIZONTAL);

}
