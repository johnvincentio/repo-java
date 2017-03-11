/*
 *  UIConsoleWriter.java Copyright (c) 2006,07 Swaroop Belur
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import net.sf.jdec.config.Configuration;


/**
 * @author belurs
 *
 * TODO: Remove this class for next release 
 
 */
public class UIConsoleWriter extends PrintWriter {
    
    
    private static UIConsoleWriter writer=null;
    private static PrintWriter printwriter=null;
    
    
    
    public static UIConsoleWriter getInstance() throws IOException {
        if(printwriter==null) {
            
            printwriter=new PrintWriter(new FileOutputStream(Configuration.getConsoleDetailFile()));
            writer=new UIConsoleWriter(printwriter);
            return writer;
        } else {
            return writer;
        }
        
    }
    
    
    
    /**
     * @param writer
     */
    private UIConsoleWriter(java.io.Writer writer) {
        super(writer);
        
    }
    public  void writeOutput(String data) throws IOException {
        if(printwriter!=null) {
            printwriter.write(data);
            printwriter.flush();
        }
        
    }
    public  void writeLog(String data) throws IOException {
        // Ignore Implementation
    }
    
    public  void writeLog(String data,String level) throws IOException {
        // Ignore Implementation
    }
    
    public void close() {
        
        if(printwriter!=null) {
            printwriter.flush();
            printwriter=null;
        }
        
        
    }
    
    
    
}
