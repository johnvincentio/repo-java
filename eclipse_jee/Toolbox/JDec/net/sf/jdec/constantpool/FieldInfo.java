/*
 *  FieldInfo.java Copyright (c) 2006,07 Swaroop Belur 
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

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

public class FieldInfo {

	private AbstractRuntimeAnnotation runtimeVisibleAnnotations ;
	private AbstractRuntimeAnnotation runtimeInvisibleAnnotations ;
	private String signature = "";
	private java.lang.String[] accessSpecifiers = null;
	private java.lang.String fieldName = "";
	private ArrayList fieldType = new ArrayList();
	private boolean isArray = false;
	private int dimension = -1;
	private Object obj = null;
	private java.lang.String belongsToClass = "";
	
	public AbstractRuntimeAnnotation getRuntimeInvisibleAnnotations() {
		return runtimeInvisibleAnnotations;
	}

	public void setRuntimeInvisibleAnnotations(
			AbstractRuntimeAnnotation runtimeInvisibleAnnotations) {
		this.runtimeInvisibleAnnotations = runtimeInvisibleAnnotations;
	}

	public void addFieldType(java.lang.String type)
	{
		fieldType.add(type);		
	}

	public java.lang.String getFieldName() {
		return fieldName;
	}

	public void setFieldName(java.lang.String fieldName) {
		this.fieldName = fieldName;
	}

	public void addAccessSpecifiers(int access)
	{
		accessSpecifiers = Util.parseAccessSpecifiers(new Integer(access),Constants.FIELD_ACC);
		//accessSpecifiers.add(new Integer(access));
	}
	
	public java.lang.String[] getAccessSpecifiers()
	{
		return accessSpecifiers;
	}

	public boolean isArray() {
		return isArray;
	}

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public Object getObj() {
		if(this.obj==null){
			Object temp=findFieldValue();
			this.obj=temp;
		}
			return this.obj;
	}

	public void setObj(Object obj) {
			this.obj = obj;
	
	}

	public ArrayList getFieldType() {
		return fieldType;
	}

	public void setFieldType(ArrayList fieldType) {
		this.fieldType = fieldType;
	}

	public java.lang.String getBelongsToClass() {
		return belongsToClass;
	}

	public void setBelongsToClass(java.lang.String belongsToClass) {
		this.belongsToClass = belongsToClass;
	}
	
	private Object findFieldValue()
	{
		Object o=null;
		ArrayList allfieldrefs=CPool.getAllFieldRefs();
		Iterator allfieldrefsIt=allfieldrefs.iterator();
		java.lang.String fieldName=null;
		int frefNmTypePointer=-1;
		while(allfieldrefsIt.hasNext())
		{
			FieldRef fref=(FieldRef)allfieldrefsIt.next();
		    frefNmTypePointer=fref.getDescriptionPointer();
			fieldName=getNameOfField(frefNmTypePointer);
		}
		o=getValueFromName(fieldName,frefNmTypePointer);
		
		return o;
		
		
	}
	
	
	private Object getValueFromName(java.lang.String name,int frefNmTypePointer)
	{
		
		
		return null;
		
	}
	
	private java.lang.String getNameOfField(int pointer)
	{
	
		ArrayList allNmTypes=CPool.getAllNameAndTypes();
		Iterator allNmTypesIt=allNmTypes.iterator();
		NameAndType nmtype=null;
		while(allNmTypesIt.hasNext())
		{
			nmtype=(NameAndType)allNmTypesIt.next();
			if(nmtype.getCppos()==pointer)
			{
				break;
			}
		}
		int utf8pointer=nmtype.getUtf8pointer();
		java.lang.String content=getUTF8Content(utf8pointer);
		return content;
	}
	
	private java.lang.String getUTF8Content(int utf8pointer)
	{
		
		ArrayList utf8s=CPool.getAllUtf8();
		Iterator utf8sIt=utf8s.iterator();
		UTF8 utf8=null;
		while(utf8sIt.hasNext())
		{
			utf8=(UTF8)utf8sIt.next();
			if(utf8.getCppos()==utf8pointer)
				break;
		}
		return utf8.getStringVal();//new java.lang.String(utf8.getBytes());
		
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public AbstractRuntimeAnnotation getRuntimeVisibleAnnotations() {
		return runtimeVisibleAnnotations;
	}

	public void setRuntimeVisibleAnnotations(
			AbstractRuntimeAnnotation runtimeVisibleAnnotations) {
		this.runtimeVisibleAnnotations = runtimeVisibleAnnotations;
	}
	
	
}
