/*
 *  UIObserver.java Copyright (c) 2006,07 Swaroop Belur 
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

package net.sf.jdec.ui.core;

public class UIObserver {

	private java.lang.String observerType="";
	private static UIObserver observer=null;
	
	private UIObserver()
	{
		
	}
	
	public static UIObserver getUIObserver()
	{
		if(observer==null)
		{
			observer=new UIObserver();
			return observer;
		}
		else
			return observer;
		
	}
	
	
	public void resetOutputFrame(String type)
	{
		if(type.equalsIgnoreCase("jeditorPane") || type.equalsIgnoreCase("table"))
			observerType=type;
		else
			observerType="jeditorPane";
	}
	
	public String getOutputFrameType()
	{
		return observerType;
	}
	
	public void resetTabsPane(boolean jarTree)
	{
		renderJarTree=jarTree;
	}
	
	public boolean renderJarTree()
	{
		return renderJarTree;
	}
	
	private boolean renderJarTree=false;
}
