package org.taxes.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.List;

public class Util {
	private static final RowConstraints HIDDEN_CONSTRAINTS = new RowConstraints();
	private static final RowConstraints MAIN_CONSTRAINTS = new RowConstraints();

	public final static ObjectMapper mapper = new ObjectMapper();
	public final static HttpClient client = HttpClient.newHttpClient();
	public final static String origin = "http://localhost:8080/api/";
	public static final Logger log = LogManager.getLogger("MainLogger");
	public final static Image logo = new Image(
			Util.class.getResourceAsStream("/org/taxes/logo.png"));

	static {
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		mapper.setDateFormat(dateFormat);

		HIDDEN_CONSTRAINTS.setMinHeight(0);
		HIDDEN_CONSTRAINTS.setPrefHeight(0);
		HIDDEN_CONSTRAINTS.setMaxHeight(0);

		MAIN_CONSTRAINTS.setMinHeight(50);
		MAIN_CONSTRAINTS.setPrefHeight(50);
		MAIN_CONSTRAINTS.setMaxHeight(Integer.MAX_VALUE);
	}

	public static Object map(String obj, TypeReference<?> typeReference) {
		try {
			return mapper.readValue(obj, typeReference);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getRequest(String path, String... headers) {
		try {
			HttpRequest.Builder builder = HttpRequest
					.newBuilder(new URI(origin + path))
					.GET();
			if (headers != null && headers.length > 0) {
				builder.headers(headers);
			}
			HttpRequest request = builder.build();

			return makeRequest(request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String bodyRequestBuilder(String method, String path, Object body, String... headers) {
		try {
			HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(origin + path))
					.header("Content-Type", "application/json")
					.method(method, HttpRequest.BodyPublishers.ofByteArray(
							mapper.writeValueAsBytes(body)));

			if (headers != null && headers.length > 0) {
				builder.headers(headers);
			}
			HttpRequest request = builder.build();

			return makeRequest(request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String makeRequest(HttpRequest request) {
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.body();
		} catch (ConnectException e) {
			showAlert(Alert.AlertType.ERROR, "Error",
					null, "Can't connect to the server");
			Alert alert = new Alert(Alert.AlertType.ERROR);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ButtonType showAlert(Alert.AlertType alertType,
									   String title, String header, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(logo);
		return alert.showAndWait().orElse(null);
	}

	public static void centerStage(Stage stage) {
		stage.sizeToScene();
		stage.setMinHeight(0);
		stage.setMaxHeight(Double.MAX_VALUE);
		stage.setMinWidth(0);
		stage.setMaxWidth(Double.MAX_VALUE);
		stage.centerOnScreen();
	}

	public static void removeEmptyRows(GridPane gridPane, Stage stage) {
		int rowCount = gridPane.getRowConstraints().size();
		List<Node> child = gridPane.getChildren()
				.stream()
				.filter(Node::isVisible)
				.toList();

		for (int row = 2; row < rowCount; row++) {
			int r = row;

			if (child.stream()
					.noneMatch(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == r)) {
				gridPane.getRowConstraints().set(row, HIDDEN_CONSTRAINTS);
			} else {
				gridPane.getRowConstraints().set(row, MAIN_CONSTRAINTS);
			}
		}

		centerStage(stage);
	}
}
