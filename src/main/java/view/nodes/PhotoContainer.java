package view.nodes;

import java.io.File;
import java.util.ArrayList;

import data.ImageOperator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import model.Attachment;
import model.Photo;

public class PhotoContainer {

	public PhotoContainer(Boolean incomingMessage, Boolean editable) {
		this.editable = editable;
		this.root = new FlowPane(Orientation.HORIZONTAL);
		this.root.getStyleClass().add(incomingMessage ? "photo-container-incoming" : "photo-container");
		this.root.setPrefWidth(0);
	}
	

	public void addImage(Photo photo) {
		PhotoView photoView = new PhotoView(photo, buildSaveAllMenuItem());
		photoView.getRemovalRequested().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == true && editable) {
					photos.remove(photo);
					root.getChildren().remove(photoView);
				}
			}
		});
		
		double photoWidth = photo.getWidth() > photo.getHeight() ? 130 : photo.getWidth()/photo.getHeight()*130;
		root.setPrefWidth(root.getPrefWidth()+photoWidth+15);
		photos.add(photo);
		root.getChildren().add(photoView);
	}

	public void clear() {
		this.photos.clear();
		this.root.getChildren().clear();
	}

	private MenuItem buildSaveAllMenuItem() {
		MenuItem saveAll = new MenuItem("Save all as...");
		saveAll.setOnAction((ActionEvent a) -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("Choose folder");
			File folder = chooser.showDialog(root.getScene().getWindow());
			for (Attachment p : photos) {
				Photo photo = (Photo) p;
				File file = new File(folder, photo.toString());
				ImageOperator.saveImageFromUrl(photo.getLargestResolutionUrl(), file, false);
			}
		});
		return saveAll;
		
	}
	
	private ArrayList<Attachment> photos = new ArrayList<>();
	private FlowPane root;
	private Boolean editable;

	public ArrayList<Attachment> getPhotos() {
		return photos;
	}
	
	public Pane getRoot() {
		return root;
	}
	
}
