
/*
 *  ClassFileFilter.java Copyright (c) 2006,07 Swaroop Belur 
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
import javax.swing.filechooser.FileFilter;

public class ClassFileFilter extends FileFilter
{
	private String fileTypeDescription = "Java Class File";
	private String fileType = "class";
	public ClassFileFilter() {
	}
	public String getDescription() {
		return fileTypeDescription;
	}
	public boolean accept(File f) {
		if (f == null)
			return false;
		if (f.isDirectory())
			return true;
		return f.getName().toLowerCase().endsWith(fileType);
	}
}