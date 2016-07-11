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
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.BankUser;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;


@Service
@Transactional
public class BankService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Log logger = LogFactory.getLog( BankService.class );


	@Transactional(readOnly=true)
	public Bank getAllActiveBanks( Long userId ) throws ServerBusinessException{

		return genericDAO.getByID(userId, Bank.class );
		
	}

	@Transactional(readOnly=true)
	public Bank getBankById( Long bankId ) throws ServerBusinessException{

		return genericDAO.getByID(bankId, Bank.class );

	}


	@Transactional(readOnly=true)
	public Bank getBankByBankCode( String bankCode ) throws ServerBusinessException{

		List<Bank> banks= new ArrayList<>();

		banks= genericDAO.getList(() -> "from Bank p WHERE p.bankCode=?", 
				bankCode);
		if (!banks.isEmpty())
			return banks.get(0);
		else 
			return null;

	}
	
	

	
	
	@Transactional(readOnly=true)
	public BankUser getBankUserByUser( User user ) throws ServerBusinessException{

		return genericDAO.getUniqueObject(BankUser.class, ()->" from BankUser b where b.id=?",
				user.getId());
		
	}
	
	
	@Transactional(readOnly=true)
	public List<Bank> getBankByBillerAndMandateStatus(Biller biller,Long mandateStatus) throws ServerBusinessException{

		List<Bank> banks= new ArrayList<>();

		banks= genericDAO.getList(() -> "select distinct p from Bank p left join p.mandates m where m.status.id=? and m.biller=?", 
				mandateStatus,biller);
		return banks;

	}
	

	@Transactional(readOnly=true)
	public List<Bank> getAllBanks() throws ServerBusinessException{

		return genericDAO.getList(Bank.class);

	}
	
	

	public List<User> getUsersByBank(Bank bank)  throws ServerBusinessException{
		return genericDAO.getList(()->"from BankUser b where b.bank=?",
				bank);
	}

	public BankUser addNewBankUser(BankUser bankUser) throws Exception {
		return genericDAO.insert(bankUser);
	}

	public User getUserByBankAndUserId(Long id, Bank bank) {
		return genericDAO.getUniqueObject(User.class, 
				() -> "select p.user from BankUser p WHERE p.user.id=? and p.bank=?",
				id,bank);
	}

	public Biller addNewBiller(Biller biller) throws Exception {
		return genericDAO.insert(biller);
	}

	@Transactional(readOnly=true)
	public Biller getBankBillerById(Long id, Bank bank) throws ServerBusinessException {
		return genericDAO.getUniqueObject(Biller.class, 
				() -> "from Biller b where b.bank=? and b.id=?",
				bank,id);
	}

	




}
