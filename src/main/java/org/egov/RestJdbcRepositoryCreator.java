package org.egov;

import static org.egov.Utility.NEWLINE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.rmi.CORBA.Util;

import org.atteo.evo.inflector.English;
import org.ja.annotation.DrillDown;
import org.ja.annotation.DrillDownTable;




public class RestJdbcRepositoryCreator {

	PojoHolder pojoHolder=new PojoHolder();
	private Field[] declaredFields;
	private Set<String> serviceSet=new HashSet<String>();
	private List<String> childList=new ArrayList<String>();
	public static void main(String[] args) {
		
		RestJdbcRepositoryCreator rc=new RestJdbcRepositoryCreator();
		rc.createService("org.egov.egf.master.domain.model.Bank");
	}
	//this is to standardize only 
	public void	create(String fullyQualifiedName)
	{	
		createService(fullyQualifiedName);
		
		for(int i = 0; i < childList.size(); i++)
		{
			
			createService(childList.get(i));
			
		 
		}
	}
	

	public void	createService(String fullyQualifiedName)
	{	
	try {
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
	
		declaredFields=	pojo.getDeclaredFields();
		 for(Field f:declaredFields)
			{
				String name = f.getName();
				if(name.equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}

		if(f.isAnnotationPresent(DrillDown.class)  )
		{
			
			
			childList.add(f.getType().getName());
			 
		}
		if(  f.isAnnotationPresent(DrillDownTable.class))
		{
			childList.add(Utility.getEnclosingType(f).getName());
		}
		
		}
		String refFileName="org.egov.egf.master.persistence.repository.FundJdbcRepository";
		PrintWriter sqlWriter;
	 
			String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/persistence/repository/";
			//String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+".persistence.queue.contract";
			String contractFileName = contractPackageDir+pojo.getSimpleName()+"JdbcRepository.java";
			File ff=Utility.createNewFile(contractFileName);
			
			sqlWriter = new PrintWriter(contractFileName, "UTF-8");
	       // String fileName=Utility.SRCFOLDER+"/"+refFileName.replace(".", "/")+".java";
			String fileName="/home/mani/Workspaces/ms/egov-services/financials/egf-master/src/main/java/org/egov/egf/master/persistence/repository/FundJdbcRepository.java";
	        String entityPakage= fullyQualifiedName.substring(0,fullyQualifiedName.lastIndexOf("."));
	        File file=new File(fileName);
			//Scanner sc=new Scanner(fileName);
		System.out.println(fileName);	
		 String content = new Scanner(file).useDelimiter("\\Z").next(); 
		 String name = pojo.getSimpleName();
		 String p = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".")+1,fullyQualifiedName.length());
		  
		
		 String contractContent = content;//.replaceAll(maping1, replace);
		 System.out.println("    now "+contractContent);
		  contractContent = contractContent.replace("Fund",name);
		 System.out.println(contractContent);
		 contractContent = contractContent.replace("fund", Utility.toCamelCase(name));
		 contractContent = contractContent.replace("master", Utility.SUBMODULE_IDENTIFIER);
		 System.out.println(contractContent);
	//	 contractContent = contractContent.replace("Banks",English.plural(name));
		 String ss=name+"s";
		 System.out.println(ss);
		 contractContent = contractContent.replace(ss,English.plural(name));
		 contractContent = contractContent.replace(Utility.toCamelCase(ss),English.plural(name).toLowerCase());
		 
		 Field[] declaredFields = pojo.getDeclaredFields();
		 SB search=new SB();
		 
		 
		 for(Field f:declaredFields)
			{
				String fname = f.getName();
				if(name.equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}
				
				
				if(f.isAnnotationPresent(DrillDown.class)  )
				{
					
					
					
					 
				}
				if(  f.isAnnotationPresent(DrillDownTable.class))
				{
					
					
				}
				
				/*if (fundSearchEntity.getName() != null) {
					if (params.length() > 0)
						params.append(" and ");
					params.append("name=:name");
					paramValues.put("name", fundSearchEntity.getName());
				} */
		
				
				
				search.as("if(").a(Utility.toCamelCase(pojo.getSimpleName())).a("SearchEntity")
				.a(".get").a(Utility.toSentenceCase(f.getName()));
				if(Utility.findTypes(f).equals("l"))
					search.a("Id");
					search.as("()!=null)").a("{").n();
				search.as("if (params.length() > 0)").n();
				search.as("params.append(\" and \");").n();
				search.as("params.append(").a("\"").as(f.getName()).a("=:").a(f.getName()).a("\");").n();
				search.a("paramValues.put(\"").a(f.getName()).as("\"").a(",").a(Utility.toCamelCase(pojo.getSimpleName())).a("SearchEntity")
				.a(".get").a(Utility.toSentenceCase(f.getName()));
				if(Utility.findTypes(f).equals("l"))
					search.a("Id");
				search.as("());}").n();

			}
		 
		 
		 search.as("if(").a(Utility.toCamelCase(pojo.getSimpleName())).a("SearchEntity")
			.a(".getIds");
		    search.as("()!=null)").a("{").n();
			search.as("if (params.length() > 0)").n();
			search.as("params.append(\" and \");").n();
			search.as("params.append(").a("\"").as("ids").a("=:").a("ids").a("\");").n();
			search.a("paramValues.put(\"").a("ids").as("\"").a(",").a(Utility.toCamelCase(pojo.getSimpleName())).a("SearchEntity")
			.a(".get").a("Ids");
			search.as("());}").n();
		 
		 contractContent= contractContent.replace("implement jdbc specfic search","implement jdbc specfic search"+"\n"+ search.str());
		 
		 System.out.println(contractContent);
		 
	
		 
		 
		  sqlWriter.write(contractContent);
		  sqlWriter.flush();
		  sqlWriter.close();
		  
	 
			  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	
	}
	
	

}
