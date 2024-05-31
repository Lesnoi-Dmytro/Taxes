package org.taxes.server.auth;

import lombok.RequiredArgsConstructor;
import org.taxes.server.config.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	private final JwtService jwtService;
	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(
			@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authenticationService.register(request));
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticateRequest(
			@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}

	@GetMapping("/type")
	public String getType(@RequestHeader(name = "Authorization") String token) {
		token = token.substring(7);
		System.out.println(jwtService.extractClaim(token, (c) -> c.get("type")).toString());
		return (String) jwtService.extractClaim(token, (c) -> c.get("type"));
	}

	@GetMapping("/ping")
	public Byte ping(@RequestHeader(name = "Authorization") String token) {
		return 1;
	}
}
