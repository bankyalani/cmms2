package com.nibss.cmms.web.controllers;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.BankNotification;
import com.nibss.cmms.domain.BankUser;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.BillerNotification;
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.MandateStatus;
import com.nibss.cmms.domain.Notification;
import com.nibss.cmms.domain.Product;
import com.nibss.cmms.domain.Role;
import com.nibss.cmms.domain.Transaction;
import com.nibss.cmms.domain.TransactionResponse;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.service.BillerService;
import com.nibss.cmms.service.MandateService;
import com.nibss.cmms.service.MandateStatusService;
import com.nibss.cmms.service.ProductService;
import com.nibss.cmms.service.TransactionResponseService;
import com.nibss.cmms.service.TransactionService;
import com.nibss.cmms.utils.ExcelUtility;
import com.nibss.cmms.utils.ExcelUtility.ExcelType;
import com.nibss.cmms.utils.OptionBean;
import com.nibss.cmms.utils.Utilities;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.DataTablesRequest;
import com.nibss.cmms.web.JQueryDataTableRequest;
import com.nibss.cmms.web.JQueryDataTableResponse;
import com.nibss.cmms.web.SearchMapper;
import com.nibss.cmms.web.WebAppConstants;
import com.nibss.cmms.web.domain.BillerTable;
import com.nibss.cmms.web.domain.MandateTable;
import com.nibss.cmms.web.domain.NotificationTable;
import com.nibss.cmms.web.domain.UserTable;
import com.nibss.nip.dao.NipDAO;
import com.nibss.nip.dto.NESingleRequest;
import com.nibss.nip.dto.NESingleResponse;
import com.nibss.util.SessionUtil;


@Controller("baseController")
@Configuration
@PropertySource("file:${cmms.app.config}/app.properties")
public class BaseController {

	@Autowired
	private MandateStatusService mandateStatusService;

	@Autowired
	private MandateService mandateService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private TransactionResponseService transactionResponseService;
	@Autowired
	protected ThreadPoolTaskExecutor executor;
	
	@Autowired
	private	NipDAO nipService;

	@Autowired
	private BankService bankService;
	
	@Autowired
	private BillerService billerService;

	private static final Logger logger = Logger.getLogger(BaseController.class);

	@Autowired
	protected ApplicationContext appContext;
	


	@Value("${mandateImagePath}")
	protected String mandateImagePath;




	@ModelAttribute("frequencies")
	public List<OptionBean> populateFreq(){
		List<OptionBean> options= new ArrayList<>();
		//options.add(new OptionBean("-1", "--Select--"));
		options.add(new OptionBean("0", "Variable"));
		options.add(new OptionBean("1", "Weekly"));
		options.add(new OptionBean("2", "Every 2 weeks"));
		options.add(new OptionBean("4", "Monthly"));
		options.add(new OptionBean("8", "Every 2 months"));
		options.add(new OptionBean("12", "Every 3 months"));
		options.add(new OptionBean("16", "Every 4 months"));
		options.add(new OptionBean("20", "Every 5 months"));
		options.add(new OptionBean("24", "Every 6 months"));
		options.add(new OptionBean("28", "Every 7 months"));
		options.add(new OptionBean("32", "Every 8 months"));
		options.add(new OptionBean("36", "Every 9 months"));
		options.add(new OptionBean("40", "Every 10 months"));
		options.add(new OptionBean("44", "Every 11 months"));
		options.add(new OptionBean("48", "Every 12 months"));

		return options;
	}

	private Map<String, String> mimeTypes() {
		Map<String, String> mimeTypes;

		mimeTypes = new HashMap<String, String>();
		mimeTypes.put("css", "text/css");
		mimeTypes.put("js", "text/javascript");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("pdf", "application/pdf");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("zip", "application/zip");
		mimeTypes.put("swf", "application/x-shockwave-flash");
		return mimeTypes;
	}

	protected BillerUser getCurrentBillerUser(HttpServletRequest request){
		return (BillerUser)request.getSession().getAttribute(WebAppConstants.CURRENT_BILLER_USER);
	}


	protected User getCurrentUser(HttpServletRequest request){
		return (User)request.getSession().getAttribute(WebAppConstants.CURRENT_USER_USER);
	}


	protected Role getCurrentUserRole(HttpServletRequest request) {
		return (Role)request.getSession().getAttribute(WebAppConstants.CURRENT_USER_ROLE);
	}

	protected BankUser getCurrentBankUser(HttpServletRequest request) {
		return (BankUser)request.getSession().getAttribute(WebAppConstants.CURRENT_BANK_USER);
	}



	protected void setBreadCrumb(List<OptionBean> breadCrumb, Model model) {
		model.addAttribute(WebAppConstants.VIEW_BREADCRUMB, breadCrumb);
	}

	protected void setViewMainHeading(String viewMainHeading, Model model) {
		model.addAttribute(WebAppConstants.VIEW_MAIN_HEADER, viewMainHeading);
	}
	
