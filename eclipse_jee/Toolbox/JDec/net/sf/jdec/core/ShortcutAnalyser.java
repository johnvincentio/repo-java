/*
 *  ShortcutAnalyser.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public Licensewith this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.sf.jdec.core;

import net.sf.jdec.blocks.Loop;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.InterfaceMethodRef;
import net.sf.jdec.constantpool.MethodInfo;
import net.sf.jdec.constantpool.MethodRef;
import net.sf.jdec.constantpool.NameAndType;
import net.sf.jdec.io.Writer;
import net.sf.jdec.lookup.FinderFactory;
import net.sf.jdec.lookup.IFinder;
import net.sf.jdec.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author sbelur
 */
public class ShortcutAnalyser {

	String loadtype = "";

	String storetype = "";

	int loadindex = -1;

	int storeindex = -1;

	private ArrayList ifsWithNoConnector = new ArrayList();

	private boolean booleanAssignFound = false;

	private ArrayList lastIfs;

	private ArrayList groups;

	private ArrayList starts;

	private HashMap connectorMap = new HashMap();

	private MethodInfo minfo;

	private byte[] code;

	private ArrayList allchains;

	public static final String AND = "&&";

	public static final String OR = "||";

	public static final String NONE = "NONE";

	private HashMap booleanAssignMap = new HashMap();

	private HashMap skipWRTbooleanShortcutAssignFound = new HashMap();

	private ArrayList allstarts = null;

	/** Creates a new genericFinder of ShortcutAnalyser */
	public ShortcutAnalyser(MethodInfo minfo, byte[] info) {

		this.minfo = minfo;
		this.code = info;
		starts = minfo.getInststartpos();
		allchains = new ArrayList();
		groups = new ArrayList();
		lastIfs = new ArrayList();
		allstarts = minfo.getInststartpos();
	}

	// Both inclusive
	public int searchForIfInRange(int start, int end) {

		for (int z = start; z <= end; z++) {
			boolean isif = checkForIf(z);
			if (isif) {
				return z;
			}
		}
		return -1;
	}

	public void analysze() {
		if (code == null || code.length == 0)
			return;
		boolean ifinst = false;
		for (int z = 0; z < code.length; z++) {
			if (checkForIf(z)) {
				boolean invalid = checkToDisQualifyIf(z + 1, code);
				if (invalid) {
					z = z + 2;
					continue;
				}
				int ifstart = z;
				int jump = getJumpAddress(code, ifstart);
				int firstIfJump = jump;
				boolean ifjumpisif = checkForIf(jump - 3);

				if (jump < ifstart) {
					// Jumping Back [Within loop]
					int jumpbackifs[] = getJumpBackIfRange(code, jump, ifstart);
					jumpbackifs = checkForMethodInvocationNotPartOfChain(code,jumpbackifs);
					int jumpbackjump[] = new int[jumpbackifs.length];
					if (jumpbackifs.length > 1) {
						for (int f = 0; f < jumpbackifs.length; f++) {
							jumpbackjump[f] = jump;
						}
						StringBuffer sbp = new StringBuffer();
						connectBlockSetByAnd(jumpbackifs, jumpbackjump, sbp);
						int in = Integer.parseInt(sbp.toString());
						if (in > 0 && in < jumpbackifs.length) {
							z = jumpbackifs[in] + 2;
						} else
							z = jumpbackifs[jumpbackifs.length - 1] + 2;
						continue;
					}

				}
				if (ifjumpisif && ((jump - 3) > ifstart)) {
					int initialJump = jump;
					int alljumps[] = getAllIfJumpsInRange(code, ifstart, jump)[0];
					int reqd = getReqdJump(alljumps, code);// Will get max jump
															// and if for that
															// jump-3 is present

					if (reqd != -1) {

						int i = reqd - 3;

						while (true) {

							int j = getJumpAddress(code, i);
							if (j > reqd) {
								if (checkForIf(j - 3)) {
									reqd = j;
									i = reqd - 3;
								} else {
									break;
								}
							} else {
								break;
							}
						}

						// Continue ...if reqd-3 is not if then take
						// all ifs in new range and check if not same
						// as current if range, Then basically
						// chain shud include those ifs as well
						// nut set jumpusedto to -1

						// Implementation for above
						int w = (reqd - 3);
						boolean ifsetreset = false;
						w = getJumpAddress(code, w);
						int trackJump = w;
						w = w - 3;
						boolean diff = false;
						boolean checkChainAgain = false;
						int someifs[] = null;
						int newsomeifs[] = null;
						if (!checkForIf(w) && w > reqd) {
							someifs = getIfsInRange(ifstart, w);
							// TODO: iinc case - need to modify someifs
							someifs = checkForIINCCaseNotPartOfChain(code,
									someifs);
							someifs = checkForMethodInvocationNotPartOfChain(code, someifs);
							int thisifs[] = getIfsInRange(ifstart, reqd - 3);
							diff = compare2IfSets(someifs, thisifs);
							if (diff) {
								ifsetreset = true;
								checkChainAgain = true;
							}
						}
						// End

						int temp1 = reqd - 3;
						if (checkForIf(temp1)) {
							int temp2 = getJumpAddress(code, temp1);
							if (temp2 < temp1) {
								int tempar[] = getJumpBackIfRange(code, temp2,
										temp1);
								if (tempar != null && tempar.length > 1) {
									someifs = getIfsInRange(ifstart,
											tempar[tempar.length - 1]);
									int thisifs[] = getIfsInRange(ifstart,
											reqd - 3);
									diff = compare2IfSets(someifs, thisifs);
									if (diff) {
										ifsetreset = true;
									}
								}
							}
						}

						Shortcutchain chain = null;
						if (diff == false) {
							chain = registerIfBlockSet(code, ifstart, reqd);
						} else {
							if (someifs != null && someifs.length > 0) {
								// here one check for someifs

								if (!checkChainAgain) {
									int pos = detectInvalidShortcutIf(someifs);
									if (pos == -1) {
										Shortcutchain schain = new Shortcutchain();
										schain.setJumpUsedToGetLast(-1);
										schain.setBegin(ifstart);
										schain
												.setLast(someifs[someifs.length - 1]);
										schain.setIfStarts(someifs);
										schain.setIfjumps(getIfJumps(someifs));
										chain = schain;
										allchains.add(chain);
									} else {
										if (pos == 0) {
											chain = null;
										} else {
											int newifs[] = new int[(pos + 1)];
											int newifjumps[] = new int[(pos + 1)];
											for (int p = 0; p <= pos; p++) {
												newifs[p] = someifs[p];

											}
											Shortcutchain schain = new Shortcutchain();
											schain.setJumpUsedToGetLast(-1);
											schain.setBegin(newifs[0]);
											schain
													.setLast(newifs[newifs.length - 1]);
											schain.setIfStarts(newifs);
											schain
													.setIfjumps(getIfJumps(newifs));
											chain = schain;
											allchains.add(chain);

										}
									}
								} else {
									int position = -1;
									for (int l = someifs.length - 1; l >= 0; l--) {
										int iff = someifs[l];
										int iffjump = getJumpAddress(code, iff);
										if (iffjump == trackJump) {
											position = l;
											break;
										}
									}
									if (position != -1) {

										int number = position + 1;
										newsomeifs = new int[number];
										for (int k1 = 0; k1 < number; k1++) {
											newsomeifs[k1] = someifs[k1];
										}

										int pos = detectInvalidShortcutIf(newsomeifs);
										if (pos == -1) {
											Shortcutchain schain = new Shortcutchain();
											schain.setJumpUsedToGetLast(-1);
											schain.setBegin(newsomeifs[0]);
											schain
													.setLast(newsomeifs[newsomeifs.length - 1]);
											schain.setIfStarts(newsomeifs);
											schain
													.setIfjumps(getIfJumps(newsomeifs));
											chain = schain;
											allchains.add(chain);
										} else {

											if (pos == 0) {
												chain = null;
											} else {
												int newifs[] = new int[(pos + 1)];
												for (int p = 0; p <= pos; p++) {
													newifs[p] = newsomeifs[p];
												}
												Shortcutchain schain = new Shortcutchain();
												schain.setJumpUsedToGetLast(-1);
												schain.setBegin(newifs[0]);
												schain
														.setLast(newifs[newifs.length - 1]);
												schain.setIfStarts(newifs);
												schain
														.setIfjumps(getIfJumps(newifs));
												chain = schain;
												allchains.add(chain);

											}

										}

									} else { // Revert
										int pos = detectInvalidShortcutIf(someifs);
										if (pos == -1) {
											Shortcutchain schain = new Shortcutchain();
											schain.setJumpUsedToGetLast(-1);
											schain.setBegin(ifstart);
											schain
													.setLast(someifs[someifs.length - 1]);
											schain.setIfStarts(someifs);
											schain
													.setIfjumps(getIfJumps(someifs));
											chain = schain;
											allchains.add(chain);
											checkChainAgain = false;
										} else {
											if (pos == 0) {
												chain = null;
											} else {
												int newifs[] = new int[(pos + 1)];
												int newifjumps[] = new int[(pos + 1)];
												for (int p = 0; p <= pos; p++) {
													newifs[p] = someifs[p];

												}
												Shortcutchain schain = new Shortcutchain();
												schain.setJumpUsedToGetLast(-1);
												schain.setBegin(newifs[0]);
												schain
														.setLast(newifs[newifs.length - 1]);
												schain.setIfStarts(newifs);
												schain
														.setIfjumps(getIfJumps(newifs));
												chain = schain;
												allchains.add(chain);
												checkChainAgain = false;

											}
										}

									}

								}
							} else {

								diff = false;
								chain = registerIfBlockSet(code, ifstart, reqd);
							}
						}
						if (chain != null) { // start analysis of the set
							boolean minus3IsOr = true;
							if (reqd > initialJump) {
								if (isThisInstrStart(starts, reqd - 3)
										&& isInstructionIF(code[reqd - 3])) {
									int allj[] = chain.getIfjumps();
									int len = allj.length;
									if (allj.length > 2
											&& (allj[len - 1] == reqd && allj[len - 2] == reqd)) {
										minus3IsOr = false;
									}
								}
							}
							analyzeBlockSet(chain, minus3IsOr);
							if (diff == false)
								z = reqd - 1;
							else {

								if (!checkChainAgain)
									z = someifs[someifs.length - 1] + 2;
								else
									z = newsomeifs[newsomeifs.length - 1] + 2;

							}
							continue;
						} else {
							z = jump - 3;// here
							int someifpos = searchForIfInRange(ifstart + 3,
									z - 1);
							if (someifpos != -1) {
								z = someifpos - 1;
							}
							continue;
						}
					} else {
						z = jump - 3;// here
						int someifpos = searchForIfInRange(ifstart + 3, z - 1);
						if (someifpos != -1) {
							z = someifpos - 1;
						}
						continue;
					}
				} else if (!ifjumpisif && ((jump - 3) > ifstart)) {
					// Check for all and condition here
					int ar[][] = getAllIfJumpsInRange(code, ifstart, (jump - 3));
					// Here check for any method invocation
					// which does not return value to stack
					// and hence NOT part of chain...
					if (ar.length != 0 && ar[0] != null && ar[0].length != 0
							&& ar[1] != null && ar[1].length != 0) {
						ar[1] = checkForMethodInvocationNotPartOfChain(code,
								ar[1]);
					}

					if (ar.length == 0 || ar[0] == null || ar[0].length == 0
							|| ar[1] == null || ar[1].length == 0) {
						z = jump - 3;// here
						int someifpos = searchForIfInRange(ifstart + 3, z - 1);
						if (someifpos != -1) {
							z = someifpos - 1;
						}
						continue;
					} else {
						int jmps[] = ar[0];
						if (jmps.length > 1 && ar[1].length > 1) {
							int first = jmps[0];
							boolean unique = true;
							for (int d = 1; d < jmps.length; d++) {
								int n = jmps[d];
								if (n != first) {
									unique = false;
									break;
								}
							}
							if (unique) { // All are and
								int jumpArray[] = getIfJumps(ar[1]);
								StringBuffer sbp = new StringBuffer();
								connectBlockSetByAnd(ar[1], jumpArray, sbp);
								int in = Integer.parseInt(sbp.toString());
								int f = ar[1][ar[1].length - 1];
								if (in > 0 && in < ar[1].length) {
									f = ar[1][in];
								}
								z = jump - 3;// here

								int someifpos = searchForIfInRange(f + 3, z - 1);
								if (someifpos != -1) {
									z = someifpos - 1;
								}
								continue;
							} else {
								int counter = 0;
								int st[] = ar[1];
								int pos = -1;
								for (int t = 0; t < st.length; t++) {

									int if_jump = getJumpAddress(code, st[t]);
									// Here check needs to be done
									// whether the 2 ifs really are in shortcut
									// chain
									// Like there may be an assignment in b/w
									// ...
									// However the assigned value can actually
									// be pushed
									// back to stack to be compared in another
									// if..
									// To identify this, a dup needs to be
									// present
									// before the assignment. Otherwise it is
									// not shortcut....

									boolean assignmentPresent = false;//
									if (t > 0)
										assignmentPresent = anyAssignmentPresentBtw2Ifs(
												st[t - 1], st[t]);
									if (if_jump == firstIfJump
											&& !assignmentPresent) {
										counter++;
										pos = t;
									} else {
										break;
									}

								}
								if (counter > 1 && pos != -1) {
									int ifarray[] = new int[(pos + 1)];

									for (int r = 0; r <= pos; r++) {
										ifarray[r] = st[r];
									}
									int jumpArray[] = getIfJumps(ifarray);
									StringBuffer sbp = new StringBuffer();
									connectBlockSetByAnd(ifarray, jumpArray,
											sbp);
									// int remaining[]=new
									// int[st.length-ifarray.length];
									// analyzeSubsetIfRange(remaining);
									int in = Integer.parseInt(sbp.toString());
									if (in > 0 && in < ifarray.length) {
										z = ifarray[in] + 2;
									}
									z = ifarray[pos] + 2;
									continue;
								} else {

									// sbelur
									// Here check for shortcut boolean or method
									// condition here
									// Because boolean sc will form a set
									int b = checkForShortcutCondition(st[0]);
									if (b != -1 && b > z) {
										z = b;
									} else {
										z = st[0] + 2;
									}
									continue;
								}
							}

						}
					}
				}
			}
		}
	}

