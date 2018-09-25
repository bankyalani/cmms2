/**
 * 
 */
package com.nibss.cmms.service;



import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.TransactionResponse;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;


@Service
@Transactional
public class TransactionResponseService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Log logger = LogFactory.getLog(TransactionResponseService.class);


	@Transactional(readOnly=true)
	public List<TransactionResponse> getTransactionResponse() throws ServerBusinessException{

		//return genericDAO.getList(TransactionResponse.class );
		List<TransactionResponse> transactionResponses= new ArrayList<>();

		transactionResponses= genericDAO.getList(() -> "from TransactionResponse p WHERE p.id>?", 
				0L);
		
		return transactionResponses;
		
	}

	@Transactional(readOnly=true)
	public TransactionResponse getTransactionResponseById( Long transactionResponseId ) throws ServerBusinessException{

		return genericDAO.getByID(transactionResponseId, TransactionResponse.class );

	}


	@Transactional(readOnly=true)
	public TransactionResponse getTransactionResponseByResponseCode( String responseCode ) throws ServerBusinessException{

		List<TransactionResponse> transactionResponses= new ArrayList<>();

		transactionResponses= genericDAO.getList(() -> "from TransactionResponse p WHERE p.responseCode=?", 
				responseCode);
		if (!transactionResponses.isEmpty())
			return transactionResponses.get(0);
		else 
			return null;

	}
	
	

	




}
