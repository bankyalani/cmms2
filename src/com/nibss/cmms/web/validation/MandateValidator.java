package com.nibss.cmms.web.validation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.utils.DateUtils;
import com.nibss.cmms.utils.Utilities;
import com.nibss.nip.dao.NipDAO;
import com.nibss.nip.dto.NESingleRequest;
import com.nibss.nip.dto.NESingleResponse;
import com.nibss.util.SessionUtil;

@Component
public class MandateValidator implements Validator {

	private static final Logger logger = LoggerFactory.getLogger(MandateValidator.class);

	@Autowired
	private NipDAO nipService;

	@Value("${cmms.channel.code}")
	private int cmmChannelCode;

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
		return Mandate.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		logger.info("Doing validation...");
		/*ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount",
				"required.amount", "Amount is required.");*/

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subscriberCode",
				"required.subscriberCode", "Subscriber Code is required.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "payerName",
				"required.payerName", "Payer Name is required.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "payerAddress",
				"required.payerAddress", "Payer Address is required.");

		/*		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
				"required.email", "Email Address is required.");*/

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "narration",
				"required.narration", "Narration is required.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber",
				"required.phone", "Phone Number is required.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "validityDateRange",
				"required.validityDateRange", "Validity Date Range is required.");



		Mandate mandate= (Mandate) target;
		/*if(mandate.getAccountName() != null && mandate.getAccountName()!="") {
			String[] accountNameArray=mandate.getAccountName().split(" ");
			if(accountNameArray.length<2)
				errors.reject("accountName", "Account Name must be at least 2 names");
		}*/

		if(mandate.getBank() == null) {
			errors.rejectValue("bank", "required.bank");
		}
		if(mandate.getProduct() == null) {
			errors.rejectValue("product", "required.product");
		}

		/*if(mandate.getId()==null){
			if (mandate.getUploadFile()==null || mandate.getUploadFile().getSize()==0){
				errors.rejectValue("uploadFile", "required.mandateImage");
			}
		}
		 */
		if (mandate.getFrequency()==-1){
			errors.reject("frequecny", "Mandate Debit Frequecy is required!");
		}

		logger.info("mandate.getFrequency()"+mandate.getFrequency());
		logger.info("mandate.getValidityDateRange()"+mandate.getValidityDateRange());
		//check the date vs the frequecy select
		if(mandate.getFrequency()>0 && !mandate.getValidityDateRange().isEmpty()){
			logger.info("Trying to validate period and frequency");
			String[] dateRange=mandate.getValidityDateRange().split("-");
			SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");

			try {
				Date startDate;
				startDate = sdf.parse(dateRange[0]);
				Date endDate= sdf.parse(dateRange[1]);
				if(startDate.compareTo(DateUtils.nullifyTime(new Date()))<=0){
					errors.reject("validityDateRange", "Mandate Start date cannot be today");
				}else{

					long diff= endDate.getTime()-startDate.getTime();		
					diff=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
					logger.info("Date difference bewteen start and end date is "+diff);
					if(diff<(mandate.getFrequency()*7))
						errors.reject("frequency", "Mandate date range must be able to accommodate frequency");
				}
			} catch (ParseException e) {
				logger.error(null,e);
				errors.reject("validityDateRange", "Unable to compute debit frequency and period");
			}

		}


		logger.info("mandate upload file "+mandate.getUploadFile());
		if(mandate.getUploadFile()!=null &&  mandate.getUploadFile().getSize()>0){
			boolean acceptableContentType = false;
			String incomingContentType = mandate.getUploadFile().getContentType();
			logger.info("FileName = "+mandate.getUploadFile().getName());      
			logger.info("incomingContentType = "+incomingContentType);
			if("application/octet-stream".equalsIgnoreCase(incomingContentType)){
				int index = mandate.getUploadFile().getOriginalFilename().lastIndexOf('.');
				String incomingExtension = mandate.getUploadFile().getOriginalFilename().substring(index + 1);
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
				errors.reject("uploadFile", "Please upload a file with one of the following file types; "+Arrays.asList(ACCEPTED_EXTENSIONS).toString());
			}else if(mandate.getUploadFile().getSize() >2500000){
				errors.reject("uploadFile", "Upload file must not be larger than 2.5MB");

			}

		}

		if(mandate.getEmail() != null && !Utilities.validateEmailID(mandate.getEmail())){
			errors.rejectValue("email", "invalid.email");
		}
		
		if(mandate.getAmount() == null || mandate.getAmount().compareTo(BigDecimal.ZERO)<=0){
			errors.rejectValue("amount", "required.amount");
		}

		/*logger.debug("NubanVerifier.isNubanAccount("+mandate.getBank().getBankCode()+","+mandate.getAccountNumber()+")==>"+NubanVerifier.isNubanAccount(mandate.getBank().getBankCode(),mandate.getAccountNumber()));
		if(NubanVerifier.isNubanAccount(mandate.getBank().getBankCode(),mandate.getAccountNumber())!=1){

			errors.rejectValue("accountNumber", "invalid.accountNumber",
					"Account number is not Valid");//for "+mandate.getBank().getBankName());
		}
		 */
		if(mandate.getAccountNumber()!=null && !mandate.getAccountNumber().equals("")&& mandate.getBank()!=null){
			NESingleRequest nESingleRequest= new NESingleRequest();
			nESingleRequest.setChannelCode(cmmChannelCode);
			nESingleRequest.setDestinationInstitutionCode(mandate.getBank().getNipBankCode());
			nESingleRequest.setAccountNumber(mandate.getAccountNumber());
			nESingleRequest.setSessionID(SessionUtil.generateSessionID("999", "999"));
			try {
				NESingleResponse nESingleResponse = nipService.sendNameEnquiry(nESingleRequest);
				if(nESingleResponse.getResponseCode()==null || nESingleResponse.getResponseCode().equals("91")){
					
		
					
					errors.reject("accountNumber", "Unable to verify account information from bank");
				}else{
					if(nESingleResponse.getResponseCode().equals("00")){
						//if(nESingleResponse.getAccountName().equalsIgnoreCase(mandate.getAccountName())){
						mandate.setBvn(nESingleResponse.getBankVerificationNumber());
						mandate.setKycLevel(nESingleResponse.getKycLevel());
						mandate.setAccountName(nESingleResponse.getAccountName());
						/*}else{
							errors.reject("accountNumber", "Invalid account name. Please use "+nESingleResponse.getAccountName());
						}*/
					}else{
						errors.reject("accountNumber", "Account number not valid for Bank");
					}
				}
			} catch (Exception e) {
			
				errors.reject("accountNumber", "Unable to verify account information from bank");
				logger.error(null,e);
			}
		}


	}

	public static void main(String[] args) {
		//System.out.println(NubanVerifier.isNubanAccount("033","8888888888"));
		System.out.println("Ademola".split(" ").length);
	}

}
