package org.egov;

import static org.egov.Utility.NEWLINE;
import static org.egov.Utility.TAB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.validator.annotation.Required;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class SearchJspCreator {
	PojoHolder pojoHolder=new PojoHolder();
	private StringBuilder tiles;
	private StringBuilder labels;
	private StringBuilder messages;
	private StringBuilder errors;
	private StringBuilder newjsp;
	private StringBuilder editjsp;
	private StringBuilder resultjsp;
	private StringBuilder viewJsp;
	private StringBuilder formJsp;
	private Class<?> type;
	public static void main(String[] args) {

		JspCreator rc=new JspCreator();
		rc.createJSP("org.egov.tl.domain.entity.LicenseDocumentType");

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
			resultjsp = new StringBuilder();
			viewJsp = new StringBuilder();
			formJsp=new StringBuilder();


			Class<?> pojo = pojoHolder.getPojo();

			addTileEntry(pojo);
			//System.out.println(tiles);
			addLabelsEntry(pojo);
			//System.out.println(labels);
			addMessagesEntry(pojo);
			makeNewJsp(pojo);
			makeResultJsp(pojo);
			makeViewJsp(pojo);
			makeEditJsp(pojo);
			
			
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
				labelsFile.writeBytes(NEWLINE.toString());
				labelsFile.writeBytes(labels.toString());
				labelsFile.close();

				String simpleName = pojo.getSimpleName().toLowerCase();
				File dir=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+simpleName);
				if(!dir.exists())
				{
					dir.mkdirs();

				}
//new jsp		
				if(newjsp.length()>0)
				{
				File newJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-new.jsp");
				PrintWriter newJspFile=new PrintWriter(newJspFileName);
				newJspFile.write(newjsp.toString());
				newJspFile.flush();
				newJspFile.close();
				}
//result jsp
				if(resultjsp.length()>0)
				{
				File resultJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-result.jsp");
				PrintWriter resultJspFile=new PrintWriter(resultJspFileName);
				resultJspFile.write(resultjsp.toString());
				resultJspFile.flush();
				resultJspFile.close();
				}
//view jsp		
				if(viewJsp.length()>0)
				{
				File viewJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-view.jsp");
				PrintWriter viewJspFile=new PrintWriter(viewJspFileName);
				viewJspFile.write(viewJsp.toString());
				viewJspFile.flush();
				viewJspFile.close();
				}
				
				if(formJsp.length()>0)
				{
				File formJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-form.jsp");
				PrintWriter formJspFile=new PrintWriter(formJspFileName);
				formJspFile.write(formJsp.toString());
				formJspFile.flush();
				formJspFile.close();
				}
				if(editjsp.length()>0)
				{
				File editJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-edit.jsp");
				PrintWriter editJspFile=new PrintWriter(editJspFileName);
				editJspFile.write(editjsp.toString());
				editJspFile.flush();
				editJspFile.close();
				}
				

				
			}

		}catch (Exception e)
		{

		}
	}
	
	private void makeViewJsp(Class<?> pojo) {
		
		String simpleName = pojo.getSimpleName();
		viewJsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		viewJsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
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
						"<div class=\"panel-body custom\">";
		viewJsp.append(uptoForm);
		
		
		
		
		Field[] declaredFields = pojo.getDeclaredFields();
		int i=0;

		for (Field f:declaredFields)
		{
			//System.out.println(f.getType().getName());
			//System.out.println();
			if(f.getName().equals("serialVersionUID"))
				continue;
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				continue;
			if(f.getName().equals("id"))
				continue;

			String egFieldType="";
			type = f.getType();
			egFieldType = Utility.findTypes(f);
			SB s=new SB();
			if(i%2==0)	
			{
				viewJsp.append("<div class=\"row add-border\">");
			}
          
			s.a("<div class=\"col-xs-3 add-margin\"><spring:message code=\"lbl."+f.getName().toLowerCase()+"\" />"+NEWLINE);
			
			s.a("</div>");
			s.a("<div class=\"col-sm-3 add-margin view-content\">"+NEWLINE);
			if(egFieldType.equals("l"))
			{
				s.a("${"+Utility.toCamelCase(simpleName)+"."+f.getName()+".name}"); // may thing place you have to change to field /getName,getCode etc

			}else if (egFieldType.equals("s"))
			{

				s.a("${"+Utility.toCamelCase(simpleName)+"."+f.getName()+"}");
				
			}else if(egFieldType.equals("d"))
			{

				s.a("<fmt:formatDate pattern=\"MM/dd/yyyyy\" value=\"${"+Utility.toCamelCase(simpleName)+"."+f.getName()+"} />");
				
			}else if(egFieldType.equals("i")){

				//s.a(" <form:hidden path=\""+f.getName()+"\" />");
			}
			else	
			{

				s.a("${"+Utility.toCamelCase(simpleName)+"."+f.getName()+"}");
				
			}
			
			
			
			
			s.a(""+NEWLINE);
			s.a("</div>");
			if(i%2==1)	
			{
				s.a("</div>"+NEWLINE);
			}

			viewJsp.append(s.str());
			i++;
		}
		
		viewJsp.append("</div></div></div></div>");
		//create buttons
		SB buttons=new SB();
		buttons.a("<div class=\"row text-center\"><div class=\"add-margin\">");
		buttons.a("<a href=\"javascript:void(0)\" class=\"btn btn-default\" onclick=\"self.close()\">Close</a>");
		buttons.a("</div></div>");
		viewJsp.append(buttons.str());
		
		
	}

	private void makeResultJsp(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		resultjsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		resultjsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
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
		resultjsp.append(uptoForm);
		resultjsp.append("<div class=\"panel-title text-center no-float\">");
		resultjsp.append("<strong>${message}</strong>");
		resultjsp.append("</div>");
		resultjsp.append(" </div></div></div></div>");
		SB buttons=new SB();

		buttons.a("<div class=\"form-group\">")
		.a("<div class=\"text-center\">")
		.a("<a href='javascript:void(0)' class='btn btn-default' onclick='self.close()'><spring:message code='lbl.close' /></a>")
		.a("</div></div>");
		resultjsp.append(buttons.str());	

		
	}

	private void makeNewJsp(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		newjsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		newjsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
	
		newjsp.append("<form:form role=\"form\" action=\"create\" modelAttribute=\""
				+Utility.toCamelCase(simpleName)+"\" id=\""+Utility.toCamelCase(simpleName)+"form\""+
				" cssClass=\"form-horizontal form-groups-bordered\" enctype=\"multipart/form-data\">"+NEWLINE);	


		//the below content will go to form
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
		formJsp.append(uptoForm);
		
		


		Field[] declaredFields = pojo.getDeclaredFields();
		int i=0;
		

		for (Field f:declaredFields)
		{
			String requiredMarker="";
			//System.out.println(f.getType().getName());
			//System.out.println();
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
			egFieldType = Utility.findTypes(f);


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
				requiredMarker="required=\"required\"";
			}
			s.a("</label>");
			s.a("<div class=\"col-sm-3 add-margin\">"+NEWLINE);
			if(egFieldType.equals("l"))
			{
				String select="<form:select path=\""+f.getName()+".id\" id=\""+f.getName()+".id\" cssClass=\"form-control\" "+
						"cssErrorClass=\"form-control error\" >"+NEWLINE+
						"<form:option value=\"\"> <spring:message code=\"lbl.select\"/> </form:option>"+NEWLINE+
						"<form:options items=\"${"+Utility.toCamelCase(f.getType().getSimpleName())+"s}\" itemValue=\"id\" itemLabel=\"name\" "+requiredMarker+" />"+NEWLINE+
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
				s.a("data-pattern=\"alphanumeric\" maxlength=\""+max+"\" "+requiredMarker+" ");
				s.a("/>"+NEWLINE);
			}else if(egFieldType.equals("d"))
			{

				s.a(" <form:input path=\""+f.getName()+"\" class=\"form-control datepicker\" data-date-end-date=\"0d\"");
				s.a("  data-inputmask=\"'mask': 'd/m/y'\" "+requiredMarker+"");
				s.a("/>"+NEWLINE);
				
			}else if(egFieldType.equals("i")){

				s.a(" <form:hidden path=\""+f.getName()+"\" />");
				
			}
			else	
			{

				s.a(" <form:input path=\""+f.getName()+"\" class=\"form-control text-right patternvalidation\"");
				s.a(" data-pattern=\"number\" "+requiredMarker+" ");
				s.a("/>"+NEWLINE);
				
			}
			 
			s.a("<form:errors path=\""+f.getName()+"\" cssClass=\"error-msg\" />");
			s.a("</div>");
			if(i%2==1)	
			{
				s.a("</div>"+NEWLINE);
			}

			formJsp.append(s.str());
			i++;
		}
		
		formJsp.append(" <input type=\"hidden\" name=\""+Utility.toCamelCase(simpleName)+"\" value=\"${"+Utility.toCamelCase(simpleName)+".id}\" />");
		//create buttons
		SB buttons=new SB();
		newjsp.append("<%@ include file=\""+simpleName.toLowerCase()+"-form.jsp\"  %>");
		
		buttons.a("<div class=\"form-group\">")
		.a("<div class=\"text-center\">")
		.a("<button type='submit' class='btn btn-primary' id=\"buttonSubmit\"><spring:message code='lbl.create'/></button>")
		.a("<a href='javascript:void(0)' class='btn btn-default' onclick='self.close()'><spring:message code='lbl.close' /></a>")
		.a("</div></div>");
	

		newjsp.append(" </div></div></div></div> "+NEWLINE);
		newjsp.append(buttons.str());	
		newjsp.append("</form:form>");
		String script=" <script> "+
		"$('#buttonSubmit').click(function(e){"+
        " if($('form').valid()){"+          
        " }else{"+
         " e.preventDefault();"+
         " }  });"+
          "</script>";
		newjsp.append(script);

	}

	
	private void makeEditJsp(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		editjsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		editjsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
	
		editjsp.append("<form:form role=\"form\" action=\"../update\" modelAttribute=\""
				+Utility.toCamelCase(simpleName)+"\" id=\""+Utility.toCamelCase(simpleName)+"form\""+
				" cssClass=\"form-horizontal form-groups-bordered\" enctype=\"multipart/form-data\">"+NEWLINE);	


		//the below content will go to form
				//create buttons
		SB buttons=new SB();
		editjsp.append("<%@ include file=\""+simpleName.toLowerCase()+"-form.jsp\" %>"+NEWLINE);
		
		editjsp.append("<input type=\"hidden\" name=\""+Utility.toCamelCase(simpleName)+"\" value=\"${"+Utility.toCamelCase(simpleName)+".id}\" />");
		
		buttons.a("<div class=\"form-group\">")
		.a("<div class=\"text-center\">")
		.a("<button type='submit' class='btn btn-primary' id=\"buttonSubmit\"><spring:message code='lbl.update'/></button>")
		.a("<a href='javascript:void(0)' class='btn btn-default' onclick='self.close()'><spring:message code='lbl.close' /></a>")
		.a("</div></div>");
		

		editjsp.append(" </div></div></div></div>");
		editjsp.append(buttons.str()+"</form:form>");	
		String script=" <script> "+
		"$('#buttonSubmit').click(function(e){"+
        " if($('form').valid()){"+          
        " }else{"+
         " e.preventDefault();"+
         " }  });"+
          "</script>";
		editjsp.append(script);

  
		
	


	}


	

	private void addMessagesEntry(Class<?> pojo) {
		messages.append("");

	}

	private void addLabelsEntry(Class<?> pojo) {

		String simpleName = pojo.getSimpleName();
		File labelsFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/resources/messages/labels.properties");
		Properties labelProp=new Properties();
		try {
			labelProp.load(new FileInputStream(labelsFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		labels.append(NEWLINE);
		String labelHeader="title."+simpleName.toLowerCase()+".new";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=Create New "+pojo.getSimpleName()+NEWLINE);
		labelHeader="title."+simpleName.toLowerCase()+".view";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=View "+pojo.getSimpleName()+NEWLINE);
		labelHeader="title."+simpleName.toLowerCase()+".edit";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=Edit "+pojo.getSimpleName()+NEWLINE);
		labelHeader="title."+simpleName.toLowerCase()+".result";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=Result "+pojo.getSimpleName()+NEWLINE);
		Field[] declaredFields = pojo.getDeclaredFields();
		for (Field f:declaredFields)
		{
			if(f.getName().equals("serialVersionUID"))
				continue;
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				continue;
			if(f.getName().equals("id"))
				continue;

			String key = "lbl."+f.getName().toLowerCase();
			if(!labelProp.containsKey(key))
			{
				type = f.getType();
				if(type.getName().contains("org.egov"))
				{
				labels.append(key+"="+f.getClass().getSimpleName()+NEWLINE);
				}else
				{
					labels.append(key+"="+f.getName()+NEWLINE);
				}
			}
		}

	}

	private void addTileEntry(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		tiles.append(addSingleEntry(simpleName,"new"));
		tiles.append(addSingleEntry(simpleName,"view"));
		tiles.append(addSingleEntry(simpleName,"edit"));
		tiles.append(addSingleEntry(simpleName,"result"));

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


