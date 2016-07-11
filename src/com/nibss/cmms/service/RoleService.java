/**
 * 
 */
package com.nibss.cmms.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.Role;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;


@Service
@Transactional
public class RoleService  {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Log logger = LogFactory.getLog( RoleService.class );


	@Transactional(readOnly=true)
	public Role getRoleById( Long roleId ) throws ServerBusinessException{

		return genericDAO.getByID(roleId, Role.class );

	}
	
	@Transactional(readOnly=true)
	public List<Role> getRolesByName(String[] roleNames){
		return genericDAO.getNamedList(Role.class, 
				() -> "from Role p WHERE 1=:item1 and concat('ROLE_', upper(replace(p.name, ' ', '_')) ) in (:item2) ",
				1,roleNames);
	}


	@Transactional(readOnly=true)
	public List<Role> getAllRoles(){
		return genericDAO.getList(Role.class);
	}
	

}