	protected void setViewModalHeading(String viewModalHeading, Model model) {
		model.addAttribute(WebAppConstants.VIEW_MODAL_HEADER, viewModalHeading);
	}

	protected void setViewBoxHeading(String viewBoxHeading, Model model) {
		model.addAttribute(WebAppConstants.VIEW_BOX_HEADER, viewBoxHeading);
	}
	protected String getDatatableUsers(DataTablesRequest dtReq, List<User> users,Integer filterSize, int totalRecords){
		return "";
	}


	//to build user tables
	protected JQueryDataTableResponse<BillerTable> 
	getJQueryDatatableBillers(JQueryDataTableRequest dtReq, List<Biller> billers,Integer filterSize, int totalRecords){
		JQueryDataTableResponse<BillerTable> response= new JQueryDataTableResponse<BillerTable>();
		SimpleDateFormat sdf= new SimpleDateFormat("YYYY-MM-dd hh:mm");
		BillerTable[] billerTableArray=new BillerTable[billers.size()];
		final List<Biller> finalBillers=billers;
		IntStream.range(0,finalBillers.size())
		.forEach(index->{
			Biller biller=finalBillers.get(index);
			BillerTable billerTable= new BillerTable();
			billerTable.setAccountNumber(biller.getAccountNumber());
			billerTable.setBankName(biller.getBank().getBankName());
			billerTable.setCompanyName(biller.getCompany().getName());
			billerTable.setCreatedBy(biller.getCreatedBy().getEmail());
			billerTable.setRcNumber(biller.getCompany().getRcNumber());
			billerTable.setId(biller.getId());
			billerTable.setBillerId(biller.getId());
			billerTable.setBankCode(biller.getBank().getBankCode());
			billerTable.setIndustryName(biller.getCompany().getIndustry().getName());
			billerTable.setIndustry(biller.getCompany().getIndustry().getId());
			int billerStatus=biller.getStatus();
			String _billerStatus="UnKnown";
			if(billerStatus==(WebAppConstants.BILLER_STATUS_ACTIVE)){
				_billerStatus="Active";
			}else if(billerStatus==(WebAppConstants.BILLER_STATUS_INACTIVE)){
				_billerStatus="Inactive";
			}
			billerTable.setStatus(_billerStatus);
			billerTable.setDateCreated(sdf.format(biller.getDateCreated()));
			billerTableArray[index]=billerTable;

		});


		response.setDraw(dtReq.getDraw());
		response.setData(billerTableArray);
		response.setRecordsFiltered(filterSize==null?totalRecords:filterSize);
		response.setRecordsTotal(totalRecords);

		return response;

	}


	//to build user tables
	protected JQueryDataTableResponse<UserTable> 
	getJQueryDatatableUsers(JQueryDataTableRequest dtReq, List<User> users,Integer filterSize, int totalRecords){
		JQueryDataTableResponse<UserTable> response= new JQueryDataTableResponse<UserTable>();
		SimpleDateFormat sdf= new SimpleDateFormat("YYYY-MM-dd hh:mm");
		UserTable[] userTableArray=new UserTable[users.size()];
		final List<User> finalUsers=users;
		IntStream.range(0,finalUsers.size())
		.forEach(index->{
			User user=finalUsers.get(index);
			UserTable userTable= new UserTable();
			userTable.setId(user.getId());
			userTable.setEmail(user.getEmail());
			userTable.setFirstName(user.getFirstName());
			userTable.setLastName(user.getLastName());
			if (user instanceof BankUser){
				userTable.setOrganization(((BankUser) user).getBank().getBankName());
				userTable.setBankCode(((BankUser) user).getBank().getBankCode());
			}else if(user instanceof BillerUser){
				userTable.setOrganization(((BillerUser) user).getBiller().getCompany().getName());	
				userTable.setBillerId(((BillerUser) user).getBiller().getId());
			}else{
				userTable.setOrganization("NIBSS"); //nibss user
			}
			userTable.setRoleId(user.getRole().getId());
			userTable.setRoleName(user.getRole().getName());
			userTable.setStatus(String.valueOf(user.getStatus()));
			userTable.setDateCreated(sdf.format(user.getDateCreated()));

			userTableArray[index]=userTable;
		});
		response.setDraw(dtReq.getDraw());
		response.setData(userTableArray);
		response.setRecordsFiltered(filterSize==null?totalRecords:filterSize);
		response.setRecordsTotal(totalRecords);

		return response;

	}


