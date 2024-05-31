package org.taxes.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.taxes.server.config.JwtService;
import org.taxes.server.user.data.Employee;
import org.taxes.server.user.data.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
	@Autowired
	private UserController userController;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@MockBean
	private JwtService jwtService;

	private final String token = "0";
	private final Integer id = 0;

	@BeforeEach
	public void setUp() {
		when(jwtService.extractId(token)).thenReturn(id);
	}

	@Test
	public void contentLoads() {
		assertNotNull(userController);
	}

	@Test
	@WithMockUser
	public void testGetUserCompanies() throws Exception {
		List<User> users = List.of(new User(), new User(), new User());
		when(userService.getUserCompanies(id)).thenReturn(users);

		mvc.perform(get("/api/user/companies")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(users)));

		verify(userService).getUserCompanies(id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testGetCompanyEmployees() throws Exception {
		List<Employee> employees = List.of(new Employee(), new Employee(), new Employee());
		when(userService.getCompanyEmployees(id)).thenReturn(employees);

		mvc.perform(get("/api/user/employees")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(employees)));

		verify(userService).getCompanyEmployees(id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testGetEmployees() throws Exception {
		List<User> users = List.of(new User(), new User(), new User());
		String name = "1";
		when(userService.getUsersWithName(name, id)).thenReturn(users);

		mvc.perform(get("/api/user/users?name=1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(users)));

		verify(userService).getUsersWithName(name, id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testCreateEmployee() throws Exception {
		Employee employee = new Employee();
		AddEmployeeRequest addEmployeeRequest = new AddEmployeeRequest();
		when(userService.createEmployee(addEmployeeRequest, id)).thenReturn(employee);

		mvc.perform(post("/api/user/employee")
						.header("Authorization", "Bearer " + token)
						.header("Content-Type", "application/json")
						.content(objectMapper.writeValueAsString(addEmployeeRequest)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(employee)));

		verify(userService).createEmployee(addEmployeeRequest, id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testDeleteEmployee() throws Exception {
		Integer employeeId = 0;

		mvc.perform(delete("/api/user/employee")
						.header("Authorization", "Bearer " + token)
						.header("Content-Type", "application/json")
						.content(objectMapper.writeValueAsString(employeeId)))
				.andExpect(status().isOk());

		verify(userService).deleteEmployee(employeeId, id);
		verify(jwtService).extractId(token);
	}
}