/**
 * 
 */
package com.nibss.cmms.service;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.DataFilter;
import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.RejectionReason;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;


@Service
public class MandateService  {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Logger logger = Logger.getLogger( MandateService.class );


	@Transactional(readOnly=true)
	public Mandate getMandateByMandateId( Long mandateId ) throws ServerBusinessException{

		return genericDAO.getByID(mandateId, Mandate.class );

	}

	@Transactional(readOnly=true)
	public Mandate getMandateByMandateCode( String mandateCode ) throws ServerBusinessException{

		return genericDAO.getUniqueObject(Mandate.class, 
				() -> "from Mandate p WHERE p.mandateCode=?",
				mandateCode);
	}

	@Transactional(readOnly=true)
	public Mandate getMandateByMandateCode(Long billerId, String mandateCode) throws ServerBusinessException{

		return genericDAO.getUniqueObject(Mandate.class, 
				() -> "from Mandate p WHERE p.mandateCode=? and p.product.biller.id=?",
				mandateCode,billerId);
	}


	@Transactional(readOnly=true)
	public Mandate getMandateByBillerAndMandateId( Long id, Biller biller ) throws ServerBusinessException{
		return genericDAO.getUniqueObject(Mandate.class, 
				() -> "from Mandate p WHERE p.id=? and p.product.biller=?",
				id,biller);

	}


	@Transactional(readOnly=true)
	public Mandate getMandateByBankAndMandateId( Long id, Bank bank ) throws ServerBusinessException{
		return genericDAO.getUniqueObject(Mandate.class, 
				() -> "from Mandate p WHERE p.id=? and (p.bank=? or p.product.biller.bank=?)",
				id,bank,bank);

	}


	@Transactional(readOnly=true)
	public List<Mandate> getMandatesByBiller( Biller biller ) throws ServerBusinessException{

		return genericDAO.getList(() -> "from Mandate p WHERE p.product.biller=? order by p.id desc",
				biller);

	}

	@Transactional(readOnly=true)
	public List<Mandate> getMandatesByBiller( Biller biller, int startIndex, int endIndex) throws ServerBusinessException{

		String query="from Mandate m where m.product.biller=?0 order by m.id desc";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put(String.valueOf(0), biller);

		return genericDAO.getPaginatedList(query, params, startIndex, endIndex);

	}


	@Transactional(readOnly=true)
	public List<Mandate> getMandatesByBank( Bank bank, int startIndex, int endIndex) throws ServerBusinessException{

		String query="from Mandate m where m.bank=?0 order by m.id desc";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put(String.valueOf(0), bank);

		return genericDAO.getPaginatedList(query, params, startIndex, endIndex);

	}

	@Transactional(readOnly=true)
	public List<Mandate> getMandates(DataFilter d, Object...param) throws ServerBusinessException{

		return genericDAO.getList(d,param);
	}


	@Transactional(readOnly=true)
	public List<Mandate> findPaginatedMandates(Map<String,Object> filters, Integer startIndex, Integer endIndex)  throws ServerBusinessException{

		String query="from Mandate m where requestStatus<> "+WebAppConstants.STATUS_MANDATE_DELETED;
		Map<String,Object> params = new HashMap<String,Object>();
		int i=0;

		if(filters!=null)
			for(Map.Entry<String, Object> mapE:filters.entrySet()){
				if(mapE.getKey().equals("dateCreated") || mapE.getKey().equals("dateApproved")|| mapE.getKey().equals("nextDebitDate")){
					int x=i;
					try {
						SimpleDateFormat df= new SimpleDateFormat("dd/MM/yyyy");
						String[] paramValue=((String)mapE.getValue()).split("-");
						query+=(" and m."+mapE.getKey()+">= ?"+(x)+" and m."+mapE.getKey()+" <= ?"+(x+1));
						params.put(String.valueOf(x),df.parse(paramValue[0]));
						Calendar c = new GregorianCalendar();
						c.setTime(df.parse(paramValue[1]));
						c.set(Calendar.HOUR_OF_DAY, 23);
						c.set(Calendar.MINUTE, 59);
						c.set(Calendar.SECOND, 59);
						params.put(String.valueOf(x+1),c.getTime());
						i=x+1;
					} catch (ParseException e) {
						logger.error(null,e);
					}
					i=x+1;
					
				}else if(mapE.getKey().equals("status.id")){
					query=query.concat(" and m."+mapE.getKey()+" in (:item"+i+")");
					params.put("item"+i, mapE.getValue());
				}else if (mapE.getKey().equals("fixedAmountMandate")){
					query=query.concat(" and m."+mapE.getKey()+" in (:item"+i+")");
					params.put("item"+i, Boolean.valueOf((String) mapE.getValue()));
				}else{
					query=query.concat(" and m."+mapE.getKey()+" = :item"+i);
					params.put("item"+i,mapE.getValue());
				}
				i++;
			}
		query=query.concat(" order by m.id desc");
		return genericDAO.getPaginatedList(query, params, startIndex, endIndex);
	}




