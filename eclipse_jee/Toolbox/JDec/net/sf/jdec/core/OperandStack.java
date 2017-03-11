
/*
 *  OperandStack.java Copyright (c) 2006,07 Swaroop Belur 
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

package net.sf.jdec.core;

import java.util.EmptyStackException;
import java.util.Stack;

import net.sf.jdec.exceptions.StackOperationException;
import net.sf.jdec.util.AllExceptionHandler;


public class OperandStack extends Stack {

	public OperandStack() {
		super();
	}
	
	/***
	 * Below are methods which may be useful 
	 * while doing some Error checking 
	 * Actually Not Needed directly as the variable
	 * can be accessed....BUT Added Them
	 * Since it is extending Stack Class and not
	 * doing anything else
	 * @return
	 */
	
	public int getEntryCount()
	{
		return this.elementCount;
	}
	
	public Operand getTopOfStack() 
	{
		Operand topofstack=(Operand)this.pop();
		return topofstack;
	}
	
	public Operand peekTopOfStack()
	{
        if(this.isEmpty()==false)
        {
		Operand topofstack=(Operand)this.peek();
		return topofstack;
        }
        return null;
	}
	
	public Integer getStackTopType()
	{
		Operand topofstack=(Operand)this.peek();
		return new Integer(topofstack.getOperandType());
	}
	
	/**
	 * Just A error or Debug method ...Can be used in case of ClassCastException
	 * The Advantage of This method is that it will set the position of the First 
	 * corrupt Object in the stack...(If there are many , it will set the first)
	 * Usage 
	 * 1> Call checkStackSanity
	 * 2>Then Either
	 *  Call getWrongClassEntryPos For That operandStack(this class) Object
	 * Or
	 *  Call getCorruptObject
	 *  
	 * @return
	 */
	
	
	public boolean checkStackSanity()
	{
	 boolean ok=true;	
	 int count=this.getEntryCount();
	 for(int i=0;i<count;i++)
	 {
		Object o=this.peek();
		if(o instanceof Operand)
		{
			ok=true;continue;
		}
		else
		{
			ok=false;
			setCorruptEntryPosition(i);
			break;
		}
	 }
	 return ok;
		
	}
	
	private void setCorruptEntryPosition(int i)
	{
		wrongClassEntryPos=i;
	}

	private int wrongClassEntryPos=-1;

	
	/***
	 * 
	 * @return position in stack of First corrupt object
	 * If the user desires the object itself Then
	 *  See getCorruptObject() Method
	 * 
	 */
	
	
	public int getWrongClassEntryPos() {
		return wrongClassEntryPos;
	}
	
	
	/***
	 *  
	 * @return Either null or the corruptObject
	 * NOTE: This method returns null in case the 
	 * user calls this method before calling
	 * checkStackSanity and ClassCastException
	 * has occurred OR There is no corrupt 
	 * object in stack.
	 */
	
	public Object getCorruptObject()
	{
		if(wrongClassEntryPos!=-1)
		{
			Object corruptObj=this.get(wrongClassEntryPos);
			return corruptObj;
		}
		else
		{
			return null; 
		}
		
	}
	
	
	
	public Object push(Object value)
	{
		
		try
		{
			if(value==null)
			{
				throw new StackOperationException("Error while Pushing operand into Stack...\nnull Object Passed while trying to push operand to stack");
			}
			else if(!(value instanceof Operand))
			{
				throw new StackOperationException("Error while Pushing operand into Stack... \nObject Passed is not type of Operand Class...."); 
			}
			
			else // OK
			{
				Operand newtopofstack=(Operand)value;
				return super.push(newtopofstack);
			}
			
		}		
		catch(StackOperationException stoe)
		{
			AllExceptionHandler handler=new AllExceptionHandler(stoe);
			handler.reportException();
			return null;
			
		}
		
	}
	
	/***
	 * What was i thinking here..? can be removed i guess..
	 * @return
	 */
	public boolean emptyMe()
	{
		try
		{
			for(;;)
			{
				Object temp=this.getTopOfStack();
			}
			
		}
		catch(EmptyStackException ese)
		{
			// OK
			return (this.getEntryCount()==0);  // Should Be True 
		}
		
	}
	
	
}
