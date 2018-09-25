package com.nibss.cmms.web.controllers;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nibss.cmms.domain.BankUser;
import com.nibss.cmms.domain.Role;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.service.CommonService;
import com.nibss.cmms.service.MandateService;
import com.nibss.cmms.service.MandateStatusService;
import com.nibss.cmms.service.ProductService;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;
import com.nibss.cmms.web.validation.MandateValidator;


@Controller
@RequestMapping("/search")
public class SearchController extends BaseController {

	private static final Logger logger = Logger.getLogger(SearchController.class);

	@Autowired
	private BankService bankService;

	@Autowired
	private MandateService mandateService;

	@Autowired
	private ProductService productService;

	@Autowired
	private MandateValidator mandateValidator;

	@Autowired
	private MandateStatusService mandateStatusService;

	@Autowired
	private CommonService commonService;



	@Override
	protected BankUser getCurrentBankUser(HttpServletRequest request){
		return (BankUser)request.getSession().getAttribute(WebAppConstants.CURRENT_BANK_USER);
	}


	@Override
	protected User getCurrentUser(HttpServletRequest request){
		return (User)request.getSession().getAttribute(WebAppConstants.CURRENT_USER_USER);
	}



	@RequestMapping(value="/mandate")
	public ModelAndView searchMandate(HttpServletRequest request, 
			@RequestParam(value="s_mandateCode", required=false) String mandateCode) throws ServerBusinessException{

		ModelAndView mav= new ModelAndView();

		HttpSession session= (HttpSession)request.getSession();
		User user=getCurrentUser(request);
		String querySuffix="";
		
		if(request.isUserInRole(WebAppConstants.ROLE_BILLER_INITIATOR) || request.isUserInRole(WebAppConstants.ROLE_BILLER_AUTHORIZER)){
			mav.setViewName("biller/list_mandate");
			querySuffix="biller.biller=";
			

		}else if (request.isUserInRole(WebAppConstants.ROLE_BANK_INITIATOR) || request.isUserInRole(WebAppConstants.ROLE_BANK_AUTHORIZER)){
			mav.setViewName("aggregator/list_mandate");
			
		}else{
			throw new ServerBusinessException(403,"Unable to determine user role User email["+user.getEmail()+"]");
		}


		return mav;


	}




}
