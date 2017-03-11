
/*
 *  InnerClassTracker.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.core;


import java.util.ArrayList;

import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.util.ClassStructure;


public class InnerClassTracker {
    
    
    public static ArrayList rootnodes=new ArrayList();
    private ClassDescription cd=null;
    public static Node currentRoot=null;
    private static ArrayList allnodes=new ArrayList();
    
    
    public static void reinitializeStaticMembers(){
        rootnodes=new ArrayList();
        currentRoot=null;
        allnodes=new ArrayList();
    }
    
    
    public InnerClassTracker(ClassDescription cd) {
        this.cd=cd;
    }
    
    public class Node {
        private ArrayList children=null;
        private ArrayList siblings=null;
        private boolean isRootNode=false;
        private Node parentNode=null;
        private java.lang.String className="";
        private java.lang.String classPath="";
        private boolean hasBeenDecompiled=false;
        private boolean compiledWithMinusG=true; // set it same as parent class file
        
        
        public Node(java.lang.String name,java.lang.String path) {
            this.className=name;
            this.classPath=path;
            
        }
        
        
        public void addChildNode(Node n) {
            if(children==null)children=new ArrayList();
            children.add(n);
        }
        
        public Node getNextSibling(Node parent) {
            //if(isRootNode)return null;
            //else
            //{
            siblings=parent.getChildren();
            int pos=getCurrentPositionInSiblingList(siblings,this);
            if(pos!=-1 && pos < (siblings.size()-1)) {
                Node next=(Node)siblings.get((pos+1));
                if(next!=null) {
                    return next;
                }
                
            }
            if(pos!=-1 && pos==(siblings.size()-1)) {
                Node newnode=resetRootNode(this);
                if(newnode!=null) {
                    setCurrentRoot(newnode);
                    return null;
                }
            }
            
            //}
            return null;
            
        }
        
        
        public ArrayList getSiblings() {
            return siblings;
        }
        
        public void setSiblings(ArrayList siblings) {
            this.siblings = siblings;
        }
        
        public ArrayList getChildren() {
            return children;
        }
        
        public void setChildren(ArrayList children) {
            this.children = children;
        }
        
        public boolean isRootNode() {
            return isRootNode;
        }
        
        public void setRootNode(boolean rootNode) {
            isRootNode = rootNode;
            if(children==null)
                children=new ArrayList();
        }
        
        public String getClassName() {
            return className;
        }
        
        public void setClassName(String className) {
            this.className = className;
        }
        
        public String getClassPath() {
            return classPath;
        }
        
        public void setClassPath(String classPath) {
            this.classPath = classPath;
        }
        
        public boolean isHasBeenDecompiled() {
            return hasBeenDecompiled;
        }
        
        public void setHasBeenDecompiled(boolean hasBeenDecompiled) {
            this.hasBeenDecompiled = hasBeenDecompiled;
        }
        
        public boolean isCompiledWithMinusG() {
            return compiledWithMinusG;
        }
        
        public void setCompiledWithMinusG(boolean compiledWithMinusG) {
            this.compiledWithMinusG = compiledWithMinusG;
        }
        
        private void setParent(InnerClassTracker.Node parent) {
            parentNode=parent;
        }
        
        public Node getParent() {
            return parentNode;
        }
        
        
        
        
    }
    
    public Node addRootNode(Node node) {
        if(node==null)return null;
        java.lang.String name=node.getClassName();
        java.lang.String path=node.getClassPath();
        boolean addRoot=isRootAlreadyPresent(name,path);
        if(!addRoot) {
            rootnodes.add(node);
            node.setRootNode(true);
            
            if(rootnodes.size()==1) {
                setCurrentRoot(node);
            } else {
                int i=1;
            }
            return node;
        } else {
            return null;
        }
        
    }
    
    
    private boolean isRootAlreadyPresent(java.lang.String name,java.lang.String  path) {
        if(rootnodes==null || rootnodes.size()==0)return false;
        else {
            for(int s=0;s<rootnodes.size();s++) {
                Node n=(Node)rootnodes.get(s);
                if(n.getClassName().equals(name) && n.getClassPath().equals(path)) {
                    return true;
                }
            }
            
            
        }
        
        return false;
    }
    
    
    public ArrayList getRootnodes() {
        return rootnodes;
    }
    
    
    public Node getCurrentRoot() {
        return currentRoot;
    }
    
    public void setCurrentRoot(Node currentRoot) {
        this.currentRoot = currentRoot;
    }
    
    public Node getParentNode(java.lang.String name,java.lang.String path) {
        if(rootnodes==null || rootnodes.size()==0)return null;
        for(int s=0;s<rootnodes.size();s++) {
            Node n=(Node)rootnodes.get(s);
            if(n.getClassName().equals(name) && n.getClassPath().equals(path)) {
                return n;
            }
        }
        
        return null;
    }
    
    
    
    
    public Node registerChildNode(Node parent,Node child) {
        
        if(parent!=null && child!=null) {
            boolean b=isChildAlreadyRegistered(parent,child);
            if(!b) {
                parent.addChildNode(child);
                child.setParent(parent);
                child.setCompiledWithMinusG(parent.isCompiledWithMinusG());
                //addRootNode(child);
                return child;
            } else
                return null;
        } else
            return null;
        
    }
    
    
    private static int getCurrentPositionInSiblingList(ArrayList siblings,Node current) {
        
        if(siblings!=null) {
            for(int s=0;s<siblings.size();s++) {
                
                Node n=(Node)siblings.get(s);
                if(current==n) {
                    return s;
                }
                
            }
        }
        return -1;
        
    }
    
    
    
    private boolean isChildAlreadyRegistered(Node parent,Node child) {
        
        ArrayList children=parent.getChildren();
        if(children==null || children.size()==0) {
            return false;
        } else {
            java.lang.String name=child.getClassName();
            java.lang.String path=child.getClassPath();
            for(int s=0;s<children.size();s++) {
                Node n=(Node)children.get(s);
                if(n.getClassName().equals(name) && n.getClassPath().equals(path)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    
    
    // n --> Current Root Node
    public Node resetRootNode(Node n) {
        ArrayList allroots=this.rootnodes;
        int pos=getPositionOfCurrentRoot(allroots,n);
        if(pos!=-1 && (pos!=allroots.size()-1)) {
            Node next=(Node)rootnodes.get((pos+1));
            return next;
        } else if(pos!=-1 && (pos==allroots.size()-1)) {
            Node p=n.getParent();
            if(p!=null) {
                // Detect next
                // else Detect prev
                // else Default case
                Node nextValidNode=getNextSiblingForParentWithChildren(p);
                if(nextValidNode!=null){
                    
                    setCurrentRoot(nextValidNode);
                } else{
                    Node prevValidNode=getPrevSiblingForParentWithChildren(p);
                    if(prevValidNode!=null){
                        setCurrentRoot(prevValidNode);
                        return getCurrentRoot();
                    } else {
                        setCurrentRoot((Node)p.getChildren().get(0));
                        return getCurrentRoot();
                    }
                    
                }
            }
            return null;
            
        } else
            return null;
    }
    
    private int getPositionOfCurrentRoot(ArrayList allroots,Node cur) {
        int pos=-1;
        for(int s=0;s<allroots.size();s++) {
            Node n=(Node)allroots.get(s);
            if(n.getClassName().equals(cur.getClassName()) && n.getClassPath().equals(cur.getClassPath())) {
                pos=s;
                break;
            }
            
        }
        return pos;
    }
    
    
    public Node createNode(java.lang.String path,java.lang.String name) {
        Node n=isNodeAlreadyPresent(name,path);
        if(n==null){
            n=new Node(name,path);
            allnodes.add(n);
        }
        return n;
    }
    
    
    
    public Node getParentsFirstNode(Node parent) {
        ArrayList list=parent.getChildren();
        if(list!=null && list.size()>0) {
            return (Node)list.get(0);
        }
        return null;
        
    }
    
    public static Node getNextSiblingForParentWithChildren(Node p) {
        
        Node superp=p.getParent();
        if(superp==null)return null;
        ArrayList siblist=superp.getChildren();
        int pos=getCurrentPositionInSiblingList(siblist,p);
        if(pos!=-1){
            
            for(int k=pos+1;k<siblist.size();k++){
                
                Node cur=(Node)siblist.get(k);
                if(cur.getChildren()!=null && cur.getChildren().size() > 0){
                    return cur;
                }
            }
        }
        return null;
        
    }
    
    
    private Node getPrevSiblingForParentWithChildren(Node p) {
        
        Node superp=p.getParent();
        if(superp==null)return null;
        ArrayList siblist=superp.getChildren();
        
        for(int k=0;k<siblist.size();k++){
            
            Node cur=(Node)siblist.get(k);
            if(cur.getChildren()!=null && cur.getChildren().size() > 0){
                
                ArrayList children=cur.getChildren();
                for(int k1=0;k1<children.size();k1++){
                    Node cn=(Node)children.get(k1);
                    if(cn.getChildren()!=null && cn.getChildren().size() > 0){
                        return cn;
                    }
                }
            }
        }
        
        return null;
        
    }
    
    private Node isNodeAlreadyPresent(String name, String path) {
        for(int z=0;z<allnodes.size();z++){
            Node n=(Node)allnodes.get(z);
            if(n.getClassName().equals(name) && n.getClassPath().equals(path)){
                return n;
            }
        }
        return null;
    }
    
    public static ClassStructure getClassStructure(String name, ArrayList allClassStructures) {
        
        for(int z=0;z<allClassStructures.size();z++){
            ClassStructure cst=(ClassStructure)allClassStructures.get(z);
            if(cst.getName().equals(name)){
                
                return cst;
            }
            
        }
        return null;
        
        
    }
    
}
