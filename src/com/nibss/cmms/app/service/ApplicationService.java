package com.nibss.cmms.app.service;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;
import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nibss.cmms.domain.APITransaction;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.Transaction;
import com.nibss.cmms.domain.TransactionParam;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.service.BillerService;
import com.nibss.cmms.service.MandateService;
import com.nibss.cmms.service.TransactionService;
import com.nibss.cmms.utils.DateUtils;
import com.nibss.cmms.utils.NotificationUtils;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.WebAppConstants;
import com.nibss.exceptions.NIPException;
import com.nibss.nip.dao.NipDAO;
import com.nibss.nip.dto.FTSingleCreditRequest;
import com.nibss.nip.dto.FTSingleCreditResponse;
import com.nibss.nip.dto.FTSingleDebitRequest;
import com.nibss.nip.dto.FTSingleDebitResponse;
import com.nibss.nip.dto.MandateAdviceRequest;
import com.nibss.nip.dto.MandateAdviceResponse;
import com.nibss.nip.dto.NESingleRequest;
import com.nibss.nip.dto.NESingleResponse;
import com.nibss.util.SessionUtil;

@Component("applicationService")
// @Configuration
@PropertySource("file:${cmms.app.config}/app.properties")
public class ApplicationService {

	private final static Logger logger = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	private NotificationUtils notificationUtils;
	@Autowired
	private RestClient restClient;

	@Autowired
	private TransactionService transactionService;
	/*
	 * @Autowired private WebserviceNotificationService
	 * webserviceNotificationService;
	 * 
	 * @Autowired private BillingTransactionService billingTransactionService;
	 */
	@Autowired
	private MandateService mandateService;

	@Autowired
	private BillerService billerService;

	@Autowired
	private BankService bankService;

	@Autowired
	private NipDAO nipayment;

	@Value("${cmms.channel.code}")
	private int cmmChannelCode;

	@Value("${max.suspended.mandate.days}")
	private int maxSuspendedMandateDays;

	@Value("${creditFilePath}")
	private String creditFilePath;

	@Value("${billingFilePath}")
	private String billingFilePath;

	@Value("#{new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").parse(\"${creditTrialStartDate}\") }")
	private Date creditTrialStartDate;

	@Value("#{new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").parse(\"${billingStartDate}\") }")
	private Date billingStartDate;

	@Value("#{new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").parse(\"${billingEndDate}\") }")
	private Date billingEndDate;

	@Autowired
	private MailSender mailSender;

