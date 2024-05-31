package org.taxes.server.tax;

import lombok.RequiredArgsConstructor;
import org.taxes.server.tax.data.*;
import org.taxes.server.user.data.UserRepository;
import org.taxes.server.user.data.User;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxService {
	private final TaxRepository taxRepository;
	private final SalaryTaxRepository salaryTaxRepository;
	private final TaxTypeRepository taxTypeRepository;
	private final UserRepository userRepository;

	public List<TaxType> getAllTaxTypes() {
		return taxTypeRepository.findAll();
	}

	public List<TaxType> getCompanyTaxTypes() {
		return taxTypeRepository.findAllSalaryTaxes();
	}

	public List<Tax> getUserTax(Integer id) {
		return taxRepository.findByUserId(id);
	}

	public Tax createTax(Tax tax, Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(() ->
				new IllegalArgumentException("User not found"));
		tax.setUser(user);
		if (tax.getTax() != null) {
			tax.getTax().setTax(tax);
			if (tax.getTax().getCompany().getId() != null) {
				User company = userRepository.findById(tax.getTax().getCompany().getId()).orElseThrow(() ->
						new IllegalArgumentException("Company not found"));
				tax.getTax().setCompany(company);
			}
		}
		return taxRepository.save(tax);
	}

	public void deleteTax(Integer id, Integer userId)
			throws AuthenticationException {
		Tax tax = taxRepository.findById(id).orElseThrow(() ->
				new IllegalArgumentException("Tax not found"));
		if (!tax.getUser().getId().equals(userId)) {
			throw new AuthenticationException("User " + userId + " don't have tax " + id);
		}

		taxRepository.delete(tax);
	}

	public List<Tax> getCompanyTax(Integer id) {
		return salaryTaxRepository.findByCompanyId(id).stream()
				.map(SalaryTax::getTax)
				.toList();
	}

	public Tax createTaxByCompany(Tax tax, Integer companyId) {
		User company = userRepository.findById(companyId).orElseThrow(() ->
				new IllegalArgumentException("Company not found"));
		tax.getTax().setCompany(company);
		tax.getTax().setTax(tax);
		return taxRepository.save(tax);
	}

	public void deleteTaxByCompany(Integer id, Integer companyId)
			throws AuthenticationException {
		Tax tax = taxRepository.findById(id).orElseThrow(() ->
				new IllegalArgumentException("Tax not found"));
		if (!tax.getTax().getCompany().getId().equals(companyId)) {
			throw new AuthenticationException("Company " + companyId + " don't have tax " + id);
		}

		taxRepository.delete(tax);
	}
}
