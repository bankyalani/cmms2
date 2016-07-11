package com.nibss.cmms.web;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.nibss.cmms.domain.Mandate;
	


public class PdfBuilder extends AbstractPdfView implements WebAppConstants {
	
	private static Logger log= LoggerFactory.getLogger(PdfBuilder.class);

	@Override
	protected Document newDocument() {
        return new Document(PageSize.A4.rotate(),10,10,10,5);
    }
	
	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document,
		PdfWriter writer, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
 
		
		if(model.get(REPORT_TYPE)!=null){
			 if(model.get(REPORT_TYPE).equals(REPORT_TYPE_DETAILED))
				 writeDetailedMandatePdfReport(model,document,writer,request,response);
		}
 
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void writeDetailedMandatePdfReport(Map<String, Object> model, Document document,
			PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		
		
		List<Mandate> mandates= (List<Mandate>)model.get(REPORT_DATA);
		
		String[] mandateReportHeaders= new String[]{
				"Mandate Code",
				"Subscriber Mandate Code",
				"Payer Name",
				"Biller",
				/*"Biller's Bank",*/
				"Product",
				"Amount",
				"Customer's Email",
				"Customer's PhoneNumber",
				"Customer's Bank",
				"Account Number",
				"Account Name",	
				"Mandate Effective Date",
				"Mandate Expiry Date",
				/*"Channel",*/
				"Status",
				"Debit Frequency",
				"Date Created"/*,
				"Created By",
				"Date Verified",
				"Verified By",
				"Date Approved",
				"Approved By",
				"Date Authorized",
				"Authorized By",*/

		};

		PdfPTable table = new PdfPTable(mandateReportHeaders.length);
		table.setWidthPercentage(100.0f);
        table.setWidths(new float[] {4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,/*4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,4.0f,*/4.0f});
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);
      

        Font hFont = new Font(Font.HELVETICA,8F,Font.BOLD,Color.WHITE);

       
		IntStream.range(0, mandateReportHeaders.length)
		.forEachOrdered(index->{
			try {
				PdfPCell cell=new PdfPCell(new Phrase(mandateReportHeaders[index], hFont));
				cell.setBackgroundColor(Color.blue);
				table.addCell(cell);
			} catch (Exception e) {
				log.error(null,e);
			}
		});
 
		Font bFont = new Font(Font.HELVETICA,8F,Font.NORMAL,Color.BLACK);
		mandates.stream().forEach(mandate->{
			try {
				table.addCell(new PdfPCell(new Phrase(mandate.getMandateCode(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getSubscriberCode(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getPayerName(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getProduct().getBiller().getCompany().getName(), bFont)));
				//table.addCell(new PdfPCell(new Phrase(mandate.getProduct().getBiller().getBank().getBankName(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getProduct().getName(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getAmount().toPlainString(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getEmail(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getPhoneNumber(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getBank().getBankName(), bFont)));	
				table.addCell(new PdfPCell(new Phrase(mandate.getAccountNumber(), bFont)));	
				table.addCell(new PdfPCell(new Phrase(mandate.getAccountName(), bFont)));		
				table.addCell(new PdfPCell(new Phrase(mandate.getStartDate().toString(), bFont)));		
				table.addCell(new PdfPCell(new Phrase(mandate.getEndDate().toString(), bFont)));		
				//table.addCell(new PdfPCell(new Phrase(""+mandate.getChannel(), bFont)));		
				table.addCell(new PdfPCell(new Phrase(mandate.getStatus().getName(), bFont)));		
				table.addCell(new PdfPCell(new Phrase(""+mandate.getFrequency(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getDateCreated().toString(), bFont)));
				/*table.addCell(new PdfPCell(new Phrase(mandate.getCreatedBy().getEmail(), bFont)));	
				table.addCell(new PdfPCell(new Phrase(mandate.getDateApproved()==null?"":mandate.getDateApproved().toString(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getApprovedBy()==null?"":mandate.getApprovedBy().getEmail(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getDateAccepted()==null?"":mandate.getDateAccepted().toString(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getAcceptedBy()==null?"":mandate.getAcceptedBy().getEmail(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getDateAuthorized()==null?"":mandate.getDateAuthorized().toString(), bFont)));
				table.addCell(new PdfPCell(new Phrase(mandate.getAuthorizedBy()==null?"":mandate.getAuthorizedBy().getEmail(), bFont)));
*/
				
			} catch (Exception e) {
				log.error(null,e);
			}
		});
		
		
		document.add(table);
		
		String reportName=(String) model.get("reportName");
		response.setContentType("application/octet-stream");
		response.setHeader( "Content-Disposition", "attachment;filename="+(!reportName.endsWith(".pdf")?reportName+".pdf":reportName));
		
	}

}
