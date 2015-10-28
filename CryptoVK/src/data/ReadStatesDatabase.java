package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

import org.json.JSONObject;

import model.MessageModel.ReadState;

public class ReadStatesDatabase {
	
	public static final String READ_STATE_DATABASE = "src/data/readStateDatabase.json";
	
	public static void put(Integer chatId, int lastMessageId, ReadState RS ) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		JSONObject state = new JSONObject().put("lastMessageId", lastMessageId).put("readState", RS);
		db.put(chatId.toString(), state);
		writeJSONtoFile(READ_STATE_DATABASE, db);
	}
	
	public static JSONObject get(Integer chatId) {
		JSONObject db = readJSONfromFile(READ_STATE_DATABASE);
		return db.optJSONObject(chatId.toString());
	}
	
	
	public static void writeJSONtoFile(String path, JSONObject JO) {
		try {
			Files.write(Paths.get(path), JO.toString().getBytes(),
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject readJSONfromFile(String path) {
		String jsonString = "";
		try {
			jsonString = Files.lines(Paths.get(path)).collect(
					Collectors.joining(""));
		} catch (NoSuchFileException e) {
			initializeGeneralInfoFile(Paths.get(path));
			try {
				jsonString = Files.lines(Paths.get(path)).collect(
						Collectors.joining(""));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject(jsonString);
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
}
