package org.taxes.server.user;

import lombok.RequiredArgsConstructor;
import org.taxes.server.config.JwtService;
import org.taxes.server.user.data.Employee;
import org.taxes.server.user.data.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final JwtService jwtService;

	@GetMapping("/companies")
	public List<User> getUserCompanies(@RequestHeader(name = "Authorization") String token) {
		return userService.getUserCompanies(
				jwtService.extractId(token.substring(7)));
	}

	@GetMapping("/employees")
	public List<Employee> getCompanyEmployees(@RequestHeader(name = "Authorization") String token) {
		return userService.getCompanyEmployees(
				jwtService.extractId(token.substring(7)));
	}

	@GetMapping("/users")
	public List<User> getEmployees(@RequestHeader(name = "Authorization") String token,
								   @RequestParam String name) {
		return userService.getUsersWithName(name,
				jwtService.extractId(token.substring(7)));
	}

	@PostMapping("/employee")
	public Employee createEmployee(@RequestHeader(name = "Authorization") String token,
								   @RequestBody AddEmployeeRequest employeeRequest) {
		return userService.createEmployee(employeeRequest,
				jwtService.extractId(token.substring(7)));
	}

	@DeleteMapping("/employee")
	public void deleteEmployee(@RequestHeader(name = "Authorization") String token,
							   @RequestBody Integer employeeId) {
		userService.deleteEmployee(employeeId,
				jwtService.extractId(token.substring(7)));
	}
}
