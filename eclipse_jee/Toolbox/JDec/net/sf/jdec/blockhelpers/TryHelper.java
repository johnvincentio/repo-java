package net.sf.jdec.blockhelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jdec.blocks.CatchBlock;
import net.sf.jdec.blocks.FinallyBlock;
import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.TryBlock;
import net.sf.jdec.constantpool.MethodInfo.ExceptionTable;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.ExecutionState;

/*******************************************************************************
 * Helper functions for exception blocks. TODO: move remn from Decompiler.java
 * to here
 * 
 * @author sbelur
 * 
 */
public class TryHelper {

	private static TryHelper helper = new TryHelper();

	private TryHelper() {
	}

	public static final TryHelper getInstance() {
		return helper;
	}

	public static ArrayList removeExtraTriesDueToFinallyBlock(ArrayList list) {
		Map buckets = new HashMap();
		if(list == null)return new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			ExceptionTable et = (ExceptionTable) list.get(i);
			if (et.getExceptionName() != null
					&& et.getExceptionName().equals("<any>")) {
				List temp = (List) buckets.get(""
						+ et.getStartOfHandlerForGuardRegion());
				if (temp == null) {
					temp = new ArrayList();
				}
				buckets.put("" + et.getStartOfHandlerForGuardRegion(), temp);
				temp.add(et);
			}
		}

		Iterator it = buckets.values().iterator();

		ArrayList finalList = new ArrayList();
		if (it.hasNext())
			while (it.hasNext()) {
				int smallest = Integer.MAX_VALUE;
				ExceptionTable reqd = null;
				List l = (List) it.next();
				for (int z = 0; z < l.size(); z++) {
					ExceptionTable t = (ExceptionTable) l.get(z);
					if (t.getStartOfGuardRegion() < smallest) {
						smallest = t.getStartOfGuardRegion();
						reqd = t;
					}
				}
				if (reqd != null) {
					for (int z = 0; z < l.size(); z++) {
						ExceptionTable t = (ExceptionTable) l.get(z);
						if(t != reqd){
							t.setIgnore(true);
						}
					}
					
					finalList.add(reqd);
				}
				else{
					finalList.addAll(l); // just add back
				}
			}
		else
			finalList = list; // just reset back

