package org.egov;

public class Utility {
	
	public static final String WEBPACKAGE = "org.egov.tl.web.controller";
	public static final boolean USE_PERSISTENCE_SERVICE = true;
	public static final boolean WRITETOTEMPFILES = false;
	public static String NEWLINE="\n";
	public static String TAB="\t";
	public static String PROJECTHOME="/home/mani/Workspaces/github_phoenix/eGov/egov/egov-tl";//only change this rest is taken care from this
	public static String PROJECT_WEBHOME=PROJECTHOME+"web";
	public static String SRCFOLDER=PROJECTHOME+"/src/main/java";
	public static String CONTROLLER_FOLDER=PROJECT_WEBHOME+"/src/main/java";
	public static String SQL_FOLDER=PROJECTHOME+"/src/main/resources/db/migration/main";
	public static String CONTEXT="tl";
	public static String MODULE_NAME="Trade License";
	public static String SUBMODULE_NAME="Transactions";
	
	
	
	public static void main(String[] args) {
		System.out.println(toCamelCase("ComplaintTypeRepository"));
		System.out.println(createUrls("Create-FeeMatrix","/feematrix/create", "true", "1"));
	}
	public static String toCamelCase(String s)
	{
		return s.substring(0,1).toLowerCase()+s.substring(1);
	}
	
	public static String createUrls(String name,String url,String context,String module,String subModule,String isActive,String orderNo) {
		String urlTemplate="Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,"
				+ "contextroot,application) values(nextval('SEQ_EG_ACTION'),'"+name+"',"
				+ "'"+url+"',(select id from eg_module where name='"+subModule+"'),"
				+ orderNo+",'"+name+"',"+isActive+",'"+context+"',"
				+ "(select id from eg_module where name='"+module+"' and parentmodule is null));";
		
		String urlMap="Insert into eg_roleaction values((select id from eg_role where name='Super User'),"
				+ "(select id from eg_action where name='"+name+"'));";
		return urlTemplate+"\n"+urlMap+"\n\n";
		
	}
	
	public static String createUrls(String name,String url,String isActive,String orderNo) {
		String urlTemplate="Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,"
				+ "contextroot,application) values(nextval('SEQ_EG_ACTION'),'"+name+"',"
				+ "'"+url+"',(select id from eg_module where name='"+SUBMODULE_NAME+"'),"
				+ orderNo+",'"+name+"',"+isActive+",'"+CONTEXT+"',"
				+ "(select id from eg_module where name='"+MODULE_NAME+"' and parentmodule is null));";
		
		String urlMap="Insert into eg_roleaction values((select id from eg_role where name='Super User'),"
				+ "(select id from eg_action where name='"+name+"'));";
		return urlTemplate+"\n"+urlMap+"\n\n";
		
	}


}
