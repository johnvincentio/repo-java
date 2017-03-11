/*
 *  MethodMember.java Copyright (c) 2006,07 Swaroop Belur 
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

import net.sf.jdec.blocks.CatchBlock;
import net.sf.jdec.blocks.FinallyBlock;
import net.sf.jdec.blocks.TryBlock;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.MethodInfo;
import net.sf.jdec.core.LocalVariableStructure;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MethodMember  extends Behaviour{


    private ShortcutAnalyser shortCutA=null;
    private java.lang.String methodName=null;
	private java.lang.String[] methodAccessors=null;
	private java.lang.String[] methodParams=null;
	private int numberofparamters=-1;
	private java.lang.String returnType=null;
	private boolean isAbstract=false;
	private java.lang.String[] exceptionTypes=null;
	private java.lang.String declaringClass=null;
	private byte[] code;
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

    /**
	 * @return Returns the declaringClass.
	 */
	public java.lang.String getDeclaringClass() {
		return declaringClass;
	}
	/**
	 * @param declaringClass The declaringClass to set.
	 */
	public void setDeclaringClass(java.lang.String declaringClass) {
		this.declaringClass = declaringClass;
	}
	/**
	 * @return Returns the exceptionTypes.
	 */
	public String[] getExceptionTypes() {
		return exceptionTypes;
	}
	/**
	 * @param exceptionTypes The exceptionTypes to set.
	 */
	public void setExceptionTypes(String[] exceptionTypes) {
		this.exceptionTypes = exceptionTypes;
	}
	/**
	 * @return Returns the isAbstract.
	 */
	public boolean isAbstract() {
		return isAbstract;
	}
	/**
	 * @param isAbstract The isAbstract to set.
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	/**
	 * @return Returns the methodAccessors.
	 */
	public java.lang.String[] getMethodAccessors() {
		return methodAccessors;
	}

	public java.lang.String getUserFriendlyMethodAccessors() {

		String temp="";
		for(int i=0;i<methodAccessors.length;i++)
		{
			temp+=methodAccessors[i];
			temp+="  ";
		}
		return temp;
	}
	/**
	 * @param methodAccessors The methodAccessors to set.
	 */
	public void setMethodAccessors(java.lang.String[] methodAccessors) {
		this.methodAccessors = methodAccessors;
	}
	/**
	 * @return Returns the methodName.
	 */
	public java.lang.String getMethodName() {
		return methodName;
	}
	/**
	 * @param methodName The methodName to set.
	 */
	public void setMethodName(java.lang.String methodName) {
		this.methodName = methodName;
	}
	/**
	 * @return Returns the methodParams.
	 */
	public String[] getMethodParams() {
		return methodParams;
	}


	public String getUserFriendlyMethodParams() {
	   String temp="";
	  if(!this.getBehaviourName().equalsIgnoreCase("static"))
	  {
	   if(methodParams.length < 1 )
	   {

		   temp="( )";
	   }

	   else
	   {
		   temp+="(";
		   for(int i=0;i<methodParams.length;i++)
		   {
			   String tmp="";
			   if(i > 0 && i< methodParams.length)temp+=",";
			   if(methodParams[i].indexOf("[")!=-1 || methodParams[i].indexOf("L")!=-1)
			   {
				  tmp=parseObjectType(methodParams[i]);
				  temp+=tmp;
			   }
			   else
			   {
				   temp+=methodParams[i];
			   }
			   temp+="  ";
		   }
		   temp+=")";
	   }
		return temp;
	  }
	  else
	  {
		  return "";
	  }
	}
	/**
	 * @param methodParams The methodParams to set.
	 */
	// TODO : What if one of the parameters is an array.
	public void setMethodParams(String[] methodParams) {
		this.methodParams = methodParams;
		setNumberofparamters(methodParams.length);
	}
	/**
	 * @return Returns the numberofparamters.
	 */
	public int getNumberofparamters() {
		return numberofparamters;
	}
	/**
	 * @param numberofparamters The numberofparamters to set.
	 */
	private void setNumberofparamters(int numberofparamters) {
		this.numberofparamters = numberofparamters;
	}
	/**
	 * @return Returns the returnType.
	 */
	public String getReturnType() {
		return returnType;
	}
	/**
	 * @param returnType The returnType to set.
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/* Call This method to print out
	 * The Signature And Code Of The Method
	 * As It Appears In The Java File
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

	java.lang.String temp="";
	int length=this.methodAccessors.length;
	for(int i=0;i<length;i++)
	{
		temp+=this.methodAccessors[i];
		temp+=" ";
	}
	temp+=this.returnType;
	temp+="\t"+this.getMethodName();
	temp+="{";
	/**
	 * See Note At end for getRandomName().
	 * If The name of parameter name can be found out
	 * Then dont call this method.
	 */
	for(int count=0;count<numberofparamters;count++)
	{
		temp+=this.getMethodParams()[count]+getRandomName();
		if(count!=numberofparamters)temp+=",";
	}
	temp+=")\t";
	int except_count=this.getExceptionTypes().length;
	for(int count=0;count<except_count;count++)
	{
		temp+=this.getExceptionTypes()[count];
		if(count!=except_count)temp+=",";
	}
	if(this.getCode().length>0)
	{
		temp+="\n{\n\t";
		temp+=getCode();
		temp+="\n}";

	}
	else
	{
		temp+=";";
	}

	return temp;
	}

	
	public byte[] getCode() {
		return code;
	}


	/**
	 * @param code The code to set.
	 */
	public void setCode(byte[] code) {
		this.code = code;
	}

	// NOTE: Modify if The random name returned clashes with
	// some other name
	// This method is used to create a random name
	// for method parameter name.

	private String getRandomName()
	{
		double num=Math.random();
		return new String("var"+num);
	}


	private static String parseObjectType(java.lang.String str)
	{
		String type="";
		int ch;
		int counter=0;
		String refName="";
		ch=str.indexOf("[");
		if(ch!=-1)
		{

		 while(ch!=-1)
		 {
			 counter++;
			 ch=str.indexOf("[",ch+1);
		 }
		 ch=str.indexOf("L");
		 if(ch!=-1)
		 {
			 refName=str.substring(ch+1);
			 if(refName.indexOf(";")!=-1)
				 refName=refName.substring(0,refName.indexOf(";"));
		 }
		 if(ch==-1)
		 {
			 refName=str.substring(str.lastIndexOf("[")+1);
			 if(refName.indexOf(";")!=-1)
				 refName=refName.substring(0,refName.indexOf(";"));
		 }
		 type+=refName+" ";
		 for(int count=0;count<counter;count++)
		 {
			 type+="[]";
		 }

		 return type;
		}

		else if(str.indexOf("L")!=-1)
		{
			ch=str.indexOf("L");
			refName=str.substring(ch+1);
			if(refName.indexOf(";")!=-1)
			refName=refName.substring(0,refName.indexOf(";"));

			 type+=refName;

			 return type;
		}
		else
		{
			return " ERROR";
		}

	}
	public String getBehaviourName() {

        java.lang.String v=methodName;
        StringBuffer bb=new StringBuffer("");
        //Util.checkForImport(v,bb);
        return v;//bb.toString();

	}

    public void setVMInstructions(String code) {
        super.setVMInstructions(code);    //To change body of overridden methods use File | ExceptionSettings | File Templates.
    }

	public java.lang.String getStringifiedParameters()
	{
		java.lang.String temp="";



		List al=java.util.Arrays.asList(methodParams);
		Iterator i=al.iterator();
		if(i.hasNext()==false){
			temp+="()";
			return temp;
		}
		else
		{
			temp+="(";
			while(i.hasNext())
			{

				temp+= (java.lang.String)i.next();
				if(i.hasNext())
				{
					temp += ",";
				}
			}
			temp+=")";
			return temp;
		}
	}


     public  void setExceptionTableList(ArrayList tables)
     {
           this.exceptionTables=tables;
     }


    public  ArrayList getExceptionTableList()
    {
    //	exceptionTables=sortExceptionTableList(exceptionTables);
    	return exceptionTables;
    }

    public  ArrayList getAllTriesForMethod()
    {
    	return allTrys;
    }
    public  void setAllTriesForMethod(ArrayList alltries){

    	allTrys=alltries;
    }
	// TODO later
    private ArrayList sortExceptionTableList(ArrayList exceptionTables)
    {
    	ArrayList sortedList=new ArrayList();

    	return sortedList;
    }
	public void setCreatedTableList(ArrayList list) {

	 this.createdTableList=list;
	}
	public ArrayList getCreatedTableList() {
		return createdTableList;

	}
	public ArrayList getAllSwitchBlks()
	{
		return allswitches;
	}

    public void setSynchronizedTableEntries(ArrayList list) {
        synchronizedEntries=list;
    }

    public void setAllswitches(ArrayList allswitches) {
        this.allswitches = allswitches;
    }
	public ArrayList getBehaviourLoops() {
		return behaviourLoops;
	}
	public void setBehaviourLoops(ArrayList behaviourLoops) {
		this.behaviourLoops = behaviourLoops;
	}
    public  ArrayList getInstructionStartPositions()
    {
         return this.starts;
    }

    public void setInstrStartPos(ArrayList starts)
    {
       this.starts=starts;
    }
    private ArrayList starts=null;


     public java.lang.String removeDummyLabels()
    {
      java.lang.String temp=getCodeStatements();
          //if(getLabels()==null || getLabels().size()==0)return temp;
         int i=-1;
         int start=0;
         String copy=getCodeStatements();
         i=copy.indexOf("#FORINDEX",start);
         while(i!=-1)
         {
           int j=i+9;
           int hash=copy.indexOf("#",(i+1));
           java.lang.String num=copy.substring(j,hash);
           java.lang.String dummy=copy.substring(i,(hash+1));
           int inum=Integer.parseInt(num);
           boolean skip=false;
           if(getLabels()!=null)
           {
               java.lang.String lbl=(java.lang.String)getLabels().get(new Integer(inum));
               if(lbl!=null)
               {
                  copy=copy.replaceAll(dummy,lbl+":\n");
                  skip=true;
               }
           }
           if(!skip)
           {
             //copy=copy.replaceAll(dummy,"");
             copy=copy.replaceAll("\n"+dummy+"\n","");
           }

           i=copy.indexOf("#FORINDEX",start);

         }
         temp=copy;
         temp=temp.replaceAll("\n\n","\n");
         return temp;


    }

    public  boolean isMethodConstructor()
    {
           return false;
    }


    public  void  createExceptionTableInStringifiedForm()
    {
        java.lang.String detailed= Configuration.getDetailedExceptionTableInfo();
        if(detailed==null)detailed="false";
        StringBuffer synch=new StringBuffer("");
        StringBuffer str=new StringBuffer("");
        ArrayList alltables=getExceptionTableList();
        ArrayList alltries=getAllTriesForMethod();
        if(alltables==null || alltables.size()==0)
        {

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
        for(int z=0;z<alltables.size();z++)
        {

            MethodInfo.ExceptionTable curtab=(MethodInfo.ExceptionTable)alltables.get(z);
            int startpc=curtab.getStartPC();
            int endpc=curtab.getEndPC();
            int hpc=curtab.getStartOfHandler();
            java.lang.String temp=" "+startpc+"   ";
            temp+=endpc+"    ";
            temp+=hpc+"    ";
            java.lang.String name=curtab.getExceptionName();
            if(name.trim().equals("<any>"))
            {
                name="Any Handler(Special Handler)";
            }
            temp+="     "+name+" ";
            if(curtab.getMonitorEnterPosInCode()==-1)
            {
               showExcep=true;
               str.append(Util.formatDisassembledOutput(temp+"\n"));
            }
            else
            {
                showSynch=true;
                synch.append(Util.formatDisassembledOutput(temp+"\n"));
            }

        }

        if(showExcep)
        {
           StringBuffer  sb=new StringBuffer("");
           sb.append(Util.formatDisassembledOutput("TRY_CATCH_FINALLY BLOCK DETAILS...\n"));
           sb.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
           sb.append(Util.formatDisassembledOutput("StartPC\tEndPC\tHandlerPC\tException Handler Name\n"));
            sb.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
           shortExceptionTableDetails=sb.toString()+"\n"+str.toString()+"\n\n";
           detailedExceptionTableDetails=sb.toString()+"\n"+str.toString()+"\n\n";
        }
        if(showSynch)
        {
           StringBuffer  sb=new StringBuffer("");
           sb.append(Util.formatDisassembledOutput("Synchronized BLOCK DETAILS...\n"));
           sb.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
           sb.append(Util.formatDisassembledOutput("StartPC\tEndPC\tHandlerPC\tException Handler Name\n"));
           sb.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
           synchTableDetails=sb.toString()+"\n"+synch.toString();
        }
        if(detailed.equalsIgnoreCase("true"))
        {
            if(alltries!=null && alltries.size() > 0)
            {
                StringBuffer tries=new StringBuffer("");
                tries.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
                tries.append(Util.formatDisassembledOutput("Try Block Details\n"));
                tries.append(Util.formatDisassembledOutput("----------------------------------------------------\n"));
                for(int f=0;f<alltries.size();f++)
                {
                    tries.append(Util.formatDisassembledOutput("*** START OF TRY BLOCK ***\n\n"));
                    TryBlock tryb=(TryBlock)alltries.get(f);
                    int num=tryb.getNumberOfCatchBlks();
                    int start=tryb.getStart();
                    int end=tryb.getEnd();
                    tries.append((Util.formatDisassembledOutput(start+" Is the Start of This Try Block\n")));
                    tries.append((Util.formatDisassembledOutput(end+" Is the End of This Try Block\n")));
                    ArrayList catches=tryb.getAllCatchesForThisTry();
                    if(catches!=null && catches.size() > 0)
                    {
                        tries.append((Util.formatDisassembledOutput(num+" Is the Number of Catch blocks In Method For This Try\n")));
                        for(int t=0;t<catches.size();t++)
                        {
                            CatchBlock cblk=(CatchBlock)catches.get(t);
                            int cs=cblk.getStart();
                            int ce=cblk.getEnd();
                            tries.append(Util.formatDisassembledOutput("\nCATCH BLOCK "+(t+1)+" Details....\n\n"));
                            tries.append((Util.formatDisassembledOutput(cs+" Is the Catch Start For This Try\n")));
                            tries.append((Util.formatDisassembledOutput(ce+" Is the Catch End For This Try\n")));
                        }
                    }
                    if(tryb.hasFinallyBlk())
                    {
                        FinallyBlock block=tryb.getFinallyBlock();
                        if(block!=null)
                        {
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

    public void setClassRef(JavaClass classRef) {
        this.classRef = classRef;
    }

    public void setDatatypesForParams(Map datatypesForParams) {
        this.datatypesForParams = datatypesForParams;
    }

    public  void setShortCutAnalyser(ShortcutAnalyser sa){
        shortCutA=sa;
    }
    public  ShortcutAnalyser getShortCutAnalyser(){
        return shortCutA;
    }

}
