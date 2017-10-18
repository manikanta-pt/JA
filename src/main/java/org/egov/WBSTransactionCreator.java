package org.egov;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WBSTransactionCreator {

 
		
		public static void main(String[] args) throws IOException {
	        XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("Java Books");
	        
	        List<String> masters=new ArrayList<>();
	       
	        
	        List<String> transaction=new ArrayList<String>();
	       
	        transaction.add("Expense Bill");
	        transaction.add("Voucher from Bill");
	        transaction.add("Bill Payment");
	        transaction.add("Direct Bank Payment");
	        transaction.add("Remittance Payment");
	        transaction.add("Salary Payment");
	        transaction.add("Pension Payment");
	        transaction.add("Advance Payment");
	        transaction.add("Auto Remittance");
	        transaction.add("Contra BTB");
	        transaction.add("Contra BTC");
	        transaction.add("Contra CTB");
	        transaction.add("Loans");
	        transaction.add("Grants");
	        transaction.add("Advance");
	        transaction.add("BRS");
	        transaction.add("Cheque Assignment");
	        
	        
	        
	         
	       List<String> tasks=new ArrayList<String>();
	       tasks.add("Analysis and Design :Create \n-YAML\n-Interaction Diagram\n-Update the story");
	       tasks.add("REST API:\n-Create");
	       tasks.add("Validation API:\n-Validate Create");
	       tasks.add("UI : \n-Create \n-Integrate with API.\n-Localization");
	        // tasks.add("Unit Testing \n-Controller\n-Service\n-Repository\n-Contract :Create");
	       tasks.add("Kafka: Producer\n-Push After validation: Create");
	       tasks.add("Kafka: Consumer\n-implement the workflow :Create");
	       tasks.add("Kafka: Consumer\n-Persist to DB\n-Produce to ES :Create");
	       tasks.add("Kafka: Consumer\n-Push to Elastic Search :Create");
	       tasks.add("Automation-Testing:Create");
	       tasks.add("API and Business Functionality Testing:Create");
	       List<Float> time=new ArrayList<>();
	       time.add(.5f);
	       time.add(.5f);
	       time.add(.25f);
	       time.add(0f);
	       time.add(.5f);
	       time.add(.5f);
	       time.add(.5f);
	       time.add(.5f);
	       time.add(.5f);
	       time.add(.5f);
	       
	       List<String> readTasks=new ArrayList<String>();  
	       
	       readTasks.add("REST API:\n-View") ;
	       readTasks.add("UI : \n-View \n-Integrate with API.\n-Localization");
	       readTasks.add("Unit Testing View:\n-Controller\n-Service\n-Repository\n-Contract");
	       readTasks.add("Automation-Testing :View");
	       readTasks.add("API and Business Functionality Testing:View");
	       
	        
	        
	        
	        		
	        
	        
	        
	     
	        int rowCount = 0;
	      for(String master:transaction) {  
	    	  int seq=0;
	        for (String t : tasks) {
	            Row row = sheet.createRow(++rowCount);
	             
	            int columnCount = -1;
	             
	         
	                Cell cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF000001");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(++seq);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(0);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("Create_"+master);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(t);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(time.get(tasks.indexOf(t)));
	                
	            }
	        
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
	                cell.setCellValue("Update_"+master);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(t.replace("Create", "Update"));
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(time.get(tasks.indexOf(t)));
	                
	            }
	        
	        
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
	                cell.setCellValue("Cancel_"+master);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(t.replace("Create", "Cancel"));
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(time.get(tasks.indexOf(t)));
	                
	            }
	        
	      /*  for (String t : readTasks) {
	            Row row = sheet.createRow(++rowCount);
	             
	            int columnCount = -1;
	             
	         
	                Cell cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF000001");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(++seq);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(1);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("Workflow_"+master);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(t);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(time.get(readTasks.indexOf(t)));
	                
	            }
	      */  
	        for (String t : readTasks) {
	            Row row = sheet.createRow(++rowCount);
	             
	            int columnCount = -1;
	             
	         
	                Cell cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF000001");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(++seq);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(1);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("Search_"+master);
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(t.replace("View", "Search"));
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("EGF");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue("");
	                cell = row.createCell(++columnCount);
	                cell.setCellValue(time.get(readTasks.indexOf(t)));
	                
	            }
	        
	        
	        
	      }
	         
	         
	        try (FileOutputStream outputStream = new FileOutputStream("JavaBooksTrans.xlsx")) {
	            workbook.write(outputStream);
	            
	            
	            System.out.println();
	            
	        }
	    }
 

}
