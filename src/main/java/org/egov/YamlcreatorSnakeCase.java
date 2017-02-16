package org.egov;

import static org.egov.Utility.NEWLINE;
import static org.egov.Utility.TAB;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import com.google.common.base.CaseFormat;

public class YamlcreatorSnakeCase {

	
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_PUT = "PUT";
	PojoHolder pojoHolder=new PojoHolder();
	
	public static void main(String[] args) {
		YamlcreatorSnakeCase rc=new YamlcreatorSnakeCase();
		rc.create("org.egov.egf.entity.BankBranch");

	}
	
	public void create(String fullyQualifiedName)    
	{
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		PrintWriter sqlWriter;
		try {
			sqlWriter = new PrintWriter(Utility.YML_FOLDER+"/"+pojo.getSimpleName()+".yaml", "UTF-8");
			StringBuilder crud=new StringBuilder();
			StringBuilder info=new StringBuilder();
		    crud.append(createCRUD(fullyQualifiedName));
			String objectDefinition = getObjectDefinition(fullyQualifiedName);
			
			  sqlWriter.write(crud.toString()+objectDefinition);
			  sqlWriter.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//StringBuilder main=new StringBuilder();
		
		
	}

	private String getObjectDefinition(String fullyQualifiedName) {
		int tabCount=0;
		SB objectDef=new SB();
		try {
			Class<?> pojo = pojoHolder.getPojo();
			Field[] declaredFields = pojo.getDeclaredFields();
			
			
			 String objectSnakecase =CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,  pojo.getSimpleName());
			 String objectSnakecaseReq=objectSnakecase+"_request";
			
			
			objectDef.a("definitions:").a(NEWLINE);
			tabCount=tabCount+1;
			objectDef.a(tabCount);
			objectDef.a(objectSnakecase).a(":").a(NEWLINE);
			tabCount=tabCount+1;
			objectDef.a(tabCount).a("type: object").a(NEWLINE);
			objectDef.a(tabCount).a("properties:").a(NEWLINE);
			
		    tabCount=tabCount+1;
			
		 
			for(Field f:declaredFields)
			{
				String name = f.getName();
				if(name.equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}
				String snakeCaseName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getName());
				objectDef.a(tabCount);
				objectDef.a(snakeCaseName).a(":").a(NEWLINE);
				if(!Utility.findTypes(f).equals("l"))
				{
			 
				String swaggerType = Utility.getSwaggerType(f.getType().getSimpleName());
				objectDef.a(tabCount+1);
				objectDef.a("type:").a(TAB).a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length==2)
				{
				objectDef.a(tabCount+1);
				objectDef.a("format:").a(TAB).a(swaggerType.split(",")[1]).a(NEWLINE);
				}
				objectDef.a(tabCount+1);
				objectDef.a("description:").a(TAB).a("describe "+snakeCaseName);
				}else
				{
					objectDef.a(tabCount+1);
					objectDef.a("type:").a(TAB).a("object").a(NEWLINE);	
					objectDef.a(tabCount+1);
					objectDef.a("description:").a(TAB).a("describe "+snakeCaseName).a(NEWLINE);
					objectDef.a(tabCount+1);
					objectDef.a(" items:").a(NEWLINE);
					objectDef.a(tabCount+1);
					objectDef.a(TAB).a(" $ref: \"http://localhost:8081/swagger-yaml/financials.yaml/#/definitions/"+snakeCaseName+"\"");
				}
			
				objectDef.a(NEWLINE);
				
			}
			
		
			objectDef.a(getReq_RespDef(pojo));
			
			//sqlWriter.write(seq.toString());
			
			
		
