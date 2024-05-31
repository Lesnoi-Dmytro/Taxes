package org.taxes.server.user;

import org.taxes.server.user.data.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {
	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private EmployeeRepository employeeRepository;

	@MockBean
	private UserTypeRepository userTypeRepository;

	private final int id = 0;

	@Test
	public void contentLoads() {
		assertNotNull(userService);
	}

	@Test
	public void testGetUserCompanies() {
		List<User> companies = List.of(new User(), new User(), new User());
		List<Employee> employees = List.of(
				new Employee(new EmployeeId(null, companies.get(0)), null),
				new Employee(new EmployeeId(null, companies.get(1)), null),
				new Employee(new EmployeeId(null, companies.get(2)), null)
		);
		User user = new User(null, null, null,
				null, null, null, null,
				null, null, employees, null);

		when(userRepository.findById(id)).thenReturn(Optional.of(user));
		assertEquals(companies, userService.getUserCompanies(id));

		when(userRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.getUserCompanies(id));

		verify(userRepository, times(2)).findById(id);
	}

	@Test
	public void testGetCompanyEmployees() {
		List<Employee> employees = List.of(new Employee(), new Employee(), new Employee());
		User user = new User(null, null, null,
				null, null, null, null,
				null, null, null, employees);

		when(userRepository.findById(id)).thenReturn(Optional.of(user));
		assertEquals(employees, userService.getCompanyEmployees(id));

		when(userRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.getCompanyEmployees(id));

		verify(userRepository, times(2)).findById(id);
	}

	@Test
	public void testGetUsersWithName() {
		String name = "";
		UserType userType = new UserType();
		userType.setId((byte) 0);
		User company = new User();
		company.setId(id);
		company.setCompanies(List.of());
		company.setType(new UserType((byte) 1, null, null));

		List<User> employees = new ArrayList<>(List.of(
				new User(),
				new User(),
				new User()
		));
		employees.forEach(e -> {
			e.setType(userType);
			e.setCompanies(List.of());
		});
		employees.get(2).setCompanies(List.of(
				new Employee(new EmployeeId(null, company), null)));
		employees.add(company);

		when(userRepository.findByName(name)).thenReturn(employees);
		when(userTypeRepository.findByName("USER")).thenReturn(userType);
		assertEquals(employees.subList(0, 2),
				userService.getUsersWithName(name, id));

		verify(userRepository).findByName(name);
		verify(userTypeRepository).findByName("USER");
	}

	@Test
	public void testCreateEmployee() {
		AddEmployeeRequest request = new AddEmployeeRequest(-1, null);
		Employee employee = new Employee();

		when(userRepository.findById(request.getEmployeeId())).thenReturn(Optional.of(new User()));
		when(userRepository.findById(id)).thenReturn(Optional.of(new User()));
		when(employeeRepository.save(any())).thenReturn(employee);
		assertEquals(employee, userService.createEmployee(request, id));

		when(userRepository.findById(request.getEmployeeId())).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.createEmployee(request, id));

		when(userRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.createEmployee(request, id));

		verify(userRepository, times(3)).findById(id);
		verify(userRepository, times(2)).findById(request.getEmployeeId());
		verify(employeeRepository, times(1)).save(any());
	}

	@Test
	public void testDeleteEmployee() {
		Employee employee = new Employee();

		when(employeeRepository.findCompanyEmployee(id, id)).thenReturn(Optional.of(employee));
		userService.deleteEmployee(id, id);

		when(employeeRepository.findCompanyEmployee(id, id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.deleteEmployee(id, id));

		verify(employeeRepository, times(2)).findCompanyEmployee(id, id);
		verify(employeeRepository, times(1)).delete(any());
	}
}