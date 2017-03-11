
package com.idc.http.utils;

import com.idc.trace.JVLog;

import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.io.PrintWriter;

public class ShowServlet {
    private static ShowServlet instance = null;
    private ShowServlet() {}
    public static ShowServlet getInstance() {
        if (instance == null)
              instance = new ShowServlet();                        
        return instance;
    }
    public void showAll (HttpServlet servlet, String msg) {
        showAll (servlet, msg, JVLog.getInstance().getWriter());
    }
    public void showAll (HttpServlet servlet, String msg, PrintWriter out) {
        showServlet (servlet, msg, out);
        showAttributes (servlet, msg, out);
        showInitParameters (servlet, msg, out);                        
    }
    public void showServlet (HttpServlet servlet, String msg) {
         showServlet (servlet, msg, JVLog.getInstance().getWriter());
    }
    public void showServlet (HttpServlet servlet, String msg, PrintWriter out) {        
         out.println(">>> showServlet; "+msg);
         out.println("serverName :"+ servlet.getServletContext().getServletContextName());
         out.println("serverInfo :"+ servlet.getServletContext().getServerInfo());
         out.println("<<< showServlet");                
    }
    public void showAttributes  (HttpServlet servlet, String msg) {
         showAttributes  (servlet, msg, JVLog.getInstance().getWriter());
    }        
    public void showAttributes  (HttpServlet servlet, String msg, PrintWriter out) {
         out.println(">>> showServletAttributes ; "+msg);
         String strElement, strValue;
         Object obj;
         Enumeration<?> enumjv = servlet.getServletContext().getAttributeNames();
         if (enumjv != null) {
              while (enumjv.hasMoreElements()) {
                   strElement = (String) enumjv.nextElement();
                   strValue = "";
                   obj = servlet.getServletContext().getAttribute(strElement);
                       if (obj instanceof String)
                            strValue = (String) obj;
                        else
                            strValue = obj.toString();
                        out.println("Element ("+strElement+") Value ("+strValue+")");
              }
         }
         out.println("<<< showServletAttributes ; "+msg);
     }
     public void showInitParameters (HttpServlet servlet, String msg) {
         showInitParameters (servlet, msg, JVLog.getInstance().getWriter());
     }        
     public void showInitParameters (HttpServlet servlet, String msg, PrintWriter out) {
           out.println(">>> showServletInitParameters; "+msg);
           String strElement, strValue;
           Object obj;
           Enumeration<?> enumjv = servlet.getServletContext().getInitParameterNames();
           if (enumjv != null) {
                while (enumjv.hasMoreElements()) {
                    strElement = (String) enumjv.nextElement();
                    strValue = "";
                    obj = servlet.getServletContext().getInitParameter(strElement);
                    if (obj instanceof String)
                         strValue = (String) obj;
                    else
                         strValue = obj.toString();
                    out.println("Element ("+strElement+") Value ("+strValue+")");
                }
           }
           out.println("<<< showServletInitParameters; "+msg);
     }
}

	