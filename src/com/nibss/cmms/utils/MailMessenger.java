package com.nibss.cmms.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nibss.cmms.domain.BankUser;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.Notification;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.service.NotificationService;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;


@Component
//@Scope(proxyMode=ScopedProxyMode.NO)
public class MailMessenger {

	private static final Logger logger= LoggerFactory.getLogger(MailMessenger.class);

	@Autowired
	private MailSender mailSender;

	@Value("${appUrl}")
	private String AppUrl;

	private MimeMessageHelper helper=null;

	private Mandate mandate=null;
	
	SimpleDateFormat sdf=null;
	DecimalFormat df= null;

	@Autowired
	private NotificationService notificationService;

	public enum MailType{
		NEW_MANDATE,
		MANDATE_AUTHORIZED, //bank authorized
		MANDATE_APPROVED,//biller approved
		MANDATE_REJECTED, //bank rejected
		MANDATE_DISAPPROVED, //biller disapprove
		BULK_MANDATE_UPLOAD,
		BULK_MANDATE_FAILED,
		NEW_USER,
		RESET_USER_PASSWORD
	}

	@Async
	public void sendMail(String[] to, String[] cc, String message, File file){
		JavaMailSenderImpl sender =(JavaMailSenderImpl)mailSender;
		MimeMessage msg = sender.createMimeMessage();
		try {
			this.helper = new MimeMessageHelper(msg, true);
			if (null!=file)
				helper.addAttachment(file.getName(), file);
			if(null!=cc)
				helper.setCc(cc);
			helper.setTo(to);
			sender.send(msg);
		} catch (Exception e) {
			logger.error(null,e);
		}		
	}
	
	
	

	@Async
	public void sendMail(MailType mailType,Mandate mandate){
		JavaMailSenderImpl sender =(JavaMailSenderImpl)mailSender;
		MimeMessage msg = sender.createMimeMessage();
		try {
			this.helper = new MimeMessageHelper(msg, true);
			sdf= new SimpleDateFormat("YYYY-MMM-dd");
			df=new DecimalFormat("#0,000.00");
			this.mandate=mandate;

			switch(mailType){
			case NEW_MANDATE:
				mandateInitiated();
				break;
			case MANDATE_APPROVED:
				mandateApproved();
				break;
			case MANDATE_REJECTED:
				mandateRejected();
				break;
			/*case MANDATE_VERIFIED:
				mandateVerified();
				break;
			case MANDATE_DENIED:
				mandateDenied();
				break;		*/
			case MANDATE_AUTHORIZED:
				mandateAuthorized();
				break;
			case MANDATE_DISAPPROVED:
				mandateDisapproved();
				break;
			default:
				break;
			}
			sender.send(msg);
		} catch (Exception e) {
			logger.error("--Could not send email--",e);
		}


	}
	
	@Async
	public void sendMail(MailType mailType,User user){
		JavaMailSenderImpl sender =(JavaMailSenderImpl)mailSender;
		MimeMessage msg = sender.createMimeMessage();
		try {
			this.helper = new MimeMessageHelper(msg, true);
			switch(mailType){
				case NEW_USER:
				newUserCreation(user);
				break;
			case RESET_USER_PASSWORD:
				resetUserPassword(user);
				break;
			default:
				break;
			}
			sender.send(msg);
		} catch (MessagingException | ServerBusinessException e) {
			logger.error("--Could not send email--",e);
		}


	}

	/*private void resetUserPassword(User user) {
		// TODO Auto-generated method stub
		
	}*/
	private void resetUserPassword(User user) throws MessagingException,ServerBusinessException {
		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello %s,</b></p>"+
				"<p class=\"style1\">This is to inform you of your new password on CMMS;</p>"
				+ "<p class=\"style1\">Username: %s</p>"
				+ "<p class=\"style1\">Password: %s</p>"
				+ "<p class=\"style1\">Role: %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">Kindly <a href='"+AppUrl+"'>log in</a> to the CMMS platform.</p>"
				+ "<br/>"	
				+"</body>";
		
		helper.setTo(user.getEmail());

		helper.setSubject(String.format("CMMS Notification Service – Profile reset"));
		helper.setText(String.format(message, 
				user.getFirstName(),
				user.getEmail(),
				user.getPassword(),
				user.getRole().getName()),true);		
	}





