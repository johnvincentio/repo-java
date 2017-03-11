/*
 *  Disassembler.java Copyright (c) 2006,07 Swaroop Belur
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
package net.sf.jdec.core;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.math.BigInteger;

import net.sf.jdec.io.Writer;
import net.sf.jdec.blocks.CatchBlock;
import net.sf.jdec.blocks.FinallyBlock;
import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.blocks.TryBlock;
import net.sf.jdec.blocks.TryCatchFinally;
import net.sf.jdec.blocks.Switch.Case;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.ClassInfo;
import net.sf.jdec.constantpool.DoublePrimitive;
import net.sf.jdec.constantpool.FieldRef;
import net.sf.jdec.constantpool.FloatPrimitive;
import net.sf.jdec.constantpool.IntPrimitive;
import net.sf.jdec.constantpool.InterfaceMethodRef;
import net.sf.jdec.constantpool.LongPrimitive;
import net.sf.jdec.constantpool.MethodRef;
import net.sf.jdec.constantpool.NameAndType;
import net.sf.jdec.constantpool.CPString;
import net.sf.jdec.constantpool.MethodInfo.ExceptionTable;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.reflection.ConstructorMember;
import net.sf.jdec.util.AllExceptionHandler;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;


public class Disassembler {

    Behaviour behaviour;
    ClassDescription cd;
    OperandStack opStack;
    boolean prevInstInvokeVirtual = false;
    private boolean hasIfBeenGenerated = false;
    private int ifCloseLineNo = -1;
    private boolean hasElseBeenGenerated = false;
    private int elseCloseLineNo = -1;
    private boolean isPrevInstructionIf = false;
    private boolean isWhileInScope = false;
    private boolean isIfInScope = false;
    private Hashtable ifHashTable = new Hashtable();
    private Hashtable elseHashTable = new Hashtable();
    private int whileIndex = 0;
    private int ifLevel = 0;
    private int blockLevel = -1;
    private LocalVariable prevLocalGenerated = null;
    private boolean elsehasbegun=false;
    private boolean ifhasbegun=false;
    private Hashtable synchSkips = null;

    /***
     *
     * @param behaviour Represents Either an Instance
     * of Method of a class or a constructor of a class
     */

    public Disassembler(Behaviour behaviour,ClassDescription cd) {
        Util.resetCurrentSpace();
        StringBuffer t=new StringBuffer("");
        Util.checkForImport(behaviour.getReturnType(),t);
        this.behaviour=behaviour;
        this.cd=cd;
        //opStack = new OperandStack();
        opStack = behaviour.getOpStack();
        if(cd.isClassCompiledWithMinusG()==false)
        {

            storeDataTypesForMethodParams(this.behaviour,this.cd);
            createLocalVariablesForMethodParams(this.behaviour,this.cd);
            if(this.behaviour.getCode()!=null)
            {
                //BigInteger b
                storeDataTypesWRTConversionInst(this.behaviour,this.cd);
                storeDataTypesWRTMethodParams(this.behaviour,this.cd);
                storeDataTypesWRTLoadNStoreCombinations(this.behaviour,this.cd);
                long l=-1,l2=-1;
                try
                {   anewarrayrefpos=new Hashtable();
                    problematicInvokes=new ArrayList();
                    storeDataTypesWRTInvokeInst(this.behaviour,this.cd);

                }
                catch(RuntimeException re) {
                    //AllExceptionHandler handler=new AllExceptionHandler(re,"Runtime Exception while handling storeDataTypesWRTInvokeInst");
                    //handler.setBehaviour(behaviour);
                    //handler.reportException();         // TODO: Uncomment This later
                   // ConsoleLauncher.addCouldNotFinish(behaviour);
                     //l2=System.currentTimeMillis();
                   // System.out.println("After Invoke Catch"+(l2-l)/1000);
                    return;
                }

            }
            //java.lang.String s="sdfasdf";


        }
        if(cd.isClassCompiledWithMinusG())
        {
           // storeDataTypesWRTInvokeInst(this.behaviour,this.cd);
        }

        findSkipRangesWRTSynchronizedBlocks();



    }

    public void disassembleCode() {
        StringBuffer buffer=new StringBuffer();
        try {
            if(behaviour==null) {
                java.lang.String mesg="Exception Occured while Disassembling Code\n";
                mesg+="The behaviour Object was null";
                AllExceptionHandler handler=new AllExceptionHandler(new NullPointerException(mesg));
                handler.reportException();

            } else {
                // Log Message

                java.lang.String mesg="\n[INFO]Attempting to generate code represented By\n";
                mesg+="[INFO]Method Name :\t"+behaviour.getBehaviourName()+"\n";
                Writer writer=Writer.getWriter("log");
                writer.writeLog(mesg, Configuration.getLogLevel());
                writer.flush();

                // Write to ConsoleDetail file For UI....





                // Attempt to Dis

                byte[] bytecodes=behaviour.getCode();


                /** NOTE:
                 *
                 * opcodes below represents the disassembled String
                 * for the behaviour...
                 * This Disasembler Object has the object ref to
                 * the behaviour passed.
                 * Since the opcode is the complete disassembled
                 * code for this behaviour, we can go and set
                 * it for the behaviour object using Reflection API.
                 * Later on The code to print out the complete
                 * representation of the Class can access the
                 * Reflection API to print out the class...
                 *
                 *
                 */

                long l1=System.currentTimeMillis();
                parseJVMOpCodes(bytecodes);
                long l2=System.currentTimeMillis();
                double duration=(double)(l2-l1)/1000;



            }

        } catch(IOException ioe) {
            AllExceptionHandler handler=new AllExceptionHandler(ioe);
            handler.setBehaviour(behaviour);
            handler.reportException();
            // Just in Case Code Returns from
            // reportException method

        }


    }


    int currentForIndex;
    LocalVariableStructure structure=null;
    boolean createdLocalVariableStructure;
    //java.lang.String //codeStatements = "";
    //private boolean createdLocalVariableStructure=false;
    HashMap returnsAtI=null;
    boolean multinewound;
    ArrayList returnsaddedAtIfElse=null;
    public  void parseJVMOpCodes(byte[] info) {

        continue_JumpOffsets=new ArrayList();
        break_JumpOffsets=new ArrayList();
        labelAssociated=new Hashtable();
        retAtIfElseEnd=new Hashtable();
        ConsoleLauncher.setCurrentMethodBeingExecuted(this.behaviour);
        createdLocalVariableStructure=false; // for -g:none option
        returnsAtI=new HashMap();
        boolean newfound=false;
        ArrayList methodTries=behaviour.getAllTriesForMethod();
        Operand tempOperand=null;
        java.lang.String tempString="";

        //codeStatements = "";
        int opValueI = Integer.MIN_VALUE;
        byte opValueB = Byte.MIN_VALUE;
        float opValueF = Float.MIN_VALUE;
        double opValueD = Double.NEGATIVE_INFINITY;
        long opValueL = Long.MIN_VALUE;
        java.lang.String errorMesg="";
        IntPrimitive constInt = null;
        FloatPrimitive constFloat = null;
        DoublePrimitive constDouble = null;
        LongPrimitive constLong = null;
        ClassInfo constCInfo = null;
        CPString constString = null;
        MethodRef mref = null;
        FieldRef fref = null;
        NameAndType ninfo = null;
        java.lang.String stringLiteral = null;

        Operand op=null;
        Operand op1 = null;
        Operand op2 = null;
        Operand op3 = null;
        Operand op4 = null;

        IFBlock ifst = null;

        Behaviour b = null;

        java.lang.String funcCall = "";

        int type = -1;

        Hashtable methodLookUp = cd.getMethodLookUp();

        LocalVariableTable table = LocalVariableTable.getInstance();
        boolean cons=false;
        if(behaviour instanceof ConstructorMember)
        {
            cons=true;
        }

        structure = table.getLocaVarStructure(behaviour.getBehaviourName().concat(behaviour.getStringifiedParameters()).concat(""+cons));
        if(structure!=null)
            behaviour.setMethodLocalVariables(structure);
        LocalVariable local=null;
        LABELS=new Hashtable();
        returnsaddedAtIfElse=new ArrayList();


        // TODO:
        /*
        * Decide when that infinite loop should be added to //codeStatements
        *
        */
        boolean invokevirtualFound=false;
multinewound=false;
        for(int i=0;i<info.length;i++) {
           // Shutdown se;
            resetEndOfSynchBlockIfNecessary(i);
            //emitStackContent(opStack,i,info);

            // checkEndOfLoops(ifHashTable,behaviour);

            boolean skip;

            currentForIndex=i;

            /*skip=skipCurrentIteraton(i,false,info);
            if(skip && Configuration.getDecompileroption().equals("dc")) {
                continue;
            }  */
            /*int instructionAtI=info[i];
            java.lang.String fromExceptionTable=pollExcepionTables(i,instructionAtI);
            //codeStatements+=fromExceptionTable+((fromExceptionTable.trim().length()==0)?"":"\n");

            java.lang.String switchStmt=pollSwitchBlksForMethod(i);
            //codeStatements+=switchStmt+((switchStmt.trim().length()==0)?"":"\n");*/

            try {


                //if(ifLevel >= 1) {

                boolean needtoresetelsehasbugun=false;
                StringBuffer ifelsecode=new StringBuffer("");
                StringBuffer reset=new StringBuffer("");
                int prevstart=getPrevStartCodePos(info,i);
                checkForIFElseEndStatement(info,ifHashTable,i,reset,opStack,ifelsecode,"if");

                /*if(prevstart >= 0)
                {
                    ifelsecode=new StringBuffer("");
                    checkForIFElseEndStatement(info,ifHashTable,prevstart,reset,opStack,ifelsecode,"else");
                } */
                if(reset.toString().equals("true"))
                {
                    //System.out.println("Reset for "+i+ behaviour);
                    elsehasbegun=false;
                }
                //codeStatements+= ifelsecode.toString();
                //}

                java.lang.String brlbl=getAnyLabelAtI(LABELS,i);
                if(brlbl!=null && brlbl.trim().length() > 0)
                {
                    //codeStatements+="\n"+brlbl+":\n";
                }
                else
                {
                    //codeStatements+="\n"+"#FORINDEX"+i+"#"+"\n";
                }




                java.lang.String printLater="";
                Iterator loopIterator = behaviour.getBehaviourLoops().iterator();
               /* while(loopIterator.hasNext())
                {
                    Loop iloop = (Loop)loopIterator.next();
                    if(iloop.getStartIndex() == i && iloop.isInfinite())
                    {
                        boolean clash=doesthisClashWithCaseBegin(behaviour.getAllSwitchBlks(),i);
                        if(clash)printLater="while(true)\n{\n";
                        if(!clash){
                            tempString = "while(true)\n{\n";
                            //codeStatements += Util.formatDecompiledStatement(tempString);
                            printLater="";
                        }
                    }
                    if(iloop.getEndIndex() == i)
                    {
                        tempString=anyWhileBodyHere(i,iloop,opStack);
                        if(tempString.trim().length() > 0)
                        {
                            tempString =tempString+"}\n";
                            iloop.setWasLoopClosedInCode(true);
                            i=i+2;
                            //codeStatements += Util.formatDecompiledStatement(tempString); // TODO: BUg in formatting
                            continue;
                        }
                        else
                        {
                           if(iloop.wasLoopClosedInCode()==false)
                           {
                                  tempString ="}\n";
                                  //codeStatements += Util.formatDecompiledStatement(tempString);
                                  iloop.setWasLoopClosedInCode(true);
                           }
                        }
                    }
                    else if(iloop.getLoopEndForBracket()==i && iloop.wasLoopClosedInCode()==false)
                    {

                       tempString ="}\n";// Loop End\n";
                       //codeStatements += Util.formatDecompiledStatement(tempString);
                        iloop.setWasLoopClosedInCode(true);
                    }


                }

                java.lang.String switchStmt=pollSwitchBlksForMethod(i);

                //codeStatements+=switchStmt;
                //codeStatements+=((switchStmt.trim().length()==0)?"":"\n");     // Moved switch before Try


                // OK in all cases?

                if(printLater.trim().length() > 0)
                {
                    //codeStatements += Util.formatDecompiledStatement(printLater);
                }



                int instructionAtI=info[i];
                java.lang.String fromExceptionTable=pollExcepionTables(i,instructionAtI);   */

                printAnyReturnATEndOfGuard(i,info);
               /* boolean end=isIEndOfGuard(i,behaviour);
                boolean returnadded=false;
                int returnposincode=-1;
                if(end)
                {
                    StringBuffer sb=new StringBuffer("");
                    java.lang.String returnString=isAnyReturnPresentInSkipRegion(info,i,behaviour,sb);
                    java.lang.String str=sb.toString();
                    try
                    {
                        returnposincode=Integer.parseInt(str);
                    }
                    catch(NumberFormatException ne){}

                    if(returnString!=null && returnString.trim().length()==0)
                    {
                        if(i==(info.length-1))
                        {
                            returnString=getReturnTypeInst(info,i);
                            returnposincode=i;

                        }
                    }
                    java.lang.Object val=null;


                    if(returnString!=null && returnString.trim().length() > 0)
                    {
                        int loadIndex=-1;
                        StringBuffer stb=new StringBuffer("");
                        if(returnposincode!=-1)
                            loadIndex=getLoadIndexForReturn(info,returnposincode,stb);
                        if(loadIndex!=-1)
                        {
                            LocalVariableStructure struck=behaviour.getLocalVariables();
                            int rangeinx=Integer.parseInt(stb.toString());
                            if(rangeinx!=-1)
                            {
                                if(cd.isClassCompiledWithMinusG())
                                {
                                    LocalVariable temp=struck.getVariabelAtIndex(loadIndex,rangeinx);
                                    if(temp!=null)
                                    {
                                        op=new Operand();
                                        op.setOperandValue(temp.getVarName());
                                        op.setOperandName(temp.getVarName());
                                        opStack.push(op);
                                    }

                                }
                                else
                                {
                                    LocalVariable temp=struck.getVariabelAtIndex(loadIndex);
                                    if(temp!=null)
                                    {
                                        op=new Operand();
                                        op.setOperandValue(temp.getVarName());
                                        op.setOperandName(temp.getVarName());
                                        opStack.push(op);
                                    }
                                }
                            }
                        }
                       int returnPos=getReturnStringPosInCode(info,i,behaviour);
                        if(returnString.equals("return")==false && opStack.size() > 0)
                        {
                            //   StackTop=opStack.peekTopOfStack();
                            Operand StackTop=opStack.getTopOfStack();
                            if(StackTop!=null)
                            {
                                val=StackTop.getOperandValue();
                                if(val!=null)val=val.toString();
                            }
                            if(StackTop!=null)
                            {



                                if(val!=null)tempString="return "+val;
                                else tempString="return ";
                                //codeStatements+=Util.formatDecompiledStatement(tempString);


                                returnadded=true;
                            }

                        }
                        else if(returnString.equals("return")==true)
                        {
                            tempString="return ";
                            //codeStatements+=Util.formatDecompiledStatement(tempString);


                            returnadded=true;
                        }
                        returnsAtI.put(new Integer(returnPos),"true");
                    }

                } */


                /*if(returnadded)//codeStatements+=";\n";
                tempString=fromExceptionTable;
                //codeStatements+=Util.formatDecompiledStatement(tempString);
                //codeStatements+=((fromExceptionTable.trim().length()==0)?"":"\n");


                java.lang.String synch=pollsynchblocks(i);
                //codeStatements+=Util.formatDecompiledStatement(synch);


                skip=skipCurrentIteraton(i,true,info);
                if(skip)continue;   */
                thisLoop=null;


               /* java.lang.String brlbl=getAnyLabelAtI(LABELS,i);
                if(brlbl!=null && brlbl.trim().length() > 0)
                {
                    //codeStatements+="\n"+brlbl+":\n";
                }
                else
                {
                    //codeStatements+="\n"+"#FORINDEX"+i+"#"+"\n";
                }   */

                switch(info[i]) {
                    case JvmOpCodes.AALOAD :
                        handleAALOAD(opStack);

                        continue;
                    case JvmOpCodes.AASTORE:

                        handleAASTORECase();

                        continue;
                    case JvmOpCodes.ACONST_NULL:
                        handleACONSTNULL();

                        continue;
                    case JvmOpCodes.ALOAD:
                        handleAloadCase(info,i,opStack);
                        i=i+1;
                        continue;

                    case JvmOpCodes.ALOAD_0:

                        handlesimpleaload(0);

                        //parsedString += "ALOAD_0\n";
                        //parsedString+="\t";//parsedString+="\t";
                        continue;
                    case JvmOpCodes.ALOAD_1:

                        handlesimpleaload(1);
                        //parsedString += "ALOAD_1\n";
                        //parsedString+="\t";//parsedString+="\t";
                        continue;
                    case JvmOpCodes.ALOAD_2:

                        handlesimpleaload(2);
                        //parsedString += "ALOAD_2\n";
                        //parsedString+="\t";//parsedString+="\t";
                        continue;
                    case JvmOpCodes.ALOAD_3:
                        handlesimpleaload(3);

                        //parsedString += "ALOAD_3\n";
                        continue;

                    case JvmOpCodes.ANEWARRAY:
                        //parsedString += "ANEWARRAY\t";
                        //int classIndex=((info[++i] << 8) | info[++i]);
                        handleANEWARRAYCase(info);
                        i++;
                        i++;
                        continue;
                    case JvmOpCodes.ARETURN:

                        boolean oktoadd=true;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" areturn\n");
                        handleARETURNCase(currentForIndex,returnsAtI);

                        continue;
                    case JvmOpCodes.ARRAYLENGTH:
                        handleARRAYLENGTHCase();

                        continue;
                    case JvmOpCodes.ASTORE:
                        handleComplexAStore(info,i);
                        i++;

                        continue;
                    case JvmOpCodes.ASTORE_0:
                       handleSimpleASTORECase(i,0);
                        //parsedString += "ASTORE_0\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.ASTORE_1:
                        //   parsedString += "ASTORE_1\n";
                        //  parsedString+="\t";parsedString+="\t";
                       handleSimpleASTORECase(i,1);
                        continue;
                    case JvmOpCodes.ASTORE_2:
                        handleSimpleASTORECase(i,2);

                        //parsedString += "ASTORE_2\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.ASTORE_3:
                        handleSimpleASTORECase(i,3);
                        //parsedString += "ASTORE_3\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.ATHROW:

                        //parsedString += "ATHROW "+"\n";
                        //parsedString+="\t";parsedString+="\t";
                        boolean add=true;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" athrow\n");
                        boolean aloadPresent=isPrevInstructionAload(i,info);


                        //Problem:
                        // Some Defect Here. If Exception thrown as the last stmt in finally in java code
                        // Then it will not show it...
                        // TODO: Keep Testing This.


                        if(aloadPresent==true)  // Is this enough to make add=false ?
                        {
                            // The following is just to confirm that this is indeed the end of finally
                            if(methodTries!=null)
                            {
                                for(int st=0;st<methodTries.size();st++)
                                {
                                    TryBlock tryblk=(TryBlock)methodTries.get(st);
                                    if(tryblk!=null)
                                    {
                                        FinallyBlock fin=tryblk.getFinallyBlock();
                                        if(fin!=null)
                                        {

                                            int finEnd=fin.getEnd();
                                            if(finEnd==i)
                                            {
                                                add=false;
                                                break;
                                            }
                                            else if(finEnd!=i)
                                            {
                                                int finStart=fin.getStart();
                                                int fromWhere=i;
                                                int tillWhere=finStart;
                                                while(fromWhere >= tillWhere)
                                                {
                                                    int inst=info[fromWhere];
                                                    if(inst==JvmOpCodes.JSR) // TODO : Check for Jsr_w
                                                    {
                                                        add=false;
                                                        break;
                                                    }
                                                    fromWhere--;
                                                }

                                            }
                                            else
                                                add=true;
                                        }
                                    }

                                }
                            }
                        }





                        if(add && addATHROWOutput(currentForIndex))
                        {
                            op = (Operand)opStack.pop();
                            opStack.push(op);
                            tempString="throw "+op.getOperandValue()+";\n";
                            //codeStatements+=Util.formatDecompiledStatement(tempString);
                        }

                        continue;

                        //LETTER B
                    case JvmOpCodes.BALOAD:

                        handleBALOAD(opStack);

                        continue;
                    case JvmOpCodes.BASTORE:
                       addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" bastore\n");
                       handleBASTORE(opStack);
                        continue;
                    case JvmOpCodes.BIPUSH:
                        handleBIPush(info);
                        i=i+1;


                        continue;

                        //LETTER C
                    case JvmOpCodes.CALOAD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" caload\n");
                        handleCALOAD(opStack);

                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.CASTORE:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" castore\n");
                        handleCASTORE(opStack);
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.CHECKCAST:

                        handleCheckCast(opStack,info);
                        i=i+2;
                        continue;

                        //LETTER D
                    case JvmOpCodes.D2F:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" d2f\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue("(float)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_FLOAT);
                        opStack.push(op);
                        // TODO : Did not push / pop the operands as we need to
                        // convert the double to float and then push it back to the stack.
                        //parsedString += "D2F\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.D2I:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" d2i\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue("(int)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        opStack.push(op);
                        // TODO : Did not push / pop the operands as we need to
                        // convert the double to int and then push it back to the stack.
                        //parsedString += "D2I\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.D2L:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" d2l\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue("(long)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        opStack.push(op);

                        continue;
                    case JvmOpCodes.DADD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dadd\n");
                        op= (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();
                        //op2.setOperandValue(new Double(((Double)op.getOperandValue()).doubleValue()+((Double)op.getOperandValue()).doubleValue()));
                        op2.setOperandValue("("+op1.getOperandValue()+")" +"+"+"("+op.getOperandValue()+")");
                        op2.setOperandType(Constants.IS_CONSTANT_DOUBLE);

                        opStack.push(op2);

                        continue;
                    case JvmOpCodes.DALOAD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" daload\n");
                        handleDALOAD(opStack);

                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.DASTORE:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dastore\n");
                        handleDASTORE(opStack);

                        continue;
                    case JvmOpCodes.DCMPG:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dcmpg\n");
                        handleDCMPG(opStack,info);

                        continue;
                    case JvmOpCodes.DCMPL:
                       addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dcmpl\n");
                       handleDCMPL(opStack,info) ;
                        continue;
                    case JvmOpCodes.DCONST_0:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dconst_0\n");
                       handleDCONST(opStack,0.0);
                        //parsedString += "DCONST_0\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.DCONST_1:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dconst_1\n");
                        handleDCONST(opStack,1.0);
                        //parsedString += "DCONST_1\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;

                    case JvmOpCodes.DDIV:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ddiv\n");
                        handleDDIV(opStack);

                        //parsedString += "DDIV\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.DLOAD:
                        opValueI = info[++i];
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dload "+opValueI);
                        handleDLOADCase(opValueI,opStack,false);




                        //parsedString += "DLOAD";
                        // parsedString += "  "+opValueI+"\n";
                        //parsedString+="\t";parsedString+="\t";
                        continue;
                    case JvmOpCodes.DLOAD_0:

                     addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dload_0");
                      handleDLOADCase(0,opStack,true);


                        continue;
                    case JvmOpCodes.DLOAD_1:

                      addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dload_1");
                       handleDLOADCase(1,opStack,true);



                        continue;
                    case JvmOpCodes.DLOAD_2:

                      addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dload_2");
                       handleDLOADCase(2,opStack,true);



                        continue;
                    case JvmOpCodes.DLOAD_3:

                      addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dload_3");
                       handleDLOADCase(3,opStack,true);


                        continue;
                    case JvmOpCodes.DMUL:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dmul\n");
                        handleDMUL(opStack);


                        continue;
                    case JvmOpCodes.DNEG:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dneg\n");
                        handleDNEG(opStack);



                        continue;
                    case JvmOpCodes.DREM:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" drem\n");
                        handleDREM(opStack);

                        continue;
                    case JvmOpCodes.DRETURN:
                        oktoadd=true;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dreturn\n");
                       Iterator mapIT=returnsAtI.entrySet().iterator();
                        while(mapIT.hasNext())
                        {
                            Map.Entry entry=(Map.Entry)mapIT.next();
                            Object key=entry.getKey();
                            Object retStatus=entry.getValue().toString();
                            if(key instanceof Integer)
                            {
                                Integer pos=(Integer)key;
                                int temp=pos.intValue();
                                if(temp==i)
                                {
                                    if(retStatus.equals("true"))
                                    {

                                        oktoadd=false;
                                        break;
                                    }
                                }
                            }

                        }


                        if(!oktoadd)
                        {
                            returnsAtI.remove(new Integer(i));
                        }

                        if(oktoadd && opStack.size() > 0){
                             //System.out.println(currentForIndex+"i"+behaviour.getBehaviourName());
                            op = (Operand)opStack.pop();
                            tempString="return "+op.getOperandValue().toString()+";\n";
                            //codeStatements+=Util.formatDecompiledStatement(tempString);
                        }

                        //behaviour.getParentBehaviour().getOpStack().push(op);
                        /*parsedString += "DRETURN";
                        parsedString+="\n";
                        parsedString+="\t";parsedString+="\t";*/
                        continue;
                    case JvmOpCodes.DSTORE:
                        int classIndex = info[++i];




                        local=getLocalVariable(classIndex,"store","double",false,currentForIndex);
                        if(cd.isClassCompiledWithMinusG() && local!=null)
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dstore "+classIndex+" THIS LOCALVARIABLE :- "+local.getVarName()+"\n");
                        else
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dstore "+classIndex);
                        if(local != null && !doNotPop) {
                              op =(Operand)opStack.pop();
                            prevLocalGenerated = local;
                            boolean push=isPrevInstDup(info,currentForIndex);
                            if(!push)
                            {
                                if(!local.isDeclarationGenerated()) {
                                    //if(local.wasCreated())local.setDataType(op.getClassType());
                                    local.setBlockIndex(blockLevel);
                                    tempString=local.getDataType()+" "+local.getVarName()+"="+op.getOperandValue()+";\n";
                                    //codeStatements += Util.formatDecompiledStatement(tempString);
                                    local.setDeclarationGenerated(true);
                                } else {
                                    tempString=local.getVarName()+"="+op.getOperandValue()+";\n";
                                    //codeStatements += Util.formatDecompiledStatement(tempString);
                                }
                            }
                            else
                            {
                                if( ((currentForIndex-1) >=0) && info[currentForIndex-1]==JvmOpCodes.DUP)
                                    opStack.getTopOfStack();
                                if( ((currentForIndex-1) >=0) && info[currentForIndex-1]==JvmOpCodes.DUP2)
                                {
                                    opStack.getTopOfStack();
                                    opStack.getTopOfStack();
                                }
                                 //codeStatements += Util.formatDecompiledStatement(local.getVarName()+"=("+op.getOperandValue()+");\n");
                                op2=createOperand(local.getVarName());
                                opStack.push(op2);
                            }
                        }

                        if(doNotPop==true)doNotPop=false;

                        continue;
                    case JvmOpCodes.DSTORE_0:

                        StringBuffer codes=new StringBuffer("") ;
                        handleSimpleDstoreCaseInst(opStack,info,0,codes);
                        //codeStatements+=codes.toString();

                        continue;
                    case JvmOpCodes.DSTORE_1:
                        codes=new StringBuffer("") ;
                        handleSimpleDstoreCaseInst(opStack,info,1,codes);
                        //codeStatements+=codes.toString();

                        continue;
                    case JvmOpCodes.DSTORE_2:
                        codes=new StringBuffer("") ;
                        handleSimpleDstoreCaseInst(opStack,info,2,codes);
                        //codeStatements+=codes.toString();
                        continue;
                    case JvmOpCodes.DSTORE_3:
                        codes=new StringBuffer("") ;
                        handleSimpleDstoreCaseInst(opStack,info,3,codes);
                        //codeStatements+=codes.toString();
                        continue;
                    case JvmOpCodes.DSUB:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dsub\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();
                        op2.setOperandType(Constants.IS_CONSTANT_DOUBLE);
                        op2.setOperandValue("("+op.getOperandValue()+"- "+op1.getOperandValue() +")");
                        //op2.setCategory(Constants.CATEGORY1);

                        opStack.push(op2);

                        continue;
                    case JvmOpCodes.DUP:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dup\n");
                        op1 = (Operand)opStack.pop();
                              opStack.push(op1);
                        opStack.push(op1);

                        continue;
                    case JvmOpCodes.DUP_X1:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dup_x1\n");
                        op1 = (Operand)opStack.pop();
                        op2 = (Operand)opStack.pop();
                        opStack.push(op1);
                        opStack.push(op2);
                        opStack.push(op1);
                        continue;
                    case JvmOpCodes.DUP_X2:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dup_x2\n");
                        op1 = (Operand)opStack.pop();
                        op2 = (Operand)opStack.pop();
                        op3 = (Operand)opStack.pop();
                        opStack.push(op1);
                        opStack.push(op3);
                        opStack.push(op2);
                        opStack.push(op1);
                        continue;
                    case JvmOpCodes.DUP2:
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dup2\n");
                        op1 = (Operand)opStack.pop();
                        op2 = (Operand)opStack.pop();


                            opStack.push(op2);
                            opStack.push(op1);
                            opStack.push(op2);
                            opStack.push(op1);




                        continue;
                    case JvmOpCodes.DUP2_X1:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dup2_x1\n");
                        op1 = (Operand)opStack.pop();
                        op2 = (Operand)opStack.pop();
                        op3 = (Operand)opStack.pop();
                            opStack.push(op2);
                            opStack.push(op1);
                            opStack.push(op3);
                            opStack.push(op2);
                            opStack.push(op1);
                        continue;
                    case JvmOpCodes.DUP2_X2:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dup2_x2\n");
                        handleDUP2X2(opStack);


                        continue;

                        // LETTER F
                    case JvmOpCodes.F2D:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" f2d\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue("(double)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_DOUBLE);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.F2I:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" f2i\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue("(int)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.F2L:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" f2l\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue("(long)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.FADD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fadd\n");
                        op= (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        op2 = new Operand();
                        op2.setOperandValue("("+op.getOperandValue()+" + "+op1.getOperandValue()+")");
                        op2.setOperandType(Constants.IS_CONSTANT_FLOAT);
                       // op2.setCategory(Constants.CATEGORY1);
                        opStack.push(op2);

                        continue;
                    case JvmOpCodes.FALOAD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" faload\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        op2 = new Operand();
                        op2.setOperandValue(op1.getOperandValue()+"["+op.getOperandValue()+"]");
                        //op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_CONSTANT_FLOAT);
                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.FASTORE:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fastore\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        op2 = (Operand)opStack.pop();

                        tempString=op2.getOperandValue()+"["+op1.getOperandValue()+"]="+op.getOperandValue();
                        if(tempString.indexOf(";")==-1)tempString+=";\n";
                        //codeStatements+=Util.formatDecompiledStatement(tempString);

                        continue;
                    case JvmOpCodes.FCMPG:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fcmpg\n");
                        handleFCMPG(opStack,info);
                        continue;
                    case JvmOpCodes.FCMPL:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fcmpl\n");
                       handleFCMPL(opStack,info);
                        continue;
                    case JvmOpCodes.FCONST_0:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fonst_0\n");
                        handleFCONST(opStack,"0.0f");

                        continue;
                    case JvmOpCodes.FCONST_1:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fonst_1\n");
                        handleFCONST(opStack,"1.0f");
                        continue;
                    case JvmOpCodes.FCONST_2:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fonst_2\n");
                        handleFCONST(opStack,"2.0f");
                        continue;
                    case JvmOpCodes.FDIV:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fdiv\n");
                        handleFDIV(opStack);


                        continue;
                    case JvmOpCodes.FLOAD:
                        opValueI = info[++i];

                        handleFLOAD(opValueI,opStack,false);





                        //opStack.push(element);
                        ;continue;
                    case JvmOpCodes.FLOAD_0:


                        handleFLOAD(0,opStack,true);



                        continue;
                    case JvmOpCodes.FLOAD_1:

                      handleFLOAD(1,opStack,true);




                        //opStack.push(element);
                        continue;
                    case JvmOpCodes.FLOAD_2:
                        handleFLOAD(2,opStack,true);




                        //opStack.push(element);
                        continue;
                    case JvmOpCodes.FLOAD_3:

                         handleFLOAD(3,opStack,true);





                        //opStack.push(element);
                        continue;
                    case JvmOpCodes.FMUL:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fmul\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();


                        op2 = new Operand();
                        op2.setOperandValue("("+op.getOperandValue()+" * "+op1.getOperandValue()+")");
                        op2.setOperandType(Constants.IS_CONSTANT_FLOAT);
                       // op2.setCategory(Constants.CATEGORY1);

                        opStack.push(op2);

                        continue;
                    case JvmOpCodes.FNEG:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fneg\n");
                        op = (Operand)opStack.pop();
                        op1 = new Operand();
                        op1.setOperandValue("-"+"("+op.getOperandValue()+")");
                        //op1.setCategory(Constants.CATEGORY1);
                        op1.setOperandType(Constants.IS_CONSTANT_FLOAT);
                        opStack.push(op1);
                        continue;
                    case JvmOpCodes.FREM:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" frem\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();
                        //op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_CONSTANT_FLOAT);
                        op2.setOperandValue("("+op.getOperandValue()+"% "+op1.getOperandValue()+")");

                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.FRETURN:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" frerturn\n");
                        oktoadd=true;
                        mapIT=returnsAtI.entrySet().iterator();
                        while(mapIT.hasNext())
                        {
                            Map.Entry entry=(Map.Entry)mapIT.next();
                            Object key=entry.getKey();
                            Object retStatus=entry.getValue().toString();
                            if(key instanceof Integer)
                            {
                                Integer pos=(Integer)key;
                                int temp=pos.intValue();
                                if(temp==i)
                                {
                                    if(retStatus.equals("true"))
                                    {

                                        oktoadd=false;
                                        break;
                                    }
                                }
                            }

                        }


                        if(!oktoadd)
                        {
                            returnsAtI.remove(new Integer(i));
                        }

                        if(oktoadd && opStack.size() > 0){
                            op = (Operand)opStack.pop();
                            tempString="return "+op.getOperandValue().toString()+";\n";
                            //codeStatements+=Util.formatDecompiledStatement(tempString);
                        }

                        //behaviour.getParentBehaviour().getOpStack().push(op);
                        continue;
                    case JvmOpCodes.FSTORE:
                        classIndex = info[++i];



                        local=getLocalVariable(classIndex,"store","float",false,currentForIndex);
                        if(cd.isClassCompiledWithMinusG() && local!=null)
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fstore "+classIndex+" THIS localVariable:-  "+local.getVarName()+"\n" );
                        }
                        else
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fstore "+classIndex+"\n");

                        if(local != null && !doNotPop) {
                             op =(Operand)opStack.pop();
                            prevLocalGenerated = local;
                            boolean push=isPrevInstDup(info,currentForIndex);
                            if(!push)
                            {
                                if(!local.isDeclarationGenerated()) {
                                    local.setBlockIndex(blockLevel);
                                    tempString=local.getDataType()+" "+local.getVarName()+"="+op.getOperandValue()+";\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    local.setDeclarationGenerated(true);
                                } else {
                                    tempString=local.getVarName()+"="+op.getOperandValue()+";\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                            }


                            else
                            {
                                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP)
                                    opStack.getTopOfStack();
                                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP2)
                                {
                                    opStack.getTopOfStack();
                                    opStack.getTopOfStack();
                                }
                                //codeStatements +=Util.formatDecompiledStatement(local.getVarName()+"=("+op.getOperandValue()+");\n");
                                Operand op5=createOperand(local.getVarName());
                                opStack.push(op5);
                            }
                        }
                        if(doNotPop==true)doNotPop=false;
                        continue;
                    case JvmOpCodes.FSTORE_0:

                        StringBuffer srb=new StringBuffer("");
                        handleSimpleFstoreCaseInst(opStack,info,0,srb);
                        //codeStatements+=srb.toString();

                        continue;
                    case JvmOpCodes.FSTORE_1:
                       srb=new StringBuffer("");
                        handleSimpleFstoreCaseInst(opStack,info,1,srb);
                        //codeStatements+=srb.toString();

                        continue;

                    case JvmOpCodes.FSTORE_2:
                       srb=new StringBuffer("");
                        handleSimpleFstoreCaseInst(opStack,info,2,srb);
                        //codeStatements+=srb.toString();

                        continue;
                    case JvmOpCodes.FSTORE_3:
                        srb=new StringBuffer("");
                        handleSimpleFstoreCaseInst(opStack,info,3,srb);
                        //codeStatements+=srb.toString();
                        continue;
                    case JvmOpCodes.FSUB:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fsub\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();
                        op2.setOperandType(Constants.IS_CONSTANT_FLOAT);
                        op2.setOperandValue("("+op.getOperandValue()+") - ("+op1.getOperandValue()+")");
                        //op2.setCategory(Constants.CATEGORY1);

                        opStack.push(op2);
                        continue;

                        // LETTER G
                    case JvmOpCodes.GETFIELD: // TODO: check whether this needs to push classtype intp stack
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" getField");
                        op = (Operand)opStack.pop();
                        //temp1=info[++i];
                        //temp2=info[++i];
                        classIndex=getOffset(info,i);//(temp1 << 8) | temp2);
                        i+=2;
                        //if(classIndex < 0)classIndex=(temp1+1)*256-Math.abs(temp2);
                        fref = cd.getFieldRefAtCPoolPosition(classIndex);

                        //NameAndType ninfo=cd.getNameAndTypeAtCPoolPosition(classIndex);
                        op2 = new Operand();
                        //(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_ARRAY_REF);
                        op2.setOperandValue(op.getOperandValue()+"."+fref.getFieldName());
                        opStack.push(op2);
                        Util.parseReturnType(fref.getTypeoffield());
                        ArrayList returntype=Util.getreturnSignatureAsList();
                        if(returntype.size() > 0)
                        {
                            addParsedOutput("Field :- "+fref.getFieldName()+" TYPE:-  "+(java.lang.String)returntype.get(0)+"\n");
                            op2.setClassType((java.lang.String)returntype.get(0));
                        }


                        continue;
                    case JvmOpCodes.GETSTATIC:

                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" getStatic");
                        classIndex=getOffset(info,i);
                        i+=2;
                        fref = cd.getFieldRefAtCPoolPosition(classIndex);
                                        op = new Operand();
                        //(Constants.CATEGORY1);
                        if(opStack.size() > 0 && isThisInstrStart(behaviour.getInstructionStartPositions(),(currentForIndex-1)) && (info[currentForIndex-1]==JvmOpCodes.POP || info[currentForIndex-1]==JvmOpCodes.POP2 && opStack.size() > 0))
                        {
                          java.lang.String opv=opStack.getTopOfStack().getOperandValue();
                          if(opv!=null)
                           op.setOperandValue(opv.replace('/','.')+"."+fref.getFieldName());
                          else
                           op.setOperandValue(fref.getClassname().replace('/','.')+"."+fref.getFieldName());
                        }
                        else
                            op.setOperandValue(fref.getClassname().replace('/','.')+"."+fref.getFieldName());
                          Util.parseReturnType(fref.getTypeoffield());
                        returntype=Util.getreturnSignatureAsList();

                          if(returntype.size() > 0)
                          {
                               java.lang.Object tempv=returntype.get(0);
                               addParsedOutput("Field :- "+fref.getFieldName()+" TYPE:-  "+tempv+"\n");
                               op.setClassType((java.lang.String)tempv);
                          }
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.GOTO:  // Mistake here. GOTO of switch not taken into consideration
                        // Also goto can occur at the end of try block also or a catch block
                        int instructionPos=i;

                        int b1=info[++i];      // TODO: Replace by jumpaddress
                        int b2=info[++i];
                        int z;
                        if(b1 < 0)b1=(256+b1);
                        if(b2 < 0)b2=(256+b2);

                        int indexInst = ((((b1 << 8) | b2)) + (i - 2));
                        if(indexInst > 65535)
                            indexInst=indexInst-65536;
                        if(indexInst < 0)indexInst=256+indexInst;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" goto "+indexInst+"\n");
                        if(isIfInScope) {
                            Iterator iterIfHash = ifHashTable.keySet().iterator();
                            while(iterIfHash.hasNext()) {
                                Object key = iterIfHash.next();
                                IFBlock ifs = (IFBlock)ifHashTable.get(key);
                                boolean prevGotoPresent=false;
                                int if_start=ifs.getIfStart();
                                boolean loop_start=isThisLoopStart(ifs,behaviour.getBehaviourLoops() ,info);
                                int thisifclose=ifs.getIfCloseLineNumber();
                                boolean donotgeneratelese=false;
                                if(isThisInstrStart(behaviour.getInstructionStartPositions(),(thisifclose-3)) && isInstructionIF(info[thisifclose-3]))
                                {
                                   donotgeneratelese=true;
                                }
                                if((ifs.getIfCloseLineNumber() - (i-2)) == 0 && !loop_start && !donotgeneratelese) {                // Removed Math.abs : belurs
                                    elseCloseLineNo = indexInst;
                                   // System.out.println("elseCloseLineNo "+elseCloseLineNo);
                                    java.lang.String checkAgain= "";
                                    // if(elseCloseLineNo > i)
                                    // {
                                    this.getCurrentIFStructues();
                                    Object ifsSorted[]=sortIFStructures();
                                    IFBlock parent=getParentBlock(ifsSorted,ifs.getIfStart());
                                    ArrayList loopList=behaviour.getBehaviourLoops();
                                    int loopSize=loopList.size();
                                    java.lang.String checkSwitch="";
                                    StringBuffer again=new StringBuffer("");
                                    if(parent==null)
                                    {
                                        ifs.setHasElse(true);
                                        if(loopSize > 0)    {
                                            ifs.setElseCloseLineNumber(elseCloseLineNo);
                                            checkAgain="true";
                                        }
                                        else
                                            ifs.setElseCloseLineNumber(elseCloseLineNo);

                                        //ifs.setHasMatchingElseBeenGenerated(true);
                                        if(!checkAgain.equalsIgnoreCase("true") && behaviour.getAllSwitchBlks()!=null && behaviour.getAllSwitchBlks().size() > 0)checkSwitch="true";
                                        elseCloseLineNo=ifs.getElseCloseLineNumber();
                                    }

                                    else
                                    {
                                        ifs.setHasElse(true);
                                        ifs.setElseCloseLineNumber(elseCloseLineNo); // Check This logic
                                        //ifs.setHasMatchingElseBeenGenerated(true);

                                        int tmpend=checkElseCloseLineNumber(ifsSorted,parent,ifs,ifs.getIfStart(),elseCloseLineNo,again);
                                        if(tmpend!=-1 && !again.toString().equals("true"))
                                        {
                                            ifs.setElseCloseLineNumber(tmpend);
                                            elseCloseLineNo=ifs.getElseCloseLineNumber();
                                        }

                                    }
                                    if(checkAgain.equals("true") || again.toString().equals("true") )
                                    {
                                        elseCloseLineNo=ifs.getElseCloseLineNumber();              //
                                        elseCloseLineNo=resetElseCloseNumber(loopList,ifs,currentForIndex);
                                        ifs.setElseCloseLineNumber(elseCloseLineNo);

                                    }
                                    ArrayList switches=behaviour.getAllSwitchBlks();

                                    int newelseend=-1;
                                    if(switches!=null && switches.size() > 0)
                                    {
                                        newelseend=resetEndofIFElseWRTSwitch(switches,ifs,ifs.getElseCloseLineNumber(),currentForIndex,"else");
                                        boolean valid=isNewEndValid(newelseend,ifs,"else",ifs.getElseCloseLineNumber());
                                        if(valid)
                                        {
                                            ifs.setElseCloseLineNumber(newelseend);
                                            elseCloseLineNo=ifs.getElseCloseLineNumber();
                                        }
                                    }

                                    //
                                    if(continue_JumpOffsets.size() > 0)
                                    {
                                        elseCloseLineNo=resetElseCloseNumber(currentForIndex,elseCloseLineNo);
                                        ifs.setElseCloseLineNumber(elseCloseLineNo);
                                    }


                                    if(elseCloseLineNo < (currentForIndex+3) && behaviour.getAllSwitchBlks()!=null && behaviour.getAllSwitchBlks().size() > 0)
                                    {
                                        Case caseblk=null;
                                        caseblk=isIFInCase(behaviour,currentForIndex,ifs);
                                        if(caseblk!=null)
                                        {
                                            elseCloseLineNo=getElseEndwrtcaseblk(caseblk,info,currentForIndex+3);
                                            ifs.setElseCloseLineNumber(elseCloseLineNo);
                                        }



                                    }
                                    // }
                                    /*   else if(elseCloseLineNo < i)        // TODO: DOUBLE CHECK IF THIS IS REQD!!!
                                    {
                                    elseCloseLineNo = findElseCloseLineNumber(i,elseCloseLineNo,info);
                                    }*/
                                    // TOFIX Problem of generating an else when it is not reqd
                                    boolean loopEndalso=isThisLoopEndAlso(behaviour.getBehaviourLoops(),currentForIndex,ifs.getIfStart());
                                    if(loopEndalso)ifs.setElseCloseLineNumber(-1);
                                    ArrayList gotos=cd.getGotoStarts();
                                    ArrayList gotoj=cd.getGotojumps();
                                    boolean skipElse=skipGeneratingElse(gotos,gotoj,currentForIndex,ifs);
                                    if(skipElse)ifs.setElseCloseLineNumber(-1);
                                    if(!loopEndalso && !skipElse)
                                    {
                                        if((elseCloseLineNo > ifs.getIfCloseLineNumber())==false)
                                        {

                                            elseCloseLineNo=getElseCloseFromInRangeIfStructures(ifs,currentForIndex);
                                            if(elseCloseLineNo!=-1)
                                            {
                                                ifs.setElseCloseLineNumber(elseCloseLineNo);
                                            }
                                        }
                                        if(elseCloseLineNo==-1)
                                        {
                                            elseCloseLineNo=checkElseCloseWRTAnyParentLoop(ifs,currentForIndex,info);
                                        }
                                        boolean addelsestart=addElseStart(currentForIndex);
                                        if(addelsestart && elseCloseLineNo!=-1 && elseCloseLineNo!=ifs.getIfCloseLineNumber() && elseCloseLineNo > ifs.getIfCloseLineNumber())           // changed by belurs
                                        {
                                            ifs.setHasMatchingElseBeenGenerated(true);
                                            ifs.setElseCloseLineNumber(elseCloseLineNo);
                                            java.lang.String s="";
                                            int x=getReqdGoto(currentForIndex,info,ifs.getElseCloseLineNumber());
                                            StringBuffer sb=new StringBuffer("");
                                            if(x!=-1)
                                                s=getBranchType(currentForIndex,x,info,behaviour.getBehaviourLoops(),sb)  ;
                                            branchLabels.put(new BranchLabel(ifst,s,sb.toString()),new Integer(ifs.getElseCloseLineNumber() ));
                                            //codeStatements += "\n";
                                            tempString="else\n{\n";
                                            elsestartadded.add(new Integer(currentForIndex));
                                            //codeStatements+=Util.formatDecompiledStatement(tempString);
                                            elsehasbegun=true;
                                            //System.out.println("else has begun for else at "+currentForIndex);
                                        }
                                        else        // TODO : Recheck this else blk
                                        {
                                            if(elseCloseLineNo > ifs.getIfCloseLineNumber()) {
                                                //ifs.setHasMatchingElseBeenGenerated(true);
                                                ifs.setElseCloseLineNumber(elseCloseLineNo);
                                            }
                                        }
                                    }
                                    // ifs.setIfCloseLineNumber(-1);

                                    ifs.setIfHasBeenClosed(true);
                                    blockLevel++;
                                }

                                ifHashTable.put(key,ifs);
                            }
                        }
                        if(isWhileInScope)  // TODO: Check if this can be removed
                        {
                            if(whileIndex == 1)
                                isWhileInScope = false;
                            whileIndex--;
                            // Changed by belurs ..This line was producing an extra bracket in output
                            //tempString="\n}\n";
                            ////codeStatements += Util.formatDecompiledStatement(tempString);    // Definitly source of bug
                        }


                        continue;
                    case JvmOpCodes.GOTO_W: // TODO  Need to do Test vigorously and find a goto_w in any program


                        classIndex=((info[++i] << 24)| (info[++i] << 16) | (info[++i] << 8) | info[++i]);
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" goto_w"+classIndex+"\n");
                        //parsedString+=classIndex+"\n";
                        //parsedString+="\t";parsedString+="\t";continue;

                        // LETTER I
                    case JvmOpCodes.I2B:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" i2b\n");

                        op =(Operand)opStack.pop();
                        op.setOperandValue("(byte)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_BYTE);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.I2C:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" i2c\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue("(char)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_CHARACTER);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.I2D:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" i2d\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue(op.getOperandValue());
                        op.setOperandType(Constants.IS_CONSTANT_DOUBLE);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.I2F:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" i2f\n");
                        op =(Operand)opStack.pop();
                        op.setOperandValue(op.getOperandValue());
                        op.setOperandType(Constants.IS_CONSTANT_FLOAT);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.I2L:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" i2l\n");

                        op =(Operand)opStack.pop();
                        op.setOperandValue(op.getOperandValue());
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.I2S:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" i2s\n");

                        op =(Operand)opStack.pop();
                        op.setOperandValue("(short)"+"("+op.getOperandValue()+")");
                        op.setOperandType(Constants.IS_CONSTANT_SHORT);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.IADD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iadd\n");
                        op= (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        op2 = new Operand();
                        op2.setOperandValue("("+op.getOperandValue()+" + "+op1.getOperandValue()+")");
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        //op2.setCategory(Constants.CATEGORY1);
                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.IALOAD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iaload\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        op2 = new Operand();
                        op2.setOperandValue(op1.getOperandValue()+"["+op.getOperandValue()+"]");
                        op2.setCategory(Constants.IS_CONSTANT_INT);
                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.IAND:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iand\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        op2 = new Operand();
                        op2.setOperandValue("("+op1.getOperandValue()+" & "+op.getOperandValue()+")");
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        op2.setCategory(Constants.CATEGORY1);
                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.IASTORE:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iastore\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        op2 = (Operand)opStack.pop();

                        tempString=op2.getOperandValue()+"["+op1.getOperandValue()+"]="+op.getOperandValue()+";\n";
                        //codeStatements+=Util.formatDecompiledStatement(tempString);


                        continue;
                    case JvmOpCodes.ICONST_0:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iconst_0\n");
                        op = new Operand();
                        op.setOperandValue(new Integer(0));
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.ICONST_1:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iconst_1\n");
                        op = new Operand();
                        op.setOperandValue(new Integer(1));
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.ICONST_2:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iconst_2\n");
                        op = new Operand();
                        op.setOperandValue(new Integer(2));
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;

                    case JvmOpCodes.ICONST_3:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iconst_3\n");
                        op = new Operand();
                        op.setOperandValue(new Integer(3));
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.ICONST_4:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iconst_4\n");
                        op = new Operand();
                        op.setOperandValue(new Integer(4));
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.ICONST_5:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iconst_5\n");
                        op = new Operand();
                        op.setOperandValue(new Integer(5));
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.ICONST_M1:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iconst_M1\n");
                        op = new Operand();
                        op.setOperandValue(new Integer(-1));
                        op.setOperandType(Constants.IS_CONSTANT_INT);
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.IDIV:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" idiv\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        op2 = new Operand();
                        op2.setOperandValue("("+op1.getOperandValue()+")"+"/"+"("+op.getOperandValue()+")");
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        op2.setCategory(Constants.CATEGORY1);
                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.IF_ACMPEQ:

                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        /*classIndex = ((info[++i] << 8) | info[++i]);
                        classIndex += i - 2;*/
                        classIndex=getJumpAddress(info,i);
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" if_acmpeq "+classIndex+"\n");
                        i++;
                        i++;

                        blockLevel++;
                        ArrayList list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                        boolean isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        int loopstart=-1;
                        boolean correctIf=false;
                        boolean  beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }

                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//(info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);*/
                                        //ifst.setIfCloseLineNumber(classIndex-3);
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        /*ifHashTable.put(""+(ifLevel),ifst);
                                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);*/
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            tempString= "\nif("+op1.getOperandValue()+"=="+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            //ifst.setIfHasBeenClosed(true);
                                            print=false;
                                        }
                                        isIfInScope = true;
                                        boolean c;
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" == "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" != "+op.getOperandValue());
                                        if(c)
                                        {
                                        tempString= "\nif(!"+op1.getOperandValue()+"=="+op.getOperandValue()+"))\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" == "+op.getOperandValue(),true,"while",last,op1.getOperandValue()+" != "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nwhile(!"+op1.getOperandValue()+"=="+op.getOperandValue()+"))\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            //int x = 0;
                            else
                            {

                                /*  ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                //ifst.setIfCloseLineNumber(classIndex-3);*/

                                ifst.setElseCloseLineNumber(gotoIndex);
                                /*ifHashTable.put(""+(ifLevel),ifst);
                                isIfInScope = true;*/

                                // addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                isIfInScope = true;
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    tempString= "\nif("+op1.getOperandValue()+"=="+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;

                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" == "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" != "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nif(!"+op1.getOperandValue()+"=="+op.getOperandValue()+"))\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }

                            }
                        }
                        else
                        {
                            /*  ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            isIfInScope = true;
                            ifHashTable.put(""+(ifLevel),ifst);

                            addBranchLabel(classIndex,i,ifst,currentForIndex,info);   */
                            isIfInScope = true;
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                             boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                tempString= "\nif("+op1.getOperandValue()+"=="+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                               // ifst.setIfHasBeenClosed(true);

                            }
                             boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" == "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" != "+op.getOperandValue());
                            if(c)
                            {
                                tempString= "\nif(!"+op1.getOperandValue()+"=="+op.getOperandValue()+"))\n{\n";
                                //codeStatements += Util.formatDecompiledStatement(tempString);
                            }
                        }

                        continue;
                    case JvmOpCodes.IF_ACMPNE:
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" if_acmpne "+classIndex+"\n");
                        i++;
                        i++;
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);

                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        loopstart=-1;
                        correctIf=false;
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        //
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }
                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)

                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//(info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);
                                        // ifst.setIfCloseLineNumber(classIndex-3);*/
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        /*ifHashTable.put(""+(ifLevel),ifst);

                                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);*/
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        isIfInScope = true;
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            tempString= "\nif(!"+op1.getOperandValue()+"=="+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                           print=false;
                                        }
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" != "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" == "+op.getOperandValue());
                                        if(c)
                                        {
                                        tempString= "\nif("+op1.getOperandValue()+"=="+op.getOperandValue()+"))\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                       boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" != "+op.getOperandValue(),true,"while",last,op1.getOperandValue()+" == "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nwhile("+op1.getOperandValue()+"=="+op.getOperandValue()+"))\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            //int x = 0;
                            else
                            {

                                /*ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                //ifst.setIfCloseLineNumber(classIndex-3);
                                isIfInScope=true;                      */
                                ifst.setElseCloseLineNumber(gotoIndex);
                                /*ifHashTable.put(""+(ifLevel),ifst);
                                addBranchLabel(classIndex,i,ifst,currentForIndex,info);*/
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                isIfInScope = true;
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    tempString= "\nif(!"+op1.getOperandValue()+"=="+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;
                                   // ifst.setIfHasBeenClosed(true);
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" != "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" == "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nif("+op1.getOperandValue()+"==("+op.getOperandValue()+"))\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                ifhasbegun=true;

                            }
                        }
                        else
                        {
                            /*ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);
                            isIfInScope=true;
                            addBranchLabel(classIndex,i,ifst,currentForIndex,info);*/
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            isIfInScope = true;
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                tempString= "\nif(!"+op1.getOperandValue()+"=="+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                                //ifst.setIfHasBeenClosed(true);
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" != "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" == "+op.getOperandValue());
                            if(c)
                            {
                            tempString= "\nif("+op1.getOperandValue()+"=="+op.getOperandValue()+"))\n{\n";
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IF_ICMPEQ:
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();



                        /* classIndex = ((i4 << 8) | i5);
                        if(classIndex < 0)classIndex=(i4+1)*256-Math.abs(i5);*/
                        classIndex=getJumpAddress(info,i);
                        i++;
                        i++;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" if_icmpeq "+classIndex+"\n");


                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        isIfInScope=true;
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);

                        //isEndOfLoop=isIndexEndOfLoop(list,(classIndex-3));
                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        loopstart=-1;
                        correctIf=false;
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }

                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)

                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//(info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*   ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);*/
                                        //ifst.setIfCloseLineNumber(classIndex-3);
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        //ifHashTable.put(""+(ifLevel),ifst);
                                        // isIfInScope=true;
                                        //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        isIfInScope=true;
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            tempString= "\nif("+op1.getOperandValue()+" == "+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            print=false;
                                           // ifst.setIfHasBeenClosed(true);
                                        }
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" == "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" != "+op.getOperandValue());
                                        if(c)
                                        {
                                        tempString= "\nif("+op1.getOperandValue()+" != "+op.getOperandValue()+")\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" == "+op.getOperandValue(),true,"while",last,op1.getOperandValue()+" != "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nwhile("+op1.getOperandValue()+"!= "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            // int x = 0;
                            else
                            {

                                /*  ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                // ifst.setIfCloseLineNumber(classIndex-3);*/
                                ifst.setElseCloseLineNumber(gotoIndex);
                                //  ifHashTable.put(""+(ifLevel),ifst);
                                isIfInScope=true;
                                // addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    tempString= "\nif("+op1.getOperandValue()+" == "+op.getOperandValue()+")\n{\n" +
                                            "break;\n" +
                                            "}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;
                                   // ifst.setIfHasBeenClosed(true);
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" == "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" != "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nif("+op1.getOperandValue()+" != "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                 ifhasbegun=true;

                            }
                        }
                        else
                        {
                            /* ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);
                            isIfInScope=true;
                            addBranchLabel(classIndex,i,ifst,currentForIndex,info);*/
                            isIfInScope=true;
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                             boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                tempString= "\nif("+op1.getOperandValue()+" == "+op.getOperandValue()+")\n{\n" +
                                        "break;\n" +
                                        "}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                                //ifst.setIfHasBeenClosed(true);
                            }
                             boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" == "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" != "+op.getOperandValue());
                            if(c)
                            {
                            tempString= "\nif("+op1.getOperandValue()+" != "+op.getOperandValue()+")\n{\n";
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                            ifhasbegun=true;
                        }

                        continue;

                    case JvmOpCodes.IF_ICMPNE:
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                        i++;
                        i++;
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" if_icmpne "+classIndex+"\n");
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                        isEndOfLoop=isIndexEndOfLoop(list,(ifst.getIfCloseLineNumber()));
                        loopstart=-1;
                        correctIf=false;
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }


                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < classIndex)
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /* ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);*/
                                        //ifst.setIfCloseLineNumber(classIndex-3);
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        //ifHashTable.put(""+(ifLevel),ifst);

                                        isIfInScope=true;
                                        //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                         boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            tempString= "\nif("+op1.getOperandValue()+" != "+op.getOperandValue()+")\n{\n" +
                                                    "break;\n" +
                                                    "}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            print=false;
                                            //ifst.setIfHasBeenClosed(true);
                                        }
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" != "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" == "+op.getOperandValue());
                                        if(c)
                                        {
                                        tempString= "\nif("+op1.getOperandValue()+" == "+op.getOperandValue()+")\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                               boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" != "+op.getOperandValue(),true,"while",last,op1.getOperandValue()+" == "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nwhile("+op1.getOperandValue()+" == "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            //int x = 0;
                            else
                            {

                                /*  ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);   */
                                //ifst.setIfCloseLineNumber(classIndex-3);
                                ifst.setElseCloseLineNumber(gotoIndex);
                                /*  ifHashTable.put(""+(ifLevel),ifst);
                                isIfInScope=true;
addBranchLabel(classIndex,i,ifst,currentForIndex,info);*/
                                isIfInScope=true;
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    tempString= "\nif("+op1.getOperandValue()+" != "+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;
                                    //ifst.setIfHasBeenClosed(true);
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" != "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" == "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nif("+op1.getOperandValue()+" == "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                ifhasbegun=true;

                            }
                        }
                        else
                        {
                            /*  ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);
                            isIfInScope=true;
                            addBranchLabel(classIndex,i,ifst,currentForIndex,info);*/
                            isIfInScope=true;
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() &&!encounteredOrComp)
                            {
                                tempString= "\nif("+op1.getOperandValue()+" != "+op.getOperandValue()+")\n{\n" +
                                        "break;\n" +
                                        "}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                                //ifst.setIfHasBeenClosed(true);
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" != "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" == "+op.getOperandValue());
                            if(c)
                            {
                            tempString= "\nif("+op1.getOperandValue()+" == "+op.getOperandValue()+")\n{\n";
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                            ifhasbegun=true;
                        }

                        continue;

                    case JvmOpCodes.IF_ICMPLT:

                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                        i+=2;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" if_icmplt "+classIndex+"\n");
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);

                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        loopstart=-1;
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                {
                                    correctIf=true;
                                }
                            }
                        }
                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex <= (t+3))

                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex  && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*   ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);
                                        // ifst.setIfCloseLineNumber(classIndex-3);*/
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        //ifHashTable.put(""+(ifLevel),ifst);
                                        isIfInScope=true;
                                        //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                                     boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            tempString= "\nif("+op1.getOperandValue()+" < "+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            print=false;
                                           // ifst.setIfHasBeenClosed(true);
                                        }
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                         boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" < "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" >= "+op.getOperandValue());
                                        if(c)
                                        {
                                        tempString= "\nif("+op1.getOperandValue()+" >= "+op.getOperandValue()+")\n{";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                 boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" < "+op.getOperandValue(),true,"while",last,op1.getOperandValue()+" >= "+op.getOperandValue());
                                if(c){
                                tempString= "\nwhile("+op1.getOperandValue()+" >= "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }

                            else
                            {

                                /*ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                // ifst.setIfCloseLineNumber(classIndex-3);*/
                                ifst.setElseCloseLineNumber(gotoIndex);
                                //ifHashTable.put(""+(ifLevel),ifst);
                                isIfInScope=true;
                                //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    tempString= "\nif("+op1.getOperandValue()+" < "+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;
                                  //  ifst.setIfHasBeenClosed(true);
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                 boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" < "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" >= "+op.getOperandValue());
                                if(c){
                                tempString= "\nif("+op1.getOperandValue()+" >= "+op.getOperandValue()+")\n{";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                ifhasbegun=true;

                            }

                        }
                        else
                        {
                            /*ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);*/
                            isIfInScope=true;
                            //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                tempString= "\nif("+op1.getOperandValue()+" < "+op.getOperandValue()+")\n{\n" +
                                        "break;\n" +
                                        "}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                               // ifst.setIfHasBeenClosed(true);
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" < "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" >= "+op.getOperandValue());
                            if(c)
                            {
                            tempString= "\nif("+op1.getOperandValue()+" >= "+op.getOperandValue()+")\n{";
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IF_ICMPGE:
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);i+=2;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" if_icmpge "+classIndex+"\n");
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        //
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        //  ifst.setIfCloseLineNumber(classIndex-3);

                        ifHashTable.put(""+(ifLevel),ifst);

                            addBranchLabel(classIndex,i,ifst,currentForIndex,info);

                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        correctIf=false;
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        loopstart=-1;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }
                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex =getJumpAddress(info,t);// ((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))

                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*              ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);
                                        //  ifst.setIfCloseLineNumber(classIndex-3);*/
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        //ifHashTable.put(""+(ifLevel),ifst);
                                        isIfInScope=true;
//addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                       // System.out.println("First param "+getJumpAddress(info,currentForIndex));
                                        //System.out.println(currentForIndex+" "+behaviour.getBehaviourName());

                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            tempString= "\nif("+op1.getOperandValue()+" >= "+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                          print=false;

                                        }
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                         boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" >= "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" < "+op.getOperandValue());
                                        if(c)
                                        {
                                        tempString= "\nif("+op1.getOperandValue()+" < "+op.getOperandValue()+")\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                 boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" >= "+op.getOperandValue(),true,"while",last,op1.getOperandValue()+" < "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nwhile("+op1.getOperandValue()+" < "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            //int x = 0;   Added by belurs: NO else was here
                            else
                            {
                                /*                              ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                // ifst.setIfCloseLineNumber(classIndex-3);*/
                                ifst.setElseCloseLineNumber(gotoIndex);
                                //ifHashTable.put(""+(ifLevel),ifst);
                                isIfInScope=true;
                                //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    tempString= "\nif("+op1.getOperandValue()+" >= "+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                 print=false;
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                 boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" >= "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" < "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nif("+op1.getOperandValue()+" <"+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                ifhasbegun=true;
                            }
                        }
                        else
                        {
                            /*   ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);*/
                            isIfInScope=true;
                            //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                tempString= "\nif("+op1.getOperandValue()+" >= "+op.getOperandValue()+")\n{\n" +
                                        "break;\n" +
                                        "}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                               // ifst.setIfHasBeenClosed(true);
                                print=false;
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                             boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" >= "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" <"+op.getOperandValue());
                            if(c)
                            {
                            tempString= "\nif("+op1.getOperandValue()+" <"+op.getOperandValue()+")\n{\n";
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IF_ICMPGT:
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i); i+=2;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" if_icmpgt "+classIndex+"\n");
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);

                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        correctIf=false;
                        loopstart=-1;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }
                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))

                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*  ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);
                                        //fst.setIfCloseLineNumber(classIndex-3);*/
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        isIfInScope=true;
                                        //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            tempString= "\nif("+op1.getOperandValue()+" > "+op.getOperandValue()+")\n{\n" +
                                                    "break;\n" +
                                                    "}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                           //ifst.setIfHasBeenClosed(true);
                                            print=false;
                                        }
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                         boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" > "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" <= "+op.getOperandValue());
                                        if(c)
                                        {
                                        tempString= "\nif("+op1.getOperandValue()+" <= "+op.getOperandValue()+")\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                 boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" > "+op.getOperandValue(),true,"while",last,op1.getOperandValue()+" <= "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nwhile("+op1.getOperandValue()+" <= "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            //int x = 0;
                            // Added by belurs: There was no else here
                            else
                            {

                                /*  ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                // ifst.setIfCloseLineNumber(classIndex-3);        */
                                ifst.setElseCloseLineNumber(gotoIndex);
                                //  ifHashTable.put(""+(ifLevel),ifst);
                                isIfInScope=true;
                                //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    tempString= "\nif("+op1.getOperandValue()+" > "+op.getOperandValue()+")\n{\n" +
                                            "break;\n" +
                                            "}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                   // ifst.setIfHasBeenClosed(true);
                                    print=false;
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                 boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" > "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" <= "+op.getOperandValue());
                                if(c){
                                tempString= "\nif("+op1.getOperandValue()+" <="+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                ifhasbegun=true;

                            }
                        }
                        else
                        {
                            /*  ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);     */
                            isIfInScope=true;
                            //  addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                tempString= "\nif("+op1.getOperandValue()+" > "+op.getOperandValue()+")\n{\n" +
                                        "break;\n" +
                                        "}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                               // ifst.setIfHasBeenClosed(true);
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                             boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" > "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" <= "+op.getOperandValue());
                            if(c)
                            {
                            tempString= "\nif("+op1.getOperandValue()+" <= "+op.getOperandValue()+")\n{\n";
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IF_ICMPLE:
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                        i+=2;
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" if_icmple "+classIndex+"\n");
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();

                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        //ifst.setIfCloseLineNumber(classIndex-3); // changed by belurs

                        ifHashTable.put(""+(ifLevel),ifst);

                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        loopstart=-1;
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }
                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /* ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);
                                        //ifst.setIfCloseLineNumber(classIndex-3); // changed by belurs*/
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        //ifHashTable.put(""+(ifLevel),ifst);
                                        isIfInScope=true;
                                        //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            tempString= "\nif("+op1.getOperandValue()+" <= "+op.getOperandValue()+")\n{\n" +
                                                    "break;\n" +
                                                    "}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            print=false;
                                          //  ifst.setIfHasBeenClosed(true);
                                        }
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                         boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" <= "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" > "+op.getOperandValue());
                                        if(c)
                                        {
                                        tempString= "\nif("+op1.getOperandValue()+" > "+op.getOperandValue()+")\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                 boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" <= "+op.getOperandValue(),true,"while",last,op1.getOperandValue()+" > "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nwhile("+op1.getOperandValue()+" > "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            //int x = 0;
                            else
                            {

                                /*ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                //   ifst.setIfCloseLineNumber(classIndex-3);  */
                                ifst.setElseCloseLineNumber(gotoIndex);
                                //  ifHashTable.put(""+(ifLevel),ifst);
                                isIfInScope=true;
                                //    addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    tempString= "\nif("+op1.getOperandValue()+" <= "+op.getOperandValue()+")\n{\n" +
                                            "break;\n" +
                                            "}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;
                                  //  ifst.setIfHasBeenClosed(true);
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                 boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" <= "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" > "+op.getOperandValue());
                                if(c)
                                {
                                tempString= "\nif("+op1.getOperandValue()+" > "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                ifhasbegun=true;

                            }
                        }
                        else
                        {
                            /*    ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);

                            ifHashTable.put(""+(ifLevel),ifst);    */
                            isIfInScope = true;

                            // addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                tempString= "\nif("+op1.getOperandValue()+" <= "+op.getOperandValue()+")\n{\n" +
                                        "break;\n" +
                                        "}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                              //  ifst.setIfHasBeenClosed(true);
                            }
                           boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,op1.getOperandValue()+" <= "+op.getOperandValue(),print,"if",last,op1.getOperandValue()+" > "+op.getOperandValue());
                            if(c)
                            {
                                tempString= "\nif("+op1.getOperandValue()+" > "+op.getOperandValue()+")\n{\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IFEQ:
                        java.lang.String previnstret=getReturnTypeIfPreviousInvoke(currentForIndex,info);

                        op = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ifeq "+classIndex+"\n");
                        i+=2;
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        //ifst.setIfCloseLineNumber(classIndex-3);

                        ifHashTable.put(""+(ifLevel),ifst);


                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }

                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);
                                        //ifst.setIfCloseLineNumber(classIndex-3);    */
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        //  ifHashTable.put(""+(ifLevel),ifst);

                                        isIfInScope=true;
                                        //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            //info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF &&
                                            if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && (info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                                tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\nbreak;\n}\n";
                                            else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                            {
                                                tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\nbreak;\n}\n";
                                            }
                                            else
                                                tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                          print=false;
                                        }
                                        java.lang.String tempstr="";
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) &&  info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                        {
                                            tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\n";
                                            tempstr=op.getOperandValue()+"=="+previnstret;
                                        }
                                        else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                        {
                                           tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\n";
                                           tempstr=op.getOperandValue()+"=="+previnstret;
                                        }
                                        else
                                        {
                                            tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                            tempstr=op.getOperandValue();
                                        }

                                        boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"!="+previnstret);
                                        if(c)
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                java.lang.String tempstr="";
                                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                {
                                    tempString= "\nwhile("+op.getOperandValue()+"!="+previnstret+")\n{\n";
                                    tempstr=op.getOperandValue()+"=="+previnstret;
                                }
                                else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                {
                                  tempString= "\nwhile("+op.getOperandValue()+"!="+previnstret+")\n{\n";
                                  tempstr=op.getOperandValue()+"=="+previnstret;
                                }
                                else
                                {
                                    tempString= "\nwhile("+op.getOperandValue()+")\n{\n";
                                    tempstr=op.getOperandValue();
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,true,"while",last,op.getOperandValue()+"!="+previnstret);
                                if(c)
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            else
                            {
                                /*  ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                // ifst.setIfCloseLineNumber(classIndex-3);       */
                                ifst.setElseCloseLineNumber(gotoIndex);
                                //    ifHashTable.put(""+(ifLevel),ifst);

                                isIfInScope=true;
                                //   addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && (info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                        tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\nbreak;\n}\n";
                                    else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                    {
                                     tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\nbreak;\n}\n";
                                    }
                                    else
                                        tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                   print=false;
                                }
                                java.lang.String tempstr="";
                                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                {
                                    tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\n";
                                    tempstr=op.getOperandValue()+"=="+previnstret;
                                }
                                else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                {
                                    tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\n";
                                    tempstr=op.getOperandValue()+"=="+previnstret;
                                }

                                else
                                {
                                    tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                    tempstr=op.getOperandValue();
                                }
                               boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"!="+previnstret);
                                if(c)
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                ifhasbegun=true;
                            }

                        }
                        else
                        {
                            /*    ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);       */
                            isIfInScope=true;
                            addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && (info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\nbreak;\n}\n";
                                else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                {
                                    tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\nbreak;\n}\n";
                                }
                                else
                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                            }
                            java.lang.String tempstr="";
                            if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) &&  info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                            {
                                tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\n";
                                tempstr=op.getOperandValue()+"=="+previnstret;
                            }
                            else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                {
                                    tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\n";
                                    tempstr=op.getOperandValue()+"=="+previnstret;
                                }
                            else
                            {
                                tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                tempstr=op.getOperandValue();
                            }
                             boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"!="+previnstret);
                            if(c)
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IFNE:
                        previnstret=getReturnTypeIfPreviousInvoke(currentForIndex,info);
                        op = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ifne "+classIndex+"\n");
                        i+=2;
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        //ifst.setIfCloseLineNumber(classIndex-3);

                        ifHashTable.put(""+(ifLevel),ifst);

                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }
                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                                if(gotoIndex < classIndex)
                                {
                                    boolean isInfiniteLoop = false;
                                    Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                    while(infLoop.hasNext())
                                    {
                                        Loop iloop = (Loop)infLoop.next();
                                        if(iloop.getStartIndex() == gotoIndex  && iloop.isInfinite())
                                        {
                                            isInfiniteLoop = true;
                                            /* ifLevel++;
                                            ifst = new IFBlock();
                                            ifst.setIfStart(currentForIndex);
                                            ifst.setHasIfBeenGenerated(true);
                                            //ifst.setIfCloseLineNumber(classIndex-3);       */
                                            ifst.setElseCloseLineNumber(gotoIndex);
                                            // ifHashTable.put(""+(ifLevel),ifst);
                                            isIfInScope=true;
                                            // addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                            boolean print=true;
                                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                            {
                                                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && (info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                                    tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\nbreak;\n}\n";
                                                else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                                    tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\nbreak;\n}\n";
                                                else
                                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                                print=false;
                                                //ifst.setIfHasBeenClosed(true);
                                            }
                                            java.lang.String tempstr="";
                                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                            if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && (info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                            {
                                                tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\n";
                                                tempstr=op.getOperandValue()+"!="+previnstret;
                                            }
                                            else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                            {
                                                tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\n";
                                                tempstr=op.getOperandValue()+"!="+previnstret;
                                            }
                                            else
                                            {
                                                tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                                tempstr=op.getOperandValue();
                                            }

                                            boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"=="+previnstret);
                                            if(c)
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            ifhasbegun=true;
                                            break;
                                        }
                                    }
                                    if(isInfiniteLoop)
                                    {
                                        continue;
                                    }
                                    java.lang.String tempstr="";
                                    if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    {
                                        tempString= "\nwhile("+op.getOperandValue()+"=="+previnstret+")\n{\n";
                                        tempstr=op.getOperandValue()+"!="+previnstret;
                                    }
                                    else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                    {
                                        tempString= "\nwhile("+op.getOperandValue()+"=="+previnstret+")\n{\n";
                                        tempstr=op.getOperandValue()+"!="+previnstret;
                                    }

                                    else
                                    {
                                        tempString= "\nwhile("+op.getOperandValue()+")\n{\n";
                                        tempstr=op.getOperandValue();
                                    }
                                     boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                    boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,true,"while",last,op.getOperandValue()+"=="+previnstret);
                                    if(c)
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    isWhileInScope = true;
                                    whileIndex++;

                                }
                                else
                                {
                                    /*   ifLevel++;
                                    ifst = new IFBlock();
                                    ifst.setIfStart(currentForIndex);
                                    ifst.setHasIfBeenGenerated(true);
                                    //ifst.setIfCloseLineNumber(classIndex-3);        */
                                    ifst.setElseCloseLineNumber(gotoIndex);
                                    //   ifHashTable.put(""+(ifLevel),ifst);
                                    isIfInScope=true;
                                    //  addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                    boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                    boolean print=true;
                                    if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                    {
                                        if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && (info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                            tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\nbreak;\n}\n";
                                        else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                            tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\nbreak;\n}\n";
                                        else
                                            tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        print=false;
                                        //ifst.setIfHasBeenClosed(true);
                                    }
                                    java.lang.String tempstr="";
                                    if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    {
                                        tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\n";
                                        tempstr=op.getOperandValue()+"!="+previnstret;
                                    }
                                    else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                    {
                                         tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\n";
                                        tempstr=op.getOperandValue()+"!="+previnstret;
                                    }
                                    else
                                    {
                                        tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                        tempstr=op.getOperandValue();
                                    }
                                      boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                    boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"=="+previnstret);
                                    if(c)
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    ifhasbegun=true;
                                }

                        }
                        else
                        {
                            /*   ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);
                            isIfInScope=true;
                            addBranchLabel(classIndex,i,ifst,currentForIndex,info);     */
                            isIfInScope=true;
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && (info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\nbreak;\n}\n";
                                else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                                   tempString= "\nif("+op.getOperandValue()+"!="+previnstret+")\n{\nbreak;\n}\n";

                                else
                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                               // ifst.setIfHasBeenClosed(true);
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            java.lang.String tempstr="";
                            if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                            {
                                tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\n";
                                tempstr=op.getOperandValue()+"!="+previnstret;
                            }
                            else if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3) && info[currentForIndex-3]!=JvmOpCodes.INSTANCEOF && isPreviousInst(info,currentForIndex,currentForIndex-3))
                            {
                                tempString= "\nif("+op.getOperandValue()+"=="+previnstret+")\n{\n";
                                tempstr=op.getOperandValue()+"!="+previnstret;
                            }
                            else
                            {
                                tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                tempstr=op.getOperandValue();
                            }

                            boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"=="+previnstret);
                            if(c)
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IFLT:
                        //previnstret=getReturnTypeIfPreviousInvoke(currentForIndex,info);
                        op = (Operand)opStack.pop();
                        //op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                        i+=2;
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iflt "+classIndex+"\n");
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        // ifst.setIfCloseLineNumber(classIndex-3);

                        ifHashTable.put(""+(ifLevel),ifst);

                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        isEndOfLoop=isIndexEndOfLoop(list,(ifst.getIfCloseLineNumber()));
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                            else
                                correctIf=false;
                        }
                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                                if(gotoIndex < classIndex)
                                {
                                    boolean isInfiniteLoop = false;
                                    Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                    while(infLoop.hasNext())
                                    {
                                        Loop iloop = (Loop)infLoop.next();
                                        if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                        {
                                            isInfiniteLoop = true;
                                            /* ifLevel++;
                                            ifst = new IFBlock();
                                            ifst.setIfStart(currentForIndex);
                                            ifst.setHasIfBeenGenerated(true);
                                            // ifst.setIfCloseLineNumber(classIndex-3);     */
                                            ifst.setElseCloseLineNumber(gotoIndex);
                                            //      ifHashTable.put(""+(ifLevel),ifst);
                                            isIfInScope=true;
//addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                            boolean print=true;
                                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                            {
                                                if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                                    tempString= "\nif("+op.getOperandValue()+"<0)\n{\nbreak;\n}\n";
                                                else
                                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                               print=false;
                                            }
                                            java.lang.String tempstr="";
                                            if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                            {
                                                tempString= "\nif("+op.getOperandValue()+">=0)\n{\n";
                                                tempstr=op.getOperandValue()+"<0";
                                            }
                                            else
                                            {
                                                tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                                tempstr=op.getOperandValue();
                                            }
                                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                            boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+">=0");
                                            if(c)
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            ifhasbegun=true;
                                            break;
                                        }
                                    }
                                    if(isInfiniteLoop)
                                    {
                                        continue;
                                    }
                                    java.lang.String tempstr="";
                                    if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    {
                                        tempString= "\nwhile("+op.getOperandValue()+" >= 0)\n{\n";
                                        tempstr=op.getOperandValue()+" < 0";
                                    }
                                    else
                                    {
                                        tempString= "\nwhile("+op.getOperandValue()+")\n{\n";
                                        tempstr=op.getOperandValue();
                                    }
                                     boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                    boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,true,"while",last,op.getOperandValue()+" >= 0");
                                    if(c)
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    isWhileInScope = true;
                                    whileIndex++;

                                }
                                else
                                {
                                    /*  ifLevel++;
                                    ifst = new IFBlock();
                                    ifst.setIfStart(currentForIndex);
                                    ifst.setHasIfBeenGenerated(true);
                                    // ifst.setIfCloseLineNumber(classIndex-3); */
                                    ifst.setElseCloseLineNumber(gotoIndex);
                                    //  ifHashTable.put(""+(ifLevel),ifst);
                                    isIfInScope=true;
                                    //  addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                    boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                    boolean print=true;
                                    java.lang.String tempstr="";
                                    if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                    {
                                        if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                        {
                                            tempString= "\nif("+op.getOperandValue()+"<0)\n{\nbreak;\n}\n";
                                        }
                                        else
                                            tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        print=false;
                                       // ifst.setIfHasBeenClosed(true);
                                    }
                                    if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    {
                                        tempString= "\nif("+op.getOperandValue()+">=0)\n{\n";
                                        tempstr=op.getOperandValue()+"<0";
                                    }
                                    else
                                    {
                                        tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                        tempstr=op.getOperandValue();
                                    }
                                    boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                    boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+">=0");
                                     if(c)
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    ifhasbegun=true;
                                }

                        }
                        else
                        {
                            /*   ifLevel++;
                            ifst = new IFBlock();
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);
                            isIfInScope=true;
                            ifst.setIfStart(currentForIndex);
                            addBranchLabel(classIndex,i,ifst,currentForIndex,info);       */
                            isIfInScope=true;
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    tempString= "\nif("+op.getOperandValue()+"<0)\n{\nbreak;\n}\n";
                                else
                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                              //  ifst.setIfHasBeenClosed(true);
                            }
                            java.lang.String tempstr="";
                            if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                            {
                                tempString= "\nif("+op.getOperandValue()+">=0)\n{\n";
                                tempstr=op.getOperandValue()+"<0";
                            }
                            else
                            {
                                tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                tempstr=op.getOperandValue();
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+">=0");
                            if(c)
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                            ifhasbegun=true;
                        }
                        // Continue from here
                        continue;
                    case JvmOpCodes.IFGE:
                        op = (Operand)opStack.pop();
                        //op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                        i+=2;
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ifge "+classIndex+"\n");
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                        isEndOfLoop=isIndexEndOfLoop(list,(ifst.getIfCloseLineNumber()));
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }


                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop &&correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex =getJumpAddress(info,t);// ((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*       ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);*/
                                        // ifst.setIfCloseLineNumber(classIndex-3);
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        //ifHashTable.put(""+(ifLevel),ifst);

                                        isIfInScope=true;

                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        boolean print=true;
                                        java.lang.String tempstr="";
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                                tempString= "\nif("+op.getOperandValue()+">=0)\n{\nbreak;\n}\n";
                                            else
                                                tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            print=false;
                                          //  ifst.setIfHasBeenClosed(true);
                                        }
                                        if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                        {
                                            tempString= "\nif("+op.getOperandValue()+"<0)\n{\n";
                                            tempstr=op.getOperandValue()+">=0";
                                        }
                                        else
                                        {
                                            tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                            tempstr=op.getOperandValue();
                                        }
                                         boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"<0");
                                        if(c)
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                java.lang.String tempstr="";
                                if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                {
                                    tempString= "\nwhile("+op.getOperandValue()+"<0)\n{\n";
                                    tempstr=op.getOperandValue()+">=0";
                                }
                                else
                                {
                                    tempString= "\nwhile("+op.getOperandValue()+")\n{\n";
                                    tempstr=op.getOperandValue();
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,true,"while",last,op.getOperandValue()+"<0");
                                if(c)
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            else
                            {
                                /* ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);*/
                                // ifst.setIfCloseLineNumber(classIndex-3);
                                ifst.setElseCloseLineNumber(gotoIndex);
                                //ifHashTable.put(""+(ifLevel),ifst);

                                isIfInScope=true;
                                //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                        tempString= "\nif("+op.getOperandValue()+">=0)\n{\nbreak;\n}\n";
                                    else
                                        tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;
                                   //ifst.setIfHasBeenClosed(true);
                                }
                                java.lang.String tempstr="";
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                {
                                    tempString= "\nif("+op.getOperandValue()+"<0)\n{\n";
                                    tempstr=op.getOperandValue()+">=0";
                                }
                                else
                                {
                                    tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                    tempstr=op.getOperandValue();
                                }
                                boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"<0");
                                if(c)
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                ifhasbegun=true;
                            }
                        }
                        else
                        {
                            /*ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);*/
                            isIfInScope=true;
                            //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    tempString= "\nif("+op.getOperandValue()+">=0)\n{\nbreak;\n}\n";
                                else
                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                               // ifst.setIfHasBeenClosed(true);
                            }
                            java.lang.String tempstr="";
                            if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                            {
                                tempString= "\nif("+op.getOperandValue()+"<0)\n{\n";
                                tempstr=op.getOperandValue()+">=0";
                            }
                            else
                            {
                                tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                tempstr=op.getOperandValue();
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"<0");
                            if(c)
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IFGT:
                        op = (Operand)opStack.pop();
                        //op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ifgt "+classIndex+"\n");
                        i+=2;
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);

                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        beyondLoop= isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }



                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex =getJumpAddress(info,t);// ((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                        isInfiniteLoop = true;
                                        /*ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);
                                        //ifst.setIfCloseLineNumber(classIndex-3);
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        ifHashTable.put(""+(ifLevel),ifst);*/
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        isIfInScope=true;
                                        //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                                tempString= "\nif("+op.getOperandValue()+">0)\n{\nbreak;\n}\n";
                                            else
                                                tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            print=false;
                                           // ifst.setIfHasBeenClosed(true);
                                        }
                                        java.lang.String tempstr="";
                                        if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                        {
                                            tempString= "\nif("+op.getOperandValue()+"<=0)\n{\n";
                                            tempstr=op.getOperandValue()+">0";
                                        }
                                        else
                                        {
                                            tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                            tempstr=op.getOperandValue();
                                        }
                                        boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"<=0");
                                        if(c)
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                java.lang.String tempstr="";
                                if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                {
                                    tempString= "\nwhile("+op.getOperandValue()+"<=0)\n{\n";
                                    tempstr=op.getOperandValue()+">0";
                                }
                                else
                                {
                                    tempString= "\nwhile("+op.getOperandValue()+")\n{\n";
                                    tempstr=op.getOperandValue();
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,true,"while",last,op.getOperandValue()+"<=0");
                                if(c)
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            else
                            {
                                /*ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                //ifst.setIfCloseLineNumber(classIndex-3);*/
                                ifst.setElseCloseLineNumber(gotoIndex);
                                //ifHashTable.put(""+(ifLevel),ifst);

                                isIfInScope=true;
                                //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                {
                                    if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                        tempString= "\nif("+op.getOperandValue()+">0)\n{\nbreak;\n}\n";
                                    else
                                        tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;
                                   // ifst.setIfHasBeenClosed(true);
                                }
                                java.lang.String tempstr="";
                                if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                {
                                    tempString= "\nif("+op.getOperandValue()+"<=0)\n{\n";
                                    tempstr=op.getOperandValue()+">0";
                                }
                                else
                                {
                                    tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                    tempstr=op.getOperandValue();
                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"<=0");
                                if(c)
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                ifhasbegun=true;
                            }

                        }
                        else
                        {
                            /*    ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);*/
                            isIfInScope=true;
                            //   addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            java.lang.String tempstr="";
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                if((info[currentForIndex-1]!=JvmOpCodes.DCMPG) && (info[currentForIndex-1] != JvmOpCodes.DCMPL) && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    tempString= "\nif("+op.getOperandValue()+">0)\n{\nbreak;}\n";
                                else
                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;}\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                               // ifst.setIfHasBeenClosed(true);
                            }
                            if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                            {
                                tempString= "\nif("+op.getOperandValue()+"<=0)\n{\n";
                                tempstr=op.getOperandValue()+"> 0";
                            }
                            else
                            {
                                tempString= "\nif("+op.getOperandValue()+")\n{\n";
                                tempstr=op.getOperandValue();
                            }
                            boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                            boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+"<=0");
                            if(c)
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                            ifhasbegun=true;

                        }

                        continue;
                    case JvmOpCodes.IFLE:
                        op = (Operand)opStack.pop();
                        //op1 = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                        i+=2;
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ifle "+classIndex+"\n");
                        blockLevel++;
                        //int s=info[classIndex-3];
                        list=behaviour.getBehaviourLoops();

                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);

                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber() ,list,info);
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                            else
                            {
                                correctIf=false;
                            }

                        }

                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                                if(gotoIndex < classIndex)
                                {
                                    boolean isInfiniteLoop = false;
                                    Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                    while(infLoop.hasNext())
                                    {
                                        Loop iloop = (Loop)infLoop.next();
                                        if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                        {
                                            isInfiniteLoop = true;
                                            /*                ifLevel++;
                                            ifst = new IFBlock();
                                            ifst.setIfStart(currentForIndex);
                                            ifst.setHasIfBeenGenerated(true);*/

                                            ifst.setElseCloseLineNumber(gotoIndex);

                                            /*ifHashTable.put(""+(ifLevel),ifst);
                                            int resetVal=checkIfElseCloseNumber(classIndex-3,ifst);
                                            ifst.setIfCloseLineNumber(resetVal);*/

                                            isIfInScope = true;

                                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                            boolean print=true;
                                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                            {
                                                if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                                    tempString= "\nif("+op.getOperandValue()+"<=0)\n{\nbreak;\n}\n";

                                                else
                                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                                print=false;
                                               // ifst.setIfHasBeenClosed(true);
                                            }

                                          java.lang.String tempstr="";
                                                if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                                {
                                                    tempString= "\nif("+op.getOperandValue()+ " > 0)\n{\n";
                                                    tempstr=op.getOperandValue()+ "<= 0";
                                                }
                                                else
                                                {
                                                    tempString= "\nif("+op.getOperandValue()+ " )\n{\n";
                                                    tempstr=op.getOperandValue();
                                                }

                                              boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                             boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+ " > 0");
                                            if(c)
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            ifhasbegun=true;
                                            break;
                                        }
                                    }
                                    if(isInfiniteLoop)
                                    {
                                        continue;
                                    }
                                    java.lang.String tempstr="";
                                    if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    {
                                        tempString= "\nwhile("+op.getOperandValue()+" > 0)\n{\n";
                                      tempstr=op.getOperandValue()+" <= 0";
                                    }
                                    else
                                    {
                                        tempString= "\nwhile("+op.getOperandValue()+")\n{\n";
                                        tempstr=op.getOperandValue();
                                    }
                                     boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                    boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,true,"while",last,op.getOperandValue()+" > 0");
                                    if(c)
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    isWhileInScope = true;
                                    whileIndex++;

                                }
                                else
                                {
                                    /*ifLevel++;
                                    ifst = new IFBlock();
                                    ifst.setIfStart(currentForIndex);
                                    ifst.setHasIfBeenGenerated(true);*/
                                    ifst.setElseCloseLineNumber(gotoIndex);
                                    /*ifHashTable.put(""+(ifLevel),ifst);
                                    int resetVal=checkIfElseCloseNumber(classIndex-3,ifst);
                                    ifst.setIfCloseLineNumber(resetVal);
                                    isIfInScope = true;*/
                                    isIfInScope = true;
                                    //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                    boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                    boolean print=true;
                                    if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                    {
                                        if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                            tempString= "\nif("+op.getOperandValue()+"<=0)\n{\nbreak;\n}\n";

                                        else
                                            tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        print=false;
                                       // ifst.setIfHasBeenClosed(true);
                                    }

                                    java.lang.String tempstr="";
                                        if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                        {
                                            tempString= "\nif("+op.getOperandValue()+ " > 0)\n{\n";
                                            tempstr=op.getOperandValue()+ " <= 0";
                                        }
                                        else
                                        {
                                            tempString= "\nif("+op.getOperandValue()+ " )\n{\n";
                                            tempstr=op.getOperandValue();
                                        }
                                       boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+ " > 0");
                                        if(c)
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);




                                    ifhasbegun=true;
                                }

                        }
                        else
                        {
                            /*ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);*/
                            isIfInScope = true;
                            //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {
                                if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                                    tempString= "\nif("+op.getOperandValue()+"<=0)\n{\nbreak;\n}\n";

                                else
                                    tempString= "\nif("+op.getOperandValue()+")\n{\nbreak;\n}\n";
                             //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                            }
                               // ifst.setIfHasBeenClosed(true);
                               java.lang.String tempstr="";
                               if(info[currentForIndex-1]!=JvmOpCodes.DCMPG && info[currentForIndex-1] != JvmOpCodes.DCMPL && info[currentForIndex-1]!=JvmOpCodes.FCMPG && info[currentForIndex-1]!=JvmOpCodes.FCMPL && info[currentForIndex-1]!=JvmOpCodes.LCMP)
                               {
                                    tempString= "\nif("+op.getOperandValue()+ " > 0)\n{\n";
                                    tempstr=op.getOperandValue()+ " <= 0";
                               }
                                else
                               {
                                    tempString= "\nif("+op.getOperandValue()+ " )\n{\n";
                                   tempstr=op.getOperandValue();
                               }
                             boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                             boolean c=addCodeStatementWRTShortcutOR(ifst,tempstr,print,"if",last,op.getOperandValue()+ " > 0");
                             if(c)
                             //codeStatements +=Util.formatDecompiledStatement(tempString);




                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IFNONNULL:
                        op = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                        i+=2;
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ifnonnull "+classIndex+"\n");
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);

                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }

                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                                if(gotoIndex < classIndex)
                                {
                                    boolean isInfiniteLoop = false;
                                    Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                    while(infLoop.hasNext())
                                    {
                                        Loop iloop = (Loop)infLoop.next();
                                        if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                        {
                                            isInfiniteLoop = true;
                                            /* ifLevel++;
                                            ifst = new IFBlock();
                                            ifst.setIfStart(currentForIndex);
                                            ifst.setHasIfBeenGenerated(true);
                                            //ifst.setIfCloseLineNumber(classIndex-3);*/
                                            ifst.setElseCloseLineNumber(gotoIndex);
                                            /*ifHashTable.put(""+(ifLevel),ifst);

                                            int resetVal=checkIfElseCloseNumber(classIndex-3,ifst);
                                            ifst.setIfCloseLineNumber(resetVal);*/
                                            isIfInScope = true;
                                             boolean print=true;
                                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                            {
                                                tempString= "\nif("+op.getOperandValue()+"!= null)\n{\nbreak;\n}\n";
                                               // ifst.setIfHasBeenClosed(true);
                                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                                print=false;
                                            }
                                              boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                               boolean c=addCodeStatementWRTShortcutOR(ifst,op.getOperandValue()+"!=null",print,"if",last,op.getOperandValue()+"== null");
                                           if(c)
                                           {
                                                tempString= "\nif("+op.getOperandValue()+"== null)\n{\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                           }
                                            ifhasbegun=true;
                                            break;
                                        }
                                    }
                                    if(isInfiniteLoop)
                                    {
                                        continue;
                                    }
                                    boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                    boolean c=addCodeStatementWRTShortcutOR(ifst,op.getOperandValue()+"!=null",true,"while",last,op.getOperandValue()+"==null");
                                    if(c)
                                    {
                                        tempString= "\nwhile("+op.getOperandValue()+" == null)\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    }
                                    isWhileInScope = true;
                                    whileIndex++;

                                }
                                else
                                {
                                    /* ifLevel++;
                                    ifst = new IFBlock();
                                    ifst.setIfStart(currentForIndex);
                                    ifst.setHasIfBeenGenerated(true);*/
                                    //ifst.setIfCloseLineNumber(classIndex-3);
                                    ifst.setElseCloseLineNumber(gotoIndex);
                                    //ifHashTable.put(""+(ifLevel),ifst);

                                    isIfInScope=true;
                                    boolean print=true;
                                    boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                    if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                    {
                                        tempString= "\nif("+op.getOperandValue()+"!= null)\n{\nbreak;\n}\n";
                                      // ifst.setIfHasBeenClosed(true);
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        print=false;
                                    }
                                     boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                       boolean c=addCodeStatementWRTShortcutOR(ifst,op.getOperandValue()+"!=null",print,"if",last,op.getOperandValue()+"==null");
                                    if(c)
                                    {
                                        tempString= "\nif("+op.getOperandValue()+"== null)\n{\n";
                                     //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    }
                                    ifhasbegun=true;
                                }

                        }
                        else
                        {
                            /*ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifst.setIfCloseLineNumber(classIndex);
                            ifHashTable.put(""+(ifLevel),ifst);
                            isIfInScope = true;*/
                            isIfInScope = true;
                            //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean print=true;
                            if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                            {

                                tempString= "\nif("+op.getOperandValue()+"!= null)\n{\nbreak;\n}\n";
                             //ifst.setIfHasBeenClosed(true);
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                print=false;
                            }


                                    boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                     boolean c=addCodeStatementWRTShortcutOR(ifst,op.getOperandValue()+"!=null",print,"if",last,op.getOperandValue()+"==null");

                                    if(c)
                                    {
                                       tempString= "\nif("+op.getOperandValue()+"== null)\n{\n";
                                       //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    }

                            ifhasbegun=true;
                        }

                        continue;
                    case JvmOpCodes.IFNULL:
                        op = (Operand)opStack.pop();
                        classIndex=getJumpAddress(info,i);
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ifnull "+classIndex+"\n");
                        i+=2;
                        blockLevel++;
                        list=behaviour.getBehaviourLoops();
                        ifLevel++;
                        ifst = new IFBlock();
                        ifst.setIfStart(currentForIndex);
                        ifst.setHasIfBeenGenerated(true);
                        ifHashTable.put(""+(ifLevel),ifst);
                        addBranchLabel(classIndex,i,ifst,currentForIndex,info);


                        beyondLoop=isBeyondLoop(ifst.getIfCloseLineNumber(),list,info);

                        isEndOfLoop=isIndexEndOfLoop(list,ifst.getIfCloseLineNumber());
                        correctIf=false;
                        if(isEndOfLoop)
                        {
                            loopstart=getLoopStartForEnd(ifst.getIfCloseLineNumber(),list);
                            if(currentForIndex > loopstart)
                            {
                                boolean ifinstcodepresent=getIfinst(loopstart,info,currentForIndex);
                                if(ifinstcodepresent)
                                {
                                    correctIf=false;
                                }
                                else
                                    correctIf=true;
                            }
                        }

                        if(info[ifst.getIfCloseLineNumber()] == JvmOpCodes.GOTO && isEndOfLoop && correctIf)
                        {
                            int t=ifst.getIfCloseLineNumber();
                            int gotoIndex = getJumpAddress(info,t);//((info[t+1]  << 8) | info[t+2]) + (ifst.getIfCloseLineNumber());
                            if(gotoIndex < (t+3))
                            {
                                boolean isInfiniteLoop = false;
                                Iterator infLoop = behaviour.getBehaviourLoops().iterator();
                                while(infLoop.hasNext())
                                {
                                    Loop iloop = (Loop)infLoop.next();
                                    if(iloop.getStartIndex() == gotoIndex && iloop.isInfinite())
                                    {
                                         isInfiniteLoop = true;
                                        /*   ifLevel++;
                                        ifst = new IFBlock();
                                        ifst.setIfStart(currentForIndex);
                                        ifst.setHasIfBeenGenerated(true);      */
                                        // ifst.setIfCloseLineNumber(classIndex-3);
                                        ifst.setElseCloseLineNumber(gotoIndex);
                                        // ifHashTable.put(""+(ifLevel),ifst);
                                        isIfInScope=true;
                                        boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                        boolean print=true;
                                        if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)
                                        {
                                            //ifst.setIfHasBeenClosed(true);
                                            tempString= "\nif("+op.getOperandValue()+" == null)\n{\nbreak;\n}\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                            print=false;
                                        }
                                         boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                        boolean c=addCodeStatementWRTShortcutOR(ifst,op.getOperandValue()+"==null",print,"if",last,op.getOperandValue()+"!=null");
                                        if(c)
                                        {
                                            tempString= "\nif("+op.getOperandValue()+" != null)\n{\n";
                                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                                        }
                                        ifhasbegun=true;
                                        break;
                                    }
                                }
                                if(isInfiniteLoop)
                                {
                                    continue;
                                }
                                 boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                boolean c=addCodeStatementWRTShortcutOR(ifst,op.getOperandValue()+"==null",true,"while",last,op.getOperandValue()+"!=null");
                                if(c)
                                {
                                    tempString= "\nwhile("+op.getOperandValue()+" != null )\n{\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                                isWhileInScope = true;
                                whileIndex++;

                            }
                            else
                            {
                                /*ifLevel++;
                                ifst = new IFBlock();
                                ifst.setIfStart(currentForIndex);
                                ifst.setHasIfBeenGenerated(true);
                                //ifst.setIfCloseLineNumber(classIndex-3);*/
                                ifst.setElseCloseLineNumber(gotoIndex);
                                /*ifHashTable.put(""+(ifLevel),ifst);
                                int resetVal=checkIfElseCloseNumber(classIndex-3,ifst);
                                ifst.setIfCloseLineNumber(resetVal);*/
                                isIfInScope = true;
                                //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                                boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                                boolean c=true;
                                boolean print=true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)

                                {
                                    tempString= "\nif("+op.getOperandValue()+"==null)\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;

                                }
                                boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                    c=addCodeStatementWRTShortcutOR(ifst,op.getOperandValue()+"==null",print,"if",last,op.getOperandValue()+"!=null");
                                    if(c)
                                    {
                                        tempString= "\nif("+op.getOperandValue()+"!= \tnull)\n{\n";

                                      //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    }
                                ifhasbegun=true;
                            }

                        }
                        else
                        {
                            /* ifLevel++;
                            ifst = new IFBlock();
                            ifst.setIfStart(currentForIndex);
                            ifst.setHasIfBeenGenerated(true);
                            ifHashTable.put(""+(ifLevel),ifst);*/
                            isIfInScope=true;
                            //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
                            boolean bb=isBeyondLoop(getJumpAddress(info,currentForIndex),behaviour.getBehaviourLoops(),info);
                            boolean c=true;
                             boolean print = true;
                                if(bb && thisLoop!=null && thisLoop.isInfinite() && !encounteredOrComp)

                                {


                                    tempString= "\nif("+op.getOperandValue()+"==null)\n{\nbreak;\n}\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    print=false;

                                }

                                  boolean last=lastIFinShortCutChain(info,ifst,currentForIndex);
                                    c=addCodeStatementWRTShortcutOR(ifst,op.getOperandValue()+"==null",print,"if",last,op.getOperandValue()+"!=null");
                                    if(c)
                                    {
                                        tempString= "\nif("+op.getOperandValue()+"!= \tnull)\n{\n";
                                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    }



                            ifhasbegun=true;

                        }

                        continue;
                    case JvmOpCodes.IINC:
                        classIndex =info[++i];
                        java.lang.String constantStr="";
                        int constant = info[++i];
                        java.lang.String varName = "";
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iinc "+classIndex+"\n");
                        // NOTE: passing load  is ok as the rangeindex used to query will be correct
                        local=getLocalVariable(classIndex,"load","int",true,currentForIndex);

                        if(local != null) {

                            boolean ad=checkForLoadAfterIINC(info,opStack,currentForIndex,local,classIndex,""+constant);
                            if(!ad)
                            {
                                prevLocalGenerated = local;
                                varName = local.getVarName();


                                if(local.isDeclarationGenerated()==false)
                                {
                                    if(constant < 0)constantStr="("+constant+")";
                                    else constantStr=""+constant;
                                    tempString=local.getDataType() + " "+varName+" = "+varName + "+"+constantStr+";\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                    local.setDeclarationGenerated(true);
                                }
                                else
                                {
                                    if(constant < 0)constantStr="("+constant+")";
                                    else constantStr=""+constant;
                                    tempString=varName+" = "+varName + "+"+constantStr+";\n";
                                    //codeStatements +=Util.formatDecompiledStatement(tempString);
                                }
                            }
                        }
                        continue;
                    case JvmOpCodes.ILOAD:
                        opValueI = info[++i];

                        local=getLocalVariable(opValueI,"load","int",false,currentForIndex);
                        if(cd.isClassCompiledWithMinusG() && local!=null)
                        {
                             addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload "+opValueI+" THIS LocalVariable:-  "+local.getVarName());
                        }
                        else
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload "+opValueI+"\n");
                        }
                        if(local != null) {
                            prevLocalGenerated = local;
                            op = new Operand();
                            boolean bo=isPrevInstIINC(info,currentForIndex,opValueI);
                            java.lang.String ltmp=local.getTempVarName();
                            if(bo && ltmp!=null)
                            {
                                op.setOperandValue(ltmp);
                            }
                            else
                            {
                                op.setOperandValue(local.getVarName());
                            }

                            op.setLocalVarIndex(opValueI);
                            op.setLocalVarType(local.getDataType());
                            opStack.push(op);
                        }


                        continue;
                    case JvmOpCodes.ILOAD_0:


                        local=getLocalVariable(0,"load","int",true,currentForIndex);
                        if(cd.isClassCompiledWithMinusG() && local!=null)
                        {
                             addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload _0  THIS LocalVariable:-  "+local.getVarName());
                        }
                        else
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload_0\n");
                        }
                        if(local != null) {
                            prevLocalGenerated = local;
                            op = new Operand();
                            boolean bo=isPrevInstIINC(info,currentForIndex,0);
                            java.lang.String ltmp=local.getTempVarName();
                            if(bo && ltmp!=null)
                            {
                                op.setOperandValue(ltmp);
                            }
                            else
                            {
                                op.setOperandValue(local.getVarName());
                            }

                            op.setLocalVarIndex(0);
                            op.setLocalVarType(local.getDataType());
                            opStack.push(op);
                        }

                        continue;
                    case JvmOpCodes.ILOAD_1:
                        local=getLocalVariable(1,"load","int",true,currentForIndex);
                        if(cd.isClassCompiledWithMinusG() && local!=null)
                        {
                             addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload _1  THIS LocalVariable:-  "+local.getVarName());
                        }
                        else
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload_1\n");
                        }
                        if(local != null) {
                            prevLocalGenerated = local;
                            op = new Operand();
                            boolean bo=isPrevInstIINC(info,currentForIndex,1);
                            java.lang.String ltmp=local.getTempVarName();
                            if(bo && ltmp!=null)
                            {
                                op.setOperandValue(ltmp);
                            }
                            else
                            {
                                op.setOperandValue(local.getVarName());
                            }

                            op.setLocalVarIndex(1);
                            op.setLocalVarType(local.getDataType());
                            opStack.push(op);
                        }

                        continue;
                    case JvmOpCodes.ILOAD_2:

                        local=getLocalVariable(2,"load","int",true,currentForIndex);
                        if(cd.isClassCompiledWithMinusG() && local!=null)
                        {
                             addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload _2  THIS LocalVariable:-  "+local.getVarName());
                        }
                        else
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload_2\n");
                        }
                        if(local != null) {
                            prevLocalGenerated = local;
                            op = new Operand();
                            boolean bo=isPrevInstIINC(info,currentForIndex,2);
                            java.lang.String ltmp=local.getTempVarName();
                            if(bo && ltmp!=null)
                            {
                                op.setOperandValue(ltmp);
                            }
                            else
                            {
                                op.setOperandValue(local.getVarName());
                            }

                            op.setLocalVarIndex(2);
                            op.setLocalVarType(local.getDataType());
                            opStack.push(op);
                        }

                        continue;
                    case JvmOpCodes.ILOAD_3:
                        local=getLocalVariable(3,"load","int",true,currentForIndex);
                        if(cd.isClassCompiledWithMinusG() && local!=null)
                        {
                             addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload _3 THIS LocalVariable:-  "+local.getVarName());
                        }
                        else
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" iload_3\n");
                        }
                        if(local != null) {
                            prevLocalGenerated = local;
                            op = new Operand();
                           boolean bo=isPrevInstIINC(info,currentForIndex,3);
                            java.lang.String ltmp=local.getTempVarName();
                            if(bo && ltmp!=null)
                            {
                                op.setOperandValue(ltmp);
                            }
                            else
                            {
                                op.setOperandValue(local.getVarName());
                            }

                            op.setLocalVarIndex(3);
                            op.setLocalVarType(local.getDataType());
                            opStack.push(op);
                        }

                        continue;
                    case JvmOpCodes.IMUL:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" imul\n");
                        op =(Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();
                        op2.setOperandValue(op1.getOperandValue()+"*"+op.getOperandValue());
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        op2.setCategory(Constants.CATEGORY1);

                        opStack.push(op2);

                        continue;
                    case JvmOpCodes.INEG:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ineg\n");
                        op =(Operand)opStack.pop();

                        op1 = new Operand();
                        op1.setOperandValue("-"+"("+op.getOperandValue()+")");
                        op1.setOperandType(Constants.IS_CONSTANT_INT);
                        op1.setCategory(Constants.CATEGORY1);
                        opStack.push(op1);
                        continue;
                    case JvmOpCodes.INSTANCEOF:


                        op = (Operand)opStack.pop();
                        op1 = new Operand();
                        op1.setCategory(Constants.CATEGORY1);

                        classIndex=getOffset(info,i);
                        i+=2;

                        constCInfo = cd.getClassInfoAtCPoolPosition(classIndex);
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" instanceof "+cd.getUTF8String(constCInfo.getUtf8pointer()).replace('/','.'));
                        ninfo=cd.getNameAndTypeAtCPoolPosition(classIndex);
                        op1.setOperandValue(op.getOperandValue()+" instanceof "+cd.getUTF8String(constCInfo.getUtf8pointer()).replace('/','.'));

                        opStack.push(op1);
                        instanceoffound=true;
                        continue;
                    case JvmOpCodes.INVOKEINTERFACE:


                        classIndex=getOffset(info,i);

                        i+=2;


                        InterfaceMethodRef iref=cd.getInterfaceMethodAtCPoolPosition(classIndex);
                        java.lang.String classname=iref.getClassname();
                        java.lang.String typeofmet=iref.getTypeofmethod();
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"invokeinterface "+classIndex+" CLASSNAME:-  "+classname+" TYPEOFMET :- "+typeofmet+"\n");
                        Util.parseDescriptor(typeofmet);
                        ArrayList paramlist=Util.getParsedSignatureAsList();
                        boolean takeret=isNextInstAStore(info,currentForIndex+5);
                         int s1=typeofmet.indexOf(")");
                        returntype=null;
                         java.lang.String rettp="";
                        if(s1!=-1 && s1+1 < typeofmet.length() )
                        {

                            rettp=typeofmet.substring(s1+1);

                            Util.parseReturnType(rettp);
                        returntype=Util.getreturnSignatureAsList();
                        }
                         java.lang.String pushStr=classname;
                        if(takeret && returntype!=null && returntype.size() > 0){pushStr=(java.lang.String)returntype.get(0);}





                        resetMethodParameters(opStack,paramlist,currentForIndex);
                        java.lang.String argumentRetType="";
                        int br=typeofmet.indexOf(")");
                        if(br!=-1)
                        {
                            char c;
                            if(typeofmet.length() >= (br+1))
                                c= typeofmet.charAt(br+1);
                            else
                                c='?';
                            argumentRetType=""+c;

                        }
                        int nargs=paramlist.size();
                        ClassInfo clazz=cd.getClassInfoAtCPoolPosition(iref.getClassPointer());
                        NameAndType  nmtype=cd.getNameAndTypeAtCPoolPosition(iref.getDescriptionPointer());
                        java.lang.String clazzName=cd.getUTF8String(clazz.getUtf8pointer());
                        registerInnerClassIfAny(clazzName.replace('.','/'));
                        java.lang.String description=cd.getUTF8String(nmtype.getUtf8pointer());
                        int j=i+1;
                        //int nargs=info[j]-1;
                        Operand allargs[]=new Operand[nargs];
                        int start=0;




                        int dex=0;
                        for(int counter=nargs-1;counter>=0;counter--)
                        {
                            //boolean boolparam=isParameterTypeBoolean(paramlist,counter);
                                op2=opStack.getTopOfStack();
                                resetOperandValueIfNecessary(paramlist,counter,op2);
                                allargs[dex++]=op2;




                        }
                        Operand interfaceRef=opStack.getTopOfStack();
                        java.lang.String args="";
                        java.lang.String bracket="(";
                        for(int c=(allargs.length-1);c>=0;c--){
                            args+=allargs[c].getOperandValue();
                            if(c!=0)
                                args+=",";
                        }
                        if(args.length() > 0)
                        {
                            bracket+=args+")";
                        }
                        else
                            bracket+=")";
                        i++;i++;
                        op1=new Operand();
                                op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket);

                                op1.setClassType(pushStr);
                        java.lang.String opvalue=(java.lang.String)op1.getOperandValue();
                        if(opvalue.startsWith("\n"))
                        {
                            opvalue=opvalue.trim()+"\n";
                            op1.setOperandValue(opvalue);
                        }
                        if(isInstStore0(info,i+1))
                        {
                          opStack.push(op1);
                        }
                        else if(isNextInstructionInvokeStatic((info[i+1])) || isNextInstructionInvokeVirtual(info[i+1]) || isNextInstructionInvokeInterface(info[i+1]) || isNextInstructionInvokeSpecial(info[i+1]) || isNextInstructionStore(info[i+1]) || isNextInstructionIf(info[i+1]) || (info[(i+1)]==JvmOpCodes.PUTFIELD) || (info[(i+1)]==JvmOpCodes.PUTSTATIC)) {  //TODO need to check for other cases like switch


                           if(argumentRetType.equalsIgnoreCase("V")==false || argumentRetType.equalsIgnoreCase("void")==false)
                           {
                            //op1=new Operand();
                            //op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
                            opStack.push(op1);
                            //op1.setClassType(pushStr);
                           }
                            else
                           {
                                tempString=interfaceRef.getOperandValue()+"."+description+bracket+";\n";
                                //codeStatements+=Util.formatDecompiledStatement(tempString);
                           }
                        }
                        else if(isNextInstructionLoad(info[i+1]))
                        {
                            if(argumentRetType.equalsIgnoreCase("V")==true || argumentRetType.equalsIgnoreCase("void")==true)
                            {
                                tempString=interfaceRef.getOperandValue()+"."+description+bracket+";\n";
                                //codeStatements+=Util.formatDecompiledStatement(tempString);
                            }
                            else
                            {
                              //  op1=new Operand();
                                ///op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
                                opStack.push(op1);
                               // op1.setClassType(pushStr);
                            }

                        }
                        else if(isNextInstructionPop(info[i+1]) || isNextInstructionReturn(info[i+1]))
                        {

                            tempString=interfaceRef.getOperandValue()+"."+description+bracket+";\n";
                            //codeStatements+=Util.formatDecompiledStatement(tempString);

                        }
                        else if(isNextInstructionConversionInst(info[i+1]))
                        {

                            opStack.push(op1);

                        }
                        else if(checkForValueReturn(info,(i+1)))
                        {

                            opStack.push(op1);

                        }
                        else if(checkForSomeSpecificInstructions(info,(i+1)))
                        {
                            opStack.push(op1);
                            //op1.setClassType(classname);
                        }
                        else {
                            /*op1=new Operand();
                            op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
                            opStack.push(op1);*/
                            if(argumentRetType.equalsIgnoreCase("V")==false || argumentRetType.equalsIgnoreCase("void")==false )
                            {
                                   opStack.push(op1);
                              //     op1.setClassType(classname);
                            }
                            else
                            {
                                tempString=interfaceRef.getOperandValue()+"."+description+bracket+";\n";
                                //codeStatements+=Util.formatDecompiledStatement(tempString);
                            }
                        }


                        // TODO check this condition whether it will apply
                        // in all cases . Why only if ot store....Check
                        /*if(isNextInstructionStore(info[i+1]) || isNextInstructionIf(info[i+1])) {
                        op1=new Operand();
                        op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
                        opStack.push(op1);
                        }
                        else if(info[i+1] == JvmOpCodes.POP)  // TODO: Handle POP2
                        {
                        op1=new Operand();
                        op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
                        opStack.push(op1);
                        tempString=interfaceRef.getOperandValue()+"."+description+bracket+";\n";
                        //codeStatements+=Util.formatDecompiledStatement(tempString);
                        }
                        else if(info[i+1] == JvmOpCodes.CHECKCAST)  // TODO: Handle POP2
                        {
                        op1=new Operand();
                        op1.setOperandValue(interfaceRef.getOperandValue()+"."+description+bracket+";\n");
                        opStack.push(op1);
                        }
                        else
                        {
                        tempString=interfaceRef.getOperandValue()+"."+description+bracket+";\n";
                        //codeStatements+=Util.formatDecompiledStatement(tempString);
                        }*/




                        continue;
                    case JvmOpCodes.INVOKESPECIAL:

                        // TODO:
                        /***
                         * 1>check when result has to be pushed and when just printed and when both
                         * 2>In case of method calls and b is not null check whether this has to be
                         *  added or super (no other case possible ? )
                         * 3> Need to handle new instruction properly
                         */
                        boolean previnstwasinvoke=false;
                        if(currentForIndex-3 >=0 && isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-3))
                        {
                        if(info[(currentForIndex-3)]==JvmOpCodes.INVOKESPECIAL)previnstwasinvoke=true;
                        }
                        java.lang.String RET="";
                        boolean appendToCodeStmt=false;
                        Operand invokingObjectRef=null;
                        prevInstInvokeVirtual = true;
                        funcCall = "";

                        /*temp1=info[++i];
                        temp2=info[++i];
                        classIndex=((temp1 << 8) | temp2);*/
                        //if(classIndex < 0)classIndex=(temp1+1)*256-Math.abs(temp2);
                        classIndex=getOffset(info,i);
                        i+=2;

                        mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        java.lang.String classtype=mref.getClassname();
                        ninfo = cd.getNameAndTypeAtCPoolPosition(mref.getDescriptionPointer());

                        b = (Behaviour)methodLookUp.get(mref.getKey());


                        boolean argumentReturnTypeChecked=false;
                        typeofmet=mref.getTypeofmethod();
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"invokespecial "+classIndex+" CLASSNAME:-  "+classtype+" TYPEOFMET :- "+typeofmet+"\n");
                        Util.parseDescriptor(typeofmet);
                        paramlist=Util.getParsedSignatureAsList();

                        takeret=isNextInstAStore(info,currentForIndex+3);
                        s1=typeofmet.indexOf(")");
                        returntype=null;
                        if(s1!=-1 && s1+1 < typeofmet.length() )
                        {

                            rettp=typeofmet.substring(s1+1);

                            Util.parseReturnType(rettp);
                        returntype=Util.getreturnSignatureAsList();
                        }
                         pushStr=classtype;
                        if(returntype!=null && returntype.size() > 0){pushStr=(java.lang.String)returntype.get(0);}
                        resetMethodParameters(opStack,paramlist,currentForIndex);
                        br=typeofmet.indexOf(")");
                        if(br!=-1)
                        {
                            char c;
                            if(typeofmet.length() >= (br+1))
                                c= typeofmet.charAt(br+1);
                            else
                                c='?';
                            RET=""+c;

                        }
                        argumentRetType="";
                        if( b != null) {
                            argumentRetType=b.getReturnType();
                            op1 = new Operand();
                            //op1.setCategory(Constants.CATEGORY1);
                            op1.setOperandType(Constants.IS_OBJECT_REF);

                            int numParams = b.getMethodParams().length;
                            if(!newfound)
                                funcCall = b.getBehaviourName()+"(";
                            if(numParams==0) {
                                funcCall+=")";
                                op2 = (Operand)opStack.pop();
                                invokingObjectRef=op2;
                            } else {
                                Operand[] oparr = new Operand[numParams];
                                int opArrIndx = numParams - 1;
                                dex=0;
                                for(int indx=numParams-1;indx>=0;indx--) {

                                    op2 = (Operand)opStack.pop();
                                    resetOperandValueIfNecessary(paramlist,indx,op2);
                                    oparr[dex++] = op2;
                                    opArrIndx--;
                                    /*if(indx == numParams - 1)
                                    {
                                    funcCall += op2.getOperandValue()+")";
                                    }
                                    else
                                    {
                                    funcCall += op2.getOperandValue()+",";
                                    }*/
                                    //	b.getOpStack().push(op2);

                                }
                                Operand objRef = (Operand)opStack.pop();
                                invokingObjectRef=objRef;

                                for(int indx = numParams-1;indx>=0;indx--) {
                                    op2 = oparr[indx];
                                    if(indx > 0) {
                                        funcCall += op2.getOperandValue()+",";

                                    } else {
                                        funcCall += op2.getOperandValue()+")";
                                    }
                                }



                            }
                            java.lang.String ThisClassName=this.behaviour.getDeclaringClass(); // Class getting decompiled
                            java.lang.String methodName=mref.getMethodName(); // Method being invoked
                            java.lang.String declaringClassname=mref.getClassname(); // Class declaring the method being invoked
                            java.lang.String superClassName=cd.getSuperClassName();
                            if(methodName.equals(declaringClassname) && declaringClassname.equals(ThisClassName))
                            {  // Constructor Call
                                /*Operand objRef = (Operand)opStack.pop();
                                invokingObjectRef=objRef;*/
                                if(superClassName.equals(declaringClassname) && !newfound)
                                {
                                    invokingObjectRef.setOperandValue("super");


                                    op1.setOperandValue(invokingObjectRef.getOperandValue());
                                }
                            }
                            else
                            {
                                /*Operand objRef = (Operand)opStack.pop();
                                invokingObjectRef=objRef;*/
                                if(superClassName.equals(declaringClassname) && !newfound)
                                {
                                    invokingObjectRef.setOperandValue("super");
                                     funcCall=invokingObjectRef.getOperandValue()+"."+funcCall;
                                    op1.setOperandValue(funcCall);
                                }
                                else
                                {
                                    op1.setOperandValue(invokingObjectRef.getOperandValue()+"."+funcCall);
                                }
                                appendToCodeStmt=true;
                            }
                            if(newfound)
                            {
                                appendToCodeStmt=true;


                            }


                        } else {

                            java.lang.String methodSignature = mref.getTypeofmethod();
                            br=methodSignature.indexOf(")");
                            if(br!=-1)
                            {
                                char c;
                                if(methodSignature.length() >= (br+1))
                                    c= methodSignature.charAt(br+1);
                                else
                                    c='?';
                                argumentRetType=""+c;

                            }
                            methodSignature = methodSignature.substring(1,methodSignature.indexOf(")"));
                            int numberOfParameters = paramlist.size();
                            java.lang.String[] funcArray = new java.lang.String[numberOfParameters];
                            java.lang.String ThisClassName=this.behaviour.getDeclaringClass(); // Class getting decompiled
                            java.lang.String declaringClassname=mref.getClassname(); // Class declaring the method being invoked
                            java.lang.String methodName=mref.getMethodName(); // Method being invoked

                            int index = 0;
                            int funcArrayIndex = 0;
                            dex=0;
                            for(int indx=numberOfParameters-1;indx>=0;indx--) {
                                op2 = (Operand)opStack.pop();
                                 resetOperandValueIfNecessary(paramlist,indx,op2);
                                funcArray[dex++] = (op2.getOperandValue()).toString();
                                funcArrayIndex++;
                            }



                            // NOTE:
                            // Case where it applies for the constructor of this class
                            // we dont do anything here . This is because the new instruction
                            // takes care of that

                            //funcCall += (java.lang.String)op2.getOperandValue() + "." + mref.getMethodName() + "(";
                            if(methodName.equals(declaringClassname) && declaringClassname.replace('/','.').equals(cd.getSuperClassName()) && !newfound)
                            {
                                op2 = (Operand)opStack.pop();
                                funcCall="super(";
                                appendToCodeStmt=true;
                            }
                            // Handle Case of super.<someMethod> Here
                            else if(methodName.equals(declaringClassname)==false && declaringClassname.replace('/','.').equals(cd.getSuperClassName()) && !newfound)
                            {

                                op2 = (Operand)opStack.pop();
                                funcCall="super."+mref.getMethodName()+"(";
                                appendToCodeStmt=true;

                            }

                            //TODO : Why is this else block needed ?
                         /*   else if(methodN45ame.equals(declaringClassname)==false && declaringClassname.replace('/','.').equals(cd.getSuperClassName())==false)
                            {

                                int d=1;
                                int d2=2;
                            } */
                            else  if(newfound || previnstwasinvoke)
                                {
                                    appendToCodeStmt=true;
                                    op2 = (Operand)opStack.pop();

                                }
                            else
                            {

                                appendToCodeStmt=true;
                                    op2 = (Operand)opStack.pop();
                            }

                                // TODO: check if anything else comes here


                            for(int indx = funcArray.length-1;indx >= 0; indx--) {
                                if(indx != 0) {
                                    funcCall += funcArray[indx]+",";
                                } else {
                                    funcCall += funcArray[indx];
                                }

                            }


                            funcCall+=")";


                            op1 = new Operand();
                            //op1.setCategory(Constants.CATEGORY1);
                            op1.setOperandType(Constants.IS_OBJECT_REF);
                            op1.setOperandValue(funcCall);

                        }
                        /*boolean invokedfound=false;
                        boolean retonload=false;
                        if(isNextInstructionAnyInvoke(info[(i+1)],new StringBuffer()))
                        {
                                       invokedfound=true;
                        } */

                        if(newfound && !previnstwasinvoke) // removed appendToCodeStmt
                        {
                            //booleaisNewPresentBeforeThisNew(currentForIndex);
                            tempString="("+funcCall+";\n";
                            ////codeStatements +=Util.formatDecompiledStatement(tempString);
                            //codeStatements +=tempString;


                        }
                        if(previnstwasinvoke) // removed appendToCodeStmt
                        {

                            if(opStack.isEmpty()==false)op2=opStack.getTopOfStack();
                            funcCall=op2.getOperandValue()+funcCall;
                            op1 = new Operand();
                            //op1.setCategory(Constants.CATEGORY1);
                            op1.setOperandType(Constants.IS_OBJECT_REF);
                            op1.setOperandValue(funcCall);
                            if(opStack.size() > 1 )
                             {
                                  Operand temp1=opStack.peekTopOfStack();
                                  if(temp1.getOperandValue().equals(op2.getOperandValue()))
                                  {
                                    opStack.pop();
                                  }

                             }



                        }
                        /*java.lang.String v=op1.getOperandValue();
                        StringBuffer bb=new StringBuffer("");
                        Util.checkForImport(v,bb);
                        op1.setOperandValue(bb.toString());*/
                        StringBuffer dummy=new StringBuffer("");
                        //else
                        //{
                        if(isInstStore0(info,i+1))
                        {
                           /* if(opStack.size() > 0)
                            {
                                Operand top=opStack.peekTopOfStack();
                                java.lang.String v=top.getOperandValue();
                                /*try
                                {
                                  if(v!=null)
                                  {
                                    Integer.parseInt(v.trim()) ;
                                  }
                                }
                                catch(NumberFormatException ne)
                                {
                                    opStack.pop();
                                }

                            }          */

                            if(!newfound && !previnstwasinvoke)
                            {
                            opStack.push(op1);
                            op1.setClassType(pushStr);
                            }
                        }
                        else if(isNextInstructionInvokeStatic((info[i+1])) || isNextInstructionInvokeVirtual(info[i+1]) || isNextInstructionInvokeInterface(info[i+1]) || isNextInstructionInvokeSpecial(info[i+1]) || isNextInstructionStore(info[i+1]) || isNextInstructionIf(info[i+1])) {
                            if(RET.equalsIgnoreCase("V")==false)
                            {
                                if(newfound)// || previnstwasinvoke)
                                {
                                    op1=opStack.peekTopOfStack();
                                }
                                //  if(previnstwasinvoke || newfound)
                                opStack.push(op1);
                                op1.setClassType(pushStr);
                            }
                            else
                            {
                                boolean n=false;
                                if(!newfound && !previnstwasinvoke){
                                    tempString=Util.formatDecompiledStatement(funcCall+";\n");
                                    //codeStatements += tempString;
                                    n=true;
                                }
                                if(previnstwasinvoke)
                                {
                                    //op1=opStack.peekTopOfStack();

                                    n=true;
                                    opStack.push(op1);
                                    op1.setClassType(pushStr);
                                }
                                if(!n)
                                {
                                    if(newfound)// || previnstwasinvoke)
                                    {
                                        op1=opStack.peekTopOfStack();
                                    }
                                    opStack.push(op1);
                                    op1.setClassType(pushStr);
                                }
                            }
                        }
                        else if( (info[(i+1)]==JvmOpCodes.PUTFIELD) || (info[(i+1)]==JvmOpCodes.PUTSTATIC))
                        {
                            if(newfound)// || previnstwasinvoke)  // TODO: test invokespecial tho
                            {
                                op1=opStack.peekTopOfStack();
                            }
                            opStack.push(op1);
                            op1.setClassType(pushStr);
                        }
                        else if(isNextInstructionLoad(info[i+1]))
                        {

                            if(RET.equalsIgnoreCase("V")==true)
                            {
                                boolean n=false;
                                if(!newfound && !previnstwasinvoke){
                                    tempString=Util.formatDecompiledStatement(funcCall+";\n");
                                    //codeStatements += tempString;
                                    n=true;
                                }
                                if(previnstwasinvoke)
                                {
                                    //op1=opStack.peekTopOfStack();
                                    n=true;
                                    opStack.push(op1);
                                    op1.setClassType(pushStr);
                                }

                                if(!n)
                                {
                                    int removeme=1;
                                    if(newfound)// || previnstwasinvoke)
                                    {
                                        op1=opStack.peekTopOfStack();
                                    }
                                    //   opStack.push(op1);
                                    op1.setClassType(pushStr);

                                }
                            }
                            else

                            {
                                if(newfound)// || previnstwasinvoke)
                                {
                                    op1=opStack.peekTopOfStack();
                                }
                                //if(previnstwasinvoke || newfound)
                                opStack.push(op1);
                                op1.setClassType(pushStr);
                                /*  tempString=Util.formatDecompiledStatement(funcCall+";\n");
                                //codeStatements += tempString;*/
                            }

                        }  //?
                        else if(isNextInstructionPop(info[i+1]) || isNextInstructionReturn(info[i+1]))
                        {
                            boolean n=false;
                            boolean skipr=false;
                            if(isThisInstrStart(behaviour.getInstructionStartPositions(),(i+2)))
                            {

                                if(info[(i+2)]==JvmOpCodes.GETSTATIC)
                                {
                                    opStack.push(op1);
                                    op1.setClassType(pushStr);
                                    skipr=true;
                                }


                            }


                            if(!newfound && !previnstwasinvoke && !skipr){
                                tempString=Util.formatDecompiledStatement(funcCall+";\n");
                                //codeStatements += tempString;
                                n=true;
                            }
                            if(previnstwasinvoke && !skipr)
                            {
                                //op1=opStack.peekTopOfStack();
                                n=true;
                                opStack.push(op1);
                                op1.setClassType(pushStr);
                            }
                            if(!n  && !skipr)
                            {
                                if(newfound)// || previnstwasinvoke)
                                {
                                    op1=opStack.peekTopOfStack();
                                }
                                opStack.push(op1);
                                op1.setClassType(pushStr);
                            }

                        }
                        else if(isNextInstructionConversionInst(info[i+1]))
                        {
                            if(newfound)// || previnstwasinvoke)
                            {
                                op1=opStack.peekTopOfStack();
                            }
                            //if(previnstwasinvoke || newfound)
                            opStack.push(op1);
                            op1.setClassType(pushStr);
                        }

                        else if(checkForValueReturn(info,(i+1)))
                        {
                            if(newfound)// || previnstwasinvoke)
                            {
                                op1=opStack.peekTopOfStack();
                            }
                            //if(previnstwasinvoke || newfound)
                            opStack.push(op1);
                            op1.setClassType(pushStr);
                        }
                        else if(checkForSomeSpecificInstructions(info,(i+1)) )
                        {
                            if(newfound)// || previnstwasinvoke)
                            {
                                op1=opStack.peekTopOfStack();
                            }
                            //if(previnstwasinvoke || newfound) // because of checkcast addition
                            opStack.push(op1);
                            op1.setClassType(pushStr);
                            //else

                        }

                        else if(isInstAnyBasicPrimitiveOperation(info,(i+1),dummy) || (info[i+1]==JvmOpCodes.GETFIELD || info[i+1]==JvmOpCodes.GETSTATIC))
                        {
                            if(newfound)// || previnstwasinvoke)
                            {
                                op1=opStack.peekTopOfStack();
                            }
                            //if(previnstwasinvoke || newfound) // because of checkcast addition
                            opStack.push(op1);
                            op1.setClassType(pushStr);
                        }
                        else {
                            if(!newfound && !previnstwasinvoke ){
                                tempString=Util.formatDecompiledStatement(funcCall+";\n");
                                //codeStatements += tempString;
                            }
                            else
                            {
                                Operand opd = new Operand();
                                //opd.setCategory(Constants.CATEGORY1);
                                opd.setOperandType(Constants.IS_OBJECT_REF);
                                opd.setOperandValue(funcCall);
                                if(newfound)
                                {
                                    Operand OP=opStack.peekTopOfStack();
                                    if(OP!=null)
                                    {
                                        opd.setOperandValue(OP.getOperandValue() );
                                    }
                                }
                                //  opStack.push(opd);


                            }
                        }
                        /*if(!newfound && (!invokedfound && !retonload ))
                        {
                        tempString=funcCall+";\n";
                        //   opStack.push(op1);
                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                        }
                        if(!newfound && (invokedfound || retonload))
                        {
                        if(invokedfound)
                        {
                        if(RET.equalsIgnoreCase("V")==false)
                        {
                        opStack.push(op1);
                        }
                        else
                        {
                        tempString=funcCall+";\n";

                        //codeStatements +=Util.formatDecompiledStatement(tempString);
                        }
                        }
                        else
                        opStack.push(op1);
                        // //codeStatements +=Util.formatDecompiledStatement(tempString);
                        } */
                        //}
                        if(newfound)newfound=false;
                        Operand p=opStack.peekTopOfStack();
                        if(p!=null)
                            p.setClassType(classtype);


                        continue;

                    case JvmOpCodes.INVOKESTATIC:
                        RET="";
                        boolean codeStmtFormed=false;
                        funcCall="";
                        cd.printAllUtf8StringInNameAndTypeObjects();

                        /*temp1=info[++i];
                        temp2=info[++i];
                        classIndex=((temp1 << 8) | temp2);
                        if(classIndex < 0)classIndex=(temp1+1)*256-Math.abs(temp2);*/
                        classIndex=getOffset(info,i);
                        i+=2;
                        mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        classname=mref.getClassname();
                        typeofmet=mref.getTypeofmethod();
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"invokestatic "+classIndex+" CLASSNAME:-  "+classname+" TYPEOFMET :- "+typeofmet+"\n");
                        Util.parseDescriptor(typeofmet);
                        paramlist=Util.getParsedSignatureAsList();
                        takeret=isNextInstAStore(info,currentForIndex+3);
                        s1=typeofmet.indexOf(")");
                        returntype=null;
                        if(s1!=-1 && s1+1 < typeofmet.length() )
                        {

                            rettp=typeofmet.substring(s1+1);

                            Util.parseReturnType(rettp);
                        returntype=Util.getreturnSignatureAsList();
                        }
                         pushStr=classname;

                        if(returntype!=null && returntype.size() > 0){pushStr=(java.lang.String)returntype.get(0);}
                        resetMethodParameters(opStack,paramlist,currentForIndex);
                        registerInnerClassIfAny(classname.replace('.','/'));
                        ninfo = cd.getNameAndTypeAtCPoolPosition(mref.getDescriptionPointer());

                        b = (Behaviour)methodLookUp.get(mref.getKey());

                       br=typeofmet.indexOf(")");
                        if(br!=-1)
                        {
                            char c;
                            if(typeofmet.length() >= (br+1))
                                c= typeofmet.charAt(br+1);
                            else
                                c='?';
                            RET=""+c;

                        }
                        argumentReturnTypeChecked=false;
                        argumentRetType="";
                        if( b != null) {
                            argumentRetType=b.getReturnType();
                            op1 = new Operand();
                            //op1.setCategory(Constants.CATEGORY1);
                            op1.setOperandType(Constants.IS_OBJECT_REF);
                            // if(!b.isHasBeenDissassembled()) {
                            int numParams = b.getMethodParams().length;
                            funcCall = b.getBehaviourName()+"(";
                            dex=0;
                           /* for(int indx=numParams-1;indx>=0;indx--) {
                                op2 = (Operand)opStack.pop();
                                resetOperandValueIfNecessary(paramlist,indx,op2);
                                if(indx > 0) {
                                    funcCall += op2.getOperandValue()+",";

                                } else {
                                    funcCall += op2.getOperandValue()+")";
                                }
                                b.getOpStack().push(op2);
                                op2.setClassType(classname);

                            } */
                             java.lang.String[] funcArray = new java.lang.String[numParams];
                            dex=0;
                                                       for(int indx=numParams-1;indx>=0;indx--) {
                                                           op2 = (Operand)opStack.pop();
                                                           resetOperandValueIfNecessary(paramlist,indx,op2);
                                                           if(op2!=null && op2.getOperandValue()!=null)
                                                               funcArray[dex++] = (op2.getOperandValue()).toString();
                                                           else
                                                               funcArray[dex++] = ""+(op2.getOperandValue());

                                                       }
                               for(int indx = funcArray.length-1;indx >= 0; indx--) {
                                if(indx != 0) {
                                    funcCall += funcArray[indx]+",";
                                } else {
                                    funcCall += funcArray[indx];
                                }

                            }

                                funcCall += ");\n";


                            op1.setOperandValue(funcCall);
                            //opStack.push(op1);
                            /*b.setParentBehaviour(behaviour);
                            Disassembler disassembler=new Disassembler(b,cd);
                            disassembler.disassembleCode();
                            disassembler=null;*/

                            ////codeStatements += Util.formatDecompiledStatement(funcCall);
                            ////codeStatements+=";\n";
                            //codeStmtFormed=true;


                            //}
                        }
                        else {
                            java.lang.String methodSignature = mref.getTypeofmethod();   // Should Be Refactored...Or getting called wrongly
                            br=methodSignature.indexOf(")");
                            if(br!=-1)
                            {
                                char c;
                                if(methodSignature.length() >= (br+1))
                                    c= methodSignature.charAt(br+1);
                                else
                                    c='?';
                                argumentRetType=""+c;

                            }
                            methodSignature = methodSignature.substring(1,methodSignature.indexOf(")"));
                            int numberOfParameters = paramlist.size();
                            // int numberOfParameters =
                            java.lang.String[] funcArray = new java.lang.String[numberOfParameters];
                            int index = 0;
                            int funcArrayIndex = 0;
                            dex=0;
                            for(int indx=numberOfParameters-1;indx>=0;indx--) {
                                op2 = (Operand)opStack.pop();
                                resetOperandValueIfNecessary(paramlist,indx,op2);
                                if(op2!=null && op2.getOperandValue()!=null)
                                    funcArray[dex++] = (op2.getOperandValue()).toString();
                                else
                                    funcArray[dex++] = ""+(op2.getOperandValue());
                                funcArrayIndex++;
                            }

                            //    op2 = (Operand)opStack.pop();
                            //mref.getClassname()+"."+mref.getMethodName()
                            boolean funcCallFormed=false;
                            if(Configuration.getShowImport().equalsIgnoreCase("false")){
                                funcCall += mref.getClassname().replace('/','.')+"."+mref.getMethodName() + "(";
                                funcCallFormed=true;
                            }
                            else
                            {
                                java.lang.String simplename="";
                                java.lang.String fullName=mref.getClassname();
                                int lastSlash=mref.getClassname().lastIndexOf("/");
                                if(lastSlash==-1)
                                {
                                    lastSlash=mref.getClassname().lastIndexOf(".");
                                }
                                if(lastSlash!=-1)
                                {
                                    simplename=fullName.substring(lastSlash+1);
                                }
                                else
                                    simplename=fullName;
                                funcCall += simplename+"."+mref.getMethodName() + "(";
                                fullName=fullName.replace('/','.');
                                ConsoleLauncher.addImportClass(fullName);
                                funcCallFormed=true;

                            }
                            for(int indx = funcArray.length-1;indx >= 0; indx--) {
                                if(indx != 0) {
                                    funcCall += funcArray[indx]+",";
                                } else {
                                    funcCall += funcArray[indx];
                                }

                            }
                            if(funcCallFormed && funcCall.indexOf(";")==-1)
                                funcCall += ")";
                            op1 = new Operand();
                            //op1.setCategory(Constants.CATEGORY1);
                            op1.setOperandType(Constants.IS_OBJECT_REF);
                            op1.setOperandValue(funcCall);
                            //op3.setOperandValue(funcCall);
                            //opStack.push(op3);
                        }
                        /*	if(isNextInstructionInvokeVirtual(info[i+1]) || isNextInstructionInvokeSpecial(info[i+1]) || isNextInstructionStore(info[i+1]) || isNextInstructionIf(info[i+1])) {  //TODO need to check for other cases like switch
                        /*java.lang.String  Temp=op1.getOperandValue().toString();

                        Temp+=";\n";
                        op1.setOperandValue(Temp);
                        opStack.push(op1);
                        }
                        else {
                        /*if(info[i+1] == JvmOpCodes.ATHROW && behaviour.getParentBehaviour() != null) {  // Can this be removed
                        //behaviour.getParentBehaviour().getOpStack().push(op1);
                        ////codeStatements += funcCall+";\n";
                        } else {

                        opStack.push(op1);
                        // Commented by belurs
                        //	if(!codeStmtFormed)
                        //	//codeStatements += Util.formatDecompiledStatement(funcCall);
                        }*/
                        opvalue=(java.lang.String)op1.getOperandValue();
                        opvalue=opvalue.trim();
                        if(opvalue.endsWith(";"))
                        {
                            opvalue=opvalue.substring(0,opvalue.lastIndexOf(";"));
                        }

                        op1.setOperandValue(opvalue);
                        opvalue=(java.lang.String)op1.getOperandValue();
                        if(opvalue.startsWith("\n"))
                        {
                            opvalue=opvalue.trim();
                            op1.setOperandValue(opvalue);
                        }
                       /* v=op1.getOperandValue();
                        bb=new StringBuffer("");
                        Util.checkForImport(v,bb);
                       // op1.setOperandValue(bb.toString());*/
                         if(isInstStore0(info,i+1))
                        {
                             opStack.push(op1);
                            op1.setClassType(pushStr);
                         }
                        else if(isNextInstructionInvokeStatic((info[i+1])) || isNextInstructionInvokeVirtual(info[i+1]) || isNextInstructionInvokeInterface(info[i+1]) || isNextInstructionInvokeSpecial(info[i+1]) || isNextInstructionStore(info[i+1]) || isNextInstructionIf(info[i+1]) || (info[(i+1)]==JvmOpCodes.PUTFIELD) || (info[(i+1)]==JvmOpCodes.PUTSTATIC)) {  //TODO need to check for other cases like switch
                           if(RET.equalsIgnoreCase("V")==false)
                           {


                            opStack.push(op1);
                            op1.setClassType(pushStr);

                           }
                            else
                           {
                               tempString=Util.formatDecompiledStatement("\n"+funcCall+";\n");
                                //codeStatements += tempString;
                           }
                        }
                        else if(isNextInstructionLoad(info[i+1]))
                        {
                            if(RET.equalsIgnoreCase("V")==true)
                            {
                                tempString=Util.formatDecompiledStatement("\n"+funcCall+"\n");
                                //codeStatements += tempString;
                            }
                            else
                            {
                                opStack.push(op1);
                                op1.setClassType(pushStr);
                                /*  tempString=Util.formatDecompiledStatement(funcCall+";\n");
                                //codeStatements += tempString;*/
                            }

                        }  //?
                        else if(isNextInstructionPop(info[i+1]) || isNextInstructionReturn(info[i+1]))
                        {

                            tempString=Util.formatDecompiledStatement("\n"+funcCall+"\n");
                            //codeStatements += tempString;

                        }
                        else if(isNextInstructionConversionInst(info[i+1]))
                        {

                            opStack.push(op1);
                            op1.setClassType(pushStr);
                        }


                        else if(checkForValueReturn(info,(i+1)))
                        {

                            opStack.push(op1);
                            op1.setClassType(pushStr);
                        }
                        else if(checkForSomeSpecificInstructions(info,(i+1)))
                        {
                            opStack.push(op1);
                            op1.setClassType(pushStr);
                        }
                        else {
                            // //opStack.push(op1);
                             if(RET.equalsIgnoreCase("V")==false)
                            {
                                opStack.push(op1);
                                 op1.setClassType(pushStr);
                            }
                            else
                             {
                            tempString=Util.formatDecompiledStatement("\n"+funcCall+"\n");
                            //codeStatements += tempString;
                             }
                        }

                        continue;
                    case JvmOpCodes.INVOKEVIRTUAL:
                        prevInstInvokeVirtual = true;
                        invokevirtualFound=true;
                        funcCall = "";


                        classIndex=getOffset(info,i);i+=2;
                        mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        classname=mref.getClassname();
                        registerInnerClassIfAny(classname.replace('.','/'));
                        ninfo = cd.getNameAndTypeAtCPoolPosition(mref.getDescriptionPointer());
                        typeofmet=mref.getTypeofmethod();
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"invokevirtual "+classIndex+" CLASSNAME:-  "+classname+" TYPEOFMET :- "+typeofmet+"\n");
                        s1=typeofmet.indexOf(")");
                        rettp="";
                        returntype=null;
                        takeret=isNextInstAStore(info,currentForIndex+3);
                        if(s1!=-1 && s1+1 < typeofmet.length() )
                        {

                            rettp=typeofmet.substring(s1+1);

                            Util.parseReturnType(rettp);
                        returntype=Util.getreturnSignatureAsList();
                        }
                        pushStr=classname;
                        // NOTE: removed takeret : sbelur
                        if(returntype!=null && returntype.size() > 0){pushStr=(java.lang.String)returntype.get(0);}
                        Util.parseDescriptor(typeofmet);
                        paramlist=Util.getParsedSignatureAsList();

                        resetMethodParameters(opStack,paramlist,currentForIndex);
                        b = (Behaviour)methodLookUp.get(mref.getKey());
                        argumentReturnTypeChecked=false;
                        argumentRetType="";
                        br=typeofmet.indexOf(")");
                       RET="";
                        if(br!=-1)
                        {
                            char c;
                            if(typeofmet.length() >= (br+1))
                                c= typeofmet.charAt(br+1);
                            else
                                c='?';
                            RET=""+c;

                        }
                        if( b != null) {
                            argumentReturnTypeChecked=true;
                            argumentRetType=b.getReturnType();
                            op1 = new Operand();
                            //op1.setCategory(Constants.CATEGORY1);
                            op1.setOperandType(Constants.IS_OBJECT_REF);
                            int numParams = b.getMethodParams().length;
                            funcCall = b.getBehaviourName()+"(";
                            if(numParams==0) {
                                funcCall+=")";
                                Operand objRef=opStack.getTopOfStack();
                                java.lang.String temp=funcCall;
                                funcCall="";
                                funcCall=objRef.getOperandValue()+"."+temp;
                            } else {
                                Operand[] oparr = new Operand[numParams];
                                int opArrIndx = numParams - 1;
                                dex=0;
                                for(int indx=numParams-1;indx>=0;indx--) {
                                    op2 = (Operand)opStack.pop();
                                    resetOperandValueIfNecessary(paramlist,indx,op2);
                                    oparr[dex++] = op2;
                                    opArrIndx--;
                                    /*if(indx == numParams - 1)
                                    {
                                    funcCall += op2.getOperandValue()+")";
                                    }
                                    else
                                    {
                                    funcCall += op2.getOperandValue()+",";
                                    }*/
                                    //b.getOpStack().push(op2);

                                }

                                for(int indx = numParams-1;indx >=0;indx--) {
                                    op2 = oparr[indx];
                                    if(indx > 0) {
                                        funcCall += op2.getOperandValue()+",";

                                    } else {
                                        funcCall += op2.getOperandValue()+")";
                                    }
                                }
                                Operand objRef=opStack.getTopOfStack();
                                java.lang.String temp=funcCall;
                                funcCall="";
                                funcCall=objRef.getOperandValue()+"."+temp;
                            }

                            op1.setOperandValue(funcCall);

                            // Commented by belurs
                            // Do we have to do it ?

                            //opStack.push(op1);
                            /*b.setParentBehaviour(behaviour);
                            Disassembler disassembler=new Disassembler(b,cd);
                            disassembler.disassembleCode();
                            disassembler=null;
                            //TODO : Call the parseJVMCodes Function recrusively to parse the invokedfunction*/
                            //b.setHasBeenDissassembled(true);  // TODO: Commented by belurs
                            // Need to check where all this method is used
                            // and if and how it shud be is used
                            ////codeStatements += funcCall+";\n";
                        } else {
                            argumentReturnTypeChecked=true;
                            java.lang.String methodSignature = mref.getTypeofmethod();   // Should Be Refactored...Or getting called wrongly
                            br=methodSignature.indexOf(")");
                            if(br!=-1)
                            {
                                char c;
                                if(methodSignature.length() >= (br+1))
                                    c= methodSignature.charAt(br+1);
                                else
                                    c='?';
                                argumentRetType=""+c;

                            }
                            methodSignature = methodSignature.substring(1,methodSignature.indexOf(")"));
                            int numberOfParameters =paramlist.size();
                            // int numberOfParameters =
                            java.lang.String[] funcArray = new java.lang.String[numberOfParameters];
                            int index = 0;
                            int funcArrayIndex = 0;
                            for(int indx=numberOfParameters-1;indx>=0;indx--) {
                                op2 = (Operand)opStack.pop();
                                resetOperandValueIfNecessary(paramlist,indx,op2);

                                if(op2.getOperandValue()!=null)
                                    funcArray[indx] = (op2.getOperandValue()).toString();
                                else
                                    funcArray[indx] = ""+(op2.getOperandValue());
                                funcArrayIndex++;
                            }

                            op2 = (Operand)opStack.pop();
//                            System.out.println(op2.getOperandValue().getClass()+"           op2.getOperandValue().class");
                            funcCall += (op2.getOperandValue()!=null?op2.getOperandValue().toString():"") + "." + mref.getMethodName() + "(";
                            for(int indx = 0;indx <funcArray.length; indx++) {
                                if(indx < funcArray.length-1) {
                                    funcCall += funcArray[indx]+",";
                                } else {
                                    funcCall += funcArray[indx];
                                }

                            }
                            funcCall += ")";
                            op1 = new Operand();
                            //op1.setCategory(Constants.CATEGORY1);
                            op1.setOperandType(Constants.IS_OBJECT_REF);
                            op1.setOperandValue(funcCall);
                            //op3.setOperandValue(funcCall);
                            //opStack.push(op3);
                        }
                        /*v=op1.getOperandValue();
                        bb=new StringBuffer("");
                        Util.checkForImport(v,bb);
                        op1.setOperandValue(bb.toString());*/
                         if(isInstStore0(info,i+1))
                        {
                             opStack.push(op1);
                            op1.setClassType(pushStr);
                         }
                        else if(isNextInstructionInvokeStatic((info[i+1])) || isNextInstructionInvokeVirtual(info[i+1]) || isNextInstructionInvokeInterface(info[i+1]) || isNextInstructionInvokeSpecial(info[i+1]) || isNextInstructionStore(info[i+1]) || isNextInstructionIf(info[i+1]) || (info[(i+1)]==JvmOpCodes.PUTFIELD) || (info[(i+1)]==JvmOpCodes.PUTSTATIC)) {  //TODO need to check for other cases like switch
                            if(RET.equalsIgnoreCase("V")==false)
                            {
                                opStack.push(op1);
                                op1.setClassType(pushStr);
                            }
                            else
                            {
                                tempString=Util.formatDecompiledStatement(funcCall+";\n");
                                //codeStatements += tempString;
                            }
                        }
                        else if(isNextInstructionLoad(info[i+1]))
                        {
                            if(RET.equalsIgnoreCase("V")==true)
                            {
                                tempString=Util.formatDecompiledStatement(funcCall+";\n");
                                //codeStatements += tempString;
                            }
                            else
                            {
                                opStack.push(op1);
                                op1.setClassType(pushStr);
                            }

                        }
                        else if(isNextInstructionPop(info[i+1]) || isNextInstructionReturn(info[i+1]))
                        {

                            tempString=Util.formatDecompiledStatement(funcCall+";\n");
                            //codeStatements += tempString;

                        }
                        else if(isNextInstructionConversionInst(info[i+1]))
                        {
                           opStack.push(op1);
                                op1.setClassType(pushStr);
                        }
                        else if(checkForValueReturn(info,(i+1)))
                        {
                        opStack.push(op1);
                                op1.setClassType(pushStr);
                        }
                        else if(checkForSomeSpecificInstructions(info,(i+1)) ) // BUG here w.r.t LDC
                        {
                            opStack.push(op1);
                                op1.setClassType(pushStr);
                        }
                        else {
                            //opStack.push(op1);
                            if(RET.equalsIgnoreCase("V")==false)
                            {
                                opStack.push(op1);
                                op1.setClassType(pushStr);
                            }

                            else
                             {
                            tempString=Util.formatDecompiledStatement(funcCall+";\n");
                            //codeStatements += tempString;
                             }
                        }
                        continue;
                    case JvmOpCodes.IOR:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ior\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();
                        //op2.setCategory(Constants.CATEGORY1);
                        //op2.setOperandType(Constants.IS_CONSTANT_INT);

                        op2.setOperandValue("("+op1.getOperandValue()+"|"+op.getOperandValue()+")");

                        opStack.push(op2);

                        continue;
                    case JvmOpCodes.IREM:
                       addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"irem\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();
                        //op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_CONSTANT_INT);

                        op2.setOperandValue("("+op1.getOperandValue()+"%"+op.getOperandValue()+")");

                        opStack.push(op2);

                        continue;
                    case JvmOpCodes.IRETURN:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ireturn\n");
                        oktoadd=true;
                        mapIT=returnsAtI.entrySet().iterator();
                        while(mapIT.hasNext())
                        {
                            Map.Entry entry=(Map.Entry)mapIT.next();
                            Object key=entry.getKey();
                            Object retStatus=entry.getValue().toString();
                            if(key instanceof Integer)
                            {
                                Integer pos=(Integer)key;
                                int temp=pos.intValue();
                                if(temp==i)
                                {
                                    if(retStatus.equals("true"))
                                    {

                                        oktoadd=false;
                                        break;
                                    }
                                }
                            }

                        }


                        if(!oktoadd)
                        {
                            returnsAtI.remove(new Integer(i));
                        }

                        if(oktoadd && opStack.size() > 0){
                            op = (Operand)opStack.pop();
                            boolean bool=isMethodRetBoolean(this.behaviour);
                            if(bool)
                            {
                               if(op.getOperandValue().equals("1")){
                                   op.setOperandValue("true");
                               }
                               else if(op.getOperandValue().equals("0")){
                                op.setOperandValue("false");
                               }
                            }
                            tempString="return "+op.getOperandValue()+";\n";
                            //codeStatements +=Util.formatDecompiledStatement(tempString);
                        }
                        //behaviour.getParentBehaviour().getOpStack().push(op);
                        continue;
                    case JvmOpCodes.ISHL:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ishl\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();



                        op2 = new Operand();
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                                    op2.setOperandValue("("+op1.getOperandValue()+"<<"+op.getOperandValue()+")");

                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.ISHR:
                              addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ishr\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();



                        op2 = new Operand();
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        op2.setOperandValue("("+op1.getOperandValue()+">>"+op.getOperandValue()+")");
                        opStack.push(op2);

                        continue;
                    case JvmOpCodes.ISTORE:
                        classIndex = info[++i];

                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"istore "+classIndex);
                        handleISTORE(opStack,info,classIndex,false);
                        continue;
                    case JvmOpCodes.ISTORE_0:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"istore_0");
                        handleISTORE(opStack,info,0,true);
                        // Store the op.getOperandValue() in the local Variable;
                        continue;
                    case JvmOpCodes.ISTORE_1:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"istore_1");
                        handleISTORE(opStack,info,1,true);
                        continue;
                    case JvmOpCodes.ISTORE_2:
                      addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"istore_2");
                      handleISTORE(opStack,info,2,true);

                        continue;
                    case JvmOpCodes.ISTORE_3:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"istore_3");
                        handleISTORE(opStack,info,3,true);
                        continue;
                    case JvmOpCodes.ISUB:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"isub\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();
                        op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
op2.setOperandValue("("+op1.getOperandValue()+"- "+op.getOperandValue()+")");

                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.IUSHR:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"iushr\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op.setOperandValue(op.getOperandValue()+" & 31");

                        op2 = new Operand();

                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        op2.setOperandValue("("+op1.getOperandValue()+">>>"+op.getOperandValue() +")");
                        opStack.push(op2);
                        continue;
                    case JvmOpCodes.IXOR:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ixor\n");
                        op = (Operand)opStack.pop();
                        op1 = (Operand)opStack.pop();

                        op2 = new Operand();

                        op2.setOperandType(Constants.IS_CONSTANT_INT);

                        op2.setOperandValue("("+op1.getOperandValue()+" ^ "+op.getOperandValue()+")");

                        opStack.push(op2);

                        continue;
                        //LETTER J

                    case JvmOpCodes.JSR_W:            // TODO: Generate a program with this inst

                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"jsr_w\n");

                        // Push the Return Address on top of Stack

                        i=i+4;

                        ////codeStatements+="";  // TODO To form the original stmt....HOW?
                        continue;

                    case JvmOpCodes.JSR:   // Dummy code needed to increment i
addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"jsr\n");
                        byte nextBytes[]=new byte[3];
                        nextBytes[0]=info[++i];
                        nextBytes[1]=info[++i];
                        int offset = ((nextBytes[0] << 8) | nextBytes[1])+(i-2);

                        continue;
                        ////codeStatements+="";  // TODO To form the original stmt....HOW?	continue;


                        // Letter L
                    case JvmOpCodes.L2D:
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"l2d\n");
                        Operand operand=(Operand)opStack.pop();
                        operand.setOperandValue(operand.getOperandValue());
                        operand.setOperandType(Constants.IS_CONSTANT_DOUBLE);

                        opStack.push(operand);
                        continue;
                    case JvmOpCodes.L2F:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"l2f\n");
                        operand=opStack.getTopOfStack();
                        op.setOperandValue(op.getOperandValue());
                        operand.setOperandType(Constants.IS_CONSTANT_FLOAT);

                        opStack.push(operand);
                        continue;
                    case JvmOpCodes.L2I:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"l2i\n");
                        operand=opStack.getTopOfStack();
                        op.setOperandValue("(int)"+"("+op.getOperandValue()+")");
                        operand.setOperandType(Constants.IS_CONSTANT_INT);

                        opStack.push(operand);
                        continue;
                    case JvmOpCodes.LADD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ladd\n");
                        Operand operand1=opStack.getTopOfStack();
                        Operand operand2=opStack.getTopOfStack();
                        Object result="("+operand1.getOperandValue()+"+"+operand2.getOperandValue()+")";
                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        op.setOperandValue(result);
                        //(Constants.CATEGORY2);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.LALOAD:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"laload\n");
                        Operand index=opStack.getTopOfStack();
                        Operand arRef=opStack.getTopOfStack();
                        result=arRef.getOperandValue()+"["+index.getOperandValue()+"]";
                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        op.setOperandValue(result);
                        //(Constants.CATEGORY2);
                        opStack.push(op);continue;
                    case JvmOpCodes.LAND:
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"land\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();
                        result="("+operand1.getOperandValue()+" & "+operand2.getOperandValue()+")";
                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        //(Constants.CATEGORY2);
                        op.setOperandValue(result);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.LASTORE:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lastore\n");
                        Operand value=opStack.getTopOfStack();
                        index=opStack.getTopOfStack();
                        arRef=opStack.getTopOfStack();
                        java.lang.String stmt=arRef.getOperandValue()+"["+index.getOperandValue()+"]"+"  = "+value.getOperandValue();
                        if(stmt.indexOf(";")==-1)stmt+=";\n";
                        //codeStatements+=Util.formatDecompiledStatement(stmt);continue;
                    case JvmOpCodes.LCMP:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lcmp\n");
                        handleLCMP(opStack,info);
                        continue;

                    case JvmOpCodes.LCONST_0:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lconst_0\n");
                        handleLCONST(opStack,"0");

                        continue;
                    case JvmOpCodes.LCONST_1:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lconst_1\n");
                        handleLCONST(opStack,"1");
                        continue;
                    case JvmOpCodes.LDC :

                        opValueI = info[++i];
                        
                        // Important Fix
                        if(opValueI < 0)opValueI+=256;


                        type = -1;
                        constInt = cd.getINTPrimitiveAtCPoolPosition(opValueI);
                        if(constInt == null) {
                            constFloat = cd.getFloatPrimitiveAtCPoolPosition(opValueI);
                            if(constFloat == null) {
                                constString = cd.getStringsAtCPoolPosition(opValueI);
                                stringLiteral = cd.getUTF8String(constString.getUtf8pointer());
                                if(constString == null ) {
                                    //ERROR CONDITION
                                } else {
                                    type = Constants.IS_OBJECT_REF;
                                }
                            } else {
                                type = Constants.IS_CONSTANT_FLOAT;
                            }
                        } else {
                            type = Constants.IS_CONSTANT_INT;
                        }
                        op = new Operand();
                        op.setOperandType(type);
                        if(type == Constants.IS_CONSTANT_INT) {
                            op.setOperandValue(new Integer(constInt.getValue()));
                            op.setClassType("int");
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldc "+opValueI+" ("+constInt.getValue()+")\n");
                        }
                        if(type == Constants.IS_CONSTANT_FLOAT) {
                            op.setOperandValue(new Float(constFloat.getValue()));
                            op.setClassType("float");
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldc "+opValueI+" ("+constFloat.getValue()+")\n");
                        }
                        if(type == Constants.IS_OBJECT_REF) {
                            op.setOperandValue("\""+stringLiteral+"\"");
                            op.setClassType("String");
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldc "+opValueI+" ("+stringLiteral+")\n");
                        }
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.LDC_W:

                        /* temp1=info[++i];
                        temp2=info[++i];
                        opValueI = ((temp1<<8)|temp2);
                        if(opValueI < 0)opValueI=(temp1+1)*256-Math.abs(temp2);*/
                        opValueI=getOffset(info,i);
                        i+=2;
                       
                        constInt = null;
                        constFloat = null;
                        type = -1;
                        constInt = cd.getINTPrimitiveAtCPoolPosition(opValueI);
                        if(constInt == null) {
                            constFloat = cd.getFloatPrimitiveAtCPoolPosition(opValueI);
                            if(constFloat == null) {
                                constString = cd.getStringsAtCPoolPosition(opValueI);
                                stringLiteral = cd.getUTF8String(constString.getUtf8pointer());
                                if(stringLiteral == null || stringLiteral.length() == 0) {
                                    //ERROR CONDITION
                                } else {
                                    type = Constants.IS_OBJECT_REF;
                                }
                            } else {
                                type = Constants.IS_CONSTANT_FLOAT;
                            }
                        } else {
                            type = Constants.IS_CONSTANT_INT;
                        }
                        op = new Operand();
                        op.setOperandType(type);
                        if(type == Constants.IS_CONSTANT_INT) {
                            op.setOperandValue(new Integer(constInt.getValue()));
                            op.setClassType("int");
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldc_w "+constInt.getValue()+"\n");
                        }
                        if(type == Constants.IS_CONSTANT_FLOAT) {
                            op.setOperandValue(new Float(constFloat.getValue()));
                            op.setClassType("float");
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldc_w "+constFloat.getValue()+"\n");
                        }
                        if(type == Constants.IS_OBJECT_REF) {
                            op.setOperandValue(stringLiteral);
                            op.setClassType("String");
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldc_w "+stringLiteral+"\n");
                        }
                        //(Constants.CATEGORY1);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.LDC2_W:
                        opValueI = getOffset(info,i);
                        
                        i+=2;
                        constLong = null;
                        constDouble = null;
                        type = -1;
                        constLong = cd.getLongPrimitiveAtCPoolPosition(opValueI);
                        if(constLong == null) {
                            constDouble = cd.getDoublePrimitiveAtCPoolPosition(opValueI);
                            if(constDouble == null) {
                                // ERROR CONDITION
                            } else {
                                type = Constants.IS_CONSTANT_DOUBLE;
                            }
                        } else {
                            type = Constants.IS_CONSTANT_LONG;
                        }
                        op = new Operand();
                        op.setOperandType(type);
                        if(type == Constants.IS_CONSTANT_DOUBLE) {
                            op.setOperandValue(new Double(constDouble.getValue()));
                            
                        }
                        if(type == Constants.IS_CONSTANT_LONG) {
                            op.setOperandValue(new Long(constLong.getValue())+"L");
                        }
                        //(Constants.CATEGORY2);
                        opStack.push(op);
                        java.lang.String Value="";
                        if(type==Constants.IS_CONSTANT_LONG)
                        {
                            Value="[Long "+constLong.getValue()+"L]";
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldc2_w "+opValueI+" "+Value+"\n");
                        }
                        if(type==Constants.IS_CONSTANT_DOUBLE)
                        {
                            Value="[Double "+constDouble.getValue()+"]";
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldc2_w "+opValueI+" "+Value+"\n");
                        }

                        continue;

                    case JvmOpCodes.LDIV:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"ldiv\n");
                        handleLDIV(opStack);

                        continue;
                    case JvmOpCodes.LLOAD:
                        opValueI = info[++i];
                        if(opValueI < 0)opValueI+=256;
                        local=getLocalVariable(opValueI,"load","long",false,currentForIndex);
                        if(local!=null && cd.isClassCompiledWithMinusG())
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lload"+opValueI+" THIS LocalVariable:-  "+local.getVarName()+"\n");
                        }
                        else
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lload"+opValueI+"\n");
                        }
                        if(local!=null ) {
                            prevLocalGenerated = local;

                            op=new Operand();
                            op.setOperandType(Constants.IS_CONSTANT_LONG);
                            //(Constants.CATEGORY2);
                            op.setOperandValue(local.getVarName());
                            opStack.push(op);


                        }

                        continue;
                    case JvmOpCodes.LLOAD_0:
//
                        handleSIMPLELLOAD(opStack,0);




                        continue;
                    case JvmOpCodes.LLOAD_1:
                         handleSIMPLELLOAD(opStack,1);

                        continue;
                    case JvmOpCodes.LLOAD_2:
                        handleSIMPLELLOAD(opStack,2);


                        continue;
                    case JvmOpCodes.LLOAD_3:
                         handleSIMPLELLOAD(opStack,3);

                        continue;
                    case JvmOpCodes.LMUL:

                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" lmul\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();


                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        //(Constants.CATEGORY2);
                        op.setOperandValue("("+operand1.getOperandValue()+"*"+operand2.getOperandValue()+")");
                        opStack.push(op);

                        continue;
                    case JvmOpCodes.LNEG:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" lneg\n");
                        operand1=opStack.getTopOfStack();

                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        //(Constants.CATEGORY2);
                        op.setOperandValue("-"+"("+operand1.getOperandValue()+")");
                        opStack.push(op);

                        continue;
                    case JvmOpCodes.LOOKUPSWITCH:

                        int lookupSwitchPos=i;
                        int leave_bytes = (4 - (i % 4))-1;
                        for(int indx=0;indx<leave_bytes;indx++) {
                            i++;
                        }
                        // Read Default
                        int Default=getSwitchOffset(info,i,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                        i=i+4;
                        int numberOfLabels=getSwitchOffset(info,i,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                        i+=4;
                        //int high=(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                        //int numberOfOffsets=(high-low)+1;
                        int offsetValues[]=new int[numberOfLabels];
                        int labels[]=new int[numberOfLabels];
                        for(start=0;start<numberOfLabels;start++) {
                            int label=getSwitchOffset(info,i,"label");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                            i+=4;
                            int offsetVal=getSwitchOffset(info,i,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                            i+=4;
                            labels[start]=label;
                            offsetValues[start]=offsetVal;

                        }

                        Object ob=opStack.getTopOfStack().getOperandValue();
                        // Add to each offset
                        for(start=0;start<numberOfLabels;start++) {

                            offsetValues[start]=offsetValues[start]+lookupSwitchPos;
                        }
                        Default+=lookupSwitchPos;
                        java.lang.StringBuffer desc=new StringBuffer("");
                        desc.append("[DEFAULT :- "+Default+"]\n");
                        for(int c=0;c<labels.length;c++)
                        {
                            desc.append("[LABEL "+(c+1)+":-  "+labels[c]+" OFFSET :-  "+offsetValues[c]+"]\n");
                        }

                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" lookupswitch \n"+desc+"\n\n");
                        //start=low;
                        tempString="switch("+ob.toString()+")\n{\n ";
                        //codeStatements+=Util.formatDecompiledStatement(tempString);

                        continue;
                    case JvmOpCodes.LOR:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lor\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();

                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        op.setOperandValue("("+operand1.getOperandValue()+"|"+operand2.getOperandValue()+")");
                        //(Constants.CATEGORY2);
                        opStack.push(op);

                        continue;
                    case JvmOpCodes.LREM:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lrem\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();


                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        //(Constants.CATEGORY2);
                        op.setOperandValue("("+operand2.getOperandValue()+"%"+operand1.getOperandValue()+")");
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.LRETURN:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lreturn\n");
                        oktoadd=true;
                        mapIT=returnsAtI.entrySet().iterator();
                        while(mapIT.hasNext())
                        {
                            Map.Entry entry=(Map.Entry)mapIT.next();
                            Object key=entry.getKey();
                            Object retStatus=entry.getValue().toString();
                            if(key instanceof Integer)
                            {
                                Integer pos=(Integer)key;
                                int temp=pos.intValue();
                                if(temp==i)
                                {
                                    if(retStatus.equals("true"))
                                    {

                                        oktoadd=false;
                                        break;
                                    }
                                }
                            }

                        }


                        if(!oktoadd)
                        {
                            returnsAtI.remove(new Integer(i));
                        }

                        if(oktoadd && opStack.size() > 0){
                            op = opStack.getTopOfStack();
                            tempString="return "+op.getOperandValue().toString()+";\n";
                            //codeStatements+=Util.formatDecompiledStatement(tempString);
                        }



                        //behaviour.getParentBehaviour().getOpStack().push(op);
                        continue;
                    case JvmOpCodes.LSHL:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lshl\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();

                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        op.setCategory(Constants.CATEGORY2);
                        op.setOperandValue("("+operand2.getOperandValue()+"<<"+operand1.getOperandValue()+")");
                        opStack.push(op);

                        continue;
                    case JvmOpCodes.LSHR:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lshr\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();


                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        op.setOperandValue("("+operand2.getOperandValue()+">>"+operand1.getOperandValue()+")");
                        opStack.push(op);

                        continue;
                    case JvmOpCodes.LSTORE:
                        int pos=info[++i];
                        if(pos < 0) pos+=256;



                        local=getLocalVariable(pos,"store","long",false,currentForIndex);
                        if(local!=null && cd.isClassCompiledWithMinusG())
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lstore "+pos+" THIS LocalVariable:-  "+local.getVarName()+"\n");
                        }
                        else
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lstore "+pos+"\n");

                        if(local != null && !doNotPop) {
                            operand1=opStack.getTopOfStack();
                            prevLocalGenerated = local;
                            boolean push=isPrevInstDup(info,currentForIndex);
                             if(!push)
                           {
                            if(!local.isDeclarationGenerated()) {
                                local.setBlockIndex(blockLevel);
                                tempString=local.getDataType()+" "+local.getVarName()+"="+operand1.getOperandValue()+";\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                local.setDeclarationGenerated(true);
                            } else {
                                tempString=local.getVarName()+"="+operand1.getOperandValue()+";\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                        }
                             else
                           {
                               if(info[currentForIndex-1]==JvmOpCodes.DUP)
                                opStack.getTopOfStack();
                              if(info[currentForIndex-1]==JvmOpCodes.DUP2)
                              {
                                opStack.getTopOfStack();
                                   opStack.getTopOfStack();
                              }

                             //codeStatements +=Util.formatDecompiledStatement("("+local.getVarName()+"=("+operand1.getOperandValue()+"));\n");
                             op=createOperand(local.getVarName());
                             opStack.push(op);
                           }
                        }
                         if(doNotPop==true)doNotPop=false;
                        continue;
                    case JvmOpCodes.LSTORE_0:
                        codes=new StringBuffer("") ;
                        handleSimpleLStoreCase(opStack,info,codes,0);
                        //codeStatements+=codes.toString();

                        continue;
                    case JvmOpCodes.LSTORE_1:

                        codes=new StringBuffer("") ;
                        handleSimpleLStoreCase(opStack,info,codes,1);
                        //codeStatements+=codes.toString();
                        continue;
                    case JvmOpCodes.LSTORE_2:

                        codes=new StringBuffer("") ;
                        handleSimpleLStoreCase(opStack,info,codes,2);
                        //codeStatements+=codes.toString();

                        continue;
                    case JvmOpCodes.LSTORE_3:

                        codes=new StringBuffer("") ;
                        handleSimpleLStoreCase(opStack,info,codes,3);
                        //codeStatements+=codes.toString();
                        continue;
                    case JvmOpCodes.LSUB:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" lsub\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();

                        result="("+operand2.getOperandValue()+"-"+operand1.getOperandValue()+")";
                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);

                        op.setOperandValue(result);
                        opStack.push(op);

                        continue;
                    case JvmOpCodes.LUSHR:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" lushr\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();
                        result="("+operand2.getOperandValue()+">>>"+operand1.getOperandValue()+")";
                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        op.setCategory(Constants.CATEGORY2);
                        op.setOperandValue(result);
                        opStack.push(op);

                        continue;
                    case JvmOpCodes.LXOR:
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" lxor\n");
                        operand1=opStack.getTopOfStack();
                        operand2=opStack.getTopOfStack();


                        result="("+operand2.getOperandValue()+"^"+operand1.getOperandValue()+")";
                        op=new Operand();
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        op.setOperandValue(result);
                        op.setCategory(Constants.CATEGORY2);
                        opStack.push(op);

                        continue;

                        // Letter M
                    case JvmOpCodes.MONITORENTER:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" monitorenter\n");
                        blockLevel++;
                        op=opStack.getTopOfStack();
                        tempString="synchronized("+op.getOperandValue()+")\n{\n";
                        //codeStatements+=Util.formatDecompiledStatement(tempString);
                        currentMonitorEnterPos=currentForIndex;

                        continue;
                    case JvmOpCodes.MONITOREXIT:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" monitorexitr\n");
                        if(info[i+1]!=JvmOpCodes.ATHROW)
                            op=opStack.getTopOfStack();
                        //Shutdown s;
                        // Note: i is not icremented
                        /*if(info[i+1]!=JvmOpCodes.ATHROW)
                        {
                            //codeStatements+="\n";

                            //codeStatements+=Util.formatDecompiledStatement("}\n");
                        } */

                        continue;
                    case JvmOpCodes.MULTIANEWARRAY:
                        multinewound=true;
                        classIndex=getOffset(info,i);//(info[++i] << 8) | info[++i]);
                        i+=2;
                        ClassInfo cinfo=cd.getClassInfoAtCPoolPosition(classIndex);
                        java.lang.String temp=cd.getUTF8String(cinfo.getUtf8pointer());;

                        int lastBracket=temp.lastIndexOf("[");
                        temp=temp.substring(lastBracket+1);
                        temp=getArrayType(temp);


                        int dimensions=info[++i];
                        int Temp=i+1;
                        int nextindex=-1;
                        boolean ty=false;
                        if(info[Temp] == JvmOpCodes.ASTORE)
                        {
                            nextindex=info[(Temp+1)];
                            ty=true;
                        }
                        if(info[Temp] == JvmOpCodes.ASTORE_0)
                        {

                            nextindex=0;
                        }
                        if(info[Temp] == JvmOpCodes.ASTORE_1)
                        {

                            nextindex=1;
                        }
                        if(info[Temp] == JvmOpCodes.ASTORE_2)
                        {

                            nextindex=2;
                        }
                        if(info[Temp] == JvmOpCodes.ASTORE_3)
                        {

                            nextindex=3;
                        }
                        int d=-1;
                        int cnt=-1;
                        if(variableDimAss!=null) {
                        Integer n=(Integer)variableDimAss.get(new Integer(nextindex));
                        d=-1;

                        if(n!=null)
                        {
                            d=n.intValue();
                        }
                        }
                        else
                        {
                            if(cd.isClassCompiledWithMinusG() )
                            {
                                LocalVariable lv;
                                java.lang.String tpe=null;

                                if(ty)
                                {
                                    lv=structure.getVariabelAtIndex(nextindex,Temp+2) ;
                                    if(lv!=null)
                                    tpe=lv.getDataType();

                                }
                                else
                                {
                                    lv=structure.getVariabelAtIndex(nextindex,Temp+1) ;
                                    if(lv!=null)
                                    tpe=lv.getDataType();
                                }
                                if(tpe!=null)
                                {
                                    int openb=tpe.indexOf("[");

                                    if(openb!=-1)
                                    {
                                      cnt=1;

                                      while((openb+1) < tpe.length())
                                      {

                                        char ch=tpe.charAt((openb+1));
                                        if(ch=='[')
                                        {
                                            cnt++;

                                        }
                                          openb++;
                                      }

                                    }
                                }
                            }

                        }


                        Operand ops[]=new Operand[dimensions];
                        java.lang.String dimenPart="[";
                        for(int indx=0;indx<dimensions;indx++) {
                            ops[indx]=opStack.getTopOfStack();

                        }

                        // Reverse Array
                        Operand opsTemp[]=new Operand[dimensions];
                        int lastPos=dimensions-1;
                        for(int indx=0;indx<dimensions;indx++) {
                            opsTemp[indx]=ops[lastPos];
                            lastPos--;

                        }
                        for(int indx=0;indx<dimensions;indx++) {
                            dimenPart+=opsTemp[indx].getOperandValue()+"]";
                            if(indx!=dimensions-1)dimenPart+="[";
                        }
                        if(d > dimensions  )
                        {
                            int rem=d-dimensions;
                            for(int zz=0;zz<rem;zz++)
                            {
                                dimenPart+="[]";
                            }
                        }

                        if(cnt > dimensions  && d==-1)
                        {
                            int rem=cnt-dimensions;
                            for(int zz=0;zz<rem;zz++)
                            {
                                dimenPart+="[]";
                            }
                        }
                        if(cd.isClassCompiledWithMinusG()==false)
                        {
                            variableDimAss.put(new Integer(nextindex),new Integer(dimensions));
                        }

                        op2 = new Operand();
                        op2.setClassType(temp);
                        temp="new "+temp+dimenPart;

                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" MULTIANEWARRAY :- "+temp+dimenPart+"\n");
                        op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_ARRAY_REF);
                        op2.setOperandValue(temp);
                        op2.setMultiDimension(true);
                        opStack.push(op2);
                        continue;
                        // letter N
                    case JvmOpCodes.NEW:
                        boolean  nextisnew=true;
                        nextisnew=isNewFollowedByNew(info,currentForIndex); // returns false for invoke
                        newfound=true;
                        int newpos=i;

//					classIndex=((info[++i] << 8)|info[++i]);
                        /*temp1=info[++i];
                        temp2=info[++i];
                        classIndex=((temp1 << 8) | temp2);
                        if(classIndex < 0)classIndex=(temp1+1)*256-Math.abs(temp2);*/
                        classIndex=getOffset(info,i);
                        i+=2;

                        cinfo=cd.getClassInfoAtCPoolPosition(classIndex);
                        op=new Operand();
                        java.lang.String Type=cd.getUTF8String(cinfo.getUtf8pointer()).replace('/','.');
                        registerInnerClassIfAny(Type.replace('.','/'));
                        op.setOperandType(Constants.IS_OBJECT_REF);
                        java.lang.String Reference="JdecGenerated"+i;
                        java.lang.String newTemp="";
                        //java.lang.String objParams=getObjectParameters(newpos,info,methodLookUp);
                        if(Configuration.getShowImport().equals("false") && !nextisnew)
                            newTemp=Type +" "+Reference+"=new "+Type.trim();
                        if(Configuration.getShowImport().equals("true") && !nextisnew)
                        {
                            java.lang.String fullName=Type;
                            java.lang.String simpleName="";
                            int lastdot=fullName.lastIndexOf(".");
                            if(lastdot!=-1)
                            {
                                simpleName=fullName.substring(lastdot+1);
                                Type=simpleName;
                                ConsoleLauncher.addImportClass(fullName);
                            }
                            newTemp=Type +" "+Reference+"=new "+Type.trim();
                        }
                        if(Configuration.getShowImport().equals("false") && nextisnew)
                            newTemp="new "+Type.trim()+"(";
                        if(Configuration.getShowImport().equals("true") && nextisnew)
                        {
                            java.lang.String fullName=Type;
                            java.lang.String simpleName="";
                            int lastdot=fullName.lastIndexOf(".");
                            if(lastdot!=-1)
                            {
                                simpleName=fullName.substring(lastdot+1);
                                Type=simpleName;
                                ConsoleLauncher.addImportClass(fullName);
                            }
                            newTemp="new "+Type.trim()+"(";
                        }
                        newTemp=newTemp.trim();
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" new :- "+Type);
                        if(!nextisnew)
                        {
                        //codeStatements+="\n"+Util.formatDecompiledStatement(newTemp);
                        op.setOperandValue(Reference);
                        }
                        else
                        {
                          op.setOperandValue(newTemp);
                        }
                        op.setClassType(Type);
                        opStack.push(op);
                        continue;
                    case JvmOpCodes.NEWARRAY:

                            handleNEWARRAYCase(info);
                        i++;


                        continue;
                    case JvmOpCodes.NOP:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" nop \n");
                        continue;

                        // letter p

                    case JvmOpCodes.POP:
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" pop \n");
                        instructionPos=i;
                        //add=checkForStartOfCatch(instructionPos,methodTries);
                        if(opStack.size() > 0) // removed add==true
                        {
                            if(info[i+1]!=JvmOpCodes.GETSTATIC)
                                opStack.getTopOfStack();
                        }
                        continue;
                    case JvmOpCodes.POP2: // TODO: check this out
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" pop2 \n");
                       if(opStack.size() >= 3){
                        operand=opStack.getTopOfStack();
                        operand=opStack.getTopOfStack();
                        operand=opStack.getTopOfStack();
                       }
                              // BigInteger n;
                        continue;
                    case JvmOpCodes.PUTFIELD: //put


                        pos=getOffset(info,i);
                        i+=2;
                        fref=cd.getFieldRefAtCPoolPosition(pos);
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" putfield "+fref.getFieldName()+"\n");
                         if(doNotPop==false)
                        {
                        value=opStack.getTopOfStack();
                        Operand objRef=opStack.getTopOfStack();

                        java.lang.String freftype=fref.getTypeoffield();
                        StringBuffer sb=new StringBuffer("");
                        checkForImport(objRef.getOperandValue(),sb);
                        temp=sb.toString()+"."+fref.getFieldName()+" = "+value.getOperandValue()+";";
                        //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                        }
                        if(doNotPop)doNotPop=false;
                        continue;
                    case JvmOpCodes.PUTSTATIC:

                        pos=getOffset(info,i);i+=2;
                        handlePUTSTATIC(pos);
                        continue;

                        // Letter R
                    case JvmOpCodes.RET:
                        pos=info[++i];
                         addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" ret "+pos+"\n");
                        /* parsedString+="RET";
                        parsedString+="\t"+pos+"\n";
                        parsedString+="\t";parsedString+="\t";*/
                        if(structure!=null) {
                            local=structure.getVariabelAtIndex(pos,i-1);
                            //TODO Check comment For RET of wide instruction
                        }
                        continue;
                    case JvmOpCodes.RETURN:
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" return\n");
                        //  parsedString+="RETURN\n";
                        // parsedString+="\t";parsedString+="\t";
                        handleSimpleReturn();



                        continue;




                        // Letter S
                    case JvmOpCodes.SALOAD:
                          addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" saload\n");
                        handleSALOAD();
                        continue;
                    case JvmOpCodes.SASTORE:
                          addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" sastore\n");
                       handleSASTORE();
                        continue;
                    case JvmOpCodes.SIPUSH:

                        handleSIPUSH(info);i+=2;

                        continue;
                    case JvmOpCodes.SWAP:
                          addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" swap\n");
                        handleSwapInst(opStack);
                         continue;

                        //Letter T
                    case JvmOpCodes.TABLESWITCH        :   // TODO
                        int tableSwitchPos=i;
                        leave_bytes = (4 - (i % 4))-1;
                        for(int indx=0;indx<leave_bytes;indx++) {
                            i++;
                        }
                        // Read Default
                        Default=getSwitchOffset(info,i,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                        i+=4;
                        int low=getSwitchOffset(info,i,"label");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                        i+=4;
                        int high=getSwitchOffset(info,i,"label");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                        i+=4;
                        int numberOfOffsets=(high-low)+1;

                        offsetValues=new int[numberOfOffsets];
                        for(start=0;start<numberOfOffsets;start++) {
                            int offsetVal=getSwitchOffset(info,i,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                            i=i+4;
                            offsetValues[start]=offsetVal;

                        }

                        ob=opStack.getTopOfStack().getOperandValue();
                        Integer Index=null;
                        // Add to each offset
                        for(start=0;start<numberOfOffsets;start++) {

                            offsetValues[start]=offsetValues[start]+tableSwitchPos;
                        }
                        Default+=tableSwitchPos;
                        //parsedString+="\ntableswitch\t"+low+" "+high+": default "+Default+"\n";
                        start=low;
                        tempString="switch("+ob.toString()+")\n{\n";
                        addTBSWITCHtoDISSTMT(Default,offsetValues,low,high);
                        //codeStatements+=Util.formatDecompiledStatement(tempString);
                        //System.out.println();
                        continue;

                        // Letter W
                    case JvmOpCodes.WIDE:

                        byte nextEntry=info[++i];
                        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" wide"+nextEntry+"\n");
                        if(nextEntry==JvmOpCodes.IINC) {
                            int localVarPos=getOffset(info,i);i+=2;
                            constant=getOffset(info,i);i+=2;

                            local=getLocalVariable(localVarPos,"load","int",false,currentForIndex);

                            if(local!=null){
                                temp=local.getVarName()+" += ("+constant+");";
                                //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                            }


                        } else {
                            //   parsedString+="WIDE\n";
                            switch(nextEntry) {
                                case JvmOpCodes.ILOAD:

                                    pos=getOffset(info,i);i+=2;

                                    local=getLocalVariable(pos,"load","int",false,currentForIndex);
                                    if(local!=null) {
                                        prevLocalGenerated = local;
                                        op=new Operand();
                                        op.setOperandType(Constants.IS_CONSTANT_INT);
                                        op.setOperandValue(local.getVarName());
                                        opStack.push(op);
                                    }

                                    break;
                                case JvmOpCodes.FLOAD:
                                    pos=getOffset(info,i);i+=2;

                                    local=getLocalVariable(pos,"load","float",false,currentForIndex);
                                    if(local!=null) {
                                        prevLocalGenerated = local;
                                        op=new Operand();
                                        op.setOperandType(Constants.IS_CONSTANT_FLOAT);
                                        op.setOperandValue(local.getVarName());
                                        opStack.push(op);
                                    }

                                    //parsedString+="FLOAD "+pos+"\n";
                                    break;
                                case JvmOpCodes.ALOAD:
                                    pos=getOffset(info,i);i+=2;

                                    local=getLocalVariable(pos,"load","java.lang.Object",false,currentForIndex);
                                    if(local!=null) {
                                        prevLocalGenerated = local;
                                        op=new Operand();
                                        op.setOperandType(Constants.IS_OBJECT_REF);
                                        op.setOperandValue(local.getVarName());
                                        opStack.push(op);
                                    }

                                    //parsedString+="ALOAD"+"\n";
                                    break;
                                case JvmOpCodes.LLOAD:

                                    pos=getOffset(info,i);i+=2;

                                    local=getLocalVariable(pos,"load","long",false,currentForIndex);
                                    if(local!=null) {
                                        prevLocalGenerated = local;
                                        op=new Operand();
                                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                                        op.setOperandValue(local.getVarName());
                                        opStack.push(op);
                                    }

                                    break;
                                case JvmOpCodes.DLOAD:
                                    pos=getOffset(info,i);i+=2;

                                    local=getLocalVariable(pos,"load","double",false,currentForIndex);
                                    if(local!=null) {
                                        prevLocalGenerated = local;
                                        op=new Operand();
                                        op.setOperandType(Constants.IS_CONSTANT_DOUBLE);
                                        op.setOperandValue(local.getVarName());
                                        opStack.push(op);
                                    }

                                    // parsedString+="DLOAD"+"\n";
                                    break;
                                case JvmOpCodes.ISTORE:

                                    pos=getOffset(info,i);i+=2;
                                    operand=opStack.getTopOfStack();


                                    local=getLocalVariable(pos,"store","int",false,currentForIndex);
                                    if(local!=null){
                                        if(local.isDeclarationGenerated()==false)
                                        {
                                            temp=local.getDataType()+"   "+local.getVarName()+"= "+operand.getOperandValue()+";";
                                            local.setDeclarationGenerated(true);
                                        }
                                        else
                                        {
                                            temp=local.getVarName()+"= "+operand.getOperandValue()+";";
                                        }
                                        //codeStatements+=Util.formatDecompiledStatement(temp);
                                    }
                                    break;
                                case JvmOpCodes.FSTORE:
                                    temp="";
                                    pos=getOffset(info,i);i+=2;
                                    operand=opStack.getTopOfStack();


                                    local=getLocalVariable(pos,"store","float",false,currentForIndex);
                                    if(local!=null){
                                        if(local.isDeclarationGenerated()==false)
                                        {
                                            temp=local.getDataType()+"   "+local.getVarName()+"= "+operand.getOperandValue()+";";
                                            local.setDeclarationGenerated(true);
                                        }
                                        else
                                        {
                                            temp=local.getVarName()+"= "+operand.getOperandValue()+";";
                                        }
                                    }

                                    //codeStatements+=Util.formatDecompiledStatement(temp);
                                    break;
                                case JvmOpCodes.ASTORE:
                                    pos=getOffset(info,i);i+=2;
                                    operand=opStack.getTopOfStack();

                                    temp="";
                                    local=getLocalVariable(pos,"store","java.lang.Object",false,currentForIndex);
                                    if(local!=null){
                                        if(local.isDeclarationGenerated()==false)
                                        {
                                            if(local.wasCreated() && operand!=null && operand.getClassType().trim().length() > 0)local.setDataType(operand.getClassType());
                                            temp=local.getDataType()+"   "+local.getVarName()+"= "+operand.getOperandValue()+";";
                                            local.setDeclarationGenerated(true);
                                        }
                                        else
                                        {
                                            temp=local.getVarName()+"= "+operand.getOperandValue()+";";
                                        }
                                    }


                                    //codeStatements+=Util.formatDecompiledStatement(temp);
                                    // parsedString+="ASTORE"+"\n";
                                    break;
                                case JvmOpCodes.LSTORE:
                                    pos=getOffset(info,i);i+=2;
                                    operand=opStack.getTopOfStack();

                                    temp="";
                                    local=getLocalVariable(pos,"store","long",false,currentForIndex);
                                    if(local!=null){
                                        if(local.isDeclarationGenerated()==false)
                                        {
                                            temp=local.getDataType()+"   "+local.getVarName()+"= "+operand.getOperandValue()+";";
                                            local.setDeclarationGenerated(true);
                                        }
                                        else
                                        {
                                            temp=local.getVarName()+"= "+operand.getOperandValue()+";";
                                        }
                                    }


                                    //codeStatements+=Util.formatDecompiledStatement(temp)+"\n";
                                    // parsedString+="LSTORE"+"\n\t\t";
                                    break;
                                case JvmOpCodes.DSTORE:
                                    pos=getOffset(info,i);i+=2;
                                    operand=opStack.getTopOfStack();

                                    temp="";
                                    local=getLocalVariable(pos,"store","double",false,currentForIndex);
                                    if(local!=null){
                                        if(local.isDeclarationGenerated()==false)
                                        {
                                            temp=local.getDataType()+"   "+local.getVarName()+"= "+operand.getOperandValue()+";";
                                            local.setDeclarationGenerated(true);
                                        }
                                        else
                                        {
                                            temp=local.getVarName()+"= "+operand.getOperandValue()+";";
                                        }
                                    }
                                    //codeStatements+=Util.formatDecompiledStatement(temp);
                                    //parsedString+="DSTORE"+"\n";
                                    break;
                                case JvmOpCodes.RET: // TODO: Not doing anything here ?
                                    pos=getOffset(info,i);i+=2;
                                    /*if(structure!=null) {
                                    local=structure.getVariabelAtIndex(pos,i-2);
                                    if(local!=null) {
                                    prevLocalGenerated = local;
                                    local.getDataType();
                                    //If Type is not ReturnAddress Throw an ApplicationException
                                    }
                                    } */



                                    //  parsedString+="RET"+"\n";
                                    break;
                            }

                            // parsedString+=(info[++i] << 8) | info[++i];
                            // parsedString+="\n";

                        }
                }






            }

            catch(EmptyStackException ese)
            {
               // Skip
            }


            catch(Exception e) {
                // Skip
            }

        }
        /*
        Cases to be complete ALOAD_WIDE
        ASTORE_WIDE
        FSTORE_WIDE
        */
        behaviour.setLabels(this.LABELS);
        behaviour.setVMInstructions(disOutput);

        //return parsedString;
    }

    private static StringBuffer interpretByteCodes(StringBuffer input) {
        StringBuffer opcodes=new StringBuffer();



        return opcodes;

    }

    private java.lang.String getArrayType(java.lang.String temp) {

        if(temp.indexOf("L")!=-1) {
            java.lang.String tmp= temp.substring(temp.indexOf("L")+1,temp.indexOf(";"));
            tmp=tmp.replace('/','.');
            return tmp;

        }


        if(temp.equals("Z")) {
            return "boolean";
        }
        if(temp.equals("D")) {
            return "double";
        }
        if(temp.equals("J")) {
            return "long";
        }
        if(temp.equals("F")) {
            return "float";
        }
        if(temp.equals("B")) {
            return "byte";
        }
        if(temp.equals("I")) {
            return "int";
        }

        if(temp.equals("C")) {
            return "char";
        }
        if(temp.equals("S")) {
            return "short";
        }
        return null;
    }



    private java.lang.String parseOperandType(int type) {

        switch(type) {

            case Constants.IS_ARRAY_REF:
                return "Array Reference";
            case Constants.IS_CONSTANT_BYTE:
                return "byte";
            case Constants.IS_CONSTANT_DOUBLE:
                return "double";
            case Constants.IS_CONSTANT_FLOAT:
                return "float";
            case Constants.IS_CONSTANT_INT:
                return "int";
            case Constants.IS_CONSTANT_LONG:
                return "long";
            case Constants.IS_CONSTANT_SHORT:
                return "short";
            case Constants.IS_CONSTANT_STRING:
                return "string";
            case Constants.IS_NULL:
                return "NULL";
            case Constants.IS_OBJECT_REF:
                return "Object Reference Type";
            case Constants.IS_RETURN_ADDRESS:
                return "Return Address Type";

            default:
                return "ERROR...Unknown TYPE";

        }

    }


    private boolean isNextInstructionAnyInvoke(int nextinst,StringBuffer sb)
    {
        boolean b=isNextInstructionInvokeInterface(nextinst);
        if(b){sb.append("interface");return b;}
        b=isNextInstructionInvokeSpecial(nextinst);
        if(b){sb.append("special");return b;}
        b=isNextInstructionInvokeStatic(nextinst);
        if(b)return b;
        b=isNextInstructionInvokeVirtual(nextinst);
        if(b)return b;
        return false;
    }


    private boolean isNextInstructionInvokeInterface(int nextInst)
    {


        if(nextInst==JvmOpCodes.INVOKEINTERFACE)
            return true;
        else
            return false;
    }


    private boolean isNextInstructionInvokeSpecial(int nextInst)
    {


        if(nextInst==JvmOpCodes.INVOKESPECIAL)
            return true;
        else
            return false;
    }

    private boolean isNextInstructionInvokeStatic(int nextInst)
    {


        if(nextInst==JvmOpCodes.INVOKESTATIC)
            return true;
        else
            return false;
    }


    private boolean isNextInstructionInvokeVirtual(int nextInst)
    {


        if(nextInst==JvmOpCodes.INVOKEVIRTUAL)
            return true;
        else
            return false;
    }



    private boolean isNextInstructionConversionInst(int next)
    {
        boolean flag=false;
        switch(next)
        {

            case JvmOpCodes.D2L:
            case JvmOpCodes.D2I:
            case JvmOpCodes.D2F:
            case JvmOpCodes.I2B:
            case JvmOpCodes.I2C:
            case JvmOpCodes.I2D:
            case JvmOpCodes.I2F:
            case JvmOpCodes.I2L:
            case JvmOpCodes.I2S:
            case JvmOpCodes.L2D:
            case JvmOpCodes.L2F:
            case JvmOpCodes.L2I:
            case JvmOpCodes.F2D:

            case JvmOpCodes.F2I:
            case JvmOpCodes.F2L:
                return true;
            default    :
                return false;

        }



    }


    private int isNextInstructionConversionInst(int i,byte[] code)
    {
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),i)==false)  return -1;
        switch(code[i])
        {

            case JvmOpCodes.D2L:
            case JvmOpCodes.D2I:
            case JvmOpCodes.D2F:
            case JvmOpCodes.I2B:
            case JvmOpCodes.I2C:
            case JvmOpCodes.I2D:
            case JvmOpCodes.I2F:
            case JvmOpCodes.I2L:
            case JvmOpCodes.I2S:
            case JvmOpCodes.L2D:
            case JvmOpCodes.L2F:
            case JvmOpCodes.L2I:
            case JvmOpCodes.F2D:

            case JvmOpCodes.F2I:
            case JvmOpCodes.F2L:
                return i;
            default    :
                return -1;

        }



    }

    private java.lang.String   getSourceTypeForConversionInst(int i,byte[] code)
    {
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),i)==false)  return "";
        switch(code[i])
        {

            case JvmOpCodes.D2L:
                return "double";
            case JvmOpCodes.D2I:
                return "double";
            case JvmOpCodes.D2F:
                return "double";
            case JvmOpCodes.I2B:
                return "int";
            case JvmOpCodes.I2C:
                return "int";

            case JvmOpCodes.I2D:
                return "int";

            case JvmOpCodes.I2F:
                return "int";

            case JvmOpCodes.I2L:
                return "int";

            case JvmOpCodes.I2S:
                return "int";

            case JvmOpCodes.L2D:
                return "long";

            case JvmOpCodes.L2F:
                return "long";

            case JvmOpCodes.L2I:
                return "long";

            case JvmOpCodes.F2D:
                return "float";


            case JvmOpCodes.F2I:
                return "float";

            case JvmOpCodes.F2L:
                return "float";
            default    :
                return "";

        }



    }

    private java.lang.String  getResulatantTypeForConversionInst(int i,byte[] code)
    {
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),i)==false)  return "";
        switch(code[i])
        {

            case JvmOpCodes.D2L:
                return "long";
            case JvmOpCodes.D2I:
                return "int";
            case JvmOpCodes.D2F:
                return "float";
            case JvmOpCodes.I2B:
                return "byte";
            case JvmOpCodes.I2C:
                return "char";

            case JvmOpCodes.I2D:
                return "double";

            case JvmOpCodes.I2F:
                return "float";

            case JvmOpCodes.I2L:
                return "long";

            case JvmOpCodes.I2S:
                return "short";

            case JvmOpCodes.L2D:
                return "double";

            case JvmOpCodes.L2F:
                return "float";

            case JvmOpCodes.L2I:
                return "int";

            case JvmOpCodes.F2D:
                return "double";


            case JvmOpCodes.F2I:
                return "int";

            case JvmOpCodes.F2L:
                return "long";
            default    :
                return "";

        }



    }




    private boolean isNextInstructionLoad(int nextInstruction) {
        boolean flag=false;
        switch (nextInstruction) {

            case JvmOpCodes.BIPUSH:
                flag = true;
                break;

            case JvmOpCodes.SIPUSH:
                flag = true;
                break;


            case JvmOpCodes.AALOAD:
                flag = true;
                break;

            case JvmOpCodes.BALOAD:
                flag = true;
                break;

            case JvmOpCodes.DALOAD:
                flag = true;
                break;

            case JvmOpCodes.FALOAD:
                flag = true;
                break;

            case JvmOpCodes.LALOAD:
                flag = true;
                break;

            case JvmOpCodes.IALOAD:
                flag = true;
                break;

            case JvmOpCodes.SALOAD:
                flag = true;
                break;

            case JvmOpCodes.CALOAD:
                flag = true;
                break;


            case JvmOpCodes.ALOAD:
                flag = true;
                break;
            case JvmOpCodes.ALOAD_0 :
                flag = true;
                break;
            case JvmOpCodes.ALOAD_1 :
                flag = true;
                break;
            case JvmOpCodes.ALOAD_2:
                flag = true;
                break;
            case JvmOpCodes.ALOAD_3:
                flag = true;
                break;
            case JvmOpCodes.ILOAD:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_0:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_1:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_2:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_3:
                flag = true;
                break;

            case JvmOpCodes.LLOAD:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_3:
                flag = true;
                break;

            case JvmOpCodes.FLOAD:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_3:
                flag = true;
                break;

            case JvmOpCodes.DLOAD:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_3:
                flag = true;
                break;
            case JvmOpCodes.ICONST_0:
            case JvmOpCodes.ICONST_1:
            case JvmOpCodes.ICONST_2:
            case JvmOpCodes.ICONST_3:
            case JvmOpCodes.ICONST_M1:
            case JvmOpCodes.ICONST_4:
            case JvmOpCodes.ICONST_5:
            case JvmOpCodes.LCONST_0:
            case JvmOpCodes.LCONST_1:
            case JvmOpCodes.DCONST_0:
            case JvmOpCodes.DCONST_1:
            case JvmOpCodes.FCONST_0:
            case JvmOpCodes.FCONST_1:
            case JvmOpCodes.FCONST_2:
                flag=true;
                break;



        }

        return flag;
    }

    private boolean isNextInstructionStore(int nextInstruction) {
        boolean flag = false;

        switch (nextInstruction) {
           /* case JvmOpCodes.AASTORE :
                flag = true;
                break;*/
            case JvmOpCodes.ASTORE :
                flag = true;
                break;
            case JvmOpCodes.ASTORE_WIDE :
                flag = true;
                break;
            case JvmOpCodes.ASTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.ASTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.ASTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.ASTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.DSTORE :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.FSTORE :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.ISTORE :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.LSTORE :
                flag = true;
                break;

            case JvmOpCodes.LSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_3 :
                flag = true;
                break;


            default:
                flag = false;
        }
        return flag;
    }



    public boolean isNextInstructionReturn(int nextInstruction)
    {
        boolean flag=false;
        switch(nextInstruction)
        {
            case JvmOpCodes.RETURN:
                return true;
            default:
                return false;

        }
    }
    public boolean isNextInstructionPop(int nextInstruction)
    {
        boolean flag=false;
        switch(nextInstruction)
        {
            case JvmOpCodes.POP:
            case JvmOpCodes.POP2:
                return true;
            default:
                return false;

        }
    }

    public boolean isNextInstructionIf(int nextInstruction) {
        boolean flag = false;

        switch (nextInstruction) {
            case JvmOpCodes.IF_ACMPEQ :
                flag = true;
                break;
            case JvmOpCodes.IF_ACMPNE :
                flag = true;
                break;
            case JvmOpCodes.IF_ICMPEQ :
                flag = true;
                break;
            case JvmOpCodes.IF_ICMPGE :
                flag = true;
                break;
            case JvmOpCodes.IF_ICMPGT :
                flag = true;
                break;
            case JvmOpCodes.IF_ICMPLE :
                flag = true;
                break;
            case JvmOpCodes.IF_ICMPLT :
                flag = true;
                break;
            case JvmOpCodes.IF_ICMPNE :
                flag = true;
                break;
            case JvmOpCodes.IFEQ :
                flag = true;
                break;
            case JvmOpCodes.IFGE :
                flag = true;
                break;
            case JvmOpCodes.IFGT :
                flag = true;
                break;
            case JvmOpCodes.IFLE :
                flag = true;
                break;
            case JvmOpCodes.IFLT :
                flag = true;
                break;
            case JvmOpCodes.IFNE:
                flag = true;
                break;
            case JvmOpCodes.IFNONNULL :
                flag = true;
                break;
            case JvmOpCodes.IFNULL :
                flag = true;
                break;
            default:
                flag = false;
        }
        return flag;
    }

    // TODO: Write logic to poll for the end of the last catch
    private java.lang.String pollExcepionTables(int i,int instruction) {
        java.lang.String temp="";
        //Algo
        // For Each ExceptionTable Do
        // 1> check whether = to start of Guard
        // If so Check Whether guardtype is try or catch
        // 2> Else check whether = to end of Guard
        // If so Just return a "}"
        // 3> Else check whether = to start of Handler
        // If so check whether type is catch or finally
        // Return appropriately
        // 4> Else check whether = to end of Handler
        // just Return "}"

        // 5> Check for invalid Table values
        ArrayList alltables=this.behaviour.getExceptionTableList();
        if(alltables!=null) {
            for(int c=0;c<alltables.size();c++) {
                ExceptionTable table=(ExceptionTable)alltables.get(c);
                // System.out.println(i+"table.getEndOfHandlerForGuardRegion()"+table.getEndOfHandlerForGuardRegion());

                // Check for Invalid condition
                byte code[]=behaviour.getCode();
                if(code!=null && code[table.getStartOfGuardRegion()+1] == JvmOpCodes.MONITOREXIT)continue;

                if(code!=null && code[table.getEndOfGuardRegion()-1] == JvmOpCodes.MONITOREXIT)
                {

                    continue;
                }



                if(table.getStartOfGuardRegion()==table.getStartOfHandlerForGuardRegion())
                    continue;
                // Another  invalid to check
                if(table.getEndOfGuardRegion() > table.getStartOfHandlerForGuardRegion())
                    continue;


                // Check 1
                if(table.getStartOfGuardRegion()==i) {
                    if(table.getTypeOfGuardRegion().equals("try")) {
                        ArrayList Al=new ArrayList();
                        temp="try\n{\n";
                        Al.add(table);
                        Iterator IT=behaviour.getExceptionTableList().iterator();
                        while(IT.hasNext()) {
                            ExceptionTable etb=(ExceptionTable)IT.next();
                            if(etb.getStartOfGuardRegion()==i && etb.getTypeOfGuardRegion().equals("try")) {
                                boolean Present=alreadyPresent(Al,etb);
                                if(Present==false) {
                                    temp+="\ntry\n{\n";
                                    Al.add(etb);
                                }

                            }
                        }

                        // Check from created Exception Table List.
                        ArrayList createdList=behaviour.getCreatedTableList();
                        if(createdList!=null) {
                            IT=createdList.iterator();
                            while(IT.hasNext()) {
                                ExceptionTable etb=(ExceptionTable)IT.next();
                                if(etb.getStartOfGuardRegion()==i && etb.getTypeOfGuardRegion().equals("try")) {
                                    boolean Present=alreadyPresent(Al,etb);
                                    if(Present==false) {
                                        temp+="\ntry\n{\n";
                                        Al.add(etb);
                                    }

                                }
                            }
                        }



                        break;
                    }
                    if(table.getTypeOfGuardRegion().equals("catch")) {
                        // Need to Get Exception Name
                        java.lang.String expName="";
                        for(int c2=0;c2<alltables.size();c2++) {
                            ExceptionTable temptable=(ExceptionTable)alltables.get(c2);
                            if(temptable.getEndOfGuardRegion()==i) {
                                expName=temptable.getExceptionName();
                                expName.replace('/','.');
                                LocalVariableStructure structure=this.behaviour.getLocalVariables();
                                int indexPos=-1;
                                boolean simple=true;
                                switch(instruction)
                                {
                                    case JvmOpCodes.ASTORE:
                                        indexPos=this.behaviour.getCode()[i+1];
                                        simple=false;
                                        break;
                                    case JvmOpCodes.ASTORE_0:
                                        indexPos=0;
                                        break;
                                    case JvmOpCodes.ASTORE_1:
                                        indexPos=1;
                                        break;
                                    case JvmOpCodes.ASTORE_2:
                                        indexPos=2;
                                        break;
                                    case JvmOpCodes.ASTORE_3:
                                        indexPos=3;
                                        break;
                                        // TODO: Later check for wide instruction with  astore.
                                        // Need to Handle it.

                                }

                                LocalVariable expVar=getLocalVariable(indexPos,"store","java.lang.Object",simple,i);     //
                                java.lang.String expVarName="";
                                if(expVar!=null)
                                    expVarName=expVar.getVarName();
                                if(expVar==null)
                                {
                                    // TODO handle Later
                                    expVarName="exception";
                                }
                                temp+="\ncatch("+expName.replace('/','.')+" "+ expVarName+")\n{\n";
                                break;
                            }
                        }
                        temp+="";
                        break;

                    }
                }
                if(table.getEndOfGuardRegion()==i) {
                    // Apply Rule For Try
                    // Purpose of this rule is to check whether
                    // i is actually the end for this try or not
                    int startOfGuard=table.getStartOfGuardRegion();
                    boolean alreadyEnded=false;
                    if(table.getTypeOfGuardRegion().equals("try")) {
                        ArrayList excepTables=this.behaviour.getExceptionTableList();;
                        for(int Start=0;Start<excepTables.size();Start++) {
                            ExceptionTable tab=(ExceptionTable)excepTables.get(Start);
                            if(tab.getStartOfGuardRegion()==startOfGuard) {
                                int endOfGuard=tab.getEndOfGuardRegion();
                                int beginofHandler=tab.getStartOfHandlerForGuardRegion();
                                if(i > endOfGuard && i < beginofHandler) {
                                    alreadyEnded=true;
                                    break;
                                }
                            }
                        }
                    }
                    boolean addGE=addGuardEnd(i);
                    boolean b1=isHandlerEndPresentAtGuardEnd(i);
                    if(alreadyEnded==false && (table.getTypeOfGuardRegion().equals("")==false) && addGE && !b1)
                    {
                        temp+="}";// some try catch end
                        guardEnds.put(new Integer(i),"true");
                    }
                    else
                        temp+="";
//					Check for overlap
                    ArrayList excepTables=this.behaviour.getExceptionTableList();;
                    for(int Start=0;Start<excepTables.size();Start++) {
                        ExceptionTable tab=(ExceptionTable)excepTables.get(Start);
                        if(tab.getStartOfHandlerForGuardRegion()==i && tab.getExceptionName().equals("<any>")==false) {
                            // Added code for generating excepion Name
                            LocalVariableStructure structure=this.behaviour.getLocalVariables();
                            int indexPos=-1;
                            boolean simple=true;
                            switch(instruction)
                            {
                                case JvmOpCodes.ASTORE:
                                    indexPos=this.behaviour.getCode()[i+1];
                                    simple=false;
                                    break;
                                case JvmOpCodes.ASTORE_0:
                                    indexPos=0;
                                    break;
                                case JvmOpCodes.ASTORE_1:
                                    indexPos=1;
                                    break;
                                case JvmOpCodes.ASTORE_2:
                                    indexPos=2;
                                    break;
                                case JvmOpCodes.ASTORE_3:
                                    indexPos=3;
                                    break;
                                    // TODO: Later check for wide instruction with  astore.
                                    // Need to Handle it.

                            }

                            LocalVariable expVar=getLocalVariable(indexPos,"store","java.lang.Object",simple,i);
                            java.lang.String expName="";
                            if(expVar!=null)
                                expName=expVar.getVarName();
                            if(expVar==null)
                            {
                                // TODO
                                expName="exception";
                            }
                            temp+="\ncatch("+tab.getExceptionName().replace('/','.')+"  "+expName+")\n{\n";
                            break;
                        }
                        if(tab.getStartOfHandlerForGuardRegion()==i && tab.getExceptionName().equals("<any>")==true) {
                            temp+="\nfinally\n{\n";
                            break;
                        }
                    }

                    // Check from newly created Table list
                    ArrayList newexcepTables=this.behaviour.getCreatedTableList();
                    if(newexcepTables!=null) {
                        for(int Start=0;Start<newexcepTables.size();Start++) {
                            ExceptionTable tab=(ExceptionTable)newexcepTables.get(Start);
                            boolean addG=addGuardEnd(i);
                            boolean z=isHandlerEndPresentAtGuardEnd(i);
                            if(tab.getEndOfGuardRegion()==i && addG && !z) {
                                temp+="}\n";
                                guardEnds.put(new Integer(i),"true");
                                break;
                            }
                        }
                    }



                    //	System.out.println();
                    break;
                }

                if(table.getStartOfHandlerForGuardRegion()==i) {

                    /* Iterator itz=behaviour.getExceptionTableList().iterator();
                    while(itz.hasNext()) {
                    ExceptionTable etb=(ExceptionTable)itz.next();
                    if(etb.getEndOfHandlerForGuardRegion()==i)	    			 {
                    //  temp+="\n}// end of handler2\n";

                    }
                    } */


                    if(table.getTypeOfHandlerForGuardRegion().equals("FinallyBlock")) {
                        temp+="finally\n{\n";
                        break;
                    }
                    if(table.getTypeOfHandlerForGuardRegion().equals("CatchBlock")) {
                        LocalVariableStructure structure=this.behaviour.getLocalVariables();
                        int indexPos=-1;
                        int blockIndexValue=-1;
                        boolean simple=true;
                        switch(instruction) {
                            case JvmOpCodes.ASTORE:
                                indexPos=this.behaviour.getCode()[i+1];
                                blockIndexValue=i+2;
                                simple=false;
                                break;
                            case JvmOpCodes.ASTORE_0:
                                indexPos=0;
                                blockIndexValue=i+1;
                                break;
                            case JvmOpCodes.ASTORE_1:
                                blockIndexValue=i+1;
                                indexPos=1;
                                break;
                            case JvmOpCodes.ASTORE_2:
                                blockIndexValue=i+1;
                                indexPos=2;
                                break;
                            case JvmOpCodes.ASTORE_3:
                                blockIndexValue=i+1;
                                indexPos=3;
                                break;
                                // TODO: Later check for wide instruction with  astore.
                                // Need to Handle it.

                        }

                        LocalVariable expVar=getLocalVariable(indexPos,"store","java.lang.Object",simple,i);
                        java.lang.String expName="";
                        if(expVar!=null)
                            expName=expVar.getVarName();
                        else
                            expName="exception";
                        boolean add=addHandlerStart(i);
                        if(add){
                            temp+="catch("+table.getExceptionName().replace('/','.')+"  "+expName+")\n{\n";
                            handlerStarts.put(new Integer(i),"true");
                        }
                        break;

                    }
                }
                if(table.getEndOfHandlerForGuardRegion()==i) {

                    boolean b=addhandlerEnd(i,table);
                    if(b){
                        addedHandlerEnds.add(new Integer(i));
                        temp+="\n}\n";
                        int s=table.getStartOfHandlerForGuardRegion();
                        int e=table.getEndOfHandlerForGuardRegion();
                        java.lang.String t=table.getTypeOfHandlerForGuardRegion();
                        handlertracker.put(new Integer(i),new handlerEndTracker(s,e,t));
                        //  guardEnds.put(new Integer(i),"true");
                    }
                    Iterator tempIterator=null;

                    // Check for End of Guard
                    // TODO: Get the example for This...
                    tempIterator=behaviour.getExceptionTableList().iterator();
                    while(tempIterator.hasNext()) {
                        ExceptionTable etb=(ExceptionTable)tempIterator.next();
                        boolean addG=addGuardEnd(i);
                        boolean z=isHandlerEndPresentAtGuardEnd(i);
                        if(etb.getEndOfGuardRegion()==i && addG && !z)
                        {
                            temp+="\n}";
                            guardEnds.put(new Integer(i),"true");
                            break;
                        }
                    }


                    // Check for overlap with Created Table List
                    ArrayList newList=behaviour.getCreatedTableList();
                    if(newList!=null) {
                        tempIterator=newList.iterator();
                        while(tempIterator.hasNext()) {
                            ExceptionTable etb=(ExceptionTable)tempIterator.next();
                            boolean addG=addGuardEnd(i);
                            boolean z=isHandlerEndPresentAtGuardEnd(i);
                            if(etb.getEndOfGuardRegion()==i && addG && !z) {
                                temp+="}\n";
                                guardEnds.put(new Integer(i),"true");
                                break;
                            }
                        }
                    }

                    // Check here for overlap condition
                    tempIterator=behaviour.getExceptionTableList().iterator();
                    while(tempIterator.hasNext()) {
                        ExceptionTable etb=(ExceptionTable)tempIterator.next();
                        if(etb.getStartOfHandlerForGuardRegion()==i && etb.getExceptionName().equals("<any>")==false) {
                            java.lang.String ename=etb.getExceptionName();
                            ename=ename.replace('/','.');
                            LocalVariableStructure structure=this.behaviour.getLocalVariables();
                            int indexPos=-1;
                            int blockIndexValue=-1;
                            boolean simple=true;
                            switch(instruction)
                            {
                                case JvmOpCodes.ASTORE:
                                    indexPos=this.behaviour.getCode()[i+1];
                                    blockIndexValue=i+2;
                                    simple=false;
                                    break;
                                case JvmOpCodes.ASTORE_0:
                                    blockIndexValue=i+1;
                                    indexPos=0;
                                    break;
                                case JvmOpCodes.ASTORE_1:
                                    blockIndexValue=i+1;
                                    indexPos=1;
                                    break;
                                case JvmOpCodes.ASTORE_2:
                                    blockIndexValue=i+1;
                                    indexPos=2;
                                    break;
                                case JvmOpCodes.ASTORE_3:
                                    blockIndexValue=i+1;
                                    indexPos=3;
                                    break;
                                    // TODO: Later check for wide instruction with  astore.
                                    // Need to Handle it.

                            }

                            LocalVariable expVar=getLocalVariable(indexPos,"store","java.lang.Object",simple,i);
                            java.lang.String expName="";
                            if(expVar!=null)
                                expName=expVar.getVarName();
                            if(expVar==null)
                            {
                                // TODO handle Later
                                expName="exception";
                            }
                            boolean add=addHandlerStart(i);
                            if(add){
                                temp+="catch("+ename.replace('/','.')+" "+ expName+")\n{\n";
                                handlerStarts.put(new Integer(i),"true");
                            }
                            break;
                        }
                    }

                    // break;
                }



            }

        }
        // Check For End of Catch For Last Catch
        // Note Handled Here because This there is no entry in exception table list for The end of last catch
        // as there is no finally for try

        //if(temp.length()==0)  // Removing if because this was preventing last catch's end of guard of to be printed
        // As that info was only held in a CatchBlock Object
        //{
        ArrayList allTryBlks=getAllTriesForBehaviour();  // All try blocks for method
        if(allTryBlks!=null) {
            Iterator it=allTryBlks.iterator();
            while(it.hasNext()) {
                TryBlock tryblock=(TryBlock)it.next(); // Get Each Try
                if(tryblock.hasFinallyBlk()==false)  // Go in only if there is no finally for this try
                {
                    ArrayList allcatches=tryblock.getAllCatchesForThisTry();  // Get catches for this try
                    if(allcatches.size()>0)    // Try may not have any catches
                    {
                        CatchBlock LastCatch=(CatchBlock)allcatches.get(allcatches.size()-1);
                        int endOfCatch=LastCatch.getEnd(); // Get end of last catch block for this try
                        if(endOfCatch==i && LastCatch.isUsedForDeterminingTheEndOfLastCatch())  // compare with i
                        {
                            temp+="}\n"; // end Catch and break
                            break;
                        }
                    }
                }

            }

        }


        return temp;

    }

    private ArrayList getAllTriesForBehaviour() {
        ArrayList alltries= behaviour.getAllTriesForMethod();
        int size=alltries.size();
        if(size==0)
            return null;
        else
            return alltries;
    }


    private boolean skipCurrentIteraton(int i,boolean includeEndOfGuard,byte[] info) {
        boolean skip=false;
        boolean exit=false;
        ArrayList tries=this.behaviour.getAllTriesForMethod();
        Iterator triesIT=tries.iterator();
        while(triesIT.hasNext()) {
            TryBlock TRY=(TryBlock)triesIT.next();
            ArrayList catches=TRY.getAllCatchesForThisTry();
            Iterator catchesIT=catches.iterator();
            TryCatchFinally prevBlock=TRY;
            while(catchesIT.hasNext()) {
                CatchBlock Catch=(CatchBlock)catchesIT.next();
                TryCatchFinally curBlock=Catch;
                if(includeEndOfGuard)
                {
                    if(i >= prevBlock.getEnd() && i < curBlock.getStart()) {
                        skip=true;
                        boolean retStmt=checkForReturn(info,i);
                        if(retStmt==true)skip=false;
                        exit=true;
                        break;
                    }
                }
                if(!includeEndOfGuard && i > prevBlock.getEnd() && i < curBlock.getStart() )
                {
                    skip=true;
                    boolean retStmt=checkForReturn(info,i);
                    if(retStmt==true)skip=false;
                    exit=true;
                    break;
                }
                prevBlock=curBlock;
            }
            if(exit)break;
            // Check for Finally Block
            FinallyBlock Finally=TRY.getFinallyBlock();
            if(Finally!=null) {
                CatchBlock catchblock=TRY.getLastCatchBlock();
                if(catchblock!=null)
                {
                    if(!includeEndOfGuard && i > catchblock.getEnd() && i< Finally.getStart())
                    {
                        boolean retStmt=checkForReturn(info,i);
                        if(retStmt==true){
                            skip=false;
                        }
                        else{
                            skip=true;
                        }
                    }
                    if(includeEndOfGuard && i >= catchblock.getEnd() && i< Finally.getStart())
                    {
                        boolean retStmt=checkForReturn(info,i);
                        if(retStmt==true){
                            skip=false;
                        }
                        else{
                            skip=true;
                        }
                    }
                }
                else
                {
                    if(!includeEndOfGuard && i > TRY.getEnd() && i< Finally.getStart())
                    {


                        boolean retStmt=checkForReturn(info,i);
                        if(retStmt==true){
                            skip=false;
                        }
                        else{
                            skip=true;
                        }
                    }
                    if(includeEndOfGuard && i >= TRY.getEnd() && i< Finally.getStart())
                    {
                        boolean retStmt=checkForReturn(info,i);
                        if(retStmt==true){
                            skip=false;
                        }
                        else{
                            skip=true;
                        }
                    }
                }

            }
        }

       /* if(!skip && behaviour.getSynchronizedEntries()!=null && behaviour.getSynchronizedEntries().size() > 0)
        {
            ArrayList synchEntries=behaviour.getSynchronizedEntries();
            for(int s=0;s<synchEntries.size();s++)
            {
                ExceptionTable synchTab=(ExceptionTable)synchEntries.get(s);
                int endPC=synchTab.getEndPC();
                int athrowpos=getNextAthrowPos(endPC,info);
                if(athrowpos!=-1)
                {
                    if(i >=endPC && i<= athrowpos)
                    {
                        skip=true;
                        break;
                    }
                }
            }

        }*/

        if(synchSkips!=null && synchSkips.size() > 0)
        {

            Iterator it=synchSkips.entrySet().iterator();
            while(it.hasNext())
            {

                Map.Entry e=(Map.Entry)it.next();
                Integer st=(Integer)e.getKey();
                Integer en=(Integer)e.getValue();
                if(i >=st.intValue() && i<= en.intValue())
                {
                    skip=true;
                    break;
                }

            }

        }

        if(skip==false)
        {

           ArrayList tableList=Util.getAllTablesWithFinallyAsHandler(behaviour.getExceptionTableList());
           tableList=Util.getTablesSortedByGuardStart(tableList);
           if(tableList!=null)
           {
               for(int z=0;z<tableList.size();z++)
               {

                   ExceptionTable table1=(ExceptionTable)tableList.get(z);
                   int next=z+1;
                   if(next < tableList.size())
                   {
                       ExceptionTable table2=(ExceptionTable)tableList.get(next);
                       int epc=table1.getEndPC();
                       int spc=table2.getStartPC();
                       if(i >= epc && i < spc)
                       {
                          boolean sometrystart=isThisTryStart(i);
                           if(!sometrystart)
                           {
                                  skip=true;
                                  break;
                           }
                       }

                   }
                   else
                       break;
               }
           }

        }


        return skip;

    }
    private boolean alreadyPresent(ArrayList AL,ExceptionTable etb) {
        boolean present=false;
        for(int jj=0;jj<AL.size();jj++) {
            ExceptionTable t=(ExceptionTable)AL.get(jj);
            int start=t.getStartOfGuardRegion();
            int end=t.getEndOfGuardRegion();
            if(start==etb.getStartOfGuardRegion() && end==etb.getEndOfGuardRegion()) {
                present=true;
                break;
            }
            else
                present=false;
        }
        if(present==false)
        {

            for(int jj=0;jj<AL.size();jj++)
            {
                ExceptionTable t=(ExceptionTable)AL.get(jj);
                int start=t.getStartOfGuardRegion();
                int endPC=t.getEndPC();
                int handlerPC=t.getStartOfHandler();
                if(start==etb.getStartPC())
                {
                    int etbendPC=etb.getEndPC();
                    if(etbendPC > endPC && etbendPC < handlerPC)
                    {
                        //present=true;                                     // NOTE: commented on 16 oct by belurs
                        // As it was preventing a try from appearing

                        // TODO: Continuous thoruough testing of try/catch/finally
                        break;												// Necessary
                    }

                }
            }


        }
        return present;


    }
    // Copied from LocalVariable Class
    private java.lang.String parse(java.lang.String input) {
        java.lang.String type="";
        if(input.equals("I")) {
            type="int";
        } else if(input.equals("B")) {
            type="byte";
        } else if(input.equals("C")) {
            type="char";
        } else if(input.equals("S")) {
            type="short";
        } else if(input.equals("Z")) {
            type="boolean";
        } else if(input.equals("F")) {
            type="float";
        } else if(input.equals("D")) {
            type="double";
        } else if(input.equals("J")) {
            type="long";
        } else if(input.startsWith("L")) {
            type=input.substring(1);
            if(type.indexOf(";")!=-1)type=type.substring(0,type.indexOf(";"));
        } else if(input.startsWith("[")) {
            int lastBracket=input.lastIndexOf("[");
            int objectType=input.indexOf("L");
            java.lang.String className="";
            if(objectType!=-1)
                className=input.substring(objectType+1);
            else
                className=input.substring(lastBracket+1);
            if(className.indexOf(";")!=-1)
                className=className.substring(0,className.indexOf(";"));
            boolean b=AmIPrimitive(className);
            if(b==true)
                type=parse(className);
            else {
                java.lang.String  temp="";
                for(int c=0;c<lastBracket+1;c++)
                    temp+="[]";
                type=className+" "+temp;
            }


        }
        else
        {
            type=input;
        }

        return type;

    }
    private boolean AmIPrimitive(java.lang.String className) {
        if(className.equals("I") || className.equals("B") || className.equals("C") || className.equals("S") || className.equals("F") || className.equals("D") || className.equals("J") || className.equals("Z")) {
            return true;
        } else
            return false;


    }


    private java.lang.String pollSwitchBlksForMethod(int i)
    {

        java.lang.String stmt="";
        boolean processed=false;
        ArrayList allswitches=behaviour.getAllSwitchBlks();
        if(allswitches!=null)
        {

            for(int start=0;start<allswitches.size();start++)
            {
                Switch switchblk=(Switch)allswitches.get(start);
                ArrayList allcases=switchblk.getAllCases();
                allcases=sortCasesByStart(allcases);
                if(allcases.size() > 0)
                {
                    for(int c=0;c<allcases.size();c++)
                    {
                        Case caseblk=(Case)allcases.get(c);
                        if(caseblk.getCaseStart()==i)
                        {
                            java.lang.String temp="case "+caseblk.getCaseLabel()+":\n{\n";
                            processed=true;
                            stmt=Util.formatDecompiledStatement(temp);
                            return stmt;

                        }
                        if(caseblk.getCaseEnd()==i)
                        {

                            java.lang.String temp="";
                            if(caseblk.isFallsThru()==false && caseblk.isGotoAsEndForCase())
                                temp+="break;\n";
                            temp=Util.formatDecompiledStatement(temp);
                            java.lang.String tempString="\n}\n";
                            temp+=Util.formatDecompiledStatement(tempString);
                            ArrayList allcasesDUP=switchblk.getAllCases();
                            if(allcasesDUP.size() > 0)
                            {
                                for(int c2=0;c2<allcasesDUP.size();c2++)
                                {
                                    Case caseblkDUP=(Case)allcasesDUP.get(c2);
                                    if(caseblkDUP.getCaseStart()==i)
                                    {
                                        tempString="case "+caseblkDUP.getCaseLabel()+": \n{\n";
                                        temp+=Util.formatDecompiledStatement(tempString);
                                        stmt=temp;
                                        break;
                                    }


                                }
                            }

                            // Now check with default:
                            if(i==switchblk.getDefaultStart() && switchblk.defaultToBeDisplayed())
                            {
                                tempString="default:\n{\n";
                                temp+=Util.formatDecompiledStatement(tempString);
                            }
                            if(i==switchblk.getDefaultStart() && !switchblk.defaultToBeDisplayed())
                            {
                                tempString="\n}\n";
                                temp+=Util.formatDecompiledStatement(tempString);
                            }

                            return temp;
                        }
                    }
                    // check default

                }
                int defStart=switchblk.getDefaultStart();
                if(defStart==i && switchblk.defaultToBeDisplayed())
                {
                    stmt="default:\n{\n";
                    stmt=Util.formatDecompiledStatement(stmt);
                    return stmt;
                }
                int defEnd=switchblk.getDefaultEnd();
                if(defEnd==i)
                {
                    if(switchblk.defaultToBeDisplayed())
                    {
                        stmt=Util.formatDecompiledStatement("}// Some End");
                        stmt+=Util.formatDecompiledStatement("\n}// Some end\n");
                    }
                    else
                        stmt=Util.formatDecompiledStatement("}// Some End\n");
                    return stmt;
                }

            }

        }
        return stmt;
    }

    private boolean checkForStartOfCatch(int instructionPos,ArrayList methodTries)
    {
        boolean add=true;
        boolean returnFromMethod=false;
//		Figure is this is a start of catch here
        if(methodTries!=null)
        {
            for(int st=0;st<methodTries.size();st++)
            {
                TryBlock tryblk=(TryBlock)methodTries.get(st);
                FinallyBlock finblk=tryblk.getFinallyBlock();
                if(tryblk!=null)
                {
                    ArrayList allCatches=tryblk.getAllCatchesForThisTry();
                    if(allCatches!=null && allCatches.size() > 0)
                    {

                        for(int s1=0;s1<allCatches.size();s1++)
                        {

                            CatchBlock catchBlk=(CatchBlock)allCatches.get(s1);
                            if(catchBlk!=null)
                            {
                                int catchStart=catchBlk.getStart();
                                if(catchStart==instructionPos)
                                {
                                    add=false;
                                    returnFromMethod=true;
                                    break;
                                }
                            }
                        }
                        if(returnFromMethod)return add;
                    }
                    if(finblk!=null)
                    {
                        int finstart=finblk.getStart();
                        if(finstart==instructionPos)
                        {
                            return false;
                        }
                    }
                }

            }

        }


        return add;
    }
                         ArrayList list;

    private boolean isPrevInstructionAload(int pos,byte[] code)
    {
        boolean present=false;
        int pos1=pos-1;
        int pos2=pos-2;
        int jvmInst_1=code[pos1];
        int jvmInst_2=code[pos2];
        switch(jvmInst_1)
        {
            case JvmOpCodes.ALOAD_0:
            case JvmOpCodes.ALOAD_1:
            case JvmOpCodes.ALOAD_2:
            case JvmOpCodes.ALOAD_3:
                present=true;
                break;
            default:
                present=false;
                break;

        }
        if(present==false)
        {
            if(jvmInst_2==JvmOpCodes.ALOAD)
                present=true;
        }

        return present;
    }

    private boolean isPrevInstructionAload(int pos,byte[] code,StringBuffer sb)
    {
        boolean present=false;
        int pos1=pos-1;
        int pos2=pos-2;
        int jvmInst_1=-1;
        int jvmInst_2=-1;
        if(pos1 >= 0)
        jvmInst_1=code[pos1];
        if(pos2 >= 0)
       jvmInst_2=code[pos2];
        if(pos1!=-1)
        {
            switch(jvmInst_1)
            {

                case JvmOpCodes.ALOAD_0:
                case JvmOpCodes.ALOAD_1:
                case JvmOpCodes.ALOAD_2:
                case JvmOpCodes.ALOAD_3:
                    present=true;
                    break;
                default:
                    present=false;
                    break;

            }
        }
        if(present)sb.append(pos1);
        if(present==false)
        {

            if(jvmInst_2==JvmOpCodes.ALOAD)
            {
                present=true;
                sb.append(pos2);
            }

        }

        return present;
    }

    private boolean checkForReturn(byte[] code,int i) {
        boolean present=false;
        int jvmInst=code[i]  ;
        boolean b=isThisInstrStart(behaviour.getInstructionStartPositions(),i);
        if(b==false)return false;
        switch(jvmInst) {
            case JvmOpCodes.ARETURN:
            case JvmOpCodes.IRETURN:
            case JvmOpCodes.FRETURN:
            case JvmOpCodes.DRETURN:
            case JvmOpCodes.LRETURN:
            case JvmOpCodes.RETURN:
                present=true;
                break;
            default:  present=false;
                break;
        }
        return present;
    }


    private boolean checkForValueReturn(byte[] code,int i) {
        boolean present=false;
        int jvmInst=code[i]  ;
        switch(jvmInst) {
            case JvmOpCodes.ARETURN:
            case JvmOpCodes.IRETURN:
            case JvmOpCodes.FRETURN:
            case JvmOpCodes.DRETURN:
            case JvmOpCodes.LRETURN:
                present=true;
                break;
            default:  present=false;
                break;
        }
        return present;
    }




    private boolean isIEndOfGuard(int i,Behaviour behaviour)
    {
        boolean end=false;

        ArrayList alltries=behaviour.getAllTriesForMethod();
        Iterator it=alltries.iterator();
        while(it.hasNext())
        {

            TryBlock Try=(TryBlock)it.next();
            if(Try!=null)
            {
                int endoftry=Try.getEnd();
                if(endoftry==i)  // check for try's end
                {
                    end=true;
                    break;
                }
                else   // Check for catches
                {
                    ArrayList catches=Try.getAllCatchesForThisTry();
                    for(int s=0;s<catches.size();s++)
                    {

                        CatchBlock catchblk=(CatchBlock)catches.get(s);
                        if(catchblk!=null)
                        {
                            int catchend=catchblk.getEnd();
                            if(catchend==i)
                            {
                                end=true;
                                break;
                            }


                        }

                    }


                }


            }
        }


        return end;
    }


    private java.lang.String isAnyReturnPresentInSkipRegion(byte[] info,int i,Behaviour behaviour,StringBuffer stb)
    {
        boolean exit=false;
        java.lang.String ret="";
        ArrayList tries=this.behaviour.getAllTriesForMethod();
        Iterator triesIT=tries.iterator();
        while(triesIT.hasNext()) {
            TryBlock TRY=(TryBlock)triesIT.next();
            ArrayList catches=TRY.getAllCatchesForThisTry();
            Iterator catchesIT=catches.iterator();
            TryCatchFinally prevBlock=TRY;
            while(catchesIT.hasNext()) {
                CatchBlock Catch=(CatchBlock)catchesIT.next();
                TryCatchFinally curBlock=Catch;

                if(i >= prevBlock.getEnd() && i < curBlock.getStart() )
                {

                    java.lang.String retStmt=getReturnInstinRange(info,i,curBlock.getStart(),stb);
                    ret=retStmt;
                    exit=true;
                    break;
                }
                prevBlock=curBlock;
            }
            if(exit)break;
            // Check for Finally Block
            if(!exit)
            {
                FinallyBlock Finally=TRY.getFinallyBlock();
                if(Finally!=null) {
                    CatchBlock catchblock=TRY.getLastCatchBlock();
                    if(catchblock!=null)
                    {

                        if(i >= catchblock.getEnd() && i< Finally.getStart())
                        {
                            java.lang.String retStmt=getReturnInstinRange(info,i,Finally.getStart(),stb);
                            ret=retStmt;
                            break;
                        }
                    }
                    else
                    {
                        if(i >= TRY.getEnd() && i< Finally.getStart())
                        {
                            java.lang.String retStmt=getReturnInstinRange(info,i,Finally.getStart(),stb);
                            ret=retStmt;
                            break;
                        }
                    }

                }
            }

            if(ret.length()==0)
            {

                if(i == TRY.getEnd())
                {
                    java.lang.String retStmt=getReturnInstinRange(info,i,i+1,stb);
                    ret=retStmt;
                    break;
                }


            }
        }








        return ret;
    }


    private java.lang.String getReturnInstinRange(byte[] info,int i,int blockstart,StringBuffer sb)
    {

        for(int s=i;s<blockstart;s++)
        {
            int jvmInst=info[s];
            switch(jvmInst)
            {
                case JvmOpCodes.IRETURN:
                    sb.append(s);
                    return "ireturn";
                case JvmOpCodes.LRETURN:
                    sb.append(s);
                    return "lreturn";
                case JvmOpCodes.FRETURN:
                    sb.append(s);
                    return "freturn";
                case JvmOpCodes.DRETURN:
                    sb.append(s);
                    return "dreturn";
                case JvmOpCodes.ARETURN:
                    sb.append(s);
                    return "areturn";
                case JvmOpCodes.RETURN:
                    sb.append(s);
                    return "return";
                default:
                    continue;

            }

        }
        sb.append("-1");
        return null;

    }

    private int getReturnStringPosInCode(byte[] info,int i,Behaviour behaviour)
    {
        int pos=-1;
        boolean exit=false;

        ArrayList tries=this.behaviour.getAllTriesForMethod();
        Iterator triesIT=tries.iterator();
        while(triesIT.hasNext()) {
            TryBlock TRY=(TryBlock)triesIT.next();
            ArrayList catches=TRY.getAllCatchesForThisTry();
            Iterator catchesIT=catches.iterator();
            TryCatchFinally prevBlock=TRY;
            while(catchesIT.hasNext()) {
                CatchBlock Catch=(CatchBlock)catchesIT.next();
                TryCatchFinally curBlock=Catch;

                if(i >= prevBlock.getEnd() && i < curBlock.getStart() )
                {

                    pos=getReturnInstPosInRange(info,i,curBlock.getStart());
                    exit=true;
                    break;
                }
                prevBlock=curBlock;
            }
            if(exit)break;
            // Check for Finally Block
            if(!exit)
            {
                FinallyBlock Finally=TRY.getFinallyBlock();
                if(Finally!=null) {
                    CatchBlock catchblock=TRY.getLastCatchBlock();
                    if(catchblock!=null)
                    {

                        if(i >= catchblock.getEnd() && i< Finally.getStart())
                        {

                            pos=getReturnInstPosInRange(info,i,Finally.getStart());
                            break;
                        }
                    }
                    else
                    {
                        if(i >= TRY.getEnd() && i< Finally.getStart())
                        {
                            pos=getReturnInstPosInRange(info,i,Finally.getStart());

                            break;
                        }
                    }

                }
            }
             if(i == TRY.getEnd())
                        {
                            pos=getReturnInstPosInRange(info,i,i+1);

                            break;
                        }
        }  //Shutdown d;
        return pos;
    }

    private int getReturnInstPosInRange(byte[] info,int i,int blockstart)
    {

        for(int s=i;s<blockstart;s++)
        {
            int jvmInst=info[s];
            switch(jvmInst)
            {
                case JvmOpCodes.IRETURN:
                case JvmOpCodes.LRETURN:
                case JvmOpCodes.FRETURN:
                case JvmOpCodes.DRETURN:
                case JvmOpCodes.ARETURN:
                case JvmOpCodes.RETURN:
                    return s;
                default:
                    continue;

            }

        }

        return -1;

    }

    private int getNextAthrowPos(int endPC,byte[] info)
    {
        int pos=-1;
        for(int s=endPC;s<info.length;s++)
        {
            if(info[s]==JvmOpCodes.ATHROW)
            {
                pos=s;
                break;
            }
        }

        return pos;
    }

    private int findElseCloseLineNumber(int codeIndex, int gotoIndex, byte[] srcArray)
    {
        int retCodeIndex = -1;

        for(int srcArrayIndex=codeIndex;srcArrayIndex<srcArray.length;srcArrayIndex++)
        {
            if(srcArray[srcArrayIndex] == JvmOpCodes.GOTO)
            {
                int nextGotoIndex = getJumpAddress(srcArray,(srcArrayIndex));//(srcArray[++srcArrayIndex] << 8) | srcArray[++srcArrayIndex]) + (srcArrayIndex - 2);

                if(nextGotoIndex == gotoIndex)
                {
                    retCodeIndex = srcArrayIndex - 2;
                    break;
                }
            }
        }
        return retCodeIndex;
    }

    private int findCodeIndexFromInfiniteLoop(IFBlock ifst,ArrayList LoopTable, int codeIndex)
    {
        Iterator iterInfLoop = LoopTable.iterator();
        int start=ifst.getIfStart();
        int loopstarts[]=new int[LoopTable.size()];
        int j=0;
        boolean ok=false;
        while(iterInfLoop.hasNext())
        {
            Loop iloop = (Loop)iterInfLoop.next();
            int lstart=iloop.getStartIndex();
            loopstarts[j]=lstart;
            j++;
            ok=true;
            /*if(iloop.getStartIndex() == codeIndex)
            {
                return iloop.getEndIndex();
            } */
        }
        if(ok)
        {
             for(int z=loopstarts.length-1;z>=0;z--)
             {

                 if(loopstarts[z] < start)
                 {
                    int end=getloopEndForStart(LoopTable,loopstarts[z]);
                    if(end < start)return -1;
                    return end;
                 }


             }
        }

        return -1;
    }

    private boolean isIndexEndOfLoop(ArrayList list,int s)
    {
        boolean ok=false;
        for(int st=0;st<list.size();st++)
        {
            if(((Loop)list.get(st)).getEndIndex()==s)
                return true;
        }
        return ok;
    }


    private int getLoopStartForEnd(int s,ArrayList list)
    {
        for(int k=0;k<list.size();k++)
        {
            Loop l=(Loop)list.get(k);
            if(l.getEndIndex()==s)
                return l.getStartIndex();
        }
        return -1;
    }

    private boolean getIfinst(int start,byte[] info,int tillWhere)
    {
        boolean ok=false;
        ArrayList list=behaviour.getInstructionStartPositions();
        for(int k=start;k<tillWhere;k++)
        {
            int current=info[k];
            ok=isNextInstructionIf(current);
            if(ok && isThisInstrStart(list,k))return ok;
        }
        return ok;
    }

    private Collection curentIFS;
    private Collection getCurrentIFStructues()
    {
        if(ifHashTable!=null && ifHashTable.size()>0){
            curentIFS=ifHashTable.values();
            return curentIFS;
        }
        else
            return null;
    }

    private Object[] sortIFStructures()
    {
        if(curentIFS!=null && curentIFS.size() > 0){
            Object o[]=curentIFS.toArray();
            Arrays.sort(o);
            return o;
        }
        else
            return null;
    }


    private IFBlock getParentBlock(Object o[],int startOfIf)
    {

        IFBlock parent=null;
        int reqdPos=-1;
        for(int s=0;s<o.length;s++)
        {
            if(o[s] instanceof IFBlock)
            {
                IFBlock IF=(IFBlock)o[s];
                if(IF.getIfStart()==startOfIf)
                {
                    if(s > 0)
                    {
                        reqdPos=s-1;
                        return (IFBlock)o[reqdPos];
                    }
                }

            }
            else
            {
                return null;
            }
        }
        return null;
    }



    /***
     *
     * @param parent  Immediate above IF instruction in code...MAY OR MAY NOT BE PARENT
     * @param ifstart For the If Block for which we are checking end of else
     * @param elseClose The current end of else...for which we are checking
     * @return
     */


    private int checkElseCloseLineNumber(Object ifsorted[], IFBlock parent, IFBlock currentIF,int ifstart,int elseClose,java.lang.StringBuffer needToCheck)
    {
        int elseToReturn=-1;
        int parentIFStart=parent.getIfStart();
        int parentIFEnd=parent.getIfCloseLineNumber();
        boolean doesParentHaveElse=parent.isHasElse();
        boolean needToFindSuperParent=false;
        int curifend=currentIF.getIfCloseLineNumber();

        // Check 1: Check whether currentIF lies withing the if of parent
        if(ifstart < parentIFEnd)  // Yes it lies
        {
            if(elseClose > parentIFEnd)
            {
                elseToReturn=parentIFEnd;
                return elseToReturn;
            }
            else if(elseClose < curifend)
            {
                elseToReturn=parentIFEnd;
                return elseToReturn;
            }
            else
            {
                elseToReturn=elseClose;   // Passes else was correct
                return elseToReturn;
            }

        }
        if(ifstart > parentIFEnd)
        {
            if(doesParentHaveElse)
            {
                int parentElseEnd=parent.getElseCloseLineNumber();
                if(currentIF.getIfCloseLineNumber() < parentElseEnd)  // Lies within the else of parent IF
                {
                    if(elseClose > parentElseEnd)
                    {
                        elseToReturn=parentElseEnd;
                        return elseToReturn;
                    }
                     else if(elseClose < curifend)
                    {
                        elseToReturn=parentElseEnd;
                        return elseToReturn;
                    }
                    else
                    {
                        elseToReturn=elseClose;   // Passes else was correct
                        return elseToReturn;
                    }
                }
                else
                {
                    needToFindSuperParent=true;
                }

            }
            else
            {
                needToFindSuperParent=true;
            }


        }
        if(needToFindSuperParent)
        {
            IFBlock superParent=getParentBlock(ifsorted,parentIFStart);
            int tmp;
            if(superParent!=null)
            {
                StringBuffer t=new StringBuffer("false");
                tmp=checkElseCloseLineNumber(ifsorted,superParent,currentIF,ifstart,elseClose,t);
            }
            else
            {
                if(behaviour.getBehaviourLoops().size() == 0)
                    tmp=elseClose;
                else
                {
                    tmp=elseClose;
                    needToCheck.append("true");
                }

            }
            return tmp;
        }
        else
        {
            return elseClose;  // Should Never come here
        }



    }






    private Loop thisLoop=null;
    private boolean isBeyondLoop(int ifjump,ArrayList list,byte[] info)
    {
        boolean b=false;
        int temp=ifjump-3;
        Loop l;

        if(temp >=0 && info[temp]==JvmOpCodes.GOTO)
        {

            int jmp=getJumpAddress(info,temp);
            boolean end;
            end=isIndexEndOfLoop(list,temp);
            if(end)
            {
                b=true;
                l=getThisLoop(list,temp);
                thisLoop=l;
            }
            /*if(!b)
            {
                int tmp2=jmp-3;
                end=isIndexEndOfLoop(list,tmp2);
                if(end)
                {
                    b=true;

                    l=getThisLoop(list,tmp2);
                    thisLoop=l;
                }
            } */

        }
        return b;


    }


    // Primarily use it for goto and some special cases where appplicable
    private int getJumpAddress(byte []info,int counter)
    {


        int b1=info[++counter];
        int b2=info[++counter];
        int z;
        if(b1 < 0)b1=(256+b1);
        if(b2 < 0)b2=(256+b2);

        int indexInst = ((((b1 << 8) | b2)) + (counter - 2));
        if(indexInst > 65535)
            indexInst=indexInst-65536;
        if(indexInst < 0)indexInst=256+indexInst;
        return indexInst;
    }


    private int getOffset(byte []info,int counter)
    {


        int b1=info[++counter];
        int b2=info[++counter];
        int z;
        if(b1 < 0)b1=(256+b1);
        if(b2 < 0)b2=(256+b2);

        int indexInst = (((b1 << 8) | b2));
        if(indexInst > 65535)
            indexInst=indexInst-65536;
        if(indexInst < 0)indexInst=256+indexInst;
        return indexInst;
    }



    private int checkIfElseCloseNumber(int end, IFBlock ifs)
    {
        int ifend=-1;
        this.getCurrentIFStructues();
        Object ifsSorted[]=sortIFStructures();
        IFBlock parent=getParentBlock(ifsSorted,ifs.getIfStart());
        if(parent==null)
        {


            int temp=checkLoopsAndSwitchForIfEnd(end,ifs,behaviour);
            if(temp!=-1)ifend=temp;
            else ifend=end;
        }
        else
        {
            ifend=reEvaluateIFStart(ifs,ifsSorted,parent,end);
        }
        return ifend;
    }

    private int reEvaluateIFStart(IFBlock ifs,Object[] ifsSorted, IFBlock parent,int currentEnd)
    {
        int ifend=-1;
        int parentEnd=parent.getIfCloseLineNumber();
        int thisStart=ifs.getIfStart();
        if(thisStart < parentEnd)
        {
            if(currentEnd > parentEnd)
            {
                // belurs:
                // Test case BigInteger.class
                /***
                 *  BasicallY this part of the code is not bug free.
                 *  It assumes that if the parentIF is ending before
                 *  this if end is ending then this ifend is wrong.
                 *  Well, it worked for every case until BigInteger
                 *  ws decompiled. So basically resetting parent
                 *  if end to this if end if parent if end was not
                 *  a GOTO instruction.
                 */
                // Test for The above case
                if((behaviour.getCode()[parentEnd]!=JvmOpCodes.GOTO) && (behaviour.getCode()[parentEnd]!=JvmOpCodes.GOTO_W))
                {
                   ifend=currentEnd;
                   parent.setIfCloseLineNumber(ifend);
                }
                else
                {

                    ifend=parentEnd;
                }
            }
            else if(currentEnd < thisStart)
            {
                ifend=parentEnd;
            }
            else
                ifend=currentEnd;

        }  // Need to handle thisif end inside parent's else end
      /*  else  if(thisStart > parentEnd)
        {
            boolean doesParentHaveElse=parent.isHasElse();
            if(doesParentHaveElse)
            {
                int parentElseEnd=parent.getElseCloseLineNumber();
                if(thisStart < parentElseEnd)
                {
                    // Within parent else block
                    if(currentEnd > parentElseEnd)
                    {
                         ifend=parentElseEnd;
                    }
                    else if(currentEnd < thisStart)
                    {
                        ifend=parentElseEnd;
                    }
                    else
                    {
                        ifend=currentEnd;
                    }

                    BigInteger b;
                }
            }
        } */
        else
        {

            IFBlock superparent=getParentBlock(ifsSorted,parent.getIfStart());
            if(superparent==null)
            {
                int temp=checkLoopsAndSwitchForIfEnd(currentEnd,ifs,behaviour);
                if(temp!=-1)ifend=temp;
                else ifend=currentEnd;

            }
            else
            {
                int tmp=reEvaluateIFStart(ifs,ifsSorted,superparent,currentEnd);
                ifend=tmp;
            }

        }


        return ifend;

    }


    private java.lang.String getBranchType(int if_else_begin,int GotoStart,byte[] info,ArrayList loops,StringBuffer sb,boolean b)
    {
        java.lang.String lbl="";
        boolean done=false;
        int immGotoJmp=getJumpAddress(info,GotoStart);
        int i=immGotoJmp;
        boolean end=isIndexEndOfLoop(loops,GotoStart);
        int lend=-1;
        if(end)lend=GotoStart;



        if(!end)
        {
            StringBuffer S=new StringBuffer("");
            end=checkForMatchingLoopAgain(loops,immGotoJmp,S);
            if(end)lend=Integer.parseInt(S.toString());
        }
        if(end)
        {
            int start=getLoopStartForEnd(lend,loops);
            if(start > if_else_begin){return lbl;}
            Object []sortedLoops=sortLoops(loops);
            int parentLoopStart=getParentLoopStartForIf(sortedLoops,if_else_begin);
            if(parentLoopStart==start)
            {
                java.lang.String s=setJdecLabelForContinue(parentLoopStart,lend) ;
                sb.append(s);
                lbl="continue";
                continue_JumpOffsets.add(new Integer(immGotoJmp));

            }
            else
            {
                int nextstart=getNextLoopStart(start);
                if(nextstart!=-1)
                {
                    int loopend=getloopEndForStart(loops,nextstart);
                    if(loopend!=-1)
                    {
                     if(if_else_begin >  loopend && nextstart < GotoStart)
                     {
                          sb.append("jdecLABEL"+start);
                         lbl="continue";
                         done=true;
                         LABELS.put(new Integer(start),"jdecLABEL"+start);
                     }
                    }
                    if(done==false && (nextstart < GotoStart))
                    {
                    break_JumpOffsets.add(new Integer(nextstart) );
                    sb.append("jdecLABEL"+nextstart);
                    LABELS.put(new Integer(nextstart),"jdecLABEL"+nextstart);
                         lbl="break";
                    }
                }


            }

        }
        else
        {
            lbl="";

        }
        return lbl;
    }


    private java.lang.String   getBranchType(int if_else_begin,int GotoStart,byte[] info,ArrayList loops,StringBuffer sb)
    {
       boolean over=false;
        java.lang.String lbl="";
        int immGotoJmp=getJumpAddress(info,GotoStart);
        int i=immGotoJmp;
        boolean cont=continueFindingBranchType(immGotoJmp,info);


        // check whether immGotoJmp is loop start
        int loop_end=getloopEndForStart(loops,immGotoJmp);
        boolean done=false;
        if(loop_end!=-1)
        {
            // Some loop is present

            Object []sortedLoops=sortLoops(loops);
            int parentLoopStart=getParentLoopStartForIf(sortedLoops,if_else_begin);
            if(parentLoopStart==immGotoJmp)
            {
                java.lang.String s=setJdecLabelForContinue(parentLoopStart,loop_end) ;
                sb.append(s);
                lbl="continue";
                continue_JumpOffsets.add(new Integer(immGotoJmp));
                done=true;
            }
            else
            {
                int nextstart=getNextLoopStart(immGotoJmp);
                if(nextstart!=-1)
                {
                    int loopend=getloopEndForStart(loops,nextstart);
                    if(loopend!=-1)
                    {
                     if(if_else_begin >  loopend && nextstart < GotoStart)
                     {
                         sb.append("jdecLABEL"+immGotoJmp);
                         lbl="continue";
                         over=true;
                         LABELS.put(new Integer(immGotoJmp),"jdecLABEL"+immGotoJmp);
                     }
                    }
                     if(over==false && nextstart < GotoStart)
                    {
                    break_JumpOffsets.add(new Integer(nextstart));
                    sb.append("jdecLABEL"+nextstart);
                    LABELS.put(new Integer(nextstart),"jdecLABEL"+nextstart);
                          lbl="break";
                     }
                }

                done=true;
            }

        }



        if(done==false)
        {
            while((i < info.length))// && cont)   // TODO:  check why  This was needed
            {
                int current=info[i];
                if(current==JvmOpCodes.GOTO)
                {
                   BigInteger b;

                    if(i > immGotoJmp && info[immGotoJmp]!=JvmOpCodes.GOTO)
                    {
                        lbl="";
                        break;
                    }
                    boolean end=isIndexEndOfLoop(loops,i);
                    if(end)
                    {
                        int start=getLoopStartForEnd(i,loops);
                        if(start > if_else_begin){return lbl;}
                        Object []sortedLoops=sortLoops(loops);
                        int parentLoopStart=getParentLoopStartForIf(sortedLoops,if_else_begin);
                        if(parentLoopStart==start)
                        {
                            java.lang.String s=setJdecLabelForContinue(parentLoopStart,i) ;
                            sb.append(s);
                            lbl="continue";
                            continue_JumpOffsets.add(new Integer(immGotoJmp));
                            break;
                        }
                        else
                        {
                            int nextstart=getNextLoopStart(start);
                            if(nextstart!=-1 && nextstart < GotoStart)
                            {
                               break_JumpOffsets.add(new Integer(nextstart));
                               sb.append("jdecLABEL"+nextstart);
                               LABELS.put(new Integer(nextstart),"jdecLABEL"+nextstart);
                            }
                            lbl="break";
                            break;
                        }

                    }
                    else
                    {
                        lbl="";
                        break;
                    }

                }
                else
                    i++;

            }
        }


        return lbl;
    }

    private Object[] sortLoops(ArrayList list)
    {
        Object o[]=list.toArray();
        Arrays.sort(o);
        return o;
    }

    private int getParentLoopStartForIf(Object []sortedloops,int ifbegin)
    {
        int reqdStart=-1;
        int max=-1;
        int pos=0;
        int counter=sortedloops.length-1;
        while(counter >= 0)
        {
            Object o=sortedloops[counter];
            if(o instanceof Loop)
            {
                Loop l=(Loop)o;
                int ls=l.getStartIndex();
                if(ls < ifbegin)
                {
                    reqdStart=ls;
                    break;
                }

            }
            counter--;
        }
        return reqdStart;

    }

    private int getReqdGoto(int start,byte[] info,int end)
    {
        int x=-1;
        for(int s=(start+3);s <= end;s++)
        {
            int cur=info[s];
            if(cur==JvmOpCodes.GOTO){x=s;}//break;}
        }
        boolean b=isThisInstrStart(behaviour.getInstructionStartPositions(),x);
        if(b)
        return x;
        else
            return -1;
    }

    private Hashtable branchLabels=new Hashtable();

    private class BranchLabel
    {
        IFBlock IF;
        java.lang.String l;   // continue or break or empty

        public java.lang.String getBrlbl() {
            return brlbl;
        }

        java.lang.String brlbl; // jmpindexlabel
        BranchLabel(IFBlock ifst,java.lang.String label,java.lang.String brlbl)
        {
            IF=ifst;
            l=label;
            this.brlbl=brlbl;
        }

        public IFBlock getIF()
        {
            return IF;
        }
        public java.lang.String getLBL()
        {
            return l;
        }
    }


    private java.lang.String getBranchTypeAtI(int i, IFBlock ifst,StringBuffer sb)
    {
        boolean skip=skipBranchCheck(i);
        if(skip)return "";
        Iterator it=branchLabels.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry e=(Map.Entry)it.next();
            int end=((Integer)(e.getValue())).intValue();
            if(end==i)
            {
                BranchLabel b=(BranchLabel)e.getKey();
                IFBlock IF=b.getIF();
                if(IF==ifst)
                {
                    labelAssociated.put(new Integer(i),"true");
                    sb.append(b.getBrlbl());
                    return b.getLBL();
                }
            }

        }

        return "";
    }

    private boolean skipBranchCheck(int i)
    {
        boolean b=false;
        Iterator it=labelAssociated.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry e=(Map.Entry)it.next();
            java.lang.String skip=((java.lang.String)(e.getValue()));
            int end=((Integer)e.getKey()).intValue();
            if( i==end && skip.equals("true") )
                b=true;
        }
        return b;
    }
    private Hashtable labelAssociated=null;//new Hashtable();

    private ArrayList continue_JumpOffsets=null;//new ArrayList();
    private ArrayList break_JumpOffsets=null;



    private int resetElseCloseNumber(ArrayList loopList, IFBlock ifs,int gotoStart)
    {

        if(loopList.size() > 0)
        {
            Object []sortedLoops=sortLoops(loopList);
            int parentLoopStart=getParentLoopStartForIf(sortedLoops,ifs.getIfStart());
            int end=getloopEndForStart(loopList,parentLoopStart);
            int elseEnd=ifs.getElseCloseLineNumber();
            if(elseEnd > end && end!=-1 && parentLoopStart!=-1)ifs.setElseCloseLineNumber(end);
            return ifs.getElseCloseLineNumber();
        }
        else
            return ifs.getElseCloseLineNumber();


    }

    private boolean isNewEndValid(int newend, IFBlock ifs,java.lang.String type,int curend)
    {

        int end;
        if(type.equals("else"))
            end=ifs.getElseCloseLineNumber();
        else
            end=curend;
        if(newend == -1)return false;
        if(newend > end)
        {
            return false;
        }

        else
            return true;
    }


    private int resetEndofIFElseWRTSwitch(ArrayList switches, IFBlock anIf,int curend,int curStart,java.lang.String type)
    {
        int k=-1;
        ArrayList possibleCaseEnds=new ArrayList();
        for(int s=0;s<switches.size();s++)
        {

            Switch swblk=(Switch)switches.get(s);
            ArrayList cases=swblk.getAllCases();
            for(int c=0;c<cases.size();c++)
            {
                Case caseblk=(Case)cases.get(c);
                int end=caseblk.getCaseEnd();
                int ifelseStart=curStart;
                int ifelseEnd;
                if(type.equals("else"))
                    ifelseEnd= anIf.getElseCloseLineNumber();
                else
                    ifelseEnd=curend;
                if(end > ifelseStart && end < ifelseEnd)
                {
                    possibleCaseEnds.add(new Integer(end));
                }

            }

        }

        if(possibleCaseEnds.size() > 0)
        {
            Integer ints[]=(Integer[])possibleCaseEnds.toArray(new Integer[possibleCaseEnds.size()]);
            Arrays.sort(ints);
            k=ints[0].intValue();

        }


        return k;
    }

    private int getloopEndForStart(ArrayList list,int start)
    {
        for(int i=0;i<list.size();i++)
        {
            Loop l=(Loop)list.get(i);
            if(l.getStartIndex()==start)
            {
                return l.getEndIndex();
            }
        }
        return -1;

    }

    private int resetElseCloseNumber(int start,int end)
    {
        int newend=end;
        int t1=start+2;
        int t2=end;
        for(int s=0;s<continue_JumpOffsets.size();s++)
        {
            int i=((Integer)continue_JumpOffsets.get(s)).intValue();
            if((i > t1) && (i < t2))
            {
                newend=i;
                break;
            }
        }
        return newend;
    }



    private Loop getThisLoop(ArrayList list,int s)
    {
        Loop l;
        for(int st=0;st<list.size();st++)
        {
            Loop tmp=(Loop)list.get(st);
            if(tmp.getEndIndex()==s)
                return tmp;
        }
        return null;
    }




    private void addBranchLabel(int classIndex,int i, IFBlock ifst,int currentForIndex,byte[] info)
    {

        boolean add=true;
        boolean continuetofind=false;
       /* if(classIndex < i)
        {
            ifst.setIfCloseLineNumber(findCodeIndexFromInfiniteLoop(ifst,behaviour.getBehaviourLoops(),classIndex));
            if(ifst.getIfCloseLineNumber()==-1)
                continuetofind=true;
        }
        if(classIndex > i || continuetofind)
        {*/
            if(isThisInstrStart(behaviour.getInstructionStartPositions(),classIndex-3) && info[classIndex-3]==JvmOpCodes.GOTO)  // GOTO_W?
            {

                int resetVal=checkIfElseCloseNumber(classIndex-3,ifst);
                ifst.setIfCloseLineNumber(resetVal);
            }
            else
            {

                int resetVal=checkIfElseCloseNumber(classIndex,ifst);
                ifst.setIfCloseLineNumber(resetVal);
            }

       // }


        int if_start=ifst.getIfStart();
        int if_end=ifst.getIfCloseLineNumber();
        if(if_end==-1 || if_end < if_start)
        {
            boolean b=false;
            int bytecodeend=ifst.getIfCloseFromByteCode();
            b=isByteCodeALoopStart(behaviour.getBehaviourLoops(),bytecodeend);
            if(b)
            {
                int loopend=getloopEndForStart(behaviour.getBehaviourLoops(),bytecodeend);
                if(loopend!=-1)
                {
                    ifst.setIfCloseLineNumber(loopend);
                }
            }
        }



        int k=ifst.getIfCloseFromByteCode();
        k=k-3;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),k) && k > ifst.getIfStart())
        {
          boolean IF=isInstructionIF(info[k]);
          if(IF && k!=ifst.getIfStart())
          {
             ifst.setDonotclose(true);
          }
        }


        k=ifst.getIfCloseLineNumber();
        k=k-3;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),k) && ifst.getIfCloseFromByteCode() < ifst.getIfStart())
        {
           boolean IF=isInstructionIF(info[k]);
          if(IF && k!=ifst.getIfStart())
          {
             ifst.setDonotclose(true);
          }
        }
        java.lang.String s="";
        StringBuffer sb=new StringBuffer("");
        int gotos=-1;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex+3) && info[currentForIndex+3]==JvmOpCodes.GOTO)
        {
            s=getBranchType(currentForIndex,currentForIndex+3,info,behaviour.getBehaviourLoops(),sb,true);
            if(s.trim().length()==0)
            {
                int end=getClosestLoopEndForThisIf(currentForIndex,behaviour.getBehaviourLoops(),info);
                if(end!=-1)
                {
                    s="break";
                }

            }
            // TODO
            gotos=currentForIndex+3;
        }
        else
        {
            int x=getReqdGoto(currentForIndex,info,ifst.getIfCloseLineNumber());
            if(x!=-1)
            {
                s=getBranchType(currentForIndex,x,info,behaviour.getBehaviourLoops(),sb)  ;
                gotos=x;
            }

        }
        if(gotos!=-1)
        {
          // TODO: Skip if IFSTRUCTURE is a loop



          int gotoj=getJumpAddress(info,gotos);
          if(isThisInstrStart(behaviour.getInstructionStartPositions(),gotoj) && info[gotoj]==JvmOpCodes.RETURN)
          {
              retAtIfElseEnd.put(new Integer(ifst.getIfCloseLineNumber()),"return");
              add=false;
          }
        }

        if(add)
        branchLabels.put(new BranchLabel(ifst,s,sb.toString()),new Integer(ifst.getIfCloseLineNumber()));

    }

    private ArrayList  sortCasesByStart(ArrayList allcases)
    {
        ArrayList sorted=new ArrayList();
        int s[]=new int[allcases.size()];
        for(int i=0;i<allcases.size();i++)
        {
            s[i]=((Case)allcases.get(i)).getCaseStart();
        }
        Arrays.sort(s);
        for(int j=0;j<s.length;j++)
        {
            int c=s[j];
            for(int k=0;k<allcases.size();k++)
            {
                Case current=(Case)allcases.get(k);
                if(current.getCaseStart()==c){sorted.add(current);break;}
            }
        }
        return sorted;
    }

    private  int checkLoopsAndSwitchForIfEnd(int end, IFBlock ifs,Behaviour behaviour)
    {
        int reqdEnd=-1;
        ArrayList loops=behaviour.getBehaviourLoops();
        if(loops!=null && loops.size() > 0)
        {

            Object []sortedLoops=sortLoops(loops);
            int parentLoopStart=getParentLoopStartForIf(sortedLoops,ifs.getIfStart());
            int loopend=getloopEndForStart(loops,parentLoopStart);
            if(ifs.getIfStart() < loopend && end > loopend)ifs.setIfCloseLineNumber(loopend);
            reqdEnd=ifs.getIfCloseLineNumber();

        }

        ArrayList allswicthes=behaviour.getAllSwitchBlks();
        if(allswicthes!=null && allswicthes.size() > 0)
        {

            int newifend=-1;
            newifend=resetEndofIFElseWRTSwitch(allswicthes,ifs,reqdEnd,currentForIndex,"if");
            boolean valid=isNewEndValid(newifend,ifs,"if",end);
            if(valid)
            {
                ifs.setIfCloseLineNumber(newifend);
                reqdEnd=ifs.getIfCloseLineNumber();

            }

        }
        return reqdEnd;

    }


    private void checkEndOfLoops(Hashtable IfS,Behaviour b)
    {
        this.getCurrentIFStructues();
        Object ifsSorted[]=sortIFStructures();
        ArrayList loops=b.getBehaviourLoops();
        Object sorted[]=sortLoops(loops);
        ArrayList allswitches=b.getAllSwitchBlks();
        if(loops.size()==0)return;
        else
        {
            for(int s=0;s<sorted.length;s++)
            {
                if(!(sorted[s] instanceof Loop))continue;
                Loop cur=(Loop)sorted[s];
                if(IfS.size() > 0)
                    resetLoopEndWRTIFs(IfS,b,cur,ifsSorted);
                int newloopend=resetLoopEndWRTLoops(sorted,b,cur);
                cur.setEndIndex(newloopend);
                if(allswitches!=null && allswitches.size() > 0)
                {
                    newloopend= resetLoopEndWRTSwitch(allswitches,b,cur);
                    cur.setEndIndex(newloopend);
                }
            }


        }

    }


    private void resetLoopEndWRTIFs(Hashtable IfS,Behaviour b,Loop cur,Object ifsorted[])
    {

        IFBlock parentif=getParentIFforLoop(cur,ifsorted);
        if(parentif==null) return;
        else
        {
            int lend=checkEndofLoopWRTIF(parentif,ifsorted,cur);
            cur.setEndIndex(lend);
        }

    }

    private IFBlock getParentIFforLoop(Loop cur,Object ifsorted[])
    {
        IFBlock par=null;
        for(int n=(ifsorted.length-1); n>=0;n--)
        {
            if(!(ifsorted[n] instanceof IFBlock))continue;
            IFBlock IF=(IFBlock)ifsorted[n];
            int s=IF.getIfStart();
            if(s < cur.getStartIndex()){par=IF;break;}
        }
        return par;
    }


    private int checkEndofLoopWRTIF(IFBlock parentif,Object []ifsorted,Loop cur)
    {
        int end=-1;
        int parentIFStart=parentif.getIfStart();
        int parentIFEnd=parentif.getIfCloseLineNumber();
        boolean doesParentHaveElse=parentif.isHasElse();
        boolean needToFindSuperParent=false;

        if(cur.getStartIndex() < parentIFEnd)  // Yes it lies
        {
            if(cur.getEndIndex() > parentIFEnd)
            {
                end=parentIFEnd;
                return end;
            }
            else
            {
                end=cur.getEndIndex();   // Passes else was correct
                return end;
            }

        }

        if(cur.getStartIndex() > parentIFEnd)
        {
            if(doesParentHaveElse)
            {
                int parentElseEnd=parentif.getElseCloseLineNumber();
                if(cur.getStartIndex() < parentElseEnd)  // Lies within the else of parent IF
                {
                    if(cur.getEndIndex() > parentElseEnd)
                    {
                        end=parentElseEnd;
                        return end;
                    }
                    else
                    {
                        end=cur.getEndIndex();
                        return end;
                    }
                }
                else
                {
                    needToFindSuperParent=true;
                }

            }
            else
            {
                needToFindSuperParent=true;
            }


        }
        if(needToFindSuperParent)
        {
            IFBlock superParent=getParentBlock(ifsorted,parentIFStart);
            int tmp;
            if(superParent!=null)
                tmp=checkEndofLoopWRTIF(superParent,ifsorted,cur);
            else
            {
                tmp=cur.getEndIndex();
            }
            return tmp;
        }
        else
        {
            return cur.getEndIndex();  // Should Never come here
        }

    }

    private int resetLoopEndWRTLoops(Object[] loops,Behaviour b,Loop cur)
    {
        Loop temp=null;
        Loop parent=null;
        for(int s=0;s<loops.length;s++)
        {
            temp=(Loop)loops[s];
            if(temp==cur)
            {
                if(s==0)return cur.getEndIndex();
                if(s > 0)
                {
                    int prev=s-1;
                    parent=(Loop)loops[prev];
                    int parEnd=parent.getEndIndex();
                    if(cur.getEndIndex() > parEnd)
                    {
                        return parEnd;
                    }
                    else
                    {
                        return cur.getEndIndex();
                    }
                }
            }

        }
        return cur.getEndIndex();

    }

    private int resetLoopEndWRTSwitch(ArrayList allswitches,Behaviour b,Loop cur)
    {
        int k=-1;
        ArrayList possibleCaseEnds=new ArrayList();
        for(int s=0;s<allswitches.size();s++)
        {

            Switch swblk=(Switch)allswitches.get(s);
            ArrayList cases=swblk.getAllCases();
            for(int c=0;c<cases.size();c++)
            {
                Case caseblk=(Case)cases.get(c);
                int end=caseblk.getCaseEnd();
                if(end > cur.getStartIndex() && end < cur.getEndIndex())
                {
                    possibleCaseEnds.add(new Integer(end));
                }

            }

        }

        if(possibleCaseEnds.size() > 0)
        {
            Integer ints[]=(Integer[])possibleCaseEnds.toArray(new Integer[possibleCaseEnds.size()]);
            Arrays.sort(ints);
            k=ints[0].intValue();
            return k;
        }
        else
            return cur.getEndIndex();
    }


    private boolean isThisLoopEndAlso(ArrayList loops,int i,int ifstart)
    {
        for(int s=0;s<loops.size();s++)
        {
            Loop l=(Loop)loops.get(s);
            int lend=(l).getEndIndex();
            if(lend==i && ifstart > l.getStartIndex())return true;
        }
        return false;
    }

    private boolean doesthisClashWithCaseBegin(ArrayList switches,int i)
    {
        if(switches==null || switches.size()==0)return false;
        boolean ret=false;
        for(int s=0;s<switches.size();s++)
        {

            Switch swblk=(Switch)switches.get(s);
            ArrayList cases=swblk.getAllCases();
            for(int k=0;k<cases.size();k++)
            {
                Case c=(Case)cases.get(k);
                if(c.getCaseStart()==i)
                {
                    ret=true;
                    break;
                }
            }

        }
        return ret;
    }




    private Case isIFInCase(Behaviour behaviour,int currentForIndex, IFBlock ifs)
    {

        Case caseblk=null;
        boolean present=false;
        int ifstart=ifs.getIfStart();
        int ifend=ifs.getIfCloseLineNumber();
        ArrayList allswitches=behaviour.getAllSwitchBlks();
        for(int s=0;s<allswitches.size();s++)
        {

            ArrayList allcases=((Switch)allswitches.get(s)).getAllCases();
            for(int k=0;k<allcases.size();k++)
            {
                Case cblk=(Case)allcases.get(k);
                int casestart=cblk.getCaseStart();
                int caseend=cblk.getCaseEnd();
                if(ifstart >= casestart && ifend < caseend )
                {

                    present=true;
                    caseblk=cblk;
                    break;
                }

            }
            //System.out.println(caseblk);
            if(present)break;

        }

        return caseblk;

    }



    private int getElseEndwrtcaseblk(Case caseblk,byte[] info,int start)
    {

        int end=-1;
        boolean found=false;
        int caseend=caseblk.getCaseEnd();
        for(int i=start;i<=caseend;i++)
        {
            int inst=info[i];
            if(isThisInstrStart(behaviour.getInstructionStartPositions(),i) && inst==JvmOpCodes.GOTO)
            {
                end=i;
                found=true;
                break;
            }

        }
        if(!found)
            end=caseend;
        return end;
    }


    private boolean checkForSomeSpecificInstructions(byte[] code,int i)

    {
        int inst=code[i];
        switch(inst)
        {

            //LDC // TODO: Need to Regressoion Testing Here  // uncommented bcoz of trytrest_4
            case JvmOpCodes.LDC2_W:
            case JvmOpCodes.LDC_W:

                // case JvmOpCodes.ACONST_NULL:
            case JvmOpCodes.DUP:
            case JvmOpCodes.DUP2:
         //  case JvmOpCodes.NEW:
            case JvmOpCodes.CHECKCAST:
            case JvmOpCodes.ATHROW:

            case JvmOpCodes.ANEWARRAY:
            case JvmOpCodes.NEWARRAY:
            case JvmOpCodes.MULTIANEWARRAY:
                return true;

        }
        if(inst==JvmOpCodes.LDC && (isThisInstrStart(behaviour.getInstructionStartPositions(),(i-3)) && code[(i-3)]!=JvmOpCodes.INVOKESPECIAL))
        {
             return true;
        }
        return false;


    }

    private Hashtable LABELS;
    private Hashtable retAtIfElseEnd;
    private java.lang.String setJdecLabelForContinue(int start,int end)
    {

        // Skip If IFBlock under examination is a Loop
        ArrayList loops=behaviour.getBehaviourLoops();
        for(int s1=0;s1<loops.size();s1++)
        {
            Loop l=(Loop)loops.get(s1);
            int st=l.getStartIndex();
            int ed=l.getEndIndex();
            if(st==start && ed==end)
            {
                return "";
            }
        }



        byte[] info;
        if(behaviour!=null)
        {
            info=behaviour.getCode();
            int inst=info[start];
            int loadIndex=getLoadInstIndex(inst,info,start);
            if(loadIndex==-1)return "";
            else
            {
                int pos=getStoreInstPosInCode(info,end,loadIndex);
                if(pos!=-1)
                {
                    LABELS.put(new Integer(pos),"jdecLABEL"+pos);
                    return "jdecLABEL"+pos;
                }


            }
        }


        return "";
    }


    private int getStoreInstPosInCode(byte[] info,int lend,int loadIndex)

    {
        for(int s=lend;s>=0;s--)
        {
            if(isThisInstrStart(behaviour.getInstructionStartPositions(),s))
            {
                int curinst=info[s];
                boolean b=isCurrentInstStore(curinst);
                if(b)
                {
                    int temp=s+1;
                    int storeindex=info[temp];
                    if(storeindex==loadIndex)
                        return s;
                }
            }

        }
        return -1;
    }



    private boolean isCurrentInstStore(int inst)
    {
        boolean flag ;
        switch (inst) {

            case JvmOpCodes.ASTORE :
                flag = true;
                break;

            case JvmOpCodes.ASTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.ASTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.ASTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.ASTORE_3 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.FSTORE :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.ISTORE :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_3 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE :
                flag = true;
                break;

            case JvmOpCodes.LSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_3 :
                flag = true;
                break;

            default:
                flag = false;
        }
        return flag;
    }




    /****
     * NOTE: This is not general purpose method tofind load index inst...Skips ceratian loads
     * see usages
     * @param inst
     * @param info
     * @param s
     * @return
     */
    public int getLoadInstIndex(int inst,byte info[],int s)
    {
        // chkIndex is the index of the goto instruction.

        switch(inst)
        {


            case JvmOpCodes.ALOAD	:
                return info[++s];

            case JvmOpCodes.ALOAD_0	:
                return 0;

            case JvmOpCodes.ALOAD_1	:
                return 1;

            case JvmOpCodes.ALOAD_2	:
                return 2;

            case JvmOpCodes.ALOAD_3	:
                return 3;





            case JvmOpCodes.DLOAD	:
                return info[++s];

            case JvmOpCodes.DLOAD_0	:
                return 0;

            case JvmOpCodes.DLOAD_1	:
                return 1;

            case JvmOpCodes.DLOAD_2	:
                return 2;

            case JvmOpCodes.DLOAD_3	:
                return 3;


            case JvmOpCodes.FLOAD	:
                return info[++s];

            case JvmOpCodes.FLOAD_0	:
                return 0;

            case JvmOpCodes.FLOAD_1	:
                return 1;

            case JvmOpCodes.FLOAD_2	:
                return 2;

            case JvmOpCodes.FLOAD_3	:
                return 3;


            case JvmOpCodes.ILOAD	:
                return info[++s];

            case JvmOpCodes.ILOAD_0	:
                return 0;

            case JvmOpCodes.ILOAD_1	:
                return 1;

            case JvmOpCodes.ILOAD_2	:
                return 2;

            case JvmOpCodes.ILOAD_3	:
                return 3;


            case JvmOpCodes.LLOAD:
                return info[++s];

            case JvmOpCodes.LLOAD_0:
                return 0;
            case JvmOpCodes.LLOAD_1:
                return 1;
            case JvmOpCodes.LLOAD_2:
                return 2;
            case JvmOpCodes.LLOAD_3:
                return 3;



        }

        return -1;
    }


    private java.lang.String getAnyLabelAtI(Hashtable labels,int i)
    {
        java.lang.String lbl=(java.lang.String)labels.get(new Integer(i));
        return lbl;
    }


    /****
     * NOTE: This is not general purpose method tofind load  inst...Skips ceratian loads
     * see usages
     */
    public boolean continueFindingBranchType(int i,byte info[])
    {
        // chkIndex is the index of the goto instruction.

        switch(info[i])
        {


            case JvmOpCodes.ALOAD	:


            case JvmOpCodes.ALOAD_0	:


            case JvmOpCodes.ALOAD_1	:


            case JvmOpCodes.ALOAD_2	:


            case JvmOpCodes.ALOAD_3	:






            case JvmOpCodes.DLOAD	:


            case JvmOpCodes.DLOAD_0	:


            case JvmOpCodes.DLOAD_1	:


            case JvmOpCodes.DLOAD_2	:


            case JvmOpCodes.DLOAD_3	:



            case JvmOpCodes.FLOAD	:


            case JvmOpCodes.FLOAD_0	:


            case JvmOpCodes.FLOAD_1	:


            case JvmOpCodes.FLOAD_2	:


            case JvmOpCodes.FLOAD_3	:



            case JvmOpCodes.ILOAD	:


            case JvmOpCodes.ILOAD_0	:


            case JvmOpCodes.ILOAD_1	:

            case JvmOpCodes.ILOAD_2	:


            case JvmOpCodes.ILOAD_3	:



            case JvmOpCodes.LLOAD:


            case JvmOpCodes.LLOAD_0:

            case JvmOpCodes.LLOAD_1:

            case JvmOpCodes.LLOAD_2:

            case JvmOpCodes.LLOAD_3:
                return true;



        }

        return false;
    }

    private boolean skipGeneratingElse(ArrayList gotos,ArrayList gotoj,int currentForIndex, IFBlock ifs)
    {
        boolean skip=false;
        int temp=currentForIndex+3;
        for(int s=0;s<gotoj.size();s++)
        {

            int jump=((Integer)gotoj.get(s)).intValue();
            if(jump==temp)
            {
                int corS=((Integer)gotos.get(s)).intValue();
                if(corS > ifs.getIfStart() && corS < ifs.getIfCloseLineNumber())return true;
            }

        }


        return skip;

    }



    private boolean isThisIfALoopCondition(IFBlock IF,byte[] info,ArrayList loops)
    {
        boolean b=true;
        int ifend=IF.getIfCloseLineNumber();
        int ifs=IF.getIfStart();
        boolean b1=isThisLoopEndAlso(loops,ifend,ifs);
        ArrayList list=behaviour.getInstructionStartPositions();
        if(!b1)return false;
        if(b1)
        {
            int jump=getJumpAddress(info,ifend);
            if(jump >= ifs)return false;
            for(int s=jump;s<ifs;s++)
            {
                int inst=info[s];
                boolean b2=isNextInstructionIf(inst);
                if(b2 && isThisInstrStart(list,s))
                {
                    b=false;
                    return b;
                }
            }

        }

        return b;



    }


    private java.lang.String  getReturnTypeInst(byte[] info,int i)
    {
        switch(info[i])
        {
            case JvmOpCodes.IRETURN:
                return "ireturn";
            case JvmOpCodes.LRETURN:
                return "lreturn";
            case JvmOpCodes.FRETURN:
                return "freturn";
            case JvmOpCodes.DRETURN:
                return "dreturn";
            case JvmOpCodes.ARETURN:
                return "areturn";
            case JvmOpCodes.RETURN:
                return "return";
            default:
                return "";

        }

    }



    private Hashtable handlertracker=new Hashtable();





    private class handlerEndTracker
    {

        private int start=-1;
        private int end=-1;

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public java.lang.String getType() {
            return type;
        }

        private java.lang.String type="";

        private boolean closed=false;

        handlerEndTracker(int s,int e,java.lang.String t)
        {
            start=s;
            end=e;
            type=t;
        }


        void setClose(boolean c)
        {
            closed=c;
        }

        boolean getClosed()
        {
            return closed;
        }

    }

    private boolean addhandlerEnd(int i,ExceptionTable table)
    {
        boolean add=true;
        Object o=handlertracker.get(new Integer(i));
        if(o==null)
            return add;
        else
        {
            handlerEndTracker h=(handlerEndTracker)o;
            if(h.getType().equals(table.getTypeOfHandlerForGuardRegion()))
            {
                if(h.getStart()==table.getStartOfHandlerForGuardRegion() && h.getEnd()==table.getEndOfHandlerForGuardRegion())
                {
                    return false;
                }
            }

        }


        return add;
    }
    private Hashtable handlerStarts=new Hashtable();


    private boolean addHandlerStart(int i)
    {
        boolean add=true;
        if(handlerStarts.size() > 0)
        {
            java.lang.String s=(java.lang.String)handlerStarts.get(new Integer(i));
            if(s==null)return add;
            else
                return false;
        }
        return add;
    }


    private Hashtable guardEnds=new Hashtable();

    private boolean addGuardEnd(int i)
    {

        if(guardEnds.size() == 0)return true;
        else
        {
            java.lang.String s=(java.lang.String)guardEnds.get(new Integer(i));
            if(s==null)return true;
            else
                return false;
        }

    }

    /***
     *
     * @param index  localvarindex
     * @param insttype pass it as store or load
     * @return
     */

    // [NOTE:] THIS METHOD CAN RETURN NULL: SO HANDLE WITH CARE IN CALLING METHOD....[belurs]

    private LocalVariable getLocalVariable(int index,java.lang.String insttype,java.lang.String dataType,boolean simpleLoadStore, int instpos)
    {
        LocalVariable l=null;
        LocalVariableStructure structure=behaviour.getLocalVariables();
        if(cd.isClassCompiledWithMinusG())
        {
            if(structure!=null)// Just a double check.. Need not check actually
            {
                int rangeIndex=-1;
                if(insttype.equals("store"))
                {

                    if(simpleLoadStore==true)
                        rangeIndex=instpos+1;
                    else
                        rangeIndex=instpos+2;
                    LocalVariable var=structure.getVariabelAtIndex(index,rangeIndex);
                    if(var==null)
                    {
                        Object o=cd.getMethod_name_storeNLoad_Map().get(this.behaviour.getBehaviourName().concat(behaviour.getStringifiedParameters()));
                        if(o instanceof Hashtable)
                        {
                            Hashtable h=(Hashtable)o;
                            if(h!=null && h.size() > 0)
                            {
                                Integer il=(Integer)h.get(new Integer(instpos));
                                if(il!=null)
                                {
                                    int loadpos=il.intValue();
                                    var=structure.getVariabelAtIndex(index,loadpos);
                                }
                            }
                        }
                    }
                    if(var==null)
                    {
                        //  NOT Sure what to do here// SHOULD NEVER COME HERE.....//
                        // Possible due to a finally block
                       /* try
                        {
                            Writer wr=Writer.getWriter("log");
                            wr.writeLog("Could not obtain local variable While decompiling "+behaviour.getBehaviourName().concat(behaviour.getStringifiedParameters()));
                            wr.writeLog("\nDetails.......");
                            wr.writeLog("\n[Index Pos "+index+",Instruction Pos "+instpos+" INSTRUCTION CODE: "+behaviour.getCode()[instpos]+"]");
                            wr.flush();
                        }
                        catch(Exception ex){}   */

                    }
                    return var;


                }
                else // This is for load
                {
                    LocalVariable var=structure.getVariabelAtIndex(index,instpos);
                    if(var==null)     // GOD forbid it reaches here
                    {
                        //  NOT Sure what to do here// SHOULD NEVER COME HERE.....
                        // Possible due to a finally block
                        try
                        {
                            Writer wr=Writer.getWriter("log");
                            wr.writeLog("Could not obtain local variable While decompiling "+behaviour.getBehaviourName().concat(behaviour.getStringifiedParameters()));
                            wr.writeLog("\nDetails.......");
                            wr.writeLog("\n[Index Pos "+index+",Instruction Pos "+instpos+" INSTRUCTION CODE: "+behaviour.getCode()[instpos]+"]");
                            wr.flush();
                        }
                        catch(Exception ex){}

                    }
                    return var;

                }




            }
            else
                return null; //  should never come here // Programming error
        }
        else
        {
            LocalVariable toreturn=null;
            if(behaviour.getLocalVariables()==null)   // Again shud not happen...
            {
                java.lang.String methodName=behaviour.getBehaviourName();
                structure=new LocalVariableStructure();
                behaviour.setMethodLocalVariables(structure);
                structure.setMethodDescription(methodName.concat(behaviour.getStringifiedParameters()));
                LocalVariableTable localVarTable=LocalVariableTable.getInstance();
                localVarTable.addEntry(methodName.concat(behaviour.getStringifiedParameters().concat(""+behaviour.isMethodConstructor())),structure);
            }
            l=structure.getVariabelAtIndex(index,dataType,datatypesForParams);

            LocalVariable tmp=null;
            if(l==null)  // Create and Add
            {
                java.lang.String variableName="Var"+"_"+instpos+"_"+index;
                if(this.behaviour.getUserFriendlyMethodAccessors().indexOf("static")==-1 && (index==0))
                    variableName="this";
                l=new LocalVariable(behaviour.getBehaviourName().concat(behaviour.getStringifiedParameters()),variableName,index);
                l.setDeclarationGenerated(false);
                l.setDataType(dataType);
                l.setWasCreated(true);
                structure.addLocalVariable(l);
                toreturn=l;
                l.setPassedDataTypeWhileCreatingWithOutMinusG(dataType);


            }
            else
            {

                if(structure.getNumberOfSimilarIndexVars(index) > 1)
                {
                    Object o=cd.getMethod_name_storeNLoad_Map().get(this.behaviour.getBehaviourName().concat(behaviour.getStringifiedParameters()));
                    if(o instanceof Hashtable)
                    {
                        Hashtable h=(Hashtable)o;
                        if(h!=null && h.size() > 0)
                        {
                            Integer il=(Integer)h.get(new Integer(instpos));
                            if(il!=null)
                            {
                                int loadpos=il.intValue();
                                tmp=structure.getVariableForLoadOrStorePos(index,loadpos);
                            }
                        }
                    }

                }

                if(tmp==null)toreturn=l;
                else toreturn=tmp;

            }

            java.lang.String dt=getStoredDataType(toreturn.getIndexPos());
            if(dt!=null && dt.trim().length()!=0)toreturn.setDataType(dt.trim());
            if(this.behaviour.getUserFriendlyMethodAccessors().indexOf("static")==-1 && (toreturn.getIndexPos()==0))
            {
                if(toreturn.getVarName().equals("this")==false)
                {
                    toreturn.setVarName("this");
                }
            }
            return toreturn;

        }


    }


    private  int getLoadIndexForReturn(byte []info,int i,StringBuffer sb)
    {
        int index=-1;
        switch (info[(i-1)]) {




            case JvmOpCodes.ALOAD_0 :

                index=0;
                break;
            case JvmOpCodes.ALOAD_1 :
                index=1;
                break;
            case JvmOpCodes.ALOAD_2:
                index=2;
                break;
            case JvmOpCodes.ALOAD_3:
                index=3;
                break;

            case JvmOpCodes.ILOAD_0:
                index=0;
                break;
            case JvmOpCodes.ILOAD_1:
                index=1;
                break;
            case JvmOpCodes.ILOAD_2:
                index=2;
                break;
            case JvmOpCodes.ILOAD_3:
                index=3;
                break;


            case JvmOpCodes.LLOAD_0:
                index=0;
                break;
            case JvmOpCodes.LLOAD_1:
                index=1;
                break;
            case JvmOpCodes.LLOAD_2:
                index=2;
                break;
            case JvmOpCodes.LLOAD_3:
                index=3;
                break;

            case JvmOpCodes.FLOAD_0:
                index=0;
                break;
            case JvmOpCodes.FLOAD_1:
                index=1;
                break;
            case JvmOpCodes.FLOAD_2:
                index=2;
                break;
            case JvmOpCodes.FLOAD_3:
                index=3;
                break;


            case JvmOpCodes.DLOAD_0:
                index=0;
                break;
            case JvmOpCodes.DLOAD_1:
                index=1;
                break;
            case JvmOpCodes.DLOAD_2:
                index=2;
                break;
            case JvmOpCodes.DLOAD_3:
                index=3;
                break;
            default:
                index=-1;



        }
        if(index!=-1)sb.append((i-1));

        if(index==-1)
        {

            switch(info[(i-2)])
            {
                case JvmOpCodes.ALOAD:
                    index=info[(i-1)];
                    break;

                case JvmOpCodes.DLOAD:
                    index=info[(i-1)];
                    break;

                case JvmOpCodes.FLOAD:
                    index=info[(i-1)];
                    break;
                case JvmOpCodes.LLOAD:
                    index=info[(i-1)];
                    break;
                case JvmOpCodes.ILOAD:
                    index=info[(i-1)];
                    break;

                default:
                    index=-1;
            }

            if(index!=-1)sb.append((i-2));
        }

        return index;
    }



    private Hashtable datatypesForParams=null;

    private void storeDataTypesForMethodParams(Behaviour b,ClassDescription cd)
    {
        datatypesForParams=new Hashtable();

        int count=1;
        java.lang.String str=b.getUserFriendlyMethodParams();
        int staticPresent=b.getUserFriendlyMethodAccessors().indexOf("static");
        if(staticPresent!=-1){count=0;}
        int total=b.getNumberofparamters();
        if(total == 0)return;
        int s=0;
        int endindex=str.indexOf(")");
        int startindex=str.indexOf("(");
        startindex+=1;
        if(startindex < endindex )
        {
            if(endindex > startindex)
            {
                java.lang.String reqdStr=str.substring(startindex,endindex);
                StringTokenizer tokenizer=new StringTokenizer(reqdStr,",");
                while(tokenizer.hasMoreElements())
                {
                    Object o=tokenizer.nextElement();
                    java.lang.String t=(java.lang.String)o;
                    datatypesForParams.put(new Integer(count),t);
                    if(t.trim().equals("long") || t.trim().equals("double"))count++;
                    count++;
                }

            }
            else
                return;

        }




    }


    private java.lang.String getStoredDataType(int index)
    {
        java.lang.String dt="";
        if(this.datatypesForParams!=null && datatypesForParams.size() > 0)
        {
            return (java.lang.String)datatypesForParams.get(new Integer(index));
        }
        return dt;




    }



    private void storeDataTypesWRTConversionInst(Behaviour b,ClassDescription cd)
    {

        byte c[]=b.getCode();
        StringBuffer sb;
        LocalVariableStructure struc=b.getLocalVariables();
        for(int i=0;i<c.length;i++)
        {
            sb=new StringBuffer("");
            int pos=isNextInstructionConversionInst(i,c);

            if(pos!=-1)
            {
                java.lang.String resType=getResulatantTypeForConversionInst(i,c);
                java.lang.String srcType=getSourceTypeForConversionInst(i,c);
                boolean store=isNextInstructionPrimitiveStoreInst(c,(pos+1));
                if(store)
                {
                    StringBuffer sb1=new StringBuffer(""); // denotes type
                    StringBuffer sb2=new StringBuffer(""); // denotes index
                    getIndexNTypeForNextInst(sb1,sb2,c,(pos+1));
                    int index=Integer.parseInt(sb2.toString());
                    java.lang.String varName="Var_"+(pos+1)+"_"+index;
                    LocalVariable local=new LocalVariable(b.getBehaviourName().concat(b.getStringifiedParameters()),varName,index);
                    local.setDeclarationGenerated(false);
                    if(resType==null || resType.trim().equals(""))
                        local.setDataType(sb1.toString());
                    else
                        local.setDataType(resType);
                    local.setWasCreated(true);

                    LocalVariable l=struc.getVariabelAtIndex(index);
                    if(l==null)
                    {
                        b.getLocalVariables().addLocalVariable(local);
                    }
                    else
                    {

                        LocalVariable  l2=struc.getVariableForLoadOrStorePos(index,(pos+1));
                        if(l2!=null)
                        {
                            if(resType==null || resType.trim().equals(""))
                                local.setDataType(sb1.toString());
                            else
                                local.setDataType(resType);
                        }


                    }
                }
                StringBuffer sb3=new StringBuffer("");
                boolean prev=isPrevInstPrimitiveLoad(c,(pos),sb3);
                if(prev)
                {
                    StringBuffer sb1=new StringBuffer(""); // denotes type
                    StringBuffer sb2=new StringBuffer(""); // denotes index
                    getIndexNTypeForPrevInst(sb1,sb2,c,(Integer.parseInt(sb3.toString())));
                    int index=Integer.parseInt(sb2.toString());
                    java.lang.String varName="Var_"+(Integer.parseInt(sb3.toString()))+"_"+index;

                    LocalVariable local=new LocalVariable(b.getBehaviourName().concat(b.getStringifiedParameters()),varName,index);
                    local.setDeclarationGenerated(false);
                    if(srcType==null || srcType.trim().equals(""))
                        local.setDataType(sb1.toString());
                    else
                        local.setDataType(srcType);
                    local.setWasCreated(true);

                    LocalVariable l=struc.getVariabelAtIndex(index);
                    if(l==null)
                    {
                        b.getLocalVariables().addLocalVariable(local);
                    }
                    else
                    {

                        LocalVariable  l2=struc.getVariableForLoadOrStorePos(index,(Integer.parseInt(sb3.toString())));
                        if(l2!=null)
                        {
                            if(srcType==null || srcType.trim().equals(""))
                                local.setDataType(sb1.toString());
                            else
                                local.setDataType(srcType);
                        }


                    }
                }
            }


        }




    }



    private boolean isNextInstructionPrimitiveStoreInst(byte[] c,int pos)
    {
        boolean flag=false;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),pos)==false)  return false;
        switch(c[(pos)])
        {
            case JvmOpCodes.DSTORE :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.DSTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.FSTORE :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.FSTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.ISTORE :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.ISTORE_3 :
                flag = true;
                break;

            case JvmOpCodes.LSTORE :
                flag = true;
                break;

            case JvmOpCodes.LSTORE_0 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_1 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_2 :
                flag = true;
                break;
            case JvmOpCodes.LSTORE_3 :
                flag = true;
                break;


        }

        return flag;
    }


    // sb1 type
    private void getIndexNTypeForNextInst(StringBuffer sb1,StringBuffer sb2,byte[] c,int pos)
    {
        switch(c[(pos)])
        {
            case JvmOpCodes.DSTORE :
                sb1.append("double");
                sb2.append(c[(pos+1)]);
                break;
            case JvmOpCodes.DSTORE_0 :
                sb1.append("double");
                sb2.append(0);
                break;
            case JvmOpCodes.DSTORE_1 :
                sb1.append("double");
                sb2.append(1);
                break;
            case JvmOpCodes.DSTORE_2 :
                sb1.append("double");
                sb2.append(2);
                break;
            case JvmOpCodes.DSTORE_3 :
                sb1.append("double");
                sb2.append(3);
                break;

            case JvmOpCodes.FSTORE :
                sb1.append("float");
                sb2.append(c[(pos+1)]);
                break;
            case JvmOpCodes.FSTORE_0 :
                sb1.append("float");
                sb2.append(0);
                break;
            case JvmOpCodes.FSTORE_1 :
                sb1.append("float");
                sb2.append(1);
                break;
            case JvmOpCodes.FSTORE_2 :
                sb1.append("float");
                sb2.append(2);
                break;
            case JvmOpCodes.FSTORE_3 :
                sb1.append("float");
                sb2.append(3);
                break;

            case JvmOpCodes.ISTORE :
                sb1.append("int");
                sb2.append(c[(pos+1)]);
                break;
            case JvmOpCodes.ISTORE_0 :
                sb1.append("int");
                sb2.append(0);
                break;
            case JvmOpCodes.ISTORE_1 :
                sb1.append("int");
                sb2.append(1);
                break;
            case JvmOpCodes.ISTORE_2 :
                sb1.append("int");
                sb2.append(2);
                break;
            case JvmOpCodes.ISTORE_3 :
                sb1.append("int");
                sb2.append(3);
                break;

            case JvmOpCodes.LSTORE :
                sb1.append("long");
                sb2.append(c[(pos+1)]);
                break;

            case JvmOpCodes.LSTORE_0 :
                sb1.append("long");
                sb2.append(0);
                break;
            case JvmOpCodes.LSTORE_1 :
                sb1.append("long");
                sb2.append(1);
                break;
            case JvmOpCodes.LSTORE_2 :
                sb1.append("long");
                sb2.append(2);
                break;
            case JvmOpCodes.LSTORE_3 :
                sb1.append("long");
                sb2.append(3);
                break;


        }

    }

    // pos --> conversion inst
    private boolean isPrevInstPrimitiveLoad(byte c[],int pos,StringBuffer sb)
    {
        boolean flag=false;

        switch(c[(pos-1)])
        {

            case JvmOpCodes.ILOAD_0:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_1:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_2:
                flag = true;
                break;
            case JvmOpCodes.ILOAD_3:
                flag = true;
                break;


            case JvmOpCodes.LLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.LLOAD_3:
                flag = true;
                break;


            case JvmOpCodes.FLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.FLOAD_3:
                flag = true;
                break;


            case JvmOpCodes.DLOAD_0:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_1:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_2:
                flag = true;
                break;
            case JvmOpCodes.DLOAD_3:
                flag = true;
                break;

            default:
                flag=false;
                break;

        }
        if(flag)sb.append((pos-1));
        if(!flag){
            switch(c[(pos-2)])
            {
                case JvmOpCodes.ILOAD:
                    flag = true;
                    break;
                case JvmOpCodes.LLOAD:
                    flag = true;
                    break;

                case JvmOpCodes.FLOAD:
                    flag = true;
                    break;

                case JvmOpCodes.DLOAD:
                    flag = true;
                    break;
                default:
                    flag=false;
                    break;
            }
            if(flag)sb.append((pos-2));
        }

        return flag;
    }

    private void getIndexNTypeForPrevInst(StringBuffer sb1,StringBuffer sb2,byte c[],int pos)
    {

        boolean flag=false;
        switch(c[(pos)])
        {

            case JvmOpCodes.ILOAD_0:
                flag = true;
                sb1.append("int");
                sb2.append(0);
                break;
            case JvmOpCodes.ILOAD_1:
                flag = true;
                sb1.append("int");
                sb2.append(1);
                break;
            case JvmOpCodes.ILOAD_2:
                flag = true;
                sb1.append("int");
                sb2.append(2);
                break;
            case JvmOpCodes.ILOAD_3:
                flag = true;
                sb1.append("int");
                sb2.append(3);
                break;


            case JvmOpCodes.LLOAD_0:
                flag = true;
                sb1.append("long");
                sb2.append(0);
                break;
            case JvmOpCodes.LLOAD_1:
                flag = true;
                sb1.append("long");
                sb2.append(1);
                break;
            case JvmOpCodes.LLOAD_2:
                flag = true;
                sb1.append("long");
                sb2.append(2);

                break;
            case JvmOpCodes.LLOAD_3:
                flag = true;
                sb1.append("long");
                sb2.append(3);
                break;


            case JvmOpCodes.FLOAD_0:
                flag = true;
                sb1.append("float");
                sb2.append(0);
                break;
            case JvmOpCodes.FLOAD_1:
                flag = true;
                sb1.append("float");
                sb2.append(1);
                break;
            case JvmOpCodes.FLOAD_2:
                flag = true;
                sb1.append("float");
                sb2.append(2);
                break;
            case JvmOpCodes.FLOAD_3:
                flag = true;
                sb1.append("float");
                sb2.append(3);
                break;


            case JvmOpCodes.DLOAD_0:
                flag = true;
                sb1.append("double");
                sb2.append(0);
                break;
            case JvmOpCodes.DLOAD_1:
                flag = true;
                sb1.append("double");
                sb2.append(1);
                break;
            case JvmOpCodes.DLOAD_2:
                flag = true;
                sb1.append("double");
                sb2.append(2);
                break;
            case JvmOpCodes.DLOAD_3:
                flag = true;
                sb1.append("double");
                sb2.append(3);
                break;

            default:
                flag=false;
                break;

        }

        if(!flag){
            switch(c[(pos)])
            {
                case JvmOpCodes.ILOAD:
                    flag=true;
                    sb1.append("int");
                    sb2.append(c[(pos+1)]);
                    break;
                case JvmOpCodes.LLOAD:
                    flag = true;
                    sb1.append("long");
                    sb2.append(c[(pos+1)]);
                    break;

                case JvmOpCodes.FLOAD:
                    flag = true;
                    sb1.append("float");
                    sb2.append(c[(pos+1)]);
                    break;

                case JvmOpCodes.DLOAD:
                    flag = true;
                    sb1.append("double");
                    sb2.append(c[(pos+1)]);
                    break;
                default:
                    flag=false;
                    break;
            }
            if(!flag)
            {
                sb1.append("");
                sb2.append(-1);
            }
        }


    }


    private void   storeDataTypesWRTMethodParams(Behaviour b,ClassDescription cd)
    {
        LocalVariableStructure struc=b.getLocalVariables();
        if(datatypesForParams!=null && datatypesForParams.size() > 0)
        {
            byte []code=b.getCode();
            for(int s=0;s<code.length;s++)
            {
                StringBuffer sb=new StringBuffer("");
                boolean bo=isThisInstructionIStoreInst(code,s,sb);
                if(bo)
                {
                    StringBuffer  sb2=new StringBuffer("");
                    boolean bo2=isPrevInstIloadInst(code,s,sb2);
                    if(bo2)
                    {
                        int loadindex=Integer.parseInt(sb2.toString());
                        java.lang.String type=(java.lang.String)datatypesForParams.get(new Integer(loadindex));
                        LocalVariable var=struc.getVariabelAtIndex(Integer.parseInt(sb.toString()));
                        if(var==null && type!=null)
                        {
                            int thisIndex=Integer.parseInt(sb.toString());
                            java.lang.String varName="Var_"+s+"_"+thisIndex;
                            LocalVariable local=new LocalVariable(b.getBehaviourName().concat(b.getStringifiedParameters()),varName,thisIndex);
                            local.setDeclarationGenerated(false);
                            local.setWasCreated(true);
                            local.setDataType(type);
                            struc.addLocalVariable(local);
                        }
                    }
                }

                // TODO: Handle astore and aload combinatiobn
                sb=new StringBuffer("");
                bo=isThisInstASTOREInst(code,s,sb);
                if(bo)
                {
                    StringBuffer  sb2=new StringBuffer("");
                    boolean bo2=isPrevInstALOADInst(code,s,sb2);
                    if(bo2)
                    {
                        int loadindex=Integer.parseInt(sb2.toString());
                        java.lang.String type=(java.lang.String)datatypesForParams.get(new Integer(loadindex));
                        LocalVariable var=struc.getVariabelAtIndex(Integer.parseInt(sb.toString()));
                        if(var==null && type!=null)
                        {
                            int thisIndex=Integer.parseInt(sb.toString());
                            java.lang.String varName="Var_"+s+"_"+thisIndex;
                            LocalVariable local=new LocalVariable(b.getBehaviourName().concat(b.getStringifiedParameters()),varName,thisIndex);
                            local.setDeclarationGenerated(false);
                            local.setWasCreated(true);
                            local.setDataType(type);
                            struc.addLocalVariable(local);
                        }

                    }
                }
            }
        }
    }


    private boolean isThisInstructionIStoreInst(byte []code,int s,StringBuffer sb)
    {
        boolean b=false;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),s)==false)return false;
        switch(code[s])
        {
            case JvmOpCodes.ISTORE_0:
                b=true;
                sb.append(0);
                break;
            case JvmOpCodes.ISTORE_1:
                b=true;
                sb.append(1);
                break;
            case JvmOpCodes.ISTORE_2:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.ISTORE_3:
                b=true;
                sb.append(3);
                break;
            case JvmOpCodes.ISTORE:
                b=true;
                sb.append(code[(s+1)]);
                break;
        }

        return b;
    }


    private boolean isPrevInstIloadInst(byte[] code,int s,StringBuffer sb2)
    {
        ArrayList starts=behaviour.getInstructionStartPositions();
        boolean b=false;
        if(isThisInstrStart(starts,(s-1)))
        {
            switch(code[(s-1)])
            {
                case JvmOpCodes.ILOAD_0:
                    b=true;
                    sb2.append(0);
                    break;
                case JvmOpCodes.ILOAD_1:
                    b=true;
                    sb2.append(1);
                    break;
                case JvmOpCodes.ILOAD_2:
                    b=true;
                    sb2.append(2);
                    break;

                case JvmOpCodes.ILOAD_3:
                    b=true;
                    sb2.append(3);
                    break;

            }
        }

        if(!b && (s-2) >= 0 && isThisInstrStart(starts,(s-2)))
        {

            switch(code[s-2])
            {

                case JvmOpCodes.ILOAD:
                    b=true;
                    sb2.append(code[s-1]);
                    break;
                default:
                    b=false;
                    break;

            }
        }

        return b;
    }


    private void storeDataTypesWRTLoadNStoreCombinations(Behaviour b,ClassDescription  cd)
    {
        byte code[]=b.getCode();
        LocalVariableStructure struc=b.getLocalVariables();
        for(int s=0;s<code.length;s++)
        {
            StringBuffer sb=new StringBuffer("");
            boolean bo=isThisInstructionIStoreInst(code,s,sb);
            if(bo)
            {
                StringBuffer  sb2=new StringBuffer("");
                boolean bo2=isPrevInstIloadInst(code,s,sb2);
                if(bo2)
                {
                    int loadindex=Integer.parseInt(sb2.toString());
                    LocalVariable var=struc.getVariabelAtIndex(Integer.parseInt(sb.toString()));
                    LocalVariable loadvar=struc.getVariabelAtIndex(Integer.parseInt(sb2.toString()));
                    if(loadvar!=null)
                    {

                        // author :belurs:
                        // NOTE:
                        // Possible source of error here.
                        // There may be more than one localvariable with the same index
                        // And since this code is reachable from the option -g:none
                        // It is not really possible to determine the exact variable
                        // The below code is expected to give out the correct data type
                        // in most cases.
                        // END OF NOTE:

                        // TODO: FIX ME later
                        // FIX THE ambiguity in local variable at this point
                        // [Possible correction : If localvar is null for this load
                        // Index...Keep going back till a store occurs with same
                        // index....Possibly recursive here..may have to serach for
                        // prev local/store till local is not null...Will have to do
                        // This carefully



                        if(var==null)
                        {
                            int thisIndex=Integer.parseInt(sb.toString());
                            java.lang.String varName="Var_"+s+"_"+thisIndex;
                            LocalVariable local=new LocalVariable(b.getBehaviourName().concat(b.getStringifiedParameters()),varName,thisIndex);
                            local.setDeclarationGenerated(false);
                            local.setWasCreated(true);
                            local.setDataType(loadvar.getDataType());
                            struc.addLocalVariable(local);
                        }
                    }
                }
            }

            // TODO: aload and astore
            sb=new StringBuffer("");
            boolean astore=isThisInstASTOREInst(code,s,sb);
            if(astore)
            {
                StringBuffer sb2=new StringBuffer("");
                boolean aload=isPrevInstALOADInst(code,s,sb2);
                if(aload)
                {

                    int loadindex=Integer.parseInt(sb2.toString());
                    LocalVariable var=struc.getVariabelAtIndex(Integer.parseInt(sb.toString()));
                    LocalVariable loadvar=struc.getVariabelAtIndex(Integer.parseInt(sb2.toString()));
                    if(loadvar!=null)
                    {
                        if(var==null)
                        {
                            int thisIndex=Integer.parseInt(sb.toString());
                            java.lang.String varName="Var_"+s+"_"+thisIndex;     //
                            LocalVariable local=new LocalVariable(b.getBehaviourName().concat(b.getStringifiedParameters()),varName,thisIndex);
                            local.setDeclarationGenerated(false);
                            local.setWasCreated(true);
                            local.setDataType(loadvar.getDataType());
                            struc.addLocalVariable(local);
                        }
                    }
                }

            }


        }
    }


    private void createLocalVariablesForMethodParams(Behaviour b,ClassDescription cd)
    {
      byte[] code=b.getCode();
      ArrayList loadStoreIndexes=new ArrayList();

      if(datatypesForParams!=null && datatypesForParams.size() > 0)
      {
        Set set=datatypesForParams.keySet();
       /* for(int s=0;s<code.length;s++)
        {

           StringBuffer sb=new StringBuffer("");
           boolean anyIndex=getAnyLoadStoreIndex(code,s,sb);
           if(anyIndex)
           {
              Integer i=new Integer(sb.toString());
              loadStoreIndexes.add(i);
           }

        }*/
        Iterator it = set.iterator();
        LocalVariableStructure struc=b.getLocalVariables();
        while(it.hasNext())
        {
            Integer pos=(Integer)it.next();
            if(pos!=null)  // Just a check ...
            {
                int ipos=pos.intValue();



                    LocalVariable var=struc.getVariabelAtIndex(ipos);
                    if(var==null) // Create dummy variable
                    {
                        java.lang.String vname="param_"+ipos;
                        LocalVariable lv=new LocalVariable(b.getBehaviourName().concat(b.getStringifiedParameters()),vname,ipos);
                        lv.setDeclarationGenerated(false);
                        lv.setWasCreated(true);
                        lv.setDataType((java.lang.String)datatypesForParams.get(pos));
                        struc.addLocalVariable(lv);

                    }
                    else
                    {
                        java.lang.String vname="param_"+ipos;
                        var.setVarName(vname);
                        var.setDataType((java.lang.String)datatypesForParams.get(pos));
                    }


            }

        }


      }


    }



    private boolean getAnyLoadStoreIndex(byte[] code,int s,StringBuffer sb)
    {
        boolean flag=false;
        switch(code[s])
        {

            case JvmOpCodes.ALOAD:
                flag = true;
                sb.append(code[(s+1)]);
                break;
            case JvmOpCodes.ALOAD_0 :
                flag = true;
                sb.append(0);
                break;
            case JvmOpCodes.ALOAD_1 :
                flag = true;
                sb.append(1);
                break;
            case JvmOpCodes.ALOAD_2:
                flag = true;
                sb.append(2);
                break;
            case JvmOpCodes.ALOAD_3:
                flag = true;
                sb.append(3);
                break;
            case JvmOpCodes.ILOAD:
                flag = true;
                sb.append(code[(s+1)]);
                break;
            case JvmOpCodes.ILOAD_0:
                flag = true;
                sb.append(0);
                break;
            case JvmOpCodes.ILOAD_1:
                flag = true;
                sb.append(1);
                break;
            case JvmOpCodes.ILOAD_2:
                flag = true;
                sb.append(2);
                break;
            case JvmOpCodes.ILOAD_3:
                flag = true;
                sb.append(3);
                break;

            case JvmOpCodes.LLOAD:
                flag = true;
                sb.append(code[(s+1)]);
                break;
            case JvmOpCodes.LLOAD_0:
                flag = true;
                sb.append(0);
                break;
            case JvmOpCodes.LLOAD_1:
                flag = true;
                sb.append(1);
                break;
            case JvmOpCodes.LLOAD_2:
                flag = true;
                sb.append(2);
                break;
            case JvmOpCodes.LLOAD_3:
                flag = true;
                sb.append(3);
                break;

            case JvmOpCodes.FLOAD:
                flag = true;
                sb.append(code[(s+1)]);
                break;
            case JvmOpCodes.FLOAD_0:
                sb.append(0);
                flag = true;
                break;
            case JvmOpCodes.FLOAD_1:
                flag = true;
                sb.append(1);

                break;
            case JvmOpCodes.FLOAD_2:
                flag = true;
                sb.append(2);

                break;
            case JvmOpCodes.FLOAD_3:
                flag = true;
                sb.append(3);

                break;

            case JvmOpCodes.DLOAD:
                flag = true;
                sb.append(code[(s+1)]);

                break;
            case JvmOpCodes.DLOAD_0:
                sb.append(0);

                flag = true;
                break;
            case JvmOpCodes.DLOAD_1:
                flag = true;
                sb.append(1);

                break;
            case JvmOpCodes.DLOAD_2:
                flag = true;
                sb.append(2);

                break;
            case JvmOpCodes.DLOAD_3:
                flag = true;
                sb.append(3);

                break;

            case JvmOpCodes.ASTORE :
                flag = true;
                sb.append(code[(s+1)]);
                break;

            case JvmOpCodes.ASTORE_0 :
                sb.append(0);
                flag = true;
                break;
            case JvmOpCodes.ASTORE_1 :
                sb.append(1);
                flag = true;
                break;
            case JvmOpCodes.ASTORE_2 :
                sb.append(2);
                flag = true;
                break;
            case JvmOpCodes.ASTORE_3 :
                sb.append(3);
                flag = true;
                break;

            case JvmOpCodes.DSTORE :
                flag = true;
                sb.append(code[(s+1)]);
                break;
            case JvmOpCodes.DSTORE_0 :
                flag = true;
                sb.append(0);
                break;
            case JvmOpCodes.DSTORE_1 :
                flag = true;
                sb.append(1);
                break;
            case JvmOpCodes.DSTORE_2 :
                flag = true;
                sb.append(2);
                break;
            case JvmOpCodes.DSTORE_3 :
                flag = true;
                sb.append(3);
                break;

            case JvmOpCodes.FSTORE :
                flag = true;
                sb.append(code[(s+1)]);
                break;
            case JvmOpCodes.FSTORE_0 :
                sb.append(0);
                flag = true;
                break;
            case JvmOpCodes.FSTORE_1 :
                sb.append(1);
                flag = true;
                break;
            case JvmOpCodes.FSTORE_2 :
                sb.append(2);
                flag = true;
                break;
            case JvmOpCodes.FSTORE_3 :
                sb.append(3);
                flag = true;
                break;

            case JvmOpCodes.ISTORE :
                sb.append(code[(s+1)]);
                flag = true;
                break;
            case JvmOpCodes.ISTORE_0 :
                sb.append(0);
                flag = true;
                break;
            case JvmOpCodes.ISTORE_1 :
                sb.append(1);
                flag = true;
                break;
            case JvmOpCodes.ISTORE_2 :
                sb.append(2);
                flag = true;
                break;
            case JvmOpCodes.ISTORE_3 :
                sb.append(3);
                flag = true;
                break;

            case JvmOpCodes.LSTORE :
                sb.append(code[(s+1)]);
                flag = true;
                break;

            case JvmOpCodes.LSTORE_0 :
                sb.append(0);
                flag = true;
                break;
            case JvmOpCodes.LSTORE_1 :
                sb.append(1);
                flag = true;
                break;
            case JvmOpCodes.LSTORE_2 :
                sb.append(2);
                flag = true;
                break;
            case JvmOpCodes.LSTORE_3 :
                sb.append(3);
                flag = true;
                break;


            default:
                flag=false;

        }
        return flag;
    }


    public Hashtable getVariableDimAss() {
        return variableDimAss;
    }

    private Hashtable invokeStart_StackStart =null;
    private Hashtable invokeStartEnd =null;
    private Hashtable invokespecialpos =null;
    private ArrayList getField =null;
    private ArrayList getStatic=null;
    private Hashtable invokeinstrefpos=null;
    private Hashtable invokeinstrefretTypes=null;



    private void storeDataTypesWRTInvokeInst(Behaviour b,ClassDescription cd)
    {
       //if(cd.isClassCompiledWithMinusG()==false)
       //{
        variableDimAss=new Hashtable();
        invokeStart_StackStart=new Hashtable();
        invokeStartEnd=new Hashtable();
        invokespecialpos=new Hashtable();
        getField=new ArrayList();
        getStatic=new ArrayList();
        invokeinstrefpos=new Hashtable();
        invokeinstrefretTypes=new Hashtable();

       //}
       pushTypes=new Hashtable();
        byte code[]=b.getCode();
        LocalVariableStructure struc=b.getLocalVariables();
   label:
        for(int s=0;s<code.length;s++)
        {
            //System.out.println(s+" In method "+b.getBehaviourName());
            StringBuffer sb=new StringBuffer("");
            boolean  invoke=isNextInstructionAnyInvoke(code[s],sb);
            Integer skip=(Integer) ConsoleLauncher.getInstructionMap().get(new Integer(code[s]));
            boolean skipthisinvoke=false;
            if(skip==null)continue;
            else
            {
              if(invoke)
              {

                      int newpresent=s-3;
                 if(code[s]==JvmOpCodes.INVOKESPECIAL)
                 {
                  if(newpresent>=0 && code[newpresent]==JvmOpCodes.INVOKESPECIAL)
                  {
                      // int prevpos=
                      ArrayList statpos=behaviour.getInstructionStartPositions();
                      Integer z=(Integer)invokeinstrefpos.get(new Integer(newpresent));
                      if(z!=null)
                      {
                             int prevpos=z.intValue()-1;
                        ArrayList startpos=behaviour.getInstructionStartPositions();
                      for(int k=prevpos;k>=0;k--)
                      {
                              boolean st=isThisInstrStart(startpos,k);
                              while(st==false && k>=0)
                              {
                                k--;
                                st=isThisInstrStart(startpos,k);
                              }


                             // some start pos found
                          if(k>=0)
                          {
                              if(code[k]==JvmOpCodes.NEW)
                              {
                                  invokespecialpos.put(new Integer(s),new Integer(k));
                                  invokeinstrefpos.put(new Integer(s),new Integer(k));
                                  break;
                              }
                           }

                      }
                      }

                  }
                  else
                  {
                      int prevpos=s-1;
                      ArrayList startpos=behaviour.getInstructionStartPositions();
                      for(int k=prevpos;k>=0;k--)
                      {
                              boolean st=isThisInstrStart(startpos,k);
                              while(st==false && k>=0)
                              {
                                k--;
                                st=isThisInstrStart(startpos,k);
                              }


                             // some start pos found
                          boolean needtocontinue=false;
                              if(k >=0 && code[k]==JvmOpCodes.GETFIELD)
                              {
                                  Iterator it=invokeinstrefpos.entrySet().iterator();
                                  while(it.hasNext())
                                  {
                                      Map.Entry en=(Map.Entry)it.next();
                                      Integer i1=(Integer)en.getKey();
                                      Integer i2=(Integer)en.getValue();

                                      if(i2.intValue()==(k-1) && i1.intValue()!=s)
                                      {
                                         needtocontinue=true;
                                         break;
                                      }
                                      if(i2.intValue()==(k-2) && i1.intValue()!=s)
                                      {
                                         needtocontinue=true;
                                         break;
                                      }

                                  }

                                  if(!needtocontinue)break;


                              }
                              if(k>=0 && (code[k]==JvmOpCodes.NEW ))
                              {
                                  invokespecialpos.put(new Integer(s),new Integer(k));
                                  invokeinstrefpos.put(new Integer(s),new Integer(k));
                                  break;
                              }

                      }




                  }

              }


              }

             //?
             if(!invoke)
              {

                 /*if(code[s]==JvmOpCodes.ANEWARRAY)
                 {

                     StringBuffer invokepos=new StringBuffer("");
                     boolean process=processANEWARRAYb4Invoke(code,s,invokepos);
                     if(process)
                     {
                         stackHandler=new Stack();
                         InstParam first=new InstParam();
                         first.setNumberOfParamsLeft(1);
                         first.setCurrentParamOffsetInCode(-1);
                         stackHandler.push(first);
                         handleBasicPrimitiveLoadOp(code,s-1);
                         InstParam obj=(InstParam)stackHandler.pop();
                         int offset=obj.getCurrentParamOffsetInCode();
                         anewarrayrefpos.put(new Integer(Integer.parseInt(invokepos.toString())),new Integer(offset));

                     }


                 } */





                 if(code[s]==JvmOpCodes.GETSTATIC)
                 {
                     getStatic.add(new Integer(s));
                 }
                 if(code[s]==JvmOpCodes.GETFIELD)
                 {
                     getField.add(new Integer(s));
                 }
                 int toskip=skip.intValue();
                 boolean xx=false;
                 if(toskip==-1)               // lookup // TODO: verify increment
                 {
                      xx=true;
                     int lookupSwitchPos=s;
                     int leave_bytes = (4 - (s % 4))-1;
                     for(int indx=0;indx<leave_bytes;indx++) {
                        s++;
                     }
                     // Read Default
                     int Default=getSwitchOffset(code,s,"");//info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                     s+=4;
                     int numberOfLabels=(getSwitchOffset(code,s,"")); //info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                     //int high=(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                     //int numberOfOffsets=(high-low)+1;
                     int offsetValues[]=new int[numberOfLabels];
                     int labels[]=new int[numberOfLabels];
                    s+=4;
                     for(int start=0;start<numberOfLabels;start++) {
                         int label=getSwitchOffset(code,s,"label");
                         s+=4;
                         int offsetVal=getSwitchOffset(code,s,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                         s+=4;
                         labels[start]=label;
                         offsetValues[start]=offsetVal;

                     }

                 }
                 if(toskip==-2)               // table
                 {
                      xx=true;
                     int tableSwitchPos=s;
                     int leave_bytes = (4 - (s% 4))-1;
                     for(int indx=0;indx<leave_bytes;indx++) {
                         s++;
                     }
                     // Read Default
                     int Default=getSwitchOffset(code,s,"");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                    s+=4;
                     int low=getSwitchOffset(code,s,"label");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                     s+=4;
                     int high=getSwitchOffset(code,s,"label");//(info[++i] << 24) | (info[++i] << 16) | (info[++i] << 8) |info[++i];
                     s+=4;
                     int numberOfOffsets=(high-low)+1;
                     int[] offsetValues=new int[numberOfOffsets];
                     for(int start=0;start<numberOfOffsets;start++) {
                         int offsetVal=getSwitchOffset(code,s,"");
                         s+=4;
                         offsetValues[start]=offsetVal;

                     }

                 }
                 if(toskip==-3) // wide  // TODO ?
                 {


                 }
                 if(!xx)
                     s=s+toskip;
                 else
                     s=s+1;

                  continue;
              }

            }




            java.lang.String belongingClassName="";
            java.lang.String returnTypeName="";
            if(invoke)                                                                      // && !(sb.toString().equalsIgnoreCase("special")))
            {
              int classIndex=getOffset(code,s);
              java.lang.String methodSignature="";
                if(sb.toString().indexOf("interface")==-1)
                {
                    MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
                    methodSignature = mref.getTypeofmethod();
                    belongingClassName=mref.getClassname();
                    returnTypeName=mref.getTypeofmethod();
                    int brk=returnTypeName.indexOf(")");
                    if(brk!=-1)
                    {
                        returnTypeName=returnTypeName.substring((brk+1));
                        brk=returnTypeName.indexOf("L");
                        if(brk!=-1)
                        {
                            returnTypeName=returnTypeName.substring(brk+1,returnTypeName.indexOf(";"));

                        }
                    }
                    invokeinstrefretTypes.put(new Integer(s),belongingClassName+"::"+returnTypeName);
                }
                else
                {
                    InterfaceMethodRef mref = cd.getInterfaceMethodAtCPoolPosition(classIndex);
                    methodSignature = mref.getTypeofmethod();
                    belongingClassName=mref.getClassname();
                    returnTypeName=mref.getTypeofmethod();
                    int brk=returnTypeName.indexOf(")");
                    if(brk!=-1)
                    {
                        returnTypeName=returnTypeName.substring((brk+1));
                        brk=returnTypeName.indexOf("L");
                        if(brk!=-1)
                        {
                            returnTypeName=returnTypeName.substring(brk+1,returnTypeName.indexOf(";"));

                        }
                    }
                    invokeinstrefretTypes.put(new Integer(s),belongingClassName+"::"+returnTypeName);
                }

              Util.parseDescriptor(methodSignature);
              ArrayList list=Util.getParsedSignatureAsList();


              int num=list.size();
              int z=(num-1);
                StringBuffer pos=null;
                int start=s-1;
                 int ipos=-1; // where local var occurs
              boolean skiploop=false;
              while(z >= 0)
              {
                  StringBuffer index=new StringBuffer("");
                  StringBuffer dim=new StringBuffer("");


                  pos=new StringBuffer(""); // where local var occurs
                  java.lang.String type=(java.lang.String)list.get(z);

                  //
                  Integer isinvokestart=(Integer)invokeStartEnd.get(new Integer(start));
                                while(isinvokestart!=null )
                                {
                                    int invokestart=isinvokestart.intValue();
                                    Integer stackstart=(Integer)invokeStart_StackStart.get(new Integer(invokestart));
                                    Integer refstart=(Integer)invokeinstrefpos.get(new Integer(invokestart));
                                    int istart=-1 ;
                                    if(refstart==null)
                                    {
                                        problematicInvokes.add(new Integer(s));
                                        skipthisinvoke=true;
                                        skiploop=true;
                                        break;
                                    }
                                    if(refstart!=null)
                                    {


                                       istart=refstart.intValue();
                                        StringBuffer buffer=new StringBuffer("");
                                            if(isNextInstructionAnyInvoke(code[istart],buffer))
                                        {
                                              java.lang.String sTR=(java.lang.String)invokeinstrefretTypes.get(new Integer(istart));
                                               if(sTR!=null)
                                               {
                                                   int sep=sTR.indexOf("::");
                                                   java.lang.String rEF=sTR.substring(0,sep);
                                                   java.lang.String rET=sTR.substring(sep+2);
                                                   if(rEF.trim().equalsIgnoreCase(rET.trim()))
                                                   {
                                                     ipos=istart;
                                                   }
                                                   else
                                                   {
                                                     refstart=(Integer)invokeinstrefpos.get(new Integer(istart));
                                                     if(refstart!=null)
                                                     ipos=refstart.intValue();
                                                   }
                                               }

                                        }
                                      start=refstart.intValue()-1;
                                    }

                                    z--;
                                    int h=-1    ;
                                   if(ipos==-1)h=istart-1;
                                    else
                                   h=ipos-1;
                                   isinvokestart=(Integer)invokeStartEnd.get(new Integer(h));

                                  if(z==-1)
                                  {
                                      skiploop=true;
                                      break;
                                  }
                                }

                   if(skiploop)break;
                  //
                  type=(java.lang.String)list.get(z);
                  try{
                  getCorrespondingLoadInst(index,dim,start,pos,this.behaviour,this.cd,type,s);
                  }
                  catch(Exception e){
                      problematicInvokes.add(new Integer(s));
                     skipthisinvoke=true;
                     break;
                  }
                  // Should the code stop this method and take next invoke into account
                  try
                  {
                    int dummy=Integer.parseInt(pos.toString());
                  }
                  catch(NumberFormatException ne)
                  {
                     // Skip This invoke
                     // Because the current rules are not sufficient to track the beginning
                     // of a prameter load for a method
                     problematicInvokes.add(new Integer(s));
                     skipthisinvoke=true;
                     break;

                  }


                  ipos=Integer.parseInt(pos.toString());
                  start=(ipos-1);
                  int in=Integer.parseInt(index.toString());
                  int dimensions=Integer.parseInt(dim.toString()) ;
                  LocalVariable l=struc.getVariabelAtIndex(in);

                  if(cd.isClassCompiledWithMinusG()==false)
                  {
                      if(l!=null)
                      {

                         int bracket=type.indexOf("[");
                         if(bracket!=-1)
                         {
                           int brackets=0;
                           for(int d=bracket;d<type.length();d++)
                           {
                               if(type.charAt(d)=='[')brackets++;
                               else
                                   break;
                           }

                           int totalDimenForVar=dimensions+brackets;
                           //Assocaite here
                           if(variableDimAss!=null)
                           variableDimAss.put(new Integer(in),new Integer(totalDimenForVar));
                         }
                          else
                         {
                            int L=type.indexOf("L");
                            if(L!=-1)
                            {
                                type=type.substring((L+1));
                            }
                            int semi=type.indexOf(";");
                             if(semi!=-1)
                             {
                                 type=type.substring(0,(semi));
                             }
                        //    l.setDataType(type);    // NOTE: commented this line as it was resetting already set correct datatype
                         }
                      }
                      if(l==null && in!=-100 && in!=-200)
                      {
                            int bracket=type.indexOf("[");
                            if(bracket==-1)
                            {
                                int L=type.indexOf("L");
                                if(L!=-1)
                                {
                                    type=type.substring((L+1));
                                }
                                int semi=type.indexOf(";");
                                if(semi!=-1)
                                {
                                    type=type.substring(0,(semi));
                                }

                                java.lang.String varName="Var_"+ipos+"_"+in;
                                if(this.behaviour.getUserFriendlyMethodAccessors().indexOf("static")==-1 && (in==0))
                                    varName="this";
                                LocalVariable newl=new LocalVariable(b.getBehaviourName().concat(b.getStringifiedParameters()),varName,in);
                                newl.setDeclarationGenerated(false);
                                newl.setWasCreated(true);
                                newl.setDataType(type);
                                struc.addLocalVariable(newl);

                            }
                          if(bracket!=-1)
                         {
                           int brackets=0;
                           for(int d=bracket;d<type.length();d++)
                           {
                               if(type.charAt(d)=='[')brackets++;
                               else
                                   break;
                           }

                           int totalDimenForVar=dimensions+brackets;
                           //Assocaite here
                              if(variableDimAss!=null)
                           variableDimAss.put(new Integer(in),new Integer(totalDimenForVar));
                         }
                      }
                  }
                  if(in==-200)
                  {
                      pushTypes.put(new Integer(ipos),type);
                  }



                 z--;


              }
             boolean ok=false;
            if(invoke && !skipthisinvoke)
            {
                StringBuffer S=new StringBuffer("");
                ok=checkForAssociatedGetField(getField,s,S,code);
                if(ok)
                {
                    int iS=Integer.parseInt(S.toString());
                    int refpos=getObjectRefForGetField(iS,code);
                    if(refpos!=-1)
                    {
                        if(invokeinstrefpos.get(new Integer(s))==null)
                        {
                            invokeinstrefpos.put(new Integer(s),new Integer(refpos));
                        }

                    }
                }
                if(!ok)
                {
                        S=new StringBuffer("");
                        ok=checkForAssociatedGetStatic(getStatic,s,S,code);   // TODO: possible bug : check for pop and aload then
                        if(ok)
                        {
                            int iS=Integer.parseInt(S.toString());
                            if((iS-1)>=0 &&  code[iS-1]==JvmOpCodes.POP)
                            {
                                StringBuffer s2=new StringBuffer("");
                                boolean bb=isPrevInstructionAload(iS-1,code,s2);
                                if(bb)
                                {
                                    iS=Integer.parseInt(s2.toString());
                                }
                            }
                            if(invokeinstrefpos.get(new Integer(s))==null)
                            {
                            invokeinstrefpos.put(new Integer(s),new Integer(iS));
                            }
                        }
                }
                if(!ok)
                {
                            S=new StringBuffer("");
                       ok=checkForAssociatedInvokeSpecial(invokespecialpos,s,S,code);
                        if(ok)
                        {
                            int iS=Integer.parseInt(S.toString());
                            if(invokeinstrefpos.get(new Integer(s))==null)
                            {
                              invokeinstrefpos.put(new Integer(s),new Integer(iS));
                            }
                        }

                }




            }
            boolean skip2=false;
            if(!ok && code[s]==JvmOpCodes.INVOKESTATIC && ipos==-1 && !skipthisinvoke)
            {
                ipos=s;
                if(invokeinstrefpos.get(new Integer(s))==null)
                {
                    invokeinstrefpos.put(new Integer(s),new Integer(ipos));
                }
                skip2=true;

            }
            if(!ok && code[s]==JvmOpCodes.INVOKESTATIC && ipos!=-1 && !skipthisinvoke)
            {
                if(invokeinstrefpos.get(new Integer(s))==null)
                {
                invokeinstrefpos.put(new Integer(s),new Integer(ipos));
                }
                skip2=true;

            }


            int prev=s-1;
            if(ipos!=-1)prev=ipos-1;
            if(!ok && prev >= 0 && !skip2 && !skipthisinvoke)
            {
               Integer pr=new Integer(prev);
               Integer be=(Integer)invokeStartEnd.get(pr);
               if(be!=null)
               {
                   int Ibe=be.intValue();
                   if(code[Ibe]==JvmOpCodes.INVOKEVIRTUAL || code[Ibe]==JvmOpCodes.INVOKEINTERFACE)
                   {
                       java.lang.String sTR=(java.lang.String)invokeinstrefretTypes.get(new Integer(Ibe));
                       if(sTR!=null)
                       {
                           int sep=sTR.indexOf("::");
                           java.lang.String rEF=sTR.substring(0,sep);
                           java.lang.String rET=sTR.substring(sep+2);
                           if(rEF.trim().equalsIgnoreCase(rET.trim()))
                           {
                               Integer refpos=(Integer)invokeinstrefpos.get(new Integer(Ibe));
                               if(refpos!=null)
                               {
                                   int irefpos=refpos.intValue();
                                   Integer reftemp=(Integer)invokeinstrefpos.get(new Integer(s));
                                   if(reftemp==null)
                                   {
                                       invokeinstrefpos.put(new Integer(s),new Integer(irefpos));
                                   }
                               }
                           }
                           else
                           {
                            Integer refpos=(Integer)invokeinstrefpos.get(new Integer(s));
                               if(refpos==null)
                               {
                            invokeinstrefpos.put(new Integer(s),new Integer(Ibe));
                               }
                           }
                       }
                   }
                   if(code[Ibe]==JvmOpCodes.INVOKESTATIC || code[Ibe]==JvmOpCodes.INVOKESPECIAL )
                   {
                      Integer refpos=(Integer)invokeinstrefpos.get(new Integer(Ibe));
                      if(invokeinstrefpos.get(new Integer(s))==null)
                      {
                        invokeinstrefpos.put(new Integer(s),new Integer(refpos.intValue()));
                      }

                   }
               }
               else
               {
                 StringBuffer sfb=new StringBuffer("");
                 boolean bol=isPrevInstructionAload(prev+1,code,sfb);
                 if(bol)
                 {
                     int aloadpos=Integer.parseInt(sfb.toString());
                     if(invokeinstrefpos.get(new Integer(s))==null)
                      {
                     invokeinstrefpos.put(new Integer(s),new Integer(aloadpos));

                     }
                 }
                 else
                 {
                     if(code[prev]==JvmOpCodes.AALOAD)
                     {
                         int x=prev;
                         do
                         {
                            sfb=new StringBuffer("");
                            x--;
                            bol=isPrevInstructionAload(x,code,sfb);
                         }while(!bol);
                         int aloadpos=Integer.parseInt(sfb.toString());
                         if(invokeinstrefpos.get(new Integer(s))==null)
                      {
                         invokeinstrefpos.put(new Integer(s),new Integer(aloadpos));
                         }


                     }
                 }
               }



            }







             if(invokeStart_StackStart!=null)
             invokeStart_StackStart.put(new Integer(s),new Integer(ipos)); // modify for special/static

                 int end=-1;
             if(invokeStartEnd!=null){
                    end=findWhereThisInvokeEnds(s,code);
                     invokeStartEnd.put(new Integer(end),new Integer(s));
             }
              if(end!=-1)
             {
               int next=end+1;
               s=end;         // changed from next to end
             }


            }
        }



    }

    public Hashtable getPushTypes() {
        return pushTypes;
    }

    private Hashtable pushTypes=null;


    private java.lang.String [] convertTokenizerToArray(StringTokenizer tokens)
    {

       if(tokens!=null)
       {
         int total=tokens.countTokens();
         java.lang.String array[]=new java.lang.String[total];
         int s=0;
         while(tokens.hasMoreTokens())
         {
            java.lang.String cur=(java.lang.String)tokens.nextToken();
            array[s++]=cur;
         }
         return array;
       }
       else
           return null;

    }


    private Hashtable variableDimAss=null;


    private  boolean isInstAAload(int inst)
    {
        return inst==JvmOpCodes.AALOAD;
    }

    private  int isInstAload(int i,byte[] code,StringBuffer bf)
    {
           if(isThisInstrStart(behaviour.getInstructionStartPositions(),i))
                 {
        switch(code[i])
        {

            case JvmOpCodes.ALOAD_0 :
                                        bf.append(0);
                                 return i;
            case JvmOpCodes.ALOAD_1 :
                bf.append(1);
                                 return i;
            case JvmOpCodes.ALOAD_2 :
                bf.append(2);
                                 return i;
            case JvmOpCodes.ALOAD_3 :
                           bf.append(3);
                                 return i;
           case JvmOpCodes.ALOAD :
                           bf.append(code[(i+1)]);
                                 return i;



        }
           }

            int temp=i-1;
           if(isThisInstrStart(behaviour.getInstructionStartPositions(),temp))
                 {
            if(code[temp]==JvmOpCodes.ALOAD)
            {
                    bf.append(code[i]);
                    return temp;
            }
           }
                return -1;

    }




                  // todo:         // Search For StringBuffer from here

    private void getCorrespondingLoadInst(StringBuffer index,StringBuffer dim,int start,StringBuffer pos,Behaviour b,ClassDescription cd,java.lang.String type,int thisinvokestart)
    throws Exception
    {
       ArrayList starts=b.getInstructionStartPositions();
       boolean primitive=isPrimitive(type);

       byte[] code=b.getCode();
       int number=0;
       boolean aaloadFound=false;
       int k=start;
       int orig=k;
       // Find out the instuction start here
       boolean istart=isThisInstrStart(starts,k);
       while(istart==false && k>=0)
       {
           k=k-1;
           istart=isThisInstrStart(starts,k);
       }

       // Now k must be inst start



           if(primitive)
           {

              if(code[k]==JvmOpCodes.ARRAYLENGTH)      // TODO: TEST IT THOROUhLY
              {

                  StringBuffer s6=new StringBuffer("");
                  boolean aloadp=isPrevInstructionAload(k,code,s6);
                  if(aloadp)
                  {
                    index.append(-200);
                    dim.append(0);
                    pos.append(Integer.parseInt(s6.toString()));
                    return;
                  }
                 if((k-1) >= 0)
                  {
                  boolean aaloadp=isInstAAload(code[k-1]);           // BUG check for getstatic alos.:TODO
                  if(aaloadp)
                  {
                      s6=new StringBuffer("");
                      int in=k-1;
                      int posFound=isInstAload(in,code,s6);
                      while(posFound==-1)
                      {
                          in=in-1;
                          posFound=isInstAload(in,code,s6);
                          if(posFound==-1)
                          {
                              if(code[in]==JvmOpCodes.GETSTATIC) // TODO: Need to check at all such places whether the index is start of instruction
                              {

                                  if(code[in-1]==JvmOpCodes.POP)           // TODO: Possible bug with pop2
                                  {
                                      StringBuffer s3=new StringBuffer("");
                                      boolean bb=isPrevInstructionAload(in-1,code,s3);
                                      if(bb)
                                      {
                                          index.append(-200);
                                          dim.append(0);
                                          pos.append(Integer.parseInt(s3.toString()));
                                          return;
                                      }
                                  }
                                  else
                                  {
                                      index.append(-200);
                                      dim.append(0);
                                      pos.append(in);
                                      return;
                                  }

                              }
                              if(code[in]==JvmOpCodes.GETFIELD)
                              {
                                  s6=new StringBuffer("");
                                  aloadp=isPrevInstructionAload(in,code,s6);
                                  index.append(-200);
                                  dim.append(0);
                                  pos.append(Integer.parseInt(s6.toString()));
                                  return;
                              }



                          }

                      }
                      index.append(-200);
                      dim.append(0);
                      pos.append(posFound);
                      return;

                  }
              }
                  if((k-3) >= 0)
                  {
                      boolean getfield=(code[k-3]==JvmOpCodes.GETFIELD);
                      if(getfield)
                      {
                          s6=new StringBuffer("");
                          aloadp=isPrevInstructionAload(k-3,code,s6);
                          index.append(-200);
                          dim.append(0);
                          pos.append(Integer.parseInt(s6.toString()));
                          return;
                      }
                  }
                  if((k-3) >= 0)
                  {
                      boolean getS=(code[k-3]==JvmOpCodes.GETSTATIC);
                      if(getS)
                      {
                          if(code[k-4]==JvmOpCodes.POP)           // TODO: Possible bug with pop2
                          {
                              StringBuffer s3=new StringBuffer("");
                              boolean bb=isPrevInstructionAload(k-4,code,s3);
                              if(bb)
                              {
                                   index.append(-200);
                                  dim.append(0);
                                  pos.append(Integer.parseInt(s3.toString()));
                                  return;
                              }
                          }
                               index.append(-200);
                                  dim.append(0);
                                  pos.append(k-3);
                          return;

                      }
                  }


                  // TODO:newarray ?

                  if((k-4) >= 0)
                  {
                      boolean multi=(code[k-4]==JvmOpCodes.MULTIANEWARRAY);
                      if(multi)
                      {
                          stackHandler=new Stack();
                          InstParam first=new InstParam();
                          first.setNumberOfParamsLeft(code[k-4+3]);
                          first.setCurrentParamOffsetInCode(-1);
                          stackHandler.push(first);
                          handleBasicPrimitiveLoadOp(code,k-5);
                          index.append(-100);
                          dim.append(0);
                          InstParam obj=(InstParam)stackHandler.pop(); // should be first
                          pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                          return;

                      }
                  }
                  if((k-3) >= 0)
                  {

                  boolean multi=(code[k-3]==JvmOpCodes.ANEWARRAY);
                  if(multi)
                  {

                      stackHandler=new Stack();
                      InstParam first=new InstParam();
                      first.setNumberOfParamsLeft(1);
                      first.setCurrentParamOffsetInCode(-1);
                      stackHandler.push(first);
                      handleBasicPrimitiveLoadOp(code,k-3-1);
                      index.append(-100);
                      dim.append(0);
                      InstParam obj=(InstParam)stackHandler.pop(); // should be first
                      pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                      return;


                  }
                  }

                  if((k-2) >= 0)
                  {

                  boolean  multi=(code[k-2]==JvmOpCodes.NEWARRAY);
                  if(multi)
                  {

                      stackHandler=new Stack();
                      InstParam first=new InstParam();
                      first.setNumberOfParamsLeft(1);
                      first.setCurrentParamOffsetInCode(-1);
                      stackHandler.push(first);
                      handleBasicPrimitiveLoadOp(code,k-3);
                      index.append(-100);
                      dim.append(0);
                      InstParam obj=(InstParam)stackHandler.pop(); // should be first
                      pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                      return;


                  }
                  }


              }



           }




            if(!primitive)         // TODO: Need to check for getstatic and getfield also
            {
                boolean prevAAload=isInstAAload(code[k]);      // TODO !!! Can there be any other option???
                boolean prevaload=false;
                if(prevAAload)
                {
                  number++;
                  k=k-1;
                  prevAAload=isInstAAload(code[k]);
                  StringBuffer sb=new StringBuffer("");
                  int posFound=isInstAload(k,code,sb);
                  while(posFound==-1)
                  {
                       if(prevAAload)number++;
                       k=k-1;
                       prevAAload=isInstAAload(code[k]);
                       sb=new StringBuffer("");
                       posFound=isInstAload(k,code,sb);
                       /////////////////////
                        if(posFound==-1)
                          {
                              if(code[k]==JvmOpCodes.GETSTATIC) // TODO: Need to check at all such places whether the index is start of instruction
                              {

                                  if(isThisInstrStart(behaviour.getInstructionStartPositions(),(k-1)) && code[k-1]==JvmOpCodes.POP)           // TODO: Possible bug with pop2
                                  {
                                      StringBuffer s3=new StringBuffer("");
                                      boolean bb=isPrevInstructionAload(k-1,code,s3);
                                      if(bb)
                                      {
                                          index.append(-200);
                                          dim.append(0);
                                          pos.append(Integer.parseInt(s3.toString()));
                                          return;
                                      }
                                  }
                                  else
                                  {
                                      index.append(-200);
                                      dim.append(0);
                                      pos.append(k);
                                      return;
                                  }

                              }
                              if(code[k]==JvmOpCodes.GETFIELD)
                              {
                                  StringBuffer s6=new StringBuffer("");
                                  boolean aloadp=isPrevInstructionAload(k,code,s6);
                                  if(aloadp)
                                  {
                                      index.append(-200);
                                      dim.append(0);
                                      pos.append(Integer.parseInt(s6.toString()));
                                      return;
                                  }
                              }



                          }
                      /////////





                  }

                        index.append(Integer.parseInt(sb.toString()));
                        dim.append(number);
                        pos.append(posFound);
                        return;

                }
            }
            if(primitive)
            {
                if(code[k]==JvmOpCodes.BALOAD || code[k]==JvmOpCodes.CALOAD || code[k]==JvmOpCodes.DALOAD || code[k]==JvmOpCodes.FALOAD || code[k]==JvmOpCodes.LALOAD || code[k]==JvmOpCodes.IALOAD || code[k]==JvmOpCodes.SALOAD)
                {
                    StringBuffer sb=new StringBuffer("");
                    int posFound=isInstAload(k,code,sb);
                    while(posFound==-1)
                    {

                       k=k-1;
                       sb=new StringBuffer("");
                       posFound=isInstAload(k,code,sb);

                    }
                       index.append(Integer.parseInt(sb.toString()));
                        dim.append(0);
                        pos.append(posFound);
                        return;


                }
            }
            if(primitive)
            {
                if(code[k]==JvmOpCodes.BIPUSH)
                {
                    index.append(-200);
                    dim.append(0);
                    pos.append(k);
                    return;
                }
            }

        if(primitive)
        {
                if(code[k]==JvmOpCodes.SIPUSH )
                    {
                        index.append(-200);
                        dim.append(0);
                        pos.append(k);
                        return;
                    }
        }
        StringBuffer sfb;
           if(!primitive)
           {
               sfb=new StringBuffer("");
               int y=isInstAload(k,code,sfb);
               if(y!=-1)
               {
                    index.append(Integer.parseInt(sfb.toString()));
                    dim.append(0);
                    pos.append(y);
                    return;
               }
           }

        if(primitive)
        {
           sfb=new StringBuffer("");
           int y=isInstIloadInst(code,k,sfb);
           if(y!=-1)
          {
                index.append(Integer.parseInt(sfb.toString()));
                dim.append(0);
                pos.append(y);
                return;
          }
        }

        if(type.equals("float") || type.equals("double"))
        {
           sfb=new StringBuffer("");
            int y=isInstFloadInst(code,k,sfb);
        if(y!=-1)
          {
                index.append(Integer.parseInt(sfb.toString()));
                dim.append(0);
                pos.append(y);
                return;
          }
        }

        if(type.equals("double"))
        {
           sfb=new StringBuffer("");
            int y=isInstDloadInst(code,k,sfb);
          if(y!=-1)
          {
                index.append(Integer.parseInt(sfb.toString()));
                dim.append(0);
                pos.append(y);
                return;
          }
        }


        if(type.equals("float") || type.equals("double"))
        {
          if(code[k]==JvmOpCodes.D2F)// || code[k]==JvmOpCodes.D2I || code[k]==JvmOpCodes.D2L)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int dload=isInstDloadInst(code,t,strb);
                if(dload!=-1)
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(dload);
                    return;
                }
                else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
              stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;

          }
        }
        if(primitive)
        {
          if(code[k]==JvmOpCodes.D2I )//|| code[k]==JvmOpCodes.D2L)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int dload=isInstDloadInst(code,t,strb);
                if(dload!=-1)
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(dload);
                    return;
                }
               else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
                 stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;
            }
        }
        if(type.equals("long") || type.equals("float") || type.equals("double"))
        {
          if(code[k]==JvmOpCodes.D2L)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int dload=isInstDloadInst(code,t,strb);
                if(dload!=-1 )
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(dload);
                    return;
                }
              else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
                 stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;


            }
        }


          if(primitive)
          {
          if(code[k]==JvmOpCodes.I2B || code[k]==JvmOpCodes.I2C  || code[k]==JvmOpCodes.I2S )// || code[k]==JvmOpCodes.I2D || code[k]==JvmOpCodes.I2F ||  code[k]==JvmOpCodes.I2L || code[k]==JvmOpCodes.I2S )
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Iload=isInstIloadInst(code,t,strb);
                if(Iload!=-1 )
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Iload);
                    return;
                }
                else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }


                }
                 stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;



             }
          }



        if(type.equals("double"))
          {
          if(code[k]==JvmOpCodes.I2D)// || code[k]==JvmOpCodes.I2F ||  code[k]==JvmOpCodes.I2L || code[k]==JvmOpCodes.I2S )
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Iload=isInstIloadInst(code,t,strb);
                if(Iload!=-1 )
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Iload);
                    return;
                }
              else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
                 stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;



             }

          }

        if(type.equals("double") || type.equals("float"))
          {
          if(code[k]==JvmOpCodes.I2F)// ||  code[k]==JvmOpCodes.I2L || code[k]==JvmOpCodes.I2S )
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Iload=isInstIloadInst(code,t,strb);
                if(Iload!=-1 )
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Iload);
                    return;
                }
              else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
                 stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;
             }
          }


         if(type.equals("double") || type.equals("float") || type.equals("long"))
          {
          if(code[k]==JvmOpCodes.I2L)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Iload=isInstIloadInst(code,t,strb);
                if(Iload!=-1 )
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Iload);
                    return;
                }
              else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
                 stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;
             }
          }


        if(type.equals("double"))
        {
        if(code[k]==JvmOpCodes.F2D )//|| code[k]==JvmOpCodes.F2I || code[k]==JvmOpCodes.F2L)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Fload=isInstFloadInst(code,t,strb);
                if(Fload!=-1)
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Fload);
                    return;
                }
            else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
             stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;
            }
        }


        if(type.equals("double") || type.equals("float") || type.equals("long"))
        {
        if(code[k]==JvmOpCodes.F2L )//|| code[k]==JvmOpCodes.F2I || code[k]==JvmOpCodes.F2L)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Fload=isInstFloadInst(code,t,strb);
                if(Fload!=-1)
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Fload);
                    return;
                }
            else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                       return;
                    }

                }
             stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;
            }
        }
        if(primitive)
        {
        if( code[k]==JvmOpCodes.F2I)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Fload=isInstFloadInst(code,t,strb);
                if(Fload!=-1 )
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Fload);
                    return;
                }
            else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
             stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;
            }
        }



         if(type.equals("double"))
         {
         if(code[k]==JvmOpCodes.L2D)// || code[k]==JvmOpCodes.L2F || code[k]==JvmOpCodes.L2I)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Fload=isInstLloadInst(code,t,strb);
                if(Fload!=-1)
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Fload);
                    return;
                }
             else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
              stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;

        }
         }


        if(type.equals("double") || type.equals("float"))
         {
         if(code[k]==JvmOpCodes.L2F)// || code[k]==JvmOpCodes.L2I)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Fload=isInstLloadInst(code,t,strb);
                if(Fload!=-1)
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Fload);
                    return;
                }
               else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
              stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;

        }
         }


        if(primitive)
         {
         if(code[k]==JvmOpCodes.L2I)
          {

                int t=k-1;
                StringBuffer strb=new StringBuffer("");
                int Fload=isInstLloadInst(code,t,strb);
                if(Fload!=-1)
                {
                    index.append(-100);
                    dim.append(0);
                    pos.append(Fload);
                    return;
                }
             else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        index.append(-100);
                        dim.append(0);
                        pos.append(loadin);
                        return;
                    }

                }
              stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(1);
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode());
              return;

        }
         }
        int tmp=k;
        if(code[tmp]==JvmOpCodes.LDC)
        {
              index.append(-100);
                dim.append(0);
                pos.append(tmp);
                return;
        }

      if(code[tmp]==JvmOpCodes.LDC_W )// || code[tmp]==JvmOpCodes.LDC2_W)
      {
            index.append(-100);
              dim.append(0);
              pos.append(tmp);
              return;
      }
       if(type.equals("long") || type.equals("float") || type.equals("double"))
       {
         tmp=k;
          if(code[tmp]==JvmOpCodes.LDC2_W )
          {
                index.append(-100);
                  dim.append(0);
                  pos.append(tmp);
                  return;
          }
       }

        if(!primitive)
        {
          if(code[k]==JvmOpCodes.ACONST_NULL)
          {
                index.append(-100);
                dim.append(0);
                pos.append(k);
                return;
          }
        }
        if(primitive)
        {
        if(isInstIConstInst(code,k)!=-1 )    //
          {
                index.append(-100);
                dim.append(0);
                pos.append(k);
                return;
          }
        }
        if(type.equals("float") || type.equals("double"))
        {
        if(isInstFConstInst(code,k)!=-1 )
          {
                index.append(-100);
                dim.append(0);
                pos.append(k);
                return;
          }
        }

        if(type.equals("double"))
        {
        if(isInstdConstInst(code,k)!=-1 )
          {
                index.append(-100);
                dim.append(0);
                pos.append(k);
                return;
          }
        }
        if(type.equals("float") || type.equals("double") || type.equals("long"))
        {
        if(isInstLConstInst(code,k)!=-1 )
          {
                index.append(-100);
                dim.append(0);
                pos.append(k);
                return;
          }
        }

        if(!primitive)
        {

               if(code[k]==JvmOpCodes.MULTIANEWARRAY )
               {
                   stackHandler=new Stack();
                   InstParam first=new InstParam();
                   first.setNumberOfParamsLeft(code[k+3]);
                   first.setCurrentParamOffsetInCode(-1);
                   stackHandler.push(first);
                   handleBasicPrimitiveLoadOp(code,k-1);
                   index.append(-100);
                   dim.append(0);
                   InstParam obj=(InstParam)stackHandler.pop(); // should be first
                   pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                   return;
               }
               if(code[k]==JvmOpCodes.ANEWARRAY || code[k]==JvmOpCodes.NEWARRAY)
               {

                   stackHandler=new Stack();
                   InstParam first=new InstParam();
                   first.setNumberOfParamsLeft(1);
                   first.setCurrentParamOffsetInCode(-1);
                   stackHandler.push(first);
                   handleBasicPrimitiveLoadOp(code,k-1);
                   index.append(-100);
                   dim.append(0);
                   InstParam obj=(InstParam)stackHandler.pop(); // should be first
                   pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                   return;


               }



        }

        if(primitive){

            if(code[k]==JvmOpCodes.INSTANCEOF)
            {
                if(isThisInstrStart(behaviour.getInstructionStartPositions(),(k-2)) && code[k-2]==JvmOpCodes.NEWARRAY)
                {

                    stackHandler=new Stack();
                    InstParam first=new InstParam();
                    first.setNumberOfParamsLeft(1);
                    first.setCurrentParamOffsetInCode(-1);
                    stackHandler.push(first);
                    handleBasicPrimitiveLoadOp(code,k-3);
                    index.append(-100);
                    dim.append(0);
                    InstParam obj=(InstParam)stackHandler.pop(); // should be first
                    pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                    return;



                }
                StringBuffer s8=new StringBuffer("");
                boolean aloadp=isPrevInstructionAload(k,code,s8);
                if(aloadp)
                {

                    index.append(-100);
                    dim.append(0);
                    pos.append(Integer.parseInt(s8.toString()));
                    return;
                }
                if(isThisInstrStart(behaviour.getInstructionStartPositions(),(k-3)) && code[k-3]==JvmOpCodes.ANEWARRAY)
                {

                    stackHandler=new Stack();
                    InstParam first=new InstParam();
                    first.setNumberOfParamsLeft(1);
                    first.setCurrentParamOffsetInCode(-1);
                    stackHandler.push(first);
                    handleBasicPrimitiveLoadOp(code,k-4);
                    index.append(-100);
                    dim.append(0);
                    InstParam obj=(InstParam)stackHandler.pop(); // should be first
                    pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                    return;
                }
                if(isThisInstrStart(behaviour.getInstructionStartPositions(),(k-4)) && code[k-4]==JvmOpCodes.MULTIANEWARRAY)
                {
                    stackHandler=new Stack();
                    InstParam first=new InstParam();
                    first.setNumberOfParamsLeft(code[k-4+3]);
                    first.setCurrentParamOffsetInCode(-1);
                    stackHandler.push(first);
                    handleBasicPrimitiveLoadOp(code,k-5);
                    index.append(-100);
                    dim.append(0);
                    InstParam obj=(InstParam)stackHandler.pop(); // should be first
                    pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                    return;
                }


            }


        }




        if(!primitive)
        {
             if(code[k]==JvmOpCodes.CHECKCAST)
             {
               if(isThisInstrStart(behaviour.getInstructionStartPositions(),(k-4)) && code[k-4]==JvmOpCodes.MULTIANEWARRAY )
               {
                   stackHandler=new Stack();
                   InstParam first=new InstParam();
                   first.setNumberOfParamsLeft(code[k-4+3]);
                   first.setCurrentParamOffsetInCode(-1);
                   stackHandler.push(first);
                   handleBasicPrimitiveLoadOp(code,k-5);
                   index.append(-100);
                   dim.append(0);
                   InstParam obj=(InstParam)stackHandler.pop(); // should be first
                   pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                   return;
               }
               else if(isThisInstrStart(behaviour.getInstructionStartPositions(),(k-3)) && code[k-3]==JvmOpCodes.ANEWARRAY)
               {

                   stackHandler=new Stack();
                   InstParam first=new InstParam();
                   first.setNumberOfParamsLeft(1);
                   first.setCurrentParamOffsetInCode(-1);
                   stackHandler.push(first);
                   handleBasicPrimitiveLoadOp(code,k-4);
                   index.append(-100);
                   dim.append(0);
                   InstParam obj=(InstParam)stackHandler.pop(); // should be first
                   pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                   return;


               }
               else if(isThisInstrStart(behaviour.getInstructionStartPositions(),(k-2)) && code[k-2]==JvmOpCodes.NEWARRAY)
               {

                   stackHandler=new Stack();
                   InstParam first=new InstParam();
                   first.setNumberOfParamsLeft(1);
                   first.setCurrentParamOffsetInCode(-1);
                   stackHandler.push(first);
                   handleBasicPrimitiveLoadOp(code,k-3);
                   index.append(-100);
                   dim.append(0);
                   InstParam obj=(InstParam)stackHandler.pop(); // should be first
                   pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
                   return;


               }
               else
               {
                   StringBuffer s5=new StringBuffer("");
                   boolean aload=isPrevInstructionAload(k,code,s5);
                   if(aload)
                   {
                        index.append(-100);
                        dim.append(0);
                        pos.append(Integer.parseInt(s5.toString()));
                        return;
                   }

               }

                 // Check for other object refs....
                 if((k-1) >= 0)
                 {
                 boolean aaloadp=isInstAAload(code[k-1]);           // BUG check for getstatic alos.:TODO
                 if(aaloadp)
                 {
                     StringBuffer s6=new StringBuffer("");
                     int in=k-1;
                     int posFound=isInstAload(in,code,s6);
                     while(posFound==-1)
                     {
                         in=in-1;
                         posFound=isInstAload(in,code,s6);
                         if(posFound==-1)
                         {
                             if(code[in]==JvmOpCodes.GETSTATIC) // TODO: Need to check at all such places whether the index is start of instruction
                             {

                                 if(isThisInstrStart(behaviour.getInstructionStartPositions(),(in-1)) && code[in-1]==JvmOpCodes.POP)           // TODO: Possible bug with pop2
                                 {
                                     StringBuffer s3=new StringBuffer("");
                                     boolean bb=isPrevInstructionAload(in-1,code,s3);
                                     if(bb)
                                     {
                                         index.append(-200);
                                         dim.append(0);
                                         pos.append(Integer.parseInt(s3.toString()));
                                         return;
                                     }
                                 }
                                 else
                                 {
                                     index.append(-200);
                                     dim.append(0);
                                     pos.append(in);
                                     return;
                                 }

                             }
                             if(code[in]==JvmOpCodes.GETFIELD)
                             {
                                 s6=new StringBuffer("");
                                 boolean aloadp=isPrevInstructionAload(in,code,s6);
                                 if(aloadp)
                                 {
                                     index.append(-200);
                                     dim.append(0);
                                     pos.append(Integer.parseInt(s6.toString()));
                                     return;
                                 }
                             }



                         }

                     }
                     index.append(-200);
                     dim.append(0);
                     pos.append(posFound);
                     return;

                 }
             } // here
                 if((k-3) >= 0)
                 {
                 boolean getfield=(code[k-3]==JvmOpCodes.GETFIELD);
                 if(getfield)
                 {
                     StringBuffer s6=new StringBuffer("");
                     boolean aloadp=isPrevInstructionAload(k-3,code,s6);
                     if(aloadp)
                     {
                         index.append(-200);
                         dim.append(0);
                         pos.append(Integer.parseInt(s6.toString()));
                         return;
                     }
                 }
                 }
                 if((k-3) >= 0)
                 {
                     boolean getS=(code[k-3]==JvmOpCodes.GETSTATIC);
                     if(getS)
                     {
                         if(code[k-4]==JvmOpCodes.POP)           // TODO: Possible bug with pop2
                         {
                             StringBuffer s3=new StringBuffer("");
                             boolean bb=isPrevInstructionAload(k-4,code,s3);
                             if(bb)
                             {
                                 index.append(-200);
                                 dim.append(0);
                                 pos.append(Integer.parseInt(s3.toString()));
                                 return;
                             }
                         }
                         index.append(-200);
                         dim.append(0);
                         pos.append(k-3);
                         return;

                     }
                 }

                 //if(isNextInstructionAnyInvoke(code))
                 Integer isinvokestart=(Integer)invokeStartEnd.get(new Integer(k-1));
                 if(isinvokestart!=null)
                 {

                     Integer refpos=(Integer)invokeinstrefpos.get(new Integer(isinvokestart.intValue()));
                     if(refpos!=null)
                     {
                         index.append(-200);
                         dim.append(0);
                         pos.append(refpos.intValue());
                         return;
                     }


                 }



             }

        }




        if(primitive)
        {
           StringBuffer sb=new StringBuffer("");
           boolean p=isInstAnyBasicPrimitiveOperation(code,k,sb);

          if(p)
          {
              // belurs:

              // Here need to call a function whill will handle tyes like imul,lmul,ldic etc
              // This function needs to be recursive. Handle this function with care
              // In this case the code will not set the index position because
              // any complex sequence of jvm blocks can be present for this
              // instruction itself. so for time being , what is important is that
              // this function should return load position for this jvm instr so that
              // for the next parameter the code will know from where to continue.


              stackHandler=new Stack();
              InstParam first=new InstParam();
              first.setNumberOfParamsLeft(Integer.parseInt(sb.toString()));
              first.setCurrentParamOffsetInCode(-1);
              stackHandler.push(first);
              handleBasicPrimitiveLoadOp(code,k-1);
              index.append(-100);
              dim.append(0);
              InstParam obj=(InstParam)stackHandler.pop(); // should be first
              pos.append(obj.getCurrentParamOffsetInCode()); // TODO: check whether this is correct
              return;

          }

        }








        // This 2 should be last.
        // Any new thing should come before this 2
        if(code[k]==JvmOpCodes.GETFIELD)
        {
            StringBuffer sb1=new StringBuffer("");

            int pos2=isInstAload(k-1,code,sb1);
            if(pos2!=-1)
            {
                index.append(Integer.parseInt(sb1.toString()));
                dim.append(0);
                pos.append(pos2);
                return;
            }
        }
        if(code[k]==JvmOpCodes.GETSTATIC)  // POSSIBLE BUG: todo
        {


                index.append(-100);
                dim.append(0);
                pos.append(k);
                return;

        }







       //}


    }


    private int isInstIloadInst(byte[] code,int s,StringBuffer sb2)
      {
          if(isThisInstrStart(behaviour.getInstructionStartPositions(),s))
          {
          switch(code[(s)])
          {
              case JvmOpCodes.ILOAD_0:
                  sb2.append(0);
                  return s;

              case JvmOpCodes.ILOAD_1:

                  sb2.append(1);
                  return s;

              case JvmOpCodes.ILOAD_2:

                  sb2.append(2);
                  return s;


              case JvmOpCodes.ILOAD_3:

                  sb2.append(3);
                  return s;

             case JvmOpCodes.ILOAD:
                 sb2.append(code[s+1]);
                  return s;

             }
          }
             int temp=s-1;
            if(temp >=0 && isThisInstrStart(behaviour.getInstructionStartPositions(),temp) && code[temp]==JvmOpCodes.ILOAD)
            {   sb2.append(code[s]);
                return temp;

            }


                   return -1;



          }

    private int isInstFloadInst(byte[] code,int s,StringBuffer sb2)
          {
              if(isThisInstrStart(behaviour.getInstructionStartPositions(),s))
              {
              switch(code[(s)])
              {
                  case JvmOpCodes.FLOAD_0:
                      sb2.append(0);
                      return s;

                  case JvmOpCodes.FLOAD_1:

                      sb2.append(1);
                      return s;

                  case JvmOpCodes.FLOAD_2:

                      sb2.append(2);
                      return s;


                  case JvmOpCodes.FLOAD_3:

                      sb2.append(3);
                      return s;

                  case JvmOpCodes.FLOAD :

                      sb2.append(code[(s+1)]);
                      return s;



              }
              }

                 int temp=s-1;
            if(isThisInstrStart(behaviour.getInstructionStartPositions(),temp) && code[temp]==JvmOpCodes.FLOAD)
            {   sb2.append(code[s]);
                return temp;

            }


                       return -1;



              }

    private int isInstDloadInst(byte[] code,int s,StringBuffer sb2)
             {
                 if(isThisInstrStart(behaviour.getInstructionStartPositions(),s))
                 {

                 switch(code[(s)])
                 {
                     case JvmOpCodes.DLOAD_0:
                         sb2.append(0);
                         return s;

                     case JvmOpCodes.DLOAD_1:

                         sb2.append(1);
                         return s;

                     case JvmOpCodes.DLOAD_2:

                         sb2.append(2);
                         return s;


                     case JvmOpCodes.DLOAD_3:

                         sb2.append(3);
                         return s;

                    case JvmOpCodes.DLOAD:
                        sb2.append(code[s+1]);
                         return s;

                 }
                 }
                  int temp=s-1;
            if(isThisInstrStart(behaviour.getInstructionStartPositions(),temp) && code[temp]==JvmOpCodes.DLOAD)
            {   sb2.append(code[s]);
                return temp;

            }

                          return -1;



                 }


    private int isInstLloadInst(byte[] code,int s,StringBuffer sb2)
          {
                 if(isThisInstrStart(behaviour.getInstructionStartPositions(),s))
                 {
              switch(code[(s)])
              {
                  case JvmOpCodes.LLOAD_0:
                      sb2.append(0);
                      return s;

                  case JvmOpCodes.LLOAD_1:

                      sb2.append(1);
                      return s;

                  case JvmOpCodes.LLOAD_2:

                      sb2.append(2);
                      return s;


                  case JvmOpCodes.LLOAD_3:

                      sb2.append(3);
                      return s;

                 /*case JvmOpCodes.ILOAD:
                     sb2.append(code[s+1]);
                      return s;*/

                 }
                 }
                 int temp=s-1;
           if(isThisInstrStart(behaviour.getInstructionStartPositions(),temp))
                 {
                if(code[temp]==JvmOpCodes.LLOAD)
                {   sb2.append(code[s]);
                    return temp;

                }
           }


                       return -1;



              }


    private int isInstIConstInst(byte[] code,int k)
        {
              if(isThisInstrStart(behaviour.getInstructionStartPositions(),k))
                 {
            switch(code[k])
            {
                case JvmOpCodes.ICONST_0:
                case JvmOpCodes.ICONST_1:
                case JvmOpCodes.ICONST_2:
                case JvmOpCodes.ICONST_3:
                case JvmOpCodes.ICONST_M1:
                case JvmOpCodes.ICONST_4:
                case JvmOpCodes.ICONST_5:
                    return k;

            }
              }
        return -1;
    }
    private int isInstLConstInst(byte[] code,int k)
        {
               if(isThisInstrStart(behaviour.getInstructionStartPositions(),k))
                 {
            switch(code[k])
            {
                case JvmOpCodes.LCONST_0:
                     case JvmOpCodes.LCONST_1:
                         return k;
            }
               }
        return -1;

    }

    private int isInstFConstInst(byte[] code,int k)
        {
                if(isThisInstrStart(behaviour.getInstructionStartPositions(),k))
                 {
            switch(code[k])
            {
                case JvmOpCodes.FCONST_0:
                     case JvmOpCodes.FCONST_1:
                     case JvmOpCodes.FCONST_2:
                                   return k;
            }
                }
        return -1;
    }

    private int isInstdConstInst(byte[] code,int k)
        {
             if(isThisInstrStart(behaviour.getInstructionStartPositions(),k))
                 {
            switch(code[k])
            {
                case JvmOpCodes.DCONST_0:
                     case JvmOpCodes.DCONST_1:
                              return k;
            }
             }
        return -1;
    }
    private int isInstAnyConstInst(byte[] code,int k)
    {
           if(isThisInstrStart(behaviour.getInstructionStartPositions(),k))
                 {
        switch(code[k])
        {
            case JvmOpCodes.ICONST_0:
            case JvmOpCodes.ICONST_1:
            case JvmOpCodes.ICONST_2:
            case JvmOpCodes.ICONST_3:
            case JvmOpCodes.ICONST_M1:
            case JvmOpCodes.ICONST_4:
            case JvmOpCodes.ICONST_5:
            case JvmOpCodes.LCONST_0:
            case JvmOpCodes.LCONST_1:
            case JvmOpCodes.DCONST_0:
            case JvmOpCodes.DCONST_1:
            case JvmOpCodes.FCONST_0:
            case JvmOpCodes.FCONST_1:
            case JvmOpCodes.FCONST_2:
                return k;
              default:
                return -1;
        }
           }



       return -1;

    }


    private boolean isPrimitive(java.lang.String type)
    {
        if(type.equals("int") || type.equals("short") || type.equals("char") || type.equals("byte") || type.equals("long") || type.equals("float") || type.equals("long") || type.equals("double") || type.equals("boolean"))
            return true;
        else
            return false;
    }


    private int traceLoadInst(int t,byte[] code,StringBuffer strb)
    {
        int pos=-1;
        int k=t;

        int getFieldPos=k-2;



          int invokebegin=-1;
               if(invokeStartEnd!=null)
               {
                   Integer in=new Integer(k);
                   Integer invs=(Integer)invokeStartEnd.get(in);
                   if(invs!=null)
                   {
                       invokebegin=invs.intValue();
                   }
               }

        if(isThisInstrStart(behaviour.getInstructionStartPositions(),k) && code[k]==JvmOpCodes.BALOAD || code[k]==JvmOpCodes.CALOAD || code[k]==JvmOpCodes.DALOAD || code[k]==JvmOpCodes.FALOAD || code[k]==JvmOpCodes.LALOAD || code[k]==JvmOpCodes.IALOAD || code[k]==JvmOpCodes.SALOAD)
        {


            int posFound=isInstAload(k,code,strb);
            while(posFound==-1)
            {

                k=k-1;
                strb=new StringBuffer("");
                posFound=isInstAload(k,code,strb);

            }
            return posFound;

        }
        else if(invokebegin!=-1 && isThisInstrStart(behaviour.getInstructionStartPositions(),invokebegin))
               {
                   if(isNextInstructionAnyInvoke(code[invokebegin],new StringBuffer("")))
                   {
                          if(invokeinstrefpos!=null)
                          {
                              Integer in=(Integer)invokeinstrefpos.get(new Integer(invokebegin ));
                              if(in!=null)
                              {
                                int start=(in).intValue();
                                return start;
                              }


                          }
                   }
               }

     else if(code[getFieldPos]==JvmOpCodes.GETFIELD && isThisInstrStart(behaviour.getInstructionStartPositions(),getFieldPos))
     {
        StringBuffer sb=new StringBuffer("");
        boolean ok=isPrevInstructionAload(getFieldPos,code,sb);
        if(ok)
        {
            int ref=Integer.parseInt(sb.toString());
            return ref;
        }


     }
       else if(isThisInstrStart(behaviour.getInstructionStartPositions(),getFieldPos) && code[getFieldPos]==JvmOpCodes.GETSTATIC)
     {

                 if(code[getFieldPos-1]==JvmOpCodes.POP)           // TODO: Possible bug with pop2
                 {
                     StringBuffer s3=new StringBuffer("");
                     boolean bb=isPrevInstructionAload(getFieldPos-1,code,s3);
                     if(bb)
                     {
                         return Integer.parseInt(s3.toString());
                     }
                 }

                 return getFieldPos;
       }
        else
        {

           // NOTE: The name is slightly misleading....what we want here is actually the previous inst...
           // Using this as it serves the pupose
           if(isNextInstructionConversionInst(code[k]))
           {

               int g=k-1;
               int invokened=-1;
               while(isNextInstructionConversionInst(code[g]))
               {
                      g=g-1;
               }
            invokebegin=-1;
               if(invokeStartEnd!=null)
               {
                   Integer in=new Integer(g);
                   Integer invs=(Integer)invokeStartEnd.get(in);
                   if(invs!=null)
                   {
                       invokebegin=invs.intValue();
                   }
               }
               StringBuffer sbf=    new StringBuffer("");
               if(isPrevInstPrimitiveLoad(code,(g+1),sbf))
               {
                 pos=Integer.parseInt(sbf.toString());
                 return pos;
               }
               else if(invokebegin!=-1)
               {
                   if(isNextInstructionAnyInvoke(code[invokebegin],new StringBuffer("")))
                   {
                          if(invokeinstrefpos!=null)
                          {
                              Integer in=(Integer)invokeinstrefpos.get(new Integer(invokebegin ));
                              if(in!=null)
                              {
                                int start=(in).intValue();
                                return start;
                              }


                          }
                   }
               }
               else
               {
                   k=g;
                   if(isThisInstrStart(behaviour.getInstructionStartPositions(),k) && (code[k]==JvmOpCodes.BALOAD || code[k]==JvmOpCodes.CALOAD || code[k]==JvmOpCodes.DALOAD || code[k]==JvmOpCodes.FALOAD || code[k]==JvmOpCodes.LALOAD || code[k]==JvmOpCodes.IALOAD || code[k]==JvmOpCodes.SALOAD))
                   {


                       int posFound=isInstAload(k,code,strb);
                       while(posFound==-1)
                       {

                           k=k-1;
                           strb=new StringBuffer("");
                           posFound=isInstAload(k,code,strb);

                       }
                       return posFound;

                   }
               }


           }


        }



        return pos;
    }



    private int findWhereThisInvokeEnds(int s,byte[] code)
    {
        int temp=s;
        if(code[s]==JvmOpCodes.INVOKEINTERFACE)
        {
           temp=temp+4;
        }
        else
        {
            temp+=2;
        }
        return temp;

    }

    private boolean checkForAssociatedGetField(ArrayList getField,int s,StringBuffer str,byte[] code)
    {
        boolean ret=false;

        HashMap map= ConsoleLauncher.getInstructionMap();
        if(getField==null || getField.size()==0)return ret;
        else
        {
           for(int z=getField.size()-1;z>=0;z--)
           {
               Integer in=(Integer)getField.get(z);
               int Iin=in.intValue();
               if(s > Iin)  // REQD getField
               {
                   int temp1=Iin;
                   int temp2=s;
                   if(map!=null)
                   {
                       Integer skip=(Integer)map.get(new Integer(temp1));
                       if(skip!=null)
                       {

                            int iskip=skip.intValue();
                            int next=temp1+iskip+1;
                            if(next < code.length)temp1=next;
                       }

                   }

                   for(int x=temp1;x<temp2;x++)
                   {
                       int inst=code[x];
                       switch(inst)
                       {
                           case JvmOpCodes.INVOKEVIRTUAL:
                           case JvmOpCodes.INVOKESTATIC:
                           case JvmOpCodes.INVOKESPECIAL:
                           case JvmOpCodes.INVOKEINTERFACE:
                                                           return false;

                           default:
                                    ret=true;
                       }
                   }

                   if(ret){
                       str.append(Iin);
                       return ret;

                   }
               }

           }

        }
        return ret;
    }


    private boolean checkForAssociatedGetStatic(ArrayList getStatic,int s,StringBuffer str,byte[] code)
    {

        boolean ret=false;
        HashMap map= ConsoleLauncher.getInstructionMap();
        if(getStatic==null || getStatic.size()==0)return ret;
        else
        {
           for(int z=0;z<getStatic.size();z++)
           {
               Integer in=(Integer)getStatic.get(z);
               int Iin=in.intValue();
               if(s > Iin)  // REQD getStatic
               {
                   int temp1=Iin;
                   int temp2=s;
                   if(map!=null)
                   {
                       Integer skip=(Integer)map.get(new Integer(temp1));
                       if(skip!=null)
                       {

                            int iskip=skip.intValue();
                            int next=temp1+iskip+1;
                            if(next < code.length)temp1=next;
                       }

                   }

                   for(int x=temp1;x<temp2;x++)
                   {
                       if(isThisInstrStart(behaviour.getInstructionStartPositions(),x))
                       {
                           int inst=code[x];
                           switch(inst)
                           {
                               case JvmOpCodes.INVOKEVIRTUAL:
                               case JvmOpCodes.INVOKESTATIC:
                               case JvmOpCodes.INVOKESPECIAL:
                               case JvmOpCodes.INVOKEINTERFACE:
                                                               return false;

                               default:
                                        ret=true;
                           }
                       }
                   }

                   if(ret){str.append(Iin);return ret;}
               }

           }

        }
        return ret;



    }


    private boolean checkForAssociatedInvokeSpecial(Hashtable invokespecialpos,int s,StringBuffer str,byte []code)
    {

        boolean ret=false;
        HashMap map= ConsoleLauncher.getInstructionMap();
        if(invokespecialpos!=null && invokespecialpos.size() > 0)
        {
            Set set=invokespecialpos.keySet();
            Integer[] karr=(Integer[])set.toArray(new Integer[set.size()]);
            Arrays.sort(karr);
            for(int z=0;z<karr.length;z++)
            {
                Integer in=(Integer)karr[z];
                int Iin=in.intValue();
                if(s > Iin)  // REQD invokespec
                {
                    int temp1=Iin;
                    int temp2=s;
                    if(map!=null)
                    {
                        Integer skip=(Integer)map.get(new Integer(temp1));
                        if(skip!=null)
                        {

                            int iskip=skip.intValue();
                            int next=temp1+iskip+1;
                            if(next < code.length)temp1=next;
                        }

                    }

                    for(int x=temp1;x<temp2;x++)
                    {
                        if(isThisInstrStart(behaviour.getInstructionStartPositions(),x))
                        {
                        int inst=code[x];
                        switch(inst)
                        {
                            case JvmOpCodes.INVOKEVIRTUAL:
                            case JvmOpCodes.INVOKESTATIC:
                            case JvmOpCodes.INVOKESPECIAL:
                            case JvmOpCodes.INVOKEINTERFACE:
                                return false;

                            default:
                                ret=true;
                        }
                        }
                    }

                    if(ret){str.append(invokespecialpos.get(new Integer(Iin)));return ret;}
                }

            }




        }
        return false;



    }


    private int getObjectRefForGetField(int iS,byte[] code)
    {
        StringBuffer sb=new StringBuffer("");
        boolean ok=isPrevInstructionAload(iS,code,sb);
        if(ok)
        {
            int ref=Integer.parseInt(sb.toString());
            return ref;
        }
        //System.out.println("ERROR in getObjectRefForGetField "+iS);
        return iS;

    }



    private boolean isThisInstrStart(ArrayList list,int pos)
    {
        boolean ok=false;
        if(list==null)throw new NullPointerException("Starts pos is null in disassembler");
        if(list!=null)
        {
            for(int k=0;k<list.size();k++)
            {
               Integer in=(Integer)list.get(k);
               if(in!=null)
               {
                   int i=in.intValue();
                   if(i==pos)return !ok;
               }
            }
        }
        return ok;

    }

    private boolean isThisLoopStart(IFBlock IF,ArrayList list,byte[] code )
    {
        boolean ok=false;
        ok=isThisIfALoopCondition(IF,code,list);
        return ok;
    }

    // currentForIndex --> goto start
    private int getElseCloseFromInRangeIfStructures(IFBlock ifs,int currentForIndex)
    {
        int i=-1;
        ArrayList ifbytecodestarts=new ArrayList();
        IFBlock inrangeifs[]=getInRangeIFS(ifs.getIfStart(),currentForIndex);
        if(inrangeifs!=null)
        {
            for(int s=0;s<inrangeifs.length;s++)
            {
              int j=inrangeifs[s].getIfCloseFromByteCode();
              if(j > ifs.getIfCloseFromByteCode())
              {
                  ifbytecodestarts.add(new Integer(j));
              }

            }
            if(ifbytecodestarts.size() > 0)
            {
                 Integer ins[]=(Integer[])ifbytecodestarts.toArray(new Integer[ifbytecodestarts.size()]);
                 Arrays.sort(ins);
                 return ins[0].intValue();
            }
            else
                return i;
        }
        return i;

    }


    // s --> Some if start
    // e--> goto start


    private IFBlock[] getInRangeIFS(int s,int e)
    {
        Collection ifs=getCurrentIFStructues();
        if(ifs==null || ifs.size()==0)return null;
        ArrayList list=new ArrayList();
        int start=s+2+1;    // ifstart+skipbytes+1 --> start of next inst
        Iterator it=ifs.iterator();
        while(it.hasNext())
        {
            IFBlock cur=(IFBlock)it.next();
            int ifstart=cur.getIfStart();
            if(ifstart >= start && ifstart < e)
            {
               list.add(cur);
            }
        }


        if(list.size() > 0)
        return (IFBlock[])list.toArray(new IFBlock[list.size()]);
        else
        return null;
    }


    private java.lang.String anyWhileBodyHere(int e,Loop iloop,OperandStack opStack)
    {
        java.lang.String str="";
        byte[] code=behaviour.getCode();
        Operand ops[]=new Operand[2] ;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),e))
        {
        switch(code[e])
        {

            case JvmOpCodes.IF_ACMPEQ:
                  ops=getIFArgs(opStack,2);
                  if(ops!=null)
                  {
                     str="\nif("+ops[1].getOperandValue()+"=="+ops[0].getOperandValue()+"))\n{\n}\nelse\n{\nbreak\n}\n;";
                  }
                  else
                  {
                      str="";
                  }
                  break;
            case JvmOpCodes.IF_ACMPNE:
                  ops=getIFArgs(opStack,2);
                  if(ops!=null)
                  {
                     str="\nif(!"+ops[1].getOperandValue()+"=="+ops[0].getOperandValue()+"))\n{\n}\nelse\n{\nbreak\n}\n;";
                  }
                  else
                  {
                      str="";
                  }
                  break;
            case JvmOpCodes.IF_ICMPEQ:

                ops=getIFArgs(opStack,2);
                  if(ops!=null)
                  {
                     str="\nif("+ops[1].getOperandValue()+" == "+ops[0].getOperandValue()+")\n{\n}\nelse\n{\nbreak;\n}\n";
                  }
                  else
                  {
                      str="";
                  }
                  break;
            case JvmOpCodes.IF_ICMPGE:
                ops=getIFArgs(opStack,2);
                  if(ops!=null)
                  {
                     str="\nif("+ops[1].getOperandValue()+" >= "+ops[0].getOperandValue()+")\n{\n}\nelse\n{\nbreak;\n}\n";
                  }
                  else
                  {
                      str="";
                  }
                  break;

            case JvmOpCodes.IF_ICMPGT:
                ops=getIFArgs(opStack,2);
                  if(ops!=null)
                  {
                     str="\nif("+ops[1].getOperandValue()+" > "+ops[0].getOperandValue()+")\n{\n}\nelse\n{\nbreak;\n}\n";
                  }
                  else
                  {
                      str="";
                  }
                  break;

            case JvmOpCodes.IF_ICMPLE:
                ops=getIFArgs(opStack,2);
                  if(ops!=null)
                  {
                     str="\nif("+ops[1].getOperandValue()+" <= "+ops[0].getOperandValue()+")\n{\n}\nelse\n{\nbreak;\n}\n";
                  }
                  else
                  {
                      str="";
                  }
                  break;

            case JvmOpCodes.IF_ICMPLT:
                ops=getIFArgs(opStack,2);
                  if(ops!=null)
                  {
                     str="\nif("+ops[1].getOperandValue()+"<"+ops[0].getOperandValue()+")\n{\n}\nelse\n{\nbreak;\n}\n";
                  }
                  else
                  {
                      str="";
                  }
                  break;

            case JvmOpCodes.IF_ICMPNE:
                ops=getIFArgs(opStack,2);
                  if(ops!=null)
                  {
                     str="\nif("+ops[1].getOperandValue()+"!= "+ops[0].getOperandValue()+")\n{\n}\nelse\n{\nbreak;\n}\n";
                  }
                  else
                  {
                      str="";
                  }
                  break;

            case JvmOpCodes.IFEQ:
                    ops=getIFArgs(opStack,1);
                if(ops!=null)
                {
                    if((code[currentForIndex-1]!=JvmOpCodes.DCMPG) && (code[currentForIndex-1] != JvmOpCodes.DCMPL) && code[currentForIndex-1]!=JvmOpCodes.FCMPG && code[currentForIndex-1]!=JvmOpCodes.FCMPL && code[currentForIndex-1]!=JvmOpCodes.LCMP)
                        str= "\nif("+ops[0].getOperandValue()+"==0)\n{\n}\nelse\n{\nbreak;\n}\n";
                    else
                        str= "\nif("+ops[0].getOperandValue()+")\n{\nbreak;\n}\n" +
                                "else\n" +
                                "{\n" +
                                "}\n";
                }
                else
                {
                    str="";
                }
                break;
            case JvmOpCodes.IFGE:
                   ops=getIFArgs(opStack,1);
                if(ops!=null)
                {
                    if((code[currentForIndex-1]!=JvmOpCodes.DCMPG) && (code[currentForIndex-1] != JvmOpCodes.DCMPL) && code[currentForIndex-1]!=JvmOpCodes.FCMPG && code[currentForIndex-1]!=JvmOpCodes.FCMPL && code[currentForIndex-1]!=JvmOpCodes.LCMP)
                        str= "\nif("+ops[0].getOperandValue()+">=0)\n{\n}\nelse\n{\nbreak;\n}\n";
                    else
                        str= "\nif("+ops[0].getOperandValue()+")\n{\nbreak;\n}\n" +
                                "else\n" +
                                "{\n" +
                                "}\n";
                }
                else
                {
                    str="";
                }
                break;


            case JvmOpCodes.IFGT:
                    ops=getIFArgs(opStack,1);
                if(ops!=null)
                {
                    if((code[currentForIndex-1]!=JvmOpCodes.DCMPG) && (code[currentForIndex-1] != JvmOpCodes.DCMPL) && code[currentForIndex-1]!=JvmOpCodes.FCMPG && code[currentForIndex-1]!=JvmOpCodes.FCMPL && code[currentForIndex-1]!=JvmOpCodes.LCMP)
                        str= "\nif("+ops[0].getOperandValue()+">0)\n{\n}\nelse\n{\nbreak;\n}\n";
                    else
                        str= "\nif("+ops[0].getOperandValue()+")\n{\nbreak;\n}\n" +
                                "else\n" +
                                "{\n" +
                                "}\n";
                }
                else
                {
                    str="";
                }
                break;
            case JvmOpCodes.IFLE:
                    ops=getIFArgs(opStack,1);
                if(ops!=null)
                {
                    if((code[currentForIndex-1]!=JvmOpCodes.DCMPG) && (code[currentForIndex-1] != JvmOpCodes.DCMPL) && code[currentForIndex-1]!=JvmOpCodes.FCMPG && code[currentForIndex-1]!=JvmOpCodes.FCMPL && code[currentForIndex-1]!=JvmOpCodes.LCMP)
                        str= "\nif("+ops[0].getOperandValue()+"<=0)\n{\n}\nelse\n{\nbreak;\n}\n";
                    else
                        str= "\nif("+ops[0].getOperandValue()+")\n{\nbreak;\n}\n" +
                                "else\n" +
                                "{\n" +
                                "}\n";
                }
                else
                {
                    str="";
                }
                break;
            case JvmOpCodes.IFLT:
                    ops=getIFArgs(opStack,1);
                if(ops!=null)
                {
                    if((code[currentForIndex-1]!=JvmOpCodes.DCMPG) && (code[currentForIndex-1] != JvmOpCodes.DCMPL) && code[currentForIndex-1]!=JvmOpCodes.FCMPG && code[currentForIndex-1]!=JvmOpCodes.FCMPL && code[currentForIndex-1]!=JvmOpCodes.LCMP)
                        str= "\nif("+ops[0].getOperandValue()+"<0)\n{\n}\nelse\n{\nbreak;\n}\n";
                    else
                        str= "\nif("+ops[0].getOperandValue()+")\n{\nbreak;\n}\n" +
                                "else\n" +
                                "{\n" +
                                "}\n";
                }
                else
                {
                    str="";
                }
                break;
            case JvmOpCodes.IFNE:
                ops=getIFArgs(opStack,1);
                if(ops!=null)
                {
                    if((code[currentForIndex-1]!=JvmOpCodes.DCMPG) && (code[currentForIndex-1] != JvmOpCodes.DCMPL) && code[currentForIndex-1]!=JvmOpCodes.FCMPG && code[currentForIndex-1]!=JvmOpCodes.FCMPL && code[currentForIndex-1]!=JvmOpCodes.LCMP)
                        str= "\nif("+ops[0].getOperandValue()+"!=0)\n{\n}\nelse\n{\nbreak;\n}\n";
                    else
                        str= "\nif("+ops[0].getOperandValue()+")\n{\nbreak;\n}\n" +
                                "else\n" +
                                "{\n" +
                                "}\n";
                }
                else
                {
                    str="";
                }
                break;
            case JvmOpCodes.IFNONNULL:

                ops=getIFArgs(opStack,1);
                if(ops!=null)
                {
                    str="\nif("+ops[0].getOperandValue()+"!= null)\n{\n}\nelse\n{break;\n}\n";
                }
                else
                {
                    str="";
                }
                break;
            case JvmOpCodes.IFNULL:
                ops=getIFArgs(opStack,1);
                if(ops!=null)
                {
                    str="\nif("+ops[0].getOperandValue()+"== null)\n{\n}\nelse\n{break;\n}\n";
                }
                else
                {
                    str="";
                }
                break;
            default:
                    str="";
                    break;




        }
      }
        return str;

    }


    private Operand[] getIFArgs(OperandStack opStack,int number)
    {
        Operand ops[]=new Operand[number];
        if(opStack.size() >= number)
        {


            for(int z=0;z<number;z++)   // ops[0] --> Original TOS
            {
                ops[z]=opStack.getTopOfStack();

            }


            return ops;
        }

        return null;
    }


    private java.lang.String getIfElseReturnAtI(int i)
    {
        if(this.retAtIfElseEnd==null || this.retAtIfElseEnd.size()==0)
        {
            return null;
        }
        else
        {
            boolean   ret=addReturnAtIFElseEnd(i);
            if(ret)
            {
            returnsaddedAtIfElse.add(new Integer(i));
            return (java.lang.String)((this.retAtIfElseEnd).get(new Integer(i)));
            }
            else
                return null;
        }
    }


     private int getNextLoopStart(int start)
     {
        int i=-1;
        ArrayList loops=behaviour.getBehaviourLoops();
        for(int s=0;s<loops.size();s++)
        {
            Loop l=(Loop)loops.get(s);
            int next=s+1;
            if(next < loops.size())
            {
                Loop n=(Loop)loops.get(next);
                if(start==l.getStartIndex())
                {
                    return n.getStartIndex();
                }

            }
            else
                return i;
        }

       return i;
     }


    private boolean isNewFollowedByNew(byte []info,int i)
    {
        int pos=i+2+1+1;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),pos))
        {
         if(info[pos]==JvmOpCodes.NEW)return true;
            else return false;
        }
        else return false;
    }


    private  void registerInnerClassIfAny(java.lang.String Type)
    {
        java.lang.String type=Type.trim();
        if(type.indexOf("$") > 0)
        {

          java.lang.String path= Configuration.getPathForCurrentClassFile();
          java.lang.String name= ConsoleLauncher.getClassName(path);
          int numberof$inmain=0;
          for(int j=0;j<name.length();j++)
          {
              char c=name.charAt(j);
              if(c=='$')numberof$inmain++;
          }
          int numberof$intype=0;
          for(int j=0;j<Type.length();j++)
          {
              char c=Type.charAt(j);
              if(c=='$')numberof$intype++;
          }
         /// if(numberof$inmain+1==numberof$intype) // Need to register inner class
         // {
              InnerClassTracker tracker= ConsoleLauncher.getTracker();
              if(tracker!=null)
              {
                  InnerClassTracker.Node currentRoot= ConsoleLauncher.getCurrentRootAdded();  // Parent
                  java.lang.String dir= ConsoleLauncher.getClassDir();
                  if(type.endsWith(".class")==false)type+=".class";
                  type= ConsoleLauncher.getClassName(type);
                  InnerClassTracker.Node child=tracker.createNode(dir+File.separator+type,type);
                  tracker.registerChildNode(currentRoot,child);
              }
          //}

        }
        else
            return;


    }


    private boolean isParameterTypeBoolean(List paramlist,int indx)
    {
        java.lang.String type=(java.lang.String)paramlist.get(indx);
        if(type.equalsIgnoreCase("boolean"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void resetOperandValueIfNecessary(ArrayList paramlist,int indx,Operand op2)
    {

        boolean boolparam=isParameterTypeBoolean(paramlist,indx);
        if(boolparam)
        {
            if(op2!=null && op2.getOperandValue()!=null && op2.getOperandValue().toString().trim().equals("1"))
                op2.setOperandValue("true");
            if(op2!=null && op2.getOperandValue()!=null && op2.getOperandValue().toString().trim().equals("0"))
                op2.setOperandValue("false");

        }
    }

    public static java.lang.String getClassName(java.lang.String path)
	{
		java.lang.String slash="";
		if(path.indexOf("\\")!=-1){
			slash="\\";
		}
		else
		{
			slash="/";
		}
		int lastSlash=path.lastIndexOf(slash);
        return path.substring(lastSlash+1);


	}



    private boolean isInstAnyBasicPrimitiveOperation(byte code[],int pos,StringBuffer sb)
    {
        boolean b=false;
        switch(code[pos])
        {

            case JvmOpCodes.DADD:
                sb.append(2);
                b=true;
                break;
            case JvmOpCodes.DDIV:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.DMUL:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.DNEG:
                b=true;
                sb.append(1);
                break;
            case JvmOpCodes.DREM:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.DSUB:
                b=true;
                sb.append(2);
                break;

            case JvmOpCodes.FADD:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.FDIV:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.FMUL:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.FNEG:
                b=true;
                sb.append(1);
                break;
            case JvmOpCodes.FREM:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.IADD:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.IAND:
                b=true;
                break;
            case JvmOpCodes.IDIV:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.IMUL:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.INEG:
                b=true;
                sb.append(1);
                break;
            case JvmOpCodes.IOR:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.IREM:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.ISHL:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.ISHR:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.ISUB:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.IUSHR:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.IXOR:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LADD:
                b=true;
                sb.append(2);
                break;

            case JvmOpCodes.LAND:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LDIV:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LMUL:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LNEG:
                b=true;
                sb.append(1);
                break;
            case JvmOpCodes.LOR:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LREM:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LSHL:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LSHR:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LSUB:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LUSHR:
                b=true;
                sb.append(2);
                break;
            case JvmOpCodes.LXOR:
                b=true;
                sb.append(2);
                break;


        }


        return b;
    }



    private void handleBasicPrimitiveLoadOp(byte[] code,int k)
    throws Exception
    {
       if(k < 0) throw new Exception();
       ArrayList list=behaviour.getInstructionStartPositions();
       boolean b=isThisInstrStart(list,k);
       while(!b)
       {
         k=k-1;
         b=isThisInstrStart(list,k);
       }
       switch(code[k])
       {
           /***
            * First List all recursive calls
            */
             case JvmOpCodes.DADD:
                InstParam newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                InstParam tos=(InstParam)stackHandler.pop();
                int mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                int rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                  return;
                }


            case JvmOpCodes.DDIV:
                newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                  return;
                }

            case JvmOpCodes.DMUL:
                newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }


            case JvmOpCodes.DNEG:
                newentry=new InstParam();
                newentry.setNumberOfParamsLeft(1);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }


            case JvmOpCodes.DREM:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.DSUB:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }


            case JvmOpCodes.FADD:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.FDIV:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.FMUL:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.FNEG:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(1);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.FREM:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.IADD:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.IAND:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.IDIV:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.IMUL:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;

                }

            case JvmOpCodes.INEG:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(1);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.IOR:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.IREM:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.ISHL:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.ISHR:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.ISUB:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.IUSHR:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.IXOR:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LADD:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }


            case JvmOpCodes.LAND:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LDIV:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LMUL:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LNEG:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(1);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LOR:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LREM:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }


            case JvmOpCodes.LSHL:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LSHR:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LSUB:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LUSHR:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }

            case JvmOpCodes.LXOR:
                 newentry=new InstParam();
                newentry.setNumberOfParamsLeft(2);
                newentry.setCurrentParamOffsetInCode(-1);
                stackHandler.push(newentry);
                handleBasicPrimitiveLoadOp(code,(k-1));
                tos=(InstParam)stackHandler.pop();
                mybegin=tos.getCurrentParamOffsetInCode();
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(mybegin);
                stackHandler.push(tos);
                if(rem==0)
                {
                    return;
                }
                else
                {
                  handleBasicPrimitiveLoadOp(code,(mybegin-1));
                    return;
                }



       }

        if(code[k]==JvmOpCodes.BALOAD || code[k]==JvmOpCodes.CALOAD || code[k]==JvmOpCodes.DALOAD || code[k]==JvmOpCodes.FALOAD || code[k]==JvmOpCodes.LALOAD || code[k]==JvmOpCodes.IALOAD || code[k]==JvmOpCodes.SALOAD)
        {
            StringBuffer sb=new StringBuffer("");
            int posFound=isInstAload(k,code,sb);
            while(posFound==-1)
            {

                k=k-1;
                sb=new StringBuffer("");
                posFound=isInstAload(k,code,sb);

            }
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(posFound);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,posFound-1);
                return;
            }

        }

        if(code[k]==JvmOpCodes.BIPUSH || code[k]==JvmOpCodes.SIPUSH)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(k);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,k-1);
                return;
            }

        }
        int y=isInstIloadInst(code,k,new StringBuffer(""));
        if(y!=-1)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(y);
            stackHandler.push(tos);
            if(rem==0)
            {
                return;
            }
            else
            {
                handleBasicPrimitiveLoadOp(code,y-1);
                tos=(InstParam)stackHandler.pop();
                rem=tos.getNumberOfParamsLeft();
                if(rem==0)
                {
                    stackHandler.push(tos);
                    return;
                }



            }

        }
        y=isInstFloadInst(code,k,new StringBuffer(""));
        if(y!=-1)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(y);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,y-1);
                return;
            }

        }
        y=isInstDloadInst(code,k,new StringBuffer(""));
        if(y!=-1)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(y);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,y-1);
                return;
            }

        }
        if(code[k]==JvmOpCodes.LDC || code[k]==JvmOpCodes.LDC2_W || code[k]==JvmOpCodes.LDC_W)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(k);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,k-1);
                return;
            }

        }
        y=isInstIConstInst(code,k);
        if(y!=-1)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(y);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,y-1);
                return;
            }

        }
        y=isInstFConstInst(code,k);
        if(y!=-1)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(y);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,y-1);
                return;
            }

        }
         y=isInstdConstInst(code,k);
        if(y!=-1)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(y);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,y-1);
                return;
            }

        }
         y=isInstLConstInst(code,k);
        if(y!=-1)
        {
            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(y);
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,y-1);
                return;
            }

        }
        if(code[k]==JvmOpCodes.GETFIELD)
        {
            StringBuffer sb1=new StringBuffer("");
            int pos2=isInstAload(k-1,code,sb1);
            if(pos2!=-1)
            {
                InstParam tos=(InstParam)stackHandler.pop();
                int rem=tos.getNumberOfParamsLeft();
                rem=rem-1;
                tos.setNumberOfParamsLeft(rem);
                tos.setCurrentParamOffsetInCode(pos2);
                stackHandler.push(tos);
                if(rem==0)return;
                else
                {
                    handleBasicPrimitiveLoadOp(code,pos2-1);
                    return;
                }
            }
        }
        if(code[k]==JvmOpCodes.GETSTATIC)
        {

            InstParam tos=(InstParam)stackHandler.pop();
            int rem=tos.getNumberOfParamsLeft();
            rem=rem-1;
            tos.setNumberOfParamsLeft(rem);
            tos.setCurrentParamOffsetInCode(k);    // todo: TAKE POP INTO ACCOUNT
            stackHandler.push(tos);
            if(rem==0)return;
            else
            {
                handleBasicPrimitiveLoadOp(code,k-1);
                return;
            }

        }

         StringBuffer isinvoke=new StringBuffer("");
         if(isNextInstructionAnyInvoke(code[k],isinvoke))
        {

             InstParam tos=(InstParam)stackHandler.pop();
             int rem=tos.getNumberOfParamsLeft();
             rem=rem-1;
             tos.setNumberOfParamsLeft(rem);
             Integer ref=(Integer)invokeinstrefpos.get(new Integer(k));
             if(ref!=null)
             {

                 tos.setCurrentParamOffsetInCode(ref.intValue());
                 stackHandler.push(tos);
                 if(rem==0)
                 {
                     return;
                 }
                 else
                 {
                     handleBasicPrimitiveLoadOp(code,ref.intValue()-1);
                     return;

                 }
             }


        }


          if(code[k]==JvmOpCodes.D2F || code[k]==JvmOpCodes.D2I || code[k]==JvmOpCodes.D2L )
          {

                int t=k-1;
                boolean done=false;
                StringBuffer strb=new StringBuffer("");
                int dload=isInstDloadInst(code,t,strb);
                int foundpos=-1;
                if(dload!=-1)
                {
                     foundpos=dload;
                     done=true;
                }
                else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        foundpos=loadin;
                        done=true;
                    }


                }

              if(done)
              {

                  InstParam tos=(InstParam)stackHandler.pop();
                  int rem=tos.getNumberOfParamsLeft();
                  rem=rem-1;
                  tos.setNumberOfParamsLeft(rem);
                  tos.setCurrentParamOffsetInCode(foundpos);
                  stackHandler.push(tos);
                  if(rem==0)return;
                  else
                  {
                      handleBasicPrimitiveLoadOp(code,foundpos-1);
                      return;
                  }
              }

              else
              {
                  InstParam newentry=new InstParam();
                  newentry.setNumberOfParamsLeft(1);
                  newentry.setCurrentParamOffsetInCode(-1);
                  stackHandler.push(newentry);
                  handleBasicPrimitiveLoadOp(code,(k-1));
                  InstParam tos=(InstParam)stackHandler.pop();
                  int entry=tos.getCurrentParamOffsetInCode();
                  tos=(InstParam)stackHandler.pop();
                  int r=tos.getNumberOfParamsLeft();
                  r=r-1;
                  tos.setNumberOfParamsLeft(r);
                  tos.setCurrentParamOffsetInCode(entry);
                  stackHandler.push(tos);
                  if(r==0)
                  {
                    return;
                  }
                  else
                  {
                      handleBasicPrimitiveLoadOp(code,entry-1);
                      return;
                  }
              }
            }
            if(code[k]==JvmOpCodes.I2B || code[k]==JvmOpCodes.I2C  || code[k]==JvmOpCodes.I2S || code[k]==JvmOpCodes.I2D || code[k]==JvmOpCodes.I2F || code[k]==JvmOpCodes.I2L)
            {

                int t=k-1;
                boolean done=false;
                StringBuffer strb=new StringBuffer("");
                int dload=isInstIloadInst(code,t,strb);
                int foundpos=-1;
                if(dload!=-1)
                {
                     foundpos=dload;
                     done=true;
                }
                else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        foundpos=loadin;
                        done=true;
                    }


                }

              if(done)
              {

                  InstParam tos=(InstParam)stackHandler.pop();
                  int rem=tos.getNumberOfParamsLeft();
                  rem=rem-1;
                  tos.setNumberOfParamsLeft(rem);
                  tos.setCurrentParamOffsetInCode(foundpos);
                  stackHandler.push(tos);
                  if(rem==0)return;
                  else
                  {
                      handleBasicPrimitiveLoadOp(code,foundpos-1);
                      return;
                  }
              }

              else
              {
                  InstParam newentry=new InstParam();
                  newentry.setNumberOfParamsLeft(1);
                  newentry.setCurrentParamOffsetInCode(-1);
                  stackHandler.push(newentry);
                  handleBasicPrimitiveLoadOp(code,(k-1));
                  InstParam tos=(InstParam)stackHandler.pop();
                  int entry=tos.getCurrentParamOffsetInCode();
                  tos=(InstParam)stackHandler.pop();
                  int r=tos.getNumberOfParamsLeft();
                  r=r-1;
                  tos.setNumberOfParamsLeft(r);
                  tos.setCurrentParamOffsetInCode(entry);
                  stackHandler.push(tos);
                  if(r==0)
                  {
                    return;
                  }
                  else
                  {
                      handleBasicPrimitiveLoadOp(code,entry-1);
                      return;
                  }
              }
            }
            if(code[k]==JvmOpCodes.F2D || code[k]==JvmOpCodes.F2L || code[k]==JvmOpCodes.F2I)
            {
                         int t=k-1;
                boolean done=false;
                StringBuffer strb=new StringBuffer("");
                int dload=isInstFloadInst(code,t,strb);
                int foundpos=-1;
                if(dload!=-1)
                {
                     foundpos=dload;
                     done=true;
                }
                else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        foundpos=loadin;
                        done=true;
                    }


                }

              if(done)
              {

                  InstParam tos=(InstParam)stackHandler.pop();
                  int rem=tos.getNumberOfParamsLeft();
                  rem=rem-1;
                  tos.setNumberOfParamsLeft(rem);
                  tos.setCurrentParamOffsetInCode(foundpos);
                  stackHandler.push(tos);
                  if(rem==0)return;
                  else
                  {
                      handleBasicPrimitiveLoadOp(code,foundpos-1);
                      return;
                  }
              }

              else
              {
                InstParam newentry=new InstParam();
                  newentry.setNumberOfParamsLeft(1);
                  newentry.setCurrentParamOffsetInCode(-1);
                  stackHandler.push(newentry);
                  handleBasicPrimitiveLoadOp(code,(k-1));
                  InstParam tos=(InstParam)stackHandler.pop();
                  int entry=tos.getCurrentParamOffsetInCode();
                  tos=(InstParam)stackHandler.pop();
                  int r=tos.getNumberOfParamsLeft();
                  r=r-1;
                  tos.setNumberOfParamsLeft(r);
                  tos.setCurrentParamOffsetInCode(entry);
                  stackHandler.push(tos);
                  if(r==0)
                  {
                    return;
                  }
                  else
                  {
                      handleBasicPrimitiveLoadOp(code,entry-1);
                      return;
                  }
              }
            }
            if(code[k]==JvmOpCodes.L2D || code[k]==JvmOpCodes.L2F || code[k]==JvmOpCodes.L2I)
            {
                           int t=k-1;
                boolean done=false;
                StringBuffer strb=new StringBuffer("");
                int dload=isInstLloadInst(code,t,strb);
                int foundpos=-1;
                if(dload!=-1)
                {
                     foundpos=dload;
                     done=true;
                }
                else
                {
                    strb=new StringBuffer("");
                    int loadin=traceLoadInst(t,code,strb);
                    if(loadin!=-1)
                    {
                        foundpos=loadin;
                        done=true;
                    }


                }

              if(done)
              {

                  InstParam tos=(InstParam)stackHandler.pop();
                  int rem=tos.getNumberOfParamsLeft();
                  rem=rem-1;
                  tos.setNumberOfParamsLeft(rem);
                  tos.setCurrentParamOffsetInCode(foundpos);
                  stackHandler.push(tos);
                  if(rem==0)return;
                  else
                  {
                      handleBasicPrimitiveLoadOp(code,foundpos-1);
                      return;
                  }
              }

              else
              {
                 InstParam newentry=new InstParam();
                  newentry.setNumberOfParamsLeft(1);
                  newentry.setCurrentParamOffsetInCode(-1);
                  stackHandler.push(newentry);
                  handleBasicPrimitiveLoadOp(code,(k-1));
                  InstParam tos=(InstParam)stackHandler.pop();
                  int entry=tos.getCurrentParamOffsetInCode();
                  tos=(InstParam)stackHandler.pop();
                  int r=tos.getNumberOfParamsLeft();
                  r=r-1;
                  tos.setNumberOfParamsLeft(r);
                  tos.setCurrentParamOffsetInCode(entry);
                  stackHandler.push(tos);
                  if(r==0)
                  {
                    return;
                  }
                  else
                  {
                      handleBasicPrimitiveLoadOp(code,entry-1);
                      return;
                  }
              }
            }










    }


    /***
     * belurs:
     * The below 2 are used in   handleBasicPrimitiveLoadOp
     * Could Think of an appropriate names here.
     * Refactor later if better names can  be thpught of
     */

    private Stack stackHandler=null ;   // will contain InstParam Objects
    class InstParam
    {


        private int numberOfParamsLeft; // Example imul takes 2 so initially 2 is populated
        private int currentParamOffsetInCode; // Example for imul the first one cane be iload_1..So once
                                             // This is encountered, currentParamOffsetInCode will be
                                             // set to iload_1 position in code.


        public int getNumberOfParamsLeft() {
            return numberOfParamsLeft;
        }

        public void setNumberOfParamsLeft(int numberOfParamsLeft) {
            this.numberOfParamsLeft = numberOfParamsLeft;
        }

        public int getCurrentParamOffsetInCode() {
            return currentParamOffsetInCode;
        }

        public void setCurrentParamOffsetInCode(int currentParamOffsetInCode) {
            this.currentParamOffsetInCode = currentParamOffsetInCode;
        }



    }




    private Operand createOperand(Object val,int type,int categ)
    {
        Operand opr=new Operand();
        opr.setOperandValue(val);
        opr.setOperandType(type);
        opr.setCategory(categ);
        return opr;

    }
    private Operand createOperand(Object val)
    {
        Operand opr=new Operand();
        opr.setOperandValue(val);
        return opr;

    }

     private int getSwitchOffset(byte[] info,int counter,java.lang.String lbl)
    {
        int b1=info[++counter];
        int b2=info[++counter];
        int b3=info[++counter];
        int b4=info[++counter];

        if(b1 < 0)b1=(256+b1);
        if(b2 < 0)b2=(256+b2);
        if(b3 < 0)b3=(256+b3);
        if(b4 < 0)b4=(256+b4);

        int jmp=(b1 << 24) | (b2 << 16) | (b3 << 8) |b4;
        if(jmp > 65535)
            jmp=jmp-65536;
        if(lbl.equals("label"))return jmp;
        if(jmp < 0)jmp=512+jmp;
        return jmp;

    }






    private java.lang.String getReturnTypeIfPreviousInvoke(int j,byte[] info){

               //Terminator t;
        java.lang.String s="0";
        if(invokeStartEnd!=null)
        {
        Integer in=(Integer)invokeStartEnd.get(new Integer(j-1));
        if(in!=null)
        {
            int iin=in.intValue();
            switch(info[iin])
            {

                case JvmOpCodes.INVOKEINTERFACE:
                        int classIndex=getOffset(info,iin);
                        InterfaceMethodRef iref=cd.getInterfaceMethodAtCPoolPosition(classIndex);
                        java.lang.String classname=iref.getClassname();
                        java.lang.String typeofmet=iref.getTypeofmethod();
                        int br=typeofmet.indexOf(")");
                        if(br!=-1)
                        {
                             java.lang.String ret=typeofmet.substring(br+1);
                             if(ret.trim().equals("Z"))
                             {
                                 return "false";
                             }

                        }

                    break;



                    case JvmOpCodes.INVOKESPECIAL:

                        classIndex=getOffset(info,iin);
                        MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        java.lang.String classtype=mref.getClassname();
                        typeofmet=mref.getTypeofmethod();
                        br=typeofmet.indexOf(")");
                                            if(br!=-1)
                                            {
                                                 java.lang.String ret=typeofmet.substring(br+1);
                                                 if(ret.trim().equals("Z"))
                                                 {
                                                     return "false";
                                                 }

                                            }



                    break;

                  case JvmOpCodes.INVOKESTATIC:
                        classIndex=getOffset(info,iin);
                        mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        classname=mref.getClassname();
                        typeofmet=mref.getTypeofmethod();
                    br=typeofmet.indexOf(")");
                                                                if(br!=-1)
                                                                {
                                                                     java.lang.String ret=typeofmet.substring(br+1);
                                                                     if(ret.trim().equals("Z"))
                                                                     {
                                                                         return "false";
                                                                     }

                                                                }

                    break;

                  case JvmOpCodes.INVOKEVIRTUAL:
                    classIndex=getOffset(info,iin);
                        mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        classname=mref.getClassname();
                        typeofmet=mref.getTypeofmethod();
                     br=typeofmet.indexOf(")");
                                                                if(br!=-1)
                                                                {
                                                                     java.lang.String ret=typeofmet.substring(br+1);
                                                                     if(ret.trim().equals("Z"))
                                                                     {
                                                                         return "false";
                                                                     }

                                                                }


                    break;

               default:
                    return s;

            }
        }


        return s;

        }
        else
        {
           int iin=j-3;
           int classIndex=-1;
           switch(info[iin])
            {

                /*case JvmOpCodes.INVOKEINTERFACE:
                        int classIndex=getOffset(info,iin);
                        InterfaceMethodRef iref=cd.getInterfaceMethodAtCPoolPosition(classIndex);
                        java.lang.String classname=iref.getClassname();
                        java.lang.String typeofmet=iref.getTypeofmethod();
                        int br=typeofmet.indexOf(")");
                        if(br!=-1)
                        {
                             java.lang.String ret=typeofmet.substring(br+1);
                             if(ret.trim().equals("Z"))
                             {
                                 return "false";
                             }

                        }

                    break;    */



                    case JvmOpCodes.INVOKESPECIAL:

                        classIndex=getOffset(info,iin);
                        MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        java.lang.String classtype=mref.getClassname();
                        java.lang.String typeofmet=mref.getTypeofmethod();
                        int br=typeofmet.indexOf(")");
                                            if(br!=-1)
                                            {
                                                 java.lang.String ret=typeofmet.substring(br+1);
                                                 if(ret.trim().equals("Z"))
                                                 {
                                                     return "false";
                                                 }

                                            }



                    break;

                  case JvmOpCodes.INVOKESTATIC:
                        classIndex=getOffset(info,iin);
                        mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        java.lang.String classname=mref.getClassname();
                        typeofmet=mref.getTypeofmethod();
                    br=typeofmet.indexOf(")");
                                                                if(br!=-1)
                                                                {
                                                                     java.lang.String ret=typeofmet.substring(br+1);
                                                                     if(ret.trim().equals("Z"))
                                                                     {
                                                                         return "false";
                                                                     }

                                                                }

                    break;

                  case JvmOpCodes.INVOKEVIRTUAL:
                    classIndex=getOffset(info,iin);
                        mref = cd.getMethodRefAtCPoolPosition(classIndex);
                        classname=mref.getClassname();
                        typeofmet=mref.getTypeofmethod();
                     br=typeofmet.indexOf(")");
                                                                if(br!=-1)
                                                                {
                                                                     java.lang.String ret=typeofmet.substring(br+1);
                                                                     if(ret.trim().equals("Z"))
                                                                     {
                                                                         return "false";
                                                                     }

                                                                }






            }
            if(isThisInstrStart(behaviour.getInstructionStartPositions(),j-5) && info[j-5]==JvmOpCodes.INVOKEINTERFACE)
            {

                        classIndex=getOffset(info,iin);
                        InterfaceMethodRef iref=cd.getInterfaceMethodAtCPoolPosition(classIndex);
                        java.lang.String classname=iref.getClassname();
                        java.lang.String typeofmet=iref.getTypeofmethod();
                        int br=typeofmet.indexOf(")");
                        if(br!=-1)
                        {
                             java.lang.String ret=typeofmet.substring(br+1);
                             if(ret.trim().equals("Z"))
                             {
                                 return "false";
                             }

                        }
            }
        }
        return s;
    }


    ArrayList startlist=null;
    private void findSkipRangesWRTSynchronizedBlocks()
    {

        startlist=behaviour.getInstructionStartPositions();
        synchSkips=new Hashtable();
        byte code[]=behaviour.getCode();
        if(code==null)return;
        for(int j=0;j<code.length;j++)
        {

            if(isThisInstrStart(behaviour.getInstructionStartPositions(),j) && code[j]==JvmOpCodes.MONITOREXIT)
            {
                int loadpos=-1;
                boolean simple=true;
                int index=-1;
                switch(code[j+1])
                {

                    case JvmOpCodes.ALOAD:
                        simple=false;
                        loadpos=j+1;

                        break;
                    case JvmOpCodes.ALOAD_0:
                        simple=true;
                        loadpos=j+1;
                        index=0;
                        break;
                    case JvmOpCodes.ALOAD_1:
                        simple=true;
                        loadpos=j+1;
                        index=1;
                        break;
                    case JvmOpCodes.ALOAD_2:
                        simple=true;
                        loadpos=j+1;
                        index=2;
                        break;
                    case JvmOpCodes.ALOAD_3:
                        simple=true;
                        loadpos=j+1;
                        index=3;
                        break;
                    default:
                        loadpos=-1;

                }


                if(loadpos!=-1)
                {


                    if(simple)
                    {

                        if(code[j+1+1]==JvmOpCodes.ATHROW)
                        {
                            int start=j;
                            while(start >=0)
                            {

                                int st=code[start];
                                if(isThisInstrStart(startlist,start))
                                {
                                    int storeindex=-1;

                                    switch(st)
                                    {
                                        case JvmOpCodes.ASTORE_0:
                                            storeindex=0;
                                            break;



                                        case JvmOpCodes.ASTORE_1:
                                            storeindex=1;
                                            break;


                                        case JvmOpCodes.ASTORE_2:
                                            storeindex=2;
                                            break;



                                        case JvmOpCodes.ASTORE_3:
                                            storeindex=3;
                                            break;

                                        default:
                                            storeindex=-1;
                                            break;


                                    }

                                    if(storeindex==index)
                                    {
                                        synchSkips.put(new Integer(start),new Integer(j+2)) ;
                                        break;
                                    }
                                }
                                start--;

                            }

                        }
                    }
                    else
                    {
                        if(code[j+1+2]==JvmOpCodes.ATHROW)
                        {
                            index=code[j+1+1];
                            if(index < 0)index+=256; // TODO: apply this to elsewhere
                            int start=j;
                            while(start >=0)
                            {

                                int st=code[start];
                                if(isThisInstrStart(startlist,start))
                                {
                                    int storeindex=-1;

                                    switch(st)
                                    {
                                        case JvmOpCodes.ASTORE_0:
                                            storeindex=0;
                                            break;



                                        case JvmOpCodes.ASTORE_1:
                                            storeindex=1;
                                            break;


                                        case JvmOpCodes.ASTORE_2:
                                            storeindex=2;
                                            break;



                                        case JvmOpCodes.ASTORE_3:
                                            storeindex=3;
                                            break;


                                        case JvmOpCodes.ASTORE:
                                             storeindex=code[start+1];
                                            if(storeindex < 0)storeindex+=256;
                                            break;

                                        default:
                                            storeindex=-1;
                                            break;


                                    }

                                    if(storeindex==index)
                                    {
                                        synchSkips.put(new Integer(start),new Integer(j+3));
                                        break;
                                    }
                                }
                                start--;

                            }



                        }

                    }



                }
            }

        }

    }



    private void handleAloadCase(byte[] info,int i,OperandStack OpStack)
    {
        Operand op=null;
        int opValueI = info[++i];
        //addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" aload "+opValueI+"\n");

        LocalVariable local=getLocalVariable(opValueI, "load","java.lang.Object",false,currentForIndex);
        if(local!=null) {
            prevLocalGenerated = local;
            op = new Operand();
            op.setOperandName(local.getVarName());
            op.setOperandValue(local.getVarName());
            op.setOperandType(Constants.IS_OBJECT_REF);
            opStack.push(op);
            op.setClassType(local.getDataType());

        }
        else
        {
            op = new Operand();
            op.setOperandName("this");
            op.setOperandValue("this");
            op.setOperandType(Constants.IS_OBJECT_REF);
            opStack.push(op);
            op.setClassType(ConsoleLauncher.getClazzRef()!=null? ConsoleLauncher.getClazzRef().getClassName():"Object");
        }

        java.lang.String t="Instruction Pos "+currentForIndex+"\t:- "+" aload "+opValueI;
        if(cd.isClassCompiledWithMinusG() && local!=null)
        {
            t+=" This LocalVariable :- "+local.getVarName();
        }
        t+="\n";
        addParsedOutput(t);

    }


  // belurs:
 // only polls for end of synch tables . Not start(MonitorEnter)

    private java.lang.String pollsynchblocks(int cur)
    {

         ArrayList synchlist=behaviour.getSynchronizedEntries();
         if(synchlist!=null && synchlist.size() > 0)
         {

             for(int s=0;s<synchlist.size();s++)
             {
                ExceptionTable table=(ExceptionTable)synchlist.get(s);
                int end=table.getSynchEnd();
                if(end==cur)
                {
                    return "}";
                }

             }

         }
        return "";





    }





    private void resetEndOfSynchBlockIfNecessary(int current)
    {

        //  Step 1 : Determine the applicable IF block
        // fOR EACH MONITORENTER FIND out the closest if and end of that... ~ to try handling
        ArrayList synchtables=behaviour.getSynchronizedEntries();
        byte code[]=behaviour.getCode();

        if(synchtables!=null && synchtables.size() > 0)
        {
            ArrayList starts=behaviour.getInstructionStartPositions();
            for(int s=0;s<synchtables.size();s++)
            {

                ExceptionTable tab=(ExceptionTable)synchtables.get(s);
                int start=tab.getStartPC();
                if(tab.getMonitorEnterPosInCode()==currentMonitorEnterPos)
                {

                     int synchend =tab.getEndPC();                                              // belurs:
                    if(current-3 >=0 && isNextInstructionIf(code[current-3]))     // Actually The previous Instruction
                    {
                        boolean switchfound=false;
                        int switchstartpos=-1;
                        java.lang.String switchtype=null;
                        for(int x=current-3-1;x>start;x-- )
                        {
                           if(isThisInstrStart(starts,x) && code[x]==JvmOpCodes.MONITOREXIT){
                              currentMonitorEnterPos=-1;
                              return;

                           }
                           if(isThisInstrStart(starts,x) && isNextInstructionIf(code[x]))
                           {
                              currentMonitorEnterPos=-1;    // Actually This should never happen
                              return;
                           }
                           if(isThisInstrStart(starts,x) && (code[x]==JvmOpCodes.TABLESWITCH || code[x]==JvmOpCodes.LOOKUPSWITCH))
                           {
                               switchfound=true;
                               switchstartpos=x;
                               if(code[x]==JvmOpCodes.TABLESWITCH)
                               switchtype="table" ;
                               else
                               switchtype="lookup";

                           }
                        }


                        if(switchfound)
                        {
                              if(switchstartpos!=-1)
                              {
                                  Switch swblk=getSwitchBlockGivenStart(switchstartpos);
                                  if(swblk!=null)
                                  {

                                      ArrayList cases=swblk.sortCasesByEnd(swblk.getAllCases());
                                      Case endcase=null;
                                      if(cases!=null)
                                      {
                                         endcase=(Case)cases.get(0);
                                      }
                                      int defend=swblk.getDefaultEnd();
                                      if(defend!=-1)
                                      {
                                          if(defend > endcase.getCaseEnd())
                                          {
                                              if(swblk.getStartOfSwitch() > start && defend >synchend)
                                              {
                                                  tab.setSynchEnd(defend);
                                                  currentMonitorEnterPos=-1;
                                                  return;
                                              }
                                          }
                                          else
                                          {
                                              if(swblk.getStartOfSwitch() > start && endcase.getCaseEnd() >synchend)
                                              {
                                                  tab.setSynchEnd(endcase.getCaseEnd());
                                                  currentMonitorEnterPos=-1;
                                                  return;
                                              }
                                          }
                                      }
                                      else
                                      {
                                              if(swblk.getStartOfSwitch() > start && endcase.getCaseEnd() >synchend)
                                              {
                                                  tab.setSynchEnd(endcase.getCaseEnd());
                                                  currentMonitorEnterPos=-1;
                                                  return;
                                              }
                                      }

                                  }
                              }


                        }
                        else  // reset w.r.t if
                        {

                            IFBlock reqdif=null;
                            Collection ifs=getCurrentIFStructues();
                            Iterator it=ifs.iterator();
                            while(it.hasNext())
                            {
                                 IFBlock IF=(IFBlock)it.next();
                                 if(IF.getIfStart()==current-3)
                                 {
                                    reqdif=IF;
                                    break;
                                 }
                            }
                            if(reqdif!=null)
                            {
                                int ifend=reqdif.getIfCloseFromByteCode();
                                int elseend=reqdif.getElseCloseLineNumber();
                                if(elseend==-1)
                                {
                                    if(ifend > reqdif.getIfStart())
                                    {
                                        if(reqdif.getIfStart() > start)
                                        {
                                          if(ifend > synchend)
                                          {
                                              currentMonitorEnterPos=-1;
                                              tab.setSynchEnd(ifend);
                                              return;

                                          }

                                        }
                                    }

                                }
                                else
                                {
                                    if(elseend > reqdif.getIfStart())
                                    {
                                        if(reqdif.getIfStart() > start)
                                        {
                                          if(elseend > synchend)
                                          {
                                              currentMonitorEnterPos=-1;
                                              tab.setSynchEnd(elseend);
                                              return;

                                          }

                                        }
                                    }

                                }
                            }

                        }


                    }

                  return;
                }



            }


        }






    }

    private int currentMonitorEnterPos=-1; // gets set when every monitorenter is encountered

    private Switch  getSwitchBlockGivenStart(int start)
    {
        Switch sw=null;
        ArrayList allswitches=behaviour.getAllSwitchBlks();
        for(int s=0;s<allswitches.size();s++)
        {

            Switch switchblk=(Switch)allswitches.get(s);
            if(switchblk.getStartOfSwitch()==start)
            {
                return switchblk;
            }
        }
        return sw;

    }


        private boolean instanceoffound=false;



        private void handleSwapInst(OperandStack stack)
        {
            Operand op1 = stack.getTopOfStack();
            Operand             op2 = stack.getTopOfStack();
                        opStack.push(op2);
                        opStack.push(op1);
        }


    private void handleAALOAD(OperandStack stack)
    {
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" aaload\n");
        Operand sourcePos=(Operand)stack.pop();
        Operand srcarrayRef=(Operand)stack.pop(); //
        Operand op = createOperand(srcarrayRef.getOperandValue()+"["+sourcePos.getOperandValue()+"]",Constants.IS_OBJECT_REF,Constants.CATEGORY1);
        opStack.push(op);
    }




      private boolean isInstReturnInst(byte[] code,int pos,StringBuffer sb)
      {

          boolean ret=false;
          if(isThisInstrStart(behaviour.getInstructionStartPositions(),pos))
          {
             switch(code[pos] )
             {
                    case JvmOpCodes.IRETURN:
                        sb.append("ireturn");
                        ret=true;
                      break;

                    case JvmOpCodes.LRETURN:
                     sb.append("lreturn");
                        ret=true;
                      break;
                    case JvmOpCodes.FRETURN:
                     sb.append("freturn");
                        ret=true;
                      break;
                    case JvmOpCodes.DRETURN:
                     sb.append("dreturn");
                        ret=true;
                      break;
                    case JvmOpCodes.ARETURN:
                     sb.append("areturn");
                        ret=true;
                      break;
                    case JvmOpCodes.RETURN:
                     sb.append("return");
                        ret=true;
                      break;
             }
          }

          return ret;


      }



       private void addAnyReturnBeforeIfClose(byte[] info, IFBlock IF ,java.lang.StringBuffer S,int j,OperandStack stack ){
           int ifend=IF.getIfCloseLineNumber();
            boolean addret=addReturnAtIFElseEnd(j);
           if(addret)
           {
           if(info[ifend]==JvmOpCodes.GOTO)
           {
               int gotojump=getJumpAddress(info,ifend);
               StringBuffer sg=new StringBuffer("");
               boolean r=isInstReturnInst(info,gotojump,sg);

               if(r)
               {
                   if(sg.toString().equals("return")==false && stack.size() > 0)
                   {
                       java.lang.String t=stack.getTopOfStack().getOperandValue();
                           boolean b=isMethodRetBoolean(behaviour);
                           if(t!=null)
                           {
                               if(t.equals("1") && b)t="true";
                               if(t.equals("0") && b)t="false";
                               java.lang.String retst="return " +t+";";
                               boolean a=addRETURNatI(ifend,IF);
                                 if(a )
                                 {
                                 S.append(Util.formatDecompiledStatement(retst));// if end\n");
                               returnsAtI.put(new Integer(ifend),"true");
                                 }
                           }

                   }
                   else if(sg.toString().equals("return")==true)
                   {
                       boolean a=addRETURNatI(ifend,IF);
                                 if(a )
                                 {
                       java.lang.String retst="return ;";
                       S.append(Util.formatDecompiledStatement(retst));// if end\n");
                         returnsAtI.put(new Integer(ifend),"true");
                                 }
                   }
                   else
                   {

                   }
               }

           }
           else
           {

                         StringBuffer sg=new StringBuffer("");
                         boolean r=isInstReturnInst(info,ifend,sg);
                         if(r)
                         {
                             if(sg.toString().equals("return")==false && stack.size() > 0)
                             {
                                 java.lang.String t=stack.getTopOfStack().getOperandValue();
                                     boolean b=isMethodRetBoolean(behaviour);
                                     if(t!=null)
                                     {
                                         if(t.equals("1") && b)t="true";
                                         if(t.equals("0") && b)t="false";
                                         java.lang.String retst="return " +t+";";
                                         boolean a=addRETURNatI(ifend,IF);
                                         if(a)
                                         {
                                           S.append(Util.formatDecompiledStatement(retst));// if end\n");
                                         returnsAtI.put(new Integer(ifend),"true");
                                         }
                                     }

                             }
                             else if(sg.toString().equals("return")==true)
                             {
                                 boolean a=addRETURNatI(ifend,IF);
                                 if(a)
                                 {
                                     java.lang.String retst="return ;";
                                     S.append(Util.formatDecompiledStatement(retst));// if end\n");
                                       returnsAtI.put(new Integer(ifend),"true");
                                 }
                             }
                             else
                             {

                             }
                         }

                     }

           }
       }

       private void addAnyReturnBeforeElseClose(byte[] info, IFBlock IF ,java.lang.StringBuffer S,int j,OperandStack stack ){

            int end=IF.getElseCloseLineNumber();

               StringBuffer sg=new StringBuffer("");
               boolean r=isInstReturnInst(info,end,sg);
               if(!r)
               {
                  if(info[end]==JvmOpCodes.GOTO)
                  {
                      int gotoj=getJumpAddress(info,end);
                      sg=new StringBuffer("");
                      r=isInstReturnInst(info,gotoj,sg);
                  }
               }
               if(r)
               {
                   if(sg.toString().equals("return")==false)
                   {
                       if(stack.size() > 0)
                       {
                           java.lang.String t=stack.getTopOfStack().getOperandValue();
                           boolean b=isMethodRetBoolean(behaviour);
                           if(t!=null)
                           {
                               if(t.equals("1") && b)t="true";
                               if(t.equals("0") && b)t="false";
                               java.lang.String retst="return " +t+";";
                               S.append(Util.formatDecompiledStatement(retst));
                               returnsAtI.put(new Integer(end),"true");
                           }
                       }
                   }
                   if(sg.toString().equals("return")==true)
                   {
                       java.lang.String retst="return ;";
                       S.append(Util.formatDecompiledStatement(retst));
                       returnsAtI.put(new Integer(end),"true");
                   }
               }





       }



    private void checkForIFElseEndStatement(byte[] info,Hashtable ifHashTable,int i,StringBuffer reset,OperandStack stack,StringBuffer ifelsecode,java.lang.String w)

    {


                Iterator iter = ifHashTable.keySet().iterator();
               while(iter.hasNext()) {
                    Object key = iter.next();
                    IFBlock chk_ifst = (IFBlock)ifHashTable.get(key);
                    int chk_ifCloseLineNo = chk_ifst.getIfCloseLineNumber();
                    int chk_elseCloseLineNo = chk_ifst.getElseCloseLineNumber();
                    if(chk_elseCloseLineNo != -1)// && w.equals("else"))
                    {
                        if(i == chk_elseCloseLineNo)
                        {
                            //    boolean print=isThisLoopEndAlso(behaviour.getBehaviourLoops(),i);
                            boolean addelseend=true;//addElseEnd(i);
                            if(addelseend)
                            {
                                StringBuffer sb=new StringBuffer();
                                java.lang.String lbl=getBranchTypeAtI(i,chk_ifst,sb);
                                java.lang.String lbl2=getIfElseReturnAtI(i);
                                if(lbl2!=null)
                                {
                                    ifelsecode.append(Util.formatDecompiledStatement(lbl2+";\n"));
                                }

                                // BigInteger b
                                if(lbl!=null && !lbl.equals("") && !lbl.equals("continue") && lbl2==null)
                                {

                                    ifelsecode.append(Util.formatDecompiledStatement(lbl+"  "+sb.toString()+";\n"));

                                }
                                if(lbl!=null && !lbl.equals("") && lbl.equals("continue") && lbl2==null)
                                {
                                    ifelsecode.append(Util.formatDecompiledStatement(lbl+"  "+sb.toString()+";\n"));

                                }
                                 if(lbl2==null)
                                 {
                                StringBuffer sr=new StringBuffer("");
                                addAnyReturnBeforeElseClose(info,chk_ifst,sr,currentForIndex,stack);
                                ifelsecode.append(sr.toString());
                                 }

                                    StringBuffer in=new StringBuffer("");
                                    StringBuffer t=new StringBuffer("");

                                boolean isstore=isStoreInst(i,info,in,t);
                                if(isstore)
                                {

                                    int ind=Integer.parseInt(in.toString());
                                    if(ind!=-1 && opStack.size() > 0)
                                    {
                                        boolean simple=false;
                                        if((ind==0) || (ind==1) || (ind==2) || (ind==3))simple=true;
                                        LocalVariable loc=getLocalVariable(ind,"store",t.toString(),simple,i) ;
                                        Operand objref=(Operand)opStack.pop();  // Pop The Object Ref
                                        doNotPop=true;
                                        if(loc.wasCreated() && objref!=null && objref.getClassType().trim().length() > 0)loc.setDataType(objref.getClassType());
                                        java.lang.String temp=(java.lang.String)objref.getOperandValue();
                                        if(!loc.isDeclarationGenerated()) {
                                            temp=loc.getDataType().replace('/','.')+"  "+loc.getVarName()+"="+temp+";\n";
                                            //codeStatements += Util.formatDecompiledStatement(temp);

                                        }
                                        else
                                        {

                                            temp="  "+loc.getVarName()+" ="+temp+";\n";
                                            //codeStatements += Util.formatDecompiledStatement(temp);


                                        }
                                    }
                                    else
                                    {
                                        if(opStack.size() >= 3)
                                        {
                                            doNotPop=true;
                                            Operand value=opStack.getTopOfStack();  // Value
                                            Operand index=opStack.getTopOfStack();  // Index into target
                                            Operand arRef=opStack.getTopOfStack(); // Target Arref
                                            java.lang.String temp=arRef.getOperandValue()+"["+index.getOperandValue()+"] ="+value.getOperandValue()+";";
                                            //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                                        }
                                    }

                                }
                                else
                                {
                                    if(info[chk_ifCloseLineNo]==JvmOpCodes.GOTO)
                                    {
                                        int gotojumpa=getJumpAddress(info,chk_ifCloseLineNo);
                                        if(isThisInstrStart(behaviour.getInstructionStartPositions(),gotojumpa))
                                        {
                                           in=new StringBuffer("");
                                            t=new StringBuffer("");

                                             isstore=isStoreInst(gotojumpa,info,in,t);
                                            if(isstore)
                                            {
                                                int ind=Integer.parseInt(in.toString());
                                                if(ind!=-1 && opStack.size() > 0)
                                                {
                                                    boolean simple=false;
                                                    if((ind==0) || (ind==1) || (ind==2) || (ind==3))simple=true;
                                                    LocalVariable loc=getLocalVariable(ind,"store",t.toString(),simple,gotojumpa) ;
                                                    Operand objref=(Operand)opStack.pop();  // Pop The Object Ref
                                                    doNotPop=true;
                                                    if(loc.wasCreated() && objref!=null && objref.getClassType().trim().length() > 0)loc.setDataType(objref.getClassType());
                                                    java.lang.String temp=(java.lang.String)objref.getOperandValue();
                                                    if(!loc.isDeclarationGenerated())
                                                    {
                                                        temp=loc.getDataType().replace('/','.')+"  "+loc.getVarName()+"="+temp+";\n";
                                                        //codeStatements += Util.formatDecompiledStatement(temp);

                                                    }
                                                    else
                                                    {

                                                        temp="  "+loc.getVarName()+" ="+temp+";\n";
                                                        //codeStatements += Util.formatDecompiledStatement(temp);


                                                    }
                                                }
                                                else
                                                {
                                                    if(opStack.size() >= 3)
                                                    {
                                                        doNotPop=true;
                                                        Operand value=opStack.getTopOfStack();  // Value
                                                        Operand index=opStack.getTopOfStack();  // Index into target
                                                        Operand arRef=opStack.getTopOfStack(); // Target Arref
                                                        java.lang.String temp=arRef.getOperandValue()+"["+index.getOperandValue()+"] ="+value.getOperandValue()+";";
                                                        //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                                                    }
                                                }

                                            }
                                            else
                                            {
                                                if(info[gotojumpa]==JvmOpCodes.PUTFIELD)
                                                {
                                                    // Add here
                                                    doNotPop=true;
                                                    int pos=getOffset(info,gotojumpa);

                                                    FieldRef fref=cd.getFieldRefAtCPoolPosition(pos);
                                                    Operand value=opStack.getTopOfStack();
                                                    Operand objRef=null;
                                                    objRef =checkAnyStoredPUTFIELDObjRef(gotojumpa);
                                                    if(objRef==null && opStack.size() > 0)
                                                    {
                                                        objRef=opStack.getTopOfStack();
                                                    }

                                                    java.lang.String temp="";
                                                    java.lang.String freftype=fref.getTypeoffield();
                                                    if(objRef!=null) {
                                                    StringBuffer stb=new StringBuffer("");
                                                    checkForImport(objRef.getOperandValue(),stb);
                                                    temp=stb.toString()+"."+fref.getFieldName()+" = "+value.getOperandValue()+";";
                                                    }
                                                    else
                                                    {
                                                      temp=fref.getFieldName()+" = "+value.getOperandValue()+";";
                                                    }
                                                    //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                                                }
                                                if(info[gotojumpa]==JvmOpCodes.PUTSTATIC)
                                                {
                                                     doNotPop=true;
                                                    int pos=getOffset(info,gotojumpa);
                                                    /*parsedString+="PUTSTATIC\t";
                                                    parsedString+="#"+pos;
                                                    parsedString+="\n";
                                                    parsedString+="\t";parsedString+="\t";*/
                                                    FieldRef fref=cd.getFieldRefAtCPoolPosition(pos);
                                                    Operand value=opStack.getTopOfStack();
                                                    java.lang.String freftype=fref.getTypeoffield();

                                                    // For the code statement
                                                    int classpointer=fref.getClassPointer();
                                                    ClassInfo cinfo=cd.getClassInfoAtCPoolPosition(classpointer);
                                                    java.lang.String  classname=cd.getUTF8String(cinfo.getUtf8pointer());
                                                    java.lang.String v=value.getOperandValue().toString();
                                                    if(v.indexOf("(")==-1 && v.indexOf(")")!=-1)
                                                    {
                                                        v=v.replaceAll("\\)","");


                                                    }
                                                    v=v.trim();
                                                    StringBuffer stb=new StringBuffer("");
                                                    checkForImport(classname,stb);
                                                    java.lang.String  temp=stb.toString()+"."+fref.getFieldName()+" = "+v+";";
                                                    //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                                                }

                                            }


                                        }
                                    }
                                }


                                checkForATHROWAtIFelseEnd(chk_ifst,ifelsecode,i);
                                ifelsecode.append(Util.formatDecompiledStatement("}\n")); // Else End\n");
                                reset.append("true");
                                //elseendsadded.add(new Integer(i));
                            }
                            // ABove commmented by belurs
                            //ifHashTable.remove(key);
                            //ifLevel--;
                        }

                    }
                    //else
                    //{
                        if(i == chk_ifCloseLineNo)// && w.equals("if"))// && !chk_ifst.hasIfBeenClosed())
                        {
                            //   boolean print=isThisLoopEndAlso(behaviour.getBehaviourLoops(),i);
                            // System.out.println(print+"print"+i);
                           // boolean print =isThisIfALoopCondition(chk_ifst,info,behaviour.getBehaviourLoops());
                            //if(!print)          // TODO:
                            //{
                                StringBuffer sb=new StringBuffer();
                                java.lang.String lbl=getBranchTypeAtI(i,chk_ifst,sb);
                                //System.out.println(lbl+"lbl"+i);
                                java.lang.String lbl2=getIfElseReturnAtI(i);
                                if(lbl2!=null)
                                {
                                   ifelsecode.append(Util.formatDecompiledStatement(lbl2+";\n"));
                                }

                                if(lbl!=null && !lbl.equals("") && !lbl.equals("continue") && lbl2==null)
                                {
                                    ////codeStatements+= Util.formatDecompiledStatement(lbl+";\n");
                                    ifelsecode.append(Util.formatDecompiledStatement(lbl+"  "+sb.toString()+";\n"));

                                }
                                if(lbl!=null && !lbl.equals("") && lbl.equals("continue") && lbl2==null)
                                {
                                    ifelsecode.append(Util.formatDecompiledStatement(lbl+"  "+sb.toString()+";\n"));

                                }
                                if(lbl2==null)
                                {
                                    StringBuffer sr=new StringBuffer("");
                                    addAnyReturnBeforeIfClose(info,chk_ifst,sr,currentForIndex,stack);
                                    ifelsecode.append(sr.toString());
                                }
                                if(info[i]==JvmOpCodes.GOTO)
                                {
                                    int gotojumpa=getJumpAddress(info,i);
                                    boolean k=storealreadyhandledatifend(i);
                                    if(isThisInstrStart(behaviour.getInstructionStartPositions(),gotojumpa) && !k)
                                    {
                                        StringBuffer in=new StringBuffer("");
                                        StringBuffer t=new StringBuffer("");

                                        boolean isstore=isStoreInst(gotojumpa,info,in,t);
                                        if(isstore )
                                        {
                                           int ind=Integer.parseInt(in.toString());
                                           if(ind!=-1 && opStack.size() > 0)
                                           {
                                              boolean simple=false;
                                              if((ind==0) || (ind==1) || (ind==2) || (ind==3))simple=true;
                                              LocalVariable loc=getLocalVariable(ind,"store",t.toString(),simple,gotojumpa) ;
                                               Operand objref=(Operand)opStack.pop();  // Pop The Object Ref
                                               doNotPop=true;
                                               if(loc.wasCreated() && objref!=null && objref.getClassType().trim().length() > 0)loc.setDataType(objref.getClassType());
                                               java.lang.String temp=(java.lang.String)objref.getOperandValue();
                                               if(!loc.isDeclarationGenerated()) {
                                                   temp=loc.getDataType().replace('/','.')+"  "+loc.getVarName()+"="+temp+";\n";
                                                   //codeStatements += Util.formatDecompiledStatement(temp);

                                               }
                                               else
                                               {

                                                   temp="  "+loc.getVarName()+" ="+temp+";\n";
                                                   //codeStatements += Util.formatDecompiledStatement(temp);


                                               }
                                               storesatifend.add(new Integer(i));
                                           }
                                           else
                                           {
                                               if(opStack.size() >= 3)
                                               {
                                                   doNotPop=true;
                                                   Operand value=opStack.getTopOfStack();  // Value
                                                   Operand index=opStack.getTopOfStack();  // Index into target
                                                   Operand arRef=opStack.getTopOfStack(); // Target Arref
                                                   java.lang.String temp=arRef.getOperandValue()+"["+index.getOperandValue()+"] ="+value.getOperandValue()+";";
                                                   //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                                                   storesatifend.add(new Integer(i));
                                               }
                                           }

                                        }
                                        else
                                        {
                                            if(info[gotojumpa]==JvmOpCodes.PUTFIELD)
                                            {
                                                // Add here
                                                doNotPop=true;
                                                int pos=getOffset(info,gotojumpa);

                                                FieldRef fref=cd.getFieldRefAtCPoolPosition(pos);
                                                Operand value=opStack.getTopOfStack();
                                                Operand objRef=opStack.getTopOfStack();
                                                putfieldObjRefMap.put(new Integer(gotojumpa),objRef);
                                                java.lang.String freftype=fref.getTypeoffield();
                                                StringBuffer stb=new StringBuffer("");
                                                checkForImport(objRef.getOperandValue(),stb);
                                                java.lang.String temp=stb.toString()+"."+fref.getFieldName()+" = "+value.getOperandValue()+";";
                                                //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                                            }
                                            if(info[gotojumpa]==JvmOpCodes.PUTSTATIC)
                                            {
                                                 doNotPop=true;
                                                int pos=getOffset(info,gotojumpa);
                                                /*parsedString+="PUTSTATIC\t";
                                                parsedString+="#"+pos;
                                                parsedString+="\n";
                                                parsedString+="\t";parsedString+="\t";*/
                                                FieldRef fref=cd.getFieldRefAtCPoolPosition(pos);
                                                Operand value=opStack.getTopOfStack();
                                                java.lang.String freftype=fref.getTypeoffield();

                                                // For the code statement
                                                int classpointer=fref.getClassPointer();
                                                ClassInfo cinfo=cd.getClassInfoAtCPoolPosition(classpointer);
                                                java.lang.String  classname=cd.getUTF8String(cinfo.getUtf8pointer());
                                                java.lang.String v=value.getOperandValue().toString();
                                                if(v.indexOf("(")==-1 && v.indexOf(")")!=-1)
                                                {
                                                    v=v.replaceAll("\\)","");


                                                }
                                                v=v.trim();
                                                StringBuffer stb=new StringBuffer("");
                                                checkForImport(classname,stb);
                                                java.lang.String  temp=stb.toString()+"."+fref.getFieldName()+" = "+v+";";
                                                //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
                                            }

                                        }

                                    }
                                }
                                if(!chk_ifst.IfHasBeenClosed() && chk_ifst.getDonotclose()==false)
                                {
                                    checkForATHROWAtIFelseEnd(chk_ifst,ifelsecode,i);
                                    ifelsecode.append(Util.formatDecompiledStatement("\n}\n"));// if end\nIFEND"+chk_ifst.getIfCloseLineNumber()+"");
                                    //System.out.println(chk_ifst.getIfCloseLineNumber()+" "+chk_ifst.getIfCloseFromByteCode());
                                    ifhasbegun=false;
                                    chk_ifst.setIfHasBeenClosed(true);
                                }

                           // }
                            //iter.remove();
                            //ifHashTable.remove(key);
                            //ifLevel--;
                        }

                   // }
                }

    }



   /* private boolean pushOnStackForStoreInst(byte[] info,int cur,boolean type)
    {
       if(type)
       {
           int next=cur+1;
           switch(info[next])
           {

               case JvmOpCodes.IADD:
               case JvmOpCodes.IAND:
               case JvmOpCodes.I




           }


       }
       else
       {

       }

    }*/

    // belurs:
    /***
     * NOTE:
      Currently This method takes into account only dup and dup2 NOT dup2_x1 and dup_x2
     So the Disassembler might not output correctly if the dup inst if one of  dup2_x1 and dup_x2
     TODO: Need to revisit This Item and fix the problem
     This method is used by primitive store blocks to check whether to print
     Or add tp stack
     */

    private boolean isPrevInstDup(byte[] info,int cur)
    {

       if( isThisInstrStart(behaviour.getInstructionStartPositions(),cur-1) && (info[cur-1]==JvmOpCodes.DUP || info[cur-1]==JvmOpCodes.DUP2))return true;
        else
           return false;
    }


    private void popFromStackIFNec(byte[] info,int cur,OperandStack stack)

    {
      ArrayList list=behaviour.getInstructionStartPositions();

      if(info[cur-5]==JvmOpCodes.INVOKEINTERFACE && isThisInstrStart(list,cur-5))
      {
        stack.getTopOfStack();
      }
      if(info[cur-3]==JvmOpCodes.INVOKESPECIAL && isThisInstrStart(list,cur-3))
      {
         stack.getTopOfStack();
      }
      if(info[cur-3]==JvmOpCodes.INVOKEVIRTUAL && isThisInstrStart(list,cur-3))
      {
         stack.getTopOfStack();
      }
        if(info[cur-3]==JvmOpCodes.INVOKESTATIC && isThisInstrStart(list,cur-3))
      {
          stack.getTopOfStack();
      }

    }


    private void handleSimpleLStoreCase(OperandStack stack,byte[] info,StringBuffer code,int index)
    {

        java.lang.String tempString="";
        LocalVariable local;
        local=getLocalVariable(index,"store","long",true,currentForIndex);
        if(local!=null && cd.isClassCompiledWithMinusG())
                                {
                                    addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lstore_ "+index+" THIS LocalVariable:-  "+local.getVarName()+"\n");
                                }
                                else
                                addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lstore_"+index+"\n");

        if(local != null && !doNotPop) {
             Operand op=stack.getTopOfStack();
            prevLocalGenerated = local;
            boolean push=isPrevInstDup(info,currentForIndex);
            if(!push)
            {
                if(!local.isDeclarationGenerated()) {
                    local.setBlockIndex(blockLevel);
                    tempString=local.getDataType()+" "+local.getVarName()+"="+op.getOperandValue()+";\n";
                    code.append(Util.formatDecompiledStatement(tempString));
                    local.setDeclarationGenerated(true);
                } else {
                    tempString=local.getVarName()+"="+op.getOperandValue()+";\n";
                    code.append(Util.formatDecompiledStatement(tempString));
                }
            }
            else
            {
               if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP)
                                opStack.getTopOfStack();
                              if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP2)
                              {
                                opStack.getTopOfStack();
                                   opStack.getTopOfStack();
                              }
                code.append(Util.formatDecompiledStatement(local.getVarName()+"=("+op.getOperandValue()+");\n"));
                Operand op2=createOperand(local.getVarName());
                stack.push(op2);
            }
        }
       if(doNotPop==true)doNotPop=false;
    }

    private void handleSimpleFstoreCaseInst(OperandStack stack,byte[] info,int index,StringBuffer srb)
    {
        LocalVariable local=getLocalVariable(index,"store","float",true,currentForIndex);
        java.lang.String tempString="";
        if(cd.isClassCompiledWithMinusG() && local!=null)
        {
             addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fstore_"+index+" THIS LocalVariable :- "+local.getVarName()+"\n");
        }
        else
        {
            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fstore_"+index+"\n");
        }
        if(local != null && !doNotPop) {
            Operand op =(Operand)stack.pop();
            prevLocalGenerated = local;
            boolean push=isPrevInstDup(info,currentForIndex);
            if(!push)
            {
                if(!local.isDeclarationGenerated()) {
                    local.setBlockIndex(blockLevel);
                    tempString=local.getDataType()+" "+local.getVarName()+"="+op.getOperandValue()+";\n";
                    srb.append(Util.formatDecompiledStatement(tempString));
                    local.setDeclarationGenerated(true);
                } else {
                    tempString=local.getVarName()+"="+op.getOperandValue()+";\n";
                    srb.append(Util.formatDecompiledStatement(tempString));
                }
            }
            else
            {
                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP)
                    opStack.getTopOfStack();
                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP2)
                {
                    opStack.getTopOfStack();
                    opStack.getTopOfStack();
                }
                srb.append(Util.formatDecompiledStatement(local.getVarName()+"=("+op.getOperandValue()+");\n"));
                Operand op2=createOperand(local.getVarName());
                stack.push(op2);
            }
        }
        if(doNotPop==true)doNotPop=false;
    }


    private void handleSimpleDstoreCaseInst(OperandStack stack,byte[] info,int index,StringBuffer srb)
    {
        LocalVariable  local=getLocalVariable(index,"store","double",true,currentForIndex);
        if(cd.isClassCompiledWithMinusG())
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dstore_"+index+" THIS LOCALVARIABLE :- "+local.getVarName()+"\n");
        else
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" dstore_"+index+"\n");
        java.lang.String tempString="";

        if(local != null && !doNotPop) {
             Operand op =(Operand)stack.pop();
            prevLocalGenerated = local;
            boolean push=isPrevInstDup(info,currentForIndex);
            if(!push)
            {
                if(!local.isDeclarationGenerated()) {
                                local.setBlockIndex(blockLevel);
                                //if(local.wasCreated())local.setDataType(op.getClassType());
                                int semiColon=op.getOperandValue().toString().indexOf(";");
                                if(semiColon==-1)
                                {
                                    tempString=local.getDataType()+" "+local.getVarName()+"="+op.getOperandValue()+";\n";
                                    srb.append(Util.formatDecompiledStatement(tempString));
                                }
                                else
                                {
                                    tempString=local.getDataType()+" "+local.getVarName()+"="+op.getOperandValue();
                                    srb.append(Util.formatDecompiledStatement(tempString));
                                }
                                local.setDeclarationGenerated(true);
                            } else {
                                int semiColon=op.getOperandValue().toString().indexOf(";");
                                if(semiColon==-1)
                                {
                                    tempString=local.getVarName()+"="+op.getOperandValue()+";\n";
                                    srb.append(Util.formatDecompiledStatement(tempString));
                                }
                                else
                                {
                                    tempString=local.getVarName()+"="+op.getOperandValue();
                                    srb.append(Util.formatDecompiledStatement(tempString));
                                }
                            }
            }
            else
            {
                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP)
                    opStack.getTopOfStack();
                if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP2)
                {
                    opStack.getTopOfStack();
                    opStack.getTopOfStack();
                }
                srb.append(Util.formatDecompiledStatement(local.getVarName()+"=("+op.getOperandValue()+");\n"));
                Operand op2=createOperand(local.getVarName());
                stack.push(op2);
            }
        }
         if(doNotPop==true)doNotPop=false;
    }




    private Hashtable anewarrayrefpos;





    private boolean processANEWARRAYb4Invoke(byte []code,int s,StringBuffer invokepos)
    {

        int startpos=s+2+1;
        ArrayList starts=behaviour.getInstructionStartPositions();
        boolean donotprocess=false;
        boolean foundinvoke=false;
        int pos=-1;
        for(int k=startpos;k<code.length;k++)
        {

            boolean startinst=isThisInstrStart(starts,k);
            if(code[k]==JvmOpCodes.INVOKEINTERFACE || code[k]==JvmOpCodes.INVOKEVIRTUAL ||  code[k]==JvmOpCodes.INVOKESTATIC)
            {
                foundinvoke=true;
                pos=k;
                break;
            }
        }

        if(pos!=-1)
        {
            for(int k=startpos;k<pos;k++)
            {
                boolean startinst=isThisInstrStart(starts,k);
                if(startinst)
                {

                    switch(code[k])
                    {

                        case  JvmOpCodes.NEW:
                        case JvmOpCodes.NEWARRAY:
                        case JvmOpCodes.MULTIANEWARRAY:
                            invokepos.append(pos);
                            donotprocess=true;
                            return donotprocess;

                    }

                }
            }
        }

        return donotprocess;



    }


    private ArrayList problematicInvokes;




    private void resetMethodParameters(OperandStack stack,ArrayList methodParams,int j)
    {
      // Check if reset needs to be done in the first place
      if(problematicInvokes==null || problematicInvokes.size()==0)return;
      boolean ok=false;
    for(int n=0;n<problematicInvokes.size();n++)
      {

          Integer in=(Integer)problematicInvokes.get(n);
          if(in.intValue()==j)
          {
              ok=true; // Yes Disassembler
              break;
          }
          else
              ok=false;

      }
      if(!ok)return;

      // some validations
      if(methodParams==null || stack==null)return;
      int count=methodParams.size();
      if(count==0)return;
      if(stack.size() < count)return; // Should Not happen: A Bug in jdec
      Operand reqdops[]=new Operand[count];
      boolean needtoresetstack=false;
      for(int h=0;h<count;h++)
      {
         reqdops[h]=stack.getTopOfStack();
         needtoresetstack=true;
      }


     // IMPORTANT : IF needtoresetstack IS TRUE method shud return only after resetting stack .NOT before that

      // start Resetting
      int opstart=0;
      for(int z=count-1;z>=0;z--)
      {

        Operand current=reqdops[opstart];
        opstart++;
        if(current!=null)
        {
           java.lang.String type=current.getLocalVarType();
           boolean needtoreset=false;
            java.lang.String param=(java.lang.String)methodParams.get(z);
           if(type!=null && type.trim().equals("int"))
           {

             if(param.trim().equals("byte"))
             {
                needtoreset=true;
             }
             else if(param.trim().equals("boolean"))
             {
               needtoreset=true;
             }
             else if(param.trim().equals("short"))
             {
                needtoreset=true;
             }
             else if(param.trim().equals("char"))
             {
               needtoreset=true;
             }
             else
             {
               needtoreset=false;
             }
             if(needtoreset)
             {
                int localIndex=current.getLocalVarIndex();
                java.lang.String searchFor="#REPLACE_INT_"+localIndex+"#";
                int len=searchFor.length();
                int start=-1;//codeStatements.indexOf(searchFor);
                 java.lang.String name="";
                if(start!=-1)
                {
                    java.lang.String tp="";//codeStatements.substring(start+len,start+len+3);
                    int equalTo=-1;
                    StringBuffer newpos=new StringBuffer("");
                    if(tp.equals("int"))
                    {
                        //java.lang.String=//codeStatements.replaceFirst(searchFor+"int",param.trim());
                         name=(java.lang.String)current.getOperandValue();

                        //codeStatements=getStringAfterReplacement(//codeStatements,start,searchFor+"int",param.trim(),newpos, name,false,param.trim());

                    }
                    try
                    {
                        equalTo=Integer.parseInt(newpos.toString());
                    }
                    catch(NumberFormatException ne)
                    {
                        equalTo=-1;
                    }

                    if(equalTo!=-1)
                    {
                        int valueIn=-1;//codeStatements.indexOf("#VALUE"+localIndex+"#",equalTo);
                        if(valueIn!=-1)
                        {
                            java.lang.String valuehash="#VALUE"+localIndex+"#";
                            java.lang.String val="";//codeStatements.substring(valueIn+valuehash.length(),valueIn+valuehash.length()+1);

                            //codeStatements=getStringAfterReplacement(//codeStatements,valueIn,"#VALUE"+localIndex+"#",val,newpos,name,true,param.trim());
                        }
                    }
                }



             }




           }
           else if(type!=null && type.trim().equals(""))
           {

             if(param.trim().equals("byte") || param.trim().equals("short") || param.trim().equals("char"))
             {

                      current.setOperandValue("("+param+")"+current.getOperandValue());

             }
           }
           // Check for multidimensional.....

           if(param.trim().indexOf("[")!=-1)
           {
             int first=param.trim().indexOf("[");
             int last=param.trim().lastIndexOf("[");
             int howmany=last-first+1;
             boolean isMulti=current.isMultiDimension();
             if(isMulti)
             {
                 java.lang.String value=(java.lang.String)current.getOperandValue();
                 /*if(value.indexOf("[")!=-1)
                 {
                     /*int cnt=1;
                     int start=value.indexOf("[");
                     int next=start+1;
                     while(next < value.length())
                     {

                         if(value.charAt(next)=='[')
                         {
                             cnt++;
                         }
                         next++;

                     }

                     int total=cnt+howmany;*/
                  java.lang.String bracks="";
                   for(int s=0;s<howmany;s++)
                   {

                      bracks+="[]";
                   }
                   value+=bracks;
                   current.setOperandValue(value);





             }



           }


        }
        else       // Again should not happen at all. Returning  if this happens , so that jdec will not reset a wrong operand's value
        {
            // Restore Stack
            for(int l=reqdops.length-1;l>=0;l--)
            {
                Operand op=reqdops[l];
                stack.push(op);
            }
            return;
        }

      }

      // This should be final step before returning
      if(needtoresetstack)
      {
           // Restore Stack
                     for(int l=reqdops.length-1;l>=0;l--)
                     {
                         Operand op=reqdops[l];
                         stack.push(op);
                     }
      }


    }





     private java.lang.String getStringAfterReplacement(java.lang.String codeStatements,int fromwhere,java.lang.String lookfor,java.lang.String replaceString,StringBuffer sb,java.lang.String name,boolean skipone,java.lang.String methodparam)
     {
       int equal=-1;

       java.lang.String orig="";//codeStatements;
       java.lang.String temp1="";//codeStatements.substring(0,fromwhere);
       java.lang.String temp2=replaceString;
         java.lang.String temp3="";
       if(skipone)
       {
        if(methodparam.equalsIgnoreCase("boolean")){

            if(replaceString.trim().equalsIgnoreCase("0"))
            {
              temp2="false";
            }
            else if(replaceString.trim().equalsIgnoreCase("1"))
            {
              temp2="true";
            }
            else
            {

            }

        }
        temp3="";//codeStatements.substring(fromwhere+lookfor.length()+1);
       }
         else
       {
        temp3="";//codeStatements.substring(fromwhere+lookfor.length());
       }

       orig=temp1+temp2+temp3;
         equal=orig.indexOf("=",orig.indexOf(replaceString+"\t"+name));
        sb.append(equal) ;
       return orig;
     }

    private boolean isNextInstAStore(byte[] info,int pos){

        if(isThisInstrStart(behaviour.getInstructionStartPositions(),pos))
        {
            if(info[pos] == JvmOpCodes.ASTORE)
            {

                return true;
            }
            if(info[pos] == JvmOpCodes.ASTORE_0)
            {
               return true;

            }
            if(info[pos] == JvmOpCodes.ASTORE_1)
            {

               return true;
            }
            if(info[pos] == JvmOpCodes.ASTORE_2)
            {
              return true;

            }
            if(info[pos] == JvmOpCodes.ASTORE_3)
            {
             return true;

            }
        }
        return false;



    }


    private void handleACONSTNULL()
    {
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" aconst_null\n");
        NullObject nullobj=new NullObject();
        Operand op = createOperand(nullobj.getIdentification());
        op.setOperandType(Constants.IS_NULL);
        opStack.push(op);
    }




    private void handlesimpleaload(int index)
    {

        LocalVariable local=getLocalVariable(index,"load","java.lang.Object",true,currentForIndex);
        if(local==null)
        {
            local=new LocalVariable();
            local.setVarName("this");
        }

        prevLocalGenerated = local;
        Operand op = new Operand();
        op.setOperandName(local.getVarName());
        op.setOperandValue(local.getVarName());
        op.setOperandType(Constants.IS_OBJECT_REF);
        opStack.push(op);
        op.setClassType(local.getDataType());
        java.lang.String t="Instruction Pos "+currentForIndex+"\t:- "+" aload_"+index;
        if(cd.isClassCompiledWithMinusG() && local!=null)
        {
            t+=" This LocalVariable :- "+local.getVarName();
        }
        t+="\n";
        addParsedOutput(t);

    }



    private void handleARRAYLENGTHCase()
    {
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" arraylength\n");
     Operand    op = (Operand)opStack.pop();
                 Operand       tempOperand=op;
                        op = new Operand();
                        op.setOperandValue(tempOperand.getOperandValue()+".length");
                        //op.setCategory(Constants.CATEGORY1);
                        //op.setOperandType(Constants.IS_CONSTANT_INT);
                        opStack.push(op);
    }




    private void handleARETURNCase(int i,HashMap returnsAtI)
    {
                        boolean oktoadd=true;
                        Iterator mapIT=returnsAtI.entrySet().iterator();
                        while(mapIT.hasNext())
                        {
                            Map.Entry entry=(Map.Entry)mapIT.next();
                            Object key=entry.getKey();
                            Object retStatus=entry.getValue().toString();
                            if(key instanceof Integer)
                            {
                                Integer pos=(Integer)key;
                                int temp=pos.intValue();
                                if(temp==i)
                                {
                                    if(retStatus.equals("true"))
                                    {

                                        oktoadd=false;
                                        break;
                                    }
                                }
                            }

                        }


                        if(!oktoadd)
                        {
                            returnsAtI.remove(new Integer(i));
                        }
                        Object obj=returnsAtI.get(new Integer(i));
        java.lang.String tempString="";
                        if(oktoadd && opStack.size() > 0){
                                //System.out.println(currentForIndex+"i"+behaviour.getBehaviourName());
                            Operand op = (Operand)opStack.pop();

                            if(op.getOperandValue()!=null && op.getOperandValue().toString().indexOf(";")==-1)
                                tempString="return "+op.getOperandValue()+";\n";
                            else
                                tempString="return "+op.getOperandValue()+"\n";
                            //codeStatements+=Util.formatDecompiledStatement(tempString);


                        }
    }




    private void handleSIPUSH(byte[] info)
    {
        int opValueI = getOffset(info,currentForIndex);
          addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" sipush "+opValueI+"\n");
            Operand            op = new Operand();
                       java.lang.String  tp=null;
                        if(pushTypes!=null)
                        {
                             tp=(java.lang.String)pushTypes.get(new Integer(currentForIndex));
                            if(tp!=null)
                            {
                             tp="("+tp+")"+opValueI;
                            }

                        }



                        if(tp!=null)

                            op.setOperandValue(tp);//                           // new Integer(opValueI));
                        else
                          op.setOperandValue(new Integer(opValueI));
                        op.setOperandType(Constants.IS_CONSTANT_SHORT);
                        opStack.push(op);
    }


    private void handleBIPush(byte[] info)
    {
        int opValueB = info[(currentForIndex+1)];
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" bipush "+opValueB+"\n");
            Operand            op = new Operand();
                        java.lang.String tp=null;
                        if(pushTypes!=null)
                        {
                             tp=(java.lang.String)pushTypes.get(new Integer(currentForIndex));
                            if(tp!=null)
                            {
                             tp="("+tp+")"+opValueB;
                            }

                        }



                        if(tp!=null)

                            op.setOperandValue(tp);//                           // new Integer(opValueI));
                        else
                          op.setOperandValue(new Integer(opValueB));

                        //op.setOperandType(Constants.IS_CONSTANT_BYTE);
                        //op.setCategory(Constants.CATEGORY1);
                        opStack.push(op);

    }




    private void handleSASTORE()
    {
         Operand value=opStack.getTopOfStack();  // Value
                Operand        index=opStack.getTopOfStack();  // Index into target
                       Operand arRef=opStack.getTopOfStack(); // Target Arref
                        java.lang.String temp=arRef.getOperandValue()+"["+index.getOperandValue()+"] ="+value.getOperandValue()+";";
                        //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
    }




    private void handleSALOAD()
    {
       Operand  index=opStack.getTopOfStack();
               Operand         arRef=opStack.getTopOfStack();
                java.lang.String        result=arRef.getOperandValue()+"["+index.getOperandValue()+"]";
                   Operand     op=new Operand();
                        op.setOperandValue(result);
                        op.setOperandType(Constants.IS_CONSTANT_SHORT);
                        opStack.push(op);
    }




    private void handleSimpleReturn()
    {
        java.lang.String temp=behaviour.getReturnType();



                                boolean oktoadd=true;
                              Iterator  mapIT=returnsAtI.entrySet().iterator();
                                while(mapIT.hasNext())
                                {
                                    Map.Entry entry=(Map.Entry)mapIT.next();
                                    Object key=entry.getKey();
                                    Object retStatus=entry.getValue().toString();
                                    if(key instanceof Integer)
                                    {
                                        Integer position=(Integer)key;
                                        int posValue=position.intValue();
                                        if(posValue==currentForIndex)
                                        {
                                            if(retStatus.equals("true"))
                                            {

                                                oktoadd=false;
                                                break;
                                            }
                                        }
                                    }

                                }


                                if(!oktoadd)
                                {
                                    returnsAtI.remove(new Integer(currentForIndex));
                                }


                                if(oktoadd){
                                    java.lang.String tempString=Util.formatDecompiledStatement("return;\n");
                                    //codeStatements+=tempString;

                                }

    }



    private void handleNEWARRAYCase(byte[] info)
    {
             java.lang.String  temp="";
                        switch(info[(currentForIndex+1)]) {
                            case 4:

                                temp="boolean[";
                                break;
                            case 5:

                                temp="char[";
                                break;
                            case 6:

                                temp="float[";
                                break;
                            case 7:

                                temp="double[";
                                break;
                            case 8:

                                temp="byte[";
                                break;
                            case 9:

                                temp="short[";
                                break;
                            case 10:

                                temp="int[";
                                break;
                            case 11:

                                temp="long[";
                                break;
                        }


                        // Pop The Size
                        Operand arSize=opStack.getTopOfStack();

                        // A check
                        /*if(arSize.getOperandType()!=Constants.IS_CONSTANT_INT && arSize.getOperandType()!=Constants.IS_CONSTANT_BYTE) {
                        throw new StackOperationException("Error While handling newarray instruction. The SIze of array is not of type int . \nType Found to be "+arSize.getOperandType());

                        } else {*/
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" newarray :- "+temp+"\n");
                        Operand op=new Operand();
                        op.setOperandType(Constants.IS_ARRAY_REF);
                        java.lang.String Reference="JdecGenerated"+(currentForIndex+1);
                        op.setClassType(temp+"]");
                        temp=temp+"] "+Reference+" = new "+temp+arSize.getOperandValue()+"]";
                        //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+";\n";
                        //temp="new "+temp+arSize.getOperandValue()+"]";
                        op.setOperandValue(Reference);
                        opStack.push(op);
    }



    private void handleAASTORECase()
    {
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" AAstore\n");
        if(!doNotPop)
        {
                        Operand value=(Operand)opStack.pop();
                        Operand arindex=(Operand)opStack.pop();
                        Operand arRef=(Operand)opStack.pop();
                        java.lang.String tempString=arRef.getOperandValue()+"["+arindex.getOperandValue()+"]="+value.getOperandValue()+";\n";
                        //codeStatements += Util.formatDecompiledStatement(tempString);
        }
        if(doNotPop==true)doNotPop=false;
    }




    private void handleANEWARRAYCase(byte[] info)
    {




        int temp1=info[(currentForIndex+1)];
                        int temp2=info[(currentForIndex+2)];
                        int classIndex=getOffset(info,currentForIndex);//(temp1 << 8) | temp2);
                       // if(classIndex < 0)classIndex=(temp1+1)*256-Math.abs(temp2);
                        ClassInfo cinfo=cd.getClassInfoAtCPoolPosition(classIndex);
                        java.lang.String Type=cd.getUTF8String(cinfo.getUtf8pointer());
                        int bracketCount=Type.lastIndexOf("[");
                        bracketCount=bracketCount+2; // the one which is required.
                        java.lang.String tempString="";
                        Type=parse(Type);
                        int brk=Type.indexOf("[");
                        if(brk >= 0)
                            Type=Type.substring(0,brk)  ;
                        Type=Type.replace('/','.');
                        tempString+=Type;
                        java.lang.String brackets="";

                        for(int counter=0;counter<bracketCount;counter++) {
                            brackets+="[]";
                        }
                        java.lang.String Ref="JdecGeneratedVar"+(currentForIndex+2);
                        tempString+=" "+brackets+"  "+Ref+"= new "+Type;
                        brackets="";
                        Operand count=(Operand)opStack.pop();
                        Integer intsize=null;
                        java.lang.String SizeofArray="";

                            SizeofArray=count.getOperandValue();
                        for(int counter=0;counter<bracketCount;counter++) {
                            if(counter==0)
                                brackets+="["+SizeofArray+"]";
                            else
                                brackets+="[]";
                        }
                        tempString+=brackets+";";
                        //codeStatements+=Util.formatDecompiledStatement(tempString)+";\n";
                        tempString="";
                        //parsedString+=Type;
                        //parsedString+="\n";
                        Operand op=new Operand();

                        /**
                         * IMPORTANT NOTE:
                         */
                        // Previous Code Statement
                        // Needed to modify so as to produce code
                        // for statement like short s[][]={ {1,2} ,{3,4} );
                        // Side Effect of this will be to produce extra statements in the
                        // decompiled code which will have ConsoleLauncher generated temporary variables
                        // This however will REFLECT the actual code produced by the compiler
                        // and not anuthing extra
                        //op.setOperandValue("new "+(cd.getUTF8String(cinfo.getUtf8pointer())).replace('/','.')+"["+arraySize+"]");
                        op.setOperandValue(Ref);
                        op.setOperandType(Constants.IS_ARRAY_REF);
                        op.setClassType(Type);
                        opStack.push(op);

java.lang.String t="Instruction Pos "+currentForIndex+"\t:- "+" anewaray ";
        t+=temp1+" "+temp2;
        t+="new "+Type+"\n";
        addParsedOutput(t);



    }



    private void checkForImport(java.lang.String input,StringBuffer sb)
    {

        if(input.indexOf(".")==-1 && input.indexOf("/")==-1)
        {
            sb.append(input);
            return ;
        }
        if(Configuration.getShowImport().equalsIgnoreCase("false"))
        {

            sb.append(input);
            return ;
        }
        if(Configuration.getShowImport().equalsIgnoreCase("true"))
        {
            java.lang.String simplename="";
            java.lang.String fullName=input;
            int lastSlash=fullName.lastIndexOf("/");
            if(lastSlash == -1)
              {
                lastSlash=fullName.lastIndexOf(".");
              }
            if(lastSlash!=-1)
            {
                simplename=fullName.substring(lastSlash+1);
            }
            else
                simplename=fullName;
            fullName=fullName.replace('/','.');
            ConsoleLauncher.addImportClass(fullName);
            sb.append(simplename);
            return ;

        }
        // Default
            sb.append(input);
            return ;




    }




    private int getPrevStartCodePos(byte[] info,int i)
    {
        int current=i;
        ArrayList allstarts=behaviour.getInstructionStartPositions();
        int z;
        for(z=current-1;z>=0;z--)
        {

           boolean ok=isThisInstrStart(allstarts,z);
            if(ok)
            {
                return z;
            }
        }
        return z;


    }



    private boolean isStoreInst(int index,byte []info,StringBuffer varindex,StringBuffer t)
    {

        boolean b=isThisInstrStart(behaviour.getInstructionStartPositions(),index);
        if(b==false)return false;
        switch(info[index])
        {
            case JvmOpCodes.AASTORE:
            case JvmOpCodes.BASTORE:
            case JvmOpCodes.CASTORE:
            case JvmOpCodes.DASTORE:
            case JvmOpCodes.FASTORE:
            case JvmOpCodes.IASTORE:
            case JvmOpCodes.LASTORE:
            case JvmOpCodes.SASTORE:
                varindex.append("-1");
                return true;

            case JvmOpCodes.ASTORE:
                  varindex.append(info[(index+1)]);
                t.append("java.lang.Object");
                return true;
            case JvmOpCodes.DSTORE:
                varindex.append(info[(index+1)]);
                t.append("double");
                return true;
            case JvmOpCodes.FSTORE:
                varindex.append(info[(index+1)]);
                t.append("float");
                return true;
            case JvmOpCodes.ISTORE:
                varindex.append(info[(index+1)]);
                t.append("int");
                return true;
            case JvmOpCodes.LSTORE:
                varindex.append(info[(index+1)]);
                t.append("long");
                return true;

            case JvmOpCodes.ASTORE_0:
                varindex.append("0");
                t.append("java.lang.Object");
                return true;

            case JvmOpCodes.DSTORE_0:
                varindex.append("0");
                t.append("double");
                return true;
            case JvmOpCodes.FSTORE_0:
                varindex.append("0");
                t.append("float");
                return true;
            case JvmOpCodes.ISTORE_0:
                varindex.append("0");
                t.append("int");
                return true;
            case JvmOpCodes.LSTORE_0:
                varindex.append("0");
                t.append("long");
                return true;





            case JvmOpCodes.ASTORE_1:
                varindex.append("1");
                t.append("java.lang.Object");
                return true;
            case JvmOpCodes.DSTORE_1:
                varindex.append("1");
                t.append("double");
                return true;
            case JvmOpCodes.FSTORE_1:
                varindex.append("1");
                t.append("float");
                return true;
            case JvmOpCodes.ISTORE_1:
                varindex.append("1");
                t.append("int");
                return true;
            case JvmOpCodes.LSTORE_1:
                varindex.append("1");
                t.append("long");
                return true;

            case JvmOpCodes.ASTORE_2:
                varindex.append("2");
                t.append("java.lang.Object");
                return true;
            case JvmOpCodes.DSTORE_2:
                varindex.append("2");
                t.append("double");
                return true;
            case JvmOpCodes.FSTORE_2:
                varindex.append("2");
                t.append("float");
                return true;
            case JvmOpCodes.ISTORE_2:
                varindex.append("2");
                t.append("int");
                return true;
            case JvmOpCodes.LSTORE_2:
                varindex.append("2");
                t.append("long");
                return true;

            case JvmOpCodes.ASTORE_3:
                varindex.append("3");
                t.append("java.lang.Object");
                return true;
            case JvmOpCodes.DSTORE_3:
                varindex.append("3");
                t.append("double");
                return true;
            case JvmOpCodes.FSTORE_3:
                varindex.append("3");
                t.append("float");
                return true;
            case JvmOpCodes.ISTORE_3:
                varindex.append("3");
                t.append("int");
                return true;
            case JvmOpCodes.LSTORE_3:
                varindex.append("3");
                t.append("long");
                return true;


            default:
                return false;

        }



    }

    private boolean doNotPop=false;



    private void handleComplexAStore(byte[] info,int i)
    {
        int instructionPos=i;
        ArrayList methodTries=behaviour.getAllTriesForMethod();
        int classIndex = info[++i];
        boolean add=checkForStartOfCatch(instructionPos,methodTries);

        //
        java.lang.String t="Instruction Pos "+currentForIndex+"\t:- "+"astore "+classIndex;
        LocalVariable local=null;
        if(cd.isClassCompiledWithMinusG())
        {
            local=getLocalVariable(classIndex,"store","java.lang.Object",false,currentForIndex);
            if(local!=null)
            {
                t+=" THIS localvariable :-  "+local.getVarName();
            }
        }
        t+="\n";
        addParsedOutput(t);
        if(add==true && !doNotPop)
        {

            //local=getLocalVariable(classIndex,"store","java.lang.Object",false,currentForIndex);
            if(local!=null) {
                prevLocalGenerated = local;
                // temp has new <Class Name>[<size>]
                boolean push=isPrevInstDup(info,currentForIndex);
                if(!push)
                {
                     Operand objref=(Operand)opStack.pop();  // Pop The Object Ref
                if(local.wasCreated() && objref!=null && objref.getClassType().trim().length() > 0)local.setDataType(objref.getClassType());
                java.lang.String temp=(java.lang.String)objref.getOperandValue();
                    if(!local.isDeclarationGenerated()) {
                        local.setBlockIndex(i-1);
                        if(!multinewound)
                        {
                            StringBuffer m=new StringBuffer("");
                            checkForImport(local.getDataType().replace('/','.'),m);
                            temp=m+"  "+local.getVarName()+"="+temp; // This shbud contain the actual Line in the method
                        }
                        else
                        {
                            multinewound=false;
                            if(variableDimAss!=null)
                            {
                                Integer n=(Integer)variableDimAss.get(new Integer(classIndex));
                                java.lang.String bracks="";
                                if(n!=null)
                                {
                                    for(int o=0;o<n.intValue();o++)
                                    {
                                        bracks+="[]";
                                    }
                                }
                                StringBuffer m=new StringBuffer("");
                            checkForImport(local.getDataType().replace('/','.'),m);
                                temp=m+" "+bracks+"  "+local.getVarName()+"="+temp;
                            }
                            else
                            {
                                // TODO: IMPORTANT FIXME
                                temp=local.getDataType().replace('/','.')+" "+"  "+local.getVarName()+"="+temp;
                            }
                        }
                        local.setDeclarationGenerated(true);
                        //codeStatements += Util.formatDecompiledStatement(temp);
                        //codeStatements += ";\n";
                    } else {
                        temp="  "+local.getVarName()+" ="+temp; // This shbud contain the actual Line in the method
                        //codeStatements += Util.formatDecompiledStatement(temp);
                        //codeStatements += ";\n";
                    }
                }
                else
                {

                   Operand top= opStack.getTopOfStack();
                    opStack.getTopOfStack();
                    if(local.wasCreated() && top!=null && top.getClassType().trim().length() > 0)local.setDataType(top.getClassType());
                    java.lang.String temp=top.getOperandValue();
                    temp=local.getVarName()+" ="+"("+temp+")";
                    //codeStatements += Util.formatDecompiledStatement(temp);
                    Operand op6=createOperand(local.getVarName());
                    opStack.push(op6);
                }


            }

        }


        if(doNotPop==true)doNotPop=false;
    }



    private boolean isMethodRetBoolean(Behaviour b)
    {

        java.lang.String type=b.getReturnType();
        if(type.equals("boolean"))return true;
        return false;



    }





    private void handleSimpleASTORECase(int i,int index)
    {
          int instructionPos=i;
        ArrayList methodTries=behaviour.getAllTriesForMethod();
        byte[] info=behaviour.getCode();
         java.lang.String t="Instruction Pos "+currentForIndex+"\t:- "+"astore_"+index;
         LocalVariable local=getLocalVariable(index,"store","java.lang.Object",true,currentForIndex);
        if(cd.isClassCompiledWithMinusG())
        {
            local=getLocalVariable(index,"store","java.lang.Object",false,currentForIndex);
            if(local!=null)
            {
                t+=" THIS localvariable :-  "+local.getVarName();
            }
        }
        t+="\n";
        addParsedOutput(t);
                        boolean add=checkForStartOfCatch(instructionPos,methodTries);
                        if(add==true && !doNotPop)
                        {


                            if(local!=null) {
                                prevLocalGenerated = local;

                                boolean push=isPrevInstDup(info,currentForIndex);
                                if(!push)
                                {
                                     Operand objref=(Operand)opStack.pop();  // Pop The Object Ref

                                if(local.wasCreated() && objref!=null && objref.getClassType().trim().length() > 0)local.setDataType(objref.getClassType());
                                java.lang.String temp=(java.lang.String)objref.getOperandValue();  // temp has new <Class Name>[<size>]
                                    if(!local.isDeclarationGenerated()) {
                                        local.setBlockIndex(blockLevel);
                                        //temp=local.getDataType().replace('/','.')+"  "+local.getVarName()+" ="+temp; // This shbud contain the actual Line in the method
                                        if(!multinewound)
                                        {
                                            StringBuffer m=new StringBuffer("");
                                            checkForImport(local.getDataType().replace('/','.'),m);
                                            temp=m.toString()+"  "+local.getVarName()+"="+temp; // This shbud contain the actual Line in the method
                                        }
                                        else
                                        {
                                            multinewound=false;
                                            Integer n=(Integer)variableDimAss.get(new Integer(0));
                                            java.lang.String bracks="";
                                            if(n!=null)
                                            {
                                                for(int o=0;o<n.intValue();o++)
                                                {
                                                    bracks+="[]";
                                                }
                                            }
                                            StringBuffer m=new StringBuffer("");
                                            checkForImport(local.getDataType().replace('/','.'),m);
                                            temp=m.toString()+" "+bracks+"  "+local.getVarName()+"="+temp;

                                        }
                                        //codeStatements += Util.formatDecompiledStatement(temp);

                                            //codeStatements += ";\n";
                                        local.setDeclarationGenerated(true);
                                    } else {
                                        temp="  "+local.getVarName()+" ="+temp;
                                        //codeStatements += Util.formatDecompiledStatement(temp);

                                        //codeStatements += ";\n";
                                    }
                                }
                                else
                                {
                                     Operand top=opStack.getTopOfStack();
                                        opStack.getTopOfStack();
                                    java.lang.String temp=top.getOperandValue();
                                    temp=local.getVarName()+" ="+temp+";\n";
                                    if(local.wasCreated() && top!=null && top.getClassType().trim().length() > 0)local.setDataType(top.getClassType());
                                    //codeStatements += Util.formatDecompiledStatement(temp);
                                    Operand op6=createOperand(local.getVarName());//"("+local.getVarName()+"=("+op.getOperandValue()+"))");
                                    opStack.push(op6);
                                }

                            }

                        }
                        if(doNotPop==true)doNotPop=false;
    }






    private boolean isThisInstASTOREInst(byte[] info,int pos,StringBuffer sb)
    {


               if(isThisInstrStart(behaviour.getInstructionStartPositions(),pos))
               {
                   if(info[pos] == JvmOpCodes.ASTORE)
                   {
                       sb.append(info[(pos+1)]);
                       return true;
                   }
                   if(info[pos] == JvmOpCodes.ASTORE_0)
                   {
                      sb.append(0);
                      return true;

                   }
                   if(info[pos] == JvmOpCodes.ASTORE_1)
                   {
                      sb.append(1);
                      return true;
                   }
                   if(info[pos] == JvmOpCodes.ASTORE_2)
                   {
                     sb.append(2);
                     return true;

                   }
                   if(info[pos] == JvmOpCodes.ASTORE_3)
                   {
                    sb.append(3);
                    return true;

                   }
               }
               return false;



           }


    private boolean isPrevInstALOADInst(byte[] info,int pos,StringBuffer s)
    {
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),(pos-1)))
        {

            switch(info[(pos-1)])
            {

                case JvmOpCodes.ALOAD_0	:
                    s.append(0);
                    return true;

                case JvmOpCodes.ALOAD_1	:
                    s.append(1);
                    return true;

                case JvmOpCodes.ALOAD_2	:
                    s.append(2);
                    return true;

                case JvmOpCodes.ALOAD_3	:
                    s.append(3);
                    return true;

            }

        }
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),(pos-2)))
        {

            switch(info[(pos-2)])
            {
                case JvmOpCodes.ALOAD	:
                    s.append(info[(pos-2+1)]);
                    return true;

            }
        }
        return false;
    }



    private boolean checkForMatchingLoopAgain(ArrayList loops,int start,StringBuffer S)
    {
        boolean b=false;
        if(loops==null || loops.size()==0)return b;
        else
        {
           for(int s=0;s<loops.size();s++)
           {
             Loop l=(Loop)loops.get(s);
             int loopstart=l.getStartIndex();
             if(loopstart==start)
             {
               b=true;
               S.append(l.getEndIndex());
               return b;
             }

           }
        }
        return b;
    }


    private int getClosestLoopEndForThisIf(int s,ArrayList loops,byte[] info)
    
    {

        int end=-1;
        if(loops==null || loops.size()==0)return end;
        int gotos=s+3;
        if(info[gotos]==JvmOpCodes.GOTO && isThisInstrStart(behaviour.getInstructionStartPositions(),gotos))
        {
             int gotoj=getJumpAddress(info,gotos);
             int starts[]=new int[loops.size()];
             for(int z=0;z<loops.size();z++)
             {
               starts[z] = ((Loop)loops.get(z)).getEndIndex();
             }
             Arrays.sort(starts);
             int reqdloopend=-1;
             for(int x=starts.length-1;x>=0;x--)
             {
                int cur=starts[x];
                if(gotoj > cur)
                {
                  reqdloopend=cur;
                  break;
                }
             }

            if(reqdloopend!=-1)
            {
                  int lstart=getLoopStartForEnd(reqdloopend,loops);
                  if(lstart < s)
                  {
                      return  reqdloopend;
                  }
            }
            else
            {
                return -1;
            }


        }

        return end;
    }
    private int checkElseCloseWRTAnyParentLoop(IFBlock ifs,int gotostart,byte[] info)
    {


        ArrayList allloops=behaviour.getBehaviourLoops();
        if(allloops==null || allloops.size()==0)return -1;
        int gotojump=getJumpAddress(info,gotostart) ;
        Object []sortedLoops=sortLoops(allloops);

            for(int k=0;k<sortedLoops.length;k++)
            {
                Loop cur=(Loop)sortedLoops[k];
                if(cur.getStartIndex()==gotojump)
                {
                   int parentLoopStart=getParentLoopStartForIf(sortedLoops,ifs.getIfStart());
                    if(parentLoopStart==gotojump)
                    {
                        int loopend=getloopEndForStart(allloops,parentLoopStart);
                        return    loopend;

                    }
                }
            }
       return -1;
    }


   private boolean isPreviousInst(byte[] info,int current,int lookfor)
   {

       ArrayList starts=behaviour.getInstructionStartPositions();
       if(list==null || list.size()==0)return false;
       for(int s=current-1;s>=0;s--)
       {
            boolean ok=isThisInstrStart(starts,s);
            if(ok)
            {
                if(s==lookfor)
                {
                    return true;
                }
                return false;
            }
       }

      return false;
   }


    public boolean addCodeStatementWRTShortcutOR(IFBlock ifst,java.lang.String s,boolean print,java.lang.String ifw,boolean last,java.lang.String alt)
    {
        if(!print)return true;
        boolean add=true;
        int current=currentForIndex;
        byte[] code=behaviour.getCode();
        java.lang.String str ="\nif(";
        if(ifw.equals("while"))
        {
            str="\nwhile(";
        }
        java.lang.String string="";
        if(last){
            if(isInstAnyCMPInst(code,current-1)==false)
            {
                string=alt;
            }
            else
                string=s;
        }
        else
        string=s;

        string = "("+string+")";
        if(encounteredOrComp==false)
        {

            int jump=getJumpAddress(code,current);
            int i=jump-3;
            if(jump > current && i > 0 && isThisInstrStart(behaviour.getInstructionStartPositions(),i) && isInstructionIF(code[i]))
            {
                encounteredOrComp=true;
                //codeStatements+=Util.formatDecompiledStatement(str+string+" || ");
                return false;
            }
            else if(jump < current)
            {
                int close=ifst.getIfCloseLineNumber();
                int x=close-3;
                if( x > 0 && isThisInstrStart(behaviour.getInstructionStartPositions(),x) && isInstructionIF(code[x]))
                {
                    encounteredOrComp=true;
                    //codeStatements+=Util.formatDecompiledStatement(str+string+" || ");
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }


        }
        else
        {

            // codes
            //codeStatements+=Util.formatDecompiledStatement(string);
            int jump=getJumpAddress(code,current);
            int i=jump-3;

            if(jump > current && i > 0 && isThisInstrStart(behaviour.getInstructionStartPositions(),i) && isInstructionIF(code[i]))
            {
                encounteredOrComp=true;
                //codes
                //codeStatements+=" || ";
                return false;
            }
            else  if(jump < current)
            {   //BigInteger
                int close=ifst.getIfCloseLineNumber();
                int x=close-3;
                if( x > 0 && isThisInstrStart(behaviour.getInstructionStartPositions(),x) && isInstructionIF(code[x]))
                {
                    encounteredOrComp=true;
                    //codes
                    //codeStatements+=" || ";
                    return false;
                }
                else
                {
                    //codeStatements+=Util.formatDecompiledStatement(")\n{\n");
                    encounteredOrComp=false;
                    return false;
                }
            }
            else
            {
               //codeStatements+=Util.formatDecompiledStatement(")\n{\n");
                encounteredOrComp=false;
                return false;
            }




        }




    }

      private boolean isInstructionIF(int instruction)
    {

        switch(instruction)
        {

            case JvmOpCodes.IF_ACMPEQ:
                return true;
            case JvmOpCodes.IF_ACMPNE:
                return true;
            case JvmOpCodes.IF_ICMPEQ:
                return true;
            case JvmOpCodes.IF_ICMPGE:
                return true;
            case JvmOpCodes.IF_ICMPGT:
                return true;
            case JvmOpCodes.IF_ICMPLE:
                return true;
            case JvmOpCodes.IF_ICMPLT:
                return true;
            case JvmOpCodes.IF_ICMPNE:
                return true;

            case JvmOpCodes.IFEQ:
                return true;
            case JvmOpCodes.IFGE:
                return true;
            case JvmOpCodes.IFGT:
                return true;
            case JvmOpCodes.IFLE:
                return true;
            case JvmOpCodes.IFNE:
                return true;
            case JvmOpCodes.IFLT:
                return true;
            case JvmOpCodes.IFNULL:
                return true;
            case JvmOpCodes.IFNONNULL:
                return true;
            default:
                return false;

        }


    }

    private boolean encounteredOrComp=false;

    private HashMap athrowmap=new HashMap();


    private void checkForATHROWAtIFelseEnd(IFBlock ifst,StringBuffer ifelsecode,int i)
    {
        boolean start=isThisInstrStart(behaviour.getInstructionStartPositions(),i);
        byte[] info=behaviour.getCode();
        if(start && info[i]==JvmOpCodes.ATHROW)
        {
            boolean x=addATHROWOutput(i);
            if(opStack.size() > 0 && x)
            {
                Operand  op = (Operand)opStack.pop();
                opStack.push(op);
                java.lang.String tempString="throw "+op.getOperandValue()+";\n";
                ifelsecode.append(Util.formatDecompiledStatement("\n"+tempString+"\n"));
                athrowmap.put(new Integer(i),"true");
            }

        }

    }


    private boolean addATHROWOutput(int i)
    {
        if(athrowmap.size() == 0)return true;
        Set  entries=athrowmap.entrySet();
        Iterator it=entries.iterator();
        while(it.hasNext())
        {
            Map.Entry entry=(Map.Entry)it.next();
            Integer pos=(Integer)entry.getKey();
            if(pos.intValue()==i)
            {
                java.lang.String str=(java.lang.String)entry.getValue();
                if(str.equals("true"))
                {
                    return false;
                }
            }
        }




      return true;

    }

    private void handleBALOAD(OperandStack stack)
    {
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" baload\n");
        Operand op = (Operand)stack.pop();
        Operand op1 = (Operand)stack.pop();
        Operand op2 = new Operand();
        op2.setOperandValue(op1.getOperandValue()+"["+op.getOperandValue()+"]");
        stack.push(op2);
    }

    private void handleBASTORE(OperandStack stack)
    {
         Operand op = (Operand)stack.pop();
                     Operand    op1 = (Operand)stack.pop();
                      Operand   op2 = (Operand)opStack.pop();
                        //parsedString += "BASTORE\n";
                        //parsedString+="\t";parsedString+="\t";
                        java.lang.String tempString=op2.getOperandValue()+"["+op1.getOperandValue()+"]="+op.getOperandValue()+";\n";
                        //codeStatements+=Util.formatDecompiledStatement(tempString);
    }

    private void handleCALOAD(OperandStack stack){
                      Operand  op = (Operand)stack.pop();
                      Operand  op1 = (Operand)stack.pop();
                        Operand op2 = createOperand(op1.getOperandValue()+"["+op.getOperandValue()+"]",Constants.IS_CONSTANT_CHARACTER,Constants.CATEGORY1);
                        stack.push(op2);
    }

    private void handleCASTORE(OperandStack stack){
                        Operand op = (Operand)stack.pop();
                        Operand  op1 = (Operand)opStack.pop();
                        Operand  op2 = (Operand)opStack.pop();
                        java.lang.String tempString=op2.getOperandValue()+"["+op1.getOperandValue()+"]="+op.getOperandValue()+";\n";;
                        //codeStatements+=Util.formatDecompiledStatement(tempString);
    }


    private void handleCheckCast(OperandStack stack,byte[] info)
    {
        java.lang.String t="Instruction Pos "+currentForIndex+"\t:- "+" checkcast";
        addParsedOutput(t);
        int i=currentForIndex;
        Operand op = (Operand)stack.pop();
        int classIndex=getOffset(info,i);
        ClassInfo cinfo=cd.getClassInfoAtCPoolPosition(classIndex);
        Object o=op.getOperandValue();
        op=new Operand();
        java.lang.String castedRef="("+cd.getUTF8String(cinfo.getUtf8pointer()).replace('/','.')+")"+o.toString();
        op.setOperandValue(castedRef);
        opStack.push(op);
        addParsedOutput(castedRef+"\n");
    }

    private void handleDALOAD(OperandStack stack){
        Operand op = (Operand)stack.pop();
        Operand op1 = (Operand)stack.pop();
        Operand op2 = new Operand();
        op2.setOperandValue(op1.getOperandValue()+"["+op.getOperandValue()+"]");
        //op2.setCategory(Constants.IS_CONSTANT_DOUBLE);
        stack.push(op2);
    }

   private void handleDASTORE(OperandStack stack){
                   Operand     op = (Operand)stack.pop();
                     Operand   op1 = (Operand)stack.pop();
                    Operand    op2 = (Operand)stack.pop();
                      java.lang.String   tempString=op2.getOperandValue()+"["+op1.getOperandValue()+"]="+op.getOperandValue()+";\n";
                        //codeStatements+=Util.formatDecompiledStatement(tempString);
   }


    private void handleDCONST(OperandStack stack,double val)
    {
       Operand op = new Operand();
        java.lang.String str=""+val;
                        op.setOperandValue(new Double(str));
                        op.setOperandType(Constants.IS_CONSTANT_DOUBLE);
                        //(Constants.CATEGORY2);
                        stack.push(op);
    }


    private void handleDDIV(OperandStack stack){
                    Operand    op = (Operand)stack.pop();
                   Operand     op1 = (Operand)stack.pop();
                    Operand    op2 = new Operand();
                        op2.setOperandValue("("+op1.getOperandValue()+"/"+op.getOperandValue()+")");

                        //op2.setCategory(Constants.CATEGORY2);
                        stack.push(op2);
    }

    private void handleDLOADCase(int in,OperandStack stack,boolean  b)
    {


        LocalVariable local=getLocalVariable(in,"load","double",b,currentForIndex);
        if(local!=null) {
            prevLocalGenerated = local;
            Operand op = new Operand();
            op.setOperandValue(local.getVarName());
            stack.push(op);
            if(cd.isClassCompiledWithMinusG() && local!=null)
            {
                addParsedOutput(" THIS LocalVariable "+local.getVarName()+"\n");
            }
        }
    }



    private void handleDMUL(OperandStack stack){
                       Operand op = (Operand)stack.pop();
                       Operand op1 = (Operand)stack.pop();



                       Operand op2 = new Operand();
                        op2.setOperandValue("("+op.getOperandValue()+"*"+op1.getOperandValue()+")");

                        //op2.setCategory(Constants.CATEGORY1);
                        stack.push(op2);
    }

    private void handleDNEG(OperandStack stack){
        Operand   op = (Operand)stack.pop();

        Operand    op1 = new Operand();
        // op1.setCategory(Constants.CATEGORY1);

        op1.setOperandValue("-"+"("+op.getOperandValue()+")");
        stack.push(op1);
    }



    private void handleDREM(OperandStack stack){
                    Operand    op = (Operand)stack.pop();
                    Operand    op1 = (Operand)stack.pop();

                     Operand   op2 = new Operand();
                      //  op2.setCategory(Constants.CATEGORY1);

                        op2.setOperandValue("("+op.getOperandValue()+" % "+op1.getOperandValue()+ ")");
                        stack.push(op2);
}





    private void handleDUP2X2(OperandStack stack){
                       Operand op1 = (Operand)stack.pop();
                       Operand op2 = (Operand)stack.pop();
                      Operand  op3 = (Operand)stack.pop();
                       Operand op4 = (Operand)stack.pop();


                            stack.push(op2);
                            stack.push(op1);
                            stack.push(op4);
                            stack.push(op3);
                            stack.push(op2);
                            stack.push(op1);
    }


    private void handleFCONST(OperandStack stack,java.lang.String val){
                          Operand  op = new Operand();
                           op.setOperandValue(new Float(val));

                           stack.push(op);
       }


    private void handleFDIV(OperandStack stack){
                       Operand op = (Operand)stack.pop();
                      Operand  op1 = (Operand)stack.pop();

                       Operand op2 = new Operand();
                        op2.setOperandValue("("+op.getOperandValue()+" / "+""+op1.getOperandValue()+")");
        stack.push(op2);

    }

    private void handleFLOAD(int opValueI,OperandStack stack,boolean b){
    addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" fload "+opValueI+"\n");
    LocalVariable  local=getLocalVariable(opValueI, "load","float",b,currentForIndex);

    if(local!=null && local!=null) {
        addParsedOutput(" THIS LocalVariable :-  "+local.getVarName());
        Operand op = new Operand();
        op.setOperandValue(local.getVarName());
        stack.push(op);
    }
}




    private boolean isIFShortcutORComp(byte[] info,int j)
    {

        if(isThisInstrStart(behaviour.getInstructionStartPositions(),j))
        {
            int jump=getJumpAddress(info,j);
            int k=jump-3;
            if(isThisInstrStart(behaviour.getInstructionStartPositions(),k) && jump > j)
            {
                boolean b=isInstructionIF(info[k]);
                if(b)
                {
                    return true;
                }
            }

        }

        return false;
    }




    private void handleLCONST(OperandStack stack,java.lang.String str){
      Operand    op = new Operand();
                        op.setOperandValue(new Long(str));
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        //(Constants.CATEGORY2);
                        stack.push(op);
    }


    private void handleLDIV(OperandStack stack){
                    Operand    operand1=stack.getTopOfStack();
                    Operand    operand2=stack.getTopOfStack();
                 java.lang.String       result="("+operand1.getOperandValue()+"/"+operand2.getOperandValue()+")";
                   Operand     op=new Operand();
                        op.setOperandValue(result);
                        op.setOperandType(Constants.IS_CONSTANT_LONG);
                        //(Constants.CATEGORY2);
                        stack.push(op);
    }



   private void handleSIMPLELLOAD(OperandStack stack,int index){
                        LocalVariable local=getLocalVariable(index,"load","long",true,currentForIndex);
                if(local!=null && cd.isClassCompiledWithMinusG())
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lload_"+index+" THIS LocalVariable:-  "+local.getVarName()+"\n");
                        }
                        else
                        {
                            addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+"lload_"+index+"\n");
                        }
                        if(local!=null ) {


                            Operand op=new Operand();
                            op.setOperandType(Constants.IS_CONSTANT_LONG);
                            //(Constants.CATEGORY2);
                            op.setOperandValue(local.getVarName());
                            stack.push(op);


                        }
   }






    private void handleDCMPG(OperandStack stack,byte[] info)
    {
        int i=currentForIndex;
       Operand  op = (Operand)stack.pop();
                  Operand      op1 = (Operand)stack.pop();
                  Operand      op2 = new Operand();
                        //op2.setCategory(Constants.CATEGORY1);
                        //op2.setOperandType(Constants.IS_CONSTANT_INT);
                        int j=i+1;
                        int nextInstruction=info[j];
                        boolean sh=isIFShortcutORComp(info,j);
                            if(!sh)
                        {
                            int ifclose=getIfCloseNumberForThisIF(info,(currentForIndex+1));
                            ifclose=ifclose-3;
                           if(isThisInstrStart(behaviour.getInstructionStartPositions(),ifclose))
                           {
                               if(isInstructionIF(info[ifclose]) && ifclose!= j)
                               {
                                   sh=true;
                               }
                           }


                        }
                        switch(nextInstruction)
                        {
                            case JvmOpCodes.IFEQ:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFNE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                break;
                        }
                        stack.push(op2);
    }



    private void handleDCMPL(OperandStack stack,byte[] info)
    {
        int i=currentForIndex;
         Operand  op = (Operand)stack.pop();
                 Operand       op1 = (Operand)stack.pop();
                   Operand     op2 = new Operand();
                        //op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        int j=i+1;
                        int nextInstruction=info[j];
         boolean sh=isIFShortcutORComp(info,j);
             if(!sh)
                        {
                            int ifclose=getIfCloseNumberForThisIF(info,(currentForIndex+1));
                            ifclose=ifclose-3;
                           if(isThisInstrStart(behaviour.getInstructionStartPositions(),ifclose))
                           {
                               if(isInstructionIF(info[ifclose]) && ifclose!= j)
                               {
                                   sh=true;
                               }
                           }


                        }
                        switch(nextInstruction)
                        {
                            case JvmOpCodes.IFEQ:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFNE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                break;


                            case JvmOpCodes.IFLE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                break;
                        }
                        stack.push(op2); // TODO: check whether it is ok to push the operand into switch loop
    }




    private boolean addRETURNatI(int i, IFBlock ifst)
    {

        /*byte[] info=behaviour.getCode();

        boolean shortcut=isIFShortcutORComp(info,ifst.getIfStart());
        if(shortcut)return true;
        int ifclose=ifst.getIfCloseLineNumber();
        int x=ifclose-3;
        if(isThisInstrStart(behaviour.getInstructionStratPositions(),x) && x!=ifst.getIfStart())
        {
            boolean isif=isInstructionIF(info[x]);
            if(isif)return true;
        }  */


        boolean oktoadd=true;
        Iterator  mapIT=returnsAtI.entrySet().iterator();
        while(mapIT.hasNext())
        {
            Map.Entry entry=(Map.Entry)mapIT.next();
            Object key=entry.getKey();
            Object retStatus=entry.getValue().toString();
            if(key instanceof Integer)
            {
                Integer position=(Integer)key;
                int posValue=position.intValue();
                if(posValue==i)
                {
                    if(retStatus.equals("true"))
                    {

                        oktoadd=false;
                        break;
                    }
                }
            }

        }
        return oktoadd;
    }



    private boolean lastIFinShortCutChain(byte[] info, IFBlock ifst,int i)
    {

      int jump=getJumpAddress(info,i);
      if(jump > i)
      {

          int x=jump-3;
          if(isInstructionIF(info[x]))
          {
            return false;
          }
          return true;
      }
      else
      {
          int x=ifst.getIfCloseLineNumber();

          int y=x-3;
          if(isInstructionIF(info[y]))
          {
            return false;
          }
          return true;



      }



    }




    private boolean isInstAnyCMPInst(byte[] info,int i)
    {
        ArrayList starts=behaviour.getInstructionStartPositions();
        if(isThisInstrStart(starts,i))
        {
           switch(info[i])
            {
                case JvmOpCodes.DCMPG:
                case JvmOpCodes.DCMPL:
                   case JvmOpCodes.FCMPG:
                   case JvmOpCodes.FCMPL:
                   case JvmOpCodes.LCMP:
                   return true;
           }
        }
        return false;
    }


    private void handleFCMPG(OperandStack stack,byte[] info)
    {
        int i=currentForIndex;
        Operand op = (Operand)stack.pop();
                   Operand     op1 = (Operand)stack.pop();
                   Operand     op2 = new Operand();
                        //op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        int j=i+1;
                        int nextInstruction=info[j];
                        boolean sh=isIFShortcutORComp(info,j);
             if(!sh)
                        {
                            int ifclose=getIfCloseNumberForThisIF(info,(currentForIndex+1));
                            ifclose=ifclose-3;
                           if(isThisInstrStart(behaviour.getInstructionStartPositions(),ifclose))
                           {
                               if(isInstructionIF(info[ifclose]) && ifclose!= j)
                               {
                                   sh=true;
                               }
                           }


                        }
                        switch(nextInstruction)
                        {
                            case JvmOpCodes.IFEQ:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFNE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                break;
                        }

                        stack.push(op2);
    }
    private void handleFCMPL(OperandStack stack,byte[] info)
    {
        int i=currentForIndex;
        Operand  op = (Operand)stack.pop();
                  Operand      op1 = (Operand)stack.pop();
                  Operand    op2 = new Operand();
                       // op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        int j=i+1;
                       int   nextInstruction=info[j];
                       boolean   sh=isIFShortcutORComp(info,j);
             if(!sh)
                        {
                            int ifclose=getIfCloseNumberForThisIF(info,(currentForIndex+1));
                            ifclose=ifclose-3;
                           if(isThisInstrStart(behaviour.getInstructionStartPositions(),ifclose))
                           {
                               if(isInstructionIF(info[ifclose]) && ifclose!= j)
                               {
                                   sh=true;
                               }
                           }


                        }
                        switch(nextInstruction)
                        {
                            case JvmOpCodes.IFEQ:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFNE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                break;
                        }

                        stack.push(op2);
    }
    private void handleLCMP(OperandStack stack,byte[] info)
    {
        int i=currentForIndex ;
        int j;
         Operand op = (Operand)stack.pop();
               Operand          op1 = (Operand)stack.pop();
                  Operand      op2 = new Operand();
                        op2.setCategory(Constants.CATEGORY1);
                        op2.setOperandType(Constants.IS_CONSTANT_INT);
                        j=i+1;
                        int nextInstruction=info[j];
                        boolean sh=isIFShortcutORComp(info,j);
                       if(!sh)
                        {
                            int ifclose=getIfCloseNumberForThisIF(info,(currentForIndex+1));
                            ifclose=ifclose-3;
                           if(isThisInstrStart(behaviour.getInstructionStartPositions(),ifclose))
                           {
                               if(isInstructionIF(info[ifclose]) && ifclose!= j)
                               {
                                   sh=true;
                               }
                           }


                        }
                        switch(nextInstruction)
                        {
                            case JvmOpCodes.IFEQ:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFNE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFGT:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                break;
                            case JvmOpCodes.IFLE:
                                if(!sh)
                                op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                                else
                                op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                                break;
                        }

                        stack.push(op2);
    }

    private void handleISTORE(OperandStack stack,byte[] info,int classIndex,boolean b)
    {
        //java.net.URLStreamHandler u;
       LocalVariable  local=getLocalVariable(classIndex,"store","int",b,currentForIndex);
        if(cd.isClassCompiledWithMinusG() && local!=null)
        {
            addParsedOutput(" THIS LocalVariable:-  "+local.getVarName()+"\n");
        }
                        if(local != null && !doNotPop) {
                           Operand   op =(Operand)stack.pop();

                            if(local.getDataType().equalsIgnoreCase("boolean"))
                            {
                                if(op.getOperandValue().toString().equals("0"))
                                {
                                    op.setOperandValue("false");
                                }
                                if(op.getOperandValue().toString().equals("1"))
                                {
                                    op.setOperandValue("true");
                                }
                            }
                            boolean push=isPrevInstDup(info,currentForIndex);
                             if(!push)
                           {
                            if(!local.isDeclarationGenerated()) {
                                local.setBlockIndex(blockLevel);
                                java.lang.String tempString="#REPLACE_INT_"+classIndex+"#"+local.getDataType()+"\t"+local.getVarName()+"="+"#VALUE"+classIndex+"#"+op.getOperandValue()+";\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                                local.setDeclarationGenerated(true);
                                if(	ifLevel	>	0 ) {
                                    local.setWithinBlock(true);
                                    local.setBlockIndex(blockLevel);
                                }
                            } else {
                               java.lang.String  tempString=local.getVarName()+"="+op.getOperandValue()+";\n";
                                //codeStatements +=Util.formatDecompiledStatement(tempString);
                            }
                                  }
                            else
                           {
                              if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP)
                                stack.getTopOfStack();
                              if(isThisInstrStart(behaviour.getInstructionStartPositions(),currentForIndex-1) && info[currentForIndex-1]==JvmOpCodes.DUP2)
                              {
                                stack.getTopOfStack();
                                   stack.getTopOfStack();
                              }
                             //codeStatements +=Util.formatDecompiledStatement(local.getVarName()+"=("+op.getOperandValue()+");\n");
                             op=createOperand(local.getVarName());
                             stack.push(op);
                           }
                        }

                        if(doNotPop==true)doNotPop=false;
    }


    private boolean checkForLoadAfterIINC(byte[] info,OperandStack opStack,int current,LocalVariable local,int index,java.lang.String c)
    {

        boolean b=false;
        int j=current+1+1+1;
        int loadindex=-1;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),j))
        {

            switch(info[j])
            {
             case JvmOpCodes.ILOAD	:
                    loadindex=info[(j+1)];
                    break;

            case JvmOpCodes.ILOAD_0	:
                    loadindex=0;
                    break;

            case JvmOpCodes.ILOAD_1	:
            loadindex=1;
            break;


            case JvmOpCodes.ILOAD_2	:
            loadindex=2;
            break;


            case JvmOpCodes.ILOAD_3	:
            loadindex=3;
            break;
            }

            if(loadindex!=-1)
            {
                if(loadindex==index)
                {

                    if(c.trim().indexOf("-1")!=-1)
                    {
                      b=true;
                      local.setTempVarName("--"+local.getVarName());
                    }
                    else if(c.trim().indexOf("1")!=-1)
                    {
                       b=true;
                       local.setTempVarName("++"+local.getVarName());
                    }
                    else
                    {
                        b=false;
                    }
                }
            }
            else
            {
                b=false;
            }

        }



        return b;
    }


    private boolean isPrevInstIINC(byte[] info,int current,int index)
    {
        int prev=current-3;
        if(isThisInstrStart(behaviour.getInstructionStartPositions(),prev) )
        {

            if(info[prev]==JvmOpCodes.IINC)
            {
                int j=info[prev+1];
                if(index==j)
                {
                    return true;
                }
            }


        }

        return false;

    }

    private boolean addReturnAtIFElseEnd(int i)
    {


        if(returnsaddedAtIfElse.size()==0)return true;
        for(int z=0;z<returnsaddedAtIfElse.size();z++)
        {
            Integer in=new Integer(i);
            Integer val=(Integer)returnsaddedAtIfElse.get(z);
            if(val.intValue()==in.intValue())
            {
                return false;
            }
        }
        return true;
    }

    private boolean isByteCodeALoopStart(ArrayList loops,int bytecodeend)
    {
        if(loops==null || loops.size()==0)return false;
        for(int z=0;z<loops.size();z++)
        {
            Loop loop=(Loop)loops.get(z);
            int loopstart=loop.getStartIndex();
            if(loopstart==bytecodeend) {
                return true;
            }
        }
        return false;
    }




    private  int getIfCloseNumberForThisIF(byte[] info,int k)
    {
        IFBlock ifst = new IFBlock();
        ifst.setIfStart(k);
        ifst.setHasIfBeenGenerated(true);
        int t=ifLevel+1;
        ifHashTable.put(""+(t),ifst);
        //addBranchLabel(classIndex,i,ifst,currentForIndex,info);
        int i=k;
        int classIndex=getJumpAddress(info,i);
        i++;
        i++;
        boolean continuetofind=false;
        if(classIndex < i)
        {
            ifst.setIfCloseLineNumber(findCodeIndexFromInfiniteLoop(ifst,behaviour.getBehaviourLoops(),classIndex));
            if(ifst.getIfCloseLineNumber()==-1)
                continuetofind=true;
        }
        if(classIndex > i || continuetofind)
        {
            if(isThisInstrStart(behaviour.getInstructionStartPositions(),classIndex-3) && info[classIndex-3]==JvmOpCodes.GOTO)  // GOTO_W?
            {

                int resetVal=checkIfElseCloseNumber(classIndex-3,ifst);
                ifst.setIfCloseLineNumber(resetVal);
            }
            else
            {

                int resetVal=checkIfElseCloseNumber(classIndex,ifst);
                ifst.setIfCloseLineNumber(resetVal);
            }

        }


        int if_start=ifst.getIfStart();
        int if_end=ifst.getIfCloseLineNumber();
        if(if_end==-1 || if_end < if_start)
        {
            boolean b=false;
            int bytecodeend=ifst.getIfCloseFromByteCode();
            b=isByteCodeALoopStart(behaviour.getBehaviourLoops(),bytecodeend);
            if(b)
            {
                int loopend=getloopEndForStart(behaviour.getBehaviourLoops(),bytecodeend);
                if(loopend!=-1)
                {
                    ifst.setIfCloseLineNumber(loopend);
                }
            }
        }

        return ifst.getIfCloseLineNumber();

    }


    private ArrayList  addedHandlerEnds=new ArrayList();



    private boolean isHandlerEndPresentAtGuardEnd(int i)
    {
        if(addedHandlerEnds.size()==0)return false;
        for(int z=0;z<addedHandlerEnds.size();z++)
        {
            Integer in=(Integer)addedHandlerEnds.get(z);
            if(in.intValue()==i)
            {
                return true;
            }
        }
        return false;
    }



    private boolean  isThisTryStart(int i)
    {
        ArrayList tries=behaviour.getAllTriesForMethod();
        if(tries!=null){
        for(int z=0;z<tries.size();z++)
        {

            TryBlock TRY=(TryBlock)tries.get(z);
            if(TRY.getEnd()==i)return true;


        }

        }
        return false;


    }


    private ArrayList storesatifend=new ArrayList();

    private boolean storealreadyhandledatifend(int i)
    {
       if(storesatifend.size()==0)return false;
       for(int z=0;z<storesatifend.size();z++)
       {
           Integer in=(Integer)storesatifend.get(z);
           if(in.intValue()==i)
           {
               return true;
           }
       }

       return false;
    }




      private ArrayList elsestartadded=new ArrayList();

      private boolean addElseStart(int i)
      {
          if(elsestartadded.size()==0)
          {
              return true;
          }
          for(int z=0;z<elsestartadded.size();z++)
          {
              Integer in=(Integer)elsestartadded.get(z);
              if(in.intValue()==i)
              {
                  return false;
              }
          }
          return true;
      }




      private boolean isInstStore0(byte[] info,int i)
      {

          boolean flag=false;
          boolean b=isThisInstrStart(behaviour.getInstructionStartPositions(),i);
          if(!b)return false;
          switch(info[i])
          {
             case JvmOpCodes.AASTORE :
                flag = true;
                break;

              case JvmOpCodes.BASTORE :
                flag = true;
                break;
            case JvmOpCodes.CASTORE :
                flag = true;
                break;
            case JvmOpCodes.DASTORE :
                flag = true;
                break;

                 case JvmOpCodes.FASTORE :
                flag = true;
                break;

                case JvmOpCodes.IASTORE :
                flag = true;
                break;

                   case JvmOpCodes.LASTORE :
                flag = true;
                break;

                            case JvmOpCodes.SASTORE :
                flag = true;
                break;

                  default:
                  flag=false;
          }
          return flag;
      }

      private HashMap putfieldObjRefMap=new HashMap();

     private Operand checkAnyStoredPUTFIELDObjRef(int i)
     {
        if(putfieldObjRefMap.size()==0)return null;
        Set s=putfieldObjRefMap.entrySet();
        Iterator it=s.iterator();
        while(it.hasNext())
        {
            Map.Entry entry=(Map.Entry)it.next();
            Integer in=(Integer)entry.getKey();
            if(in.intValue()==i)
            {
                return (Operand)entry.getValue();
            }
        }
         return null;
     }



     private java.lang.String disOutput="";


     public void addParsedOutput(java.lang.String input)
     {

         if(input.startsWith("Instruction Pos"))
         {
             if(disOutput.length()-1 > 0)
             {
             char last=disOutput.charAt(disOutput.length()-1);
             if(last!='\n')input="\n"+input;
             }
         }
         disOutput+=Util.formatDisassembledOutput(input);

     }


    private void handlePUTSTATIC(int pos)
    {
        FieldRef  fref=cd.getFieldRefAtCPoolPosition(pos);
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- "+" putstatic "+fref.getFieldName()+"\n");
        if(doNotPop==false){
            Operand  value=opStack.getTopOfStack();
            java.lang.String freftype=fref.getTypeoffield();

            // For the code statement
            int classpointer=fref.getClassPointer();
            ClassInfo cinfo=cd.getClassInfoAtCPoolPosition(classpointer);
            java.lang.String classname=cd.getUTF8String(cinfo.getUtf8pointer());
            java.lang.String v=value.getOperandValue().toString();
            if(v.indexOf("(")==-1 && v.indexOf(")")!=-1)
            {
                v=v.replaceAll("\\)","");


            }
            v=v.trim();
            StringBuffer sb=new StringBuffer("");
            checkForImport(classname,sb);
            java.lang.String  temp=sb.toString()+"."+fref.getFieldName()+" = "+v+";";
            //codeStatements+="\n"+Util.formatDecompiledStatement(temp)+"\n";
        }
        if(doNotPop)doNotPop=false;
    }




    private void printAnyReturnATEndOfGuard(int i,byte[] info)
    {

        boolean end=isIEndOfGuard(i,behaviour);
                       boolean returnadded=false;
                       int returnposincode=-1;
                       if(end)
                       {
                           StringBuffer sb=new StringBuffer("");
                           java.lang.String returnString=isAnyReturnPresentInSkipRegion(info,i,behaviour,sb);
                           java.lang.String str=sb.toString();
                           try
                           {
                               returnposincode=Integer.parseInt(str);
                           }
                           catch(NumberFormatException ne){}

                           if(returnString!=null && returnString.trim().length()==0)
                           {
                               if(i==(info.length-1))
                               {
                                   returnString=getReturnTypeInst(info,i);
                                   returnposincode=i;

                               }
                           }
                           java.lang.Object val=null;


                           if(returnString!=null && returnString.trim().length() > 0)
                           {
                               int loadIndex=-1;
                               StringBuffer stb=new StringBuffer("");
                               if(returnposincode!=-1)
                                   loadIndex=getLoadIndexForReturn(info,returnposincode,stb);
                               if(loadIndex!=-1)
                               {
                                   LocalVariableStructure struck=behaviour.getLocalVariables();
                                   int rangeinx=Integer.parseInt(stb.toString());
                                   if(rangeinx!=-1)
                                   {
                                       if(cd.isClassCompiledWithMinusG())
                                       {
                                           LocalVariable temp=struck.getVariabelAtIndex(loadIndex,rangeinx);
                                           if(temp!=null)
                                           {
                                               Operand op=new Operand();
                                               op.setOperandValue(temp.getVarName());
                                               op.setOperandName(temp.getVarName());
                                               opStack.push(op);
                                           }

                                       }
                                       else
                                       {
                                           LocalVariable temp=struck.getVariabelAtIndex(loadIndex);
                                           if(temp!=null)
                                           {
                                               Operand op=new Operand();
                                               op.setOperandValue(temp.getVarName());
                                               op.setOperandName(temp.getVarName());
                                               opStack.push(op);
                                           }
                                       }
                                   }
                               }
                              int returnPos=getReturnStringPosInCode(info,i,behaviour);
                               if(returnString.equals("return")==false && opStack.size() > 0)
                               {
                                   //   StackTop=opStack.peekTopOfStack();
                                   Operand StackTop=opStack.getTopOfStack();
                                   if(StackTop!=null)
                                   {
                                       val=StackTop.getOperandValue();
                                       if(val!=null)val=val.toString();
                                   }
                                   if(StackTop!=null)
                                   {



                                     //  if(val!=null)tempString="return "+val;
                                     //  else tempString="return ";
                                       //codeStatements+=Util.formatDecompiledStatement(tempString);


                                       returnadded=true;
                                   }

                               }
                               else if(returnString.equals("return")==true)
                               {
                                  // tempString="return ";
                                   //codeStatements+=Util.formatDecompiledStatement(tempString);


                                   returnadded=true;
                               }
                               returnsAtI.put(new Integer(returnPos),"true");
                           }

                       }

    }



    private void addTBSWITCHtoDISSTMT(int Default,int [] offsetValues,int low,int high)
    {
        java.lang.String desc="";
        desc+="[DEFAULT "+Default+"]\n";
        desc+="[LOW "+low+"]\n";
        desc+="[high "+high+"]\n";
        for(int z=0;z<offsetValues.length;z++)
        {
          desc+="[Offset"+z+" Value->"+ offsetValues[z] +"]\n";
        }
        addParsedOutput("Instruction Pos "+currentForIndex+"\t:- tableswitch\n\n");
        addParsedOutput(desc+"\n");
    }
}












// TODO: replace unnecesary -200s with -100z