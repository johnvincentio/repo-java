
/*
 *  LogConsoleWriter.java Copyright (c) 2006,07 Swaroop Belur
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import net.sf.jdec.config.Configuration;


public class LogConsoleWriter extends ConsoleWriter {
    
    private static LogConsoleWriter consolelog=null;
    private LogConsoleWriter(java.io.Writer writer) {
        super(writer);
        
    }
    
    public static LogConsoleWriter getLogConsoleWriter() {
        if(consolelog==null) {
            logwriter = new PrintWriter(new BufferedOutputStream(System.out));
            consolelog=new LogConsoleWriter(logwriter);
        }
        return consolelog;
    }
    
    
    /***
     * Dummy Method...Added for overriding purpose From Writer Class....
     * DO NOT USE
     * DOES NOTHING
     *
     */
    
    public void writeOutput(String data) throws IOException {
        //		 	EMPTY
        // DO NOT IMPLEMENT
    }
    
    
    public void writeLog(String data,int level) throws java.io.IOException {
        java.lang.String logLevel= Configuration.getLogLevel();
        if(logLevel.equals("2"))  // 2 is for more/extra output to log
        {					  // 1 is default
            logwriter.write(data);
        }
    }
    public void writeLog(String data) throws IOException {
        
        logwriter.write(data);
        
    }
    
    /****
     * @see OutputFileWriter#close() For NOTE. That Applies Here as Well Except that
     * LogConsoleWriter might not be null at that time(time of calling close)
     */
    
    
    public void close(){
        try{
            if(logwriter!=null) {
                logwriter.flush();
                logwriter.close();
            }
            logwriter=null;
        } catch(IOException ioe) {
            // Leave Blank
        }
    }
    /**
     * Need to call This after calling writeOutput method of this class
     */
    public void flush() {
        try {
            logwriter.flush();
        } catch(IOException ioe) {
            // Leave Blank
        }
    }
    
    public void writeLog(String data,java.lang.String level) throws java.io.IOException {
        //java.lang.String logLevel=Configuration.getLogLevel();
        if(level.equals("2"))  // 2 is for more/extra output to log
        {					  // 1 is default
            logwriter.write(data);
        }
    }
}
