package view.nodes;

import java.util.ArrayList;

import org.json.JSONObject;

import javafx.scene.layout.VBox;
import model.Attachment;
import model.Attachment.AttachmentType;
import model.Document;
import model.MessageModel;
import model.Photo;

public class AttachmentsContainer extends VBox {

	public AttachmentsContainer(Boolean isMessageIncoming, Boolean editable) {
		this.photoContainer = new PhotoContainer(isMessageIncoming, editable);
		this.documentContainer = new DocumentContainer(isMessageIncoming, editable);
		this.forwardedMessagesContainer = new ForwardedMessagesContainer(editable);
		getChildren().addAll(photoContainer.getRoot(), documentContainer.getRoot(), forwardedMessagesContainer.getRoot());
	}

	public AttachmentsContainer(ArrayList<JSONObject> attachments, Boolean isMessageIncoming, Boolean editable) {
		this.photoContainer = new PhotoContainer(isMessageIncoming, editable);
		this.documentContainer = new DocumentContainer(isMessageIncoming, editable);
		this.forwardedMessagesContainer = new ForwardedMessagesContainer(editable);
		
		for (JSONObject attachment : attachments) {
			switch (attachment.optString("type")) {
			case "photo":
				addImage(new Photo(attachment.getJSONObject("photo")));
				break;

			case "doc":
				addDocument(new Document(attachment.getJSONObject("doc")));
				break;	
			
			case "":
				addMessage(new MessageModel(attachment));
				break;
			}
		}
		getChildren().addAll(photoContainer.getRoot(), documentContainer.getRoot(), forwardedMessagesContainer.getRoot());
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

	private void addImage(Photo photo) {
		photoContainer.addImage(photo);
	}
	
	private void addMessage(MessageModel message) {
		forwardedMessagesContainer.addMessage(message);
	}

	public void clear() {
		photoContainer.clear();
		documentContainer.clear();
		forwardedMessagesContainer.clear();
	}

	private PhotoContainer photoContainer;
	private DocumentContainer documentContainer;
	private ForwardedMessagesContainer forwardedMessagesContainer;
	
	public ArrayList<Attachment> getAttachments() {
		ArrayList<Attachment> tmp = new ArrayList<>();
		tmp.addAll(photoContainer.getPhotos());
		tmp.addAll(documentContainer.getDocuments());
		tmp.addAll(forwardedMessagesContainer.getAttachments());
		return tmp;
	}
}
