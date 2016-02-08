package org.egov.ulb.db;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * 
 * @author mani
 *  Add your formatted query without create statement
 *  put your schema name to be replaced as :schemaName
 *  put the file in the same location of this file with name ulbnames.txt
 *  run this file 
 *  
 *  This will read ulbnames from the file and replace the :schemaName with ulbnames and
 */

public class ViewCreator {
	
	
	

	public static void main(String[] args) throws IOException {
		
		String query="select date(header.receiptdate) as  receipt_date,service.code AS service,header.source as source, "
				+ " (select code from :schemaName.eg_city )  as ulb,  sum(header.Totalamount) as total,count(*) as recordCount "
				+ "  from	:schemaName.egcl_collectionheader header,  :schemaName.egcl_servicedetails service where "
				+ " header.servicedetails = service.id AND status in (select id from	:schemaName.egw_status where  moduletype='ReceiptHeader' "
				+ " and code in ('TO_BE_SUBMITTED','SUBMITTED','APPROVED','REMITTED'))   group by receipt_date,service,source ";
		StringBuilder finalQuery=new StringBuilder(1000);
		
		  String ulbName="";
		  Path currentRelativePath = Paths.get("");
		  String s = currentRelativePath.toAbsolutePath().toString();
		  System.out.println("Current relative path is: " + s);	
	  FileReader reader=new FileReader(s+"/src/main/java/org/egov/ulb/db/"+"ulbnames.txt");
	  BufferedReader br=new BufferedReader(reader);
	  while ((ulbName=br.readLine())!=null)
	  {
		 String querycopy = query.replace(":schemaName", ulbName);
		 finalQuery.append(querycopy +"\n union \n ");
		 //System.out.println(finalQuery);
	  }
	  
	  //finalQuery.lastIndexOf("union");
	  
	  System.out.println(finalQuery);
	  
	}

}
