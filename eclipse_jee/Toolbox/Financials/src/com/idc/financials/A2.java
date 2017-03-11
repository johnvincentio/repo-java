package com.idc.financials;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class A2 {

	public static void main (String[] args) {
		(new A2()).doTest1();
	}
	private void doTest1() {
		InputStream jv1 = getData ("SPY");
		display (jv1);
	}

	public InputStream getData (String stockSymbol) {
		System.out.println (">>> getData; stockSymbol :"+stockSymbol+":");
//		System.setProperty("http.proxyHost", "proxywest.i-flex.com");
//		System.setProperty("http.proxyPort", "8080");
		String hostName = "http://query.yahooapis.com/v1/public/yql";
		String query = "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28%22" + stockSymbol + "%22%29";
		String env = "store://datatables.org/alltableswithkeys";
		String urlName = hostName + "?q=" + query + "&env=" + env;
		URL url;
		InputStream inStrm = null;
		try {
			System.out.println ("urlName :"+urlName+":");
			url = new URL(urlName);
			URLConnection urlCon = url.openConnection();
			inStrm = urlCon.getInputStream();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return inStrm;
		}

		public void display(InputStream is) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			NodeList nodes_i = document.getDocumentElement().getElementsByTagName("quote");
			if (nodes_i != null && nodes_i.getLength() > 0) {
				for (int i = 0; i < nodes_i.getLength(); i++) {
					Element el1 = (Element) nodes_i.item(i);
					if (el1 != null && el1.getFirstChild() != null) {
						String textVal = el1.getFirstChild().getNodeValue();
						String nodeName = el1.toString().split(":")[0];
						System.out.println(nodeName + " : " + textVal);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
