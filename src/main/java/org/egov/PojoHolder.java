package org.egov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.ja.annotation.AjaxCall;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;

public class PojoHolder {

	private Class<?> pojo;

	public void loadPojo(String fullyQualifiedName) {

		try {
			this.pojo = Class.forName(fullyQualifiedName);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}

	}

	private void readPojo()
	{
		Field[] fields = pojo.getFields();
		for(Field f:fields)
		{
			Class<?> type = f.getType();
			if(type.getName().equalsIgnoreCase(String.class.getCanonicalName()))
			{

			}
		}
	}

	public Class<?> getPojo() {
		return pojo;
	}

	public void setPojo(Class<?> pojo) {
		this.pojo = pojo;
	}

	public List<String> findAjaxCalls(PojoHolder pojoHolder)
	{
		List<String> ajaxCallsList=new ArrayList<String>();
		try {
			for(Field f:pojoHolder.getPojo().getDeclaredFields())
				
			if(f.isAnnotationPresent(AjaxCall.class))
			ajaxCallsList.add(f.getName());
			
			}
		catch(Exception e)
		{
		}
		return ajaxCallsList;
	}
	public String[] readImports(String fullFileName)
	{
		String[] imports=null;
		try {
			String fileFullPath = fullFileName;
			JavaDocBuilder builder = new JavaDocBuilder();

			builder.addSource(new FileReader( fileFullPath  ));


			JavaSource src = builder.getSources()[0];
			imports = src.getImports();


			for ( String imp : imports )
			{
				System.out.println(imp);
			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imports;

	}
	public String getServiceFor(String entityName)
	{
		String serviceName="";
		 serviceName = entityName.replace("entity", "service");
		serviceName+="Service";
		return serviceName;
	}
	
	public String getControllerFor(String entityName)
	{
		String controllerName="";
		controllerName = entityName.replace("entity", "controller");
		controllerName+="Controller";
		return controllerName;
	}

	public List<String> findSearchFields(String fullFileName) {
			List<String> searchFieldList=new ArrayList<String>();
			try {
				BufferedReader f=new BufferedReader(new FileReader(new File(fullFileName)));
				
				String readLine = f.readLine();
				while (readLine!=null)
				{
					if(readLine.contains("//search"))
							{
							readLine=f.readLine();
							searchFieldList.add(readLine);
							}
					else
					{
						readLine=f.readLine();
					}
							
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return searchFieldList;
		}
	


}
