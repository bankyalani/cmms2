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
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;


@Service
@Transactional
public class UserService  {

	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}

	private static final Log logger = LogFactory.getLog( UserService.class );


	@Transactional(readOnly=true)
	public User getUserById( Long userId ) throws ServerBusinessException{

		return genericDAO.getByID(userId, User.class );

	}

	public void updateUser(User user) throws ServerBusinessException{
		genericDAO.update(user);

	}
	
	public void deletUser(User user) throws ServerBusinessException{
		genericDAO.delete(user);

	}
	
	@Transactional(readOnly=true)
	public User getUserByEmail(String email) throws ServerBusinessException{
		return genericDAO.getUniqueObject(User.class, ()->"from User where email=?", email);
	}
	
	@Transactional(readOnly=true)
	public User getUserByEmailAndPassword(String email,String password) throws ServerBusinessException{
		return genericDAO.getUniqueObject(User.class, ()->"from User where email=? and password=?", email,password);
	}

	public User addUser(User user) throws Exception {
		//hash the password before saving
		
		return genericDAO.insert(user);
		
	}
	
	
	@Transactional(readOnly=true)
	public List<User> findPaginatedUser(Map<String, Object> filters,
		Integer startIndex, Integer endIndex) {
		String query="from User u where 1=1 ";
		 Map<String,Object> params = new HashMap<String,Object>();
		int i=0;
		
		if(filters!=null)
		for(Map.Entry<String, Object> mapE:filters.entrySet()){
			
			if(mapE.getKey().equals("email")){
				query=query.concat(" and u."+mapE.getKey()+"  like :item"+i+")");
				params.put("item"+i, "%"+mapE.getValue()+"%");
			}else{
				query=query.concat(" and u."+mapE.getKey()+" = :item"+i);
				params.put("item"+i,mapE.getValue());
			}
			i++;
		}
		
		
		query=query.concat(" order by u.id desc");
		return genericDAO.getPaginatedList(query, params, startIndex, endIndex);
	}
	
	
	@Transactional(readOnly=true)
	public List<User> findPaginatedBillerUser(Map<String, Object> filters,
			Integer startIndex, Integer endIndex) throws ServerBusinessException {
		String query="from BillerUser u where 1=1 ";
		 Map<String,Object> params = new HashMap<String,Object>();
		int i=0;
		
		if(filters!=null)
		for(Map.Entry<String, Object> mapE:filters.entrySet()){
			query+=String.format(" and u.%s=%d",mapE.getKey(),i);
			params.put(String.valueOf(i),mapE.getValue());
			i++;
		}
		query=query.concat(" order by u.id desc");
		return genericDAO.getPaginatedList(query, params, startIndex, endIndex);
	}
	
	

	@Transactional(readOnly=true)
	public List<User> getAllUsers() throws ServerBusinessException{
		
		return genericDAO.getList(User.class);
	}
	
	
	
	
	

}
