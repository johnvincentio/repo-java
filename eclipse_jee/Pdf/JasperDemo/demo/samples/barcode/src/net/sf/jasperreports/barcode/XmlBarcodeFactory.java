/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2006 JasperSoft Corporation http://www.jaspersoft.com
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 * JasperSoft Corporation
 * 303 Second Street, Suite 450 North
 * San Francisco, CA 94107
 * http://www.jaspersoft.com
 */
package net.sf.jasperreports.barcode;

import net.sf.jasperreports.engine.xml.JRBaseFactory;

import org.xml.sax.Attributes;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: XmlBarcodeFactory.java 2306 2008-08-20 17:50:37Z lucianc $
 */
public class XmlBarcodeFactory extends JRBaseFactory
{

	public Object createObject(Attributes attrs) throws Exception
	{
		BarcodeComponent bc = new BarcodeComponent();
		
		String type = attrs.getValue("type");
		bc.setType(type);
		
		String drawTextAttr = attrs.getValue("drawText");
		if (drawTextAttr != null)
		{
			boolean drawText = Boolean.valueOf(drawTextAttr).booleanValue();
			bc.setDrawText(drawText);
		}
		
		return bc;
	}

}
