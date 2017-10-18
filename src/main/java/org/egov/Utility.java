package org.egov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.validator.annotation.Required;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.google.common.base.CaseFormat;

public class Utility {

	public static final boolean USE_PERSISTENCE_SERVICE = false;
	public static final boolean WRITETOTEMPFILES = false;
	public static String NEWLINE = "\n";
	public static String QUOTE = "\"";
	public static String SINGLEQUOTE = "'";
	public static String TAB = "  ";
	public static String PROJECTHOME = "/home/mani/Workspaces/ms/egov-services/financials/egf-voucher";// only
																								// change
																								// this
																								// rest
																								// is
																								// taken
																								// care
																								// from
																								// this
	public static String PROJECT_WEBHOME = PROJECTHOME + "";// "web"
	public static String SRCFOLDER = PROJECTHOME + "/src/main/java";
	public static String CONTROLLER_FOLDER = PROJECT_WEBHOME + "/src/main/java";
	public static String SQL_FOLDER = PROJECTHOME + "/src/main/resources/db/migration/main";
	public static String YML_FOLDER = PROJECTHOME+"/docs/contract";
	public static String CONTEXT = "/egf-voucher";// change this
	public static String MODULEIDENTIFIER = "egf";// change this
	public static String MODULE_NAME = "Financials Management"; // change this
	public static String SUBMODULE_NAME = "voucher";// change this
	public static String SUBMODULE_IDENTIFIER = "voucher";// change this
	public static String SEARCH_URL = "ajaxsearch";
	public static String BEFORE_SEARCH_URL = "search";

	public static final String WEBPACKAGE = "org.egov." + MODULEIDENTIFIER.toLowerCase()+"." +SUBMODULE_IDENTIFIER+ ".web.controller";
	public static final boolean ADD_VALIDATE = true;
	public static final boolean USEOBJECTINGET = false;//this will add the object in get method if false only property will be printed
	public static final boolean USETENANT = true;
	
	public static SimpleDateFormat flywayVersionFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat flywayVersionFormatMin = new SimpleDateFormat("yyyyMMddHHmm");

	public static void main(String[] args) {
		// System.out.println(toCamelCase("ComplaintTypeRepository"));
		// System.out.println(createUrls("Create-FeeMatrix","/feematrix/create",
		// "true", "1"));
	}

