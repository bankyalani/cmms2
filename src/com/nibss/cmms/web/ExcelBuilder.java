package com.nibss.cmms.web;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nibss.cmms.domain.Mandate;
import com.nibss.cmms.domain.Transaction;
import com.nibss.cmms.domain.TransactionParam;
import com.nibss.cmms.domain.TransactionResponse;
import com.nibss.cmms.service.TransactionResponseService;


public class ExcelBuilder extends AbstractExcelView implements WebAppConstants {

	

	@Override
	protected boolean generatesDownloadContent(){
		return false;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			HSSFWorkbook hssfWorkbook, HttpServletRequest request, HttpServletResponse response)
					throws Exception {
		logger.info("Report Type::"+model.get(REPORT_TYPE));
		if(model.get(REPORT_TYPE)!=null){
			
			if(model.get(REPORT_OBJECT_TYPE) instanceof Mandate){
				if (model.get(REPORT_TYPE).equals(REPORT_TYPE_MANDATE))
					writeMandateXlsReport(model, hssfWorkbook, request, response);
				else if(model.get(REPORT_TYPE).equals(REPORT_TYPE_DETAILED))
					writeDetailedMandateXlsReport(model,hssfWorkbook,request,response);
			}else if (model.get(REPORT_OBJECT_TYPE) instanceof Transaction){
				if (model.get(REPORT_TYPE).equals(REPORT_TYPE_DETAILED))
					writeDetailedTransactionXlsReport(model,hssfWorkbook,request,response);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void writeDetailedMandateXlsReport(Map<String, Object> model,
			HSSFWorkbook hssfWorkbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		List<Mandate> mandates= (List<Mandate>)model.get(REPORT_DATA);
		// create a new Excel sheet
		HSSFSheet sheet = hssfWorkbook.createSheet("Mandates");
		sheet.setDefaultColumnWidth(30);


		CellStyle style=applyDefaultStyle(hssfWorkbook);

		
		IntStream.range(0,mandates.size())
		.forEach(index->{
			HSSFRow aRow = sheet.createRow(index+1);
			int i=0;
			aRow.createCell(i++).setCellValue(mandates.get(index).getMandateCode());
			aRow.createCell(i++).setCellValue(mandates.get(index).getSubscriberCode());
			aRow.createCell(i++).setCellValue(mandates.get(index).getPayerName());
			aRow.createCell(i++).setCellValue(mandates.get(index).getProduct().getBiller().getCompany().getName());
			aRow.createCell(i++).setCellValue(mandates.get(index).getProduct().getBiller().getBank().getBankName());
			aRow.createCell(i++).setCellValue(mandates.get(index).getProduct().getName());
			aRow.createCell(i++).setCellValue(mandates.get(index).getAmount().toPlainString());
			aRow.createCell(i++).setCellValue(mandates.get(index).getEmail());
			aRow.createCell(i++).setCellValue(mandates.get(index).getPhoneNumber());
			aRow.createCell(i++).setCellValue(mandates.get(index).getBank().getBankName());		
			aRow.createCell(i++).setCellValue(mandates.get(index).getAccountNumber());		
			aRow.createCell(i++).setCellValue(mandates.get(index).getAccountName());		
			aRow.createCell(i++).setCellValue(mandates.get(index).getStartDate().toString());		
			aRow.createCell(i++).setCellValue(mandates.get(index).getEndDate().toString());		
			aRow.createCell(i++).setCellValue(mandates.get(index).getChannel());		
			aRow.createCell(i++).setCellValue(mandates.get(index).getStatus().getName());		
			aRow.createCell(i++).setCellValue(mandates.get(index).getFrequency());
			aRow.createCell(i++).setCellValue(mandates.get(index).getDateCreated().toString());
			aRow.createCell(i++).setCellValue(mandates.get(index).getCreatedBy().getEmail());	
			aRow.createCell(i++).setCellValue(mandates.get(index).getDateApproved()==null?"":mandates.get(index).getDateApproved().toString());
			aRow.createCell(i++).setCellValue(mandates.get(index).getApprovedBy()==null?"":mandates.get(index).getApprovedBy().getEmail());
			aRow.createCell(i++).setCellValue(mandates.get(index).getDateAccepted()==null?"":mandates.get(index).getDateAccepted().toString());
			aRow.createCell(i++).setCellValue(mandates.get(index).getAcceptedBy()==null?"":mandates.get(index).getAcceptedBy().getEmail());
			aRow.createCell(i++).setCellValue(mandates.get(index).getDateAuthorized()==null?"":mandates.get(index).getDateAuthorized().toString());
			aRow.createCell(i++).setCellValue(mandates.get(index).getAuthorizedBy()==null?"":mandates.get(index).getAuthorizedBy().getEmail());

		});
		
		// create header row
				HSSFRow header = sheet.createRow(0);

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

				IntStream.range(0, mandateReportHeaders.length)
				.forEachOrdered(index->{
					header.createCell(index).setCellValue(mandateReportHeaders[index]);
					header.getCell(index).setCellStyle(style);
					sheet.autoSizeColumn(index);
				});


		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		hssfWorkbook.write(outByteStream);
		byte [] outArray = outByteStream.toByteArray();
		response.setContentLength(outArray.length);
		String reportName=(String) model.get("reportName");
		response.setContentType("application/octet-stream");
		response.setHeader( "Content-Disposition", "attachment;filename="+(!reportName.endsWith(".xls")?reportName+".xls":reportName));
		OutputStream outStream = null;

		try {
			outStream = response.getOutputStream();
			hssfWorkbook.write(outStream);
			outStream.flush();
		} finally {
			outStream.close();
			//response.flushBuffer();
		}  

	}



	@SuppressWarnings("unchecked")
	private void writeDetailedTransactionXlsReport(Map<String, Object> model,
			HSSFWorkbook hssfWorkbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		List<TransactionResponse> transactionResponses =(List<TransactionResponse>)model.get(TRANSACTION_STATUS);
		List<Transaction> transactions= (List<Transaction>)model.get(REPORT_DATA);
		// create a new Excel sheet
		HSSFSheet sheet = hssfWorkbook.createSheet("Mandates");
		sheet.setDefaultColumnWidth(30);

Log.info("Transaction Size  "+ transactions.size());
		CellStyle style=applyDefaultStyle(hssfWorkbook);

		// create header row
		

		IntStream.range(0,transactions.size())
		.forEach(index->{
			HSSFRow aRow = sheet.createRow(index+1);
			int i=0;
			aRow.createCell(i++).setCellValue(transactions.get(index).getMandate().getMandateCode());
			aRow.createCell(i++).setCellValue(transactions.get(index).getMandate().getSubscriberCode());
			aRow.createCell(i++).setCellValue(transactions.get(index).getMandate().getAccountNumber());
			aRow.createCell(i++).setCellValue(transactions.get(index).getMandate().getAccountName());
			aRow.createCell(i++).setCellValue(transactions.get(index).getMandate().getPayerName());
			aRow.createCell(i++).setCellValue(transactions.get(index).getMandate().getBank().getBankName());
			aRow.createCell(i++).setCellValue(transactions.get(index).getMandate().getProduct().getName());
			aRow.createCell(i++).setCellValue(transactions.get(index).getAmount().toPlainString());
			
			aRow.createCell(i++).setCellValue(transactions.get(index).getNumberOfTrials());
			
			//get the last transaction_param of this transaction
			TransactionParam trxn=transactions.get(index).getTransactionParam().stream().findFirst().orElse(null);

			aRow.createCell(i++).setCellValue(trxn == null ? "" : trxn.getSessionId());
			
			aRow.createCell(i++).setCellValue(trxn != null ?trxn.getDebitResponseCode():"");
			aRow.createCell(i++).setCellValue(trxn != null ?trxn.getCreditResponseCode():"");
			aRow.createCell(i++).setCellValue(trxn != null ?trxn.getDateCreated().toString():"");
		
			if(trxn != null && trxn.getDebitResponseCode()!=null){
				TransactionResponse trxnResponse = transactionResponses.stream().filter(s -> s.getResponseCode().equals(trxn.getDebitResponseCode())).findFirst().orElse(null);
				aRow.createCell(i++).setCellValue(trxnResponse != null ?trxnResponse.getDescription():"");
			}
			if(trxn != null && trxn.getCreditResponseCode()!=null){
				TransactionResponse trxnResponse = transactionResponses.stream().filter(s -> s.getResponseCode().equals(trxn.getCreditResponseCode())).findFirst().orElse(null);
				aRow.createCell(i++).setCellValue(trxnResponse != null ?trxnResponse.getDescription():"");
			}
			
		});
		
		HSSFRow header = sheet.createRow(0);

		String[] transactionReportHeaders= new String[]{
				"Mandate Code",
				"Subscriber Mandate Code",
				"Account Number",
				"Account Name",
				"Payer Name",
				"Customer's Bank",
				"Product",
				"Amount",
				"No Of Trials",
				"Session Id",
				"Debit Status",
				"Credit Status",
				"Transaction Date",
				"Debit Status Description",
				"Credit Status Description",

		};

		IntStream.range(0, transactionReportHeaders.length)
		.forEachOrdered(index->{
			header.createCell(index).setCellValue(transactionReportHeaders[index]);
			header.getCell(index).setCellStyle(style);
			sheet.autoSizeColumn(index);
		});

		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		hssfWorkbook.write(outByteStream);
		byte [] outArray = outByteStream.toByteArray();
		response.setContentLength(outArray.length);
		String reportName=(String) model.get("reportName");
		response.setContentType("application/octet-stream");
		response.setHeader( "Content-Disposition", "attachment;filename="+(!reportName.endsWith(".xls")?reportName+".xls":reportName));
		OutputStream outStream = null;

		try {
			outStream = response.getOutputStream();
			hssfWorkbook.write(outStream);
			outStream.flush();
		} finally {
			outStream.close();
			//response.flushBuffer();
		}  

	}

	@SuppressWarnings("unchecked")
	private void writeMandateXlsReport(Map<String, Object> model,
			HSSFWorkbook hssfWorkbook, HttpServletRequest request, HttpServletResponse response) throws Exception{

		List<Mandate> mandates= (List<Mandate>)model.get("mandates");
		// create a new Excel sheet
		HSSFSheet sheet = hssfWorkbook.createSheet("Mandates");
		sheet.setDefaultColumnWidth(30);


		CellStyle style=applyDefaultStyle(hssfWorkbook);

		// create header row
		HSSFRow header = sheet.createRow(0);

		String[] mandateReportHeaders= new String[]{
				"Mandate Code",
				"Subscriber Mandate Code",
				"Biller",
				"Biller's Bank",
				"Product",
				"Amount",
				"Customer's Bank",
				"Status"
		};

		IntStream.range(0, mandateReportHeaders.length)
		.forEachOrdered(index->{
			header.createCell(index).setCellValue(mandateReportHeaders[index]);
			header.getCell(index).setCellStyle(style);
		});

		IntStream.range(0,mandates.size())
		.forEach(index->{
			HSSFRow aRow = sheet.createRow(index+1);
			int i=0;
			aRow.createCell(i++).setCellValue(mandates.get(index).getMandateCode());
			aRow.createCell(i++).setCellValue(mandates.get(index).getSubscriberCode());
			aRow.createCell(i++).setCellValue(mandates.get(index).getProduct().getBiller().getCompany().getName());
			aRow.createCell(i++).setCellValue(mandates.get(index).getProduct().getBiller().getBank().getBankName());
			aRow.createCell(i++).setCellValue(mandates.get(index).getProduct().getName());
			aRow.createCell(i++).setCellValue(mandates.get(index).getAmount().toPlainString());
			aRow.createCell(i++).setCellValue(mandates.get(index).getBank().getBankName());		
			aRow.createCell(i++).setCellValue(mandates.get(index).getStatus().getName());			
		});
		String reportName=(String) model.getOrDefault("reportName", "cmms_mandates.xls");
		logger.info("Report Name::"+reportName);
		response.setHeader( "Content-Disposition", "attachment;filename="+(!reportName.endsWith(".xls")?reportName+".xls":reportName));
	}

	private CellStyle applyDefaultStyle(HSSFWorkbook hssfWorkbook) {
		// create style for header cells
		CellStyle style = hssfWorkbook.createCellStyle();
		Font font = hssfWorkbook.createFont();
		font.setFontName("Century Gothic");
		style.setFillForegroundColor(HSSFColor.DARK_GREEN.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);

		return style;

	}

}
