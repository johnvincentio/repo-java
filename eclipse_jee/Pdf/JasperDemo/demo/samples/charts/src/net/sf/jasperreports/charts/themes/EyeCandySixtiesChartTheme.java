package net.sf.jasperreports.charts.themes;
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;

import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.charts.JRThermometerPlot;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.charts.fill.JRFillMeterPlot;
import net.sf.jasperreports.charts.fill.JRFillPie3DPlot;
import net.sf.jasperreports.charts.fill.JRFillPieDataset;
import net.sf.jasperreports.charts.fill.JRFillPiePlot;
import net.sf.jasperreports.charts.fill.JRFillThermometerPlot;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.JRChartPlot;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseFont;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.ui.TextAnchor;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: EyeCandySixtiesChartTheme.java 2513 2009-01-07 16:06:07Z shertage $
 */
public class EyeCandySixtiesChartTheme extends GenericChartTheme
{

	public static final Color THERMOMETER_COLOR = Color.BLACK;
//	public static final Color MARKER_COLOR = new Color(210,210,210);
	public static final Color SCATTER_GRIDLINE_COLOR = new Color(196, 196, 196);

	/**
	 *
	 */
	public EyeCandySixtiesChartTheme()
	{
	}

	/**
	 *
	 */
	protected void configureChart(JFreeChart jfreeChart, JRChartPlot jrPlot, byte evaluation) throws JRException
	{
		super.configureChart(jfreeChart, jrPlot, evaluation);

		TextTitle title = jfreeChart.getTitle();
		if(title != null)
		{
			RectangleInsets padding = title.getPadding();
			double bottomPadding = Math.max(padding.getBottom(), 15d);
			title.setPadding(padding.getTop(), padding.getLeft(), bottomPadding, padding.getRight());
		}

		GradientPaint gp = (GradientPaint)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.DEFAULT_BACKGROUND_PAINT);

		jfreeChart.setBackgroundPaint(new GradientPaint(0f, 0f, gp.getColor1(), 0f, chart.getHeight() * 0.7f, gp.getColor2(), false));
	}

	/**
	 *
	 */
	protected void configurePlot(Plot plot, JRChartPlot jrPlot)
	{
		super.configurePlot(plot, jrPlot);

		if(plot instanceof CategoryPlot)
		{
			CategoryPlot categoryPlot = (CategoryPlot)plot;
			CategoryItemRenderer categoryRenderer = categoryPlot.getRenderer();
			CategoryDataset categoryDataset = categoryPlot.getDataset();
			for(int i = 0; i < categoryDataset.getRowCount(); i++)
			{
				categoryRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
			}
			categoryPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_134);
			categoryPlot.setRangeGridlineStroke(new BasicStroke(1f));
			categoryPlot.setDomainGridlinesVisible(false);
			categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		}
		else if(plot instanceof XYPlot)
		{
			XYPlot xyPlot = (XYPlot)plot;
			XYDataset xyDataset = xyPlot.getDataset();
			XYItemRenderer xyItemRenderer = xyPlot.getRenderer();
			for(int i = 0; i < xyDataset.getSeriesCount(); i++)
			{
				xyItemRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
			}
			xyPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_134);
			xyPlot.setRangeGridlineStroke(new BasicStroke(1f));
			xyPlot.setDomainGridlinesVisible(false);
			
			xyPlot.setRangeZeroBaselineVisible(true);

		}
	}

	protected JFreeChart createAreaChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createAreaChart(evaluation);
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		calculateTickUnits(categoryPlot.getRangeAxis());
		return jfreeChart;
	}

	protected JFreeChart createStackedAreaChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createStackedAreaChart(evaluation);
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		calculateTickUnits(categoryPlot.getRangeAxis());
		return jfreeChart;
	}

	protected JFreeChart createXyAreaChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createXyAreaChart(evaluation);

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		SquareXYAreaRenderer squareXyAreaRenderer = new SquareXYAreaRenderer((XYAreaRenderer)xyPlot.getRenderer());
		xyPlot.setRenderer(squareXyAreaRenderer);

//		for(int i = 0; i < xyDataset.getSeriesCount(); i++)
//		{
//			//xyAreaRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
//			//xyAreaRenderer.setSeriesPaint(i, gp[i]);
//			xyAreaRenderer.setSeriesShape(i, new Rectangle2D.Double(-3, -3, 6, 6));
//		}

		calculateTickUnits(xyPlot.getDomainAxis());
		calculateTickUnits(xyPlot.getRangeAxis());

		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createPieChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createPieChart(evaluation);

		PiePlot piePlot = (PiePlot)jfreeChart.getPlot();
		piePlot.setLabelBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		piePlot.setLabelShadowPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		piePlot.setLabelOutlinePaint(ChartThemesConstants.TRANSPARENT_PAINT);
		piePlot.setShadowXOffset(5);
		piePlot.setShadowYOffset(10);
		piePlot.setShadowPaint(new GradientPaint(0, chart.getHeight() / 2, new Color(41, 120, 162), 0, chart.getHeight(), Color.white));
		PieDataset pieDataset = piePlot.getDataset();
		for(int i = 0; i < pieDataset.getItemCount(); i++)
		{
			piePlot.setSectionOutlinePaint(pieDataset.getKey(i), ChartThemesConstants.TRANSPARENT_PAINT);
			//makes pie colors darker
			//piePlot.setSectionPaint(pieDataset.getKey(i), GRADIENT_PAINTS[i]);
		}

		if (
			((JRFillPieDataset)getDataset()).getLabelGenerator() == null
			&& ((JRFillPiePlot)getPlot()).getLabelFormat() == null
			)
		{
			piePlot.setLabelGenerator(null);
		}

		if (((JRFillPiePlot)getPlot()).getLegendLabelFormat() == null)
		{
			piePlot.setLegendLabelGenerator(
				new StandardPieSectionLabelGenerator("{0}")
				);
		}
