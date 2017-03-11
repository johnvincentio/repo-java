package net.sf.jasperreports.charts.themes;

import java.util.Map;

import net.sf.jasperreports.charts.ChartTheme;
import net.sf.jasperreports.charts.ChartThemeBundle;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net) 
 * @version $Id: ChartThemeMapBundle.java 2505 2009-01-06 12:58:38Z shertage $
 */

public class ChartThemeMapBundle implements ChartThemeBundle
{

	private Map themes;
	
	public ChartTheme getChartTheme(String themeName)
	{
		return (ChartTheme) themes.get(themeName);
	}

	public String[] getChartThemeNames()
	{
		return (String[]) themes.keySet().toArray(new String[themes.size()]);
	}

	/**
     * @return the themes
     */
    public Map getThemes()
    {
    	return themes;
    }

	/**
     * @param themes the themes to set
     */
    public void setThemes(Map themes)
    {
    	this.themes = themes;
    }

}
