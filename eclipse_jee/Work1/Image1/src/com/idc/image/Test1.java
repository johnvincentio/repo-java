package com.idc.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Test1 {

	public static void main(String[] args) {
		Test1 test1 = new Test1();
//		test1.doTest();
		test1.doTest1();
//		test1.doTest2();
//		test1.doTest4();
	}
//Get bad image for US and separately for CA
	//https://forums.oracle.com/forums/thread.jspa?threadID=1261674
	//http://www.mkyong.com/java/how-to-convert-byte-to-bufferedimage-in-java/
	//http://download.oracle.com/javase/tutorial/2d/images/saveimage.html
//http://www.rgagnon.com/javadetails/java-0266.html	
	public void doTest1() {
		System.out.println(">>> doTest1");
		try {
			String str0 = "https://hertzequipus.rousesales.com/Pictures/Thumbnails/0";
			BufferedImage badImage = getBufferedImage (str0);
			saveImage (badImage, "c:/tmp/jv_image0.jpg");

			String str1 = "https://hertzequipus.rousesales.com/Pictures/Thumbnails/005206036";
			BufferedImage bufferedImage1 = getBufferedImage (str1);
			saveImage (bufferedImage1, "c:/tmp/jv_image1.jpg");

			boolean status = compare (badImage, bufferedImage1);
			System.out.println("1 vs bad; status "+status);

			String str2 = "https://hertzequipus.rousesales.com/Pictures/Thumbnails/ftrghtrh";
			BufferedImage bufferedImage2 = getBufferedImage (str2);
			saveImage (bufferedImage2, "c:/tmp/jv_image2.jpg");

			status = compare (badImage, bufferedImage2);
			System.out.println("2 vs bad; status "+status);

			String str3 = "https://hertzequipus.rousesales.com/Pictures/Thumbnails/005206036";
			BufferedImage bufferedImage3 = getBufferedImage (str3);
			saveImage (bufferedImage3, "c:/tmp/jv_image3.jpg");

			status = compare (bufferedImage1, bufferedImage3);
			System.out.println("1 vs 3 status "+status);
			status = compare (bufferedImage2, bufferedImage3);
			System.out.println("2 vs 3 status "+status);
		}
		catch (Exception ex) {
			System.out.println("Error; "+ex.getMessage());
		}
		System.out.println("<<< doTest1");
	}

	private boolean compare (BufferedImage bufferedImage1, BufferedImage bufferedImage2) {
		Raster r1 = bufferedImage1.getData();
		DataBuffer db1 = r1.getDataBuffer();
		int size1 = db1.getSize();
		System.out.println("size1 "+size1);

		Raster r2 = bufferedImage2.getData();
		DataBuffer db2 = r2.getDataBuffer();
		int size2 = db2.getSize();
		System.out.println("size2 "+size2);

		if (size1 != size2) return false;

		for (int i = 0; i < size1; i++) {
			int px1 = db1.getElem(i);
			int px2 = db2.getElem(i);
			if (px1 != px2) {
				System.out.println("pixel "+i+" px1 "+px1+" px2 "+px2);
				return false;
			}
		}
		return true;
	}

	private void saveImage (BufferedImage bufferedImage, String pathname) {
		try {
			System.out.println("saving file");
			ImageIO.write (bufferedImage, "JPG", new File(pathname));
		}
		catch (Exception ex) {
			System.err.println("Error; "+ex.getMessage());
		}
	}

	public BufferedImage getBufferedImage (String url) {
		System.out.println(">>> doTest1");
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read (new URL (url));
			if (bufferedImage == null) {
				System.err.println("bi is null");
			}
		}
		catch (Exception ex) {
			System.err.println("Error; "+ex.getMessage());
			return null;
		}
		System.out.println("<<< doTest1");
		return bufferedImage;
	}

	/*
	BufferedImage bi ...

	Raster r = bi.getData();
	DataBuffer db = r.getDataBuffer();
	int size = db.getSize();

	for (int i = 0; i < size; i++) {
	  int px = db.getElem(i);
	}

	One more question.  Do you know where in the data buffer for the image is the information for the background color of the image stored.  


	sedj (Programmer) 	
	24 Jul 06 2:26
	I think it is in the ColorModel for the Raster - just read the API documentation for the classes used - you should find your information there. 

	BufferedImage bi = ImageIO.read(new File("bla.jpg")); 


	*/


   
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
	public static InputStream getURLInputStream (String url) throws AppException {
		AppURL appURL = new AppURL (url);
		try {
			HttpURLConnection httpConnection = (HttpURLConnection) appURL.getURL().openConnection();
			int responseCode = httpConnection.getResponseCode();
			LogBroker.debug(HttpMessage.class,"responseCode "+responseCode);
			return httpConnection.getInputStream();
		}
		catch (IOException ioex) {
			throw new AppException ("IO error; "+ioex.getMessage());			
		}
	}

