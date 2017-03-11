package com.idc.explorer.abc5;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import com.idc.file.JVFile;

public class Explorer {
	public static final int DATA_BLOCK_SIZE = 1024;

//	public Explorer() {}
	private File m_workingDirectory;

	public Explorer (File workingDirectory) {
		m_workingDirectory = workingDirectory;
	}


	public static void main (String[] args) {(new Explorer (new File ("c:/jvExplorer"))).doTest();}
	public void doTest() {
//		mkdirsTest();
//		zipTest();
//		jarTest();
//		gzTest();
//		tarTest();
//		dirTest();
//		workingDirTest();
	}

	@SuppressWarnings("unused")
	private void workingDirTest() {
		System.out.println("working directory :"+Utils.makeWorkingDirectory (new File ("c:/jvExplorer")));
		System.out.println("working directory :"+Utils.makeWorkingDirectory (new File ("c:/jvExplorer/")));
		System.out.println("working directory :"+Utils.makeWorkingDirectory (new File ("c:/jvExplorer/cud")));
		System.out.println("working directory :"+Utils.makeWorkingDirectory (new File ("c:/jvExplorer/cud/")));
		System.out.println("working directory :"+Utils.makeWorkingDirectory (new File ("c:/jvExplorer/cud/abc")));
		System.out.println("working directory :"+Utils.makeWorkingDirectory (new File ("c:/jvExplorer/cud/abc/")));
	}

	@SuppressWarnings("unused")
	private void mkdirsTest() {
		String strDir;
		strDir = "c:/jvExplorer/AWT";
//		makeDirectories ("c:/jvexplorer/a/b/c/d/e/f/g/h");
//		makeDirectories ("c:/jvexplorer/a/b/c/d/e/f/g/h/j/k/ab.txt");
//		makeDirectories ("c:/jvExplorer\\AWT/index.src");
//		makeDirectories ("c:/jvExplorer/AWT/index.src");
//		makeDirectories ("c:/jvExplorer/AWT/index.src/");
//		makeDirectories ("c:/jvExplorer/AWT/index.src\\");
//		makeDirectories ("c:/jvExplorer/AWT/");
		makeDirectories (new File ("c:/jvExplorer/AWT"));
//		makeDirectories ("c:\jvExplorer\AWT\index.src");
	}

	@SuppressWarnings("unused")
	private void dirTest() {
		File file = new File ("c:/tmp101");
		NodeItemInfo nodeItemInfo = unDir (file);
		NodeItemInfo.show (0, nodeItemInfo);
	}
	@SuppressWarnings("unused")
	private void jarTest() {
//		String strJarFile = "c:/tmp/1/ant-swing-1.6.2.jar";
		File file = new File ("c:/tmp/1/connector.jar");
		NodeItemInfo nodeItemInfo = unJar (file);
		NodeItemInfo.show (0, nodeItemInfo);
	}
	@SuppressWarnings("unused")
	private void zipTest() {
		File file = new File ("c:/tmp/1/Auction.zip");
//		NodeItemInfo nodeItemInfo = listZip (file, m_strWorkDir);
		NodeItemInfo nodeItemInfo = unZip (file);
		NodeItemInfo.show (0, nodeItemInfo);
	}
	@SuppressWarnings("unused")
	private void gzTest() {
		File file = new File ("c:/tmp/1/xml-commons-external-1.2.01-src.tar.gz");
		NodeItemInfo nodeItemInfo = unGz (file);
		NodeItemInfo.show (0, nodeItemInfo);
	}
	@SuppressWarnings("unused")
	private void tarTest() {
		File file = new File ("c:/tmp/1/abc.tar");
		NodeItemInfo nodeItemInfo = unTar (file);
		NodeItemInfo.show (0, nodeItemInfo);
	}
	/*
	 * 1 = file
	 * 2 = directory
	 * 3 = zip
	 * 4 = jar
	 * 5 = gz
	 * 6 = tar
	 * 7 = html
	 */
	public NodeItemInfo unPack (File file, File workingDirectory) {
		NodeItemInfo nodeItemInfo = new NodeItemInfo (file);
		int type = nodeItemInfo.getType();
		switch (type) {
		case 2:
			return unDir (file);
		case 3:
			return unZip (file, workingDirectory);
		case 4:
			return unJar (file, workingDirectory);
		case 5:
			return unGz (file, workingDirectory);
		case 6:
			return unTar (file, workingDirectory);
		case 1:
		case 7:
		default:
			;
		}
		return nodeItemInfo;
	}
	public NodeItemInfo unPack (File file) {
		File workingDirectory = Utils.makeWorkingDirectory (m_workingDirectory);
		return unPack (file, workingDirectory);
	}

