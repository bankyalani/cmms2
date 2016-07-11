package com.nibss.cmms.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.Product;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.service.BillerService;
import com.nibss.cmms.service.ProductService;
import com.nibss.cmms.utils.OptionBean;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;
import com.nibss.cmms.web.converters.BankEditor;
import com.nibss.cmms.web.validation.ProductValidator;


@Controller
@RequestMapping("/product")

public class ProductController implements WebAppConstants{
	
		
	private static final Log logger = LogFactory.getLog( ProductController.class );
	

	@Autowired
	private BillerService billerUserService;
	
	@Autowired
	private BankService bankService;
		
	@Autowired
	private ProductService productService;
	
	
	@Autowired
	private ProductValidator productValidator;
	

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(productValidator);
	}
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Bank.class, new BankEditor(bankService));
	}


	
	protected BillerUser getCurrentBillerUser(HttpServletRequest request){
		return (BillerUser)request.getSession().getAttribute(CURRENT_BILLER_USER);
	}
	
	
	@RequestMapping(value = "/getActiveProducts/{billerId}", method = RequestMethod.GET, produces={"application/json"})
	public @ResponseBody List<OptionBean> getActiveBillerProducts(@PathVariable(value = "billerId") Long billerId, HttpServletRequest request) {
		List<Product> products = new ArrayList<>();

		try {
			products=productService.getProductsByBillerId(billerId,STATUS_ACTIVE);
		} catch (ServerBusinessException e) {
			logger.error("Error occurred getting products",e);
		}
		
		return products.stream().map(p->
			new OptionBean(String.valueOf(p.getId()),p.getName())).collect(Collectors.toList());
		
	}
	
	
	@RequestMapping(value = "/getAllProducts/{billerId}", method = RequestMethod.GET, produces={"application/json"})
	public @ResponseBody List<OptionBean> getAllBillerProducts(@PathVariable(value = "billerId") Long billerId, HttpServletRequest request) {
		List<Product> products = new ArrayList<>();

		try {
			products=productService.getProductsByBillerId(billerId);
		} catch (ServerBusinessException e) {
			logger.error("Error occurred getting products",e);
		}
		
		return products.stream().map(p->
			new OptionBean(String.valueOf(p.getId()),p.getName())).collect(Collectors.toList());
		
	}
	
	@RequestMapping(value = "/getProductAmount/{productId}", method = RequestMethod.GET, produces={"application/json"})
	public @ResponseBody String getProductAmount(@PathVariable(value = "productId") Long productId, HttpServletRequest request) {
		String amount=null;
		Product product = null;

		try {
			product=productService.getProductById(productId);
			amount=product.getAmount().toPlainString();
		} catch (ServerBusinessException e) {
			logger.error("Error occurred getting products",e);
		}
		
		return amount;
		
	}
}
