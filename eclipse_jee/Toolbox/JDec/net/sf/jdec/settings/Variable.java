package net.sf.jdec.settings;

/*
 *  Variable.java Copyright (c) 2006,07 Swaroop Belur 
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

public class Variable {

	private String prefix;

	private String pkg;

	private String defaultPackage;

	private String className;

	private String isPrimitive; 
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDefaultPackage() {
		return defaultPackage;
	}

	public void setDefaultPackage(String defaultPackage) {
		this.defaultPackage = defaultPackage;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getIsPrimitive() {
		return isPrimitive;
	}

	public void setIsPrimitive(String isPrimitive) {
		this.isPrimitive = isPrimitive;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((className == null) ? 0 : className.hashCode());
		result = PRIME * result + ((defaultPackage == null) ? 0 : defaultPackage.hashCode());
		result = PRIME * result + ((isPrimitive == null) ? 0 : isPrimitive.hashCode());
		result = PRIME * result + ((pkg == null) ? 0 : pkg.hashCode());
		result = PRIME * result + ((prefix == null) ? 0 : prefix.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Variable other = (Variable) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (defaultPackage == null) {
			if (other.defaultPackage != null)
				return false;
		} else if (!defaultPackage.equals(other.defaultPackage))
			return false;
		if (isPrimitive == null) {
			if (other.isPrimitive != null)
				return false;
		} else if (!isPrimitive.equals(other.isPrimitive))
			return false;
		if (pkg == null) {
			if (other.pkg != null)
				return false;
		} else if (!pkg.equals(other.pkg))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		return true;
	}

	

}