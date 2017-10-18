package org.ja;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class AddingActionAnnotation {

    public static void main(final String[] args) {
        final List<String> urls = new ArrayList<String>();
        final File backUpDirectory = new File("/home/" + System.getProperty("user.name") + "/backup/");
        final String createApi = "create()";
        final String saveApi = "save()";
        final String editApi = "edit()";
        final String modifyApi = "modify()";
        final String updateApi = "update()";
        final String viewApi = "view()";
        final String searchApi = "search()";
        final String actionRoleActionFileName = "actionRoleActions.sql";
        String fileName = "";
        String backUpfileName = "";
        Boolean importStatementAdded = false;
        final String importStatement = "import org.apache.struts2.convention.annotation.Action;";
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
            final RandomAccessFile actionRoleActionFile = new RandomAccessFile("/home/" + System.getProperty("user.name") + "/"
                    + actionRoleActionFileName, "rw");

            StringBuffer actionInsertScript;
            StringBuffer roleActionInsertScript;
            for (final String url : urls)
                if (!url.equalsIgnoreCase(""))
                {
                    backUpfileName = backUpDirectory.getPath() + "/";
                    importStatementAdded = false;
                    fileName = fileNameFolderPath;
                    final String fileFolderName = url.substring(url.indexOf('/'), url.indexOf('/', url.indexOf('/') + 1) + 1);
                    fileName = fileName + fileFolderName;
                    // backUpfileName = backUpfileName +( fileFolderName.replace("/", ""));
                    String className = url.substring(url.indexOf('/', url.indexOf('/') + 1) + 1, url.indexOf('!'));
                    String subFolderName = "";
                    while (className.contains("/"))
                    {
                        subFolderName = "";
                        subFolderName = className.substring(0, className.indexOf('/') + 1);
                        fileName = fileName + subFolderName;
                        // backUpfileName = backUpfileName + (subFolderName.replace("/", ""));
                        className = className.split("/")[1];
                    }
                    final String actionName = className;
                    className = className.substring(0, 1).toUpperCase() + className.substring(1);
                    className = className + "Action.java";
                    fileName = fileName + className;
                    // backUpfileName = backUpfileName + className;

                    String apiName = url.split("!")[1];
                    apiName = apiName.split("!")[0];
                    apiName = apiName.substring(0, apiName.indexOf('.'));
                    apiName = apiName + "()";// this is to resolve adding same @Action string for more then one api
                    // backUpfileName = backUpfileName.replace("Action.java", "");
                    backUpfileName = backUpfileName + backUp + ".java";
                    String annotation = "@Action(value=\"";
                    annotation = annotation + url.split(".action")[0];
                    annotation = annotation.replace('!', '-') + "\")";
                    final RandomAccessFile file1 = new RandomAccessFile(fileName, "r");
                    final RandomAccessFile file2 = new RandomAccessFile(backUpfileName, "rw");

                    String line = null;
                    String prevLine = "";
                    System.out.println("fileName:" + fileName + "--->" + backUpfileName);
                    // System.out.println("backUpfileName:"+backUpfileName);
                    while ((line = file1.readLine()) != null) {
                        actionInsertScript = new StringBuffer(
                                "Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) values(nextval('SEQ_EG_ACTION'),'");
                        roleActionInsertScript = new StringBuffer(
                                "Insert into eg_roleaction values((select id from eg_role where name='Super User'),(select id from eg_action where name='");
                        if (line.equalsIgnoreCase(importStatement))
                            importStatementAdded = true;
                        if (line.contains("import") && !importStatementAdded) {
                            file2.write(importStatement.getBytes());
                            file2.write("\n".getBytes());
                            importStatementAdded = true;
                        }
                        if (line.contains("public"))
                        {
                            if (line.contains("String"))
                            {
                                if (line.contains(apiName))
                                {
                                    if (!prevLine.contains("@Action(value"))
                                    {
                                        file2.write(annotation.getBytes());
                                        file2.write("\n".getBytes());
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    } else {
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    }

                                } else if (line.contains(saveApi)) {
                                    actionInsertScript
                                            .append(actionName
                                                    + "-"
                                                    + saveApi.replace("(", "").replace(")", "")
                                                    + "','"
                                                    + url.replace('!', '-').split("-")[0]
                                                    + "-"
                                                    + saveApi.replace("(", "").replace(")", "")
                                                    + ".action"
                                                    + "',(select id from eg_module where name='ReplaceModuleName'),1,'"
                                                    + actionName
                                                    + "-"
                                                    + saveApi.replace("(", "").replace(")", "")
                                                    + "',false,'EGF',(select id from eg_module where name='EGF' and parentmodule is null));");
                                    roleActionInsertScript.append(actionName + "-" + saveApi.replace("(", "").replace(")", "")
                                            + "'));");
                                    actionRoleActionFile.write(("--Action InsertScript for " + actionName + "-" + saveApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(actionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(("--RoleAction InsertScript for " + actionName + "-" + saveApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(roleActionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    final String saveAnnotation = annotation.split("-")[0] + "-save" + "\")";
                                    if (!prevLine.contains("@Action(value"))
                                    {
                                        file2.write(saveAnnotation.getBytes());
                                        file2.write("\n".getBytes());
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    } else {
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    }

                                } else if (line.contains(createApi)) {
                                    actionInsertScript
                                            .append(actionName
                                                    + "-"
                                                    + createApi.replace("(", "").replace(")", "")
                                                    + "','"
                                                    + url.replace('!', '-').split("-")[0]
                                                    + "-"
                                                    + createApi.replace("(", "").replace(")", "")
                                                    + ".action"
                                                    + "',(select id from eg_module where name='ReplaceModuleName'),1,'"
                                                    + actionName
                                                    + "-"
                                                    + createApi.replace("(", "").replace(")", "")
                                                    + "',false,'EGF',(select id from eg_module where name='EGF' and parentmodule is null));");
                                    roleActionInsertScript.append(actionName + "-" + createApi.replace("(", "").replace(")", "")
                                            + "'));");
                                    actionRoleActionFile.write(("--Action InsertScript for " + actionName + "-" + createApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(actionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(("--RoleAction InsertScript for " + actionName + "-" + createApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(roleActionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());

                                    final String createAnnotation = annotation.split("-")[0] + "-create" + "\")";
                                    if (!prevLine.contains("@Action(value"))
                                    {
                                        file2.write(createAnnotation.getBytes());
                                        file2.write("\n".getBytes());
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    } else {
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    }
                                } else if (line.contains(editApi)) {
                                    actionInsertScript
                                            .append(actionName
                                                    + "-"
                                                    + editApi.replace("(", "").replace(")", "")
                                                    + "','"
                                                    + url.replace('!', '-').split("-")[0]
                                                    + "-"
                                                    + editApi.replace("(", "").replace(")", "")
                                                    + ".action"
                                                    + "',(select id from eg_module where name='ReplaceModuleName'),1,'"
                                                    + actionName
                                                    + "-"
                                                    + editApi.replace("(", "").replace(")", "")
                                                    + "',false,'EGF',(select id from eg_module where name='EGF' and parentmodule is null));");
                                    roleActionInsertScript.append(actionName + "-" + editApi.replace("(", "").replace(")", "")
                                            + "'));");
                                    actionRoleActionFile.write(("--Action InsertScript for " + actionName + "-" + editApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(actionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(("--RoleAction InsertScript for " + actionName + "-" + editApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(roleActionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());

                                    final String editAnnotation = annotation.split("-")[0] + "-edit" + "\")";
                                    if (!prevLine.contains("@Action(value"))
                                    {
                                        file2.write(editAnnotation.getBytes());
                                        file2.write("\n".getBytes());
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    } else {
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    }
                                } else if (line.contains(updateApi)) {
                                    actionInsertScript
                                            .append(actionName
                                                    + "-"
                                                    + updateApi.replace("(", "").replace(")", "")
                                                    + "','"
                                                    + url.replace('!', '-').split("-")[0]
                                                    + "-"
                                                    + updateApi.replace("(", "").replace(")", "")
                                                    + ".action"
                                                    + "',(select id from eg_module where name='ReplaceModuleName'),1,'"
                                                    + actionName
                                                    + "-"
                                                    + updateApi.replace("(", "").replace(")", "")
                                                    + "',false,'EGF',(select id from eg_module where name='EGF' and parentmodule is null));");
                                    roleActionInsertScript.append(actionName + "-" + updateApi.replace("(", "").replace(")", "")
                                            + "'));");
                                    actionRoleActionFile.write(("--Action InsertScript for " + actionName + "-" + updateApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(actionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(("--RoleAction InsertScript for " + actionName + "-" + updateApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(roleActionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());

                                    final String updateAnnotation = annotation.split("-")[0] + "-update" + "\")";
                                    if (!prevLine.contains("@Action(value"))
                                    {
                                        file2.write(updateAnnotation.getBytes());
                                        file2.write("\n".getBytes());
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    } else {
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    }
                                } else if (line.contains(modifyApi)) {
                                    actionInsertScript
                                            .append(actionName
                                                    + "-"
                                                    + modifyApi.replace("(", "").replace(")", "")
                                                    + "','"
                                                    + url.replace('!', '-').split("-")[0]
                                                    + "-"
                                                    + modifyApi.replace("(", "").replace(")", "")
                                                    + ".action"
                                                    + "',(select id from eg_module where name='ReplaceModuleName'),1,'"
                                                    + actionName
                                                    + "-"
                                                    + modifyApi.replace("(", "").replace(")", "")
                                                    + "',false,'EGF',(select id from eg_module where name='EGF' and parentmodule is null));");
                                    roleActionInsertScript.append(actionName + "-" + modifyApi.replace("(", "").replace(")", "")
                                            + "'));");
                                    actionRoleActionFile.write(("--Action InsertScript for " + actionName + "-" + modifyApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(actionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(("--RoleAction InsertScript for " + actionName + "-" + modifyApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(roleActionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());

                                    final String modifyAnnotation = annotation.split("-")[0] + "-modify" + "\")";
                                    if (!prevLine.contains("@Action(value"))
                                    {
                                        file2.write(modifyAnnotation.getBytes());
                                        file2.write("\n".getBytes());
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    } else {
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    }
                                } else if (line.contains(viewApi)) {
                                    actionInsertScript
                                            .append(actionName
                                                    + "-"
                                                    + viewApi.replace("(", "").replace(")", "")
                                                    + "','"
                                                    + url.replace('!', '-').split("-")[0]
                                                    + "-"
                                                    + viewApi.replace("(", "").replace(")", "")
                                                    + ".action"
                                                    + "',(select id from eg_module where name='ReplaceModuleName'),1,'"
                                                    + actionName
                                                    + "-"
                                                    + viewApi.replace("(", "").replace(")", "")
                                                    + "',false,'EGF',(select id from eg_module where name='EGF' and parentmodule is null));");
                                    roleActionInsertScript.append(actionName + "-" + viewApi.replace("(", "").replace(")", "")
                                            + "'));");
                                    actionRoleActionFile.write(("--Action InsertScript for " + actionName + "-" + viewApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(actionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(("--RoleAction InsertScript for " + actionName + "-" + viewApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(roleActionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());

                                    final String viewAnnotation = annotation.split("-")[0] + "-view" + "\")";
                                    if (!prevLine.contains("@Action(value"))
                                    {
                                        file2.write(viewAnnotation.getBytes());
                                        file2.write("\n".getBytes());
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    } else {
                                        file2.write(line.getBytes());
                                        file2.write("\n".getBytes());
                                    }
                                } else if (line.contains(searchApi)) {
                                    actionInsertScript
                                            .append(actionName
                                                    + "-"
                                                    + searchApi.replace("(", "").replace(")", "")
                                                    + "','"
                                                    + url.replace('!', '-').split("-")[0]
                                                    + "-"
                                                    + searchApi.replace("(", "").replace(")", "")
                                                    + ".action"
                                                    + "',(select id from eg_module where name='ReplaceModuleName'),1,'"
                                                    + actionName
                                                    + "-"
                                                    + searchApi.replace("(", "").replace(")", "")
                                                    + "',false,'EGF',(select id from eg_module where name='EGF' and parentmodule is null));");
                                    roleActionInsertScript.append(actionName + "-" + searchApi.replace("(", "").replace(")", "")
                                            + "'));");
                                    actionRoleActionFile.write(("--Action InsertScript for " + actionName + "-" + searchApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(actionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(("--RoleAction InsertScript for " + actionName + "-" + searchApi)
                                            .getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write(roleActionInsertScript.toString().getBytes());
                                    actionRoleActionFile.write("\n".getBytes());
                                    actionRoleActionFile.write("\n".getBytes());

                                    final String searchAnnotation = annotation.split("-")[0] + "-search" + "\")";
                                    if (!prevLine.contains("@Action(value"))
                                    {
                                        file2.write(searchAnnotation.getBytes());
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
            actionRoleActionFile.close();
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