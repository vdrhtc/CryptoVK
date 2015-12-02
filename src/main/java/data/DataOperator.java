package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class DataOperator {

	private static final String EMPTY = "";
	private static final String TOKEN = "token";
	private static final String OWNER_EMAIL = "owner_email";
	private static final String ACCESS_TOKEN = "access_token";

	static {
		File appData = new File(System.getProperty("user.home") + "/.concrypt");
		appData.mkdir();
	}

	private static final String GENERAL_DATAFILE = System.getProperty("user.home") + "/.concrypt/generalDataFile.json";

	public static String formatDate(Date date) {
		DateFormat df = DateFormat.getInstance();
		df.setTimeZone(TimeZone.getDefault());
		return df.format(date);
	}

	public static String getLastAccessToken() {
		JSONObject JO = readJSONfromFile(GENERAL_DATAFILE);
		return JO.optJSONObject(ACCESS_TOKEN).getString(TOKEN);
	}

	public static void setAccesToken(String value) {
		JSONObject JO = readJSONfromFile(GENERAL_DATAFILE);
		JO.getJSONObject(ACCESS_TOKEN).put(TOKEN, value);
		writeJSONtoFile(GENERAL_DATAFILE, JO);
	}

	public static String getOwnerEmail() {
		JSONObject JO = readJSONfromFile(GENERAL_DATAFILE);
		return JO.optString(OWNER_EMAIL);
	}

	public static void setOwnerEmail(String email) {
		JSONObject JO = readJSONfromFile(GENERAL_DATAFILE);
		JO.put(OWNER_EMAIL, email);
		writeJSONtoFile(GENERAL_DATAFILE, JO);
	}

	public static void writeJSONtoFile(String path, JSONObject JO) {
		try {
			Files.write(Paths.get(path), JO.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject readJSONfromFile(String path) {
		String jsonString = "";
		try {
			jsonString = Files.lines(Paths.get(path)).collect(Collectors.joining(""));
		} catch (NoSuchFileException e) {
			initializeGeneralInfoFile(Paths.get(path));
			try {
				jsonString = Files.lines(Paths.get(path)).collect(Collectors.joining(""));
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
			JO.put(OWNER_EMAIL, EMPTY);
			JO.put(ACCESS_TOKEN, new JSONObject().put(TOKEN, EMPTY));
			Files.write(path, JO.toString().getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
