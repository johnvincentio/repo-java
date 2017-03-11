package com.idc.rgb;

import org.apache.log4j.*;

public class HexConverter {
	private static Logger debug = Logger.getRootLogger();

	public HexConverter() {}

	public String getHexValue (int r, int g, int b) {
		debug.info(">>>HexConverter::getHexValue");
		String strValue = makeString(r) + makeString(g) + makeString(b);
		debug.info ("strValue :"+strValue+":");
		debug.info("<<<HexConverter::getHexValue");
		return strValue;
	}
//
//		Color color = new Color(r, g, b);
//		String strValue = Integer.toHexString(color.getRGB() & 0x00ffffff); 
//
	public void getRGBValues (String strValue, int[] rgb) {
		debug.info(">>>HexConverter::getRGBValues :"+strValue+":");
		rgb[0] = Integer.parseInt (strValue.substring(0,2),16);
		rgb[1] = Integer.parseInt (strValue.substring(2,4),16);
		rgb[2] = Integer.parseInt (strValue.substring(4,6),16);
		debug.info("r,g,b "+rgb[0]+","+rgb[1]+","+rgb[2]);
		debug.info("<<<HexConverter::getRGBValues");
	}
	private String makeString (int nValue) {
		String strTmp = Integer.toHexString(nValue); 
		if (strTmp.length() == 2)
			return strTmp;
		else if (strTmp.length() == 1)
			return "0" + strTmp;
		else
			return "00";
	}
}
