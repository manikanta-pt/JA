package org.egov;

import static org.egov.Utility.NEWLINE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.atteo.evo.inflector.English;
import org.ja.annotation.DrillDown;
import org.ja.annotation.DrillDownTable;
import org.ja.annotation.Ignore;



public class ReqResponseCreator {

	
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_PUT = "PUT";
	PojoHolder pojoHolder=new PojoHolder();
	private List<String> jsonOrderList=new ArrayList<String>();
	private List<String> childList=new ArrayList<String>();
	
	public static void main(String[] args) {
		ReqResponseCreator rc=new ReqResponseCreator();
		rc.create("org.egov.workflow.web.contract.ProcessInstance");
	}
	
	public void create(String fullyQualifiedName)     {
		createContract(fullyQualifiedName);
		createReqRes(fullyQualifiedName);
		for(int i = 0; i < childList.size(); i++)
		{
			jsonOrderList=new ArrayList<String>();
			createContract(childList.get(i));
		 
		}
		
	}
	
	public void createContract(String fullyQualifiedName)    
	{
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		
		PrintWriter sqlWriter;
		try {
			String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/web/contract/";
			String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+".web.contract";

			
			
			String contractFileName = contractPackageDir+pojo.getSimpleName()+"Contract.java";
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
	        String fileName=Utility.SRCFOLDER+"/"+fullyQualifiedName.replace(".", "/")+".java";
	        String entityPakage= fullyQualifiedName.substring(0,fullyQualifiedName.lastIndexOf("."));
	        File file=new File(fileName);
			//Scanner sc=new Scanner(fileName);
		System.out.println(fileName);	
		 String content = new Scanner(file).useDelimiter("\\Z").next(); 
		 String name = pojo.getSimpleName();
		 String p = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".")+1,fullyQualifiedName.length());
		 
		 String contractContent = content.replaceAll(name, name+"Contract");
		 contractContent=contractContent.replaceAll("persistence.entity", "persistence.queue.contract");
		 contractContent=contractContent.replaceAll("@Entity", "");
		 contractContent=contractContent.replaceAll("@Table.*?\n", "");
		 contractContent=contractContent.replaceAll("@SequenceGenerator.*?\n", "");
		 contractContent=contractContent.replaceAll("@ManyToOne.*?\n", "");
		 contractContent=contractContent.replaceAll("@Column.*?\n", "");
		 contractContent=contractContent.replaceAll("@OneToMany.*?\n", "");
		 contractContent=contractContent.replaceAll("@JoinColumn.*?\n", "");
		 contractContent=contractContent.replaceAll("public static.*?\n", "");
		 contractContent=contractContent.replaceAll("@Override.*?\n", "");
		 contractContent=contractContent.replaceAll("@Id.*?\n", "");
		 contractContent=contractContent.replaceAll("@GeneratedValue.*?\n", "");
		 contractContent=contractContent.replaceAll("AbstractAuditable", "AuditableContract");
		 contractContent=contractContent.replaceAll("private static.*?\n", "");
		 if(!contractContent.contains("import lombok.Data"))
		 {
		String ss="import lombok.AllArgsConstructor;"+
					"import lombok.Builder;"+
					"import lombok.Getter;"+
					"import lombok.NoArgsConstructor;"+
					"import lombok.Setter;"+
					"import java.util.ArrayList;"+
					"import com.fasterxml.jackson.annotation.JsonPropertyOrder;"+
					"@Builder"+
					"@Getter"+
					"@Setter"+
					"@AllArgsConstructor"+
					"@NoArgsConstructor\n\n"+
					"public class";
		 contractContent=contractContent.replaceAll("public class", ss);
		 }else
		 {
			 String ss="import lombok.AllArgsConstructor;"+
						"import lombok.Builder;"+
						"import lombok.Getter;"+
						"import lombok.NoArgsConstructor;"+
						"import lombok.Setter;"+
						"import java.util.ArrayList;"+
						"import com.fasterxml.jackson.annotation.JsonPropertyOrder;"+
						"@Builder"+
						"@Getter"+
						"@Setter"+
						"@AllArgsConstructor"+
						"@NoArgsConstructor\n\n"+
						"public class";
			 contractContent=contractContent.replaceAll("public class", ss); 
		 }
		 
