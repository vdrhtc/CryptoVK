package model;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class Document extends Attachment{
	
	public Document(JSONObject content) {
		this.ownerId = content.getInt("owner_id");
		this.documentId = content.getInt("id");
		this.title = content.getString("title");
		this.size = content.getDouble("size");
		this.extension = content.getString("ext");
		try {
			this.url = new URL(content.getString("url"));
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getDisplayedName() {
		return title+" ("+size/1E3+" kB)";
	}
	
	public String getName() {
		return title.split("\\.")[0];
	}
	
	
	public URL getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return "doc"+ownerId+"_"+documentId;
	}
	
	public String getExtension() {
		return extension;
	}
	
	private String title;
	private Integer documentId;
	private Integer ownerId;
	private Double size;
	private URL url;
	private String extension;

	public AttachmentType getType() {
		return AttachmentType.DOCUMENT;
	}
}
