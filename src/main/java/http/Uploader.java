package http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import model.Attachment;
import model.Document;
import model.Photo;

public class Uploader extends HttpOperator {

	public Uploader() {
	}

	public Uploader(int connectionTimeout) {
		super(connectionTimeout);
	}

	public synchronized Attachment upload(File file) {

		String fileExtension = "";
		int i = file.getAbsolutePath().lastIndexOf('.');
		if (i > 0) {
			fileExtension = file.getAbsolutePath().substring(i + 1);
		}

		if (Arrays.asList("png", "jpg", "bmp", "gif").contains(fileExtension.toLowerCase()))
			return uploadPhoto(file);
		else
			return uploadDocument(file);
	}

	private Attachment uploadDocument(File document) {
		String uploadServerData = sendRequest(
				documents_getUploadServerRequest.concat("&access_token=" + ConnectionOperator.getAccessToken()));
		JSONObject uploadServerDataJSON = new JSONObject(uploadServerData).getJSONObject("response");

		try {
			httppost.setURI(new URI(uploadServerDataJSON.getString("upload_url")));
		} catch (JSONException | URISyntaxException e) {
			e.printStackTrace();
		}

		HttpEntity mpEntity = MultipartEntityBuilder.create().addBinaryBody("file", document).build();
		JSONObject response = executePost(mpEntity);
		String loadedDocumentInfo = "";
		try {
			loadedDocumentInfo = sendRequest(
					documents_saveRequest.concat("&file=" + URLEncoder.encode(response.getString("file"), "UTF-8"))
							.concat("&access_token=" + ConnectionOperator.getAccessToken()));
		} catch (JSONException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new Document(new JSONObject(loadedDocumentInfo).getJSONArray("response").getJSONObject(0));
	}
	
	public synchronized Attachment uploadPhoto(File image) {
		String uploadServerData = sendRequest(
				photos_getMessagesUploadServerRequest.concat("&access_token=" + ConnectionOperator.getAccessToken()));
		JSONObject uploadServerDataJSON = new JSONObject(uploadServerData).getJSONObject("response");

		try {
			httppost.setURI(new URI(uploadServerDataJSON.getString("upload_url")));
		} catch (JSONException | URISyntaxException e) {
			e.printStackTrace();
		}

		HttpEntity mpEntity = MultipartEntityBuilder.create().addBinaryBody("photo", image).build();
		JSONObject response = executePost(mpEntity);
		
		String loadedPhotoInfo = "";
		try {
			loadedPhotoInfo = sendRequest(photos_saveMessagesPhotoRequest.concat("&server=" + response.getInt("server"))
					.concat("&photo=" + URLEncoder.encode(response.getString("photo"), "UTF-8"))
					.concat("&hash=" + response.getString("hash"))
					.concat("&access_token=" + ConnectionOperator.getAccessToken()));
		} catch (JSONException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new Photo(new JSONObject(loadedPhotoInfo).getJSONArray("response").getJSONObject(0));
	}

	private HttpPost httppost = new HttpPost();

	private static String documents_getUploadServerRequest = "https://api.vk.com/method/docs.getUploadServer?v=5.23";
	private static String documents_saveRequest = "https://api.vk.com/method/docs.save?v=5.23";
	private static String photos_getMessagesUploadServerRequest = "https://api.vk.com/method/photos.getMessagesUploadServer?v=5.23";
	private static String photos_saveMessagesPhotoRequest = "https://api.vk.com/method/photos.saveMessagesPhoto?v=5.23";
	
	private JSONObject executePost(HttpEntity mpEntity) {
		httppost.setEntity(mpEntity);

		System.out.println(Thread.currentThread().getName() + " is executing request " + httppost.getRequestLine());

		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					String enitityResponse = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					return enitityResponse;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			}
		};
		String responseBody = "";
		try {
			responseBody = httpclient.execute(httppost, responseHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject response = new JSONObject(responseBody);
		return response;
	}
}
