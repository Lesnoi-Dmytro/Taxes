package org.taxes.server.user.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.taxes.server.tax.data.SalaryTax;
import org.taxes.server.tax.data.Tax;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate dob;
	private String email;
	@JsonIgnore
	private String password;

	@ManyToOne
	@JoinColumn(name = "type")
	@JsonIgnore
	private UserType type;

	@OneToMany(mappedBy = "company")
	@JsonIgnore
	private List<SalaryTax> salaryTaxes;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<Tax> taxes;

	@OneToMany(mappedBy = "id.employee", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Employee> companies;

	@OneToMany(mappedBy = "id.company", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Employee> employees;

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(type.getName()));
	}

	@Override
	@JsonIgnore
	public String getUsername() {
		return email;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}
}
