package org.taxes.server.user.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class EmployeeId {
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "employee")
	private User employee;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "company")
	private User company;
}
