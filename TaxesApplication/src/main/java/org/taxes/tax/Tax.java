package org.taxes.tax;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.taxes.user.User;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tax {
	public static final int MINIMUM_WAGE = 8000;
	public static final double USC_COEFFICIENT = 0.22;

	private Integer id;
	private TaxType type;
	private LocalDate date;
	private double amount;
	private double taxAmount;
	private SalaryTax tax;
	private User user;

	public void calculateTaxAmount() {
		if (tax == null) {
			taxAmount = type.getTaxPart() * amount;
		} else {
			if (type.getName().equals("Private Entrepreneur(1)") ||
					type.getName().equals("Private Entrepreneur(2)")) {
				taxAmount = MINIMUM_WAGE * type.getTaxPart();
			} else {
				taxAmount = type.getTaxPart() * amount;
			}

			if (type.getName().equals("Primary Salary") ||
					type.getName().equals("Secondary Salary")) {
				double usc = amount * USC_COEFFICIENT;
				usc = Math.max(usc, USC_COEFFICIENT * MINIMUM_WAGE);
				usc = Math.min(usc, USC_COEFFICIENT * MINIMUM_WAGE * 15);
				tax.setCompanyTax(usc);
			} else {
				taxAmount += MINIMUM_WAGE * USC_COEFFICIENT;
			}
		}
	}
}
