/*
 *  LogFileWriter.java Copyright (c) 2006,07 Swaroop Belur
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;


public class LogFileWriter extends FileWriter {
    
    private static LogFileWriter logfilewriter=null;
    
    private static String filepath=null;
    
    private LogFileWriter(java.io.Writer writer) throws IOException {
        super(writer);
    }
    
    public static LogFileWriter getLogFileWriter(String path) throws IOException {
        if(logfilewriter==null) {
           if(new java.io.File(path).exists()==false){
              //path=System.getProperty("user.dir")+File.separator + "jdeclog.txt";
        	   new File(path).createNewFile();
            }
            logwriter = new java.io.PrintWriter(new BufferedWriter(new java.io.FileWriter(new java.io.File(path))));
            logfilewriter=new LogFileWriter(logwriter);
            filepath = path;
        }
        else{
        	if(path != null && !path.equals(filepath)){
        		 if(new java.io.File(path).exists()==false){
                     //path=System.getProperty("user.dir")+File.separator + "jdeclog.txt";
               	   new File(path).createNewFile();
                   }
                   logwriter = new java.io.PrintWriter(new BufferedWriter(new java.io.FileWriter(new java.io.File(path))));
                   logfilewriter=new LogFileWriter(logwriter);
                   filepath = path;
        	}
        }
        return logfilewriter;
        
    }
    
    public void writeLog(String data) throws java.io.IOException {
        logwriter.write(data);
    }
    
    public void writeLog(String data,java.lang.String level) throws java.io.IOException {
        //java.lang.String logLevel=Configuration.getLogLevel();
        if(level.equals("2"))  // 2 is for more/extra output to log
        {					  // 1 is default
            logwriter.write("\n"+data+"\n");
        }
    }
    
    /**
     * Need to call This after calling writeLog method of this class
     */
    public void flush() {
        try {
            logwriter.flush();
        } catch(IOException ioe) {
            // Leave Blank
        }
    }
    
    /**
     * NOTE:
     * This method Nullifies the Reference to the
     * encapsualating writer object...
     * Calling any write method using this Object reference will
     * throw a @see java.lang.NullPointerException ; even
     * though The reference to the LogFileWriter Or the
     * FileWriter Object may not be null at that time.
     *
     */
    
    public void close() {
        try {
            if(logwriter!=null) {
                logwriter.flush();
                logwriter.close();
            }
            logwriter=null;
        } catch(IOException ioe) {
            // Leave Blank
        }
    }
    
        /* **
         *
         * Writes The Data using the underlying writer...
         */
        /*public void write(char[] arg0, int arg1, int arg2)  {
         
                try
                {
                        if(logwriter!=null)
                                logwriter.write(arg0,arg1,arg2);
                        else
                                throw new IOException("Writer Reference Held By LogfileWriter Object IS NULL....");
                }
                catch(IOException ioe)
                {
                        AllExceptionHandler handler=new AllExceptionHandler(ioe);
                        handler.reportException();
         
                }
         
         
        /***
         * NOTE: Dummy method...implemented for overriding purpose
         * Please use Inherited method writeLog method...
         * DO NOT USE THIS METHOD....
         * IT DOES NOTHING
         */
    
    public void writeOutput(String data) throws IOException {
        
        // EMPTY METHOD....
        // DO  NOT IMPLEMENT HERE
    }
    
}