	public NodeItemInfo unDir (File currentFile) {
		NodeItemInfo nodeItemInfo = new NodeItemInfo (currentFile);
		if (currentFile.isDirectory()) {
			File[] allFiles = currentFile.listFiles();
			for (int i = 0; i < allFiles.length; i++)
				nodeItemInfo.getNodeInfo().add (new NodeItemInfo (allFiles[i]));
		}
		return nodeItemInfo;
	}
	public NodeItemInfo listDir (File currentFile) {
		NodeItemInfo nodeItemInfo = new NodeItemInfo (currentFile);
		if (currentFile.isDirectory()) {
			File[] allFiles = currentFile.listFiles();
			for (int i = 0; i < allFiles.length; i++)
//				nodeItemInfo.getNodeInfo().add (new NodeItemInfo (allFiles[i]));
				nodeItemInfo.getNodeInfo().add (listDir (allFiles[i]));
		}
		return nodeItemInfo;
	}
	public NodeItemInfo unJar (File file, File workingDirectory) {
		System.out.println("--- unJar");
		return listDir (unJarToFileSystem (file, workingDirectory));
	}
	public NodeItemInfo unJar (File file) {
		System.out.println("--- unJar");
		File workingDirectory = Utils.makeWorkingDirectory (m_workingDirectory);
		return listDir (unJarToFileSystem (file, workingDirectory));
	}
	private File unJarToFileSystem (File file, File workingDirectory) {
		System.out.println("Opening JAR file "+file.getPath());
		System.out.println("Output Directory "+workingDirectory.getPath());
		try {
			JarInputStream jarInput = new JarInputStream (new FileInputStream (file));
			JarEntry jarEntry;
			while ((jarEntry = jarInput.getNextJarEntry()) != null) {
				if (jarEntry.isDirectory()) continue;	// ignore directory entries	
				File currentFile = Utils.convertToOSName (workingDirectory.getPath() + File.separatorChar + jarEntry.getName());
				makeDirectories (currentFile);

				FileOutputStream fos = new FileOutputStream (currentFile);
				BufferedOutputStream bufOut = new BufferedOutputStream (fos, DATA_BLOCK_SIZE);
				byte[] data = new byte[1024];
				int byteCount;
				while ((byteCount = jarInput.read (data, 0, DATA_BLOCK_SIZE)) != -1) {
					bufOut.write(data, 0, byteCount);
				}
				bufOut.flush();
				bufOut.close();
			}
			System.out.println ("Closing JAR file "+file.getPath());					
			jarInput.close();
		}
		catch (IOException ex) {
			System.out.println("Unable to Unjar the jar file; "+ex.getMessage());
		}
		return workingDirectory;
	}

