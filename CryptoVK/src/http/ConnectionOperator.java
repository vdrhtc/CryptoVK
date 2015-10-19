package http;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnectionOperator {
	
	public static int REQUEST_COUNT = 0;
	

	public static JSONObject getLongPollServer() {
		String realRequest = messages_getLongPollServer
				.concat("&access_token=" + acess_token);
		
		return new JSONObject(sendRequest(realRequest))
				.getJSONObject("response");
	}

	public static JSONObject getUpdates(String server, String key, long ts) {
		String realRequest = "http://" + server + "?act=a_check&key=" + key
				+ "&ts=" + ts + "&wait=25&mode=2";
		
		return new JSONObject(sendRequest(realRequest));
	}

	public static JSONArray getDialogs(int count, int offset) {
		String realRequest = messages_getDialogsRequestTemplate
				.concat("&offset=" + offset)
				.concat("&access_token=" + acess_token)
				.concat("&count=" + count);

		return new JSONObject(sendRequest(realRequest)).getJSONObject(
				"response").getJSONArray("items");
	}

	public static JSONObject getDialog(int positionInHistory) {

		String realRequest = messages_getDialogRequestTemplate.concat(
				"&offset=" + positionInHistory).concat(
				"&access_token=" + acess_token);

		return new JSONObject(sendRequest(realRequest))
				.getJSONObject("response").getJSONArray("items")
				.getJSONObject(0).getJSONObject("message");
	}

	public static JSONObject getUser(int id) {
		String realRequest = users_getTemplate.concat("&user_ids=" + id)
				.concat("&fields=photo_50")
				.concat("&access_token=" + acess_token);
		;

		return new JSONObject(sendRequest(realRequest))
				.getJSONArray("response").getJSONObject(0);

	}

	public static JSONObject getChat(int id) {
		String realRequest = messages_getChatTemplate.concat("&chat_id=" + id)
				.concat("&fields=photo_50")
				.concat("&access_token=" + acess_token);

		return new JSONObject(sendRequest(realRequest))
				.optJSONObject("response");
	}
	
	public static JSONArray getChatHistory(int interlocutorId, int chatId, int count, int startMessageId) {
		
		String realRequest = messages_getHistoryTemplate.concat("&chat_id=" + chatId)
				.concat("&count="+count).concat("&user_id="+interlocutorId)
				.concat("&start_message_id="+startMessageId)
				.concat("&access_token=" + acess_token);
		
		return new JSONObject(sendRequest(realRequest))
				.optJSONObject("response").getJSONArray("items");
	}

	public static JSONObject getOwner() {
		String realRequest = users_getTemplate.concat("&fields=photo_50")
				.concat("&access_token=" + acess_token);

		JSONArray response = new JSONObject(sendRequest(realRequest))
				.optJSONArray("response");
		return response == null ? new JSONObject("{error:error}") : response
				.getJSONObject(0);
	}

	public static String sendRequest(String URL) {
		
		REQUEST_COUNT++;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(URL);
			System.out.println("Executing request " + httpget.getRequestLine());

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}
			};
			String responseBody = (String) httpclient.execute(httpget,
					responseHandler);

			JSONObject response = new JSONObject(responseBody);
			if (response.optJSONObject("error") != null) {
				if (response.getJSONObject("error").getInt("error_code") == 6) {
					Thread.sleep(1000 / 2);
					responseBody = sendRequest(URL);
				} else
					log.warning(responseBody);

			}
			System.out.println("-----"+response.toString());
			return responseBody;

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getAccessToken() {
		return acess_token;
	}

	public static void setAccessToken(String acess_token) {
		ConnectionOperator.acess_token = acess_token;
	}

	private static String acess_token;

	private static String messages_getDialogRequestTemplate = "https://api.vk.com/method/messages.getDialogs?count=1&v=5.23";
	private static String messages_getDialogsRequestTemplate = "https://api.vk.com/method/messages.getDialogs?&v=5.23";
	private static String users_getTemplate = "https://api.vk.com/method/users.get?&v=5.23";
	private static String messages_getLongPollServer = "https://api.vk.com/method/messages.getLongPollServer?&v=5.23";
	private static String messages_getChatTemplate = "https://api.vk.com/method/messages.getChat?&v=5.23";
	private static String messages_getHistoryTemplate = "https://api.vk.com/method/messages.getHistory?&v=5.23";

	
	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.ALL);
	}

}
