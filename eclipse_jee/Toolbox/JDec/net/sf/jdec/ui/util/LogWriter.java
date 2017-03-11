
/*
 *  LogWriter.java Copyright (c) 2006,07 Swaroop Belur 
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

package net.sf.jdec.ui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import net.sf.jdec.ui.main.UILauncher;



public class LogWriter {

    static PrintWriter pw;
    static LogWriter uilog=null;

    public PrintWriter getPrintWriter(){return pw;}


    private LogWriter(String s) throws Exception
    {
    	
       boolean isValid=checkFile(s);
     //  System.out.println(isValid);
       if(isValid)
       {
        pw=new PrintWriter(new FileOutputStream(s),false);
        logfilepath=s;
       }
       else
       {
         s=System.getProperty("user.dir")+File.separator+"JDecuilog.txt";
           logfilepath=s;
         pw=new PrintWriter(new FileOutputStream(s),true);
       }

    }
    static UIUtil util;
    public static LogWriter getInstance() 
    {
    	
        try {
			if(uilog==null || pw==null){
			    util=UILauncher.getUIutil();
			   
			    uilog= new LogWriter(util.getUilogfile());
			    return uilog;
			}
			else
			    return uilog;
		} catch (Exception e) {
			
			return null;
		}
    }

    public void flush()
    {
        if(pw!=null)
           {

             pw.flush();
        }
    }
    public void writeLog(String s)
    {
        if(pw!=null)
        {
            pw.write(s);
        //    pw.flush();
        }
    }
    
    
    public void writeLog(String s,String mode)
    {
    	 if(mode.trim().equals("2"))
    	 {
    	 	s=s+"\nTime: "+new Date(System.currentTimeMillis()).toString();
    	 	s+="\n\n";
			writeLog(s);
    	 }
    	
    }
    

    public void close()
    {
        if(pw!=null)
        {
            pw.flush();
            pw.close();
            pw=null;
        }




    }


    private boolean checkFile(String s)
    {
        File f=new File(s);
        if(f.exists())return true;
        else
        {
            try
            {
                f.createNewFile();
                return true;
            }
            catch(IOException ioe)
            {
                return false;
            }
        }
    }

    public String logFilePath()
    {
        return logfilepath;
    }

    private String logfilepath=null;


}
