package org.taxes.app.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.taxes.app.auth.req.AuthenticationRequest;
import org.taxes.app.auth.req.AuthenticationResponse;
import org.taxes.util.Util;

public class LogInController {
	@Setter
	private AuthenticationSwitch authenticationSwitch;
	@Setter
	private AuthenticateApplication app;

	@FXML
	private TextField email;
	@FXML
	private TextField password;

	@FXML
	public void toRegister() {
		authenticationSwitch.setCurrentView(AuthenticationSwitch.View.REGISTER);
	}

	@FXML
	public void onLogin() {
		AuthenticationRequest authenticationRequest = new AuthenticationRequest();
		authenticationRequest.setEmail(email.getText());
		authenticationRequest.setPassword(password.getText());

		String result = Util.bodyRequestBuilder("POST", "auth/authenticate", authenticationRequest);
		if (result.isEmpty()) {
			Util.showAlert(Alert.AlertType.WARNING, "Warning",
					null, "Wrong email or password");
		} else {
			AuthenticationResponse response = (AuthenticationResponse)
					Util.map(result, new TypeReference<AuthenticationResponse>() {});
			app.openUserPart(response);
		}
	}
}