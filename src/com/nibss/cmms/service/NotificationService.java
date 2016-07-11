package com.nibss.cmms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.common.CommonAppConstants;
import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.MandateStatus;
import com.nibss.cmms.domain.Notification;
import com.nibss.cmms.domain.NotificationConfig;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;

@Service
public class NotificationService implements CommonAppConstants {

	private static final Log logger = LogFactory.getLog( NotificationService.class );


	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}
	
	public Notification addNewNotification(Notification notification) throws Exception{
		return genericDAO.insert(notification);
	}
	
	
	public NotificationConfig getConfigByMandateStatus(MandateStatus status) throws ServerBusinessException{
		
		return genericDAO.getUniqueObject(NotificationConfig.class, 
				() -> "from NotificationConfig n WHERE n.mandateStatus=?",
				status);
		
	}
	
	@Transactional(readOnly=true)
	public List<Notification> getNotificationByBillerAndStatus(Biller biller, MandateStatus status) throws ServerBusinessException{
		return genericDAO.getList(() -> "from Notification n WHERE n.mandateStatus=? and n.biller=?",
				status,biller);
	}
	
	@Transactional(readOnly=true)
	public List<Notification> getNotificationByBillerAndStatusId(Biller biller, Long statusId) throws ServerBusinessException{
		return genericDAO.getList(() -> "from BillerNotification n WHERE n.mandateStatus.id=? and n.biller=?",
				statusId,biller);
	}
	
	@Transactional(readOnly=true)
	public List<Notification> getNotificationByBankAndStatusId(Bank bank, Long statusId) throws ServerBusinessException{
		return genericDAO.getList(() -> "from BankNotification n WHERE n.mandateStatus.id=? and n.bank=?",
				statusId,bank);
	}
	
	@Transactional(readOnly=true)
	public List<Notification> getNotificationByBiller(Biller biller) throws ServerBusinessException{
		return genericDAO.getList(() -> "from Notification n WHERE n.biller=? order by n.mandateStatus desc",
				biller);
	}
	
	
	@Transactional(readOnly=true)
	public Notification getNotificationById(Long id) throws ServerBusinessException{
		return genericDAO.getByID(id,Notification.class);
	}
	
	@Transactional(readOnly=true)
	public List<NotificationConfig> getNotificationConfigs() throws ServerBusinessException{
		return genericDAO.getList(NotificationConfig.class);
	}
	
	@Transactional(readOnly=true)
	public NotificationConfig getNotificationConfigById(Long id) throws ServerBusinessException{
		return genericDAO.getByID(id,NotificationConfig.class);
	}

	
	public void updateNotificationMaxEmailConfig(NotificationConfig nConfig) {
		
		genericDAO.update(nConfig);
	
	}
	
	
	public void updateNotification(Notification n) {
		
		genericDAO.update(n);
	
	}

	
	public void deleteNotification(Notification notification) {
		
		genericDAO.delete(notification);
		
	}

	public Notification createNewNotification(Notification notification) throws Exception {
		return genericDAO.insert(notification);
	}
	
	@Transactional(readOnly=true)
	public List<Notification> findPaginatedNotifications(Map<String, Object> filters,Integer startIndex, Integer endIndex) throws ServerBusinessException {
		String query="from Notification b where 1=1 ";
		Map<String,Object> params = new HashMap<String,Object>();
		int i=0;

		if(filters!=null)
			for(Map.Entry<String, Object> mapE:filters.entrySet()){
				query=query.concat(" and b."+mapE.getKey()+" = ?"+i);
				params.put(String.valueOf(i),mapE.getValue());
				i++;
			}
		query=query.concat(" order by b.id desc");
		return genericDAO.getPaginatedList(query, params, startIndex, endIndex);
	}


}
