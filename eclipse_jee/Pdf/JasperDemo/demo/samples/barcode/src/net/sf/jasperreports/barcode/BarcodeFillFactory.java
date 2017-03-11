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

import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentFillFactory;
import net.sf.jasperreports.engine.component.FillComponent;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BarcodeFillFactory.java 2306 2008-08-20 17:50:37Z lucianc $
 */
public class BarcodeFillFactory implements ComponentFillFactory
{
	
	private BarcodeProviders providers;

	public FillComponent toFillComponent(Component component,
			JRFillObjectFactory factory)
	{
		BarcodeComponent barcode = (BarcodeComponent) component;
		return new FillBarcode(providers, barcode);
	}

	public FillComponent cloneFillComponent(FillComponent component,
			JRFillCloneFactory factory)
	{
		FillBarcode fillBarcode = (FillBarcode) component;
		return new FillBarcode(providers, fillBarcode.getBarcode());
	}

	public BarcodeProviders getProviders()
	{
		return providers;
	}

	public void setProviders(BarcodeProviders providers)
	{
		this.providers = providers;
	}

}
