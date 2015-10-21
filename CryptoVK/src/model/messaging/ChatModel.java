package model.messaging;

import java.util.ArrayList;
import java.util.Arrays;

import model.VKPerson;
import model.preview.ChatPreviewModel;

public class ChatModel {
	


	public ChatModel(ChatPreviewModel sourcePreviewModel) {
		this.chatId = sourcePreviewModel.getChatId();
		this.chatIconURL = sourcePreviewModel.getChatIconURL();
		this.chatTitle = sourcePreviewModel.getTitle();
	}
	
	public void loadNewMessages(int count) {
		
	}

	private int chatId;
	private String chatTitle;
	private int lastMessageId;
	private String[] chatIconURL;
	private ArrayList<MessageModel> loadedMessages = new ArrayList<>();

	
	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public String[] getChatIconURL() {
		return chatIconURL;
	}

	public void setChatIconURL(String[] chatIconURL) {
		this.chatIconURL = chatIconURL;
	}

	public ArrayList<MessageModel> getLoadedMessages() {
		return loadedMessages;
	}

	public void setLoadedMessages(ArrayList<MessageModel> loadedMessages) {
		this.loadedMessages = loadedMessages;
	}

	public int getLastMessageId() {
		return lastMessageId;
	}

	public void setLastMessageId(int lastMessageId) {
		this.lastMessageId = lastMessageId;
	}

	public String getTitle() {
		return chatTitle;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(chatIconURL);
		result = prime * result + chatId;
		result = prime * result + ((chatTitle == null) ? 0 : chatTitle.hashCode());
		result = prime * result + lastMessageId;
		result = prime * result + ((loadedMessages == null) ? 0 : loadedMessages.hashCode());
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
		if (!(obj instanceof ChatModel)) {
			return false;
		}
		ChatModel other = (ChatModel) obj;
		if (!Arrays.equals(chatIconURL, other.chatIconURL)) {
			return false;
		}
		if (chatId != other.chatId) {
			return false;
		}
		if (chatTitle == null) {
			if (other.chatTitle != null) {
				return false;
			}
		} else if (!chatTitle.equals(other.chatTitle)) {
			return false;
		}
		if (lastMessageId != other.lastMessageId) {
			return false;
		}
		if (loadedMessages == null) {
			if (other.loadedMessages != null) {
				return false;
			}
		} else if (!loadedMessages.equals(other.loadedMessages)) {
			return false;
		}
		return true;
	}


}