		System.out.println(objectDef.str());	
		 
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return objectDef.str();
		
	}

	private String getReq_RespDef(Class<?> pojo) {
		SB ss=new SB();
		 String objectSnakecase =CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,  pojo.getSimpleName());
		int tabCount=1;
		ss.a(tabCount);
		ss.a(objectSnakecase+"_request:").a(NEWLINE);
		tabCount++;
		
		ss.a(tabCount).a("properties:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("request_info:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/request_info'").a(NEWLINE);
		tabCount=tabCount-1;
		ss.a(tabCount).a(objectSnakecase+":").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/"+objectSnakecase+"'").a(NEWLINE);
		//return ss.str();
		
		//response
		tabCount=1;
		ss.a(tabCount);
		ss.a(objectSnakecase+"_response:").a(NEWLINE);
		tabCount++;
		
		ss.a(tabCount).a("properties:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("response_info:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/response_info'").a(NEWLINE);
		tabCount=tabCount-1;
		ss.a(tabCount).a(objectSnakecase+":").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("type:"+TAB+"array").a(NEWLINE);
		ss.a(tabCount).a("items:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/"+objectSnakecase+"'").a(NEWLINE);
		return ss.str();
		
		
		
	}

	private String createCRUD(String fullyQualifiedName) {
		StringBuffer json=new StringBuffer();
		json.append(getGetMethod(fullyQualifiedName));	
		json.append(getPostMethod(fullyQualifiedName));	
		json.append(getPutMethod(fullyQualifiedName));	
		return json.toString();
	}

	private String getGetMethod(String fullyQualifiedName) {
		SB jsn=new SB();
		int tabCount=0;
		  String GET_DESCRIPTION = "";
		 String REQ_INFO = "request_info";
		  String GET_SUMMARY="Get the list of ";
			try {
				 
				Class<?> pojo = pojoHolder.getPojo();
				 String objectSnakecase =CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,  pojo.getSimpleName());
				Field[] declaredFields = pojo.getDeclaredFields();
				tabCount=tabCount+1;
				jsn.a(tabCount);
				String uriName = pojo.getSimpleName().toLowerCase()+"s";
				jsn.a("/").a(uriName).a(":").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("get:").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("summary: ").a(GET_SUMMARY).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("description: ").a(GET_DESCRIPTION).a(uriName).a(NEWLINE);
				
				jsn.a(tabCount).a("parameters:").a(NEWLINE);
				tabCount=tabCount+1;
				PojoHolder reqPojoHolder=new PojoHolder();
				reqPojoHolder.loadPojo("org.egov.boundary.web.wrapper.RequestInfo");//change this
				Class<?> requestPojo = reqPojoHolder.getPojo();
				Field[] reqDeclaredFields = requestPojo.getDeclaredFields();
				 String reqSnakecase =CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,  requestPojo.getSimpleName());
				for(Field f:reqDeclaredFields)
				{
					String name = f.getName();
					if(name.equals("serialVersionUID"))
						continue;
					if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
					{
						continue;
					}
				String snakeCaseName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getName());
					
				if(!Utility.findTypes(f).equals("l"))	
				{
				jsn.a(tabCount).a("-"+TAB+"name:").a(5).a(reqSnakecase).a(".").a(snakeCaseName).a(NEWLINE);
				String swaggerType = Utility.getSwaggerType(f.getType().getSimpleName());
				jsn.a(tabCount).a(TAB).a(TAB).a("type: ").a(5).a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length>1)
				{
					jsn.a(tabCount).a(TAB).a(TAB).a("format: ").a(5).a(swaggerType.split(",")[1]).a(NEWLINE);	
				}
				}
				else
				{
				jsn.a(tabCount).a("-"+TAB+TAB+"name:").a(5).a(reqSnakecase).a(".").a(snakeCaseName).a("id").a(NEWLINE);
				String swaggerType = Utility.getSwaggerType("long");
				jsn.a(tabCount).a(TAB).a(TAB).a("type: ").a(5).a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length>1)
				{
					jsn.a(tabCount).a(TAB).a(TAB).a("format: ").a(5).a(swaggerType.split(",")[1]).a(NEWLINE);	
				}
				}
				
				jsn.a(tabCount).a(TAB).a(TAB).a("in:").a(5).a("query").a(NEWLINE);
				
				jsn.a(tabCount).a(TAB).a(TAB).a("description:").a(5).a("common request_info").a(NEWLINE);
			    if(Utility.isMandatory(f))
				jsn.a(tabCount).a(TAB).a(TAB).a("required:").a(5).a("true").a(NEWLINE);
			    else
			    jsn.a(tabCount).a(TAB).a(TAB).a("required:").a(5).a("false").a(NEWLINE);	
				 
				}
				
				for(Field f:declaredFields)
				{
					String name = f.getName();
					if(name.equals("serialVersionUID"))
						continue;
					if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
					{
						continue;
					}
					String snakeCaseName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getName());
					
				if(!Utility.findTypes(f).equals("l"))	
				{
				jsn.a(tabCount).a("-"+TAB+"name:").a(TAB).a(objectSnakecase).a(".").a(snakeCaseName).a(NEWLINE);
				String swaggerType = Utility.getSwaggerType(f.getType().getSimpleName());
				jsn.a(tabCount).a(TAB).a(TAB).a("type: ").a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length>1)
				{
					jsn.a(tabCount).a(TAB).a(TAB).a("format: ").a(swaggerType.split(",")[1]).a(NEWLINE);	
				}
				}
				else
				{
				jsn.a(tabCount).a("-"+TAB+TAB+"name:").a(objectSnakecase).a(".").a(snakeCaseName).a("id").a(NEWLINE);
				String swaggerType = Utility.getSwaggerType("long");
				jsn.a(tabCount).a(TAB).a(TAB).a("type: ").a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length>1)
				{
					jsn.a(tabCount).a(TAB).a(TAB).a("format: ").a(swaggerType.split(",")[1]).a(NEWLINE);	
				}
				}
				
				jsn.a(tabCount).a(TAB).a(TAB).a("in: query").a(NEWLINE);
				
				jsn.a(tabCount).a(TAB).a(TAB).a("description: common request_info").a(NEWLINE);
			    if(Utility.isMandatory(f))
				jsn.a(tabCount).a(TAB).a(TAB).a("required: true").a(NEWLINE);
			    else
			    jsn.a(tabCount).a(TAB).a(TAB).a("required: false").a(NEWLINE);	
				 
				} 
			String res=	getResponse(tabCount, objectSnakecase,METHOD_GET);
			jsn.a(res);
				
			 
				
				
				
				
			} catch (Exception e) {
				 e.printStackTrace();
			}
	
	
	return jsn.str();
	}

	private String getResponse( int tabCount, String objectSnakecase,String method) {
		SB jsn=new SB();
		tabCount=tabCount-1;
		jsn.a(tabCount).a("responses:").a(NEWLINE);
		tabCount=tabCount+1;
		if(method.equalsIgnoreCase(METHOD_POST))
		{
			jsn.a(tabCount).a("201:").a(NEWLINE);
			tabCount=tabCount+1;
			jsn.a(tabCount).a("description:"+TAB+objectSnakecase+" created Successfully").a(NEWLINE);
		}else if(method.equalsIgnoreCase(METHOD_PUT))
		{
		jsn.a(tabCount).a("200:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("description:"+TAB+objectSnakecase+" update Successfully").a(NEWLINE);
		
		}
		else
		{
			jsn.a(tabCount).a("200:").a(NEWLINE);
			tabCount=tabCount+1;
			jsn.a(tabCount).a("description:"+TAB+objectSnakecase+" retrieved Successfully").a(NEWLINE);
		}
		jsn.a(tabCount).a("schema:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("$ref: '#/definitions/"+objectSnakecase+"_response'").a(NEWLINE);
		tabCount=tabCount-2;
		jsn.a(tabCount).a("400:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("description:"+TAB+"Invalid Input").a(NEWLINE);
		//tabCount=tabCount+1;
		jsn.a(tabCount).a("schema:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("$ref: '#/definitions/"+"error_response'").a(NEWLINE);
		tabCount=tabCount-2;
		jsn.a(tabCount).a("404:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("description:"+TAB+objectSnakecase+ "Not Found").a(NEWLINE);
		return jsn.str();
	}
	
	
	private String getPostMethod(String fullyQualifiedName) {
		SB jsn=new SB();
		int tabCount=0;
		  String GET_DESCRIPTION = "";
		 String REQ_INFO = "request_info";
		  String GET_SUMMARY="Get the list of ";
			try {
				 
				Class<?> pojo = pojoHolder.getPojo();
				 String objectSnakecase =CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,  pojo.getSimpleName());
				 String objectSnakecaseReq=objectSnakecase+"_request";
				Field[] declaredFields = pojo.getDeclaredFields();
				tabCount=tabCount+1;
				//jsn.a(tabCount);
				String uriName = pojo.getSimpleName().toLowerCase()+"s";
				//jsn.a("/").a(uriName).a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("post:").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("summary: ").a(GET_SUMMARY).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("description: ").a(GET_DESCRIPTION).a(uriName).a(NEWLINE);
				//tabCount=tabCount+1;
				jsn.a(tabCount).a("parameters:").a(NEWLINE);
				tabCount=tabCount+1;
				
				jsn.a(tabCount).a("-"+TAB+"name:"+TAB).a(objectSnakecaseReq).a(NEWLINE);
				jsn.a(tabCount).a(TAB).a(TAB).a("in: body").a(NEWLINE);
				jsn.a(tabCount).a(TAB).a(TAB).a("description: common request_info").a(NEWLINE);
				jsn.a(tabCount).a(TAB).a(TAB).a("schema:").a(NEWLINE);
				jsn.a(tabCount).a(TAB).a(TAB).a(TAB).a("$ref: '#/definitions/"+objectSnakecaseReq+"'").a(NEWLINE);
				String res=	getResponse(tabCount, objectSnakecase,METHOD_POST);
				jsn.a(res);
				
			} catch (Exception e) {
				 e.printStackTrace();
			}
	
	
	return jsn.str();
	}
	
	
	private String getPutMethod(String fullyQualifiedName) {
		SB jsn=new SB();
		int tabCount=0;
		  String GET_DESCRIPTION = "";
		 String REQ_INFO = "request_info";
		  String GET_SUMMARY="Get the list of ";
			try {
				 
				Class<?> pojo = pojoHolder.getPojo();
				 String objectSnakecase =CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,  pojo.getSimpleName());
				 String objectSnakecaseReq=objectSnakecase+"_request";
				Field[] declaredFields = pojo.getDeclaredFields();
				//jsn.a(NEWLINE);
				tabCount=tabCount+1;
				//jsn.a(tabCount);
				String uriName = pojo.getSimpleName().toLowerCase()+"s";
				//jsn.a("/").a(uriName).a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("put:").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("summary: ").a(GET_SUMMARY).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("description: ").a(GET_DESCRIPTION).a(uriName).a(NEWLINE);
				//tabCount=tabCount+1;
				jsn.a(tabCount).a("parameters:").a(NEWLINE);
				tabCount=tabCount+1;
				
				jsn.a(tabCount).a("-"+TAB+"name:"+TAB).a(objectSnakecaseReq).a(NEWLINE);
				jsn.a(tabCount).a(TAB).a(TAB).a("in: body").a(NEWLINE);
				jsn.a(tabCount).a(TAB).a(TAB).a("description: common request_info").a(NEWLINE);
				jsn.a(tabCount).a(TAB).a(TAB).a("schema:").a(NEWLINE);
				jsn.a(tabCount).a(TAB).a(TAB).a(TAB).a("$ref: '#/definitions/"+objectSnakecaseReq+"'").a(NEWLINE);
				String res=	getResponse(tabCount, objectSnakecase,METHOD_PUT);
				jsn.a(res);
				
			} catch (Exception e) {
				 e.printStackTrace();
			}
	
	
	return jsn.str();
	}
	 

}

