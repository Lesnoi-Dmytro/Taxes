package org.taxes.server.auth;

import org.taxes.server.config.JwtService;
import org.taxes.server.user.data.UserRepository;
import org.taxes.server.user.data.UserTypeRepository;
import org.taxes.server.user.data.User;
import org.taxes.server.user.data.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthenticationServiceTest {
	@Autowired
	private AuthenticationService authenticationService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UserTypeRepository userTypeRepository;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@Test
	public void contentLoads() {
		assertNotNull(authenticationService);
	}

	@Test
	public void testRegister() {
		RegisterRequest request = new RegisterRequest("", "",
				LocalDate.now(), "", "", "");
		UserType userType = new UserType((byte) 0, "", null);
		User user = new User();

		when(userRepository.save(any())).thenReturn(user);
		when(userTypeRepository.findByName(request.getType())).thenReturn(userType);
		when(jwtService.generateToken(user)).thenReturn("1");

		assertEquals(new AuthenticationResponse("1"), authenticationService.register(request));

		verify(userRepository).findByEmail(request.getEmail());
		verify(userTypeRepository).findByName(request.getType());
		verify(passwordEncoder).encode(request.getPassword());
		verify(jwtService).generateToken(user);
	}

	@Test
	public void testRegisterException() {
		RegisterRequest request = new RegisterRequest();

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));
		assertThrows(AuthenticationServiceException.class, () -> authenticationService.register(request));

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
		when(userTypeRepository.findByName(request.getType())).thenReturn(null);
		assertThrows(AuthenticationServiceException.class, () -> authenticationService.register(request));
	}

	@Test
	public void testAuthenticate() {
		AuthenticationRequest request = new AuthenticationRequest("1", "1");
		User user = new User();

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
		when(jwtService.generateToken(user)).thenReturn("1");
		assertEquals(new AuthenticationResponse("1"), authenticationService.authenticate(request));
	}

	@Test
	public void testAuthenticateException() {
		AuthenticationRequest request = new AuthenticationRequest("1", "1");

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(request));
	}
}