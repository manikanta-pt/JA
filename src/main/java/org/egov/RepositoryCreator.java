package org.egov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

public class RepositoryCreator {


	PojoHolder pojoHolder=new PojoHolder();

	public static void main(String[] args) {
		RepositoryCreator rc=new RepositoryCreator();
		rc.createRepository("org.egov.egf.entity.Bank");

	}

	public void	createRepository(String fullyQualifiedName) 
	{
		try {
			pojoHolder.loadPojo(fullyQualifiedName);
			Class<?> pojo = pojoHolder.getPojo();
			Package package1 = pojo.getPackage();
			String name = package1.getName();
			String packageName = name.substring(name.indexOf("package")+1,name.lastIndexOf("."));
			packageName=packageName+".repository";
			String fileName=pojo.getSimpleName()+"Repository";
			//File f=new File(name);
			File f = new File(Utility.SRCFOLDER+"/"+packageName.replace(".", "/"));
			if (f.exists() && f.isDirectory()) {
				System.out.println("folder exists");
			}else
			{
				f.mkdir();
			}

			PrintWriter writer = new PrintWriter(Utility.SRCFOLDER+"/"+packageName.replace(".", "/")+"/" +fileName+".java", "UTF-8");
			writer.write("package "+packageName+";"+Utility.NEWLINE);
			writer.write(Utility.NEWLINE);
			writer.write(Utility.NEWLINE);
			writer.write("import "+name+"."+pojo.getSimpleName()+";"+Utility.NEWLINE);
			writer.write("import org.springframework.data.jpa.repository.JpaRepository;"+Utility.NEWLINE);
			writer.write("import org.springframework.data.jpa.repository.Query;"+Utility.NEWLINE);
			writer.write("import org.springframework.stereotype.Repository;"+Utility.NEWLINE);

			writer.write(Utility.NEWLINE);
			writer.write(Utility.NEWLINE);
			
			Class<?> type=null;
			String idType="Long";
			try {
				Field declaredFieldId = pojo.getDeclaredField("id");
				type = declaredFieldId.getType();
				idType = type.getName();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			 
			writer.write("@Repository "+Utility.NEWLINE);
			writer.write("public interface "+fileName+" extends JpaRepository<"+pojo.getSimpleName()+","+idType+"> {"+Utility.NEWLINE);
			writer.write(Utility.NEWLINE);
		   //writing specific methods
			try {
				if(pojo.getField("name")!=null)
				{
					writer.write(pojo.getSimpleName()+" "+"findByName(String name);"+Utility.NEWLINE);
					writer.write(Utility.NEWLINE);
				}
			} catch (Exception e) {
				System.err.println("error while writing findByName api");
			} 
			try {	
				if(pojo.getField("code")!=null)
				{
					writer.write(pojo.getSimpleName()+" "+"findByCode(String code);"+Utility.NEWLINE);
					writer.write(Utility.NEWLINE);
				}
			} catch (Exception e) {
				System.err.println("error while writing findByCode api");
			}  

			writer.write("}");
			writer.flush();
			writer.close();



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
}
