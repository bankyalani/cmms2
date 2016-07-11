package com.nibss.cmms.web.controllers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nibss.cmms.app.service.ApplicationService;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.BankNotification;
import com.nibss.cmms.domain.BankUser;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.Company;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.MandateStatus;
import com.nibss.cmms.domain.Notification;
import com.nibss.cmms.domain.Product;
import com.nibss.cmms.domain.Rejection;
import com.nibss.cmms.domain.RejectionReason;
import com.nibss.cmms.domain.Role;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.security.Sha1PasswordEncoder;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.service.BillerService;
import com.nibss.cmms.service.CommonService;
import com.nibss.cmms.service.CompanyService;
import com.nibss.cmms.service.MandateService;
import com.nibss.cmms.service.MandateStatusService;
import com.nibss.cmms.service.NotificationService;
import com.nibss.cmms.service.ProductService;
import com.nibss.cmms.service.RoleService;
import com.nibss.cmms.service.UserService;
import com.nibss.cmms.utils.DateUtils;
import com.nibss.cmms.utils.MailMessenger;
import com.nibss.cmms.utils.MailMessenger.MailType;
import com.nibss.cmms.utils.OptionBean;
import com.nibss.cmms.utils.Utilities;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.JQueryDataTableRequest;
import com.nibss.cmms.web.JQueryDataTableResponse;
import com.nibss.cmms.web.WebAppConstants;
import com.nibss.cmms.web.converters.BankEditor;
import com.nibss.cmms.web.converters.BillerEditor;
import com.nibss.cmms.web.converters.CompanyEditor;
import com.nibss.cmms.web.converters.ProductEditor;
import com.nibss.cmms.web.converters.RoleEditor;
import com.nibss.cmms.web.domain.BillerTable;
import com.nibss.cmms.web.domain.MandateTable;
import com.nibss.cmms.web.domain.NotificationTable;
import com.nibss.cmms.web.domain.UserTable;
import com.nibss.cmms.web.validation.BillerValidator;
import com.nibss.cmms.web.validation.MandateValidator;
import com.nibss.cmms.web.validation.UserValidator;


@Controller
@RequestMapping("/bank")
public class BankController extends BaseController implements WebAppConstants {

	private static final Logger logger = Logger.getLogger(BankController.class);
	@Autowired
	private Sha1PasswordEncoder sha1PasswordEncoder;
	@Autowired
	private BankService bankService;

	@Autowired
	private MandateService mandateService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private BillerService billerService;

	@Autowired
	private MandateValidator mandateValidator;

	@Autowired
	private MandateStatusService mandateStatusService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private BillerValidator billerValidator;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private MailMessenger mailUtility;
	 
	@Autowired
	private ThreadPoolTaskExecutor executor;


	@InitBinder("mandate")
	private void initMandateBinder(WebDataBinder binder) {
		binder.setValidator(mandateValidator);
	};

	@InitBinder("user")
	private void initUserBinder(WebDataBinder binder) {
		binder.setValidator(userValidator);
	}

