package org.egov.ulb.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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

public class SingleFileCreator {
	

	public static void main(String[] args) throws IOException {
		  
		  String ulbName="";
		  Path currentRelativePath = Paths.get("");
		  String s = currentRelativePath.toAbsolutePath().toString();
		  System.out.println("Current relative path is: " + s);	
		  String CurrfilePath = "/src/main/java/org/egov/ulb/file/";
		  String content = "select tenatid,count(*) from :schemaName.egf_accountcodepurpose;";
	  
	  FileReader reader=new FileReader(s+"/src/main/java/org/egov/ulb/"+"ulbnames.txt");
	  BufferedReader br=new BufferedReader(reader);
	  StringBuilder sb=new StringBuilder(4000);
	  while ((ulbName=br.readLine())!=null)
	  {
		 String cityWiseContent = content.replace(":schemaName", ulbName);
		 sb.append(cityWiseContent).append("\n");
	  }
	  
	   PrintWriter out = new PrintWriter(s+CurrfilePath+"/files/query.sql");
	     out.write(sb.toString());
		 out.flush();
		 out.close();
	  
	  System.out.println("Done"); 
	
	}

}
