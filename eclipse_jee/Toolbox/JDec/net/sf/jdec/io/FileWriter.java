/*
 *  FileWriter.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.exceptions.ApplicationException;

import java.io.IOException;
import java.io.Writer;


public abstract class FileWriter extends net.sf.jdec.io.Writer {
    
    
    
    
    public FileWriter(Writer writer) {
        super(writer);
    }
    
    public static net.sf.jdec.io.Writer getFileWriter(String path,String type)  throws IOException {
        if(type.equals("output")) {
            return OutputFileWriter.getOutputFileWriter(path);
        }
        if(type.equals("log")) {
            return LogFileWriter.getLogFileWriter(path);
        } else
            throw new ApplicationException("Incorrect Type specified for creating writer object");
    }
    
    
}
