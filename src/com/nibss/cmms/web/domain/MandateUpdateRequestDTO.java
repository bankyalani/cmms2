package com.nibss.cmms.web.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nibss.cmms.utils.BASE64DecodedMultipartFile;

public 	class MandateUpdateRequestDTO implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -964544633212988530L;

		@NotBlank(message="Mandate Code is mandatory")
		private String mandateCode;
		
				
		public String getMandateCode() {
			return mandateCode;
		}

		public void setMandateCode(String mandateCode) {
			this.mandateCode = mandateCode;
		}

		@NotBlank(message="Payer Name is mandatory")
		private String payerName;
		
		@NotBlank(message="Payer Address is mandatory")
		private String payerAddress;
		
	
		@NotBlank(message="Phone Number is mandatory")
		private String phoneNumber;
		
		@NotBlank(message="Email Address is mandatory")
		@Email(message="Email Address is not valid")
		private String emailAddress; 
		
		//@NotBlank(message="Status  is mandatory")
		@NotNull(message="Status is required")
		private int status;
	
		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}
		
		//@NotBlank(message="Work Flow Status  is mandatory")
		@NotNull(message="Work Flow Status is required")
		private int workFlowStatus;
	
	

		public int getWorkFlowStatus() {
			return workFlowStatus;
		}

		public void setWorkFlowStatus(int workFlowStatus) {
			this.workFlowStatus = workFlowStatus;
		}

		private String narration;
		
	
		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public String getEmailAddress() {
			return emailAddress;
		}

		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}



		public String getNarration() {
			return narration;
		}

		public void setNarration(String narration) {
			this.narration = narration;
		}

	
	
		public String getPayerName() {
			return payerName;
		}

		public void setPayerName(String payerName) {
			this.payerName = payerName;
		}

		public String getPayerAddress() {
			return payerAddress;
		}

		public void setPayerAddress(String payerAddress) {
			this.payerAddress = payerAddress;
		}

}
