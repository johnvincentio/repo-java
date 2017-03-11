
/*
 *  CatchBlock.java Copyright (c) 2006,07 Swaroop Belur 
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

/***
 * Represents a catch block handler of one try/catch block in code 
 * @author sbelur
 *
 */
public class CatchBlock implements TryCatchFinally {

    private boolean useToFindLastCatchEnd=true;
    private final java.lang.String type="catch";
	private TryBlock associatedTry=null;
	private int start=-1;
	private int end=-1;
	private TryCatchFinally nextBlock=null;

    public void useMeForDeterminingTheEndOfLastCatch(boolean use)
    {
        this.useToFindLastCatchEnd=use;
    }

    public boolean isUsedForDeterminingTheEndOfLastCatch()
    {
        return useToFindLastCatchEnd;
    }
    public TryCatchFinally getNextBlock() {
		return nextBlock;
	}

	public void setNextBlock(TryCatchFinally nextBlock) {
		this.nextBlock = nextBlock;
	}

	public CatchBlock() {
		super();
		
	}

	public String getType() {
		
		return type;
	}

	public TryBlock getAssociatedTry()
	{
		return associatedTry;
		
	}

	public void setAssociatedTry(TryBlock associatedTry) {
		this.associatedTry = associatedTry;
	}
	
	public void setMyStart(int pos)
	{
		start=pos;
	}
	public int getStart()
	{
		return start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
}
