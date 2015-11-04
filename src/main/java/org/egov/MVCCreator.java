package org.egov;

public class MVCCreator {


	public static void main(String[] args) {
		String fullyQualifiedName="org.egov.tl.domain.entity.FeeMatrix";//default Either Ucan change this or pass as arguement
		
		
		if(args!=null && args.length>1 && args[1]!=null)
			fullyQualifiedName=args[1];
		
		RepositoryCreator rep=new RepositoryCreator();
		rep.createRepository(fullyQualifiedName);
	
		ServiceCreator sc=new ServiceCreator();
		sc.createService(fullyQualifiedName);
		
		ControllerCreator cc=new ControllerCreator();
		cc.createController(fullyQualifiedName);
		
		JspCreator jc=new JspCreator();
		jc.createJSP(fullyQualifiedName);
		
		
		
	}

}
