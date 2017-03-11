/*
 * AllExceptionHandler.java Copyright (c) 2006,07 Swaroop Belur 
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
package net.sf.jdec.util;

import java.io.IOException;

import net.sf.jdec.io.Writer;
import net.sf.jdec.exceptions.ApplicationException;
import net.sf.jdec.reflection.Behaviour;



public class AllExceptionHandler {


	Throwable t=null;

  private int codePos=-1;
  public void setCodePosition(int i)
  {
     codePos=i;
  }

    public Behaviour getB() {
        return b;
    }

    private Behaviour b=null;

    public void setBehaviour( Behaviour b)
    {
        this.b=b;
    }
	/**
	 *
	 */
	public AllExceptionHandler(Throwable t) {
		super();
		this.t=t;
	}

    public AllExceptionHandler(java.lang.String  msg) {
		super();
		this.msg=msg;
	}

    private java.lang.String msg="";

    public AllExceptionHandler(Throwable t,java.lang.String extraMessage) {
		super();
		this.t=t;
        anyMessage=extraMessage;
	}


    private java.lang.String anyMessage=null;



    public void sendMessage()
    {
        java.lang.String s=msg;
        Writer projectwriter=null;
        try
        {
            java.io.PrintWriter writer=Writer.getWriter("log");
            projectwriter=(Writer)writer;
            projectwriter.writeLog(s);
            projectwriter.flush();

        }
        catch(IOException ioe)
        {
            throw new ApplicationException(new Throwable(ioe));
        }


    }

	public void reportException()
	{
		if(t!=null)
		{

			StringBuffer exceptionContent=null;
			String temp="";
			temp+="\n\n[ERROR] Exception Report produced by Exception Handler... \n\n";
            temp+=anyMessage+"\n\n";
            if(b!=null)
            {
             temp+="[ERROR]\tFaulty Method:\t"+b.getShortDescription()+"\n";
             temp+="[ERROR]\tBytecode position:\t"+codePos+"\n";   
            }
			temp+="[ERROR]\tCAUSE:\t"+t.getCause()+"\n";
			temp+="[ERROR]\tMESSAGE:\t"+t.getMessage()+"\n";
			temp+="[ERROR]\tCLASS:\t"+t.getClass()+"\n\n";

			temp+="---EXCEPTION STACK TRACE---\n";

			exceptionContent=new StringBuffer(temp);


			Writer projectwriter=null;
			try
			{
				java.io.PrintWriter writer=Writer.getWriter("log");
				projectwriter=(Writer)writer;
				projectwriter.writeLog(exceptionContent.toString());
				t.printStackTrace(writer);
				projectwriter.flush();
			}
			catch(IOException ioe)
			{
				throw new ApplicationException(new Throwable(ioe));
			}
			finally
			{
				temp=null;
				exceptionContent=null;
				//Close Log writer objects...
				//projectwriter.close("log");


			}


		}
		else
		{
			throw new ApplicationException("Exception Handler invoked without Registering any Throwable Object...");
		}


	}




}
