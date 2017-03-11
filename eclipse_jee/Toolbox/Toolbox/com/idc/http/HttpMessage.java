package com.idc.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.idc.file.JVFile;
//import com.idc.trace.LogHelper;

//import java.net.JarURLConnection;
//import java.util.jar.JarFile;
//import java.util.jar.JarEntry;

/**
 * @author John Vincent
 */

public class HttpMessage {
	private HttpMessage() {}
	private static String m_newline = "\n";

	public static Receiver getReceiverWithRedirects (Sender sender, AppCookies appCookies) throws AppException {
//		LogHelper.info(">>> HttpMessage::getReceiverWithRedirects; cookies :"+appCookies.toString()+":");
		Receiver receiver;
		while (true) {
			receiver = getReceiver (sender, appCookies);
//			LogHelper.info("HttpMessage::getReceiverWithRedirects; cookies after receiver :"+appCookies.toString()+":");
			if (receiver.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {		// code = 302
//				LogHelper.info("getReceiverWithRedirects; redirect to "+receiver.getLocation());
				sender.setSenderURL(receiver.getLocation());
			}
			else {
//				LogHelper.info("<<< HttpMessage::getReceiverWithRedirects");
				return receiver;
			}
		}
	}
	public static Receiver getReceiver (Sender sender) throws AppException {
		return getReceiver (sender, null, null);
	}
	public static Receiver getReceiver (Sender sender, AppCookies appCookies) throws AppException {
		return getReceiver (sender, appCookies, null);
	}
	public static Receiver getReceiver (Sender sender, AppCookies appCookies, String strXML) throws AppException {
//		LogHelper.info(">>> HttpMessage::getReceiver");
		Receiver receiver = new Receiver();
		HttpURLConnection httpConnection = null;
		
//		if (sender.isUrlSSL()) 
//			LogHelper.info("Using https protocol");

		try {
			httpConnection = (HttpURLConnection) sender.getSenderURL().getURL().openConnection();
/*
 * masquerade as a browser
*/ 
			httpConnection.setRequestProperty ("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			httpConnection.setRequestProperty ("Accept-Language", "en-us");
			httpConnection.setRequestProperty ("Accept-Encoding", "gzip");
			httpConnection.setRequestProperty ("User-Agent", "compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)");
/*
 * handle POST and GET properties
 */
			if (sender.isPostMethod()) {
				httpConnection.setRequestMethod("POST");
				httpConnection.setDoOutput(true);				// will be sending a body
				httpConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
				httpConnection.setRequestProperty("Content-length", Integer.toString(sender.getEncodedBodyLength()));
			}
			else {
				httpConnection.setRequestMethod("GET");
				httpConnection.setDoOutput(false);				// will not be sending body
			}
			httpConnection.setUseCaches (false);
			httpConnection.setAllowUserInteraction(false);		// you may not ask the user

/*
 * this is important; these must be false else nonsense information is returned
 * in the case of a redirect. The redirect must be handled programatically.
 */
			HttpURLConnection.setFollowRedirects(false);
			httpConnection.setInstanceFollowRedirects(false);

/*
 * send cookies
 */
			if (appCookies != null) appCookies.sendCookies(httpConnection);

/*
 * send the body for a Post
 */
			if (sender.isPostMethod()) {
				OutputStream rawOutStream = httpConnection.getOutputStream();
				PrintWriter pw = new PrintWriter(rawOutStream);
				if (sender.getEncodedBodyLength() > 0) {
					String myBody = sender.getEncodedBody();
					pw.print(myBody);
				}
				if (strXML != null && strXML.length() > 0) pw.print(strXML);
				pw.flush();
				pw.close();
			}

/*
 * Get information from the server.
 * 
 * Start with cookies and the response code
 */
			if (appCookies != null) appCookies.receiveCookies (httpConnection);
			receiver.setResponseCode (httpConnection.getResponseCode());
/*
 * if server sends a redirect, save the url and return
 */
			if (receiver.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				receiver.setLocation(httpConnection);
			}
/*
 * if server sends OK, get the headers and body
 */
			else if (receiver.getResponseCode() == HttpURLConnection.HTTP_OK) {		
				receiver.setLocation(sender);			
				InputStream rawInStream = httpConnection.getInputStream();		// complete writing to output before this
				String compression = httpConnection.getHeaderField("Content-Encoding");
				BufferedReader rdr = null;
				if (compression == null)
					rdr = new BufferedReader (new InputStreamReader(rawInStream));
				else {
				    if ("gzip".equals(compression))
				    	rdr = new BufferedReader (new InputStreamReader(new GZIPInputStream(rawInStream)));
				    else 
				        throw new AppException(compression +" not supported");
				}
				    
				StringBuffer sb = new StringBuffer();
				//LogHelper.info("m_newline :"+m_newline+":");
				String line;
				while ((line = rdr.readLine()) != null) {
					sb.append(line).append(m_newline);
				}
				rdr.close();
				//LogHelper.info("body follows:");
				//LogHelper.info(sb.toString());
				//LogHelper.info("body done");
				receiver.setBody(sb);
			}
			else {
				throw new AppException ("Unexpected response code "+receiver.getResponseCode());
			}
//			LogHelper.info("<<< HttpMessage::getReceiver");
			return receiver;
		}
		catch (java.net.ConnectException ce) {
			throw new AppException ("HTTP Connect error; "+ce.getMessage());			
		}
		catch (IOException ioex) {
			throw new AppException ("IO error; "+ioex.getMessage());			
		}
		finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
				httpConnection = null;
			}
		}
	}

