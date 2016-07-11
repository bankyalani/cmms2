package com.nibss.cmms.web.validation;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nibss.cmms.domain.Product;

@Component
public class ProductValidator implements Validator {
	private static final Logger logger = Logger.getLogger(ProductValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return Product.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		logger.info("Doing Product Validation...");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name",
				"required.product.name", "Product Name is required.");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description",
				"required.product.description", "Product description is required.");
			
				
	}

}
