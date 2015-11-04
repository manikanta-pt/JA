package org.egov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.List;

 


public class ServiceCreator {

	PojoHolder pojoHolder=new PojoHolder();
	public static void main(String[] args) {
		
		ServiceCreator rc=new ServiceCreator();
		rc.createService("org.egov.tl.domain.entity.FeeMatrixDetail");
	}

	public void	createService(String fullyQualifiedName)
	{	
	try {
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		Package package1 = pojo.getPackage();
		String name = package1.getName();
		String packageName = name.substring(name.indexOf("package")+1,name.lastIndexOf("."));
		String repositoryPackage=packageName+".repository";
		packageName=packageName+".service";
		String fileName=pojo.getSimpleName()+"Service";
		String repositoryName=pojo.getSimpleName()+"Repository";
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
		
		
		writer.write("import java.util.List;"+Utility.NEWLINE);
		writer.write(Utility.NEWLINE);
		writer.write(Utility.NEWLINE);

		writer.write("import javax.persistence.EntityManager;"+Utility.NEWLINE);
		writer.write("import javax.persistence.PersistenceContext;"+Utility.NEWLINE);
		writer.write(Utility.NEWLINE);
		writer.write("import "+name+"."+pojo.getSimpleName()+";"+Utility.NEWLINE);
		writer.write("import "+repositoryPackage+"."+repositoryName+";"+Utility.NEWLINE);
		writer.write(Utility.NEWLINE);
		writer.write("import org.springframework.beans.factory.annotation.Autowired;"+Utility.NEWLINE);
		writer.write("import org.springframework.data.domain.Sort;"+Utility.NEWLINE);
		writer.write("import org.springframework.stereotype.Service;"+Utility.NEWLINE);
		writer.write("import org.springframework.transaction.annotation.Transactional;"+Utility.NEWLINE);
		
		writer.write(Utility.NEWLINE);
		writer.write(Utility.NEWLINE);
		
		writer.write("@Service "+Utility.NEWLINE);
		writer.write("@Transactional(readOnly = true)"+Utility.NEWLINE);
		writer.write("public class "+fileName+"  {"+Utility.NEWLINE);
		writer.write(Utility.NEWLINE);
		writer.write(Utility.TAB+"private final "+repositoryName+" "+Utility.toCamelCase(repositoryName)+";"+Utility.NEWLINE);
		writer.write(Utility.TAB+"@PersistenceContext"+Utility.NEWLINE+"private EntityManager entityManager;"+Utility.NEWLINE);
		writer.write(Utility.NEWLINE);
		writer.write(Utility.TAB+"@Autowired"+Utility.NEWLINE+"public "+fileName+"(final "+repositoryName+" "
						+ Utility.toCamelCase(repositoryName)+") {"+Utility.NEWLINE);
		
		writer.write(Utility.TAB+" this."+Utility.toCamelCase(repositoryName)+" = "+Utility.toCamelCase(repositoryName)+";"+Utility.NEWLINE);
		writer.write("  }"+Utility.NEWLINE);
		writer.write(Utility.NEWLINE);
		
		writer.write(Utility.TAB+" @Transactional"+Utility.NEWLINE);
		writer.write(Utility.TAB+" public "+pojo.getSimpleName()+" create(final "+pojo.getSimpleName()+" "
					+Utility.toCamelCase(pojo.getSimpleName())+") {"+Utility.NEWLINE);
		writer.write(Utility.TAB+"return "+Utility.toCamelCase(repositoryName)+".save("+Utility.toCamelCase(pojo.getSimpleName())+");"+Utility.NEWLINE);
		writer.write("  } "+Utility.NEWLINE);
		
		writer.write(Utility.TAB+" @Transactional"+Utility.NEWLINE);
		writer.write(Utility.TAB+" public "+pojo.getSimpleName()+" update(final "+pojo.getSimpleName()+" "
					+Utility.toCamelCase(pojo.getSimpleName())+") {"+Utility.NEWLINE);
		writer.write(Utility.TAB+"return "+Utility.toCamelCase(repositoryName)+".save("+Utility.toCamelCase(pojo.getSimpleName())+");"+Utility.NEWLINE);
		writer.write(Utility.TAB+"  } "+Utility.NEWLINE);
		
		writer.write(Utility.TAB+"public List<"+pojo.getSimpleName()+"> findAll() {"+Utility.NEWLINE);
		writer.write(Utility.TAB+" return "+Utility.toCamelCase(repositoryName)+".findAll(new Sort(Sort.Direction.ASC, \"name\"));"+Utility.NEWLINE);
		writer.write(Utility.TAB+"   }"+Utility.NEWLINE);

		writer.write(Utility.TAB+"public "+pojo.getSimpleName()+" "+"findByName(String name){"+Utility.NEWLINE);
		writer.write(Utility.TAB+"return"+Utility.toCamelCase(repositoryName)+".findByName(name);"+Utility.NEWLINE);
		writer.write(Utility.TAB+"}");
		writer.write(Utility.NEWLINE);
		Field code=null;
		try {
			code = pojo.getDeclaredField("code");
		} catch (NoSuchFieldException e) {
						
		} catch (SecurityException e) {
			
		}
		if(code!=null)
		{
			writer.write(Utility.TAB+"public "+pojo.getSimpleName()+" "+"findByCode(String code){"+Utility.NEWLINE);
			writer.write(Utility.TAB+"return"+Utility.toCamelCase(repositoryName)+".findByCode(code);"+Utility.NEWLINE);
			writer.write(Utility.TAB+"}");
			writer.write(Utility.NEWLINE);
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
