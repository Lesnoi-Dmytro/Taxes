package org.taxes.app.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import org.taxes.app.auth.req.AuthenticationResponse;
import org.taxes.app.auth.req.RegisterRequest;
import org.taxes.util.Util;

public class RegisterController {
	@Setter
	private AuthenticationSwitch authenticationSwitch;
	@Setter
	private AuthenticateApplication app;
	@Setter
	private Stage stage;

	@FXML
	private TextField email;
	@FXML
	private TextField firstName;
	@FXML
	private TextField lastName;
	@FXML
	private DatePicker birthDate;
	@FXML
	private TextField password;
	@FXML
	private TextField confirmPassword;
	@FXML
	private ChoiceBox<String> accountType;
	@FXML
	private VBox vBox;

	public void initialize() {
		accountType.getItems().setAll("User", "Company");
		accountType.getSelectionModel().select(0);

		accountType.setOnAction((e) -> {
			if (accountType.getSelectionModel().getSelectedItem().equals("User")) {
				firstName.setPromptText("First Name");
				birthDate.setPromptText("Birth Date");
				lastName.setVisible(true);
				vBox.getChildren().add(2, lastName);
			} else {
				firstName.setPromptText("Name");
				birthDate.setPromptText("Foundation date");
				lastName.setVisible(false);
				vBox.getChildren().remove(lastName);
			}
			Util.centerStage(stage);
		});
	}

	@FXML
	public void toLogIn() {
		authenticationSwitch.setCurrentView(AuthenticationSwitch.View.LOG_IN);
	}

	@FXML
	public void onRegister() {
		if (invalidData()) {
			return;
		}
		String type = accountType.getValue();

		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setFirstName(firstName.getText());
		registerRequest.setLastName(lastName.getText());
		registerRequest.setDob(birthDate.getValue());
		registerRequest.setEmail(email.getText());
		registerRequest.setType(type.toUpperCase());
		registerRequest.setPassword(password.getText());

		String result = Util.bodyRequestBuilder("POST", "auth/register", registerRequest);
		AuthenticationResponse response = (AuthenticationResponse)
				Util.map(result, new TypeReference<AuthenticationResponse>() {
				});
		if (response != null) {
			Util.log.info("{} Registered", type);
			Util.showAlert(Alert.AlertType.INFORMATION, type + " Registered",
					null, "You have successfully registered your account");
			app.openUserPart(response);
		} else {
			Util.showAlert(Alert.AlertType.WARNING, "Warning",
					null, "Can't register " + type);
		}
	}

	private boolean invalidData() {
		if (!password.getText().equals(confirmPassword.getText())) {
			Util.showAlert(Alert.AlertType.WARNING, "Warning",
					null, "Passwords do not match");
			return true;
		}

		if (password.getText().length() < 8) {
			Util.showAlert(Alert.AlertType.WARNING, "Warning",
					null, "Password must be at least 8 characters");
			return true;
		}

		if (!email.getText().matches("\\w*@\\w*\\.\\w*")) {
			Util.showAlert(Alert.AlertType.WARNING, "Warning",
					null, "Invalid email address");
			return true;
		}

		return false;
	}
}
