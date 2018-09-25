/**
 * 
 */
package com.nibss.cmms.service;



import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.utils.StringUtils;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;


@Service
public class CommonService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Logger logger = LoggerFactory.getLogger( CommonService.class );


	@Transactional(readOnly=true)
	public synchronized String generateMandateCode(String billerNo, String productId) throws ServerBusinessException{

		List<BigInteger> objects=genericDAO.getByNativeQuery("SELECT IFNULL(max(id),1) FROM mandate_request");
		
		
		billerNo=StringUtils.lpad(billerNo, 7, "0");
		productId=StringUtils.lpad(productId, 3, "0");
		  if(objects!=null && objects.size()>0 && objects.get(0)!=null){
			return billerNo+"/"+productId+"/"+StringUtils.lpad(String.valueOf(objects.stream().findFirst().get()), 10, "0");
		}
		else return null;

	}


}
