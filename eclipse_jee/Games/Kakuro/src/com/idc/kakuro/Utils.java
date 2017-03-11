
package com.idc.kakuro;

public class Utils {
	private DataStore m_dataStore;
	private int m_squares;
	private int m_total;
	private Answers m_all = new Answers();
	public Utils (DataStore dataStore, int squares, int total) {
		m_dataStore = dataStore;
		m_squares = squares;
		m_total = total;
	}
	public Answers doCalculate() {
		System.out.println("Handle Count "+m_total+" Squares "+m_squares);
		Answers answers = m_dataStore.getAnswers (m_squares, m_total);
		if (answers != null) {
			System.out.println("Retrieved from storage");
			return answers;
		}

		System.out.println("Calculating results");
		nextMove (0, new Answer(m_squares));

		System.out.println("Adding results to storage");
		m_dataStore.addAnswers (m_squares, m_total, m_all);
		return m_all;
	}
	private void nextMove (int wheel, Answer answer) {
		for (int value = 1; value <= 9; value++) {
			answer.set (wheel, value);
//			System.out.println("wheel no. "+wheel+" wheel value "+value+" answer "+answer.toString());
			if (! answer.isPossible()) continue;
			if (wheel < m_squares - 1)
				nextMove (wheel+1, answer);
			else {
				if (answer.isSolution (m_total))
					m_all.add (new Answer (m_squares, answer));
			}
		}
		answer.set (wheel, 0);
	}
}
