package org.egov;

import java.io.File;

public class StructureCreator {
	
	

	public static void main(String[] args) {
		
		StructureCreator rc=new StructureCreator();
		rc.create(Utility.SRCFOLDER);

	}
	
	public void create(String s)
	{
	
		add(s+"/"+"org.egov." + Utility.MODULEIDENTIFIER.toLowerCase() + ".persistence.entity");
		
		
		System.out.println("done");
		
	}

	private void add(String dotedName) {
	
		String name = dotedName.replace(".", "/");
		File f=new File(name);
		if(!f.exists())
		{
			f.mkdirs();
		}
		
	
		
	}
	

}
