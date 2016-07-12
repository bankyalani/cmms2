package com.nibss.cmms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.DataFilter;
import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.WebserviceNotification;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;

@Service
public class WebserviceNotificationService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}
	
	
	public WebserviceNotification saveWebserviceNotification(WebserviceNotification b) throws Exception{
		return genericDAO.insert(b);
	}
	

	
	@Transactional(readOnly=true)
	public List<WebserviceNotification> getWebserviceNotifications(final Map<String,Object> filters){
		String query="from WebserviceNotification m where 1=1 ";
		Map<String,Object> params = new HashMap<String,Object>();
		int i=0;
		
		if(filters!=null)
		for(Map.Entry<String, Object> mapE:filters.entrySet()){
			query=query.concat(" and m."+mapE.getKey()+" = ?"+i);
			params.put(String.valueOf(i),mapE.getValue());
			i++;
		}
		query=query.concat(" order by m.id desc");
		return genericDAO.getPaginatedList(query, params, null, null);
	}


	public void updateWebserviceNotification(WebserviceNotification bt) {
		genericDAO.update(bt);
	}
	

	
	@Transactional(readOnly=true)
	public List<WebserviceNotification> getWebserviceNotifications(DataFilter d, Object...param) throws ServerBusinessException{

		return genericDAO.getList(d,param);
	}


}
