/*
 *  Readerjava Copyright (c) 2006,07 Swaroop Belur
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

import java.io.DataInputStream;
import java.io.FileNotFoundException;

import net.sf.jdec.exceptions.IOError;


public abstract class Reader {
    
        /* (non-Javadoc)
         * @see java.io.Reader#close()
         */
    
    protected static DataInputStream instream=null;
    
    
    public static net.sf.jdec.io.Reader  createStreamReader(String path) {
        try {
            return new FileReader(path);
        } catch(FileNotFoundException fne) {
            throw new IOError("File Specified By "+path +" not found....");
        }
        
    }
    
    
    
    public abstract String toString();
    
    public abstract DataInputStream getStreamReader();
    
    
    protected String whoami=null;
    
    
}
