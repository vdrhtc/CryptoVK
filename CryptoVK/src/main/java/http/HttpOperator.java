package http;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HttpOperator {

	public int REQUEST_COUNT = 0;
	
	public HttpOperator(int connectionTimeout) {
		RequestConfig reqConf = RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout)
				.setConnectTimeout(connectionTimeout).setSocketTimeout(connectionTimeout).build();
		httpget.setConfig(reqConf);
	}

	public HttpOperator() {
		
	}

	public synchronized String sendRequest(String URL) {

		REQUEST_COUNT++;
		try {
			try {
				httpget.setURI(new URI(URL));
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " is executing request " + httpget.getRequestLine());

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

			String responseBody = execute(responseHandler);
			JSONObject response = new JSONObject(responseBody);
			if (response.optJSONObject("error") != null) {
				if (response.getJSONObject("error").getInt("error_code") == 6) {
					Thread.sleep(1000);
					responseBody = sendRequest(URL);
				} else
					log.warning(responseBody);

			}
			System.out.println("----- " + Thread.currentThread().getName() + " got response: " + response.toString());
			return responseBody;

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			httpget.reset();
		}
		return null;
	}

	private String execute(ResponseHandler<String> responseHandler)
			throws IOException, InterruptedException, ClientProtocolException {
		String responseBody = "";
		try {
			responseBody = (String) httpclient.execute(httpget, responseHandler);
		} catch (UnknownHostException | ClientProtocolException | SocketTimeoutException  e) {
			log.warning(e.getMessage());
			Thread.sleep(1000 / 2);
			responseBody = execute(responseHandler);
		}
		return responseBody;
	}

	CookieStore httpCookieStore = new BasicCookieStore();
	protected CloseableHttpClient httpclient = HttpClients.createDefault();
	private HttpGet httpget = new HttpGet();

	protected static Logger log = Logger.getAnonymousLogger();

}