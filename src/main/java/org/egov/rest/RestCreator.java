package org.egov.rest;

import org.egov.ReqResponseCreator;
import org.egov.RestDdlCreator;
import org.egov.RestJdbcRepositoryCreator;
import org.egov.RestRepositoryCreator;
import org.egov.RestServiceCreator;
import org.egov.YamlcreatorGetByPost;
import org.egov.YamlcreatorGetByPostUpdate;

public class RestCreator {

	public static void main(String[] args) {
		// System.err.println(Utility.MODULE_NAME);
		System.out.println("For all error it is better to fix and regenerate ");
		/*create("org.egov.egf.bill.domain.model.BillRegister");
		create("org.egov.egf.bill.domain.model.Checklist");
		 */
		 
	
		
		
		
		create("org.egov.egf.voucher.domain.model.Voucher");
		
		
	 
		/*create("org.egov.egf.master.domain.model.Fund");
		create("org.egov.egf.master.domain.model.FinancialYear");
		create("org.egov.egf.master.domain.model.FiscalPeriod");
		create("org.egov.egf.master.domain.model.Function");
		create("org.egov.egf.master.domain.model.Functionary");
		create("org.egov.egf.master.domain.model.Fundsource");
		create("org.egov.egf.master.domain.model.Scheme");
		create("org.egov.egf.master.domain.model.Bank");
		create("org.egov.egf.master.domain.model.BankBranch");
		create("org.egov.egf.master.domain.model.BankAccount");
		create("org.egov.egf.master.domain.model.SubScheme");
		create("org.egov.egf.master.domain.model.Supplier");
		create("org.egov.egf.master.domain.model.AccountDetailType");
		create("org.egov.egf.master.domain.model.AccountDetailKey");
		create("org.egov.egf.master.domain.model.AccountEntity");
		create("org.egov.egf.master.domain.model.AccountCodePurpose");
		create("org.egov.egf.master.domain.model.ChartOfAccount");
		create("org.egov.egf.master.domain.model.ChartOfAccountDetail");
		create("org.egov.egf.master.domain.model.BudgetGroup");
		create("org.egov.egf.master.domain.model.FinancialStatus");
		create("org.egov.egf.master.domain.model.FinancialConfiguration"); */
		
		

		System.out.println("creating modulewise yaml");

		ModulewiseYamlCreator mv=new ModulewiseYamlCreator();
		mv.create();
		

	}

	public static void create(String fullyQualifiedName) {
		YamlcreatorGetByPostUpdate ymlc = new YamlcreatorGetByPostUpdate();
		ymlc.create(fullyQualifiedName);
	
		/*ReqResponseCreator reqRes = new ReqResponseCreator();
		reqRes.create(fullyQualifiedName);

		RestControllerCreator restController = new RestControllerCreator();
		restController.create(fullyQualifiedName);

		RestRepositoryCreator rep = new RestRepositoryCreator();
		rep.create(fullyQualifiedName);

		RestServiceCreator ser = new RestServiceCreator();
		ser.create(fullyQualifiedName);

		RestJdbcRepositoryCreator jdbc = new RestJdbcRepositoryCreator();
		jdbc.create(fullyQualifiedName);


		RestDdlCreator ddl = new RestDdlCreator();
		ddl.createDdl(fullyQualifiedName);*/
		
		  /*SpecificationCreator sp=new SpecificationCreator();
		  sp.create(fullyQualifiedName);*/

	}

}
