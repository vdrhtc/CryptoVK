package model;

public abstract class Attachment {
	
	public enum AttachmentType {
		DOCUMENT, PHOTO
	}
	
	public abstract AttachmentType getType();
}
