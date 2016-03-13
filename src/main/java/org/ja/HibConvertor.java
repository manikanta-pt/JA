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
		
		File f=new File(Utility.PROJECTHOME+"/src/main/java/org/egov/");
		HibConvertor dao=new HibConvertor();
		dao.walk(Utility.PROJECTHOME+"/src/main/java/org/egov");
		
		
	}
	
	
	public void list(File file) {
	if( file.isFile() )
	{
		if(file.getName().contains("HibernateDAO"))
		System.out.println(file.getName()+""+file.getAbsolutePath());
	}else{
	
	    File[] children = file.listFiles();
	    for (File child : children) {
	        list(child);
	    }
	}
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
            	
            	File srcFile=new File(f.getAbsolutePath());
    			String content = new Scanner(srcFile).useDelimiter("\\Z").next();
    			if(content.contains("HibernateUtil.getCurrentSession"))
    			{
    				StringBuffer contentBuffer=new StringBuffer(content);
    				String str="\n\nimport org.egov.infstr.services.PersistenceService;"+Utility.NEWLINE;
    				String addpersistence=" @Autowired"+Utility.NEWLINE+
    									  " @Qualifier(\"persistenceService\")"+Utility.NEWLINE+
    									  " private PersistenceService persistenceService;"+Utility.NEWLINE;
    				if(!content.contains("PersistenceService persistenceService;"))
    				{
    					contentBuffer.insert(contentBuffer.indexOf("import")-1, str);
    					contentBuffer.insert(contentBuffer.indexOf("{")+1,addpersistence);
    					
    				}
    				String modifiedContent=contentBuffer.toString();
    				modifiedContent=modifiedContent.replace("HibernateUtil.getCurrentSession", "persistenceService.getCurrentSession");
    				modifiedContent.replace("import org.egov.infstr.utils.HibernateUtil;", "");
    				System.out.println(modifiedContent);
    				/*PrintWriter orginal=new PrintWriter(srcFile);
					orginal.write(modifiedContent);
					orginal.flush();
					orginal.close();*/
    				
    			}else
    			{
    			continue;	
    			}
            		
                //System.out.println( "File:" + f.getAbsoluteFile() );
            }
        }
    }

}
