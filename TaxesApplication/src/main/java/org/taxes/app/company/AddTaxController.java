package org.taxes.app.company;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.taxes.tax.SalaryTax;
import org.taxes.tax.Tax;
import org.taxes.tax.TaxType;
import org.taxes.user.Employee;
import org.taxes.util.Util;

import java.util.List;

public class AddTaxController {
	private Stage stage;
	private Node buttonYes;
	private Tax tax;

	@FXML
	private GridPane gridPane;
	@FXML
	private ChoiceBox<TaxType> taxType;
	@FXML
	private DatePicker date;
	@FXML
	private Spinner<Double> amount;
	@FXML
	private ChoiceBox<Employee> employee;

	public void setStage(Stage stage) {
		this.stage = stage;
		Util.removeEmptyRows(gridPane, stage);
	}

	public void setChoiceBoxes(List<TaxType> taxType,
							   List<Employee> employee) {
		this.taxType.getItems().setAll(taxType);
		this.employee.getItems().setAll(employee);

		this.taxType.setConverter(new StringConverter<>() {
			@Override
			public String toString(TaxType type) {
				return type == null ? "" : type.getName();
			}

			@Override
			public TaxType fromString(String s) {
				return null;
			}
		});
		this.employee.setConverter(new StringConverter<>() {
			@Override
			public String toString(Employee employee) {
				return employee == null ? "" : employee.getId().getEmployee().getName() +
						"(" + employee.getHireDate() + ")";
			}

			@Override
			public Employee fromString(String s) {
				return null;
			}
		});
	}

	public void setButtonYes(Node buttonYes) {
		this.buttonYes = buttonYes;
		buttonYes.setDisable(true);
	}

	public void setTax(Tax tax) {
		this.tax = tax;
		buttonYes.setDisable(false);

		taxType.getSelectionModel().select(tax.getType());
		date.setValue(tax.getDate());
		amount.getValueFactory().setValue(tax.getAmount());
		if (tax.getTax() != null) {
			int index = 0;
			for (int i = 0;  i < employee.getItems().size(); i++) {
				if (employee.getItems().get(i).getId().getEmployee()
						.equals(tax.getUser())) {
					index = i;
					break;
				}
			}
			employee.getSelectionModel().select(index);
		}
	}

	public void initialize() {
		amount.setValueFactory(
				new SpinnerValueFactory.DoubleSpinnerValueFactory(
						1000.0, 10000000.0, 1000.0, 100.0));


		taxType.setOnAction(e -> onChange());
		date.setOnAction(e -> onChange());
		employee.setOnAction(e -> onChange());
	}

	public void onChange() {
		if (taxType.getValue() != null && date.getValue() != null &&
				date.getValue() != null && (!taxType.getValue().isSalaryTax() ||
				taxType.getValue().isSalaryTax() && employee.getValue() != null)) {
			buttonYes.setDisable(false);
		}
	}

	public Tax getTax() {
		if (tax == null) {
			tax = new Tax();
		}
		tax.setUser(employee.getValue().getId().getEmployee());
		tax.setType(taxType.getValue());
		tax.setDate(date.getValue());
		tax.setAmount(amount.getValue());
		if (tax.getType().isSalaryTax()) {
			SalaryTax salaryTax = new SalaryTax(null, null, 0);
			tax.setTax(salaryTax);
		}
		tax.calculateTaxAmount();
		return tax;
	}
}
