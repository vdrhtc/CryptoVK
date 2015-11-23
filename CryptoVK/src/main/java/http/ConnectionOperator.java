package http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConnectionOperator extends HttpOperator {

	public ConnectionOperator(int connectionTimeout) {
		super(connectionTimeout);
	}

	public ConnectionOperator() {
		super();
	}

	public JSONObject getMesageById(int messageId) {
		String realRequest = messages_getByIdTemplate.concat("&message_ids=" + messageId)
				.concat("&access_token=" + acess_token);
		return new JSONObject(sendRequest(realRequest)).getJSONObject("response").getJSONArray("items")
				.getJSONObject(0);
	}

	public int readChat(Long chatId, Long lastMessageId) {
		String realRequest = messages_readTemplate.concat("&start_message_id=" + lastMessageId)
				.concat("&message_ids=" + lastMessageId).concat("&access_token=" + acess_token);
		return new JSONObject(sendRequest(realRequest)).getInt("response");
	}

	public int sendMessage(Long chatId, Long userId, String message, String... attachments)
			throws UnsupportedEncodingException {

		String realRequest = messages_sendTemplate.concat("&chat_id=" + chatId).concat("&user_id=" + userId)
				.concat("&access_token=" + acess_token);

		if (message != "")
			realRequest = realRequest.concat("&message=" + URLEncoder.encode(message, "UTF-8"));

		if (attachments.length > 0) {
			realRequest = realRequest.concat("&attachment=");
			for (String attachment : attachments)
				realRequest += attachment + ",";
		}
		return new JSONObject(sendRequest(realRequest)).optInt("response");
	}

	public JSONObject getLongPollServer() {
		String realRequest = messages_getLongPollServer.concat("&access_token=" + acess_token);

		return new JSONObject(sendRequest(realRequest)).getJSONObject("response");
	}

	public JSONObject getUpdates(String server, String key, long ts) {
		String realRequest = "http://" + server + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25&mode=2";

		return new JSONObject(sendRequest(realRequest));
	}

	public JSONArray getDialogs(int count, int offset) {
		String realRequest = messages_getDialogsRequestTemplate.concat("&offset=" + offset)
				.concat("&access_token=" + acess_token).concat("&count=" + count);

		return new JSONObject(sendRequest(realRequest)).getJSONObject("response").getJSONArray("items");
	}

	public JSONObject getDialog(int positionInHistory) {

		String realRequest = messages_getDialogRequestTemplate.concat("&offset=" + positionInHistory)
				.concat("&access_token=" + acess_token);

		return new JSONObject(sendRequest(realRequest)).getJSONObject("response").getJSONArray("items").getJSONObject(0)
				.getJSONObject("message");
	}

	public JSONObject getUser(int id) {
		String realRequest = users_getTemplate.concat("&user_ids=" + id).concat("&fields=photo_50,last_seen")
				.concat("&access_token=" + acess_token);
		;

		return new JSONObject(sendRequest(realRequest)).getJSONArray("response").getJSONObject(0);

	}

	public JSONObject getChat(int id) {
		String realRequest = messages_getChatTemplate.concat("&chat_id=" + id).concat("&fields=photo_50,last_seen")
				.concat("&access_token=" + acess_token);

		return new JSONObject(sendRequest(realRequest)).optJSONObject("response");
	}

	public JSONObject getChatHistory(int interlocutorId, Long chatId, int count, int offset) {

		String realRequest = messages_getHistoryTemplate.concat("&chat_id=" + chatId).concat("&count=" + count)
				.concat("&user_id=" + interlocutorId).concat("&offset=" + offset)
				.concat("&access_token=" + acess_token);
		JSONObject test = new JSONObject(sendRequest(realRequest)).optJSONObject("response");
		while (test.getInt("count") == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			test = new JSONObject(sendRequest(realRequest)).optJSONObject("response");
		}
		return test;
	}

	public JSONObject getOwner() {
		String realRequest = users_getTemplate.concat("&fields=photo_50,last_seen")
				.concat("&access_token=" + acess_token);

		JSONArray response = new JSONObject(sendRequest(realRequest)).optJSONArray("response");
		return response == null ? new JSONObject("{error:error}") : response.getJSONObject(0);
	}

	public static void setAccessToken(String acess_token) {
		ConnectionOperator.acess_token = acess_token;
	}

	public static String getAccessToken() {
		return acess_token;
	}

	private static String acess_token;
	private static String messages_getDialogRequestTemplate = "https://api.vk.com/method/messages.getDialogs?count=1&v=5.23";
	private static String messages_getDialogsRequestTemplate = "https://api.vk.com/method/messages.getDialogs?&v=5.23";
	private static String users_getTemplate = "https://api.vk.com/method/users.get?&v=5.23";
	private static String messages_getLongPollServer = "https://api.vk.com/method/messages.getLongPollServer?&v=5.23";
	private static String messages_getChatTemplate = "https://api.vk.com/method/messages.getChat?&v=5.23";
	private static String messages_getHistoryTemplate = "https://api.vk.com/method/messages.getHistory?&v=5.23";
	private static String messages_sendTemplate = "https://api.vk.com/method/messages.send?&v=5.23";
	private static String messages_readTemplate = "https://api.vk.com/method/messages.markAsRead?&v=5.23";
	private static String messages_getByIdTemplate = "https://api.vk.com/method/messages.getById?&v=5.23";

	static {
		log.setLevel(Level.ALL);
	}

}
