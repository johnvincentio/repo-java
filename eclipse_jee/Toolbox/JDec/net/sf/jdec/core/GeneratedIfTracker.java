package net.sf.jdec.core;

import net.sf.jdec.util.ExecutionState;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.blocks.*;
import net.sf.jdec.exceptions.ApplicationException;

import java.util.*;
/*
 *  GeneratedIfTracker.java Copyright (c) 2006,07 Swaroop Belur
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
public class GeneratedIfTracker {

    private Map ifs = new HashMap();

    private static final int PROCESSED  = 1;

    private static final int NOT_PROCESSED  = 0;

    public class IfStore {

        private int start;

        private int end;

        private String name;
        
        private boolean invalid = false;
        
        private IFBlock ifb;

        IfStore(IFBlock ifb ,int s, int e,String name){
        	this.ifb = ifb;
            start = s;
            end = Helper.checkIfEnd(s,e);
            this.name = name;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IfStore ifStore = (IfStore) o;

            if (end != ifStore.end) return false;
            if (start != ifStore.start) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = start;
            result = 31 * result + end;
            return result;
        }

		public boolean isInvalid() {
			return invalid;
		}

		public void setInvalid(boolean invalid) {
			this.invalid = invalid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
        
        
    }


    public void add(IFBlock ifb ,int start, int end , String name){
        ifs.put(new IfStore(ifb,start,end , name) , new Integer(NOT_PROCESSED));
    }

    public String startsAtIndex(int i){

        Iterator iterator = ifs.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer temp = (Integer)entry.getValue();
            if(temp != null && temp.intValue() == NOT_PROCESSED){
                IfStore aif = (IfStore)entry.getKey();
                if(aif.start == i){
                	boolean hasElse = aif.ifb.hasMatchingElseBeenGenerated();
                	IFBlock parentWithElse = null;
                	if(!hasElse){
                	   parentWithElse = doesAnyParentIfHaveElse(aif.ifb.getIfStart());
                	   if(parentWithElse != null)
                		   hasElse = true;
                	}
                	if(hasElse){
                		int elseclose = aif.ifb.getElseCloseLineNumber();
                		if(parentWithElse != null){
                			elseclose = parentWithElse.getElseCloseLineNumber();
                		}
                		if(elseclose >= i && aif.ifb.getIfCloseLineNumber() < i){
                			ifs.put(aif,new Integer(PROCESSED));
                			aif.invalid = true;
                			return null;
                		}
                	}
                	return aif.name;
                }
            }
        }
        return null;
    }


    private IFBlock doesAnyParentIfHaveElse(int ifStart) {
    	Set ifs = ExecutionState.getMethodContext().getMethodIfs();
    	ArrayList starts = ExecutionState.getMethodContext().getInstructionStartPositions();
    	byte[] code = ExecutionState.getMethodContext().getCode();
    	for(int from = ifStart-1;from >=0 ;from--){
    		if(isThisInstrStart(from,starts,code)){
    			
    			if(isInstructionIF(from,code)){
    					
    				IFBlock  ifb = IFHelper.getIfGivenStart(from);
    				if(ifb != null && ifb.hasMatchingElseBeenGenerated()){
    					return ifb;
    				}
    			}
    		}
    		
    	}
    	return null;
	}
    
    /***
     * IMPORTANT : does not check for start of instruction
     * @param pos
     * @param code
     * @return
     */
    public boolean isInstructionIF(int pos , byte[] code) {

	
		switch (code[pos]) {

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

    public boolean isThisInstrStart(int pos,ArrayList starts , byte[] code) {
		ArrayList list = starts;
		boolean start = false;
		
		if (list != null) {
			for (int k = 0; k < list.size(); k++) {
				Integer in = (Integer) list.get(k);
				if (in != null) {
					int i = in.intValue();
					if (i == pos) {
						return !start;
					}
				}
			}
		}
		return start;

	}

    
    
	public boolean closesAtIndex(int i){

        Iterator iterator = ifs.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer temp = (Integer)entry.getValue();
            if(temp != null && temp.intValue() == NOT_PROCESSED){
                IfStore aif = (IfStore)entry.getKey();
                if(aif.end == i){
                    ifs.put(aif,new Integer(PROCESSED));
                    return true;
                }
            }
        }
        return false;
    }


    public int getCountOfCloseIfsAt(int i){
        int count=0;
        Iterator iterator = ifs.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer temp = (Integer)entry.getValue();
            if(temp != null && temp.intValue() == NOT_PROCESSED){
                IfStore aif = (IfStore)entry.getKey();
                if(aif.end == i){
                    ifs.put(aif,new Integer(PROCESSED));
                    count++;
                }
            }
        }
        return count;
        
        
        
    }




    private static class Helper {

        public static int checkIfEnd(int start,int end){
            Behaviour behaviour = ExecutionState.getMethodContext();
            List tries=behaviour.getAllTriesForMethod();
            end = checkWRTTryBlocks(start,end,tries);
            Set ifset = behaviour.getMethodIfs();
            if(ifset != null)
                end = checkWRTIfBlocks(start,end,new ArrayList(ifset));
            List loops= behaviour.getBehaviourLoops();
            end = checkWRTLoops(start,end,loops);
            return end;
        }

        private static int checkWRTLoops(int start,int end , List loops){
            if(loops == null)return end;
            for(int i=0;i<loops.size();i++){
                Loop l = (Loop)loops.get(i);
                int ls = l.getStartIndex();
                int le = l.getEndIndex();
                if(start > ls && start < le){
                    if(le < end){
                        end = le;
                    }
                }
            }
            return end;
        }




        private static int checkWRTIfBlocks(int start,int end , List methodifs){

            for(int i=0;i<methodifs.size();i++){
                IFBlock ifb = (IFBlock)methodifs.get(i);
                int ifbs = ifb.getIfStart();
                int ifbe = ifb.getIfCloseLineNumber();
                if(start > ifbs && start < ifbe){
                    if(ifbe < end){
                        end = ifbe;
                    }
                }
            }
            return end;
        }


        private static int checkWRTTryBlocks(int start,int end, List tries){
            if(tries == null)return end;
            for(int i=0;i<tries.size();i++){
                TryBlock tryb = (TryBlock)tries.get(i);
                int trye =tryb.getEnd();
                int trys =tryb.getStart();
                if(trys < start && start <trye){
                    if(trye < end){
                        end = trye;
                    }
                }
                List catches = tryb.getAllCatchesForThisTry();
                for(int j=0;j<catches.size();j++){
                    CatchBlock catchb = (CatchBlock)catches.get(j);
                    int catchbe =catchb.getEnd();
                    int catchbs =catchb.getStart();
                    if(catchbs < start && start <catchbe){
                        if(catchbe < end){
                            end = catchbe;
                        }
                    }
                }

                FinallyBlock fblock = tryb.getFinallyBlock();
                if(fblock != null){
                    int fblocke =fblock.getEnd();
                    int fblocks =fblock.getStart();
                    if(fblocks < start && start <fblocke){
                        if(fblocke < end){
                            end = fblocke;
                        }
                    }
                }


            }

            return end;

        }


    }


	public Map getIfs() {
		return ifs;
	}

	public void setIfs(Map ifs) {
		this.ifs = ifs;
	}



}