	public NodeItemInfo unZip (File file, File workingDirectory) {
		System.out.println("--- unZip");
		return listDir (unZipToFileSystem (file, workingDirectory));
	}
	public NodeItemInfo unZip (File file) {
		System.out.println("--- unZip");
		File workingDirectory = Utils.makeWorkingDirectory (m_workingDirectory);
		return listDir (unZipToFileSystem (file, workingDirectory));
	}
	private File unZipToFileSystem (File file, File workingDirectory) {
		System.out.println("--- unZipToFileSystem");
		try {
			ZipInputStream zipInput = new ZipInputStream (new FileInputStream (file));
			ZipEntry zipEntry;
			int byteCount;
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) continue;
				File currentFile = Utils.convertToOSName (workingDirectory.getPath() + File.separatorChar + zipEntry.getName());
				makeDirectories (currentFile);

				FileOutputStream fos = new FileOutputStream (currentFile);
				BufferedOutputStream bufOut = new BufferedOutputStream (fos, DATA_BLOCK_SIZE);
				byte[] data = new byte[DATA_BLOCK_SIZE];
        		while ((byteCount = zipInput.read (data, 0, DATA_BLOCK_SIZE)) != -1) {
					bufOut.write (data, 0, byteCount);
				}
				bufOut.flush();
				bufOut.close();
			}
			zipInput.close();
		}
		catch (IOException e) {
			System.out.println("Trouble; "+e.getMessage());
		}
		return workingDirectory;
	}

	public NodeItemInfo unGz (File file, File workingDirectory) {
		return listDir (unGzToFileSystem (file, workingDirectory));
	}
	public NodeItemInfo unGz (File file) {
		File workingDirectory = Utils.makeWorkingDirectory (m_workingDirectory);
		return listDir (unGzToFileSystem (file, workingDirectory));
	}
	private File unGzToFileSystem (File file, File workingDirectory) {
		System.out.println("Opening GZ file "+file.getPath());
		System.out.println("Output Directory "+workingDirectory.getPath());
        String outFilename = unGZName (file);
		File currentFile = Utils.convertToOSName (workingDirectory.getPath() + File.separatorChar + outFilename);
		try {
	        GZIPInputStream gzipInputStream = new GZIPInputStream (new FileInputStream (file));
	        System.out.println("Opening the output file............. : opened");
			makeDirectories (currentFile);
			FileOutputStream fos = new FileOutputStream (currentFile);

	        System.out.println("Transferring bytes from the compressed file to the output file........: Transfer successful");
	        byte[] buf = new byte[1024];  //size can be changed according to programmer's need.
	        int len;
	        while ((len = gzipInputStream.read(buf)) > 0) {
	          fos.write(buf, 0, len);
	        }
	        System.out.println("The file and stream is .....closing.......... : closed"); 
	        gzipInputStream.close();
	        fos.close();
		}
		catch (IOException e) {
			System.out.println("Trouble "+e.getMessage());
		}
		return workingDirectory;
	}

	public NodeItemInfo unTar (File file, File workingDirectory) {
		return listDir (unTarToFileSystem (file, workingDirectory));
	}
	public NodeItemInfo unTar (File file) {
		File workingDirectory = Utils.makeWorkingDirectory (m_workingDirectory);
		return listDir (unTarToFileSystem (file, workingDirectory));
	}
	private File unTarToFileSystem (File file, File workingDirectory) {
		try {
			System.out.println("Opening TAR file "+file.getPath());
			TarInputStream tarInput = new TarInputStream (new FileInputStream (file));
			TarEntry tarEntry;
			while ((tarEntry = tarInput.getNextEntry()) != null) {
				if (tarEntry.isDirectory()) continue;
				File currentFile = Utils.convertToOSName (workingDirectory.getPath() + File.separatorChar + tarEntry.getName());
				makeDirectories (currentFile);

				FileOutputStream fos = new FileOutputStream (currentFile);
				tarInput.copyEntryContents (fos);
	            fos.close();
			}
			System.out.println("Closing TAR file "+file.getPath());					
			tarInput.close();
		}
		catch (IOException ex) {
			System.out.println("Unable to UnTar the Tar file; "+ex.getMessage());
		}
		return workingDirectory;
	}

	public String unGZName (File file) {
//		System.out.println(">>> unGZName; file :"+file.getPath()+":");
		String unGZName = JVFile.getName (file.getName());
//		System.out.println("<<< unGZName; file :"+unGZName+":");
		return unGZName;
	}

	public boolean makeDirectories (File file) {
		System.out.println("--- Expander::makeDirectories; file :"+file.getPath()+":");
		file = new File (file.getPath());
		return file.getParentFile().mkdirs();
//		System.out.println("<<< makeDirectories; bool "+bool);
	}
	public boolean makeFullDirectories (File file) {
		System.out.println("--- Expander::makeFullDirectories; file :"+file.getPath()+":");
		file = new File (file.getPath());
		return file.mkdirs();
//		System.out.println("<<< makeDirectories; bool "+bool);
	}

	public NodeItemInfo listZip (File file) {
		NodeItemInfo nodeItemInfo = new NodeItemInfo (file);
		try {
			ZipFile zf = new ZipFile(file);
			for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
            	String zipEntryName = ((ZipEntry) entries.nextElement()).getName();
            	System.out.println("Zipfile entry "+zipEntryName);
//            	nodeItemInfo.getNodeInfo().add (new NodeItemInfo (zipEntryName));
			}
		}
		catch (IOException e) {
			System.out.println("Trouble "+e.getMessage());
		}
		return nodeItemInfo;
	}
	public NodeItemInfo listJar (File file) {
		NodeItemInfo nodeItemInfo = new NodeItemInfo (file);
		try {
			System.out.println("Opening JAR file "+file.getPath());
			JarInputStream jarInput = new JarInputStream (new FileInputStream(file));
			JarEntry jarEntry;
			while ((jarEntry = jarInput.getNextJarEntry()) != null) {
				System.out.println("Jar Entry; "+jarEntry.getName());
//				nodeItemInfo.getNodeInfo().add (new NodeItemInfo (jarEntry.getName()));
			}
			System.out.println("Closing JAR file "+file.getPath());					
			jarInput.close();
		}
		catch (IOException ex) {
			System.out.println("Unable to Unjar the jar file; "+ex.getMessage());
		}
		return nodeItemInfo;
	}
	public NodeItemInfo listTar (File file) {
		NodeItemInfo nodeItemInfo = new NodeItemInfo (file);
		try {
			System.out.println("Opening TAR file "+file.getPath());
			TarInputStream tarInput = new TarInputStream (new FileInputStream (file));
			TarEntry tarEntry;
			while ((tarEntry = tarInput.getNextEntry()) != null) {
				System.out.println("TAR Entry; "+tarEntry.getName());
//				nodeItemInfo.getNodeInfo().add (new NodeItemInfo (tarEntry.getName()));
			}
			System.out.println("Closing TAR file "+file.getPath());					
			tarInput.close();
		}
		catch (IOException ex) {
			System.out.println("Unable to UnTar the Tar file; "+ex.getMessage());
		}
		return nodeItemInfo;
	}
}
/*
public InputStream getInputStream(String tarFileName) throws Exception{
      if(tarFileName.substring(tarFileName.lastIndexOf(".") + 1, tarFileName.lastIndexOf(".") + 3).equalsIgnoreCase("gz")){
         System.out.println("Creating an GZIPInputStream for the file");
         return new GZIPInputStream(new FileInputStream(new File(tarFileName)));
      }else{
         System.out.println("Creating an InputStream for the file");
         return new FileInputStream(new File(tarFileName));
      }
   }
 
   public void readTar(InputStream in, String untarDir) throws IOException{
      System.out.println("Reading TarInputStream... (using classes from http://www.trustice.com/java/tar/)");
      TarInputStream tin = new TarInputStream(in);
      TarEntry tarEntry = tin.getNextEntry();
      if(new File(untarDir).exists()){
	      while (tarEntry != null){
	         File destPath = new File(untarDir + File.separatorChar + tarEntry.getName());
	         System.out.println("Processing " + destPath.getAbsoluteFile());
	         if(!tarEntry.isDirectory()){
// start of new code
				String pathStr = destPath.getPath();
				int idx = pathStr.lastIndexOf(File.separatorChar);
				if (idx > 0) {
					File destDir = new File(pathStr.substring(0,idx));
					destDir.mkdirs();
				} 
// end of new code
	            FileOutputStream fout = new FileOutputStream(destPath);
	            tin.copyEntryContents(fout);
	            fout.close();
	         }else{
	            destPath.mkdir();
	         }
	         tarEntry = tin.getNextEntry();
	      }
	      tin.close();
      }else{
         System.out.println("That destination directory doesn't exist! " + untarDir);
      }
   }
*/

