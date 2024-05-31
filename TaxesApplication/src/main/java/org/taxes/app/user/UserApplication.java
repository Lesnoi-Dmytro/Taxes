package org.taxes.app.user;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import org.taxes.util.Util;

@Setter
public class UserApplication extends Application {
	private String token;

	@Override
	public void start(Stage stage) throws Exception {
		Util.log.info("User Application Started");

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("app.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		UserController controller = fxmlLoader.getController();
		controller.setToken(token);
		controller.setStage(stage);

		stage.setScene(scene);
		stage.setTitle("Taxes");
		stage.getIcons().add(Util.logo);
		stage.show();
	}
}
