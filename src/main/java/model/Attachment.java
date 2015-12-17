package model;

public abstract class Attachment {
	
	public enum AttachmentType {
		DOCUMENT, PHOTO, MESSAGE
	}
	
	public abstract String getStringRepresentation();
	
	public abstract AttachmentType getType();
}
