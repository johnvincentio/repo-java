/*
 * PreviewJar.java Copyright (c) 2006,07 Swaroop Belur
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class PreviewJar {


    public static Object[] getPackages(String arg)  throws IOException{

       JarFile f=new JarFile(arg);
       Enumeration e=f.entries();
       ArrayList allpaths=new ArrayList();
       while(e.hasMoreElements())
       {
          ZipEntry ze=(ZipEntry)e.nextElement();
          File file=new File(ze.getName());
          if(file.isDirectory()==false)
          {
              String p=ze.getName();
              if(p.indexOf(".")!=-1)
              {
                  String sep=File.separator;
                  if(p.indexOf(sep)==-1)
                      sep="/";
                  char ch=sep.charAt(0);
                  int i=p.indexOf(".");
                  char c=p.charAt(i);
                  while(c!=ch && i > 0)
                  {
                      i--;
                      c=p.charAt(i);
                  }
                  if(i > 0 ){
                      String reqd=p.substring(0,i);
                      if(reqd.indexOf("META-INF")==-1)
                      allpaths.add(reqd);
                  }
              }
          }
       }

       if(allpaths.size() > 0)
       {
           Object temp[]=allpaths.toArray();
           Arrays.sort(temp);
           return temp;
       }
       else
       {
           return null;
       }

    }


    public static Object[] getJarDetails(String arg)throws IOException{

        ArrayList list=new ArrayList();
        JarFile f=new JarFile(arg);
        int size=f.size();

        Manifest manifest=f.getManifest();

        Map map=manifest.getMainAttributes();
        Set set=map.entrySet();
        Iterator it=set.iterator();
        if(it.hasNext())
        {
        	for(int z=0;z<10;z++)
        			list.add("");
        	list.add("--------------------");
        	list.add("General Jar Details");
           	list.add("--------------------");
            list.add("Jar File Size"+size);

        }
        else
        {
            list.add("Jar File Size"+size);
           	list.add("No Manifest Information Available");
        }

        while(it.hasNext())
        {

            Map.Entry entry=(Map.Entry)it.next();
            if(entry!=null)
            {
                Object key=entry.getKey();
                Object val=entry.getValue();
                String s=key+" :"+val;
                list.add(s);

            }
        }
        return list.toArray();
    }

    public static void main(String[] args) throws IOException {
        getJarDetails("c:\\jar\\test2.jar");
    }


}
