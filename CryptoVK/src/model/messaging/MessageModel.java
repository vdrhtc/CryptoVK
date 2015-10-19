package model.messaging;

import java.util.Date;

import model.VKPerson;

import org.json.JSONObject;

public class MessageModel {

	public MessageModel(JSONObject content) {
		this.date = new Date(content.getLong("date") * 1000);
		this.id = content.getInt("id");
		if (content.getInt("out") !=1 )
			this.sender = VKPerson.getKnownPerson(content.getInt("from_id"));
		else
			this.sender = VKPerson.getOwner();
	}
	
	private int id;
	private Date date;
	private VKPerson sender;
	private String text;
	private Boolean read;
	
	
	
}
