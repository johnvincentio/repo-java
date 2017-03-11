
import java.io.*;
import java.sql.Time;

public class Debug {
  private static String strFile = "";
  private static boolean bConsole = true;
  private static boolean bAppend = false;
  private static boolean bStatus = false;

  public Debug() {}

  private static void setFileName (String name) {strFile = name;}
  private static void setFileName () {strFile = "";}
  private static void setAppend (boolean bValue) {bAppend = bValue;}
  private static boolean getAppend() {return bAppend;}
  private static String getFileName () {return strFile;}
  private static void setConsole (boolean bValue) {bConsole = bValue;}
  private static boolean getConsole() {return bConsole;}
  public static void setStatus (boolean bValue) {bStatus = bValue;}
  private static boolean getStatus() {return bStatus;}
  private static void setConsoleMode () {setConsole(true); setFileName();}

  public static void setFile () {setConsoleMode();}
  public static void setFile (String name, boolean bValue) {
    if ( (name.toUpperCase()).equals("CONSOLE")) {
      setConsoleMode();
    }
    else {
      setFileName(name);
      setAppend(bValue);
      setConsole(false);
    }
    timing("Initializing");
    setStatus(true);
  }

  public static String newLine() {return "\r\n";}
  public static synchronized void println () {print(newLine());}
  public static synchronized void println (String msg) {print(msg + newLine());}
  public static synchronized void print (String msg) {
    if (! getStatus()) return;
    if (getConsole()) {System.out.print (msg); return;}
    try {
      FileWriter out = new FileWriter (strFile,getAppend());
      out.write (msg);
      out.close();
      setAppend(true);
    }
    catch (IOException e) {
      String badName = getFileName();
      setConsoleMode();
      println ("Unable to write to File :" + badName + ":");
      println (msg);
    }
  }
  public static synchronized void timing (String msg) {
    Time time = new Time(System.currentTimeMillis());
    println (time.toString() + "; " + msg);
  }
}