	public static String toCamelCase(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static String toSentenceCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	public static String camelToSpace(String s) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, s).replaceAll("_", " ");
	}

	public static String createUrls(String name, String url, String context, String module, String subModule,
			String isActive, String orderNo) {
		String urlTemplate = "Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,"
				+ "contextroot,application) values(nextval('SEQ_EG_ACTION'),'" + name + "'," + "'" + url
				+ "',(select id from eg_module where name='" + subModule
				+ "' and parentmodule=(select id from eg_module where name='" + module + "' and parentmodule is null)),"
				+ orderNo + ",'" + name + "'," + isActive + ",'" + context + "',"
				+ "(select id from eg_module where name='" + module + "' and parentmodule is null));";

		String urlMap = "Insert into eg_roleaction values((select id from eg_role where name='Super User'),"
				+ "(select id from eg_action where name='" + name + "'));";
		return urlTemplate + "\n" + urlMap + "\n\n";

	}

	public static String createUrls(String name, String url, String isActive, String orderNo) {
		String urlTemplate = "Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,"
				+ "contextroot,application) values(nextval('SEQ_EG_ACTION'),'" + name + "'," + "'" + url
				+ "',(select id from eg_module where name='" + SUBMODULE_NAME
				+ "' and parentmodule=(select id from eg_module where name='" + MODULE_NAME
				+ "' and parentmodule is null))," + orderNo + ",'" + name + "'," + isActive + ",'" + CONTEXT + "',"
				+ "(select id from eg_module where name='" + MODULE_NAME + "' and parentmodule is null));";

		String urlMap = "Insert into eg_roleaction values((select id from eg_role where name='Super User'),"
				+ "(select id from eg_action where name='" + name + "'));";
		return urlTemplate + "\n" + urlMap + "\n\n";

	}

	public static String findTypes(Field f) {
		String egFieldType;
		Class<?> type;
		type = f.getType();

		if (type.getName().contains("Date"))
			egFieldType = "d";
		else if (type.getName().contains("java.util"))
			egFieldType = "ignore";
		else if (type.getName().contains("org.egov"))
			egFieldType = "l";
		else if (type.getName().equals("java.lang.String"))
			egFieldType = "s";
		else if (type.getName().toLowerCase().contains("boolean"))
			egFieldType = "b";
		else if (f.getName().equals("id"))
			egFieldType = "i";
		else if (f.getType().isEnum()) //it was a earlier
			egFieldType = "e";
		else if (type.getName().equals("java.lang.Character"))
			egFieldType = "c";
		else
			egFieldType = "n";
		//System.out.println(egFieldType+"  for "+f.getName());
		return egFieldType;
	}

	/**
	 * 
	 * @param f
	 * @return if it is basic type and not static return true
	 */
	public static boolean isBasicType(Field f) {
		Class<?> type = f.getType();
		if (type.getName().contains("java.lang")) {
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}

	}

	public static boolean isModel(Class<?> type) {
		if (type.getName().contains(".model.")) {
			return true;
		} else if (type.getName().contains(".entity.")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isCollection(Field f) {
		Class<?> type = f.getType();
		if (type.getName().contains("java.util")) {
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public static Class<?> getEnclosingType(Field f) {
		ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
		Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
		return stringListClass;
	}

	public static String getSwaggerType(String name) {
		String data = "";
		if (name == null)
			return data;
		switch (name.toLowerCase()) {

		case "integer":
			data = "integer,int32";
			break;
		case "long":
			data = "integer,int64";
			break;
		case "float":
			data = "number,float";
			break;
		case "double":
			data = "number,double";
			break;
		case "string":
			data = "string,";
			break;
		case "byte":
			data = "string,byte";
			break;
		case "boolean":
			data = "boolean,";
			break;
		case "date":
			data = "string,date";
			break;
		case "date-time":
			data = "string,date-time";
			break;
		case "character":
			data = "string,";
			break;
		case "bigdecimal":
                    data = "number,double";
                    break;

		}

		return data;

	}

	public static boolean isMandatory(Field f) {
		Required required = f.getDeclaredAnnotation(org.egov.infra.persistence.validator.annotation.Required.class);
		NotBlank notblank = f.getDeclaredAnnotation(org.hibernate.validator.constraints.NotBlank.class);
		NotNull notnull = f.getDeclaredAnnotation(javax.validation.constraints.NotNull.class);
		Length length = f.getDeclaredAnnotation(org.hibernate.validator.constraints.Length.class);
		boolean mandatory = false;
		if (required != null || notblank != null || notnull != null) {
			mandatory = true;
		}
		return mandatory;
	}
	
	 

	public static String getDesc(String name, String objectName) {
		
		
		
		String desc = "";
		if (objectName != null) {
			if(objectName.equals("Page"))
				objectName="Search Results";
			switch (name.toLowerCase()) {
			case "id":
				desc = "Unique Identifier of the " + objectName;
				break;
			case "phone":
				desc = "Phone number of the " + objectName;
				break;
			case "fax":
				desc = "Fax number of the " + objectName;
				break;
			case "micr":
				desc = "MICR Code of the " + objectName;
				break;
			case "active":
				desc = "Whether " + objectName + " is Active or not. " + "If the value is TRUE, then " + objectName
						+ " is active,If the value is FALSE then " + objectName + " is inactive,Default value is TRUE";
				break;
				
			case "offset":
				desc = "page number of the "+objectName+", Default value is 0";
				break;
			case "pagesize":
				desc = "Number of records in a per page in  the "+objectName+", Default value is 20";
				break;
			default:
				desc = camelToSpace(name) + " of the " + objectName;
			}
		} else {
			switch (name.toLowerCase()) {
			default:
				desc = camelToSpace(name);
			}
		}
		return desc;
	}

	public static String readFile(File srcFile) {
		String fileAsString=null;
		try {
			InputStream is = new FileInputStream(srcFile);
			BufferedReader buf = new BufferedReader(new InputStreamReader(is));
			String line = buf.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
			is.close();
			fileAsString = sb.toString();
			//System.out.println("Contents : " + fileAsString);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileAsString;

	}

	public static File createNewFile(String contractFileName) {
		try {
			if(contractFileName==null)
				return null;
			String dirName = contractFileName.substring(0,contractFileName.lastIndexOf("/")+1);
			File dir=new File(dirName);
			if(!dir.exists())
				dir.mkdirs();
			File f=new File(contractFileName);
					 
				if(!f.exists())
				{
					f.createNewFile();
				}
				return f;
		} catch (IOException e) {
			 throw new RuntimeException(e.getMessage());
		}
				 
	}
	
	public static Map<String,String> readComments(String fullyQualifiedName)
	{
		Map<String,String> coments=new HashMap<>();
		List<String> fields=new ArrayList<String>();
		File f=new File(Utility.SRCFOLDER+"/"+fullyQualifiedName.replace(".", "/")+".java");
		String fileContents = Utility.readFile(f);
		PojoHolder pojoHolder=new PojoHolder();
		pojoHolder.loadPojo(fullyQualifiedName);
		Field[] declaredFields = pojoHolder.getPojo().getDeclaredFields();
		
		
			
		
		 String slComment = "//[^\r\n]*";
		    String mlComment = "/\\*[\\s\\S]*?\\*/";
		    String strLit = "\"(?:\\\\.|[^\\\\\"\r\n])*\"";
		    String chLit = "'(?:\\\\.|[^\\\\'\r\n])+'";
		    String any = "[\\s\\S]";

		    java.util.regex.Pattern p = java.util.regex.Pattern.compile(String.format("(%s)",  mlComment)
		    );

		    Matcher m = p.matcher(fileContents);

		    while(m.find()) {
		      String hit = m.group();
		      if(hit != null) {
		    	  hit=hit.replace("*", "").replace("/", "");
		    	 hit= hit.trim();
		    	 
		    	 if(hit.startsWith( pojoHolder.getPojo().getSimpleName()))
	    		  {
	    			  coments.put(pojoHolder.getPojo().getSimpleName(), hit);
	    			  //break;
	    		  }
		    	  for(Field field:declaredFields)
		  		{
		    		  if(hit.startsWith(field.getName()))
		    		  {
		    			  coments.put(field.getName(), hit);
		    			  break;
		    		  }
		  		}
		       
		      }
		      
		    }
		    return coments;
	}



public static Map<String,String> readCommentsByFullpath(String fullpath,String fullyQualifiedName)
{
	Map<String,String> coments=new HashMap<>();
	List<String> fields=new ArrayList<String>();
	File f=new File(fullpath+".java");
	String fileContents = Utility.readFile(f);
	PojoHolder pojoHolder=new PojoHolder();
	pojoHolder.loadPojo(fullyQualifiedName);
	Field[] declaredFields = pojoHolder.getPojo().getDeclaredFields();
	
	
		
	
	 	String slComment = "//[^\r\n]*";
	    String mlComment = "/\\*[\\s\\S]*?\\*/";
	    String strLit = "\"(?:\\\\.|[^\\\\\"\r\n])*\"";
	    String chLit = "'(?:\\\\.|[^\\\\'\r\n])+'";
	    String any = "[\\s\\S]";

	    java.util.regex.Pattern p = java.util.regex.Pattern.compile(String.format("(%s)",  mlComment)
	    );

	    Matcher m = p.matcher(fileContents);

	    while(m.find()) {
	      String hit = m.group();
	      if(hit != null) {
	    	  hit=hit.replace("*", "").replace("/", "");
	    	  hit= hit.trim();
	    	  for(Field field:declaredFields)
	  		{
	    		  if(hit.startsWith(field.getName()))
	    		  {
	    			  System.out.println(""+field.getName());
	    			  coments.put(field.getName(), hit);
	    			  break;
	    		  }
	  		}
	       
	      }
	      
	    }
	    return coments;
}


public static String removeJaDependency(String contractContent) {
	 contractContent=contractContent.replaceAll("org.ja.annotation.*?\n", "");
	 contractContent=contractContent.replaceAll("@DrillDown.*?\n", "");
	 contractContent=contractContent.replaceAll("@DrillDownTable.*?\n", "");
	return contractContent;
}

}

