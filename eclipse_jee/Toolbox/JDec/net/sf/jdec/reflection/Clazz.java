/*
 *  Clazz.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.reflection;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.config.Configuration;

/***
 * TODO: Remove this class for next release
 * @author sbelur
 *
 */
public class Clazz {
    
    ArrayList allFields=new ArrayList();
    ArrayList allMethods=new ArrayList();
    ArrayList allConstructors=new ArrayList();
    ArrayList interfacesImplemented=new ArrayList();
    
    private String className="";  // Represents The complete or Full name
    private String simpleName="";  // Represents The partial name
    private boolean isInner=false;
    private boolean hasInner=false;
    private boolean isInterface=false;
    private boolean isArray=false;
    private boolean isPrimitive=false;
    private Class OuterClass=null;
    private Class InnerClass=null;
    private java.lang.String superclassname="I am not initialized!!!";
    private java.lang.String packageName="Default Package";
    private java.lang.String[] accessSpecifiers=null;
    
    
    /**
     * Constructor
     * @param name The name of the Class(Complete|Full Nmae)
     * @return
     */
    
    public Clazz(String name) {
        this.className=name;
    }
    
    /***
     * Constructor
     * @param name Stands for partial name Or Simple Name
     * @param pkg   Stands For package to which the Class belongs
     */
    
    public Clazz(java.lang.String name,java.lang.String pkg) {
        this.className=name;
        this.packageName=pkg;
        
    }
    
    
    public java.lang.String getClassName() {
        return className;
    }
    public java.lang.String getSimpleName() {
        if(className!=null && className.indexOf(".")!=-1){
            return className.substring(className.lastIndexOf(".")+1);
        } else
            return className;
    }
    public void setField(FieldMember f) {
        allFields.add(f);
    }
    public void setMethod(MethodMember m) {
        allMethods.add(m);
    }
    public void setConstructor(ConstructorMember c) {
        allConstructors.add(c);
    }
    
    public ArrayList getDecalaredFields() {
        return allFields;
    }
    public ArrayList getDecalaredMethods() {
        return allMethods;
    }
    public ArrayList getConstructors() {
        return this.allConstructors;
    }
    
    public ArrayList getALLMethods() {
        ArrayList allfunctions=new ArrayList();
        
        if(!ConsoleLauncher.isShowconstfirst()) {
            allfunctions.addAll(allMethods);
            allfunctions.addAll(allConstructors);
        } else {
            allfunctions.addAll(allConstructors);
            allfunctions.addAll(allMethods);
        }
        return  allfunctions;
        
    }
    
    public void setAccessSpecifiers(ArrayList accessSpec) {
        accessSpecifiers = new java.lang.String[accessSpec.size()];
        for(int i=0;i<accessSpec.size();i++) {
            java.lang.String access_spec = (java.lang.String)accessSpec.get(i);
            accessSpecifiers[i] = access_spec;
        }
        //this.accessSpecifiers=Util.parseAccessSpecifiers(al,Constants.CLASS_ACC);
    }
    
    public void setSuperClassName(java.lang.String superName) {
        qualifiedSuperClassName=new StringBuffer(superName);
        if(superName!="" && superName!=null && superName.trim().length()!=0) {
            boolean ok=checkThisClassName(this.className);
            if(!ok) {
                this.superclassname=superName;
            } else {
                this.superclassname="";
            }
            
            String si= Configuration.getShowImport();
            if(si.equalsIgnoreCase("true")) {
                String name=superclassname;
                if(name.indexOf("/")!=-1)name=name.replaceAll("/",".");
                ConsoleLauncher.addImportClass(name);
                String simpleName="";
                int index=name.lastIndexOf(".");
                if(index!=-1) {
                    simpleName=name.substring(index+1);
                } else
                    simpleName=name;
                this.superclassname=simpleName;
            }
            
        }
    }
    
    private boolean checkThisClassName(String className) {
        
        if(className.equals("java/lang/Object")) {
            return true;
            
        } else
            return false;
    }
    