//		piePlot.setLabelFont(new Font("Tahoma", Font.PLAIN, 4));
		
		piePlot.setCircular(true);
		return jfreeChart;
	}


	/**
	 *
	 */
	protected JFreeChart createPie3DChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createPie3DChart(evaluation);

		PiePlot3D piePlot3D = (PiePlot3D) jfreeChart.getPlot();
		piePlot3D.setLabelBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		piePlot3D.setLabelShadowPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		piePlot3D.setLabelOutlinePaint(ChartThemesConstants.TRANSPARENT_PAINT);
		piePlot3D.setDarkerSides(true);
		piePlot3D.setDepthFactor(0.1);
// does not work for 3D
//		piePlot3D.setShadowXOffset(5);
//		piePlot3D.setShadowYOffset(10);
//		piePlot3D.setShadowPaint(new GradientPaint(
//				0,
//				chart.getHeight() / 2,
//				new Color(41, 120, 162),
//				0,
//				chart.getHeight(),
//				Color.white)
//		);

		PieDataset pieDataset = piePlot3D.getDataset();

		for(int i = 0; i < pieDataset.getItemCount(); i++)
		{
			piePlot3D.setSectionOutlinePaint(pieDataset.getKey(i), ChartThemesConstants.TRANSPARENT_PAINT);
		}

		if (
			((JRFillPieDataset)getDataset()).getLabelGenerator() == null
			&& ((JRFillPie3DPlot)getPlot()).getLabelFormat() == null
			)
		{
			piePlot3D.setLabelGenerator(null);
		}

		if (((JRFillPie3DPlot)getPlot()).getLegendLabelFormat() == null)
		{
			piePlot3D.setLegendLabelGenerator(
				new StandardPieSectionLabelGenerator("{0}")
				);
		}
//		piePlot3D.setLabelFont(new Font("Tahoma", Font.PLAIN, 4));
		piePlot3D.setCircular(true);
		return jfreeChart;
	}


	/**
	 *
	 */
	protected JFreeChart createBarChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createBarChart(evaluation);

		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		//categoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
		BarRenderer barRenderer = (BarRenderer)categoryPlot.getRenderer();
		CategoryDataset categoryDataset = categoryPlot.getDataset();
		barRenderer.setItemMargin(0);

		barRenderer.setGradientPaintTransformer(
			new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
			);
		for(int i = 0; i < categoryDataset.getRowCount(); i++)
		{
			barRenderer.setSeriesPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
		}
		calculateTickUnits(categoryPlot.getRangeAxis());
		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createStackedBarChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createStackedBarChart(evaluation);

		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		//categoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
		BarRenderer barRenderer = (BarRenderer)categoryPlot.getRenderer();
		CategoryDataset categoryDataset = categoryPlot.getDataset();
		barRenderer.setItemMargin(0);
		barRenderer.setGradientPaintTransformer(
			new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
			);

		for(int i = 0; i < categoryDataset.getRowCount(); i++)
		{
			barRenderer.setSeriesPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
		}
		calculateTickUnits(categoryPlot.getRangeAxis());
		return jfreeChart;
	}

	protected JFreeChart createBar3DChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createBar3DChart(evaluation);
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		BarRenderer3D barRenderer3D = (BarRenderer3D)categoryPlot.getRenderer();

		barRenderer3D = new GradientBarRenderer3D(barRenderer3D);
		categoryPlot.setRenderer(barRenderer3D);

		barRenderer3D.setItemMargin(0);
		barRenderer3D.setWallPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		//categoryPlot.setOrientation(PlotOrientation.HORIZONTAL);

		CategoryDataset categoryDataset = categoryPlot.getDataset();
		barRenderer3D.setItemMargin(0);

		for(int i = 0; i < categoryDataset.getRowCount(); i++)
		{
			barRenderer3D.setSeriesPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
		}
		calculateTickUnits(categoryPlot.getRangeAxis());
		return jfreeChart;
	}


	protected JFreeChart createStackedBar3DChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createStackedBar3DChart(evaluation);
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		BarRenderer3D barRenderer3D = (BarRenderer3D)categoryPlot.getRenderer();
		barRenderer3D.setWallPaint(ChartThemesConstants.TRANSPARENT_PAINT);

		//CategoryDataset categoryDataset = categoryPlot.getDataset();
		barRenderer3D.setItemMargin(0);
		calculateTickUnits(categoryPlot.getRangeAxis());
		return jfreeChart;
	}


	/**
	 *
	 */
	protected JFreeChart createBubbleChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createBubbleChart(evaluation);

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		XYDataset xyDataset = xyPlot.getDataset();
		XYBubbleRenderer bubbleRenderer = (XYBubbleRenderer)xyPlot.getRenderer();
		bubbleRenderer = new GradientXYBubbleRenderer(bubbleRenderer.getScaleType());
		xyPlot.setRenderer(bubbleRenderer);
		for(int i = 0; i < xyDataset.getSeriesCount(); i++)
		{
			bubbleRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
			bubbleRenderer.setSeriesPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
		}
		calculateTickUnits(xyPlot.getDomainAxis());
		calculateTickUnits(xyPlot.getRangeAxis());
		return jfreeChart;
	}


	/**
	 *
	 */
	protected JFreeChart createXYBarChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createXYBarChart(evaluation);
		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		XYDataset xyDataset = xyPlot.getDataset();
		XYBarRenderer renderer = (XYBarRenderer)xyPlot.getRenderer();
		renderer.setMargin(0.1);
		renderer.setGradientPaintTransformer(
				new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
				);
		for(int i = 0; i < xyDataset.getSeriesCount(); i++)
		{
			renderer.setSeriesPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
		}
		calculateTickUnits(xyPlot.getDomainAxis());
		calculateTickUnits(xyPlot.getRangeAxis());
		return jfreeChart;
	}

	protected JFreeChart createHighLowChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createHighLowChart(evaluation);
		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		//xyPlot.setRangeTickBandPaint(new Color(231, 243, 255));
		calculateTickUnits(xyPlot.getDomainAxis());
		calculateTickUnits(xyPlot.getRangeAxis());
		return jfreeChart;
	}

	protected JFreeChart createScatterChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createScatterChart(evaluation);
		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		
		xyPlot.setRangeGridlinePaint(SCATTER_GRIDLINE_COLOR);
		xyPlot.setRangeGridlineStroke(new BasicStroke(0.75f));
		xyPlot.setDomainGridlinesVisible(true);
		xyPlot.setDomainGridlinePaint(SCATTER_GRIDLINE_COLOR);
		xyPlot.setDomainGridlineStroke(new BasicStroke(0.75f));
		xyPlot.setRangeZeroBaselinePaint(ChartThemesConstants.GRAY_PAINT_134);

		XYDataset xyDataset = xyPlot.getDataset();
		XYLineAndShapeRenderer lineRenderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();
		lineRenderer.setUseFillPaint(true);
		JRScatterPlot scatterPlot = (JRScatterPlot) getPlot();
		boolean isShowLines = scatterPlot.getShowLines() == null ? false : scatterPlot.getShowLines().booleanValue();
		lineRenderer.setBaseLinesVisible(isShowLines);
		for(int i = 0; i < xyDataset.getSeriesCount(); i++)
		{
			lineRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
			lineRenderer.setSeriesFillPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			lineRenderer.setSeriesPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_COLORS.get(i));
			//lineRenderer.setSeriesShape(i, new Ellipse2D.Double(-3, -3, 6, 6));
		}

		calculateTickUnits(xyPlot.getDomainAxis());
		calculateTickUnits(xyPlot.getRangeAxis());
		return jfreeChart;
	}

	protected JFreeChart createXyLineChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createXyLineChart(evaluation);
		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();

		XYLineAndShapeRenderer lineRenderer = (XYLineAndShapeRenderer) jfreeChart.getXYPlot().getRenderer();
		XYLine3DRenderer line3DRenderer = new XYLine3DRenderer();


		line3DRenderer.setBaseToolTipGenerator(lineRenderer.getBaseToolTipGenerator());
		line3DRenderer.setURLGenerator(lineRenderer.getURLGenerator());
		line3DRenderer.setBaseStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		line3DRenderer.setBaseLinesVisible(lineRenderer.getBaseLinesVisible());
		line3DRenderer.setBaseShapesVisible(lineRenderer.getBaseShapesVisible());

		XYDataset xyDataset = xyPlot.getDataset();
		for(int i = 0; i < xyDataset.getSeriesCount(); i++)
		{
			line3DRenderer.setSeriesStroke(i, new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			line3DRenderer.setSeriesLinesVisible(i, lineRenderer.getBaseLinesVisible());
			line3DRenderer.setSeriesShapesVisible(i, lineRenderer.getBaseShapesVisible());
		}
		line3DRenderer.setXOffset(2);
		line3DRenderer.setYOffset(2);
		line3DRenderer.setWallPaint(ChartThemesConstants.GRAY_PAINT_134);

		xyPlot.setRenderer(line3DRenderer);
		calculateTickUnits(xyPlot.getDomainAxis());
		calculateTickUnits(xyPlot.getRangeAxis());

		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createLineChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createLineChart(evaluation);
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer)categoryPlot.getRenderer();
		lineRenderer.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for(int i = 0; i < lineRenderer.getRowCount(); i++)
		{
			lineRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
			lineRenderer.setSeriesFillPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			lineRenderer.setSeriesPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			lineRenderer.setSeriesShapesVisible(i,true);
			//line3DRenderer.setSeriesLinesVisible(i,lineRenderer.getSeriesVisible(i));
		}
