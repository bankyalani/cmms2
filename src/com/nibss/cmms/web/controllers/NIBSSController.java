package com.nibss.cmms.web.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.BankUser;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.Company;
import com.nibss.cmms.domain.Industry;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.MandateStatus;
import com.nibss.cmms.domain.Notification;
import com.nibss.cmms.domain.NotificationConfig;
import com.nibss.cmms.domain.Product;
import com.nibss.cmms.domain.Role;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.security.Sha1PasswordEncoder;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.service.BillerService;
import com.nibss.cmms.service.CompanyService;
import com.nibss.cmms.service.MandateService;
import com.nibss.cmms.service.MandateStatusService;
import com.nibss.cmms.service.NotificationService;
import com.nibss.cmms.service.ProductService;
import com.nibss.cmms.service.RoleService;
import com.nibss.cmms.service.UserService;
import com.nibss.cmms.utils.MailMessenger;
import com.nibss.cmms.utils.MailMessenger.MailType;
import com.nibss.cmms.utils.OptionBean;
import com.nibss.cmms.utils.Utilities;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.JQueryDataTableRequest;
import com.nibss.cmms.web.JQueryDataTableResponse;
import com.nibss.cmms.web.WebAppConstants;
import com.nibss.cmms.web.converters.BankEditor;
import com.nibss.cmms.web.converters.CompanyEditor;
import com.nibss.cmms.web.converters.IndustryEditor;
import com.nibss.cmms.web.converters.ProductEditor;
import com.nibss.cmms.web.converters.RoleEditor;
import com.nibss.cmms.web.domain.BillerTable;
import com.nibss.cmms.web.domain.CompanyTable;
import com.nibss.cmms.web.domain.MandateTable;
import com.nibss.cmms.web.domain.UserTable;
import com.nibss.cmms.web.validation.AjaxValidatorResponse;
import com.nibss.cmms.web.validation.CompanyValidator;
import com.nibss.cmms.web.validation.MandateValidator;
import com.nibss.cmms.web.validation.UserValidator;


@Controller
@RequestMapping("/nibss")
public class NIBSSController extends BaseController implements WebAppConstants {
	@Autowired
	private Sha1PasswordEncoder sha1PasswordEncoder;
	private static final Logger logger = Logger.getLogger(NIBSSController.class);

	@Autowired
	private BankService bankService;

	@Autowired
	private MandateService mandateService;

	@Autowired
	private BillerService billerService;

	@Autowired
	private MandateValidator mandateValidator;

	@Autowired
	private MandateStatusService mandateStatusService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private CompanyValidator companyValidator;


	@Autowired
	private CompanyService companyService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ProductService productService;

	@Autowired
	private MailMessenger mailUtility;



	@InitBinder("company")
	private void initCompanyBinder(WebDataBinder binder) {
		binder.setValidator(companyValidator);
	};


