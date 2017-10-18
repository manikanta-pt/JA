package org.egov;

import static org.egov.Utility.NEWLINE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	private String entityPackageDir;
	private String entityPackage;
	private String lambokData;
	private String lambokDataWithoutBuilder;
	
	public static void main(String[] args) {
		ReqResponseCreator rc=new ReqResponseCreator();
		rc.create("org.egov.egf.master.domain.model.Bank");
		rc.create("org.egov.egf.master.domain.model.FiscalPeriod");
		rc.create("org.egov.egf.master.domain.model.Function");
		rc.create("org.egov.egf.master.domain.model.Functionary");
		rc.create("org.egov.egf.master.domain.model.Fundsource");
		rc.create("org.egov.egf.master.domain.model.Scheme");
		rc.create("org.egov.egf.master.domain.model.SubScheme");
		rc.create("org.egov.egf.master.domain.model.Supplier");
		rc.create("org.egov.egf.master.domain.model.AccountDetailType");
		rc.create("org.egov.egf.master.domain.model.AccountDetailKey");
		rc.create("org.egov.egf.master.domain.model.AccountEntity");
		rc.create("org.egov.egf.master.domain.model.AccountCodePurpose");
		rc.create("org.egov.egf.master.domain.model.ChartOfAccount");
		rc.create("org.egov.egf.master.domain.model.ChartOfAccountDetail");
		rc.create("org.egov.egf.master.domain.model.BudgetGroup");
		rc.create("org.egov.egf.master.domain.model.FinancialStatus");
		rc.create("org.egov.egf.master.domain.model.FinancialConfiguration");
		rc.create("org.egov.egf.master.domain.model.BankBranch");
		rc.create("org.egov.egf.master.domain.model.BankAccount");
		
		
	}
	
	public void create(String fullyQualifiedName)     {
		
		 

		 lambokDataWithoutBuilder = "import lombok.AllArgsConstructor;\n"+
					"import lombok.Builder;\n"+
					"import lombok.Getter;\n"+
					"import lombok.NoArgsConstructor;\n"+
					"import lombok.Setter;\n"+
					"@Getter\n"+
					"@Setter\n"+
					"@AllArgsConstructor\n"+
					"@NoArgsConstructor\n\n";
		 
		 lambokData =lambokDataWithoutBuilder+ 
					"@Builder\n";
		createContract(fullyQualifiedName);
		createEntity(fullyQualifiedName);
		createSearchFiles(fullyQualifiedName);
		
		createSearchRepo(fullyQualifiedName);
		
		createReqRes(fullyQualifiedName);
		for(int i = 0; i < childList.size(); i++)
		{
			jsonOrderList=new ArrayList<String>();
			createContract(childList.get(i));
			createEntity(childList.get(i));
			//createSearchFiles(childList.get(i));
		 
		}
		
	}
	
	private void createSearchFiles(String fullyQualifiedName) {
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/web/contract/";
		String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+"."+Utility.SUBMODULE_IDENTIFIER+".web.contract";
		PrintWriter sqlWriter=null;
		String contractFileName = contractPackageDir+pojo.getSimpleName()+"SearchContract.java";
		File ff=new File(contractFileName);
		try {
			if(ff.exists())
			{
				
			}else
			{
				//ff.mkdirs();
				ff.createNewFile();
			}
			sqlWriter = new PrintWriter(contractFileName, "UTF-8");	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
       // String fileName=Utility.SRCFOLDER+"/"+fullyQualifiedName.replace(".", "/")+".java";
        
        SB searchContract=new SB();
        searchContract.as("package").as(contractPackage).a(";").n();
        searchContract.a(lambokDataWithoutBuilder);
        searchContract.as("public").as("class").a(pojo.getSimpleName()).as("SearchContract").as("extends").a(pojo.getSimpleName()).as("Contract")
        .as("{");
        //add additional Attributes here
        
        searchContract.as("private String ids;").n();
        searchContract.as("private String  sortBy;").n();
        searchContract.as("private Integer pageSize;").n();
        searchContract.as("private Integer offset;").n();
        
        
        
        for(Field field:pojo.getDeclaredFields())
        {
        	
        	
        }
        
        
        
        
        searchContract.as("}");
        sqlWriter.write(searchContract.str());
        sqlWriter.flush();
        sqlWriter.close();
           
        
        String fileName=Utility.SRCFOLDER+"/"+fullyQualifiedName.replace(".", "/")+"Search.java";
        
       File createNewFile = Utility.createNewFile(fileName);
       
       try {
		sqlWriter=new PrintWriter(createNewFile,"UTF-8");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
       SB searchDomain=new SB();
       searchDomain.as("package").as(pojo.getPackage().getName()).a(";").n();
       searchDomain.a(lambokDataWithoutBuilder);
       searchDomain.as("public").as("class").a(pojo.getSimpleName()).as("Search").as("extends").a(pojo.getSimpleName())
       .as("{");
       //add additional Attributes here
       searchDomain.as("private String ids;").n();
       searchDomain.as("private String  sortBy;").n();
       searchDomain.as("private Integer pageSize;").n();
       searchDomain.as("private Integer offset;").n();
       
       
       
       
       searchDomain.as("}");
       
       sqlWriter.write(searchDomain.str());
       sqlWriter.flush();
       sqlWriter.close();
       
       
       
       
        fileName=entityPackageDir+pojo.getSimpleName()+"SearchEntity.java";
       
        createNewFile = Utility.createNewFile(fileName);
       
       try {
		sqlWriter=new PrintWriter(createNewFile,"UTF-8");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
       SB searchEntity=new SB();
       searchEntity.as("package").as(entityPackage).a(";").n();
       searchEntity.as("import ").as(fullyQualifiedName).a(";").n();
       searchEntity.a(lambokDataWithoutBuilder);
       searchEntity.as("public").as("class").a(pojo.getSimpleName()).as("SearchEntity").as("extends").a(pojo.getSimpleName()).as("Entity")
       .as("{");
       //add additional Attributes here
       searchEntity.as("private String ids;").n();
       searchEntity.as("private String  sortBy;").n();
       searchEntity.as("private Integer pageSize;").n();
       searchEntity.as("private Integer offset;").n();
       
       
       
       SB toDomain=new SB();
		 SB toEntity=new SB();
		 
		 toDomain.as("public").as(pojo.getSimpleName()).as("toDomain(){").n();
		 
		 toDomain.as(pojo.getSimpleName()).as(Utility.toCamelCase(pojo.getSimpleName()))
		 .as("= new").as(pojo.getSimpleName()).as("();").n();
		 toDomain.as("super.toDomain(").a(Utility.toCamelCase(pojo.getSimpleName())).a(");");
		 
		 toEntity.as("public").a(pojo.getSimpleName()).as("SearchEntity").as("toEntity(").a(pojo.getSimpleName()).as("Search")
		 .a(Utility.toCamelCase(pojo.getSimpleName())).a("Search){").n();
		 toEntity.as("super.toEntity((").a(pojo.getSimpleName()).a(")").a(Utility.toCamelCase(pojo.getSimpleName())).a("Search);").n();
	
		  
		 toEntity.as("this.pageSize="+Utility.toCamelCase(pojo.getSimpleName())+"Search.getPageSize();");
		 
		 toEntity.as("this.offset="+Utility.toCamelCase(pojo.getSimpleName())+"Search.getOffset();");
		 toEntity.as("this.sortBy="+Utility.toCamelCase(pojo.getSimpleName())+"Search.getSortBy();");
		 toEntity.as("this.ids="+Utility.toCamelCase(pojo.getSimpleName())+"Search.getIds();");
		 
		
	 toDomain.as("return").as(Utility.toCamelCase(pojo.getSimpleName())).a(";}").n();
	 toEntity.as("return this;}").n();
     
     
	 searchEntity.as(toDomain.str()).n();
	 searchEntity.as(toEntity.str()).n();
	 
       
       
       
       searchEntity.as("}");
       
       
       
       
       
       sqlWriter.write(searchEntity.str());
       
       sqlWriter.flush();
       sqlWriter.close();
        
        
        
		
		
	}

	private void createSearchRepo(String fullyQualifiedName) {
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/web/repository/";
		String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+"."+Utility.SUBMODULE_IDENTIFIER+".web.repository";
		PrintWriter sqlWriter=null;
		String contractFileName = contractPackageDir+pojo.getSimpleName()+"ContractRepository.java";
		
		File repoFile = Utility.createNewFile(contractFileName);
		try {
			sqlWriter=new PrintWriter(repoFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
       // String fileName=Utility.SRCFOLDER+"/"+fullyQualifiedName.replace(".", "/")+".java";
        
        SB searchContract=new SB();
        searchContract.as("package").as(contractPackage).a(";").n();
      

        
        searchContract.as("import org.egov.common.web.contract.CommonResponse;").n();
        searchContract.as("import org.egov.").a(Utility.MODULEIDENTIFIER).a(".").a(Utility.SUBMODULE_IDENTIFIER).a(".web.contract.").a(pojo.getSimpleName()).a("Contract;").n();
        searchContract.as("import org.egov.").a(Utility.MODULEIDENTIFIER).a(".").a(Utility.SUBMODULE_IDENTIFIER).a(".web.contract.").a(pojo.getSimpleName()).a("SearchContract;").n();
        searchContract.as("import org.springframework.beans.factory.annotation.Autowired;").n();
        searchContract.as("import org.springframework.beans.factory.annotation.Value;").n();
        searchContract.as("import org.springframework.web.client.RestTemplate;").n();
        searchContract.as("import org.springframework.stereotype.Service;").n();
        searchContract.as("import com.fasterxml.jackson.core.type.TypeReference;").n();
        searchContract.as("import com.fasterxml.jackson.databind.ObjectMapper;").n();
        searchContract.as("@Service").n();
        searchContract.as("public").as("class").a(pojo.getSimpleName()).as("ContractRepository").as("{").n();
        //add additional Attributes here
        
        searchContract.as("private RestTemplate restTemplate;").n();
        searchContract.as("private String hostUrl;").n();
        searchContract.as("public static final String SEARCH_URL = \"");
        searchContract.a("/").a(Utility.MODULEIDENTIFIER).a("-").a(Utility.SUBMODULE_IDENTIFIER)
        .a("/").a(English.plural(pojo.getSimpleName()).toLowerCase()).a("/").a("search?").a("\";").n();

        searchContract.a("@Autowired\n	private ObjectMapper objectMapper;").n();
        
        
      /*  public FinancialYearContractRepository(@Value("${egf.master.host.url}") String hostUrl, RestTemplate restTemplate) {
    		this.restTemplate = restTemplate;
    		this.hostUrl = hostUrl;
    	}*/
        
        
        
        
        
        
        searchContract.a("public ").a(pojo.getSimpleName()).a("ContractRepository(@Value(\"${")
        .a(Utility.MODULEIDENTIFIER).a(".").a(Utility.SUBMODULE_IDENTIFIER).a("host.url}\")")
        .a(" String hostUrl,RestTemplate restTemplate) {").n();
        searchContract.a("this.restTemplate = restTemplate;").n();
        searchContract.a("this.hostUrl = hostUrl;").n().a("}").n();
        
        Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);	
		String CurrfilePath =s+ "/src/main/resources/ContractRepoById.txt";
        
        File byIdApi=new File(CurrfilePath);
        String byIdString = Utility.readFile(byIdApi);
        
         byIdString = byIdString.replaceAll("Fund", pojo.getSimpleName());
         byIdString = byIdString.replaceAll("fund", Utility.toCamelCase(pojo.getSimpleName()));
        
         searchContract.a(byIdString);
        
        
        for(Field field:pojo.getDeclaredFields())
        {
        	
        	
        }
        
        
        
        
        searchContract.as("}");
        sqlWriter.write(searchContract.str());
        sqlWriter.flush();
        sqlWriter.close();
           
        
	}
	
	
	
	
	public void createContract(String fullyQualifiedName)    
	{
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		
		PrintWriter sqlWriter;
		try {
			String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/web/contract/";
			String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+"."+Utility.SUBMODULE_IDENTIFIER+".web.contract";

			String contractFileName = contractPackageDir+pojo.getSimpleName()+"Contract.java";
			 
			File ff=Utility.createNewFile(contractFileName);
			 
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
		 contractContent=contractContent.replaceAll("package.*?\n", "package "+contractPackage+";");
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
		 contractContent=contractContent.replaceAll("Auditable", "AuditableContract");
		 
		 contractContent=contractContent.replaceAll("import org.egov.common.domain.model.AuditableContract", "import org.egov.common.web.contract.AuditableContract");
		 contractContent = Utility.removeJaDependency(contractContent);
		 contractContent=contractContent.replaceAll("private static.*?\n", "");
		 if(!contractContent.contains("import lombok"))
		 {
		String ss="import lombok.AllArgsConstructor;\n"+
					"import lombok.Builder;\n"+
					"import lombok.Getter;\n"+
					"import lombok.NoArgsConstructor;\n"+
					"import lombok.Setter;\n"+
					"import java.util.ArrayList;\n"+
					"import com.fasterxml.jackson.annotation.JsonPropertyOrder;\n"+
					"@Builder\n"+
					"@Getter\n"+
					"@Setter\n"+
					"@AllArgsConstructor\n"+
					"@NoArgsConstructor\n\n"+
					"public class";
		 contractContent=contractContent.replaceAll("public class", ss);
		 }else
		 {
			 String ss="import lombok.AllArgsConstructor;\n"+
						"import lombok.Builder;\n"+
						"import lombok.Getter;\n"+
						"import lombok.NoArgsConstructor;\n"+
						"import lombok.Setter;\n"+
						"import java.util.ArrayList;\n"+
						"import com.fasterxml.jackson.annotation.JsonPropertyOrder;\n"+
						"@Builder\n"+
						"@Getter\n"+
						"@Setter\n"+
						"@AllArgsConstructor\n"+
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
				
			//	contractContent=contractContent.replaceAll("extends Auditable"," extends AuditableContract ");
				
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

	
	
	
	public void createEntity(String fullyQualifiedName)    
	{
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		
		PrintWriter sqlWriter;
		try {
			entityPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/persistence/entity/";
			entityPackage = "org.egov."+Utility.MODULEIDENTIFIER+"."+Utility.SUBMODULE_IDENTIFIER+".persistence.entity";

			String contractFileName = entityPackageDir+pojo.getSimpleName()+"Entity.java";
			File ff=Utility.createNewFile(contractFileName);
			sqlWriter = new PrintWriter(contractFileName, "UTF-8");
	        String fileName=Utility.SRCFOLDER+"/"+fullyQualifiedName.replace(".", "/")+".java";
	        String entityPakage= fullyQualifiedName.substring(0,fullyQualifiedName.lastIndexOf("."));
	        File file=new File(fileName);
			//Scanner sc=new Scanner(fileName);
		 
		 SB contractContent=new SB();
		 
		 contractContent.a("package ").a(entityPackage).a(";").n();
		 
		 contractContent.a("import ").a(fullyQualifiedName).a(";").n();
		 contractContent.a("import java.util.Date").a(";").n();
		
		 
		 SB toDomain=new SB();
		 SB toEntity=new SB();
		 
		 toDomain.as("public").as(pojo.getSimpleName()).as("toDomain(){").n();
		 
		 toDomain.as(pojo.getSimpleName()).as(Utility.toCamelCase(pojo.getSimpleName()))
		 .as("= new").as(pojo.getSimpleName()).as("();").n();
		 toDomain.as("super.toDomain(").a(Utility.toCamelCase(pojo.getSimpleName())).a(");");
		 
		 toEntity.as("public").a(pojo.getSimpleName()).as("Entity").as("toEntity(").as(pojo.getSimpleName()).
		 as(Utility.toCamelCase(pojo.getSimpleName())).a("){").n();
		 toEntity.as("super.toEntity((").a(pojo.getSuperclass().getSimpleName()).a(")").a(Utility.toCamelCase(pojo.getSimpleName())).a(");").n();
		 
		 contractContent.a(lambokData);
		 
		 contractContent.a("public class ").a(pojo.getSimpleName()).a("Entity extends AuditableEntity").n();
		 
		 contractContent.a("{").n();
		 
		 contractContent.a("public static final String TABLE_NAME =\"egf_").a(pojo.getSimpleName().toLowerCase()).a("\";").n();
		 
		 Field[] declaredFields = pojo.getDeclaredFields();
		 
		 for(Field f:declaredFields)
			{
				String name = f.getName();
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
				
				
			 switch(Utility.findTypes(f))
			 {
			  
			 case "l":
				
				 contractContent.a("private String ").a(Utility.toCamelCase(f.getName())+"Id").a(";").n();
				 if(f.getType().isEnum())
				 {
					 
					 toDomain.a(Utility.toCamelCase(pojo.getSimpleName())).a(".")
					 .a("set").a(Utility.toSentenceCase(f.getName())).a("(")
					 .a(f.getType().getSimpleName()).a(".valueOf(this.").a(f.getName()).a("));") 
					 .n();
					// accountEntity.getAccountDetailType()!=null?accountEntity.getAccountDetailType().getId():null; 
					 toEntity.a("this.").a(f.getName()).a("Id=").a(Utility.toCamelCase(pojo.getSimpleName())).a(".")
					 .a("get")
					 .a(Utility.toSentenceCase(f.getName())).a("()!=null?")
					 .a(Utility.toCamelCase(pojo.getSimpleName())).a(".")
					 .a("get")
					 .a(Utility.toSentenceCase(f.getName())).a("().toString():null;")
					 .n();
				 }else{
				 
				 toDomain.a(Utility.toCamelCase(pojo.getSimpleName())).a(".")
				 .a("set").a(Utility.toSentenceCase(f.getName())).a("(")
				 .a(f.getType().getSimpleName()).a(".builder().id(").a(f.getName()).a("Id").a(").build());") 
				 .n();
				// accountEntity.getAccountDetailType()!=null?accountEntity.getAccountDetailType().getId():null; 
				 toEntity.a("this.").a(f.getName()).a("Id=").a(Utility.toCamelCase(pojo.getSimpleName())).a(".")
				 .a("get")
				 .a(Utility.toSentenceCase(f.getName())).a("()!=null?")
				 .a(Utility.toCamelCase(pojo.getSimpleName())).a(".")
				 .a("get")
				 .a(Utility.toSentenceCase(f.getName())).a("().getId():null;")
				 .n();
				 }
				 break;
			 case "ignore":
				 break;
			default :	 
				 contractContent.a("private ").a(f.getType().getSimpleName()).a(" ").a(f.getName()).a(";").n();
				 toDomain.a(Utility.toCamelCase(pojo.getSimpleName())).a(".")
				 .a("set").a(Utility.toSentenceCase(f.getName())).a("(").a("this.").a(f.getName()).a(");").n();
				 toEntity.a("this.").a(f.getName()).a("=").a(Utility.toCamelCase(pojo.getSimpleName())).a(".")
				 .a("get")
				 .a(Utility.toSentenceCase(f.getName())).a("();").n();
			 
			 }
			 
			}
		 toDomain.as("return").as(Utility.toCamelCase(pojo.getSimpleName())).a(";}").n();
		 toEntity.as("return this;}").n();
		 contractContent.a(toDomain.str()).n();
		 contractContent.a(toEntity.str()).n();
		
		 contractContent.a("}").n();
			
			  sqlWriter.write(contractContent.str());
			  sqlWriter.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//StringBuilder main=new StringBuilder();
		
		
	}

	private void createReqRes(String fullyQualifiedName) {
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		SB request=new SB();
		SB response=new SB();
		String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/web/requests/";
		String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+"."+Utility.SUBMODULE_IDENTIFIER+".web.requests";

		try {
			String reqFileName = contractPackageDir+pojo.getSimpleName()+"Request.java";
			File reqFile= Utility.createNewFile(reqFileName);
			
		
		    String resFileName = contractPackageDir+pojo.getSimpleName()+"Response.java";
		    File resFile=Utility.createNewFile(resFileName);
		   
    
		    
			 
		    PrintWriter	requestFile = new PrintWriter(reqFileName, "UTF-8");
		    PrintWriter	responseFile = new PrintWriter(resFileName, "UTF-8");
		    
		    request.a("package  ").a(contractPackage).a(";").a(NEWLINE);
		    request.a("import java.util.List;  ").a(Utility.NEWLINE);
		    request.a(" import lombok.Data; ").a(Utility.NEWLINE);
		    request.a(" import org.egov.common.web.contract.RequestInfo;").n();
		    
		    request.a("public @Data class ").a(pojo.getSimpleName()+"Request {").a(NEWLINE);
		    request.a("private RequestInfo requestInfo = new RequestInfo();").a(NEWLINE);
		    request.a("private List<").a(pojo.getSimpleName()+"Contract> ").a(Utility.toCamelCase(English.plural(pojo.getSimpleName())))
		    .a(" =new ArrayList<").a(pojo.getSimpleName()+"Contract>() ;").a(NEWLINE);
		    /*request.a("private ").a(pojo.getSimpleName()+"Contract ").a(Utility.toCamelCase(pojo.getSimpleName())).
		    a(" =new ").a(pojo.getSimpleName()+"Contract() ;").a(NEWLINE);*/
		   /* request.a("private Pagination page=new Pagination();");*/
		    request.a("}");
		    requestFile.write(request.str());
		    requestFile.flush();
		    
		    
		    
		    response.a("package  ").a(contractPackage).a(";").a(NEWLINE);
		    response.a("import java.util.List;  ").a(Utility.NEWLINE);
		    response.a(" import lombok.Data; ").a(Utility.NEWLINE);
		    response.a("import com.fasterxml.jackson.annotation.JsonInclude;");
		    response.a("import com.fasterxml.jackson.annotation.JsonInclude.Include;");
		    response.a(" import org.egov.common.web.contract.ResponseInfo;").n();
		    response.a(" import org.egov.common.web.contract.PaginationContract;").n();
		    
		    response.a("@JsonInclude(value=Include.NON_NULL)");
		    response.a("public @Data class ").a(pojo.getSimpleName()+"Response {").a(NEWLINE);
		    response.a("private ResponseInfo responseInfo ;").a(NEWLINE);
		    response.a("private List<").a(pojo.getSimpleName()+"Contract> ").a(Utility.toCamelCase(English.plural(pojo.getSimpleName())))
		    .a(";").a(NEWLINE);
		   /* response.a("private ").a(pojo.getSimpleName()+"Contract ").a(Utility.toCamelCase(pojo.getSimpleName()))
		    .a(" ;").a(NEWLINE);*/
		    response.a("private PaginationContract page;");
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

