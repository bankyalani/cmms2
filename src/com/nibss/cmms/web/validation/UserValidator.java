package com.nibss.cmms.web.validation;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nibss.cmms.domain.User;

@Component
public class UserValidator implements Validator {
	private static final Logger logger = Logger.getLogger(UserValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		logger.info("Doing validation...");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
				"required.user.email", "Email is required.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
				"required.user.firstName", "First name is required.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
				"required.user.lastName", "Last name is required.");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role",
				"required.user.role", "Role is required.");

	}

}
