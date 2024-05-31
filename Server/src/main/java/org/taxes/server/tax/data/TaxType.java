package org.taxes.server.tax.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tax_type")
public class TaxType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Byte id;
	private String name;
	private double taxPart;
	private boolean salaryTax;

	@OneToMany(mappedBy = "type")
	@JsonIgnore
	private List<Tax> taxes;
}
