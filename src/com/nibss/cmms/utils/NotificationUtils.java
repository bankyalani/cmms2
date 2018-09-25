package com.nibss.cmms.utils;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.domain.Mandate;
@Component
public class NotificationUtils {
	public JsonObject buildBankMandateNotification(Bank bank, List<Mandate> mandates, String type) throws Exception {
		JsonObject jsonObject = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Gson gson = new Gson();
		JsonObject auth = new JsonObject();
		auth.addProperty("username", bank.getBankUserName());
		auth.addProperty("password", bank.getBankPassword());
		auth.addProperty("apiKey", bank.getBankPassKey());
		//String responseCode = SYSTEM_ERROR;
		
		JsonArray jsonArray = new JsonArray();
		for (Mandate  mandate : mandates) {
		
			
				jsonObject = new JsonObject();
				
				jsonObject.addProperty("notificationType", type);
				jsonObject.addProperty("accountName", mandate.getAccountName());
				jsonObject.addProperty("mandateCode",mandate.getMandateCode());
				jsonObject.addProperty("accountNumber", mandate.getAccountNumber());
				jsonObject.addProperty("bvn", mandate.getBvn());
				jsonObject.addProperty("emailAddress", mandate.getEmail());
				jsonObject.addProperty("narration", mandate.getNarration());
				jsonObject.addProperty("payerAddress", mandate.getPayerAddress());
				jsonObject.addProperty("payerName", mandate.getPayerName());
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
				jsonObject.addProperty("billerName", mandate.getProduct().getBiller().getCompany().getName());
				jsonObject.addProperty("rc", mandate.getProduct().getBiller().getCompany().getRcNumber());
				jsonObject.addProperty("billerAccountName", mandate.getProduct().getBiller().getAccountName());
				jsonObject.addProperty("billerAccountNumber", mandate.getProduct().getBiller().getAccountNumber());
				jsonObject.addProperty("billerAccountBankCode", mandate.getProduct().getBiller().getBank().getBankCode());
				jsonObject.addProperty("billerBankName", mandate.getProduct().getBiller().getBank().getBankName());
				jsonObject.addProperty("workFlowStatus", mandate.getStatus().getId());
				jsonObject.addProperty("workFlowStatusDescription", mandate.getStatus().getName());
				jsonArray.add(jsonObject);
			}
		JsonObject outer = new JsonObject();
		outer.add("auth", auth);
		outer.add("mandateRequests", jsonArray);
	return outer;
	}
	
	public JsonObject buildBillerMandateNotification(Biller biller, List<Mandate> mandates, String type) throws Exception {
		JsonObject jsonObject = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Gson gson = new Gson();
		JsonObject auth = new JsonObject();
		auth.addProperty("username", biller.getBillerUserName());
		auth.addProperty("password", biller.getBillerPassword());
		auth.addProperty("apiKey", biller.getBillerPassKey());
		//String responseCode = SYSTEM_ERROR;
		
		JsonArray jsonArray = new JsonArray();
		for (Mandate  mandate : mandates) {
		
			
				jsonObject = new JsonObject();
				
				jsonObject.addProperty("notificationType", type);
				jsonObject.addProperty("accountName", mandate.getAccountName());
				jsonObject.addProperty("mandateCode",mandate.getMandateCode());
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
				jsonArray.add(jsonObject);
			}
		JsonObject outer = new JsonObject();
		outer.add("auth", auth);
		outer.add("mandates", jsonArray);
	return outer;
	}
}
