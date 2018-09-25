package com.nibss.cmms.web.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nibss.cmms.app.service.ApplicationService;
import com.nibss.cmms.app.service.RestClient;
import com.nibss.cmms.domain.APITransaction;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.BankUser;
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.MandateStatus;
import com.nibss.cmms.domain.Product;
import com.nibss.cmms.domain.Transaction;
import com.nibss.cmms.domain.TransactionParam;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.service.CommonService;
import com.nibss.cmms.service.MandateService;
import com.nibss.cmms.service.MandateStatusService;
import com.nibss.cmms.service.ProductService;
import com.nibss.cmms.service.TransactionService;
import com.nibss.cmms.service.UserService;
import com.nibss.cmms.service.WebserviceNotificationService;
import com.nibss.cmms.utils.NotificationUtils;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;
import com.nibss.cmms.web.domain.APIAuthentication;
import com.nibss.cmms.web.domain.MandateRequest;
import com.nibss.cmms.web.domain.MandateRequestAll;
import com.nibss.cmms.web.domain.MandateRequestDTO;
import com.nibss.cmms.web.domain.MandateResponse;
import com.nibss.cmms.web.domain.MandateStatusRequest;
import com.nibss.cmms.web.domain.MandateUpdateRequest;
import com.nibss.cmms.web.domain.MandateUpdateRequestDTO;
import com.nibss.cmms.web.domain.PaymentRequest;
import com.nibss.cmms.web.domain.PaymentRequestDTO;
import com.nibss.cmms.web.domain.PaymentResponse;
import com.nibss.cmms.web.domain.PaymentStatusRequest;
import com.nibss.cmms.web.domain.PaymentStatusResponse;
import com.nibss.cmms.web.validation.AjaxValidatorResponse;
import com.nibss.cmms.web.validation.MandateValidator;

