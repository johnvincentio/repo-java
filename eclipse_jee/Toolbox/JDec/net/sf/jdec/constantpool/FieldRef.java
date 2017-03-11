/*
 *  FieldRef.java Copyright (c) 2006,07 Swaroop Belur 
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



public class FieldRef {



            private int classPointer;

            private int descriptionPointer;

            private int tag;

            private int cppos;

            private java.lang.String classname=null;

            private java.lang.String typeoffield=null;

            private ClassInfo classinfo=null;

            private NameAndType nmtype=null;

            private java.lang.String fieldName=null;

            public boolean isBooleanField()
            {
                if(typeoffield!=null && typeoffield.trim().equals("Z"))
                {
                    return true;
                }
                return false;
            }



            public int getCppos() {

                        return cppos;

            }

            public void setCppos(int cppos) {

                        this.cppos = cppos;

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

            public int getTag() {

                        return tag;

            }

            public void setTag(int tag) {

                        this.tag = tag;

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

                        this.typeoffield=name;

            }

            public void setFieldName(java.lang.String fieldname)

            {

                        this.fieldName=fieldname;

            }



            public void setNameAndTypeRef(NameAndType nmtyperef)

            {

                        this.nmtype=nmtyperef;

            }

    public java.lang.String getFieldName() {
        return fieldName;
    }

    public java.lang.String getTypeoffield() {
		return typeoffield;
	}

	public java.lang.String getClassname() {
		return classname;
	}

	public void setClassname(java.lang.String classname) {
		this.classname = classname;
	}

    
}

 

