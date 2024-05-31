package org.taxes.app.user;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Setter;
import org.controlsfx.control.ToggleSwitch;
import org.taxes.app.auth.AuthenticateApplication;
import org.taxes.tax.Tax;
import org.taxes.tax.TaxType;
import org.taxes.user.User;
import org.taxes.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UserController {
	private String token;
	@Setter
	private Stage stage;

	@FXML
	private TextField totalTaxAmount;
	@FXML
	private TableView<Tax> tableView;
	@FXML
	private VBox filters;
	@FXML
	private DatePicker fromDate;
	@FXML
	private DatePicker toDate;
	@FXML
	private ChoiceBox<TaxType> taxType;
	@FXML
	private ChoiceBox<User> company;

	private final ToggleSwitch toggleSwitch = new ToggleSwitch();
	private final TableColumn<Tax, String> companyColumn = new TableColumn<>("Company");
	private final TaxType anyType = new TaxType(null, "Any", 0, false);
	private final User anyCompany = new User(null, "Any", null, null, null);

	private final List<Tax> taxes = new ArrayList<>();
	private final ObservableList<Tax> filteredTaxes = FXCollections.observableArrayList();

	public void initialize() {
		setToggleSwitch();
		setTableColumns();

		VBox.setVgrow(tableView, Priority.SOMETIMES);
		filteredTaxes.addListener((ListChangeListener<Tax>) change -> {
			double amount = filteredTaxes.stream()
					.mapToDouble(Tax::getTaxAmount)
					.sum();
			totalTaxAmount.setText(amount + "₴");
		});
	}

	private void loadChoiceBoxes() {
		taxType.setConverter(new StringConverter<>() {
			@Override
			public String toString(TaxType type) {
				return type == null ? "" : type.getName();
			}

			@Override
			public TaxType fromString(String s) {
				return null;
			}
		});
		company.setConverter(new StringConverter<>() {
			@Override
			public String toString(User user) {
				return user == null ? "" : user.getName();
			}

			@Override
			public User fromString(String s) {
				return null;
			}
		});

		taxType.getItems().add(anyType);
		taxType.getSelectionModel().select(0);

		Util.log.info("Tax Types Loading");
		String response = Util.getRequest("tax/types");
		List<TaxType> types = (List<TaxType>) Util.map(response, new TypeReference<List<TaxType>>() {
		});
		taxType.getItems().addAll(types);

		Util.log.info("User Companies Loading");
		company.getItems().add(anyCompany);
		company.getSelectionModel().select(0);

		response = Util.getRequest("user/companies", "Authorization", token);
		List<User> companies = (List<User>) Util.map(response, new TypeReference<List<User>>() {
		});
		company.getItems().addAll(companies);
	}

	public void setToken(String token) {
		this.token = token;
		loadChoiceBoxes();
		loadTaxes();
	}

	@FXML
	public void addTax() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("add.fxml"));
		Dialog<ButtonType> dialog = createDialog("Add Tax", loader);
		AddDialogController controller = loader.getController();

		Util.log.info("Open Add Tax Dialog");
		if (dialog.showAndWait().orElse(null) == ButtonType.YES) {
			Tax tax = controller.getTax();
			String response = Util.bodyRequestBuilder("POST", "tax/user",
					tax, "Authorization", token);
			tax = (Tax) Util.map(response, new TypeReference<Tax>() {
			});
			Util.log.info("Tax {} Added", tax.getId());
			taxes.add(tax);
			filterTaxes();
		}
	}

	@FXML
	public void editTax() throws IOException {
		if (tableView.getSelectionModel().getSelectedItem() == null) {
			Util.showAlert(Alert.AlertType.WARNING, "No tax selected",
					null, "Select a tax to edit");
			return;
		}

		Tax tax = tableView.getSelectionModel().getSelectedItem();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("add.fxml"));
		Dialog<ButtonType> dialog = createDialog("Edit Tax", loader);
		AddDialogController controller = loader.getController();
		controller.setTax(tax);

		Util.log.info("Open Edit Tax {} Dialog", tax.getId());
		if (dialog.showAndWait().orElse(null) == ButtonType.YES) {
			tax = controller.getTax();
			Util.bodyRequestBuilder("POST", "tax/user",
					tax, "Authorization", token);
			Util.log.info("Tax {} Edited", tax.getId());
			filterTaxes();
		}
	}

	private Dialog<ButtonType> createDialog(String title, FXMLLoader loader) throws IOException {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(title);

		DialogPane dialogPane = loader.load();
		AddDialogController controller = loader.getController();
		controller.setChoiceBoxes(taxType.getItems().subList(1, taxType.getItems().size()),
				company.getItems().subList(1, company.getItems().size()));

		dialogPane.getButtonTypes().forEach(b ->
				dialogPane.lookupButton(b).setStyle("-fx-background-radius: 20"));
		Node yesButton = dialogPane.lookupButton(ButtonType.YES);
		controller.setButtonYes(yesButton);

		dialog.setDialogPane(dialogPane);
		Stage stage = (Stage) dialogPane.getScene().getWindow();
		controller.setStage(stage);
		stage.getIcons().add(Util.logo);

		return dialog;
	}

	@FXML
	public void deleteTax() {
		Tax tax = tableView.getSelectionModel().getSelectedItem();
		if (tax == null) {
			Util.showAlert(Alert.AlertType.WARNING, "No tax selected",
					null, "Select a tax to delete");
			return;
		}

		Util.log.info("Open Delete Tax {} Alert", tax.getId());
		if (Util.showAlert(Alert.AlertType.CONFIRMATION, "Do you want to delete this tax?",
				null, tax.getType().getName() + ": " + tax.getDate()) == ButtonType.OK) {
			Util.bodyRequestBuilder("DELETE", "tax/user",
					tax.getId(), "Authorization", token);
			Util.log.info("Tax {} Deleted", tax.getId());
			taxes.remove(tax);
			filterTaxes();
		}
	}

	@FXML
	public void logOut() {
		File file = new File("token.jwt");
		file.delete();
		stage.close();
		AuthenticateApplication app = new AuthenticateApplication();
		Util.log.info("User Logged out");
		try {
			app.start(stage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void loadTaxes() {
		String response = Util.getRequest("tax/user", "Authorization", token);
		List<Tax> taxes = (List<Tax>) Util.map(response, new TypeReference<List<Tax>>() {
		});
		Util.log.info("Loaded {} User Taxes", taxes.size());

		this.taxes.addAll(taxes);
		filterTaxes();
	}

	@FXML
	public void filterTaxes() {
		Util.log.info("Taxes Filtered");
		Stream<Tax> stream = taxes.stream();

		if (toggleSwitch.isSelected()) {
			stream = stream.filter(t -> t.getTax() != null);
		} else {
			stream = stream.filter(t -> t.getTax() == null);
		}
		if (fromDate.getValue() != null) {
			stream = stream.filter(t -> t.getDate().isAfter(fromDate.getValue()) ||
					t.getDate().equals(fromDate.getValue()));
		}
		if (toDate.getValue() != null) {
			stream = stream.filter(t -> t.getDate().isBefore(toDate.getValue()) ||
					t.getDate().equals(toDate.getValue()));
		}
		if (taxType.getValue() != anyType) {
			stream = stream.filter(t -> t.getType().equals(taxType.getValue()));
		}
		if (company.getValue() != anyCompany) {
			stream = stream.filter(t -> t.getTax().getCompany().equals(company.getValue()));
		}

		filteredTaxes.setAll(stream.toList());
	}

	private void setTableColumns() {
		tableView.getColumns().clear();
		tableView.setItems(filteredTaxes);

		tableView.setRowFactory(row -> new TableRow<Tax>() {
			@Override
			protected void updateItem(Tax tax, boolean empty) {
				super.updateItem(tax, empty);

				if (tax == null || empty) {
					setStyle("-fx-background-color: #FFF9D0;");
				} else {
					String style = "-fx-font-size: 14px; ";
					if (getIndex() % 2 == 0) {
						style += "-fx-background-color: #A0DEFF;";
					} else {
						style += "-fx-background-color: #CAF4FF;";
					}
					setStyle(style);
				}
			}
		});

		TableColumn<Tax, String> column = new TableColumn<>("Type");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getType().getName()));
		tableView.getColumns().add(column);

		column = new TableColumn<>("Date");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getDate().toString()));
		tableView.getColumns().add(column);

		column = new TableColumn<>("Amount");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getAmount() + "₴"));
		tableView.getColumns().add(column);

		column = new TableColumn<>("Tax Amount");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getTaxAmount() + "₴"));
		tableView.getColumns().add(column);

		companyColumn.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getTax().getCompany() == null ?
						"" : value.getValue().getTax().getCompany().getName()));
	}

	private void setToggleSwitch() {
		toggleSwitch.setText("Salary Taxes");
		toggleSwitch.setStyle("-fx-font-size: 14px");
		toggleSwitch.setSelected(false);
		filters.getChildren().add(0, toggleSwitch);

		toggleSwitch.setOnMouseClicked((e) -> {
			filterTaxes();
			if (toggleSwitch.isSelected()) {
				tableView.getColumns().add(companyColumn);
			} else {
				tableView.getColumns().remove(companyColumn);
			}
		});
	}
}
