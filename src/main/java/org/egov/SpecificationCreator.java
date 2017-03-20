package org.egov;

import static org.egov.Utility.NEWLINE;

import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.ja.annotation.Ignore;

public class SpecificationCreator {
	PojoHolder pojoHolder=new PojoHolder();
	
	
	public static void main(String[] args) {
		SpecificationCreator rc=new SpecificationCreator();
		rc.create("org.egov.egf.persistence.entity.BankBranch");

	}
	
	
	public void create(String fullyQualifiedName)    
	{
		pojoHolder.loadPojo(fullyQualifiedName);
		Class<?> pojo = pojoHolder.getPojo();
		SB spec=new SB();
		SB specImport=new SB();
		SB specClass=new SB();
		PrintWriter specificationWriter;
		try {
			String specificationPackageDir = Utility.SRCFOLDER+"/org/egov/"+Utility.MODULEIDENTIFIER+"/persistence/specification/";
			specificationWriter = new PrintWriter(specificationPackageDir+pojo.getSimpleName()+"Specification.java", "UTF-8");
	      
			spec.a("package ").a("org.egov."+Utility.MODULEIDENTIFIER+".persistence.specification").a(";").a(NEWLINE);
			
			String metaModel=pojo.getSimpleName()+"_";
			
			specImport.a("import java.util.ArrayList;").a(NEWLINE);
			specImport.a("import java.util.List;").a(NEWLINE);
			specImport.a("import javax.persistence.criteria.CriteriaBuilder;").a(NEWLINE);
			specImport.a("import javax.persistence.criteria.CriteriaQuery;").a(NEWLINE);
			specImport.a("import javax.persistence.criteria.Path;").a(NEWLINE);
			specImport.a("import javax.persistence.criteria.Predicate;").a(NEWLINE);
			specImport.a("import javax.persistence.criteria.Root;").a(NEWLINE);
			specImport.a("import org.springframework.data.jpa.domain.Specification;").a(NEWLINE);
			
			
			specImport.a("import org.egov."+Utility.MODULEIDENTIFIER+".persistence.entity."+pojo.getSimpleName()+";").a(NEWLINE);
		//	specImport.a("import "+pojo.getPackage()+"."+pojo.getSimpleName()+";").a(NEWLINE);
			specImport.a("import org.egov."+Utility.MODULEIDENTIFIER+".persistence.entity."+metaModel+";").a(NEWLINE);
			specImport.a("import org.egov."+Utility.MODULEIDENTIFIER+".web.contract."+pojo.getSimpleName()+"Contract;").a(NEWLINE);
			
			specClass.a("public class ").a(pojo.getSimpleName())
			.a("Specification  implements Specification<").a(pojo.getSimpleName()).a("> {").a(NEWLINE);
			specClass.a(" private "+pojo.getSimpleName()+"Contract"+" criteria;").a(NEWLINE);
			specClass.a(" public "+pojo.getSimpleName()+"Specification("+pojo.getSimpleName()+"Contract"+" criteria){").a(NEWLINE);
			specClass.a(" this.criteria = criteria;}").a(NEWLINE);;
			
			specClass.a("@Override").a(NEWLINE);
			specClass.a("public Predicate toPredicate(Root<").a(pojo.getSimpleName()).a(">")
			.a(" root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {").a(NEWLINE);
			
			Field[] declaredFields = pojo.getDeclaredFields();
			SB path=new SB();
			SB crit=new SB();
			
			for(Field f:declaredFields)
			{
				
				String name = f.getName();
				if(name.equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}
				if(f.isAnnotationPresent(Ignore.class) )
				{
					continue;
				}
				
				path.a("Path<"+f.getType().getSimpleName()+"> "+f.getName()+" = root.get("+metaModel+"."+f.getName()+");").a(NEWLINE);
				
				crit.a("if (criteria.get"+Utility.toSentenceCase(f.getName())+"() != null) {").a(NEWLINE);
				crit.a(" predicates.add(criteriaBuilder.equal("+f.getName()+", criteria.get"+Utility.toSentenceCase(f.getName())+"()));")
				.a(NEWLINE).a("}").a(NEWLINE).a(NEWLINE);
				
			}
			
			
			specClass.a(path.str())
			.a(" final List<Predicate> predicates = new ArrayList<>();")
			.a(crit.str())
			.a(" return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));")
			.a("}")
			.a(NEWLINE);
			
			spec.a(specImport.str())
			.a(specClass.str())
			.a("}").a(NEWLINE);
			
			specificationWriter.write(spec.str());
			specificationWriter.flush();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