	public int getNextInstrPos(int index) {
		byte[] code = minfo.getCode();
		for (int i = index + 1; i < code.length; i++) {
			boolean start = isThisInstrStart(starts, i);
			if (start) {
				return i;
			}
		}
		return -1;
	}

	private IFinder getGenericFinder() {
		return FinderFactory.getFinder(IFinder.BASE);
	}

	public int getOffset(int counter, byte[] info) {

		int b1 = info[++counter];
		int b2 = info[++counter];
		int z;
		if (b1 < 0)
			b1 = (256 + b1);
		if (b2 < 0)
			b2 = (256 + b2);

		int indexInst = (((b1 << 8) | b2));
		if (indexInst > 65535)
			indexInst = indexInst - 65536;
		if (indexInst < 0)
			indexInst = 256 + indexInst;
		return indexInst;
	}

	private String getMethodReturnType(int i) {
		int classIndex = getOffset(i, minfo.getCode());
		i += 2;
		ClassDescription cd = ClassDescription.ref;
		MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
		String classname = mref.getClassname();
		String typeofmet = mref.getTypeofmethod();
		int s1 = typeofmet.indexOf(")");
		String rettp = "";
		ArrayList returntype = null;
		if (s1 != -1 && s1 + 1 < typeofmet.length()) {
			rettp = typeofmet.substring(s1 + 1);
			Util.parseReturnType(rettp);
			returntype = Util.getreturnSignatureAsList();
		}
		String pushStr;
		if (returntype != null && returntype.size() > 0) {
			pushStr = (java.lang.String) returntype.get(0);
		}
		else{
			return pushStr = classname;
		}

		return pushStr;
	}

	private String getInterfaceMethodReturnType(int i) {
		int classIndex = getOffset(i, minfo.getCode());
		i += 2;
		ClassDescription cd = ClassDescription.ref;
		InterfaceMethodRef iref = cd
				.getInterfaceMethodAtCPoolPosition(classIndex);
		java.lang.String classname = iref.getClassname();
		java.lang.String typeofmet = iref.getTypeofmethod();
		Util.parseDescriptor(typeofmet);
		ArrayList paramlist = Util.getParsedSignatureAsList();
		int s1 = typeofmet.indexOf(")");
		ArrayList returntype = null;

		String tempString = "";
		java.lang.String rettp = "";
		if (s1 != -1 && s1 + 1 < typeofmet.length()) {

			rettp = typeofmet.substring(s1 + 1);

			Util.parseReturnType(rettp);
			returntype = Util.getreturnSignatureAsList();
		}
		java.lang.String pushStr;
		if (returntype != null && returntype.size() > 0) {
			pushStr = (java.lang.String) returntype.get(0);
		}
		else{
			return pushStr = classname;
		}
		/*if (pushStr != null && pushStr.trim().equalsIgnoreCase("void")) {
			pushStr = classname;
		}*/
		return pushStr;
	}
	private int[] checkForMethodInvocationNotPartOfChain(byte[] code,
			int[] ifstarts) {

		int i = 0, j = 1;
		int breakIndex = -1;
		whileloop: while (j < ifstarts.length) {
			int first = ifstarts[i];
			int second = ifstarts[j];
			for (int k = first + 1; k < second; k++) {
				if (!isThisInstrStart(starts, k))
					continue;
				switch (code[k]) {
				case JvmOpCodes.INVOKESPECIAL:
				case JvmOpCodes.INVOKESTATIC:
				case JvmOpCodes.INVOKEVIRTUAL:
					int next = getNextInstrPos(k);
					if (code[next] == JvmOpCodes.POP
							|| code[next] == JvmOpCodes.POP2) {
						breakIndex = i;
						break whileloop;
					}
					String returntype = getMethodReturnType(k);
					if (returntype.trim().equalsIgnoreCase("void")) {
						breakIndex = i;
						break whileloop;
					}
					break;
				case JvmOpCodes.INVOKEINTERFACE:	
				
					next = getNextInstrPos(k);
					if (code[next] == JvmOpCodes.POP
							|| code[next] == JvmOpCodes.POP2) {
						breakIndex = i;
						break whileloop;
					}
					returntype = getInterfaceMethodReturnType(k);
					if (returntype.trim().equalsIgnoreCase("void")) {
						breakIndex = i;
						break whileloop;
					}
					break;
					
				default:
					continue;
				}
			}
			i = i + 1;
			j = j + 1;
		}

		if (breakIndex == -1)
			return ifstarts;
		if (breakIndex >= ifstarts.length)
			return ifstarts;
		int newifstarts[] = new int[breakIndex + 1];
		for (int k = 0; k <= breakIndex; k++) {
			newifstarts[k] = ifstarts[k];
		}
		return newifstarts;
	}

	public int isInstIloadInst(int pos, StringBuffer buffer) {
		if (isThisInstrStart(starts, pos)) {
			switch (minfo.getCode()[pos]) {
			case JvmOpCodes.ILOAD_0:
				buffer.append(0);
				return pos;

			case JvmOpCodes.ILOAD_1:

				buffer.append(1);
				return pos;

			case JvmOpCodes.ILOAD_2:

				buffer.append(2);
				return pos;

			case JvmOpCodes.ILOAD_3:

				buffer.append(3);
				return pos;

			case JvmOpCodes.ILOAD:
				buffer.append(minfo.getCode()[pos + 1]);
				return pos;

			}
		}

		return -1;

	}

