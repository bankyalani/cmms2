/**
 * 
 */
package com.nibss.cmms.service;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;


@Service
@Transactional
public class BillerService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Log logger = LogFactory.getLog( BillerService.class );


	@Transactional(readOnly=true)
	public BillerUser getBillerUserById( Long userId ) throws ServerBusinessException{
		return genericDAO.getUniqueObject(BillerUser.class, 
				() -> "from BillerUser p WHERE p.id=?",
				userId);

		//return genericDAO.getByID(userId, BillerUser.class );

	}

	@Transactional(readOnly=true)
	public List<User> getUsersByBiller( Biller biller ) throws ServerBusinessException{

		return genericDAO.getList(()->"from User p where p.biller=?", biller);

	}

	@Transactional(readOnly=true)
	public BillerUser getUserByBillerAndUserId(Long id, Biller biller) {
		return genericDAO.getUniqueObject(BillerUser.class, 
				() -> "from BillerUser p WHERE p.user.id=? and p.biller=?",
				id,biller);
	}



	public BillerUser addNewBillerUser(BillerUser billerUser) throws Exception {
		return genericDAO.insert(billerUser);
	}


	public Biller addNewBiller(Biller biller) throws Exception {
		return genericDAO.insert(biller);
	}


	@Transactional(readOnly=true)
	public List<Biller> getBillerByBank(Bank bank) throws ServerBusinessException {
		return genericDAO.getList(() -> "from Biller p WHERE p.bank=?",
				bank);
	}

	@Transactional(readOnly=true)
	public List<Biller> getActiveBillers() {
		return genericDAO.getList(() -> "from Biller p where p.status=?",
				1);
	}

	@Transactional(readOnly=true)
	public List<Bank> getUnMappedCollectingBanks(Long Id) {
		return genericDAO.getList(() -> "from Bank b where b.bankCode not in (select p.bank from Account p where p.product.id=?)",
				Id);
	}


	@Transactional(readOnly=true)
	public Biller getBillerById(Long id) {
		return genericDAO.getByID(id, Biller.class);
	}


	@Transactional(readOnly=true)
	public List<Biller> getAllBillers()  throws ServerBusinessException {
		return genericDAO.getList(Biller.class);
	}

	public List<User> getBillerAdmins(Biller biller) {
		return genericDAO.getList(()-> "from BillerUser u where u.biller=? and u.role.id=?",
				biller,6L);
	}

	public List<Biller> findPaginatedBillers(Map<String, Object> filters,Integer startIndex, Integer endIndex) {
		String query="from Biller b where 1=1 ";
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
