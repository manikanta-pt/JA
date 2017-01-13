package org.egov;

public class ModuleWiseGenerator {

	public static void main(String[] args) {
   MVCCreator mv=new MVCCreator();
   mv.main(new String[]{"org.egov.process.entity.Inbox"});
  /* mv.main(new String[]{"org.egov.process.entity.Department"});
   mv.main(new String[]{"org.egov.process.entity.User"});
   mv.main(new String[]{"org.egov.process.entity.Group"});
   mv.main(new String[]{"org.egov.process.entity.WorkflowTypes"});
   mv.main(new String[]{"org.egov.process.entity.User"});
   mv.main(new String[]{"org.egov.process.entity.Bill"});
  */
  
	}

}
