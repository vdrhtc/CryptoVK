package model;

import org.json.JSONObject;

public class Photo extends Attachment {
	
	public static String[] vKPhotoSizes = { "2560", "1280", "807", "604", "130", "75" };

	public Photo(JSONObject content) {
		this.ownerId = content.getInt("owner_id");
		this.photoId = content.getInt("id");
		this.previewUrl = content.getString("photo_130");
		this.width = Double.isNaN(content.optDouble("width")) ? 130 : content.getDouble("width");
		this.height = Double.isNaN(content.optDouble("height")) ? 130 : content.getDouble("height");

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
	private Double width;
	private Double height;
	private String largestResolutionUrl;
	
	public String getPreviewURL() {
		return previewUrl;
	}
	
	@Override
	public String getStringRepresentation() {
		return "photo"+ownerId+"_"+photoId;
	}
	
	public String getLargestResolutionUrl() {
		return largestResolutionUrl;
	}
	
	public Double getWidth() {
		return width;
	}
	
	public Double getHeight() {
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
