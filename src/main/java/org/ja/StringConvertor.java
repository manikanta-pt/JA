package org.ja;

public class StringConvertor {
	
	
	public static void main(String args[])
	{
		String ef="EXECUTE FORMAT('INSERT INTO '||tableName||'.eg_role(id,name,description,createdby,createddate) values " +
"('||NEXTVAL(''||tableName||'.seq_eg_role')||','||quote_literal(NEW.name)||','||quote_literal(NEW.description)||',"+
"'||(CASE WHEN  NEW.createdby is not null THEN NEW.createdby ELSE 1 END)||',"+
"'||quote_literal(NEW.createddate)||')');";
		
		String fields="";
		String s=ef.replace("||", "");
		System.out.println(s);
		
		s=s.replaceFirst("'tableName'", "%");
		fields="tableName,";
		
		s=s.replaceFirst("'tableName'", "%");
		fields="tableName,";
		
		
		s=s.replaceAll("','", ",");
		
		s=s.replaceAll("quote_literal", "");
		
		s=s.replace("),", ",");
		s=s.replace("(,", ",");
		
	
		 
		
		System.out.println(s);
		
		
		
		
		
		
		
		
		
	}

}
