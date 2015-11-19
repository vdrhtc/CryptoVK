package model;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import data.DataOperator;
import data.ReadStatesDatabase;
import data.ReadStatesDatabase.ReadState;

public class MessageModel {

	public MessageModel(Long id, Date date, String text, VKPerson sender, ReadState RS, int chatId) {
		this.id = id;
		this.date = date;
		this.text = text;
		this.sender = sender;
		this.RS = RS;
		this.chatId = chatId;
	}

	public MessageModel(JSONObject content) {
		this.date = new Date(content.getLong("date") * 1000);
		this.id = content.getLong("id");
		if (content.getInt("out") != 1)
			this.sender = VKPerson.getKnownPerson(content.getInt("user_id"));
		else
			this.sender = VKPerson.getOwner();
		this.text = content.getString("body");
		this.chatId = content.optInt("chat_id")==0 ? content.getInt("user_id") : content.getInt("chat_id");
		
		JSONArray attachments = content.optJSONArray("attachments");
		if (attachments != null)
			for (int i = 0; i<attachments.length(); i++) 
				this.attachments.add(attachments.getJSONObject(i));
		
		setOrRecallReadState(content.getInt("read_state") == 1 ? true : false);
	}

	public ArrayList<JSONObject> getAttachments() {
		return attachments;
	}

	public boolean isIncoming() {
		return !sender.equals(VKPerson.getOwner());
	}

	public void setReadState(ReadState RS) {
		this.RS = RS;
		ReadStatesDatabase.putMessage(chatId, id, RS, !isIncoming());
	}

	private void setOrRecallReadState(boolean read) {
		
		if (ReadStatesDatabase.optChatJSON(chatId) == null) {
			ReadStatesDatabase.put(chatId, id, read ? ReadState.READ : ReadState.UNREAD);
			setReadState(read ? ReadState.READ : ReadState.UNREAD);
		}
		
		JSONObject info = ReadStatesDatabase.optChatJSON(chatId).optJSONObject(id.toString());
		ReadState RS = read ? ReadState.READ : ReadState.UNREAD;
		if (info == null) {
			setReadState(RS);
		} else if (RS == ReadState.READ) {
			setReadState(ReadState.READ);
		} else {
			setReadState(ReadState.valueOf(info.getString("readState")));
		}
	}

	private Long id;
	private Date date;
	private String text;
	private VKPerson sender;
	private ReadState RS;
	private Integer chatId;
	private ArrayList<JSONObject> attachments = new ArrayList<>();

	public MessageModel clone() {
		return new MessageModel(id, date, text, sender, RS, chatId);
	}
	
	public Integer getChatId() {
		return chatId;
	}

	public Long getId() {
		return id;
	}

	public String getDate() {
		return DataOperator.formatDate(date);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((RS == null) ? 0 : RS.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = (int) (prime * result + id);
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
		if (RS != other.RS) {
			return false;
		}
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
		return "MessageModel [id=" + id + ", date=" + date + ", text=" + text + ", read=" + RS.toString() + ", sender="
				+ sender + "]";
	}

	public ReadState getReadState() {
		return RS;
	}
	public VKPerson getSender() {
		return sender;
	}


}
