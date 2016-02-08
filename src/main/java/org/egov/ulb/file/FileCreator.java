package org.egov.ulb.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
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

public class FileCreator {
	

	public static void main(String[] args) throws IOException {
		
		  String ulbName="";
		  Path currentRelativePath = Paths.get("");
		  String s = currentRelativePath.toAbsolutePath().toString();
		  System.out.println("Current relative path is: " + s);	
		  String CurrfilePath = "/src/main/java/org/egov/ulb/file/";
		  File srcFile=new File(s+CurrfilePath+"APPTIS.sh");
		  String content = new Scanner(srcFile).useDelimiter("\\Z").next();
		  System.out.println(content);
	  
	  FileReader reader=new FileReader(s+"/src/main/java/org/egov/ulb/"+"ulbnames.txt");
	  BufferedReader br=new BufferedReader(reader);
	  while ((ulbName=br.readLine())!=null)
	  {
		 String cityWiseContent = content.replace(":schemaName", ulbName);
		 PrintWriter out = new PrintWriter(s+CurrfilePath+"/files/"+ulbName+"_apptis_es.sh");
		 out.write(cityWiseContent);
		 out.flush();
		 out.close();
	  }
	  
	  System.out.println("Done"); 
	
	}

}
