/*
 *  OutputFileWriter.java Copyright (c) 2006,07 Swaroop Belur
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


import java.io.*;


public class OutputFileWriter extends FileWriter {
    private static OutputFileWriter OutputFileWriter=null;
    
    private OutputFileWriter(java.io.Writer writer) {
        super(writer);
    }
    
    public static OutputFileWriter getOutputFileWriter(String path) throws IOException {
        if(opwriter==null) {
            OutputStream fout= new FileOutputStream(new java.io.File(path));
            //opwriter = new PrintWriter(new BufferedWriter(new java.io.OutputStreamWriter(new java.io.BufferedOutputStream(fout),"UTF-8")));
            opwriter = new java.io.OutputStreamWriter(new java.io.BufferedOutputStream(fout),"UTF8");
            OutputFileWriter=new OutputFileWriter(opwriter);
        }
        return OutputFileWriter;
        
    }
    
    public void writeOutput(String data) throws java.io.IOException {
        opwriter.write(data);
    }
    
    /**
     * Need to call This after calling writeOutput method of this class
     */
    public void flush() {
        try {
            opwriter.flush();
        } catch(IOException ioe) {
            
        }
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
    
    
    /**
     * NOTE:
     * This method Nullifies the Reference to the
     * encapsualating writer object...
     * Calling any write method using this Object reference will
     * throw a @see java.lang.NullPointerException ; even
     * though The reference to the OutputFileWriter Or the
     * FileWriter Object may not be null at that time.
     *
     */
    
    public void close() {
        try {
            if(opwriter!=null) {
                opwriter.flush();
                opwriter.close();
            }
            opwriter=null;
        } catch(IOException ioe) {
            
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
