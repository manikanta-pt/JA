package org.egov.rest;

import static org.egov.Utility.NEWLINE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.atteo.evo.inflector.English;
import org.egov.PojoHolder;
import org.egov.SB;
import org.egov.Utility;



public class RestControllerCreator {

	
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_PUT = "PUT";
	PojoHolder pojoHolder=new PojoHolder();
	private List<String> jsonOrderList=new ArrayList<String>();
	
	public static void main(String[] args) {
		RestControllerCreator rc=new RestControllerCreator();
		rc.create("org.egov.egf.persistence.entity.BankBranch");
	}
	
	public void create(String fullyQualifiedName)    
	{
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		
		String refFileName="org.egov.egf.web.controller.BankController";
		PrintWriter sqlWriter;
		try {
			String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/web/controller/";
			//String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+".persistence.queue.contract";

			
			
			String contractFileName = contractPackageDir+pojo.getSimpleName()+"Controller.java";
			File ff=new File(contractFileName);
			try {
				if(ff.exists())
				{
					
				}else
				{
					//ff.mkdirs();
					ff.createNewFile();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sqlWriter = new PrintWriter(contractFileName, "UTF-8");
	       // String fileName=Utility.SRCFOLDER+"/"+refFileName.replace(".", "/")+".java";
			String fileName="/home/mani/Workspaces/ms/pgr-services/financials/egf-masters/src/main/java/org/egov/egf/web/controller/BankController.java";
	        String entityPakage= fullyQualifiedName.substring(0,fullyQualifiedName.lastIndexOf("."));
	        File file=new File(fileName);
			//Scanner sc=new Scanner(fileName);
		System.out.println(fileName);	
		 String content = new Scanner(file).useDelimiter("\\Z").next(); 
		 String name = pojo.getSimpleName();
		 String p = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".")+1,fullyQualifiedName.length());
		  
		
		 String contractContent = content;//.replaceAll(maping1, replace);
		 System.out.println("    now "+contractContent);
		  contractContent = contractContent.replace("Bank",name);
		 System.out.println(contractContent);
		 contractContent = contractContent.replace("bank", Utility.toCamelCase(name));
		 System.out.println(contractContent);
	//	 contractContent = contractContent.replace("Banks",English.plural(name));
		 String ss=name+"s";
		 System.out.println(ss);
		 contractContent = contractContent.replace(ss,English.plural(name));
		 contractContent = contractContent.replace(Utility.toCamelCase(ss),English.plural(name).toLowerCase());
		 System.out.println(contractContent);
		 
	/*	 //String maping= "\\/banks"  ;
		 String maping1=Pattern.compile("\\/"+English.plural(name)).toString();
		 String replace= "\\/"+English.plural(name).toLowerCase();
		
		 contractContent = content.replaceAll(maping1, replace);*/
		 
		 //contractContent = contractContent.replace("Bank",name);
		 //contractContent = contractContent.replace("bank"," "+Utility.toCamelCase(name));
		 
		 /*contractContent = contractContent.replace("bankContract", Utility.toCamelCase(name)+"Contract");
		 contractContent = contractContent.replace("BankContract", name+"Contract");
		 contractContent = contractContent.replace("BankController", name+"Controller");
		 contractContent = contractContent.replace("BankService", name+"Service");
		 contractContent = contractContent.replace("bankService", Utility.toCamelCase(name)+"Service");*/
			
			  sqlWriter.write(contractContent);
			  sqlWriter.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//StringBuilder main=new StringBuilder();
		
		
	}

	private void createReqRes(String fullyQualifiedName) {
		Class<?> pojo = pojoHolder.getPojo();
		SB request=new SB();
		SB response=new SB();
		String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/persistence/queue/contract/";
		String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+".persistence.queue.contract";

		try {
			String reqFileName = contractPackageDir+pojo.getSimpleName()+"ContractRequest.java";
			File reqFile=new File(reqFileName);
			
		
		    String resFileName = contractPackageDir+pojo.getSimpleName()+"ContractResponse.java";
		    File resFile=new File(resFileName);
		   
       
		    try {
				if(reqFile.exists())
				{
					
				}else
				{
				//	reqFile.mkdirs();
					
					reqFile.createNewFile();
				}
				
				if(resFile.exists())
				{
					
				}else
				{
					//resFile.mkdirs();
					resFile.createNewFile();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    PrintWriter	requestFile = new PrintWriter(reqFileName, "UTF-8");
		    PrintWriter	responseFile = new PrintWriter(resFileName, "UTF-8");
		    
		    request.a("package  ").a(contractPackage).a(";").a(NEWLINE);
		    request.a("import java.util.List;  ").a(Utility.NEWLINE);
		    request.a(" import lombok.Data; ").a(Utility.NEWLINE);
		    
		    request.a("public @Data class ").a(pojo.getSimpleName()+"ContractRequest {").a(NEWLINE);
		    request.a("private RequestInfo requestInfo = null;").a(NEWLINE);
		    request.a("private List<").a(pojo.getSimpleName()+"Contract> ").a(Utility.toCamelCase(English.plural(pojo.getSimpleName())))
		    .a(";").a(NEWLINE);
		    request.a("private ").a(pojo.getSimpleName()+"Contract ").a(Utility.toCamelCase(pojo.getSimpleName())).a(";").a(NEWLINE);
		    request.a("private Page page=new Page();");
		    request.a("}");
		    requestFile.write(request.str());
		    requestFile.flush();
		    
		    
		    
		    response.a("package  ").a(contractPackage).a(";").a(NEWLINE);
		    response.a("import java.util.List;  ").a(Utility.NEWLINE);
		    response.a(" import lombok.Data; ").a(Utility.NEWLINE);
		    
		    response.a("public @Data class ").a(pojo.getSimpleName()+"ContractResponse {").a(NEWLINE);
		    response.a("private ResponseInfo responseInfo = null;").a(NEWLINE);
		    response.a("private List<").a(pojo.getSimpleName()+"Contract> ").a(Utility.toCamelCase(English.plural(pojo.getSimpleName())))
		    .a(";").a(NEWLINE);
		    response.a("private ").a(pojo.getSimpleName()+"Contract ").a(Utility.toCamelCase(pojo.getSimpleName())).a(";").a(NEWLINE);
		    response.a("private Page page=new Page();");
		    response.a("}");
		    responseFile.write(response.str());
		    responseFile.flush();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	 
}

