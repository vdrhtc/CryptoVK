package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

public class ReadStatesDatabase {

	public enum MessageReadState {
		READ, UNREAD, VIEWED;
	}

	public enum ChatReadState {
		READ, UNREAD, POSTPONED, VIEWED;
	}

	static {
		File appData = new File(System.getProperty("user.home") + "/.concrypt");
		appData.mkdir();
	}

	public static final String READ_STATE_DATABASE = System.getProperty("user.home")
			+ "/.concrypt/readStateDatabase.json";

	public static void putChat(Long chatId, Long lastMessageId, boolean lastMessageOut, ChatReadState RS) {
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
			state.put("lastMessageId", lastMessageId).put("readState", RS);
		}

		if (lastMessageOut == false || previousLastMessageOut == false)
			if (RS == ChatReadState.READ || RS == ChatReadState.VIEWED) {
				if (previousReadState == ChatReadState.UNREAD) {
					db.put("unreadCount", db.optInt("unreadCount") - 1);
					System.out.println("&&&&&& Decrementing: " + (db.optInt("unreadCount") - 1));
				}
			} else if (RS == ChatReadState.UNREAD)
				if (previousReadState != RS) {
					db.put("unreadCount", db.optInt("unreadCount") + 1);
					System.out.println("&&&&&& Incrementing: "+(db.optInt("unreadCount") + 1));
				}

		if (RS == ChatReadState.READ)
			db = clear(db, chatId); // No need to store read chat data

		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	public static void putMessage(Long chatId, Long messageId, boolean out, MessageReadState RS) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		JSONObject messageInfo = new JSONObject().put("readState", RS.toString()).put("out", out);
		db.getJSONObject(chatId.toString()).put(messageId.toString(), messageInfo);
		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	public static Integer getUnreadCounter() {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		return db.optInt("unreadCount");
	}

	@SuppressWarnings("unchecked")
	private static JSONObject clear(JSONObject db, Long chatId) {
		JSONObject old = (JSONObject) db.remove(chatId.toString());
		JSONObject newObj = new JSONObject();
		Iterator<String> iter = old.keys();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key.equals("lastMessageId") || key.equals("readState") || key.equals("lastMessageOut"))
				newObj.put(key, old.get(key));
			else if (old.getJSONObject(key).getBoolean("out")
					&& !old.getJSONObject(key).getString("readState").equals("READ"))
				newObj.put(key, old.getJSONObject(key));
		}
		db.put(chatId.toString(), newObj);
		return db;
	}

	public static JSONObject optChatJSON(Long chatId) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		return db.optJSONObject(chatId.toString());
	}

	public static void writeJSONtoFile(String path, JSONObject JO) {
		rwlock.writeLock().lock();
		try {
			Files.write(Paths.get(path), JO.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			rwlock.writeLock().unlock();
		}
	}

	public static synchronized JSONObject readJSONfromFile(String path) {
		String jsonString = "";
		rwlock.readLock().lock();
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
		rwlock.readLock().unlock();
		return JS;
	}

	public static void initializeGeneralInfoFile(Path path) {
		try {
			Files.createFile(path);
			JSONObject JO = new JSONObject();
			Files.write(path, JO.toString().getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static ReadWriteLock rwlock = new ReentrantReadWriteLock();
}
