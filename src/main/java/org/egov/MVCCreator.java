package org.egov;

public class MVCCreator {

	public static void main(String[] args) {
		String fullyQualifiedName = "org.egov.model.budget.BudgetCheckConfig";// default
																					// Either
																					// Ucan
																					// change
																					// this
																					// or
																					// pass
																					// as
																					// arguement

		if (args != null && args.length >= 1 && args[0] != null)
			fullyQualifiedName = args[0];
		// ddl creator is only for new projects
	/*	 DdlCreator ddl=new DdlCreator();
		 ddl.createDdl(fullyQualifiedName);
	

		RepositoryCreator rep = new RepositoryCreator();
		rep.createRepository(fullyQualifiedName); 	

		ServiceCreator sc = new ServiceCreator();
		sc.createService(fullyQualifiedName);

		ControllerCreator cc = new ControllerCreator();
		cc.createController(fullyQualifiedName);*/

		JspCreator jc = new JspCreator();
		jc.createJSP(fullyQualifiedName);

		SearchAdaptorAndJSCreator saaj = new SearchAdaptorAndJSCreator();
		saaj.create(fullyQualifiedName);

	}

}
