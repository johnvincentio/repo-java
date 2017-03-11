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
package com.jaspersoft.sample.ofc;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRExpression;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DefaultBarSeries.java 2423 2008-10-23 12:34:13Z lucianc $
 */
public class DefaultBarSeries implements BarSeries, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JRExpression seriesExpression;
	private JRExpression categoryExpression;
	private JRExpression valueExpression;
	
	public JRExpression getCategoryExpression()
	{
		return categoryExpression;
	}

	public JRExpression getSeriesExpression()
	{
		return seriesExpression;
	}

	public JRExpression getValueExpression()
	{
		return valueExpression;
	}

	/**
	 * @param seriesExpression the seriesExpression to set
	 */
	public void setSeriesExpression(JRExpression seriesExpression)
	{
		this.seriesExpression = seriesExpression;
	}

	/**
	 * @param categoryExpression the categoryExpression to set
	 */
	public void setCategoryExpression(JRExpression categoryExpression)
	{
		this.categoryExpression = categoryExpression;
	}

	/**
	 * @param valueExpression the valueExpression to set
	 */
	public void setValueExpression(JRExpression valueExpression)
	{
		this.valueExpression = valueExpression;
	}

}