	@InitBinder("biller")
	private void initBillerBinder(WebDataBinder binder) {
		binder.setValidator(billerValidator);
	}

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Product.class, new ProductEditor(productService));
		binder.registerCustomEditor(Bank.class, new BankEditor(bankService));
		binder.registerCustomEditor(Role.class, new RoleEditor(roleService));
		binder.registerCustomEditor(Company.class, new CompanyEditor(companyService));
		binder.registerCustomEditor(Biller.class, new BillerEditor(billerService));
	}

	@Override
	protected BankUser getCurrentBankUser(HttpServletRequest request){
		return (BankUser)request.getSession().getAttribute(CURRENT_BANK_USER);
	}

	@Override
	protected User getCurrentUser(HttpServletRequest request){
		return (User)request.getSession().getAttribute(CURRENT_USER_USER);
	}

	@RequestMapping(value="/")
	public ModelAndView index(Model model, HttpServletRequest request) throws ServerBusinessException{


		HttpSession session= (HttpSession)request.getSession();
		User user=getCurrentUser(request);
		if (getCurrentUserRole(request)==null)			
			session.setAttribute(CURRENT_USER_ROLE,user.getRole());

		if (getCurrentBankUser(request)==null){
			BankUser bankUser= (BankUser)bankService.getBankUserByUser(user);
			System.err.println("bankUserbankUser"+bankUser);
			session.setAttribute(CURRENT_BANK_USER,bankUser);

		}
		if (request.isUserInRole(ROLE_BANK_INITIATOR))
			return new ModelAndView("redirect:/bank/mandate/list");
		else if(request.isUserInRole(ROLE_BANK_AUTHORIZER))
			return new ModelAndView("redirect:/bank/mandate/list");
		else if(request.isUserInRole(ROLE_BANK_ADMINISTRATOR))
			return new ModelAndView("redirect:/bank/user/list");
		else if(request.isUserInRole(ROLE_BANK_AUDITOR))
			return new ModelAndView("redirect:/bank/mandate/list");
		else 
			return new ModelAndView("redirect:/bank/mandate/list");
	}

	@ModelAttribute("banks")
	public List<Bank> getCurrentBankList(HttpServletRequest request){
		List<Bank> banks= new ArrayList<>();
		banks.add(getCurrentBankUser(request)!=null?getCurrentBankUser(request).getBank():null);
		return banks;
	}

	@RequestMapping(value="/mandate/list")
	public ModelAndView getMandate(HttpServletRequest request, Model model){
		ModelAndView mav= new ModelAndView();
		List<MandateStatus> mandateStatuses= new ArrayList<>();
		BankUser bankUser=getCurrentBankUser(request);
		try {
			mandateStatuses=mandateStatusService.getMandateStatuses(new Long[]{BANK_APPROVE_MANDATE,BILLER_APPROVE_MANDATE,BILLER_AUTHORIZE_MANDATE,BANK_AUTHORIZE_MANDATE,BANK_REJECT_MANDATE, BANK_INITIATE_MANDATE});
			mav.addObject("bankBillers", billerService.getBillerByBank(bankUser.getBank()));
			mav.addObject("billers", billerService.getAllBillers());
			mav.addObject("customerBanks", bankService.getAllBanks());

		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		mav.setViewName("/bank/list_mandate");
		this.setViewMainHeading("All Mandates <small>These includes all the mandates belonging to a bank together with their status</small>", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "All Mandates"));

		this.setBreadCrumb(breadCrumb, model);
		mav.addObject("mandateStatuses", mandateStatuses);

		return mav;
	}

	public @ResponseBody @RequestMapping(value="datatable/mandate/list/{id}",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	JQueryDataTableResponse<MandateTable> getMandateTable(@RequestBody JQueryDataTableRequest dtReq,HttpServletRequest request,@PathVariable("id") int id) {
		List<Mandate> mandatesData= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;
		Map<String,Object> filters=buildJQueryDataTableFilter(dtReq);
		logger.info(filters);
		Map<String,Object> defaultFilter= new TreeMap<>();
		try {
			switch (id){
			case 1:
				
				/*defaultFilter.put("bank",getCurrentBankUser(request).getBank());
				defaultFilter.put("status.id", new Long[]{BANK_APPROVE_MANDATE,BILLER_APPROVE_MANDATE,BILLER_AUTHORIZE_MANDATE,BANK_AUTHORIZE_MANDATE,BANK_REJECT_MANDATE, BANK_INITIATE_MANDATE});
				filters.putAll(defaultFilter);
				
				//defaultFilter.put("status.id", new Long[]{BANK_APPROVE_MANDATE,BILLER_APPROVE_MANDATE,BILLER_AUTHORIZE_MANDATE,BANK_AUTHORIZE_MANDATE,BANK_REJECT_MANDATE, BANK_INITIATE_MANDATE});
				if(!filters.isEmpty()){
					request.getSession().setAttribute(DT_FILTER_1,filters);
					mandatesData=mandateService.findPaginatedMandates(filters,dtReq.getStart(),dtReq.getLength());
					filterSize=mandateService.findPaginatedMandates(filters,null,null).size();
				}else{
					request.getSession().setAttribute(DT_FILTER_1,null);
					mandatesData=mandateService.findPaginatedMandates(filters,dtReq.getStart(),dtReq.getLength());
					//totalRecordSize=mandatesData.size();
				}
				totalRecordSize=mandateService.findPaginatedMandates(defaultFilter,null,null).size();
*/
				defaultFilter.put("bank",getCurrentBankUser(request).getBank());
				defaultFilter.put("status.id", new Long[]{BANK_APPROVE_MANDATE,BILLER_APPROVE_MANDATE,BILLER_AUTHORIZE_MANDATE,BANK_AUTHORIZE_MANDATE,BANK_REJECT_MANDATE, BANK_INITIATE_MANDATE});
				filters.putAll(defaultFilter);
				
					request.getSession().setAttribute(DT_FILTER_1,filters);
					mandatesData=mandateService.findPaginatedMandates(filters,dtReq.getStart(),dtReq.getLength());
					filterSize=mandateService.findPaginatedMandates(filters,null,null).size();
				
					//mandatesData=mandateService.findPaginatedMandates(filters,dtReq.getStart(),dtReq.getLength());
					//totalRecordSize=mandatesData.size();
				
					totalRecordSize=mandateService.findPaginatedMandates(defaultFilter,null,null).size();

				break;
			case 2:
			
				defaultFilter.put("product.biller.bank.bankCode", getCurrentBankUser(request).getBank().getBankCode());
				filters.putAll(defaultFilter);

				request.getSession().setAttribute(DT_FILTER_2,filters);
				mandatesData=mandateService.findPaginatedMandates(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=mandateService.findPaginatedMandates(filters,null,null).size();
				totalRecordSize=mandateService.findPaginatedMandates(defaultFilter,null,null).size();
				break;
			}

		} catch (ServerBusinessException e) {
			logger.error(e);
		}		
		return getJQueryDatatableMandates(dtReq, mandatesData,filterSize,totalRecordSize);
	}


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/download/mandate/xls/{id}", method = RequestMethod.GET)
	public ModelAndView exportMandate(HttpServletRequest request, @PathVariable("id") int id) {
		List<Mandate> mandates = new ArrayList<>();
		Map<String,Object> filters=new TreeMap<>();
		filters.put("bank",getCurrentBankUser(request).getBank());
		filters.put("status.id", new Long[]{BILLER_APPROVE_MANDATE,BANK_AUTHORIZE_MANDATE,BANK_REJECT_MANDATE});
		try{
			switch (id){

			case 1: 
				filters.putAll((Map<String, Object>) request.getSession().getAttribute(DT_FILTER_1));
				break;
			case 2:
				filters.putAll((Map<String, Object>) request.getSession().getAttribute(DT_FILTER_2));
				break;

			}
			mandates=mandateService.findPaginatedMandates(filters,null,null);

		}catch(ServerBusinessException e){
			logger.error(e);
		}
		// return a view which will be resolved by an excel view resolver
		ModelMap model = new ModelMap();
		model.addAttribute(REPORT_TYPE,REPORT_TYPE_MANDATE);
		model.addAttribute(REPORT_OBJECT_TYPE, new Mandate());
		model.addAttribute("mandates",mandates);
		return new ModelAndView("excelView",model);
	}

	@RequestMapping(value="/mandate/view/{id}",method=RequestMethod.GET)
	public String viewMandate(@PathVariable(value="id") Long id, 
			HttpServletRequest request,
			Model model){

		BankUser bankUser=getCurrentBankUser(request);
		Mandate mandate = null;
		List<RejectionReason> rejectionReasons = new ArrayList<>();
		try {
			mandate = mandateService.getMandateByBankAndMandateId(id,bankUser.getBank());
			rejectionReasons=mandateService.getRejectionReasons();
		} catch (ServerBusinessException e) {
			logger.fatal(e);
		}

		this.setViewMainHeading("View Mandate", model);
		this.setViewBoxHeading("Mandate Code - "+mandate.getMandateCode(), model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("/bank/mandate/list", "All Mandates"));
		breadCrumb.add(new OptionBean("#", mandate.getMandateCode()));

		this.setBreadCrumb(breadCrumb, model);

		model.addAttribute("mandate", mandate);
		model.addAttribute("rejectionReasons", rejectionReasons);
		return ("/mandate/view_mandate");
		//return mav;
	}


	private ModelAndView approveMandate(Long id, String action, 
			final RedirectAttributes redirectAttributes,
			HttpServletRequest request){

		
		ModelAndView mav= new ModelAndView();

		String message = "An error has occured while processing the request. Please try later.";
		String messageClass="danger";

		try {
			Mandate mandate= mandateService.getMandateByMandateId(id);
			mandate.setLastActionBy(getCurrentUser(request));
			Runnable r=null;
			if ("accept".equalsIgnoreCase(action)){
				mandate.setStatus(mandateStatusService.getMandateStatusById(BANK_AUTHORIZE_MANDATE));
				mandate.setAcceptedBy(getCurrentUser(request));
				mandate.setDateAccepted(new Date());
				message = "Mandate <b>"+mandate.getMandateCode()+"</b> was successfully Approved!";
				messageClass="success";
				mandateService.modifyMandate(mandate);
				r = ()->{
					mailUtility.sendMail(MailType.MANDATE_AUTHORIZED, mandate);
				};
			}else if ("approve".equalsIgnoreCase(action)){
				//if the mandate is 
				mandate.setStatus(mandateStatusService.getMandateStatusById(BANK_APPROVE_MANDATE));
				mandate.setApprovedBy(getCurrentUser(request));
				mandate.setDateApproved(new Date());
				message = "Mandate <b>"+mandate.getMandateCode()+"</b> was successfully Authorized!";
				messageClass="success";
				mandateService.modifyMandate(mandate);

				r = ()->{
					try {
						applicationService.sendMandateAdvice(mandate);
					} catch (Exception e) {
						logger.error(null,e);
					}
					mailUtility.sendMail(MailType.MANDATE_APPROVED, mandate);
					
				};
			}
			executor.execute(r);
		} catch (ServerBusinessException e) {
			logger.error(e);
		}
		redirectAttributes.addFlashAttribute("message", message);   
		redirectAttributes.addFlashAttribute("messageClass", messageClass);   

		mav.setViewName("redirect:/bank/mandate/list");
		return mav;
	}

	@Secured (value={ROLE_BANK_INITIATOR})
	@RequestMapping(value="/mandate/approve",method=RequestMethod.POST, params="accept")
	public ModelAndView acceptMandate(@RequestParam(value="id", required=true) Long id, 
			final RedirectAttributes redirectAttributes,
			HttpServletRequest request){

		return approveMandate(id, "accept",redirectAttributes,request);

	}

	@Secured (value={ROLE_BANK_AUTHORIZER})
	@RequestMapping(value="/mandate/approve",method=RequestMethod.POST, params="approve")
	public ModelAndView approveMandate(@RequestParam(value="id", required=true) Long id, 
			final RedirectAttributes redirectAttributes,
			HttpServletRequest request){

		return approveMandate(id, "approve",redirectAttributes,request);

	}



	@Secured (value={ROLE_BANK_INITIATOR,ROLE_BANK_AUTHORIZER})
	@RequestMapping(value="/mandate/approve",method=RequestMethod.POST, params="reject")
	public ModelAndView rejectMandate(@RequestParam(value="id", required=true) Long id, 
			final RedirectAttributes redirectAttributes,
			@RequestParam(value="comment", required=true) String comment,
			@RequestParam(value="rejectionReason", required=true) Long rejectionReasonId,
			HttpServletRequest request){

		ModelAndView mav= new ModelAndView();


		String message = "An error has occured while processing the request. Please try later.";
		String messageClass="danger";

		try {

			Mandate mandate= mandateService.getMandateByMandateId(id);
			mandate.setStatus(mandateStatusService.getMandateStatusById(BANK_REJECT_MANDATE));
			mandate.setLastActionBy(getCurrentUser(request));
			Rejection r= new Rejection();
			r.setComment(comment);
			r.setDateRejected(new Date());
			r.setUser(getCurrentUser(request));
			r.setRejectionReason(mandateService.getRejectionReasonById(rejectionReasonId));
			mandate.setRejection(r);
			message = String.format("Mandate <b>%s</b> was successfully Rejected!",mandate.getMandateCode());
			messageClass="success";

			mandateService.modifyMandate(mandate);
			mailUtility.sendMail(MailType.MANDATE_DISAPPROVED, mandate);
		}catch (ServerBusinessException e){
			logger.error(e);
		}

		redirectAttributes.addFlashAttribute("message", message);   
		redirectAttributes.addFlashAttribute("messageClass", messageClass);   

		mav.setViewName("redirect:/bank/mandate/list");
		return mav;
	}

	@RequestMapping(value="/user/list")
	public ModelAndView getUsers(HttpServletRequest request, Model model,@ModelAttribute("user") User user){
		ModelAndView mav= new ModelAndView();

		List<Role> roles=roleService
				.getRolesByName(new String[]{ROLE_BANK_AUTHORIZER, ROLE_BANK_INITIATOR,ROLE_BANK_AUDITOR});
		model.addAttribute("roles", roles);

		mav.setViewName("/user/list_user");
		this.setViewMainHeading("All Users <small>All Users belonging to a Bank together with their status</small>", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "All Users"));
		mav.addObject("userDataTableUrl", "/bank/datatable/user/list");
		mav.addObject("updateUserUrl", "/bank/user/edit");
		mav.addObject("resetUserUrl", "/bank/user/reset");
		mav.addObject("createUserUrl", "/bank/user/add");
		mav.addObject("deleteUserUrl", "/bank/user/delete");
		this.setBreadCrumb(breadCrumb, model);
		return mav;
	}


	@RequestMapping(value="/biller/user/admin/list")
	public ModelAndView getBillerAdminUsers(HttpServletRequest request, Model model,@ModelAttribute("user") User user){
		ModelAndView mav= new ModelAndView();

		List<Role> roles=roleService
				.getRolesByName(new String[]{ROLE_BANK_AUTHORIZER, ROLE_BANK_INITIATOR,ROLE_BANK_AUDITOR,ROLE_BANK_ADMINISTRATOR});
		model.addAttribute("roles", roles);

		mav.setViewName("/user/list_user");
		this.setViewMainHeading("All Users <small>All Users belonging to a Bank together with their status</small>", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "All Users"));
		mav.addObject("userDataTableUrl", "/bank/datatable/user/list");
		mav.addObject("updateUserUrl", "/bank/user/edit");
		mav.addObject("createUserUrl", "/bank/user/add");
		mav.addObject("deleteUserUrl", "/bank/user/delete");
		this.setBreadCrumb(breadCrumb, model);
		return mav;
	}


	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/user/edit",method=RequestMethod.POST)
	public @ResponseBody String updateUser(HttpServletRequest request,@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("roleId") Long roleId, 
			@RequestParam("status") Byte status,@RequestParam("userId") Long userId) throws Exception{

		String res="";
		try {			
			User user= userService.getUserById(userId);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setRole(roleService.getRoleById(roleId));
			user.setStatus(status);
			user.setDateModified(new Date());
			userService.updateUser(user);
			res=AJAX_SUCCESS;	
		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		}
		return res;
	}


	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/user/add",method=RequestMethod.POST)
	public @ResponseBody String createNewUser(HttpServletRequest request,@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("roleId") Long roleId,
			@RequestParam("status") Byte status,@RequestParam("email") String email) throws Exception{

		String res="";
		try {
			BankUser user= new BankUser();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setRole(roleService.getRoleById(roleId));
			user.setStatus(status);
			user.setDateCreated(new Date());
			user.setBank(getCurrentBankUser(request).getBank());
			user.setEmail(email);
			
			String password=Utilities.generatePassword();
			//user.setPassword(password);
			user.setPassword(sha1PasswordEncoder.encodePassword(password, ""));
			userService.addUser(user);
			user.setPassword(password);
			res=AJAX_SUCCESS;	
			
			Runnable r = ()->{
				mailUtility.sendMail(MailType.NEW_USER, user);
			};
			executor.execute(r);
		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		} 
		return res;
	}
	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/user/reset",method=RequestMethod.POST)
	public @ResponseBody String resetUser(HttpServletRequest request,@RequestParam("userId") Long userId) throws Exception{

		String res="";
		try {		
			
			String password=Utilities.generatePassword();			
			
			
			
			User user= userService.getUserById(userId);
			user.setPassword(sha1PasswordEncoder.encodePassword(password, ""));
			user.setDateModified(new Date());
			userService.updateUser(user);
			Runnable r = ()->{
				mailUtility.sendMail(MailType.RESET_USER_PASSWORD, user);
			};
			executor.execute(r);
			res=AJAX_SUCCESS;	
		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		}
		return res;
	}

	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/user/delete",method=RequestMethod.POST)
	public @ResponseBody String deleteNewUser(HttpServletRequest request,@RequestParam("userId") Long userId) throws Exception{

		String res="";
		try {
			userService.deletUser(userService.getUserById(userId));
			res=AJAX_SUCCESS;	
		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		} 
		return res;
	}


	@ResponseBody @RequestMapping(value="datatable/user/list",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	public JQueryDataTableResponse<UserTable> getDatatableUsers(@RequestBody JQueryDataTableRequest dtReq,HttpServletRequest request){

		List<User> users= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;
		Map<String,Object> defaultFilter= new TreeMap<>();
		defaultFilter.put("bank",((BankUser)getCurrentUser(request)).getBank());
		try {
			Map<String,Object> filters= buildJQueryDataTableFilter(dtReq);
			if(!filters.isEmpty()){
				filters.putAll(defaultFilter);
				users=userService.findPaginatedUser(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=userService.findPaginatedUser(filters,null,null).size();
			}else{

				users=userService.findPaginatedUser(defaultFilter,dtReq.getStart(),dtReq.getLength());
			}
			totalRecordSize=bankService.getUsersByBank(((BankUser)getCurrentUser(request)).getBank()).size();
		} catch (ServerBusinessException e) {
			logger.error(e);
		}	

		return getJQueryDatatableUsers(dtReq,users, filterSize,totalRecordSize);
	}






	@RequestMapping(value="/user/view/{id}",method=RequestMethod.GET)
	public String viewUser(@PathVariable(value="id") Long id, HttpServletRequest request, Model model){

		BankUser bankUser=getCurrentBankUser(request);
		User user = null;
		user = bankService.getUserByBankAndUserId(id,bankUser.getBank());
		model.addAttribute("user", user);



		this.setViewMainHeading("View User", model);
		this.setViewBoxHeading("User details", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("/bank/user/list", "All Users"));
		breadCrumb.add(new OptionBean("#", user.getEmail()));
		this.setBreadCrumb(breadCrumb, model);
		return ("/user/view_user");
		//return mav;
	}

	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping("/biller/list")
	public ModelAndView listAllBillers(HttpServletRequest request, Model model,
			@ModelAttribute("Biller") Biller biller){
		ModelAndView mav= new ModelAndView();

		this.setViewMainHeading("All Billers <small>All Billers created by a Bank</small>", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		//breadCrumb.add(new OptionBean("/bank/mandate/list", "All Mandates"));
		mav.addObject("categories", companyService.getCategories());
		breadCrumb.add(new OptionBean("#", "Billers"));
		mav.addObject("billerDataTableUrl", "/bank/datatable/biller/list");
		mav.addObject("viewUrl", "/bank/biller/view/");
		this.setBreadCrumb(breadCrumb, model);
		mav.setViewName("/biller/list_biller");
		return mav;
	}


	@ResponseBody @RequestMapping(value="datatable/biller/list",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	public JQueryDataTableResponse<BillerTable> getBillers(@RequestBody JQueryDataTableRequest dtReq,HttpServletRequest request){

		List<Biller> billers= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;
		Map<String,Object> defaultFilter= new TreeMap<>();
		defaultFilter.put("bank",((BankUser)getCurrentUser(request)).getBank());

		try {
			Map<String,Object> filters=buildJQueryDataTableFilter(dtReq);
			if(!filters.isEmpty()){
				filters.putAll(defaultFilter);
				billers=billerService.findPaginatedBillers(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=billerService.findPaginatedBillers(filters,null,null).size();
			}else{
				billers=billerService.findPaginatedBillers(defaultFilter,dtReq.getStart(),dtReq.getLength());
			}
			totalRecordSize=billerService.getBillerByBank(((BankUser)getCurrentUser(request)).getBank()).size();
		} catch (ServerBusinessException e) {
			logger.error(e);
		}	

		return getJQueryDatatableBillers(dtReq,billers, filterSize,totalRecordSize);
	}


	@RequestMapping(value="/biller/view/{id}",method=RequestMethod.GET)
	public String viewBiller(@PathVariable(value="id") Long id, HttpServletRequest request, Model model){

		BankUser bankUser=getCurrentBankUser(request);
		Biller biller = null;

		List<User> billerAdmins= new ArrayList<>();

		List<Product> billerProducts= new ArrayList<>();

		try {
			biller = bankService.getBankBillerById(id,bankUser.getBank());
			billerProducts= productService.getProductsByBiller(biller);
			billerAdmins=billerService.getBillerAdmins(biller);
		} catch (ServerBusinessException e) {
			logger.error(e);
		}
		model.addAttribute("biller", biller);
		model.addAttribute("products", billerProducts);
		model.addAttribute("users", billerAdmins);
		model.addAttribute("billerUserDataTableUrl", "/bank/datatable/biller/"+id+"/admin/list");
		model.addAttribute("billerUpdateUserUrl", "/bank/biller/"+id+"/user/admin/edit");
		model.addAttribute("billerCreateUserUrl", "/bank/biller/"+id+"/user/admin/add");
		model.addAttribute("billerDeleteUserUrl", "/bank/biller/"+id+"/user/admin/delete");

		this.setViewMainHeading(String.format("%s (%s)",biller.getCompany().getName(),biller.getCompany().getRcNumber()), model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("/bank/biller/list", "All Billers"));
		breadCrumb.add(new OptionBean("#", biller.getCompany().getName()));
		this.setBreadCrumb(breadCrumb, model);
		return ("/bank/view_biller");
	}


	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/biller/{billerId}/user/admin/edit",method=RequestMethod.POST)
	public @ResponseBody String updateBillerAdminUser(HttpServletRequest request,@RequestParam(required=true,value="firstName") String firstName,
			@RequestParam(required=true,value="lastName") String lastName, @RequestParam("status") Byte status,@RequestParam("userId") Long userId) throws Exception{

		String res="";
		try {			
			User user= userService.getUserById(userId);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setStatus(status);
			user.setDateModified(new Date());
			userService.updateUser(user);
			res=AJAX_SUCCESS;	
		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		}
		return res;
	}


	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/biller/{billerId}/user/admin/add",method=RequestMethod.POST)
	public @ResponseBody String createBillerAdminUser(HttpServletRequest request,@RequestParam(required=true,value="firstName") String firstName,
			@RequestParam(required=true,value="lastName") String lastName,@PathVariable("billerId") Long billerId,
			@RequestParam("status") Byte status,@Email @RequestParam(required=true,value="email") String email) throws Exception{

		String res="";
		try {
			String password=Utilities.generatePassword();
			BillerUser user= new BillerUser();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setPassword(password);
			user.setRole(roleService.getRolesByName(new String[]{ROLE_BILLER_ADMINISTRATOR}).stream().findFirst().get());
			user.setStatus(status);
			user.setDateCreated(new Date());
			user.setBiller(billerService.getBillerById(billerId));
			user.setEmail(email);
		
			userService.addUser(user);
			res=AJAX_SUCCESS;	
			Runnable r = ()->{

				mailUtility.sendMail(MailType.NEW_USER, user);
			};
			executor.execute(r);

		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		} 
		return res;
	}


	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/biller/{billerId}/user/admin/delete",method=RequestMethod.POST)
	public @ResponseBody String deleteBillerAdminUser(HttpServletRequest request,@RequestParam(required=true,value="userId") Long userId) throws Exception{

		String res="";
		try {
			//userService.deletUser(userService.getUserById(userId));
			User user=userService.getUserById(userId);
			user.setStatus(USER_STATUS_INACTIVE);
			//userService.deletUser(userService.getUserById(userId));
			userService.updateUser(user);
			res=AJAX_SUCCESS;	
		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		} 
		return res;
	}

	@ResponseBody @RequestMapping(value="/datatable/biller/{billerId}/admin/list",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	public JQueryDataTableResponse<UserTable> getBillerAdminsDatatable(@RequestBody JQueryDataTableRequest dtReq,HttpServletRequest request,
			@PathVariable(value="billerId") Long billerId){

		List<User> users= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;
		Map<String,Object> defaultFilter= new TreeMap<>();
		defaultFilter.put("biller.id",billerService.getBillerById(billerId).getId());
		defaultFilter.put("role.id",roleService.getRolesByName(new String []{ROLE_BILLER_ADMINISTRATOR}).stream().findFirst().get().getId());
		try {
			Map<String,Object> filters= buildJQueryDataTableFilter(dtReq);
			if(!filters.isEmpty()){
				filters.putAll(defaultFilter);
				users=userService.findPaginatedUser(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=userService.findPaginatedUser(filters,null,null).size();
			}else{

				users=userService.findPaginatedUser(defaultFilter,dtReq.getStart(),dtReq.getLength());
			}
			totalRecordSize=userService.findPaginatedUser(filters,null,null).size();
		} catch (Exception e) {
			logger.error(e);
		}	

		return getJQueryDatatableUsers(dtReq,users, filterSize,totalRecordSize);
	}


	@ResponseBody @RequestMapping(value="datatable/notification/email",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	public JQueryDataTableResponse<NotificationTable> getBillerNotifications(@RequestBody JQueryDataTableRequest dtReq,HttpServletRequest request){

		List<Notification> notifications= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;
		Map<String,Object> defaultFilter= new TreeMap<>();
		defaultFilter.put("bank.bankCode",getCurrentBankUser(request).getBank().getBankCode());		
		try {
			Map<String,Object> filters=buildJQueryDataTableFilter(dtReq);
			if(!filters.isEmpty()){
				filters.putAll(defaultFilter);
				notifications=notificationService.findPaginatedNotifications(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=notificationService.findPaginatedNotifications(filters,null,null).size();
			}else{
				notifications=notificationService.findPaginatedNotifications(defaultFilter,dtReq.getStart(),dtReq.getLength());
			}
			totalRecordSize=notificationService.findPaginatedNotifications(defaultFilter,null,null).size();
		} catch (ServerBusinessException e) {
			logger.error(e);
		}	

		return getJQueryDatatableNotifications(dtReq,notifications, filterSize,totalRecordSize);
	}


	@RequestMapping(value = "/biller/getUnregisteredBillers", method = RequestMethod.GET, produces={"application/json"})
	public @ResponseBody List<OptionBean> getUnregisteredBillers(@RequestParam(value = "billerName") String billerName, HttpServletRequest request) {
		List<Company> companies = new ArrayList<>();

		try {
			companies=companyService.getAvailableCompanies(billerName);
		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		List<OptionBean> options= new ArrayList<>();
		companies.stream().forEach(e->{
			options.add(new OptionBean(String.valueOf(e.getId()), e.getName()));
		});

		return options;
		/*for(Account account:collectionAccounts){
			;
		}
		return options;*/
	}



	@RequestMapping(value = "/getBillerProducts/{billerId}", method = RequestMethod.GET, produces={"application/json"})
	public @ResponseBody List<OptionBean> getBillerProducts(@PathVariable(value = "billerId") Long billerId, HttpServletRequest request) {

		List<Product> products = new ArrayList<>();
		List<OptionBean> options= new ArrayList<>();

		try {
			products=productService.getProductsByBillerId(billerId);
			for(Product product:products)
				options.add(new OptionBean(String.valueOf(product.getId()), product.getName()));
		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		return options;
	}



	@RequestMapping(value="/mandate/add",method=RequestMethod.GET)
	@Secured (value={ROLE_BILLER_INITIATOR})
	public ModelAndView createNewMandate(@ModelAttribute("mandate") Mandate mandate,HttpServletRequest request, Model model){
		ModelAndView mav = new ModelAndView();

		mav.addObject("billers",billerService.getActiveBillers());
		//mav.addObject("products",productService.getActiveProductsByBank(bankUser.getBank()));

		this.setViewMainHeading("Create New Mandate <small>Fill the form below to create a new Mandate</small>",model);
		this.setViewBoxHeading("Enter Mandate Details",model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Add New Mandate"));
		this.setBreadCrumb(breadCrumb,model);


		mav.addObject("mandate", mandate);
		//mav.addObject("selectMask", "readonly='true'");
		mav.addObject("collectionAccountUrl","/bank/getCollectionAccounts/");
		mav.addObject("action", "/bank/mandate/add");
		mav.setViewName("/mandate/add_mandate");


		return mav;		
	}


	@RequestMapping(value="/mandate/add",method=RequestMethod.POST)
	public ModelAndView saveNewMandate(@ModelAttribute("mandate") @Valid  Mandate mandate,
			BindingResult result, Model model,
			final RedirectAttributes redirectAttributes, 
			HttpServletRequest request){


		if (result.hasErrors())
			return createNewMandate(mandate, request, model);

		ModelAndView mav = new ModelAndView();

		String[] dateRange=mandate.getValidityDateRange().split("-");
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");

		Mandate m=null;
		String message = "An error has occured while creating the mandate. Please try later.";
		String messageClass="danger";
		String mandateCode=null;

		try {
			String billerRCNumber=mandate.getProduct().getBiller().getCompany().getRcNumber();
			mandateCode=commonService.generateMandateCode(billerRCNumber, String.valueOf(mandate.getProduct().getId()));
			mandate.setMandateCode(mandateCode);

			uploadMandateImage(mandate);

			if (mandateCode==null)
				throw new ServerBusinessException(0, "Unable to generate mandateCode");
			//mandate.setBiller(mandate.getProduct().getBiller());
			mandate.setCreatedBy(getCurrentUser(request));
			mandate.setStartDate(sdf.parse(dateRange[0].trim()));
			mandate.setEndDate(sdf.parse(dateRange[1].trim()));
			mandate.setDateCreated(new Date());
			mandate.setNextDebitDate(DateUtils.calculateNextDebitDate(mandate.getStartDate(), mandate.getEndDate(),mandate.getFrequency()));
			mandate.setChannel(CHANNEL_PORTAL);
			mandate.setRejection(null);
			mandate.setRequestStatus(STATUS_ACTIVE);
			mandate.setLastActionBy(getCurrentUser(request));
			mandate.setStatus(mandateStatusService.getMandateStatusById(BANK_INITIATE_MANDATE));
			if(mandate.getFrequency()>0){
				Date nextDebitDate=DateUtils.calculateNextDebitDate(mandate.getStartDate(), mandate.getEndDate(), mandate.getFrequency());
				mandate.setNextDebitDate(nextDebitDate==null?DateUtils.lastSecondOftheDay(mandate.getEndDate()):DateUtils.nullifyTime(nextDebitDate));
			}
			m=mandateService.addMandate(mandate);
		} catch (Exception e) {
			logger.error(e);
		}

		if (m!=null && m.getId()!=null && mandateCode!=null){
			message = String.format("New Mandate <b>%s</b> was successfully created!",mandateCode);
			messageClass="success";
		}

		redirectAttributes.addFlashAttribute("message", message);   
		redirectAttributes.addFlashAttribute("messageClass", messageClass);   
		mav.setViewName("redirect:/bank/mandate/add");
		return mav;   

	}
	
	@Secured(WebAppConstants.ROLE_BANK_INITIATOR)
	@RequestMapping(value="/mandate/bulk/add",method=RequestMethod.GET)
	public ModelAndView createBulkMandates(HttpServletRequest request,HttpServletResponse response, Model model){
		ModelAndView mav= new ModelAndView();
		mav.setViewName("/mandate/add_bulk_mandate");
		this.setViewMainHeading("Mandate Bulk Creation <small>create several mandates by uploading a zip file</small>", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Bulk Creation"));
		this.setBreadCrumb(breadCrumb, model);
		return mav;
	}


	@Secured(WebAppConstants.ROLE_BANK_INITIATOR)
	@RequestMapping(value="/mandate/bulk/add",method=RequestMethod.POST)
	public ModelAndView createBulkMandates(HttpServletRequest request,HttpServletResponse response, Model model, 
			@RequestParam("uploadFile")MultipartFile multipartFile){

		String message="";
		String messageClass="danger";
		//do basic file validation
		if (multipartFile.getOriginalFilename()==null || !multipartFile.getOriginalFilename().toLowerCase().endsWith(".zip")){
			message="Invalid file or file type. Only zip files are allowed!";
		}else{
			message="Your file has been uploaded successfully, a report would be sent to you upon completion.";
			messageClass="success";
		}

		BillerController billerController= appContext.getBean(BillerController.class);
		billerController.processBulkMandateUpload(request,multipartFile);
		model.addAttribute("message", message);
		model.addAttribute("messageClass",messageClass);
		return createBulkMandates(request,response,model);

	}


	@RequestMapping(value="/biller/add",method=RequestMethod.GET)
	@Secured (value={ROLE_BANK_ADMINISTRATOR})
	public ModelAndView addNewBiller(@ModelAttribute Biller biller,HttpServletRequest request, Model model){
		ModelAndView mav = new ModelAndView();

		mav.addObject("billers",billerService.getActiveBillers());
		//mav.addObject("products",productService.getActiveProductsByBank(bankUser.getBank()));

		this.setViewMainHeading("Create New Biller <small>Fill the form below to create a new Biller</small>",model);
		this.setViewBoxHeading("Enter Biller Details",model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("/bank/biller/list", "Manage Billers"));
		breadCrumb.add(new OptionBean("#", "Add New Biller"));
		this.setBreadCrumb(breadCrumb,model);

		mav.addObject("biller", biller);
		mav.setViewName("/bank/add_biller");
		return mav;		
	}


	@RequestMapping(value="/biller/add",method=RequestMethod.POST)
	@Secured (value={ROLE_BANK_ADMINISTRATOR})
	public ModelAndView saveNewProduct(HttpServletRequest request,
			@ModelAttribute @Valid Biller biller,
			BindingResult result,
			Model model,final RedirectAttributes redirectAttributes, @Value("${slaAttachmentPath}") String slaAttachmentPath){

		if (result.hasErrors())
			return addNewBiller(biller, request, model);

		ModelAndView mav= new ModelAndView();
		String message = "An error has occured while creating the mandate. Please try later.";
		String messageClass="danger";
		mav.setViewName("redirect:/bank/biller/add");

		try {			
			MultipartFile file= biller.getSlaAttachment();
			if(file!=null){
				File dest = new File(slaAttachmentPath);
				if(!dest.exists())
					dest.mkdirs();
				String fileName=slaAttachmentPath+"/"+getCurrentUser(request).getId()+"."+System.currentTimeMillis()
				+""+FilenameUtils.getExtension(file.getOriginalFilename());
				file.transferTo(new File(fileName));
				biller.setSlaAttachmentPath(fileName);
			}

			biller.setStatus(STATUS_ACTIVE);
			biller.setBank(getCurrentBankUser(request).getBank());
			biller.setCreatedBy(getCurrentUser(request));
			biller.setDateCreated(new Date());
			biller=bankService.addNewBiller(biller);
			message = String.format("New Biller <b>%s</b> was successfully created!",biller.getCompany().getName());
			messageClass="success";
			mav.setViewName("redirect:/bank/biller/list");
		} catch (ConstraintViolationException e) {
			message = "Biller already exist!";
			logger.error(e);
		} catch (Exception e) {
			message="An error has occurred while setting up the biller. please try later";
			logger.error(e);
		}
		redirectAttributes.addFlashAttribute("message", message);   
		redirectAttributes.addFlashAttribute("messageClass", messageClass);   

		return mav;

	}


	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/notification/config",method=RequestMethod.GET)
	public ModelAndView notificationConfigPage(HttpServletRequest request,HttpServletResponse response, Model model){
		ModelAndView mav= new ModelAndView();
		mav.setViewName("/bank/notification_config");
		this.setViewMainHeading("Notification Configuration", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Notification Configuration"));
		this.setBreadCrumb(breadCrumb, model);
		return mav;
	}

	@RequestMapping(value="/notification/email/config/update",method=RequestMethod.POST)
	public @ResponseBody String updateEmailConfig(HttpServletRequest request,
			@RequestParam("id") Long id,@RequestParam("value") String value){

		String res="";
		try {
			Notification notification=notificationService.getNotificationById(id);
			if (notification==null)
				throw new ServerBusinessException(0, "Unable to getNotificationById("+id+")");
			notification.setId(id);
			notification.setEmailAddress(value);

			notificationService.updateNotification(notification);
			res=AJAX_SUCCESS;

		} catch (ServerBusinessException e) {	
			res=AJAX_FAILED;
			logger.error(e);
		} 


		return res;

	}



	@RequestMapping(value="/notification/email/config/delete",method=RequestMethod.POST)
	public @ResponseBody String deleteEmailConfig(HttpServletRequest request,@RequestParam("id") Long id){

		String res="";
		try {
			Notification notification=notificationService.getNotificationById(id);
			if (notification==null)
				throw new ServerBusinessException(0, "Unable to getNotificationById("+id+")");				
			notificationService.deleteNotification(notification);
			res=AJAX_SUCCESS;

		} catch (ServerBusinessException e) {	
			res=AJAX_FAILED;
			logger.error(e);
		} 


		return res;

	}


	@Secured(ROLE_BANK_ADMINISTRATOR)
	@RequestMapping(value="/notification/email/config/add",method=RequestMethod.POST)
	public @ResponseBody String createEmailConfig(HttpServletRequest request,@RequestParam("status.id") Long id,
			@RequestParam("emailAddress") String email) throws Exception{

		String res="";
		try {
			BankNotification notification= new BankNotification();
			notification.setBank(getCurrentBankUser(request).getBank());
			notification.setDateCreated(new Date());
			notification.setDateModified(new Date());
			notification.setMandateStatus(mandateStatusService.getMandateStatusById(id));
			notification.setEmailAddress(email);
			notification.setCreatedBy(getCurrentUser(request));


			notificationService.createNewNotification(notification);
			res=AJAX_SUCCESS;	
		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		} 


		return res;

	}


	@RequestMapping(value="/report",method=RequestMethod.GET)
	public ModelAndView viewReport(HttpServletRequest request,HttpServletResponse response, Model model){
		ModelAndView mav= new ModelAndView();
		mav.setViewName("/common/report");
		this.setViewMainHeading("Report Download <small>Filter by several criterias</small>", model);
		try {
			//mav.addObject("banks", billerService.getBillerByBank(getCurrentBankUser(request).getBank()));
			mav.addObject("billers", billerService.getBillerByBank(getCurrentBankUser(request).getBank()));
			mav.addObject("mandateStatuses", mandateStatusService.getAllMandateStatus());

		} catch (ServerBusinessException e) {
			logger.error(e);
		}
		mav.addObject("mandateReportUrl", "/bank/report/mandate/");
		mav.addObject("transactionReportUrl", "/bank/report/transaction/");
		
		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Report"));
		this.setBreadCrumb(breadCrumb, model);
		return mav;

	}

	@RequestMapping(value="/report/mandate/{format}",method=RequestMethod.GET, produces = "application/octet-stream")
	public ModelAndView downloadMandateReport(HttpServletRequest request,HttpServletResponse response, ModelMap model, 
			@PathVariable("format") String reportFormat,
			@RequestParam("biller") String billerId, @RequestParam("product") String productId,
			@RequestParam("frequency") String frequency,@RequestParam("bank") String customerBank,
			@RequestParam("mandateStatus") String mandateStatus, @RequestParam("dateCreated") String dateCreated) throws IOException{

		return generateMandateReport
				(request, response, model, reportFormat,billerId, productId, frequency, customerBank, mandateStatus, dateCreated);

	}


	@RequestMapping(value="/report/transaction/{format}",method=RequestMethod.GET, produces = "application/octet-stream")
	public ModelAndView downloadTransactionReport(HttpServletRequest request,HttpServletResponse response, ModelMap model, 
			@PathVariable("format") String reportFormat,@RequestParam("subscriberCode") String subscriberCode, @RequestParam("debitStatus") String debitStatus,
			@RequestParam("biller") String billerId, @RequestParam("product") String productId,@RequestParam("bank") String customerBank, 
			@RequestParam("dateCreated") String dateCreated) throws IOException{

		return generateTransactionReport
				(request, response, model, reportFormat,billerId, productId, customerBank, dateCreated,subscriberCode,debitStatus);

	}

}
