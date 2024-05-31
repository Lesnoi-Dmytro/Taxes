package org.taxes.app.auth;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.taxes.app.auth.req.AuthenticationResponse;
import org.taxes.app.company.CompanyApplication;
import org.taxes.app.user.UserApplication;
import org.taxes.util.Util;

import java.io.*;

public class AuthenticateApplication extends Application {
	private Stage stage;

	@Override
	public void start(Stage stage) throws IOException {
		try {
			Util.log.info("Authorization Window Opened");
			this.stage = stage;
			if (openUserPart()) {
				return;
			}

			AuthenticationSwitch authenticationSwitch = new AuthenticationSwitch();

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("log_in.fxml"));
			Parent logIn = fxmlLoader.load();
			LogInController logInController = fxmlLoader.getController();
			logInController.setAuthenticationSwitch(authenticationSwitch);
			logInController.setApp(this);

			fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
			Parent register = fxmlLoader.load();
			RegisterController registerController = fxmlLoader.getController();
			registerController.setAuthenticationSwitch(authenticationSwitch);
			registerController.setStage(stage);
			registerController.setApp(this);

			Scene scene = new Scene(logIn);
			scene.rootProperty().bind(Bindings.createObjectBinding(() -> {
				if (authenticationSwitch.getCurrentView() == AuthenticationSwitch.View.LOG_IN) {
					Util.log.info("Log In Window");
					stage.setTitle("Log In");
					return logIn;
				} else if (authenticationSwitch.getCurrentView() == AuthenticationSwitch.View.REGISTER) {
					Util.log.info("Register Window");
					stage.setTitle("Register");
					return register;
				} else {
					return null;
				}
			}, authenticationSwitch.currentViewProperty()));
			stage.setOnHidden(e -> {
			});
			scene.rootProperty().addListener((observable, oldValue, newValue) -> {
				Util.centerStage(stage);
			});
			stage.sceneProperty().addListener((observable, oldValue, newValue) -> {
				Util.centerStage(stage);
			});

			stage.setTitle("Log In");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.getIcons().add(Util.logo);
			stage.show();
		} catch (Exception e) {
			Util.log.error("Error", e);
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		launch();
	}

	public void openUserPart(AuthenticationResponse response) {
		File file = new File("token.jwt");
		if (file.exists()) {
			file.delete();
		}
		try (FileWriter writer = new FileWriter(file)) {
			file.createNewFile();
			writer.write("Bearer " + response.getToken());
			Util.log.info("Token written");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		openUserPart();
	}

	public boolean openUserPart() {
		File file = new File("token.jwt");
		if (!file.exists()) {
			return false;
		}

		String token;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			token = reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String ping = Util.getRequest("auth/ping", "Authorization", token);
		if (ping == null) {
			Util.log.info("Token not valid");
			return false;
		}
		Util.log.info("User logged into account");
		String type = Util.getRequest("auth/type", "Authorization", token);
		try {
			if (type.equals("USER")) {
				UserApplication userApp = new UserApplication();
				userApp.setToken(token);
				userApp.start(stage);
				return true;
			} else if (type.equals("COMPANY")) {
				CompanyApplication companyApp = new CompanyApplication();
				companyApp.setToken(token);
				companyApp.start(stage);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}