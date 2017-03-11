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
package jcharts;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.base.JRBaseElementDataset;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CompiledAxisDataset.java 2351 2008-09-11 14:27:00Z lucianc $
 */
public class CompiledAxisDataset extends JRBaseElementDataset implements AxisDataset
{
	
	private static final long serialVersionUID = 1L;
	
	private final JRExpression labelExpression;
	private final JRExpression valueExpression;
	
	public CompiledAxisDataset(AxisDataset dataset, JRBaseObjectFactory factory)
	{
		super(dataset, factory);
		
		labelExpression = factory.getExpression(dataset.getLabelExpression());
		valueExpression = factory.getExpression(dataset.getValueExpression());
	}

	public void collectExpressions(JRExpressionCollector collector)
	{
		AxisChartCompiler.collectExpressions(this, collector);
	}

	public JRExpression getLabelExpression()
	{
		return labelExpression;
	}

	public JRExpression getValueExpression()
	{
		return valueExpression;
	}

}