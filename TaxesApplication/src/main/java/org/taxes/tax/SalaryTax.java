package org.taxes.tax;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.taxes.user.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryTax {
	private Integer id;
	private User company;
	private double companyTax;
}
