package org.egov;

import java.lang.reflect.Field;

public class Utility {

	public static final boolean USE_PERSISTENCE_SERVICE = false;
	public static final boolean WRITETOTEMPFILES = false;
	public static String NEWLINE="\n";
	public static String QUOTE="\"";
	public static String TAB="\t";
	public static String PROJECTHOME="/home/mani/Workspaces/github_phoenix/eGov/egov/egov-egf";//only change this rest is taken care from this
	public static String PROJECT_WEBHOME=PROJECTHOME+"web";
	public static String SRCFOLDER=PROJECTHOME+"/src/main/java";
	public static String CONTROLLER_FOLDER=PROJECT_WEBHOME+"/src/main/java";
	public static String SQL_FOLDER=PROJECTHOME+"/src/main/resources/db/migration/main";
	public static String CONTEXT="EGF";//change this 

	public static String MODULE_NAME="EGF"; //change this 
	public static String SUBMODULE_NAME="Masters";//change this
	public static String SEARCH_URL="ajaxsearch";
	public static String BEFORE_SEARCH_URL="search";
	
	public static final String WEBPACKAGE = "org.egov."+CONTEXT.toLowerCase()+".web.controller";



	public static void main(String[] args) {
		//System.out.println(toCamelCase("ComplaintTypeRepository"));
		//System.out.println(createUrls("Create-FeeMatrix","/feematrix/create", "true", "1"));
	}
	public static String toCamelCase(String s)
	{
		return s.substring(0,1).toLowerCase()+s.substring(1);
	}
	public static String toSentenceCase(String s)
	{
		return s.substring(0,1).toUpperCase()+s.substring(1);
	}

	public static String createUrls(String name,String url,String context,String module,String subModule,String isActive,String orderNo) {
		String urlTemplate="Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,"
				+ "contextroot,application) values(nextval('SEQ_EG_ACTION'),'"+name+"',"
				+ "'"+url+"',(select id from eg_module where name='"+subModule+"' and parentmodule=(select id from eg_module where name='"+module+"' and parentmodule is null)),"
				+ orderNo+",'"+name+"',"+isActive+",'"+context+"',"
				+ "(select id from eg_module where name='"+module+"' and parentmodule is null));";

		String urlMap="Insert into eg_roleaction values((select id from eg_role where name='Super User'),"
				+ "(select id from eg_action where name='"+name+"'));";
		return urlTemplate+"\n"+urlMap+"\n\n";

	}

	public static String createUrls(String name,String url,String isActive,String orderNo) {
		String urlTemplate="Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,"
				+ "contextroot,application) values(nextval('SEQ_EG_ACTION'),'"+name+"',"
				+ "'"+url+"',(select id from eg_module where name='"+SUBMODULE_NAME+"' and parentmodule=(select id from eg_module where name='"+MODULE_NAME+"' and parentmodule is null)),"
				+ orderNo+",'"+name+"',"+isActive+",'"+CONTEXT+"',"
				+ "(select id from eg_module where name='"+MODULE_NAME+"' and parentmodule is null));";

		String urlMap="Insert into eg_roleaction values((select id from eg_role where name='Super User'),"
				+ "(select id from eg_action where name='"+name+"'));";
		return urlTemplate+"\n"+urlMap+"\n\n";

	}

	public static String findTypes(Field f) {
		String egFieldType;
		Class<?> type;
		type = f.getType();
		if(type.getName().contains("Date"))
			egFieldType="d";
		else if(type.getName().contains("java.util"))
			egFieldType="ignore";
		else if(type.getName().contains("org.egov"))
			egFieldType="l";
		else if(type.getName().equals("java.lang.String"))
			egFieldType="s";
		else if(type.getName().toLowerCase().contains("boolean") )
			egFieldType="b";
		else if(f.getName().equals("id") )
			egFieldType="i";
		else
			egFieldType="n";
		return egFieldType;
	}

	/**
	 * 
	 * @param f
	 * @return
	 *   if it is basic type and not static return true
	 */
	public static boolean isBasicType(Field f) {
		Class<?> type = f.getType();
		if(type.getName().contains("java.lang"))
		{
			if(java.lang.reflect.Modifier.isStatic(f.getModifiers()))
			{
				return false;
			}else
			{
				return true;
			}
		}else
		{
			return false;
		}

	}
	public static boolean isModel(Class<?> type) {
		if(type.getName().contains(".model."))
		{
			return true;
		}else if(type.getName().contains(".entity."))
		{
			return true;
		}else 
		{
			return false;
		}
	}
	public static boolean isCollection(Field f) {
		Class<?> type = f.getType();
		if(type.getName().contains("java.util"))
		{
			if(java.lang.reflect.Modifier.isStatic(f.getModifiers()))
			{
				return false;
			}else
			{
				return true;
			}
		}else
		{
			return false;
		}
	}
	


}