*/
/*
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.net.*;
 
public class ImageSave
{
    public static void main(String args[])
    {
        try {
             // This is where you'd define the proxy's host name and port.
             SocketAddress address = new InetSocketAddress(hostName, port);
             
             // Create an HTTP Proxy using the above SocketAddress.
             Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
             
             URL url = new URL("www.example.com/image.jpg");
             
             // Open a connection to the URL using the proxy information.
             URLConnection conn = url.openConnection(proxy);
             InputStream inStream = conn.getInputStream();
             
             // BufferedImage image = ImageIO.read(url);
             // Use the InputStream flavor of ImageIO.read() instead.
             BufferedImage image = ImageIO.read(inStream);
             
             ImageIO.write(image, "JPG", new File("image.jpg"));
        } 
        catch (Exception e) {
             e.printStackTrace();
        }
    }
}

*/
/*
	private static boolean getImage (MigrationLogger migrationLogger, SalesItem item) {
		String str = US_IMAGES;
		if (item.getCountry().equals("02")) str = CA_IMAGES;

		if (item.getIcNumber() == null) return false;
		str += item.getIcNumber().trim();

		//TODO; do I need to know what type of image is involved?
		try {
			System.out.println(">>> 99; Getting image at "+str);
			System.out.println("Test 1");
			java.awt.Image image = HttpMessage.getURLImage (str);
			if (image == null) System.out.println("image is null");

			System.out.println("convert to bytes");
			byte[] bytes = getBytes (image);
			System.out.println("bytes length is "+bytes.length);

 * does not work
Error getting image; ex sun.awt.image.ToolkitImage incompatible with java.awt.image.BufferedImage

			System.out.println("Test 1a");
			BufferedImage image1 = (BufferedImage) HttpMessage.getURLImage (str);
			if (image1 == null) System.out.println("image1 is null");

			System.out.println("Test 2");
			URL url = new URL(str);
			java.awt.Image image2 = ImageIO.read(url);
			if (image2 == null) System.out.println("image2 is null");

			System.out.println("Test 3");
			System.out.println("before ImageIO.read");
			BufferedImage bi = ImageIO.read(url);
			if (bi == null) System.out.println("bi is null");

			System.out.println("Save jpg");
			String save = "c:/tmp/" + item.getIcNumber().trim()+".jpg";
			System.out.println("before ImageIO.write");
			ImageIO.write (bi, "jpg", new File (save));

			System.out.println("<<< got image");
		}
		catch (IOException ioex) {
			System.out.println("Error getting image; ioex "+ioex.getMessage());
			return false;
		}
		catch (Exception ex) {
			System.out.println("Error getting image; ex "+ex.getMessage());
			return false;
		}

		return true;
	}
	public static byte[] getBytes (Object object) throws Exception {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeObject(object);
		objStream.flush();
		return byteStream.toByteArray();
	}
}



public void doTest2() {
System.out.println(">>> doTest2");
try {
	doSetupHttps();
	String str = "https://hertzequipus.rousesales.com/Pictures/Thumbnails/005206036";
	URL url = new URL (str);
    URLConnection conn = url.openConnection();
    InputStream inStream = conn.getInputStream();

    BufferedImage image = ImageIO.read (inStream);
	if (image == null) System.out.println("image is null");
}
catch (Exception ex) {
	System.out.println("Error; "+ex.getMessage());
}
System.out.println("<<< doTest2");
}

public void doTest4() {
try {
	doSetupHttps();
	String hostName = "hertzequipus.rousesales.com";
	int port = 80;

     // This is where you'd define the proxy's host name and port.
     SocketAddress address = new InetSocketAddress(hostName, port);
     
     // Create an HTTP Proxy using the above SocketAddress.
     Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
     String str = "https://hertzequipus.rousesales.com/Pictures/Thumbnails/005206036";            
     URL url = new URL(str);
     
     // Open a connection to the URL using the proxy information.
     URLConnection conn = url.openConnection(proxy);
     InputStream inStream = conn.getInputStream();
     
     // BufferedImage image = ImageIO.read(url);
     // Use the InputStream flavor of ImageIO.read() instead.
     BufferedImage image = ImageIO.read(inStream);
     
     ImageIO.write(image, "JPG", new File("c:/tmp/jv_image.jpg"));
} 
catch (Exception e) {
     e.printStackTrace();
}
}
	public void doTest() {
		System.out.println(">>> doTest");
		try {
			String str = "https://hertzequipus.rousesales.com/Pictures/Thumbnails/005206036";
			java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(new URL (str));
			if (image == null) System.out.println("image is null");
		}
		catch (Exception ex) {
			System.out.println("Error; "+ex.getMessage());
		}
		System.out.println("<<< doTest");
	}
		public void doTest1() {
		System.out.println(">>> doTest1");
		try {
			String str = "https://hertzequipus.rousesales.com/Pictures/Thumbnails/005206036";
			BufferedImage bi = ImageIO.read (new URL (str));
			if (bi == null) {
				System.out.println("bi is null");
			}
			else {
				System.out.println("saving file");
				ImageIO.write(bi, "JPG", new File("c:/tmp/jv_image.jpg"));
			}
		}
		catch (Exception ex) {
			System.out.println("Error; "+ex.getMessage());
		}
		System.out.println("<<< doTest1");
	}
*/