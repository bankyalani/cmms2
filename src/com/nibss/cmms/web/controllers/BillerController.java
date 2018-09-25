package com.nibss.cmms.web.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nibss.cmms.app.service.ApplicationService;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.BillerNotification;
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.MandateStatus;
import com.nibss.cmms.domain.Notification;
import com.nibss.cmms.domain.Product;
import com.nibss.cmms.domain.Rejection;
import com.nibss.cmms.domain.Role;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.security.Sha1PasswordEncoder;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.service.BillerService;
import com.nibss.cmms.service.CommonService;
import com.nibss.cmms.service.MandateService;
import com.nibss.cmms.service.MandateStatusService;
import com.nibss.cmms.service.NotificationService;
import com.nibss.cmms.service.ProductService;
import com.nibss.cmms.service.RoleService;
import com.nibss.cmms.service.UserService;
import com.nibss.cmms.utils.DateUtils;
import com.nibss.cmms.utils.FileUtility;
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
import com.nibss.cmms.web.converters.ProductEditor;
import com.nibss.cmms.web.converters.RoleEditor;
import com.nibss.cmms.web.domain.MandateTable;
import com.nibss.cmms.web.domain.NotificationTable;
import com.nibss.cmms.web.domain.UserTable;
import com.nibss.cmms.web.validation.AjaxValidatorResponse;
import com.nibss.cmms.web.validation.MandateValidator;
import com.nibss.cmms.web.validation.ProductValidator;
import com.nibss.cmms.web.validation.UserValidator;

@Controller
@RequestMapping("/biller")
public class BillerController extends BaseController {

	private static final Logger logger = Logger.getLogger(BillerController.class);
	@Autowired
	private Sha1PasswordEncoder sha1PasswordEncoder;
	@Autowired
	private BankService bankService;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private MandateService mandateService;

	@Autowired
	private ProductService productService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private BillerService billerService;

	@Autowired
	private MailMessenger mailUtility;

	@Autowired
	private MailSender mailSender;

	/* Validators */

	@Autowired
	private MandateValidator mandateValidator;

	@Autowired
	private ProductValidator productValidator;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private MandateStatusService mandateStatusService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private BillerService billerUserService;

	@Autowired
	private NotificationService notificationService;

	@InitBinder("mandate")
	private void initMandateBinder(WebDataBinder binder) {
		// logger.info("calling mandate validator");
		binder.setValidator(mandateValidator);
	}

	@InitBinder("user")
	private void initUserBinder(WebDataBinder binder) {
		logger.info("calling user validator");
		binder.setValidator(userValidator);
	}

	@InitBinder("product")
	private void initProductBinder(WebDataBinder binder) {
		logger.info("calling product validator");
		binder.setValidator(productValidator);
	}