    public void setPackageName(String pkgName) {
        this.packageName=pkgName;
        
    }
    /***
     * NOTE: Not used now
     * @return
     */
    public String describeMe() {
        StringBuffer whoami=new StringBuffer();
        whoami.append("The ClassName of This Class is ..."+this.getClassName());
        whoami.append("The packageName to which this Class Name belongs is..."+this.getPackageName());
        whoami.append("******************************************************");
        whoami.append("Total Number of fields  belonging To this Class..."+this.allFields.size()+"...[NOTE: THIS DOES NOT INCLUDE INHERITED FIELDS]");
        whoami.append("******************************************************\n\t");
        whoami.append("Field Descriptions...");
        Iterator fields=this.allFields.iterator();
        while(fields.hasNext()) {
            FieldMember f=(FieldMember)fields.next();
            
            
            whoami.append(f);
        }
        whoami.append("******************************************************");
        whoami.append("Total Number of Methods  belonging To this Class..."+this.allMethods.size()+"...[NOTE: THIS DOES NOT INCLUDE INHERITED Methods]");
        whoami.append("******************************************************\n\t");
        whoami.append("Field Descriptions...");
        Iterator methods=this.allMethods.iterator();
        while(methods.hasNext()) {
            MethodMember m=(MethodMember)methods.next();
            
            //Change this line later...
            whoami.append(m);
        }
        return whoami.toString();
    }
    
    
    /**
     * @return Returns the packageName.
     */
    public java.lang.String getPackageName() {
        
        
        java.lang.String pkg="";
        if(className!=null && className.indexOf(".")!=-1) {
            
            pkg=className.substring(0,className.lastIndexOf("."));
            return pkg;
        }
        
        return packageName;
    }
    
    
    /**
     * @return Returns the isArray.
     */
    public boolean isArray() {
        return isArray;
    }
    /**
     * @param isArray The isArray to set.
     */
    public void setArray(boolean isArray) {
        this.isArray = isArray;
    }
    /**
     * @return Returns the isInterface.
     */
    public boolean isInterface() {
        return isInterface;
    }
    /**
     * @param isInterface The isInterface to set.
     */
    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }
    /**
     * @return Returns the isInner.
     */
    public boolean isInner() {
        return isInner;
    }
    /**
     * @param isInner The isInner to set.
     */
    public void setInner(boolean isInner) {
        this.isInner = isInner;
    }
    /**
     * @return Returns the hasInner.
     */
    public boolean isHasInner() {
        return hasInner;
    }
    /**
     * @param hasInner The hasInner to set.
     */
    public void setHasInner(boolean hasInner) {
        this.hasInner = hasInner;
    }
    /**
     * @return Returns the outerClass.
     */
    public Class getOuterClass() {
        return OuterClass;
    }
    /**
     * @param outerClass The outerClass to set.
     */
    public void setOuterClass(Class outerClass) {
        OuterClass = outerClass;
    }
    /**
     * @return Returns the innerClass.
     */
    public Class getInnerClass() {
        return InnerClass;
    }
    /**
     * @param innerClass The innerClass to set.
     */
    public void setInnerClass(Class innerClass) {
        InnerClass = innerClass;
    }
    
    StringBuffer qualifiedSuperClassName=null;
    
    /**
     * @return Returns the superclassname.
     */
    public java.lang.String getSuperclassname() {
        
        String si= Configuration.getShowImport();
        
        if(si.equalsIgnoreCase("true")) {
            String name=superclassname;
            if(name.indexOf("/")!=-1)name=name.replaceAll("/",".");
            ConsoleLauncher.addImportClass(name);
            String simpleName="";
            int index=name.lastIndexOf(".");
            if(index!=-1) {
                simpleName=name.substring(index+1);
            } else
                simpleName=name;
            return simpleName;
        }
        return superclassname;
    }
    /**
     * @param superclassname The superclassname to set.
     */
    /**
     * @return Returns the isPrimitive.
     */
    public boolean isPrimitive() {
        return isPrimitive;
    }
    /**
     * @param isPrimitive The isPrimitive to set.
     */
    public void setPrimitive(boolean isPrimitive) {
        this.isPrimitive = isPrimitive;
    }
    
    
    
    public FieldMember getField(String fname) {
        FieldMember f=null;
        Iterator it=this.allFields.iterator();
        while(it.hasNext()) {
            FieldMember field=(FieldMember)it.next();
            if(f.getName()==fname) {
                f=field;
            }
        }
        
        return f;
    }
    /**
     * @return Returns the accessSpecifiers in a form of list .
     */
    public java.lang.String getAccessSpecifiers() {
        if(accessSpecifiers!=null && accessSpecifiers.length > 0) {
            StringBuffer temp=new StringBuffer();
            temp.append("[");
            for(int i=0;i<accessSpecifiers.length;i++) {
                temp.append(accessSpecifiers[i]);
                if(i!=accessSpecifiers.length)
                    temp.append("\t");
            }
            temp.append("]");
            return temp.toString();
        } else
            return "";
        //return "Default Package Access";
        
    }
    
    public java.lang.String getUserFriendlyAccessSpecifiers() {
        if(accessSpecifiers!=null && accessSpecifiers.length > 0) {
            StringBuffer temp=new StringBuffer();
            //   temp.append("[");
            boolean interfacep=false;
            for(int i=0;i<accessSpecifiers.length;i++) {
                if(accessSpecifiers[i].indexOf("super") == -1 && accessSpecifiers[i].indexOf("interface") == -1) {
                    temp.append(accessSpecifiers[i]);
                    if(i!=accessSpecifiers.length)
                        temp.append("  ");
                }
                if(accessSpecifiers[i].indexOf("interface") != -1){
                    interfacep=true;
                }
            }
            if(interfacep){
                temp.append("interface");
            }
            //temp.append("]");
            return temp.toString();
        } else
            return "";
        
    }
    
    
    public void setInterfacesImplemented(ArrayList interfaces) {
        interfacesImplemented=interfaces;
    }
    
    public ArrayList getInterfacesImplemented() {
        return interfacesImplemented;
    }
    
    public String getQualifiedSuperClassName() {
        if(qualifiedSuperClassName!=null)
            return qualifiedSuperClassName.toString();
        else
            return "";
    }
    
}