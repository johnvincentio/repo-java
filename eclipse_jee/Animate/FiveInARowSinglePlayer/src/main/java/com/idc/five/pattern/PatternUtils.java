package com.idc.five.pattern;

public class PatternUtils {
	/*
	Pattern 0;
		I
		I
		I
	
	Pattern 1;
       -----
	
	Pattern 2;
          /
         /
        /
	Pattern 3;
        \
         \
          \
 */
	private static Pattern[] m_victoryPattern;
	static {
		m_victoryPattern = new Pattern[4];
		m_victoryPattern[0] = new Pattern (1, 0);
		m_victoryPattern[1] = new Pattern (0, 1);
		m_victoryPattern[2] = new Pattern (1, -1);
		m_victoryPattern[3] = new Pattern (1, 1);		
	}
	public static int getVictoryPatternRow (int pattern) {
		assert pattern >= 0 && pattern < getNumberOfVictoryPatterns();
		return m_victoryPattern[pattern].getRow();
	}
	public static int getVictoryPatternCol (int pattern) {
		assert pattern >= 0 && pattern < getNumberOfVictoryPatterns();
		return m_victoryPattern[pattern].getCol();
	}
	public static final int getNumberOfVictoryPatterns() {return m_victoryPattern.length;}

	public static boolean isValidPattern (int pattern) {
		return pattern >= 0 && pattern < getNumberOfVictoryPatterns();
	}
}
