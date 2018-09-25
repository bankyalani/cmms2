package com.nibss.cmms.web.validation;


import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nibss.cmms.domain.Biller;

@Component
public class BillerValidator implements Validator {
	private static final Logger logger = Logger.getLogger(BillerValidator.class);

	// Content types the user can upload
		private static final String[] ACCEPTED_CONTENT_TYPES = new String[] {
			"application/pdf",
			"image/jpeg",
			"image/jpg",
			"image/png",
			"image/gif",
			"image/pjpeg"

		};

		private static final String[] ACCEPTED_EXTENSIONS = new String[] {
			"jpg",
			"jpeg",
			"gif",
			"png",  
			"pdf",  
		};
		
	@Override
	public boolean supports(Class<?> clazz) {
		return Biller.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		logger.info("Doing validation...");
				
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountName",
				"required.account.name", "Account name is required.");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountNumber",
				"required.account.number", "Account number is required.");
		
		Biller biller= (Biller) target;
		if(biller.getCompany() == null || biller.getCompany().getId()==0) {
	          errors.rejectValue("company", "required.biller.name","Please select a Biller");
	      }
	
		logger.info("biller.getSlaAttachment()"+biller.getSlaAttachment());
		if(biller.getSlaAttachment() == null || biller.getSlaAttachment().equals("")) {
			errors.reject("slaAttachment", "SLA & Indemnity attachment is required");
	      }
	
		
		if(biller.getSlaAttachment()!=null &&  biller.getSlaAttachment().getSize()>0){
			boolean acceptableContentType = false;
			String incomingContentType = biller.getSlaAttachment().getContentType();
			logger.info("FileName = "+biller.getSlaAttachment().getName());      
			logger.info("incomingContentType = "+incomingContentType);
			if("application/octet-stream".equalsIgnoreCase(incomingContentType)){
				int index = biller.getSlaAttachment().getOriginalFilename().lastIndexOf('.');
				String incomingExtension = biller.getSlaAttachment().getOriginalFilename().substring(index + 1);
				for(String extendsion : ACCEPTED_EXTENSIONS){
					if(extendsion.equalsIgnoreCase(incomingExtension)){
						acceptableContentType = true;
						break;
					}           
				}
			}else{
				for(String contentType : ACCEPTED_CONTENT_TYPES){
					logger.debug("Comparing " + incomingContentType +" to "+ contentType);
					if(contentType.equalsIgnoreCase(incomingContentType)){
						acceptableContentType = true;
						break;
					}           
				}
			}
			
			if(!acceptableContentType){
				errors.reject("slaAttachment", "Please upload a file with one of the following file types; "+Arrays.asList(ACCEPTED_EXTENSIONS).toString());
			}else if(biller.getSlaAttachment().getSize() >5000000){
				errors.reject("slaAttachment", "Upload file must not be larger than 5MB");

			}

		}


		
				
	}

}
