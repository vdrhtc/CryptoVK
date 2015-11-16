package model;

import org.json.JSONObject;

public class Photo extends Attachment {
	
	public static String[] vKPhotoSizes = { "2560", "1280", "807", "604", "130", "75" };


	public Photo(JSONObject content) {
		this.setOwner(VKPerson.getKnownPerson(content.getInt("owner_id")));
		this.setPhotoId(content.getInt("id"));
		this.previewUrl = content.getString("photo_130");
		this.width = content.getInt("width");
		this.height = content.getInt("height");

		largestResolutionUrl = "";
		for (String size : vKPhotoSizes) {
			String value = content.optString("photo_" + size);
			if (!value.equals("")) {
				largestResolutionUrl = value;
				break;
			}
		}		
	}

	private VKPerson owner;
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
		return "photo"+owner.getId()+"_"+photoId;
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
	
	public VKPerson getOwner() {
		return owner;
	}
	public void setOwner(VKPerson owner) {
		this.owner = owner;
	}

	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}


}
