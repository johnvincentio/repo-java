
package com.idc.kakuro;

public class Answer {
	private int m_wheels;		// number of wheels in use
	private int[] m_anAnswer;	// a bank of wheel settings

	/**
	 * Constructor to initialize the number of wheels
	 * 
	 * @param wheels	number of wheels
	 */
	public Answer (int wheels) {
		m_wheels = wheels;
		m_anAnswer = new int[m_wheels];
	}

	/**
	 * Constructor to initialize the number of wheels and the setting of the wheels
	 * 
	 * @param wheels	number of wheels
	 * @param ans		setting of the wheels
	 */
	public Answer (int wheels, Answer ans) {
		this (wheels);
		set (ans);
	}
	/**
	 * Set wheel settings to the settings that have been passed
	 * 
	 * @param ans	wheel settings
	 */
	public void set (Answer ans) {
		for (int i=0; i<m_wheels; i++)
			m_anAnswer[i] = ans.get(i);
	}

	/**
	 * Set the wheel number to the value
	 * 
	 * @param wheel		wheel number
	 * @param value		wheel value
	 */
	public void set (int wheel, int value) {m_anAnswer[wheel] = value;}

	/**
	 * Get the value of a wheel
	 * 
	 * @param wheel		the wheel number
	 * @return			value of the wheel number
	 */
	public int get (int wheel) {return m_anAnswer[wheel];}

	/**
	 * Calculate value of all wheels
	 * 
	 * @return	value of all wheels
	 */
	public int getTotal() {
		int total = 0;
		for (int i=0; i<m_wheels; i++)
			total += m_anAnswer[i];
		return total;
	}

	/**
	 * Determine if wheel values are legal
	 * 		Duplicates are not allowed
	 * 		If a wheel value is zero, the wheel has not yet been moved, thus a solution is still possible.
	 * 
	 * @return	true if wheel values are legal
	 */
	public boolean isPossible() {
		for (int i=0; i<m_wheels; i++) {
			int value = m_anAnswer[i];
			if (value == 0) continue;
			if (count(value) > 1) return false; 
		}
		return true;
	}

	/**
	 * Count number of wheels that are set to value
	 * 
	 * @param value		wheel value to look for
	 * @return			number of wheels that are set to value
	 */
	public int count (int value) {
		int total = 0;
		for (int i=0; i<m_wheels; i++) {
			if (m_anAnswer[i] == value) total++;
		}
		return total;
	}

	/**
	 * Verify if this is a possible solution. 
	 * 		Verify correct total
	 * 		All wheels must have a value of [1,9]
	 * 
	 * @param total			Total wheel count for a successful solution
	 * @return				true if this is a possible solution
	 */
	public boolean isSolution (int total) {
		if (total != getTotal()) return false;
		for (int i=0; i<m_wheels; i++) {
			if (m_anAnswer[i] < 1) return false;
		}
		return true;
	}

	/**
	 * Determine whether the parameter wheel settings are identical to this objects wheel settings
	 * 
	 * @param answer		Wheel settings to compare
	 * @return				true if identical
	 */
	public boolean isDuplicate (Answer answer) {
		int same = 0;
		for (int i=0; i<m_wheels; i++) {
			if (count (answer.get(i)) > 0) same++;
		}
		if (same >= m_wheels - 1) return true;
		return false;
	}

	/**
	 * Get the String representation of this object
	 * 
	 * @return String representation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_wheels; i++) {
			buf.append(m_anAnswer[i]);
		}
		return buf.toString();
	}
}
