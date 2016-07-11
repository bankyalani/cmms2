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

public 	class MandateRequestDTO implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -964544633212988530L;

		@NotBlank(message="Account Number is mandatory")
		private String accountNumber;
		
		@NotNull(message="Product Id is mandatory")
		private Long productId;
		
		@NotBlank(message="Bank Code is mandatory")
		private String bankCode;
				
		@NotBlank(message="Payer Name is mandatory")
		private String payerName;
		
		@NotBlank(message="Payer Address is mandatory")
		private String payerAddress;
		
		@NotBlank(message="Account Name is mandatory")
		private String accountName;
		
		private BigDecimal amount;
		
		private String payeeName;
		
		private String payeeAddress;
		
		private FileAttachment mandateFile;
		
		@NotBlank(message="Phone Number is mandatory")
		private String phoneNumber;
		
		@NotBlank(message="Email Address is mandatory")
		@Email(message="Email Address is not valid")
		private String emailAddress; 
		
		@NotBlank(message="Subscriber Code is mandatory")
		private String subscriberCode;
		
		@NotNull(message="Start Date is mandatory")
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyyMMdd")
		@DateTimeFormat(pattern="yyyyMMdd")
		@Future(message="Mandate start date must be greater than today")
		private  Date startDate;
		
		@NotNull(message="End Date is mandatory")
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyyMMdd")
		@DateTimeFormat(pattern="yyyyMMdd")
		private Date endDate;
		
		@NotNull(message="Frequency is required")
		private int frequency;
		
		private String narration;
		
		public String getAccountNumber() {
			return accountNumber;
		}

		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}

		public Long getProductId() {
			return productId;
		}

		public void setProductId(Long productId) {
			this.productId = productId;
		}

		public String getBankCode() {
			return bankCode;
		}

		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}

		public String getAccountName() {
			return accountName;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

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

		public String getSubscriberCode() {
			return subscriberCode;
		}

		public void setSubscriberCode(String subscriberCode) {
			this.subscriberCode = subscriberCode;
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}

		public String getNarration() {
			return narration;
		}

		public void setNarration(String narration) {
			this.narration = narration;
		}

		public MultipartFile getMandateImage() {
			return new BASE64DecodedMultipartFile(this.getMandateFile());
		}

		public FileAttachment getMandateFile() {
			return mandateFile;
		}

		public void setMandateFile(FileAttachment mandateFile) {
			this.mandateFile = mandateFile;
		}

		public String getPayeeName() {
			return payeeName;
		}

		public void setPayeeName(String payeeName) {
			this.payeeName = payeeName;
		}

		public String getPayeeAddress() {
			return payeeAddress;
		}

		public void setPayeeAddress(String payeeAddress) {
			this.payeeAddress = payeeAddress;
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
