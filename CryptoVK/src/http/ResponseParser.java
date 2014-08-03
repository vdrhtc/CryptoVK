package http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class ResponseParser {

	/**
	 * Method which works with server responses while authorizing via internal browser
	 * 
	 * @param response
	 * @return acess_token or null if the token was not provided in the response
	 */
	public static String parseAuthorizeResponse(String response, String key) {
		if(response.contains("#"))
			response = response.replace("#", "?");
		List<NameValuePair> params;
		try {
			params = URLEncodedUtils.parse(new URI(response), "UTF-8");
			for (NameValuePair param : params) {
				if (param.getName().equals(key))
					return param.getValue();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
