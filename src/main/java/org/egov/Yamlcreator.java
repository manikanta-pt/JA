package org.egov;

import static org.egov.Utility.NEWLINE;
import static org.egov.Utility.TAB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.atteo.evo.inflector.English;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.ja.annotation.DrillDown;
import org.ja.annotation.DrillDownTable;
import org.ja.annotation.Ignore;

public class Yamlcreator {

	
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_PUT = "PUT";
	private static final int SPACE = 1;
	private static final String PUT_SUMMARY = "Update any of the ";
	private static final String PUT_DESCRIPTION = "Update any of the ";
	private static final String POST_DESCRIPTION = "Create  new ";
	private static final String POST_SUMMARY = "Create  new  ";
	PojoHolder pojoHolder=new PojoHolder();
    private List<String> referencedDetailedObjects=new ArrayList<String>();
	
	public static void main(String[] args) {
		Yamlcreator rc=new Yamlcreator();
		if(args!=null && args.length>0)
		{
			rc.create(args[0]);
		}
		else
		rc.create("org.egov.egf.persistence.entity.Bank");

	}
	
	public void create(String fullyQualifiedName)    
	{
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		PrintWriter classFileWriter;
		PrintWriter modulePathsFileWriter=null;
		PrintWriter moduleDefFileWriter=null;
		try {
			classFileWriter = new PrintWriter(Utility.YML_FOLDER+"/"+pojo.getSimpleName()+".yaml", "UTF-8");
			String moduleFileName=Utility.MODULE_NAME.replace(" ", "");
			System.err.println(moduleFileName);
			 
			StringBuilder crud=new StringBuilder();
			StringBuilder info=new StringBuilder();
		    crud.append(createCRUD(fullyQualifiedName));
		   
			String objectDefinition = getFullObjectDefinition(fullyQualifiedName);
			
			classFileWriter.write(crud.toString()+objectDefinition);
			classFileWriter.flush();
			//write to module file 
			File paths=new File(Utility.YML_FOLDER+"/"+moduleFileName+"_paths.yaml");
			
			if(!paths.exists())
			{
				//paths.mkdirs();
				paths.createNewFile();
			}
				
			final RandomAccessFile path = new RandomAccessFile(paths, "rw");
			if(path.length()!=0)
			path.seek(path.length());
			crud.append(NEWLINE);
			path.write(crud.toString().getBytes());
			path.close();
			
			File defs=new File(Utility.YML_FOLDER+"/"+moduleFileName+"_def.yaml");
			
			if(!defs.exists())
			{
				//defs.mkdirs();
				defs.createNewFile();
			}
			final RandomAccessFile def = new RandomAccessFile(defs, "rw");
			if(def.length()!=0)
				def.seek(def.length());
			String replace = objectDefinition.toString().replace("definitions:", "");
			def.write(replace.getBytes());
			def.close();
			 
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//StringBuilder main=new StringBuilder();
 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public String getFullObjectDefinition(String fullyQualifiedName)
	{
		SB objectDef=new SB();
		objectDef.a("definitions:").a(NEWLINE);
		Class<?> pojo = pojoHolder.getPojo();
		objectDef.a(getObjectDefinition(fullyQualifiedName,pojo));
		for(String s:referencedDetailedObjects)
		{
			objectDef.a(getObjectDefinition(s,null));	
		}
		
		objectDef.a(getReq_RespDef(pojo));
		return objectDef.str();
	}

	private String getObjectDefinition(String fullyQualifiedName,Class<?> pojo ) {
		int tabCount=0;
		SB objectDef=new SB();
		try {
			if(pojo==null)
			{
				PojoHolder po=new PojoHolder();
				po.loadPojo(fullyQualifiedName);
				  pojo=po.getPojo();
			}
			Field[] declaredFields = pojo.getDeclaredFields();
			
			 String objectSnakecase =pojo.getSimpleName();
			 String objectSnakecaseReq=objectSnakecase+"Request";
			
			
		
			tabCount=tabCount+1;
			objectDef.a(tabCount);
			objectDef.a(objectSnakecase).a(":").a(NEWLINE);
			tabCount=tabCount+1;
			objectDef.a(tabCount).a("type: object").a(NEWLINE);
			objectDef.a(tabCount).a("properties:").a(NEWLINE);
			
		    tabCount=tabCount+1;
		    List<String> requiredList=new ArrayList<String>();
		 
			for(Field f:declaredFields)
			{
				String name = f.getName();
				if(name.equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}
				if(f.isAnnotationPresent(Ignore.class) )
				{
					continue;
				}
				if(Utility.findTypes(f).equals("ignore"))
				{
					if(!f.isAnnotationPresent(DrillDown.class) && ! f.isAnnotationPresent(DrillDownTable.class) )
					{
						continue;
					}
				}
				String snakeCaseName = f.getName();
				objectDef.a(tabCount);
				objectDef.a(snakeCaseName).a(":").a(NEWLINE);
				if(f.isAnnotationPresent(NotBlank.class) || f.isAnnotationPresent(NotNull.class) ||f.isAnnotationPresent(NotEmpty.class))
				{
					
					requiredList.add(f.getName());
				}
				
				if(Utility.findTypes(f).equals("l"))
				{
					if(f.isAnnotationPresent(DrillDown.class) || f.isAnnotationPresent(DrillDownTable.class) )
					{
						referencedDetailedObjects.add(f.getType().getName());
					}if(f.getType().getSimpleName().equals(pojo.getSimpleName()))
					{
						objectDef.a(tabCount+1);
						objectDef.a("type: ").a("integer").a(NEWLINE);	
						objectDef.a(tabCount+1);
						objectDef.a("format: ").a("int64").a(NEWLINE);	
						objectDef.a(tabCount+1);
						objectDef.a("description: ").a(Utility.getDesc(snakeCaseName,objectSnakecase)).a(NEWLINE);
						
					}else{
					objectDef.a(tabCount+1);
					/*objectDef.a("type: ").a("object").a(NEWLINE);	
					objectDef.a(tabCount+1);
					objectDef.a("description: ").a(Utility.getDesc(snakeCaseName,objectSnakecase)).a(NEWLINE);
					objectDef.a(tabCount+2);*/
					objectDef.a("$ref: \"#/definitions/"+f.getType().getSimpleName()+"\"").a(NEWLINE);
					}
				
				}else if(Utility.findTypes(f).equals("ignore"))
				{
					if(f.isAnnotationPresent(DrillDown.class) || f.isAnnotationPresent(DrillDownTable.class) )
					{
						referencedDetailedObjects.add(f.getType().getName());
						objectDef.a(tabCount+1);
						objectDef.a("type:").a(TAB).a("array").a(NEWLINE);	
						objectDef.a(tabCount+1);
						objectDef.a("description:").a(TAB).a(Utility.getDesc(snakeCaseName,objectSnakecase)).a(NEWLINE);
						//objectDef.a(tabCount+1);
						objectDef.a("items:").a(NEWLINE);
						objectDef.a(tabCount+2);
						objectDef.a("$ref: \"#/definitions/"+Utility.getEnclosingType(f).getSimpleName()+"\"").a(NEWLINE);
					}
					
					
					
				}else
				{
					String swaggerType = Utility.getSwaggerType(f.getType().getSimpleName());
					objectDef.a(tabCount+1);
					objectDef.a("type: ").a(swaggerType.split(",")[0]).a(NEWLINE);
					if(swaggerType.split(",").length==2)
					{
					objectDef.a(tabCount+1);
					objectDef.a("format: "). a(swaggerType.split(",")[1]).a(NEWLINE);
					}else if(swaggerType.split(",")[0].equalsIgnoreCase("string")){
						if(!f.isAnnotationPresent(Length.class))
						{
							System.err.println("Length is not added for string filed"+f.getName());
						}
					}
					objectDef.a(tabCount+1);
					objectDef.a("description: ").a(Utility.getDesc(snakeCaseName,objectSnakecase)).a(NEWLINE);
					
				}
				
				if(f.isAnnotationPresent(Length.class))
				{
					 
					Length len = f.getDeclaredAnnotation(org.hibernate.validator.constraints.Length.class);
					int max=0;
					int min=0;
					if(len.max()>0)
					{
						objectDef.a(tabCount+1);
						objectDef.a("maxLength: ").a(""+len.max()).a(NEWLINE);
					}
					if(len.min()>0)
					{
						objectDef.a(tabCount+1);
						objectDef.a("minLength: ").a(""+len.min()).a(NEWLINE);
					}
					
				}
				
				if(f.isAnnotationPresent(Pattern.class))
				{
					Pattern pat = f.getDeclaredAnnotation(javax.validation.constraints.Pattern.class);
					
						objectDef.a(tabCount+1);
						objectDef.a("pattern: ").a(Utility.SINGLEQUOTE+pat.regexp()+Utility.SINGLEQUOTE).a(NEWLINE);
					
					
				}
			
				
			}
			
			
			
		
		
			
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
		 String objectSnakecase =pojo.getSimpleName();
		int tabCount=1;
		ss.a(tabCount);
		ss.a(objectSnakecase+"Request:").a(NEWLINE);
		tabCount++;
		
		ss.a(tabCount).a("properties:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("requestInfo:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/RequestInfo'").a(NEWLINE);
		tabCount=tabCount-1;
		ss.a(tabCount).a(Utility.toCamelCase(objectSnakecase)+"s:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("type:"+TAB+"array").a(NEWLINE);
		ss.a(tabCount).a("items:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/"+objectSnakecase+"'").a(NEWLINE);
		//request post put patch will not need the page information
		/*tabCount=tabCount-2;
		ss.a(tabCount).a(Utility.toCamelCase("Page")+":").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/Page"+"'").a(NEWLINE);*/
		//return ss.str();
		
		//response
		tabCount=1;
		ss.a(tabCount);
		ss.a(objectSnakecase+"Response:").a(NEWLINE);
		tabCount++;
		
		ss.a(tabCount).a("properties:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("responseInfo:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/ResponseInfo'").a(NEWLINE);
		tabCount=tabCount-1;
		ss.a(tabCount).a(Utility.toCamelCase(objectSnakecase)+"s:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("type:"+TAB+"array").a(NEWLINE);
		ss.a(tabCount).a("items:").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/"+objectSnakecase+"'").a(NEWLINE);
		tabCount=tabCount-2;
		ss.a(tabCount).a(Utility.toCamelCase("Page")+":").a(NEWLINE);
		tabCount++;
		ss.a(tabCount).a("$ref: '#/definitions/Page"+"'").a(NEWLINE);
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
		 String REQ_INFO = "requestInfo";
		  String GET_SUMMARY="Get the list of ";
			try {
				 
				Class<?> pojo = pojoHolder.getPojo();
				 String objectSnakecase =pojo.getSimpleName();
				Field[] declaredFields = pojo.getDeclaredFields();
				tabCount=tabCount+1;
				jsn.a(tabCount);
				/*String uriName = pojo.getSimpleName().toLowerCase()+"s";*/
				String uriName =English.plural(pojo.getSimpleName().toLowerCase());
				System.out.println(uriName);
				jsn.a("/").a(uriName).a(":").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("get:").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("summary: ").a(GET_SUMMARY).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("description: ").a(GET_DESCRIPTION).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("tags: ").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("- Masters").a(NEWLINE);
				jsn.a(tabCount).a("- "+objectSnakecase).a(NEWLINE);
				tabCount--;
				jsn.a(tabCount).a("parameters:").a(NEWLINE);
				tabCount=tabCount+1;
				PojoHolder reqPojoHolder=new PojoHolder();
				reqPojoHolder.loadPojo("org.egov.egf.web.contract.RequestInfo");//change this
				Class<?> requestPojo = reqPojoHolder.getPojo();
				Field[] reqDeclaredFields = requestPojo.getDeclaredFields();
				 String reqSnakecase =Utility.toCamelCase(requestPojo.getSimpleName());
				for(Field f:reqDeclaredFields)
				{
					String name = f.getName();
					if(name.equals("serialVersionUID"))
						continue;
					if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
					{
						continue;
					}
					if(f.isAnnotationPresent(Ignore.class) )
					{
						continue;
					}
				String snakeCaseName =  f.getName();
					
				if(!Utility.findTypes(f).equals("l"))	
				{
				jsn.a(tabCount).a("- name: ").a(reqSnakecase).a(".").a(snakeCaseName).a(NEWLINE);
				String swaggerType = Utility.getSwaggerType(f.getType().getSimpleName());
				jsn.a(tabCount).a("  ").a("type: "). a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length>1)
				{
					jsn.a(tabCount).a("  ").a("format: "). a(swaggerType.split(",")[1]).a(NEWLINE);	
				}
				}
				else
				{
				jsn.a(tabCount).a("- name: ").a(SPACE).a(reqSnakecase).a(".").a(snakeCaseName).a("id").a(NEWLINE);
				String swaggerType = Utility.getSwaggerType("long");
				jsn.a(tabCount).a("  ").a("type: ").a(SPACE).a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length>1)
				{
					jsn.a(tabCount).a("  ").a("format: ").a(SPACE).a(swaggerType.split(",")[1]).a(NEWLINE);	
				}
				}
				
				jsn.a(tabCount).a("  ").a("in: "). a("query").a(NEWLINE);
				
				jsn.a(tabCount).a("  ").a("description: ").a(Utility.getDesc(f.getName(), null)).a(NEWLINE);
			    if(Utility.isMandatory(f))
				jsn.a(tabCount).a("  ").a("required: ").a("true").a(NEWLINE);
			    /*else
			    jsn.a(tabCount).a(TAB).a(TAB).a("required:").a(SPACE).a("false").a(NEWLINE);	*/
				 
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
					String snakeCaseName =  f.getName();
					if(f.isAnnotationPresent(Ignore.class) )
					{
						continue;
					}
				Boolean skip=false;
				if(Utility.findTypes(f).equals("l"))	
				{
					jsn.a(tabCount).a("- name: ").a(Utility.toCamelCase(objectSnakecase)).a(".").a(snakeCaseName).a("id").a(NEWLINE);
					String swaggerType = Utility.getSwaggerType("long");
					jsn.a(tabCount).a("  ").a("type: ").a(swaggerType.split(",")[0]).a(NEWLINE);
					if(swaggerType.split(",").length>1)
					{
						jsn.a(tabCount).a("  ").a("format: ").a(swaggerType.split(",")[1]).a(NEWLINE);	
					}	
					
					
				
				}
				else if(Utility.findTypes(f).equals("ignore"))
				{
					if(f.isAnnotationPresent(DrillDown.class) || f.isAnnotationPresent(DrillDownTable.class))
					{
					jsn.a(tabCount).a("- name: ").a(Utility.toCamelCase(objectSnakecase)).a(".").a(snakeCaseName).a(NEWLINE);
					String swaggerType = Utility.getSwaggerType(f.getType().getSimpleName());
					jsn.a(tabCount).a("  ").a("type: ").a("object").a(NEWLINE);
					jsn.a(tabCount+1);
					jsn.a("items:").a(NEWLINE);
					jsn.a(tabCount+1);
					jsn.a(TAB).a("$ref: \"#/definitions/"+Utility.getEnclosingType(f).getSimpleName()+"\"").a(NEWLINE);
					
					if(swaggerType.split(",").length>1)
					{
						jsn.a(tabCount).a(TAB).a(TAB).a("format: ").a(swaggerType.split(",")[1]).a(NEWLINE);	
					}
					}else{
						skip=true;
					}
				}else{
					jsn.a(tabCount).a("- name: ").a(Utility.toCamelCase(objectSnakecase)).a(".").a(snakeCaseName).a(NEWLINE);
					String swaggerType = Utility.getSwaggerType(f.getType().getSimpleName());
					jsn.a(tabCount).a("  ").a("type: ").a(swaggerType.split(",")[0]).a(NEWLINE);
					if(swaggerType.split(",").length>1)
					{
						jsn.a(tabCount).a("  ").a("format: ").a(swaggerType.split(",")[1]).a(NEWLINE);	
					}	
				}
				if(!skip)
				{
				jsn.a(tabCount).a("  ").a("in: query").a(NEWLINE);
				
				jsn.a(tabCount).a("  ").a("description: ").a(Utility.getDesc(f.getName(), objectSnakecase)).a(NEWLINE);
			    if(Utility.isMandatory(f))
				jsn.a(tabCount).a("  ").a("required: true").a(NEWLINE);
				}
			   /* else
			    jsn.a(tabCount).a(TAB).a(TAB).a("required: false").a(NEWLINE);	*/
				 
				}
				
				PojoHolder pagePojoHolder=new PojoHolder();
				reqPojoHolder.loadPojo("org.egov.egf.web.contract.Page");//change this
				Class<?> pagePojo = reqPojoHolder.getPojo();
				Field[] pageDeclaredFields = pagePojo.getDeclaredFields();
				 String pageSnakecase =Utility.toCamelCase(pagePojo.getSimpleName());
				for(Field f:pageDeclaredFields)
				{
					String name = f.getName();
					if(name.equals("serialVersionUID"))
						continue;
					if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
					{
						continue;
					}
				String snakeCaseName =  f.getName();
					
				if(!Utility.findTypes(f).equals("l"))	
				{
				jsn.a(tabCount).a("- name: ").a(pageSnakecase).a(".").a(snakeCaseName).a(NEWLINE);
				String swaggerType = Utility.getSwaggerType(f.getType().getSimpleName());
				jsn.a(tabCount).a("  ").a("type: ") .a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length>1)
				{
					jsn.a(tabCount).a("  ").a("format: "). a(swaggerType.split(",")[1]).a(NEWLINE);	
				}
				}
				else
				{
				jsn.a(tabCount).a("- name: ").a(pageSnakecase).a(".").a(snakeCaseName).a("id").a(NEWLINE);
				String swaggerType = Utility.getSwaggerType("long");
				jsn.a(tabCount).a("  ").a("type: ").a(swaggerType.split(",")[0]).a(NEWLINE);
				if(swaggerType.split(",").length>1)
				{
					jsn.a(tabCount).a("  ").a("format: ").a(swaggerType.split(",")[1]).a(NEWLINE);	
				}
				}
				
				jsn.a(tabCount).a("  ").a("in: query").a(NEWLINE);
				
				jsn.a(tabCount).a("  ").a("description: ").a(Utility.getDesc(f.getName(), "Page")).a(NEWLINE);
			    if(Utility.isMandatory(f))
				jsn.a(tabCount).a("  ").a("required: ").a("true").a(NEWLINE);
			    /*else
			    jsn.a(tabCount).a(TAB).a(TAB).a("required:").a(SPACE).a("false").a(NEWLINE);	*/
				 
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
			jsn.a(tabCount).a("description: "+objectSnakecase+" created Successfully").a(NEWLINE);
		}else if(method.equalsIgnoreCase(METHOD_PUT))
		{
		jsn.a(tabCount).a("200:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("description: "+objectSnakecase+" update Successfully").a(NEWLINE);
		
		}
		else
		{
			jsn.a(tabCount).a("200:").a(NEWLINE);
			tabCount=tabCount+1;
			jsn.a(tabCount).a("description: "+objectSnakecase+" retrieved Successfully").a(NEWLINE);
		}
		jsn.a(tabCount).a("schema:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("$ref: '#/definitions/"+objectSnakecase+"Response'").a(NEWLINE);
		tabCount=tabCount-2;
		jsn.a(tabCount).a("400:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("description: Invalid Input").a(NEWLINE);
		//tabCount=tabCount+1;
		jsn.a(tabCount).a("schema:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("$ref: '#/definitions/"+"ErrorResponse'").a(NEWLINE);
		tabCount=tabCount-2;
		jsn.a(tabCount).a("404:").a(NEWLINE);
		tabCount=tabCount+1;
		jsn.a(tabCount).a("description: "+objectSnakecase+ " Not Found").a(NEWLINE);
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
				 String objectSnakecase =  pojo.getSimpleName();
				 String objectSnakecaseReq=objectSnakecase+"Request";
				Field[] declaredFields = pojo.getDeclaredFields();
				tabCount=tabCount+1;
				//jsn.a(tabCount);
				String uriName = pojo.getSimpleName().toLowerCase()+"s";
				//jsn.a("/").a(uriName).a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("post:").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("summary: ").a(POST_SUMMARY).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("description: ").a(POST_DESCRIPTION).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("tags: ").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("- Masters").a(NEWLINE);
				jsn.a(tabCount).a("- "+objectSnakecase).a(NEWLINE);
				tabCount--;
				jsn.a(tabCount).a("parameters:").a(NEWLINE);
				tabCount=tabCount+1;
				
				jsn.a(tabCount).a("- "+"name: ").a(Utility.toCamelCase(objectSnakecaseReq)).a(NEWLINE);
				jsn.a(tabCount).a("  ").a("in: body").a(NEWLINE);
				jsn.a(tabCount).a("  ").a("description: ").a(POST_DESCRIPTION).a(NEWLINE);
				jsn.a(tabCount).a("  ").a("schema:").a(NEWLINE);
				jsn.a(tabCount).a("  ").a(TAB).a("$ref: '#/definitions/"+objectSnakecaseReq+"'").a(NEWLINE);
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
				 String objectSnakecase = pojo.getSimpleName();
				 String objectSnakecaseReq=objectSnakecase+"Request";
				Field[] declaredFields = pojo.getDeclaredFields();
				//jsn.a(NEWLINE);
				tabCount=tabCount+1;
				//jsn.a(tabCount);
				String uriName = English.plural(pojo.getSimpleName()).toLowerCase();
				//jsn.a("/").a(uriName).a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("put:").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("summary: ").a(PUT_SUMMARY).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("description: ").a(PUT_DESCRIPTION).a(uriName).a(NEWLINE);
				jsn.a(tabCount).a("tags: ").a(NEWLINE);
				tabCount=tabCount+1;
				jsn.a(tabCount).a("- Masters").a(NEWLINE);
				jsn.a(tabCount).a("- "+objectSnakecase).a(NEWLINE);
				tabCount--;
				jsn.a(tabCount).a("parameters:").a(NEWLINE);
				tabCount=tabCount+1;
				
				jsn.a(tabCount).a("- name: ").a(Utility.toCamelCase(objectSnakecaseReq)).a(NEWLINE);
				jsn.a(tabCount).a("  ").a("in: body").a(NEWLINE);
				jsn.a(tabCount).a("  ").a("description: common request_info").a(NEWLINE);
				jsn.a(tabCount).a("  ").a("schema:").a(NEWLINE);
				jsn.a(tabCount).a("  ").a(TAB).a("$ref: '#/definitions/"+objectSnakecaseReq+"'").a(NEWLINE);
				String res=	getResponse(tabCount, objectSnakecase,METHOD_PUT);
				jsn.a(res);
				
			} catch (Exception e) {
				 e.printStackTrace();
			}
	
	
	return jsn.str();
	}
	 

}

