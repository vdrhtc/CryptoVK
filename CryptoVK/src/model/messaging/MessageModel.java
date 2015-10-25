package model.messaging;

import java.util.Date;

import org.json.JSONObject;

import data.DataOperator;
import model.VKPerson;

public class MessageModel {

	public MessageModel(int id, Date date, String text, Boolean read, VKPerson sender) {
		super();
		this.id = id;
		this.date = date;
		this.text = text;
		this.read = read;
		this.sender = sender;
	}

	public MessageModel(JSONObject content) {
		this.date = new Date(content.getLong("date") * 1000);
		this.id = content.getInt("id");
		if (content.getInt("out") !=1 )
			this.sender = VKPerson.getKnownPerson(content.getInt("from_id"));
		else
			this.sender = VKPerson.getOwner();
		this.text = content.getString("body");
		this.read = content.getInt("read_state") == 1;
	}
	
	public boolean isIncoming() {
		return !sender.equals(VKPerson.getOwner());
	}
	
	
	private int id;
	private Date date;
	private String text;
	private Boolean read;
	private VKPerson sender;	
	
	public MessageModel clone() {
		return new MessageModel(id, date, text, read, sender);
	}

	public int getId() {
		return id;
	}

	public String getDate() {
		return DataOperator.formatDate(date);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + id;
		result = prime * result + ((read == null) ? 0 : read.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MessageModel)) {
			return false;
		}
		MessageModel other = (MessageModel) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (read == null) {
			if (other.read != null) {
				return false;
			}
		} else if (!read.equals(other.read)) {
			return false;
		}
		if (sender == null) {
			if (other.sender != null) {
				return false;
			}
		} else if (!sender.equals(other.sender)) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "MessageModel [id=" + id + ", date=" + date + ", text=" + text + ", read=" + read + ", sender=" + sender
				+ "]";
	}

	public Boolean isRead() {
		return read;
	}

	public VKPerson getSender() {
		return sender;
	}
	
}