//		configureChart(jfreeChart, getPlot(), evaluation);
		calculateTickUnits(categoryPlot.getRangeAxis());
		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createGanttChart(byte evaluation) throws JRException
	{

		JFreeChart jfreeChart = super.createGanttChart(evaluation);
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		categoryPlot.setDomainGridlinesVisible(true);
		categoryPlot.setDomainGridlinePosition(CategoryAnchor.END);
		categoryPlot.setDomainGridlineStroke(new BasicStroke(
				0.5f,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER,
				50,
				new float[] {1},
				0
				)
		);

		categoryPlot.setDomainGridlinePaint(ChartThemesConstants.GRAY_PAINT_134);

		categoryPlot.setRangeGridlinesVisible(true);
		categoryPlot.setRangeGridlineStroke(new BasicStroke(
				0.5f,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER,
				50,
				new float[] {1},
				0
				)
		);

		categoryPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_134);
		categoryPlot.getDomainAxis().setTickLabelsVisible(
				//barPlot.isShowTickLabels()
				true
				);
		CategoryDataset categoryDataset = categoryPlot.getDataset();
		CategoryItemRenderer categoryRenderer = categoryPlot.getRenderer();
		categoryRenderer.setBaseItemLabelsVisible(true);
		BarRenderer barRenderer = (BarRenderer)categoryRenderer;
		barRenderer.setSeriesPaint(0, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_COLORS.get(3));
		barRenderer.setSeriesPaint(1, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_COLORS.get(0));
		for(int i = 0; i < categoryDataset.getRowCount(); i++)
		{
			barRenderer.setSeriesItemLabelFont(i, categoryPlot.getDomainAxis().getTickLabelFont());
			barRenderer.setSeriesItemLabelsVisible(i, true);
//			barRenderer.setSeriesPaint(i, GRADIENT_PAINTS[i]);
//			CategoryMarker categoryMarker = new CategoryMarker(categoryDataset.getColumnKey(i),MARKER_COLOR, new BasicStroke(1f));
//			categoryMarker.setAlpha(0.5f);
//			categoryPlot.addDomainMarker(categoryMarker, Layer.BACKGROUND);
		}
		categoryPlot.setOutlinePaint(Color.DARK_GRAY);
		categoryPlot.setOutlineStroke(new BasicStroke(1.5f));
		categoryPlot.setOutlineVisible(true);
		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createMeterChart(byte evaluation) throws JRException
	{
		return createDialChart(evaluation);
//		JRFillMeterPlot jrPlot = (JRFillMeterPlot)getPlot();
//
//		// Start by creating the plot that will hold the meter
//		MeterPlot chartPlot = new MeterPlot((ValueDataset)getDataset().getDataset());
//
//		// Set the shape
//		int shape = jrPlot.getShape();
//		if (shape == JRMeterPlot.SHAPE_CHORD)
//			chartPlot.setDialShape(DialShape.CHORD);
//		else if (shape == JRMeterPlot.SHAPE_CIRCLE)
//		chartPlot.setDialShape(DialShape.CIRCLE);
//		else
//			chartPlot.setDialShape(DialShape.PIE);
//
//		// Set the meter's range
//		chartPlot.setRange(convertRange(jrPlot.getDataRange(), evaluation));
//
//		// Set the size of the meter
//		chartPlot.setMeterAngle(jrPlot.getMeterAngle());
//
//		// Set the units - this is just a string that will be shown next to the
//		// value
//		String units = jrPlot.getUnits();
//		if (units != null && units.length() > 0)
//			chartPlot.setUnits(units);
//
//		// Set the spacing between ticks.  I hate the name "tickSize" since to me it
//		// implies I am changing the size of the tick, not the spacing between them.
//		chartPlot.setTickSize(jrPlot.getTickInterval());
//
//		JRValueDisplay display = jrPlot.getValueDisplay();
//		JRFont jrFont = display.getFont();
//
//		if (jrFont != null)
//		{
//			chartPlot.setTickLabelFont(new Font(JRFontUtil.getAttributes(jrFont)).deriveFont(Font.BOLD));
//		}
//
//
//		// Set all the colors we support
//		//Color color = jrPlot.getMeterBackgroundColor();
//		//if (color != null)
//
//		chartPlot.setDialBackgroundPaint(GRIDLINE_COLOR);
//
//		//chartPlot.setForegroundAlpha(1f);
//		chartPlot.setNeedlePaint(Color.DARK_GRAY);
//
//		// Set how the value is displayed.
//		if (display != null)
//		{
//			if (display.getColor() != null)
//			{
//				chartPlot.setValuePaint(display.getColor());
//			}
//
//			if (display.getMask() != null)
//			{
//				chartPlot.setTickLabelFormat(new DecimalFormat(display.getMask()));
//			}
//
//			if (jrFont != null)
//			{
//				Font font = new Font(JRFontUtil.getAttributes(jrFont));
//				if(jrFont.isOwnBold() == null)
//				{
//					font = font.deriveFont(Font.BOLD);
//				}
//				chartPlot.setValueFont(font);
//			}
//
//		}
//
//		chartPlot.setTickPaint(Color.BLACK);
//
//		// Now define all of the intervals, setting their range and color
//		List intervals = jrPlot.getIntervals();
//		if (intervals != null)
//		{
//			Iterator iter = intervals.iterator();
//			int i = 0;
//			while (iter.hasNext())
//			{
//				JRMeterInterval interval = (JRMeterInterval)iter.next();
//				interval.setBackgroundColor(COLORS[i]);
//				i++;
//				interval.setAlpha(1f);
//				chartPlot.addInterval(convertInterval(interval, evaluation));
//			}
//		}
//
//		// Actually create the chart around the plot
//		JFreeChart jfreeChart =
//			new JFreeChart(
//				(String)evaluateExpression(chart.getTitleExpression(), evaluation),
//				null,
//				chartPlot,
//				chart.isShowLegend()
//				);
//
//		// Set all the generic options
//		configureChart(jfreeChart, getPlot(), evaluation);
//
//		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createThermometerChart(byte evaluation) throws JRException
	{
		JRFillThermometerPlot jrPlot = (JRFillThermometerPlot)getPlot();

		// Create the plot that will hold the thermometer.
		ThermometerPlot chartPlot = new ThermometerPlot((ValueDataset)getDataset().getDataset());
		// Build a chart around this plot
		JFreeChart jfreeChart = new JFreeChart(chartPlot);

		// Set the generic options
		configureChart(jfreeChart, getPlot(), evaluation);
		jfreeChart.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		jfreeChart.setBorderVisible(false);

		Range range = convertRange(jrPlot.getDataRange(), evaluation);

		// Set the boundary of the thermomoter
		chartPlot.setLowerBound(range.getLowerBound());
		chartPlot.setUpperBound(range.getUpperBound());
		chartPlot.setGap(0);

		// Units can only be Fahrenheit, Celsius or none, so turn off for now.
		chartPlot.setUnits(ThermometerPlot.UNITS_NONE);

		// Set the color of the mercury.  Only used when the value is outside of
		// any defined ranges.
		Paint paint = (jrPlot.getMercuryColor() != null ? (Paint)jrPlot.getMercuryColor() : (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(0));
		chartPlot.setMercuryPaint(paint);

		chartPlot.setThermometerPaint(THERMOMETER_COLOR);
		chartPlot.setThermometerStroke(new BasicStroke(2f));
		chartPlot.setOutlineVisible(false);
		chartPlot.setValueFont(chartPlot.getValueFont().deriveFont(Font.BOLD));



		// Set the formatting of the value display
		JRValueDisplay display = jrPlot.getValueDisplay();
		if (display != null)
		{
			if (display.getColor() != null)
			{
				chartPlot.setValuePaint(display.getColor());
			}
			if (display.getMask() != null)
			{
				chartPlot.setValueFormat(new DecimalFormat(display.getMask()));
			}
			if (display.getFont() != null)
			{
//				chartPlot.setValueFont(JRFontUtil.getAwtFont(display.getFont()).deriveFont(Font.BOLD));
			}
		}

		// Set the location of where the value is displayed
		// Set the location of where the value is displayed
		byte valueLocation = jrPlot.getValueLocationByte() == null ? ThermometerPlot.BULB : jrPlot.getValueLocationByte().byteValue();
		switch (valueLocation)
		{
		  case JRThermometerPlot.LOCATION_NONE:
			 chartPlot.setValueLocation(ThermometerPlot.NONE);
			 break;
		  case JRThermometerPlot.LOCATION_LEFT:
			 chartPlot.setValueLocation(ThermometerPlot.LEFT);
			 break;
		  case JRThermometerPlot.LOCATION_RIGHT:
			 chartPlot.setValueLocation(ThermometerPlot.RIGHT);
			 break;
		  case JRThermometerPlot.LOCATION_BULB:
		  default:
			 chartPlot.setValueLocation(ThermometerPlot.BULB);
			 break;
		}

		// Define the three ranges
		range = convertRange(jrPlot.getLowRange(), evaluation);
		if (range != null)
		{
			chartPlot.setSubrangeInfo(2, range.getLowerBound(), range.getUpperBound());
		}

		range = convertRange(jrPlot.getMediumRange(), evaluation);
		if (range != null)
		{
			chartPlot.setSubrangeInfo(1, range.getLowerBound(), range.getUpperBound());
		}

		range = convertRange(jrPlot.getHighRange(), evaluation);
		if (range != null)
		{
			chartPlot.setSubrangeInfo(0, range.getLowerBound(), range.getUpperBound());
		}

		return jfreeChart;

//
//
//
//
//
//		JFreeChart jfreeChart = super.createThermometerChart(evaluation);
//		ThermometerPlot thermometerPlot = (ThermometerPlot)jfreeChart.getPlot();
//		thermometerPlot.setMercuryPaint(GRADIENT_PAINTS[0]);
//		thermometerPlot.setThermometerPaint(THERMOMETER_COLOR);
//		thermometerPlot.setThermometerStroke(new BasicStroke(2f));
//		thermometerPlot.setGap(2);
//		thermometerPlot.setForegroundAlpha(1f);
//		thermometerPlot.setValueFont(thermometerPlot.getValueFont().deriveFont(Font.BOLD));
//		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createDialChart(byte evaluation) throws JRException
	{

		JRFillMeterPlot jrPlot = (JRFillMeterPlot)getPlot();

		GradientPaint gp =
			new GradientPaint(
				new Point(), Color.LIGHT_GRAY,
				new Point(), Color.BLACK,
				false
				);

		GradientPaint gp2 =
			new GradientPaint(
				new Point(), Color.GRAY,
				new Point(), Color.BLACK
				);

		// get data for diagrams
		DialPlot dialPlot = new DialPlot();
		//dialPlot.setView(0.0, 0.0, 1.0, 1.0);
		dialPlot.setDataset((ValueDataset)getDataset().getDataset());
		StandardDialFrame dialFrame = new StandardDialFrame();
		//dialFrame.setRadius(0.60);
		//dialFrame.setBackgroundPaint(gp2);
		dialFrame.setForegroundPaint(gp2);
		dialPlot.setDialFrame(dialFrame);

		DialBackground db = new DialBackground(gp);
		db.setGradientPaintTransformer(new StandardGradientPaintTransformer(
				GradientPaintTransformType.VERTICAL));
		dialPlot.setBackground(db);
		JRValueDisplay display = jrPlot.getValueDisplay();
		JRFont jrFont = display != null  && display.getFont() != null ?
				display.getFont() :
				new JRBaseFont(null, null, chart, null);

		Range range = convertRange(jrPlot.getDataRange(), evaluation);
		double bound = Math.max(Math.abs(range.getUpperBound()), Math.abs(range.getLowerBound()));
		int dialUnitScale = ChartThemesUtilities.getScale(bound);

		double lowerBound = ChartThemesUtilities.getTruncatedValue(range.getLowerBound(), dialUnitScale);
		double upperBound = ChartThemesUtilities.getTruncatedValue(range.getUpperBound(), dialUnitScale);

		StandardDialScale scale =
			new StandardDialScale(
				lowerBound,
				upperBound,
				225,
				-270,
				(upperBound - lowerBound)/6,
				15
				);
		scale.setTickRadius(0.9);
		scale.setTickLabelOffset(0.16);
//		scale.setTickLabelFont(JRFontUtil.getAwtFont(jrFont).deriveFont(8f).deriveFont(Font.BOLD));
		scale.setMajorTickStroke(new BasicStroke(1f));
		scale.setMinorTickStroke(new BasicStroke(0.3f));
		scale.setMajorTickPaint(Color.WHITE);
		scale.setMinorTickPaint(Color.WHITE);

		scale.setTickLabelsVisible(true);
		scale.setFirstTickLabelVisible(true);
		if((lowerBound == (int)lowerBound &&
				upperBound == (int)upperBound &&
				scale.getMajorTickIncrement() == (int)scale.getMajorTickIncrement()) ||
				dialUnitScale > 1
				)
		{
			scale.setTickLabelFormatter(new DecimalFormat("#,##0"));
		}
		else if(dialUnitScale == 1)
		{

			scale.setTickLabelFormatter(new DecimalFormat("#,##0.0"));
		}
		else if(dialUnitScale <= 0)
		{
			scale.setTickLabelFormatter(new DecimalFormat("#,##0.00"));
		}

		dialPlot.addScale(0, scale);
		List intervals = jrPlot.getIntervals();
		if (intervals != null && intervals.size() > 0)
		{
			int size = Math.min(3, intervals.size());
			for(int i = 0; i < size; i++)
			{
				JRMeterInterval interval = (JRMeterInterval)intervals.get(i);
				Range intervalRange = convertRange(interval.getDataRange(), evaluation);
				double intervalLowerBound = ChartThemesUtilities.getTruncatedValue(intervalRange.getLowerBound(), dialUnitScale);
				double intervalUpperBound = ChartThemesUtilities.getTruncatedValue(intervalRange.getUpperBound(), dialUnitScale);

				ScaledDialRange dialRange =
					new ScaledDialRange(
						intervalLowerBound,
						intervalUpperBound,
						interval.getBackgroundColor() == null
							? (Color)ChartThemesConstants.AEGEAN_INTERVAL_COLORS.get(i)
							: interval.getBackgroundColor(),
						12f
						);
				dialRange.setInnerRadius(0.41);
				dialRange.setOuterRadius(0.41);
				dialPlot.addLayer(dialRange);
			}
			if(intervals.size() > 3)
			{
				int colorStep = 255 / (intervals.size() - 3);
				for(int i = 3; i < intervals.size(); i++)
				{
					JRMeterInterval interval = (JRMeterInterval)intervals.get(i);
					Range intervalRange = convertRange(interval.getDataRange(), evaluation);
					double intervalLowerBound = ChartThemesUtilities.getTruncatedValue(intervalRange.getLowerBound(), dialUnitScale);
					double intervalUpperBound = ChartThemesUtilities.getTruncatedValue(intervalRange.getUpperBound(), dialUnitScale);
	
					ScaledDialRange dialRange =
						new ScaledDialRange(
							intervalLowerBound,
							intervalUpperBound,
							interval.getBackgroundColor() == null
								? new Color(255 - colorStep * (i - 3), 0 + colorStep * (i - 3), 0)
								: interval.getBackgroundColor(),
							12f
							);
					dialRange.setInnerRadius(0.41);
					dialRange.setOuterRadius(0.41);
					dialPlot.addLayer(dialRange);
				}
			}
		}

        String displayVisibility = display != null && chart.hasProperties() ? 
        		chart.getPropertiesMap().getProperty("net.sf.jasperreports.chart.dial.value.display.visible") : "false";
        
        if(Boolean.valueOf(displayVisibility).booleanValue())
        {
        	ScaledDialValueIndicator dvi = new ScaledDialValueIndicator(0, dialUnitScale);
	        dvi.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
//	        dvi.setFont(JRFontUtil.getAwtFont(jrFont).deriveFont(10f).deriveFont(Font.BOLD));
	        dvi.setOutlinePaint(ChartThemesConstants.TRANSPARENT_PAINT);
	        dvi.setPaint(Color.WHITE);
	        
	        String pattern = display.getMask() != null ? display.getMask() : "#,##0.####";
	        if(pattern != null)
	        	dvi.setNumberFormat( new DecimalFormat(pattern));
	        dvi.setRadius(0.15);
	        dvi.setValueAnchor(RectangleAnchor.CENTER);
	        dvi.setTextAnchor(TextAnchor.CENTER);
	        //dvi.setTemplateValue(Double.valueOf(getDialTickValue(dialPlot.getValue(0),dialUnitScale)));
	        dialPlot.addLayer(dvi);
        }
		
		if(Boolean.parseBoolean(displayVisibility))
		{
			ScaledDialValueIndicator dvi = new ScaledDialValueIndicator(0, dialUnitScale);
			dvi.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
//			dvi.setFont(JRFontUtil.getAwtFont(jrFont).deriveFont(10f).deriveFont(Font.BOLD));
			dvi.setOutlinePaint(ChartThemesConstants.TRANSPARENT_PAINT);
			dvi.setPaint(Color.WHITE);

			String pattern = display.getMask() != null ? display.getMask() : "#,##0.####";
			if(pattern != null)
				dvi.setNumberFormat( new DecimalFormat(pattern));
			dvi.setRadius(0.15);
			dvi.setValueAnchor(RectangleAnchor.CENTER);
			dvi.setTextAnchor(TextAnchor.CENTER);
			//dvi.setTemplateValue(Double.valueOf(getDialTickValue(dialPlot.getValue(0),dialUnitScale)));
			dialPlot.addLayer(dvi);
		}

		String label = chart.hasProperties() ?
				chart.getPropertiesMap().getProperty("net.sf.jasperreports.chart.dial.label") : null;

		if(label != null)
		{
			if(dialUnitScale < 0)
				label = new MessageFormat(label).format(new Object[]{String.valueOf(Math.pow(10, dialUnitScale))});
			else if(dialUnitScale < 3)
				label = new MessageFormat(label).format(new Object[]{"1"});
			else
				label = new MessageFormat(label).format(new Object[]{String.valueOf((int)Math.pow(10, dialUnitScale-2))});

			String[] textLines = label.split("\\n");
			for(int i = 0; i < textLines.length; i++)
			{
				DialTextAnnotation dialAnnotation = new DialTextAnnotation(textLines[i]);
//				dialAnnotation.setFont(JRFontUtil.getAwtFont(jrFont).deriveFont(Font.BOLD));
				dialAnnotation.setPaint(Color.WHITE);
				dialAnnotation.setRadius(Math.sin(Math.PI/4.0) + i/10.0);
				dialAnnotation.setAnchor(TextAnchor.CENTER);
				dialPlot.addLayer(dialAnnotation);
			}
		}


		//DialPointer needle = new DialPointer.Pointer();
		Paint paint = new Color(191, 48, 0);
		DialPointer needle = new ScaledDialPointer(dialUnitScale, paint, paint);

		needle.setVisible(true);
		needle.setRadius(0.91);
		dialPlot.addLayer(needle);

		DialCap cap = new DialCap();
		cap.setRadius(0.05);
		cap.setFillPaint(Color.DARK_GRAY);
		cap.setOutlinePaint(Color.GRAY);
		cap.setOutlineStroke(new BasicStroke(0.5f));
		dialPlot.setCap(cap);

		JFreeChart jfreeChart =
		new JFreeChart(
			(String)evaluateExpression(chart.getTitleExpression(), evaluation),
			null,
			dialPlot,
			chart.getShowLegend() == null ? false : chart.getShowLegend().booleanValue()
			);

		// Set all the generic options
		configureChart(jfreeChart, getPlot(), evaluation);

		jfreeChart.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		jfreeChart.setBorderVisible(false);

		return jfreeChart;

	}

	/**
	 *
	 */
	protected JFreeChart createCandlestickChart(byte evaluation) throws JRException
	{
		JFreeChart jfreeChart = super.createCandlestickChart(evaluation);
		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		CandlestickRenderer renderer = (CandlestickRenderer)xyPlot.getRenderer();
		DefaultHighLowDataset dataset = (DefaultHighLowDataset)xyPlot.getDataset();

		for(int i = 0; i < dataset.getSeriesCount(); i++)
		{
			renderer.setSeriesFillPaint(i, (Paint)ChartThemesConstants.EYE_CANDY_SIXTIES_COLORS.get(i));
			renderer.setSeriesPaint(i, Color.DARK_GRAY);
		}
		calculateTickUnits(xyPlot.getDomainAxis());
		calculateTickUnits(xyPlot.getRangeAxis());
		return jfreeChart;
	}

	private void calculateTickUnits(Axis axis)
	{
		if(axis instanceof NumberAxis)
		{
			NumberAxis numberAxis = (NumberAxis)axis;
			int maxNumberOfTicks = 5;
			int axisRange = (int)numberAxis.getRange().getLength();
			int newTickUnitSize = axisRange/maxNumberOfTicks;
			int tickUnitSize = newTickUnitSize;

			//preferably multiple of 5 values should be used as tick units lengths:
			int i = 1;
			while(tickUnitSize > 9)
			{
				tickUnitSize /= 10;
				i *= 10;
			}
			tickUnitSize *= i;
			newTickUnitSize = tickUnitSize + i/2;

			if(axisRange/newTickUnitSize > maxNumberOfTicks)
			{
				newTickUnitSize += i/2;
			}
			if(numberAxis.getNumberFormatOverride() != null)
			{
				numberAxis.setTickUnit(new NumberTickUnit(newTickUnitSize, numberAxis.getNumberFormatOverride()));
			}
			else
			{
				numberAxis.setTickUnit(new NumberTickUnit(newTickUnitSize));
			}
		}
	}
}

class SquareXYAreaRenderer extends XYAreaRenderer
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public SquareXYAreaRenderer(XYAreaRenderer parent)
	{
		super(XYAreaRenderer.AREA, parent.getToolTipGenerator(), parent.getURLGenerator());
	}

	public LegendItem getLegendItem(int datasetIndex, int series)
	{
		setLegendArea(new Rectangle2D.Double(-3, -3, 6, 6));
		return super.getLegendItem(datasetIndex, series);
	}
}


class GradientXYBubbleRenderer extends XYBubbleRenderer
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public GradientXYBubbleRenderer(int scaleType)
	{
		super(scaleType);
	}

	public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass)
	{
		// return straight away if the item is not visible
		if (!getItemVisible(series, item)) {
			return;
		}

		PlotOrientation orientation = plot.getOrientation();

		// get the data point...
		double x = dataset.getXValue(series, item);
		double y = dataset.getYValue(series, item);
		double z = Double.NaN;
		if (dataset instanceof XYZDataset) {
			XYZDataset xyzData = (XYZDataset) dataset;
			z = xyzData.getZValue(series, item);
		}
		if (!Double.isNaN(z)) {
			RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
			RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
			double transX = domainAxis.valueToJava2D(x, dataArea,
					domainAxisLocation);
			double transY = rangeAxis.valueToJava2D(y, dataArea,
					rangeAxisLocation);

			double transDomain = 0.0;
			double transRange = 0.0;
			double zero;

			switch(getScaleType()) {
				case SCALE_ON_DOMAIN_AXIS:
					zero = domainAxis.valueToJava2D(0.0, dataArea,
							domainAxisLocation);
					transDomain = domainAxis.valueToJava2D(z, dataArea,
							domainAxisLocation) - zero;
					transRange = transDomain;
					break;
				case SCALE_ON_RANGE_AXIS:
					zero = rangeAxis.valueToJava2D(0.0, dataArea,
							rangeAxisLocation);
					transRange = zero - rangeAxis.valueToJava2D(z, dataArea,
							rangeAxisLocation);
					transDomain = transRange;
					break;
				default:
					double zero1 = domainAxis.valueToJava2D(0.0, dataArea,
							domainAxisLocation);
					double zero2 = rangeAxis.valueToJava2D(0.0, dataArea,
							rangeAxisLocation);
					transDomain = domainAxis.valueToJava2D(z, dataArea,
							domainAxisLocation) - zero1;
					transRange = zero2 - rangeAxis.valueToJava2D(z, dataArea,
							rangeAxisLocation);
			}
			transDomain = Math.abs(transDomain);
			transRange = Math.abs(transRange);
			Ellipse2D circle = null;
			if (orientation == PlotOrientation.VERTICAL) {
				circle = new Ellipse2D.Double(transX - transDomain / 2.0,
						transY - transRange / 2.0, transDomain, transRange);
			}
			else if (orientation == PlotOrientation.HORIZONTAL) {
				circle = new Ellipse2D.Double(transY - transRange / 2.0,
						transX - transDomain / 2.0, transRange, transDomain);
			}

			Paint paint = getItemPaint(series, item);
			if (paint instanceof GradientPaint)
			{
				paint = new StandardGradientPaintTransformer().transform((GradientPaint)paint, circle);
			}
			g2.setPaint(paint);
			g2.fill(circle);
			g2.setStroke(getItemOutlineStroke(series, item));
			g2.setPaint(getItemOutlinePaint(series, item));
			g2.draw(circle);

			if (isItemLabelVisible(series, item)) {
				if (orientation == PlotOrientation.VERTICAL) {
					drawItemLabel(g2, orientation, dataset, series, item,
							transX, transY, false);
				}
				else if (orientation == PlotOrientation.HORIZONTAL) {
					drawItemLabel(g2, orientation, dataset, series, item,
							transY, transX, false);
				}
			}

			// add an entity if this info is being collected
			EntityCollection entities = null;
			if (info != null) {
				entities = info.getOwner().getEntityCollection();
				if (entities != null && circle.intersects(dataArea)) {
					addEntity(entities, circle, dataset, series, item,
							circle.getCenterX(), circle.getCenterY());
				}
			}

			int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
			int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
			updateCrosshairValues(crosshairState, x, y, domainAxisIndex,
					rangeAxisIndex, transX, transY, orientation);
		}
	}
};

