package org.taxes.server.tax;

import lombok.RequiredArgsConstructor;
import org.taxes.server.config.JwtService;
import org.taxes.server.tax.data.Tax;
import org.taxes.server.tax.data.TaxType;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequestMapping("api/tax")
@RequiredArgsConstructor
public class TaxController {
	private final TaxService taxService;
	private final JwtService jwtService;

	@GetMapping("/types")
	public List<TaxType> getTaxTypes() {
		return taxService.getAllTaxTypes();
	}

	@GetMapping("/types/company")
	public List<TaxType> getCompanyTaxTypes() {
		return taxService.getCompanyTaxTypes();
	}

	@GetMapping("/user")
	public List<Tax> getUserTaxes(@RequestHeader(name = "Authorization") String token) {
		return taxService.getUserTax(
				jwtService.extractId(token.substring(7)));
	}

	@PostMapping("/user")
	public Tax createTax(@RequestHeader(name = "Authorization") String token,
						 @RequestBody Tax tax) {
		return taxService.createTax(tax,
				jwtService.extractId(token.substring(7)));
	}

	@DeleteMapping("/user")
	public void deleteTax(@RequestHeader(name = "Authorization") String token,
						  @RequestBody Integer id)
			throws AuthenticationException {
		taxService.deleteTax(id,
				jwtService.extractId(token.substring(7)));
	}

	@GetMapping("/company")
	public List<Tax> getCompanyTaxes(@RequestHeader(name = "Authorization") String token) {
		return taxService.getCompanyTax(
				jwtService.extractId(token.substring(7)));
	}

	@PostMapping("/company")
	public Tax createTaxByCompany(@RequestHeader(name = "Authorization") String token,
						 @RequestBody Tax tax) {
		return taxService.createTaxByCompany(tax,
				jwtService.extractId(token.substring(7)));
	}

	@DeleteMapping("/company")
	public void deleteTaxByCompany(@RequestHeader(name = "Authorization") String token,
						  @RequestBody Integer id)
			throws AuthenticationException {
		taxService.deleteTaxByCompany(id,
				jwtService.extractId(token.substring(7)));
	}
}