	//to build notification tables
	protected JQueryDataTableResponse<NotificationTable> 
	getJQueryDatatableNotifications(JQueryDataTableRequest dtReq, List<Notification> notifications,Integer filterSize, int totalRecords){
		JQueryDataTableResponse<NotificationTable> response= new JQueryDataTableResponse<>();
		SimpleDateFormat sdf= new SimpleDateFormat("YYYY-MM-dd hh:mm");
		NotificationTable[] notificationTableArray=new NotificationTable[notifications.size()];
		final List<Notification> finalNotifications=notifications;
		IntStream.range(0,finalNotifications.size())
		.forEach(index->{
			Notification notification=finalNotifications.get(index);
			NotificationTable notificationTable= new NotificationTable();
			notificationTable.setEmail(notification.getEmailAddress());
			notificationTable.setStatusName(notification.getMandateStatus().getName());
			notificationTable.setMailDescription(notification.getMandateStatus().getMailDescription());
			notificationTable.setStatusDescription(notification.getMandateStatus().getDescription());
			notificationTable.setId(notification.getId());

			if (notification instanceof BankNotification){
				notificationTable.setOrganization(((BankNotification) notification).getBank().getBankName());
				notificationTable.setBankCode(((BankNotification) notification).getBank().getBankCode());
			}else if(notification instanceof BillerNotification){
				notificationTable.setOrganization(((BillerNotification) notification).getBiller().getCompany().getName());	
				notificationTable.setBillerId(((BillerNotification) notification).getBiller().getId());
			}

			notificationTable.setDateCreated(sdf.format(notification.getDateCreated()));

			notificationTableArray[index]=notificationTable;
		});
		response.setDraw(dtReq.getDraw());
		response.setData(notificationTableArray);
		response.setRecordsFiltered(filterSize==null?totalRecords:filterSize);
		response.setRecordsTotal(totalRecords);

		return response;

	}


	//to build mandate tables
	protected JQueryDataTableResponse<MandateTable> 
	getJQueryDatatableMandates(JQueryDataTableRequest dtReq, List<Mandate> mandates,Integer filterSize, int totalRecords){
		JQueryDataTableResponse<MandateTable> response= new JQueryDataTableResponse<>();
		SimpleDateFormat sdf= new SimpleDateFormat("YYYY-MM-dd hh:mm");
		DecimalFormat df= new DecimalFormat("#,##0.00");
		SimpleDateFormat sdf2= new SimpleDateFormat("YYYY-MM-dd");
		MandateTable[] mandateTableArray=new MandateTable[mandates.size()];
		final List<Mandate> finalMandates=mandates;
		IntStream.range(0,finalMandates.size())
		.forEach(index->{
			Mandate mandate=finalMandates.get(index);
			MandateTable mandateTable= new MandateTable();
			mandateTable.setMandateCode(mandate.getMandateCode());
		    mandateTable.setStatus(mandate.getStatus().getName());
			mandateTable.setBillerName(mandate.getProduct().getBiller().getCompany().getName());
			mandateTable.setSubscriberCode(mandate.getSubscriberCode());
			mandateTable.setProductName(mandate.getProduct().getName());
			mandateTable.setAmount(df.format(mandate.getAmount().doubleValue()));
			mandateTable.setFixedAmountMandate(mandate.isFixedAmountMandate());
			mandateTable.setBank(mandate.getBank().getBankName());	
		
			String debitFreq=populateFreq()
					.stream()
					.filter(e->e.getId().equals(String.valueOf(mandate.getFrequency())))
					.map(e->e.getValue()).reduce("",String::concat);
			
			mandateTable.setDebitFrequency(debitFreq==null?"":debitFreq);
			if(mandate.getFrequency()==0)
				mandateTable.setNextDebitDate("Variable");
			else{
				
				mandateTable.setNextDebitDate(mandate.getNextDebitDate().after(mandate.getEndDate())?"Expired":new SimpleDateFormat("yyyy-MM-dd").format(mandate.getNextDebitDate()));
			}
			mandateTable.setLastActionBy(mandate.getLastActionBy()==null?"":mandate.getLastActionBy().getRole().getName());
			
			
			mandateTable.setDateAdded(sdf.format(mandate.getDateCreated()));
			mandateTable.setDebitStartDate(sdf2.format(mandate.getStartDate()));
			mandateTable.setDebitEndDate(sdf2.format(mandate.getEndDate()));
			mandateTable.setId(mandate.getId());
			
			mandateTable.setRequestStatus(mandate.getRequestStatus()+"");
			if(mandate.getRequestStatus()==1){
				mandateTable.setRequestStatus("Active");
			}else if(mandate.getRequestStatus()==2){
				mandateTable.setRequestStatus("Suspended");
			}
			else if(mandate.getRequestStatus()==3){
				mandateTable.setRequestStatus("Deleted");
			}
			else{
				mandateTable.setRequestStatus("Unknown");
			}
			mandateTable.setPayerName(mandate.getPayerName());
			mandateTableArray[index]=mandateTable;
		});
		response.setDraw(dtReq.getDraw());
		response.setData(mandateTableArray);
		response.setRecordsFiltered(filterSize==null?totalRecords:filterSize);
		response.setRecordsTotal(totalRecords);

		return response;

	}



