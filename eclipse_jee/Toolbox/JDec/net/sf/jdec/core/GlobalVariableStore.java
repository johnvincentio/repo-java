package net.sf.jdec.core;

import net.sf.jdec.blocks.Loop;

import java.util.*;

/*
*  GlobalVariableStore.java Copyright (c) 2006,07 Swaroop Belur
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
public class GlobalVariableStore {

	private static List dowhileloopmarkers = new ArrayList(); // loop starts
    private static boolean athrowseen = false;
    private static int athrowseenpos = -1;
    private static boolean prevInstInvokeVirtual = false;
	private static ArrayList problematicInvokes = new ArrayList();
	private static Hashtable invokeStartEnd =new Hashtable();
	private static ArrayList elsebreaksforifchain=new ArrayList();
	private static HashMap skipWRTbooleanShortcutAssignFound=new HashMap();
	private static boolean booleanAssignFound=false;
	private static Loop thisLoop;
    private static boolean encounteredAndOrComp=false;
	private static boolean initialBracketAdded=false;
	private static boolean newfound=false;
    private static StringBuffer methodsc=new StringBuffer("");
	private static HashMap booleanAssignMap=new HashMap();
	private static Hashtable retAtIfElseEnd=new Hashtable();
	private static int currentMonitorEnterPos=-1; // gets set when every monitorenter is encountered
    private static Map skipWRTClassLoadIf = new HashMap();
    private static boolean elsehasbegun=false;
    private static Map LABELS=new Hashtable();
    private static List break_JumpOffsets=new ArrayList();
    private static Hashtable labelAssociated=new Hashtable();
    private static boolean isIfInScope = false;
    private static Hashtable branchLabels=new Hashtable();
    private static ArrayList elsestartadded=new ArrayList();
    private static int elseCloseLineNo = -1;
    private static ArrayList continue_JumpOffsets=new ArrayList();
    private static ArrayList skipWRTClassLoadIfUpperLimits=new ArrayList();
	private static ArrayList skipPrimitiveArrayStores=new ArrayList();
	private static HashMap athrowmap=new HashMap();
	private static List skipaastores = new ArrayList();
    private static boolean multinewfound;
    private static Hashtable variableDimAss=new Hashtable();
    private static boolean dupnothandled=false;
    private static boolean doNotPop=false;
    private static boolean embeddedNewArrayCopy=false;
    private static Stack newfoundstack=new Stack();
    private static Stack arraytimesstack=new Stack();
    private static int arraytimespush=0;
    private static boolean primitiveastore=false;
    private static Hashtable pushTypes=new Hashtable();
    private static ArrayList ternList=new ArrayList();
    private static boolean embeddedANEWARRAYCopy=false;
    private static boolean embeddedANEWARRAY=false;
    private static boolean embeddedNEWARRAY=false;
    private static boolean specialIASTORE=false;
    private static boolean IfPartOfShortCutChain=false;
    private static int[] skipsWRTPostIncr=new int[0];
    private static HashMap returnsAtI=new HashMap();
    private static ArrayList variablesatfront=new ArrayList();

    public static void reset(){
    	variablesatfront = new ArrayList();
    	returnsAtI = new HashMap();
    	skipsWRTPostIncr=new int[0];
    	IfPartOfShortCutChain = false;
    	specialIASTORE=false;
    	embeddedNEWARRAY=false;
    	embeddedANEWARRAY=false;
    	embeddedANEWARRAYCopy=false;
    	pushTypes=new Hashtable();
    	primitiveastore=false;
    	arraytimespush=0;
    	arraytimesstack=new Stack();
    	newfoundstack=new Stack();
    	embeddedNewArrayCopy=false;
    	doNotPop=false;
    	dupnothandled=false;
    	variableDimAss=new Hashtable();
    	multinewfound = false;
    	skipaastores = new ArrayList();
    	athrowmap=new HashMap();
    	skipPrimitiveArrayStores=new ArrayList();
    	continue_JumpOffsets=new ArrayList();
    	elseCloseLineNo = -1;
    	elsestartadded=new ArrayList();
    	branchLabels=new Hashtable();
    	isIfInScope = false;
    	labelAssociated=new Hashtable();
    	break_JumpOffsets=new ArrayList();
    	LABELS=new Hashtable();
    	elsehasbegun=false; 
    	skipWRTClassLoadIf = new HashMap();
    	currentMonitorEnterPos=-1;
    	retAtIfElseEnd=new Hashtable();
    	booleanAssignMap=new HashMap();
    	methodsc=new StringBuffer("");
    	newfound=false;
    	initialBracketAdded=false;
    	encounteredAndOrComp=false;
    	thisLoop=null;
    	booleanAssignFound=false;
    	skipWRTbooleanShortcutAssignFound=new HashMap();
    	prevInstInvokeVirtual = false;
    	problematicInvokes = new ArrayList();
    	invokeStartEnd =new Hashtable();
    	elsebreaksforifchain=new ArrayList();
        athrowseen = false;
        dowhileloopmarkers = new ArrayList();
        athrowseenpos = -1;
    }
    
    
    




	public static List getDowhileloopmarkers() {
		return dowhileloopmarkers;
	}


	public static void setDowhileloopmarkers(List dowhileloopmarkers) {
		GlobalVariableStore.dowhileloopmarkers = dowhileloopmarkers;
	}


	public static HashMap getReturnsAtI() {
        return returnsAtI;
    }

    public static void setReturnsAtI(HashMap returnsAtI) {
        GlobalVariableStore.returnsAtI = returnsAtI;
    }

    public static List getSkipaastores() {
        return skipaastores;
    }

    public static void setSkipaastores(List skipaastores) {
        GlobalVariableStore.skipaastores = skipaastores;
    }


    public static boolean isDoNotPop() {
        return doNotPop;
    }

    public static void setDoNotPop(boolean doNotPop) {
        GlobalVariableStore.doNotPop = doNotPop;
    }


    public static boolean isEmbeddedNewArrayCopy() {
        return embeddedNewArrayCopy;
    }

    public static void setEmbeddedNewArrayCopy(boolean embeddedNewArrayCopy) {
        GlobalVariableStore.embeddedNewArrayCopy = embeddedNewArrayCopy;
    }


    public static int getArraytimespush() {
        return arraytimespush;
    }

    public static void setArraytimespush(int arraytimespush) {
        GlobalVariableStore.arraytimespush = arraytimespush;
    }

    public static Stack getArraytimesstack() {
        return arraytimesstack;
    }

    public static void setArraytimesstack(Stack arraytimesstack) {
        GlobalVariableStore.arraytimesstack = arraytimesstack;
    }

    public static Stack getNewfoundstack() {
        return newfoundstack;
    }

    public static void setNewfoundstack(Stack newfoundstack) {
        GlobalVariableStore.newfoundstack = newfoundstack;
    }


    public static boolean isPrimitiveastore() {
        return primitiveastore;
    }

    public static void setPrimitiveastore(boolean primitiveastore) {
        GlobalVariableStore.primitiveastore = primitiveastore;
    }


    public static boolean isEmbeddedANEWARRAYCopy() {
        return embeddedANEWARRAYCopy;
    }

    public static void setEmbeddedANEWARRAYCopy(boolean embeddedANEWARRAYCopy) {
        GlobalVariableStore.embeddedANEWARRAYCopy = embeddedANEWARRAYCopy;
    }

    public static boolean isEmbeddedANEWARRAY() {
        return embeddedANEWARRAY;
    }

    public static void setEmbeddedANEWARRAY(boolean embeddedANEWARRAY) {
        GlobalVariableStore.embeddedANEWARRAY = embeddedANEWARRAY;
    }

    public static boolean isEmbeddedNEWARRAY() {
        return embeddedNEWARRAY;
    }

    public static void setEmbeddedNEWARRAY(boolean embeddedNEWARRAY) {
        GlobalVariableStore.embeddedNEWARRAY = embeddedNEWARRAY;
    }


    public static int[] getSkipsWRTPostIncr() {
        return skipsWRTPostIncr;
    }

    public static void setSkipsWRTPostIncr(int[] skipsWRTPostIncr) {
        GlobalVariableStore.skipsWRTPostIncr = skipsWRTPostIncr;
    }

	public static boolean isMultinewfound() {
		return multinewfound;
	}

	public static void setMultinewfound(boolean multinewfound) {
		GlobalVariableStore.multinewfound = multinewfound;
	}

	public static ArrayList getVariablesatfront() {
		return variablesatfront;
	}

	public static void setVariablesatfront(ArrayList variablesatfront) {
		GlobalVariableStore.variablesatfront = variablesatfront;
	}

	public static ArrayList getTernList() {
		return ternList;
	}

	public static void setTernList(ArrayList ternList) {
		GlobalVariableStore.ternList = ternList;
	}

	public static boolean isDupnothandled() {
		return dupnothandled;
	}

	public static void setDupnothandled(boolean dupnothandled) {
		GlobalVariableStore.dupnothandled = dupnothandled;
	}

	public static Hashtable getVariableDimAss() {
		return variableDimAss;
	}

	public static void setVariableDimAss(Hashtable variableDimAss) {
		GlobalVariableStore.variableDimAss = variableDimAss;
	}

	public static HashMap getAthrowmap() {
		return athrowmap;
	}

	public static void setAthrowmap(HashMap athrowmap) {
		GlobalVariableStore.athrowmap = athrowmap;
	}

	public static ArrayList getSkipPrimitiveArrayStores() {
		return skipPrimitiveArrayStores;
	}

	public static void setSkipPrimitiveArrayStores(
			ArrayList skipPrimitiveArrayStores) {
		GlobalVariableStore.skipPrimitiveArrayStores = skipPrimitiveArrayStores;
	}

	public static boolean isSpecialIASTORE() {
		return specialIASTORE;
	}

	public static void setSpecialIASTORE(boolean specialIASTORE) {
		GlobalVariableStore.specialIASTORE = specialIASTORE;
	}

	public static Hashtable getPushTypes() {
		return pushTypes;
	}

	public static void setPushTypes(Hashtable pushTypes) {
		GlobalVariableStore.pushTypes = pushTypes;
	}

	public static Map getSkipWRTClassLoadIf() {
		return skipWRTClassLoadIf;
	}

	public static void setSkipWRTClassLoadIf(Map skipWRTClassLoadIf) {
		GlobalVariableStore.skipWRTClassLoadIf = skipWRTClassLoadIf;
	}

	public static ArrayList getSkipWRTClassLoadIfUpperLimits() {
		return skipWRTClassLoadIfUpperLimits;
	}

	public static void setSkipWRTClassLoadIfUpperLimits(
			ArrayList skipWRTClassLoadIfUpperLimits) {
		GlobalVariableStore.skipWRTClassLoadIfUpperLimits = skipWRTClassLoadIfUpperLimits;
	}

	public static boolean isIfInScope() {
		return isIfInScope;
	}

	public static void setIfInScope(boolean isIfInScope) {
		GlobalVariableStore.isIfInScope = isIfInScope;
	}

	public static int getElseCloseLineNo() {
		return elseCloseLineNo;
	}

	public static void setElseCloseLineNo(int elseCloseLineNo) {
		GlobalVariableStore.elseCloseLineNo = elseCloseLineNo;
	}

	public static ArrayList getContinue_JumpOffsets() {
		return continue_JumpOffsets;
	}

	public static void setContinue_JumpOffsets(ArrayList continue_JumpOffsets) {
		GlobalVariableStore.continue_JumpOffsets = continue_JumpOffsets;
	}

	public static ArrayList getElsestartadded() {
		return elsestartadded;
	}

	public static void setElsestartadded(ArrayList elsestartadded) {
		GlobalVariableStore.elsestartadded = elsestartadded;
	}

	public static Hashtable getBranchLabels() {
		return branchLabels;
	}

	public static void setBranchLabels(Hashtable branchLabels) {
		GlobalVariableStore.branchLabels = branchLabels;
	}

	public static Hashtable getLabelAssociated() {
		return labelAssociated;
	}

	public static void setLabelAssociated(Hashtable labelAssociated) {
		GlobalVariableStore.labelAssociated = labelAssociated;
	}

	public static List getBreak_JumpOffsets() {
		return break_JumpOffsets;
	}

	public static void setBreak_JumpOffsets(List break_JumpOffsets) {
		GlobalVariableStore.break_JumpOffsets = break_JumpOffsets;
	}

	public static Map getLABELS() {
		return LABELS;
	}

	public static void setLABELS(Map labels) {
		LABELS = labels;
	}

	public static boolean isElsehasbegun() {
		return elsehasbegun;
	}

	public static void setElsehasbegun(boolean elsehasbegun) {
		GlobalVariableStore.elsehasbegun = elsehasbegun;
	}

	public static int getCurrentMonitorEnterPos() {
		return currentMonitorEnterPos;
	}

	public static void setCurrentMonitorEnterPos(int currentMonitorEnterPos) {
		GlobalVariableStore.currentMonitorEnterPos = currentMonitorEnterPos;
	}

	public static boolean isNewfound() {
		return newfound;
	}

	public static void setNewfound(boolean newfound) {
		GlobalVariableStore.newfound = newfound;
	}

	public static Hashtable getRetAtIfElseEnd() {
		return retAtIfElseEnd;
	}

	public static void setRetAtIfElseEnd(Hashtable retAtIfElseEnd) {
		GlobalVariableStore.retAtIfElseEnd = retAtIfElseEnd;
	}

	public static HashMap getBooleanAssignMap() {
		return booleanAssignMap;
	}

	public static void setBooleanAssignMap(HashMap booleanAssignMap) {
		GlobalVariableStore.booleanAssignMap = booleanAssignMap;
	}

	public static StringBuffer getMethodsc() {
		return methodsc;
	}

	public static void setMethodsc(StringBuffer methodsc) {
		GlobalVariableStore.methodsc = methodsc;
	}

	public static boolean isBooleanAssignFound() {
		return booleanAssignFound;
	}

	public static void setBooleanAssignFound(boolean booleanAssignFound) {
		GlobalVariableStore.booleanAssignFound = booleanAssignFound;
	}

	public static boolean isIfPartOfShortCutChain() {
		return IfPartOfShortCutChain;
	}

	public static void setIfPartOfShortCutChain(boolean ifPartOfShortCutChain) {
		IfPartOfShortCutChain = ifPartOfShortCutChain;
	}

	public static boolean isInitialBracketAdded() {
		return initialBracketAdded;
	}

	public static void setInitialBracketAdded(boolean initialBracketAdded) {
		GlobalVariableStore.initialBracketAdded = initialBracketAdded;
	}

	public static boolean isEncounteredAndOrComp() {
		return encounteredAndOrComp;
	}

	public static void setEncounteredAndOrComp(boolean encounteredAndOrComp) {
		GlobalVariableStore.encounteredAndOrComp = encounteredAndOrComp;
	}

	public static HashMap getSkipWRTbooleanShortcutAssignFound() {
		return skipWRTbooleanShortcutAssignFound;
	}

	public static void setSkipWRTbooleanShortcutAssignFound(
			HashMap skipWRTbooleanShortcutAssignFound) {
		GlobalVariableStore.skipWRTbooleanShortcutAssignFound = skipWRTbooleanShortcutAssignFound;
	}

	public static Loop getThisLoop() {
		return thisLoop;
	}

	public static void setThisLoop(Loop thisLoop) {
		GlobalVariableStore.thisLoop = thisLoop;
	}

	public static ArrayList getElsebreaksforifchain() {
		return elsebreaksforifchain;
	}

	public static void setElsebreaksforifchain(ArrayList elsebreaksforifchain) {
		GlobalVariableStore.elsebreaksforifchain = elsebreaksforifchain;
	}

	public static Hashtable getInvokeStartEnd() {
		return invokeStartEnd;
	}

	public static void setInvokeStartEnd(Hashtable invokeStartEnd) {
		GlobalVariableStore.invokeStartEnd = invokeStartEnd;
	}

	public static ArrayList getProblematicInvokes() {
		return problematicInvokes;
	}

	public static void setProblematicInvokes(ArrayList problematicInvokes) {
		GlobalVariableStore.problematicInvokes = problematicInvokes;
	}

	public static boolean isPrevInstInvokeVirtual() {
		return prevInstInvokeVirtual;
	}

	public static void setPrevInstInvokeVirtual(boolean prevInstInvokeVirtual) {
		GlobalVariableStore.prevInstInvokeVirtual = prevInstInvokeVirtual;
	}


    public static boolean isAthrowseen() {
        return athrowseen;
    }

    public static void setAthrowseen(boolean athrowseen) {
        GlobalVariableStore.athrowseen = athrowseen;
    }


    public static int getAthrowseenpos() {
        return athrowseenpos;
    }

    public static void setAthrowseenpos(int athrowseenpos) {
        GlobalVariableStore.athrowseenpos = athrowseenpos;
    }
}
