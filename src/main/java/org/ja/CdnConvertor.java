package org.ja;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.rmi.CORBA.Util;

import org.egov.Utility;
import org.egov.infstr.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class CdnConvertor {

	List<String> daoList=new ArrayList<String>();

	public static void main(String[] args) throws IOException {

		//File f=new File(Utility.PROJECTHOME+"/src/main/java/org/egov/");
		CdnConvertor dao=new CdnConvertor();
		dao.walk(Utility.PROJECT_WEBHOME+"/src/main/webapp/");


	}

 

	public void walk( String path ) throws FileNotFoundException {
		//System.out.println( "path:"+path);
		File root = new File( path );
		root.isDirectory();
		root.isFile();
		File[] list = root.listFiles();

		if (list == null) return;

		for ( File f : list ) {
			if ( f.isDirectory() ) {
				walk( f.getAbsolutePath() );
				// System.out.println( "Dir:" + f.getAbsoluteFile() );
			}
			else {
				//System.out.println(f.getName());
				if(!f.getName().endsWith(".jsp"))
					continue;
				if(!f.getName().contains("payment-advancePaymentView.jsp"))
					continue;

				File srcFile=new File(f.getAbsolutePath());
				String cdnStartUrl="<cdn:url cdn='${applicationScope.cdn}'  value='";
			    String cdnEndUrl="'/>";
			    boolean modified=false;
				
				String content = new Scanner(srcFile).useDelimiter("\\Z").next();
				if((content.contains("<link") || content.contains("script type")) && !content.contains("<cdn:url") )
				{
					modified=false;
					//System.out.println(".....Trying convertion.......for"+f.getAbsolutePath());	
					StringBuffer contentBuffer=new StringBuffer(content);
					
					Scanner scan = new Scanner(content); // I have named your StringBuilder object sb
					boolean found=false;
					while (scan.hasNextLine() ){
						
					 String oneLine = scan.nextLine();
					if( !(oneLine.contains("<link") || oneLine.contains("script type"))&&!found)
							continue;
					else
						found=true;
					
					String[] split = oneLine.split(" ");
					for(String s:split)
					{
						String str;
						str=new String(s);
						if(s.contains("href="))
						{
							
							System.out.println(s.indexOf("\""));
							int firstQuotes=s.indexOf("\"");
							String value = s.substring(firstQuotes+1,s.indexOf("\"",firstQuotes+1));
							System.out.println(value);
							System.out.println(cdnStartUrl+value+cdnEndUrl);
							content = content.replace(value, cdnStartUrl+value+cdnEndUrl);
							 modified=true;
							
						}
						
						if(s.contains("src="))
						{
							int firstQuotes=s.indexOf("\"");
							String value = s.substring(firstQuotes+1,s.indexOf("\"",firstQuotes+1));
							content=content.replace(value, cdnStartUrl+value+cdnEndUrl);
							 modified=true;
							
						}
						
					}
					 
					}
					
					String modifiedContent="";
					modifiedContent=content+"\n";
					System.out.println(modifiedContent);
				if(modified)
				{
									PrintWriter orginal=new PrintWriter(srcFile);
									orginal.write(modifiedContent);
									orginal.flush();
									orginal.close(); 
									//System.out.println(modifiedContent);
				}

				}else
				{
					continue;	
				}

				//System.out.println( "File:" + f.getAbsoluteFile() );
			}
		}
	}

}