	/*//to build transaction tables
	protected String  getDatatableTransactions(DataTablesRequest dtReq, List<Mandate> mandates,Integer filterSize, int totalRecords){

		JsonArray mainArray=new JsonArray();
		DecimalFormat df= new DecimalFormat("#,##0.00");
		SimpleDateFormat sdf= new SimpleDateFormat("YYYY-MM-dd hh:mm");
		SimpleDateFormat sdf2= new SimpleDateFormat("YYYY-MM-dd");
		mandates.stream().forEach(mandate->{
			JsonArray temp = new JsonArray();
			temp.add(new JsonPrimitive(""));// placeholder for the serial number on the client side
			temp.add(new JsonPrimitive(mandate.getMandateCode()));
			temp.add(new JsonPrimitive(mandate.getStatus().getName()));
			temp.add(new JsonPrimitive(mandate.getProduct().getBiller().getCompany().getName()));
			temp.add(new JsonPrimitive(mandate.getSubscriberCode()));
			temp.add(new JsonPrimitive(mandate.getProduct().getName()));
			temp.add(new JsonPrimitive(df.format(mandate.getAmount().doubleValue())));
			temp.add(new JsonPrimitive(mandate.getBank().getBankName()));		
			String debitFreq=populateFreq()
					.stream()
					.filter(e->e.getId().equals(String.valueOf(mandate.getFrequency())))
					.map(e->e.getValue()).reduce("",String::concat);
			Date nextDebitDate=DateUtils.calculateNextDebitDate(mandate.getStartDate(), mandate.getEndDate(),mandate.getFrequency());
			temp.add(new JsonPrimitive(debitFreq==null?"":debitFreq));
			if(mandate.getFrequency()==0)
				temp.add(new JsonPrimitive("Variable"));
			else
				temp.add(new JsonPrimitive(nextDebitDate==null?"Expired":sdf2.format(nextDebitDate)));
			temp.add(new JsonPrimitive(mandate.getLastActionBy().getRole().getName()));
			temp.add(new JsonPrimitive(sdf.format(mandate.getDateCreated())));
			temp.add(new JsonPrimitive(mandate.getId()));

			mainArray.add(temp);
		});

		JsonObject mainObj = new JsonObject();
		mainObj.addProperty("iTotalDisplayRecords",filterSize==null?totalRecords:filterSize);
		mainObj.addProperty("sColumns", dtReq.getColumns());
		mainObj.addProperty("sEcho", dtReq.getEcho());
		mainObj.addProperty("sColumns", dtReq.getColumnSearches().toString());		
		mainObj.addProperty("iTotalRecords",totalRecords);
		mainObj.add("aaData", mainArray);

		return new Gson().toJson(mainObj);


	}*/

	protected List<SearchMapper> mandateDatatablePropOrder(){
		List<SearchMapper> prop= new ArrayList<>();
		int i=0;
		prop.add(new SearchMapper(++i, "mandateCode",""));
		prop.add(new SearchMapper(++i, "status.id","Long"));
		prop.add(new SearchMapper(++i, "product.biller.id","Long"));
		prop.add(new SearchMapper(++i, "subscriberCode",""));
		prop.add(new SearchMapper(++i, "product.id","Long"));
		prop.add(new SearchMapper(++i, "amount",""));
		prop.add(new SearchMapper(++i, "bank.bankCode",""));
		prop.add(new SearchMapper(++i, "fixed","Boolean"));
		prop.add(new SearchMapper(++i, "frequency",""));
		return prop;
	}


	protected List<SearchMapper> userDatatablePropOrder(){
		List<SearchMapper> prop= new ArrayList<>();
		int i=0;
		prop.add(new SearchMapper(++i, "email",""));
		prop.add(new SearchMapper(++i, "bank.bankCode",""));
		prop.add(new SearchMapper(++i, "biller.id","Long"));
		prop.add(new SearchMapper(++i, "role.id","Long"));
		return prop;
	}



	protected Map<String,Object> buildJQueryDataTableFilter(JQueryDataTableRequest dtReq){

		Map<String,Object> filter=new TreeMap<>();
		IntStream.range(0, dtReq.getColumns().length)
		.forEach(index->{
			String itemName=dtReq.getColumns()[index].getData();
			String itemValue=dtReq.getColumns()[index].getSearch().getValue();

			if(itemName!=null && itemValue!=""){
				Object obj=null;
				if(itemName.contains(".") && itemName.endsWith("id")){//this is definitely an id
					obj=Utilities.reflectToLong(itemValue);
				}else{
					obj=itemValue;
				}
				filter.put(itemName.replace("\\", ""), obj);
			}
		});
		return filter;
	}


	protected Map<String,Object> buildDataTableFilter(List<SearchMapper> searchMapper,DataTablesRequest dtReq, HttpServletRequest request){
		Map<String,Object> filter=new TreeMap<>();
		if(dtReq.getColumnSearches()!=null && dtReq.getColumnSearches().toArray().length>0){
			String[] searchParams=dtReq.getColumnSearches().stream().toArray(size -> new String[size]);
			IntStream.range(0, searchParams.length)
			.forEach(index->{
				List<SearchMapper> mappers=searchMapper
						.stream()
						.filter(s->s.getItemIndex()==index)
						.collect(Collectors.toList());

				if (mappers!=null && !searchParams[index].isEmpty()){
					Object obj=null;
					if(mappers.get(0).getType().equalsIgnoreCase("long")){
						obj=Utilities.reflectToLong(searchParams[index].trim());
					}else if(mappers.get(0).getType().equalsIgnoreCase("byte")){
						obj=Utilities.reflectToByte(searchParams[index].trim());
					}else{
						obj=searchParams[index].trim();
					}
					filter.put(mappers.get(0).getSearchItem(), obj);
				}
			});
		}

		return filter;
	}


