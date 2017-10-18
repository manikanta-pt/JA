package org.egov;

import java.io.File;
import java.util.regex.Matcher;

public class CommentReader {
	
	public static void main(String[] args)
	{
		File f=new File(Utility.SRCFOLDER+"/"+"org/egov/workflow/web/contract/Task.java");
		String fileContents = Utility.readFile(f);
		
		
		 String slComment = "//[^\r\n]*";
		    String mlComment = "/\\*[\\s\\S]*?\\*/";
		    String strLit = "\"(?:\\\\.|[^\\\\\"\r\n])*\"";
		    String chLit = "'(?:\\\\.|[^\\\\'\r\n])+'";
		    String any = "[\\s\\S]";

		    java.util.regex.Pattern p = java.util.regex.Pattern.compile(String.format("(%s)",  mlComment)
		    );

		    Matcher m = p.matcher(fileContents);

		    while(m.find()) {
		      String hit = m.group();
		      if(m.group(1) != null) {
		        System.out.println("SingleLine :: " + hit.replace("*", "").replace("/", ""));
		      }
		      
		    }

	}

}
