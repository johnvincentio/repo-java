/*
 *  ClassInfo.java Copyright (c) 2006,07 Swaroop Belur 
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

package net.sf.jdec.constantpool;



public class ClassInfo {

	private  int utf8pointer;
	private int tag;
	private int cppos;

	public int getCppos() {
		return cppos;
	}

	public void setCppos(int cppos) {
		this.cppos = cppos;
	}

	public int getUtf8pointer() {
		return utf8pointer;
	}

	public void setUtf8pointer(int utf8pointer) {
		this.utf8pointer = utf8pointer;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}



}
