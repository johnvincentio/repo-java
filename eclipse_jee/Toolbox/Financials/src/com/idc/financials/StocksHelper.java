package com.idc.financials;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONObject;

import com.idc.file.JVFile;
import com.idc.http.FtpMessage;
import com.idc.http.HttpMessage;
import com.idc.utils.JVString;

/*
 * from: http://www.jarloo.com/get-yahoo-finance-api-data-via-yql/
 * 
 * https://developer.yahoo.com/yql/
 */
// ftp://ftp.nasdaqtrader.com/symboldirectory/mfundslist.txt
// http://news.morningstar.com/etf/Lists/ETFReturns.html
//
public class StocksHelper {
	private static final String HOSTNAME = "http://query.yahooapis.com/v1/public/yql";
	private static final String SQL_QUERY = "select * from yahoo.finance.quotes where symbol = 'MY_STOCK_SYMBOL'";
	private static final String SQL_ENV = "store://datatables.org/alltableswithkeys";

	private static final String SQL_QUERY_1 = "select * from yahoo.finance.analystestimate where symbol = 'MY_STOCK_SYMBOL'";

	private static String m_newline = "\n";

	public static void main (String[] args) {
//		doTest();
		makeSymbolList();
//		doPriceLookup ("COFRX");
	}
	private static void doTest() {
		File output = JVFile.makeWorkingFile (new File("c:/jvWork/jvWork/"));
		JVFile.makeDirectories (output);
		JVFile jvFile = new JVFile (output);
		jvFile.open();
		String abc = "AAEBX|Invesco Growth Allocation Fund Class B|Invesco Funds|MF|O|AIM Advisors";
		System.out.println ("String length "+abc.length ());
		String[] parts = abc.split ("\\|");
		System.out.println ("parts count "+parts.length);
		jvFile.writeNL (parts[0] + ":" + parts[1]);
		jvFile.close();
	}

// Program arguments -vmargs -Djava.net.preferIPv4Stack=true
	public static void makeSymbolList() {
		String urlName = "ftp://anonymous:anonymous@ftp.nasdaqtrader.com/symboldirectory/mfundslist.txt;type=i";
		String str = "";
		try {
			str = FtpMessage.getString (urlName);
		}
		catch (Exception ex) {
			System.out.println ("Error getting the symbols file");
			System.exit(1);
		}

		File output = JVFile.makeWorkingFile (new File("c:/jvWork/jvWork/"));
		JVFile.makeDirectories (output);
		JVFile jvFile = new JVFile (output);
		jvFile.open();

		String[] lines = str.split("\n");
		System.out.println ("lines "+lines.length);
		int cntr = 0;
		for (String line : lines) {
//			System.out.println (cntr+" :"+line);
			if (line.startsWith ("Fund Symbol")) continue;
			if (line.startsWith ("File Creation")) continue;
			cntr++;
			String[] parts = line.split ("\\|");
//			System.out.println ("parts count "+parts.length);
			jvFile.writeNL (parts[0] + ":" + parts[1]);
		}
		jvFile.close();
	}

	public static double doPriceLookup (String stockSymbol) {
		System.out.println (">>> doPriceLookup; stockSymbol :"+stockSymbol+":");
		boolean json = true;
		double price = -9999;
		String jv = JVString.replace (SQL_QUERY, "MY_STOCK_SYMBOL", stockSymbol);
		System.out.println ("jv :"+jv+":");
		String urlName = appendParam (HOSTNAME, "q", jv);
		urlName = appendParam (urlName, "env", SQL_ENV);
		if (json) urlName = appendParam (urlName, "format", "json");

		try {
			System.out.println ("urlName :" + urlName + ":");
			StringBuffer buf = HttpMessage.getURLStringBuffer (urlName);
			System.out.println ("buf " + buf);
			JSONObject jsonObject1 = new JSONObject (buf.toString());
//			show (jsonObject1);

			JSONObject jsonObject2 = jsonObject1.getJSONObject ("query").getJSONObject ("results").getJSONObject ("quote");
//			show (jsonObject2);

			String symbol = jsonObject2.getString ("symbol");
			System.out.println ("symbol "+symbol);

			price = jsonObject2.getDouble ("LastTradePriceOnly");
			System.out.println ("price "+price);

		}
		catch (Exception ex) {
			ex.printStackTrace ();
		}
		System.out.println ("<<< doPriceLookup; stockSymbol :"+stockSymbol+": price :"+price+":");
		return price;
	}

	@SuppressWarnings ("unused")
	private static void show (JSONObject jsonObject) {
		System.out.println ("jsonObject1 "+jsonObject);
		Iterator<?> iter1 = jsonObject.keys();
		while (iter1.hasNext()) {
			String key1 = (String) iter1.next();
			System.out.println ("key1 :"+key1+":");
		}
	}

	public static String appendParam (String currentURL, String key, String value) {
		StringBuffer buf = new StringBuffer (currentURL);
		if (currentURL.indexOf ("?") > -1)
			buf.append ("&");
		else
			buf.append ("?");
		buf.append (encodeURI (key)).append ("=").append (encodeURI (value));
		return buf.toString ();
	}

	public static String encodeURI (String str) {
		try {
			return URLEncoder.encode (str, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			return str;
		}
	}
}
