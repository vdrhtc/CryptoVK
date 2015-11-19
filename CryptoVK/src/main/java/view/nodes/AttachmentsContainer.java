package view.nodes;

import java.util.ArrayList;

import org.json.JSONObject;

import javafx.scene.layout.VBox;
import model.Attachment;
import model.Attachment.AttachmentType;
import model.Document;
import model.Photo;

public class AttachmentsContainer extends VBox {

	public AttachmentsContainer(Boolean isMessageIncoming) {
		this.photoContainer = new PhotoContainer(isMessageIncoming);
		this.documentContainer = new DocumentContainer(isMessageIncoming);
		this.getChildren().addAll(photoContainer.getRoot(), documentContainer.getRoot());
	}

	public AttachmentsContainer(ArrayList<JSONObject> attachments, Boolean isMessageIncoming) {
		this.photoContainer = new PhotoContainer(isMessageIncoming);
		this.documentContainer = new DocumentContainer(isMessageIncoming);

		for (JSONObject attachment : attachments) {
			switch (attachment.getString("type")) {
			case "photo":
				addImage(new Photo(attachment.getJSONObject("photo")));
				break;

			case "doc":
				addDocument(new Document(attachment.getJSONObject("doc")));
				break;	
			
			default:
				break;
			}
		}
		getChildren().addAll(photoContainer.getRoot(), documentContainer.getRoot());
	}
	
	public void addAttachment(Attachment attachment) {
		if (attachment.getType() == AttachmentType.DOCUMENT) 
			addDocument((Document) attachment);
		else 
			addImage((Photo) attachment);
	}

	private void addDocument(Document document) {
		documentContainer.addDocument(document);
	}

	private void addImage(Photo photoAttachment) {
		photoContainer.addImage(photoAttachment);
	}

	public void clear() {
		photoContainer.clear();
		documentContainer.clear();
		
	}

	private PhotoContainer photoContainer;
	private DocumentContainer documentContainer;
}
