package org.egov;

import static org.egov.Utility.NEWLINE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.atteo.evo.inflector.English;
import org.springframework.validation.SmartValidator;




public class RestServiceCreator {

	PojoHolder pojoHolder=new PojoHolder();
	private Field[] declaredFields;
	private Set<String> serviceSet=new HashSet<String>();
	public static void main(String[] args) {
		
		RestServiceCreator rc=new RestServiceCreator();
		rc.createService("org.egov.egf.master.domain.model.Bank");
	}
	//this is to standardize only 
	public void	create(String fullyQualifiedName)
	{	
		createService(fullyQualifiedName);
	}
	

	public void	createService(String fullyQualifiedName)
	{	
	try {
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		
		String refFileName="org.egov.egf.master.domain.service.FundService";
		PrintWriter sqlWriter;
	 
			String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/domain/service/";
			//String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+".persistence.queue.contract";

			
			
			String contractFileName = contractPackageDir+pojo.getSimpleName()+"Service.java";
			File ff=Utility.createNewFile(contractFileName);
			
			sqlWriter = new PrintWriter(contractFileName, "UTF-8");
	       // String fileName=Utility.SRCFOLDER+"/"+refFileName.replace(".", "/")+".java";
			String fileName="/home/mani/Workspaces/ms/egov-services/financials/egf-master/src/main/java/org/egov/egf/master/domain/service/FundService.java";
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
		 System.out.println(contractContent);
		 
		 
		 contractContent= contractContent.replace("fetch related items", "fetch related items\n"+getRelatedContent(pojo));
		 
		 SB repos=new SB();
		 for(String s:serviceSet)
		 {
			 repos.a("@Autowired").n();
			 repos.as("private").as(Utility.toSentenceCase(s)).a(s).a(";").n();
		 
	
		 }
		 contractContent= contractContent.replace("private SmartValidator validator;", "private SmartValidator validator;\n"+repos.str());
		 
		 
		 contractContent = contractContent.replace("ContractContract", "Contract");
		  sqlWriter.write(contractContent);
		  sqlWriter.flush();
		  sqlWriter.close();
		  
	 
			  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	
	}
	
	public String getRelatedContent(Class<?> pojo) {
		SB content=new SB();
		
		Field[] fields = pojo.getDeclaredFields();
		String entityName=pojo.getSimpleName();
		String pojoObject = Utility.toCamelCase(pojo.getSimpleName());
		boolean innerContentExists=false;
		SB contentInner=new SB();
		
		
		for(Field f:fields)
		{
			if(Utility.findTypes(f).equals("l"))
					{
				if(f.getType().isEnum())
				continue;	
				innerContentExists=true;
				serviceSet.add(Utility.toCamelCase(f.getType().getSimpleName())+"Repository");
				contentInner.a("if(").a(pojoObject).a(".").a("get").a(Utility.toSentenceCase(f.getName())).a("()!=null").a(")").a(NEWLINE);
				contentInner.a("{").a(NEWLINE);
				contentInner.a(f.getType().getSimpleName()).a(Utility.TAB).a(f.getName())
				.a("=").a(Utility.toCamelCase(f.getType().getSimpleName())).a("Repository").a(".findById(").a(pojoObject).a(".").a("get")
				.a(Utility.toSentenceCase(f.getName())).a("());").a(NEWLINE);
				contentInner.a("if(").a(f.getName()).a("==null)").a(NEWLINE);
				contentInner.a("{").a(NEWLINE);
				String fieldName="\""+f.getName()+"\"";
				String messageKey="\""+f.getName()+".invalid\"";
				String defaultMessage="\" Invalid "+f.getName()+"\"";
				contentInner.a("throw new InvalidDataException("+fieldName+","+messageKey+","+defaultMessage+");").a(NEWLINE).a("}");
				
				contentInner.a(pojoObject).a(".").a("set").a(Utility.toSentenceCase(f.getName())).a("(").a(f.getName()).a(");").n();
				//contentInner.a("model.map("+f.getName()+","+pojoObject+".get"+Utility.toSentenceCase(f.getName())+"());").a(NEWLINE);
				contentInner.a("}").a(NEWLINE);
				}
		}
		//contentInner.a("}").a(NEWLINE);
		
		
		
		return contentInner.str();
	}

}
