package org.taxes.server.tax.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.taxes.server.user.data.User;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tax")
public class Tax {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "\"type\"")
	private TaxType type;
	private LocalDate date;
	private double amount;
	private double taxAmount;

	@OneToOne(mappedBy = "tax", cascade = CascadeType.ALL)
	private SalaryTax tax;

	@ManyToOne
	@JoinColumn(name = "\"user\"")
	private User user;
}
