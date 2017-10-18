package org.egov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.atteo.evo.inflector.English;

public class ListnerCreator {

	PojoHolder pojoHolder = new PojoHolder();
	private Field[] declaredFields;
	private Set<String> serviceSet = new HashSet<String>();

	public static void main(String[] args) {

		ListnerCreator rc = new ListnerCreator();
		rc.create("org.egov.egf.master.domain.model.Fund");
		rc.create("org.egov.egf.master.domain.model.FinancialYear");
		rc.create("org.egov.egf.master.domain.model.FiscalPeriod");
		rc.create("org.egov.egf.master.domain.model.Function");
		rc.create("org.egov.egf.master.domain.model.Functionary");
		rc.create("org.egov.egf.master.domain.model.Fundsource");
		rc.create("org.egov.egf.master.domain.model.Scheme");
		rc.create("org.egov.egf.master.domain.model.BankAccount");
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
	}

	// this is to standardize only
	public void create(String fullyQualifiedName) {
		createService(fullyQualifiedName);
	}

	public void	createService(String fullyQualifiedName)
	{	
	try {
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
	    String filePath=	Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/persistence/queue/Financial"
				+Utility.toSentenceCase(Utility.SUBMODULE_IDENTIFIER)+"sListener.java";
	 //   System.out.println(filePath);
	  //  System.out.println("/egf-master/src/main/java/org/egov/egf/master/persistence/queue/FinancialMastersListener.java");
	
	    File f=new File(filePath);
         String readFile = Utility.readFile(f);
        //System.out.println(readFile);
	
         readFile=    readFile.replace("implement the details here", "implement the details here\n"+getRelatedContent(pojo));
		
         
         File createNewFile = Utility.createNewFile("/home/mani/keyFile.txt");
         String key= " @Value(\"${kafka.topics.egf.masters.keyName.completed.key}\")\n"+
        			" private String fundCompletedKey;";
        String newKey = key.replace("fund", Utility.toCamelCase(pojo.getSimpleName()));
        newKey = newKey.replace("keyName", pojo.getSimpleName().toLowerCase());
        
        
        System.out.println(newKey);
         
        // File createNewFile = Utility.createNewFile(filePath);
        PrintWriter pr=new PrintWriter(createNewFile);
        pr.write(newKey);
        pr.flush();
        pr.close();
	 
			  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

	public String getRelatedContent(Class<?> pojo) {
		SB content = new SB();
		 Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			//System.out.println("Current relative path is: " + s);	
			String CurrfilePath =s+ "/src/main/resources/ListnerContent.txt";
	        
	        File byIdApi=new File(CurrfilePath);
	        String byIdString = Utility.readFile(byIdApi);
	        byIdString=    byIdString.replace("Funds", English.plural(pojo.getSimpleName()));
	        byIdString=    byIdString.replace("Fund", pojo.getSimpleName());
	        byIdString=   byIdString.replace("fund", Utility.toCamelCase(pojo.getSimpleName()));
	        byIdString=   byIdString.replace("_update",pojo.getSimpleName().toLowerCase()+"_update");
	        byIdString=   byIdString.replace("_create",pojo.getSimpleName().toLowerCase()+"_create");
	        byIdString=   byIdString.replace("_persisted",pojo.getSimpleName().toLowerCase()+"_persisted");
	      //  System.out.println(byIdString);
		return content.str();
	}

}
