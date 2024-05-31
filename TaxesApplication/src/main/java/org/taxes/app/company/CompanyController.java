package org.taxes.app.company;

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
import org.taxes.app.auth.AuthenticateApplication;
import org.taxes.tax.Tax;
import org.taxes.tax.TaxType;
import org.taxes.user.AddEmployeeRequest;
import org.taxes.user.Employee;
import org.taxes.user.EmployeeId;
import org.taxes.user.User;
import org.taxes.util.Util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CompanyController {
	private String token;
	@Setter
	private Stage stage;

	@FXML
	private TextField totalTaxAmount;
	@FXML
	private TableView<Tax> taxTable;
	@FXML
	private DatePicker fromDate;
	@FXML
	private DatePicker toDate;
	@FXML
	private ChoiceBox<TaxType> taxType;
	@FXML
	private ChoiceBox<Employee> employee;
	@FXML
	private TableView<Employee> employeeTable;
	@FXML
	private DatePicker hiredAfter;
	@FXML
	private DatePicker hiredBefore;
	@FXML
	private TextField firstName;
	@FXML
	private TextField lastName;


	private final TaxType anyType = new TaxType(null, "Any", 0, false);
	private final Employee anyEmployee = new Employee(
			new EmployeeId(new User(null, "Any", null, null, null),
					new User(null, null, null, null, null)), LocalDate.now());

	private final List<Tax> taxes = new ArrayList<>();
	private final ObservableList<Tax> filteredTaxes = FXCollections.observableArrayList();
	private final ObservableList<Employee> employees = FXCollections.observableArrayList();
	private final ObservableList<Employee> filteredEmployees = FXCollections.observableArrayList();

	public void initialize() {
		setTaxTableColumns();
		setEmployeeTableColumns();

		VBox.setVgrow(taxTable, Priority.SOMETIMES);
		filteredTaxes.addListener((ListChangeListener<Tax>) change -> {
			double amount = filteredTaxes.stream()
					.mapToDouble(t -> t.getTax().getCompanyTax())
					.sum();
			totalTaxAmount.setText(amount + "₴");
		});
	}

	private void loadChoiceBoxes() {
		loadEmployees();

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

		taxType.getItems().add(anyType);
		taxType.getSelectionModel().select(0);

		Util.log.info("Tax Types Loading");
		String response = Util.getRequest("tax/types/company");
		List<TaxType> types = (List<TaxType>) Util.map(response, new TypeReference<List<TaxType>>() {
		});
		taxType.getItems().addAll(types);
	}

	private void loadEmployees() {
		employee.setConverter(new StringConverter<>() {
			@Override
			public String toString(Employee employee) {
				return employee == null ? "" :
						employee.equals(anyEmployee) ? anyEmployee.getId().getEmployee().getName() :
								employee.getId().getEmployee().getName() + "(" + employee.getHireDate() + ")";
			}

			@Override
			public Employee fromString(String s) {
				return null;
			}
		});

		Util.log.info("Company Employees Loading");
		employee.getItems().add(anyEmployee);
		employee.getSelectionModel().select(0);

		String response = Util.getRequest("user/employees", "Authorization", token);
		employees.setAll((List<Employee>) Util.map(response, new TypeReference<List<Employee>>() {
		}));
		employees.addListener((ListChangeListener<Employee>) change -> {
			while (change.next()) {
				employee.getItems().removeAll(change.getRemoved());
				employee.getItems().addAll(change.getAddedSubList());
			}
		});

		employee.getItems().addAll(employees);
		filerEmployees();
	}

	private void loadTaxes() {
		String response = Util.getRequest("tax/company", "Authorization", token);
		List<Tax> taxes = (List<Tax>) Util.map(response, new TypeReference<List<Tax>>() {
		});
		Util.log.info("Loaded {} Company Taxes", taxes.size());

		this.taxes.addAll(taxes);
		filterTaxes();
	}

	private void setTaxTableColumns() {
		taxTable.setItems(filteredTaxes);

		taxTable.setRowFactory(row -> new TableRow<Tax>() {
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

		TableColumn<Tax, String> column = new TableColumn<>("User");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getUser().getName()));
		taxTable.getColumns().add(column);

		column = new TableColumn<>("Type");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getType().getName()));
		taxTable.getColumns().add(column);

		column = new TableColumn<>("Date");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getDate().toString()));
		taxTable.getColumns().add(column);

		column = new TableColumn<>("Amount");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getAmount() + "₴"));
		taxTable.getColumns().add(column);

		column = new TableColumn<>("Tax Amount");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getTaxAmount() + "₴"));
		taxTable.getColumns().add(column);

		column = new TableColumn<>("Company Tax");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getTax().getCompanyTax() + "₴"));
	}

	private void setEmployeeTableColumns() {
		employeeTable.setItems(filteredEmployees);

		employeeTable.setRowFactory(row -> new TableRow<>() {
			@Override
			protected void updateItem(Employee employee, boolean empty) {
				super.updateItem(employee, empty);

				if (employee == null || empty) {
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

		TableColumn<Employee, String> column = new TableColumn<>("First Name");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getId().getEmployee().getFirstName()));
		employeeTable.getColumns().add(column);

		column = new TableColumn<>("Last Name");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getId().getEmployee().getLastName()));
		employeeTable.getColumns().add(column);

		column = new TableColumn<>("Birth Date");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getId().getEmployee().getDob().toString()));
		employeeTable.getColumns().add(column);

		column = new TableColumn<>("Hire Date");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getHireDate().toString()));
		employeeTable.getColumns().add(column);
	}

	public void setToken(String token) {
		this.token = token;
		loadChoiceBoxes();
		loadTaxes();
	}

	@FXML
	public void addTax() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("add_tax.fxml"));
		Dialog<ButtonType> dialog = createTaxDialog("Add Tax", loader);
		AddTaxController controller = loader.getController();

		Util.log.info("Open Add Tax Dialog");
		if (dialog.showAndWait().orElse(null) == ButtonType.YES) {
			Tax tax = controller.getTax();
			String response = Util.bodyRequestBuilder("POST", "tax/company",
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
		if (taxTable.getSelectionModel().getSelectedItem() == null) {
			Util.showAlert(Alert.AlertType.WARNING, "No tax selected",
					null, "Select a tax to edit");
			return;
		}

		Tax tax = taxTable.getSelectionModel().getSelectedItem();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("add_tax.fxml"));
		Dialog<ButtonType> dialog = createTaxDialog("Edit Tax", loader);
		AddTaxController controller = loader.getController();
		controller.setTax(tax);

		Util.log.info("Open Edit Tax {} Dialog", tax.getId());
		if (dialog.showAndWait().orElse(null) == ButtonType.YES) {
			tax = controller.getTax();
			Util.bodyRequestBuilder("POST", "tax/company",
					tax, "Authorization", token);
			Util.log.info("Tax {} Edited", tax.getId());
			filterTaxes();
		}
	}

	private Dialog<ButtonType> createTaxDialog(String title, FXMLLoader loader)
			throws IOException {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(title);

		DialogPane dialogPane = loader.load();
		AddTaxController controller = loader.getController();
		controller.setChoiceBoxes(taxType.getItems().subList(1, taxType.getItems().size()),
				employee.getItems().subList(1, employee.getItems().size()));

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
		Tax tax = taxTable.getSelectionModel().getSelectedItem();
		if (tax == null) {
			Util.showAlert(Alert.AlertType.WARNING, "No tax selected",
					null, "Select a tax to delete");
			return;
		}

		Util.log.info("Open Delete Tax {} Alert", tax.getId());
		if (Util.showAlert(Alert.AlertType.CONFIRMATION, "Do you want to delete this tax?",
				null, tax.getType().getName() + ": " + tax.getDate()) == ButtonType.OK) {
			Util.bodyRequestBuilder("DELETE", "tax/company",
					tax.getId(), "Authorization", token);
			Util.log.info("Tax {} Deleted", tax.getId());
			taxes.remove(tax);
			filterTaxes();
		}
	}

	public void filterTaxes() {
		Util.log.info("Taxes Filtered");
		Stream<Tax> stream = taxes.stream();

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
		if (employee.getValue() != anyEmployee) {
			stream = stream.filter(t -> t.getUser().equals(
					employee.getValue().getId().getEmployee()));
		}

		filteredTaxes.setAll(stream.toList());
	}

	@FXML
	public void addEmployee() throws IOException {
		Dialog<ButtonType> dialog = new Dialog<>();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("add_employee.fxml"));
		DialogPane dialogPane = loader.load();
		AddEmployeeDialog controller = loader.getController();
		dialog.setDialogPane(dialogPane);

		dialogPane.getButtonTypes().forEach(b ->
				dialogPane.lookupButton(b).setStyle("-fx-background-radius: 20"));
		Node yesButton = dialogPane.lookupButton(ButtonType.YES);
		controller.setButtonYes(yesButton);
		controller.setToken(token);

		dialog.setDialogPane(dialogPane);
		Stage stage = (Stage) dialogPane.getScene().getWindow();
		stage.getIcons().add(Util.logo);
		dialog.setTitle("Add Employee");

		if (dialog.showAndWait().orElse(null) == ButtonType.YES) {
			AddEmployeeRequest request = controller.getAddEmployeeRequest();
			String response = Util.bodyRequestBuilder("POST", "user/employee",
					request, "Authorization", token);
			Util.log.info("Add {} Employee to Company", request.getEmployeeId());
			Employee employee = (Employee) Util.map(response, new TypeReference<Employee>() {
			});
			employees.add(employee);
			filerEmployees();
		}
	}

	@FXML
	public void deleteEmployee() {
		Employee employee = employeeTable.getSelectionModel().getSelectedItem();
		if (employee == null) {
			Util.showAlert(Alert.AlertType.WARNING, "No tax selected",
					null, "Select a employee to delete");
			return;
		}

		Util.log.info("Open Delete Employee {} Alert", employee.getId().getEmployee().getId());
		if (Util.showAlert(Alert.AlertType.CONFIRMATION, "Do you want to delete this employee?",
				null, employee.getId().getEmployee().getName() +
						"(" + employee.getHireDate() + ")") == ButtonType.OK) {
			Util.bodyRequestBuilder("DELETE", "user/employee",
					employee.getId().getEmployee().getId(), "Authorization", token);
			Util.log.info("Employee {} Deleted", employee.getId().getEmployee().getId());
			employees.remove(employee);
			filerEmployees();
		}
	}

	@FXML
	private void filerEmployees() {
		Stream<Employee> stream = employees.stream();

		if (hiredAfter.getValue() != null) {
			stream = stream.filter(e -> e.getHireDate().equals(hiredAfter.getValue()) ||
					e.getHireDate().isAfter(hiredAfter.getValue()));
		}
		if (hiredBefore.getValue() != null) {
			stream = stream.filter(e -> e.getHireDate().equals(hiredBefore.getValue()) ||
					e.getHireDate().isBefore(hiredBefore.getValue()));
		}
		if (!firstName.getText().isEmpty()) {
			stream = stream.filter(e -> e.getId().getEmployee().getFirstName()
					.contains(firstName.getText()));
		}
		if (!lastName.getText().isEmpty()) {
			stream = stream.filter(e -> e.getId().getEmployee().getFirstName()
					.contains(lastName.getText()));
		}

		filteredEmployees.setAll(stream.toList());
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
}
