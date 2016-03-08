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
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.context.MessageSource;

public class ControllerCreator {

	PojoHolder pojoHolder=new PojoHolder();
	StringBuilder imports=new StringBuilder();
	StringBuilder services=new StringBuilder();
	StringBuilder findAll=new StringBuilder();
	StringBuilder constants=new StringBuilder();
	StringBuilder methods=new StringBuilder();
	StringBuilder urls=new StringBuilder();
	public static void main(String[] args) {

		ControllerCreator rc=new ControllerCreator();
		rc.createController("org.egov.tl.entity.Validity");

	}

	public void	createController(String fullyQualifiedName)
	{	
		try {
			pojoHolder.loadPojo(fullyQualifiedName);
			

			
			

			Class<?> pojo = pojoHolder.getPojo();
			List<String> findAjaxCalls = pojoHolder.findAjaxCalls(Utility.SRCFOLDER+"/"+pojo.getCanonicalName().replace(".", "/")+".java");
			
			String[] readImports = pojoHolder.readImports(Utility.SRCFOLDER+"/"+pojo.getCanonicalName().replace(".", "/")+".java");
			for(String s: readImports)
			{
				if(s.contains("org.egov"))
				{
					if(s.contains("BaseModel"))
						continue;
					if(s.contains("AbstractAuditable"))
						continue;
					if(s.contains("AbstractPersistable"))
						continue;
					if(s.contains("annotation"))
						continue;
				
				//	imports.append("import "+s+";"+NEWLINE);	
				//	imports.append("import "+pojoHolder.getServiceFor(s)+";"+NEWLINE);
					String entityName=s.substring(s.lastIndexOf(".")+1);
					String service=entityName+"Service";
				//	services.append("@Autowired"+NEWLINE+TAB+"private final " +service+" "+Utility.toCamelCase(service)+";"+NEWLINE);
					//findAll.append(TAB+"public List<"+entityName+"> findAll"+entityName+"(){"+NEWLINE);
					//findAll.append(TAB+"return "+Utility.toCamelCase(service)+".findAll();"+NEWLINE+TAB+"}"+NEWLINE);

				}
			}

			//adding find All for fields
			
			Field[] declaredFields = pojoHolder.getPojo().getDeclaredFields();
			findAll.append(new SB()
			.a("private void prepareNewForm(Model model) {")
			.a(NEWLINE)
			.str());
			
			if(Utility.USE_PERSISTENCE_SERVICE)
			{	
				services.append(new SB()
				.a(TAB)
				.a("@Autowired")
				.a(NEWLINE)
				.a("public PersistenceService persistenceService;")
				.a(NEWLINE).a(NEWLINE).str());
				
				imports.append(new SB()
				.a("import org.egov.infstr.services.PersistenceService;")
				.a(NEWLINE).str());
			}
			for(Field field:declaredFields)
			{
				Class<?> type = field.getType();
				String fieldFullName = type.getName();
				boolean ajax=false;
				//Deprecated annotation = type.getAnnotation(Deprecated.class);
				//boolean annotationPresent = type.isAnnotationPresent(Deprecated.class);
				if(!findAjaxCalls.isEmpty())
				{
					for(String s:findAjaxCalls)
					{
						if(s.contains(type.getSimpleName()))
						{
							ajax=true;
							createAjaxActions(fieldFullName, urls, type);
						}
					}
				}

				if(fieldFullName.contains("org.egov"))
				{
					String serviceFor = pojoHolder.getServiceFor(fieldFullName)	;
					imports.append(new SB()
					.a("import ")
					.a(fieldFullName)
					.a(";"+NEWLINE).str());
					
					if(!Utility.USE_PERSISTENCE_SERVICE)
					{	
						findAll.append(
								new SB()
								.a(TAB)
								.a("model.addAttribute(\"")
								.a(Utility.toCamelCase(type.getSimpleName()+"s\""))
								.a(",").str());
								if(ajax)
									findAll.append(new SB()
									.a("Collections.EMPTY_LIST);").a(NEWLINE).str());	
								else
									findAll.append(new SB()
									.a(Utility.toCamelCase(type.getSimpleName())+"Service.findAll());")
											.a(NEWLINE)
											.str());

				
					services.append(new SB()
					.a(TAB)
					.a("@Autowired")
					.a(NEWLINE)
					.a("private ")
					.a(type.getSimpleName()+"Service")
					.a(" ")
					.a(Utility.toCamelCase(type.getSimpleName())+"Service")
					.a(";")
					.a(NEWLINE).str());
					
					
					
					imports.append(new SB()
					.a("import ")
					.a(serviceFor)
					.a(";"+NEWLINE).a(NEWLINE).str());

					}else
					{
						
						findAll.append(
								new SB()
								.a(TAB)
								.a("model.addAttribute(\"")
								.a(Utility.toCamelCase(type.getSimpleName()+"s\""))
								.a(",").str());
						if(ajax)
							findAll.append(new SB()
							.a("Collections.EMPTY_LIST);").a(NEWLINE).str());	
						else
							findAll.append(new SB()
								.a("(List<"+type.getSimpleName()+">)persistenceService.findAllBy(\"from "+type.getSimpleName()+" order by name asc\"));")
								.a(NEWLINE)
								.str());
					}
				}
			}
			
			findAll.append(new SB()
			.a("}")
			.a(NEWLINE)
			.a(NEWLINE)
			.str());
			imports.append("import java.util.List;"+NEWLINE);
			imports.append("import org.springframework.stereotype.Controller;"+NEWLINE);
			imports.append("import org.springframework.ui.Model;"+NEWLINE);
			imports.append("import org.springframework.validation.BindingResult;"+NEWLINE);
			imports.append("import org.springframework.web.bind.annotation.ModelAttribute;"+NEWLINE);
			imports.append("import org.springframework.web.bind.annotation.PathVariable;"+NEWLINE);
			imports.append("import org.springframework.web.bind.annotation.RequestMapping;"+NEWLINE);
			imports.append("import org.springframework.web.bind.annotation.RequestMethod;"+NEWLINE);
			imports.append("import javax.validation.Valid;"+NEWLINE);
			imports.append("import org.springframework.web.bind.annotation.ResponseBody;"+NEWLINE);
			imports.append("import org.springframework.web.servlet.mvc.support.RedirectAttributes;"+NEWLINE);
			imports.append("import org.springframework.http.MediaType;");

			Package package1 = pojo.getPackage();
			String name = package1.getName();
			String packageName = name.substring(name.indexOf("package")+1,name.lastIndexOf("."));
			String servicePackage=packageName+".service";
			String serviceName=pojo.getSimpleName()+"Service";
			packageName=Utility.WEBPACKAGE;
			String fileName=pojo.getSimpleName()+"Controller";
			//File f=new File(name);
			File f = new File(Utility.CONTROLLER_FOLDER+"/"+packageName.replace(".", "/"));

			if (f.exists()) {
				//System.out.println("file exists");
			}else
			{
				f.mkdirs();
			}

			PrintWriter writer = new PrintWriter(Utility.CONTROLLER_FOLDER+"/"+packageName.replace(".", "/")+"/" +fileName+".java", "UTF-8");
			writer.write(new SB().a("package ").a(packageName).a(";").a(NEWLINE).str());
			writer.write(NEWLINE);
			writer.write(NEWLINE);


			writer.write("import java.util.List;"+NEWLINE);
			writer.write("import java.util.Collections;"+NEWLINE);
			writer.write(NEWLINE);
			writer.write(NEWLINE);

			writer.write("import javax.persistence.EntityManager;"+NEWLINE);
			writer.write("import javax.persistence.PersistenceContext;"+NEWLINE);
			writer.write(NEWLINE);
			writer.write("import "+name+"."+pojo.getSimpleName()+";"+NEWLINE);
			writer.write("import "+servicePackage+"."+serviceName+";"+NEWLINE);		
			writer.write(NEWLINE);
			
			writer.write("import org.springframework.beans.factory.annotation.Autowired;"+NEWLINE);
			writer.write("import org.springframework.stereotype.Controller;"+NEWLINE);
			writer.write("import org.springframework.web.bind.annotation.RequestMapping;"+NEWLINE);
			writer.write("import com.google.gson.Gson;"+Utility.NEWLINE);
			writer.write("import com.google.gson.GsonBuilder;"+Utility.NEWLINE);
			
			writer.write(imports.toString());
			writer.write(NEWLINE);
			writer.write(NEWLINE);

			writer.write("@Controller "+NEWLINE);

			writer.write(new SB().a("@RequestMapping(\"/")
					.a(pojo.getSimpleName().toLowerCase())
					.a("\")").a(NEWLINE)
					.str());


			writer.write(
					new SB()
					.a(" public class ")
					.a(pojo.getSimpleName())
					.a("Controller {")
					.a(NEWLINE).str());

			constants.append("private final static String "+pojo.getSimpleName().toUpperCase()+"_NEW=\""+pojo.getSimpleName().toLowerCase()+"-new\";"+NEWLINE);
			constants.append("private final static String "+pojo.getSimpleName().toUpperCase()+"_RESULT=\""+pojo.getSimpleName().toLowerCase()+"-result\";"+NEWLINE);
			constants.append("private final static String "+pojo.getSimpleName().toUpperCase()+"_EDIT=\""+pojo.getSimpleName().toLowerCase()+"-edit\";"+NEWLINE);
			constants.append("private final static String "+pojo.getSimpleName().toUpperCase()+"_VIEW=\""+pojo.getSimpleName().toLowerCase()+"-view\";"+NEWLINE);
			constants.append("private final static String "+pojo.getSimpleName().toUpperCase()+"_SEARCH=\""+pojo.getSimpleName().toLowerCase()+"-search\";"+NEWLINE);

			writer.write(constants.toString());


			writer.write(new SB()
			.a("@Autowired"+NEWLINE)
			.a(TAB)
			.a("private  ")
			.a(serviceName)
			.a(" ")
			.a(Utility.toCamelCase(serviceName))
			.a(";")
			.a(NEWLINE).str());
			
			writer.write("@Autowired \n private MessageSource messageSource;");

			writer.write(services.toString());//these will be from imports 
			writer.write(findAll.toString());

			methods.append(new SB()
			.a("@RequestMapping(value = \"/new\", method = RequestMethod.GET)"+NEWLINE)
			.a(TAB)
			.a("public String ")
			.a("newForm(final Model model){")
			.a(NEWLINE)
			.a("prepareNewForm(model);")
			.a(NEWLINE)
			.a("model.addAttribute(\""+Utility.toCamelCase(pojo.getSimpleName())+"\", new "+pojo.getSimpleName()+"() );")
			.a(NEWLINE)
			.a(TAB)
			.a("return ")
			.a(pojo.getSimpleName().toUpperCase()+"_NEW")
			.a(";")
			.a(NEWLINE)
			.a("}")
			.a(NEWLINE)
			.a(NEWLINE)
			.str());



			methods.append(new SB()
			.a("@RequestMapping(value = \"/create\", method = RequestMethod.POST)"+NEWLINE)
			.a(TAB)
			.a("public String ")
			.a("create(@Valid @ModelAttribute final "+pojo.getSimpleName()+" "+Utility.toCamelCase(pojo.getSimpleName())
					+",final BindingResult errors,final Model model,final RedirectAttributes redirectAttrs){")
					.a(NEWLINE)
					.a(TAB)
					.a( "if (errors.hasErrors()) {"+NEWLINE)
					.a("prepareNewForm(model);")
					.a(NEWLINE)
					.a(TAB+"return ")
					.a(pojo.getSimpleName().toUpperCase()+"_NEW")
					.a("; }")
					.a(NEWLINE)
					.a(Utility.toCamelCase(serviceName)+".create("+Utility.toCamelCase(pojo.getSimpleName())+");"+NEWLINE)
					.a("redirectAttrs.addFlashAttribute(\"message\", messageSource.getMessage(\"msg."+Utility.toCamelCase(pojo.getSimpleName())+".success\",null,null));"+NEWLINE)
					.a("return \"redirect:/"+pojo.getSimpleName().toLowerCase()+"/result/\"+"+Utility.toCamelCase(pojo.getSimpleName())+".getId();")
					.a(NEWLINE) 
					.a("}")
					.a(NEWLINE)
					.a(NEWLINE)
					.str()); 

			String viewField=null;
			String	viewType="String";
			Field code=null;
			try {
				pojo.getDeclaredField("code");
			} catch (NoSuchFieldException e) {

			} catch (SecurityException e) {

			}
			if(code!=null)
			{
				viewField="code";
				viewType="String";
			}
			else 
			{
				viewField="id";
				viewType="Long";
			}

			String s="";
			methods.append(new SB()
			.a("@RequestMapping(value = \"/edit/{"+viewField+"}\", method = RequestMethod.GET)"+NEWLINE)
			.a(TAB)
			.a("public String ")
			.a("edit(@PathVariable(\""+viewField+"\") final "+viewType+" "+viewField+",Model model){")
			.a(NEWLINE).str());
			if(viewField.equalsIgnoreCase("code"))
			{
				s=pojo.getSimpleName()+" "+Utility.toCamelCase(pojo.getSimpleName()) +"  = "+Utility.toCamelCase(serviceName) +
						".findByCode(code);";
			}else
			{
				s=pojo.getSimpleName()+" "+Utility.toCamelCase(pojo.getSimpleName()) +"  = "+Utility.toCamelCase(serviceName) +
						".findOne(id);";
			}
			methods.append(new SB()
			.a(s)
			.a("prepareNewForm(model);")
			.a(NEWLINE)
			.a("model.addAttribute(\""+Utility.toCamelCase(pojo.getSimpleName())+"\", "+Utility.toCamelCase(pojo.getSimpleName())+");")
			.a(TAB)
			.a("return ")
			.a(pojo.getSimpleName().toUpperCase()+"_EDIT")
			.a(";")
			.a(NEWLINE)
			.a("}")
			.a(NEWLINE)
			.a(NEWLINE)
			.str());


			methods.append(new SB()
			.a("@RequestMapping(value = \"/update\", method = RequestMethod.POST)"+NEWLINE)
			.a(TAB)
			.a("public String ")
			.a("update(@Valid @ModelAttribute final "+pojo.getSimpleName()+" "+Utility.toCamelCase(pojo.getSimpleName())
					+",final BindingResult errors,final Model model,final RedirectAttributes redirectAttrs){")
					.a(NEWLINE)
					.a(TAB)
					.a( "if (errors.hasErrors()){"+NEWLINE)
					.a("prepareNewForm(model);")
					.a("return ")
					.a(pojo.getSimpleName().toUpperCase()+"_EDIT")
					.a(";")
					.a(NEWLINE)
					.a("}")
					.a(Utility.toCamelCase(serviceName)+".update("+Utility.toCamelCase(pojo.getSimpleName())+");"+NEWLINE)
					.a("redirectAttrs.addFlashAttribute(\"message\", messageSource.getMessage(\"msg."+Utility.toCamelCase(pojo.getSimpleName())+".success\",null,null));"+NEWLINE)
					.a("return \"redirect:/"+pojo.getSimpleName().toLowerCase()+"/result/\"+"+Utility.toCamelCase(pojo.getSimpleName())+".getId();")
					.a(NEWLINE) 
					.a("}")
					.a(NEWLINE)
					.a(NEWLINE)
					.str());



			methods.append(new SB()
			.a("@RequestMapping(value = \"/view/{"+viewField+"}\", method = RequestMethod.GET)"+NEWLINE)
			.a(TAB)
			.a("public String ")
			.a("view(@PathVariable(\""+viewField+"\") final "+viewType+" "+viewField+",Model model){")
			.a(NEWLINE)
			.str());
			if(viewField.equalsIgnoreCase("code"))
			{
				s=pojo.getSimpleName()+" "+Utility.toCamelCase(pojo.getSimpleName()) +"  = "+Utility.toCamelCase(serviceName) +
						".findByCode(code);";
			}else
			{
				s=pojo.getSimpleName()+" "+Utility.toCamelCase(pojo.getSimpleName()) +"  = "+Utility.toCamelCase(serviceName) +
						".findOne(id);";
			}
			methods.append(new SB()
			.a(s)
			.a(NEWLINE)
			.a("prepareNewForm(model);"+NEWLINE)
			.a("model.addAttribute(\""+Utility.toCamelCase(pojo.getSimpleName())+"\", "+Utility.toCamelCase(pojo.getSimpleName())+");")
			
			.a(TAB)
			.a("return ")
			.a(pojo.getSimpleName().toUpperCase()+"_VIEW")
			.a(";")
			.a(NEWLINE)
			.a("}")
			.a(NEWLINE)
			.a(NEWLINE)
			.str());


		SB result=new SB();	
	result.a("@RequestMapping(value = \"/result/{"+viewField+"}\", method = RequestMethod.GET)"+NEWLINE+
			      " public String result(@PathVariable(\""+viewField+"\") final Long id,Model model){"+NEWLINE);
	
	if(viewField.equalsIgnoreCase("code"))
	{
		result.a(pojo.getSimpleName()+" "+Utility.toCamelCase(pojo.getSimpleName()) +"  = "+Utility.toCamelCase(serviceName) +
				".findByCode(code);");
	}else
	{
		result.a(pojo.getSimpleName()+" "+Utility.toCamelCase(pojo.getSimpleName()) +"  = "+Utility.toCamelCase(serviceName) +
				".findOne(id);");
	}
	
	result.a("model.addAttribute(\""+Utility.toCamelCase(pojo.getSimpleName())+"\", "+Utility.toCamelCase(pojo.getSimpleName())+");"+NEWLINE+
				   "return "+pojo.getSimpleName().toUpperCase()+"_RESULT;}");
			
				methods.append(result.str());
				
				
				
				createSearch(pojoHolder);
				writer.write(methods.toString());
				writer.write(NEWLINE);
				writer.write("}");
				writer.flush();
				writer.close();

				File sqlf = new File(Utility.SQL_FOLDER);

				if (sqlf.exists()) {
					//System.out.println("file exists");
				}else
				{
					sqlf.mkdirs();
				}
				System.out.println(pojo.getSimpleName());
				String urlName = pojo.getSimpleName().toLowerCase();				
				PrintWriter sqlWriter = new PrintWriter(Utility.SQL_FOLDER+"/" +Utility.CONTEXT+"_"+pojo.getSimpleName()+"_actions_DML.sql", "UTF-8");
				sqlWriter.append(Utility.createUrls("New-"+pojo.getSimpleName(),"/"+urlName+"/new", "true", "1"));
				sqlWriter.append(Utility.createUrls("Create-"+pojo.getSimpleName(),"/"+urlName+"/create", "false", "1"));
				sqlWriter.append(Utility.createUrls("Update-"+pojo.getSimpleName(),"/"+urlName+"/update",  "false", "1"));
				sqlWriter.append(Utility.createUrls("View-"+pojo.getSimpleName(),"/"+urlName+"/view",  "false", "1"));
				sqlWriter.append(Utility.createUrls("Edit-"+pojo.getSimpleName(),"/"+urlName+"/edit",  "false", "1"));
				sqlWriter.append(Utility.createUrls("Result-"+pojo.getSimpleName(),"/"+urlName+"/result",  "false", "1"));
				sqlWriter.append(Utility.createUrls("Search and View-"+pojo.getSimpleName(),"/"+urlName+"/"+Utility.BEFORE_SEARCH_URL+"/view",  "true", "2"));
				sqlWriter.append(Utility.createUrls("Search and Edit-"+pojo.getSimpleName(),"/"+urlName+"/"+Utility.BEFORE_SEARCH_URL+"/edit",  "true", "3"));
				sqlWriter.append(Utility.createUrls("Search and View Result-"+pojo.getSimpleName(),"/"+urlName+"/"+Utility.SEARCH_URL+"/view",  "false", "1"));
				sqlWriter.append(Utility.createUrls("Search and Edit Result-"+pojo.getSimpleName(),"/"+urlName+"/"+Utility.SEARCH_URL+"/edit",  "false", "1"));
				sqlWriter.append(urls.toString());
				sqlWriter.flush();
				sqlWriter.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	public void createSearch(PojoHolder pojoHolder2) {
		SB beforeSearch=new SB();
		
		methods.append(NEWLINE+NEWLINE);
		String simpleName = pojoHolder2.getPojo().getSimpleName();
		beforeSearch.a("@RequestMapping(value =  \"/"+Utility.BEFORE_SEARCH_URL+"/{mode}\", method = RequestMethod.GET)").a(NEWLINE)
		.a("public String "+ Utility.BEFORE_SEARCH_URL+"(@PathVariable(\"mode\") final String  mode,Model model)\n{").a(NEWLINE)
		.a(simpleName+" "+Utility.toCamelCase(simpleName)+"  = new "+simpleName+"();").a(NEWLINE)
		.a("prepareNewForm(model);").a(NEWLINE)
		.a("model.addAttribute(\""+Utility.toCamelCase(simpleName)+"\","+Utility.toCamelCase(simpleName)+");").a(NEWLINE)
		.a("return "+simpleName.toUpperCase()+"_SEARCH;").a(NEWLINE)
		.a("\n}").a(NEWLINE).a(NEWLINE);
		
		methods.append(beforeSearch.str());
		
		SB search=new SB();
		search.a("@RequestMapping(value = \"/"+Utility.SEARCH_URL+"/{mode}\", method = RequestMethod.POST,produces = MediaType.TEXT_PLAIN_VALUE)")
		.a(NEWLINE)
		.a("public @ResponseBody String "+Utility.SEARCH_URL+"(@PathVariable(\"mode\") final String mode, Model model,@ModelAttribute final "+simpleName+" "
		+Utility.toCamelCase(simpleName)+" ) \n{")
		.a(NEWLINE)
		.a("List<"+simpleName+"> searchResultList = "+Utility.toCamelCase(simpleName)+"Service.search("+Utility.toCamelCase(simpleName)+");")
		.a(NEWLINE)
		.a("String result = new StringBuilder(\"{ \\\"data\\\":\").append(toSearchResultJson(searchResultList)).append(\"}\").toString();")
		.a(NEWLINE)
		.a("return result;\n}")	.a(NEWLINE);
		methods.append(search.str());
		
		
		//this part is for JsonConverson
		SB jsonCon=new SB();
		jsonCon.a("public Object toSearchResultJson(final Object object)\n {")
			.a(Utility.NEWLINE)
			.a("final GsonBuilder gsonBuilder = new GsonBuilder();")
			.a(Utility.NEWLINE)
			.a("final Gson gson = gsonBuilder.registerTypeAdapter("+simpleName+".class,new "+simpleName+"JsonAdaptor()).create();")
			.a(Utility.NEWLINE)
			.a("final String json = gson.toJson(object);")
			.a(Utility.NEWLINE)
			.a("return json;\n}");
	
		methods.append(jsonCon.str());
		
		
		
		
	}

	private void createAjaxActions(String fullyQualifiedName,
			StringBuilder urls, Class<?> type) throws IOException,
			FileNotFoundException {
		String fileName=null;
		Class<?> controller =null;
		String controllerName=pojoHolder.getControllerFor(fullyQualifiedName);
		String ajaxControler=null;			
		try {
			controller= Class.forName(controllerName);
		} catch (ClassNotFoundException e) {

			System.out.println("Cannot find "+controllerName);
			System.out.println("searching for AjaxController");
			try {

				ajaxControler = controllerName.replace(type.getSimpleName(), "Ajax");
				controller = Class.forName(ajaxControler);
			} catch (ClassNotFoundException ex) {

				System.out.println("Cannot find "+ajaxControler);
				System.out.println("writing to temp file use it from there");
			}

		}

		if(controller!=null)
		{
			Method[] declaredMethods = controller.getDeclaredMethods();
			boolean methodExists=false;
			for(Method m:declaredMethods)
			{
				if(m.getName().contains("ajax"+type.getSimpleName()))
				{
					methodExists=true;
					System.out.println("seems like this method already present,still if u need you can find in temp file");
				}

			}
			if(methodExists)
			{
				fileName="temp";
			}else
			{
				fileName=controller.getCanonicalName();
			}  
		}else
		{

			fileName="temp";
		}
		File ajaxFile=new File(Utility.CONTROLLER_FOLDER+"/"+fileName.replace(".", "/")+".java");
		System.out.println("temp file is in :"+ajaxFile.getAbsolutePath());
		if(!ajaxFile.exists())
		{
			PrintWriter pp=new PrintWriter(ajaxFile);
			pp.flush();
			pp.close();
		}

		RandomAccessFile ajaxFileWriter=new RandomAccessFile(ajaxFile, "rw");
		//  ajaxFileWriter.re
		if(ajaxFile.length()!=0)
		{
			ajaxFileWriter.seek(ajaxFile.length()-3);
		}
		ajaxFileWriter.writeBytes("@RequestMapping(value = \"ajax/"+type.getSimpleName().toLowerCase()+"/byparent/{parentId}\", method = RequestMethod.GET)"+NEWLINE);
		ajaxFileWriter.writeBytes("public String ajax"+type.getSimpleName()+"(@pathVariable Long parentId) {"+NEWLINE);
		ajaxFileWriter.writeBytes("return  "+Utility.toCamelCase(type.getSimpleName())+"Service.findByParent(parentId);"+NEWLINE+"}"+NEWLINE);
		ajaxFileWriter.writeBytes("\n\n\n\n\n");
		urls.append(Utility.createUrls("Ajax-"+type.getSimpleName()+"ByParent",
				type.getSimpleName().toLowerCase()+"/ajax"+type.getSimpleName().toLowerCase()+"/byparent",
				"false", "1"));
		ajaxFileWriter.close();
	}

		

	}