	/* Editors */

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Product.class, new ProductEditor(productService));
		binder.registerCustomEditor(Bank.class, new BankEditor(bankService));
		binder.registerCustomEditor(Role.class, new RoleEditor(roleService));
		binder.registerCustomEditor(Biller.class, new BillerEditor(billerService));
	}

	@RequestMapping(value = "/")
	public ModelAndView index(Model model, HttpServletRequest request, HttpServletResponse response)
			throws ServerBusinessException {

		HttpSession session = (HttpSession) request.getSession();
		User user = getCurrentUser(request);
		if (getCurrentUserRole(request) == null)
			session.setAttribute(WebAppConstants.CURRENT_USER_ROLE, user.getRole());

		if (getCurrentBillerUser(request) == null) {
			BillerUser billerUser = (BillerUser) billerUserService.getBillerUserById(user.getId());
			System.out.println("biller User 1:" + billerUser);
			session.setAttribute(WebAppConstants.CURRENT_BILLER_USER, billerUser);

		}

		// Can be used to get the last requested urls
		/*
		 * SavedRequest savedRequest = new
		 * HttpSessionRequestCache().getRequest(request, response);
		 * 
		 * if(savedRequest != null) { logger.debug("saved url::"
		 * +savedRequest.getRedirectUrl()); return new ModelAndView("redirect:"+
		 * savedRequest.getRedirectUrl()); }
		 */

		if (request.isUserInRole(WebAppConstants.ROLE_BILLER_INITIATOR))
			return new ModelAndView("redirect:/biller/mandate/add");
		else if (request.isUserInRole(WebAppConstants.ROLE_BILLER_INITIATOR))
			return new ModelAndView("redirect:/biller/mandate/list");
		else if (request.isUserInRole(WebAppConstants.ROLE_BILLER_ADMINISTRATOR))
			return new ModelAndView("redirect:/biller/user/list");
		else if (request.isUserInRole(WebAppConstants.ROLE_BILLER_AUDITOR))
			return new ModelAndView("redirect:/biller/mandate/list");
		else
			return new ModelAndView("redirect:/biller/mandate/list");

	}

	@ModelAttribute("billers")
	public List<Biller> getCurrentBillerList(HttpServletRequest request) {
		List<Biller> billers = new ArrayList<>();
		billers.add(getCurrentBillerUser(request) != null ? getCurrentBillerUser(request).getBiller() : null);
		return billers;
	}

	@RequestMapping(value = "/mandate/add", method = RequestMethod.GET)
	@Secured(value = { WebAppConstants.ROLE_BILLER_INITIATOR })
	public ModelAndView createNewMandate(@ModelAttribute("mandate") Mandate mandate, HttpServletRequest request,
			Model model) {
		ModelAndView mav = new ModelAndView();

		BillerUser billerUser = getCurrentBillerUser(request);
		List<Biller> billers = new ArrayList<>();
		billers.add(billerUser.getBiller());
		// Biller biller=billerUser.getBiller();
		try {
			mav.addObject("banks", bankService.getAllBanks());
			mav.addObject("billers", billers);
			// mav.addObject("products",productService.getProductsByBiller(biller));
		} catch (ServerBusinessException e) {
			logger.fatal(e);
		}

		this.setViewMainHeading("Create New Mandate <small>Fill the form below to create a new Mandate</small>", model);
		this.setViewBoxHeading("Enter Mandate Details", model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Add New Mandate"));
		this.setBreadCrumb(breadCrumb, model);

		mav.addObject("mandate", mandate);
		mav.addObject("collectionAccountUrl", "/biller/getCollectionAccounts/");
		mav.addObject("selectMask", "readonly='false'");
		mav.addObject("action", "/biller/mandate/add");
		mav.setViewName("/mandate/add_mandate");

		return mav;
	}

	@RequestMapping(value = "/mandate/add", method = RequestMethod.POST)
	public ModelAndView saveNewMandate(@ModelAttribute("mandate") @Valid Mandate mandate, BindingResult result,
			final RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {

		mandateValidator.validate(mandate, result);
		if (result.hasErrors())
			return createNewMandate(mandate, request, model);

		ModelAndView mav = new ModelAndView();

		String[] dateRange = mandate.getValidityDateRange().split("-");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Mandate m = null;
		String message = "An error has occured while creating the mandate. Please try later.";
		String messageClass = "danger";
		String mandateCode = null;
		try {
			BillerUser billerUser = getCurrentBillerUser(request);

			String billerRCNumber = billerUser.getBiller().getCompany().getRcNumber();
			mandateCode = commonService.generateMandateCode(billerRCNumber,
					String.valueOf(mandate.getProduct().getId()));
			mandate.setMandateCode(mandateCode);

			uploadMandateImage(mandate);

			if (mandateCode == null)
				throw new ServerBusinessException(0, "Unable to generate mandateCode");

			mandate.setCreatedBy(getCurrentUser(request));
			mandate.setStartDate(sdf.parse(dateRange[0].trim()));
			mandate.setEndDate(sdf.parse(dateRange[1].trim()));
			mandate.setDateCreated(new Date());
			mandate.setNextDebitDate(new Date());
			mandate.setLastActionBy(getCurrentUser(request));
			mandate.setChannel(WebAppConstants.CHANNEL_PORTAL);
			mandate.setFixedAmountMandate(Boolean.getBoolean(request.getParameter("fixedAmountMandate")));
			if (!mandate.isFixedAmountMandate())
				mandate.setVariableAmount(mandate.getAmount());

			mandate.setRejection(null);
			mandate.setStatus(mandateStatusService.getMandateStatusById(WebAppConstants.BILLER_INITIATE_MANDATE));
			if (mandate.getFrequency() > 0) {
				Date nextDebitDate = DateUtils.calculateNextDebitDate(mandate.getStartDate(), mandate.getEndDate(),
						mandate.getFrequency());
				mandate.setNextDebitDate(nextDebitDate == null ? DateUtils.lastSecondOftheDay(mandate.getEndDate())
						: DateUtils.nullifyTime(nextDebitDate));
			}
			m = mandateService.addMandate(mandate);

			mailUtility.sendMail(MailType.NEW_MANDATE, mandate);
		} catch (ServerBusinessException | ParseException | IllegalStateException | IOException
				| SizeLimitExceededException e) {
			logger.error(null, e);
		}

		if (m != null && m.getId() != null && mandateCode != null) {
			message = String.format("New Mandate <b>%s</b> was successfully created!", mandateCode);
			messageClass = "success";
		}

		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addFlashAttribute("messageClass", messageClass);
		mav.setViewName("redirect:/biller/mandate/add");
		return mav;

	}

	@RequestMapping(value = "/mandate/list")
	public ModelAndView getMandate(HttpServletRequest request, Model model) {
		ModelAndView mav = new ModelAndView();
		List<Mandate> mandates = new ArrayList<>();
		List<MandateStatus> mandateStatuses = new ArrayList<>();
		BillerUser billerUser = getCurrentBillerUser(request);
		try {
			mandates = mandateService.getMandatesByBiller(billerUser.getBiller());
			mandateStatuses = mandateStatusService.getAllMandateStatus();
			mav.addObject("banks", bankService.getAllBanks());
			mav.addObject("products", productService.getProductsByBiller(billerUser.getBiller()));

		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		mav.setViewName("/biller/list_mandate");
		this.setViewMainHeading(
				"All Mandates <small>These includes all the mandates belonging to a biller together with their status</small>",
				model);
		this.setViewBoxHeading("Item Count(" + mandates.size() + ")", model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "All Mandates"));

		this.setBreadCrumb(breadCrumb, model);
		mav.addObject("mandates", mandates);
		mav.addObject("mandateStatuses", mandateStatuses);

		return mav;
	}

	public @ResponseBody @RequestMapping(value = "datatable/mandate/list", method = RequestMethod.POST, headers = {
			"Content-type=application/json" }) JQueryDataTableResponse<MandateTable> getMandateTable(
					@RequestBody JQueryDataTableRequest dtReq, HttpServletRequest request) {
		List<Mandate> mandatesData = new ArrayList<>();
		BillerUser billerUser = getCurrentBillerUser(request);
		int totalRecordSize = 0;
		Integer filterSize = null;

		try {
			Map<String, Object> filters = buildJQueryDataTableFilter(dtReq);
			Map<String, Object> defaultFilter = new TreeMap<>();
			defaultFilter.put("product.biller.id", billerUser.getBiller().getId());
			filters.putAll(defaultFilter);

			request.getSession().setAttribute(WebAppConstants.DT_FILTER, filters);
			mandatesData = mandateService.findPaginatedMandates(filters, dtReq.getStart(), dtReq.getLength());
			filterSize = mandateService.findPaginatedMandates(filters, null, null).size();

			if (filters.size() != defaultFilter.size())
				totalRecordSize = mandateService.getMandatesByBiller(billerUser.getBiller()).size();
			else
				totalRecordSize = filterSize;

		} catch (ServerBusinessException e) {
			logger.error(e);
		}
		return getJQueryDatatableMandates(dtReq, mandatesData, filterSize, totalRecordSize);

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/download/mandate/xls", method = RequestMethod.GET)
	public ModelAndView exportMandate(HttpServletRequest request) {
		List<Mandate> mandates = new ArrayList<>();
		try {
			Map<String, Object> filters = (Map<String, Object>) request.getSession()
					.getAttribute(WebAppConstants.DT_FILTER);
			mandates = mandateService.findPaginatedMandates(filters, null, null);
		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		// return a view which will be resolved by an excel view resolver
		ModelMap model = new ModelMap();
		model.addAttribute(WebAppConstants.REPORT_TYPE, WebAppConstants.REPORT_TYPE_MANDATE);
		model.addAttribute(WebAppConstants.REPORT_OBJECT_TYPE, new Mandate());
		model.addAttribute("mandates", mandates);
		logger.info("Download Report Size::" + mandates.size());
		return new ModelAndView("excelView", model);

	}

	@RequestMapping(value = "/mandate/view/{id}", method = RequestMethod.GET)
	public String viewMandate(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model) {

		BillerUser billerUser = getCurrentBillerUser(request);
		Mandate mandate = null;
		try {
			mandate = mandateService.getMandateByBillerAndMandateId(id, billerUser.getBiller());
			model.addAttribute("rejectionReasons", mandateService.getRejectionReasons());
			model.addAttribute("mandate", mandate);
		} catch (ServerBusinessException e) {
			logger.fatal(e);
		}

		this.setViewMainHeading("View Mandate", model);
		this.setViewBoxHeading("Mandate Code - " + mandate.getMandateCode(), model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("/biller/mandate/list", "All Mandates"));
		breadCrumb.add(new OptionBean("#", mandate.getMandateCode()));

		this.setBreadCrumb(breadCrumb, model);
		return ("/mandate/view_mandate");
		// return mav;
	}

	@RequestMapping(value = "/mandate/edit/{id}", method = RequestMethod.GET)
	public ModelAndView modifyMandate(@PathVariable(value = "id") Long id, @ModelAttribute("mandate") Mandate mandate,
			HttpServletRequest request, Model model) {

		BillerUser billerUser = getCurrentBillerUser(request);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Mandate mandate = null;
		try {
			mandate = mandateService.getMandateByBillerAndMandateId(id, billerUser.getBiller());
			mandate.setValidityDateRange(sdf.format(mandate.getStartDate()) + " - " + sdf.format(mandate.getEndDate()));

		} catch (ServerBusinessException e) {
			logger.error("Unable to get mandate with Id=>" + id, e);
		}
		return modifyMandate(request, mandate, model);

	}

	private ModelAndView modifyMandate(HttpServletRequest request, @ModelAttribute("mandate") Mandate mandate,
			Model model) {
		ModelAndView mav = new ModelAndView();

		// SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		BillerUser billerUser = getCurrentBillerUser(request);
		try {

			List<Biller> billers = new ArrayList<>();
			billers.add(billerUser.getBiller());
			mav.addObject("mandate", mandate);
			// Biller biller=billerUser.getBiller();
			mav.addObject("billers", billers);
			mav.addObject("banks", bankService.getAllBanks());
			mav.addObject("products", productService.getProductsByBiller(billerUser.getBiller()));

			// mandate.setValidityDateRange(sdf.format(mandate.getStartDate())+"
			// - "+sdf.format(mandate.getEndDate()));

		} catch (ServerBusinessException e) {
			logger.fatal(e);
		}

		this.setViewMainHeading("Edit Mandate", model);
		this.setViewBoxHeading("Mandate Code - " + mandate.getMandateCode(), model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("/biller/mandate/list", "All Mandates"));
		breadCrumb.add(new OptionBean("#", mandate.getMandateCode()));

		this.setBreadCrumb(breadCrumb, model);

		// If the view is for suspension
		// if ("")
		mav.setViewName("/mandate/edit_mandate");
		return mav;
	}

	@RequestMapping(value = "/mandate/edit/{id}", method = RequestMethod.POST)
	public ModelAndView saveModifiedMandate(@PathVariable(value = "id") Long id,
			@ModelAttribute("mandate") @Valid Mandate mandate, BindingResult result,
			final RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {

		ModelAndView mav = new ModelAndView();
		// mav.setViewName("/mandate/edit_mandate");
		mandateValidator.validate(mandate, result);
		if (result.hasErrors()) {
			return modifyMandate(request, mandate, model);
			/*
			 * mav.addObject("mandate", mandate); return mav;
			 */
		}

		BillerUser billerUser = getCurrentBillerUser(request);

		String message = "An error has occured while modifying the mandate. Please try later.";
		String messageClass = "danger";
		try {

			Mandate oldMandate = mandateService.getMandateByBillerAndMandateId(id, billerUser.getBiller());

			String billerRCNumber = billerUser.getBiller().getCompany().getRcNumber();

			if (mandate.getUploadFile().getOriginalFilename() != null
					&& !mandate.getUploadFile().getOriginalFilename().equals("")) {// want
																					// to
																					// overwrite
																					// the
																					// existing
																					// file
				MultipartFile multipartFile = mandate.getUploadFile();

				// get all the files in the biller's directory
				File billerFiles = new File(mandateImagePath + File.separator + billerRCNumber + File.separator);

				long numberOfMandateFiles = Arrays.asList(billerFiles.listFiles()).stream().filter(f -> f.getName()
						.toLowerCase().contains(mandate.getMandateCode().toLowerCase().replace("/", ""))).count();

				String newFileName = String.format(mandate.getMandateCode().replace("/", "") + "_" + "%s.%s",
						numberOfMandateFiles, FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
				File newDestination = new File(
						mandateImagePath + File.separator + billerRCNumber + File.separator + newFileName);
				multipartFile.transferTo(newDestination);
				oldMandate.setMandateImage(newDestination.getName());

				logger.info("Destination " + mandateImagePath);
				logger.info("Extension " + FilenameUtils.getExtension(multipartFile.getOriginalFilename()));

			}

			String[] dateRange = mandate.getValidityDateRange().split("-");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			oldMandate.setStartDate(sdf.parse(dateRange[0].trim()));
			oldMandate.setEndDate(sdf.parse(dateRange[1].trim()));
			oldMandate.setDateModified(new Date());
			oldMandate.setStatus(mandateStatusService.getMandateStatusById(WebAppConstants.BILLER_INITIATE_MANDATE));
			oldMandate.setProduct(mandate.getProduct());
			oldMandate.setSubscriberCode(mandate.getSubscriberCode());
			oldMandate.setPayerAddress(mandate.getPayerAddress());
			oldMandate.setAmount(mandate.getAmount());
			oldMandate.setPhoneNumber(mandate.getPhoneNumber());
			oldMandate.setEmail(mandate.getEmail());
			oldMandate.setPayerName(mandate.getPayerName());
			oldMandate.setFrequency(mandate.getFrequency());
			oldMandate.setBank(mandate.getBank());
			oldMandate.setAccountName(mandate.getAccountName());
			oldMandate.setAccountNumber(mandate.getAccountNumber());
			oldMandate.setNarration(mandate.getNarration());
			oldMandate.setChannel(mandate.getChannel());
			oldMandate.setLastActionBy(getCurrentUser(request));
			mandateService.modifyMandate(oldMandate);
			message = "Mandate was successfully modified!";
			messageClass = "success";
		} catch (ServerBusinessException | IllegalStateException | IOException | ParseException e) {
			logger.error(e);
		}

		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addFlashAttribute("messageClass", messageClass);

		mav.setViewName("redirect:/biller/mandate/list");
		return mav;
	}

	private ModelAndView approveMandate(Long id, String action, final RedirectAttributes redirectAttributes,
			HttpServletRequest request) {

		ModelAndView mav = new ModelAndView();
		String message = "An error has occured while modifying the mandate. Please try later.";
		String messageClass = "danger";

		try {

			Mandate mandate = mandateService.getMandateByMandateId(id);
			mandate.setLastActionBy(getCurrentUser(request));
			logger.info("Role:" + getCurrentUser(request).getRole().getId().toString() + " Action :" + action
					+ " Status: " + mandate.getStatus().getId() + getCurrentUser(request).getRole().getName() + "  "
					+ getCurrentUser(request).getRole().getDescription());

			// Role:2 Action :approve Status: 1

			if ("approve".equalsIgnoreCase(action)) {
				// logger.info("1");
				if (mandate.getStatus().getId() == WebAppConstants.BANK_AUTHORIZE_MANDATE && getCurrentUser(request)
						.getRole().getId().toString().equalsIgnoreCase(WebAppConstants.BILLER_AUTHORIZER)) {
					// logger.info("2");
					// a mandate created by a bank to be authorized by biller
					mandate.setStatus(
							mandateStatusService.getMandateStatusById(WebAppConstants.BILLER_APPROVE_MANDATE));
					mandate.setApprovedBy(getCurrentUser(request));
					mandate.setDateApproved(new Date());
					mandate.setDateModified(new Date());
					mandate.setLastActionBy(getCurrentUser(request));
					message = "Mandate was successfully Approved!";
					messageClass = "success";

					mandateService.modifyMandate(mandate);
					mailUtility.sendMail(MailType.MANDATE_APPROVED, mandate);
				} else if (mandate.getStatus().getId() == WebAppConstants.BILLER_INITIATE_MANDATE
						&& getCurrentUser(request).getRole().getName()
								.equalsIgnoreCase(WebAppConstants.BILLER_AUTHORIZER)) {
					// logger.info("3");
					mandate.setStatus(mandateStatusService.getMandateStatusById(WebAppConstants.BILLER_AUTHORIZE_MANDATE));
					message = "Mandate was successfully Authorized!";
					
					//mandate.setAcceptedBy(getCurrentUser(request));
					//mandate.setDateAccepted(new Date());
					
					mandate.setAuthorizedBy(getCurrentUser(request));
					mandate.setDateAuthorized(new Date());
					
					mandate.setLastActionBy(getCurrentUser(request));
					
					mandate.setDateModified(new Date());
					messageClass = "success";

					mandateService.modifyMandate(mandate);
					mailUtility.sendMail(MailType.MANDATE_AUTHORIZED, mandate);
				}
				// mandate.setApprovedBy(getCurrentUser(request));
				// mandate.setDateApproved(new Date());
				// mandate.setDateModified(new Date());
				// message = "Mandate was successfully Approved!";
				// messageClass = "success";
				// mandateService.modifyMandate(mandate);
				// mailUtility.sendMail(MailType.MANDATE_APPROVED, mandate);
			} else if ("reject".equalsIgnoreCase(action)) {
				mandate.setStatus(mandateStatusService.getMandateStatusById(WebAppConstants.BILLER_REJECT_MANDATE));
				message = "Mandate was successfully Rejected!";
				messageClass = "success";
				mandate.setDateModified(new Date());
				mandate.setLastActionBy(getCurrentUser(request));
				/*
				 * mandate.setRejection(rejection); Rejection r= new
				 * Rejection(); r.setComment(comment); r.setDateRejected(new
				 * Date()); r.setUser(getCurrentUser(request));
				 * r.setRejectionReason(mandateService.getRejectionReasonById(
				 * rejectionReasonId)); mandate.setRejection(r);
				 */

				mandateService.modifyMandate(mandate);

			}

			if (mandate != null) {
				logger.info("Notification for mandate " + mandate.getMandateCode());

				Runnable r = () -> {
					applicationService.notifyMandateStatusAll(0, mandate, null);
				};
				executor.execute(r);

			}

		} catch (ServerBusinessException e) {
			e.printStackTrace();
			logger.error(e);
		}

		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addFlashAttribute("messageClass", messageClass);

		mav.setViewName("redirect:/biller/mandate/list");
		return mav;
	}

	@Secured(value = { WebAppConstants.ROLE_BILLER_AUTHORIZER })
	@RequestMapping(value = "/mandate/approve", method = RequestMethod.POST, params = "approve")
	public ModelAndView approveMandate(@RequestParam(value = "id", required = true) Long id,
			final RedirectAttributes redirectAttributes, HttpServletRequest request) {

		return approveMandate(id, "approve", redirectAttributes, request);

	}

	/*
	 * @Secured (value={ROLE_BILLER_AUTHORIZER})
	 * 
	 * @RequestMapping(value="/mandate/approve",method=RequestMethod.POST,
	 * params="reject") public ModelAndView
	 * rejectMandate(@RequestParam(value="id", required=true) Long id, final
	 * RedirectAttributes redirectAttributes, HttpServletRequest request){
	 * 
	 * return approveMandate(id, "reject",redirectAttributes,request);
	 * 
	 * }
	 */

	@Secured(value = { WebAppConstants.ROLE_BILLER_AUTHORIZER })
	@RequestMapping(value = "/mandate/approve", method = RequestMethod.POST, params = "reject")
	public ModelAndView rejectMandate(@RequestParam(value = "id", required = true) Long id,
			final RedirectAttributes redirectAttributes,
			@RequestParam(value = "comment", required = true) String comment,
			@RequestParam(value = "rejectionReason", required = true) Long rejectionReasonId,
			HttpServletRequest request) {

		ModelAndView mav = new ModelAndView();

		String message = "An error has occured while processing the request. Please try later.";
		String messageClass = "danger";

		try {

			Mandate mandate = mandateService.getMandateByMandateId(id);
			mandate.setDateModified(new Date());
			mandate.setLastActionBy(getCurrentUser(request));
			mandate.setStatus(mandateStatusService.getMandateStatusById(WebAppConstants.BILLER_REJECT_MANDATE));
			Rejection r = new Rejection();
			r.setComment(comment);
			r.setDateRejected(new Date());
			r.setUser(getCurrentUser(request));
			r.setRejectionReason(mandateService.getRejectionReasonById(rejectionReasonId));
			mandate.setRejection(r);
			message = "Mandate <b>" + mandate.getMandateCode() + "</b> was successfully Rejected!";
			messageClass = "success";

			mandateService.modifyMandate(mandate);
			mailUtility.sendMail(MailType.MANDATE_DISAPPROVED, mandate);

			if (mandate != null) {
				logger.info("Notification for mandate " + mandate.getMandateCode());
				Runnable runnable = () -> {
					applicationService.notifyMandateStatusAll(0, mandate, null);
				};
				executor.execute(runnable);
			}

		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addFlashAttribute("messageClass", messageClass);

		mav.setViewName("redirect:/biller/mandate/list");
		return mav;
	}

	@RequestMapping(value = "/product/list")
	public ModelAndView getBillerProducts(HttpServletRequest request, Model model,
			@ModelAttribute("product") Product product) {
		ModelAndView mav = new ModelAndView();
		List<Product> products = new ArrayList<>();
		BillerUser billerUser = getCurrentBillerUser(request);

		try {
			products = productService.getProductsByBiller(billerUser.getBiller());
		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		this.setViewMainHeading("Products <small>All Products belonging to a Biller</small>", model);
		this.setViewBoxHeading("Product Count (" + products.size() + ")", model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "All Products"));

		this.setBreadCrumb(breadCrumb, model);
		mav.addObject("products", products);
		mav.setViewName("/biller/list_product");
		return mav;
	}

	@RequestMapping(value = "/product/edit/{id}")
	public ModelAndView editProductForm(HttpServletRequest request, Model model, @PathVariable("id") Long productId) {
		ModelAndView mav = new ModelAndView();
		Product product = null;
		BillerUser billerUser = getCurrentBillerUser(request);

		try {
			product = productService.getProductsByBillerAndProductId(billerUser.getBiller(), productId);
		} catch (ServerBusinessException e) {
			logger.error(e);
		}
		this.setViewModalHeading("Modify Product", model);
		mav.addObject("product", product);
		mav.setViewName("/biller/modal/edit_product");
		return mav;
	}

	@RequestMapping(value = "/product/delete/{id}")
	public ModelAndView deleteProductAction(HttpServletRequest request, @PathVariable("id") Long productId,
			RedirectAttributes redirectAttribute) {
		ModelAndView mav = new ModelAndView();

		try {
			productService.deleteProduct(productId);
			redirectAttribute.addFlashAttribute("message", "Product deleted successfully");
			redirectAttribute.addFlashAttribute("messageClass", "success");
		} catch (Exception e) {
			logger.error(e);
			redirectAttribute.addFlashAttribute("message", "Unable to delete product, please try again later");
			redirectAttribute.addFlashAttribute("messageClass", "danger");
		}
		mav.setViewName("redirect:/biller/product/list");
		return mav;
	}

	@RequestMapping(value = "/product/edit", method = RequestMethod.POST)
	public ModelAndView editProductAction(HttpServletRequest request, Model model,
			@ModelAttribute("product") Product product, RedirectAttributes redirectAttribute) {
		ModelAndView mav = new ModelAndView();
		BillerUser billerUser = getCurrentBillerUser(request);

		try {
			product.setBiller(billerUser.getBiller());
			productService.updateProduct(product);
			redirectAttribute.addFlashAttribute("message", "Product updated successfully");
			redirectAttribute.addFlashAttribute("messageClass", "success");
		} catch (ServerBusinessException e) {
			logger.error(e);
			redirectAttribute.addFlashAttribute("message", "unable to update product, please try again later");
			redirectAttribute.addFlashAttribute("messageClass", "danger");
		}
		mav.setViewName("redirect:/biller/product/list");
		return mav;
	}

	@RequestMapping(value = "/user/add", method = RequestMethod.GET)
	@Secured(value = { WebAppConstants.ROLE_BILLER_ADMINISTRATOR })
	public ModelAndView createNewUser(@ModelAttribute("user") User user, HttpServletRequest request, Model model) {
		ModelAndView mav = new ModelAndView();

		this.setViewMainHeading("Create New User <small>Fill the form below to create a new user</small>", model);
		this.setViewBoxHeading("Enter User Details", model);
		List<Role> roles = new ArrayList<>();

		roles = roleService.getRolesByName(new String[] { WebAppConstants.ROLE_BILLER_AUTHORIZER,
				WebAppConstants.ROLE_BILLER_INITIATOR, WebAppConstants.ROLE_BILLER_AUDITOR });

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Add New User"));
		this.setBreadCrumb(breadCrumb, model);

		mav.addObject("action", "/biller/user/add");
		mav.addObject("roles", roles);
		mav.setViewName("/user/add_user");
		return mav;
	}

	@RequestMapping(value = "/user/list")
	public ModelAndView getUsers(HttpServletRequest request, Model model, @ModelAttribute("user") User user) {
		ModelAndView mav = new ModelAndView();

		List<Role> roles = roleService.getRolesByName(new String[] { WebAppConstants.ROLE_BILLER_INITIATOR,
				WebAppConstants.ROLE_BILLER_AUDITOR, WebAppConstants.ROLE_BILLER_AUTHORIZER });
		model.addAttribute("roles", roles);

		mav.setViewName("/user/list_user");
		this.setViewMainHeading("All Users <small>All Users belonging to a Bank together with their status</small>",
				model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "All Users"));
		mav.addObject("userDataTableUrl", "/biller/datatable/user/list");
		mav.addObject("updateUserUrl", "/biller/user/edit");
		mav.addObject("resetUserUrl", "/biller/user/reset");
		mav.addObject("createUserUrl", "/biller/user/add");
		mav.addObject("deleteUserUrl", "/biller/user/delete");
		this.setBreadCrumb(breadCrumb, model);
		return mav;
	}

	@Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	@RequestMapping(value = "/user/edit", method = RequestMethod.POST)
	public @ResponseBody String updateUser(HttpServletRequest request, @RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("roleId") Long roleId,
			@RequestParam("status") Byte status, @RequestParam("userId") Long userId) throws Exception {

		String res = "";
		try {
			User user = userService.getUserById(userId);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setRole(roleService.getRoleById(roleId));
			user.setStatus(status);
			user.setDateModified(new Date());
			userService.updateUser(user);
			res = WebAppConstants.AJAX_SUCCESS;
		} catch (Exception e) {
			res = WebAppConstants.AJAX_FAILED;
			throw new Exception(e);
		}
		return res;
	}

	@Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	@RequestMapping(value = "/user/add", method = RequestMethod.POST)
	public @ResponseBody String createNewUser(HttpServletRequest request, @RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("roleId") Long roleId,
			@RequestParam("status") Byte status, @RequestParam("email") String email) throws Exception {

		String res = "";
		try {
			BillerUser user = new BillerUser();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setRole(roleService.getRoleById(roleId));
			user.setStatus(status);
			user.setDateCreated(new Date());
			user.setBiller(getCurrentBillerUser(request).getBiller());
			user.setEmail(email);
			String password = Utilities.generatePassword();
			// user.setPassword(password);
			user.setPassword(sha1PasswordEncoder.encodePassword(password, ""));
			userService.addUser(user);
			res = WebAppConstants.AJAX_SUCCESS;
			Runnable r = () -> {
				user.setPassword(password);
				mailUtility.sendMail(MailType.NEW_USER, user);
			};
			executor.execute(r);
		} catch (Exception e) {
			res = WebAppConstants.AJAX_FAILED;
			throw new Exception(e);
		}
		return res;
	}

	/*
	 * @Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	 * 
	 * @RequestMapping(value = "/user/reset", method = RequestMethod.POST)
	 * public @ResponseBody String resetUser(HttpServletRequest
	 * request, @RequestParam("userId") Long userId) throws Exception {
	 * 
	 * String res = ""; try {
	 * 
	 * String password = Utilities.generatePassword();
	 * 
	 * User user = userService.getUserById(userId);
	 * user.setPassword(sha1PasswordEncoder.encodePassword(password, ""));
	 * user.setDateModified(new Date()); userService.updateUser(user); Runnable
	 * r = () -> { mailUtility.sendMail(MailType.RESET_USER_PASSWORD, user); };
	 * executor.execute(r); // res=AJAX_SUCCESS; } catch (Exception e) { //
	 * res=AJAX_FAILED; throw new Exception(e); } return res; }
	 */

	@Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	@RequestMapping(value = "/user/delete", method = RequestMethod.POST)
	public @ResponseBody String deleteNewUser(HttpServletRequest request, @RequestParam("userId") Long userId)
			throws Exception {

		String res = "";
		try {

			User user = userService.getUserById(userId);
			user.setStatus(WebAppConstants.USER_STATUS_INACTIVE);
			// userService.deletUser(userService.getUserById(userId));
			userService.updateUser(user);
			// userService.deletUser(userService.getUserById(userId));
			res = WebAppConstants.AJAX_SUCCESS;
		} catch (Exception e) {
			res = WebAppConstants.AJAX_FAILED;
			throw new Exception(e);
		}
		return res;
	}

	@ResponseBody
	@RequestMapping(value = "datatable/user/list", method = RequestMethod.POST, headers = {
			"Content-type=application/json" })
	public JQueryDataTableResponse<UserTable> getDatatableUsers(@RequestBody JQueryDataTableRequest dtReq,
			HttpServletRequest request) {

		List<User> users = new ArrayList<>();
		int totalRecordSize = 0;
		Integer filterSize = null;
		Map<String, Object> defaultFilter = new TreeMap<>();
		defaultFilter.put("biller", ((BillerUser) getCurrentUser(request)).getBiller());
		try {
			Map<String, Object> filters = buildJQueryDataTableFilter(dtReq);
			if (!filters.isEmpty()) {
				filters.putAll(defaultFilter);
				users = userService.findPaginatedUser(filters, dtReq.getStart(), dtReq.getLength());
				filterSize = userService.findPaginatedUser(filters, null, null).size();
			} else {

				users = userService.findPaginatedUser(defaultFilter, dtReq.getStart(), dtReq.getLength());
			}
			totalRecordSize = billerService.getUsersByBiller(((BillerUser) getCurrentUser(request)).getBiller()).size();
		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		return getJQueryDatatableUsers(dtReq, users, filterSize, totalRecordSize);
	}

	@RequestMapping(value = "/user/view/{id}", method = RequestMethod.GET)
	public String viewUser(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model) {

		BillerUser billerUser = getCurrentBillerUser(request);
		BillerUser theUser = null;
		theUser = billerService.getUserByBillerAndUserId(id, billerUser.getBiller());

		this.setViewMainHeading("View User", model);
		this.setViewBoxHeading("User details", model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("/biller/user/list", "All Users"));
		breadCrumb.add(new OptionBean("#", theUser.getEmail()));
		this.setBreadCrumb(breadCrumb, model);
		return ("/user/view_user");
		// return mav;
	}

	@RequestMapping(value = "/product/add", method = RequestMethod.POST)
	public @ResponseBody AjaxValidatorResponse saveNewProduct(HttpServletRequest request,
			@ModelAttribute("product") @Valid Product product, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {

		AjaxValidatorResponse res = new AjaxValidatorResponse();

		List<String> errors = new ArrayList<>();

		if (result.hasErrors()) {
			result.getAllErrors().stream().forEach(e -> {
				logger.debug(e.getObjectName() + "=>" + e.getDefaultMessage());
				errors.add(e.getDefaultMessage());
				res.setErrorMessageList(errors);
				res.setStatus("FAILED");
			});

		} else {
			try {
				BillerUser billerUser = getCurrentBillerUser(request);

				product.setBiller(billerUser.getBiller());
				product.setCreatedBy(billerUser);
				product.setCreatedDate(new Date());
				product.setStatus(WebAppConstants.STATUS_ACTIVE);

				productService.addProduct(product);
				res.setStatus("SUCCESS");
			} catch (ConstraintViolationException e) {
				errors.add("The product already exist for the selected Bank");
				res.setErrorMessageList(errors);
				res.setStatus("FAILED");
				logger.error(e);
			} catch (Exception e) {
				res.setStatus("FAILED");
				errors.add("An error has occurred while setting up you product. please try later");
				res.setErrorMessageList(errors);
				logger.error(e);
			}
		}

		return res;

	}

	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public ModelAndView viewReport(HttpServletRequest request, HttpServletResponse response, Model model) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/common/report");
		this.setViewMainHeading("Report Download <small>Filter by several criterias</small>", model);
		try {
			mav.addObject("banks", bankService.getAllBanks());
			mav.addObject("mandateStatuses", mandateStatusService.getAllMandateStatus());

		} catch (ServerBusinessException e) {
			logger.error(e);
		}
		mav.addObject("mandateReportUrl", "/biller/report/mandate/");
		mav.addObject("transactionReportUrl", "/biller/report/transaction/");

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Report"));
		this.setBreadCrumb(breadCrumb, model);
		return mav;

	}

	@RequestMapping(value = "/report/mandate/{format}", method = RequestMethod.GET, produces = "application/octet-stream")
	public ModelAndView downloadMandateReport(HttpServletRequest request, HttpServletResponse response, ModelMap model,
			@PathVariable("format") String reportFormat, @RequestParam("biller") String billerId,
			@RequestParam("product") String productId, @RequestParam("frequency") String frequency,
			@RequestParam("bank") String customerBank, @RequestParam("mandateStatus") String mandateStatus,
			@RequestParam("dateCreated") String dateCreated) throws IOException {

		billerId = String.valueOf(getCurrentBillerUser(request).getBiller().getId()); // protect
																						// biller
																						// from
																						// changing
																						// biller
																						// ID
		return generateMandateReport(request, response, model, reportFormat, billerId, productId, frequency,
				customerBank, mandateStatus, dateCreated);

	}

	@RequestMapping(value = "/report/transaction/{format}", method = RequestMethod.GET, produces = "application/octet-stream")
	public ModelAndView downloadTransactionReport(HttpServletRequest request, HttpServletResponse response,
			ModelMap model, @PathVariable("format") String reportFormat,
			@RequestParam("subscriberCode") String subscriberCode, @RequestParam("debitStatus") String debitStatus,
			@RequestParam("biller") String billerId, @RequestParam("product") String productId,
			@RequestParam("bank") String customerBank, @RequestParam("dateCreated") String dateCreated)
			throws IOException {

		billerId = String.valueOf(getCurrentBillerUser(request).getBiller().getId()); // protect
																						// biller
																						// from
																						// changing
																						// biller
																						// ID
		return generateTransactionReport(request, response, model, reportFormat, billerId, productId, customerBank,
				dateCreated, subscriberCode, debitStatus);

	}

	@Secured(WebAppConstants.ROLE_BILLER_INITIATOR)
	@RequestMapping(value = "/mandate/bulk/add", method = RequestMethod.GET)
	public ModelAndView createBulkMandates(HttpServletRequest request, HttpServletResponse response, Model model) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/mandate/add_bulk_mandate");
		this.setViewMainHeading("Mandate Bulk Creation <small>create several mandates by uploading a zip file</small>",
				model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Bulk Creation"));
		this.setBreadCrumb(breadCrumb, model);
		return mav;
	}

	@Secured(WebAppConstants.ROLE_BILLER_INITIATOR)
	@RequestMapping(value = "/mandate/bulk/add", method = RequestMethod.POST)
	public ModelAndView createBulkMandates(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("uploadFile") MultipartFile multipartFile) {

		String message = "";
		String messageClass = "danger";
		// do basic file validation
		if (multipartFile.getOriginalFilename() == null
				|| !multipartFile.getOriginalFilename().toLowerCase().endsWith(".zip")) {
			message = "Invalid file or file type. Only zip files are allowed!";
		} else {
			message = "Your file has been uploaded successfully, a report would be sent to you on completion.";
			messageClass = "success";
		}

		BillerController billerController = appContext.getBean(BillerController.class);
		billerController.processBulkMandateUpload(request, multipartFile);
		model.addAttribute("message", message);
		model.addAttribute("messageClass", messageClass);
		return createBulkMandates(request, response, model);

	}

	@Async // only billers are allowed to use this method
	public void processBulkMandateUpload(HttpServletRequest request, MultipartFile multipartFile) {
		final User user = getCurrentUser(request);
		String tempDir = request.getServletContext().getRealPath("/WEB-INF/temp/") + File.separator
				+ getCurrentUser(request).getId() + File.separator + System.currentTimeMillis(); // this
																									// path
																									// must
																									// always
																									// be
																									// unique

		if (!multipartFile.isEmpty()) {
			logger.info(String.format("---Setting temp upload path to [%s]---", tempDir));
			ZipFile zf = null;
			try {
				File tempDestination = new File(tempDir);
				logger.info("---tempDestination.exists()--" + tempDestination.exists());
				if (!tempDestination.exists()) {
					boolean made = tempDestination.mkdirs(); // create the new
																// temp path
					logger.info("---tempDestination.mkdirs()--" + made);
				}

				// transfer the file to the temp path
				multipartFile.transferTo(new File(tempDir + File.separator + multipartFile.getOriginalFilename()));

				FileUtility.unzip(tempDir + File.separator + multipartFile.getOriginalFilename(), tempDir);
				zf = new ZipFile(tempDir + File.separator + multipartFile.getOriginalFilename());
				Enumeration<? extends ZipEntry> entries = zf.entries();

				List<ZipEntry> recordFiles = Collections
						.list(entries).stream().filter(e -> e.getName().endsWith(".xls")
								|| e.getName().endsWith(".xlsx") || e.getName().endsWith(".csv"))
						.collect(Collectors.toList());
				if (recordFiles.size() != 1) {
					// reject based on the multiple files
					logger.warn("--No file ends with xls,xlsx or csv--");
				} else {
					ZipEntry ze = zf.getEntry(recordFiles.get(0).getName());
					List<Mandate> mandates = new ArrayList<>();
					if (ze.getName().endsWith(".xls") || ze.getName().endsWith(".xlsx")) {
						logger.warn("--file ends with xls or xls--");
						mandates = processExcelMandateUpload(zf.getInputStream(ze), ze.getName(), request);
					} else {

					}
					String[] extensions = new String[] { "png", "jpg", "jpeg", "pdf" };
					File allFiles = new File(tempDir);
					int successRecords = 0, failedRecords = 0;

					for (Mandate m : mandates) {
						InputStream input = null;
						OutputStream os = null;
						try {

							@SuppressWarnings("unchecked")
							List<File> files = (List<File>) FileUtils.listFiles(allFiles, extensions, true);
							Optional<File> fileFilter = files.stream()
									.filter(f -> f.getName().equals(m.getMandateImage())).findFirst();
							if (fileFilter.isPresent()) {
								File newFile = fileFilter.get();
								String originalFileName = newFile.getName();
								String contentType = "application/octet-stream";
								int size = (int) newFile.length();
								logger.info(String.format("-- " + size + " byte file to be uploaded [%s]--",
										newFile.getAbsolutePath()));
								FileItem fileItem = new DiskFileItem("file", contentType, false, originalFileName, size,
										newFile.getParentFile());
								input = new FileInputStream(newFile);
								os = fileItem.getOutputStream();
								int ret = input.read();
								while (ret != -1) {
									os.write(ret);
									ret = input.read();
								}
								MultipartFile mpf = new CommonsMultipartFile(fileItem);
								m.setUploadFile(mpf);
							} else {
								logger.warn("--Unable to upload image--");
							}
							m.setCreatedBy(user);
							m.setLastActionBy(user);
							m.setChannel(WebAppConstants.CHANNEL_PORTAL);
							m.setDateCreated(new Date());
							m.setRequestStatus(WebAppConstants.STATUS_ACTIVE);
							// m.setStatus(mandateStatusService.getMandateStatusById(WebAppConstants.BILLER_INITIATE_MANDATE));
							m.setMandateCode(commonService.generateMandateCode(
									m.getProduct().getBiller().getCompany().getRcNumber(),
									Long.toString(m.getProduct().getId())));
							if (m.getFrequency() > 0) {
								Date nextDebitDate = DateUtils.calculateNextDebitDate(m.getStartDate(), m.getEndDate(),
										m.getFrequency());
								m.setNextDebitDate(nextDebitDate == null ? DateUtils.lastSecondOftheDay(m.getEndDate())
										: DateUtils.nullifyTime(nextDebitDate));
							}
							uploadMandateImage(m); // upload the image

							m.setMandateAdviceSent(false);

							mandateService.addMandate(m);
							successRecords++;
						} catch (Exception e) {
							e.printStackTrace();
							failedRecords++;
							logger.error(null, e);
						} finally {
							try {
								os.flush();
								os.close();
							} catch (Exception e1) {
								logger.error(null, e1);
							}
						}
					}
					// zf.close();
					String message = "<head><style type=\"text/css\">" + "<!--<style type=\"text/css\"> <!--"
							+ ".style1 {font-family: \"Century Gothic\";font-size: 12px;}" + "--></style></head>"
							+ "<body>" + "<p class=\"style1\"><b>Hello " + user.getFirstName() + ",</b></p>"
							+ "<p class=\"style1\">Your file " + multipartFile.getOriginalFilename()
							+ " has been processed. The details are as below;</p><br/>"
							+ "<p class=\"style1\">Total number of mandates found: <b>" + mandates.size() + "</b></p>"
							+ "<p class=\"style1\">Total number of mandates saved: <b>" + successRecords + "</b></p>"
							+ "<p class=\"style1\">Total number of mandate failed to save: <b>" + failedRecords
							+ "</b></p>" + "<p class=\"style1\"></p>";

					if (failedRecords > 0) {
						message += "<p class=\"style1\">A file has been attached for the failed mandates.</p>";
					}
					message += "<br/>" + "</body>";
					try {
						JavaMailSenderImpl sender = (JavaMailSenderImpl) mailSender;
						MimeMessage msg = sender.createMimeMessage();
						MimeMessageHelper helper = new MimeMessageHelper(msg, true);
						helper.setSubject("Notification for Bulk mandate upload processing on CMMS");
						helper.setTo(user.getEmail());
						helper.setText(message, true);
						sender.send(msg);
					} catch (MessagingException e) {
						logger.error(null, e);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {

				/*
				 * Arrays.stream(tempDestination.list()).forEach(f->new
				 * File(f).delete()); Path fp = tempDestination.toPath(); try {
				 * Files.delete(fp); logger.info("Deleted::"); } catch
				 * (IOException e) { logger.error(null,e); }
				 */
				try {
					// FileUtils.deleteDirectory(new File(tempDir));
					zf.close();
					new File(tempDir).delete();
				} catch (Exception e) {
					logger.error(null, e);
				}
			}
		} else {
			logger.info("Upload is not a multipart object");
		}
	}

	@Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	@RequestMapping(value = "/notification/config", method = RequestMethod.GET)
	public ModelAndView notificationConfigPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/biller/notification_config");
		this.setViewMainHeading("Notification Configuration", model);

		List<OptionBean> breadCrumb = new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "Notification Configuration"));
		this.setBreadCrumb(breadCrumb, model);
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "datatable/notification/email", method = RequestMethod.POST, headers = {
			"Content-type=application/json" })
	public JQueryDataTableResponse<NotificationTable> getBillerNotifications(@RequestBody JQueryDataTableRequest dtReq,
			HttpServletRequest request) {

		List<Notification> notifications = new ArrayList<>();
		int totalRecordSize = 0;
		Integer filterSize = null;
		Map<String, Object> defaultFilter = new TreeMap<>();
		defaultFilter.put("biller.id", getCurrentBillerUser(request).getBiller().getId());
		try {
			Map<String, Object> filters = buildJQueryDataTableFilter(dtReq);
			if (!filters.isEmpty()) {
				filters.putAll(defaultFilter);
				notifications = notificationService.findPaginatedNotifications(filters, dtReq.getStart(),
						dtReq.getLength());
				filterSize = notificationService.findPaginatedNotifications(filters, null, null).size();
			} else {
				notifications = notificationService.findPaginatedNotifications(defaultFilter, dtReq.getStart(),
						dtReq.getLength());
			}
			totalRecordSize = notificationService.findPaginatedNotifications(defaultFilter, null, null).size();
		} catch (ServerBusinessException e) {
			logger.error(e);
		}

		return getJQueryDatatableNotifications(dtReq, notifications, filterSize, totalRecordSize);
	}

	@Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	@RequestMapping(value = "/notification/email/config/update", method = RequestMethod.POST)
	public @ResponseBody String updateEmailConfig(HttpServletRequest request, @RequestParam("id") Long id,
			@RequestParam("value") String value) {

		String res = "";
		try {
			Notification notification = notificationService.getNotificationById(id);
			if (notification == null)
				throw new ServerBusinessException(0, "Unable to getNotificationById(" + id + ")");
			notification.setId(id);
			notification.setEmailAddress(value);

			notificationService.updateNotification(notification);
			res = WebAppConstants.AJAX_SUCCESS;

		} catch (ServerBusinessException e) {
			res = WebAppConstants.AJAX_FAILED;
			logger.error(e);
		}
		return res;
	}

	@Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	@RequestMapping(value = "/notification/email/config/delete", method = RequestMethod.POST)
	public @ResponseBody String deleteEmailConfig(HttpServletRequest request, @RequestParam("id") Long id) {

		String res = "";
		try {
			Notification notification = notificationService.getNotificationById(id);
			if (notification == null)
				throw new ServerBusinessException(0, "Unable to getNotificationById(" + id + ")");
			notificationService.deleteNotification(notification);
			res = WebAppConstants.AJAX_SUCCESS;

		} catch (ServerBusinessException e) {
			res = WebAppConstants.AJAX_FAILED;
			logger.error(e);
		}
		return res;
	}

	@Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	@RequestMapping(value = "/user/reset", method = RequestMethod.POST)
	public @ResponseBody String resetUser(HttpServletRequest request, @RequestParam("userId") Long userId)
			throws Exception {

		String res = "";
		try {

			String password = Utilities.generatePassword();

			User user = userService.getUserById(userId);
			user.setPassword(sha1PasswordEncoder.encodePassword(password, ""));
			user.setStatus(WebAppConstants.USER_STATUS_ACTIVE);
			user.setDateModified(new Date());
			userService.updateUser(user);
			user.setPassword(password);
			Runnable r = () -> {
				mailUtility.sendMail(MailType.RESET_USER_PASSWORD, user);
			};
			executor.execute(r);
			res = WebAppConstants.AJAX_SUCCESS;
		} catch (Exception e) {
			res = WebAppConstants.AJAX_FAILED;
			throw new Exception(e);
		}
		return res;
	}

	@Secured(WebAppConstants.ROLE_BILLER_ADMINISTRATOR)
	@RequestMapping(value = { "/notification/email/config/add" }, method = RequestMethod.POST)
	public @ResponseBody String createEmailConfig(HttpServletRequest request, @RequestParam("status.id") Long id,
			@RequestParam("emailAddress") String email) throws Exception {

		String res = "";
		try {
			BillerNotification notification = new BillerNotification();
			notification.setBiller(getCurrentBillerUser(request).getBiller());
			notification.setDateCreated(new Date());
			notification.setDateModified(new Date());
			notification.setMandateStatus(mandateStatusService.getMandateStatusById(id));
			notification.setEmailAddress(email);
			notification.setCreatedBy(getCurrentUser(request));

			notificationService.createNewNotification(notification);
			res = WebAppConstants.AJAX_SUCCESS;
		} catch (Exception e) {
			res = WebAppConstants.AJAX_FAILED;
			throw new Exception(e);
		}
		return res;
	}

}
