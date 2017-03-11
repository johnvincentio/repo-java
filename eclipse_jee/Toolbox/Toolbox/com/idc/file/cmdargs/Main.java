package com.idc.file.cmdargs;

public class Main {
	public static void main (String[] args) {
		Main main = new Main();
		main.doTest(args);
	}
	public void doTest (String[] args) {
		Processor processor = null;
		try {
			processor = new Processor(this, args);
			processor.add ('b', "print brief results",false);
			processor.add ('c', "print compressed results",false);
			processor.add ('d', "print detailed results",false);
			processor.add ('h', "display usage information",false);
			processor.add ('f', "display floating number", 0.0f, 1.0f, 10.0f);
			processor.add ('p', "run NN passes", 1, 1, 99);
			processor.add ('e', "exit NN times", 0, 0, 99);
			processor.add ('x', "display text","yup!");
			processor.process();

			String[] myArgs = processor.getArgs();
			for (int i=0; i<myArgs.length; i++)
				System.out.println("Main:arg "+i+" value "+myArgs[i]);

			boolean m_briefFlag = processor.getBooleanValue('b');
			boolean m_compFlag = processor.getBooleanValue('c');
			boolean m_detailFlag = processor.getBooleanValue('d');
			boolean m_helpFlag = processor.getBooleanValue('h');
			float m_float = processor.getFloatValue('f');
			int m_passCount = processor.getIntValue('p');
			int m_exitCount = processor.getIntValue('e');
			String m_extraText = processor.getStringValue('x');

			System.out.println("m_briefFlag "+m_briefFlag);
			System.out.println("m_compFlag "+m_compFlag);
			System.out.println("m_detailFlag "+m_detailFlag);
			System.out.println("m_helpFlag "+m_helpFlag);
			System.out.println("m_float "+m_float);
			System.out.println("m_passCount "+m_passCount);
			System.out.println("m_exitCount "+m_exitCount);
			System.out.println("m_extraText "+m_extraText);
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			processor.showUsage();
		}
	}
}

