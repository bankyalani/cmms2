package com.nibss.cmms.web.validation;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nibss.cmms.domain.Company;
import com.nibss.cmms.domain.Product;

@Component
public class CompanyValidator implements Validator {
	private static final Logger logger = Logger.getLogger(CompanyValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return Company.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name",
				"required.company.name", "Company Name is required.");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description",
				"required.company.description", "Company Description is required.");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rcNumber",
				"required.company.description", "RC Number is required.");
			
		
				
	}

}
