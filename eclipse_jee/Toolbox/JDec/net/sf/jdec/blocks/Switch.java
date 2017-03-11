
/*
 *  Switch.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.util.Util;

public class Switch {
    
    
    
    private boolean doNotCloseDefault=false;

    public void setDoNotCloseDefault(boolean doNotCloseDefault) {
        this.doNotCloseDefault = doNotCloseDefault;
    }

    public boolean isDoNotCloseDefault() {
        return doNotCloseDefault;
    }
    

    
    
    public boolean defaultToBeDisplayed() {
        return displayDefault;
    }
    
    public void setDisplayDefault(boolean displayDefault) {
        this.displayDefault = displayDefault;
    }
    
    boolean defaultStarted=false; // to indicate default start has been used to display
    boolean defaultEnded=false; // to indicate default end has been used to display
    boolean displayDefault=true;
    ArrayList allCases=new ArrayList();
    int startOfSwitch=-1;
    int endOfSwitch=-1;
    int defaultStart=-1;
    int defaultEnd=-1;
    
    public int getEndOfSwitch() {
        int defEnd=getDefaultEnd();
        Case caseblk=getLastBlockForSwitch();
        int last=-1;
        if(caseblk!=null){
            last=caseblk.getCaseEnd();
        }
        if(last!=-1 && last > defEnd)endOfSwitch=last;
        else endOfSwitch=defEnd;
        return endOfSwitch;
    }
    
    
    
    
    public Switch(int startOfSwitch,int startOfDefault) {
        this.startOfSwitch=startOfSwitch;
        defaultStart=startOfDefault;
    }
    
    public void setEndOfDefault(int end) {
        defaultEnd=end;
    }
    
    public ArrayList getAllCases() {
        return allCases;
    }
    
    public void setAllCases(ArrayList allCases) {
        this.allCases = allCases;
    }
    
    public int getDefaultEnd() {
        return defaultEnd;
    }
    
    
    public int getDefaultStart() {
        return defaultStart;
    }
    
    public void setDefaultStart(int defaultStart) {
        this.defaultStart = defaultStart;
    }
    
    public int getStartOfSwitch() {
        return startOfSwitch;
    }
    
    public void setStartOfSwitch(int startOfSwitch) {
        this.startOfSwitch = startOfSwitch;
    }
    
    
    public class Case {
        
        
        private boolean doNotClose=false;

        public void setDoNotClose(boolean doNotClose) {
            this.doNotClose = doNotClose;
        }

        public boolean isDoNotClose() {
            return doNotClose;
        }

        
        private boolean wasCaseStartedPrinted=false;

        public void setWasCaseStartedPrinted(boolean wasCaseStartedPrinted) {
            this.wasCaseStartedPrinted = wasCaseStartedPrinted;
        }

        public boolean isWasCaseStartedPrinted() {
            return wasCaseStartedPrinted;
        }

        public boolean  usedinmultipleCaseCase=false;
        
        private boolean hasCaseBeenClosed=false;
        
        public void setClosed(boolean close){
            hasCaseBeenClosed=close;
        }
        
        public boolean wasClosed(){
            return hasCaseBeenClosed;
        }
        
        public boolean isGotoAsEndForCase() {
            return gotoAsEndForCase;
        }
        
        public void setGotoAsEndForCase(boolean gotoAsEndForCase) {
            this.gotoAsEndForCase = gotoAsEndForCase;
        }
        
        boolean gotoAsEndForCase=true;
        public boolean isFallsThru() {
            return fallsThru;
        }
        
        public void setFallsThru(boolean fallsThru) {
            this.fallsThru = fallsThru;
        }
        
        boolean fallsThru=true;
        int caseStart=-1;
        int caseEnd=-1;
        java.lang.String caseLabel;
        
        public Case(int start,int end,int label) {
            caseStart=start;
            caseEnd=end;
            caseLabel=checkForNonAscii(label);
        }
        
        public int getCaseEnd() {
            return caseEnd;
        }
        
        public void setCaseEnd(int caseEnd) {
            this.caseEnd = caseEnd;
        }
        
        public java.lang.String getCaseLabel() {
            return caseLabel;
        }
        
        
        
        public int getCaseStart() {
            return caseStart;
        }
        
        public void setCaseStart(int caseStart) {
            this.caseStart = caseStart;
        }
        
        public boolean isUsedinmultipleCaseCase() {
            return usedinmultipleCaseCase;
        }
        
        public void setUsedinmultipleCaseCase(boolean usedinmultipleCaseCase) {
            this.usedinmultipleCaseCase = usedinmultipleCaseCase;
        }
        
        private boolean isNumber(int i){
        try {
             
            Integer.parseInt(""+i); 
            return true;
        } catch (NumberFormatException ex) {
            return false;
        } 
        
    
    }
    
        private java.lang.String checkForNonAscii(int caseLabel) {
            java.lang.String str=""+caseLabel;
            String nonascii=UIUtil.getUIUtil().getInterpretNonAscii();
            
             if(isNumber(caseLabel)){
            return ""+caseLabel;
        }
            
            if(caseLabel < 32) {
                if(nonascii.equals("true"))
                    str=Util.formatForUTF(str,"char",new StringBuffer("octal"));
                else
                    str=""+caseLabel;
                return str;
            } else if(caseLabel > 127) {
                if(nonascii.equals("true"))
                    str=Util.formatForUTF(str,"char",new StringBuffer("UTF"));
                else
                    str=""+caseLabel;
                return str;
            } else
                
                return str;
        }
        
    }
    
    public void addCase(Case caseBlock) {
        if(caseBlock!=null){
            this.allCases.add(caseBlock);
            labelstartmap.put(caseBlock.getCaseLabel(),new Integer(caseBlock.getCaseStart()));
        }
        
    }
    
    // Must be called after all case blocks have been added
    
    public void removeUnwantedCaseBlocks() {
        
        this.allCases=allCases;
        return;
                /*ArrayList reInitializedCases=new ArrayList();
                int size=allCases.size();
                if(size > 0)
                {
                        for(int start=0;start<size;start++)
                        {
                 
                                Case caseblk=(Case)allCases.get(start);
                                /*if(caseblk.getCaseStart()!=defaultStart)
                                {
                                        //reInitializedCases.add(caseblk);
                                }
                 
                        }
                        this.allCases=reInitializedCases;
                }*/
        
        
    }
    
    // Check for null on return // list list of cases for this switch
    public ArrayList sortCasesByEnd(ArrayList list) {
        if(list==null || list.size() == 0 )return null;
        ArrayList sortedlist=new ArrayList();
        Hashtable temp=new Hashtable();
        for(int s=0;s<list.size();s++) {
            Case c=(Case)list.get(s);
            temp.put(new Integer(c.getCaseEnd()),c);
        }
        Set keys=temp.keySet();
        Integer ends[]=(Integer[])keys.toArray(new Integer[list.size()]);
        Arrays.sort(ends);
        Iterator t=null;
        for(int z=ends.length-1;z>=0;z--) {
            
            int curend=((Integer)ends[z]).intValue();
            temp.entrySet().iterator();
            while(t.hasNext()) {
                
                Map.Entry entry=(Map.Entry)t.next();
                Integer i=(Integer)entry.getKey();
                Case c=(Case)entry.getValue();
                if(i.intValue()==curend) {
                    sortedlist.add(c);
                    break;
                }
                
            }
            
        }
        
        
        return sortedlist;
    }
    
    
    public Case getFirstCase() {
        Case first=null;
        int sz=allCases.size();
        int allstarts[]=new int[sz];
        for (int i = 0; i < sz; i++) {
            
            Case c=(Case)allCases.get(i);
            allstarts[i]=c.getCaseStart();
        }
        Arrays.sort(allstarts);
        if(allstarts.length == 0)return null;
        int f=allstarts[0];
        first=getCaseGivenStart(f);
        return first;
        
    }
    
    public Case getCaseGivenStart(int start) {
        int sz=allCases.size();
        Case cs=null;
        for (int i = 0; i < sz; i++) {
            
            Case c=(Case)allCases.get(i);
            if(c.getCaseStart()==start) {
                cs=c;
                //break;
            }
        }
        return cs;
    }
    
    public boolean isDefaultEnded() {
        return defaultEnded;
    }
    
    public void setDefaultEnded(boolean defaultEnded) {
        this.defaultEnded = defaultEnded;
    }
    
    public boolean isDefaultStarted() {
        return defaultStarted;
    }
    
    public void setDefaultStarted(boolean defaultStarted) {
        this.defaultStarted = defaultStarted;
    }
    
    
    /***
     * c--> some case of this switch
     * block-->Either Case OR Default block
     * *@param c
     * @return the last case block OR null
     * null -> if default is the last block
     */
    public Case getLastBlockForSwitch() {
        Case cs=null;
        int sz=allCases.size();
        int allstarts[]=new int[sz];
        for (int i = 0; i < sz; i++) {
            
            Case temp=(Case)allCases.get(i);
            int  start=temp.getCaseStart();
            allstarts[i]=start;
        }
        Arrays.sort(allstarts);
        if(allstarts.length == 0)return null;
        int last=allstarts[allstarts.length-1];
        Case lastcase=getCaseGivenStart(last);
        if(last > defaultStart) {
            cs=lastcase;
        }
        return cs;
    }

    private boolean switchClosed=false;
    public boolean hasBeenClosed() {
        return switchClosed;
    }
    
    public void setSwitchClosed(boolean b){
        switchClosed=b;
    }
    
    public static int[] getSortedCaseStarts(ArrayList cases){
       
        int temp[]=new int[cases.size()];
        for(int z=0;z<cases.size();z++){
            temp[z]=((Case)cases.get(z)).getCaseStart();
        }
        Arrays.sort(temp);
        return temp;
        
    }
    
    
    private LinkedHashMap labelstartmap=new LinkedHashMap();
    
    
    private int getCaseEndForLabel(String lbl){
        if(allCases!=null && allCases.size() > 0){
            Iterator iterator=allCases.iterator();
            while(iterator.hasNext()){
                Case caseblk=(Case)iterator.next();
                if(caseblk.getCaseLabel().equals(lbl)){
                    return caseblk.getCaseEnd();
                }
                
            }
        }
        return -1;
    }
    
    public int getPositionForLabelInCaseGroupWithSameStarts(String label){
        
        int pos=-1;
        Set set=labelstartmap.entrySet();
        Integer start=(Integer)labelstartmap.get(label);
        int total=0;
        Iterator iterator=set.iterator();
        Map tempMap=new LinkedHashMap();
        int passedEnd=getCaseEndForLabel(label);
        while(iterator.hasNext()){
            Entry entry=(Entry)iterator.next();
            Integer temp=(Integer)entry.getValue();
            int curEnd=getCaseEndForLabel((String)entry.getKey());
            if(curEnd==passedEnd && temp.intValue()==start.intValue()){
                total++;
                tempMap.put(entry.getKey(),entry.getValue());
            }
        }
        if(total > 1){
           Set keyset=tempMap.keySet();
           Iterator tempiterator=keyset.iterator();
           pos=0;
           while(tempiterator.hasNext()){
               String l=(String)tempiterator.next();
               pos++;
               if(l.equals(label)){
                   if(!tempiterator.hasNext())pos=-1;
                   break;
               }
           }
            
        }
        return pos;
    }
    
}
