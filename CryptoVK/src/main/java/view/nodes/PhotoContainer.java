package view.nodes;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
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
		PhotoView photoView = new PhotoView(photo);
		photoView.getRemovalRequested().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == true && editable) {
					photos.remove(photo);
					root.getChildren().remove(photoView);
				}
			}
		});
		photos.add(photo);
		root.getChildren().add(photoView);
	}

	public void clear() {
		this.root.getChildren().clear();
		this.root.setPrefWidth(0);
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
