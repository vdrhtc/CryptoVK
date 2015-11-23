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
		File appData = new File(System.getProperty("user.home")+"/.concrypt");
		appData.mkdir();
	}
	public static final String READ_STATE_DATABASE = System.getProperty("user.home")+"/.concrypt/readStateDatabase.json";

	public static void put(Integer chatId, long lastMessageId, ChatReadState RS) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		JSONObject state = db.optJSONObject(chatId.toString());
		if (state == null)
			state = new JSONObject().put("lastMessageId", lastMessageId).put("readState", RS);
		else
			state.put("lastMessageId", lastMessageId).put("readState", RS);
		db.put(chatId.toString(), state);
		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	public static void putMessage(Integer chatId, Long messageId, MessageReadState RS, boolean out) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		JSONObject messageInfo = new JSONObject().put("readState", RS.toString()).put("out", out);
		JSONObject chatReadStateInfo = db.getJSONObject(chatId.toString()).put(messageId.toString(), messageInfo);
		db.put(chatId.toString(), chatReadStateInfo);
		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	@SuppressWarnings("unchecked")
	public static void clear(Integer chatId) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		JSONObject old = (JSONObject) db.remove(chatId.toString());
		JSONObject newObj = new JSONObject();
		Iterator<String> iter = old.keys();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key.equals("lastMessageId") || key.equals("readState"))
				newObj.put(key, old.get(key));
			else if (old.getJSONObject(key).getBoolean("out")
					&& !old.getJSONObject(key).getString("readState").equals("READ"))
				newObj.put(key, old.getJSONObject(key));
		}
		db.put(chatId.toString(), newObj);
		writeJSONtoFile(READ_STATE_DATABASE, db);
	}

	public static JSONObject optChatJSON(Integer chatId) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		return db.optJSONObject(chatId.toString());
	}

	public static void writeJSONtoFile(String path, JSONObject JO) {
		rwlock.writeLock().lock();
		try {
			Files.write(Paths.get(path),
					JO.toString(4).getBytes(),
					StandardOpenOption.TRUNCATE_EXISTING);
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
