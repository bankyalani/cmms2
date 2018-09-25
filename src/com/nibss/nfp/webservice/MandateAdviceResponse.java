package com.nibss.nfp.webservice;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MandateAdviceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MandateAdviceResponse implements Serializable {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 5919589138302146319L;

	@XmlElement(name="SessionID")
	private String sessionID;
	
	@XmlElement(name="DestinationInstitutionCode")
	private String destinationInstitutionCode;
	
	@XmlElement(name="ChannelCode")
	private int channelCode;
	
	@XmlElement(name="MandateReferenceNumber")
	private String mandateReferenceNumber;
	
	@XmlElement(name="Amount")
	private BigDecimal amount;
	
	@XmlElement(name="DebitAccountName")
	private String debitAccountName;
	
	@XmlElement(name="DebitAccountNumber")
	private String debitAccountNumber;
	
	@XmlElement(name="DebitBankVerificationNumber")
	private String debitBankVerificationNumber;
	
	@XmlElement(name="DebitKYCLevel")
	private int debitKYCLevel;
	
	@XmlElement(name="BeneficiaryAccountName")
	private String beneficiaryAccountName;
	
	@XmlElement(name="BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;
	
	@XmlElement(name="BeneficiaryBankVerificationNumber")
	private String beneficiaryBVN;
	
	@XmlElement(name="BeneficiaryKYCLevel")
	private String beneficiaryKYCLevel;
	
	@XmlElement(name="ResponseCode")
	private String responseCode;

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getDestinationInstitutionCode() {
		return destinationInstitutionCode;
	}

	public void setDestinationInstitutionCode(String destinationInstitutionCode) {
		this.destinationInstitutionCode = destinationInstitutionCode;
	}

	public int getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(int channelCode) {
		this.channelCode = channelCode;
	}

	public String getMandateReferenceNumber() {
		return mandateReferenceNumber;
	}

	public void setMandateReferenceNumber(String mandateReferenceNumber) {
		this.mandateReferenceNumber = mandateReferenceNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDebitAccountName() {
		return debitAccountName;
	}

	public void setDebitAccountName(String debitAccountName) {
		this.debitAccountName = debitAccountName;
	}

	public String getDebitAccountNumber() {
		return debitAccountNumber;
	}

	public void setDebitAccountNumber(String debitAccountNumber) {
		this.debitAccountNumber = debitAccountNumber;
	}

	public String getDebitBankVerificationNumber() {
		return debitBankVerificationNumber;
	}

	public void setDebitBankVerificationNumber(String debitBankVerificationNumber) {
		this.debitBankVerificationNumber = debitBankVerificationNumber;
	}

	public int getDebitKYCLevel() {
		return debitKYCLevel;
	}

	public void setDebitKYCLevel(int debitKYCLevel) {
		this.debitKYCLevel = debitKYCLevel;
	}

	public String getBeneficiaryAccountName() {
		return beneficiaryAccountName;
	}

	public void setBeneficiaryAccountName(String beneficiaryAccountName) {
		this.beneficiaryAccountName = beneficiaryAccountName;
	}

	public String getBeneficiaryAccountNumber() {
		return beneficiaryAccountNumber;
	}

	public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
		this.beneficiaryAccountNumber = beneficiaryAccountNumber;
	}

	public String getBeneficiaryBVN() {
		return beneficiaryBVN;
	}

	public void setBeneficiaryBVN(String beneficiaryBVN) {
		this.beneficiaryBVN = beneficiaryBVN;
	}

	public String getBeneficiaryKYCLevel() {
		return beneficiaryKYCLevel;
	}

	public void setBeneficiaryKYCLevel(String beneficiaryKYCLevel) {
		this.beneficiaryKYCLevel = beneficiaryKYCLevel;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

}
