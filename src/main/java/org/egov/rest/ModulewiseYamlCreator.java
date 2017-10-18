package org.egov.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.egov.Utility;

public class ModulewiseYamlCreator {

	public void create()
	{
		try {
			String version1 = Utility.flywayVersionFormatMin.format(new Date());
			File fullfile=new File(Utility.YML_FOLDER+"/"+Utility.SUBMODULE_NAME+"_v1.0.0.yaml");
			System.out.println(fullfile.toString().length());
			if(fullfile.exists())
			{
				
			}
			else{
				
			fullfile.createNewFile();
			//System.out.println(fullfile.get());
			PrintWriter fullFileWriter=new PrintWriter(fullfile);
			 Path currentRelativePath = Paths.get("");
				String s = currentRelativePath.toAbsolutePath().toString();
				//System.out.println("Current relative path is: " + s);	
				String CurrfilePath =s+ "/src/main/resources/yamlheader.txt";
			    
			    File header=new File(CurrfilePath);
			    String headerString = Utility.readFile(header);
			    
			    headerString=      headerString.replace("Instrument",Utility.toSentenceCase(Utility.SUBMODULE_NAME));
			    headerString=      headerString.replace("instrument", Utility.SUBMODULE_NAME);
			    headerString=      headerString.replace("egf", Utility.MODULEIDENTIFIER);
			    fullFileWriter.write(headerString);
			   // fullFileWriter.write(headerString);

			    
			//fullFileWriter.write(headerString);
			fullFileWriter.flush();
			fullFileWriter.close();

}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
}