@EnableAsync
@RestController
@RequestMapping("/api")
public class ApplicationAPI extends BaseController implements WebAppConstants {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationAPI.class);

	@Autowired
	private NotificationUtils notificationUtils;

	@Autowired
	private RestClient restClient;

	@Autowired
	private WebserviceNotificationService webserviceNotificationService;

	@Autowired
	private ProductService productService;

	@Autowired
	private BankService bankService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private MandateService mandateService;

	@Autowired
	private MandateStatusService mandateStatusService;

	@Autowired
	private MandateValidator mandateValidator;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private ApplicationService applicationService;

	// generic
	private final static String SUCCESS = "00";

	private final static String INVALID_REQUEST = "01";

	private final static String MANDATE_NOT_FOUND = "25";

	// private final static String SYSTEM_ERROR = "99";
	private final static String SYSTEM_ERROR = "96";
	// mandate
	private final static String EXPIRED_MANDATE = "40";
	private final static String PREMATURE_MANDATE = "42";
	private final static String UNAPPROVED_MANDATE = "43";
	private final static String SUSPENDED_DELETED_MANDATE = "44";
	private final static String NOT_VARIABLE_FREQUENCY_MANDATE = "45";
	private final static String NOT_VARIABLE_AMOUNT_MANDATE = "46";

	private final static String DEBIT_AMOUNT_GREATER_THAN_MANDATE_AMOUNT = "41";

	// payment
	private final static String PAYMENT_NOT_STARTED = "02";

	private final static String PAYMENT_IN_PROGRESS = "03";

	private final static String PAYMENT_FAILED = "04";

	private final static String PAYMENT_REVRESED = "05";

	private Date dateFormatStartAndEndDate(String incomingDate) {
		System.out.println(incomingDate);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

		try {

			if (incomingDate.length() == 8) {
				return sdf1.parse(incomingDate);
			} else if (incomingDate.length() == 14) {
				return sdf2.parse(incomingDate);
			} else {
				return null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// @Secured(ROLE_BILLER_INITIATOR)
	@RequestMapping(value = "/mandate/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object[]> createMandate(@RequestBody MandateRequest mandateRequest, BindingResult result)
			throws ServerBusinessException {
		logger.info("/mandate/create");
		BillerUser user = null;
		try {
			user = _authenticateAPICall(mandateRequest.getApiAuthentication());
			if (mandateRequest.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		MandateResponse[] mandateResponses = new MandateResponse[mandateRequest.getMandateRequestBean().size()];

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}

		for (int index = 0; index < mandateResponses.length; index++) {
			MandateRequestDTO mandateRequestBean = mandateRequest.getMandateRequestBean().get(index);
			System.out.println("End Date :: " + mandateRequestBean.getEndDate());
			logger.info("End Date :: " + mandateRequestBean.getEndDate());
			MandateResponse mandateResponse = new MandateResponse();
			try {
				Product p = productService.getProductById(mandateRequestBean.getProductId());

				if (p == null || !p.getBiller().equals(user.getBiller()))
					throw new ServerBusinessException(0, "Product is not valid for this biller");

				Mandate mandate = new Mandate();
				mandate.setAccountNumber(mandateRequestBean.getAccountNumber());

				// mandate image is not mandaotry
				if (null != mandateRequestBean.getMandateImage().getName())
					mandate.setUploadFile(mandateRequestBean.getMandateImage());
				mandate.setProduct(p);

				if (p.getAmount() != null && p.getAmount().doubleValue() != 0) {
					mandate.setAmount(p.getAmount());
				} else {
					if (mandateRequestBean.getAmount() == null || mandateRequestBean.getAmount().doubleValue() == 0)
						throw new ServerBusinessException(0, "Amount is compulsory for the specified product");
					mandate.setAmount(mandateRequestBean.getAmount());
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				Bank bank = bankService.getBankByBankCode(mandateRequestBean.getBankCode());
				if (bank == null)
					throw new ServerBusinessException(0,
							"Invalid Bank code [" + mandateRequestBean.getBankCode() + "] specified");

				mandate.setBank(bank);
				mandate.setAccountName(mandateRequestBean.getAccountName());
				mandate.setPhoneNumber(mandateRequestBean.getPhoneNumber());
				mandate.setEmail(mandateRequestBean.getEmailAddress());
				mandate.setChannel(CHANNEL_API);
				mandate.setLastActionBy(user);
				mandate.setDateCreated(new Date());
				mandate.setDateModified(new Date());

				// mandate.set

				mandate.setCreatedBy(user);

				mandate.setPayerName(mandateRequestBean.getPayeeName());
				mandate.setPayerAddress(mandateRequestBean.getPayeeAddress());
				mandate.setNarration(mandateRequestBean.getNarration());
				mandate.setPayerAddress(mandateRequestBean.getPayerAddress());
				mandate.setPayerName(mandateRequestBean.getPayerName());
				mandate.setFrequency(mandateRequestBean.getFrequency());
				mandate.setStartDate(dateFormatStartAndEndDate(mandateRequestBean.getStartDate()));
				System.out.println("End Date :: " + mandateRequestBean.getEndDate());
				mandate.setEndDate(dateFormatStartAndEndDate(mandateRequestBean.getEndDate()));
				mandate.setSubscriberCode(mandateRequestBean.getSubscriberCode());
				// mandate.setStatus(mandateStatusService.getMandateStatusById(BILLER_AUTHORIZE_MANDATE));

				// if auto approve

				if (user.getBiller().getAutoApproveOnAPI() != null
						&& user.getBiller().getAutoApproveOnAPI().equalsIgnoreCase("1")) {
					mandate.setStatus(mandateStatusService.getMandateStatusById(BILLER_AUTHORIZE_MANDATE));
					//mandate.setDateAccepted(new Date());
					//mandate.setAcceptedBy(user);
					
					mandate.setDateAuthorized(new Date());
					mandate.setAuthorizedBy(user);
					// mandate.setDateModified(new Date());
				} else {
					mandate.setStatus(mandateStatusService.getMandateStatusById(BILLER_INITIATE_MANDATE));
				}
				// pass the validity date range in the validator
				// mandate.setValidityDateRange(sdf.format(mandateRequestBean.getStartDate())
				// + "-"
				// + sdf.format(mandateRequestBean.getEndDate()));

				mandate.setValidityDateRange(sdf.format(dateFormatStartAndEndDate(mandateRequestBean.getStartDate()))
						+ "-" + sdf.format(dateFormatStartAndEndDate(mandateRequestBean.getEndDate())));

				mandate.setMandateCode(commonService.generateMandateCode(user.getBiller().getCompany().getRcNumber(),
						Long.toString(mandateRequestBean.getProductId())));

				if (null != mandateRequestBean.getMandateImage().getName())
					uploadMandateImage(mandate);

				Errors errors = new org.springframework.validation.BindException(mandate, "mandate");
				mandateValidator.validate(mandate, errors);
				logger.info("error count" + errors.toString());
				if (errors.getErrorCount() > 0) {
					String[] validationErrors = new String[errors.getAllErrors().size()];
					IntStream.range(0, validationErrors.length).forEach(i -> {
						validationErrors[i] = errors.getAllErrors().get(i).getDefaultMessage();
					});
					throw new ServerBusinessException(0, Arrays.asList(validationErrors).toString());
				}
				Mandate m = mandateService.addMandate(mandate);

				try {
					applicationService.notifyMandateStatusAll(0, mandate, null);
				} catch (Exception ep) {
					ep.printStackTrace();
				}
				mandateResponse.setResponseCode(SUCCESS);
				mandateResponse.setResponseDescription("Mandate Created Successfully");
				mandateResponse.setMandateCode(m.getMandateCode());
				mandateResponse.setPhoneNumber(m.getPhoneNumber());
				mandateResponse.setSubscriberCode(m.getSubscriberCode());

			} catch (Exception e) {
				logger.error(user.getEmail(), e);
				mandateResponse.setResponseCode(SYSTEM_ERROR);
				mandateResponse.setResponseDescription(e.getMessage());
			}
			mandateResponses[index] = mandateResponse;
		}
		return new ResponseEntity<>(mandateResponses, HttpStatus.OK);

	}

	/**
	 *
	 * @param paymentRequest
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mandate/payment/initiate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> debitCustomer(@Valid @RequestBody PaymentRequest paymentRequest, BindingResult result)
			throws Exception {

		BillerUser user = null;
		try {
			user = _authenticateAPICall(paymentRequest.getApiAuthentication());
			if (paymentRequest.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}
		PaymentResponse response = new PaymentResponse();
		if (paymentRequest.getPaymentRequestBean().size() > 100)
			throw new ServerBusinessException(paymentRequest.getPaymentRequestBean().size(),
					"Request limit exceeded. Maximum allowed is 100");

		/*
		 * Set<String> set = mandateRequest.stream()
		 * .map(MandateAPIDebitRequest::getMandateCode)
		 * .collect(Collectors(TreeSet::new));
		 */
		Map<String, List<PaymentRequestDTO>> set = paymentRequest.getPaymentRequestBean().stream()
				.collect(Collectors.groupingBy(g -> g.getMandateCode()));

		Set<String> duplicates = set.keySet().stream().filter(s -> set.get(s).size() > 1).collect(Collectors.toSet());

		if (set.size() != paymentRequest.getPaymentRequestBean().size())
			throw new ServerBusinessException(0,
					String.format("Duplicate mandateCode found in request %s", duplicates.toString()));

		response.setPaymentRequestBean(new ArrayList<>());
		String batchId = paymentRequest.getBatchId();
		if (batchId == null) {
			batchId = "" + System.currentTimeMillis();
		}
		logger.info("Incoming Batch ID :: " + batchId);
		// String batchId = "" + System.currentTimeMillis();
		List<Transaction> successfulTransactions = new ArrayList<>();

		for (PaymentRequestDTO m : paymentRequest.getPaymentRequestBean()) {
			m.setResponseCode(validateMandate(user.getBiller().getId(), m));
			if (m.getResponseCode().equals(SUCCESS)) {
				try {
					APITransaction t = new APITransaction();
					// mandate amount is the upper bound so feel save to use the
					// request amount
					t.setAmount(m.getAmount());
					t.setStatus(1);
					t.setBatchId(batchId);
					t.setDateCreated(new Date());
					t.setCreatedBy(user);
					t.setIsClosed("0");
					Mandate mandate = mandateService.getMandateByMandateCode(m.getMandateCode());
					if (mandate != null) {
						t.setMandate(mandate);
						m.setNarration(mandate.getNarration());

					}
					t = (APITransaction) transactionService.saveTransaction(t);
					successfulTransactions.add(t);

				} catch (Exception e) {
					logger.error(null, e);
					m.setResponseCode(SYSTEM_ERROR);
				}
			}
			response.getPaymentRequestBean().add(m);
		}
		response.setBatchId(batchId);

		List<PaymentStatusResponse> paymentStatusResponse = new ArrayList<>();
		BillerUser localUser = user;

		CompletableFuture<Void> trxnProcessor = CompletableFuture
				.runAsync(() -> applicationService.transactionProcessor(successfulTransactions), taskExecutor);
		trxnProcessor.thenAccept((x) -> {
			logger.info("Push report");
			// Send report
			/*
			 * Map<String, Object> filters = new TreeMap<>();
			 * 
			 * filters.put("batchId", batchId);
			 * filters.put("mandate.product.biller.id",
			 * localUser.getBiller().getId()); List<Transaction> t =
			 * transactionService.getTransactions(filters); Map<String,
			 * Map<String, String>> params = buildPaymentStatusResponse(t);
			 * params.forEach((k, v) -> { PaymentStatusResponse r = new
			 * PaymentStatusResponse(); r.setMandateCode(k); r.setParams(v);
			 * paymentStatusResponse.add(r); }); if
			 * (localUser.getBiller().getNotificationUrl() != null &&
			 * !localUser.getBiller().getNotificationUrl().equals("")) {
			 * 
			 * }
			 */

			// call web service here
		});

		/*
		 * taskExecutor.execute(() -> {
		 * applicationService.transactionProcessor(successfulTransactions); });
		 */

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/mandate/status", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getMandateStatus(@RequestBody @Valid MandateStatusRequest mandateStatusRequest,
			BindingResult result) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BillerUser user = null;
		try {
			user = _authenticateAPICall(mandateStatusRequest.getApiAuthentication());
			if (mandateStatusRequest.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}
		Gson gson = new Gson();

		JsonArray jsonArray = new JsonArray();

		for (String m : mandateStatusRequest.getMandateCodes()) {
			String responseCode = SYSTEM_ERROR;

			JsonObject jsonObject = new JsonObject();
			try {
				Map<String, Object> filter = new TreeMap<>();
				filter.put("product.biller.id", user.getBiller().getId());
				filter.put("mandateCode", m);
				List<Mandate> mandates = mandateService.findPaginatedMandates(filter, null, null);
				if (mandates == null || mandates.isEmpty())
					responseCode = INVALID_REQUEST;
				// could not find mandate for this biller
				/*
				 * else if(mandates.get(0).getEndDate().before(new Date())){
				 * responseCode=EXPIRED_MANDATE; }
				 */else {
					for (Mandate mandate : mandates) {
						jsonObject.addProperty("accountName", mandate.getAccountName());
						jsonObject.addProperty("mandateCode", mandate.getMandateCode());
						jsonObject.addProperty("accountNumber", mandate.getAccountNumber());
						jsonObject.addProperty("bvn", mandate.getBvn());
						jsonObject.addProperty("email", mandate.getEmail());
						jsonObject.addProperty("narration", mandate.getNarration());
						jsonObject.addProperty("payerAddress", mandate.getPayerAddress());
						jsonObject.addProperty("payer", mandate.getPayerName());
						jsonObject.addProperty("phoneNumber", mandate.getPhoneNumber());
						jsonObject.addProperty("amount", mandate.getAmount());
						jsonObject.addProperty("subscriberCode", mandate.getSubscriberCode());
						jsonObject.addProperty("frequency", mandate.getFrequency());
						jsonObject.addProperty("kyc", mandate.getKycLevel());
						jsonObject.addProperty("bankCode", mandate.getBank().getBankCode());
						jsonObject.addProperty("bank", mandate.getBank().getBankName());
						jsonObject.addProperty("status", mandate.getRequestStatus());
						jsonObject.addProperty("endDate", sdf.format(mandate.getEndDate()));
						jsonObject.addProperty("startDate", sdf.format(mandate.getStartDate()));
						jsonObject.addProperty("productId", mandate.getProduct().getId());
						jsonObject.addProperty("productName", mandate.getProduct().getName());
						jsonObject.addProperty("billerId", mandate.getProduct().getBiller().getId());
						jsonObject.addProperty("biller", mandate.getProduct().getBiller().getCompany().getName());
						jsonObject.addProperty("rc", mandate.getProduct().getBiller().getCompany().getRcNumber());
						jsonObject.addProperty("billerAccountName", mandate.getProduct().getBiller().getAccountName());
						jsonObject.addProperty("billerAccountNumber",
								mandate.getProduct().getBiller().getAccountNumber());
						jsonObject.addProperty("billerBankCode",
								mandate.getProduct().getBiller().getBank().getBankCode());
						jsonObject.addProperty("billerBankName",
								mandate.getProduct().getBiller().getBank().getBankName());
						jsonObject.addProperty("workFlowStatus", mandate.getStatus().getId());
						jsonObject.addProperty("workFlowStatusDescription", mandate.getStatus().getName());
						responseCode = SUCCESS;

					}
				}
			} catch (ServerBusinessException e) {
				logger.error(null, e);
				responseCode = SYSTEM_ERROR;
			}
			// jsonObject.addProperty("mandateCode", m);
			jsonObject.addProperty("responseCode", responseCode);

			jsonArray.add(jsonObject);

		}

		return new ResponseEntity<>(gson.toJson(jsonArray), HttpStatus.OK);
	}

	/*
	 * All mandates with me as biller
	 */

	@RequestMapping(value = "/mandate/status/{approvalStatus}/{index}", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getBillerMandateByApprovalStatus(
			@PathVariable("approvalStatus") String approvalStatus, @PathVariable("index") String page,
			@RequestBody @Valid APIAuthentication apiAuthentication, BindingResult result) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BillerUser user = null;
		try {
			user = _authenticateAPICall(apiAuthentication);
			if (apiAuthentication == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}
		Gson gson = new Gson();

		JsonArray jsonArray = new JsonArray();

		int startIndex = 0;
		int endIndex = 100;
		try {
			int index_ = Integer.parseInt(page);
			if (index_ > 0) {
				startIndex = index_ * 100 - 100;
				endIndex = index_ * 100;
			}
		} catch (Exception ex) {

		}
		String responseCode = SYSTEM_ERROR;

		JsonObject jsonObject = new JsonObject();
		try {
			Map<String, Object> filter = new TreeMap<>();
			filter.put("product.biller.id", user.getBiller().getId());

			filter.put("requestStatus", 1);
			filter.put("status.id", Long.parseLong(approvalStatus));
			List<Mandate> mandates = mandateService.findPaginatedMandates(filter, startIndex, endIndex);
			if (mandates == null || mandates.isEmpty())
				responseCode = INVALID_REQUEST;
			// could not find mandate for this biller
			/*
			 * else if(mandates.get(0).getEndDate().before(new Date())){
			 * responseCode=EXPIRED_MANDATE; }
			 */else {
				for (Mandate mandate : mandates) {
					jsonObject.addProperty("accountName", mandate.getAccountName());
					jsonObject.addProperty("mandateCode", mandate.getMandateCode());
					jsonObject.addProperty("accountNumber", mandate.getAccountNumber());
					jsonObject.addProperty("bvn", mandate.getBvn());
					jsonObject.addProperty("email", mandate.getEmail());
					jsonObject.addProperty("narration", mandate.getNarration());
					jsonObject.addProperty("payerAddress", mandate.getPayerAddress());
					jsonObject.addProperty("payer", mandate.getPayerName());
					jsonObject.addProperty("phoneNumber", mandate.getPhoneNumber());
					jsonObject.addProperty("amount", mandate.getAmount());
					jsonObject.addProperty("subscriberCode", mandate.getSubscriberCode());
					jsonObject.addProperty("frequency", mandate.getFrequency());
					jsonObject.addProperty("kyc", mandate.getKycLevel());
					jsonObject.addProperty("bankCode", mandate.getBank().getBankCode());
					jsonObject.addProperty("bank", mandate.getBank().getBankName());
					jsonObject.addProperty("status", mandate.getRequestStatus());
					jsonObject.addProperty("endDate", sdf.format(mandate.getEndDate()));
					jsonObject.addProperty("startDate", sdf.format(mandate.getStartDate()));
					jsonObject.addProperty("productId", mandate.getProduct().getId());
					jsonObject.addProperty("productName", mandate.getProduct().getName());
					jsonObject.addProperty("billerId", mandate.getProduct().getBiller().getId());
					jsonObject.addProperty("biller", mandate.getProduct().getBiller().getCompany().getName());
					jsonObject.addProperty("rc", mandate.getProduct().getBiller().getCompany().getRcNumber());
					jsonObject.addProperty("billerAccountName", mandate.getProduct().getBiller().getAccountName());
					jsonObject.addProperty("billerAccountNumber", mandate.getProduct().getBiller().getAccountNumber());
					jsonObject.addProperty("billerBankCode", mandate.getProduct().getBiller().getBank().getBankCode());
					jsonObject.addProperty("billerBankName", mandate.getProduct().getBiller().getBank().getBankName());
					jsonObject.addProperty("workFlowStatus", mandate.getStatus().getId());
					jsonObject.addProperty("workFlowStatusDescription", mandate.getStatus().getName());
					responseCode = SUCCESS;
					jsonObject.addProperty("responseCode", responseCode);

					jsonArray.add(jsonObject);
				}
			}
		} catch (ServerBusinessException e) {
			logger.error(null, e);
			responseCode = SYSTEM_ERROR;
		}
		// jsonObject.addProperty("mandateCode", m);

		return new ResponseEntity<>(gson.toJson(jsonArray), HttpStatus.OK);
	}

	/*
	 * All mandates with me as biller
	 */

	@RequestMapping(value = "/mandate/all", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllBillerMandate(@RequestBody @Valid MandateRequestAll mandateRequestAll,
			BindingResult result) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BillerUser user = null;
		try {
			user = _authenticateAPICall(mandateRequestAll.getApiAuthentication());
			if (mandateRequestAll.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}
		Gson gson = new Gson();

		JsonArray jsonArray = new JsonArray();

		for (String m : mandateRequestAll.getSubscriberCodes()) {
			String responseCode = SYSTEM_ERROR;

			JsonObject jsonObject = new JsonObject();
			try {
				Map<String, Object> filter = new TreeMap<>();
				filter.put("product.biller.id", user.getBiller().getId());
				filter.put("subscriberCode", m);
				filter.put("requestStatus", 1);
				List<Mandate> mandates = mandateService.findPaginatedMandates(filter, null, null);
				if (mandates == null || mandates.isEmpty())
					responseCode = INVALID_REQUEST;
				// could not find mandate for this biller
				/*
				 * else if(mandates.get(0).getEndDate().before(new Date())){
				 * responseCode=EXPIRED_MANDATE; }
				 */else {
					for (Mandate mandate : mandates) {
						jsonObject.addProperty("accountName", mandate.getAccountName());
						jsonObject.addProperty("mandateCode", mandate.getMandateCode());
						jsonObject.addProperty("accountNumber", mandate.getAccountNumber());
						jsonObject.addProperty("bvn", mandate.getBvn());
						jsonObject.addProperty("email", mandate.getEmail());
						jsonObject.addProperty("narration", mandate.getNarration());
						jsonObject.addProperty("payerAddress", mandate.getPayerAddress());
						jsonObject.addProperty("payer", mandate.getPayerName());
						jsonObject.addProperty("phoneNumber", mandate.getPhoneNumber());
						jsonObject.addProperty("amount", mandate.getAmount());
						jsonObject.addProperty("subscriberCode", mandate.getSubscriberCode());
						jsonObject.addProperty("frequency", mandate.getFrequency());
						jsonObject.addProperty("kyc", mandate.getKycLevel());
						jsonObject.addProperty("bankCode", mandate.getBank().getBankCode());
						jsonObject.addProperty("bank", mandate.getBank().getBankName());
						jsonObject.addProperty("status", mandate.getRequestStatus());
						jsonObject.addProperty("endDate", sdf.format(mandate.getEndDate()));
						jsonObject.addProperty("startDate", sdf.format(mandate.getStartDate()));
						jsonObject.addProperty("productId", mandate.getProduct().getId());
						jsonObject.addProperty("productName", mandate.getProduct().getName());
						jsonObject.addProperty("billerId", mandate.getProduct().getBiller().getId());
						jsonObject.addProperty("biller", mandate.getProduct().getBiller().getCompany().getName());
						jsonObject.addProperty("rc", mandate.getProduct().getBiller().getCompany().getRcNumber());
						jsonObject.addProperty("billerAccountName", mandate.getProduct().getBiller().getAccountName());
						jsonObject.addProperty("billerAccountNumber",
								mandate.getProduct().getBiller().getAccountNumber());
						jsonObject.addProperty("billerBankCode",
								mandate.getProduct().getBiller().getBank().getBankCode());
						jsonObject.addProperty("billerBankName",
								mandate.getProduct().getBiller().getBank().getBankName());
						jsonObject.addProperty("workFlowStatus", mandate.getStatus().getId());
						jsonObject.addProperty("workFlowStatusDescription", mandate.getStatus().getName());
						responseCode = SUCCESS;

					}
				}
			} catch (ServerBusinessException e) {
				logger.error(null, e);
				responseCode = SYSTEM_ERROR;
			}
			// jsonObject.addProperty("mandateCode", m);
			jsonObject.addProperty("responseCode", responseCode);

			jsonArray.add(jsonObject);

		}

		return new ResponseEntity<>(gson.toJson(jsonArray), HttpStatus.OK);
	}

	/*
	 * All mandates with me as payer's bank
	 */

	@RequestMapping(value = "/mandate/payerbank/all", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllPayerBankMandate(@RequestBody @Valid MandateRequestAll mandateRequestAll,
			BindingResult result) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BankUser user = null;
		try {
			user = _authenticateBankAPICall(mandateRequestAll.getApiAuthentication());
			if (mandateRequestAll.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}
		Gson gson = new Gson();

		JsonArray jsonArray = new JsonArray();

		for (String m : mandateRequestAll.getSubscriberCodes()) {
			String responseCode = SYSTEM_ERROR;

			JsonObject jsonObject = new JsonObject();
			try {
				Map<String, Object> filter = new TreeMap<>();
				filter.put("bank.bankcode", user.getBank().getBankCode());
				// filter.put("subscriberCode", m);
				// filter.put("requestStatus", 1);
				List<Mandate> mandates = mandateService.findPaginatedMandates(filter, null, null);
				if (mandates == null || mandates.isEmpty())
					responseCode = INVALID_REQUEST;
				// could not find mandate for this biller
				/*
				 * else if(mandates.get(0).getEndDate().before(new Date())){
				 * responseCode=EXPIRED_MANDATE; }
				 */else {
					for (Mandate mandate : mandates) {
						jsonObject.addProperty("accountName", mandate.getAccountName());
						jsonObject.addProperty("mandateCode", mandate.getMandateCode());
						jsonObject.addProperty("accountNumber", mandate.getAccountNumber());
						jsonObject.addProperty("bvn", mandate.getBvn());
						jsonObject.addProperty("email", mandate.getEmail());
						jsonObject.addProperty("narration", mandate.getNarration());
						jsonObject.addProperty("payerAddress", mandate.getPayerAddress());
						jsonObject.addProperty("payer", mandate.getPayerName());
						jsonObject.addProperty("phoneNumber", mandate.getPhoneNumber());
						jsonObject.addProperty("amount", mandate.getAmount());
						jsonObject.addProperty("subscriberCode", mandate.getSubscriberCode());
						jsonObject.addProperty("frequency", mandate.getFrequency());
						jsonObject.addProperty("kyc", mandate.getKycLevel());
						jsonObject.addProperty("bankCode", mandate.getBank().getBankCode());
						jsonObject.addProperty("bank", mandate.getBank().getBankName());
						jsonObject.addProperty("status", mandate.getRequestStatus());
						jsonObject.addProperty("endDate", sdf.format(mandate.getEndDate()));
						jsonObject.addProperty("startDate", sdf.format(mandate.getStartDate()));
						jsonObject.addProperty("productId", mandate.getProduct().getId());
						jsonObject.addProperty("productName", mandate.getProduct().getName());
						jsonObject.addProperty("billerId", mandate.getProduct().getBiller().getId());
						jsonObject.addProperty("biller", mandate.getProduct().getBiller().getCompany().getName());
						jsonObject.addProperty("rc", mandate.getProduct().getBiller().getCompany().getRcNumber());
						jsonObject.addProperty("billerAccountName", mandate.getProduct().getBiller().getAccountName());
						jsonObject.addProperty("billerAccountNumber",
								mandate.getProduct().getBiller().getAccountNumber());
						jsonObject.addProperty("billerBankCode",
								mandate.getProduct().getBiller().getBank().getBankCode());
						jsonObject.addProperty("billerBankName",
								mandate.getProduct().getBiller().getBank().getBankName());
						jsonObject.addProperty("workFlowStatus", mandate.getStatus().getId());
						jsonObject.addProperty("workFlowStatusDescription", mandate.getStatus().getName());
						responseCode = SUCCESS;

					}
				}
			} catch (ServerBusinessException e) {
				logger.error(null, e);
				responseCode = SYSTEM_ERROR;
			}
			// jsonObject.addProperty("mandateCode", m);
			jsonObject.addProperty("responseCode", responseCode);

			jsonArray.add(jsonObject);

		}

		return new ResponseEntity<>(gson.toJson(jsonArray), HttpStatus.OK);
	}

	/*
	 * All mandates with me as payer's bank
	 */

	@RequestMapping(value = "/mandate/billerbank/all", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllBillerBankMandate(@RequestBody @Valid MandateRequestAll mandateRequestAll,
			BindingResult result) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BankUser user = null;
		try {
			user = _authenticateBankAPICall(mandateRequestAll.getApiAuthentication());
			if (mandateRequestAll.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}
		Gson gson = new Gson();

		JsonArray jsonArray = new JsonArray();

		for (String m : mandateRequestAll.getSubscriberCodes()) {
			String responseCode = SYSTEM_ERROR;

			JsonObject jsonObject = new JsonObject();
			try {
				Map<String, Object> filter = new TreeMap<>();
				filter.put("product.biller.bank.bankcode", user.getBank().getBankCode());
				// filter.put("subscriberCode", m);
				// filter.put("requestStatus", 1);
				List<Mandate> mandates = mandateService.findPaginatedMandates(filter, null, null);
				if (mandates == null || mandates.isEmpty())
					responseCode = INVALID_REQUEST;
				// could not find mandate for this biller
				/*
				 * else if(mandates.get(0).getEndDate().before(new Date())){
				 * responseCode=EXPIRED_MANDATE; }
				 */else {
					for (Mandate mandate : mandates) {
						jsonObject.addProperty("accountName", mandate.getAccountName());
						jsonObject.addProperty("mandateCode", mandate.getMandateCode());
						jsonObject.addProperty("accountNumber", mandate.getAccountNumber());
						jsonObject.addProperty("bvn", mandate.getBvn());
						jsonObject.addProperty("email", mandate.getEmail());
						jsonObject.addProperty("narration", mandate.getNarration());
						jsonObject.addProperty("payerAddress", mandate.getPayerAddress());
						jsonObject.addProperty("payer", mandate.getPayerName());
						jsonObject.addProperty("phoneNumber", mandate.getPhoneNumber());
						jsonObject.addProperty("amount", mandate.getAmount());
						jsonObject.addProperty("subscriberCode", mandate.getSubscriberCode());
						jsonObject.addProperty("frequency", mandate.getFrequency());
						jsonObject.addProperty("kyc", mandate.getKycLevel());
						jsonObject.addProperty("bankCode", mandate.getBank().getBankCode());
						jsonObject.addProperty("bank", mandate.getBank().getBankName());
						jsonObject.addProperty("status", mandate.getRequestStatus());
						jsonObject.addProperty("endDate", sdf.format(mandate.getEndDate()));
						jsonObject.addProperty("startDate", sdf.format(mandate.getStartDate()));
						jsonObject.addProperty("productId", mandate.getProduct().getId());
						jsonObject.addProperty("productName", mandate.getProduct().getName());
						jsonObject.addProperty("billerId", mandate.getProduct().getBiller().getId());
						jsonObject.addProperty("biller", mandate.getProduct().getBiller().getCompany().getName());
						jsonObject.addProperty("rc", mandate.getProduct().getBiller().getCompany().getRcNumber());
						jsonObject.addProperty("billerAccountName", mandate.getProduct().getBiller().getAccountName());
						jsonObject.addProperty("billerAccountNumber",
								mandate.getProduct().getBiller().getAccountNumber());
						jsonObject.addProperty("billerBankCode",
								mandate.getProduct().getBiller().getBank().getBankCode());
						jsonObject.addProperty("billerBankName",
								mandate.getProduct().getBiller().getBank().getBankName());
						jsonObject.addProperty("workFlowStatus", mandate.getStatus().getId());
						jsonObject.addProperty("workFlowStatusDescription", mandate.getStatus().getName());
						responseCode = SUCCESS;

					}
				}
			} catch (ServerBusinessException e) {
				logger.error(null, e);
				responseCode = SYSTEM_ERROR;
			}
			// jsonObject.addProperty("mandateCode", m);
			jsonObject.addProperty("responseCode", responseCode);

			jsonArray.add(jsonObject);

		}

		return new ResponseEntity<>(gson.toJson(jsonArray), HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/mandate/payment/status/{batchId}", consumes = {
	 * "application/json" }, method = RequestMethod.POST, produces =
	 * MediaType.APPLICATION_JSON_VALUE) public ResponseEntity<Object>
	 * getPaymentStatus(@PathVariable("batchId") String batchId,
	 * 
	 * @RequestBody @Valid PaymentStatusRequest paymentStatusRequest,
	 * BindingResult result) throws Exception {
	 * 
	 * BillerUser user = null; try { user =
	 * _authenticateAPICall(paymentStatusRequest.getApiAuthentication()); if
	 * (paymentStatusRequest.getApiAuthentication() == null || user == null) {
	 * throw new ServerBusinessException(0, "Authentication Failed"); } } catch
	 * (Exception e) { logger.error(null, e); throw new
	 * ServerBusinessException(0, "An authentication error has occurred"); }
	 * 
	 * if (result.hasErrors()) { String[] validationErrors = new
	 * String[result.getAllErrors().size()]; IntStream.range(0,
	 * validationErrors.length).forEach(index -> { validationErrors[index] =
	 * result.getAllErrors().get(index).getDefaultMessage(); }); return new
	 * ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST); }
	 * 
	 * Map<String, Object> filters = new TreeMap<>();
	 * List<PaymentStatusResponse> response = new ArrayList<>();
	 * 
	 * if (paymentStatusRequest != null &&
	 * !paymentStatusRequest.getMandateCodes().isEmpty()) { for (String m :
	 * paymentStatusRequest.getMandateCodes()) { filters.put("batchId",
	 * batchId); filters.put("mandate.mandateCode", m);
	 * filters.put("mandate.product.biller.id", user.getBiller().getId());
	 * PaymentStatusResponse res = new PaymentStatusResponse();
	 * List<Transaction> t = transactionService.getTransactions(filters); if (t
	 * != null && !t.isEmpty()) { Map<String, Map<String, String>> params =
	 * buildPaymentStatusResponse(t); res.setParams(params.get(m));
	 * 
	 * } res.setMandateCode(m); response.add(res);
	 * 
	 * } } else { filters.put("batchId", batchId);
	 * filters.put("mandate.product.biller.id", user.getBiller().getId());
	 * List<Transaction> t = transactionService.getTransactions(filters);
	 * Map<String, Map<String, String>> params = buildPaymentStatusResponse(t);
	 * params.forEach((k, v) -> { PaymentStatusResponse r = new
	 * PaymentStatusResponse(); r.setMandateCode(k); r.setParams(v);
	 * response.add(r); }); } return new ResponseEntity<>(response,
	 * HttpStatus.OK); }
	 */

	/*
	 * returns transactions where requesting bank is the bank of the customer
	 * 
	 */
	@RequestMapping(value = "/mandate/payerbank/transaction/status/{startDate}/{endDate}", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getCustomerPaymentTransactions(@PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestBody @Valid PaymentStatusRequest paymentStatusRequest,
			BindingResult result) throws Exception {

		BankUser user = null;

		try {
			user = _authenticateBankAPICall(paymentStatusRequest.getApiAuthentication());
			if (paymentStatusRequest.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}

		Map<String, Object> filters = new TreeMap<>();
		filters.put("mandate.bank.bankcode", user.getBank().getBankCode());
		// try to check if batch id is an instance of date

		try {
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
			sdFormat.parse(startDate);
			filters.put("dateCreated", startDate + "000000");
			filters.put("dateCreated", endDate + "235959");

		} catch (Exception ex) {// filters.put("batchId", batchId);

		}

		// filters.put("batchId", batchId);
		List<PaymentStatusResponse> response = new ArrayList<>();

		List<Transaction> t = transactionService.getTransactions(filters);
		Map<String, Map<String, String>> params = buildTransactionStatusResponse(t);
		params.forEach((k, v) -> {
			PaymentStatusResponse r = new PaymentStatusResponse();
			r.setMandateCode(k);
			r.setParams(v);
			response.add(r);
		});

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/*
	 * returns transactions where requesting bank is the bank of the customer
	 * 
	 */
	@RequestMapping(value = "/mandate/billerbank/transaction/status/{startDate}/{endDate}", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getBillerBankPaymentTransactions(@PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestBody @Valid PaymentStatusRequest paymentStatusRequest,
			BindingResult result) throws Exception {

		BankUser user = null;

		try {
			user = _authenticateBankAPICall(paymentStatusRequest.getApiAuthentication());
			if (paymentStatusRequest.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}

		Map<String, Object> filters = new TreeMap<>();
		filters.put("mandate.product.biller.bank.bankcode", user.getBank().getBankCode());
		// try to check if batch id is an instance of date

		try {
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
			sdFormat.parse(startDate);
			filters.put("dateCreated", startDate + "000000");
			filters.put("dateCreated", endDate + "235959");

		} catch (Exception ex) {// filters.put("batchId", batchId);

		}

		// filters.put("batchId", batchId);
		List<PaymentStatusResponse> response = new ArrayList<>();

		List<Transaction> t = transactionService.getTransactions(filters);
		Map<String, Map<String, String>> params = buildTransactionStatusResponse(t);
		params.forEach((k, v) -> {
			PaymentStatusResponse r = new PaymentStatusResponse();
			r.setMandateCode(k);
			r.setParams(v);
			response.add(r);
		});

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/mandate/payment/status/{batchId}", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getPaymentStatus(@PathVariable("batchId") String batchId,
			@RequestBody @Valid PaymentStatusRequest paymentStatusRequest, BindingResult result) throws Exception {

		BillerUser user = null;

		try {
			user = _authenticateAPICall(paymentStatusRequest.getApiAuthentication());
			if (paymentStatusRequest.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}

		Map<String, Object> filters = new TreeMap<>();
		filters.put("mandate.product.biller.id", user.getBiller().getId());
		// try to check if batch id is an instance of date
		/*
		 * try { SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
		 * sdFormat.parse(batchId); filters.put("dateCreated", batchId +
		 * "000000"); filters.put("dateCreated", batchId + "235959");
		 * 
		 * } catch (Exception ex) { filters.put("batchId", batchId);
		 * 
		 * }
		 */
		filters.put("batchId", batchId);
		List<PaymentStatusResponse> response = new ArrayList<>();

		List<APITransaction> t = transactionService.getAPITransactions(filters);
		Map<String, Map<String, String>> params = buildPaymentStatusResponse(t);
		params.forEach((k, v) -> {
			PaymentStatusResponse r = new PaymentStatusResponse();
			r.setMandateCode(k);
			r.setParams(v);
			response.add(r);
		});
		/*
		 * if (paymentStatusRequest != null &&
		 * !paymentStatusRequest.getMandateCodes().isEmpty()) { for (String m :
		 * paymentStatusRequest.getMandateCodes()) { filters.put("batchId",
		 * batchId); filters.put("mandate.mandateCode", m);
		 * filters.put("mandate.product.biller.id", user.getBiller().getId());
		 * PaymentStatusResponse res = new PaymentStatusResponse();
		 * List<Transaction> t = transactionService.getTransactions(filters); if
		 * (t != null && !t.isEmpty()) { Map<String, Map<String, String>> params
		 * = buildPaymentStatusResponse(t); res.setParams(params.get(m));
		 * 
		 * } res.setMandateCode(m); response.add(res);
		 * 
		 * } } else { filters.put("batchId", batchId);
		 * filters.put("mandate.product.biller.id", user.getBiller().getId());
		 * List<Transaction> t = transactionService.getTransactions(filters);
		 * Map<String, Map<String, String>> params =
		 * buildPaymentStatusResponse(t); params.forEach((k, v) -> {
		 * PaymentStatusResponse r = new PaymentStatusResponse();
		 * r.setMandateCode(k); r.setParams(v); response.add(r); }); }
		 */
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private Map<String, Map<String, String>> buildPaymentStatusResponse(List<APITransaction> transactions) {
		Map<String, Map<String, String>> returnMap = new TreeMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		transactions.stream().forEach(t -> {

			Map<String, String> params = new HashMap<>();
			TransactionParam tParam = null;
			if (t.getTransactionParam() != null && !t.getTransactionParam().isEmpty()) {
				tParam = t.getTransactionParam().stream().findFirst().get();
			}

			if (tParam != null) {
				params.put("transactionId", tParam.getSessionId().substring(6));
				params.put("valueDate", sdf.format(tParam.getDateCreated()));
				params.put("responseCode", tParam.getDebitResponseCode());
			}
			// params.put("status", String.valueOf(t.getStatus()));

			params.put("requestDate", sdf.format(t.getDateCreated()));
			params.put("batchId", ((APITransaction) t).getBatchId());
			params.put("mandateCode", t.getMandate().getMandateCode());

			returnMap.put(t.getMandate().getMandateCode(), params);
		});
		return returnMap;
	}

	private Map<String, Map<String, String>> buildTransactionStatusResponse(List<Transaction> transactions) {
		Map<String, Map<String, String>> returnMap = new TreeMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		transactions.stream().forEach(t -> {

			Map<String, String> params = new HashMap<>();
			TransactionParam tParam = null;
			if (t.getTransactionParam() != null && !t.getTransactionParam().isEmpty()) {
				tParam = t.getTransactionParam().stream().findFirst().get();
			}

			if (tParam != null) {
				params.put("sessionId", tParam.getSessionId().substring(6));
				params.put("valueDate", sdf.format(tParam.getDateCreated()));
				params.put("responseCodeDebit", tParam.getDebitResponseCode());
				params.put("responseCodeCredit", tParam.getCreditResponseCode());
			}
			// params.put("status", String.valueOf(t.getStatus()));

			params.put("requestDate", sdf.format(t.getDateCreated()));
			params.put("lastDebitDate", sdf.format(t.getLastDebitDate()));
			params.put("Amount", t.getAmount().toPlainString());
			params.put("numberOfDebitTrials", "" + t.getNumberOfTrials());
			params.put("numberOfCreditTrials", "" + t.getNumberOfCreditTrials());
			params.put("mandateCode", t.getMandate().getMandateCode());
			params.put("customerAccountNumber", t.getMandate().getAccountNumber());
			params.put("customerAccountName", t.getMandate().getAccountName());
			params.put("customerBankName", t.getMandate().getBank().getBankName());
			params.put("productName", t.getMandate().getProduct().getName());
			params.put("billerAccountName", t.getMandate().getProduct().getBiller().getAccountName());
			params.put("billerAccountNumber", t.getMandate().getProduct().getBiller().getAccountNumber());
			params.put("billerName", t.getMandate().getProduct().getBiller().getCompany().getName());

			returnMap.put(t.getMandate().getMandateCode(), params);
		});
		return returnMap;
	}

	private String validateMandate(Long billerId, PaymentRequestDTO request) {
		String responseCode = SYSTEM_ERROR;
		try {
			Map<String, Object> filter = new TreeMap<>();
			filter.put("product.biller.id", billerId);
			filter.put("subscriberCode", request.getSubscriberCode());
			filter.put("mandateCode", request.getMandateCode());
			List<Mandate> mandates = mandateService.findPaginatedMandates(filter, null, null);
			if (mandates == null || mandates.isEmpty())
				responseCode = MANDATE_NOT_FOUND; // could not find mandate for
													// this biller
			//else if (mandates.get(0).getAmount().compareTo(request.getAmount()) > 1)
				//responseCode = DEBIT_AMOUNT_GREATER_THAN_MANDATE_AMOUNT; 
			
			else if (mandates.get(0).getAmount().compareTo(request.getAmount())<= 0)
				responseCode = DEBIT_AMOUNT_GREATER_THAN_MANDATE_AMOUNT;
			
			// amount																			// to
																			// be
																			// debited
			else if (mandates.get(0).getEndDate().before(new Date())) {
				responseCode = EXPIRED_MANDATE;
			} else if (mandates.get(0).getStartDate().after(new Date())) {
				responseCode = PREMATURE_MANDATE;
			} else if (mandates.get(0).getStatus().getId().intValue() != 8) {
				responseCode = UNAPPROVED_MANDATE;
			} else if (mandates.get(0).getRequestStatus() != 1) {
				responseCode = SUSPENDED_DELETED_MANDATE;

			} else if (mandates.get(0).getFrequency() != 0) {
				responseCode = NOT_VARIABLE_FREQUENCY_MANDATE;
			} else if (mandates.get(0).isFixedAmountMandate()) {
				responseCode = NOT_VARIABLE_AMOUNT_MANDATE;

			}

			else {
				responseCode = SUCCESS;
			}
		} catch (Exception e) {
			logger.error(null, e);
			responseCode = SYSTEM_ERROR;
		}
		return responseCode;
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public AjaxValidatorResponse handleException(Exception e) {
		logger.error(null, e);
		AjaxValidatorResponse response = new AjaxValidatorResponse();
		response.setStatus("error");
		response.setErrorMessageList(Arrays.asList(new String[] { e.getMessage() }));
		return response;
	}

	private BillerUser _authenticateAPICall(APIAuthentication apiAuthentication) throws Exception {
		BillerUser user = null;
		String currentUserEmail = apiAuthentication.getUsername();
		String currentUserPassword = apiAuthentication.getPassword();
		try {
			user = (BillerUser) userService.getUserByEmailAndPassword(currentUserEmail, currentUserPassword);
			if (user != null && user.getBiller().getApiKey().equals(apiAuthentication.getApiKey()))
				return user;
		} catch (Exception e) {
			logger.error(null, e);
		}
		return null;
	}

	private BankUser _authenticateBankAPICall(APIAuthentication apiAuthentication) throws Exception {
		BankUser user = null;
		String currentUserEmail = apiAuthentication.getUsername();
		String currentUserPassword = apiAuthentication.getPassword();
		try {
			user = (BankUser) userService.getUserByEmailAndPassword(currentUserEmail, currentUserPassword);
			if (user != null && user.getBank().getBankPassKey().equals(apiAuthentication.getApiKey()))
				return user;
		} catch (Exception e) {
			logger.error(null, e);
		}
		return null;
	}

	@RequestMapping(value = "/mandate/biller/update", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateMandateByBiller(@RequestBody @Valid MandateUpdateRequest mandateUpdateRequest,
			BindingResult result) throws Exception {
		BillerUser user = null;
		try {
			user = _authenticateAPICall(mandateUpdateRequest.getApiAuthentication());
			if (mandateUpdateRequest.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}
		Gson gson = new Gson();
		String responseCode = SYSTEM_ERROR;
		JsonArray jsonArray = new JsonArray();
		List<MandateUpdateRequestDTO> mandateUpdateRequestsDTOs = mandateUpdateRequest.getMandateRequestBean();

		for (MandateUpdateRequestDTO m : mandateUpdateRequestsDTOs) {
			Mandate mandate = mandateService.getMandateByMandateCode(m.getMandateCode());
			if (mandate == null) {
				responseCode = MANDATE_NOT_FOUND;
			} else {
				mandate.setPayerAddress(m.getPayerAddress());
				mandate.setPayerName(m.getPayerName());
				mandate.setPhoneNumber(m.getPhoneNumber());
				mandate.setEmail(m.getEmailAddress());
				mandate.setNarration(m.getNarration());
				mandate.setDateModified(new Date());
				try {
					
					//if mandate initiated status 1 and incoming status is 2 or 3 permit
					
					//if mandate status =6 and incoming is 4 or 5
					if (mandate.getStatus().getId() ==WebAppConstants.BILLER_INITIATE_MANDATE.intValue()&&(m.getWorkFlowStatus()==WebAppConstants.BILLER_AUTHORIZE_MANDATE||m.getWorkFlowStatus()==WebAppConstants.BILLER_REJECT_MANDATE)) {
						mandate.setStatus(setMandateStatus(m.getWorkFlowStatus()));
					}
					
					if (m.getStatus() > 1 && m.getStatus() < 6) {
						mandate.setRequestStatus(m.getStatus());
					}
					mandateService.modifyMandate(mandate);

					try {
						applicationService.notifyMandateStatusAll(0, mandate, null);
					} catch (Exception ep) {
						ep.printStackTrace();
					}

					responseCode = SUCCESS;
				} catch (ServerBusinessException e) {
					logger.error(null, e);
					responseCode = SYSTEM_ERROR;
				}
			}
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("mandateCode", m.getMandateCode());
			jsonObject.addProperty("responseCode", responseCode);
			jsonObject.addProperty("responseDescription", "");
			jsonArray.add(jsonObject);

		}

		return new ResponseEntity<>(gson.toJson(jsonArray), HttpStatus.OK);
	}

	@RequestMapping(value = "/mandate/bank/update", consumes = {
			"application/json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateMandateByBank(@RequestBody @Valid MandateUpdateRequest mandateUpdateRequest,
			BindingResult result) throws Exception {

		BankUser user = null;
		try {
			user = (BankUser) _authenticateBankAPICall(mandateUpdateRequest.getApiAuthentication());
			if (mandateUpdateRequest.getApiAuthentication() == null || user == null) {
				throw new ServerBusinessException(0, "Authentication Failed");
			}
		} catch (Exception e) {
			logger.error(null, e);
			throw new ServerBusinessException(0, "An authentication error has occurred");
		}

		if (result.hasErrors()) {
			String[] validationErrors = new String[result.getAllErrors().size()];
			IntStream.range(0, validationErrors.length).forEach(index -> {
				validationErrors[index] = result.getAllErrors().get(index).getDefaultMessage();
			});
			return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
		}
		Gson gson = new Gson();
		String responseCode = SYSTEM_ERROR;
		JsonArray jsonArray = new JsonArray();
		List<MandateUpdateRequestDTO> mandateUpdateRequestsDTOs = mandateUpdateRequest.getMandateRequestBean();

		for (MandateUpdateRequestDTO m : mandateUpdateRequestsDTOs) {
			Mandate mandate = mandateService.getMandateByMandateCode(m.getMandateCode());
			if (mandate == null) {
				responseCode = MANDATE_NOT_FOUND;
			} else {
				mandate.setPayerAddress(m.getPayerAddress());
				mandate.setPayerName(m.getPayerName());
				mandate.setPhoneNumber(m.getPhoneNumber());
				mandate.setEmail(m.getEmailAddress());
				mandate.setNarration(m.getNarration());
				try {
					if (m.getWorkFlowStatus() > 5 && m.getWorkFlowStatus() < 10) {
						mandate.setStatus(setMandateStatus(m.getWorkFlowStatus(), mandate));
					}
					/*
					 * if (m.getStatus() > 1 && m.getStatus() < 6) {
					 * mandate.setRequestStatus(m.getStatus()); }
					 */
					mandateService.modifyMandate(mandate);
					try {
						applicationService.notifyMandateStatusAll(0, mandate, null);
					} catch (Exception ep) {
						ep.printStackTrace();
					}
					responseCode = SUCCESS;
				} catch (ServerBusinessException e) {
					logger.error(null, e);
					responseCode = SYSTEM_ERROR;
				}
			}
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("mandateCode", m.getMandateCode());
			jsonObject.addProperty("mandateCode", m.getMandateCode());
			jsonObject.addProperty("responseCode", responseCode);
			jsonObject.addProperty("phoneNumber", m.getPhoneNumber());
			jsonObject.addProperty("subscriberCode",
					mandate.getSubscriberCode() == null ? "" : mandate.getSubscriberCode());
			jsonArray.add(jsonObject);

		}

		return new ResponseEntity<>(gson.toJson(jsonArray), HttpStatus.OK);
	}

	private MandateStatus setMandateStatus(long statusId, Mandate mandate) throws ServerBusinessException {
		return mandateStatusService.getMandateStatusById(statusId);
	}
	private MandateStatus setMandateStatus(long statusId) throws ServerBusinessException {
		return mandateStatusService.getMandateStatusById(statusId);
	}
}
