/*
 * ZipWriter.java Copyright (c) 2006,07 Swaroop Belur
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipOutputStream;

public class ZipWriter extends FileWriter {
    
    
    private static ZipWriter zipwriter=null;
    
    
    
        /*public void write(char[] cbuf, int off, int len) throws IOException {
         
                opwriter.write(cbuf,off,len);
        }*/
    
    
    /**
     * Need to call This after calling writeOutput method of this class
     */
    public void flush() {
        
        try {
            opwriter.flush();
        } catch(IOException ioe) {
            // Leve blank
        }
    }
    
    /**
     * NOTE:
     * This method Nullifies the Reference to the
     * encapsualating writer object...
     * Calling any write method using this Object reference will
     * throw a @see java.lang.NullPointerException ; even
     * though The reference to the ZipWriter Or the
     * FileWriter Object may not be null at that time.
     *
     */
    
    public void close()  {
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
    
    private ZipWriter(java.io.Writer writer) {
        super(writer);
    }
    
    public static ZipWriter getZipWriter(java.lang.String path) throws IOException {
        if(zipwriter==null) {
            opwriter = new BufferedWriter(new OutputStreamWriter(new ZipOutputStream(new FileOutputStream(path))));
            zipwriter=new ZipWriter(opwriter);
        }
        return zipwriter;
    }
    
    
    public void writeOutput(String data) throws IOException {
        
        opwriter.write(data);
        
    }
    
    /***
     * NOTE: Dummy method...implemented for overriding purpose
     * There is no use of this method for this Writer Class
     * DO NOT USE THIS METHOD....
     * IT DOES NOTHING
     */
    
    public void writeLog(String data) throws IOException {
        // EMPTY
        // DO NOT IMPLEMENT
    }
    
    public void writeLog(String data,java.lang.String level) throws java.io.IOException {
        //java.lang.String logLevel=Configuration.getLogLevel();
        if(level.equals("2"))  // 2 is for more/extra output to log
        {					  // 1 is default
            logwriter.write(data);
        }
    }
    
    
}