		return finalList;
	}

	public static boolean synchBlockEndedForStartAt(int startpc, List list,
			ExceptionTable table) {
		for (int i = 0; i < list.size(); i++) {
			ExceptionTable st = (ExceptionTable) list.get(i);
			if (st.getExceptionName() != null
					&& st.getExceptionName().equals("<any>")) {
				if (st.getMonitorEnterPosInCode() != -1) {
					if (st.getStartPC() == startpc) {
						if (st.isWasSynchClosed() && st != table) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean checkForStartOfCatch(int instructionPos,
			ArrayList methodTries) {
		boolean add = true;
		boolean returnFromMethod = false;
		// Figure is this is a start of catch here
		if (methodTries != null) {
			for (int st = 0; st < methodTries.size(); st++) {
				TryBlock tryblk = (TryBlock) methodTries.get(st);
				FinallyBlock finblk = tryblk.getFinallyBlock();
				if (tryblk != null) {
					ArrayList allCatches = tryblk.getAllCatchesForThisTry();
					if (allCatches != null && allCatches.size() > 0) {

						for (int s1 = 0; s1 < allCatches.size(); s1++) {

							CatchBlock catchBlk = (CatchBlock) allCatches
									.get(s1);
							if (catchBlk != null) {
								int catchStart = catchBlk.getStart();
								if (catchStart == instructionPos) {
									add = false;
									returnFromMethod = true;
									break;
								}
							}
						}
						if (returnFromMethod)
							return add;
					}
					if (finblk != null) {
						int finstart = finblk.getStart();
						if (finstart == instructionPos) {
							return false;
						}
					}
				}

			}

		}

		return add;
	}

	public static int checkForElseEndWRTExcepionTables(IFBlock ifst, int close) {
		ArrayList tables = getContext().getAllTriesForMethod();
		if (tables == null || tables.size() == 0)
			return close;
		int ifs = ifst.getIfStart();
		int ifc = ifst.getIfCloseLineNumber();
		int ecl = ifst.getElseCloseLineNumber();
		for (int z = 0; z < tables.size(); z++) {

			TryBlock tryb = (TryBlock) tables.get(z);
			ExceptionTable table = tryb.getTableUsedToCreateTry();
			int trys = tryb.getStart();
			int trye = tryb.getEnd();

			// Check1 whether else lies inside try
			if (ifs > trys && ifs < trye) {

				if (ecl > trye) {
					ifst.setElseCloseLineNumber(trye);
					return trye;
				}
			}

			// Check2 whether if lies within any catch of try
			ArrayList allcatches = tryb.getAllCatchesForThisTry();
			for (int x = 0; x < allcatches.size(); x++) {
				CatchBlock cblk = (CatchBlock) allcatches.get(x);
				int cblks = cblk.getStart();
				int cblke = cblk.getEnd();
				if (ifs > cblks && ifs < cblke) {
					if (ecl > cblke) {
						ifst.setElseCloseLineNumber(cblke);
						return cblke;
					}

				}

			}

			// Check2 whether if lies within any catch of finally
			FinallyBlock fin = tryb.getFinallyBlock();
			if (fin != null) {
				int fins = fin.getStart();
				int fine = fin.getEnd();
				if (ifs > fins && ifs < fine) {
					if (ecl > fine) {
						ifst.setElseCloseLineNumber(fine);
						return fine;
					}
				}
			}

		}
		return close;

	}

	private static Behaviour getContext() {
		return ExecutionState.getMethodContext();
	}

	public static void checkIFEndWRTExceptionTables(IFBlock ifst) {

		ArrayList tables = getContext().getAllTriesForMethod();
		if (tables == null || tables.size() == 0)
			return;
		int ifs = ifst.getIfStart();
		int ifc = ifst.getIfCloseLineNumber();
		for (int z = 0; z < tables.size(); z++) {

			TryBlock tryb = (TryBlock) tables.get(z);
			ExceptionTable table = tryb.getTableUsedToCreateTry();
			int trys = tryb.getStart();
			int trye = tryb.getEnd();

			// Check1 whether if lies inside try
			if (ifs > trys && ifs < trye) {
				int y = trye;
				// int endg=table.getEndOfGuardRegion();
				// if(y < endg)y=endg;
				if (ifc > y) {
					ifst.setIfCloseLineNumber(y);
					return;
				}
			}

			// Check2 whether if lies within any catch of try
			ArrayList allcatches = tryb.getAllCatchesForThisTry();
			for (int x = 0; x < allcatches.size(); x++) {
				CatchBlock cblk = (CatchBlock) allcatches.get(x);
				int cblks = cblk.getStart();
				int cblke = cblk.getEnd();
				if (ifs > cblks && ifs < cblke) {
					if (ifc > cblke) {
						ifst.setIfCloseLineNumber(cblke);
						return;
					}

				}

			}

			// Check2 whether if lies within any catch of finally
			FinallyBlock fin = tryb.getFinallyBlock();
			if (fin != null) {
				int fins = fin.getStart();
				int fine = fin.getEnd();
				if (ifs > fins && ifs < fine) {
					if (ifc > fine) {
						ifst.setIfCloseLineNumber(fine);
						return;
					}
				}
			}

		}

	}

	public static boolean doesClashedIfWithExceptionHandlerStartBefore(int ifend) {

		List tries = getContext().getAllTriesForMethod();
		for (int z = 0; z < tries.size(); z++) {
			TryBlock tryb = (TryBlock) tries.get(z);
			List catches = tryb.getAllCatchesForThisTry();
			for (int w = 0; w < catches.size(); w++) {
				CatchBlock cb = (CatchBlock) catches.get(w);
				if (cb.getEnd() == ifend) {
					int start = IFHelper.getIfStartGivenEnd(ifend);
					if (start != -1 && start < tryb.getStart()) {
						return true;
					}
				}
			}
			FinallyBlock fb = tryb.getFinallyBlock();
			if (fb != null && fb.getEnd() == ifend) {
				int start = IFHelper.getIfStartGivenEnd(ifend);
				if (start != -1 && start < tryb.getStart()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean doesHandlerBlockStartAtIndex(int index) {
		List tries = getContext().getAllTriesForMethod();
		for (int i = 0; i < tries.size(); i++) {
			TryBlock tryb = (TryBlock) tries.get(i);
			List catches = tryb.getAllCatchesForThisTry();
			for (int j = 0; j < catches.size(); j++) {
				CatchBlock cb = (CatchBlock) catches.get(j);
				if (cb.getStart() == index) {
					return true;
				}
			}
			FinallyBlock fb = tryb.getFinallyBlock();
			if (fb != null) {
				if (fb.getStart() == index) {
					return true;
				}
			}
		}
		return false;
	}

}