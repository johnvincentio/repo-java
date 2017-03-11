package com.idc.file.cmdargs;

import java.util.ArrayList;
import java.util.List;

public class Processor {
	private Args m_args = new Args();
	private Args m_cmdArgs = new Args();
	private Template m_template;

	public Processor (Object obj, String[] args) {
		m_template = new Template(obj);
		for (int i=0; i<args.length; i++) m_cmdArgs.add(args[i].trim());
		m_cmdArgs.show("cmdArgs");
	}
	public void add (char flag, String desc, boolean b) {
		m_template.add (new BoolDef(b, flag, desc));
	}
	public void add (char flag, String desc, int i, int min, int max) {
		m_template.add (new IntDef(i, flag, desc, min, max));
	}
	public void add (char flag, String desc, String s) {
		m_template.add (new StringDef(s, flag, desc));
	}
	public void add (char flag, String desc, float f, float min, float max) {
		m_template.add (new FloatDef(f, flag, desc, min, max));
	}
	private ParameterDef getValidDef (char chr) {
		ParameterDef def = m_template.find(chr);
		if (def == null)
			throw new ProcessorException (chr+" is not a valid flag");
		return def;
	}
	public boolean getBooleanValue(char chr) {
		return ((BoolDef) getValidDef(chr)).getValue();
	}
	public int getIntValue(char chr) {
		return ((IntDef) getValidDef(chr)).getValue();
	}
	public float getFloatValue(char chr) {
		return ((FloatDef) getValidDef(chr)).getValue();
	}
	public String getStringValue(char chr) {
		return ((StringDef) getValidDef(chr)).getValue();
	}
	public String[] getArgs() {return m_args.getArgs();}
	public void showUsage() {m_template.showUsage();}

	public void process() {
		String strArg;
		char chr;
		Flags flags;
		ParameterDef def;

		m_template.showUsage();
		try {
			while (m_cmdArgs.hasNext()) {
				strArg = m_cmdArgs.getNext();
				if (! isCommand(strArg))
					m_args.add(strArg);
				else {
					flags = new Flags(strArg);
					while (flags.hasNext()) {
						chr = flags.getNext();
						if (isCommand(chr)) continue;
						def = getValidDef(chr);
						if (def instanceof BoolDef)
							def.setValue("");
						else {
							if (! m_cmdArgs.hasNext())
								throw new ProcessorException(
									"Flag "+chr+" needs a value");
							String strTmp = m_cmdArgs.getNext();
							def.setValue(strTmp);
						}
					}
				}
			}
		}
		catch (Exception ex) {
			throw new ProcessorException(ex.getMessage());
		}
		m_template.show("args");
		m_args.show("args");
	}
	private boolean isCommand (String str) {return isCommand (str.charAt(0));}
	private boolean isCommand (char chr) {
		if (chr == '-' || chr == '/') return true;
		return false;
	}
	private class Args {
		private List<String> m_list = new ArrayList<String>();
		private int m_pos = 0;
		public Args() {}
		public void add (String s) {m_list.add(s);}
		public int length() {return m_list.size();}
		public boolean hasNext() {return m_pos < length();}
		public String getNext() {return (String) m_list.get(m_pos++);}
//		public String peekCurrent() {return (String) m_list.get(m_pos);}
//		public int getPos() {return m_pos;}
		public String[] getArgs() {
			if (length() < 1) return null;
			String[] strArray = new String[length()];
			for (int i=0; i<length(); i++) strArray[i] = (String) m_list.get(i);
			return strArray;
		}
		public void show (String msg) {
			System.out.println("Show args :"+msg);
			for (int i=0; i<length(); i++)
				System.out.println(" arg "+i+" value "+m_list.get(i));
		}
	}
	private class Flags {
		private String m_string;
		private int m_pos = 0;
		public Flags(String str) {m_string = str;}

		public int length() {return m_string.length();}
		public boolean hasNext() {return m_pos < length();}
		public char getNext() {return m_string.charAt(m_pos++);}
//		public char peekCurrent() {return m_string.charAt(m_pos);}
//		public int getPos() {return m_pos;}
	}
}

