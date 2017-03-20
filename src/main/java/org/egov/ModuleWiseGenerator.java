package org.egov;

public class ModuleWiseGenerator {

	public static void main(String[] args) {
   MVCCreator mv=new MVCCreator();
   mv.main(new String[]{"org.egov.workflow.persistence.entity.State"});
   mv.main(new String[]{"org.egov.workflow.persistence.entity.StateHistory"});
   mv.main(new String[]{"org.egov.workflow.persistence.entity.WorkflowAction"});
   mv.main(new String[]{"org.egov.workflow.persistence.entity.WorkFlowMatrix"});
   mv.main(new String[]{"org.egov.workflow.persistence.entity.WorkflowTypes"});
   
    
  
  
	}

}
