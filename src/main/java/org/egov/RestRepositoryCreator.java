package org.egov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.atteo.evo.inflector.English;
import org.ja.annotation.DrillDown;
import org.ja.annotation.DrillDownTable;




public class RestRepositoryCreator {

	PojoHolder pojoHolder=new PojoHolder();
	private Field[] declaredFields;
	private Set<String> serviceSet=new HashSet<String>();
	
	private List<String> childList=new ArrayList<String>();
	public static void main(String[] args) {
		
		RestRepositoryCreator rc=new RestRepositoryCreator();
		rc.createService("org.egov.egf.master.domain.model.Bank");
	}
	//this is to standardize only 
	public void	create(String fullyQualifiedName)
	{	
		createService(fullyQualifiedName);
		
		for(int i = 0; i < childList.size(); i++)
		{
			
			createService(childList.get(i));
			
		 
		}
	}
	

	public void	createService(String fullyQualifiedName)
	{	
	try {
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		
		declaredFields=	pojo.getDeclaredFields();
		 for(Field f:declaredFields)
			{
				String name = f.getName();
				if(name.equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}

		if(f.isAnnotationPresent(DrillDown.class)  )
		{
			
			
			childList.add(f.getType().getName());
			 
		}
		if(  f.isAnnotationPresent(DrillDownTable.class))
		{
			childList.add(Utility.getEnclosingType(f).getName());
		}
		
		}

		
		
		String refFileName="org.egov.egf.master.domain.repository.FundRepository";
		PrintWriter sqlWriter;
	 
			String contractPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/"+Utility.SUBMODULE_IDENTIFIER+"/domain/repository/";
			//String contractPackage = "org.egov."+Utility.MODULEIDENTIFIER+".persistence.queue.contract";
			String contractFileName = contractPackageDir+pojo.getSimpleName()+"Repository.java";
			File ff=Utility.createNewFile(contractFileName);
			
			sqlWriter = new PrintWriter(contractFileName, "UTF-8");
	       // String fileName=Utility.SRCFOLDER+"/"+refFileName.replace(".", "/")+".java";
			String fileName="/home/mani/Workspaces/ms/egov-services/financials/egf-master/src/main/java/org/egov/egf/master/domain/repository/FundRepository.java";
	        String entityPakage= fullyQualifiedName.substring(0,fullyQualifiedName.lastIndexOf("."));
	        File file=new File(fileName);
			//Scanner sc=new Scanner(fileName);
		System.out.println(fileName);	
		 String content = new Scanner(file).useDelimiter("\\Z").next(); 
		 String name = pojo.getSimpleName();
		 String p = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".")+1,fullyQualifiedName.length());
		  
		
		 String contractContent = content;//.replaceAll(maping1, replace);
		 System.out.println("    now "+contractContent);
		  contractContent = contractContent.replace("Fund",name);
		 System.out.println(contractContent);
		 contractContent = contractContent.replace("fund", Utility.toCamelCase(name));
		 System.out.println(contractContent);
		 
		 contractContent = contractContent.replace("master", Utility.SUBMODULE_IDENTIFIER);
		 
	//	 contractContent = contractContent.replace("Banks",English.plural(name));
		 
		 
		 
		 String ss=name+"s";
		 System.out.println(ss);
		 contractContent = contractContent.replace(ss,English.plural(name));
		 contractContent = contractContent.replace(Utility.toCamelCase(ss),English.plural(name).toLowerCase());
		 System.out.println(contractContent);
		 contractContent = contractContent.replace("ContractContract", "Contract");
		 
		 
		 
		 
		 getSaveMethod(pojo);
	
		  sqlWriter.write(contractContent);
		  sqlWriter.flush();
		  sqlWriter.close();
		  
	 
			  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	
	}
	
	
	private String getSaveMethod(Class<?> pojo) {
		SB saveMethod=new SB();
		
		/*@Transactional
		public Voucher save(Voucher voucher) {*/
		saveMethod.a("@Transactional").n()
		.as("public").as(pojo.getSimpleName()).a("save(").as(pojo.getSimpleName()).a(Utility.toCamelCase(pojo.getSimpleName())).a("){").n();
		
		for(Field f:pojo.getDeclaredFields())
		{
			
			
			if(f.isAnnotationPresent(DrillDown.class)  )
			{
				
				/*VouchermisEntity misEntity = new VouchermisEntity().toEntity(voucher.getVouchermis());
				misEntity.setVoucherId(savedVoucher.getId());
				Vouchermis savedMis=vouchermisJdbcRepository.create(misEntity).toDomain();
				savedVoucher.setVouchermis(savedMis);*/
				
				saveMethod.a(f.getType().getSimpleName()).as("Entity").a(f.getName()).as("Entity")
				.a("= new ").a(f.getType().getSimpleName()).a("Entity()").a(".toEntity").a(Utility.toCamelCase(pojo.getSimpleName()))
				.a(".get").a(Utility.toSentenceCase(f.getName())).a("();").n();
				
				saveMethod.a(f.getName()).a("Entity").a(".set").a(pojo.getSimpleName()).a("Id(saved").a(pojo.getSimpleName()).a("getId());").n();
				
				saveMethod.as(f.getType().getSimpleName()).a("saved").a(f.getType().getSimpleName())
				.a("= ").a(Utility.toCamelCase(f.getType().getSimpleName())).a("JdbcRepository.create(").a(f.getName()).as("Entity")
				.a(").toDomain();").n();
				
				saveMethod.a("saved").a(pojo.getSimpleName()).a(".set(saved").a(f.getType().getSimpleName()).a(");").n();
				
			}
			if(  f.isAnnotationPresent(DrillDownTable.class))
			{
				
				childList.add(Utility.getEnclosingType(f).getName());
			}
		
		}
		
		
		return "";
	}
	
	
	
	

}
