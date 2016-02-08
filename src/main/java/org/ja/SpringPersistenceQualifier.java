package org.ja;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.egov.PojoHolder;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringPersistenceQualifier {
	public static String module;
	public static String basedir="/home/mani/Workspaces/github_phoenix/eGov/egov/";
	public static String workingDirName=basedir+"/egov-"+module+"/src/main/java";
	public static String webpackage=basedir+"/egov-"+module+"web";
	public static String xmlLoc=basedir+"/egov-"+module+"/src/main/resources/config/spring/";
	private static PrintWriter springPrototype;
	private static List<String> serviceBeans=new ArrayList<String>();
	
	
     final static String importStatement = "import org.springframework.beans.factory.annotation.Qualifier;";




	public static void main(String[] args) {
		module="tl";
		findBeans(args);
		/*module="egf";
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
		findBeans(args);*/
	}

	private static void findBeans(String[] args) {
		try {

			basedir="/home/mani/Workspaces/github_phoenix/eGov/egov/";
			workingDirName=basedir+"/egov-"+module+"/src/main/java";
			webpackage=basedir+"/egov-"+module+"web";
			String webDirName=webpackage+"/src/main/java";
			xmlLoc=basedir+"/egov-"+module+"/src/main/resources/config/spring/";

			serviceBeans=new ArrayList<String>();
			findByService(args,workingDirName);
			findByService(args,webDirName);
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private static void findByService(String[] args,String dirName) {
		try {

			File workingDir=new File(dirName);
			Process proc = Runtime.getRuntime().exec("./qualifier.sh",args,workingDir);
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

			//remove duplicates
			Map<String,String> serviceMap=new HashMap<String,String>();
			
			for(String ss:fileByService)
			{
				String substring = ss.substring(2, ss.indexOf(".java"));	
				String beanName = substring.replaceAll("/", ".");
				System.out.println("beanName"+beanName);
				
				
				serviceMap.put(beanName, beanName);
			}
			
       
 int i=0;
			for(String ss:serviceMap.keySet())
				
			{
				
				 serviceBeans=new ArrayList<String>();
				 Boolean importStatementAdded = false;
				
				//String substring = ss.substring(2, ss.indexOf(".java"));	
				String beanName = ss;
				//System.out.println("beanName"+beanName);

				PojoHolder pojo=new PojoHolder();
		
				boolean isProto = containsPersistenceService(pojo, beanName);
				if(isProto)
				{
                   boolean importPresesnt=false;
                   boolean qualifieradded=false;
                   
					//String backUpfileName="/home/" + System.getProperty("user.name")+"/qualify.java";
					File fileName=new File(dirName+"/"+beanName.replace(".","/")+".java");
					String backUpfileName="/home/" + System.getProperty("user.name")+"/"+beanName.substring(beanName.lastIndexOf(".")+1,beanName.length())+i+".java";
					i++;
					final RandomAccessFile file1 = new RandomAccessFile(fileName, "r");
					final RandomAccessFile file2 = new RandomAccessFile(backUpfileName, "rw");
					String  readLine=file1.readLine();
					String prev="";
					while (readLine!=null)
					{
						for(String service:serviceBeans)
						{
							if(readLine.contains(service) && (readLine.contains("private") || readLine.contains("public") ||  readLine.contains("protected") )
									&& prev.contains("@Autowired") && !prev.contains("@Qualifier")	)
							{
								file2.writeBytes("@Qualifier(\""+service+"\")");
								file2.writeBytes("\n");
								qualifieradded=true;
								break;
							}
						}
						 if (readLine.equalsIgnoreCase(importStatement))
	                            importPresesnt = true;
	                      
						file2.writeBytes(readLine);
						file2.writeBytes("\n");
						prev=readLine;
						readLine=file1.readLine();
					}
					file2.close();
					file1.close();
					
					File srcFile=new File(backUpfileName);
					String content = new Scanner(srcFile).useDelimiter("\\Z").next();
					StringBuffer contentBuffer=new StringBuffer(content);
					
					if(!importPresesnt && qualifieradded)
					{
						contentBuffer.insert((contentBuffer.indexOf("import")-(6)),importStatement);
						
					}
					
					
					PrintWriter orginal=new PrintWriter(fileName);
					orginal.write(contentBuffer.toString());
					orginal.flush();
					orginal.close();


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



	private static boolean containsPersistenceService(PojoHolder pojoholder, String name) {
		pojoholder.loadPojo(name);
		Class<?> pojo2 = pojoholder.getPojo();
		boolean isProto=false;

		System.out.println(pojoholder.getPojo().getSuperclass() +"    "+name);


		Field[] declaredFields = pojoholder.getPojo().getDeclaredFields();
		for(Field f:declaredFields)
		{
			System.out.println("--------"+f.getName()+"--------------");
			if(f.getName().equalsIgnoreCase("PersistenceService"))
			{
				serviceBeans.add(f.getName());
				isProto=true;
			}
			else if(f.getType().getName().contains("PersistenceService"))
			{
				serviceBeans.add(f.getName());
				isProto=true;
			}
			else if(f.getType().getSuperclass()!=null && f.getType().getSuperclass().getName().contains("PersistenceService"))
			{
				isProto=true;
				serviceBeans.add(f.getName());
			}


		}

		return isProto;
	}



}
