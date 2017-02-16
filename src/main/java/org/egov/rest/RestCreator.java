package org.egov.rest;

import org.egov.Yamlcreator;

public class RestCreator {
	
	public static void main(String [] args)
	{
	
		create("org.egov.egf.persistence.entity.Bank");
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
		create("org.egov.egf.persistence.entity.AccountDetailType");
		create("org.egov.egf.persistence.entity.AccountDetailKey");
		create("org.egov.egf.persistence.entity.AccountEntity");
		 
		
	}
	
  public static void  create(String fullyQualifiedName)
	{
	  Yamlcreator ymlc=new Yamlcreator();
		ymlc.create(fullyQualifiedName);
	}

}
