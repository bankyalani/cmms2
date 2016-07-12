/**
 *
 */
package com.nibss.cmms.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.web.multipart.MultipartFile;


@Entity
@Table(name = "BILLERS")
public class Biller extends AbstractTimeStampEntity implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7453947266569689148L;

	private Long id;
	
    private int status;
    
    private Bank bank;
    
    private String accountNumber;
    
    private String accountName;
    
    private User createdBy;
    
    private Company company;
    
    private MultipartFile slaAttachment;
    
    private String slaAttachmentPath;
    
    private String bvn;
    
    private int kycLevel;
    
    private String apiKey;
    private String billingMandateCode;
    private String notificationUrl;
    
    public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}

	private BigDecimal unitFee;
    //private Set<Mandate> mandates= new HashSet<>();
    
    
    public BigDecimal getUnitFee() {
		return unitFee;
	}

	public void setUnitFee(BigDecimal unitFee) {
		this.unitFee = unitFee;
	}

	public String getBillingMandateCode() {
		return billingMandateCode;
	}

	public void setBillingMandateCode(String billingMandateCode) {
		this.billingMandateCode = billingMandateCode;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(referencedColumnName ="bankCode")
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}
	

	/*@OneToMany(mappedBy="biller", cascade=CascadeType.ALL)  
	public Set<Mandate> getMandates() {
		return mandates;
	}

	public void setMandates(Set<Mandate> mandates) {
		this.mandates = mandates;
	}
*/
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@OneToOne
	@JoinColumn(unique=true)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Biller other = (Biller) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getBvn() {
		return bvn;
	}

	public void setBvn(String bvn) {
		this.bvn = bvn;
	}

	public int getKycLevel() {
		return kycLevel;
	}

	public void setKycLevel(int kycLevel) {
		this.kycLevel = kycLevel;
	}

	@Transient
	public MultipartFile getSlaAttachment() {
		return slaAttachment;
	}

	public void setSlaAttachment(MultipartFile slaAttachment) {
		this.slaAttachment = slaAttachment;
	}

	public String getSlaAttachmentPath() {
		return slaAttachmentPath;
	}

	public void setSlaAttachmentPath(String slaAttachmentPath) {
		this.slaAttachmentPath = slaAttachmentPath;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

    
    
    
}
