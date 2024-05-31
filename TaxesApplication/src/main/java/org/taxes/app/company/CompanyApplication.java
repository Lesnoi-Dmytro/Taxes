package org.taxes.app.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import org.taxes.util.Util;

import java.io.IOException;

@Setter
public class CompanyApplication extends Application {
	private String token;

	@Override
	public void start(Stage stage) throws IOException {
		Util.log.info("Company Application Started");

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("app.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		CompanyController controller = fxmlLoader.getController();
		controller.setToken(token);
		controller.setStage(stage);

		stage.setScene(scene);
		stage.setTitle("Taxes");
		stage.getIcons().add(Util.logo);
		stage.show();
	}
}
