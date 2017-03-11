package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.constantpool.ClassInfo;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.util.Util;
import net.sf.jdec.util.Constants;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.core.*;
import net.sf.jdec.main.ConsoleLauncher;

import java.util.Stack;
/*
 *  AnewarrayCommand.java Copyright (c) 2006,07 Swaroop Belur
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

/**
 * @author swaroop belur
 * @since 1.2.1
 */
public class AnewarrayCommand extends AbstractInstructionCommand{

    public AnewarrayCommand(Behaviour context) {
        super(context);
    }


    public int getSkipBytes() {
        return 2;
    }

    public void execute() {

    	Behaviour behavior = getContext();
        int arraytimespush = GlobalVariableStore.getArraytimespush();
        Stack arraytimesstack = GlobalVariableStore.getArraytimesstack();
        boolean embeddedANEWARRAY = GlobalVariableStore.isEmbeddedANEWARRAY();
        boolean embeddedANEWARRAYCopy = GlobalVariableStore.isEmbeddedANEWARRAYCopy();
        byte[] info=getContext().getCode();
        OperandStack opStack = getContext().getOpStack();
        int currentForIndex = getCurrentInstPosInCode();
        ClassDescription cd = getContext().getClassRef().getCd();
        int classIndex=getGenericFinder().getOffset(currentForIndex);//(temp1 << 8) | temp2);
        ClassInfo cinfo=cd.getClassInfoAtCPoolPosition(classIndex);
        java.lang.String Type=cd.getUTF8String(cinfo.getUtf8pointer());
        int bracketCount=Type.lastIndexOf("[");
        bracketCount=bracketCount+2; // the one which is required.
        java.lang.String tempString="";
        Type= DecompilerHelper.parse(Type);
        int brk=Type.indexOf("[");
        if(brk >= 0)
            Type=Type.substring(0,brk)  ;
        Type=Type.replace('/','.');
        if (Configuration.getShowImport().equals("true")) {
			java.lang.String fullName = Type;
			java.lang.String simpleName = "";
			int lastdot = fullName.lastIndexOf(".");
			if (lastdot != -1) {
				simpleName = fullName.substring(lastdot + 1);
				Type = simpleName;
				ConsoleLauncher.addImportClass(fullName);
			}
		}


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
        boolean dup=DecompilerHelper.isInstDup(info,(currentForIndex+3));
        for(int counter=0;counter<bracketCount;counter++) {
            if(counter==0 && !dup)
                brackets+="["+SizeofArray+"]";
            else
                brackets+="[]";
        }

        if(!dup) {
            tempString+=brackets+";";
            behavior.appendToBuffer(Util.formatDecompiledStatement(tempString)+"\n");
            tempString="";
            Operand op=new Operand();
            op.setOperandValue(Ref);
            op.setOperandType(Constants.IS_ARRAY_REF);
            op.setClassType(Type);
            opStack.push(op);
        } else {
            if(newfound()) {
                Operand op=new Operand();
                op.setOperandType(Constants.IS_ARRAY_REF);
                java.lang.String Reference=Ref;
                op.setClassType(Type+brackets);
                Util.forceStartSpace=false;
                op.setOperandValue("new "+Type+brackets+"\n{\n");
                boolean r=false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
                if(r){
                    if(opStack.size() > 0){
                        java.lang.String str=opStack.getTopOfStack().getOperandValue();
                        str=str+op.getOperandValue();
                        op.setOperandValue(str);
                    }
                }

                opStack.push(op);

                try {
                    arraytimespush=Integer.parseInt(SizeofArray);
                } catch(NumberFormatException ne){
                    //Check whether prev was bipush or sipush
                    int currentPos=currentForIndex;
                    int prevs=getGenericFinder().getPrevStartOfInst(currentPos);
                    if(info[prevs]== JvmOpCodes.BIPUSH){
                        int bipushvalue=info[(prevs+1)];
                        arraytimespush=bipushvalue;
                    } else {
                        if(info[prevs]==JvmOpCodes.SIPUSH){
                            int sipushvalue = getGenericFinder().getOffset(prevs);
                            arraytimespush=sipushvalue;
                        } else {
                            arraytimespush=0;
                        }

                    }

                }
                arraytimesstack.push(""+arraytimespush);
            } else {
                Operand op=new Operand();
                op.setOperandType(Constants.IS_ARRAY_REF);
                java.lang.String Reference=Ref;
                op.setClassType(Type+brackets);
                tempString+=brackets;
                if(arraytimesstack.size()==0){
                    Util.forceNewLine=false; Util.forceStartSpace=true;
                    Util.forceTrimLines = false;
                    behavior.appendToBuffer("\n"+Util.formatDecompiledStatement(tempString));
                    boolean specialtypearray=DecompilerHelper.getArrayDimensionForAnewArrayCase(currentForIndex);
                    if(specialtypearray && (bracketCount==0 || bracketCount==1)){
                        behavior.appendToBuffer(Util.formatDecompiledStatement("\n{\n"));
                    }
                    else
                        behavior.appendToBuffer(Util.formatDecompiledStatement("\n{\n"));
                }

                else {
                    Util.forceNewLine=false; Util.forceStartSpace=true;
                    Util.forceTrimLines = false;
                    java.lang.String c="new "+Type+brackets;
                    behavior.appendToBuffer("\n"+Util.formatDecompiledStatement(c));
                    boolean specialtypearray=DecompilerHelper.getArrayDimensionForAnewArrayCase(currentForIndex);
                    if(specialtypearray && (bracketCount==0 || bracketCount==1)){
                        behavior.appendToBuffer(Util.formatDecompiledStatement("\n{\n"));
                    }
                    else
                        behavior.appendToBuffer(Util.formatDecompiledStatement("\n{\n"));

                }

                boolean embed=DecompilerHelper.isAnewArrayEmbedded(currentForIndex,info);
                if(!embed){
                    op.setOperandValue(Reference);
                    opStack.push(op);
                }
                else
                {
                    if(opStack.size() > 0){
                        opStack.getTopOfStack();
                    }
                    embeddedANEWARRAY=true;
                    embeddedANEWARRAYCopy=true;
                }

                try {
                    arraytimespush=Integer.parseInt(SizeofArray);

                } catch(NumberFormatException ne){
                    //Check whether prev was bipush or sipush
                    int currentPos=currentForIndex;
                    int prevs=getGenericFinder().getPrevStartOfInst(currentPos);
                    if(info[prevs]==JvmOpCodes.BIPUSH){
                        int bipushvalue=info[(prevs+1)];
                        arraytimespush=bipushvalue;
                    } else {
                        if(info[prevs]==JvmOpCodes.SIPUSH){
                            int sipushvalue = getGenericFinder().getOffset(prevs);
                            arraytimespush=sipushvalue;
                        } else {
                            arraytimespush=0;
                        }

                    }

                }
                arraytimesstack.push(""+arraytimespush);
            }
        }

        GlobalVariableStore.setArraytimespush(arraytimespush);
        GlobalVariableStore.setArraytimesstack(arraytimesstack);
        GlobalVariableStore.setEmbeddedANEWARRAY(embeddedANEWARRAY);
        GlobalVariableStore.setEmbeddedNewArrayCopy(embeddedANEWARRAYCopy);
        Util.forceTrimLines = true;
        Util.forceNewLine=true;

    }
}