	/**
	 * This method is triggered by the cron ${suspend.mandate.cron} in the
	 * app.properties file
	 *
	 * The method simply flags a suspended mandate that has stayed in the
	 * application from more that
	 *
	 */
	public void deleteSuspendedMandate() {

		logger.info(Thread.currentThread().getName() + ":__Running the delete suspended job__");
		try {
			Calendar minDate = Calendar.getInstance();
			minDate.add(Calendar.DATE, -maxSuspendedMandateDays);

			int count = mandateService.updateMandateRequest(
					() -> "update Mandate set requestStatus=? where requestStatus=? and dateSuspended <=? ",
					WebAppConstants.STATUS_MANDATE_DELETED, WebAppConstants.STATUS_MANDATE_SUSPENDED,
					DateUtils.nullifyTime(minDate.getTime()));

			logger.info("__[" + count + "] records updated in deleteSuspendedMandate()__");

		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	/**
	 *
	 * Only posts fresh transactions. Directed by cron ${debit.cron} in the
	 * app.properties file
	 *
	 * Passes list of fetched transactions to the transactionProcessor(List<?
	 * extends Transaction> t) method
	 *
	 */
	public void postTransactions() {
		logger.info(Thread.currentThread().getName() + ":__Running the transaction posting job__");
		try {

			List<Transaction> transactions = transactionService.getTransactions(
					() -> "from Transaction t where t.status=? and t.numberOfTrials=? and t.mandate.frequency !=?  and t.amount >= ?",
					WebAppConstants.PAYMENT_ENTERED, 0, 0, 100000000);

			logger.info("__Size of transactions fetched [" + transactions.size() + "]__ in postTransactions()");

			transactionProcessor(transactions);
		} catch (Exception e) {
			logger.error(null, e);
		}

	}

	/**
	 * This method is triggered by the cron ${first.transaction.retry.cron} in
	 * the app.properties file
	 *
	 * Re-processes transactions that have stay in that was originally tried 2
	 * days ago.
	 *
	 */
	public void firstTransactionPostingRetry() {
		logger.info(Thread.currentThread().getName() + ":__Running the first transaction posting retry job__");
		try {
			Calendar minDate = Calendar.getInstance();
			minDate.add(Calendar.DATE, -2);

			List<Transaction> transactions = transactionService.getTransactions(
					() -> "from Transaction t where t.status!=? and t.numberOfTrials=? and t.dateCreated >=? and t.mandate.frequency !=?",
					WebAppConstants.PAYMENT_SUCCESSFUL, 1, DateUtils.nullifyTime(minDate.getTime()), 0);

			logger.info(
					"__Size of transactions fetched [" + transactions.size() + "]__ in firstTransactionPostingRetry()");

			transactionProcessor(transactions);
		} catch (Exception e) {
			logger.error(null, e);
		}

	}

	/**
	 * This method is triggered by the cron ${second.transaction.retry.cron} in
	 * the app.properties file
	 *
	 * Re-processes transactions that have stay in that was originally tried 2
	 * days ago.
	 *
	 */
	public void secondTransactionPostingRetry() {
		logger.info(Thread.currentThread().getName() + ":__Running the second transaction posting retry job__");
		try {
			Calendar minDate = Calendar.getInstance();
			minDate.add(Calendar.DATE, -3);
			List<Transaction> transactions = transactionService.getTransactions(
					() -> "from Transaction t where t.status!=? and t.numberOfTrials=? and t.dateCreated >=? and  t.mandate.frequency !=?",
					WebAppConstants.PAYMENT_SUCCESSFUL, 2, DateUtils.nullifyTime(minDate.getTime()), 0);

			logger.info("__Size of transactions fetched [" + transactions.size()
					+ "]__ in secondTransactionPostingRetry()");

			transactionProcessor(transactions);
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	/**
	 * This should run as defined in the ${mandate.migration.cron} in
	 * app.properties. It creates transaction records in the transactions table
	 * for mandates that are due for posting
	 *
	 */
	/*
	 * public void moveDailyDueTransactions() {
	 * logger.info(Thread.currentThread().getName() +
	 * ":__Running the mandate migration job__"); try { List<Mandate> mandates =
	 * mandateService.getMandates( () ->
	 * "from Mandate p WHERE p.mandateAdviceSent=? and cast(p.nextDebitDate as date) between ? and ? and p.requestStatus=?"
	 * +
	 * "and p.nextDebitDate <= p.endDate and p.channel=? and  p.status.id in (?,?)"
	 * , Boolean.TRUE, trimToBOD(new Date()), trimToEOD(new Date()),
	 * WebAppConstants.STATUS_ACTIVE, WebAppConstants.CHANNEL_PORTAL,
	 * WebAppConstants.BANK_APPROVE_MANDATE,
	 * WebAppConstants.BILLER_APPROVE_MANDATE);
	 * 
	 * logger.info("__Size of mandate request fetched [" + mandates.size() +
	 * "]__ in moveDailyDueTransactions()"); mandates.stream().forEach(m -> {
	 * Transaction t = new Transaction(); t.setMandate(m);
	 * t.setStatus(WebAppConstants.PAYMENT_ENTERED); if
	 * (m.isFixedAmountMandate()) { t.setAmount(m.getAmount()); } else {
	 * t.setAmount(m.getVariableAmount()); } t.setDateCreated(new Date());
	 * t.setTransactionType(Integer.valueOf(WebAppConstants.TRANSACTION_APP));
	 * try { transactionService.saveTransaction(t); Date nextDebitDate = null;
	 * //Date nextDebitDate =
	 * DateUtils.calculateNextDebitDate(t.getDateCreated(), m.getEndDate(),
	 * m.getFrequency()); int freqModulo =m.getFrequency()%4; if(
	 * m.getFrequency()>0 && freqModulo==0){
	 * nextDebitDate=getNextDebitDate(t.getDateCreated(),m.getStartDate(),
	 * m.getEndDate(), m.getFrequency()/4); }else{
	 * nextDebitDate=DateUtils.calculateNextDebitDate(m.getStartDate(),
	 * m.getEndDate(), m.getFrequency()); }
	 * 
	 * 
	 * m.setNextDebitDate( nextDebitDate == null ?
	 * DateUtils.lastSecondOftheDay(m.getEndDate()) : nextDebitDate); try{
	 * logger.info("Calculating Next Debit Date " + "\nMandate Code : "
	 * +m.getMandateCode() +"\nStart Date : "+sdf2.format(m.getStartDate()) +
	 * "\nDate Created : "+sdf2.format(t.getDateCreated())
	 * 
	 * 
	 * +"\nEnd Date : "+sdf2.format( m.getEndDate()) +"\nFrequency : "+
	 * m.getFrequency() //+"\nNext Debit Date : "
	 * +nextDebitDate==null?null:sdf2.format( nextDebitDate) );
	 * 
	 * logger.info("Final Next Debit Date " + "\nMandate Code : "
	 * +m.getMandateCode() +"\nNext Debit Date : "+sdf2.format(
	 * nextDebitDate));} catch(Exception esp){ logger.error(esp.getMessage(),
	 * esp); //esp.printStackTrace(); }
	 * 
	 * mandateService.modifyMandate(m); } catch (Exception e) {
	 * logger.error(null, e); } }); } catch (Exception e) { logger.error(null,
	 * e); } }
	 */

	private Date nextDebitDateLocalLogic(Mandate m, Transaction t) {
		Date nextDebitDate_ = null;
		int freqModulo = m.getFrequency() % 4;

		if (m.getFrequency() > 0 && freqModulo == 0) {
			nextDebitDate_ = getNextDebitDate(t.getDateCreated(), m.getStartDate(), m.getEndDate(),
					m.getFrequency() / 4);
		} else {
			nextDebitDate_ = DateUtils.calculateNextDebitDate(m.getStartDate(), m.getEndDate(), m.getFrequency());
		}
		return nextDebitDate_;
	}

	private int compareDates(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		calendar1.set(Calendar.HOUR, 0);
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE, 0);
		calendar1.set(Calendar.SECOND, 0);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		calendar2.set(Calendar.HOUR, 0);
		calendar2.set(Calendar.HOUR_OF_DAY, 0);
		calendar2.set(Calendar.MINUTE, 0);
		calendar2.set(Calendar.SECOND, 0);

		return calendar2.compareTo(calendar1);

	}

	public Date getNextDebitDate(Date lastCreatedDate, Date startDate, Date endDate, int month) {
		Calendar startDateCalendar = Calendar.getInstance();
		startDateCalendar.setTime(startDate);
		Calendar nextDebitDate = Calendar.getInstance();
		nextDebitDate.setTime(lastCreatedDate);
		while (nextDebitDate.getTime().before(new Date())) {
			nextDebitDate.add(Calendar.MONTH, month);
			LocalDateTime nxtDD = LocalDateTime.ofInstant(nextDebitDate.toInstant(), ZoneId.systemDefault());
			if (nxtDD.getMonth().maxLength() >= startDateCalendar.get(Calendar.DAY_OF_MONTH)) {
				nextDebitDate.set(Calendar.DATE, startDate.getDate());
			}
			nextDebitDate.set(Calendar.HOUR_OF_DAY, 0);
			nextDebitDate.set(Calendar.MINUTE, 0);
			nextDebitDate.set(Calendar.SECOND, 0);
			Calendar endMandateDate = Calendar.getInstance();
			endMandateDate.setTime(endDate);
			endMandateDate.set(Calendar.HOUR_OF_DAY, 0);
			endMandateDate.set(Calendar.MINUTE, 0);
			endMandateDate.set(Calendar.SECOND, 0);
			if (endMandateDate.compareTo(nextDebitDate) <= 0) {
				return null;
			}
		}
		return nextDebitDate.getTime();
	}

	public void correctNextDebitDates() {

		logger.info(Thread.currentThread().getName() + ":__Running the next debit date sanitizer job__");

		try {
			List<Mandate> mandates = mandateService.getMandates(
					() -> "from Mandate p WHERE p.mandateAdviceSent=? and cast(p.nextDebitDate as date) between ? and ? and p.requestStatus=?"
							+ "and p.nextDebitDate <= p.endDate and p.channel=? and  p.status.id in (?,?)",
					Boolean.TRUE, trimToBOD(new Date()), trimToEOD(new Date()), WebAppConstants.STATUS_ACTIVE,
					WebAppConstants.CHANNEL_PORTAL, WebAppConstants.BANK_APPROVE_MANDATE,
					WebAppConstants.BILLER_APPROVE_MANDATE);

			mandates.stream().forEach(m -> {
				// getNextDebitDate(m., Date startDate, Date endDate, int month)

			});
		} catch (Exception e) {

		}

		// filter out those with correct next debit dates
		// take last created date if null, pass, if exists
		// mandates.stream().findAny()
	}

	public void moveDailyDueTransactions() {

		logger.info(Thread.currentThread().getName() + ":__Running the mandate migration job__");
		/*
		 * try { List<Mandate> mandates=mandateService.getMandates(() ->
		 * "from Mandate p WHERE p.mandateAdviceSent=? and p.nextDebitDate=? and p.requestStatus=?"
		 * +
		 * "and p.nextDebitDate <= p.endDate and p.channel=? and  p.status.id in (?,?)"
		 * , Boolean.TRUE, DateUtils.nullifyTime(new Date()),
		 * WebAppConstants.STATUS_ACTIVE,WebAppConstants.CHANNEL_PORTAL,
		 * WebAppConstants.BANK_APPROVE_MANDATE,
		 * WebAppConstants.BILLER_APPROVE_MANDATE);
		 */

		/*
		 * 
		 * List<Mandate> mandates = mandateService.getMandates( () ->
		 * "from Mandate p WHERE p.mandateAdviceSent=? and cast(p.nextDebitDate as date) between ? and ? and p.requestStatus=?"
		 * +
		 * "and p.nextDebitDate <= p.endDate and p.channel=? and  p.status.id in (?,?) and p.endDate >= current_date"
		 * , Boolean.TRUE, trimToBOD(new Date()), trimToEOD(new Date()),
		 * WebAppConstants.STATUS_ACTIVE, WebAppConstants.CHANNEL_PORTAL,
		 * WebAppConstants.BANK_APPROVE_MANDATE,
		 * WebAppConstants.BILLER_APPROVE_MANDATE);
		 */

		try {
			List<Mandate> mandates = mandateService.getMandates(
					() -> "from Mandate p WHERE p.mandateAdviceSent=? and cast(p.nextDebitDate as date) between ? and ? and p.requestStatus=?"
							+ "and p.nextDebitDate <= p.endDate and p.channel=? and  p.status.id in (?,?)",
					Boolean.TRUE, trimToBOD(new Date()), trimToEOD(new Date()), WebAppConstants.STATUS_ACTIVE,
					WebAppConstants.CHANNEL_PORTAL, WebAppConstants.BANK_APPROVE_MANDATE,
					WebAppConstants.BILLER_APPROVE_MANDATE);

			// filter out those with correct next debit dates
			// take last created date if null, pass, if exists
			// mandates.stream().findAny()

			logger.info("__Size of mandate request fetched [" + mandates.size() + "]__ in moveDailyDueTransactions()");
			mandates.stream().forEach(m -> {
				Transaction t = new Transaction();
				t.setMandate(m);
				t.setStatus(WebAppConstants.PAYMENT_ENTERED);
				if (m.isFixedAmountMandate()) {
					t.setAmount(m.getAmount());
				} else {
					t.setAmount(m.getVariableAmount());
				}
				t.setDateCreated(new Date());
				t.setTransactionType(Integer.valueOf(WebAppConstants.TRANSACTION_APP));
				try {
					List<Transaction> oldTransactionsByMandate = transactionService.getTransactionByMandate(m);
					if (oldTransactionsByMandate != null && oldTransactionsByMandate.size() > 0) {
						Transaction lastTransaction = oldTransactionsByMandate.get(0);
						// get next transatcion date based on last transaction
						// date

						Date supposedNextDebitDate = nextDebitDateLocalLogic(m, lastTransaction);

						// is supposednextdebit date today
						int compareDates = compareDates(supposedNextDebitDate, new Date());
						if (compareDates == 0) {
							// today is the debit date
							transactionService.saveTransaction(t);
						} else if (compareDates > 0) {
							// next debit date is in front
							m.setNextDebitDate(supposedNextDebitDate);
						} else if (compareDates < 0) {
							// next debit date is behind

						}

					} else {

						transactionService.saveTransaction(t);
					}

					Date nextDebitDate = null;
					/*
					 * int freqModulo =m.getFrequency()%4; if(
					 * m.getFrequency()>0 && freqModulo==0){
					 * nextDebitDate=getNextDebitDate(t.getDateCreated(),m.
					 * getStartDate(), m.getEndDate(), m.getFrequency()/4);
					 * }else{ nextDebitDate=DateUtils.calculateNextDebitDate(m.
					 * getStartDate(), m.getEndDate(), m.getFrequency()); }
					 */

					nextDebitDate = nextDebitDateLocalLogic(m, t);

					m.setNextDebitDate(
							nextDebitDate == null ? DateUtils.lastSecondOftheDay(m.getEndDate()) : nextDebitDate);
					try {
						logger.info("Calculating Next Debit Date " + "\nMandate Code : " + m.getMandateCode()
								+ "\nStart Date : " + sdf2.format(m.getStartDate()) + "\nDate Created : "
								+ sdf2.format(t.getDateCreated()) + "\nEnd Date : " + sdf2.format(m.getEndDate())
								+ "\nFrequency : " + m.getFrequency());
						logger.info("Final Next Debit Date " + "\nMandate Code : " + m.getMandateCode()
								+ "\nNext Debit Date : " + sdf2.format(nextDebitDate));
					} catch (Exception esp) {
						logger.error(esp.getMessage(), esp);
					}
					mandateService.modifyMandate(m);
				} catch (Exception e) {
					logger.error(null, e);
				}
			});
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	private Calendar calendar;
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// private SimpleDateFormat sdf;
	public Date trimToBOD(Date date) {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			// calendar.setLenient(false);
		}
		date = date == null ? new Date() : date;
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public Date trimToEOD(Date date) {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			// calendar.setLenient(false);
		}
		date = date == null ? new Date() : date;
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/*
	 * Notifies a customer of an upcoming debit reminder.days.before.debit days
	 */
	@Scheduled(cron = "0 0 8 1/1 * ?")
	public void notifyCustomerBeforeDebit(@Value("${reminder.days.before.debit}") int numberOfDays) {
		Calendar minDate = Calendar.getInstance();
		minDate.add(Calendar.DATE, numberOfDays);
		try {
			List<Mandate> mandates = mandateService.getMandates(
					() -> "from Mandate p WHERE p.mandateAdviceSent=? and p.nextDebitDate=? and p.requestStatus=?"
							+ "and p.nextDebitDate <= p.endDate and p.channel=? and  p.status.id in (?,?)",
					Boolean.TRUE, DateUtils.nullifyTime(minDate.getTime()), WebAppConstants.STATUS_ACTIVE,
					WebAppConstants.CHANNEL_PORTAL, WebAppConstants.BANK_AUTHORIZE_MANDATE,
					WebAppConstants.BILLER_AUTHORIZE_MANDATE);

			JavaMailSenderImpl sender = (JavaMailSenderImpl) mailSender;
			MimeMessage msg = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String message = "<head><style type=\"text/css\">" + "<!--<style type=\"text/css\"> <!--"
					+ ".style1 {font-family: \"Century Gothic\";font-size: 12px;}" + "--></style></head>" + "<body>"
					+ "<p class=\"style1\"><b>Hello %s,</b></p>"
					+ "<p class=\"style1\">This is to inform you of an upcoming debit on your account;</p>"
					+ "<p class=\"style1\">Account Name: %s</p>" + "<p class=\"style1\">Account Number: %s</p>"
					+ "<p class=\"style1\">Bank: %s</p>" + "<p class=\"style1\">Biller: %s</p>"
					+ "<p class=\"style1\">Product: %s</p>" + "<p class=\"style1\">Amount: %s</p>"
					+ "<p class=\"style1\">Debit Date: %s</p>" + "<p class=\"style1\"></p>"
					+ "<p class=\"style1\">Kindly ensure your account is funded on or before this date.</p>" + "<br/>"
					+ "</body>";

			for (Mandate mandate : mandates) {
				try {
					helper.setSubject("Reminder on upcoming debit on CMMS for mandate - " + mandate.getMandateCode());
					helper.setTo(mandate.getEmail());
					/*
					 * helper.setText(String.format(message,
					 * mandate.getAccountName(), mandate.getAccountNumber(),
					 * mandate.getBank().getBankName(),
					 * mandate.getProduct().getBiller().getCompany().getName(),
					 * mandate.getAmount().toPlainString(), new
					 * SimpleDateFormat("YYYY/MM/dd").format(mandate.
					 * getNextDebitDate())), true);
					 */
					helper.setText(String.format(message, mandate.getPayerName(), mandate.getAccountName(),
							mandate.getAccountNumber(), mandate.getBank().getBankName(),
							mandate.getProduct().getBiller().getCompany().getName(),
							mandate.getProduct().getDescription(), mandate.getAmount().toPlainString(),
							new SimpleDateFormat("YYYY/MM/dd").format(mandate.getNextDebitDate())), true);
					sender.send(msg);
				} catch (Exception e) {
					logger.error(null, e);
				}
			}
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	public void updateMandateAdvices() {
		try {
			/*
			 * List<Mandate> mandates=mandateService.getMandates(() ->
			 * "from Mandate p WHERE p.mandateAdviceSent=? and p.status.id in (?,?)"
			 * , Boolean.FALSE,WebAppConstants.BANK_AUTHORIZE_MANDATE,
			 * WebAppConstants.BILLER_AUTHORIZE_MANDATE);
			 */
			List<Mandate> mandates = mandateService.getMandates(() -> "from Mandate p WHERE p.mandateAdviceSent=?",
					Boolean.FALSE);
			mandates.stream().forEach(m -> {
				try {
					sendMandateAdvice(m);

				} catch (Exception e) {
					logger.error(null, e);
				}
			});
		} catch (ServerBusinessException e) {
			logger.error(null, e);
		}
	}

	public void recalculateNextDebitDate() {
		logger.info(Thread.currentThread().getName() + "__Running the debit day recalculation job__");
		try {
			List<Mandate> mandates = mandateService.getMandates(
					() -> "from Mandate p WHERE p.endDate >= current_date() "
							+ "and p.nextDebitDate <= current_date() and p.frequency>? and requestStatus=?",
					0, WebAppConstants.STATUS_ACTIVE);

			logger.info("__Size of mandate request fetched [" + mandates.size() + "]__ in recalculateNextDebitDate()");
			mandates.stream().forEach(m -> {
				try {
					// calculate next detbit date using start date and range
					Date nextDebitDate = DateUtils.calculateNextDebitDate(m.getStartDate(), m.getEndDate(),
							m.getFrequency());
					m.setNextDebitDate(nextDebitDate == null ? DateUtils.lastSecondOftheDay(m.getEndDate())
							: DateUtils.nullifyTime(nextDebitDate));
					mandateService.modifyMandate(m);
				} catch (Exception e) {
					logger.error(null, e);
				}
			});
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	/**
	 * Processes a collection of transactions Sends a DD and DC request. Marks
	 * transaction status based on the following conditions
	 *
	 * WebAppConstants.PAYMENT_IN_PROGRESS: For the current transaction in
	 * progress WebAppConstants.PAYMENT_SUCCESSFUL: For payment that was
	 * successful, both debit and credit WebAppConstants.PAYMENT_REVERSED: For
	 * payment that debit was successful but credit failed
	 * WebAppConstants.PAYMENT_FAILED: For payment that debit failed outrightly,
	 * no need to try credit leg.
	 *
	 *
	 */
	public void transactionProcessor(List<? extends Transaction> transactions) {
		logger.debug(String.format("Got [%s] transactions", transactions.size()));

		transactions.stream().forEach(t -> {
			FTSingleDebitResponse ftDebitResponse = null;
			Mandate m = t.getMandate();
			t.setTransactionParam(new HashSet<>());
			try {
				t.setStatus(WebAppConstants.PAYMENT_IN_PROGRESS); // in progress
				t.setLastDebitDate(new Date());

				transactionService.updateTransaction(t);

				String debitSessionId = SessionUtil.generateSessionID("999", "999");

				try {
					ftDebitResponse = doDirectDebit(m, debitSessionId, t.getAmount());
				} catch (Exception e) {
					logger.error(null, e);
				}
				// update the dd table
				TransactionParam param = new TransactionParam();
				param.setDateCreated(new Date());

				String debitResponse = "95";
				try {
					if (ftDebitResponse != null) {
						debitResponse = ftDebitResponse.getResponseCode();
					}
				} catch (Exception ex) {

				}
				param.setDebitResponseCode(debitResponse);
				// param.setDebitResponseCode(ftDebitResponse == null ? "95" :
				// ftDebitResponse.getResponseCode());
				param.setSessionId(debitSessionId);
				param.setTransaction(t);

				param = transactionService.saveTransactionParam(param);

				// if (ftDebitResponse != null &&
				// ftDebitResponse.getResponseCode().equals("00")) {
				if (debitResponse.equals("00")) {
					FTSingleCreditResponse ftCreditResponse = null;
					try {
						// String creditSessionId =
						// SessionUtil.generateSessionID("999", "999");
						ftCreditResponse = doDirectCredit(m, debitSessionId, t.getAmount());
					} catch (Exception e) {
						logger.error(null, e);
					}
					// update the dc column
					String creditResponse = "95";
					try {
						if (ftCreditResponse != null) {
							creditResponse = ftCreditResponse.getResponseCode();
						}
					} catch (Exception ex) {

					}

					param.setCreditResponseCode(creditResponse);
					// param.setCreditResponseCode(ftCreditResponse == null ?
					// "95" : ftCreditResponse.getResponseCode());
					transactionService.updateTransactionParam(param);

					// if (ftCreditResponse != null &&
					// ftCreditResponse.getResponseCode().equals("00")) {
					if (creditResponse.trim().equals("00")) {
						// update the status to paid
						t.setStatus(WebAppConstants.PAYMENT_SUCCESSFUL);
					} else {
						// reverse transaction
						// t.setStatus(WebAppConstants.PAYMENT_REVERSED);
						// Assume transaction successfull if debit is
						// successfull
						t.setStatus(WebAppConstants.PAYMENT_SUCCESSFUL);

					}
				} else {
					t.setStatus(WebAppConstants.PAYMENT_FAILED);
				}
			} catch (Exception e) {
				logger.debug(null, e);
			} finally {
				t.setNumberOfTrials(t.getNumberOfTrials() + 1);
				transactionService.updateTransaction(t);
			}
		});
	}

	private static int DEBIT = 0;
	private static int CREDIT = 1;

	private FTSingleCreditResponse doDirectCredit(Mandate m, String debitSessionId, BigDecimal amount)
			throws NIPException {
		NESingleResponse neSingleResponse = new NESingleResponse();
		neSingleResponse = doNameEnquiry(m, debitSessionId, CREDIT);
		if (null == neSingleResponse || !neSingleResponse.getResponseCode().equals("00")) {
			logger.info("NESingleResponse in Fund transfer credit is either null or response code !=00");
			return null;
		}
		FTSingleCreditRequest ftCreditRequest = new FTSingleCreditRequest();
		// logger.info("Successfully credited [" + m.getMandateCode() + "]
		// session Id [" + debitSessionId + "]");
		ftCreditRequest.setNameEnquiryRef(neSingleResponse.getSessionID());
		// ftCreditRequest.setBeneficiaryAccountName(m.getProduct().getBiller().getAccountName());
		ftCreditRequest.setBeneficiaryAccountName(neSingleResponse.getAccountName());

		ftCreditRequest.setBeneficiaryBVN(neSingleResponse.getBankVerificationNumber() == null ? ""
				: neSingleResponse.getBankVerificationNumber());

		// ftCreditRequest.setBeneficiaryBVN(m.getProduct().getBiller().getBvn());
		ftCreditRequest.setBeneficiaryAccountNumber(m.getProduct().getBiller().getAccountNumber());
		ftCreditRequest.setBeneficiaryKycLevel(m.getProduct().getBiller().getKycLevel());
		ftCreditRequest.setChannelCode(cmmChannelCode);
		ftCreditRequest.setOriginatorAccountName(m.getAccountName());
		ftCreditRequest.setOriginatorAccountNumber(m.getAccountNumber());
		// ftCreditRequest.setAmount(m.getAmount().setScale(2,
		// RoundingMode.HALF_UP));
		ftCreditRequest.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
		/*
		 * if (m.isFixedAmountMandate()) {
		 * ftCreditRequest.setAmount(m.getAmount().setScale(2,
		 * RoundingMode.HALF_UP)); // t.setAmount(m.getAmount()); } else { //
		 * t.setAmount(m.getVariableAmount());
		 * ftCreditRequest.setAmount(m.getVariableAmount().setScale(2,
		 * RoundingMode.HALF_UP)); }
		 */

		ftCreditRequest.setOriginatorBVN(m.getBvn());
		ftCreditRequest.setOriginatorKycLevel(m.getKycLevel());
		ftCreditRequest.setDestinationInstitutionCode(m.getProduct().getBiller().getBank().getNipBankCode());
		/*
		 * ftCreditRequest.setNarration(m.getNarration());
		 * ftCreditRequest.setPaymentReference(m.getSubscriberCode());
		 */
		ftCreditRequest.setNarration(m.getSubscriberCode() + "_" + m.getProduct().getBiller().getCompany().getRcNumber()
				+ "_" + m.getMandateCode());
		ftCreditRequest.setPaymentReference("CMMS/" + m.getSubscriberCode());
		ftCreditRequest.setSessionID(debitSessionId);
		neSingleResponse = null;
		return nipayment.sendDirectCredit(ftCreditRequest);

	}

	private NESingleResponse doNameEnquiry(Mandate m, String neSessionId, int switchCode) throws NIPException {
		NESingleRequest nESingleRequest = new NESingleRequest();
		neSessionId = SessionUtil.generateSessionID("999", "999");
		// logger.info("Successfully [" + m.getMandateCode() + "] session Id ["
		// + neSessionId + "]");

		nESingleRequest.setSessionID(neSessionId);
		if (switchCode == DEBIT) {
			nESingleRequest.setAccountNumber(m.getAccountNumber());
			nESingleRequest.setChannelCode(cmmChannelCode);
			nESingleRequest.setDestinationInstitutionCode(m.getBank().getNipBankCode());

		} else if (switchCode == CREDIT) {
			nESingleRequest.setAccountNumber(m.getProduct().getBiller().getAccountNumber());
			nESingleRequest.setChannelCode(cmmChannelCode);
			nESingleRequest.setDestinationInstitutionCode(m.getProduct().getBiller().getBank().getNipBankCode());
		}
		return nipayment.sendNameEnquiry(nESingleRequest);

	}

	// private FTSingleDebitResponse doDirectDebit(Mandate m, String
	// debitSessionId,BigDecimal amount) throws NIPException {
	private FTSingleDebitResponse doDirectDebit(Mandate m, String debitSessionId, BigDecimal amount)
			throws NIPException {
		NESingleResponse neSingleResponse = new NESingleResponse();
		neSingleResponse = doNameEnquiry(m, debitSessionId, DEBIT);

		FTSingleDebitRequest ftDebitRequest = new FTSingleDebitRequest();

		if (null == neSingleResponse || !neSingleResponse.getResponseCode().equals("00")) {
			logger.info("NESingleResponse in Fund transfer debit is either null or response code !=00");
			return null;
		}
		ftDebitRequest.setNameEnquiryRef(neSingleResponse.getSessionID());
		ftDebitRequest.setBeneficiaryAccountName(m.getProduct().getBiller().getAccountName());
		ftDebitRequest.setBeneficiaryBVN(m.getProduct().getBiller().getBvn());
		ftDebitRequest.setBeneficiaryAccountNumber(m.getProduct().getBiller().getAccountNumber());
		ftDebitRequest.setBeneficiaryKycLevel(m.getProduct().getBiller().getKycLevel());
		ftDebitRequest.setChannelCode(cmmChannelCode);
		ftDebitRequest.setDebitAccountName(neSingleResponse.getAccountName());
		ftDebitRequest.setDebitAccountNumber(m.getAccountNumber());
		ftDebitRequest.setAmount(amount.setScale(2, RoundingMode.HALF_UP));

		/*
		 * if (m.isFixedAmountMandate()) {
		 * ftDebitRequest.setAmount(m.getAmount().setScale(2,
		 * RoundingMode.HALF_UP)); // t.setAmount(m.getAmount()); } else { //
		 * t.setAmount(m.getVariableAmount());
		 * ftDebitRequest.setAmount(m.getVariableAmount().setScale(2,
		 * RoundingMode.HALF_UP)); }
		 */

		ftDebitRequest.setDebitBVN(neSingleResponse.getBankVerificationNumber());
		ftDebitRequest.setDebitKycLevel(neSingleResponse.getKycLevel());
		ftDebitRequest.setDestinationInstitutionCode(m.getBank().getNipBankCode());
		ftDebitRequest.setMandateReferenceNumber(m.getMandateCode());
		/*
		 * ftDebitRequest.setNarration(m.getNarration());
		 * ftDebitRequest.setPaymentReference(m.getSubscriberCode());
		 */
		ftDebitRequest.setNarration(m.getSubscriberCode() + "_" + m.getProduct().getBiller().getCompany().getRcNumber()
				+ "_" + m.getMandateCode());
		ftDebitRequest.setPaymentReference("CMMS/" + m.getSubscriberCode());
		ftDebitRequest.setSessionID(debitSessionId);
		neSingleResponse = null;
		return nipayment.sendDirectDebit(ftDebitRequest);
	}

	@Async
	public void sendMandateAdvice(Mandate m) throws NIPException, ServerBusinessException {
		NESingleResponse neSingleResponse = new NESingleResponse();
		neSingleResponse = doNameEnquiry(m, SessionUtil.generateSessionID("999", "999"), DEBIT);
		if (null == neSingleResponse || !neSingleResponse.getResponseCode().equals("00")) {
			logger.info("NESingleResponse in Mandate Advice is either null or response code !=00");
			return;
		}
		MandateAdviceRequest mandateAdviceRequest = new MandateAdviceRequest();
		mandateAdviceRequest.setBeneficiaryAccountName(m.getProduct().getBiller().getAccountName());
		mandateAdviceRequest.setBeneficiaryBVN(m.getProduct().getBiller().getBvn());
		// mandateAdviceRequest.setBeneficiaryBVN(m.getProduct().getBiller().getAccountNumber());
		mandateAdviceRequest.setBeneficiaryAccountNumber(m.getProduct().getBiller().getAccountNumber());
		mandateAdviceRequest.setBeneficiaryKycLevel(m.getProduct().getBiller().getKycLevel());

		mandateAdviceRequest.setChannelCode(cmmChannelCode);

		mandateAdviceRequest.setAmount(m.getAmount().setScale(2, RoundingMode.HALF_UP));

		mandateAdviceRequest.setDebitAccountNumber(m.getAccountNumber());
		mandateAdviceRequest.setDebitAccountName(neSingleResponse.getAccountName());
		mandateAdviceRequest.setDebitBVN(neSingleResponse.getBankVerificationNumber());
		mandateAdviceRequest.setDebitKycLevel(neSingleResponse.getKycLevel());
		mandateAdviceRequest.setDestinationInsitutionCode(m.getBank().getNipBankCode());

		/*
		 * mandateAdviceRequest.setDebitAccountName(m.getAccountName());
		 * mandateAdviceRequest.setDebitAccountNumber(m.getAccountNumber());
		 * mandateAdviceRequest.setDebitBVN(m.getBvn());
		 * mandateAdviceRequest.setDebitKycLevel(m.getKycLevel());
		 * mandateAdviceRequest.setDestinationInsitutionCode(m.getBank().
		 * getNipBankCode());
		 */
		// mandateAdviceRequest.setDebitBankVerificationNumber(m.getAccountNumber());
		mandateAdviceRequest.setMandateReferenceNumber(m.getMandateCode());
		mandateAdviceRequest.setSessionId(SessionUtil.generateSessionID("999", "999"));

		if (!m.isMandateAdviceSent()) {
			MandateAdviceResponse mandateAdviceResponse = nipayment.sendMandateAdvice(mandateAdviceRequest);
			if (mandateAdviceResponse != null && (mandateAdviceResponse.getResponseCode().equals("00")
					|| mandateAdviceResponse.getResponseCode().equals("94")
					|| mandateAdviceResponse.getResponseCode().equals("26"))) {
				m.setMandateAdviceSent(true);
				mandateService.modifyMandate(m);
			}
		}

	}

	/**
	 * This should run as defined in the ${billing.cron} in app.properties.
	 * processes billing on successful transactions from monday the week before
	 * 00:00:00 till last sunday 23:59:59 This cron shld run monday mornings
	 * private static final String EMAIL_PATTERN =
	 * "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
	 * "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	 */

	public void postBilling() {

		logger.info(Thread.currentThread().getName() + ":__Running the billing posting job__");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		String billingEndDateMinFormat = simpleDateFormat.format(billingEndDate);
		String billingStartDateMinFormat = simpleDateFormat.format(billingStartDate);

		if (billingEndDateMinFormat.equals("0000-00-00") || billingStartDateMinFormat.equals("0000-00-00")) {
			billingEndDate = getLastWeek(Calendar.MONDAY, 1);
			billingStartDate = getLastWeek(Calendar.SUNDAY, 2);

		}

		String fileName = new SimpleDateFormat("yyyyMMdd").format(billingEndDate) + ".csv";
		String dir = billingFilePath;
		File file;
		FileWriter fileWriter;
		CsvListWriter csvWriter;
		try {
			file = new File(dir);
			if (!file.exists())
				file.mkdirs();

			file = new File(dir, fileName);

			fileWriter = new FileWriter(file);
			csvWriter = new CsvListWriter(fileWriter, CsvPreference.EXCEL_PREFERENCE);

			String[] mandateReportHeaders = new String[] { "Mandate Code", "Subscriber Code", "Payer Name",
					"Payer's Bank", "Biller", "Biller's Bank", "Biller's Account Name", "Biller's Account Number",
					"Product", "Amount", "Narration", "Transaction Date", "Payment Reference", "SessionId",

			};

			/*
			 * List<Biller> billers = billerService.getActiveBillers(); String
			 * feeLine = ""; SimpleDateFormat dateFormat = new SimpleDateFormat(
			 * "yyyy-MM-dd HH:mm:ss"); logger.info(
			 * "Running the billing posting job between "+
			 * dateFormat.format(billingStartDate)+" and "+
			 * dateFormat.format(billingEndDate));
			 * 
			 * for (Biller biller : billers) { BigDecimal feeSum = new
			 * BigDecimal("0.00"); List<Transaction> transactions =
			 * transactionService.getTransactions( () ->
			 * "from Transaction t where t.status=? and cast(t.dateCreated as date) between ? and ? and t.mandate.product.biller.id =? "
			 * , WebAppConstants.PAYMENT_SUCCESSFUL, billingStartDate,
			 * billingEndDate, biller.getId());
			 * 
			 * if (transactions != null && transactions.size() > 0) { feeSum =
			 * biller.getUnitFee().multiply(new
			 * BigDecimal(transactions.size()));
			 * 
			 * final String[] obj = new String[] {
			 * biller.getBank().getBankCode()+"150000", "19", new
			 * SimpleDateFormat("YYYYMMdd").format(billingEndDate),
			 * feeSum.toPlainString(),
			 * 
			 * }; csvWriter.write(obj); } }
			 */
			List<Bank> banks = bankService.getAllBanks();
			String feeLine = "";
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logger.info("Running the billing posting job between " + dateFormat.format(billingStartDate) + " and "
					+ dateFormat.format(billingEndDate));

			for (Bank bank : banks) {
				// BigDecimal feeSum = new BigDecimal("0.00");
				List<Transaction> transactions = transactionService.getTransactions(
						() -> "from Transaction t where t.status=? and cast(t.dateCreated as date) between ? and ? and t.mandate.product.biller.bank.id =? ",
						WebAppConstants.PAYMENT_SUCCESSFUL, billingStartDate, billingEndDate, bank.getId());
				BigDecimal totalFee = new BigDecimal("0.00");
				if (transactions != null && transactions.size() > 0) {
					for (Transaction tr : transactions) {
						totalFee = totalFee.add(tr.getMandate().getProduct().getBiller().getUnitFee());
					}
					final String[] obj = new String[] { bank.getBankCode() + "150000", "19",
							new SimpleDateFormat("YYYYMMdd").format(billingEndDate), totalFee.toPlainString(),

					};
					csvWriter.write(obj);
				}

				/*
				 * if (transactions != null && transactions.size() > 0) { feeSum
				 * = transactions.getUnitFee().multiply(new
				 * BigDecimal(transactions.size()));
				 * 
				 * final String[] obj = new String[] {
				 * biller.getBank().getBankCode()+"150000", "19", new
				 * SimpleDateFormat("YYYYMMdd").format(billingEndDate),
				 * feeSum.toPlainString(),
				 * 
				 * }; csvWriter.write(obj); }
				 */
			}
			csvWriter.close();

		} catch (Exception e) {
			logger.error(null, e);
		}

	}

	private Date getLastWeek(int dayOfTheWeek, int beginEnd) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		cal.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
		cal.add(Calendar.DAY_OF_WEEK, -7);
		if (beginEnd == 1) {
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		} else if (beginEnd == 2) {
			cal.set(Calendar.HOUR, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
		}

		return cal.getTime();
	}

	/**
	 * This should run as defined in the ${webservice.biller.notification.cron}
	 * in app.properties. processes billing on successful transactions from
	 * monday the week before 00:00:00 till last sunday 23:59:59 This cron shld
	 * run monday mornings
	 */

	/*
	 * public void notifyBiller() {
	 * 
	 * try { List<WebserviceNotification> webserviceNotifications =
	 * webserviceNotificationService .getWebserviceNotifications( () ->
	 * "from WebserviceNotification t where t.billerNotified=? and t.billerNotificationCounter and cast(t.dateCreated as date) between ? and ?"
	 * , 0, 5, getLastWeek(Calendar.MONDAY, 1), getLastWeek(Calendar.SUNDAY,
	 * 2));
	 * 
	 * if (webserviceNotifications != null && webserviceNotifications.size() >
	 * 0) {
	 * 
	 * for (WebserviceNotification webserviceNotification :
	 * webserviceNotifications) { Mandate m =
	 * mandateService.getMandateByMandateId(webserviceNotification.getId());
	 * switch (webserviceNotification.getNotificationType()) {
	 * 
	 * case 1:
	 * 
	 * m.getProduct().getBiller().getNotificationUrl();
	 * 
	 * break; case 2: break; }
	 * 
	 * } } } catch (ServerBusinessException e) { // TODO Auto-generated catch
	 * block logger.error(null, e); } }
	 */

	/**
	 * This should run as defined in the ${mandate.notification.bank.cron} in
	 * app.properties. It creates pushes fresh mandates to banks
	 *
	 *
	 */

	public void pushFreshMandatesToBanks() {

		logger.info(Thread.currentThread().getName() + ":__Running the mandate notification for banks__");

		try {
			List<Bank> allBanks = bankService.getAllBanks();
			for (Bank bank : allBanks) {
				if (bank.getNotificationUrl() != null && !bank.getNotificationUrl().equals("")) {

					// List<Mandate> mandates = mandateService.getMandates( //()
					// ->
					// " from Mandate p WHERE cast(p.dateModified as date)
					// between ? and ? and p.bank =?"
					// ,trimTopOfTheHour(new Date()), trimBottomOfTheHour(new
					// Date()),bank);
					List<Mandate> mandates = mandateService.getMandates(() -> " from Mandate p WHERE   p.bank =?",
							bank);

					logger.info("__Size of mandate request fetched [" + mandates.size()
							+ "]__ in pushFreshMandatesToBanks()");
					if (mandates != null) {
						JsonObject mandateObject = notificationUtils.buildBankMandateNotification(bank, mandates,
								"New");
						logger.info("REQUEST TO BANK " + mandateObject.toString());
						String responseFromBank = restClient.postToClient(bank.getNotificationUrl(),
								mandateObject.toString());
						logger.info("RESPONSE FROM BANK " + responseFromBank);

						// update sent to

					}
				}
			}
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	/**
	 * This should run as defined in the ${mandate.notification.biller.cron} in
	 * app.properties. It creates pushes fresh mandates to billers
	 *
	 *
	 */
	/*
	 * public void pushFreshMandatesToBillers() {
	 * 
	 * logger.info(Thread.currentThread().getName() +
	 * ":__Running the mandate notification for billers__");
	 * 
	 * try { List<Biller> allBiller = billerService.getAllBillers(); for (Biller
	 * biller : allBiller) { if (biller.getNotificationUrl() != null &&
	 * !biller.getNotificationUrl().equals("")) {
	 * 
	 * List<Mandate> mandates = mandateService.getMandates( () ->
	 * " from Mandate p WHERE  cast(p.dateModified as date) between ? and ? and p.biller =?"
	 * , trimTopOfTheHour(new Date()), trimBottomOfTheHour(new Date()),biller);
	 * 
	 * logger.info("__Size of mandate request fetched [" + mandates.size() +
	 * "]__ in pushFreshMandatesToBanks()"); if(mandates!=null){ JsonObject
	 * mandateObject =
	 * notificationUtils.buildBillerMandateNotification(biller,mandates, "New");
	 * logger.info("REQUEST TO BILLER "+mandateObject.getAsString()); String
	 * responseFromBiller = restClient.postToClient(biller.getNotificationUrl(),
	 * mandateObject.getAsString()); logger.info("RESPONSE FROM BILLER "
	 * +responseFromBiller);
	 * 
	 * // send object to rest server of bank
	 * 
	 * 
	 * //update sent to
	 * 
	 * } } } } catch (Exception e) { logger.error(null, e); } }
	 */
	public Date trimTopOfTheHour(Date date) {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			// calendar.setLenient(false);
		}
		date = date == null ? new Date() : date;
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, -1);
		// calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public Date trimBottomOfTheHour(Date date) {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			// calendar.setLenient(false);
		}
		date = date == null ? new Date() : date;
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, -1);
		// calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * This method is triggered by the cron
	 * ${first.credit.transaction.retry.cron} in the app.properties file
	 */
	public void firstCreditTransactionPostingRetry() {
		logger.info(Thread.currentThread().getName() + ":__Running the firstCreditTransactionPostingRetry retry job__");
		try {
			List<Transaction> transactions = transactionService.getTransactions(
					() -> " SELECT t from Transaction t, IN(t.transactionParam) x WHERE x.creditResponseCode != ? and x.debitResponseCode = ? AND t.numberOfCreditTrials =? AND t.status = ? and t.dateCreated >?",
					"00", "00", 0, WebAppConstants.PAYMENT_SUCCESSFUL, creditTrialStartDate);
			logger.info("__Size of transactions fetched [" + transactions.size()
					+ "]__ in firstCreditTransactionPostingRetry()");
			creditTransactionProcessor(transactions);
		} catch (Exception e) {
			logger.error(null, e);
		}

	}

	/**
	 * This method is triggered by the cron
	 * ${second.credit.transaction.retry.cron} in the app.properties file
	 */
	public void secondCreditTransactionPostingRetry() {
		logger.info(
				Thread.currentThread().getName() + ":__Running the secondCreditTransactionPostingRetry retry job__");
		try {
			List<Transaction> transactions = transactionService.getTransactions(
					() -> " SELECT t from Transaction t, IN(t.transactionParam) x WHERE x.creditResponseCode != ? and x.debitResponseCode = ? AND t.numberOfCreditTrials =? AND t.status = ? and t.dateCreated >?",
					"00", "00", 1, WebAppConstants.PAYMENT_SUCCESSFUL, creditTrialStartDate);
			logger.info("__Size of transactions fetched [" + transactions.size()
					+ "]__ in secondCreditTransactionPostingRetry()");
			creditTransactionProcessor(transactions);
		} catch (Exception e) {
			logger.error(null, e);
		}

	}

	/**
	 * This method is triggered by the cron
	 * ${third.credit.transaction.retry.cron} in the app.properties file
	 */
	public void thirdCreditTransactionPostingRetry() {
		logger.info(Thread.currentThread().getName() + ":__Running the thirdCreditTransactionPostingRetry retry job__");
		try {
			List<Transaction> transactions = transactionService.getTransactions(
					() -> " SELECT t from Transaction t, IN(t.transactionParam) x WHERE x.creditResponseCode != ? and x.debitResponseCode = ? AND t.numberOfCreditTrials =? AND t.status = ? and t.dateCreated >?",
					"00", "00", 2, WebAppConstants.PAYMENT_SUCCESSFUL, creditTrialStartDate);
			logger.info("__Size of transactions fetched [" + transactions.size()
					+ "]__ in thirdCreditTransactionPostingRetry()");
			creditTransactionProcessor(transactions);
		} catch (Exception e) {
			logger.error(null, e);
		}

	}

	public void creditTransactionProcessor(List<Transaction> transactions) {
		logger.info(String.format("Got [%s] transactions", transactions.size()));
		for (Transaction t : transactions) {
			Mandate m = t.getMandate();
			try {
				// int debitRetried=t.getTransactionParam().size();
				Set<TransactionParam> transactionParam = t.getTransactionParam();
				// Look for transaction param where credit is empty and debit is
				// 00, pick session_id for re_use
				transactionParam.stream().forEach(tp -> {

					if (tp.getDebitResponseCode() != null && tp.getDebitResponseCode().trim().equals("00")
							&& tp.getCreditResponseCode() != null && !tp.getCreditResponseCode().trim().equals("00")) {
						FTSingleCreditResponse ftCreditResponse = null;
						try {
							ftCreditResponse = doDirectCredit(m, tp.getSessionId(), t.getAmount());
							String creditResponse = "95";
							try {
								if (ftCreditResponse != null) {
									creditResponse = ftCreditResponse.getResponseCode();
								}
							} catch (Exception ex) {

							}
							// update the dc column

							logger.info(tp.getSessionId() + " >> "
									+ new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(t.getDateCreated()) + " >> "
									+ m.getProduct().getBiller().getAccountName() + " >> " + m.getMandateCode() + ">> "
									+ creditResponse);
							tp.setCreditResponseCode(creditResponse);
							/*
							 * logger.info(tp.getSessionId() + ": " +
							 * ftCreditResponse == null ? "95" :
							 * ftCreditResponse.getResponseCode());
							 * tp.setCreditResponseCode( ftCreditResponse ==
							 * null ? "95" :
							 * ftCreditResponse.getResponseCode());
							 */
							transactionService.updateTransactionParam(tp);
						} catch (Exception e) {
							logger.error(null, e);
						}
					}

				});

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("", e);
				logger.debug(null, e);
			} finally {

				t.setNumberOfCreditTrials(t.getNumberOfCreditTrials() + 1);
				t.setLastCreditDate(new Date());
				transactionService.updateTransaction(t);
			}
		}
		/*
		 * transactions.stream().forEach(t -> { Mandate m = t.getMandate(); try
		 * { //int debitRetried=t.getTransactionParam().size();
		 * Set<TransactionParam> transactionParam = t.getTransactionParam();
		 * //Look for transaction param where credit is empty and debit is 00,
		 * pick session_id for re_use transactionParam.stream().forEach(tp -> {
		 * 
		 * if(tp.getDebitResponseCode()!=null &&
		 * tp.getDebitResponseCode().trim().equals("00")&&tp.
		 * getCreditResponseCode()!=null&&tp.getCreditResponseCode().trim().
		 * equals("") ){ FTSingleCreditResponse ftCreditResponse = null; try {
		 * ftCreditResponse = doDirectCredit(m, tp.getSessionId()); // update
		 * the dc column tp.setCreditResponseCode(ftCreditResponse == null ? ""
		 * : ftCreditResponse.getResponseCode());
		 * 
		 * transactionService.updateTransactionParam(tp); } catch (Exception e)
		 * { logger.error(null, e); } }
		 * 
		 * });
		 * 
		 * 
		 * 
		 * } catch (Exception e) { logger.debug(null, e); } finally {
		 * 
		 * t.setNumberOfCreditTrials(t.getNumberOfCreditTrials() + 1);
		 * transactionService.updateTransaction(t); } });
		 */
	}

	/**
	 * This method is triggered by the cron
	 * ${credit.file.transaction.retry.cron} in the app.properties file
	 */
	public void fileCreditTransactionPosting() {
		logger.info(Thread.currentThread().getName() + ":__Running the fileCreditTransactionPostingRetry retry job__");
		try {
			List<Transaction> transactions = transactionService.getTransactions(
					() -> " SELECT t from Transaction t, IN(t.transactionParam) x WHERE x.creditResponseCode != ? and x.debitResponseCode = ? AND t.numberOfCreditTrials =?  AND t.status = ? and t.dateCreated >? and cast(t.dateCreated  as date) between ? and ?",
					"00", "00", 3, WebAppConstants.PAYMENT_SUCCESSFUL, creditTrialStartDate, trimToBOD(new Date()),
					trimToEOD(new Date()));
			logger.info(
					"__Size of transactions fetched [" + transactions.size() + "]__ in fileCreditTransactionPosting()");

			fileCreditTransactionProcessor(transactions);
		} catch (Exception e) {
			logger.info(null, e);
		}

	}

	public void fileCreditTransactionProcessor(List<Transaction> transactions) {
		logger.info(String.format("Got [%s] transactions", transactions.size()));
		String fileName = "";// "Test_File.csv";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dir = creditFilePath + "/" + sdf.format(new Date());

		// String _destination=mandateImagePath+File.separator+billerRCNumber;
		Map<String, List<Transaction>> byBank = transactions.stream()
				.collect(Collectors.groupingBy(t -> t.getMandate().getProduct().getBiller().getBank().getBankCode()));
		// List<Transaction> distinctTransactionsByDestination =
		// transactions.stream().filter(distinctByKey(p ->
		// p.getMandate().getBank().getBankCode())).collect(Collectors.toList());
		logger.info("No of Bank Size " + byBank.size());
		Set<String> bankCodes = byBank.keySet();
		try {
			File destination = new File(dir);

			logger.info("Destination " + dir);

			if (!destination.exists())
				destination.mkdirs();

			CsvListWriter csvWriter;
			FileWriter fileWriter;
			File file;

			// do all NIP transactions file here
			try {
				fileName = "NIP.csv";
				file = new File(dir, fileName);
				fileWriter = new FileWriter(file);
				csvWriter = new CsvListWriter(fileWriter, CsvPreference.EXCEL_PREFERENCE);
				writeFailedCreditNIPCSV(csvWriter, fileName, transactions);
				csvWriter.close();

			} catch (Exception ex) {
				logger.error(null, ex);
			}

			// do each bank transactions file here
			for (String bankCode : bankCodes) {
				fileName = bankCode + ".csv";
				file = new File(dir, fileName);
				fileWriter = new FileWriter(file);
				csvWriter = new CsvListWriter(fileWriter, CsvPreference.EXCEL_PREFERENCE);

				writeFailedCreditCSVReport(csvWriter, fileName, byBank.get(bankCode));
				csvWriter.close();
			}
		} catch (Exception e) {
			logger.error(null, e);
		}

	}
	/*
	 * public static <T> Predicate<T> distinctByKey(Function<? super T, Object>
	 * keyExtractor) { Map<Object, Boolean> map = new ConcurrentHashMap<>();
	 * return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	 * }
	 */

	public void writeFailedCreditCSVReport(ICsvListWriter csvWriter, String fileName, List<Transaction> transactions)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// @SuppressWarnings("unchecked")
		// List<Mandate> mandates= (List<Mandate>)model.get(REPORT_DATA);

		// setFileName(fileName);

		String[] mandateReportHeaders = new String[] { "Mandate Code", "Subscriber Code", "Payer Name", "Payer's Bank",
				"Biller", "Biller's Bank", "Biller's Account Name", "Biller's Account Number", "Product", "Amount",
				"Narration", "Transaction Date", "Payment Reference", "SessionId",

		};

		csvWriter.writeHeader(mandateReportHeaders);

		transactions.stream().forEach(transaction -> {
			Transaction t = transaction;
			try {
				Set<TransactionParam> transactionParams = t.getTransactionParam();

				transactionParams.stream().forEach(transactionParam -> {
					TransactionParam tp = transactionParam;
					if (transactionParam.getDebitResponseCode().equals("00")
							&& !transactionParam.getCreditResponseCode().equals("00")) {
						final String[] obj = new String[] { t.getMandate().getMandateCode(),
								t.getMandate().getSubscriberCode(), t.getMandate().getPayerName(),
								t.getMandate().getSubscriberCode(), t.getMandate().getBank().getBankName(),
								t.getMandate().getProduct().getBiller().getCompany().getName(),
								t.getMandate().getProduct().getBiller().getBank().getBankName(),
								t.getMandate().getProduct().getBiller().getAccountName(),
								t.getMandate().getProduct().getBiller().getAccountNumber(),
								t.getMandate().getProduct().getName(), t.getAmount().toPlainString(),
								t.getMandate().getSubscriberCode() + "_"
										+ t.getMandate().getProduct().getBiller().getCompany().getRcNumber() + "_"
										+ t.getMandate().getMandateCode(),
								sdf.format(t.getDateCreated()), "CMMS/" + t.getMandate().getSubscriberCode(),
								tp.getSessionId(),

						};
						try {
							csvWriter.write(obj);
						} catch (Exception e) {
							logger.error(null, e);
						}
					}
				});
			} catch (Exception e) {
				logger.error(null, e);
			}
		});
	}

	public void writeFailedCreditNIPCSV(ICsvListWriter csvWriter, String fileName, List<Transaction> transactions)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// @SuppressWarnings("unchecked")
		// List<Mandate> mandates= (List<Mandate>)model.get(REPORT_DATA);

		// setFileName(fileName);
		// id, session_id, name_enquiry_ref, destination_institution_code,
		// channel_code, beneficiary_account_name, beneficiary_account_number,
		// beneficiary_kyc_level, beneficiary_verfication_number,
		// originator_account_name, originator_account_number,
		// originator_bank_verification_number,
		// originator_kyc_level, transaction_location, narration,
		// paymentReference, amount, response_code, request_time, response_time
		String[] mandateReportHeaders = new String[] { "session_id", "name_enquiry_ref", "destination_institution_code",
				"channel_code", "beneficiary_account_name", "beneficiary_account_number", "beneficiary_kyc_level",
				"beneficiary_verfication_number", "originator_account_name", "originator_account_number",
				"originator_bank_verification_number", "originator_kyc_level", "transaction_location", "narration",
				"paymentReference", "amount", "response_code", "request_time", "response_time",

		};

		csvWriter.writeHeader(mandateReportHeaders);

		transactions.stream().forEach(transaction -> {
			Transaction t = transaction;
			try {
				Set<TransactionParam> transactionParams = t.getTransactionParam();

				transactionParams.stream().forEach(transactionParam -> {
					TransactionParam tp = transactionParam;
					if (transactionParam.getDebitResponseCode().equals("00")
							&& !transactionParam.getCreditResponseCode().equals("00")) {
						final String[] obj = new String[] {

								tp.getSessionId(), "",
								t.getMandate().getProduct().getBiller().getBank().getNipBankCode(), "" + cmmChannelCode,
								t.getMandate().getProduct().getBiller().getAccountName(),
								t.getMandate().getProduct().getBiller().getAccountNumber(), "", "",
								t.getMandate().getAccountName(), t.getMandate().getAccountNumber(), "", "", "",
								t.getMandate().getSubscriberCode() + "_"
										+ t.getMandate().getProduct().getBiller().getCompany().getRcNumber() + "_"
										+ t.getMandate().getMandateCode(),
								"CMMS/" + t.getMandate().getSubscriberCode(), t.getAmount().toPlainString(), "00",
								sdf.format(new Date()), sdf.format(new Date()),

						};
						try {
							csvWriter.write(obj);
						} catch (Exception e) {
							logger.error(null, e);
						}
					}
				});
			} catch (Exception e) {
				logger.error(null, e);
			}
		});
	}

	public String notifyMandateStatusAll(int notifyType, Mandate mandate, List<APITransaction> apiTransactions) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		JsonObject mandateStatus = new JsonObject();
		JsonObject mandateBody = new JsonObject();
		JsonArray mandateArray = new JsonArray();
		JsonObject headerElement = new JsonObject();

		try {

			if (notifyType == 0 && mandate != null
					&& mandate.getProduct().getBiller().getMandateStatusNotificationUrl() != null) {
				logger.info(notifyType + "  " + mandate + "  "
						+ mandate.getProduct().getBiller().getMandateStatusNotificationUrl());
				headerElement.addProperty("username", mandate.getProduct().getBiller().getBillerUserName());
				headerElement.addProperty("password", mandate.getProduct().getBiller().getBillerPassword());
				headerElement.addProperty("apiKey", mandate.getProduct().getBiller().getBillerPassKey());

				mandateBody.addProperty("notificationType", "Update");
				mandateBody.addProperty("accountName", mandate.getAccountName());
				mandateBody.addProperty("mandateCode", mandate.getMandateCode());
				mandateBody.addProperty("accountNumber", mandate.getAccountNumber());
				mandateBody.addProperty("bvn", mandate.getBvn());
				mandateBody.addProperty("email", mandate.getEmail());
				mandateBody.addProperty("narration", mandate.getNarration());
				mandateBody.addProperty("payerAddress", mandate.getPayerAddress());
				mandateBody.addProperty("payer", mandate.getPayerName());
				mandateBody.addProperty("phoneNumber", mandate.getPhoneNumber());
				mandateBody.addProperty("amount", mandate.getAmount());
				mandateBody.addProperty("subscriberCode", mandate.getSubscriberCode());
				mandateBody.addProperty("frequency", mandate.getFrequency());
				mandateBody.addProperty("kyc", mandate.getKycLevel());
				mandateBody.addProperty("bankCode", mandate.getBank().getBankCode());
				mandateBody.addProperty("bank", mandate.getBank().getBankName());
				mandateBody.addProperty("status", mandate.getRequestStatus());
				mandateBody.addProperty("endDate", sdf.format(mandate.getEndDate()));
				mandateBody.addProperty("startDate", sdf.format(mandate.getStartDate()));
				mandateBody.addProperty("productId", mandate.getProduct().getId());
				mandateBody.addProperty("productName", mandate.getProduct().getName());
				mandateBody.addProperty("billerId", mandate.getProduct().getBiller().getId());
				mandateBody.addProperty("biller", mandate.getProduct().getBiller().getCompany().getName());
				mandateBody.addProperty("rc", mandate.getProduct().getBiller().getCompany().getRcNumber());
				mandateBody.addProperty("billerAccountName", mandate.getProduct().getBiller().getAccountName());
				mandateBody.addProperty("billerAccountNumber", mandate.getProduct().getBiller().getAccountNumber());
				mandateBody.addProperty("billerBankCode", mandate.getProduct().getBiller().getBank().getBankCode());
				mandateBody.addProperty("billerBankName", mandate.getProduct().getBiller().getBank().getBankName());
				mandateBody.addProperty("workFlowStatus", mandate.getStatus().getId());
				mandateBody.addProperty("workFlowStatusDescription", mandate.getStatus().getName());
				mandateBody.addProperty("responseCode", "00");
				mandateArray.add(mandateBody);
				mandateStatus.add("auth", headerElement);
				mandateStatus.add("mandates", mandateArray);
				logger.info("Mandate Approval Notification To "
						+ mandate.getProduct().getBiller().getMandateStatusNotificationUrl() + "  "
						+ mandateStatus.toString());
				String response = restClient.postToClient(
						mandate.getProduct().getBiller().getMandateStatusNotificationUrl(), mandateStatus.toString());

				logger.info("Response From Client :: " + response);
				return response;

			} else if (notifyType == 1 && apiTransactions != null) {
				JsonObject paymentStatus = new JsonObject();
				JsonArray paymentStatusArray = new JsonArray();

				if (apiTransactions != null && !apiTransactions.isEmpty()) {
					headerElement.addProperty("username",
							apiTransactions.get(0).getMandate().getProduct().getBiller().getBillerUserName());
					headerElement.addProperty("password",
							apiTransactions.get(0).getMandate().getProduct().getBiller().getBillerPassword());
					headerElement.addProperty("apiKey",
							apiTransactions.get(0).getMandate().getProduct().getBiller().getBillerPassKey());
				}
				paymentStatus.add("auth", headerElement);

				apiTransactions.stream().forEach(t -> {
					//JsonObject paymentStatusElement = new JsonObject();
					JsonObject param = new JsonObject();


					Map<String, String> params = new HashMap<>();
					TransactionParam tParam = null;
					if (t.getTransactionParam() != null && !t.getTransactionParam().isEmpty()) {
						tParam = t.getTransactionParam().stream().findFirst().get();
					}

					if (tParam != null) {
						param.addProperty("transactionId", tParam.getSessionId().substring(6));
						param.addProperty("valueDate", sdf.format(tParam.getDateCreated()));
						param.addProperty("responseCode", tParam.getDebitResponseCode());
						//param.addProperty("responseCode", "00");
					}
					param.addProperty("requestDate", sdf.format(t.getDateCreated()));
					param.addProperty("batchId", ((APITransaction) t).getBatchId());
					param.addProperty("mandateCode", t.getMandate().getMandateCode());
					paymentStatusArray.add(param);
					
					t.setIsClosed("1");

					// Set as closed

					transactionService.updateTransaction(t);

				});
				paymentStatus.add("PaymentStatus", paymentStatusArray);

				String response = "";
				if (apiTransactions.get(0).getMandate().getProduct().getBiller()
						.getPaymentStatusNotificationUrl() != null) {
					logger.info("Payment Status Notification To " + apiTransactions.get(0).getMandate().getProduct()
							.getBiller().getPaymentStatusNotificationUrl() + "  " + paymentStatus.toString());
					response = restClient.postToClient(apiTransactions.get(0).getMandate().getProduct().getBiller()
							.getPaymentStatusNotificationUrl(), paymentStatus.toString());
					logger.info("Response From Client :: " + response);
				} else {
					logger.info("No Notification url for "
							+ apiTransactions.get(0).getMandate().getProduct().getBiller().getAccountName());
				}
				return response;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return "";
		// responseCode = SUCCESS;
	}

	public void getAllOpenPaymentNotifications() {
		try {
			logger.info("Payment Notification CRON >>");
			Map<String, Object> filters = new TreeMap<>();
			Set<String> uniqueBatchIds = null;
			filters.put("isClosed", "0");
			// filters.put("dateCreated", value)
			List<APITransaction> apiTransactions = transactionService.getAPITransactions(filters);
			//logger.info("API Transaction Size" + apiTransactions.size());
			uniqueBatchIds = apiTransactions.stream().map(a -> a.getBatchId()).distinct().collect(Collectors.toSet());

			logger.info("Batch Id Size  " + uniqueBatchIds.size());

			for (String uniqueBatchId : uniqueBatchIds) {
				//logger.info("Batch Id  " + uniqueBatchId);
				List<APITransaction> apiTransactionsFilteredByBatchId = apiTransactions.stream()
						.filter(apiTransaction -> apiTransaction.getBatchId().equals(uniqueBatchId))
						.collect(Collectors.toList());
				//logger.info("Filtered Batch Id Size ::  " + apiTransactionsFilteredByBatchId.size());
				//logger.info(uniqueBatchId);
				notifyMandateStatusAll(1, null, apiTransactionsFilteredByBatchId);

			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}

}
