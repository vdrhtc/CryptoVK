package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadStatesDatabase {

	public enum MessageReadState {
		READ, UNREAD, VIEWED;
	}

	public enum ChatReadState {
		READ, UNREAD, POSTPONED, VIEWED;
	}

	public static final Integer DATABASE_VERSION = 3;
	public static final String READ_STATE_DATABASE = System.getProperty("user.home")
			+ "/.concrypt/readStateDatabase.json";

	static {
		File appData = new File(System.getProperty("user.home") + "/.concrypt");
		appData.mkdir();
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		if (db.optInt("version") < DATABASE_VERSION)
			db = new JSONObject().put("version", DATABASE_VERSION);
		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	private static Logger log = LoggerFactory.getLogger(ReadStatesDatabase.class);

	public static synchronized void putChat(Long chatId, Long lastMessageId, boolean lastMessageOut, ChatReadState RS) {

		log.debug("Putting chat: chatId=" + chatId + ", lastMessageId=" + lastMessageId + ", lastMessageOut="
				+ lastMessageOut + ", RS=" + RS);

		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		JSONObject state = db.optJSONObject(chatId.toString());
		ChatReadState previousReadState = null;
		Boolean previousLastMessageOut = true;
		if (state == null) { // No record, creating:
			state = new JSONObject().put("lastMessageId", lastMessageId).put("readState", RS).put("lastMessageOut",
					lastMessageOut);
			db.put(chatId.toString(), state);
		} else {
			previousReadState = ChatReadState.valueOf(state.getString("readState"));
			previousLastMessageOut = state.getBoolean("lastMessageOut");
			state.put("lastMessageId", lastMessageId).put("readState", RS).put("lastMessageOut", lastMessageOut);
		}

		if (lastMessageOut == false || previousLastMessageOut == false)
			if (RS == ChatReadState.READ || RS == ChatReadState.VIEWED) {
				if (previousReadState == ChatReadState.UNREAD) {
					db.put("unreadCount", db.optInt("unreadCount") - 1);
					log.debug("Decrementing: " + (db.optInt("unreadCount")));
				}
			} else if (RS == ChatReadState.UNREAD)
				if (previousReadState != RS) {
					db.put("unreadCount", db.optInt("unreadCount") + 1);
					log.debug("Incrementing: " + (db.optInt("unreadCount")));
				}

		if (RS == ChatReadState.READ)
			db = clear(db, chatId); // No need to store read chat data

		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	public static synchronized void updateChatUnreadCount(Long chatId, Integer serverUnreadCount, ChatReadState RS) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		JSONObject chatJSON = db.getJSONObject(chatId.toString());
		if (RS == ChatReadState.READ)
			chatJSON.put("unreadCount", 0).put("viewedCount", 0);
		else if (RS == ChatReadState.VIEWED) {
			chatJSON.put("viewedCount", chatJSON.optInt("unreadCount")+chatJSON.optInt("viewedCount"));
			chatJSON.put("unreadCount", 0);
		}
		else if (RS == ChatReadState.UNREAD)
			chatJSON.put("unreadCount", serverUnreadCount - chatJSON.optInt("viewedCount"));
		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	public static synchronized Integer getChatUnreadCount(Long chatId) {
		return readJSONfromFile(READ_STATE_DATABASE).getJSONObject(chatId.toString()).getInt("unreadCount");
	}

	public static synchronized Integer getUnreadCount() {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		return db.optInt("unreadCount");
	}

	public static synchronized void putMessage(Long chatId, Long messageId, boolean out, MessageReadState RS) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		JSONObject messageInfo = new JSONObject().put("readState", RS.toString()).put("out", out);
		db.getJSONObject(chatId.toString()).put(messageId.toString(), messageInfo);
		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	public static synchronized JSONObject optChatJSON(Long chatId) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		return db.optJSONObject(chatId.toString());
	}

	@SuppressWarnings("unchecked")
	private static JSONObject clear(JSONObject db, Long chatId) {
		JSONObject old = (JSONObject) db.remove(chatId.toString());
		JSONObject newObj = new JSONObject();
		Iterator<String> iter = old.keys();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key.equals("lastMessageId") || key.equals("readState") || key.equals("lastMessageOut")
					|| key.equals("unreadCount") || key.equals("viewedCount"))
				newObj.put(key, old.get(key));
			else if (old.getJSONObject(key).getBoolean("out")
					&& !old.getJSONObject(key).getString("readState").equals("READ"))
				newObj.put(key, old.getJSONObject(key));
		}
		db.put(chatId.toString(), newObj);
		return db;
	}

	private static void writeJSONtoFile(String path, JSONObject JO) {
		try {
			Files.write(Paths.get(path), JO.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static JSONObject readJSONfromFile(String path) {
		String jsonString = "";
		try {
			Stream<String> openedFile = Files.lines(Paths.get(path));
			jsonString = openedFile.collect(Collectors.joining(""));
			openedFile.close();
		} catch (NoSuchFileException e) {
			initializeGeneralInfoFile(Paths.get(path));
			try {
				Stream<String> openedFile = Files.lines(Paths.get(path));
				jsonString = openedFile.collect(Collectors.joining(""));
				openedFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject JS = null;
		try {
			JS = new JSONObject(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JS;
	}

	private static void initializeGeneralInfoFile(Path path) {
		try {
			Files.createFile(path);
			JSONObject JO = new JSONObject();
			JO.put("version", DATABASE_VERSION);
			Files.write(path, JO.toString(4).getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
