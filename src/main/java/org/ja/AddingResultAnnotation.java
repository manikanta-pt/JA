package org.ja;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class AddingResultAnnotation {

    public static void main(final String[] args) {
        final List<String> urls = new ArrayList<String>();
        List<String> returnStrings = new ArrayList<String>();
        Boolean check = false;
        final File backUpDirectory = new File("/home/" + System.getProperty("user.name") + "/backup/");
        String fileName = "";
        String backUpfileName = "";
        Boolean importStatementAdded = false;
        Boolean resultAnnotationAdded = false;
        Boolean resultExist = false;
        final String resultsImportStatement = "import org.apache.struts2.convention.annotation.Results;";
        final String resultImportStatement = "import org.apache.struts2.convention.annotation.Result;";
        String fileNameFolderPath = "";
        // First line in the soure file should be resource folder path
        try {
            System.out.println("System Username--->" + System.getProperty("user.name"));
            final RandomAccessFile sourceFile = new RandomAccessFile("/home/" + System.getProperty("user.name") + "/source.txt",
                    "r");
            if (!backUpDirectory.exists())
                if (backUpDirectory.mkdir())
                    System.out.println("BackUp Directory is created!");
                else
                    System.out.println("Failed to create BackUp directory!");
            String sourceUrl = "";
            while ((sourceUrl = sourceFile.readLine()) != null)
                if (fileNameFolderPath.equalsIgnoreCase(""))
                    fileNameFolderPath = sourceUrl;
                else
                    urls.add(sourceUrl);

            // urls.add("/report/loangrant/loanGrantReport!newFormGC.action");
            // urls.add("/masters/scheme!beforeSearch.action");
            Integer backUp = 1;
            for (final String url : urls){
                if (!url.equalsIgnoreCase(""))
                {
                	returnStrings=new ArrayList<String>();
                	resultExist = false;
                    backUpfileName = backUpDirectory.getPath() + "/";
                    importStatementAdded = false;
                    fileName = fileNameFolderPath;
                    final String fileFolderName = url.substring(url.indexOf('/'), url.indexOf('/', url.indexOf('/') + 1) + 1);
                    fileName = fileName + fileFolderName;
                    // backUpfileName = backUpfileName +( fileFolderName.replace("/", ""));
                    String className = url.substring(url.indexOf('/', url.indexOf('/') + 1) + 1, url.indexOf('!'));
                    String tempClassName = "";
                    String subFolderName = "";
                    while (className.contains("/"))
                    {
                        subFolderName = "";
                        subFolderName = className.substring(0, className.indexOf('/') + 1);
                        fileName = fileName + subFolderName;
                        // backUpfileName = backUpfileName + (subFolderName.replace("/", ""));
                        className = className.split("/")[1];
                    }
                    tempClassName = className;
                    className = className.substring(0, 1).toUpperCase() + className.substring(1);
                   // tempClassName = className;
                    className = className + "Action.java";
                    fileName = fileName + className;
                    // backUpfileName = backUpfileName + className;

                    // backUpfileName = backUpfileName.replace("Action.java", "");
                    backUpfileName = backUpfileName + backUp + ".java";
                    final RandomAccessFile file0 = new RandomAccessFile(fileName, "r");
                    final RandomAccessFile file1 = new RandomAccessFile(fileName, "r");
                    final RandomAccessFile file2 = new RandomAccessFile(backUpfileName, "rw");

                    String line = null;
                    String prevLine = "";
                    System.out.println("fileName:" + fileName + "--->" + backUpfileName);
                    while ((line = file0.readLine()) != null) {
                    System.out.println("Starting inner loop");                   	
                   if (line.contains("@Action(value")){
                            check = true;
                        }
                        if (line.contains("return") && !line.contains("()") && check){
                            check = false;
                            String returnString = line.split("return")[1].replace('"',' ').replace(';',' ').trim();
                            if(returnStrings.indexOf(returnString)==-1){
                                returnStrings.add(returnString);
                                System.out.println("returnString:"+returnString);
                            }
                            
                        }
                        
                    }
                    file0.close();
                    final RandomAccessFile checkResultExist = new RandomAccessFile(fileName, "r");
                    while ((line = checkResultExist.readLine()) != null) {
                        if (line.contains("@Result")){
                            resultExist = true;
                        }
                        if (line.contains("public")){
                            if (line.contains("class")){
                           break;
                            }
                            
                        }
                    }
                    checkResultExist.close();
                    // System.out.println("backUpfileName:"+backUpfileName);
                    if(!resultExist){
                    while ((line = file1.readLine()) != null) {
                        if (line.equalsIgnoreCase(resultsImportStatement))
                            importStatementAdded = true;
                        if (line.contains("import") && !importStatementAdded) {
                            file2.write(resultsImportStatement.getBytes());
                            file2.write("\n".getBytes());
                            file2.write(resultImportStatement.getBytes());
                            file2.write("\n".getBytes());
                            importStatementAdded = true;
                        }
                        if (line.contains("@Results")){
                            resultAnnotationAdded = true;
                        }
                            
                        if (line.contains("public"))
                        {
                            if (line.contains("class") && !resultAnnotationAdded )
                            {
                                    file2.write("@Results({".getBytes());
                                    file2.write("\n".getBytes());
                                    Integer size = returnStrings.size();
                                    String coma = ",";
                                    for (final String returnString : returnStrings) {
                                        size--;
                                        if(size==0 ){
                                            coma = "";
                                        }
                                        if(returnString.contains("."))
                                        {
                                        	file2.write(("@Result(name = "+returnString+", location = \""+tempClassName+"-\"+"+returnString+"+\".jsp\")"+coma).getBytes());
                                        }else
                                        {
                                        file2.write(("@Result(name = \""+returnString+"\", location = \""+tempClassName+"-"+returnString+".jsp\")"+coma).getBytes());
                                        }
                                        file2.write("\n".getBytes());
                                    }
                                    file2.write("})".getBytes());
                                    file2.write("\n".getBytes());
                                    file2.write(line.getBytes());
                                    file2.write("\n".getBytes());
                                
                              } else {
                                file2.write(line.getBytes());
                                file2.write("\n".getBytes());
                            }
                        } else {
                            file2.write(line.getBytes());
                            file2.write("\n".getBytes());
                        }
                        prevLine = line;

                    }
                    file2.close();
                    file1.close();
                    final RandomAccessFile file3 = new RandomAccessFile(fileName, "rw");
                    final RandomAccessFile file4 = new RandomAccessFile(backUpfileName, "r");
                    String line1 = null;
                    while ((line1 = file4.readLine()) != null) {
                        file3.write(line1.getBytes());
                        file3.write("\n".getBytes());
                    }
                    file3.close();
                    file4.close();
                    backUp++;
                    }
                }
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (backUpDirectory.isDirectory())
                // directory is empty, then delete it
                if (backUpDirectory.list().length == 0) {

                    backUpDirectory.delete();
                    System.out.println("Directory is deleted : " + backUpDirectory.getAbsolutePath());

                } else {

                    // list all the directory contents
                    final String files[] = backUpDirectory.list();

                    for (final String temp : files) {
                        // construct the file structure
                        final File fileDelete = new File(backUpDirectory, temp);

                        // recursive delete
                        fileDelete.delete();
                    }

                    // check the directory again, if empty then delete it
                    if (backUpDirectory.list().length == 0) {
                        backUpDirectory.delete();
                        System.out.println("Directory is deleted : " + backUpDirectory.getAbsolutePath());
                    }
                }
        }
    }

}
