/**
 *
 */
package com.nibss.cmms.service;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nibss.cmms.common.CommonAppConstants;
import com.nibss.cmms.dao.GenericDAO;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.Product;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;


@Service
public class ProductService implements CommonAppConstants {

	private static final Log logger = LogFactory.getLog( ProductService.class );


	private GenericDAO genericDAO;

	@Inject
	public void setGenericDAO(final GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}




	@Transactional(readOnly=true)
	public List<Product> getProductsByBiller( Biller biller ) throws ServerBusinessException{
		return genericDAO.getList( 
				() -> "from Product p WHERE p.biller.id=?", 
				biller.getId());
		//return genericDAO.search(m, Product.class);

	}

	@Transactional(readOnly=true)
	public Product getProductsByBillerAndProductId( Biller biller, Long productId ) throws ServerBusinessException{
		return genericDAO.getUniqueObject(Product.class, 
				() -> "from Product p WHERE p.biller.id=? and p.id=?", 
				biller.getId(), productId);
	}


	@Transactional(readOnly=true)
	public List<Product> getProductsByBillerId( Long  billerId ) throws ServerBusinessException{
		return genericDAO.getList(
				() -> "from Product p WHERE p.biller.id=?", 
				billerId);
		//return genericDAO.search(m, Product.class);

	}

	@Transactional(readOnly=true)
	public List<Product> getProductsByBillerId( Long  billerId, int status) throws ServerBusinessException{
		return genericDAO.getList(
				() -> "from Product p WHERE p.biller.id=? and p.status=?", 
				billerId,status);
		//return genericDAO.search(m, Product.class);

	}


	public void updateProduct(Product p) throws ServerBusinessException{
		genericDAO.update(p);

	}



	@Transactional(readOnly=true)
	public Product getProductById(Long id) throws ServerBusinessException{

		return genericDAO.getByID(id, Product.class);

	}


	@Transactional
	public Product addProduct(Product product) throws ServerBusinessException{
		Product p=null;
		try {
			p=genericDAO.insert(product);
		} catch (Exception e) {
			logger.error("Unable to save product",e);
		}
		return p;
	}




	public void deleteProduct(Long productId) throws Exception{
		genericDAO.deleteById(productId, Product.class);
	}








}
