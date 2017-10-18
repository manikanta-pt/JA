package org.egov;

import static org.egov.Utility.NEWLINE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.atteo.evo.inflector.English;




public class ServiceCreator {

	PojoHolder pojoHolder=new PojoHolder();
	private Field[] declaredFields;
	private Set<String> serviceSet=new HashSet<String>();
	public static void main(String[] args) {
		
		ServiceCreator rc=new ServiceCreator();
		rc.createService("org.egov.workflow.persistence.entity.WorkflowMatrix");
	}
	//this is to standardize only 
	public void	create(String fullyQualifiedName)
	{	
		createService(fullyQualifiedName);
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
		String specificationPackage=packageName+".specification";
		String contractPackage=packageName+".queue.contract";
		packageName=packageName+".service";
		packageName.replace("persistence", "domain");
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
		System.out.println(f.getAbsolutePath());
		
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
		writer.write("import "+specificationPackage+"."+pojo.getSimpleName()+"Specification;"+Utility.NEWLINE);
		writer.write("import "+contractPackage+"."+pojo.getSimpleName()+"ContractRequest;"+Utility.NEWLINE);
		writer.write(Utility.NEWLINE);
		writer.write("import org.springframework.beans.factory.annotation.Autowired;"+Utility.NEWLINE);
		writer.write("import org.springframework.data.domain.Sort;"+Utility.NEWLINE);
		writer.write("import org.springframework.stereotype.Service;"+Utility.NEWLINE);
		writer.write("import org.springframework.transaction.annotation.Transactional;"+Utility.NEWLINE);
		
		writer.write("import org.modelmapper.ModelMapper;"+Utility.NEWLINE);
		
		writer.write("import org.springframework.data.domain.Page;"+Utility.NEWLINE);
		writer.write("import org.springframework.data.domain.PageRequest;"+Utility.NEWLINE);
		writer.write("import org.springframework.data.domain.Pageable;"+Utility.NEWLINE);
		writer.write("import org.springframework.data.domain.Sort;"+Utility.NEWLINE);
		
		if(Utility.ADD_VALIDATE)
		{
			writer.write("import org.springframework.util.Assert;"+Utility.NEWLINE);
			writer.write("import org.springframework.validation.BindingResult;"+Utility.NEWLINE);
			writer.write("import org.springframework.validation.ObjectError;"+Utility.NEWLINE);
			writer.write("import org.springframework.validation.SmartValidator;"+Utility.NEWLINE);
			writer.write("import "+contractPackage+"."+pojo.getSimpleName()+"Contract;"+Utility.NEWLINE);
		}
		
		
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
		
		if(Utility.ADD_VALIDATE)
		{
			writer.write("@Autowired\n	private SmartValidator validator;");
		}
		
		String relatedContent= getRelatedContractContent(pojo);
		for(String service:serviceSet)
		{
			writer.write("@Autowired\n");
			writer.write("private "+Utility.toSentenceCase(service)+"   "+service+";");
		}
		
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
		Field fname=null;
		try {
			fname = pojo.getDeclaredField("name");
		} catch (NoSuchFieldException e) {
						
		} catch (SecurityException e) {
			
		}
		if(fname!=null)
		{
		writer.write(Utility.TAB+"public "+pojo.getSimpleName()+" "+"findByName(String name){"+Utility.NEWLINE);
		writer.write(Utility.TAB+"return "+Utility.toCamelCase(repositoryName)+".findByName(name);"+Utility.NEWLINE);
		writer.write(Utility.TAB+"}");
		writer.write(Utility.NEWLINE);
		}
		Field code=null;
		try {
			code = pojo.getDeclaredField("code");
		} catch (NoSuchFieldException e) {
						
		} catch (SecurityException e) {
			
		}
		if(code!=null)
		{
			writer.write(Utility.TAB+"public "+pojo.getSimpleName()+" "+"findByCode(String code){"+Utility.NEWLINE);
			writer.write(Utility.TAB+"return "+Utility.toCamelCase(repositoryName)+".findByCode(code);"+Utility.NEWLINE);
			writer.write(Utility.TAB+"}");
			writer.write(Utility.NEWLINE);
		}
		
		writer.write(Utility.TAB+"public "+pojo.getSimpleName()+" "+"findOne(Long id){"+Utility.NEWLINE);
		writer.write(Utility.TAB+"return "+Utility.toCamelCase(repositoryName)+".findOne(id);"+Utility.NEWLINE);
		writer.write(Utility.TAB+"}");
		writer.write(Utility.NEWLINE);
		
		//this part will create for generic search 
		writer.write(Utility.TAB+"public Page<"+pojo.getSimpleName()+"> "+"search("+pojo.getSimpleName()+"ContractRequest "+Utility.toCamelCase(pojo.getSimpleName())+"ContractRequest){"+Utility.NEWLINE);
		writer.write("final "+pojo.getSimpleName()+"Specification specification = new "+pojo.getSimpleName()+"Specification("+Utility.toCamelCase(pojo.getSimpleName())+"ContractRequest.get"+pojo.getSimpleName()+"());"+Utility.NEWLINE);
		
		writer.write("Pageable page = new PageRequest("+Utility.toCamelCase(pojo.getSimpleName())+"ContractRequest.getPage().getOffSet(),"+Utility.toCamelCase(pojo.getSimpleName())+"ContractRequest.getPage().getPageSize());"+Utility.NEWLINE);
		writer.write(Utility.TAB+"return "+Utility.toCamelCase(repositoryName)+".findAll(specification,page);"+Utility.NEWLINE);
		writer.write(Utility.TAB+"}");
		writer.write(Utility.NEWLINE);
		
		String content="public BindingResult validate(BankContractRequest bankContractRequest, String method,BindingResult errors) { #	 #		try { #			switch(method) #			{ #			case \"update\": #				Assert.notNull(bankContractRequest.getBank(), \"Bank to edit must not be null\"); #				validator.validate(bankContractRequest.getBank(), errors); #				break; #			case \"view\": #				//validator.validate(bankContractRequest.getBank(), errors); #				break; #			case \"create\": #				Assert.notNull(bankContractRequest.getBanks(), \"Banks to create must not be null\"); #				for(BankContract b:bankContractRequest.getBanks()) #				 validator.validate(b, errors); #				break; #			case \"updateAll\": #				Assert.notNull(bankContractRequest.getBanks(), \"Banks to create must not be null\"); #				for(BankContract b:bankContractRequest.getBanks()) #				 validator.validate(b, errors); #				break; #			default : validator.validate(bankContractRequest.getRequestInfo(), errors); #			} #		} catch (IllegalArgumentException e) { #			 errors.addError(new ObjectError(\"Missing data\", e.getMessage())); #		} #		return errors; # #	}";
		
		 String contractContent = content.replace("Bank",pojo.getSimpleName());
		 System.out.println(contractContent);
		 contractContent = contractContent.replace("bank", Utility.toCamelCase(pojo.getSimpleName()));
		 System.out.println(contractContent);
	//	 contractContent = contractContent.replace("Banks",English.plural(name));
		 String ss=pojo.getSimpleName()+"s";
		 System.out.println(ss);
		 contractContent = contractContent.replace(ss,English.plural(pojo.getSimpleName()));
		 contractContent = contractContent.replace("#","\n");
		 System.out.println(contractContent);
		 
		 writer.write(contractContent+Utility.NEWLINE);
		 
		
		 writer.write(relatedContent);

		
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
	
	public String getRelatedContractContent(Class<?> pojo) {
		SB content=new SB();
		
		Field[] fields = pojo.getDeclaredFields();
		String entityName=pojo.getSimpleName();
		String pojoObject = Utility.toCamelCase(pojo.getSimpleName());
		boolean innerContentExists=false;
		content.a("public "+entityName+"ContractRequest 	fetchRelatedContracts("+entityName+"ContractRequest "+pojoObject+"ContractRequest)")
		.a(NEWLINE).a("{").a(NEWLINE);
		SB contentInner=new SB();
		contentInner.a("ModelMapper model=new ModelMapper();").a(NEWLINE);
		contentInner.a("for("+pojo.getSimpleName()+"Contract "+pojoObject+":"+pojoObject+"ContractRequest.get")
		.a(English.plural(entityName)+"())").a(NEWLINE);
		contentInner.a("{").a(NEWLINE);
		
		
		for(Field f:fields)
		{
			if(Utility.findTypes(f).equals("l"))
					{
				innerContentExists=true;
				serviceSet.add(Utility.toCamelCase(f.getType().getSimpleName())+"Service");
				contentInner.a("if(").a(pojoObject).a(".").a("get").a(Utility.toSentenceCase(f.getName())).a("()!=null").a(")").a(NEWLINE);
				contentInner.a("{").a(NEWLINE);
				contentInner.a(f.getType().getSimpleName()).a(Utility.TAB).a(f.getName())
				.a("=").a(Utility.toCamelCase(f.getType().getSimpleName())).a("Service").a(".findOne(").a(pojoObject).a(".").a("get")
				.a(Utility.toSentenceCase(f.getName())).a("().getId());").a(NEWLINE);
				contentInner.a("if(").a(f.getName()).a("==null)").a(NEWLINE);
				contentInner.a("{").a(NEWLINE);
				String fieldName="\""+f.getName()+"\"";
				String messageKey="\""+f.getName()+".invalid\"";
				String defaultMessage="\" Invalid "+f.getName()+"\"";
				contentInner.a("throw new InvalidDataException("+fieldName+","+messageKey+","+defaultMessage+");").a(NEWLINE).a("}");
				contentInner.a("model.map("+f.getName()+","+pojoObject+".get"+Utility.toSentenceCase(f.getName())+"());").a(NEWLINE);
				contentInner.a("}").a(NEWLINE);
				}
		}
		contentInner.a("}").a(NEWLINE);
		
		contentInner.a(""+pojo.getSimpleName()+"Contract "+pojoObject+"="+pojoObject+"ContractRequest.get")
		.a(pojo.getSimpleName()+"();").a(NEWLINE);
		
		for(Field f:fields)
		{
			if(Utility.findTypes(f).equals("l"))
					{
				
				contentInner.a("if(").a(pojoObject).a(".").a("get").a(Utility.toSentenceCase(f.getName())).a("()!=null").a(")").a(NEWLINE);
				contentInner.a("{").a(NEWLINE);
				//for interservice change service to repository
				contentInner.a(f.getType().getSimpleName()).a(Utility.TAB).a(f.getName())
				.a("=").a(Utility.toCamelCase(f.getType().getSimpleName())).a("Service").a(".findOne(").a(pojoObject).a(".").a("get")
				.a(Utility.toSentenceCase(f.getName())).a("().getId());").a(NEWLINE);
				contentInner.a("if(").a(f.getName()).a("==null)").a(NEWLINE);
				contentInner.a("{").a(NEWLINE);
				String fieldName="\""+f.getName()+"\"";
				String messageKey="\""+f.getName()+".invalid\"";
				String defaultMessage="\" Invalid "+f.getName()+"\"";
				contentInner.a("throw new InvalidDataException("+fieldName+","+messageKey+","+defaultMessage+");").a(NEWLINE).a("}");
				contentInner.a("model.map("+f.getName()+","+pojoObject+".get"+Utility.toSentenceCase(f.getName())+"());").a(NEWLINE);
				contentInner.a("}").a(NEWLINE);
					}
		}
		if(innerContentExists)
			content.a(contentInner.str());
		
		content.a("return "+pojoObject+"ContractRequest;");
		content.a("}").a(NEWLINE);
		
		return content.str();
	}

}