	protected void uploadMandateImage(Mandate mandate) 
			throws ServerBusinessException, IllegalStateException, IOException,SizeLimitExceededException{
		String fileName="";
		if(mandate.getMandateCode().isEmpty())
			throw new ServerBusinessException(0,"Mandate Code not found");
		String mandateCode=mandate.getMandateCode();
		String billerRCNumber=mandate.getProduct().getBiller().getCompany().getRcNumber();
		MultipartFile multipartFile= mandate.getUploadFile();
		
		if(multipartFile!=null){

			
			fileName=mandateCode.replace("/", "")+"_0."+FilenameUtils.getExtension(multipartFile.getOriginalFilename());
			String _destination=mandateImagePath+File.separator+billerRCNumber;
			File destination = new File(_destination);
			logger.info("Destination "+mandateImagePath);
			logger.info("Extension "+FilenameUtils.getExtension(multipartFile.getOriginalFilename()));

			if (!destination.exists())
				destination.mkdirs();
			multipartFile.transferTo(new File(_destination+File.separator+fileName));
			//fileName = multipartFile.getOriginalFilename();
			mandate.setMandateImage(fileName);
			//do whatever you want
		}
	}


	@RequestMapping(value="/common/mandateStatuses/get",method=RequestMethod.GET)
	public @ResponseBody String getAllMandateStatuses(HttpServletRequest request){

		JsonArray mainObj= new JsonArray();
		try {
			User user= getCurrentUser(request);
			List<MandateStatus> mandateStatuses=new ArrayList<>();
			if(user instanceof BankUser){
				mandateStatuses=mandateStatusService.getMandateStatuses(new Long[]{WebAppConstants.BANK_APPROVE_MANDATE,
						WebAppConstants.BANK_AUTHORIZE_MANDATE, WebAppConstants.BANK_DISAPPROVE_MANDATE, WebAppConstants.BANK_INITIATE_MANDATE,
						WebAppConstants.BANK_REJECT_MANDATE, WebAppConstants.BILLER_DISAPPROVE_MANDATE,WebAppConstants.BILLER_APPROVE_MANDATE, WebAppConstants.BILLER_DISAPPROVE_MANDATE});
			}else if(user instanceof BillerUser){
				mandateStatuses=mandateStatusService.getMandateStatuses(new Long[]{WebAppConstants.BILLER_APPROVE_MANDATE,
						WebAppConstants.BILLER_AUTHORIZE_MANDATE, WebAppConstants.BILLER_DISAPPROVE_MANDATE, WebAppConstants.BILLER_INITIATE_MANDATE,
						WebAppConstants.BILLER_REJECT_MANDATE, WebAppConstants.BANK_APPROVE_MANDATE, WebAppConstants.BANK_DISAPPROVE_MANDATE});
			}
			
			//List<MandateStatus> mandateStatuses=mandateStatusService.getAllMandateStatus();
			mandateStatuses.stream().forEach(m->{
				JsonObject temp = new JsonObject();
				temp.add("id", new JsonPrimitive(String.valueOf(m.getId())));
				temp.add("name", new JsonPrimitive(m.getName()));
				mainObj.add(temp);
			});

		} catch (ServerBusinessException e) {	
			logger.error(e);
		} 


		return new Gson().toJson(mainObj);

	}


