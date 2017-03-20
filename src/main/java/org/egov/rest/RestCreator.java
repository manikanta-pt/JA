package org.egov.rest;

import org.egov.DdlCreator;
import org.egov.RepositoryCreator;
import org.egov.ReqResponseCreator;
import org.egov.ServiceCreator;
import org.egov.SpecificationCreator;
import org.egov.Utility;
import org.egov.Yamlcreator;
import org.egov.YamlcreatorGetByPost;

public class RestCreator {
	
	public static void main(String [] args)
	{
		//System.err.println(Utility.MODULE_NAME);
		System.out.println("For all error it is better to fix and regenerate ");
		/*create("org.egov.workflow.web.contract.Task");
		create("org.egov.workflow.web.contract.ProcessInstance");*/
		create("org.egov.egf.persistence.entity.BankBranch");
		create("org.egov.egf.persistence.entity.BankAccount");
		create("org.egov.egf.persistence.entity.FinancialYear");
		create("org.egov.egf.persistence.entity.FiscalPeriod");
		create("org.egov.egf.persistence.entity.Function");
		create("org.egov.egf.persistence.entity.Functionary");
		create("org.egov.egf.persistence.entity.Fund");
		create("org.egov.egf.persistence.entity.Fundsource");
		create("org.egov.egf.persistence.entity.Scheme");
		create("org.egov.egf.persistence.entity.SubScheme");
		create("org.egov.egf.persistence.entity.Supplier");
		//create("org.egov.egf.persistence.entity.AccountDetailType");
		//create("org.egov.egf.persistence.entity.AccountDetailKey");
		create("org.egov.egf.persistence.entity.AccountEntity");
		create("org.egov.egf.persistence.entity.AccountCodePurpose");
		create("org.egov.egf.persistence.entity.ChartOfAccount");
		create("org.egov.egf.persistence.entity.ChartOfAccountDetail");
		create("org.egov.egf.persistence.entity.BudgetGroup");
		//create("org.egov.egf.persistence.entity.ChartOfAccountDetail");
		//create("org.egov.egf.persistence.entity.VoucherHeader");
		 
		 
		
	}
	
  public static void  create(String fullyQualifiedName)
	{
	  YamlcreatorGetByPost ymlc=new YamlcreatorGetByPost();
	  ymlc.create(fullyQualifiedName);
	
	  /*  
	  ReqResponseCreator reqRes=new ReqResponseCreator();
	  reqRes.create(fullyQualifiedName);
	  */
	 /* SpecificationCreator sp=new SpecificationCreator();
	  sp.create(fullyQualifiedName);
	  
	  RepositoryCreator rep=new RepositoryCreator();
	  rep.create(fullyQualifiedName);*/
	  
	 /* ServiceCreator ser=new ServiceCreator();
	  ser.create(fullyQualifiedName);*/
  
	 /* RestControllerCreator restController=new RestControllerCreator();
	  restController.create(fullyQualifiedName);  */
	  
	/*  DdlCreator ddl=new DdlCreator();
	  ddl.createDdl(fullyQualifiedName);
	*/
	  
	}

}
