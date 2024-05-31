package org.taxes.server.tax;

import org.taxes.server.tax.data.*;
import org.taxes.server.user.data.User;
import org.taxes.server.user.data.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TaxServiceTest {
	@Autowired
	private TaxService taxService;

	@MockBean
	private TaxRepository taxRepository;

	@MockBean
	private SalaryTaxRepository salaryTaxRepository;

	@MockBean
	private TaxTypeRepository taxTypeRepository;

	@MockBean
	private UserRepository userRepository;

	@Test
	public void contentLoads() {
		assertNotNull(taxService);
	}

	@Test
	public void testGetAllTaxTypes() {
		List<TaxType> taxTypes = List.of(new TaxType(), new TaxType());

		when(taxTypeRepository.findAll()).thenReturn(taxTypes);
		assertEquals(taxTypes, taxService.getAllTaxTypes());

		verify(taxTypeRepository).findAll();
	}

	@Test
	public void testGetCompanyTaxTypes() {
		List<TaxType> taxTypes = List.of(new TaxType(), new TaxType());

		when(taxTypeRepository.findAllSalaryTaxes()).thenReturn(taxTypes);
		assertEquals(taxTypes, taxService.getCompanyTaxTypes());

		verify(taxTypeRepository).findAllSalaryTaxes();
	}

	@Test
	public void testGetUserTax() {
		Integer id = 1;
		List<Tax> taxes = List.of(new Tax(), new Tax());

		when(taxRepository.findByUserId(id)).thenReturn(taxes);
		assertEquals(taxes, taxService.getUserTax(id));

		verify(taxRepository).findByUserId(id);
	}

	@Test
	public void testCreateTax() {
		Integer id = 0;
		Integer companyId = -1;
		User user = new User();
		User company = new User();
		company.setId(companyId);

		Tax tax = new Tax();
		SalaryTax salaryTax = new SalaryTax();
		salaryTax.setCompany(company);
		tax.setTax(salaryTax);

		when(userRepository.findById(id)).thenReturn(Optional.of(user));
		when(taxRepository.save(tax)).thenReturn(tax);
		when(userRepository.findById(companyId)).thenReturn(Optional.of(company));
		assertEquals(tax, taxService.createTax(tax, id));

		when(userRepository.findById(companyId)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> taxService.createTax(tax, id));

		when(userRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> taxService.createTax(tax, id));

		verify(userRepository, times(3)).findById(id);
		verify(userRepository, times(2)).findById(companyId);
		verify(taxRepository, times(1)).save(tax);
	}

	@Test
	public void testDeleteTax() throws AuthenticationException {
		Integer id = 0;
		Integer userId = 0;
		User user = new User();
		Tax tax = new Tax();
		tax.setUser(user);
		user.setId(userId);

		when(taxRepository.findById(id)).thenReturn(Optional.of(tax));
		taxService.deleteTax(id, userId);

		user.setId(-1);
		assertThrows(AuthenticationException.class, () ->
				taxService.deleteTax(id, userId));

		when(taxRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () ->
				taxService.deleteTax(id, userId));

		verify(taxRepository, times(3)).findById(id);
		verify(taxRepository, times(1)).delete(tax);
	}

	@Test
	public void testGetCompanyTax() throws AuthenticationException {
		Integer companyId = 0;
		List<SalaryTax> salaryTaxes = List.of(
				new SalaryTax(), new SalaryTax(), new SalaryTax());
		salaryTaxes.forEach(t -> t.setTax(new Tax()));

		when(salaryTaxRepository.findByCompanyId(companyId)).thenReturn(salaryTaxes);
		assertEquals(List.of(new Tax(), new Tax(), new Tax()),
				taxService.getCompanyTax(companyId));

		verify(salaryTaxRepository).findByCompanyId(companyId);
	}

	@Test
	public void testCreateTaxByCompany() {
		Integer id = 0;
		Tax tax = new Tax();
		SalaryTax salaryTax = new SalaryTax();
		tax.setTax(salaryTax);
		User company = new User();

		when(userRepository.findById(id)).thenReturn(Optional.of(company));
		when(taxRepository.save(tax)).thenReturn(tax);
		assertEquals(tax, taxService.createTaxByCompany(tax, id));

		when(userRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () ->
				taxService.createTaxByCompany(tax, id));

		verify(userRepository, times(2)).findById(id);
		verify(taxRepository, times(1)).save(tax);
	}

	@Test
	public void testDeleteTaxByCompany() throws AuthenticationException {
		Integer id = 0;
		Integer companyId = 0;
		Tax tax = new Tax();
		SalaryTax salaryTax = new SalaryTax();
		tax.setTax(salaryTax);

		User company = new User();
		company.setId(companyId);
		salaryTax.setCompany(company);

		when(taxRepository.findById(id)).thenReturn(Optional.of(tax));
		taxService.deleteTaxByCompany(id, companyId);

		company.setId(-1);
		assertThrows(AuthenticationException.class, () ->
				taxService.deleteTaxByCompany(id, companyId));

		when(taxRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () ->
				taxService.deleteTaxByCompany(id, companyId));

		verify(taxRepository, times(3)).findById(id);
		verify(taxRepository, times(1)).delete(tax);
	}
}