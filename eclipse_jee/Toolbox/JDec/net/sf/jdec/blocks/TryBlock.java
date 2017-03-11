/*
 *  TryBlock.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.blocks;
import java.util.ArrayList;

import net.sf.jdec.constantpool.MethodInfo.ExceptionTable;
import net.sf.jdec.constantpool.MethodInfo;


public class TryBlock implements TryCatchFinally {

	private final java.lang.String type="try";
	private int numberOfCatchBlks=-1;
	private boolean hasCatchBlk=false;
	private boolean hasFinallyBlk=false;
	private TryBlock parentTry=null;
	private TryBlock[] tryKids=null;
	private TryCatchFinally nextBlock=null;
	private int start=-1;
	private int end=-1;
	private ArrayList allCatchesForThisTry=new ArrayList();
	private FinallyBlock finallyBlock=null;
	private ExceptionTable tableUsedToCreateTry=null;

    public MethodInfo.ExceptionTable getBiggestTableUsedToCreateTry() {
        return biggestTableUsedToCreateTry;
    }

    public void setBiggestTableUsedToCreateTry(MethodInfo.ExceptionTable biggestTableUsedToCreateTry) {
        this.biggestTableUsedToCreateTry = biggestTableUsedToCreateTry;
    }

    private ExceptionTable biggestTableUsedToCreateTry=null;

	public ExceptionTable getTableUsedToCreateTry() {
		return tableUsedToCreateTry;
	}

	public void setTableUsedToCreateTry(ExceptionTable tableUsedToCreateTry) {
		this.tableUsedToCreateTry = tableUsedToCreateTry;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public TryBlock() {
		super();
	}

	public String getType() {

		return type;
	}

	public boolean hasCatchBlk() {
		return hasCatchBlk;
	}

	public void setHasCatchBlk(boolean hasCatchBlk) {
		this.hasCatchBlk = hasCatchBlk;
	}

	public boolean hasFinallyBlk() {
		return hasFinallyBlk;
	}

	public void setHasFinallyBlk(boolean hasFinallyBlk) {
		this.hasFinallyBlk = hasFinallyBlk;
	}

	public int getNumberOfCatchBlks() {
		return numberOfCatchBlks;
	}

	public void setNumberOfCatchBlks(int numberOfCatchBlks) {
		this.numberOfCatchBlks = numberOfCatchBlks;
	}

	public TryBlock getParentTry() {
		return parentTry;
	}

	public void setParentTry(TryBlock parentTry) {
		this.parentTry = parentTry;
	}

	public TryBlock[] getTryKids() {
		return tryKids;
	}

	public void setTryKids(TryBlock[] tryKids) {
		this.tryKids = tryKids;
	}

	public TryCatchFinally getNextBlock() {
		return nextBlock;
	}

	public void setNextBlock(TryCatchFinally nextBlock) {
		this.nextBlock = nextBlock;
	}

	public void addCatchBlk(CatchBlock Catch)
	{
		allCatchesForThisTry.add(Catch);
	}

	public ArrayList getAllCatchesForThisTry() {
		return allCatchesForThisTry;
	}

	public FinallyBlock getFinallyBlock() {
		return finallyBlock;
	}

	public void setFinallyBlock(FinallyBlock finallyBlock) {
		this.finallyBlock = finallyBlock;
	}

    public CatchBlock getLastCatchBlock()
    {
    	CatchBlock catchblock=null;
    	if(this.getAllCatchesForThisTry().size()==0)return null;
    	else
    	{
    		return (CatchBlock)this.getAllCatchesForThisTry().get(this.getAllCatchesForThisTry().size()-1);
    	}
    }

}
