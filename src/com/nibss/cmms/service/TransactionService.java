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
import com.nibss.cmms.domain.Transaction;
import com.nibss.cmms.domain.TransactionParam;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;

@Service
public class TransactionService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}
	
	
	public Transaction saveTransaction(Transaction t) throws Exception{
		return genericDAO.insert(t);
	}
	
	public TransactionParam saveTransactionParam(TransactionParam t) throws Exception{
		return genericDAO.insert(t);
	}
	
	
	@Transactional(readOnly=true)
	public List<Transaction> getTransactions(final Map<String,Object> filters){
		String query="from Transaction m where 1=1 ";
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


	public void updateTransaction(Transaction t) {
		genericDAO.update(t);
	}
	
	public void updateTransactionParam(TransactionParam t) {
		genericDAO.update(t);
	}
	
	@Transactional(readOnly=true)
	public List<Transaction> getTransactions(DataFilter d, Object...param) throws ServerBusinessException{

		return genericDAO.getList(d,param);
	}


	public List<Transaction> findPaginatedTransactions(Map<String, Object> filters,Integer startIndex, Integer endIndex)  throws Exception{
		String query="from Transaction t where 1=1 ";
		 Map<String,Object> params = new HashMap<String,Object>();
		int i=0;
		
		if(filters!=null)
		for(Map.Entry<String, Object> mapE:filters.entrySet()){
			System.err.println(mapE.getKey());
			if(mapE.getKey().equals("dateCreated")){
				SimpleDateFormat df= new SimpleDateFormat("dd/MM/yyyy");
				int x=i;
				String[] paramValue=((String)mapE.getValue()).split("-");
				query=query.concat(" and t."+mapE.getKey()+" between ?"+(x)+" and ?"+(x+1));
				params.put(String.valueOf(x),df.parse(paramValue[0]));
				params.put(String.valueOf(x+1),df.parse(paramValue[1]));
				i=x+1;
			}else if(mapE.getKey().equals("status")){
				Object paramValue=((String)mapE.getValue());
				if(paramValue.equals(WebAppConstants.PAYMENT_FAILED)){
					//for failed and reversed transactions
					query=query.concat(" and t."+mapE.getKey()+" in ?"+i);
					paramValue= new int[]{WebAppConstants.PAYMENT_FAILED,WebAppConstants.PAYMENT_REVERSED};
				}else if(paramValue.equals(WebAppConstants.PAYMENT_SUCCESSFUL)){
					query=query.concat(" and t."+mapE.getKey()+" = ?"+i);
				}
				params.put(String.valueOf(i),paramValue);
			}else{
			query=query.concat(" and t."+mapE.getKey()+" = ?"+i);
			params.put(String.valueOf(i),mapE.getValue());
			}
			i++;
		}
		query=query.concat(" order by t.id desc");
		return genericDAO.getPaginatedList(query, params, startIndex, endIndex);
	}
}
