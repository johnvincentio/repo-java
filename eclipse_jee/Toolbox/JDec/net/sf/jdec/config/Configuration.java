/*
 * Configuration.java Copyright (c) 2006,07 Swaroop Belur
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */


package net.sf.jdec.config;

import net.sf.jdec.main.ConsoleLauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class Configuration {

    private static String OutputFilePath=System.getProperty("user.dir");
    private static String mode="file";
    private static String LogFilePath=null;
    private static String LogMode="file";
    private static String decompileroption="dc";
    private static String LogLevel="1";
    private static String javaClassFile="***Not-Initialized***";
    private static String showImport="true";
    private static String tempDir="***Not-Initialized";
    private static String jarPath="";
    private static String innerdepth="0";
    private static int innerdepthLevel=0;
    private static String inlineInnerClassMethodContent="false";
    private static String detailedExceptionTableInfo="false";
    private static String skipClassVersionCheck="true";
    
    private static String intPrefix="anInt";
    private static String longPrefix="aLong";
    private static String floatPrefix="aFloat";
    private static String doublePrefix="aDouble";
    private static String stringPrefix="aString";
    private static String bytePrefix="aByte";
    private static String shortPrefix="aShort";
    private static String charPrefix="aChar";
    private static String booleanPrefix="aBoolean";
    private static String otherPrefix="Var";

    public static String getBooleanPrefix() {
        return booleanPrefix;
    }

    public static void setBooleanPrefix(String prefix) {
        if(prefix!=null)
            booleanPrefix = prefix.trim();
    }
    
    
    


    public static String getFileExtension() {
        return fileExtension;
    }

    public static void setFileExtension(String fileExtension) {
        Configuration.fileExtension = fileExtension.trim();
    }

    private static String fileExtension="jdec";



    public static String getInlineInnerClassMethodContent() {
          return inlineInnerClassMethodContent;
      }

    public static int getInnerdepth() {
        return innerdepthLevel;
    }



    public static String getShowImport() {
        return showImport;
    }


    public static void setJarPath(String path) {
      if(path!=null)
        jarPath = path.trim();
    }
    public static void setShowImport(String showImport) {
      if(showImport!=null)
        Configuration.showImport = showImport.trim();
    }



    public static String getDecompileroption() {
        return decompileroption;
    }


    public static void setDecompileroption(String decompileroption) {
      if(decompileroption!=null)
        Configuration.decompileroption = decompileroption.trim();
    }


    public static void init(String[] configNames)
    {
       try
       {
        ResourceBundle rb=ResourceBundle.getBundle(configNames[0]);
        readAllParameters(rb);
        
       }
        catch(Exception e)
       {
           System.out.println("ResourceBundle File Not Found");
           System.out.println("Please enter a valid path for config.properties file in the script used to Run Jdec");
           System.exit(1);
       }
      try
       {
        ResourceBundle rb=ResourceBundle.getBundle(configNames[1]);
        readFilterParameters(rb);
        
       }
        catch(Exception e)
       {
           System.out.println("consolefilter.properties ResourceBundle File Not Found");
        }
       

    }

    public static void setSkipClassVersionCheck(String skipClassVersionCheck) {
      if(skipClassVersionCheck!=null)
        Configuration.skipClassVersionCheck = skipClassVersionCheck.trim();
    }

    public static String getSkipClassVersionCheck() {
        return skipClassVersionCheck;
    }

    public static void setStringPrefix(String prefix) {
        stringPrefix = prefix.trim();
    }

    public static void setShortPrefix(String prefix) {
       shortPrefix = prefix.trim();
    }

    public static void setOtherPrefix(String prefix) {
        otherPrefix = prefix.trim();
    }

    public  static void setLongPrefix(String prefix) {
       longPrefix = prefix.trim();
    }

    public static void setIntPrefix(String prefix) {
        intPrefix = prefix.trim();
    }

    public static void setFloatPrefix(String prefix) {
        floatPrefix = prefix.trim();
    }

    public static void setDoublePrefix(String prefix) {
        doublePrefix = prefix.trim();
    }

    public static void setCharPrefix(String prefix) {
        charPrefix = prefix.trim();
    }

    public static void setBytePrefix(String prefix) {
        bytePrefix = prefix.trim();
    }

    public static String getStringPrefix() {
        return stringPrefix;
    }

    public static String getBytePrefix() {
        return bytePrefix;
    }

    public static String getCharPrefix() {
        return charPrefix;
    }

    public static String getDoublePrefix() {
        return doublePrefix;
    }

    public static String getFloatPrefix() {
        return floatPrefix;
    }

    public static String getIntPrefix() {
        return intPrefix;
    }

    public static String getLongPrefix() {
        return longPrefix;
    }

    public static String getOtherPrefix() {
        return otherPrefix;
    }

    public static String getShortPrefix() {
        return shortPrefix;
    }


    private static void readAllParameters(ResourceBundle rb)
    {
        String temp=null;
        temp=rb.getString("Output_Mode");
        if(temp!=null){
          setMode(temp);
        }
        
        temp=rb.getString("Output_Folder_Path");
        if(temp!=null){
          setOutputFilePath(temp);
          backupOriginalOutputFilePath(temp);
        }
        
        temp=rb.getString("Log_Mode");
        if(temp!=null){
          setLogMode(temp);
        }
        
        temp=rb.getString("Log_File_Path");
        if(temp!=null){
          setLogFilePath(temp);
        }
        
        temp=rb.getString("LOG_LEVEL");
        if(temp!=null){
          setLogLevel(temp);
        }
        
        temp=rb.getString("JAVA_CLASS_FILE");
        if(temp!=null){
          setJavaClassFile(temp);
          setClassFilePath(temp);
          setClassPath(temp);
        }
        
        temp=rb.getString("JAR_FILE_PATH");
        if(temp!=null){
          setJarPath(temp);
        }
        
        temp=rb.getString("JDec_Option");
        if(temp!=null){
            setDecompileroption(temp);
        }
        
        
        temp=rb.getString("Show_Imports");
        if(temp!=null){
          setShowImport(temp);
        }
        
        temp=rb.getString("Temp_Dir");
        if(temp!=null){
          setTempDir(temp);
        }
        
        temp=rb.getString("Inner_Depth");
        if(temp!=null){
          setInnerdepth(temp);
        }
        
        temp=rb.getString("Inline_Anonymous_Inner_Class_Content");
        if(temp!=null){
          setInlineInnerClassMethodContent(temp);
        }
        
        
        temp=rb.getString("Output_File_Extension");
        if(temp!=null){
          setFileExtension(temp);
        }
        temp=rb.getString("Interpret_Exception_Table");
        if(temp!=null){
          setDetailedExceptionTableInfo(temp);
        }
        
        temp=rb.getString("Skip_Class_Version_Check");
        if(temp!=null){
          setSkipClassVersionCheck(temp);
        }
        

        

        if(fileExtension==null)
        {
            fileExtension="jdec";
        }
        else if(fileExtension.equalsIgnoreCase("java"))
        {
            fileExtension="jdec";
        }
        else if(fileExtension.equalsIgnoreCase("class"))
        {
            fileExtension="jdec";
        }



        try
        {
           innerdepthLevel=Integer.parseInt(innerdepth);  
        }
        catch(NumberFormatException ne)
        {
        	innerdepthLevel=1;
        }

        
        
        
        if(jarPath!=null && jarPath.trim().length() > 0)
            setJarSpecified(true);
        if(jarPath==null || jarPath.trim().length() == 0)
            setJarSpecified(false);
        if(javaClassFile!=null && javaClassFile.trim().length() > 0)
            setSingleClassSpecified(true);
        if(javaClassFile==null || javaClassFile.trim().length() == 0)
            setSingleClassSpecified(false);

        if(decompileroption!=null){
        if(decompileroption.equalsIgnoreCase("decompileClass") || decompileroption.equalsIgnoreCase("dc"))
        {
            setDecompileroption("dc");
        }
        else if(decompileroption.equalsIgnoreCase("disassemble") || decompileroption.equalsIgnoreCase("dis"))
        {
            setDecompileroption("dis");
        }
        else if(decompileroption.equalsIgnoreCase("decompileJar") || decompileroption.equalsIgnoreCase("jar") )
        {
            setDecompileroption("jar");
        }
        else if(decompileroption.equalsIgnoreCase("ConstantPool") || decompileroption.equalsIgnoreCase("vcp"))
        {
            setDecompileroption("vcp");
        }
        else if(decompileroption.equalsIgnoreCase("localVariables") || decompileroption.equalsIgnoreCase("llv")) // Do not make it Public . Only For Debugging Purpose
        {
            setDecompileroption("llv");
        }
        else if(decompileroption.equalsIgnoreCase("Skeleton") || decompileroption.equalsIgnoreCase("nocode"))
        {
            setDecompileroption("nocode");
        }
        else if(decompileroption.equalsIgnoreCase("help"))
        {
            setDecompileroption("help");
        }
        }
        

    }

    public static String getOutputMode()
    {
        return mode;
    }

    public static String getOutputFolderPath()
    {
        return OutputFilePath;
    }
    public static String getLogMode()
    {
        return LogMode;

    }

    public static String getLogFilePath()
    {
    	if(LogFilePath == null){
    		LogFilePath = System.getProperty("user.dir")+File.separator + "jdec_created_log.txt";
    	}
        return LogFilePath;
    }

    public static void setPathForCurrentClassFile(String pathForCurrentClassFile) {
      if(pathForCurrentClassFile!=null)
        Configuration.pathForCurrentClassFile = pathForCurrentClassFile.trim();
    }

    public static void setClassFilePath(java.lang.String path)
    {
      if(path!=null){
        classPath=path.trim();
        pathForCurrentClassFile=path.trim();
        setJavaClassFile(classPath);
      }
      
    }
    private static java.lang.String classPath="";


    // See Not below for this method
    public static String getPathForCurrentClassFile() {
        return pathForCurrentClassFile;
    }

    private static java.lang.String pathForCurrentClassFile=""; // USed while registering any inner class File in
                                                                // Jvmopcodes.NEW
                                                                // This introduces another redundant method
                                                                // BUT because getClassPath was returning
                                                                 // something else , wrote a new getMethod




    /**
     *
     * @return Returns the classPath.
     */
    public static java.lang.String getClassPath() {

        int dotclass=classPath.indexOf(".class");
        if(dotclass!=-1)classPath.replaceAll(".class","."+ getFileExtension());
        return classPath;
    }
    /**
     * @return Returns the debugLevel.
     */
    public static String getLogLevel() {
        return LogLevel;
    }
   
    public static void setLogLevel(String LogLevel) {
        Configuration.LogLevel = LogLevel.trim();
    }
    /**
     * @return Returns the javaClassFile.
     */
    public static String getJavaClassFile() {
        return javaClassFile;
    }
    /**
     * @param javaClassFile The javaClassFile to set.
     */
    public static void setJavaClassFile(String javaClassFile) {
      if(javaClassFile!=null){
        Configuration.javaClassFile = javaClassFile.trim();
        classPath =javaClassFile.trim();
      }
    }


    public static String getJarPath() {
        return jarPath;
    }


    public static void setJarSpecified(boolean jaroption)
    {
        jarSpecified=jaroption;
    }

    public static boolean wasJarSpecified()
    {
        return jarSpecified;
    }

    private static boolean jarSpecified=false;
    private static boolean SingleClassSpecified=false;


    public static boolean wasSingleClassSpecified() {
        return SingleClassSpecified;
    }


    public static void setSingleClassSpecified(boolean singleClassSpecified) {
        SingleClassSpecified = singleClassSpecified;
    }


    public static void setLogFilePath(String logFilePath) {
        LogFilePath = logFilePath.trim();
    }


    public static void setLogMode(String logMode) {
        LogMode = logMode.trim();
    }


    public static void setOutputMode(String mode) {
        Configuration.mode = mode.trim();
    }


    public static void setOutputFolderPath(String outputFilePath) {
      if(outputFilePath!=null)
        OutputFilePath = outputFilePath.trim();
        //backupOriginalOutputFilePath(OutputFilePath);

    }

    public static void backupOriginalOutputFilePath(java.lang.String bkp)
    {
        bkpoppath=bkp;

    }

    public static String getBkpoppath() {
        return bkpoppath;
    }



    private static java.lang.String bkpoppath="";

    private static String consoleDetailFile=null;



    /**
     * @return Returns the consoleDetailFile.
     */
    public static String getConsoleDetailFile() {
        return consoleDetailFile;
    }
    /**
     * @param consoleDetailFile The consoleDetailFile to set.
     */
    public static void setConsoleDetailFile(String consoleDetailFile) {
        Configuration.consoleDetailFile = consoleDetailFile.trim();
    }
    
    
    public static java.lang.String userInput()
    {
    	
     java.lang.String s= "[Output_Mode : "+getOutputMode()+" ]\n";
     s+="[Log_Mode :"+getLogMode()+ " ]\n";
     s+="[Output_Folder_Path :"+getOutputFolderPath()+" ]\n";
     s+="[LOG_LEVEL :"+getLogLevel()+" ]\n";
     s+="[Log_File_Path :"+getLogFilePath()+" ]\n";
     s+="[JAR_FILE_PATH :"+getJarPath()+" ]\n";
     s+="[JAVA_CLASS_FILE :"+getJavaClassFile()+" ]\n";
     s+="[Show_Imports :"+getShowImport()+" ]\n";
     s+="[JDec_Option :"+getDecompileroption()+" ]\n";
     s+="[Temp_Dir :"+tempDir+" ]\n";
     s+="[Inner_Depth :"+getInnerdepth()+" ]\n";
     s+="[Inline_Anonymous_Inner_Class_Content :"+getInlineInnerClassMethodContent()+" ]\n";
     
	 return s;

     
    	
    	
    }
    
	/**
	 * @return Returns the tempDir.
	 */
	public static String getTempDir() {
		return tempDir;
	}
	/**
	 * @param tempDir The tempDir to set.
	 */
	public static void setTempDir(String tempDir) {
      if(tempDir!=null)
		Configuration.tempDir = tempDir.trim();
	}
	/**
	 * @return Returns the mode.
	 */
	public static String getMode() {
		return mode;
	}

    public static String getDetailedExceptionTableInfo() {
        return detailedExceptionTableInfo;
    }

    public static void setDetailedExceptionTableInfo(String detailedExceptionTableInfo) {
        Configuration.detailedExceptionTableInfo = detailedExceptionTableInfo.trim();
    }

    /**
	 * @param inlineInnerClassMethodContent The inlineInnerClassMethodContent to set.
	 */
	public static void setInlineInnerClassMethodContent(
			String inlineInnerClassMethodContent) {
		Configuration.inlineInnerClassMethodContent = inlineInnerClassMethodContent;
	}
	/**
	 * @param innerdepth The innerdepth to set.
	 */
	public static void setInnerdepth(String innerdepth) {
		Configuration.innerdepth = innerdepth;
		 try
	        {
	           innerdepthLevel=Integer.parseInt(innerdepth);  
	        }
	        catch(NumberFormatException ne)
	        {
	        	innerdepthLevel=1;
	        }
	}

    private static void readFilterParameters(ResourceBundle rb) {
         
        dontDecDisPvt=rb.getString("dont_decompile_dis_pvt_methods");
        ConsoleLauncher.setDontpvtmethods((dontDecDisPvt!=null && dontDecDisPvt.equalsIgnoreCase("true"))?true:false);
        dontDecDisStaticInt=rb.getString("dont_decompile_dis_static_init");
        ConsoleLauncher.setDontstaticinit((dontDecDisStaticInt!=null && dontDecDisStaticInt.equalsIgnoreCase("true"))?true:false);
        dontShowNativeMethods=rb.getString("dont_show_native_methods");
        ConsoleLauncher.setDontshownative((dontShowNativeMethods!=null && dontShowNativeMethods.equalsIgnoreCase("true"))?true:false);
        
        dontShowEmptyConst=rb.getString("dont_show_empty_const");
        ConsoleLauncher.setDontshowemptyconst((dontShowEmptyConst!=null && dontShowEmptyConst.equalsIgnoreCase("true"))?true:false);
        dontShowAbsMethods=rb.getString("dont_show_abs_methods");
        ConsoleLauncher.setDontshowabs((dontShowAbsMethods!=null && dontShowAbsMethods.equalsIgnoreCase("true"))?true:false);
        dontDecDisSynthetic=rb.getString("dont_decompile_dis_synthetic");
        ConsoleLauncher.setDontsynth((dontDecDisSynthetic!=null && dontDecDisSynthetic.equalsIgnoreCase("true"))?true:false);
        
        showConstFirst=rb.getString("show_const_before_methods");
        ConsoleLauncher.setShowconstfirst((showConstFirst!=null && showConstFirst.equalsIgnoreCase("true"))?true:false);
        showFieldsAtTop=rb.getString("show_fields_at_top");
        ConsoleLauncher.setShowfieldsfirst((showFieldsAtTop!=null && showFieldsAtTop.equalsIgnoreCase("true"))?true:false);
        showjavalangobject=rb.getString("show_java.lang.Object_superClass");
        ConsoleLauncher.setShowObjectSuperClass((showjavalangobject!=null && showjavalangobject.equalsIgnoreCase("true"))?true:false);
        inCludedPkgs=rb.getString("included_pkgs_in_jar");
        String temp=null;
        temp=rb.getString("regsitered_archive_types");
        if(temp!=null){
          setRegsiteredArchiveTypes(temp);
        }
        temp=rb.getString("process_only_default_pkg_classes");
        if(temp!=null){
          setProcessOnlyDefault(temp);
        }
        temp=rb.getString("process_enclosed_archive_files");
        if(temp!=null){
          setProcessEnclosedArchives(temp);
        }
        
        
    }
    
    private static String inCludedPkgs="pkg-all"; // process all
    private static String regsiteredArchiveTypes="[jar,zip]";
    private static String processOnlyDefault="false";
    private static String processEnclosedArchives="true";
    
    private static String dontDecDisPvt="false";
    private static String dontDecDisStaticInt="false";
    private static String dontShowNativeMethods="false";
    private static String dontShowEmptyConst="false";
    private static String dontShowAbsMethods="false";
    private static String dontDecDisSynthetic="false";

    private static String showConstFirst="false";
    private static String showFieldsAtTop="false";
    private static String showjavalangobject="false";

    public static String getDontDecDisPvt() {
        return dontDecDisPvt;
    }

    public static String getDontDecDisStaticInt() {
        return dontDecDisStaticInt;
    }

    public static  String getDontDecDisSynthetic() {
        return dontDecDisSynthetic;
    }

    public static String getDontShowAbsMethods() {
        return dontShowAbsMethods;
    }

    public static String getDontShowEmptyConst() {
        return dontShowEmptyConst;
    }

    public static  String getDontShowNativeMethods() {
        return dontShowNativeMethods;
    }

    public static String getInCludedPkgs() {
        String value=inCludedPkgs;
        if(value!=null && value.equals("pkg-all"))return "pkg-all";
        if(value==null)return "pkg-all";
        if(value!=null){
            int startbr=value.indexOf("[");
            int endbr=value.indexOf("]");
            if(startbr!=-1 && endbr!=-1){
                value=value.substring(startbr+1,endbr);
                
                StringTokenizer tokens=new StringTokenizer(value,",");
                ArrayList includedList=new ArrayList();
                while(tokens.hasMoreTokens()) {
                    java.lang.String str=(java.lang.String)tokens.nextToken();
                    includedList.add(str);
                }
                ConsoleLauncher.setInclListInJar(includedList);
            }
        }
       
        return inCludedPkgs;
    }

    public static String getProcessEnclosedArchives() {
        return processEnclosedArchives;
    }

    public static String getProcessOnlyDefault() {
        return processOnlyDefault;
    }

    public static String getRegsiteredArchiveTypes() {
        return regsiteredArchiveTypes;
    }

    public static String getShowConstFirst() {
        return showConstFirst;
    }

    public static String getShowFieldsAtTop() {
        return showFieldsAtTop;
    }

    public static String getShowjavalangobject() {
        return showjavalangobject; 
    }

    public static void setBkpoppath(java.lang.String bkpoppath) {
      Configuration.bkpoppath = bkpoppath.trim();
    }

    public static void setClassPath(java.lang.String classPath) {
      Configuration.classPath = classPath;
    }

    public static void setDontDecDisPvt(String dontDecDisPvt) {
      Configuration.dontDecDisPvt = dontDecDisPvt.trim();
    }

    public static void setDontDecDisStaticInt(String dontDecDisStaticInt) {
      Configuration.dontDecDisStaticInt = dontDecDisStaticInt.trim();
    }

    public static void setDontDecDisSynthetic(String dontDecDisSynthetic) {
      Configuration.dontDecDisSynthetic = dontDecDisSynthetic.trim();
    }

    public static void setDontShowAbsMethods(String dontShowAbsMethods) {
      Configuration.dontShowAbsMethods = dontShowAbsMethods.trim();
    }

    public static void setDontShowEmptyConst(String dontShowEmptyConst) {
      Configuration.dontShowEmptyConst = dontShowEmptyConst.trim();
    }

    public static void setDontShowNativeMethods(String dontShowNativeMethods) {
      Configuration.dontShowNativeMethods = dontShowNativeMethods.trim();
    }

    public static void setInCludedPkgs(String inCludedPkgs) {
      Configuration.inCludedPkgs = inCludedPkgs.trim();
    }

    public static void setInnerdepthLevel(int innerdepthLevel) {
      Configuration.innerdepthLevel = innerdepthLevel;
    }

    public static void setMode(String mode) {
      Configuration.mode = mode.trim();
    }

    public static void setOutputFilePath(String outputFilePath) {
      OutputFilePath = outputFilePath.trim();
    }

    public static void setProcessEnclosedArchives(String processEnclosedArchives) {
      Configuration.processEnclosedArchives = processEnclosedArchives.trim();
    }

    public static void setProcessOnlyDefault(String processOnlyDefault) {
      Configuration.processOnlyDefault = processOnlyDefault.trim();
    }

    public static void setRegsiteredArchiveTypes(String regsiteredArchiveTypes) {
      Configuration.regsiteredArchiveTypes = regsiteredArchiveTypes.trim();
    }

    public static void setShowConstFirst(String showConstFirst) {
      Configuration.showConstFirst = showConstFirst.trim();
    }

    public static void setShowFieldsAtTop(String showFieldsAtTop) {
      Configuration.showFieldsAtTop = showFieldsAtTop.trim();
    }

    public static void setShowjavalangobject(String showjavalangobject) {
      Configuration.showjavalangobject = showjavalangobject.trim();
    }
    
    

        
}