	public static InputStream getURLInputStream (String url) throws AppException {
		AppURL appURL = new AppURL (url);
		try {
//			System.out.println(">>> InputStream; url :"+url+":");
			HttpURLConnection httpConnection = (HttpURLConnection) appURL.getURL().openConnection();
//			System.out.println("stage 1");
			int responseCode = httpConnection.getResponseCode();
//			System.out.println("responseCode "+responseCode);
			if (responseCode != 200) 
				return null;
//			else
//				System.out.println("responseCode "+responseCode+" url :"+url+":");
//			LogBroker.debug(HttpMessage.class,"responseCode "+responseCode);
			return httpConnection.getInputStream();
		}
		catch (IOException ioex) {
			throw new AppException ("IO error (getURLInputStream); "+ioex.getMessage());			
		}
	}

	public static StringBuffer getURLStringBuffer (String url) throws AppException {
		InputStream input = getURLInputStream (url);
		if (input == null) return null;
		try {
			StringBuffer buffer = new StringBuffer();
			String line;
			BufferedReader dataInput = new BufferedReader (new InputStreamReader(input));
			while ((line = dataInput.readLine()) != null) {
				buffer.append(line);
				buffer.append(m_newline);
			}
			return buffer;
		}
		catch (IOException ioex) {
			throw new AppException ("IO error (getURLStringBuffer); "+ioex.getMessage());
		}
	}

	public static boolean getFile (String url, File toFile) throws AppException {
		StringBuffer buf = HttpMessage.getURLStringBuffer (url);
		if (buf == null) return false;
		toFile.getParentFile().mkdirs();
		JVFile.writeFile (buf.toString(), toFile);
		return true;
	}

	public static String getContents (String strURL) throws AppException {
		Sender sender = new Sender(strURL);
		sender.setGetMethod();
		Receiver receiver = getReceiver (sender);
//		LogHelper.info("receiver: "+receiver.toString());
		return receiver.getBody().toString();
	}

	public static java.awt.Image getURLImage (String url) throws AppException {
		AppURL appURL = new AppURL (url);
		return java.awt.Toolkit.getDefaultToolkit().createImage(appURL.getURL());
	}

// usage getURLJar("jar:http://host/file.jar!/", "jar:file:/c:/tmp/my.jar!/");
/*	
	public static void getURLJar (String fromUrl, String toUrl) throws AppException {
		AppURL fromAppURL = new AppURL (fromUrl);
		AppURL toAppURL = new AppURL (toUrl);
		try {
			JarURLConnection conn = (JarURLConnection)fromAppURL.getURL().openConnection();
			JarFile jarfile = conn.getJarFile();
//			 When no entry is specified on the URL, the entry name is null
	        String entryName = conn.getEntryName();  // null
// not sure how much of this is useful
	        conn = (JarURLConnection)toAppURL.getURL().openConnection();		// Get the jar file
	        jarfile = conn.getJarFile();
	        entryName = conn.getEntryName();					// Get the entry name; it should be the same as specified on URL
	        JarEntry jarEntry = conn.getJarEntry();				// Get the jar entry
		}
		catch (IOException ioex) {
			throw new AppException ("IO error; "+ioex.getMessage());
		}
	}
*/
	public static void sendObject (String url, Serializable obj)  throws AppException {
		if (obj == null) return;
		HttpURLConnection httpConnection = null;
		AppURL appURL = new AppURL (url);
		try {
			httpConnection = (HttpURLConnection) appURL.getURL().openConnection();
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setUseCaches (false);

			httpConnection.setRequestProperty("Content-Type","application/x-java-serialized-object");

			ObjectOutputStream out = new ObjectOutputStream (httpConnection.getOutputStream());
//			LogHelper.info("sending the obj, in sendObject(): "+obj.toString());
			out.writeObject (obj);
			out.flush();
			out.close();
		}
		catch (IOException ioex) {
			throw new AppException ("IO error; "+ioex.getMessage());			
		}
		finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
				httpConnection = null;
			}
		}
	}

	public static Object receiveObject (String url)  throws AppException {
		Object obj = null;
		HttpURLConnection httpConnection = null;
		AppURL appURL = new AppURL (url);
		try {
			httpConnection = (HttpURLConnection) appURL.getURL().openConnection();
			ObjectInputStream in = new ObjectInputStream (httpConnection.getInputStream());
//			LogHelper.info("receiving the obj, in receiveObject()");
			obj = in.readObject();
			in.close();
			return obj;
		}
		catch (ClassNotFoundException cnfex) {
			throw new AppException ("ClassNotFoundException error; "+cnfex.getMessage());			
		}
		catch (IOException ioex) {
			throw new AppException ("IO error; "+ioex.getMessage());			
		}
		finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
				httpConnection = null;
			}
		}
	}
	public static void doSetupHttps() throws Exception {
		System.out.println(">>> doSetupHttps");
		// disable SSL Certificate Authentication
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				System.out.println("Warning: URL Host: "+urlHostName+" vs. "+session.getPeerHost());
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		
		// create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};
		
		// install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		System.out.println("<<< doSetupHttps");
	}


}

/*
You can access SSL sites (URLs starting with https://...) just like normal HTTP URLs after downloading the JSSE (Java Secure Sockets Extension) from Sun, installing it in your classpath, and adding the following two lines in your program before using SSL URLs:

// Dynamically register the JSSE provider.
java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

// Set this property to use Sun's reference implementation of the HTTPS protocol.
System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

*/


/*
If you are behind a firewall that allows access using HTTP only via a proxy server, you can tell Java about this fact and the address and port of your proxy by calling

System.getProperties().put("proxySet", "true");
System.getProperties().put("proxyHost", "MyProxy");
System.getProperties().put("proxyPort", "80");
*/