class GradientBarRenderer3D extends BarRenderer3D
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public GradientBarRenderer3D(BarRenderer3D barRenderer3D)
	{
		super(barRenderer3D.getXOffset(), barRenderer3D.getYOffset());
		setBaseItemLabelGenerator(barRenderer3D.getBaseItemLabelGenerator());
		setBaseItemLabelsVisible(barRenderer3D.getBaseItemLabelsVisible());
	}

	public void drawItem(Graphics2D g2,
		CategoryItemRendererState state,
		Rectangle2D dataArea,
		CategoryPlot plot,
		CategoryAxis domainAxis,
		ValueAxis rangeAxis,
		CategoryDataset dataset,
		int row,
		int column,
		int pass)
	{

		// check the value we are plotting...
		Number dataValue = dataset.getValue(row, column);
		if (dataValue == null) {
			return;
		}

		double value = dataValue.doubleValue();

		Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
			dataArea.getY() + getYOffset(),
			dataArea.getWidth() - getXOffset(),
			dataArea.getHeight() - getYOffset());

		PlotOrientation orientation = plot.getOrientation();

		double barW0 = calculateBarW0(plot, orientation, adjusted, domainAxis,
			state, row, column);
		double[] barL0L1 = calculateBarL0L1(value);
		if (barL0L1 == null) {
			return;  // the bar is not visible
		}

		RectangleEdge edge = plot.getRangeAxisEdge();
		double transL0 = rangeAxis.valueToJava2D(barL0L1[0], adjusted, edge);
		double transL1 = rangeAxis.valueToJava2D(barL0L1[1], adjusted, edge);
		double barL0 = Math.min(transL0, transL1);
		double barLength = Math.abs(transL1 - transL0);

		// draw the bar...
		Rectangle2D bar = null;
		if (orientation == PlotOrientation.HORIZONTAL) {
			bar = new Rectangle2D.Double(barL0, barW0, barLength, state.getBarWidth());
		}
		else {
			bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), barLength);
		}
		Paint itemPaint = getItemPaint(row, column);
		if (itemPaint instanceof GradientPaint)
		{
			itemPaint = getGradientPaintTransformer().transform((GradientPaint)itemPaint, bar);
		}
		g2.setPaint(itemPaint);
		g2.fill(bar);

		double x0 = bar.getMinX();
		double x1 = x0 + getXOffset();
		double x2 = bar.getMaxX();
		double x3 = x2 + getXOffset();

		double y0 = bar.getMinY() - getYOffset();
		double y1 = bar.getMinY();
		double y2 = bar.getMaxY() - getYOffset();
		double y3 = bar.getMaxY();

		GeneralPath bar3dRight = null;
		GeneralPath bar3dTop = null;
		if (barLength > 0.0) {
			bar3dRight = new GeneralPath();
			bar3dRight.moveTo((float) x2, (float) y3);
			bar3dRight.lineTo((float) x2, (float) y1);
			bar3dRight.lineTo((float) x3, (float) y0);
			bar3dRight.lineTo((float) x3, (float) y2);
			bar3dRight.closePath();

			if (itemPaint instanceof Color) {
				g2.setPaint(((Color) itemPaint).darker());
			}
			else if (itemPaint instanceof GradientPaint)
			{
				GradientPaint gp = (GradientPaint)itemPaint;
				g2.setPaint(
					new StandardGradientPaintTransformer().transform(
						new GradientPaint(gp.getPoint1(), gp.getColor1().darker(), gp.getPoint2(), gp.getColor2().darker(), gp.isCyclic()),
						bar3dRight
						)
					);
			}
			g2.fill(bar3dRight);
		}

		bar3dTop = new GeneralPath();
		bar3dTop.moveTo((float) x0, (float) y1);
		bar3dTop.lineTo((float) x1, (float) y0);
		bar3dTop.lineTo((float) x3, (float) y0);
		bar3dTop.lineTo((float) x2, (float) y1);
		bar3dTop.closePath();
		g2.fill(bar3dTop);

		if (isDrawBarOutline()
			&& state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) 
		{
			g2.setStroke(getItemOutlineStroke(row, column));
			g2.setPaint(getItemOutlinePaint(row, column));
			g2.draw(bar);
			if (bar3dRight != null) {
				g2.draw(bar3dRight);
			}
			if (bar3dTop != null) {
				g2.draw(bar3dTop);
			}
		}

		CategoryItemLabelGenerator generator
		= getItemLabelGenerator(row, column);
		if (generator != null && isItemLabelVisible(row, column)) {
			drawItemLabel(g2, dataset, row, column, plot, generator, bar, (value < 0.0));
		}

		// add an item entity, if this information is being collected
		EntityCollection entities = state.getEntityCollection();
		if (entities != null) {
			GeneralPath barOutline = new GeneralPath();
			barOutline.moveTo((float) x0, (float) y3);
			barOutline.lineTo((float) x0, (float) y1);
			barOutline.lineTo((float) x1, (float) y0);
			barOutline.lineTo((float) x3, (float) y0);
			barOutline.lineTo((float) x3, (float) y2);
			barOutline.lineTo((float) x2, (float) y3);
			barOutline.closePath();
			addItemEntity(entities, dataset, row, column, barOutline);
		}
	}

};
