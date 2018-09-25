/**
 *
 */
package com.nibss.cmms.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import com.nibss.cmms.web.WebAppConstants;





@Entity
@Table(name = "MANDATE_REQUEST")
public class Mandate extends AbstractTimeStampEntity implements Serializable {

  
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1993901159494808386L;

	private Long id;

    private String mandateCode;
    
    private String subscriberCode;
    
    private int channel;
    
    private BigDecimal amount;
    
    private Bank bank;
    
    private String email;
    
    private String payerName;
    
    private String payerAddress;
        
    @NotBlank
    private String accountName;
    
    @NotBlank
    private String accountNumber;
    
    private String narration;
    
    private String mandateImage;
    
    private Date startDate;
    
    private Date endDate;
    
    private Product product;
    
    private int frequency;
    
    private MandateStatus status;
    
    //private MandateType mandateType;
    
    private User createdBy;
    
    private User approvedBy;
    
    private User acceptedBy;
    
    private User authorizedBy;
     
    private Date dateApproved;
    
    private Date dateAccepted;
    
    private Date dateAuthorized;
    
    private String phoneNumber;
    
    private Rejection rejection;
        
    private String validityDateRange;
    
    private MultipartFile uploadFile;
    
    private User lastActionBy;
    
    private Date dateSuspended;
    
    private boolean mandateAdviceSent;    
    
    private Date nextDebitDate;
    
    private int kycLevel;
    
    private String bvn;
    
    /**
     *  0 - active
     *  1 -  suspend requested
     *  2- suspended
     *  3 - deleted logically
     */
    private int requestStatus;
    
    private int bankNotified;
    private int billerNotified;
	@Column(nullable = true)
	private String lastApiNotificationResponse;

    public String getLastApiNotificationResponse() {
		return lastApiNotificationResponse;
	}


	public void setLastApiNotificationResponse(String lastApiNotificationResponse) {
		this.lastApiNotificationResponse = lastApiNotificationResponse;
	}

	/**
     *
     *  1 -postpaid
     *  2- prepaid
     *  
     */
    private int serviceType;
    
    
    
    /**
    *
    *  1 -Direct Debit mandate
    *  2- Balance Enquiry Only
    *  3- Fund Sweeping and Balance Enquiry
    */
    private String mandateType;
    
    private boolean fixedAmountMandate;
    
    public int getBankNotified() {
		return bankNotified;
	}


	public void setBankNotified(int bankNotified) {
		this.bankNotified = bankNotified;
	}


	public int getBillerNotified() {
		return billerNotified;
	}


	public void setBillerNotified(int billerNotified) {
		this.billerNotified = billerNotified;
	}

	private BigDecimal variableAmount;
    
    
    public String getMandateType() {
		return mandateType;
	}


	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}


	public Mandate(){
    	this.requestStatus=WebAppConstants.STATUS_ACTIVE;
    	this.serviceType=WebAppConstants.SERVICE_TYPE_POSTPAID;
    	this.bankNotified=0;
    	this.billerNotified=0;
    	this.mandateType="Direct Debit";
    }
    

    @Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public int getServiceType() {
		return serviceType;
	}


	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}


	public void setId(Long id) {
		this.id = id;
	}

	

	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Biller getBiller() {
		return biller;
	}

	public void setBillerId(Biller billerId) {
		this.biller = billerId;
	}
*/
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(referencedColumnName ="bankCode")
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public String getMandateCode() {
		return mandateCode;
	}

	public void setMandateCode(String mandateCode) {
		this.mandateCode = mandateCode;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPayerAddress() {
		return payerAddress;
	}

	public void setPayerAddress(String payerAddress) {
		this.payerAddress = payerAddress;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/*public void setBiller(Biller biller) {
		this.biller = biller;
	}*/

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

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getSubscriberCode() {
		return subscriberCode;
	}

	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(insertable=true,updatable=true,nullable=true,unique=true)
	public Rejection getRejection() {
		return rejection;
	}

	public void setRejection(Rejection rejection) {
		this.rejection = rejection;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	public MandateStatus getStatus() {
		return status;
	}
	

	/*@ManyToOne(fetch = FetchType.LAZY)
	public MandateType getMandateType() {
		return mandateType;
	}


	public void setMandateType(MandateType mandateType) {
		this.mandateType = mandateType;
	}*/


	public void setStatus(MandateStatus status) {
		
		this.status = status;
	}

	public String getMandateImage() {
		return mandateImage;
	}
	
	public void setMandateImage(String mandateImage) {
		this.mandateImage = mandateImage;
	}
	

	@Transient
	public String getValidityDateRange() {
		return validityDateRange;
	}

	public void setValidityDateRange(String validityDateRange) {
		this.validityDateRange = validityDateRange;
	}

	@Transient
	public MultipartFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public User getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public User getAcceptedBy() {
		return acceptedBy;
	}

	public void setAcceptedBy(User acceptedBy) {
		this.acceptedBy = acceptedBy;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public User getAuthorizedBy() {
		return authorizedBy;
	}

	public void setAuthorizedBy(User authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public Date getDateAccepted() {
		return dateAccepted;
	}

	public void setDateAccepted(Date dateAccepted) {
		this.dateAccepted = dateAccepted;
	}

	
	public Date getDateAuthorized() {
		return dateAuthorized;
	}

	public void setDateAuthorized(Date dateAuthorized) {
		this.dateAuthorized = dateAuthorized;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public User getLastActionBy() {
		return lastActionBy;
	}

	public void setLastActionBy(User lastActionBy) {
		this.lastActionBy = lastActionBy;
	}

	public boolean isMandateAdviceSent() {
		return mandateAdviceSent;
	}

	public void setMandateAdviceSent(boolean mandateAdviceSent) {
		this.mandateAdviceSent = mandateAdviceSent;
	}

	public int getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(int requestStatus) {
		this.requestStatus = requestStatus;
	}

	public Date getDateSuspended() {
		return dateSuspended;
	}

	public void setDateSuspended(Date dateSuspended) {
		this.dateSuspended = dateSuspended;
	}

	public int getKycLevel() {
		return kycLevel;
	}

	public void setKycLevel(int kycLevel) {
		this.kycLevel = kycLevel;
	}

	public String getBvn() {
		return bvn;
	}

	public void setBvn(String bvn) {
		this.bvn = bvn;
	}

	public Date getNextDebitDate() {
		return nextDebitDate;
	}

	public void setNextDebitDate(Date nextDebitDate) {
		this.nextDebitDate = nextDebitDate;
	}
	
	public BigDecimal getVariableAmount() {
		return variableAmount;
	}

	public void setVariableAmount(BigDecimal variableAmount) {
		this.variableAmount = variableAmount;
	}

	public boolean isFixedAmountMandate() {
		return fixedAmountMandate;
	}

	public void setFixedAmountMandate(boolean fixedAmountMandate) {
		this.fixedAmountMandate = fixedAmountMandate;
	}

	
    
    
}
