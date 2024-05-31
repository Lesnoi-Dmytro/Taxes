package org.taxes.server.tax.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.taxes.server.user.data.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "salary_tax")
public class SalaryTax {
	@Id
	private Integer id;

	@MapsId
	@OneToOne
	@JoinColumn(name = "id")
	@JsonBackReference
	private Tax tax;

	@ManyToOne
	@JoinColumn(name = "company")
	private User company;
	private double companyTax;
}
