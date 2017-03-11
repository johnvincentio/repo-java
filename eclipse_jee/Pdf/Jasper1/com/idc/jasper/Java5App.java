package com.idc.jasper;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;

/*
Equipment On Rent By start Date
Rental History
Overdue Rentals
Open Invoices
 */

public class Java5App {
	private static final String TASK_COMPILE = "compile";	
	private static final String TASK_FILL = "fill";
	private static final String TASK_PDF = "pdf";

	public static void main(String[] args) 	{
		/*
		if (args.length == 0) {
			usage();
			return;
		}
		String taskName = args[0];
		String fileName = args[1];
*/
		String taskName;
		String fileName;
		/*
		 * Phase 1
		 */
		taskName = TASK_COMPILE;
		fileName = "C:/irac7/wrkspc/Pdf/Jasper1/Java5Report.jrxml";

		/*
		 * Phase 2
		 */
//		taskName = TASK_FILL;
//		fileName = "C:/irac7/wrkspc/Pdf/Jasper1/TextReport.jasper";
		try
		{
			long start = System.currentTimeMillis();
			if (TASK_COMPILE.equals(taskName))
			{
				System.out.println("Before compileReportToFile");
				JasperCompileManager.compileReportToFile(fileName);
				System.err.println("Compile time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_FILL.equals(taskName))
			{
				Map<String,Object> parameters = new HashMap<String,Object>();
				parameters.put("greeting", Greeting.bye);

				System.out.println("Before fillReportToFile");
				JasperFillManager.fillReportToFile(fileName, parameters, new JREmptyDataSource());
				System.err.println("Filling time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_PDF.equals(taskName))
			{
				JasperExportManager.exportReportToPdfFile(fileName);
				System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
			}
			else
			{
				usage();
			}
		}
		catch (JRException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void usage()
	{
		System.out.println("Java5App usage:");
		System.out.println("\tjava Java5App task file");
		System.out.println("\tTasks : fill | pdf");
	}

}