	private void newUserCreation(User user) throws MessagingException,ServerBusinessException {
		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello %s,</b></p>"+
				"<p class=\"style1\">This is to inform you of your new profile on CMMS;</p>"
				+ "<p class=\"style1\">Username: %s</p>"
				+ "<p class=\"style1\">Password: %s</p>"
				+ "<p class=\"style1\">Role: %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">Kindly <a href='"+AppUrl+"'>log in</a> to the CMMS platform.</p>"
				+ "<br/>"	
				+"</body>";
		
		helper.setTo(user.getEmail());

		helper.setSubject(String.format("CMMS Notification Service – New user creation"));
		helper.setText(String.format(message, 
				user.getFirstName(),
				user.getEmail(),
				user.getPassword(),
				user.getRole().getName()),true);		
	}




	private void mandateInitiated() throws Exception{
		
		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello Sir/Ma</b></p>"+
				"<p class=\"style1\">We refer to mandate set-up request with details below;</p>"
				+ "<p class=\"style1\">Mandate Reference Code: %s</p>"
				+ "<p class=\"style1\">Subscriber Mandate Code: %s</p>"
				+ "<p class=\"style1\">Subscriber: %s</p>"
				+ "<p class=\"style1\">Product: %s</p>"
				+ "<p class=\"style1\">Amount: NGN %s</p>"
				+ "<p class=\"style1\">Customer Bank: %s</p>"
				+ "<p class=\"style1\">Account Name: %s</p>"
				+ "<p class=\"style1\">Mandate Validity Range: %s - %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">This notification serves to inform you that the mandate set-up request has been <strong>Initiated.<strong></p>"
				+ "<p class=\"style1\">The Biller Authorizer is required to <a href='"+AppUrl+"'>log in</a> to the CMMS platform and review (verify or reject) the mandate for further processing.</p>"
				+ "<p class=\"style1\">Should you have any concerns, please contact your Bank</p>"		
				+"</body>";


		
		helper.setCc(new InternetAddress(mandate.getCreatedBy().getEmail(), false));
		//email address for new mandates created
		if(mandate.getCreatedBy() instanceof BillerUser){
			List<Notification> billerNotifications=notificationService
					.getNotificationByBillerAndStatusId(mandate.getProduct().getBiller(), WebAppConstants.BILLER_INITIATE_MANDATE);
			helper.setTo(getInternetAddresses(billerNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList())));
		}else if(mandate.getCreatedBy() instanceof BankUser){
			List<Notification> bankNotifications=notificationService.getNotificationByBankAndStatusId(((BankUser)mandate.getCreatedBy()).getBank(), WebAppConstants.BANK_INITIATE_MANDATE);
			helper.setTo(getInternetAddresses(bankNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList())));
		}
		helper.setSubject(String.format("CMMS Notification Service – New Mandate Set-up Request Initiated on behalf of %s",
				mandate.getProduct().getBiller().getCompany().getName()));
		helper.setText(String.format(message, 
				mandate.getMandateCode(),
				mandate.getSubscriberCode(),
				mandate.getProduct().getBiller().getCompany().getName(),
				mandate.getProduct().getName(),
				df.format(mandate.getAmount().doubleValue()),
				mandate.getBank().getBankName(),
				mandate.getAccountName(),
				sdf.format(mandate.getStartDate()),
				sdf.format(mandate.getEndDate())),true);
	}

	private void mandateApproved() throws Exception{

		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello Sir/Ma</b></p>"+
				"<p class=\"style1\">We refer to mandate set-up request with details below;</p>"
				+ "<p class=\"style1\">Mandate Reference Code: %s</p>"
				+ "<p class=\"style1\">Subscriber Mandate Code: %s</p>"
				+ "<p class=\"style1\">Subscriber: %s</p>"
				+ "<p class=\"style1\">Amount: NGN %s</p>"
				+ "<p class=\"style1\">Account Name: %s</p>"
				+ "<p class=\"style1\">Mandate Validity Range: %s - %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">This notification serves to inform you that the mandate set-up request has been <strong>Verified.<strong></p>"
				+ "<p class=\"style1\">The Bank Initiator is required to <a href='"+AppUrl+"'>log in</a> to the CMMS platform and review (verify or reject) the mandate for further processing.</p>"			
				+ "<p class=\"style1\">Should you have any concerns, please contact NIBSS Support Centre on:<a href='mailto:contactcentre@nibss-plc.com.ng'>contactcentre@nibss-plc.com.ng</a> or phone: 01-448-5388</p>"
				+"</body>";


		if(mandate.getApprovedBy() instanceof BillerUser){
			List<Notification> billerNotifications=notificationService
					.getNotificationByBillerAndStatusId(mandate.getProduct().getBiller(), WebAppConstants.BILLER_APPROVE_MANDATE);
			helper.setTo(getInternetAddresses(billerNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList())));
			
		}else if(mandate.getApprovedBy() instanceof BankUser){
			List<Notification> bankNotifications=notificationService.getNotificationByBankAndStatusId(((BankUser)mandate.getCreatedBy()).getBank(), WebAppConstants.BANK_APPROVE_MANDATE);
			helper.setTo(getInternetAddresses(bankNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList())));

		}
		
		
		helper.setCc(new InternetAddress[]{new InternetAddress(mandate.getCreatedBy().getEmail(), false),
				new InternetAddress(mandate.getApprovedBy().getEmail(), false)});
		
		
		helper.setSubject(String.format("CMMS Notification Service – New Mandate Set-up Request Initiated on behalf of %s",
				mandate.getAccountName()));
		helper.setText(String.format(message, 
				mandate.getMandateCode(),
				mandate.getSubscriberCode(),
				mandate.getProduct().getBiller().getCompany().getName(),
				df.format(mandate.getAmount().doubleValue()),
				mandate.getAccountName(),
				sdf.format(mandate.getStartDate()),
				sdf.format(mandate.getEndDate())),true);

	}


	private void mandateVerified() throws ServerBusinessException, MessagingException{

		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello Sir/Ma</b></p>"+
				"<p class=\"style1\">We refer to mandate set-up request with details below;</p>"
				+ "<br/>"
				+ "<p class=\"style1\">Mandate Reference Code: %s</p>"
				+ "<p class=\"style1\">Subscriber Mandate Code: %s</p>"
				+ "<p class=\"style1\">Subscriber: %s</p>"
				+ "<p class=\"style1\">Amount: %s</p>"
				+ "<p class=\"style1\">Account Name: %s</p>"
				+ "<p class=\"style1\">Mandate Validity Range: %s - %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">This notification serves to inform you that the mandate set-up request has been <strong>Approved.<strong></p>"
				+ "<p class=\"style1\">The Bank Authroizer is required to <a href='"+AppUrl+"'>log in</a> to the CMMS platform and review (verify or reject) the mandate for further processing.</p>"			
				+ "<br/>"				
				+ "<p class=\"style1\">Should you have any concerns, please contact NIBSS Support Centre on:<a href='mailto:contactcentre@nibss-plc.com.ng'>contactcentre@nibss-plc.com.ng</a> or phone: 01-448-5388</p>"
				+"</body>";


		List<Notification> billerNotifications=notificationService
				.getNotificationByBillerAndStatusId(mandate.getProduct().getBiller(), WebAppConstants.BANK_APPROVE_MANDATE);


		List<Notification> bankNotifications=notificationService
				.getNotificationByBankAndStatusId(mandate.getBank(), WebAppConstants.BANK_APPROVE_MANDATE);

		helper.setCc(new InternetAddress[]{new InternetAddress(mandate.getAcceptedBy().getEmail(), false),
				new InternetAddress(mandate.getAuthorizedBy().getEmail(), false)});

		List<String> toEmails= new ArrayList<>();
		
		toEmails.addAll(billerNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList()));
		toEmails.addAll(bankNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList()));

		helper.setTo(getInternetAddresses(toEmails));
		
		helper.setSubject(String.format("CMMS Notification Service – New Mandate Set-up Request on behalf of %s",
				mandate.getAccountName()));
		helper.setText(String.format(message, 
				mandate.getMandateCode(),
				mandate.getSubscriberCode(),
				mandate.getPayerName(),
				mandate.getAmount().toPlainString(),
				mandate.getAccountName(),
				mandate.getStartDate().toString(),
				mandate.getEndDate().toString()),true);
	}

	private void mandateDisapproved() throws Exception{

		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello Sir/Ma</b></p>"+
				"<p class=\"style1\">We refer to mandate set-up request with details below;</p><br/>"
				+ "<p class=\"style1\">Mandate Reference Code: %s</p>"
				+ "<p class=\"style1\">Subscriber Mandate Code: %s</p>"
				+ "<p class=\"style1\">Subscriber: %s</p>"
				+ "<p class=\"style1\">Product: %s</p>"
				+ "<p class=\"style1\">Amount: %s</p>"
				+ "<p class=\"style1\">Customer Bank: %s</p>"
				+ "<p class=\"style1\">Account Name: %s</p>"
				+ "<p class=\"style1\">Mandate Validity Range: %s - %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">This notification serves to inform you that the mandate set-up request has been <strong>Rejected.<strong></p>"
				+ "<p class=\"style1\">The Biller Initiator is required to <a href='"+AppUrl+"'>log in</a> to the CMMS platform and review (edit) the mandate for further processing.</p>"
				+ "<br/>"
				+ "<p>Should you have any concerns, please contact your Bank</p>"		
				+"</body>";


		
		helper.setTo(new InternetAddress(mandate.getCreatedBy().getEmail(), false));
		
		List<String> ccEmails= new ArrayList<>();
		
		ccEmails.add(mandate.getApprovedBy().getEmail());
		if(mandate.getApprovedBy() instanceof BillerUser){
			List<Notification> billerNotifications=notificationService
					.getNotificationByBillerAndStatusId(mandate.getProduct().getBiller(), WebAppConstants.BILLER_REJECT_MANDATE);
			ccEmails.addAll((billerNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList())));
			
		}else if(mandate.getApprovedBy() instanceof BankUser){
			List<Notification> bankNotifications=notificationService.getNotificationByBankAndStatusId(((BankUser)mandate.getCreatedBy()).getBank(), WebAppConstants.BANK_REJECT_MANDATE);
			ccEmails.addAll((bankNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList())));
		}

		helper.setCc(getInternetAddresses(ccEmails));		
		
		helper.setSubject(String.format("CMMS Notification Service – New Mandate Set-up Request on behalf of %s",
				mandate.getProduct().getBiller().getCompany().getName()));
		helper.setText(String.format(message, 
				mandate.getMandateCode(),
				mandate.getSubscriberCode(),
				mandate.getPayerName(),
				mandate.getProduct().getName(),
				mandate.getAmount().toPlainString(),
				mandate.getBank().getBankName(),
				mandate.getAccountName(),
				mandate.getStartDate().toString(),
				mandate.getEndDate().toString()),true);
	}


	private void mandateAuthorized() throws ServerBusinessException, MessagingException{

		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello Sir/Ma</b></p>"+
				"<p class=\"style1\">We refer to mandate set-up request with details below;</p><br/>"
				+ "<p class=\"style1\">Mandate Reference Code: %s</p>"
				+ "<p class=\"style1\">Subscriber Mandate Code: %s</p>"
				+ "<p class=\"style1\">Subscriber: %s</p>"
				+ "<p class=\"style1\">Product: %s</p>"
				+ "<p class=\"style1\">Amount: %s</p>"
				+ "<p class=\"style1\">Customer Bank: %s</p>"
				+ "<p class=\"style1\">Account Name: %s</p>"
				+ "<p class=\"style1\">Mandate Validity Range: %s - %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">This notification serves to inform you that the mandate set-up request has been <strong>Approved.<strong></p>"
				+ "<p class=\"style1\">Please be advised that this mandate is an authority for <b>%s</b> "
				+ " to present requests for payment from your Account under specified terms with <b>%s</b>. "
				+ " Failure to honour Direct Debit requests constitutes a breach of contract and will attract applicable sanctions.</p>"
				+ "<br/>"
				+ "<p>Should you have any concerns, please contact your Bank</p>"		
				+"</body>";


		
		List<String> ccEmails= new ArrayList<>();
		if(mandate.getApprovedBy() instanceof BillerUser){
			List<Notification> billerNotifications=notificationService
					.getNotificationByBillerAndStatusId(mandate.getProduct().getBiller(), WebAppConstants.BILLER_AUTHORIZE_MANDATE);
			ccEmails.addAll((billerNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList())));
			
		}else if(mandate.getApprovedBy() instanceof BankUser){
			List<Notification> bankNotifications=notificationService.getNotificationByBankAndStatusId(((BankUser)mandate.getCreatedBy()).getBank(), WebAppConstants.BANK_AUTHORIZE_MANDATE);
			ccEmails.addAll((bankNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList())));
		}
		
	
		helper.setTo(new InternetAddress(mandate.getEmail(), false));
		helper.setCc(getInternetAddresses(ccEmails));

		helper.setSubject("CMMS Notification Service – New Mandate Set-up Request on behalf of "+	mandate.getAccountName());
		helper.setText(String.format(message, 
				mandate.getMandateCode(),
				mandate.getSubscriberCode(),
				mandate.getPayerName(),
				mandate.getProduct().getName(),
				mandate.getAmount().toPlainString(),
				mandate.getBank().getBankName(),
				mandate.getAccountName(),
				mandate.getStartDate().toString(),
				mandate.getEndDate().toString(),
				mandate.getProduct().getBiller().getCompany().getName(),
				mandate.getBank().getBankName()
				),true);
	}


	private void mandateDenied() throws ServerBusinessException, MessagingException{

		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello Sir/Ma</b></p>"+
				"<p class=\"style1\">We refer to mandate set-up request with details below;</p><br/>"
				+ "<p class=\"style1\">Mandate Reference Code: %s</p>"
				+ "<p class=\"style1\">Subscriber Mandate Code: %s</p>"
				+ "<p class=\"style1\">Subscriber: %s</p>"
				+ "<p class=\"style1\">Product: %s</p>"
				+ "<p class=\"style1\">Amount: %s</p>"
				+ "<p class=\"style1\">Customer Bank: %s</p>"
				+ "<p class=\"style1\">Account Name: %s</p>"
				+ "<p class=\"style1\">Mandate Validity Range: %s - %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">This notification serves to inform you that the mandate set-up request has been <strong>Disapproved.<strong></p>"
				+ "<br/>"
				+ "<p>Should you have any concerns, please contact your Bank</p>"		
				+"</body>";


		List<Notification> billerNotifications=notificationService
				.getNotificationByBillerAndStatusId(mandate.getProduct().getBiller(), WebAppConstants.BANK_AUTHORIZE_MANDATE);


		List<Notification> bankNotifications=notificationService
				.getNotificationByBankAndStatusId(mandate.getBank(), WebAppConstants.BANK_AUTHORIZE_MANDATE);


		List<String> ccEmails= new ArrayList<>();
		
		ccEmails.addAll(billerNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList()));
		ccEmails.addAll(bankNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList()));

		helper.setTo(new InternetAddress(mandate.getEmail(), false));
		helper.setCc(getInternetAddresses(ccEmails));

		helper.setSubject(String.format("CMMS Notification Service – New Mandate Set-up Request on behalf of %s",
				mandate.getAccountName()));
		helper.setText(String.format(message, 
				mandate.getMandateCode(),
				mandate.getSubscriberCode(),
				mandate.getPayerName(),
				mandate.getProduct().getName(),
				mandate.getAmount().toPlainString(),
				mandate.getBank().getBankName(),
				mandate.getAccountName(),
				mandate.getStartDate().toString(),
				mandate.getEndDate().toString()
				),true);
	}


	private void mandateRejected() throws ServerBusinessException, MessagingException{

		String message="<head><style type=\"text/css\">"+
				"<!--<style type=\"text/css\"> <!--"+
				".style1 {font-family: \"Century Gothic\";font-size: 12px;}"+
				"--></style></head>"+
				"<body>"
				+"<p class=\"style1\"><b>Hello Sir/Ma</b></p>"+
				"<p class=\"style1\">We refer to mandate set-up request with details below;</p><br/>"
				+ "<p class=\"style1\">Mandate Reference Code: %s</p>"
				+ "<p class=\"style1\">Subscriber Mandate Code: %s</p>"
				+ "<p class=\"style1\">Subscriber: %s</p>"
				+ "<p class=\"style1\">Product: %s</p>"
				+ "<p class=\"style1\">Amount: %s</p>"
				+ "<p class=\"style1\">Customer Bank: %s</p>"
				+ "<p class=\"style1\">Account Name: %s</p>"
				+ "<p class=\"style1\">Mandate Validity Range: %s - %s</p>"
				+ "<p class=\"style1\"></p>"
				+ "<p class=\"style1\">This notification serves to inform you that the mandate set-up request has been <strong>Disapproved.<strong></p>"
				+ "<br/>"
				+ "<p>Should you have any concerns, please contact your Bank</p>"		
				+"</body>";


		List<Notification> billerNotifications=notificationService
				.getNotificationByBillerAndStatusId(mandate.getProduct().getBiller(), WebAppConstants.BILLER_REJECT_MANDATE);


		List<Notification> bankNotifications=notificationService
				.getNotificationByBankAndStatusId(mandate.getBank(), WebAppConstants.BANK_REJECT_MANDATE);


		List<String> ccEmails= new ArrayList<>();
		
		ccEmails.addAll(billerNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList()));
		ccEmails.addAll(bankNotifications.stream().map(e->e.getEmailAddress()).collect(Collectors.toList()));
		ccEmails.add(mandate.getAuthorizedBy().getEmail());
		
		helper.setTo(new InternetAddress(mandate.getEmail(), false));
		helper.setCc(getInternetAddresses(ccEmails));
		

		helper.setSubject(String.format("CMMS Notification Service – New Mandate Set-up Request on behalf of %s",
				mandate.getAccountName()));
		helper.setText(String.format(message, 
				mandate.getMandateCode(),
				mandate.getSubscriberCode(),
				mandate.getPayerName(),
				mandate.getProduct().getName(),
				mandate.getAmount().toPlainString(),
				mandate.getBank().getBankName(),
				mandate.getAccountName(),
				mandate.getStartDate().toString(),
				mandate.getEndDate().toString()
				),true);
	}

	
	
	
	
	private InternetAddress[] getInternetAddresses(List<String> emails){
		List<InternetAddress> intAddresses = emails.stream().filter( s -> s != null)
				.map( e -> { 
					try {
						return new InternetAddress(e,false);
					} catch( Exception x) {
						logger.error(null,x);
						return null;
					}
				}).collect(Collectors.toList());

		return intAddresses.toArray( new InternetAddress[0]);
	}
}
