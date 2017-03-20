package org.ja;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import org.egov.Utility;

public class BuilderGenerator {
	String fullclassname;
	String packageName;
	String className;
	String relativePath;
	String basepackage=Utility.PROJECTHOME+"/src/test/java/";//"/home/mani/Workspaces/github_phoenix/eGov/egov/egov-commons/src/test/java/";

	public static void main(String[] args)  {
		BuilderGenerator bg=new BuilderGenerator();
		bg.generate(args);
	}

	private  void splitPackageClassName() {
	String[] split = fullclassname.split("\\.");
	StringBuffer relPath=new StringBuffer(100);
	
	for(String s:split)
	{
		System.out.println(s);
		relPath.append("/"+s);
		
	}
	relativePath=relPath.toString();
	packageName=fullclassname.substring(0,fullclassname.lastIndexOf("."));//returns.java
	System.out.println(packageName);
	//packageName=fullclassname.substring(0,fullclassname.lastIndexOf(".")-1);//returns just the package
	className=fullclassname.substring(fullclassname.lastIndexOf(".")+1,fullclassname.length());
	}
	
	
	private void generate(String[] args)
	{
		/*if(args[0].isEmpty())
		{
			throw new RuntimeException("provide fully qualified classname");
			
		}else*/
		{
			fullclassname="org.egov.egf.persistence.queue.contract.FundContract";  //Change the classname here and run
			splitPackageClassName();
		}
		
		
		try {
			String builderClassname=className+"Builder";
			String fileName = relativePath;
			System.out.println("-------"+fileName);
			FileWriter fr=null;
			try {
				File file=new File(basepackage+fileName+"Builder.java");
				System.out.println(file.getAbsolutePath());
				fr = new FileWriter(file);
			} catch (Exception e) {
				File file=new File(basepackage+builderClassname+".java");
				System.out.println(file.getAbsolutePath());
				fr = new FileWriter(file);
			}
			fr.write("package "+packageName+";\n\n");
			fr.write("import java.lang.reflect.Field;\n\n");
			fr.write("public class "+builderClassname +" {\n\n");
			
			String objName=className.substring(0,1).toLowerCase()+className.substring(1,className.length());
			System.out.println(objName);
			
			fr.write("private "+className+" "+objName+";\n\n");
			fr.write("//use this count where unique names,descriptions etc required \n");
			fr.write("private static int count;\n");
			
			fr.write("public "+builderClassname+"()\n{\n "+objName+"=new "+className+"();\n count++;\n}\n");
			
			
			Class<?> loadedClass = Class.forName(fullclassname);
			Field[] declaredFields = loadedClass.getDeclaredFields();
			for(Field f:declaredFields)
			{
				if(f.getName().equalsIgnoreCase("serialVersionUID"))
					continue;
			   String withName=	f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length());
				fr.write(" public "+builderClassname+" with"+withName+"(" +f.getType().getSimpleName()+"  "+f.getName()+"){\n");
				fr.write(objName+".set"+withName+"("+f.getName()+");\n  return this; \n } \n\n");
					
			}
			//for id 
			fr.write("public "+builderClassname+" withId(long id) {\n");
			fr.write(" try {\n ");
			fr.write(" Field idField  = "+objName+".getClass().getSuperclass().getDeclaredField(\"id\");\n");
			fr.write("   idField.setAccessible(true);\n    idField.set("+objName+", id); \n");
			fr.write("        } catch (Exception e) {\n    throw new RuntimeException(e); \n } \n   return this;\n   }\n\n");
			
			// for defaults
			
			fr.write("public "+builderClassname+" withDefaults() {\n");
			fr.write("withId(count);\n");
			for(Field f:declaredFields)
			{
				String withName=	f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length());
				if(f.getName().equalsIgnoreCase("serialVersionUID"))
					continue;
				fr.write("if(null=="+objName+".get"+withName+"()){\n");
				fr.write("with"+withName+"("+f.getName()+"); \n");
				fr.write("}\n");
			}
			
			
			fr.write("return this; \n } \n\n");
			
			//For dbdefaults
			
			fr.write("public "+builderClassname+" withDbDefaults() {\n");
			
			for(Field f:declaredFields)
			{
				String withName=	f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length());
				if(f.getName().equalsIgnoreCase("serialVersionUID"))
					continue;
				fr.write("if(null=="+objName+".get"+withName+"()){\n");
				fr.write("with"+withName+"("+f.getName()+"); \n");
				fr.write("}\n\n");
			}
			
			
			fr.write("return this; \n } \n\n");
			
			
			fr.write("public "+className+" build() {\n return "+objName+";\n } \n }");
			fr.flush();
			fr.close();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
