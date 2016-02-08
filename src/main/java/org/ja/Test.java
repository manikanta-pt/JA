package org.ja;
import java.io.*;                                                            

public class Test {                                                          
  public static void main(String[] args) throws Exception {  
	  File workingDir=new File("/home/mani/Workspaces/personal/JA/src/main/java/org/ja/");
    String[] command = { "./myScript.sh", "key", "ls -t | tail -n 1" };         
    Process process = Runtime.getRuntime().exec(command,args,workingDir);                    
    BufferedReader reader = new BufferedReader(new InputStreamReader(        
        process.getInputStream()));                                          
    String s;                                                                
    while ((s = reader.readLine()) != null) {                                
      System.out.println("Script output: " + s);                             
    }                                                                        
  }                                                                          
}      