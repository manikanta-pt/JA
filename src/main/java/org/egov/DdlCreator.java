package org.egov;

import static org.egov.Utility.NEWLINE;
import static org.egov.Utility.TAB;

import java.io.PrintWriter;
import java.lang.reflect.Field;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class DdlCreator {

	PojoHolder pojoHolder=new PojoHolder();
	public static void main(String[] args) {
		
		DdlCreator rc=new DdlCreator();
		rc.createDdl("org.egov.commons.CFinancialYear");

	}
	
	public void createDdl(String fullyQualifiedName)
	{
		
		StringBuilder main=new StringBuilder();
		StringBuilder seq=new StringBuilder();
		StringBuilder ref=new StringBuilder();
		
		try {
			pojoHolder.loadPojo(fullyQualifiedName);
			Class<?> pojo = pojoHolder.getPojo();
			Field[] declaredFields = pojo.getDeclaredFields();
			PrintWriter sqlWriter = new PrintWriter(Utility.SQL_FOLDER+"/" +Utility.CONTEXT+"_"+pojo.getSimpleName()+"_ddl.sql", "UTF-8");
			
			System.out.println("   sdfsd"+pojo.getClass().getAnnotation(Table.class));
			
			//org.egov.tl.domain.entity.Validity.class;
			
			
			String tableName = org.egov.tl.entity.Validity.class.getAnnotation(Table.class).name();//this is another place of dependency
			
			
			String sequenceName = "seq_"+tableName;
			seq.append("create sequence "+sequenceName +";");
			
			main.append("Create table "+tableName +"( "+NEWLINE );
			for(Field f:declaredFields)
			{
				if(f.getName().equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
					continue;
				String egType = Utility.findTypes(f);
				if(f.getAnnotation(ManyToOne.class)!=null)
				{
					main.append(f.getName()+" bigint ");
					if(f.getAnnotation(JoinColumn.class)!=null)
					{
						if(!f.getAnnotation(JoinColumn.class).nullable())
						{
							main.append("NOT NULL");	
						}
						
					}
				}
				else if(egType.equals("s"))
				{
					main.append(f.getName()+" varchar(");
					if(f.getAnnotation(Length.class)!=null)
					{
						main.append(f.getAnnotation(Length.class).max()+")");
					}
				
				}
				else if(egType.equals("d"))
				{
					main.append(f.getName()+" date");
				}
				else if(egType.equals("b"))
				{
					main.append(f.getName()+" boolean");
				}
				else if(egType.equals("i"))
				{
					main.append(f.getName()+" bigint");
				}
				
				else if(egType.equals("n"))
				{
					main.append(f.getName()+" ");
					if(f.getType().getName().contains("Integer"))
					{
						main.append("smallint");
					}else if(f.getType().getName().contains("Long"))
					{
						main.append("bigint");
					}else if(f.getType().getName().contains("BigDecimal"))
					{
						main.append("numeric (13,2)");
					}else if(f.getType().getName().contains("Double"))
					{
						main.append("numeric (13,2)");
					}
					else if(f.getType().getName().contains("Double"))
					{
						main.append("numeric (13,2)");
					}
					
						
				}
			    if(f.getAnnotation(NotNull.class)!=null)
				{
					main.append("NOT NULL");	
				}
				main.append(","+NEWLINE+TAB);
				
			}
			//main.append(" createddate timestamp without time zone,\n\t createdby bigint,\n\t lastmodifieddate timestamp without time zone,\n\t"+
				//	   " lastmodifiedby bigint,\n\t version bigint"+NEWLINE+");");
			sqlWriter.write(seq.toString());
			sqlWriter.write(NEWLINE);
			sqlWriter.write(main.toString());
			
			
			sqlWriter.flush();
			
			
		System.out.println(main.toString());	
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
