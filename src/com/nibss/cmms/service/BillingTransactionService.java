package com.nibss.cmms.service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.DataFilter;
import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.BillingTransaction;
import com.nibss.cmms.domain.Transaction;
import com.nibss.cmms.domain.TransactionParam;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;

@Service
public class BillingTransactionService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}
	
	
	public BillingTransaction saveBillingTransaction(BillingTransaction b) throws Exception{
		return genericDAO.insert(b);
	}
	

	
	@Transactional(readOnly=true)
	public List<BillingTransaction> getBillingTransactions(final Map<String,Object> filters){
		String query="from BillingTransaction m where 1=1 ";
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


	public void updateBillingTransaction(BillingTransaction bt) {
		genericDAO.update(bt);
	}
	

	
	@Transactional(readOnly=true)
	public List<BillingTransaction> getBillingTransaction(DataFilter d, Object...param) throws ServerBusinessException{

		return genericDAO.getList(d,param);
	}


}
