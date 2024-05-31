package org.taxes.server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.taxes.server.config.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {
	@Autowired
	private AuthenticationController authenticationController;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private JwtService jwtService;

	@Test
	public void contentLoads() {
		assertNotNull(authenticationController);
	}

	@Test
	@WithMockUser
	public void testRegister() throws Exception {
		RegisterRequest request = new RegisterRequest("", "",
				LocalDate.now(), "", "", "");
		AuthenticationResponse response = new AuthenticationResponse("1");
		when(authenticationService.register(request)).thenReturn(response);

		String requestJson = objectMapper.writeValueAsString(request);

		mvc.perform(post("/api/auth/register")
						.contentType("application/json")
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));

		verify(authenticationService).register(request);
	}

	@Test
	@WithMockUser
	public void testAuth() throws Exception {
		AuthenticationRequest request = new AuthenticationRequest("", "");
		AuthenticationResponse response = new AuthenticationResponse("1");
		when(authenticationService.authenticate(request)).thenReturn(response);

		String requestJson = objectMapper.writeValueAsString(request);

		mvc.perform(post("/api/auth/authenticate")
						.contentType("application/json")
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));

		verify(authenticationService).authenticate(request);
	}

	@Test
	@WithMockUser
	public void testPing() throws Exception {
		String token = null;
		mvc.perform(get("/api/auth/ping")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));
	}

	@Test
	public void testPingWrong() throws Exception {
		mvc.perform(get("/api/auth/ping")
						.header("Authorization", "Bearer wrong"))
				.andExpect(status().is(403));
	}

	@Test
	@WithMockUser
	public void testType() throws Exception {
		String token = null;
		when(jwtService.extractClaim(any(), any()))
				.thenReturn("1");

		mvc.perform(get("/api/auth/type")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));
	}
}