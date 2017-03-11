/*
 *  OutputConsoleWriter.java Copyright (c) 2006,07 Swaroop Belur
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

public class OutputConsoleWriter extends ConsoleWriter {
    
    private static OutputConsoleWriter consoleop=null;
    
    
    
    private OutputConsoleWriter(java.io.Writer writer) {
        super(writer);
        
    }
    
    public static OutputConsoleWriter getOutputConsoleWriter() {
        if(consoleop==null) {
            opwriter = new PrintWriter(new BufferedOutputStream(System.out));
            consoleop=new OutputConsoleWriter(opwriter);
        }
        return consoleop;
    }
    
    
    public void writeOutput(String data) throws IOException {
        opwriter.write(data);
    }
    
    /***
     * Dummy Method...Added for overriding purpose From Writer Class....
     * DO NOT USE
     *
     *
     */
    
    public final void writeLog(String data) throws IOException {
    	 throw new UnsupportedOperationException();
    }
    
    /****
     * @see OutputFileWriter#close() For NOTE. That Applies Here as Well Except that
     * OutputConsoleWriter might not be null at that time(time of calling close)
     */
    
    
    public void close() {
        try {
            if(opwriter!=null) {
                opwriter.flush();
                opwriter.close();
            }
            opwriter=null;
        } catch(IOException ioe) {
            // Leave blank
        }
    }
    /**
     * Need to call This after calling writeOutput method of this class
     */
    public void flush(){
        try {
            opwriter.flush();
        } catch(IOException ioe) {
            // Leave blank
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
