/*
 *  MethodRef.java Copyright (c) 2006,07 Swaroop Belur 
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


public class MethodRef {
	private int classPointer;
	private int descriptionPointer;
	private int tag;
	private int cppos;
	private java.lang.String classname=null;
	private java.lang.String typeofmethod=null;
	private ClassInfo classinfo=null;
	private NameAndType nmtype=null;
	private java.lang.String methodName=null;
	private java.lang.String key = "";


	public boolean isMethodRFefForAConstructor()
	{
		return key.indexOf("<init>")!=-1;
	}

	public int getCppos() {
		return cppos;
	}
	public void setCppos(int cppos) {
		this.cppos = cppos;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public int getClassPointer() {
		return classPointer;
	}
	public void setClassPointer(int classPointer) {
		this.classPointer = classPointer;
	}
	public int getDescriptionPointer() {
		return descriptionPointer;
	}
	public void setDescriptionPointer(int descriptionPointer) {
		this.descriptionPointer = descriptionPointer;
	}


	public void setClassName(java.lang.String name)
	{
		classname=name;
	}
	public void setClassRef(ClassInfo cinfo)
	{
		this.classinfo=cinfo;
	}
	public void setType(java.lang.String name)
	{
		this.typeofmethod=name;
	}
	public void setMethodName(java.lang.String methodname)
	{
		this.methodName=methodname;
	}

	public void setNameAndTypeRef(NameAndType nmtyperef)
	{
		this.nmtype=nmtyperef;
	}

	public NameAndType getNameAndTypeRef()
	{
		return this.nmtype;
	}

	public java.lang.String getKey() {
		if(methodName.equals("<init>")) methodName=this.getClassname();
		return typeofmethod+methodName;
	}

	public void setKey(java.lang.String key) {
		this.key = key;
	}
	public java.lang.String getClassname() {
		return classname;
	}
	public java.lang.String getMethodName() {
		//	if(methodName.equals("<init>"))methodName=classname.replace('/','.');
		return methodName;
	}
	public java.lang.String getTypeofmethod() {
		return typeofmethod;
	}
	public void setTypeofmethod(java.lang.String typeofmethod) {
		this.typeofmethod = typeofmethod;
	}




}
