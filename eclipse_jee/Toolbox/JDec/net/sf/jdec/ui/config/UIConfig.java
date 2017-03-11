
/*
 *  UIConfig.java Copyright (c) 2006,07 Swaroop Belur
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
package net.sf.jdec.ui.config;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.ui.util.highlight.CategoryChooser;
import net.sf.jdec.ui.util.highlight.ColorChooser;

import java.awt.*;
import java.io.*;
import java.util.*;


public class UIConfig {


	private static UIConfig config=null;

	java.lang.String showConsole="true";
	java.lang.String showFileDetail="true";
	java.lang.String showStatusWindow="true";
	java.lang.String lookAndFeelType="windows";
	java.lang.String showAuxToolBar="true"; // Auxiliary

	private UIConfig()
	{
		loadDefaultPrefs();
	}

	public static UIConfig getUIConfig()
	{
		if(config==null)
		{
			config=new UIConfig();
			return config;
		}
		else
			return config;

	}


	public void registerUIConfigInfo()
	{

		ResourceBundle rb=ResourceBundle.getBundle("uiconfig");
		readAllParameters(rb);
	}

	private void readAllParameters(ResourceBundle rb)
	{
		showConsole=rb.getString("Show_Console_Window").trim();
		showFileDetail=rb.getString("Show_FileDetail_Window").trim();
		showStatusWindow=rb.getString("Show_Status_Window").trim();
		lookAndFeelType=rb.getString("Look_N_Feel_Type").trim();
		showAuxToolBar=rb.getString("Show_Aux_Toolbar").trim();
	}

	Hashtable uiprefs=new Hashtable();

	private void loadDefaultPrefs()
	{
        addPref("JdecVerSion","2.0");
		addPref("ShowTip","true");
	}




	public void addPref(String name,String value)
	{
		
		uiprefs.put(name,value);
	
    }
    
	public String getPref(String name)
	{
		Iterator it=uiprefs.keySet().iterator();
		while(it.hasNext())
		{
			String k=(String)it.next();
			if(k.equalsIgnoreCase(name))
			{
				return (String)uiprefs.get(name);
			}
		}
		return null;

	}

	public synchronized void persistToFile() throws IOException
	{
		checkForMissingPrefs(uiprefs);
        
		String path=System.getProperty("user.home")+File.separator+"JDecUserPreferences.txt";
		File file=new File(path);
		if(!file.exists())
		{
			file.createNewFile();
		}
		else
		{
			file.delete();
			file.createNewFile();
		}
		
		// For Favorite List
		String fl=getFavoriteListAsString();
		if(fl.length()!=0)
		{
			uiprefs.put("user_favorite_folder_list",fl);
		}
		uiprefs.put("JdecVerSion","2.0");
		Iterator it=uiprefs.entrySet().iterator();
		BufferedWriter br=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		while(it.hasNext())
		{
			Map.Entry entry=(Map.Entry)it.next();
			String key=(String)entry.getKey();
			String val=(String)entry.getValue();
			br.write(key+"="+val);
			br.write("\n");
		}
		br.flush();
		br.close();

	}
	private boolean showTip=false;

	/**
	 * @return Returns the showTip.
	 */
	public boolean showTip() {
		return showTip;
	}
	/**
	 * @param showTip The showTip to set.
	 */
	public void setShowTip(boolean showTip) {
		this.showTip = showTip;
	}
	public void readUserPrefs() throws IOException
	{
        boolean jdecVersionPresent=false;
		
		String level=UILauncher.getUIutil().getLogLevel();
		String m="Reading user preferences...";
		
		String path=System.getProperty("user.home")+File.separator+"JDecUserPreferences.txt";
		File file=new File(path);
		if(!file.exists())
		{
			showTip=true;
		}
        else
		{
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String l=br.readLine();
			if(l==null)
			{
				showTip=true;
				return;
			}
			String s=l.trim();
			while(s!=null)
			{
				if(s.indexOf("=")!=-1)
				{
					String key=s.substring(0,s.indexOf("=")).trim();
					String value=s.substring(s.indexOf("=")+1).trim();
					if(key.equalsIgnoreCase("all_classes_in_archive"))
					{
						setAll_classes_in_archive(value);
					}
					if(key.equalsIgnoreCase("only_default_classes_in_archive"))
					{
						setOnly_default_classes_in_archive(value);
					}
					if(key.equalsIgnoreCase("enclosed_archives_in_archive"))
					{
						setEnclosed_archives_in_archive(value);
					}

                    if(key.equalsIgnoreCase("JdecVerSion"))
					{
						uiprefs.put(key,value);
                        jdecVersionPresent=true;
						
                        if(value!=null && !value.trim().equals("2.0"))
                        {
                          showTip=true;
                        }
					}

					if(key.equalsIgnoreCase("userprojectHome"))
					{
						UILauncher.getUIutil().setUserProjFol(value);
						uiprefs.put(key,value);
						
						
					}
					if(key.equalsIgnoreCase("browserPath"))
					{
						UILauncher.getUIutil().setBrowserPath(new File(value));
						uiprefs.put(key,value);
						
					}
					if(key.equalsIgnoreCase("JavaHome"))
					{
						UILauncher.getUIutil().setJavaFolderPath(new File(value));
						uiprefs.put(key,value);
						
					}
					if(key.equalsIgnoreCase("ShowTip"))
					{
						//UILauncher.getUIutil().setShowTip(value);
						uiprefs.put(key,value);
						
					}
					if(key.equalsIgnoreCase("DefaultLNKFeel"))
					{
						UILauncher.getUIutil().setDefaultLnkNFeelName(value);
						uiprefs.put(key,value);
						
					}
                                        if(key.equalsIgnoreCase("ShowWelcomeAtStartUp"))
					{
						UILauncher.getUIutil().setShowWelcomeScreenAtStartUp(value);
						uiprefs.put(key,value);
						
					}
					if(key.equalsIgnoreCase("DONTDECPVTMET"))
					{
						uiprefs.put(key,value);
						
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setDontpvtmethods(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setDontpvtmethods(false);
						}

					}
					if(key.equalsIgnoreCase("DONTSHOWABSMET"))
					{
						uiprefs.put(key,value);
						
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setDontshowabs(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setDontshowabs(false);
						}

					}
					if(key.equalsIgnoreCase("DONTDECSYNMET"))
					{
						uiprefs.put(key,value);
						
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setDontsynth(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setDontsynth(false);
						}

					}
					if(key.equalsIgnoreCase("DONTDECSTATICINIT"))
					{
						uiprefs.put(key,value);
						
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setDontstaticinit(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setDontstaticinit(false);
						}
					}
					if(key.equalsIgnoreCase("DONTSHOWEMPCONS"))
					{
						uiprefs.put(key,value);
						
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setDontshowemptyconst(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setDontshowemptyconst(false);
						}
					}
					if(key.equalsIgnoreCase("DONTSHOWNAT"))
					{
						uiprefs.put(key,value);
						
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setDontshownative(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setDontshownative(false);
						}
					}
					
					
					if(key.equalsIgnoreCase("SHOWFIELDSFIRST"))
					{
						uiprefs.put(key,value);
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setShowfieldsfirst(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setShowfieldsfirst(true);
						}
					}
					if(key.equalsIgnoreCase("SHOWEXTENDSOBJECT"))
					{
						uiprefs.put(key,value);
						
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setShowObjectSuperClass(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setShowObjectSuperClass(true);
						}
					}
					if(key.equalsIgnoreCase("SHOWCONSTFIRST"))
					{
						uiprefs.put(key,value);
						
						try
						{
							boolean z=new Boolean(value).booleanValue();
							ConsoleLauncher.setShowconstfirst(z);
						}
						catch(Exception e)
						{
							ConsoleLauncher.setShowconstfirst(false);
						}

					}
					if(key.equalsIgnoreCase("INCLUDEDPKGS"))
					{
                        if(value!=null && value.equals("[]")==false)
                        {
                            uiprefs.put(key,value);
                        
                            StringTokenizer tokens=new StringTokenizer(value,",");
                            ArrayList includedList=new ArrayList();
                            while(tokens.hasMoreTokens())
                            {
                                java.lang.String str=(java.lang.String)tokens.nextToken();
                                includedList.add(str);
                            }
                            ConsoleLauncher.setInclListInJar(includedList);
                        }
					}

					if(key.equalsIgnoreCase("EXCLUDEDPKGSINIMPORT"))
					{
                        if(value!=null && value.equals("[]")==false)
                        {
                            uiprefs.put(key,value);
                        
                            StringTokenizer tokens=new StringTokenizer(value,",");
                            ArrayList exclList=new ArrayList();
                            while(tokens.hasMoreTokens())
                            {
                                java.lang.String str=(java.lang.String)tokens.nextToken();
                                exclList.add(str);
                            }
                            ConsoleLauncher.setExclImpList(exclList);
                        }
					}
					if(key.equalsIgnoreCase("keyword_backg_color"))
					{
						 Color c=getColor(value);
						 UILauncher.getUIConfigRef().setCurrentBackGrndColor_KEYWORD(c);
					}
					if(key.equalsIgnoreCase("keyword_foreg_color"))
					{
						Color c=getColor(value);
						UILauncher.getUIConfigRef().setCurrentForeGrndColor_KEYWORD(c);
					}
					if(key.equalsIgnoreCase("keyword_effect"))
					{
						UILauncher.getUIConfigRef().setEffectKEYWORD(value);
					}

					if(key.equalsIgnoreCase("number_backg_color"))
					{
						Color c=getColor(value);
						UILauncher.getUIConfigRef().setCurrentBackGrndColor_NUMBER(c);
					}
					if(key.equalsIgnoreCase("number_foreg_color"))
					{
						Color c=getColor(value);
						UILauncher.getUIConfigRef().setCurrentForeGrndColor_NUMBER(c);
					}
					if(key.equalsIgnoreCase("number_effect"))
					{
						UILauncher.getUIConfigRef().setEffectNUMBER(value);
					}

					if(key.equalsIgnoreCase("operator_backg_color"))
					{
						 Color c=getColor(value);
						 UILauncher.getUIConfigRef().setCurrentBackGrndColor_OPERATOR(c);
					}
					if(key.equalsIgnoreCase("operator_foreg_color"))
					{
						  Color c=getColor(value);
						  UILauncher.getUIConfigRef().setCurrentForeGrndColor_OPERATOR(c);
					}
					if(key.equalsIgnoreCase("operator_effect"))
					{
						UILauncher.getUIConfigRef().setEffectOP(value);
					}
					if(key.equalsIgnoreCase("string_backg_color"))
					{
						Color c=getColor(value);
						UILauncher.getUIConfigRef().setCurrentBackGrndColor_STRING(c);
					}
					if(key.equalsIgnoreCase("string_foreg_color"))
					{
						Color c=getColor(value);
						UILauncher.getUIConfigRef().setCurrentForeGrndColor_STRING(c);
					}
					if(key.equalsIgnoreCase("string_effect"))
					{
						UILauncher.getUIConfigRef().setEffectSTRING(value);
					}
					
					if(key.equalsIgnoreCase("ann_backg_color"))
					{
						Color c=getColor(value);
						UILauncher.getUIConfigRef().setCurrentBackGrndColor_ANN(c);
					}
					if(key.equalsIgnoreCase("ann_foreg_color"))
					{
						Color c=getColor(value);
						UILauncher.getUIConfigRef().setCurrentForeGrndColor_ANN(c);
					}
					if(key.equalsIgnoreCase("ann_effect"))
					{
						UILauncher.getUIConfigRef().setEffectANN(value);
					}
                    if(key.equalsIgnoreCase("enableSyntax"))
					{
						UILauncher.getUIConfigRef().setSyntaxEnabled(value);
					}

                    if(key.equalsIgnoreCase("FavoriteEditor"))
					{
						UILauncher.getUIConfigRef().setCustomeEditorPath(value);
					}

                    if(key.equalsIgnoreCase("font_family_kwd"))
					{
						UILauncher.getUIConfigRef().setFontFamilykwd(value);
					}
                    if(key.equalsIgnoreCase("font_family_num"))
					{
						UILauncher.getUIConfigRef().setFontFamilynum(value);
					}
                    if(key.equalsIgnoreCase("font_family_op"))
					{
						UILauncher.getUIConfigRef().setFontFamilyop(value);
					}
                    if(key.equalsIgnoreCase("font_family_str"))
					{
						UILauncher.getUIConfigRef().setFontFamilystr(value);
					}
                    if(key.equalsIgnoreCase("font_family_ann"))
					{
						UILauncher.getUIConfigRef().setFontFamilyann(value);
					}


                    if(key.equalsIgnoreCase("font_size_kwd"))
					{
						UILauncher.getUIConfigRef().setFontSizekwd(value);
					}
                    if(key.equalsIgnoreCase("font_size_num"))
					{
						UILauncher.getUIConfigRef().setFontSizenum(value);
					}
                    if(key.equalsIgnoreCase("font_size_op"))
					{
						UILauncher.getUIConfigRef().setFontSizeop(value);
					}
                    if(key.equalsIgnoreCase("font_size_str"))
					{
						UILauncher.getUIConfigRef().setFontSizestr(value);
					}
                    if(key.equalsIgnoreCase("font_size_ann"))
					{
						UILauncher.getUIConfigRef().setFontSizeann(value);
					}
                    if(key.equalsIgnoreCase("registered_archive_types"))
					{
                    	/*ArrayList list=new ArrayList();
                    	String v=value;
                    	if(v!=null)
                    	{
                    		StringTokenizer all=new StringTokenizer(v,",");
                    		while(all.hasMoreTokens())
                    		{
                    			String t=(String)all.nextToken();
                    			list.add(t);
                    		}
                    	}
                    	else
                    	{
                    		list.add("jar");
                    		list.add("zip");
                    	}
                    	ArchiveSettings.currentlist=list;*/
                    	setArchiveTypes(value);
                    	
					}
                    if(key.equalsIgnoreCase("user_favorite_folder_list"))
                    {
                    	formFavListFromFavString(value);
                    }


				}

				// note: Add here Other entries
				String tmp=br.readLine();
				if(tmp==null)break;
				s=tmp.trim();

			}

            if(jdecVersionPresent==false)
            {
                showTip=true;
            }


		}
		
	}

	
	private String all_classes_in_archive="true";
	private String only_default_classes_in_archive="false";
	private String enclosed_archives_in_archive="false";
	
	 
	
	
	private String archiveTypes="jar,zip";
	
	public void setArchiveTypes(String list)
	{
		archiveTypes=list;
	}
	
	public String getArchiveTypes()
	{
		return archiveTypes;
	}
	
	public Object removePref(java.lang.String key)
	{
		Object o=uiprefs.remove(key);
		return o;
	}




    /***
	 * belurs:
	 * Methods Used by Syntax module
	 *
	 **/

     public Color getCurrentForeGrndColor_KEYWORD_PREVW() {
        return currentForeGrndColor_KEYWORD_PREVW;
    }

    public void setCurrentForeGrndColor_KEYWORD_PREVW(Color currentForeGrndColor_KEYWORD_PREVW) {
        this.currentForeGrndColor_KEYWORD_PREVW = currentForeGrndColor_KEYWORD_PREVW;
    }

    public Color getCurrentBackGrndColor_KEYWORD_PREVW() {
        return currentBackGrndColor_KEYWORD_PREVW;
    }

    public void setCurrentBackGrndColor_KEYWORD_PREVW(Color currentBackGrndColor_KEYWORD_PREVW) {
        this.currentBackGrndColor_KEYWORD_PREVW = currentBackGrndColor_KEYWORD_PREVW;
    }

   private Color currentForeGrndColor_KEYWORD_PREVW=null;
	private Color currentBackGrndColor_KEYWORD_PREVW=null;

	private Color currentForeGrndColor_KEYWORD=null;
	private Color currentBackGrndColor_KEYWORD=null;

	public Color getCurrentBackGrndColor_KEYWORD() {
		return currentBackGrndColor_KEYWORD;
	}

	public void setCurrentBackGrndColor_KEYWORD(Color currentBackGrndColor_KEYWORD) {
		this.currentBackGrndColor_KEYWORD = currentBackGrndColor_KEYWORD;
	}


	public Color getCurrentForeGrndColor_KEYWORD() {
		return currentForeGrndColor_KEYWORD;
	}

	public void setCurrentForeGrndColor_KEYWORD(Color currentForeGrndColor_KEYWORD) {
		this.currentForeGrndColor_KEYWORD = currentForeGrndColor_KEYWORD;
	}


    public Color getCurrentForeGrndColor_STRING_PREVW() {
        return currentForeGrndColor_STRING_PREVW;
    }

    public void setCurrentForeGrndColor_STRING_PREVW(Color currentForeGrndColor_STRING_PREVW) {
        this.currentForeGrndColor_STRING_PREVW = currentForeGrndColor_STRING_PREVW;
    }

    public Color getCurrentBackGrndColor_STRING_PREVW() {
        return currentBackGrndColor_STRING_PREVW;
    }

    public void setCurrentBackGrndColor_STRING_PREVW(Color currentBackGrndColor_STRING_PREVW) {
        this.currentBackGrndColor_STRING_PREVW = currentBackGrndColor_STRING_PREVW;
    }

    private Color currentForeGrndColor_STRING_PREVW=null;
	private Color currentBackGrndColor_STRING_PREVW=null;

	private Color currentForeGrndColor_STRING=null;
	private Color currentBackGrndColor_STRING=null;
	
	
	private Color currentForeGrndColor_ANN_PREVW=null;
	private Color currentBackGrndColor_ANN_PREVW=null;

	private Color currentForeGrndColor_ANN=null;
	private Color currentBackGrndColor_ANN=null;


	public Color getCurrentBackGrndColor_ANN() {
		return currentBackGrndColor_ANN;
	}

	public void setCurrentBackGrndColor_ANN(Color currentBackGrndColor_ANN) {
		this.currentBackGrndColor_ANN = currentBackGrndColor_ANN;
	}

	public Color getCurrentBackGrndColor_ANN_PREVW() {
		return currentBackGrndColor_ANN_PREVW;
	}

	public void setCurrentBackGrndColor_ANN_PREVW(
			Color currentBackGrndColor_ANN_PREVW) {
		this.currentBackGrndColor_ANN_PREVW = currentBackGrndColor_ANN_PREVW;
	}

	public Color getCurrentForeGrndColor_ANN() {
		return currentForeGrndColor_ANN;
	}

	public void setCurrentForeGrndColor_ANN(Color currentForeGrndColor_ANN) {
		this.currentForeGrndColor_ANN = currentForeGrndColor_ANN;
	}

	public Color getCurrentForeGrndColor_ANN_PREVW() {
		return currentForeGrndColor_ANN_PREVW;
	}

	public void setCurrentForeGrndColor_ANN_PREVW(
			Color currentForeGrndColor_ANN_PREVW) {
		this.currentForeGrndColor_ANN_PREVW = currentForeGrndColor_ANN_PREVW;
	}

	public Color getCurrentBackGrndColor_STRING() {
		return currentBackGrndColor_STRING;
	}

	public void setCurrentBackGrndColor_STRING(Color currentBackGrndColor_STRING) {
		this.currentBackGrndColor_STRING = currentBackGrndColor_STRING;
	}

	public Color getCurrentForeGrndColor_STRING() {
		return currentForeGrndColor_STRING;
	}

	public void setCurrentForeGrndColor_STRING(Color currentForeGrndColor_STRING) {
		this.currentForeGrndColor_STRING = currentForeGrndColor_STRING;
	}


    public Color getCurrentForeGrndColor_NUMBER_PREVW() {
        return currentForeGrndColor_NUMBER_PREVW;
    }

    public void setCurrentForeGrndColor_NUMBER_PREVW(Color currentForeGrndColor_NUMBER_PREVW) {
        this.currentForeGrndColor_NUMBER_PREVW = currentForeGrndColor_NUMBER_PREVW;
    }

    public Color getCurrentBackGrndColor_NUMBER_PREVW() {
        return currentBackGrndColor_NUMBER_PREVW;
    }

    public void setCurrentBackGrndColor_NUMBER_PREVW(Color currentBackGrndColor_NUMBER_PREVW) {
        this.currentBackGrndColor_NUMBER_PREVW = currentBackGrndColor_NUMBER_PREVW;
    }

    private Color currentForeGrndColor_NUMBER_PREVW=null;
	private Color currentBackGrndColor_NUMBER_PREVW=null;

	private Color currentForeGrndColor_NUMBER=null;
	private Color currentBackGrndColor_NUMBER=null;

	public Color getCurrentBackGrndColor_NUMBER() {
		return currentBackGrndColor_NUMBER;
	}

	public void setCurrentBackGrndColor_NUMBER(Color currentBackGrndColor_NUMBER) {
		this.currentBackGrndColor_NUMBER = currentBackGrndColor_NUMBER;
	}

	public Color getCurrentForeGrndColor_NUMBER() {
		return currentForeGrndColor_NUMBER;
	}

	public void setCurrentForeGrndColor_NUMBER(Color currentForeGrndColor_NUMBER) {
		this.currentForeGrndColor_NUMBER = currentForeGrndColor_NUMBER;
	}

    public Color getCurrentForeGrndColor_OPERATOR_PREVW() {
        return currentForeGrndColor_OPERATOR_PREVW;
    }

    public void setCurrentForeGrndColor_OPERATOR_PREVW(Color currentForeGrndColor_OPERATOR_PREVW) {
        this.currentForeGrndColor_OPERATOR_PREVW = currentForeGrndColor_OPERATOR_PREVW;
    }

    public Color getCurrentBackGrndColor_OPERATOR_PREVW() {
        return currentBackGrndColor_OPERATOR_PREVW;
    }

    public void setCurrentBackGrndColor_OPERATOR_PREVW(Color currentBackGrndColor_OPERATOR_PREVW) {
        this.currentBackGrndColor_OPERATOR_PREVW = currentBackGrndColor_OPERATOR_PREVW;
    }

    private Color currentForeGrndColor_OPERATOR_PREVW=null;
	private Color currentBackGrndColor_OPERATOR_PREVW=null;


	private Color currentForeGrndColor_OPERATOR=null;
	private Color currentBackGrndColor_OPERATOR=null;

	public Color getCurrentBackGrndColor_OPERATOR() {
		return currentBackGrndColor_OPERATOR;
	}

	public void setCurrentBackGrndColor_OPERATOR(Color currentBackGrndColor_OPERATOR) {
		this.currentBackGrndColor_OPERATOR = currentBackGrndColor_OPERATOR;
	}

	public Color getCurrentForeGrndColor_OPERATOR() {
		return currentForeGrndColor_OPERATOR;
	}

	public void setCurrentForeGrndColor_OPERATOR(Color currentForeGrndColor_OPERATOR) {
		this.currentForeGrndColor_OPERATOR = currentForeGrndColor_OPERATOR;
	}


	private String effectKEYWORD=null;

    public String getEffectKEYWORD_PREVW() {
        return effectKEYWORD_PREVW;
    }

    public void setEffectKEYWORD_PREVW(String effectKEYWORD_PREVW) {
        this.effectKEYWORD_PREVW = effectKEYWORD_PREVW;
    }

    private String effectKEYWORD_PREVW=null;

    private String effectANN=null;
	private String effectSTRING=null;
	private String effectNUMBER=null;
	private String effectOP=null;

    public String getEffectSTRING_PREVW() {
        return effectSTRING_PREVW;
    }

    public void setEffectSTRING_PREVW(String effectSTRING_PREVW) {
        this.effectSTRING_PREVW = effectSTRING_PREVW;
    }

    public String getEffectNUMBER_PREVW() {
        return effectNUMBER_PREVW;
    }

    public void setEffectNUMBER_PREVW(String effectNUMBER_PREVW) {
        this.effectNUMBER_PREVW = effectNUMBER_PREVW;
    }

    public String getEffectOP_PREVW() {
        return effectOP_PREVW;
    }

    public void setEffectOP_PREVW(String effectOP_PREVW) {
        this.effectOP_PREVW = effectOP_PREVW;
    }

    private String effectANN_PREVW=null;
    private String effectSTRING_PREVW=null;
	private String effectNUMBER_PREVW=null;
	private String effectOP_PREVW=null;

    public String getSyntaxEnabled() {
        return syntaxEnabled;
    }

    private String  syntaxEnabled=null;

	public String getEffectKEYWORD() {
		return effectKEYWORD;
	}

	public void setEffectKEYWORD(String effectKEYWORD) {
		this.effectKEYWORD = effectKEYWORD;
	}

	public String getEffectNUMBER() {
		return effectNUMBER;
	}

	public void setEffectNUMBER(String effectNUMBER) {
		this.effectNUMBER = effectNUMBER;
	}

	public String getEffectOP() {
		return effectOP;
	}

	public void setEffectOP(String effectOP) {
		this.effectOP = effectOP;
	}

	public String getEffectSTRING() {
		return effectSTRING;
	}

	public void setEffectSTRING(String effectSTRING) {
		this.effectSTRING = effectSTRING;
	}

    public void setSyntaxEnabled(String enabled) {
		this.syntaxEnabled = enabled;
	}

	private Color getColor(String s)
	{
		int s1=s.indexOf("[");
		if(s1==-1)return null;
		int s2=s.indexOf("]");
		String s3=s.substring(s1+1,s2);
		StringTokenizer sk=new StringTokenizer(s3,",");
		int cls[]=new int[3];
		int r=-1;
		int g=-1;
		int b=-1;
		int i=0;
		while(sk.hasMoreTokens())
		{
			String t=(String)sk.nextToken();
			t=t.trim();
			cls[i]=Integer.parseInt(t);
			i++;
		}

		r=cls[0];
		g=cls[1];
		b=cls[2];
		return new Color(r,g,b);
	}

	public CategoryChooser cc=null;

	private ColorChooser currentColorChooser=null;

	public void setColorChooserRef(ColorChooser c)
	{
		currentColorChooser=c;
	}

	public ColorChooser getCurrentColorChooser() {
		return currentColorChooser;
	}


	private String fontFamilykwd;
	private String fontFamilystr;
	private String fontFamilyop;
	private String fontFamilyann;
	private String fontFamilynum;

	private String fontSizekwd;
	private String fontSizestr;
	private String fontSizeann;
	private String fontSizeop;
	private String fontSizenum;


    private void checkForMissingPrefs(Hashtable prefs)
    {
      checkForMissingColorsAndFonts(prefs);
      checkForOtherMissingPrefs(uiprefs);
    }


    private void checkForOtherMissingPrefs(Hashtable uiprefs)
	{
        Object key=uiprefs.get("JavaHome");
        if(key==null)
        {
            String java=UILauncher.getUIutil().getJavaHomePath();
            if(java!=null)
            {
                uiprefs.put("JavaHome",java);
            }
        }
        key=uiprefs.get("ShowWelcomeAtStartUp");
        if(key==null)
        {
          String  show=UILauncher.getUIutil().getShowWelcome();
          uiprefs.put("ShowWelcomeAtStartUp",show);
        }
        key=uiprefs.get("browserPath");
        if(key==null)
        {
          File bpath=UILauncher.getUIutil().getBrowserPath();
          if(bpath!=null)
          {
              String browser=bpath.getAbsolutePath();
              uiprefs.put("browserPath",browser);
          }
        }
        key=uiprefs.get("FavoriteEditor");
        if(key==null)
        {
            String ced=UILauncher.getUIConfigRef().getCustomeEditorPath();
            if(ced!=null)
            {
                uiprefs.put("FavoriteEditor",ced);
            }

        }
        key=uiprefs.get("enableSyntax");
        if(key==null)
        {
            String syn=UILauncher.getUIConfigRef().getSyntaxEnabled();
            if(syn!=null)
            {
                uiprefs.put("enableSyntax",syn);
            }
        }
        key=uiprefs.get("DefaultLNKFeel");
        if(key==null)
        {
            String look=UILauncher.getUIutil().getDefaultLnkNFeelName();
            if(look!=null)
            {
                uiprefs.put("DefaultLNKFeel",look);
            }
        }
        key=uiprefs.get("userprojectHome");
        if(key==null)
        {
            String projfol=UILauncher.getUIutil().getUserProjFol();
            if(projfol!=null)
            {
                uiprefs.put("userprojectHome",projfol);
            }
        }

        key=uiprefs.get("DONTDECPVTMET");
        if(key==null)
        {
            String pvt=""+ ConsoleLauncher.isDontpvtmethods();
            if(pvt!=null)
            {
                uiprefs.put("DONTDECPVTMET",pvt);
            }
        }

        key=uiprefs.get("DONTSHOWABSMET");
        if(key==null)
        {
            String abs=""+ ConsoleLauncher.isDontshowabs();
            if(abs!=null)
            {
                uiprefs.put("DONTSHOWABSMET",abs);
            }
        }
         key=uiprefs.get("DONTDECSYNMET");
        if(key==null)
        {
            String syn=""+ ConsoleLauncher.isDontsynth();
            if(syn!=null)
            {
                uiprefs.put("DONTDECSYNMET",syn);
            }
        }
        key=uiprefs.get("DONTDECSTATICINIT");
        if(key==null)
        {
            String sta=""+ ConsoleLauncher.isDontstaticinit();
            if(sta!=null)
            {
                uiprefs.put("DONTDECSTATICINIT",sta);
            }
        }
        key=uiprefs.get("DONTSHOWEMPCONS");
        if(key==null)
        {
            String con=""+ ConsoleLauncher.isDontshowemptyconst();
            if(con!=null)
            {
                uiprefs.put("DONTSHOWEMPCONS",con);
            }
        }
        key=uiprefs.get("DONTSHOWNAT");
        if(key==null)
        {
            String nat=""+ ConsoleLauncher.isDontshownative();
            if(nat!=null)
            {
                uiprefs.put("DONTSHOWNAT",nat);
            }
        }
         key=uiprefs.get("INCLUDEDPKGS");
        if(key==null)
        {
            String incl=""+ ConsoleLauncher.getInclList();
            if(incl!=null)
            {
                uiprefs.put("INCLUDEDPKGS",incl);
            }
        }
        key=uiprefs.get("EXCLUDEDPKGSINIMPORT");
        if(key==null)
        {
            String excl=""+ ConsoleLauncher.getExclList();
            if(excl!=null)
            {
                uiprefs.put("EXCLUDEDPKGSINIMPORT",excl);
            }
        }
        
        
        key=uiprefs.get("all_classes_in_archive");
        if(key==null)
        {
            String excl=getAll_classes_in_archive();
            if(excl!=null)
            {
                uiprefs.put("all_classes_in_archive",excl);
            }
        }
        key=uiprefs.get("only_default_classes_in_archive");
        if(key==null)
        {
            String excl=getOnly_default_classes_in_archive();
            if(excl!=null)
            {
                uiprefs.put("only_default_classes_in_archive",excl);
            }
        }
        
        key=uiprefs.get("enclosed_archives_in_archive");
        if(key==null)
        {
            String excl=getEnclosed_archives_in_archive();
            if(excl!=null)
            {
                uiprefs.put("enclosed_archives_in_archive",excl);
            }
        }
        
        key=uiprefs.get("registered_archive_types");
        if(key==null)
        {
            String excl=getArchiveTypes();
            if(excl!=null)
            {
                uiprefs.put("registered_archive_types",excl);
            }
            else
            {
            	excl="jar,zip";
            	uiprefs.put("registered_archive_types",excl);
            }
        }
        


    }

	private void checkForMissingColorsAndFonts(Hashtable uiprefs)
	{

		Object val=uiprefs.get("keyword_backg_color");
		if(val==null)
		{
			Color c=getCurrentBackGrndColor_KEYWORD();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("keyword_backg_color",t);
			}
		}
		val=uiprefs.get("keyword_foreg_color");
		if(val==null)
		{
			Color c=getCurrentForeGrndColor_KEYWORD();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("keyword_foreg_color",t);
			}
		}
		val=uiprefs.get("keyword_effect");
		if(val==null)
		{
			String c=getEffectKEYWORD();
			if(c!=null)
			{
				uiprefs.put("keyword_effect",c);
			}
		}



		val=uiprefs.get("number_backg_color");
		if(val==null)
		{
			Color c=getCurrentBackGrndColor_NUMBER();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("number_backg_color",t);
			}
		}
		val=uiprefs.get("number_foreg_color");
		if(val==null)
		{
			Color c=getCurrentForeGrndColor_NUMBER();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("number_foreg_color",t);
			}
		}
		val=uiprefs.get("number_effect");
		if(val==null)
		{
			String c=getEffectNUMBER();
			if(c!=null)
			{
				uiprefs.put("number_effect",c);
			}
		}



		val=uiprefs.get("operator_backg_color");
		if(val==null)
		{
			Color c=getCurrentBackGrndColor_OPERATOR();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("operator_backg_color",t);
			}
		}
		val=uiprefs.get("operator_foreg_color");
		if(val==null)
		{
			Color c=getCurrentForeGrndColor_OPERATOR();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("operator_foreg_color",t);
			}
		}
		val=uiprefs.get("operator_effect");
		if(val==null)
		{
			String c=getEffectOP();
			if(c!=null)
			{
				uiprefs.put("operator_effect",c);
			}
		}


		val=uiprefs.get("string_backg_color");
		if(val==null)
		{
			Color c=getCurrentBackGrndColor_STRING();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("string_backg_color",t);
			}
		}
		val=uiprefs.get("string_foreg_color");
		if(val==null)
		{
			Color c=getCurrentForeGrndColor_STRING();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("string_foreg_color",t);
			}
		}
		val=uiprefs.get("string_effect");
		if(val==null)
		{
			String c=getEffectSTRING();
			if(c!=null)
			{
				uiprefs.put("string_effect",c);
			}
		}

		val=uiprefs.get("ann_effect");
		if(val==null)
		{
			String c=getEffectANN();
			if(c!=null)
			{
				uiprefs.put("ann_effect",c);
			}
		}
		val=uiprefs.get("ann_backg_color");
		if(val==null)
		{
			Color c=getCurrentBackGrndColor_ANN();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("ann_backg_color",t);
			}
		}
		val=uiprefs.get("ann_foreg_color");
		if(val==null)
		{
			Color c=getCurrentForeGrndColor_ANN();
			if(c!=null)
			{
				String t="["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
				uiprefs.put("ann_foreg_color",t);
			}
		}
		val=uiprefs.get("font_family_kwd");
		if(val==null)
		{
			String c=getFontFamilykwd();
			if(c!=null)
			{
				uiprefs.put("font_family_kwd",c);
			}
		}

		val=uiprefs.get("font_family_num");
		if(val==null)
		{
			String c=getFontFamilynum();
			if(c!=null)
			{
				uiprefs.put("font_family_num",c);
			}
		}
		val=uiprefs.get("font_family_ann");
		if(val==null)
		{
			String c=getFontFamilyann();
			if(c!=null)
			{
				uiprefs.put("font_family_ann",c);
			}
		}

		val=uiprefs.get("font_family_op");
		if(val==null)
		{
			String c=getFontFamilyop();
			if(c!=null)
			{
				uiprefs.put("font_family_op",c);
			}
		}

		val=uiprefs.get("font_family_str");
		if(val==null)
		{
			String c=getFontFamilystr();
			if(c!=null)
			{
				uiprefs.put("font_family_str",c);
			}
		}

		val=uiprefs.get("font_size_ann");
		if(val==null)
		{
			String c=getFontSizeann();
			if(c!=null)
			{
				uiprefs.put("font_size_ann",c);
			}
		}

		val=uiprefs.get("font_size_kwd");
		if(val==null)
		{
			String c=getFontSizekwd();
			if(c!=null)
			{
				uiprefs.put("font_size_kwd",c);
			}
		}

		val=uiprefs.get("font_size_op");
		if(val==null)
		{
			String c=getFontSizeop();
			if(c!=null)
			{
				uiprefs.put("font_size_op",c);
			}
		}


		val=uiprefs.get("font_size_num");
		if(val==null)
		{
			String c=getFontSizenum();
			if(c!=null)
			{
				uiprefs.put("font_size_num",c);
			}
		}

		val=uiprefs.get("font_size_str");
		if(val==null)
		{
			String c=getFontSizestr();
			if(c!=null)
			{
				uiprefs.put("font_size_str",c);
			}
		}

	}

	public String getFontFamilykwd() {
		return fontFamilykwd;
	}

	public void setFontFamilykwd(String fontFamilykwd) {
		this.fontFamilykwd = fontFamilykwd;
	}

	public String getFontFamilynum() {
		return fontFamilynum;
	}

	public void setFontFamilynum(String fontFamilynum) {
		this.fontFamilynum = fontFamilynum;
	}

	public String getFontFamilyop() {
		return fontFamilyop;
	}

	public void setFontFamilyop(String fontFamilyop) {
		this.fontFamilyop = fontFamilyop;
	}

	public String getFontFamilystr() {
		return fontFamilystr;
	}

	public void setFontFamilystr(String fontFamilystr) {
		this.fontFamilystr = fontFamilystr;
	}

	public String getFontSizekwd() {
		return fontSizekwd;
	}

	public void setFontSizekwd(String fontSizekwd) {
		this.fontSizekwd = fontSizekwd;
	}

	public String getFontSizenum() {
		return fontSizenum;
	}

	public void setFontSizenum(String fontSizenum) {
		this.fontSizenum = fontSizenum;
	}

	public String getFontSizeop() {
		return fontSizeop;
	}

	public void setFontSizeop(String fontSizeop) {
		this.fontSizeop = fontSizeop;
	}

	public String getFontSizestr() {
		return fontSizestr;
	}

	public void setFontSizestr(String fontSizestr) {
		this.fontSizestr = fontSizestr;
	}

    public static Color defaultBackgColor=Color.WHITE;

    public String getCustomeEditorPath() {
        return customeEditorPath;
    }

    public void setCustomeEditorPath(String customeEditorPath) {
        this.customeEditorPath = customeEditorPath;
    }

    private String customeEditorPath=null;


    public File getCurrentOpFile() {
        return currentOpFile;
    }

    private File currentOpFile=null;

    public void setCurrentResultFile(File d)
    {
     currentOpFile=d;

    }

	public String getAll_classes_in_archive() {
		return all_classes_in_archive;
	}

	public void setAll_classes_in_archive(String all_classes_in_archive) {
		this.all_classes_in_archive = all_classes_in_archive;
	}

	public String getEnclosed_archives_in_archive() {
		return enclosed_archives_in_archive;
	}

	public void setEnclosed_archives_in_archive(String enclosed_archives_in_archive) {
		this.enclosed_archives_in_archive = enclosed_archives_in_archive;
	}

	public String getOnly_default_classes_in_archive() {
		return only_default_classes_in_archive;
	}

	public  void setOnly_default_classes_in_archive(String s) {
		this.only_default_classes_in_archive = s;
	}
	
	
	private ArrayList favoriteList=new ArrayList();
	public ArrayList currentFavoriteList()
	{
		return favoriteList;
	}
	
	public void setFavoriteList(ArrayList list)
	{
		favoriteList=list;	
	}
	
	public String getFavoriteListAsString()
	{
		String s="[";
		if(favoriteList.size() > 0)
		{
			for(int z=0;z<favoriteList.size();z++)
			{
				s+=favoriteList.get(z).toString();
				if(z < (favoriteList.size() -1))
				{
					s+=",";
				}
			}
			s+="]";
			return s;
		}
		else
			return "";
	}
	
	public String[] getFavoriteListAsArray()
	{
		
		if(favoriteList.size() > 0)
		{
			String s[]=new String[favoriteList.size()];
			for(int z=0;z<favoriteList.size();z++)
			{
				s[z]=favoriteList.get(z).toString();
				
			}
			
			return s;
		}
		else
			return null;
	}
	
	
	public void formFavListFromFavString(String s)
	{
		if(s!=null && s.length() > 0)
		{
			ArrayList list=new ArrayList();
			int index1=s.indexOf("[");
			int index2=s.indexOf("]");
			if(index1!=-1 && index2!=-1)
			{
				s=s.substring(index1+1,index2);
				StringTokenizer st=new StringTokenizer(s,",");
				while(st.hasMoreTokens())
				{
					String t=(String)st.nextToken();
					list.add(t);
				}
				if(list.size() > 0)
				{
					setFavoriteList(list);
					UIUtil.getUIUtil().setFavList(list);
				}
			}
		}
		
	}

	public String getEffectANN() {
		return effectANN;
	}

	public void setEffectANN(String effectANN) {
		this.effectANN = effectANN;
	}

	public String getEffectANN_PREVW() {
		return effectANN_PREVW;
	}

	public void setEffectANN_PREVW(String effectANN_PREVW) {
		this.effectANN_PREVW = effectANN_PREVW;
	}

	public String getFontFamilyann() {
		return fontFamilyann;
	}

	public void setFontFamilyann(String fontFamilyann) {
		this.fontFamilyann = fontFamilyann;
	}

	public String getFontSizeann() {
		return fontSizeann;
	}

	public void setFontSizeann(String fontSizeann) {
		this.fontSizeann = fontSizeann;
	}


}
