package com.idc.ant.task;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.idc.http.AppException;
import com.idc.http.AppURL;
import com.idc.http.HttpMessage;

public class CopyFromHttp extends Task {
	private File m_baseDir;
	private AppURL m_url;
	private File m_toDir;

	public void setBaseDir (File file) {
		System.out.println(">>> setBaseDir; file :" + file.getPath());
		m_baseDir = file;
		if (! file.exists())
			throw new BuildException("CompareDirectory; setBaseDir " + file + " doesn\'t exist");
		System.out.println("<<< setBaseDir");
	}

	public void setFromUrl (String url) {
		System.out.println(">>> setFromUrl; file :" + url);
		try {
			m_url = new AppURL (url);
		}
		catch (AppException mfex) {
			throw new BuildException ("URL "+url+" error; "+mfex.getMessage());
		}
		System.out.println("<<< setFromUrl");
	}

	public void setToDir (File file) {
		System.out.println(">>> setToDir; file :" + file.getPath());
		m_toDir = file;
		if (file.exists())
			throw new BuildException("CompareDirectory; setToDir " + file + " doesn\'t exist");
		System.out.println("<<< setToDir");
	}

	public void execute() throws BuildException {
		System.out.println(">>> execute");
		System.out.println ("m_baseDir "+m_baseDir);
		System.out.println ("m_url "+m_url.toString());
		System.out.println ("m_toDir "+m_toDir);
		try {
			doDirectory (m_baseDir);
//			doTest();
		}
		catch (Exception ex) {
			throw new BuildException (ex);
		}
		System.out.println("<<< execute");
	}

	private void doDirectory (File currentDir) throws Exception {
		File[] allFiles = currentDir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			File file = allFiles[i];
			if (file.isDirectory())
				doDirectory (file);
			else if (file.isFile()) {
//				System.out.println("file is :"+file.getPath());
				String fromUrl = makeGetURL (file);
				File jv2 = makeWriteFile (file);
//				System.out.println("fromUrl :"+fromUrl+":");
//				System.out.println("jv2 :"+jv2.getPath()+":");
				HttpMessage.getFile (fromUrl, jv2);
			}
		}
	}
	@SuppressWarnings("unused")
	private void doTest() throws Exception {
		System.out.println(">>> doTest");
		String url = "http://end01.dc.irac.ecom.ad.hertz.com:1400/content/xml/IracContent/abouthertz/csCZ/applicantSubmittedView.jsp";

		boolean bool = HttpMessage.getFile (url, new File("c:/herc_interwoven/jv/s1/ab.xml"));
		System.out.println("bool "+bool);

		url = "http://end01.dc.irac.ecom.ad.hertz.com:1400/content/xml/IracContent\\abouthertz\\csCZ\\applicantSubmittedView.jsp";
		bool = HttpMessage.getFile (url, new File("c:/herc_interwoven/jv/ab.xml"));
		System.out.println("bool "+bool);

		url = "http://end01.dc.irac.ecom.ad.hertz.com:1400/content/xml/IracContent/abouthertz/csCZ/jv.jsp";
		bool = HttpMessage.getFile (url, new File("c:/herc_interwoven/jv/ac.xml"));
		System.out.println("bool "+bool);
		System.out.println("<<< doTest");
	}

	private String makeGetURL (File currentFile) {
		String s1 = currentFile.getPath();
		int f1 = s1.indexOf(m_baseDir.getPath());
		if (f1 < 0) return null;
		String s2 = m_url.getURL().toString() + s1.substring(f1 + m_baseDir.getPath().length()).replace('\\', '/');
//		System.out.println("s2 :"+s2+":");
		return s2;
	}

	private File makeWriteFile (File currentFile) {
		String s1 = currentFile.getPath();
		int f1 = s1.indexOf(m_baseDir.getPath());
		if (f1 < 0) return null;
		String s2 = m_toDir + "/" + s1.substring(f1 + m_baseDir.getPath().length());
//		System.out.println("s2 :"+s2+":");
		return new File (s2);
	}
}
