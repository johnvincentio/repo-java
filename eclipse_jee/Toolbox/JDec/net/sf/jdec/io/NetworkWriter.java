/*
 *  NetworkWriter.java Copyright (c) 2006,07 Swaroop Belur 
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
package net.sf.jdec.io;

import java.io.IOException;
import java.net.Socket;

import net.sf.jdec.exceptions.NetworkException;


/***
 * TODO: Seems to be useless idea. Remove this class in next release.
 * @author sbelur
 *
 */
public class NetworkWriter extends Writer {

	
	/* (non-Javadoc)
	 * @see net.sf.jdec.io.Writer#writeOutput(java.lang.String, java.lang.String)
	 */
	public void writeOutput(String data, String type) throws IOException {
		

	}

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	public void close()  {
		

	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	public void flush() {
		

	}
	
	/***
	 * Placeholder for writing to network.....
	 * Need to Check How Exactly to do it...
	 * @param host
	 * @param port
	 * @throws NetworkException
	 */
	public NetworkWriter(String host,int port) 
	{
		// DUMMY CALL
		super(null);	
		try
		{
		Socket socket=new Socket(host,port);
		}
		catch(Exception e )
		{
			
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see net.sf.jdec.io.Writer#writeOutput(java.lang.String)
	 */
	public void writeOutput(String data) throws IOException {
		
		
	}

	/* (non-Javadoc)
	 * @see net.sf.jdec.io.Writer#writeLog(java.lang.String)
	 */
	public void writeLog(String data) throws IOException {
		
		
	}
	public void writeLog(String data,java.lang.String level) throws java.io.IOException
	{
		//java.lang.String logLevel=Configuration.getLogLevel();
		if(level.equals("2"))  // 2 is for more/extra output to log
		{					  // 1 is default
			logwriter.write(data);
		}
	}

}