	private int[] checkForIINCCaseNotPartOfChain(byte[] code, int[] ifstarts) {

		int i = 0, j = 1;
		int breakIndex = -1;
		whileloop: while (j < ifstarts.length) {
			int first = ifstarts[i];
			int second = ifstarts[j];
			for (int k = first + 1; k < second; k++) {
				if (!isThisInstrStart(starts, k))
					continue;
				switch (code[k]) {
				case JvmOpCodes.IINC:
					int index = code[(k + 1)];
					int next = getNextInstrPos(k);
					StringBuffer indexBuf = new StringBuffer();
					int temp = isInstIloadInst(next, indexBuf);
					if (temp != -1) {
						try {
							int someIndex = Integer.parseInt(indexBuf
									.toString());
							if (someIndex != index) {
								breakIndex = i;
								break whileloop;
							}
						} catch (NumberFormatException ne) {
						}
					}
					break;

				default:
					continue;
				}
			}
			i = i + 1;
			j = j + 1;
		}

		if (breakIndex == -1)
			return ifstarts;
		if (breakIndex >= ifstarts.length)
			return ifstarts;
		int newifstarts[] = new int[breakIndex + 1];
		for (int k = 0; k <= breakIndex; k++) {
			newifstarts[k] = ifstarts[k];
		}
		return newifstarts;
	}

	public boolean isCurrentInstStore(int pos) {
		boolean flag;
		if (!isThisInstrStart(starts, pos))
			return false;
		int inst = code[pos];
		switch (inst) {

		case JvmOpCodes.ASTORE:
			flag = true;
			break;

		case JvmOpCodes.ASTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ASTORE_3:
			flag = true;
			break;
		case JvmOpCodes.DSTORE:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.DSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.FSTORE:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.FSTORE_3:
			flag = true;
			break;

		case JvmOpCodes.ISTORE:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_0:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_1:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_2:
			flag = true;
			break;
		case JvmOpCodes.ISTORE_3:
			flag = true;
			break;
		case JvmOpCodes.LSTORE:
			flag = true;
			break;

		case JvmOpCodes.LSTORE_0:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_1:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_2:
			flag = true;
			break;
		case JvmOpCodes.LSTORE_3:
			flag = true;
			break;

		default:
			flag = false;
		}
		return flag;
	}

