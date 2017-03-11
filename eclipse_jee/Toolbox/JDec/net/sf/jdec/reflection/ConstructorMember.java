/*
 *  ConstructorMember.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.reflection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jdec.util.Util;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.MethodInfo;
import net.sf.jdec.constantpool.RuntimeVisibleAnnotations;
import net.sf.jdec.core.LocalVariableStructure;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.blocks.TryBlock;
import net.sf.jdec.blocks.CatchBlock;
import net.sf.jdec.blocks.FinallyBlock;


public class ConstructorMember  extends Behaviour{
    
    
    private ShortcutAnalyser shortCutA=null;
    private java.lang.String name="";
    private String params[]=null;
    private int numberofparams=-1;
    private int numberofexceptions=-1;
    private String exceptions[]=null;
    private boolean isDefault=false;
    private byte[] code=null;
    private java.lang.String declaringClass=null;
    private java.lang.String[] accessSpecifier= null;
    private ArrayList allTrys=new ArrayList();
    private ArrayList createdTableList=new ArrayList();
    private ArrayList allswitches=new ArrayList();
    private ArrayList behaviourLoops = new ArrayList();
    private LocalVariableStructure localVars = new LocalVariableStructure();
    
    
    public LocalVariableStructure getLocalVariables() {
        return localVars;
    }
    
    public void setMethodLocalVariables(LocalVariableStructure methodLocalVariables) {
        this.localVars = methodLocalVariables;
    }
    
    
    
    public ArrayList getSynchronizedEntries() {
        return synchronizedEntries;
    }
    
    private ArrayList synchronizedEntries;
    
    
    
    public ConstructorMember(String name) {
        this.name=name;
    }
    
    public ConstructorMember(java.lang.String name,java.lang.String Class) {
        this.name=name;
        this.declaringClass=Class;
    }
    
    public java.lang.String[] getAccessSpecifier() {
        return accessSpecifier;
    }
    public void setAccessSpecifier(java.lang.String[] accessSpecifier) {
        this.accessSpecifier = accessSpecifier;
    }
    public byte[] getCode() {
        return code;
    }
    public void setCode(byte[] code) {
        this.code = code;
    }
    public java.lang.String getDeclaringClass() {
        return declaringClass;
    }
    public void setDeclaringClass(java.lang.String declaringClass) {
        this.declaringClass = declaringClass;
    }
    // Alternate method to use is getExceptionTypes
    // Try to use the latter one so that Behaviour Type
    // can be used when invoking.
    
    public java.lang.String[] getExceptions() {
        return exceptions;
    }
    public void setExceptionsTypes(java.lang.String[] exceptions) {
        this.exceptions = exceptions;
        setNumberofexceptions(exceptions.length);
    }
    public boolean isDefault() {
        return isDefault;
    }
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    public java.lang.String getName() {
        return name;
    }
    public int getNumberofexceptions() {
        return numberofexceptions;
    }
    private void setNumberofexceptions(int numberofexceptions) {
        this.numberofexceptions = numberofexceptions;
    }
    
    
    
    
    public int getNumberofparamters() {
        return numberofparams;
    }
    private void setNumberofparams(int numberofparams) {
        this.numberofparams = numberofparams;
    }
    public String[] getParams() {
        return params;
    }
    public void setParams(String[] params) {
        this.params = params;
        setNumberofparams(params.length);
    }
    
    public String toString() {
        String temp="";
        temp+=this.accessSpecifier+"\t"+this.name+"(";
        for(int i=0;i<this.getNumberofparamters();i++) {
            temp+=this.getParams()[i];
            if(i!=this.getNumberofparamters())temp+=",";
        }
        temp+=")";
        
        for(int i=0;i<this.getNumberofexceptions();i++) {
            temp+=this.getExceptions()[i];
            if(i!=this.getNumberofexceptions())temp+=",";
            
        }
        
        temp+="{\n\t";
        temp+=this.getCode();
        temp+="\n}";
        return temp;
    }
    
    public String getBehaviourName() {
        
        java.lang.String v=name;
        StringBuffer bb=new StringBuffer("");
        Util.checkForImport(v,bb);
        return bb.toString();
    }
    
    
    
    public java.lang.String getUserFriendlyMethodAccessors() {
        
        String temp="";
        for(int i=0;i<accessSpecifier.length;i++) {
            temp+=accessSpecifier[i];
            temp+="  ";
        }
        return temp;
    }
    public java.lang.String getReturnType() {
        return "";
    }
    
    public java.lang.String  getUserFriendlyMethodParams() {
        String temp="";
        if(params.length < 1) {
            temp="( )";
        } else {
            temp+="(";
            for(int i=0;i<params.length;i++) {
                String tmp="";
                if(i > 0 && i< params.length)temp+=",";
                if(params[i].indexOf("[")!=-1 || params[i].indexOf("L")!=-1) {
                    tmp=parseObjectType(params[i]);
                    temp+=tmp;
                } else {
                    temp+=params[i];
                }
                temp+="  ";
            }
            temp+=")";
        }
        return temp;
    }
    private static String parseObjectType(java.lang.String str) {
        String type="";
        int ch;
        int counter=0;
        String refName="";
        ch=str.indexOf("[");
        if(ch!=-1) {
            
            while(ch!=-1) {
                counter++;
                ch=str.indexOf("[",ch+1);
            }
            ch=str.indexOf("L");
            if(ch!=-1) {
                refName=str.substring(ch+1);
                if(refName.indexOf(";")!=-1)
                    refName=refName.substring(0,refName.indexOf(";"));
            }
            if(ch==-1) {
                refName=str.substring(str.lastIndexOf("[")+1);
                if(refName.indexOf(";")!=-1)
                    refName=refName.substring(0,refName.indexOf(";"));
            }
            type+=refName+"   ";
            for(int count=0;count<counter;count++) {
                type+="[]";
            }
            
            return type;
        }
        
        else if(str.indexOf("L")!=-1) {
            ch=str.indexOf("L");
            refName=str.substring(ch+1);
            if(refName.indexOf(";")!=-1)
                refName=refName.substring(0,refName.indexOf(";"));
            
            type+=refName+"\t";
            
            return type;
        } else {
            return " ERROR";
        }
        
    }
    
    public java.lang.String getStringifiedParameters() {
        java.lang.String temp="";
        
        
        
        List al=(List)java.util.Arrays.asList(params);
        Iterator i=al.iterator();
        if(i.hasNext()==false){
            temp+="()";
            return temp;
        } else {
            temp+="(";
            while(i.hasNext()) {
                temp+= (java.lang.String)i.next();
                if(i.hasNext()) {
                    temp += ",";
                }
            }
            temp+=")";
            return temp;
        }
    }
    
    public String[] getMethodParams() {
        return params;
    }
    
    public String[] getMethodAccessors() {
        
        return accessSpecifier;
    }
    public  void setExceptionTableList(ArrayList tables) {
        this.exceptionTables=tables;
    }
    
    
    public  ArrayList getExceptionTableList() {
        return exceptionTables;
    }
    
    public  ArrayList getAllTriesForMethod() {
        return allTrys;
    }
    public  void setAllTriesForMethod(ArrayList alltries){
        
        allTrys=alltries;
    }
    
    public void setCreatedTableList(ArrayList list) {
        this.createdTableList=list;
        
    }
    public ArrayList   getCreatedTableList() {
        return createdTableList;
        
    }
    
    public String[] getExceptionTypes() {
        return this.exceptions;
    }
    public ArrayList getAllSwitchBlks() {
        return allswitches;
    }
    
    public void setSynchronizedTableEntries(ArrayList list) {
        this.synchronizedEntries=list;
        //private ArrayList synchronizedEntries;
    }
    
    public void setAllswitches(ArrayList allswitches) {
        this.allswitches = allswitches;
    }
    
    public ArrayList getBehaviourLoops() {
        return behaviourLoops;
    }
    
    public void setBehaviourLoops(ArrayList indexInfiniteLoop) {
        this.behaviourLoops = indexInfiniteLoop;
    }
    
    public  ArrayList getInstructionStartPositions() {
        return this.starts;
    }
    
    public void setInstrStartPos(ArrayList starts) {
        this.starts=starts;
    }
    private ArrayList starts=null;
    
    
    public java.lang.String removeDummyLabels() {
        java.lang.String temp=getCodeStatements();
        //if(getLabels()==null || getLabels().size()==0)return temp;
        int i=-1;
        int start=0;
        String copy=getCodeStatements();
        i=copy.indexOf("#FORINDEX",start);
        while(i!=-1) {
            int j=i+9;
            int hash=copy.indexOf("#",(i+1));
            java.lang.String num=copy.substring(j,hash);
            java.lang.String dummy=copy.substring(i,(hash+1));
            int inum=Integer.parseInt(num);
            boolean skip=false;
            if(getLabels()!=null && getLabels().size()!=0) {
                java.lang.String lbl=(java.lang.String)getLabels().get(new Integer(inum));
                if(lbl!=null) {
                    copy=copy.replaceAll(dummy,lbl);
                    skip=true;
                }
            }
            if(!skip) {
                copy=copy.replaceAll("\n"+dummy+"\n","");
            }
            
            i=copy.indexOf("#FORINDEX",start);
            
        }
        temp=copy;
        temp=temp.replaceAll("\n\n","\n");
        
        return temp;
        
        
    }
    
    public  boolean isMethodConstructor() {
        return true;
    }
    
    public  void  createExceptionTableInStringifiedForm() {
        java.lang.String detailed= Configuration.getDetailedExceptionTableInfo();
        if(detailed==null)detailed="false";
        StringBuffer synch=new StringBuffer("");
        StringBuffer str=new StringBuffer("");
        ArrayList alltables=getExceptionTableList();
        ArrayList alltries=getAllTriesForMethod();
        if(alltables==null || alltables.size()==0) {
            
        }
        /*if(alltables.size() > 0)
        {
            str.append("TRY_CATCH_FINALLY BLOCK DETAILS...\n");
            str.append("StartPC\tEndPC\tHandlerPC\tException Handler Name\n");
         
            str.append("Synchronized BLOCK DETAILS...\n");
            str.append("StartPC\tEndPC\tHandlerPC\tException Handler Name\n");
        } */
        boolean showExcep=false;
        boolean showSynch=false;
        for(int z=0;z<alltables.size();z++) {
            
            MethodInfo.ExceptionTable curtab=(MethodInfo.ExceptionTable)alltables.get(z);
            int startpc=curtab.getStartPC();
            int endpc=curtab.getEndPC();
            int hpc=curtab.getStartOfHandler();
            java.lang.String temp=" "+startpc+"   ";
            temp+=endpc+"    ";
            temp+=hpc+"    ";
            java.lang.String name=curtab.getExceptionName();
            if(name.trim().equals("<any>")) {
                name="Any Handler(Special Handler)";
            }
            temp+="     "+name+" ";
            if(curtab.getMonitorEnterPosInCode()==-1) {
                showExcep=true;
                str.append(Util.formatDisassembledOutput(temp+"\n"));
            } else {
                showSynch=true;
                synch.append(Util.formatDisassembledOutput(temp+"\n"));
            }
            
        }
        
        if(showExcep) {
            StringBuffer  sb=new StringBuffer("");
            sb.append(Util.formatDisassembledOutput("TRY_CATCH_FINALLY BLOCK DETAILS...\n"));
            sb.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
            sb.append(Util.formatDisassembledOutput("StartPC\tEndPC\tHandlerPC\tException Handler Name\n"));
            sb.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
            shortExceptionTableDetails=sb.toString()+"\n"+str.toString()+"\n\n";
            detailedExceptionTableDetails=sb.toString()+"\n"+str.toString()+"\n\n";
        }
        if(showSynch) {
            StringBuffer  sb=new StringBuffer("");
            sb.append(Util.formatDisassembledOutput("Synchronized BLOCK DETAILS...\n"));
            sb.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
            sb.append(Util.formatDisassembledOutput("StartPC\tEndPC\tHandlerPC\tException Handler Name\n"));
            sb.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
            synchTableDetails=sb.toString()+"\n"+synch.toString();
        }
        if(detailed.equalsIgnoreCase("true")) {
            if(alltries!=null && alltries.size() > 0) {
                StringBuffer tries=new StringBuffer("");
                tries.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
                tries.append(Util.formatDisassembledOutput("Try Block Details\n"));
                tries.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
                for(int f=0;f<alltries.size();f++) {
                    tries.append(Util.formatDisassembledOutput("*** START OF TRY BLOCK ***\n\n"));
                    TryBlock tryb=(TryBlock)alltries.get(f);
                    int num=tryb.getNumberOfCatchBlks();
                    int start=tryb.getStart();
                    int end=tryb.getEnd();
                    tries.append((Util.formatDisassembledOutput(start+" Is the Start of This Try Block\n")));
                    tries.append((Util.formatDisassembledOutput(end+" Is the End of This Try Block\n")));
                    ArrayList catches=tryb.getAllCatchesForThisTry();
                    if(catches!=null && catches.size() > 0) {
                        tries.append((Util.formatDisassembledOutput(num+" Is the Number of Catch blocks In Method For This Try\n")));
                        for(int t=0;t<catches.size();t++) {
                            CatchBlock cblk=(CatchBlock)catches.get(t);
                            int cs=cblk.getStart();
                            int ce=cblk.getEnd();
                            tries.append(Util.formatDisassembledOutput("\nCATCH BLOCK "+(t+1)+" Details....\n\n"));
                            tries.append((Util.formatDisassembledOutput(cs+" Is the Catch Start For This Try\n")));
                            tries.append((Util.formatDisassembledOutput(ce+" Is the Catch End For This Try\n")));
                        }
                    }
                    if(tryb.hasFinallyBlk()) {
                        FinallyBlock block=tryb.getFinallyBlock();
                        if(block!=null) {
                            int s=block.getStart();
                            int e=block.getEnd();
                            tries.append(Util.formatDisassembledOutput("\nFinally BLOCK Details....\n\n"));
                            tries.append((Util.formatDisassembledOutput(s+ "is the start of FinallY Block For This Try\n")));
                            tries.append((Util.formatDisassembledOutput(e+ "is the end of FinallY Block For This Try\n")));
                        }
                        
                    }
                    
                    tries.append((Util.formatDisassembledOutput("\n*** END OF TRY BLOCK ***\n\n")));
                    
                }
                
                detailedExceptionTableDetails+=(Util.formatDisassembledOutput("\n\n"));
                detailedExceptionTableDetails+=tries.toString();
                
            }
            
        }
        
        
        
    }
    private java.lang.String shortExceptionTableDetails="";
    private java.lang.String detailedExceptionTableDetails="";
    private java.lang.String synchTableDetails="";
    
    public String getShortExceptionTableDetails() {
        return shortExceptionTableDetails;
    }
    
    
    
    public String getDetailedExceptionTableDetails() {
        return detailedExceptionTableDetails;
    }
    
    
    
    public String getSynchTableDetails() {
        return synchTableDetails;
    }
    
    public  void setShortCutAnalyser(ShortcutAnalyser sa){
        shortCutA=sa;
    }
    public  ShortcutAnalyser getShortCutAnalyser(){
        return shortCutA;
    }

    public void setClassRef(JavaClass classRef) {
        this.classRef = classRef;
    }

    public void setDatatypesForParams(Map datatypesForParams) {
        this.datatypesForParams = datatypesForParams;
    }
    
	
	

}