/*
	private void untar(InputStream in, String untarDir) throws IOException {
		
	  System.out.println("Reading TarInputStream... ");
      TarInputStream tin = new TarInputStream(in);
      TarEntry tarEntry = tin.getNextEntry();
      if(new File(untarDir).exists()){
	      while (tarEntry != null){
	         File destPath = new File(untarDir + File.separatorChar + tarEntry.getName());
	         System.out.println("Processing " + destPath.getAbsoluteFile());
	         if(!tarEntry.isDirectory()){
	            FileOutputStream fout = new FileOutputStream(destPath);
	            tin.copyEntryContents(fout);
	            fout.close();
	         }else{
	            destPath.mkdir();
	         }
	         tarEntry = tin.getNextEntry();
	      }
	      tin.close();
      }else{
         System.out.println("That destination directory doesn't exist! " + untarDir);
      }
*/
/*
	public NodeItemInfo unZip99 (File file) {
		NodeItemInfo nodeItemInfo = new NodeItemInfo (file);
		File workingDirectory = Utils.makeWorkingDirectory();
		try {
			ZipInputStream zipInput = new ZipInputStream (new FileInputStream (file));
			ZipEntry zipEntry;
			int byteCount;
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) continue;
				File currentFile = Utils.convertToOSName (workingDirectory.getPath() + File.separatorChar + zipEntry.getName());
				makeDirectories (currentFile);
				nodeItemInfo.getNodeInfo().add (new NodeItemInfo (currentFile));

				FileOutputStream fos = new FileOutputStream (currentFile);
				BufferedOutputStream bufOut = new BufferedOutputStream (fos, DATA_BLOCK_SIZE);
				byte[] data = new byte[DATA_BLOCK_SIZE];
        		while ((byteCount = zipInput.read (data, 0, DATA_BLOCK_SIZE)) != -1) {
					bufOut.write (data, 0, byteCount);
				}
				bufOut.flush();
				bufOut.close();
			}
			zipInput.close();
		}
		catch (IOException e) {
			System.out.println("Trouble; "+e.getMessage());
		}
		return nodeItemInfo;
	}
*/
/*
	public NodeItemInfo unZip99 (File file) {
		NodeItemInfo nodeItemInfo = new NodeItemInfo (file);
		File workingDirectory = Utils.makeWorkingDirectory();
		try {
			ZipInputStream zipInput = new ZipInputStream (new FileInputStream (file));
			ZipEntry zipEntry;
			int byteCount;
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) continue;
				File currentFile = Utils.convertToOSName (workingDirectory.getPath() + File.separatorChar + zipEntry.getName());
				makeDirectories (currentFile);
				nodeItemInfo.getNodeInfo().add (new NodeItemInfo (currentFile));

				FileOutputStream fos = new FileOutputStream (currentFile);
				BufferedOutputStream bufOut = new BufferedOutputStream (fos, DATA_BLOCK_SIZE);
				byte[] data = new byte[DATA_BLOCK_SIZE];
        		while ((byteCount = zipInput.read (data, 0, DATA_BLOCK_SIZE)) != -1) {
					bufOut.write (data, 0, byteCount);
				}
				bufOut.flush();
				bufOut.close();
			}
			zipInput.close();
		}
		catch (IOException e) {
			System.out.println("Trouble; "+e.getMessage());
		}
		return nodeItemInfo;
	}

*/