	private boolean anyAssignmentPresentBtw2Ifs(int firstIf, int secondIf) {
		for (int k = firstIf + 1; k < secondIf; k++) {
			boolean start = isThisInstrStart(starts, k);
			if (start) {
				if (isCurrentInstStore(k)) {
					if (!isInstructionAnyDUP(k - 1)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isInstructionAnyDUP(int pos) {
		boolean start = isThisInstrStart(starts, pos);
		if (!start) {
			return false;
		}
		switch (code[pos]) {
		case JvmOpCodes.DUP:
		case JvmOpCodes.DUP2:
		case JvmOpCodes.DUP_X1:
		case JvmOpCodes.DUP_X2:
		case JvmOpCodes.DUP2_X1:
		case JvmOpCodes.DUP2_X2:
			return true;

		}
		return false;
	}

	private int checkForShortcutCondition(int current) {
		boolean z = doesBooleanAssignFoundExist(current);
		boolean b = false;
		StringBuffer end = new StringBuffer();
		if (z == false) {
			StringBuffer methodsc = new StringBuffer("");
			booleanAssignFound = checkForShortCutIfBooleanAssignment(
					getJumpAddress(code, current), code, end, methodsc, current);
			if (booleanAssignFound) {
				booleanAssignMap.put(new Integer(current), new Integer(end
						.toString()));
				b = true;
			}
		}
		if (z == true) {
			b = true;
		}
		int next = -1;
		if (b) {
			try {
				int till = Integer.parseInt(end.toString());
				ArrayList iflist = new ArrayList();
				ArrayList ifjumps = new ArrayList();
				for (int from = current; from < till; from++) {
					if (checkForIf(from)) {
						iflist.add(new Integer(from));
						ifjumps.add(new Integer(getJumpAddress(code, from)));
					}
				}
				if (iflist.size() > 0) {
					Shortcutchain chain = new Shortcutchain();
					Integer ins[] = (Integer[]) iflist
							.toArray(new Integer[iflist.size()]);
					int temp1[] = new int[ins.length];
					for (int u = 0; u < ins.length; u++) {
						temp1[u] = ins[u].intValue();
					}
					ins = (Integer[]) ifjumps
							.toArray(new Integer[iflist.size()]);
					int temp2[] = new int[ins.length];
					for (int u = 0; u < ins.length; u++) {
						temp2[u] = ins[u].intValue();
					}
					// here one check for temp1
					int pos = detectInvalidShortcutIf(temp1);
					if (pos == -1) {
						chain.setIfStarts(temp1);
						chain.setIfjumps(temp2);
						chain.setBegin(temp1[0]);
						chain.setLast(temp1[temp1.length - 1]);
						chain.setJumpUsedToGetLast(-1);
						analyzeBlockSet(chain, true);
						next = temp1[temp1.length - 1] + 2;
					} else {
						if (pos == 0) {
							return -1;
						} else {

							int newifs[] = new int[(pos + 1)];
							int newifjumps[] = new int[(pos + 1)];
							for (int p = 0; p <= pos; p++) {
								newifs[p] = temp1[p];
								newifjumps[p] = temp2[p];
							}
							chain.setIfStarts(newifs);
							chain.setIfjumps(newifjumps);
							chain.setBegin(newifs[0]);
							chain.setLast(newifs[newifs.length - 1]);
							chain.setJumpUsedToGetLast(-1);
							analyzeBlockSet(chain, true);
							next = newifs[newifs.length - 1] + 2;

						}
					}
				} else {
					next = -1;
				}
			} catch (NumberFormatException ne) {
				next = -1;
			}

		}
		return next;
	}

	// returns true if different
	private boolean compare2IfSets(int ifs1[], int[] ifs2) {

		int len1 = ifs1.length;
		int len2 = ifs2.length;
		if (len1 != len2) {
			return true;
		}

		return false;
	}

	// Both inclusive
	public int[] getIfsInRange(int ifstart, int w) {

		ArrayList ifs = new ArrayList();
		for (int z = ifstart; z <= w; z++) {
			if (checkForIf(z)) {
				ifs.add(new Integer(z));
			}
		}

		Integer temp[] = (Integer[]) ifs.toArray(new Integer[ifs.size()]);
		int itemp[] = new int[temp.length];
		for (int x = 0; x < temp.length; x++) {
			itemp[x] = temp[x].intValue();
		}
		return itemp;
	}

	// Currently not used
	private void analyzeSubsetIfRange(int remaining[]) {

		if (remaining.length == 0)
			return;
		int jump = getJumpAddress(code, remaining[0]);
		int ifstart = remaining[0];
		boolean nextisif = checkForIf(jump - 3);
		if (nextisif && ((jump - 3) != ifstart) && ((jump - 3) > ifstart)) {

			int alljumps[] = getAllIfJumpsInRange(code, ifstart, jump)[0];
			int reqd = getReqdJump(alljumps, code);// Will get max jump and if
													// for that jump-3 is
													// present
			if (reqd != -1) {

				int i = reqd - 3;
				while (true) {

					int j = getJumpAddress(code, i);
					if (j > reqd) {
						if (checkForIf(j - 3)) {
							reqd = j;
							i = reqd - 3;
						} else {
							break;
						}
					} else {
						break;
					}
				}

				Shortcutchain chain = registerIfBlockSet(code, ifstart, reqd);
				if (chain != null) { // start analysis of the set
					analyzeBlockSet(chain, true);
				}
			}
		}
	}

	public void checkMissingLinks() {

		for (int z = 0; z < allchains.size(); z++) {

			Shortcutchain chain1 = (Shortcutchain) allchains.get(z);
			if ((z + 1) >= allchains.size())
				return;
			Shortcutchain chain2 = (Shortcutchain) allchains.get(z + 1);
			boolean connect = connectChains(chain1, chain2);
			int ifa[] = chain1.getIfstarts();
			int lastif = ifa[ifa.length - 1];
			String str = getConnector(lastif);
			boolean add = true;
			if (ifsWithNoConnector.contains(new Integer(lastif))) {
				add = false;
			}

			if (add && connect && (str == null || str.equals(NONE))) {
				int nextarray[] = chain2.getIfstarts();
				int nexts = nextarray[0];
				if (nexts > lastif) {
					Shortcutstore scut = new Shortcutstore();
					scut.setConnectortype(AND); // ok ?
					scut.setIfstart(lastif);
					scut.setNextifstart(nexts);
					scut.setIfbyend(getJumpAddress(code, lastif));
					connectorMap.put(new Integer(lastif), scut);
				}
				chain1.setNextChain(chain2);
				chain2.setPrevChain(chain1);
			}
		}
	}

	private int[] getIfJumps(int ifs[]) {
		int jumps[] = new int[ifs.length];
		for (int x = 0; x < ifs.length; x++) {
			jumps[x] = getJumpAddress(code, ifs[x]);
		}
		return jumps;
	}

	private void connectBlockSetByAnd(int starts[], int jumps[],
			boolean registerChain) {

		boolean processed = false;
		for (int x = 0; x < starts.length - 1; x++) {
			processed = true;
			int curs = starts[x];
			int curj = jumps[x];
			Shortcutstore store = new Shortcutstore();
			store.setIfstart(curs);
			store.setIfbyend(curj);
			store.setConnectortype(this.AND);
			store.setNextifstart(starts[(x + 1)]);
			connectorMap.put(new Integer(curs), store);
		}
		if (processed && registerChain) {
			Shortcutchain chain = new Shortcutchain();
			chain.setBegin(starts[0]);
			chain.setLast(starts[starts.length - 1]);
			chain.setIfStarts(starts);
			chain.setIfjumps(jumps);
			chain.setAllAnd(true);
			allchains.add(chain);
			// chain.setJumpUsedToGetLast()
		}

	}

	private void connectBlockSetByAnd(int starts[], int jumps[],
			StringBuffer position) {

		boolean processed = false;
		int pos = detectInvalidShortcutIf(starts);
		position.append("" + pos);
		if (pos == -1) {
			for (int x = 0; x < starts.length - 1; x++) {
				processed = true;
				int curs = starts[x];
				int curj = jumps[x];
				Shortcutstore store = new Shortcutstore();
				store.setIfstart(curs);
				store.setIfbyend(curj);
				store.setConnectortype(this.AND);
				store.setNextifstart(starts[(x + 1)]);
				connectorMap.put(new Integer(curs), store);
			}
			if (processed) {
				Shortcutchain chain = new Shortcutchain();
				chain.setBegin(starts[0]);
				chain.setLast(starts[starts.length - 1]);
				chain.setIfStarts(starts);
				chain.setIfjumps(jumps);
				chain.setAllAnd(true);
				allchains.add(chain);
				// chain.setJumpUsedToGetLast()
			}
		} else {
			if (pos == 0) {

			} else {
				processed = false;
				int newifs[] = new int[(pos + 1)];
				int newifjumps[] = new int[(pos + 1)];
				for (int p = 0; p <= pos; p++) {
					newifs[p] = starts[p];
					newifjumps[p] = jumps[p];
				}

				for (int x = 0; x < newifs.length - 1; x++) {
					processed = true;
					int curs = newifs[x];
					int curj = newifjumps[x];
					Shortcutstore store = new Shortcutstore();
					store.setIfstart(curs);
					store.setIfbyend(curj);
					store.setConnectortype(this.AND);
					store.setNextifstart(newifs[(x + 1)]);
					connectorMap.put(new Integer(curs), store);
				}
				if (processed) {
					Shortcutchain chain = new Shortcutchain();
					chain.setBegin(newifs[0]);
					chain.setLast(newifs[newifs.length - 1]);
					chain.setIfStarts(newifs);
					chain.setIfjumps(newifjumps);
					chain.setAllAnd(true);
					allchains.add(chain);
					// chain.setJumpUsedToGetLast()
				}

				// GridBagLayout

			}
		}

	}

	private void analyzeBlockSet(Shortcutchain chain, boolean minus3IsOr) {

		int from = chain.getLast();
		int to = chain.getBegin();
		int jumps[] = chain.getIfjumps();
		int ifstarts[] = chain.getIfstarts();
		if (jumps.length < 2)
			return; // nothing to do
		int l = jumps.length;
		int last = jumps[l - 1];

		// Check here for all and condition
		if (isThisInstrStart(starts, jumps[0] - 3)) {
			if (isInstructionIF(code[(jumps[0] - 3)])) {
				// Check for all same jumps in set
				boolean same = true;
				int first = jumps[0];
				for (int sm = 1; sm < jumps.length; sm++) {
					if (jumps[sm] != first) {
						same = false;
						break;
					}
				}
				if (same) {
					connectBlockSetByAnd(ifstarts, jumps, false);
					return;
				}

			}
		}
		// End

		int lastButOne = jumps[l - 2];
		int jumpused = chain.getJumpUsed();
		String finalConnector;
		if (last == lastButOne) {
			finalConnector = this.AND;
		} else {
			finalConnector = this.OR;
		}
		Shortcutstore store = null;
		// First create for the last pair.
		store = new Shortcutstore();
		store.setConnectortype(finalConnector);
		store.setIfstart(ifstarts[l - 2]);
		store.setNextifstart(ifstarts[l - 1]);
		store.setIfbyend(jumps[l - 2]);
		connectorMap.put(new Integer(ifstarts[l - 2]), store);

		int lastif = ifstarts[l - 1];
		if (ifstarts.length == 2)
			return;

		int startFrom = l - 3;
		// check here is jump is pointing back
		int temp = -1;
		int e = -1;

		if (last < ifstarts[ifstarts.length - 1]) {

			for (e = jumps.length - 1; e >= 0; e--) {

				int cj = jumps[e];
				if (cj == last) {
					if (e != (jumps.length - 1))
						temp = e;
					continue;
				} else {
					break;
				}
			}
			int total = (jumps.length - 1) - (temp) + 1;
			if (temp != -1 && e != -1 && total > 0) {

				startFrom = e;
				int subsetif[] = new int[total];
				int subsetjump[] = new int[total];
				int zee = 0;
				for (int m = temp; m < jumps.length; m++) {
					subsetif[zee] = ifstarts[m];
					subsetjump[zee] = jumps[m];
					zee++;
				}
				StringBuffer sbp = new StringBuffer();
				connectBlockSetByAnd(subsetif, subsetjump, sbp);
			}

		}

		// Go Back till first if is processed
		for (int p = startFrom; p >= 0; p--) {

			int curstart = ifstarts[p];
			int curjump = jumps[p];
			if (curjump > lastif && (curjump - 3) > lastif) {
				// Case 1
				// Last Change -> if jump is beyond last
				// and not same as last 2 jumps then
				// make it opposite of final connector
				// else AND

				int lastjump = jumps[l - 1];
				int lastButOnejump = jumps[l - 2];
				String c = "";
				if (curjump == lastjump && curjump == lastButOnejump) {

					c = AND;
				} else {
					if (finalConnector.equals(AND)) {
						c = OR;
					} else {
						c = AND;
					}
				}

				Shortcutstore scutstore = new Shortcutstore();
				scutstore.setConnectortype(c); // May be this needs more
												// testing
				scutstore.setIfstart(curstart);
				scutstore.setIfbyend(curjump);
				scutstore.setNextifstart(ifstarts[(p + 1)]);
				connectorMap.put(new Integer(curstart), scutstore);
			} else if (curjump == jumpused) {
				// Case 2
				Shortcutstore scutstore = new Shortcutstore();
				if (minus3IsOr)
					scutstore.setConnectortype(OR);
				else
					scutstore.setConnectortype(AND);
				scutstore.setIfstart(curstart);
				scutstore.setIfbyend(curjump);
				scutstore.setNextifstart(ifstarts[(p + 1)]);
				connectorMap.put(new Integer(curstart), scutstore);
			} else if (checkForIf((curjump - 3)) && curjump > curstart) {
				// Case 3-> Make opposite
				String connectortype = getConnector((curjump - 3));
				String connectorForThisIf = null;
				if (connectortype.equals(AND)) {
					connectorForThisIf = OR;
				} else if (connectortype.equals(OR)) {
					connectorForThisIf = AND;
				}
				if (connectorForThisIf != null) {
					Shortcutstore scutstore = new Shortcutstore();
					scutstore.setConnectortype(connectorForThisIf);
					scutstore.setIfstart(curstart);
					scutstore.setIfbyend(curjump);
					scutstore.setNextifstart(ifstarts[(p + 1)]);
					connectorMap.put(new Integer(curstart), scutstore);
				} else {
					// Case when curjump-3 is not deciding jump ...OR
					if ((curjump - 3) != jumpused) {
						Shortcutstore scutstore = new Shortcutstore();
						scutstore.setConnectortype(OR);
						scutstore.setIfstart(curstart);
						scutstore.setIfbyend(curjump);
						scutstore.setNextifstart(ifstarts[(p + 1)]);
						connectorMap.put(new Integer(curstart), scutstore);
					}
				}

			} else {

				// Default to AND...some use case not figured out.
				// Can be logged here...
				Shortcutstore scutstore = new Shortcutstore();
				scutstore.setConnectortype(AND); // May be this needs more
													// testing
				scutstore.setIfstart(curstart);
				scutstore.setIfbyend(curjump);
				scutstore.setNextifstart(ifstarts[(p + 1)]);
				connectorMap.put(new Integer(curstart), scutstore);
				try {

					Writer writer = Writer.getWriter("log");
					writer.writeLog("<REPORT TO JDEC ADMIN>\n");
					writer
							.writeLog("ISSUE: Need to check shortcut connector logic");
					writer
							.writeLog("No connector for this If-- Defaulting to AND\n");
					writer.writeLog(("Make a new case for me " + curjump
							+ " is currentJump. My If Start is " + curstart
							+ " In method " + minfo.getMethodName())
							+ "\n");
					writer.writeLog("</REPORT TO JDEC ADMIN>\n");
					writer.flush();
				} catch (IOException ex) {

				}

				// Some use case to figure out
				System.out
						.println("Shortcut if some use case to probe: Make a new case for me "
								+ curjump
								+ " is currentJump. My If Start is "
								+ curstart
								+ " In method - "
								+ minfo.getMethodName());
			}

		}

	}

	/***************************************************************************
	 * ifstart -> identifies the beginning if block till where analsys should
	 * cover( analysys starts from the end) reqd -> identifies the last ifblock
	 * i.e reqd-3 should be the start of last if(from where the analysys begins)
	 */
	private Shortcutchain registerIfBlockSet(byte[] info, int ifstart, int reqd) {

		boolean registered = false;
		ArrayList starts = new ArrayList();
		int begin = ifstart;
		int end = reqd - 3;
		for (int k = begin; k <= end; k++) {
			if (checkForIf(k)) {
				starts.add(new Integer(k));
			}
		}
		
		
		if (starts.size() > 1) {
			Integer current[]= (Integer[])starts.toArray(new Integer[starts.size()]);
			int currentAsInt[] = new int[current.length];
			for(int r = 0; r < current.length;r++){
				currentAsInt[r] = current[r].intValue();
			}
				
			int updifs[] = checkForMethodInvocationNotPartOfChain(code, currentAsInt);
			starts = new ArrayList();
			for(int r = 0; r < updifs.length;r++){
				starts.add(new Integer(updifs[r]));
			}
			
			Integer[] temp = (Integer[]) starts.toArray(new Integer[starts
					.size()]);

			int itemp[] = new int[temp.length];
			for (int c = 0; c < temp.length; c++) {
				itemp[c] = temp[c].intValue();
			}
			itemp = checkForIINCCaseNotPartOfChain(info, itemp);
			int jumps[] = new int[itemp.length];
			for (int c = 0; c < itemp.length; c++) {
				jumps[c] = getJumpAddress(info, itemp[c]);
			}
			// here one check for itemp
			int pos = detectInvalidShortcutIf(itemp);
			if (pos == -1) {
				Shortcutchain chain = new Shortcutchain();
				chain.setJumpUsedToGetLast(reqd);
				chain.setBegin(begin);
				end = itemp[itemp.length - 1];
				chain.setLast(end);
				chain.setIfStarts(itemp);
				chain.setIfjumps(jumps);
				registered = true;
				allchains.add(chain);
				return chain;
			} else {
				if (pos == 0) {
					return null;
				} else {

					int newifs[] = new int[(pos + 1)];
					int newifjumps[] = new int[(pos + 1)];
					for (int p = 0; p <= pos; p++) {
						newifs[p] = itemp[p];
						newifjumps[p] = jumps[p];
					}
					Shortcutchain chain = new Shortcutchain();
					chain.setJumpUsedToGetLast(-1);// prev was reqd
					chain.setBegin(newifs[0]);
					end = newifs[newifs.length - 1];
					chain.setLast(end);
					chain.setIfStarts(newifs);
					chain.setIfjumps(newifjumps);
					registered = true;
					allchains.add(chain);
					return chain;

				}
			}
		}
		return null;
	}

	private int getReqdJump(int[] alljumps, byte[] code) {
		int reqd = -1;
		for (int x = 0; x < alljumps.length; x++) {

			int temp = alljumps[x] - 3;
			boolean b = checkForIf(temp);
			if (b) {
				reqd = alljumps[x];
				break;
			}

		}
		return reqd;
	}

	/** Returns in descending order* */
	private int[][] getAllIfJumpsInRange(byte[] code, int z, int jump) {
		int s = z;
		int ar[][] = new int[2][];
		ArrayList slist = new ArrayList();
		ArrayList list = new ArrayList();
		for (; s < jump; s++) {
			if (checkForIf(s)) {
				int j = getJumpAddress(code, s);
				list.add(new Integer(j));
				slist.add(new Integer(s));
			}
		}
		Integer alljumps[] = (Integer[]) list.toArray(new Integer[list.size()]);
		Integer allsts[] = (Integer[]) slist.toArray(new Integer[slist.size()]);
		Arrays.sort(alljumps);
		int jumparray[] = new int[alljumps.length];
		int sarray[] = new int[alljumps.length];
		int x = 0;
		for (int k = alljumps.length - 1; k >= 0; k--) {
			Integer temp = alljumps[k];
			jumparray[x] = temp.intValue();
			x++;
		}

		for (int k = 0; k < allsts.length; k++) {
			Integer temp = allsts[k];
			sarray[k] = temp.intValue();
		}
		ar[0] = jumparray;
		ar[1] = sarray;
		return ar;
	}

	public Map getConnecorMap() {
		return connectorMap;
	}

	public void setConnectorForIf(int ifstart) {

	}

	/***************************************************************************
	 * Util method to get the connector given ifstart Called from
	 * Decompiler.java
	 */

	public String getConnector(int currentIfStart) {

		Iterator it = connectorMap.entrySet().iterator();
		Shortcutstore store = null;
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			int start = ((Integer) entry.getKey()).intValue();
			if (start == currentIfStart) {
				store = (Shortcutstore) entry.getValue();
				break;
			}
		}
		if (store == null) {
			return this.NONE;
		}
		return store.getConnectortype();
	}

	public class Shortcutchain {

		int begin = -1;

		int last = -1;

		int jumpUsed = -1;

		int ifstarts[];

		int ifjumps[];

		boolean allAND = false;

		Shortcutchain prevChain = null;

		Shortcutchain nextChain = null;

		public void setNextChain(ShortcutAnalyser.Shortcutchain nextChain) {
			this.nextChain = nextChain;
		}

		public void setPrevChain(ShortcutAnalyser.Shortcutchain prevChain) {
			this.prevChain = prevChain;
		}

		public ShortcutAnalyser.Shortcutchain getPrevChain() {
			return prevChain;
		}

		public ShortcutAnalyser.Shortcutchain getNextChain() {
			return nextChain;
		}

		void setAllAnd(boolean b) {
			allAND = b;
		}

		public boolean isAllAND() {
			return allAND;
		}

		Shortcutchain() {

		}

		void setJumpUsedToGetLast(int j) {
			jumpUsed = j;
		}

		public int getJumpUsed() {
			return jumpUsed;
		}

		void setBegin(int begin) {
			this.begin = begin;
		}

		void setLast(int last) {
			this.last = last;
		}

		void setIfStarts(int ar[]) {
			ifstarts = ar;
		}

		public int getBegin() {
			return begin;
		}

		public int[] getIfstarts() {
			return ifstarts;
		}

		public int getLast() {
			return last;
		}

		public int[] getIfjumps() {
			return ifjumps;
		}

		public void setIfjumps(int[] ifjumps) {
			this.ifjumps = ifjumps;
		}

	}

	/**
	 * Class used to store information related to ifs and their connnectors
	 */
	public class Shortcutstore {

		int ifstart = -1;

		int ifbyend = -1;

		int nextifstart = -1;

		String connectortype = "";

		boolean useMe = false;

		// Pass true to make ineffective
		public void makeStoreInEffective(boolean b) {
			useMe = !b;
		}

		// useme shud be false
		public boolean isStoreInEffective() {
			return useMe == false;
		}

		public void setIfbyend(int ifbyend) {
			this.ifbyend = ifbyend;
		}

		public void setIfstart(int ifstart) {
			this.ifstart = ifstart;
		}

		public int getNextifstart() {
			return nextifstart;
		}

		public void setNextifstart(int nextifstart) {
			this.nextifstart = nextifstart;
		}

		public int getIfbyend() {
			return ifbyend;
		}

		public int getIfstart() {
			return ifstart;
		}

		public void setConnectortype(String connectortype) {
			this.connectortype = connectortype;
			useMe = true;
		}

		public String getConnectortype() {
			return connectortype;
		}

	}

	private boolean checkForIf(int pos) {
		boolean start = isThisInstrStart(starts, pos);
		if (start) {
			if (isInstructionIF(code[pos])) {
				return true;
			}
		}
		return false;
	}

	private boolean isThisInstrStart(ArrayList list, int pos) {
		boolean ok = false;
		if (list == null)
			throw new NullPointerException("Starts pos is null in disassembler");
		if (list != null) {
			for (int k = 0; k < list.size(); k++) {
				Integer in = (Integer) list.get(k);
				if (in != null) {
					int i = in.intValue();
					if (i == pos)
						return !ok;
				}
			}
		}
		return ok;

	}

	private boolean isInstructionIF(int instruction) {

		switch (instruction) {

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

	private int getJumpAddress(byte[] info, int counter) {

		int b1 = info[++counter];
		int b2 = info[++counter];
		int z;
		if (b1 < 0)
			b1 = (256 + b1);
		if (b2 < 0)
			b2 = (256 + b2);

		int indexInst = ((((b1 << 8) | b2)) + (counter - 2));
		if (indexInst > 65535)
			indexInst = indexInst - 65536;
		if (indexInst < 0)
			indexInst = 256 + indexInst;
		return indexInst;
	}

	private boolean connectChains(Shortcutchain chain1, Shortcutchain chain2) {

		int jump1[] = chain1.getIfjumps();
		// int ifs1[]=chain1.getIfstarts();
		int jump2[] = chain2.getIfjumps();
		for (int z = 0; z < jump1.length; z++) {
			int cur = jump1[z];
			// int thisif=ifs1[z];
			// int ifsbefore[]=getIfsInRange(0,thisif-1);
			for (int z2 = 0; z2 < jump2.length; z2++) {

				if (cur == jump2[z2]) {
					return true;
				}
			}
		}
		return false;
	}

	/***************************************************************************
	 * For testing
	 */
	public void testConnectors() {

		Iterator it = connectorMap.values().iterator();
		System.out.println("CONNECTOR STRINGS for " + connectorMap.size()
				+ " ifs");
		while (it.hasNext()) {

			Shortcutstore shortcutobj = (Shortcutstore) it.next();
			int start = shortcutobj.getIfstart();
			int byend = shortcutobj.getIfbyend();
			int nexts = shortcutobj.getNextifstart();
			String connector = shortcutobj.getConnectortype();
			testprint(start, byend, nexts, connector);
		}
	}

	private void testprint(int start, int byend, int nexts, String connector) {
		System.out.println("If(" + start + " to " + byend + " Connected to "
				+ nexts + " Connected by" + connector);
	}

	public void collectGroups() {

		for (int z = 0; z < allchains.size(); z++) {

			Shortcutchain chain = (Shortcutchain) allchains.get(z);
			if (!chain.isAllAND()) {

				int first = chain.getBegin();
				int end = chain.getLast();
				int fistJump = getJumpAddress(code, first);
				int s = 0;
				int ifs[] = chain.getIfstarts();
				while (s < ifs.length) {

					int curif = ifs[s];
					int curjump = getJumpAddress(code, curif);
					if ((curjump - 3) == end && s == 0) {
						// Skip9
					} else if ((curjump - 3) == end && s != 0) {
						{
							int gst = ifs[s];
							int gend = end;
							Group grppresent = pollForGroupEnd(gend);
							if (grppresent != null) {
								groups.remove(grppresent);
							}
							if (gend > gst)
								groups.add(new Group(gst, gend));
						}
					} else {
						int u = curjump - 3;
						boolean isif = false;
						if(u >=0 && u < code.length)
							isif=isInstructionIF(code[u]);
						//if(!isif)	break;
						boolean ok = true;
						if (s == 0) {
							if (end == u) {
								ok = false;
							}
						}
						if (ok && isif && isThisInstrStart(starts, u)) {

							int gst = ifs[s];
							int gend = u;
							Group grppresent = pollForGroupEnd(gend);
							if (grppresent != null) {
								groups.remove(grppresent);
							}
							if (gend > gst)
								groups.add(new Group(gst, gend));
						}

					}
					s++;
				}
			}
		}
		updateGroups();
	}

	private int[] getJumpBackIfRange(byte[] code, int jump, int start) {

		ArrayList list = new ArrayList();
		for (int k = start; k < code.length; k++) {
			if (checkForIf(k)) {
				if (getJumpAddress(code, k) == jump) {
					list.add(new Integer(k));
				} else {
					break;
				}
			}
		}
		Integer ifs[] = (Integer[]) list.toArray(new Integer[list.size()]);
		int reqd[] = new int[ifs.length];
		for (int k = 0; k < ifs.length; k++) {
			reqd[k] = ifs[k].intValue();
		}
		return reqd;
	}

	private Group pollForGroupEnd(int gend) {
		for (int z = 0; z < groups.size(); z++) {
			Group g = (Group) groups.get(z);
			if (g.end == gend) {
				return g;
			}

		}
		return null;
	}

	public boolean beginGroup(int x) {
		for (int z = 0; z < groups.size(); z++) {
			Group g = (Group) groups.get(z);
			if (g.start == x) {
				return true;
			}

		}
		return false;

	}

	public boolean endGroup(int x) {
		for (int z = 0; z < groups.size(); z++) {
			Group g = (Group) groups.get(z);
			if (g.end == x) {
				return true;
			}

		}
		return false;

	}

	class Group {

		private int start;

		private int end;

		Group(int s, int e) {
			start = s;
			end = e;
		}
	}

	public void collectLastIfs() {
		Iterator it = connectorMap.values().iterator();
		while (it.hasNext()) {

			Shortcutstore store = (Shortcutstore) it.next();
			int nextif = store.getNextifstart();
			Shortcutstore nextstore = getShortCutStoreGivenIfStart(nextif);
			if (nextstore == null) {
				lastIfs.add(new Integer(nextif));
			}
		}

	}

	public Shortcutstore getShortCutStoreGivenIfStart(int start) {
		Iterator it = connectorMap.values().iterator();
		while (it.hasNext()) {

			Shortcutstore store = (Shortcutstore) it.next();
			int ifs = store.getIfstart();
			if (ifs == start) {
				return store;
			}
		}
		return null;
	}

	/** Pass the ifstart to this method */
	public boolean isLastIfInChain(int i) {

		boolean b = false;
		for (Iterator it = lastIfs.iterator(); it.hasNext();) {
			Integer elem = (Integer) it.next();
			if (elem.intValue() == i) {
				b = true;
				break;
			}
		}
		Shortcutstore st = getShortCutStoreGivenIfStart(i);
		if (b)
			return b;
		if (!b && st != null && st.isStoreInEffective()) {
			return true;
		}
		return false;
	}

	/** Pass the ifstart to this method */
	public boolean isFirstIfInChain(int i) {

		boolean b = false;
		for (Iterator it = lastIfs.iterator(); it.hasNext();) {
			Integer elem = (Integer) it.next();
			if (elem.intValue() == i) {
				b = true;
				break;
			}
		}
		Shortcutstore st = getShortCutStoreGivenIfStart(i);
		if (b)
			return b;
		if (!b && st != null && st.isStoreInEffective()) {
			return true;
		}
		return false;
	}

	public boolean checkToReverseCondition(int ifstart) {

		String c = getConnector(ifstart);
		if (c.equals(NONE)) {

			for (int k = 0; k < allchains.size(); k++) {
				Shortcutchain sc = (Shortcutchain) allchains.get(k);
				int ifs[] = sc.getIfstarts();
				// boolean haveSameConnectors=checkForIdenticalConnectors(ifs);
				if (sc.getLast() == ifstart) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkForIdenticalConnectors(int[] ifs) {
		boolean identical = true;
		int first = ifs[0];
		String fc = getConnector(first);
		for (int i = 1; i < ifs.length - 1; i++) {
			int cur = ifs[i];
			if (getConnector(cur).equals(fc)) {

			} else {
				identical = false;
				break;
			}

		}
		return identical;
	}

	private void updateGroups() {

		ArrayList toRemove = new ArrayList();
		for (Iterator it = groups.iterator(); it.hasNext();) {
			Group g = (Group) it.next();
			int e = g.end;
			Group some = getGroupForBegin(e);
			if (some != null) {
				toRemove.add(some);
			}
		}

		for (int z = 0; z < toRemove.size(); z++) {
			groups.remove((Group) toRemove.get(z));
		}

	}

	private Group getGroupForBegin(int e) {

		for (int z = 0; z < groups.size(); z++) {
			Group g = (Group) groups.get(z);
			if (g.start == e) {
				return g;
			}

		}
		return null;
	}

	private boolean doesBooleanAssignFoundExist(int currentForIndex) {
		Set set = booleanAssignMap.entrySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			Map.Entry elem = (Map.Entry) it.next();
			Integer in1 = (Integer) elem.getKey();
			Integer in2 = (Integer) elem.getValue();
			if (currentForIndex >= in1.intValue()
					&& currentForIndex < in2.intValue()) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForShortCutIfBooleanAssignment(int ifbyend,
			byte[] info, StringBuffer end, StringBuffer msc, int current) {

		// int inital=ifbyend
		while (ifbyend < info.length) {
			if (isThisInstrStart(starts, ifbyend)
					&& info[ifbyend] == JvmOpCodes.ICONST_1) {
				int next = ifbyend + 1;
				if (isThisInstrStart(starts, next)
						&& info[next] == JvmOpCodes.GOTO) {
					next = next + 3;
					if (isThisInstrStart(starts, next)
							&& info[next] == JvmOpCodes.ICONST_0) {
						next = next + 1;
						StringBuffer sb = new StringBuffer("");

						boolean anyif = checkForIfInRange(info, current + 3,
								ifbyend, new StringBuffer(), starts);
						if (!anyif)
							return false;
						if (isThisInstrStart(starts, next)
								&& (isThisInstructionIStoreInst(info, next, sb)
										|| (info[next] == JvmOpCodes.PUTFIELD) || (info[next] == JvmOpCodes.PUTSTATIC))) {

							try {
								/*
								 * int i=Integer.parseInt(sb.toString());
								 * boolean complex=false; if(i!=0 && i!=1 &&
								 * i!=2 && i!=3) complex=true; LocalVariable
								 * local=getLocalVariable(i,"store","int",complex,next);
								 */
								// if(local!=null &&
								// local.getDataType().equalsIgnoreCase("boolean")
								// ) {
								end.append("" + next);
								skipWRTbooleanShortcutAssignFound
										.put(new Integer(ifbyend), new Integer(
												next));
								return true;
								// }
							} catch (Exception exp) {
								return false;
							}

						} else {
							if (isThisInstrStart(starts, next)
									&& (!isThisInstructionIStoreInst(info,
											next, sb)
											&& !(info[next] == JvmOpCodes.PUTFIELD) && !(info[next] == JvmOpCodes.PUTSTATIC))) {
								end.append("" + next);
								skipWRTbooleanShortcutAssignFound
										.put(new Integer(ifbyend), new Integer(
												next));
								msc.append("true");
								return true;
							}

							return false;
						}

					} else {
						return false;
					}

				} else {
					return false;
				}
			} else if (info[ifbyend] == JvmOpCodes.ICONST_0) {

				int prev = ifbyend - 3;
				if (isThisInstrStart(starts, prev)
						&& info[prev] == JvmOpCodes.GOTO) {
					prev = prev - 1;
					if (isThisInstrStart(starts, prev)
							&& info[prev] == JvmOpCodes.ICONST_1) {
						int from = prev;
						int next = ifbyend + 1;
						StringBuffer sb = new StringBuffer("");
						boolean anyif = checkForIfInRange(info, current + 3,
								prev, new StringBuffer(), starts);
						if (!anyif)
							return false;
						if (isThisInstrStart(starts, next)
								&& (isThisInstructionIStoreInst(info, next, sb)
										|| (info[next] == JvmOpCodes.PUTFIELD) || (info[next] == JvmOpCodes.PUTSTATIC))) {
							try {
								/*
								 * int i=Integer.parseInt(sb.toString());
								 * boolean complex=false; if(i!=0 && i!=1 &&
								 * i!=2 && i!=3) complex=true; LocalVariable
								 * local=getLocalVariable(i,"store","int",complex,next);
								 * //if(local!=null &&
								 * local.getDataType().equalsIgnoreCase("boolean") ) {
								 */
								skipWRTbooleanShortcutAssignFound.put(
										new Integer(from), new Integer(next));
								end.append("" + next);
								return true;
								// }
							} catch (Exception exp) {
								return false;
							}
						} else {
							if (isThisInstrStart(starts, next)
									&& (!isThisInstructionIStoreInst(info,
											next, sb)
											&& !(info[next] == JvmOpCodes.PUTFIELD) && !(info[next] == JvmOpCodes.PUTSTATIC))) {
								skipWRTbooleanShortcutAssignFound.put(
										new Integer(from), new Integer(next));
								end.append("" + next);
								msc.append("true");
								return true;
							}
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				int cur = ifbyend;
				boolean found = false;
				while (cur < info.length) {
					boolean isst = isThisInstrStart(starts, cur);
					if (isst) {
						boolean isif = isInstructionIF(info[cur]);
						if (isif) {
							ifbyend = getJumpAddress(info, cur);
							if (ifbyend < cur)
								return false; // to avoid infinite loop
							found = true;
							break;
						}
					}
					cur++;

				}
				if (found) {

				} else {
					return false;
				}
			}
		}
		return false;
	}

	private boolean checkForIfInRange(byte[] info, int from, int end,
			StringBuffer pos, ArrayList starts) {

		for (int z = from; z < end && z < info.length; z++) {
			boolean start = isThisInstrStart(starts, z);
			if (start) {
				boolean isif = isInstructionIF(info[z]);
				if (isif) {
					pos.append("" + z);
					return true;
				}

			}

		}
		return false;
	}

	private boolean isThisInstructionIStoreInst(byte[] code, int s,
			StringBuffer sb) {
		boolean b = false;
		if (isThisInstrStart(starts, s) == false)
			return false;
		switch (code[s]) {
		case JvmOpCodes.ISTORE_0:
			b = true;
			sb.append(0);
			break;
		case JvmOpCodes.ISTORE_1:
			b = true;
			sb.append(1);
			break;
		case JvmOpCodes.ISTORE_2:
			b = true;
			sb.append(2);
			break;
		case JvmOpCodes.ISTORE_3:
			b = true;
			sb.append(3);
			break;
		case JvmOpCodes.ISTORE:
			b = true;
			sb.append(code[(s + 1)]);
			break;
		}

		return b;
	}

	// j is some if start
	public boolean isIFShortcutIfCondition(int j) {
		return (isIfInShortuctChain(j) == null) ? false : true;
	}

	private Shortcutchain isIfInShortuctChain(int j) {

		for (int z = 0; z < allchains.size(); z++) {

			Shortcutchain chain = (Shortcutchain) allchains.get(z);
			int ifs[] = chain.getIfstarts();
			for (int m = 0; m < ifs.length; m++) {
				if (ifs[m] == j) {
					return chain;
				}
			}
		}
		return null;
	}

	public int getFirstIfInChain(int i) {

		Shortcutchain chain = isIfInShortuctChain(i);
		if (chain != null) {

			Shortcutchain prev = chain.getPrevChain();
			if (prev != null) {
				int f = -1;
				do {
					f = prev.getIfstarts()[0];
					prev = prev.getPrevChain();
				} while (prev != null);
				return f;
			} else {
				return chain.getIfstarts()[0];
			}
		} else
			return -1;

	}

	public Shortcutchain getShortcutChainGivenIf(int ifs) {

		return isIfInShortuctChain(ifs);
	}

	private int detectInvalidShortcutIf(int[] ifstarts) {

		for (int x = 0; x < ifstarts.length; x++) {

			if (x == ifstarts.length - 1)
				return -1;
			int first = ifstarts[x];
			int next = ifstarts[(x + 1)];
			boolean b = checkForLoopStartInRange(first + 3, next - 1);
			if (b) {
				if (x != 0) {
					ifsWithNoConnector.add(new Integer(first));
				}
				return x;
			}
			if (!b) {
				// GradientPaintContext;
				for (int from = first + 3; from < next; from++) {

					boolean isstart = isThisInstrStart(starts, from);
					if (isstart) {
						StringBuffer nextpos = new StringBuffer("");
						loadtype = "";
						storetype = "";
						loadindex = -1;
						storeindex = -1;
						boolean store = isInstructionStore(first, from,
								code[from], nextpos);
						if (store) {
							int nextat = Integer.parseInt(nextpos.toString());
							int bkp = from;
							int storepos = from;
							from = from + nextat;
							boolean load = isInstructionLoad(from, code[from]);
							if (load) {
								forloop: for (int t = first + 3; t < storepos; t++) {
									boolean s = isThisInstrStart(starts, t);
									if (!s)
										continue;
									loadindex = -1;
									loadtype = "";
									boolean someload = isInstructionLoad(t,
											code[t]);
									if (someload) {
										int nextinstpos = -1;
										if (loadindex != -1) {
											int np = t + 1;
											while (np < code.length) {
												boolean isbegin = isThisInstrStart(
														starts, np);
												if (isbegin) {
													break;
												}
												np++;
											}

											if (loadindex == storeindex
													&& loadtype
															.equals(storetype)) {

												if (code[np] == JvmOpCodes.DUP
														|| code[np] == JvmOpCodes.DUP2
														|| code[np] == JvmOpCodes.DUP2_X1
														|| code[np] == JvmOpCodes.DUP2_X2
														|| code[np] == JvmOpCodes.DUP_X1
														|| code[np] == JvmOpCodes.DUP_X2) {
													load = false;
													break forloop;
												}
											}
										}

									}
								}

							}
							if (load) {
								if (x != 0) {
									ifsWithNoConnector.add(new Integer(first));
								}
								return x;
							}
							from = bkp;

						}

					}

				}

			}

			// check for goto in range
			boolean gotop = checkForGotoInRange(first + 3, next - 1);
			if (gotop) {
				if (x != 0) {
					ifsWithNoConnector.add(new Integer(first));
				}
				return x;
			}

		}
		return -1;

	}

	private int detectInvalidShortcutIf_PREV(int[] starts) {

		for (int x = 0; x < starts.length; x++) {

			if (x == starts.length - 1)
				return -1;
			int first = starts[x];
			int next = starts[(x + 1)];
			ArrayList types = new ArrayList();
			ArrayList indexes = new ArrayList();
			ArrayList storeindexes = checkForStoreInRange(first, next, types,
					indexes);
			if (storeindexes != null) {
				ArrayList types2 = new ArrayList();
				ArrayList indexes2 = new ArrayList();
				int from = ((Integer) storeindexes.get(0)).intValue();
				ArrayList loadindexes = checkForLoadInRange(from, next, types2,
						indexes2);
				if (loadindexes != null) {

					for (int k = 0; k < loadindexes.size(); k++) {
						int i = ((Integer) loadindexes.get(k)).intValue();
						boolean b = isLoopStart(i);
						if (b) {
							String tp2 = (String) types2.get(k);
							String in2 = (String) indexes2.get(k);
							for (int h = 0; h < types.size(); h++) {
								String tp1 = (String) types.get(h);
								String in1 = (String) indexes.get(h);
								if (tp1.equals(tp2) && in1.equals(in2)) {

									int storeloc = ((Integer) storeindexes
											.get(h)).intValue();
									if (storeloc >= first && storeloc < i) {
										return x;
									}
								}

							}

						}
					}

				}
			}

		}

		return -1;

	}

	private boolean isLoopStart(int index) {
		ArrayList loops = minfo.getBehaviourLoops();
		for (int z = 0; z < loops.size(); z++) {
			Loop l = (Loop) loops.get(z);
			if (l.getStartIndex() == index) {
				return true;
			}
		}
		return false;
	}

	private ArrayList checkForLoadInRange(int first, int next, ArrayList types,
			ArrayList indexes) {

		for (int z = first; z <= next; z++) {

			if (isThisInstrStart(allstarts, z)) {
				switch (code[z]) {

				}
			}

		}
		return null;
	}

	private ArrayList checkForStoreInRange(int first, int next,
			ArrayList types, ArrayList indexes) {
		return null;
	}

	private boolean checkForLoopStartInRange(int i, int i0) {

		for (int k = i; k <= i0; k++) {
			boolean b = isLoopStart(k);
			if (b) {
				return true;
			}
		}
		return false;
	}

	private boolean isInstructionLoad(int pos, int instruction) {

		boolean flag = false;
		switch (instruction) {

		case JvmOpCodes.BIPUSH:
			flag = true;
			break;

		case JvmOpCodes.SIPUSH:
			flag = true;
			break;

		case JvmOpCodes.AALOAD:
			flag = true;
			loadindex = -9;
			break;

		case JvmOpCodes.BALOAD:
			flag = true;
			loadindex = -6;
			break;

		case JvmOpCodes.DALOAD:
			flag = true;
			loadindex = -5;
			break;

		case JvmOpCodes.FALOAD:
			flag = true;
			loadindex = -3;
			break;

		case JvmOpCodes.LALOAD:
			flag = true;
			loadindex = -4;
			break;

		case JvmOpCodes.IALOAD:
			flag = true;
			loadindex = -2;
			break;

		case JvmOpCodes.SALOAD:
			flag = true;
			loadindex = -8;
			break;

		case JvmOpCodes.CALOAD:
			flag = true;
			loadindex = -7;
			break;

		case JvmOpCodes.ALOAD:
			flag = true;
			loadtype = "object";
			loadindex = code[pos + 1];
			break;
		case JvmOpCodes.ALOAD_0:
			flag = true;
			loadtype = "object";
			loadindex = 0;
			break;
		case JvmOpCodes.ALOAD_1:
			flag = true;
			loadtype = "object";
			loadindex = 1;
			break;
		case JvmOpCodes.ALOAD_2:
			flag = true;
			loadtype = "object";
			loadindex = 2;
			break;
		case JvmOpCodes.ALOAD_3:
			flag = true;
			loadtype = "object";
			loadindex = 3;
			break;
		case JvmOpCodes.ILOAD:
			flag = true;
			loadtype = "int";
			loadindex = code[pos + 1];
			break;
		case JvmOpCodes.ILOAD_0:
			flag = true;
			loadtype = "int";
			loadindex = 0;
			break;
		case JvmOpCodes.ILOAD_1:
			flag = true;
			loadtype = "int";
			loadindex = 1;
			break;
		case JvmOpCodes.ILOAD_2:
			flag = true;
			loadtype = "int";
			loadindex = 2;
			break;
		case JvmOpCodes.ILOAD_3:
			flag = true;
			loadtype = "int";
			loadindex = 3;
			break;

		case JvmOpCodes.LLOAD:
			flag = true;
			loadtype = "long";
			loadindex = code[pos + 1];
			break;
		case JvmOpCodes.LLOAD_0:
			flag = true;
			loadtype = "long";
			loadindex = 0;
			break;
		case JvmOpCodes.LLOAD_1:
			flag = true;
			loadtype = "long";
			loadindex = 1;
			break;
		case JvmOpCodes.LLOAD_2:
			flag = true;
			loadtype = "long";
			loadindex = 2;
			break;
		case JvmOpCodes.LLOAD_3:
			flag = true;
			loadtype = "long";
			loadindex = 3;
			break;

		case JvmOpCodes.FLOAD:
			flag = true;
			loadtype = "float";
			loadindex = code[pos + 1];

			break;
		case JvmOpCodes.FLOAD_0:
			flag = true;
			loadtype = "float";
			loadindex = 0;
			break;
		case JvmOpCodes.FLOAD_1:
			flag = true;
			loadtype = "float";
			loadindex = 1;
			break;
		case JvmOpCodes.FLOAD_2:
			flag = true;
			loadtype = "float";
			loadindex = 2;
			break;
		case JvmOpCodes.FLOAD_3:
			flag = true;
			loadtype = "float";
			loadindex = 3;
			break;

		case JvmOpCodes.DLOAD:
			flag = true;
			loadtype = "double";
			loadindex = code[pos + 1];
			break;
		case JvmOpCodes.DLOAD_0:
			flag = true;
			loadtype = "double";
			loadindex = 0;
			break;
		case JvmOpCodes.DLOAD_1:
			flag = true;
			loadtype = "double";
			loadindex = 1;
			break;
		case JvmOpCodes.DLOAD_2:
			flag = true;
			loadtype = "double";
			loadindex = 2;
			break;
		case JvmOpCodes.DLOAD_3:
			flag = true;
			loadtype = "double";
			loadindex = 3;
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
			flag = true;
			break;

		case JvmOpCodes.GETFIELD:
		case JvmOpCodes.GETSTATIC:
			flag = true;
			loadindex = -10;
			break;

		}

		return flag;
	}

	private boolean isInstructionStore(int ifpos, int storepos,
			int instruction, StringBuffer npos) {
		boolean flag = false;

		switch (instruction) {
		/*
		 * case JvmOpCodes.AASTORE : flag = true; break;
		 */
		case JvmOpCodes.ASTORE:
			flag = true;
			npos.append("2");
			storeindex = code[storepos + 1];
			storetype = "object";
			break;

		case JvmOpCodes.ASTORE_0:
			flag = true;
			npos.append("1");
			storeindex = 0;
			storetype = "object";
			break;
		case JvmOpCodes.ASTORE_1:
			flag = true;
			npos.append("1");
			storeindex = 1;
			storetype = "object";
			break;
		case JvmOpCodes.ASTORE_2:
			npos.append("1");
			flag = true;
			storeindex = 2;
			storetype = "object";
			break;
		case JvmOpCodes.ASTORE_3:
			flag = true;
			npos.append("1");
			storeindex = 3;
			storetype = "object";
			break;

		case JvmOpCodes.DSTORE:
			flag = true;
			npos.append("2");
			storeindex = code[storepos + 1];
			storetype = "double";
			break;
		case JvmOpCodes.DSTORE_0:
			flag = true;
			npos.append("1");
			storeindex = 0;
			storetype = "double";
			break;
		case JvmOpCodes.DSTORE_1:
			flag = true;
			npos.append("1");
			storeindex = 1;
			storetype = "double";
			break;
		case JvmOpCodes.DSTORE_2:
			flag = true;
			npos.append("1");
			storeindex = 2;
			storetype = "double";
			break;
		case JvmOpCodes.DSTORE_3:
			flag = true;
			npos.append("1");
			storeindex = 3;
			storetype = "double";
			break;

		case JvmOpCodes.FSTORE:
			flag = true;
			npos.append("2");
			storeindex = code[storepos + 1];
			storetype = "float";
			break;
		case JvmOpCodes.FSTORE_0:
			flag = true;
			npos.append("1");
			storetype = "float";
			storeindex = 0;
			break;
		case JvmOpCodes.FSTORE_1:
			flag = true;
			npos.append("1");
			storeindex = 1;
			storetype = "float";
			break;
		case JvmOpCodes.FSTORE_2:
			flag = true;
			npos.append("1");
			storeindex = 2;
			storetype = "float";
			break;
		case JvmOpCodes.FSTORE_3:
			flag = true;
			npos.append("1");
			storeindex = 3;
			storetype = "float";
			break;

		case JvmOpCodes.ISTORE:
			flag = true;
			npos.append("2");
			storeindex = code[storepos + 1];
			storetype = "int";
			break;
		case JvmOpCodes.ISTORE_0:
			flag = true;
			npos.append("1");
			storeindex = 0;
			storetype = "int";
			break;
		case JvmOpCodes.ISTORE_1:
			flag = true;
			npos.append("1");
			storeindex = 1;
			storetype = "int";
			break;
		case JvmOpCodes.ISTORE_2:
			flag = true;
			npos.append("1");
			storeindex = 2;
			storetype = "int";
			break;
		case JvmOpCodes.ISTORE_3:
			flag = true;
			npos.append("1");
			storeindex = 3;
			storetype = "int";
			break;

		case JvmOpCodes.LSTORE:
			flag = true;
			npos.append("2");
			storeindex = code[storepos + 1];
			storetype = "long";
			break;

		case JvmOpCodes.LSTORE_0:
			flag = true;
			npos.append("1");
			storeindex = 0;
			storetype = "long";
			break;
		case JvmOpCodes.LSTORE_1:
			flag = true;
			npos.append("1");
			storeindex = 1;
			storetype = "long";
			break;
		case JvmOpCodes.LSTORE_2:
			flag = true;
			npos.append("1");
			storeindex = 2;
			storetype = "long";
			break;
		case JvmOpCodes.LSTORE_3:
			flag = true;
			npos.append("1");
			storeindex = 3;
			storetype = "long";
			break;

		case JvmOpCodes.IASTORE:
			flag = true;
			npos.append("1");
			storeindex = -2;
			break;
		case JvmOpCodes.FASTORE:
			flag = true;
			npos.append("1");
			storeindex = -3;
			break;
		case JvmOpCodes.LASTORE:
			flag = true;
			npos.append("1");
			storeindex = -4;
			break;
		case JvmOpCodes.DASTORE:
			flag = true;
			npos.append("1");
			storeindex = -5;
			break;
		case JvmOpCodes.BASTORE:
			flag = true;
			npos.append("1");
			storeindex = -6;
			break;
		case JvmOpCodes.CASTORE:
			flag = true;
			npos.append("1");
			storeindex = -7;
			break;
		case JvmOpCodes.SASTORE:
			flag = true;
			npos.append("1");
			storeindex = -8;
			break;

		case JvmOpCodes.AASTORE:
			flag = true;
			npos.append("1");
			storeindex = -9;
			break;

		case JvmOpCodes.PUTSTATIC:
		case JvmOpCodes.PUTFIELD:
			flag = true;
			npos.append("3");
			storeindex = -10;
			break;

		default:
			flag = false;
		}
		if (flag) {
			int prev = storepos - 1;
			if (prev >= 0
					&& (code[prev] == JvmOpCodes.DUP
							|| code[prev] == JvmOpCodes.DUP2
							|| code[prev] == JvmOpCodes.DUP2_X1
							|| code[prev] == JvmOpCodes.DUP2_X2
							|| code[prev] == JvmOpCodes.DUP_X1 || code[prev] == JvmOpCodes.DUP_X2)) {
				flag = false;
			}
		}

		return flag;
	}

	private boolean checkForGotoInRange(int i, int i0) {

		for (int x = i; x <= i0; x++) {
			boolean b = isThisInstrStart(starts, x);
			if (b) {

				if (code[x] == JvmOpCodes.GOTO) {

					return true;
				}

			}
		}
		return false;
	}

	/**
	 * TODO store.getNextIfStart was yeilding a lesser value than reqd . example
	 * shortcut.class
	 * 
	 * @param pos
	 * @return
	 */
	public int getChainEnd(int pos) {
		Shortcutstore store = getShortCutStoreGivenIfStart(pos);
		if (store == null)
			return -1;
		int reqd = -1;
		while (store != null) {
			int next = store.getNextifstart();
			if (reqd >= next)
				break;
			reqd = next;
			store = getShortCutStoreGivenIfStart(next);
		}
		return reqd;
	}

	private boolean checkToDisQualifyIf(int z, byte[] code) {

		for (int cur = z; cur < code.length; cur++) {
			boolean start = isThisInstrStart(starts, cur);
			if (!start)
				continue;
			boolean isif = isInstructionIF(code[cur]);
			if (isif)
				return false;
			switch (code[cur]) {
			case JvmOpCodes.LOOKUPSWITCH:
			case JvmOpCodes.TABLESWITCH:
			case JvmOpCodes.RET:
			case JvmOpCodes.RETURN:
			case JvmOpCodes.MONITORENTER:
			case JvmOpCodes.MONITOREXIT:
			case JvmOpCodes.JSR:
			case JvmOpCodes.JSR_W:
				return true;
			}
		}
		return false;
	}

	public ArrayList getAllchains() {
		return allchains;
	}

}
