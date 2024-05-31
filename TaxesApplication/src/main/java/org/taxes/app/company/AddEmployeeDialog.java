package org.taxes.app.company;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import lombok.Setter;
import org.taxes.user.AddEmployeeRequest;
import org.taxes.user.User;
import org.taxes.util.Util;

import java.util.List;

public class AddEmployeeDialog {
	private Node buttonYes;
	@Setter
	private String token;

	@FXML
	private TableView<User> employeeTable;
	@FXML
	private TextField nameField;
	@FXML
	private DatePicker hireDate;

	private final ObservableList<User> employees = FXCollections.observableArrayList();

	public void setButtonYes(Node yesButton) {
		this.buttonYes = yesButton;
		yesButton.setDisable(true);
	}

	public void initialize() {
		setUserTableColumns();
		hireDate.setOnAction(e -> onChange());
		employeeTable.focusedProperty().addListener((observable, oldValue, newValue) -> onChange());
	}

	private void onChange() {
		if (employeeTable.getSelectionModel().getSelectedItem() != null &&
				hireDate.getValue() != null) {
			buttonYes.setDisable(false);
		} else {
			buttonYes.setDisable(true);
		}
	}

	private void setUserTableColumns() {
		employeeTable.setItems(employees);

		employeeTable.setRowFactory(row -> new TableRow<>() {
			@Override
			protected void updateItem(User employee, boolean empty) {
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

		TableColumn<User, String> column = new TableColumn<>("First Name");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getFirstName()));
		employeeTable.getColumns().add(column);

		column = new TableColumn<>("Last Name");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getLastName()));
		employeeTable.getColumns().add(column);

		column = new TableColumn<>("Birth Date");
		column.setCellValueFactory(value ->
				new SimpleStringProperty(value.getValue().getDob().toString()));
		employeeTable.getColumns().add(column);
	}

	@FXML
	private void loadEmployees() {
		String response = Util.getRequest("user/users?name=" + nameField.getText(),
				"Authorization", token);
		List<User> users = (List<User>) Util.map(response, new TypeReference<List<User>>() {
		});
		employees.setAll(users);

		onChange();
	}

	public AddEmployeeRequest getAddEmployeeRequest() {
		return new AddEmployeeRequest(
				employeeTable.getSelectionModel().getSelectedItem().getId(),
				hireDate.getValue());
	}
}
