package com.nibss.cmms.app.service;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import javax.mail.internet.MimeMessage;

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

import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.Transaction;
import com.nibss.cmms.domain.TransactionParam;
import com.nibss.cmms.service.MandateService;
import com.nibss.cmms.service.TransactionService;
import com.nibss.cmms.utils.DateUtils;
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
@PropertySource("file:${cmms.app.config}/app.properties")
public class ApplicationService {

	private final static Logger logger = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private MandateService mandateService;

	@Autowired
	private NipDAO nipayment;

	@Value("${cmms.channel.code}")
	private int cmmChannelCode;

	@Value("${max.suspended.mandate.days}")
	private int maxSuspendedMandateDays;

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
					() -> "from Transaction t where t.status=? and t.numberOfTrials=?", WebAppConstants.PAYMENT_ENTERED,
					0);

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
					() -> "from Transaction t where t.status!=? and t.numberOfTrials=? and t.dateCreated >=?",
					WebAppConstants.PAYMENT_SUCCESSFUL, 1, DateUtils.nullifyTime(minDate.getTime()));

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
					() -> "from Transaction t where t.status!=? and t.numberOfTrials=? and t.dateCreated >=?",
					WebAppConstants.PAYMENT_SUCCESSFUL, 2, DateUtils.nullifyTime(minDate.getTime()));

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
		try {
			List<Mandate> mandates = mandateService.getMandates(
					() -> "from Mandate p WHERE p.mandateAdviceSent=? and cast(p.nextDebitDate as date) between ? and ? and p.requestStatus=?"
							+ "and p.nextDebitDate <= p.endDate and p.channel=? and  p.status.id in (?,?)",
					Boolean.TRUE, trimToBOD(new Date()), trimToEOD(new Date()), WebAppConstants.STATUS_ACTIVE,
					WebAppConstants.CHANNEL_PORTAL, WebAppConstants.BANK_APPROVE_MANDATE,
					WebAppConstants.BILLER_APPROVE_MANDATE);

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
					transactionService.saveTransaction(t);
					Date nextDebitDate = DateUtils.calculateNextDebitDate(m.getStartDate(), m.getEndDate(),
							m.getFrequency());
					m.setNextDebitDate(
							nextDebitDate == null ? DateUtils.lastSecondOftheDay(m.getEndDate()) : nextDebitDate);
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
					helper.setText(String.format(message, mandate.getAccountName(), mandate.getAccountNumber(),
							mandate.getBank().getBankName(), mandate.getProduct().getBiller().getCompany().getName(),
							mandate.getAmount().toPlainString(),
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
					ftDebitResponse = doDirectDebit(m, debitSessionId);
				} catch (Exception e) {
					logger.error(null, e);
				}
				// update the dd table
				TransactionParam param = new TransactionParam();
				param.setDateCreated(new Date());
				param.setDebitResponseCode(ftDebitResponse == null ? "" : ftDebitResponse.getResponseCode());
				param.setSessionId(debitSessionId);
				param.setTransaction(t);

				param = transactionService.saveTransactionParam(param);

				if (ftDebitResponse != null && ftDebitResponse.getResponseCode().equals("00")) {
					FTSingleCreditResponse ftCreditResponse = null;
					try {
						// String creditSessionId =
						// SessionUtil.generateSessionID("999", "999");
						ftCreditResponse = doDirectCredit(m, debitSessionId);
					} catch (Exception e) {
						logger.error(null, e);
					}
					// update the dc column
					param.setCreditResponseCode(ftCreditResponse == null ? "" : ftCreditResponse.getResponseCode());
					transactionService.updateTransactionParam(param);

					if (ftCreditResponse != null && ftCreditResponse.getResponseCode().equals("00")) {
						// update the status to paid
						t.setStatus(WebAppConstants.PAYMENT_SUCCESSFUL);
					} else {
						// reverse transaction
						t.setStatus(WebAppConstants.PAYMENT_REVERSED);
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

	private FTSingleCreditResponse doDirectCredit(Mandate m, String debitSessionId) throws NIPException {
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
		ftCreditRequest.setBeneficiaryBVN(m.getProduct().getBiller().getBvn());
		ftCreditRequest.setBeneficiaryAccountNumber(m.getProduct().getBiller().getAccountNumber());
		ftCreditRequest.setBeneficiaryKycLevel(m.getProduct().getBiller().getKycLevel());
		ftCreditRequest.setChannelCode(cmmChannelCode);
		ftCreditRequest.setOriginatorAccountName(m.getAccountName());
		ftCreditRequest.setOriginatorAccountNumber(m.getAccountNumber());
		ftCreditRequest.setAmount(m.getAmount().setScale(2, RoundingMode.HALF_UP));
		ftCreditRequest.setOriginatorBVN(m.getBvn());
		ftCreditRequest.setOriginatorKycLevel(m.getKycLevel());
		ftCreditRequest.setDestinationInstitutionCode(m.getProduct().getBiller().getBank().getNipBankCode());
		ftCreditRequest.setNarration(m.getNarration());
		ftCreditRequest.setPaymentReference(m.getSubscriberCode());
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

	private FTSingleDebitResponse doDirectDebit(Mandate m, String debitSessionId) throws NIPException {
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
		ftDebitRequest.setAmount(m.getAmount().setScale(2, RoundingMode.HALF_UP));
		ftDebitRequest.setDebitBVN(neSingleResponse.getBankVerificationNumber());
		ftDebitRequest.setDebitKycLevel(neSingleResponse.getKycLevel());
		ftDebitRequest.setDestinationInstitutionCode(m.getBank().getNipBankCode());
		ftDebitRequest.setMandateReferenceNumber(m.getMandateCode());
		ftDebitRequest.setNarration(m.getNarration());
		ftDebitRequest.setPaymentReference(m.getSubscriberCode());
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
	 * This should run as defined in the ${mandate.billing.cron} in
	 * app.properties. processes billing on successful transactions from monday
	 * the week before 00:00:00 till last sunday 23:59:59 This cron shld run
	 * monday mornings
	 */

	public void postBilling() {
		logger.info(Thread.currentThread().getName() + ":__Running the transaction posting job__");
		try {
			List<Transaction> transactions = transactionService.getTransactions(
					() -> "from Transaction t where t.status=? and cast(t.dateCreated as date) between ? and ? ",
					WebAppConstants.PAYMENT_SUCCESSFUL, getLastWeek(Calendar.MONDAY,1), getLastWeek(Calendar.SUNDAY,2));

			logger.info("__Size of transactions fetched [" + transactions.size() + "]__ in Billing Transactions()");

			// transactionProcessor(transactions);

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

}
