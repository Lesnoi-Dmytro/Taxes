package org.taxes.server.tax;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.taxes.server.config.JwtService;
import org.taxes.server.tax.data.Tax;
import org.taxes.server.tax.data.TaxType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TaxControllerTest {
	@Autowired
	private TaxController taxController;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private TaxService taxService;

	@MockBean
	private JwtService jwtService;

	private final String token = "0";
	private final Integer id = 0;

	@BeforeEach
	public void setUp() {
		when(jwtService.extractId(token)).thenReturn(id);
	}

	@Test
	public void contextLoads() {
		assertNotNull(taxController);
	}

	@Test
	@WithMockUser
	public void testGetTaxTypes() throws Exception {
		List<TaxType> taxTypes = List.of(new TaxType(), new TaxType());

		when(taxService.getAllTaxTypes()).thenReturn(taxTypes);
		mvc.perform(get("/api/tax/types"))
				.andExpect(status().isOk())
				.andExpect(content().json(
						objectMapper.writeValueAsString(taxTypes)));

		verify(taxService).getAllTaxTypes();
	}

	@Test
	@WithMockUser
	public void testGetCompanyTaxTypes() throws Exception {
		List<TaxType> taxTypes = List.of(new TaxType(), new TaxType());

		when(taxService.getCompanyTaxTypes()).thenReturn(taxTypes);
		mvc.perform(get("/api/tax/types/company"))
				.andExpect(status().isOk())
				.andExpect(content().json(
						objectMapper.writeValueAsString(taxTypes)));

		verify(taxService).getCompanyTaxTypes();
	}

	@Test
	@WithMockUser
	public void testGetUserTaxes() throws Exception {
		List<Tax> taxes = List.of(new Tax(), new Tax());

		when(taxService.getUserTax(id)).thenReturn(taxes);
		mvc.perform(get("/api/tax/user")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().json(
						objectMapper.writeValueAsString(taxes)));

		verify(taxService).getUserTax(id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testCreateTax() throws Exception {
		Tax tax = new Tax();

		when(taxService.createTax(tax, id)).thenReturn(tax);
		mvc.perform(post("/api/tax/user")
						.header("Authorization", "Bearer " + token)
						.header("Content-type", "application/json")
						.content(objectMapper.writeValueAsString(tax)))
				.andExpect(status().isOk())
				.andExpect(content().json(
						objectMapper.writeValueAsString(tax)));

		verify(taxService).createTax(tax, id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testCeleteTax() throws Exception {
		Tax tax = new Tax();

		when(taxService.createTax(tax, id)).thenReturn(tax);
		mvc.perform(delete("/api/tax/user")
						.header("Authorization", "Bearer " + token)
						.header("Content-type", "application/json")
						.content(objectMapper.writeValueAsString(id)))
				.andExpect(status().isOk());

		verify(taxService).deleteTax(id, id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testGetCompanyTaxes() throws Exception {
		List<Tax> taxes = List.of(new Tax(), new Tax());

		when(taxService.getCompanyTax(id)).thenReturn(taxes);
		mvc.perform(get("/api/tax/company")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().json(
						objectMapper.writeValueAsString(taxes)));

		verify(taxService).getCompanyTax(id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testCreateTaxByCompany() throws Exception {
		Tax tax = new Tax();

		when(taxService.createTaxByCompany(tax, id)).thenReturn(tax);
		mvc.perform(post("/api/tax/company")
						.header("Authorization", "Bearer " + token)
						.header("Content-type", "application/json")
						.content(objectMapper.writeValueAsString(tax)))
				.andExpect(status().isOk())
				.andExpect(content().json(
						objectMapper.writeValueAsString(tax)));

		verify(taxService).createTaxByCompany(tax, id);
		verify(jwtService).extractId(token);
	}

	@Test
	@WithMockUser
	public void testDeleteTaxByCompany() throws Exception {
		mvc.perform(delete("/api/tax/company")
						.header("Authorization", "Bearer " + token)
						.header("Content-type", "application/json")
						.content(objectMapper.writeValueAsString(id)))
				.andExpect(status().isOk());

		verify(taxService).deleteTaxByCompany(id, id);
		verify(jwtService).extractId(token);
	}
}