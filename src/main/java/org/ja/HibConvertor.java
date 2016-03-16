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


public class HibConvertor {

	List<String> daoList=new ArrayList<String>();

	public static void main(String[] args) throws IOException {

		//File f=new File(Utility.PROJECTHOME+"/src/main/java/org/egov/");
		HibConvertor dao=new HibConvertor();
		dao.walk(Utility.PROJECTHOME+"/src/main/java/");


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
				System.out.println(f.getAbsolutePath());
				if(!f.getName().endsWith(".java"))
					continue;
				/*if(!f.getName().contains("BudgetDetailAction"))
					continue;*/

				File srcFile=new File(f.getAbsolutePath());
				String content = new Scanner(srcFile).useDelimiter("\\Z").next();
				if(content.contains("HibernateUtil"))
				{
					StringBuffer contentBuffer=new StringBuffer(content);
					String str="\n\nimport org.egov.infstr.services.PersistenceService;"+Utility.NEWLINE;
					String addpersistence="\n @Autowired"+Utility.NEWLINE+
							" @Qualifier(\"persistenceService\")"+Utility.NEWLINE+
							" private PersistenceService persistenceService;"+Utility.NEWLINE;
					
					if(!content.contains("import org.springframework.beans.factory.annotation.Qualifier;"))
					{
						contentBuffer.insert(contentBuffer.indexOf("import")-1, "import org.springframework.beans.factory.annotation.Qualifier;");
					}
					if(!content.contains("import org.springframework.beans.factory.annotation.Autowired;"))
					{
						contentBuffer.insert(contentBuffer.indexOf("import")-1, "import org.springframework.beans.factory.annotation.Autowired;");
					}
					String modifiedContent="";
					if(content.contains("extends PersistenceService"))
					{
						modifiedContent=contentBuffer.toString();
						modifiedContent=modifiedContent.replaceAll("HibernateUtil\\s*.getCurrentSession", "getSession");
					}
					else
					{
						if(!content.contains("PersistenceService persistenceService;"))
						{
						contentBuffer.insert(contentBuffer.indexOf("import"), str);
						
						if(content.contains("@Autowired"))				 
						contentBuffer.insert(contentBuffer.indexOf("@Autowired")-1,addpersistence);
						else	
						{
							
							System.out.println(contentBuffer.indexOf(f.getName().replace(".java", "")));
							System.out.println(contentBuffer.indexOf("{",content.indexOf(f.getName().replace(".java", "")))+1);
						contentBuffer.insert(contentBuffer.indexOf("{",contentBuffer.indexOf("public class "+f.getName().replace(".java", "")))+1,addpersistence);
						}
						} 
						modifiedContent=contentBuffer.toString();
						modifiedContent=modifiedContent.replaceAll("HibernateUtil.getCurrentSession", "persistenceService.getSession");
						modifiedContent=modifiedContent.replaceAll("HibernateUtil\\s*.getCurrentSession", "persistenceService.getSession");
					}
					modifiedContent.replace("import org.egov.infstr.utils.HibernateUtil;", "");
					System.out.println(modifiedContent);

					PrintWriter orginal=new PrintWriter(srcFile);
					orginal.write(modifiedContent);
					orginal.flush();
					orginal.close(); 

				}else
				{
					continue;	
				}

				//System.out.println( "File:" + f.getAbsoluteFile() );
			}
		}
	}

}
