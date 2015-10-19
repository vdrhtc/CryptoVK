package view;

import http.ConnectionOperator;
import http.ResponseParser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import controller.ViewSwitcher;
import data.DataOperator;

public class AuthorizeView implements SwitchableView {

	public final ViewName name = ViewName.AUTHORIZE_VIEW;

	public AuthorizeView() {
		buildBrowser();

		root.getChildren().addAll(browser, adressBar);
		VBox.setVgrow(browser, Priority.ALWAYS);
	}

	public boolean tokenAvailableAndValid() {
		String token = DataOperator.getLastAccessToken();
		if (!token.equals("")) {
			ConnectionOperator.setAccessToken(token);
			if (!ConnectionOperator.getOwner().has("error"))
				return true;
		}
		return false;
	}

	private void processNewURL(String newValue) {
		//TODO Перенести все в респонс парсер

		String token = ResponseParser.parseAuthorizeResponse(newValue,
				"access_token");

		String email = ResponseParser.parseAuthorizeResponse(newValue, "email");
		String oldEmail = DataOperator.getOwnerEmail();

		if (email != null) {
			if (oldEmail == "")
				DataOperator.setOwnerEmail(email);
			else if (!oldEmail.equals(email) && !email.equals(""))
				DataOperator.setOwnerEmail(email);
		}

		String expires_in = ResponseParser
				.parseAuthorizeResponse(newValue,
						"expires_in");
		if(expires_in!=null) {
			String expirationDate = DateFormat.getInstance().format(
					new Date(
							System.currentTimeMillis()
							+ 1000*Integer.parseInt(expires_in)));
			DataOperator.setTokenExpirationDate(expirationDate);
		}
		if (token != null) {
			DataOperator.setAccesToken(token);
			proceedToNextView(token);
		}
	}

	private void proceedToNextView(String token) {
		ConnectionOperator.setAccessToken(token);
		VS.switchToView(ViewName.CHATS_PREVIEW, null);

	}

	private void buildBrowser() {
		browser.getEngine().load(authURL+DataOperator.getOwnerEmail());
		browser.getEngine()
				.locationProperty()
				.addListener(
						(ObservableValue<? extends String> observable,
								String oldValue, String newValue) -> {
							adressBar.setText(newValue);
							processNewURL(newValue);
						});
	}
	
	@Override
	public void getReadyForSwitch(Object... params) {
		return;
	}

	private ViewSwitcher VS = ViewSwitcher.getInstance();
	private VBox root = new VBox();
	private WebView browser = new WebView();;
	private TextField adressBar = new TextField();
	private String authURL = "http://api.vkontakte.ru/oauth/authorize?"
			+ "client_id=4468911&scope=messages"
			+ "&redirect_uri=blank.html&response_type=token"
			+ "&display=popup&email=";

	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.ALL);
	}

	@Override
	public ViewName getName() {
		return this.name;
	}


	@Override
	public ViewName redirectTo() {
		return tokenAvailableAndValid() ? ViewName.CHATS_PREVIEW : this.name;
	}


	@Override
	public Pane getRoot() {
		return root;
	}
}
