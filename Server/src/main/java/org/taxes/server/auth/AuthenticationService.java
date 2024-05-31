package org.taxes.server.auth;

import lombok.RequiredArgsConstructor;
import org.taxes.server.config.JwtService;
import org.taxes.server.user.data.User;
import org.taxes.server.user.data.UserRepository;
import org.taxes.server.user.data.UserType;
import org.taxes.server.user.data.UserTypeRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository userRepository;
	private final UserTypeRepository userTypeRepository;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	public AuthenticationResponse register(RegisterRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new AuthenticationServiceException("Email already used");
		}

		UserType userType = userTypeRepository.findByName(request.getType());
		if (userType == null) {
			throw new AuthenticationServiceException("There is no such account type");
		}
		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setDob(request.getDob());
		user.setEmail(request.getEmail());
		user.setType(userType);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		User saved = userRepository.save(user);

		String jwtToken = jwtService.generateToken(saved);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(), request.getPassword()));

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("Email is not found"));
		String jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
}
