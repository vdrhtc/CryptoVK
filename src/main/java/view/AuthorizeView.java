package view;

import java.net.CookieHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.DataOperator;
import http.ConnectionOperator;
import http.ResponseParser;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class AuthorizeView implements View {

	public final ViewName name = ViewName.AUTHORIZE_VIEW;


	public boolean tokenAvailableAndValid() {
		String token = DataOperator.getLastAccessToken();
		if (!token.equals("")) {
			ConnectionOperator.setAccessToken(token);
			if (!CO.getOwner().has("error"))
				return true;
		}
		return false;
	}
	
	public void prepareForSwitch() {
		CookieHandler.setDefault(new com.sun.webkit.network.CookieManager());
		buildBrowser();

		root.getChildren().addAll(browser, addressBar);
		VBox.setVgrow(browser, Priority.ALWAYS);		
	}

	public boolean getTokenFromURL(String newValue) {
		// TODO Перенести все в респонс парсер

		String token = ResponseParser.parseAuthorizeResponse(newValue, "access_token");

		String email = ResponseParser.parseAuthorizeResponse(newValue, "email");
		String oldEmail = DataOperator.getOwnerEmail();

		if (email != null) {
			if (oldEmail == "")
				DataOperator.setOwnerEmail(email);
			else if (!oldEmail.equals(email) && !email.equals(""))
				DataOperator.setOwnerEmail(email);
		}

		if (token != null) {
			DataOperator.setAccesToken(token);
			ConnectionOperator.setAccessToken(token);
			return true;
		}
		return false;
	}

	private void buildBrowser() {
		browser.getEngine().load(authURL + DataOperator.getOwnerEmail());

	}

	public ReadOnlyStringProperty getBrowserLocationProperty() {
		return browser.getEngine().locationProperty();
	}

	public TextField getAddressBar() {
		return addressBar;
	}

	private VBox root = new VBox();
	private WebView browser = new WebView();;
	private TextField addressBar = new TextField();
	private String authURL = "http://api.vkontakte.ru/oauth/authorize?"
			+ "client_id=4468911&scope=messages,photos,docs,offline" + "&redirect_uri=blank.html&response_type=token"
			+ "&display=popup&email=";

	private ConnectionOperator CO = new ConnectionOperator(1000);
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AuthorizeView.class);

	@Override
	public ViewName getName() {
		return this.name;
	}

	@Override
	public Pane getRoot() {
		return root;
	}

}
