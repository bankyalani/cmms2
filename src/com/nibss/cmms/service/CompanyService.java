/**
 * 
 */
package com.nibss.cmms.service;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.Company;
import com.nibss.cmms.domain.Industry;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;


@Service
@Transactional
public class CompanyService {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Log logger = LogFactory.getLog( CompanyService.class );


	@Transactional(readOnly=true)
	public List<Company> getAvailableCompanies() throws ServerBusinessException{
		List<Company> companies= new ArrayList<>();
		
		companies= genericDAO.getList(()-> "from Company c where c.id not in (select b.id from Biller b)");
		return companies;

	}	
	
	@Transactional(readOnly=true)
	public List<Company> getAvailableCompanies(String companyName) throws ServerBusinessException{
		List<Company> companies= new ArrayList<>();
		
		companies= genericDAO.getList(() -> "from Company c where c not in (select b.company from Biller b) and c.name like ?","%"+companyName+"%");
		return companies;

	}

	@Transactional(readOnly=true)
	public Company getCompanyById(Long id) {
		return genericDAO.getByID(id, Company.class);
	}
	
	@Transactional(readOnly=true)
	public Industry getIndustryById(Long id) {
		return genericDAO.getByID(id, Industry.class);
	}

	@Transactional(readOnly=true)
	public List<Company> getAllCompanies()  throws ServerBusinessException {
		//return genericDAO.getList(Company.class);
		
		return genericDAO.getList(()->" from Company b order by b.name");
	}

	public Company addNewCompany(Company company) throws Exception {
		return genericDAO.insert(company);
	}
	
	@Transactional(readOnly=true)
	public List<Industry> getCategories(){
		return genericDAO.getList(Industry.class);
	}

	public List<Company> findPaginatedCompanies(Map<String, Object> filters,Integer startIndex, Integer endIndex) {
		String query="from Company c where 1=1 ";
		Map<String,Object> params = new HashMap<String,Object>();
		int i=0;

		if(filters!=null)
			for(Map.Entry<String, Object> mapE:filters.entrySet()){
				query=query.concat(" and c."+mapE.getKey()+" = ?"+i);
				params.put(String.valueOf(i),mapE.getValue());
				i++;
			}
		query=query.concat(" order by c.id desc");
		return genericDAO.getPaginatedList(query, params, startIndex, endIndex);
	}

	
	


}