	@Transactional(readOnly=true)
	public List<Mandate> getMandatesByBiller( Biller biller, Long status ) throws ServerBusinessException{

		return genericDAO.getList(() ->"from Mandate p WHERE p.product.biller=? and p.status.id=?",
				biller,status);

	}


	@Transactional(readOnly=true)
	public List<Mandate> getMandatesByBank( Bank bank, Long[] statuses ) throws ServerBusinessException{

		return genericDAO.getNamedList(Mandate.class, 
				() -> "from Mandate p WHERE p.bank=:item1 and p.status.id in (:item2)",
				bank,statuses);

	}

	@Transactional(readOnly=true)
	public List<Mandate> getMandatesByBank( Bank bank) throws ServerBusinessException{

		return genericDAO.getList(() -> "from Mandate p WHERE p.bank=?",
				bank);

	}


	@Transactional(readOnly=true)
	public List<Mandate> getMandatesByRecruitedBillers( Bank bank) throws ServerBusinessException{

		return genericDAO.getList(() -> "from Mandate p WHERE p.product.biller.bank=? and p.bank!=p.product.biller.bank",
				bank);

	}

	@Transactional(readOnly=true)
	public List<Mandate> getMandatesByBank( Bank bank, Long status ) throws ServerBusinessException{

		return genericDAO.getList(() -> "from Mandate p WHERE p.bank=? and p.status.id=?",
				bank,status);

	}


	@Transactional(readOnly=true)
	public List<Mandate> getAllMandatesByBank( Bank bank) throws ServerBusinessException{

		return genericDAO.getList(() -> "from Mandate p WHERE p.bank=? order by p.id desc",
				bank);

	}

	@Transactional(readOnly=true)
	public List<Mandate> getAllMandates() throws ServerBusinessException{

		return genericDAO.getList(Mandate.class);

	}



	@Transactional
	public void modifyMandate( Mandate mandate ) throws ServerBusinessException{

		genericDAO.update(mandate);

	}


	public Mandate addMandate(Mandate mandate) throws ServerBusinessException{
		Mandate m=null;
		try {
			m= genericDAO.insert(mandate);
		} catch (Exception e) {
			throw new ServerBusinessException(0, "An error has occurred while saving mandate ["+mandate+"]",e);
		}

		return m;

	}

	public void updateMandates(Map<String,Object> params,Map<String,Object> where){
		genericDAO.bulkUpdate(Mandate.class, params,where);
	}

	@Transactional(readOnly=true)
	public Bank getBankByBankCode( Long bankCode ) throws ServerBusinessException{

		return genericDAO.getByID(bankCode, Bank.class );

	}


	@Transactional(readOnly=true)
	public List<Bank> getAllBanks() throws ServerBusinessException{

		return genericDAO.getList(Bank.class);

	}


	@Transactional(readOnly=true)
	public List<RejectionReason> getRejectionReasons() throws ServerBusinessException{

		return genericDAO.getList(RejectionReason.class);

	}

	@Transactional(readOnly=true)
	public RejectionReason getRejectionReasonById(Long id) {
		return genericDAO.getByID(id, RejectionReason.class);
	}




	@Transactional(readOnly=true)
	public Mandate getMandateByBillerAndMandateId(Long id)  throws ServerBusinessException{
		return genericDAO.getByID(id, Mandate.class);
	}


	public int updateMandateRequest(DataFilter dataFilter, Object...param)  throws ServerBusinessException{
		return genericDAO.update(dataFilter, param);
	}

}