		 contractContent=contractContent.replaceAll(entityPakage, contractPackageDir);
		 
		 Field[] declaredFields = pojo.getDeclaredFields();
		 
		 for(Field f:declaredFields)
			{
				//String name = f.getName();
				if(name.equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}
				jsonOrderList.add(f.getName());
				
				if(f.isAnnotationPresent(DrillDown.class)  )
				{
					
					
					childList.add(f.getType().getName());
					 
				}
				if(  f.isAnnotationPresent(DrillDownTable.class))
				{
					childList.add(Utility.getEnclosingType(f).getName());
				}
				
				if(f.isAnnotationPresent(Ignore.class) )
				{
					
					
					contractContent=contractContent.replaceAll(".*"+f.getName()+".*\n"," ");
					 
				}
				if(Utility.findTypes(f).equals("ignore"))
				{
					
					
					
					
				}
				if(Utility.findTypes(f).equals("l"))
				{
					
					contractContent=contractContent.replaceAll(" "+f.getType().getSimpleName()+" "," " +f.getType().getSimpleName()+"Contract"+" ");
				}
				
			 
			}
		 SB sorder=new SB();
		 if(!jsonOrderList.isEmpty())
		 {
			
			 sorder.a("@JsonPropertyOrder({ ");
			 int count=0;
			 for(String s:jsonOrderList)
			 {
				 if(count>0)
					 sorder.a(",") ;
				 count++;
				 sorder.a("\""+s+"\"");
			 }
			 sorder.a("})");
		 }
		 
		 contractContent=contractContent.replaceAll("public class", sorder.str()+"\npublic class");
		 
		 contractContent=contractContent.replaceAll("import lombok.Data", "import com.fasterxml.jackson.annotation.JsonPropertyOrder;\nimport lombok.Data;"); 
		 if(!contractContent.contains("import lombok.Data"))
		 
		 contractContent=contractContent.replaceAll("@Ignore.*?\n", "");
		 
			
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
		String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/web/contract/";
		String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+".web.contract";

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
		    request.a("private RequestInfo requestInfo = new RequestInfo();").a(NEWLINE);
		    request.a("private List<").a(pojo.getSimpleName()+"Contract> ").a(Utility.toCamelCase(English.plural(pojo.getSimpleName())))
		    .a(" =new ArrayList<").a(pojo.getSimpleName()+"Contract>() ;").a(NEWLINE);
		    request.a("private ").a(pojo.getSimpleName()+"Contract ").a(Utility.toCamelCase(pojo.getSimpleName())).
		    a(" =new ").a(pojo.getSimpleName()+"Contract() ;").a(NEWLINE);
		    request.a("private Pagination page=new Pagination();");
		    request.a("}");
		    requestFile.write(request.str());
		    requestFile.flush();
		    
		    
		    
		    response.a("package  ").a(contractPackage).a(";").a(NEWLINE);
		    response.a("import java.util.List;  ").a(Utility.NEWLINE);
		    response.a(" import lombok.Data; ").a(Utility.NEWLINE);
		    response.a("import com.fasterxml.jackson.annotation.JsonInclude;");
		    response.a("import com.fasterxml.jackson.annotation.JsonInclude.Include;");
		    
		    response.a("@JsonInclude(value=Include.NON_NULL)");
		    response.a("public @Data class ").a(pojo.getSimpleName()+"ContractResponse {").a(NEWLINE);
		    response.a("private ResponseInfo responseInfo ;").a(NEWLINE);
		    response.a("private List<").a(pojo.getSimpleName()+"Contract> ").a(Utility.toCamelCase(English.plural(pojo.getSimpleName())))
		    .a(";").a(NEWLINE);
		    response.a("private ").a(pojo.getSimpleName()+"Contract ").a(Utility.toCamelCase(pojo.getSimpleName()))
		    .a(" ;").a(NEWLINE);
		    response.a("private Pagination page;");
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

