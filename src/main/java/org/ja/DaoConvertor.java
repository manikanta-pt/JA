package org.ja;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.egov.Utility;


public class DaoConvertor {
	
	List<String> daoList=new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		
		File f=new File(Utility.PROJECTHOME+"/src/main/java/org/egov/");
		DaoConvertor dao=new DaoConvertor();
		dao.walk(Utility.PROJECTHOME+"/src/main/java/");
		for(String s:dao.daoList)
		{
			File srcFile=new File(s);
			String content = new Scanner(srcFile).useDelimiter("\\Z").next();
			if(content.contains("private EntityManager entityManager;"))
			{
				continue;
			}
			StringBuffer contentBuffer=new StringBuffer(content);
			String daoNameWithJava = s.substring(s.lastIndexOf("/")+1,s.length());
			String daoName = daoNameWithJava.replace(".java", "");
			String  entityName = daoName.replace("HibernateDAO", "");
			String str="\n\nimport javax.persistence.EntityManager;\nimport javax.persistence.PersistenceContext;\n "
					+ "import java.io.Serializable;\n";
			contentBuffer.insert(contentBuffer.indexOf("import")-1, str);
			
			if(daoName.equalsIgnoreCase("BudgetDetailsHibernateDAO"))
				entityName="BudgetDetail";
			if(daoName.equalsIgnoreCase("EgBilldetailsHibernateDAO"))
				entityName="EgBilldetails";
			if(daoName.equalsIgnoreCase("EgBillRegisterHibernateDAO"))
				entityName="EgBillregister";
			if(daoName.equalsIgnoreCase("TDSHibernateDAO"))
				entityName="Recovery";
			if(daoName.equalsIgnoreCase("VoucherHibernateDAO"))
				continue;
			//9901367548
			
		String persist="\n@PersistenceContext\n "+
						"private EntityManager entityManager;\n\n"+
						"@Override"+Utility.NEWLINE+
						" public Session  getCurrentSession() {"+Utility.NEWLINE+
		                "return entityManager.unwrap(Session.class);"+Utility.NEWLINE+
		                " }"+Utility.NEWLINE;
		
		String constructor="\npublic FundHibernateDAO() {\n"+
							"super(Fund.class, null);\n"+
							"}\n";
		
		String override="public Fund findById(Serializable id, boolean lock) {"+Utility.NEWLINE+
				"		return (Fund)getCurrentSession().load(Fund.class, id);"+Utility.NEWLINE+
				"	}"+Utility.NEWLINE+
				""+Utility.NEWLINE+
				"	public List<Fund> findAll() {"+Utility.NEWLINE+
				"		return (List<Fund>) getCurrentSession().createCriteria(Fund.class).list();"+Utility.NEWLINE+
				"	}"+Utility.NEWLINE;
		                
		String constructorModified = constructor.replace("Fund", entityName);
		String overrideModified=override.replace("Fund", entityName);
						contentBuffer.insert(contentBuffer.indexOf("{")+1,persist+constructorModified);
						contentBuffer.insert(contentBuffer.indexOf("{")+1,persist+overrideModified);
						String string = contentBuffer.toString();
						  string = string.replaceAll("HibernateUtil.getCurrentSession", "getCurrentSession");
						  string =string.replace("import org.egov.infstr.utils.HibernateUtil;", "");
						 // string =string.replace("import org.egov.infstr.dao.GenericHibernateDAO;", "");
						 // string = string.replaceAll("extends GenericHibernateDAO", "");
					System.out.println(string);
					
					PrintWriter orginal=new PrintWriter(s);
					orginal.write(string.toString());
					orginal.flush();
					orginal.close();
	
						
		}
		
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
	
	
	public void walk( String path ) {
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
            	if(f.getName().contains("HibernateDAO.java"))
            	{
            		System.out.println(f.getName()+""+f.getAbsolutePath());
            		daoList.add(f.getAbsolutePath());
            	}
            		
                //System.out.println( "File:" + f.getAbsoluteFile() );
            }
        }
    }

}
