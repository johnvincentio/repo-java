package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.AllExceptionHandler;
import net.sf.jdec.util.ExecutionState;
import net.sf.jdec.core.*;
import net.sf.jdec.lookup.GenericFinder;
import net.sf.jdec.lookup.IFinder;
import net.sf.jdec.lookup.FinderFactory;
import net.sf.jdec.lookup.LoadInstrFinder;

import java.util.Stack;
import java.util.ArrayList;


public abstract class AbstractInstructionCommand implements IInstructionCommand {


    private GenericFinder genericFinder;

    private LoadInstrFinder loadFinder;

    private Behaviour context;

    private boolean exceptionOccured = false;

    private Throwable[] exceptions;

    public AbstractInstructionCommand(Behaviour context) {
        this.context = context;
    }

    public abstract int getSkipBytes();

    public boolean pollForExceeption() {
        return exceptionOccured;
    }

    protected void registerException(Throwable[] allExceptions) {
        exceptions = allExceptions;
    }

    public void handleExceptions() {

        if (exceptions == null) return;
        AllExceptionHandler handler = null;
        for (int i = 0; i < exceptions.length; i++) {
            handler = new AllExceptionHandler(exceptions[i]);
            handler.reportException();
        }
        System.gc();
    }

    protected void setExceptionOccured(boolean exceptionOccured) {
    	this.exceptionOccured=exceptionOccured;
    }

    public Behaviour getContext() {
        return context;
    }

    protected Operand getTopOfStack() {
        return getContext().getOpStack().getTopOfStack();
    }


    protected OperandStack getStack() {
        return getContext().getOpStack();
    }


    protected int getCurrentInstPosInCode() {
        return ExecutionState.getCurrentInstructionPosition();
    }


    protected byte[] getCode() {
        return getContext().getCode();
    }


    protected Operand createOperand(Object val, int type, int categ) {
        Operand opr = new Operand();
        opr.setOperandValue(val);
        opr.setOperandType(type);
        opr.setCategory(categ);
        return opr;

    }

    protected Operand createOperand(Object val) {
        Operand opr = new Operand();
        opr.setOperandValue(val);
        return opr;

    }

    protected IFinder getGenericFinder() {
        return FinderFactory.getFinder(IFinder.BASE);
    }


    public IFinder getLoadFinder() {
        return FinderFactory.getFinder(IFinder.LOAD);
    }
    
    public IFinder getStoreFinder() {
        return FinderFactory.getFinder(IFinder.STORE);
    }

    
    public IFinder getBranchFinder() {
        return FinderFactory.getFinder(IFinder.BRANCH);
    }


    protected boolean newfound() {
        Stack newfoundstack = GlobalVariableStore.getNewfoundstack();
        if (newfoundstack.size() > 0) return true;
        return false;
    }


    protected boolean arrayClosingBracketCount(int current, StringBuffer sb) {

        Behaviour behaviour = getContext();
        ArrayList starts = behaviour.getInstructionStartPositions();
        byte[] info = behaviour.getCode();
        int next = current + 1;
        java.lang.String type = "simple";
        int index = -1;
        int pos = next;
        forloop:
        for (; pos < info.length; pos++) {
            if (getGenericFinder().isThisInstrStart(pos)) {

                switch (info[pos]) {


                    case JvmOpCodes.ASTORE:
                        type = "complex";
                        index = info[pos + 1];
                        break forloop;
                    case JvmOpCodes.ASTORE_0:
                        index = 0;
                        break forloop;
                    case JvmOpCodes.ASTORE_1:
                        index = 1;
                        break forloop;
                    case JvmOpCodes.ASTORE_2:
                        index = 2;
                        break forloop;
                    case JvmOpCodes.ASTORE_3:
                        index = 3;
                        break forloop;


                }

            }
        }

        if (index != -1) {
            LocalVariable local = null;
            if (type.equals("simple")) {
                local = DecompilerHelper.getLocalVariable(index, "store", "java.lang.Object", true, pos);
            } else {

                local = DecompilerHelper.getLocalVariable(index, "store", "java.lang.Object", false, pos);
            }
            if (local != null) {
                java.lang.String datatype = local.getDataType();
                if (datatype.indexOf("[") != -1 && datatype.indexOf("]") != -1) {
                    int br = datatype.indexOf("[");
                    int count = 0;
                    do {
                        count++;
                        br = datatype.indexOf("[", (br + 1));
                    } while (br != -1);
                    sb.append((count - 1));
                    return true;
                }

            }
        }

        return false;
    }

    protected int arrayClosingBracketCount(int current) {

        int total = 0;
        Behaviour behaviour = getContext();
        ArrayList starts = behaviour.getInstructionStartPositions();
        byte[] info = behaviour.getCode();
        int next = current + 1;
        java.lang.String type = "simple";
        int index = -1;
        int pos = next;
        forloop:
        for (; pos < info.length; pos++) {
            if (getGenericFinder().isThisInstrStart(pos)) {

                switch (info[pos]) {


                    case JvmOpCodes.ASTORE:
                        type = "complex";
                        index = info[pos + 1];
                        break forloop;
                    case JvmOpCodes.ASTORE_0:
                        index = 0;
                        break forloop;
                    case JvmOpCodes.ASTORE_1:
                        index = 1;
                        break forloop;
                    case JvmOpCodes.ASTORE_2:
                        index = 2;
                        break forloop;
                    case JvmOpCodes.ASTORE_3:
                        index = 3;
                        break forloop;


                }

            }
        }

        if (index != -1) {
            LocalVariable local = null;
            if (type.equals("simple")) {
                local = DecompilerHelper.getLocalVariable(index, "store", "java.lang.Object", true, pos);
            } else {

                local = DecompilerHelper.getLocalVariable(index, "store", "java.lang.Object", false, pos);
            }
            if (local != null) {
                java.lang.String datatype = local.getDataType();
                if (datatype.indexOf("[") != -1 && datatype.indexOf("]") != -1) {
                    int br = datatype.indexOf("[");
                    int count = 0;
                    do {
                        count++;
                        br = datatype.indexOf("[", (br + 1));
                    } while (br != -1);
                    return count - 1;
                }

            }
        }

        return total;
    }
}

