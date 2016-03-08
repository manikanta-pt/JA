package org.egov;

import static org.egov.Utility.NEWLINE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.ja.annotation.SearchResult;

public class SearchAdaptorAndJSCreator {

	PojoHolder pojoHolder=new PojoHolder();
	StringBuilder imports=new StringBuilder();
	StringBuilder services=new StringBuilder();
	StringBuilder findAll=new StringBuilder();
	StringBuilder constants=new StringBuilder();
	StringBuilder methods=new StringBuilder();
	StringBuilder urls=new StringBuilder();
	private String simpleName;
	private String objectName;
	public static void main(String[] args) {

		SearchAdaptorAndJSCreator rc=new SearchAdaptorAndJSCreator();

		rc.create("org.egov.commons.Fund");

	}
	public void create(String pojoName)
	{
		pojoHolder.loadPojo(pojoName);
		simpleName = pojoHolder.getPojo().getSimpleName();	
		objectName = Utility.toCamelCase(simpleName);

		createAdaptor(pojoName);
		createJS(pojoName);
	}

	private void createJS(String pojoName)
	{
		try {
			String urltosearch="",fontoptions="",sorting="",columnsandtitle="",urluptopojo="";
			urltosearch="\"/"+Utility.CONTEXT+"/"+objectName.toLowerCase()+"/"+Utility.SEARCH_URL+"/\""+ "+$('#mode').val()";

			Class<?> pojo = pojoHolder.getPojo();

			Field[] declaredFields = pojo.getDeclaredFields();
			int i=0;

			SB cst=new SB();
			for (Field f:declaredFields)
			{
				//String requiredMarker="";
				//System.out.println(f.getType().getName());
				//System.out.println();
				if(f.getName().equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
					continue;
				if(f.getName().equals("id"))
					continue;

				SearchResult isSearchResult = f.getDeclaredAnnotation(org.ja.annotation.SearchResult.class);
				//Length length = f.getDeclaredAnnotation(org.hibernate.validator.constraints.Length.class);	
				if(isSearchResult!=null)
				{
					String egFieldType="";
					egFieldType = Utility.findTypes(f);

					cst.a("{ \n")
					.a("\"data\" : ")
					.a("\""+f.getName()+"\", ")
					.a("\"sClass\" : ");

					if(egFieldType.equals("n"))
					{
						cst.a("\"text-right\"");

					}else
					{
						cst.a("\"text-left\"");
					}
					cst.a("} ,");

				}
			}
			columnsandtitle=cst.str();
			if(!columnsandtitle.isEmpty())
			{
				columnsandtitle=columnsandtitle.substring(0,columnsandtitle.lastIndexOf(",")-1);
			}

			urluptopojo="/"+Utility.CONTEXT+"/"+simpleName.toLowerCase()+"/";
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			System.out.println("Current relative path is: " + s);	
			String CurrfilePath = "/src/main/java/org/ja/js/dataTableJS";
			File jssrcFile=new File(s+CurrfilePath);
			String content;

			content = new Scanner(jssrcFile).useDelimiter("\\Z").next();
			content=	content.replace("urltosearch".toUpperCase(), urltosearch);
			content=	content.replace("fontoptions".toUpperCase(), fontoptions);
			content= content.replace("sorting".toUpperCase(), sorting);
			content=	content.replace("columnsandtitle".toUpperCase(), columnsandtitle);
			
			content=	content.replace("urluptopojo".toUpperCase(), urluptopojo);


			String jsPathname = Utility.PROJECT_WEBHOME+"/src/main/webapp/resources/app/js/";
			File pathFile=new File(jsPathname);
			if(!pathFile.exists())
				pathFile.mkdirs();

			File jsFileName=new File(jsPathname+Utility.toCamelCase(simpleName)+"Helper.js");

			if(Utility.WRITETOTEMPFILES)
			{
				jsFileName=new File(Utility.PROJECTHOME+Utility.toCamelCase(simpleName)+"Helper.js");
			}

			PrintWriter jsFileNameWriter=null;
			jsFileNameWriter = new PrintWriter(jsFileName);

			jsFileNameWriter.write(content);
			jsFileNameWriter.flush();
			jsFileNameWriter.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}






	}
	private void createAdaptor(String pojoName) {
		SB pakages=new SB();
		pakages.a("package org.egov."+Utility.CONTEXT.toLowerCase()+".web.adaptor;")
		.a(Utility.NEWLINE)
		.a(Utility.NEWLINE);


		SB imports=new SB();
		imports.a("import java.lang.reflect.Type;")
		.a(Utility.NEWLINE)
		.a("import "+pojoName+";")
		.a(Utility.NEWLINE)
		.a("import com.google.gson.JsonElement;")
		.a(Utility.NEWLINE)
		.a("import com.google.gson.JsonObject;")
		.a(Utility.NEWLINE)
		.a("import com.google.gson.JsonSerializationContext;")
		.a(Utility.NEWLINE)
		.a("import com.google.gson.JsonSerializer;")
		.a(Utility.NEWLINE);

		SB main=new SB();
		main.a("  public class "+simpleName+"JsonAdaptor implements JsonSerializer<"+simpleName+">")
		.a(NEWLINE)
		.a(" {")
		.a(NEWLINE);

		SB methods=new SB();
		methods.a("@Override"+NEWLINE)
		.a(" public JsonElement serialize(final "+simpleName+" "+objectName+", final Type type,final JsonSerializationContext jsc) ")
		.a(NEWLINE+"{").a(NEWLINE)
		.a("  final JsonObject jsonObject = new JsonObject();")
		.a(NEWLINE)
		.a(" if ("+objectName+" != null)")
		.a(NEWLINE)
		.a(" {")	 
		.a(NEWLINE);

		Field[] declaredFields = pojoHolder.getPojo().getDeclaredFields();
		int i=0;
		String drildownField="id";

		for (Field f:declaredFields)
		{
			
			System.out.println(f.getType().getName());
			//System.out.println();
			if(f.getName().equals("serialVersionUID"))
				continue;
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				continue;
			if(f.getName().equals("id"))
				continue;
			if(f.getName().equals("code"))
			{
				  drildownField = "code";
				continue;
			}
			
			SearchResult	isResult = f.getDeclaredAnnotation(org.ja.annotation.SearchResult.class);	
			//handle here for boolean,date,number,etc etc
			if(isResult!=null)
			{
				String	egFieldType = Utility.findTypes(f);

				if(egFieldType.equals("ignore"))	
					continue;
				methods.a("if("+objectName+".get"+Utility.toSentenceCase(f.getName())+"()!=null)"+NEWLINE);
				if(egFieldType.equals("d"))	
					methods.a(" jsonObject.addProperty(\""+f.getName()+"\", "+objectName+".get"+Utility.toSentenceCase(f.getName())+"());"+NEWLINE);
				else if(egFieldType.equals("l"))
					methods.a(" jsonObject.addProperty(\""+f.getName()+"\", "+objectName+".get"+Utility.toSentenceCase(f.getName())+"().getName());"+NEWLINE);
				else
					methods.a(" jsonObject.addProperty(\""+f.getName()+"\", "+objectName+".get"+Utility.toSentenceCase(f.getName())+"());"+NEWLINE);
				methods.a("else"+NEWLINE);
				methods.a(" jsonObject.addProperty(\""+f.getName()+"\",\"\");"+NEWLINE);
				


		}}
			
		/*if(drildownField.equals("code"))
			methods.a(" jsonObject.addProperty(\"id\", "+objectName+".getCode());"+NEWLINE);
			else*/
		methods.a(" jsonObject.addProperty(\"id\", "+objectName+".getId());"+NEWLINE);

		methods.a("     } ").a(NEWLINE)
		.a(" return jsonObject; ")
		.a(" }")
		.a(NEWLINE)
		.a(" }");

		String adaptorPathname = Utility.PROJECT_WEBHOME+"/src/main/java/org/egov/"+Utility.CONTEXT.toLowerCase()+"/web/adaptor/";
		File adaptorPath=new File(adaptorPathname);
		if(!adaptorPath.exists())
			adaptorPath.mkdirs();

		File adaptorFielName=new File(adaptorPathname+simpleName+"JsonAdaptor.java");


		if(Utility.WRITETOTEMPFILES)
		{
			adaptorFielName=new File(Utility.PROJECTHOME+"/adaptorTemp.jatemp");
		}

		PrintWriter adaptorWriter=null;
		try {
			adaptorWriter = new PrintWriter(adaptorFielName);
			adaptorWriter.write(pakages.str());
			adaptorWriter.write(imports.str());
			adaptorWriter.write(main.str());
			adaptorWriter.write(methods.str());
			adaptorWriter.flush();
			adaptorWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	}
 



