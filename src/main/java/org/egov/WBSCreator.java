package org.egov;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WBSCreator {

 
		
		public static void main(String[] args) throws IOException {
	        XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("Java Books");
	        
	        List<String> masters=new ArrayList<>();
	        masters.add("InstrumentType");
	        masters.add("SurrendarReason");
	        
	        
	        List<String> transaction=new ArrayList<String>();
	        transaction.add("Voucher");
	        transaction.add("Bill");
	         
	       List<String> tasks=new ArrayList<String>();
	       tasks.add("Analysis and Design\n-YAML\n-Interaction Diagram\n-Update the story");
	       tasks.add("REST API:\n-CRUD\n-Search");
	       tasks.add("UI : \n-Create, Search, View and Modify. Use existing HTML rendered from JSP. \n-Integrate with API.\n-Localization");
	       tasks.add("Unit Testing :\n-Controller\n-Service\n-Repository\n-Contract");
	       tasks.add("Kafka: Producer\n-Push After validation");
	       tasks.add("Kafka: Consumer\n-Persist to DB\n-Produce to ES");
	       tasks.add("Kafka: Consumer\n-Push to Elastic Search");
	      // tasks.add("Convert from JPA to JDBC");
	       tasks.add("Automation-Testing");
	       tasks.add("API and Business Functionality Testing");
	        
	       
	       
	        
	        
	        
	        
	        		
	        
	        
	        
	     
	        int rowCount = 0;
	      for(String master:masters) {  
	    	  int seq=0;
	        for (String t : tasks) {
	            Row row = sheet.createRow(++rowCount);
	             
	            int columnCount = -1;
	             
	         
	                Cell cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF000001");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(++seq);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(1);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(master+"");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(t);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF");
	                
	            }
	             
	        }
		
	         
	         
	        try (FileOutputStream outputStream = new FileOutputStream("JavaBooks.xlsx")) {
	            workbook.write(outputStream);
	            
	            
	            System.out.println();
	            
	        }
	    }
 

}
