/*
 *  ClassStructure.java Copyright (c) 2006,07 Swaroop Belur
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


package net.sf.jdec.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author sbelur
 */
public class ClassStructure {
    
    private ArrayList methods=new ArrayList();
    private HashMap associatedsearches=new HashMap();
    private ArrayList innerclasses=new ArrayList();
    private HashMap innerClassMap=new HashMap();
    private String name=null;
    private ClassStructure parent=null;

    public void setParent(ClassStructure parent) {
        this.parent = parent;
    }

    public ClassStructure getParent() {
        return parent;
    }
    

    public String getSearchString(String s){
        
        return (String)associatedsearches.get(s);
        
    }
    
    public void addAssociatedSearch(String s,String s1){
        associatedsearches.put(s,s1);
    }
    /** Creates a new genericFinder of ClassStructure */
    public ClassStructure() {
      
    }
    
    public ArrayList getInnerclasses() {
        return innerclasses;
    }
    
    public ArrayList getMethods() {
        return methods;
    }
    
    public void setMethods(ArrayList methods) {
        this.methods = methods;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void addInnerClass(ClassStructure structure){
        innerclasses.add(structure);
        innerClassMap.put(structure.getName(),structure);
    }
    
    
}
