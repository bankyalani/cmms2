package com.nibss.cmms.web;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.ICsvListWriter;

import com.nibss.cmms.domain.Mandate;

public class CSVBuilder extends AbstractCSVView implements WebAppConstants{

	private static Logger log= LoggerFactory.getLogger(CSVBuilder.class);

	
	@Override
	protected void buildCsvDocument(ICsvListWriter csvWriter,
			Map<String, Object> model) throws Exception {

		if(model.get(REPORT_TYPE)!=null){
			if(model.get(REPORT_TYPE).equals(REPORT_TYPE_DETAILED))
				writeDetailedMandateCSVReport(csvWriter,model);
		}



	}

	private void writeDetailedMandateCSVReport(ICsvListWriter csvWriter,
			Map<String, Object> model) throws Exception{

		@SuppressWarnings("unchecked")
		List<Mandate> mandates= (List<Mandate>)model.get(REPORT_DATA);
		
		setFileName((String) model.get("reportName"));

		String[] mandateReportHeaders= new String[]{
				"Mandate Code",
				"Subscriber Mandate Code",
				"Payer Name",
				"Biller",
				"Biller's Bank",
				"Product",
				"Amount",
				"Customer's Email",
				"Customer's PhoneNumber",
				"Customer's Bank",
				"Account Number",
				"Account Name",	
				"Mandate Effective Date",
				"Mandate Expiry Date",
				"Channel",
				"Status",
				"Debit Frequency",
				"Date Created",
				"Created By",
				"Date Verified",
				"Verified By",
				"Date Approved",
				"Approved By",
				"Date Authorized",
				"Authorized By",

		};

		csvWriter.writeHeader(mandateReportHeaders);
		
		mandates.stream().forEach(mandate->{
			try {
				final String[] obj=new String[]{
						mandate.getMandateCode(),
						mandate.getSubscriberCode(),
						mandate.getPayerName(),
						mandate.getProduct().getBiller().getCompany().getName(),
						mandate.getProduct().getBiller().getBank().getBankName(),
						mandate.getProduct().getName(),
						mandate.getAmount().toPlainString(),
						mandate.getEmail(),
						mandate.getPhoneNumber(),
						mandate.getBank().getBankName(),		
						mandate.getAccountNumber(),		
						mandate.getAccountName(),		
						mandate.getStartDate().toString(),		
						mandate.getEndDate().toString(),		
						""+mandate.getChannel(),		
						mandate.getStatus().getName(),		
						""+mandate.getFrequency(),
						mandate.getDateCreated().toString(),
						mandate.getCreatedBy().getEmail(),	
						mandate.getDateApproved()==null?"":mandate.getDateApproved().toString(),
						mandate.getApprovedBy()==null?"":mandate.getApprovedBy().getEmail(),
						mandate.getDateAccepted()==null?"":mandate.getDateAccepted().toString(),
						mandate.getAcceptedBy()==null?"":mandate.getAcceptedBy().getEmail(),
						mandate.getDateAuthorized()==null?"":mandate.getDateAuthorized().toString(),
						mandate.getAuthorizedBy()==null?"":mandate.getAuthorizedBy().getEmail(),
	
				};
				csvWriter.write(obj);
			} catch (Exception e) {
				log.error(null,e);
			}
		});
}

}