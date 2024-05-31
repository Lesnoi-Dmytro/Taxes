package org.taxes.app.user;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.taxes.tax.SalaryTax;
import org.taxes.tax.Tax;
import org.taxes.tax.TaxType;
import org.taxes.user.User;
import org.taxes.util.Util;

import java.util.List;

public class AddDialogController {
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
	private ChoiceBox<User> company;
	@FXML
	private Label companyLabel;

	private final User emptyCompany = new User(null, "No company", null, null, null);

	public void setStage(Stage stage) {
		this.stage = stage;
		Util.removeEmptyRows(gridPane, stage);
	}

	public void setChoiceBoxes(List<TaxType> taxType,
							   List<User> company) {
		this.taxType.getItems().setAll(taxType);
		this.company.getItems().setAll(company);

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
		this.company.setConverter(new StringConverter<>() {
			@Override
			public String toString(User user) {
				return user == null ? "" : user.getName();
			}

			@Override
			public User fromString(String s) {
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
			company.getSelectionModel().select(tax.getTax().getCompany());
		}
	}

	public void initialize() {
		amount.setValueFactory(
				new SpinnerValueFactory.DoubleSpinnerValueFactory(
						1000.0, 10000000.0, 1000.0, 100.0));


		taxType.setOnAction(e -> {
			onChange();
			if (taxType.getSelectionModel().getSelectedItem().getName().contains("Private Entrepreneur")) {
				if (!company.getItems().contains(emptyCompany)) {
					company.getItems().add(emptyCompany);
				}
			} else {
				company.getItems().remove(emptyCompany);
			}
			if (taxType.getValue().isSalaryTax()) {
				companyLabel.setVisible(true);
				company.setVisible(true);
			} else {
				companyLabel.setVisible(false);
				company.setVisible(false);
			}
			Util.removeEmptyRows(gridPane, stage);
		});
		date.setOnAction(e -> onChange());
		company.setOnAction(e -> onChange());
	}

	public void onChange() {
		if (taxType.getValue() != null && date.getValue() != null &&
				date.getValue() != null && (!taxType.getValue().isSalaryTax() ||
				taxType.getValue().isSalaryTax() && company.getValue() != null)) {
			buttonYes.setDisable(false);
		}
	}

	public Tax getTax() {
		if (tax == null) {
			tax = new Tax();
		}
		tax.setType(taxType.getValue());
		tax.setDate(date.getValue());
		tax.setAmount(amount.getValue());
		if (tax.getType().isSalaryTax()) {
			SalaryTax salaryTax = new SalaryTax(null,
					company.getValue().equals(emptyCompany) ? null : company.getValue(), 0);
			tax.setTax(salaryTax);
		}
		tax.calculateTaxAmount();
		return tax;
	}
}
