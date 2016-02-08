package org.egov;

public class ModuleWiseGenerator {

	public static void main(String[] args) {
   MVCCreator mv=new MVCCreator();
   mv.main(new String[]{"org.egov.commons.CF"});
   mv.main(new String[]{"org.egov.commons.Accountdetailtype"});
   mv.main(new String[]{"org.egov.commons.CChartOfAccountDetail"});
   mv.main(new String[]{"org.egov.commons.CChartOfAccounts"});
  
	}

}