	@InitBinder("user")
	private void initUserBinder(WebDataBinder binder) {
		binder.setValidator(userValidator);
	}



	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Product.class, new ProductEditor(productService));
		binder.registerCustomEditor(Bank.class, new BankEditor(bankService));
		binder.registerCustomEditor(Role.class, new RoleEditor(roleService));
		binder.registerCustomEditor(Company.class, new CompanyEditor(companyService));
		binder.registerCustomEditor(Industry.class, new IndustryEditor(companyService));

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

		if (request.isUserInRole(ROLE_NIBSS_ADMINISTRATOR))
			return new ModelAndView("redirect:/nibss/biller/list");
		else 
			return new ModelAndView("redirect:/nibss/biller/list"); 
	}



	@RequestMapping(value="/mandate/list",method=RequestMethod.GET)
	@Secured (value={ROLE_NIBSS_ADMINISTRATOR})
	public ModelAndView getAllMandate(HttpServletRequest request, Model model){
		ModelAndView mav= new ModelAndView();
		List<MandateStatus> mandateStatuses= new ArrayList<>();

		try {
			mandateStatuses=mandateStatusService.getAllMandateStatus();
			mav.addObject("banks", bankService.getAllBanks());
			mav.addObject("billers", billerService.getAllBillers());

		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		this.setViewMainHeading("All Mandates <small>All Mandates captured on the application</small>", model);


		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "All Mandates"));

		this.setBreadCrumb(breadCrumb, model);

		mav.addObject("mandateStatuses", mandateStatuses);
		mav.setViewName("/nibss/list_mandate");
		return mav;
	}

	@RequestMapping(value="/mandate/view/{id}",method=RequestMethod.GET)
	public String viewMandate(@PathVariable(value="id") Long id, HttpServletRequest request,
			Model model){

		Mandate mandate = null;
		try {
			mandate = mandateService.getMandateByBillerAndMandateId(id);
			model.addAttribute("mandate", mandate);
		} catch (ServerBusinessException e) {
			logger.fatal(e);
		}


		this.setViewMainHeading("View Mandate", model);
		this.setViewBoxHeading("Mandate Code - "+mandate.getMandateCode(), model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("/nibss/mandate/list", "All Mandates"));
		breadCrumb.add(new OptionBean("#", mandate.getMandateCode()));

		this.setBreadCrumb(breadCrumb, model);
		return ("/mandate/view_mandate");
		//return mav;
	}


	@RequestMapping(value="/company/add",method=RequestMethod.POST)
	public @ResponseBody AjaxValidatorResponse saveNewProduct(HttpServletRequest request,
			@ModelAttribute("company") @Valid Company company,
			BindingResult result,
			Model model,final RedirectAttributes redirectAttributes){

		AjaxValidatorResponse res= new AjaxValidatorResponse();

		List<String> errors=new ArrayList<>();
		if (result.hasErrors()){
			result.getAllErrors().stream().forEach(e->{
				logger.debug(e.getObjectName()+"=>"+e.getDefaultMessage());
				errors.add(e.getDefaultMessage());
				res.setErrorMessageList(errors);
				res.setStatus("FAILED");
			});

		}else{

			try {
				company.setStatus(STATUS_ACTIVE);
				company.setCreatedBy(getCurrentUser(request));
				company.setDateCreated(new Date());
				companyService.addNewCompany(company);
				res.setStatus("SUCCESS");
			} catch (ConstraintViolationException e) {
				errors.add("The Company already exist!");
				res.setErrorMessageList(errors);
				res.setStatus("FAILED");	
				logger.error(e);
			} catch (Exception e) {
				res.setStatus("FAILED");	
				errors.add("An error has occurred while creating the company. please try later");
				res.setErrorMessageList(errors);
				logger.error(e);
			}

		}

		return res;

	}



	public @ResponseBody @RequestMapping(value="datatable/mandate/list",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	JQueryDataTableResponse<MandateTable> getMandateTable(@RequestBody JQueryDataTableRequest dtReq,HttpServletRequest request) {
		List<Mandate> mandatesData= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;

		try {
			Map<String,Object> filters=buildJQueryDataTableFilter(dtReq);
			if(!filters.isEmpty()){
				System.err.println("is filtering");
				request.getSession().setAttribute(DT_FILTER,filters);
				mandatesData=mandateService.findPaginatedMandates(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=mandateService.findPaginatedMandates(filters,null,null).size();
			}else{
				request.getSession().setAttribute(DT_FILTER,null);
				mandatesData=mandateService.findPaginatedMandates(null,dtReq.getStart(),dtReq.getLength());
				//totalRecordSize=mandatesData.size();
			}
			totalRecordSize=mandateService.getAllMandates().size();
			//mandates=mandateService.findMandates(filter);
		} catch (ServerBusinessException e) {
			logger.error(e);
		}		
		return getJQueryDatatableMandates(dtReq, mandatesData,filterSize,totalRecordSize);

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/download/mandate/xls", method = RequestMethod.GET)
	public ModelAndView exportMandate(HttpServletRequest request) {
		List<Mandate> mandates = new ArrayList<>();
		try{
			Map<String,Object> filters=(Map<String, Object>) request.getSession().getAttribute(DT_FILTER);
			mandates=mandateService.findPaginatedMandates(filters,null,null);
		}catch(ServerBusinessException e){
			logger.error(e);
		}

		// return a view which will be resolved by an excel view resolver
		ModelMap model = new ModelMap();
		model.addAttribute(REPORT_TYPE,REPORT_TYPE_MANDATE);
		model.addAttribute("mandates",mandates);
		return new ModelAndView("excelView",model);

	}


	@RequestMapping(value = "/notification/config/list", method = RequestMethod.GET)
	public ModelAndView getNotificationConfigurations(Model model){
		List<NotificationConfig> configs= new ArrayList<>();

		try {
			configs=notificationService.getNotificationConfigs();
		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		ModelAndView mav = new ModelAndView();

		this.setViewMainHeading("Notification Configuration <small>Set maximum allowed emails for each status</small>", model);


		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Email Configuartion"));

		this.setBreadCrumb(breadCrumb, model);

		mav.addObject("configs", configs);
		mav.setViewName("/nibss/list_notification_config");

		return mav;

	}



	@RequestMapping(value="/notification/config/update",method=RequestMethod.POST)
	public @ResponseBody String updateMaxEmailConfig(HttpServletRequest request,
			@RequestParam("id") Long id,@RequestParam("billerAllowedCount") int billerAllowedCount,
			@RequestParam("bankAllowedCount") int bankAllowedCount){

		String res="";


		try {
			NotificationConfig nConfig=notificationService.getNotificationConfigById(id);
			if (nConfig==null)
				throw new ServerBusinessException(0, "Unable to getNotificationConfigById("+id+")");
			nConfig.setId(id);
			nConfig.setBankAllowedCount(bankAllowedCount);
			nConfig.setBillerAllowedCount(billerAllowedCount);


			notificationService.updateNotificationMaxEmailConfig(nConfig);
			res=AJAX_SUCCESS;

		} catch (ServerBusinessException e) {	
			res=AJAX_FAILED;
			logger.error(e);
		} 


		return res;

	}



	@RequestMapping(value="/user/list")
	public ModelAndView getUsers(HttpServletRequest request, Model model,@ModelAttribute("user") User user){
		ModelAndView mav= new ModelAndView();
		try {
			mav.addObject("banks", bankService.getAllBanks());
			mav.addObject("billers", billerService.getAllBillers());
			mav.addObject("roles",roleService.getAllRoles());

		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		mav.setViewName("/nibss/list_user");
		this.setViewMainHeading("All Users <small>These includes all the users on the application together with their status</small>", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "All Users"));
		mav.addObject("userDataTableUrl", "/nibss/datatable/user/list");
		mav.addObject("updateUserUrl", "/nibss/user/edit");
		mav.addObject("resetUserUrl", "/nibss/user/reset");
		mav.addObject("createUserUrl", "/nibss/user/add");
		mav.addObject("deleteUserUrl", "/nibss/user/delete");
		this.setBreadCrumb(breadCrumb, model);
		return mav;
	}

	@RequestMapping(value="/user/create")
	public ModelAndView createUserForm(HttpServletRequest request,Model model){
		ModelAndView mav= new ModelAndView();
		List<Bank> banks= new ArrayList<>();
		try{
			banks=bankService.getAllBanks();
		}catch(Exception e){
			logger.error(null,e);
		}
		this.setViewModalHeading("Create User", model);
		mav.addObject("user", new User());
		mav.addObject("banks",banks);
		mav.setViewName("/nibss/modal/add_user");
		return mav;
	}


	@RequestMapping(value="/user/create",method= RequestMethod.POST)
	public ModelAndView processUserForm(HttpServletRequest request,Model model,@ModelAttribute("user") User user,@RequestParam(value="bank",required=false) Bank bank,
			BindingResult result,final RedirectAttributes redirectAttributes){
		ModelAndView mav= new ModelAndView();
		User _user= null;
		if(Integer.valueOf(WebAppConstants.USER_TYPE_BANK)==user.getUserType()){
			//bank user

			_user= new BankUser(user);
			((BankUser)_user).setBank(bank);
			
			Role r= roleService.getRolesByName(new String[]{WebAppConstants.ROLE_BANK_ADMINISTRATOR}).get(0);
		
			_user.setRole(r);
		}else if(Integer.valueOf(WebAppConstants.USER_TYPE_NIBSS)==user.getUserType()){
			_user=user;
			Role r= roleService.getRolesByName(new String[]{WebAppConstants.ROLE_NIBSS_ADMINISTRATOR}).get(0);
			_user.setRole(r);		
			
		}

		try {
			String password=Utilities.generatePassword();
			_user.setDateCreated(new Date());
			_user.setStatus(USER_STATUS_ACTIVE);
			//_user.setPassword(password);
			_user.setPassword(sha1PasswordEncoder.encodePassword(password, ""));
			userService.addUser(_user);
			_user.setPassword(password);
			
			final User mailUser = _user;
			Runnable r = ()->{
				mailUtility.sendMail(MailType.NEW_USER, mailUser);
			};
			executor.execute(r);
			redirectAttributes.addFlashAttribute("message", "User has been created successfully");
			redirectAttributes.addFlashAttribute("messageClass", "success");
		} catch (ConstraintViolationException e) {
			logger.error(null,e);
			redirectAttributes.addFlashAttribute("message", "User already exist");
			redirectAttributes.addFlashAttribute("messageClass", "danger");

		} catch (Exception e) {
			logger.error(null,e);
			redirectAttributes.addFlashAttribute("message", "An error occurred while creating user");
			redirectAttributes.addFlashAttribute("messageClass", "danger");
		}

		mav.setViewName("redirect:/nibss/user/list");
		return mav;
	}


	@ResponseBody @RequestMapping(value="datatable/user/list",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	public JQueryDataTableResponse<UserTable> getUsers(@RequestBody JQueryDataTableRequest dtReq,HttpServletRequest request){

		List<User> users= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;
		try {
			Map<String,Object> filters=buildJQueryDataTableFilter(dtReq);
			if(!filters.isEmpty()){
				System.err.println("is filtering");
				users=userService.findPaginatedUser(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=userService.findPaginatedUser(filters,null,null).size();
			}else{
				users=userService.findPaginatedUser(null,dtReq.getStart(),dtReq.getLength());
			}
			totalRecordSize=userService.getAllUsers().size();

		} catch (ServerBusinessException e) {
			logger.error(e);
		}	

		return getJQueryDatatableUsers(dtReq,users, filterSize,totalRecordSize);
	}


	@Secured(ROLE_NIBSS_ADMINISTRATOR)
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
	
	@Secured(ROLE_NIBSS_ADMINISTRATOR)
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


	@Secured(ROLE_NIBSS_ADMINISTRATOR)
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

			userService.addUser(user);
			mailUtility.sendMail(MailType.NEW_USER, user);
			res=AJAX_SUCCESS;	
		} catch (Exception e) {
			res=AJAX_FAILED;
			throw new Exception(e);
		} 
		return res;
	}


	@Secured(ROLE_NIBSS_ADMINISTRATOR)
	@RequestMapping(value="/user/delete",method=RequestMethod.POST)
	public @ResponseBody String deleteNewUser(HttpServletRequest request,@RequestParam("userId") Long userId) throws Exception{

		String res="";
		try {
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


	@Secured(ROLE_NIBSS_ADMINISTRATOR)
	@RequestMapping("/biller/list")
	public ModelAndView listAllBillers(HttpServletRequest request, Model model,
			@ModelAttribute("Biller") Biller biller){
		ModelAndView mav= new ModelAndView();

		this.setViewMainHeading("All Billers <small>All Billers within the application</small>", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		//breadCrumb.add(new OptionBean("/bank/mandate/list", "All Mandates"));
		breadCrumb.add(new OptionBean("#", "Billers"));
		mav.addObject("billerDataTableUrl", "/nibss/datatable/biller/list");
		mav.addObject("viewUrl", "/nibss/biller/view/");
		mav.addObject("categories", companyService.getCategories());
		try {
			mav.addObject("banks", bankService.getAllBanks());
		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		this.setBreadCrumb(breadCrumb, model);
		mav.setViewName("/biller/list_biller");
		return mav;
	}


	@Secured(ROLE_NIBSS_ADMINISTRATOR)
	@RequestMapping("/company/list")
	public ModelAndView listAllCompanies(HttpServletRequest request, Model model,
			@ModelAttribute("company") Company company){
		ModelAndView mav= new ModelAndView();

		this.setViewMainHeading("All Organizations <small>All Organizations available in the application</small>", model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Organizations"));

		this.setBreadCrumb(breadCrumb, model);
		mav.addObject("categories", companyService.getCategories());
		mav.setViewName("/nibss/list_companies");

		return mav;
	}

	@RequestMapping(value="/biller/view/{id}",method=RequestMethod.GET)
	public String viewBiller(@PathVariable(value="id") Long id, HttpServletRequest request, Model model){

		Biller biller = null;

		List<User> billerAdmins= new ArrayList<>();

		List<Product> billerProducts= new ArrayList<>();

		List<Notification> notifications= new ArrayList<>();
		try {
			biller = billerService.getBillerById(id);
			billerProducts= productService.getProductsByBiller(biller);
			billerAdmins=billerService.getBillerAdmins(biller);
			notifications= notificationService.getNotificationByBiller(biller);
		} catch (ServerBusinessException e) {
			logger.error(e);
		}
		model.addAttribute("biller", biller);
		model.addAttribute("products", billerProducts);
		model.addAttribute("users", billerAdmins);
		model.addAttribute("notifications", notifications);




		this.setViewMainHeading(String.format("%s (%s)",biller.getCompany().getName(),biller.getCompany().getRcNumber()), model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("/bank/biller/list", "All Billers"));
		breadCrumb.add(new OptionBean("#", biller.getCompany().getName()));
		this.setBreadCrumb(breadCrumb, model);
		return ("/nibss/view_biller");
	}

	@ResponseBody @RequestMapping(value="datatable/biller/list",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	public JQueryDataTableResponse<BillerTable> getBillers(@RequestBody JQueryDataTableRequest dtReq,HttpServletRequest request){

		List<Biller> billers= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;
		try {
			Map<String,Object> filters=buildJQueryDataTableFilter(dtReq);
			if(!filters.isEmpty()){
				System.err.println("is filtering");
				billers=billerService.findPaginatedBillers(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=billerService.findPaginatedBillers(filters,null,null).size();
			}else{
				billers=billerService.findPaginatedBillers(null,dtReq.getStart(),dtReq.getLength());
			}
			totalRecordSize=billerService.getAllBillers().size();
		} catch (ServerBusinessException e) {
			logger.error(e);
		}	

		return getJQueryDatatableBillers(dtReq,billers, filterSize,totalRecordSize);
	}
	
	
	


	@ResponseBody @RequestMapping(value="datatable/company/list",method= RequestMethod.POST, headers = {"Content-type=application/json"})
	public JQueryDataTableResponse<CompanyTable> getJQueryDatatableCompanies(@RequestBody JQueryDataTableRequest dtReq){
		JQueryDataTableResponse<CompanyTable> response= new JQueryDataTableResponse<CompanyTable>();
		SimpleDateFormat sdf= new SimpleDateFormat("YYYY-MM-dd hh:mm");

		List<Company> companies= new ArrayList<>();
		int totalRecordSize=0;
		Integer filterSize=null;
		try {
			Map<String,Object> filters=buildJQueryDataTableFilter(dtReq);
			if(!filters.isEmpty()){
				companies=companyService.findPaginatedCompanies(filters,dtReq.getStart(),dtReq.getLength());
				filterSize=companyService.findPaginatedCompanies(filters,null,null).size();
			}else{
				companies=companyService.findPaginatedCompanies(null,dtReq.getStart(),dtReq.getLength());
			}
			totalRecordSize=companyService.getAllCompanies().size();

			CompanyTable[] companyTableArray=new CompanyTable[companies.size()];
			final List<Company> finalCompanies=companies;
			IntStream.range(0,finalCompanies.size())
			.forEach(index->{
				Company company=finalCompanies.get(index);
				CompanyTable companyTable= new CompanyTable();
				companyTable.setCompanyName(company.getName());
				companyTable.setCreatedBy(company.getCreatedBy().getEmail());
				companyTable.setDescription(company.getDescription());
				companyTable.setRcNumber(company.getRcNumber());
				companyTable.setIndustry(company.getIndustry().getId());
				companyTable.setIndustryName(company.getIndustry().getName());
				companyTable.setId(company.getId());

				int companyStatus=company.getStatus();
				String _companyStatus="UnKnown";
				if(companyStatus==(BILLER_STATUS_ACTIVE)){
					_companyStatus="Active";
				}else if(companyStatus==(BILLER_STATUS_INACTIVE)){
					_companyStatus="Inactive";
				}
				companyTable.setStatus(_companyStatus);
				companyTable.setDateCreated(sdf.format(company.getDateCreated()));

				companyTableArray[index]=companyTable;
			});
			response.setDraw(dtReq.getDraw());
			response.setData(companyTableArray);
			response.setRecordsFiltered(filterSize==null?totalRecordSize:filterSize);
			response.setRecordsTotal(totalRecordSize);

		} catch (ServerBusinessException e) {
			logger.error(e);
		}	

		return response;

	}
	
	
	@RequestMapping(value="/report",method=RequestMethod.GET)
	public ModelAndView viewReport(HttpServletRequest request,HttpServletResponse response, Model model){
		ModelAndView mav= new ModelAndView();
		mav.setViewName("/nibss/report");
		this.setViewMainHeading("Report Download <small>Filter by several criterias</small>", model);
		try {
			mav.addObject("banks", bankService.getAllBanks());
			mav.addObject("billers", billerService.getAllBillers());
			mav.addObject("mandateStatuses", mandateStatusService.getAllMandateStatus());

		} catch (ServerBusinessException e) {
			logger.error(e);
		}
		mav.addObject("mandateReportUrl", "/nibss/report/mandate/");
		mav.addObject("transactionReportUrl", "/nibss/report/transaction/");
		
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

	
	
	//---Billing Page---
	@RequestMapping(value="/transaction/billing",method=RequestMethod.GET)
	public String billingPage(HttpServletRequest request, Model model){
		this.setViewMainHeading("Transaction Billing",model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#","Billing"));
		this.setBreadCrumb(breadCrumb, model);
		return ("/nibss/transaction-billing");
	}
	
}
