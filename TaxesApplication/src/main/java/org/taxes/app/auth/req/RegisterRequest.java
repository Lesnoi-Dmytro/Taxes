package org.taxes.app.auth.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	private String firstName;
	private String lastName;
	private LocalDate dob;
	private String email;
	private String type;
	private String password;
}
