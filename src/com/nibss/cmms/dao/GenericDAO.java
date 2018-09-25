package com.nibss.cmms.dao;

import java.util.List;
import java.util.Map;
 
public interface GenericDAO {
 
    <T> T getByID(long id, Class<T> clazz);
 
    <T> List<T> search(Map<String, Object> parameterMap, Class<T> clazz);
 
    <T> T insert(T t) throws Exception;
 
    <T> void update(T t);
    
    <T> int update(DataFilter dataFilter,Object...objects);
 
    <T> void delete(T entity);
 
    <T> List<T> getByNativeQuery(String s);
    
    <T> void deleteById(long id, Class<T> clazz);

	<T> List<T> getList(Class<T> clazz);

	<T> List<T> getList(DataFilter dataFilter, Object... objects);
	
	<T> T getUniqueObject(Class<T> clazz,DataFilter dataFilter, Object... objects);

	<T> List<T> getNamedList(Class<T> clazz, DataFilter dataFilter, Object... objects);

	<T> List<T> getPaginatedList(String hql, Map<String, Object> params, Integer startIndex, Integer endIndex);

	<T> Integer bulkUpdate(Class<T> clazz, Map<String,Object> params,Map<String,Object> where);
	
	

	 

 
}