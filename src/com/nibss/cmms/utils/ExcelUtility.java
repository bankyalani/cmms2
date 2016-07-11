package com.nibss.cmms.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nibss.cmms.utils.exception.domain.ServerBusinessException;

public class ExcelUtility {

	private static Logger logger = LoggerFactory.getLogger(ExcelUtility.class);

	public static void main(String[] args) {
		
		File newFile=new File("C:\\Users\\sfagbola\\Desktop\\mandateUpload.xlsx");
		
		String originalFileName = newFile.getName();
		String contentType = "application/octet-stream";
		int size=(int)newFile.length();
		logger.debug("size--"+size);
		FileItem fileItem = new DiskFileItem("file", contentType, false, originalFileName, size,  newFile.getParentFile());
	    try {
	    	 InputStream input =  new FileInputStream(newFile);
	         OutputStream os = fileItem.getOutputStream();
	         int ret = input.read();
	         while ( ret != -1 )
	         {
	             os.write(ret);
	             ret = input.read();
	         }
	         os.flush();
	         input.close();
			logger.debug("fileItem.getSize--"+fileItem.getSize());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	}
	
	
	private int startRow;

	private int columnCount;

	private List<String> keyColumns = new ArrayList<String>();

	private Workbook workbook;

	private ExcelType excelType;

	public enum ExcelType{
		XLS,XLSX;
	}

	public ExcelUtility(ExcelType type){
		this.excelType=type;
		logger.debug("--Intiationg class for file of type "+type.toString());
	}



	public List<String[]> readFilePath(String filePath) throws IOException,ServerBusinessException{
		InputStream is=new FileInputStream(new File(filePath));		 
		return readInputStream(is);					
	}


	public List<String[]> readFile(File file) throws IOException,ServerBusinessException{
		InputStream is=new FileInputStream(file); 
		return readInputStream(is);		

	}

	public List<String[]> readInputStream(InputStream stream) throws IOException,ServerBusinessException {
		
		switch(excelType){
		case XLS:
			workbook = new HSSFWorkbook(stream);
			break;
		case XLSX:
			workbook = new XSSFWorkbook(stream);
			break;
		default:throw new ServerBusinessException(0,"Invalid file type specified");
		}
		stream.close();
		return processWorkBook(workbook);
		

	}

	private List<String[]> processWorkBook(Workbook workbook) {
		logger.debug("--Starting processWorkBook--");
		List<String[]> recordList=new ArrayList<>();
		workbook.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL);

		Sheet sheet=workbook.getSheetAt(0); //get the first sheet
		int rowc=0;
		columnCount=this.getColumnCount(); 
		String[] currentRecord=null;
		read:
			for(rowc =startRow;;rowc++){
				Row row = sheet.getRow(rowc);

				currentRecord= new String[columnCount+1];

				String currval=null;
				for (int j = 0; j < columnCount; j++) {

					Cell cell=null;
					try {
						cell = row.getCell(j);
					} catch (NullPointerException e) {
						cell=null;

					}
					//this is to handle the spaces in the format
					if (cell==null && currentRecord[(j==0?0:j-1)]==null){
						break read;
					}

					if (null!=cell){

						switch(cell.getCellType()){
						case Cell.CELL_TYPE_STRING:
							currval = cell.getStringCellValue().trim();						          
							break;
						case Cell.CELL_TYPE_NUMERIC:
							currval =  String.format("%.2f",cell.getNumericCellValue()); // new
							break;
						case Cell.CELL_TYPE_FORMULA: 
							System.out.println("Formula is " + cell.getCellFormula());
							switch(cell.getCachedFormulaResultType()) {
							case Cell.CELL_TYPE_NUMERIC:
								System.out.println("Last evaluated as: " + cell.getNumericCellValue());
								currval = String.format("%.2f",cell.getNumericCellValue());
								break;
							case Cell.CELL_TYPE_STRING:
								System.out.println("Last evaluated as \"" + cell.getRichStringCellValue() + "\"");
								currval =cell.getStringCellValue().trim();
								break;
							}
							break;

						}
						logger.debug("[" + rowc + "," + j + "]=" + currval);
					}else{
						currval="";
					}
					currentRecord[j]=currval==null?"":currval.trim();

				}

				recordList.add(currentRecord);
			}
		return recordList;
	}


	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public List<String> getKeyColumns() {
		return keyColumns;
	}

	public void setKeyColumns(List<String> keyColumns) {
		this.keyColumns = keyColumns;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
}