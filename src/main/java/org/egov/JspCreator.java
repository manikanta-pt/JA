package org.egov;

import static org.egov.Utility.NEWLINE;
import static org.egov.Utility.TAB;

import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.validator.annotation.Required;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class JspCreator {
	PojoHolder pojoHolder=new PojoHolder();
	private StringBuilder tiles;
	private StringBuilder labels;
	private StringBuilder messages;
	private StringBuilder errors;
	private StringBuilder newjsp;
	private StringBuilder editjsp;
	private Class<?> type;
	public static void main(String[] args) {

		JspCreator rc=new JspCreator();
		rc.createJSP("org.egov.tl.domain.entity.FeeType");

	}

	public void	createJSP(String fullyQualifiedName)
	{	
		try {
			pojoHolder.loadPojo(fullyQualifiedName);
			tiles = new StringBuilder();
			labels = new StringBuilder();
			messages = new StringBuilder();
			errors = new StringBuilder();
			newjsp = new StringBuilder();
			editjsp = new StringBuilder();


			Class<?> pojo = pojoHolder.getPojo();
			
			addTileEntry(pojo);
			System.out.println(tiles);
			addLabelsEntry(pojo);
			System.out.println(labels);
			addMessagesEntry(pojo);
			makeJsp(pojo);
			System.out.println(newjsp);
	
			if(!Utility.WRITETOTEMPFILES)
			{
				File tilesFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/layout/tiles.xml");
				RandomAccessFile tilesFile=new RandomAccessFile(tilesFileName, "rw");
				tilesFile.seek(tilesFileName.length()-"</tiles-definitions>".getBytes().length-1);
				tilesFile.writeBytes(tiles.toString());
				tilesFile.writeBytes("\n</tiles-definitions>");
				tilesFile.close();
				
				File labelsFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/resources/messages/labels.properties");
				RandomAccessFile labelsFile=new RandomAccessFile(labelsFileName, "rw");
				labelsFile.seek(labelsFile.length());
				labelsFile.writeBytes(labels.toString());
				labelsFile.close();
				
				String simpleName = pojo.getSimpleName().toLowerCase();
				File dir=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/views/");
				if(!dir.exists())
				{
					dir.mkdirs();
					
				}
				File newJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"-new.jsp");
				PrintWriter newJspFile=new PrintWriter(newJspFileName);
				newJspFile.write(newjsp.toString());
				newJspFile.flush();
				newJspFile.close();
			}
			
		}catch (Exception e)
		{
			
		}
	}

	private void makeJsp(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		newjsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		newjsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
		String uptoForm=
			"<div class=\"main-content\">"+
			  "<div class=\"row\">"+
			    "<div class=\"col-md-12\">"+
				"<div class=\"panel panel-primary\" data-collapsed=\"0\">"+
				  "<div class=\"panel-heading\">"+
				     "<div class=\"panel-title\">"+
				     simpleName+
				     "</div>"+
				  "</div>"+
				"<div class=\"panel-body\">";
		newjsp.append(uptoForm);
		newjsp.append("<form:form role=\"form\" action=\""+simpleName.toLowerCase()+"/create\" modelAttribute=\""
		+Utility.toCamelCase(simpleName)+"\" id=\""+Utility.toCamelCase(simpleName)+"form\""+
" cssClass=\"form-horizontal form-groups-bordered\" enctype=\"multipart/form-data\">");	
		
		
		
		Field[] declaredFields = pojo.getDeclaredFields();
		int i=0;
		
		for (Field f:declaredFields)
		{
		System.out.println(f.getType().getName());
		System.out.println();
		if(f.getName().equals("serialVersionUID"))
			continue;
		 if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
			 continue;
		 if(f.getName().equals("id"))
			 continue;
			 
		Required required = f.getDeclaredAnnotation(org.egov.infra.persistence.validator.annotation.Required.class);
		NotBlank notblank = f.getDeclaredAnnotation(org.hibernate.validator.constraints.NotBlank.class);
		NotNull notnull = f.getDeclaredAnnotation(javax.validation.constraints.NotNull.class);
		Length length = f.getDeclaredAnnotation(org.hibernate.validator.constraints.Length.class);		
		boolean mandatory=false;
		String egFieldType="";
		type = f.getType();
		if(type.getName().contains("org.egov"))
			egFieldType="l";
		else if(type.getName().equals("java.lang.String"))
			egFieldType="s";
		else if(type.getName().contains("Date"))
			egFieldType="d";
		else if(f.getName().equals("id") )
			egFieldType="i";
		else
			egFieldType="n";
			
		
		if(required!=null || notblank!=null || notnull!=null)
		{
			mandatory=true;
			System.out.println(mandatory+"-----------"+f.getName());
		}
			SB s=new SB();

			
			
			if(i%2==0)	
			{
				s.a("<div class=\"form-group\">"+NEWLINE);
			}
			
			s.a("<label class=\"col-sm-3 control-label text-right\"><spring:message code=\"lbl."+f.getName().toLowerCase()+"\" />"+NEWLINE);
			if(mandatory)
			{
				s.a("<span class=\"mandatory\"></span>"+NEWLINE);
			}
			s.a("</label>");
			s.a("<div class=\"col-sm-3 add-margin\">"+NEWLINE);
			if(egFieldType.equals("l"))
			{
			 String select="<form:select path=\""+f.getName()+".id\" id=\""+f.getName()+".id\" cssClass=\"form-control\" "+
			 		 "cssErrorClass=\"form-control error\" >"+NEWLINE+
					 "<form:option value=\"\"> <spring:message code=\"lbl.select\"/> </form:option>"+NEWLINE+
					 "<form:options items=\"${"+Utility.toCamelCase(f.getType().getSimpleName())+"s}\" itemValue=\"id\" itemLabel=\"name\"/>"+NEWLINE+
					 "</form:select>"+NEWLINE;
			 s.a(select);
		
			}else if (egFieldType.equals("s"))
			{
				int max=0;
				if(length==null)
				{
					System.err.println("Length is not specified for String field "+f.getName()+NEWLINE);
				}else
				{
					max=length.max();
				}
				
				
				
				s.a("<form:input  path=\""+f.getName()+"\" class=\"form-control text-left patternvalidation\" ");
				s.a("data-pattern=\"alphanumeric\" maxlength=\""+max+"\" ");
				if(mandatory)
				{
					s.a("required=\"required\"");
				}
				s.a("/>"+NEWLINE);
			}else if(egFieldType.equals("d"))
			{
				
				s.a(" <form:input path=\""+f.getName()+"\" class=\"form-control datepicker\" data-date-end-date=\"0d\"");
				s.a("  data-inputmask=\"'mask': 'd/m/y'\"");
				if(mandatory)
				{
					s.a(" required=\"required\"");
				}
				s.a("/>"+NEWLINE);
			}else if(egFieldType.equals("i")){
				
				s.a(" <form:hidden path=\""+f.getName()+"\" />");
			}
			else	
			{
				
				s.a(" <form:input path=\""+f.getName()+"\" class=\"form-control text-right patternvalidation\"");
				s.a(" data-pattern=\"number\"  ");
				if(mandatory)
				{
					s.a(" required=\"required\"");
				}
				s.a("/>"+NEWLINE);
			}
			 s.a("</div>");
			if(i%2==0)	
			{
				s.a("</div>"+NEWLINE);
			}
			
			newjsp.append(s.str());
			i++;
		}
		
		newjsp.append("</form:form> </div></div></div></div>");
		
		
	}

	private void addMessagesEntry(Class<?> pojo) {
		messages.append("");
		
	}

	private void addLabelsEntry(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		labels.append("title."+simpleName.toLowerCase()+".new=Create New "+pojo.getSimpleName()+NEWLINE);
		labels.append("title."+simpleName.toLowerCase()+".view=View "+pojo.getSimpleName()+NEWLINE);
		labels.append("title."+simpleName.toLowerCase()+".edit=Edit "+pojo.getSimpleName()+NEWLINE);
		Field[] declaredFields = pojo.getDeclaredFields();
		for (Field f:declaredFields)
		{
			if(f.getName().equals("serialVersionUID"))
				continue;
			 if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				 continue;
			 if(f.getName().equals("id"))
				 continue;
			
			labels.append("lbl."+f.getName().toLowerCase()+"="+f.getName()+NEWLINE);
		}
		
	}

	private void addTileEntry(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		tiles.append(addSingleEntry(simpleName,"new"));
		tiles.append(addSingleEntry(simpleName,"view"));
		tiles.append(addSingleEntry(simpleName,"edit"));
		
	}

	private String addSingleEntry(String simpleName,String actionName) {
	StringBuilder entry=new StringBuilder();
	entry.append(new SB()
		.a("<definition name=\"")
		.a(simpleName.toLowerCase()+"-"+actionName)
		.a("\" extends=\"base_layout\">")
		.a(NEWLINE).str());
		
	entry.append(new SB()
		.a(TAB+"<put-attribute name=\"page-title\" value=\"title.")
		.a(simpleName.toLowerCase())
		.a("."+actionName)
		.a("\" cascade=\"true\"/>")
		.a(NEWLINE).str());
		
	entry.append(new SB()
		.a(TAB+"<put-attribute name=\"body\" value=\"")
		.a("/WEB-INF/views/"+simpleName.toLowerCase()+"/"+simpleName.toLowerCase()+"-"+actionName+".jsp\"")
		.a("/>")
		.a(NEWLINE)
		.a("</definition>")
		.a(NEWLINE)
		.a(NEWLINE).str());
	return entry.toString();
	}		
	}


