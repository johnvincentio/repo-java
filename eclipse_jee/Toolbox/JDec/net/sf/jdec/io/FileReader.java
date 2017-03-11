/*
 *  FileReader.java Copyright (c) 2006,07 Swaroop Belur
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


public class FileReader extends Reader {
    
    String path="";
    
    
    public FileReader(String path) throws FileNotFoundException {
        this.path=path;
        /*try
        {
                 instream=new DataInputStream(new BufferedInputStream(new InputStreamReader(new FileInputStream(path),"UTF")));
        }
        catch(UnsupportedEncodingException une)
        {
          instream=new DataInputStream((new BufferedInputStream(new FileInputStream(path))));
        }*/
        instream=new DataInputStream(new BufferedInputStream((new FileInputStream(path))));
        
        
    }
    
        /* (non-Javadoc)
         * @see net.sf.jdec.io.Reader#toString()
         */
    public String toString() {
        
        whoami="This is the Object Responsible for Reading (from a Stream) the bytecode of the Class File ....";
        whoami+="\n"+path;
        return whoami;
    }
    
        /* (non-Javadoc)
         * @see net.sf.jdec.io.Reader#getStreamReader()
         */
    public DataInputStream getStreamReader() {
        
        return instream;
    }
    
    
}
