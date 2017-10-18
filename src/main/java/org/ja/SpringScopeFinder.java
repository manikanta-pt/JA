package org.ja;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.egov.PojoHolder;
import org.egov.Utility;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SpringScopeFinder {
	public static String module;
	public static String basedir="/home/mani/Workspaces/github_phoenix/eGov/egov/";
	public static String workingDirName=basedir+"/egov-"+module+"/src/main/java";
	public static String webpackage=basedir+"/egov-"+module+"web";
	public static String xmlLoc=basedir+"/egov-"+module+"/src/main/resources/config/spring/";
	private static PrintWriter springPrototype;
	private static List<String> serviceBeans=new ArrayList<String>();
	private static List<String> localxmlBeans=new ArrayList<String>();
	private static List<String> globalxmlBeans=new ArrayList<String>();




	public static void main(String[] args) {
		module="tl";
		findBeans(args);
		module="egf";
		findBeans(args);
		module="adtax";
		findBeans(args);
		module="ptis";
		findBeans(args);
		module="egi";
		findBeans(args);
		module="eis";
		findBeans(args);
		module="wtms";
		findBeans(args);
		module="collection";
		findBeans(args);
		module="restapi";
		findBeans(args);
		module="pgr";
		findBeans(args);
		module="assets";
		findBeans(args);
		//module="works";
		//findBeans(args);
		module="portal";
		findBeans(args);
		module="demand";
		findBeans(args);
	}

	private static void findBeans(String[] args) {
		try {

			basedir="/home/mani/Workspaces/github_phoenix/eGov/egov/";
			workingDirName=basedir+"/egov-"+module+"/src/main/java";
			webpackage=basedir+"/egov-"+module+"web";
			xmlLoc=basedir+"/egov-"+module+"/src/main/resources/config/spring/";
			
		   serviceBeans=new ArrayList<String>();
		   localxmlBeans=new ArrayList<String>();
		   globalxmlBeans=new ArrayList<String>();

			springPrototype = new PrintWriter(new File(workingDirName+"/"+module+"-prototypeservices.log"));
			findByService(args);
			springPrototype.write("@     service"+"\n");
			for(String bean:serviceBeans)
			{
				springPrototype.write(bean+"\n");
			}
			springPrototype.write("\n");

			findByXML(xmlLoc+"applicationContext-"+module+".xml",localxmlBeans);
			findByXML(xmlLoc+"applicationContext-"+module+"-global-services.xml",globalxmlBeans);

			springPrototype.write("---------------------------applicationContext-"+module+".xml----------\n");
			for(String bean:localxmlBeans)
			{
				if(serviceBeans.contains(bean))
				{
					springPrototype.write(bean+" is present in both @     service and applicationContext-module.xml\n");	
				}

				springPrototype.write(bean+"\n");
			}
			springPrototype.write("\n");
			springPrototype.write("---------------------------applicationContext-"+module+"-global-services.xml----------\n");
			for(String bean:globalxmlBeans)
			{

				if(serviceBeans.contains(bean))
				{
					springPrototype.write(bean+" is present in both @          service and applicationContext-module-globalservice.xml\n");	
				}	

				if(localxmlBeans.contains(bean))
				{
					springPrototype.write(bean+" is present in both applicationContext-module.xml and applicationContext-module-globalservice.xml\n");	
				}

				springPrototype.write(bean+"\n");
			}



			springPrototype.flush();
			springPrototype.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void findByXML(String fileName,List<String> xmls) {
		try {
			File fXmlFile = new File(fileName);
			if(fXmlFile==null)
				return;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			PojoHolder pojoholder=new PojoHolder();
			NodeList elementsByTagName = doc.getElementsByTagName("bean");
			boolean isProto=false;

			for (int temp = 0; temp < elementsByTagName.getLength(); temp++) {

				Node nNode = elementsByTagName.item(temp);

				//System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					String name = eElement.getAttribute("class");
					String scope = eElement.getAttribute("scope");
					String parent = eElement.getAttribute("parent");
					String id = eElement.getAttribute("id");

					System.out.println("id : " +id +" class:"+name+" parent:"+parent+" id:"+id+" scope:"+scope);

					if(serviceBeans.contains(name))
					{
						//System.out.println("a serviceBeans bean "+ name +"is defined as service as singleton and also declared in xml");
						if(scope==null )
						{

							System.out.println("a prototype bean "+name +"is defined as @          service  singleton and also declared in xml with scope singleton");
						}else
						{
							System.out.println("a prototype bean "+name +"is defined as @          service as singleton and also declared in xml with scope "+scope);
						}
					}else
					{
						if(name!=null && !name.isEmpty())
						{
							isProto = isPrototype(pojoholder, name);

						}
						if(isProto)
						{
							if(scope==null )
							{

								System.out.println("a serviceBeans bean "+name +"is defined as service as singleton and also declared in xml with scope singleton");
							}else if(scope.equals(ConfigurableBeanFactory.SCOPE_PROTOTYPE))
							{

							}
							else
							{
								System.out.println("a  bean "+name +" is defined as singleton and also declared in xml with scope "+scope);
								xmls.add(name);
							}



						}

					}


				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 


	}

	private static void findByService(String[] args) {
		try {

			File workingDir=new File(workingDirName);
			Process proc = Runtime.getRuntime().exec("./mk.sh",args,workingDir);
			List<String> fileByService=new ArrayList<String>();
			//System.out.println(exec.getOutputStream());


			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(proc.getErrorStream()));

			// read the output from the command

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				fileByService.add(s);
			}



			for(String ss:fileByService)
			{
				String substring = ss.substring(2, ss.indexOf(".java"));	
				String beanName = substring.replaceAll("/", ".");
				System.out.println("beanName"+beanName);

				PojoHolder pojo=new PojoHolder();
				String name=beanName;
				boolean isProto = isPrototype(pojo, name);
				if(isProto)
				{
					Scope declaredAnnotation = pojo.getPojo().getDeclaredAnnotation(Scope.class);


					if(declaredAnnotation==null)
					{
						serviceBeans.add(name);
					}else
					{
						String value = pojo.getPojo().getAnnotation(Scope.class).value();
						System.out.println("value   "+value+" for "+name);
						if(ConfigurableBeanFactory.SCOPE_PROTOTYPE.equals(value))
						{
							System.out.println("found serviceBeans bean with name "+name +"and it is already serviceBeans");
						}
						else
						{
							System.out.println("found serviceBeans bean with name "+name +"and it is not serviceBeans");
							serviceBeans.add(name);	
						}
					}

				}

			}


		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean isPrototype(PojoHolder pojoholder, String name) {
		pojoholder.loadPojo(name);
		Class<?> pojo2 = pojoholder.getPojo();
		boolean isProto=false;

		System.out.println(pojoholder.getPojo().getSuperclass() +"    "+name);
		if(pojoholder.getPojo().getSuperclass()!=null && pojoholder.getPojo().getSuperclass().getSimpleName().equalsIgnoreCase("PersistenceService"))
		{
			//Any thing of persistence service should be serviceBeans
			isProto=true;

		}else
		{
			Field[] declaredFields = pojoholder.getPojo().getDeclaredFields();
			for(Field f:declaredFields)
			{
				//Any thing which contains field as model should be serviceBeans
				if(Utility.isModel(f.getType()))
				{
					if(!serviceBeans.contains(name))
					{
						isProto=true;
					}
				}
				if(Utility.isBasicType(f))
				{
					if(!serviceBeans.contains(name))
					{
						isProto=true;
					}
				}
				
				if(Utility.isCollection(f))
				{
					if(!serviceBeans.contains(name))
					{
						isProto=true;
					}
				}

			}
		}
		return isProto;
	}



}
