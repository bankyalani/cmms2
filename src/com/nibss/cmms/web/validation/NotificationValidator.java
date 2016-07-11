package com.nibss.cmms.web.validation;


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.nibss.cmms.domain.BankNotification;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.BillerNotification;
import com.nibss.cmms.domain.MandateStatus;
import com.nibss.cmms.domain.Notification;
import com.nibss.cmms.domain.NotificationConfig;
import com.nibss.cmms.service.NotificationService;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;

@Component
public class NotificationValidator implements Validator, WebAppConstants {
	private static final Logger logger = Logger.getLogger(NotificationValidator.class);

	@Autowired
	private NotificationService notificationService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Notification.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		logger.info("Doing notification config Validation...");

		Notification notification=(Notification)target;

		if(notification.getEmailAddress()==null || notification.getEmailAddress().equals("")){
			errors.reject("emailAddress","Email Address is required");
		}

		Biller formBiller= ((BillerNotification)notification).getBiller();
		
		if(formBiller==null){
			errors.reject("biller","Biller is required");
		}
		
		
			
			MandateStatus formMandateStatus=notification.getMandateStatus();

			if(formMandateStatus!=null){

				try {
					NotificationConfig config = notificationService.getConfigByMandateStatus(formMandateStatus);
					int allowedMaxUser=0;
					if (notification instanceof BillerNotification){
						allowedMaxUser=config.getBillerAllowedCount();
					}else if (notification instanceof BankNotification){
						allowedMaxUser=config.getBankAllowedCount();
					}
					
					List<Notification> currentBillerConfigs=notificationService
							.getNotificationByBillerAndStatus(formBiller, formMandateStatus);

					if (currentBillerConfigs!=null && currentBillerConfigs.size()>=allowedMaxUser){
						errors.reject("allowedUser",String.format("Maximum number of allowed emails reached. Allowed(%d)",allowedMaxUser));
					}

				} catch (ServerBusinessException e) {
					logger.error(e);
					errors.reject("allowedUser","Unabled to getConfigByMandateStatus");
				}
			}
	}

}
