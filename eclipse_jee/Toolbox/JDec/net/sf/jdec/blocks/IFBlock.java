/*
 * IFBlock.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;


public class IFBlock implements Comparable{
	private boolean hasIfBeenGenerated;
	private boolean hasMatchingElseBeenGenerated;
	private int ifCloseLineNumber;
	private int elseCloseLineNumber;
    private int startOfIf=-1;
    private boolean elsebreakadded = false;
    private boolean elsebreakinvalid = false;



    public boolean getDonotclose() {
        return donotclose;
    }

    public void setDonotclose(boolean donotclose) {
        this.donotclose = donotclose;
    }

    private boolean donotclose=false;

    public boolean IfHasBeenClosed() {
        return ifHasBeenClosed;
    }

    public void setIfHasBeenClosed(boolean ifHasBeenClosed) {
        this.ifHasBeenClosed = ifHasBeenClosed;
    }

    private boolean ifHasBeenClosed=false;
    private boolean elseHasBeenClosed=false;

    public int getIfCloseFromByteCode() {
        return ifCloseFromByteCode;
    }

    public void setIfCloseFromByteCode(int ifCloseFromByteCode) {
        this.ifCloseFromByteCode = ifCloseFromByteCode;
    }

    private int ifCloseFromByteCode;








    public boolean isHasElse() {
        return hasElse;
    }

    public void setHasElse(boolean hasElse) {
        this.hasElse = hasElse;
    }

    private boolean hasElse=false;





    public void setIfStart(int start)
    {
        startOfIf=start;
        setIFEndFromByteCode(startOfIf);
    }

    public int getIfStart()
    {
        return startOfIf;
    }

	public IFBlock() {
		ifCloseLineNumber = -1;
		elseCloseLineNumber = -1;
	}
	public int getElseCloseLineNumber() {
		return elseCloseLineNumber;
	}
	public void setElseCloseLineNumber(int elseCloseLineNumber) {
		this.elseCloseLineNumber = elseCloseLineNumber;
	}
	public boolean hasIfBeenGenerated() {
		return hasIfBeenGenerated;
	}
	public void setHasIfBeenGenerated(boolean hasIfBeenGenerated) {
		this.hasIfBeenGenerated = hasIfBeenGenerated;
	}
	public boolean hasMatchingElseBeenGenerated() {
		return hasMatchingElseBeenGenerated;
	}
	public void setHasMatchingElseBeenGenerated(boolean hasMatchingElseBeenGenerated) {
		this.hasMatchingElseBeenGenerated = hasMatchingElseBeenGenerated;
	}
	public int getIfCloseLineNumber() {
		return ifCloseLineNumber;
	}
	public void setIfCloseLineNumber(int ifCloseLineNumber) {
		this.ifCloseLineNumber = ifCloseLineNumber;
	}

    public int compareTo(Object o)
    {
     if(o instanceof IFBlock)
     {
         IFBlock IF=(IFBlock)o;
         int start=IF.getIfStart();
         if(this.startOfIf > start)return 1;
         else if(this.startOfIf < start)return -1;
         else return 0;
     }
     else
     {
         return -1;
     }
    }


    private void setIFEndFromByteCode(int startOfIf)
    {
        Behaviour b= ConsoleLauncher.getCurrentMethodBeingExecuted();
        if(b!=null)
        {
            byte[] code=b.getCode();
            int jump=getJumpAddress(code,startOfIf);
            ifCloseFromByteCode=jump;
        }
    }

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

	public boolean elseHasBeenClosed() {
		return elseHasBeenClosed;
	}

	public void setElseHasBeenClosed(boolean elseHasBeenClosed) {
		this.elseHasBeenClosed = elseHasBeenClosed;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + startOfIf;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IFBlock other = (IFBlock) obj;
		if (startOfIf != other.startOfIf)
			return false;
		return true;
	}

	public boolean isElsebreakadded() {
		return elsebreakadded;
	}

	public void setElsebreakadded(boolean elsebreakadded) {
		this.elsebreakadded = elsebreakadded;
	}

	public boolean isElsebreakinvalid() {
		return elsebreakinvalid;
	}

	public void setElsebreakinvalid(boolean elsebreakinvalid) {
		this.elsebreakinvalid = elsebreakinvalid;
	}
	
	

}
