/**
 * 
 */
package com.nibss.cmms.service;



import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.MandateStatus;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;


@Service
public class MandateStatusService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Log logger = LogFactory.getLog( MandateStatusService.class );


	@Transactional(readOnly=true)
	public MandateStatus getMandateStatusById( Long statusId ) throws ServerBusinessException{
		return genericDAO.getByID(statusId, MandateStatus.class );

	}
	
	@Transactional(readOnly=true)
	public List<MandateStatus> getMandateStatuses(Long[] statuses ) throws ServerBusinessException{
		return genericDAO.getNamedList(MandateStatus.class, 
				() -> "from MandateStatus p WHERE 1=:item1 and p.id in (:item2)",
				1,statuses);
	}
	
	@Transactional(readOnly=true)
	public List<MandateStatus> getAllMandateStatus() throws ServerBusinessException{

		return genericDAO.getList(MandateStatus.class );
	}
}
