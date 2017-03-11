package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

import java.util.List;
import java.util.Stack;

/*
*  AAstoreCommand.java Copyright (c) 2006,07 Swaroop Belur
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
public class AAstoreCommand extends AbstractInstructionCommand {


    

    public AAstoreCommand(Behaviour context) {
        super(context);
    }

    public int getSkipBytes() {
        return 0;
    }


    public void execute() {
    	Behaviour behavior = getContext();
        int currentForIndex = getCurrentInstPosInCode();
        if (skipAASTORE(currentForIndex)) return;
        boolean doNotPop = GlobalVariableStore.isDoNotPop();
        OperandStack opStack = getContext().getOpStack();
        boolean embeddedNewArrayCopy = GlobalVariableStore.isEmbeddedNewArrayCopy();
        Stack arraytimesstack = GlobalVariableStore.getArraytimesstack();
        int arraytimespush = GlobalVariableStore.getArraytimespush();
        boolean primitiveastore = GlobalVariableStore.isPrimitiveastore();
        byte cd[] = getContext().getCode();
        boolean add = (cd[currentForIndex - 1] == JvmOpCodes.AASTORE);
        if (!doNotPop) {
            if (opStack.size() >= 3) {                 // See test program A.java
                Operand value = createOperand("");
                Operand arindex = createOperand("");
                Operand arRef = createOperand("");
                if (!embeddedNewArrayCopy) {
                    value = (Operand) opStack.pop();
                    arindex = (Operand) opStack.pop();
                    arRef = (Operand) opStack.pop();
                } else {
                    arRef = (Operand) opStack.pop();

                }
                /*     if(specialIASTORE==true && opStack.size() > 2){
                    opStack.pop();
                    opStack.pop();
                    specialIASTORE=false;
                    //Add open bracket ?

                }*/
                java.lang.String tempString = "";

                tempString = arRef.getOperandValue() + "[" + arindex.getOperandValue() + "]=" + value.getOperandValue() + ";\n";
                if (arraytimesstack.size() == 0)
                    behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));
                else {
                    arraytimespush = Integer.parseInt(arraytimesstack.peek().toString());
                    if (arraytimespush > 0) {

                        if (newfound()) {
                            // Operand z=createOperand(op.getOperandValue());
                            Operand y = value;
                            java.lang.String newv = "";

                            newv = arRef.getOperandValue() + y.getOperandValue();
                            arraytimespush = Integer.parseInt(arraytimesstack.pop().toString());
                            arraytimespush--;
                            if (arraytimespush == 0) {
                                newv += "}";
                            } else {
                                arraytimesstack.push("" + arraytimespush);

                                newv += ",";
                            }
                            /*if(arindex.getOperandValue().trim().equals("0")==false)
                           {
                               opStack.pop();
                           } */
                            opStack.push(createOperand(newv));
                        } else {
                            arraytimespush = Integer.parseInt(arraytimesstack.pop().toString());
                            arraytimespush--;
                            if (primitiveastore) {
                                if (arraytimespush == 0 && arraytimesstack.size() == 0) {
                                    Util.forceNewLine = false;
                                    behavior.appendToBuffer( Util.formatDecompiledStatement("};\n"));


                                } else if (arraytimespush == 0 && arraytimesstack.size() != 0) {
                                    Util.forceNewLine = false;
                                    behavior.appendToBuffer( Util.formatDecompiledStatement("},\n"));


                                } else {
                                    arraytimesstack.push("" + arraytimespush);
                                    //  codeStatements+=value.getOperandValue()+",";
                                }
                                primitiveastore = false;
                            } else {
                                if (arraytimespush == 0 && arraytimesstack.size() == 0 && !add) {
                                    Util.forceStartSpace = true;
                                    Util.newlinebeg = true;
                                    behavior.appendToBuffer( Util.formatDecompiledStatement(value.getOperandValue()));
                                    Util.forceNewLine = false;
                                    //Util.forceStartSpace=false;
                                    Util.newlinebeg = false;
                                    StringBuffer sb = new StringBuffer("");
                                    if (arrayClosingBracketCount(currentForIndex, sb)) {
                                        int total = Integer.parseInt(sb.toString());
                                        if (total == 0) {
                                            Util.forceBeginStartSpace = false;
                                            behavior.appendToBuffer(  "\n" + Util.formatDecompiledStatement("\n}"));
                                            behavior.appendToBuffer( ";\n");
                                            Util.forceBeginStartSpace = true;

                                        } else {
                                          Util.forceBeginStartSpace = false;
                                            behavior.appendToBuffer( "\n" + Util.formatDecompiledStatement("\n}"));
                                            behavior.appendToBuffer( ";\n");
                                            Util.forceBeginStartSpace = true;
                                        }

                                    } else {
                                      Util.forceBeginStartSpace = false;
                                        behavior.appendToBuffer( "\n" + Util.formatDecompiledStatement("\n}"));
                                        behavior.appendToBuffer( ";\n");
                                          Util.forceBeginStartSpace = true;
                                    }
                                    Util.forceNewLine = true;
                                    Util.forceStartSpace = true;
                                    Util.newlinebeg = true;


                                } else if (arraytimespush == 0 && arraytimesstack.size() == 0 && add) {
                                    Util.forceNewLine = false;
                                    Util.forceStartSpace = false;
                                    Util.newlinebeg = false;
                                    StringBuffer sb = new StringBuffer("");
                                    if (arrayClosingBracketCount(currentForIndex, sb)) {
                                        int total = Integer.parseInt(sb.toString());
                                        if (total == 0) {
                                              behavior.appendToBuffer( Util.formatDecompiledStatement("\n}\n"));
                                            behavior.appendToBuffer( ";\n");

                                        } else {
                                            behavior.appendToBuffer( Util.formatDecompiledStatement("\n}\n"));
                                            behavior.appendToBuffer( ";\n");
                                        }

                                    } else {
                                        behavior.appendToBuffer( Util.formatDecompiledStatement("\n}\n"));
                                        behavior.appendToBuffer( ";\n");
                                    }
                                    Util.forceNewLine = true;
                                    Util.forceStartSpace = true;
                                    Util.newlinebeg = true;

                                } else if (arraytimespush == 0 && arraytimesstack.size() != 0 && !add) {
                                    behavior.appendToBuffer( Util.formatDecompiledStatement(value.getOperandValue()));
                                    Util.forceNewLine = false;
                                    behavior.appendToBuffer( Util.formatDecompiledStatement("},\n"));


                                } else {
                                    if (!add) {
                                        //arraytimesstack.push(""+arraytimespush);
                                        Util.forceNewLine = false;
                                        Util.forceStartSpace = false;
                                        behavior.appendToBuffer(  Util.formatDecompiledStatement(value.getOperandValue() + ","));
                                    }
                                    if (arraytimespush > 0)
                                        arraytimesstack.push("" + arraytimespush);

                                }
                            }
                        }
                    }
                }
            }
        }
        if (doNotPop) {
            if (arraytimesstack.size() > 0) {

                if (opStack.size() > 0) {
                    java.lang.String tpval = opStack.peekTopOfStack().getOperandValue();
                    try {
                        if (tpval != null) {
                            Integer.parseInt(tpval.trim());
                            opStack.pop();
                        }
                    } catch (NumberFormatException ne) {

                    }
                }


                arraytimespush = Integer.parseInt(arraytimesstack.pop().toString());
                arraytimespush--;
                if (arraytimespush == 0) {

                } else {
                    arraytimesstack.push("" + arraytimespush);
                }


            }


        }
        if (doNotPop == true) doNotPop = false;
        if (embeddedNewArrayCopy) embeddedNewArrayCopy = false;

        GlobalVariableStore.setPrimitiveastore(primitiveastore);
        GlobalVariableStore.setArraytimespush(arraytimespush);
        GlobalVariableStore.setArraytimesstack(arraytimesstack);
        GlobalVariableStore.setDoNotPop(doNotPop);
        GlobalVariableStore.setEmbeddedNewArrayCopy(embeddedNewArrayCopy);

      
    }

    private boolean skipAASTORE(int pos) {
        List skipaastores = GlobalVariableStore.getSkipaastores();
        for (int z = 0; z < skipaastores.size(); z++) {

            if (((Integer) skipaastores.get(z)).intValue() == pos) {
                return true;
            }
        }
        return false;
    }


}