	@ResponseBody
	@RequestMapping(value="/common/mandate/getImage")
	public ResponseEntity<byte[]> fetchMandateImage(@RequestParam(required=true,value="billerRCNumber") String billerRCNumber,
			@RequestParam(required=true,value="mandateImage") String mandateImage)  {
		ResponseEntity<byte[]> respEntity = null;
		Path imagePath = Paths.get(mandateImagePath+File.separator+billerRCNumber+File.separator+mandateImage);
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
		try {
			if( Files.exists(imagePath) ) {
				try(BufferedInputStream buffStream = new BufferedInputStream( Files.newInputStream(imagePath))) {
					int c = -1;
					while( (c = buffStream.read()) != -1) {
						byteArrayStream.write(c);
					}
				}

				String ext=FilenameUtils.getExtension(imagePath.getFileName().toString());
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.set("Content-Type", mimeTypes().get(ext));
				httpHeaders.set("Content-Disposition", String.format("inline;filename=\"%s\"",imagePath.getFileName().toString()));
				respEntity = new ResponseEntity<byte[]>(byteArrayStream.toByteArray(), httpHeaders,HttpStatus.OK);
				byteArrayStream.close();

			} else {
				respEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(IOException e ) {
			logger.error(null, e);
			respEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return respEntity;
	}
	
	@ResponseBody
	@RequestMapping(value="/common/mandate/bulkTemplate")
	public ResponseEntity<byte[]> getBulkManadateTemplate(@Value("${biller.bulk.mandate.template}") String billerBulkMandateTemplate, 
			@Value("${bank.bulk.mandate.template}") String bankBulkMandateTemplate, HttpServletRequest request)  {
		ResponseEntity<byte[]> respEntity = null;

		User user = getCurrentUser(request);
		Path filePath =null;
		if(user instanceof BankUser){
			filePath= Paths.get(bankBulkMandateTemplate);
		}else if (user instanceof BillerUser)
			filePath = Paths.get(billerBulkMandateTemplate);
		
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
		try {
			if( Files.exists(filePath) ) {
				try(BufferedInputStream buffStream = new BufferedInputStream( Files.newInputStream(filePath))) {
					int c = -1;
					while( (c = buffStream.read()) != -1) {
						byteArrayStream.write(c);
					}
				}

				String ext=FilenameUtils.getExtension(filePath.getFileName().toString());
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.set("Content-Type", mimeTypes().get(ext));
				httpHeaders.set("Content-Disposition", String.format("inline;filename=\"%s\"",filePath.getFileName().toString()));
				respEntity = new ResponseEntity<byte[]>(byteArrayStream.toByteArray(), httpHeaders,HttpStatus.OK);
				byteArrayStream.close();

			} else {
				respEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(IOException e ) {
			logger.error(null, e);
			respEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return respEntity;
	}

	protected List<Mandate> processExcelMandateUpload(InputStream inputStream, String fileName, HttpServletRequest request){
		ExcelUtility excelReader=null;
		List<Mandate> mandates = new ArrayList<>();
		List<String[]> data=null;
		
		//get current user
		User user= getCurrentUser(request);
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");

		if (fileName.endsWith(".xls"))
			excelReader= new ExcelUtility(ExcelType.XLS);
		else if (fileName.endsWith(".xlsx"))
			excelReader= new ExcelUtility(ExcelType.XLSX);
		excelReader.setStartRow(1);
		excelReader.setColumnCount(15);
		try {
			data=excelReader.readInputStream(inputStream);
			logger.debug("--data size ---"+data.size());
			data.stream().forEach(d->{
				Mandate m= new Mandate();
				m.setSubscriberCode(d[0]);
				m.setPayerName(d[1]);
				m.setAccountName(d[2]);
				m.setAccountNumber(d[3].replaceAll("\\.0*$", ""));
				m.setNarration(d[5]);
				m.setPhoneNumber(d[10]!=null?d[10].replaceAll("\\.0*$", ""):"");
				m.setEmail(d[11]);
				m.setPayerAddress(d[12]);
				m.setFrequency(Integer.parseInt(d[13].replaceAll("\\.0*$", "")));
				m.setMandateImage(d[14]);
				//set the amount from file 
				logger.debug("--Amount "+d[7]);
				m.setAmount(new BigDecimal(d[7]==""?"0":d[7]));

				try {
					Biller biller= null;
					if(user instanceof BankUser){//a bank user uploaded mandate
						m.setBank(((BankUser)user).getBank());
						biller= billerService.getBillerById(Long.valueOf(d[4].replaceAll("\\.0*$", "")));
					}else if(user instanceof BillerUser){
						m.setBank(bankService.getBankByBankCode(d[4]));
						biller= ((BillerUser)user).getBiller();
					}
					Product p=productService.getProductsByBillerAndProductId(biller,Long.valueOf(d[6].replaceAll("\\.0*$", "")));
					m.setProduct(p);
					m.setStartDate(sdf.parse(d[8]));
					m.setEndDate(sdf.parse(d[9]));
					if (p!=null){
						if (p.getAmount()!=null && p.getAmount().doubleValue()!=0)
							m.setAmount(p.getAmount()); //overrides the amount for products with amount
					}
				} catch (Exception e) {
					logger.error(e);
				}
				mandates.add(m);
			});

		} catch (Exception e) {
			logger.error(e);
		}
		return mandates;
	}


	//report generator
	protected ModelAndView generateAjaxReport(HttpServletRequest request, HttpServletResponse response){
		ModelAndView mav= new ModelAndView();
		Cookie c= new Cookie("fileDownload", "true");
		c.setPath("/");
		response.addCookie(c);		

		return mav;
	}
	
	protected ModelAndView generateTransactionReport(HttpServletRequest request,HttpServletResponse response, ModelMap model, 
			String reportFormat, String billerId, String productId,
			String customerBank, String dateCreated, String subscriberCode, String debitStatus) throws IOException{
		
		ModelAndView mav= generateAjaxReport(request, response);

		List<Transaction> transactions=new ArrayList<>();
		List<TransactionResponse> transactionResponses = new ArrayList<>();
		String reportName="transaction_report_"+System.currentTimeMillis()+".%s";

		try {
			//get mandate based on search parameter
			Map<String,Object> filters= new TreeMap<>();

			if (billerId!=null && billerId!=""){
				filters.put("mandate.product.biller.id",Long.valueOf(billerId));
			}
			if (productId!=null && productId!=""){
				filters.put("mandate.product.id",Long.valueOf(productId));
			}	
			if (dateCreated!=null && dateCreated!=""){
				filters.put("dateCreated",dateCreated);
			}
			if (customerBank!=null && customerBank!=""){
				filters.put("mandate.bank.bankCode",customerBank);
			}
			if (subscriberCode!=null && subscriberCode!=""){
				filters.put("mandate.subscriberCode",subscriberCode);
			}
			
			if (subscriberCode!=null && subscriberCode!=""){
				filters.put("mandate.subscriberCode",subscriberCode);
			}
			
			if(debitStatus!=null && debitStatus!=""){
				filters.put("status",debitStatus);
			}
			
			transactions=transactionService.findPaginatedTransactions(filters, null, null);
			 transactionResponses =transactionResponseService.getTransactionResponse();
		} catch (Exception e) {
			logger.error(null,e);
		}
		
		model.addAttribute(WebAppConstants.REPORT_TYPE,WebAppConstants.REPORT_TYPE_DETAILED);
		model.addAttribute(WebAppConstants.TRANSACTION_STATUS,transactionResponses);
		model.addAttribute(WebAppConstants.REPORT_DATA,transactions);
		model.addAttribute(WebAppConstants.REPORT_OBJECT_TYPE,new Transaction());
		if(reportFormat.equals(WebAppConstants.EXCEL_REPORT_FORMAT)){
			model.addAttribute("reportName",String.format(reportName,"xls"));
			mav.setViewName("excelView");
		}else if (reportFormat.equals(WebAppConstants.PDF_REPORT_FORMAT)){
			model.addAttribute("reportName",String.format(reportName,"pdf"));
			mav.setViewName("pdfView");

		}else if (reportFormat.equals(WebAppConstants.CSV_REPORT_FORMAT)){
			model.addAttribute("reportName",String.format(reportName,"csv"));
			mav.setViewName("csvView");
		}

		mav.addAllObjects(model);
		return mav;
		
	}

	protected ModelAndView generateMandateReport(HttpServletRequest request,HttpServletResponse response, ModelMap model, 
			@PathVariable("format") String reportFormat,
			@RequestParam("biller") String billerId, @RequestParam("product") String productId,
			@RequestParam("frequency") String frequency,@RequestParam("bank") String customerBank,
			@RequestParam("mandateStatus") String mandateStatus, @RequestParam("dateCreated") String dateCreated) throws IOException{

		ModelAndView mav= generateAjaxReport(request, response);

		List<Mandate> mandates=new ArrayList<>();
		String reportName="mandate_report_"+System.currentTimeMillis()+".%s";

		try {
			//get mandate based on search parameter
			Map<String,Object> filters= new TreeMap<>();

			if (billerId!=null && billerId!=""){
				filters.put("product.biller.id",Long.valueOf(billerId));
			}
			if (productId!=null && productId!=""){
				filters.put("product.id",Long.valueOf(productId));
			}
			if (frequency!=null && frequency!=""){
				filters.put("frequency",Integer.parseInt(frequency));
			}
			if (customerBank!=null && customerBank!=""){
				filters.put("bank.bankCode",customerBank);
			}
			if (mandateStatus!=null && mandateStatus!=""){
				filters.put("status.id",Long.valueOf(mandateStatus));
			}


			mandates=mandateService.findPaginatedMandates(filters, null, null);
		} catch (ServerBusinessException e) {
			logger.error(null,e);
		}
		model.addAttribute(WebAppConstants.REPORT_TYPE,WebAppConstants.REPORT_TYPE_DETAILED);
		model.addAttribute(WebAppConstants.REPORT_DATA,mandates);
		model.addAttribute(WebAppConstants.REPORT_OBJECT_TYPE,new Mandate());
		if(reportFormat.equals(WebAppConstants.EXCEL_REPORT_FORMAT)){
			model.addAttribute("reportName",String.format(reportName,"xls"));
			mav.setViewName("excelView");
		}else if (reportFormat.equals(WebAppConstants.PDF_REPORT_FORMAT)){
			model.addAttribute("reportName",String.format(reportName,"pdf"));
			mav.setViewName("pdfView");

		}else if (reportFormat.equals(WebAppConstants.CSV_REPORT_FORMAT)){
			model.addAttribute("reportName",String.format(reportName,"csv"));
			mav.setViewName("csvView");
		}

		mav.addAllObjects(model);
		return mav;
	}

	@RequestMapping(value="/scommon/updateVariableAmountMandate/{id}/",method=RequestMethod.GET)
	public @ResponseBody String updateVariableMandateAmount(HttpServletRequest request,@RequestParam("new_amount") String newAmountString,
			@PathVariable("id") Long mandateId) throws Exception{
		try {
			Mandate mandate= mandateService.getMandateByMandateId(mandateId);
			BigDecimal newAmount;
			try {
				newAmount = new BigDecimal(newAmountString!=null?newAmountString.replace(",", ""):"0");
			} catch (RuntimeException e) {
				throw new Exception("Invalid Amount Specified");
			}
				
			
			if(newAmount.compareTo(mandate.getAmount()) > 0)
				throw new Exception("Debit amount cannot be greater than mandate amount");
			mandate.setVariableAmount(newAmount);
			mandateService.modifyMandate(mandate);
			return WebAppConstants.AJAX_SUCCESS;	
		} catch (Exception e) {
			logger.error(null,e);
			return e.getMessage();
		} 
	}
	
	@RequestMapping(value="/scommon/mandate/delete/{id}", method=RequestMethod.GET)
	public String deleteMandate(HttpServletRequest request,@PathVariable("id") Long mandateId, RedirectAttributes redirectAtrribute) throws Exception{
		try {
			Mandate mandate= mandateService.getMandateByMandateId(mandateId);
			mandate.setRequestStatus(WebAppConstants.STATUS_MANDATE_DELETED);
		
			mandateService.modifyMandate(mandate);
			redirectAtrribute.addFlashAttribute("messageClass","success");
			redirectAtrribute.addFlashAttribute("message", "Mandate was deleted successfully");
		} catch (Exception e) {
			logger.error(null,e);
			redirectAtrribute.addFlashAttribute("message", "An error occurred while deleting the mandate. Please try again!");
			redirectAtrribute.addFlashAttribute("messageClass","danger");
		} 
		User user= getCurrentUser(request);
		if(user instanceof BankUser){
			return "redirect:/bank/mandate/list";
		}else{
			return "redirect:/biller/mandate/list";
		}
	}

	@RequestMapping(value="/scommon/mandate/suspend/{id}", method=RequestMethod.GET)
	public String suspendMandate(HttpServletRequest request,@PathVariable("id") Long mandateId, RedirectAttributes redirectAtrribute) throws Exception{
		try {
			Mandate mandate= mandateService.getMandateByMandateId(mandateId);
			mandate.setRequestStatus(WebAppConstants.STATUS_MANDATE_SUSPENDED);
		
			mandateService.modifyMandate(mandate);
			redirectAtrribute.addFlashAttribute("messageClass","success");
			redirectAtrribute.addFlashAttribute("message", "Mandate was suspended successfully");
		} catch (Exception e) {
			logger.error(null,e);
			redirectAtrribute.addFlashAttribute("message", "An error occurred while suspending the mandate. Please try again!");
			redirectAtrribute.addFlashAttribute("messageClass","danger");
		} 
		User user= getCurrentUser(request);
		if(user instanceof BankUser){
			return "redirect:/bank/mandate/list";
		}else{
			return "redirect:/biller/mandate/list";
		}
	}
	@RequestMapping(value="/scommon/mandate/activate/{id}", method=RequestMethod.GET)
	public String activateMandate(HttpServletRequest request,@PathVariable("id") Long mandateId, RedirectAttributes redirectAtrribute) throws Exception{
		try {
			Mandate mandate= mandateService.getMandateByMandateId(mandateId);
			mandate.setRequestStatus(WebAppConstants.STATUS_ACTIVE);
		
			mandateService.modifyMandate(mandate);
			redirectAtrribute.addFlashAttribute("messageClass","success");
			redirectAtrribute.addFlashAttribute("message", "Mandate was activated successfully");
		} catch (Exception e) {
			logger.error(null,e);
			redirectAtrribute.addFlashAttribute("message", "An error occurred while activating the mandate. Please try again!");
			redirectAtrribute.addFlashAttribute("messageClass","danger");
		} 
		User user= getCurrentUser(request);
		if(user instanceof BankUser){
			return "redirect:/bank/mandate/list";
		}else{
			return "redirect:/biller/mandate/list";
		}
	}
	@RequestMapping(value="/scommon/account/validate/{bankCode}/{accountNumber}",method=RequestMethod.GET, produces="text/xml")
	public @ResponseBody NESingleResponse validateAccountNumber(HttpServletRequest request,@PathVariable("bankCode") String bankCode,
			@PathVariable("accountNumber") String accountNumber,  @Value("${cmms.channel.code}") int cmmChannelCode) throws Exception{
		try {
			
			Bank bank= bankService.getBankByBankCode(bankCode);
			NESingleRequest nESingleRequest= new NESingleRequest();
			nESingleRequest.setChannelCode(cmmChannelCode);
			nESingleRequest.setDestinationInstitutionCode(bank.getNipBankCode());
			nESingleRequest.setAccountNumber(accountNumber);
			nESingleRequest.setSessionID(SessionUtil.generateSessionID("999", "999"));
			
			NESingleResponse neResponse = nipService.sendNameEnquiry(nESingleRequest);
			return neResponse;
		} catch (Exception e) {
			logger.error(null,e);
			return null;
		} 
	}
	
	


}
