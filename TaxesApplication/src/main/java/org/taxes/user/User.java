package org.taxes.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate dob;
	private String email;

	@JsonIgnore
	public String getName() {
		String name = firstName;
		if (lastName != null && !lastName.isEmpty()) {
			name += " " + lastName;
		}
		return name;
	}
}
