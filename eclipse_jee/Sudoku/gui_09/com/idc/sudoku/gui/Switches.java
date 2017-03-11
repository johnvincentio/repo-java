package com.idc.sudoku.gui;

import com.idc.trace.LogHelper;

public class Switches {
	public static final int MIN_SWITCH = 1;
	public static final int MAX_SWITCH = 9;
	private static final int NUM_SWITCHES = MAX_SWITCH - MIN_SWITCH + 2;
	private boolean m_switches[] = new boolean[NUM_SWITCHES];
	public Switches () {setOn();}		// true => option is available
	public void setOff (int pos) {m_switches[pos] = false;}
	public void setOn (int pos) {m_switches[pos] = true;}
	public boolean get (int pos) {return m_switches[pos];}
	public void setOn() {
		for (int pos=MIN_SWITCH; pos<=MAX_SWITCH; pos++)
			m_switches[pos] = true;
	}
	public void setOff() {
		for (int pos=MIN_SWITCH; pos<=MAX_SWITCH; pos++)
			m_switches[pos] = false;
	}
	public void set (Switches switches) {
		for (int pos=MIN_SWITCH; pos<=MAX_SWITCH; pos++)
			m_switches[pos] = switches.get(pos);
	}
	public String getStringAllSwitches() {
		StringBuffer buf = new StringBuffer();
		for (int i=MIN_SWITCH; i<=MAX_SWITCH; i++) {
			if (m_switches[i])
				buf.append(i);
			else
				buf.append(0);
		}
		return buf.toString();
	}
	public String getStringSetSwitches() {
		StringBuffer buf = new StringBuffer();
		for (int i=MIN_SWITCH; i<=MAX_SWITCH; i++) {
			if (m_switches[i]) buf.append(i);
		}
		return buf.toString();
	}
	public int getCountSwitchesSet() {
		int cntr = 0;
		for (int i=MIN_SWITCH; i<=MAX_SWITCH; i++) {
			if (m_switches[i]) cntr++;
		}
		return cntr;
	}
	public void addOff (Switches switches) { // turn off, not on
		for (int i=MIN_SWITCH; i<=MAX_SWITCH; i++) {
			if (! switches.get(i)) m_switches[i] = false;
		}
	}
	public boolean setSwitchesOff (String pattern) {
		boolean bChange = false;
		int pos;
		for (int i=0; i<pattern.length(); i++) {
			pos = Character.getNumericValue(pattern.charAt(i));
			if (m_switches[pos]) {
				bChange = true;
				setOff(pos);
			}
		}
		return bChange;
	}
	public void show(String msg) {
		LogHelper.debug ("show switches; "+msg);
		for (int i=MIN_SWITCH; i<=MAX_SWITCH; i++) {
			LogHelper.debug (m_switches[i]+" ");
		}
		LogHelper.debug ("");
		LogHelper.debug ("end of show switches");
	}
}
