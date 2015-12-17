package view.nodes;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import model.Attachment;
import model.Document;

public class DocumentContainer {
	
	public DocumentContainer(Boolean isMessageIncoming, Boolean editable) {
		this.editable = editable;
		this.root = new FlowPane(Orientation.HORIZONTAL);
		this.root.setPrefWidth(0);
		this.root.getStyleClass().add(isMessageIncoming ? "document-container-incoming" : "document-container");
	}

	public void addDocument(Document document) {
		documents.add(document);
		DocumentView documentView = new DocumentView(document, editable);
		documentView.getRemovalRequested().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == true && editable) {
					documents.remove(document);
					root.getChildren().remove(documentView);
				}
			}
		});
		root.prefWidthProperty().bind(documentView.widthProperty());
		this.root.getChildren().add(documentView);
	}

	public Pane getRoot() {
		return root;
	}

	private FlowPane root;
	private Boolean editable;
	private ArrayList<Attachment> documents = new ArrayList<>();

	public void clear() {
		documents.clear();
		root.getChildren().clear();
	}

	public ArrayList<Attachment> getDocuments() {
		return documents;
	}
}
