package view.nodes;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import model.Attachment;
import model.Document;

public class DocumentContainer {

	public DocumentContainer(Boolean isMessageIncoming, Boolean editable) {
		this.editable = editable;
		this.root = new FlowPane(Orientation.HORIZONTAL);
		this.root.setPrefWidth(0);
		this.root.setMinWidth(Region.USE_PREF_SIZE);
//		this.root.setStyle("-fx-border-color:red;");
		this.root.getStyleClass().add("document-container");
	}

	public void addDocument(Document document) {
		DocumentView documentView = new DocumentView(document, editable);
		documentView.getRemovalRequested().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == true && editable) {
					documents.remove(document);
					root.getChildren().remove(documentView);
				}
			}
		});
		if (root.getPrefWidth() < 300) {
			if (documents.size() == 0)
				root.setPrefWidth(root.getPrefWidth() + documentView.getPrefWidth());
			else
				root.setPrefWidth(root.getPrefWidth() + documentView.getPrefWidth() + 20);
		}
		documents.add(document);
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
