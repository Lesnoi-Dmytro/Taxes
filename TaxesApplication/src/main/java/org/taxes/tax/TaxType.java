package org.taxes.tax;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxType {
	private Integer id;
	private String name;
	private double taxPart;
	private boolean salaryTax;
}
