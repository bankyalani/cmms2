package com.nibss.cmms.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class HibernateDAOImpl implements GenericDAO {

	@Autowired
	protected SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
		// return sessionFactory.openSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T> T getByID(long id, Class<T> clazz) {
		return (T) getCurrentSession().get(clazz, id);

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T> List<T> getList(Class<T> clazz) {
		return (List<T>) getCurrentSession().createQuery(
				"from " + clazz.getName()).list();

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T> List<T> getList(DataFilter dataFilter,
			Object... a) {

		Query queryObject = getCurrentSession().createQuery(
				dataFilter.applyFilter());

		for(int i=0;i<a.length;i++){	

			queryObject.setParameter(i, a[i]);

		}
		return (List<T>) queryObject.list();

	}


	/**For JPA named params**/
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public  <T> List<T> getPaginatedList(String hql,Map<String,Object> params,Integer startIndex, Integer endIndex){
		Query query = getCurrentSession().createQuery(hql);
		if (params!=null) {
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				Object temp= params.get(key);
				if (temp instanceof Long[] ||temp instanceof String[] || temp instanceof int[]) {
					
					query.setParameterList(key, (Object[]) temp);
				}
				else
					query.setParameter(key, params.get(key));
			}
		}
		if(startIndex!=null && endIndex!=null){
			query.setFirstResult(startIndex);
			query.setMaxResults(endIndex);
		}
		return (List<T>)query.list(); 
	}

	
	

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T> List<T> getNamedList(Class<T> clazz, DataFilter dataFilter,
			Object... a) {

		Query queryObject = getCurrentSession().createQuery(
				dataFilter.applyFilter());

		if (null != a && a.length > 0) {

			for (int i = 0; i < a.length; i++) {

				if (a[i] instanceof Long[] || a[i] instanceof String[]) {
					queryObject.setParameterList("item"+(i+1), (Object[])a[i]);
				} else
					queryObject.setParameter("item"+(i+1), a[i]);
			}
		}
		return (List<T>) queryObject.list();

	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public <T> List<T> search(Map<String, Object> parameterMap, Class<T> clazz) {
		Criteria criteria = getCurrentSession().createCriteria(clazz);
		Set<String> fieldName = parameterMap.keySet();
		for (String field : fieldName) {
			criteria.add(Restrictions.ilike(field, parameterMap.get(field)));
		}
		return (List<T>) criteria.list();
	}

	@Override
	public <T> T insert(T t) throws Exception {
		getCurrentSession().save(t);
		getCurrentSession().flush();
		return t;
	}

	@Override
	public <T> void update(T t) {
		getCurrentSession().update(t);
	}

	@Override
	public <T> void delete(T entity) {
		getCurrentSession().delete(entity);
	}

	@Override
	public <T> void deleteById(long id, Class<T> clazz) {
		Object entity = getCurrentSession().get(clazz, id);
		getCurrentSession().delete(entity);
	} 

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public <T> List<T> getByNativeQuery(String s) {
		return (List<T>) getCurrentSession().createSQLQuery(s).list();
	}
	public <T> Integer bulkUpdate(Class<T> clazz, Map<String,Object> params, Map<String,Object> where) {
		Object[] a= new Object[params.size()+where.size()];
		String hql="UPDATE "+clazz.getSimpleName()+" set";
		int i=0;
		if(params!=null){
			for(Map.Entry<String, Object> mapE:params.entrySet()){
				hql=hql.concat(String.format(" %s=:item%d,",mapE.getKey(),i));
				a[i]=mapE.getValue();
				i++;
			}
		}
		if(hql.endsWith(","))
			hql=hql.substring(0, hql.length()-1);

		if(where!=null){
			hql=hql.concat(" where");
			for(Map.Entry<String, Object> mapE:where.entrySet()){
				hql=hql.concat(String.format(" %s=:item%d and",mapE.getKey(),i));
				a[i]=mapE.getValue();
				i++;
			}
		}
		if(hql.endsWith("and"))
			hql=hql.substring(0, hql.length()-3);
		//System.err.println(hql);
		Query queryObject = getCurrentSession().createQuery(hql);
		if (null != a && a.length > 0) {
			for (int j = 0; j < a.length; j++) {
				if (a[j] instanceof Long[]) {
					queryObject.setParameterList("item"+(j), (Long[])a[j]);
				} else if(a[j] instanceof String[]){
					queryObject.setParameterList("item"+(j), (String[])a[j]);
				}else
					queryObject.setParameter("item"+(j), a[j]);
			}
		}

		return queryObject.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUniqueObject(Class<T> clazz, DataFilter dataFilter,
			Object... a) {
		Query queryObject = getCurrentSession().createQuery(
				dataFilter.applyFilter());

		if (null != a && a.length > 0) {
			for (int i = 0; i < a.length; i++)
				queryObject.setParameter(i, a[i]);
		}
		return (T) queryObject.uniqueResult();
	}

	@Override
	public <T> int update(DataFilter dataFilter, Object... a) {
		Query queryObject = getCurrentSession().createQuery(
				dataFilter.applyFilter());

		if (null != a && a.length > 0) {
			for (int i = 0; i < a.length; i++)
				queryObject.setParameter(i, a[i]);
		}
		return queryObject.executeUpdate();
	}
	
	public static int count(String word, Character ch)  
	{  
		int pos = word.indexOf(ch);  
		return pos == -1 ? 0 : 1 + count(word.substring(pos+1),ch);  
	} 

}