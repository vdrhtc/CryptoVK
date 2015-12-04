package model;

import org.json.JSONObject;

public class Photo extends Attachment {
	
	public static String[] vKPhotoSizes = { "2560", "1280", "807", "604", "130", "75" };

	public Photo(JSONObject content) {
		this.ownerId = content.getInt("owner_id");
		this.photoId = content.getInt("id");
		this.previewUrl = content.getString("photo_130");
		this.width = content.optInt("width") == 0 ? 130 : content.getInt("width");
		this.height = content.optInt("height") == 0 ? 130 : content.getInt("height");

		largestResolutionUrl = "";
		for (String size : vKPhotoSizes) {
			String value = content.optString("photo_" + size);
			if (!value.equals("")) {
				largestResolutionUrl = value;
				break;
			}
		}		
	}

	private Integer ownerId;
	private int photoId;
	private String previewUrl;
	private int width;
	private int height;
	private String largestResolutionUrl;
	
	public String getPreviewURL() {
		return previewUrl;
	}
	
	@Override
	public String toString() {
		return "photo"+ownerId+"_"+photoId;
	}
	
	public String getLargestResolutionUrl() {
		return largestResolutionUrl;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	

	public int getPhotoId() {
		return photoId;
	}

	@Override
	public AttachmentType getType() {
		return AttachmentType.PHOTO;
	}


}
