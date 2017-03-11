package com.idc.diff.file;

public class PrintOut {
	private static final int STATUS_IDLE = 0;
	private static final int STATUS_DELETE = 1;
	private static final int STATUS_INSERT = 2;
//	private static final int STATUS_MOVE_NEW = 3;
//	private static final int STATUS_MOVE_OLD = 4;
//	private static final int STATUS_SAME = 5;
	private static final int STATUS_CHANGE = 6;

	private Diff m_diff;

	private boolean m_anyPrinted;
	private int m_printStatus;
	private int m_printOldLine;
	private int m_printNewLine;

	public PrintOut(Diff diff) {m_diff = diff;}

	public boolean printOut() {
		m_printStatus = STATUS_IDLE;
		m_anyPrinted = false;
		for (m_printOldLine = m_printNewLine = 1; ;) {
			if (m_printOldLine > m_diff.getOldInfo().getMaxLine()) {
				newConsume();
				break;
			}
			if (m_printNewLine > m_diff.getNewInfo().getMaxLine()) {
				oldConsume();
				break;
			}
			if (m_diff.getNewInfo().getOther(m_printNewLine) < 0 ) {
				if (m_diff.getOldInfo().getOther(m_printOldLine) < 0)
					showChange();
				else
					showInsert();
			}
			else if (m_diff.getOldInfo().getOther(m_printOldLine) < 0)
				showDelete();
			else if (m_diff.getBlocklen(m_printOldLine) < 0)
				skipOld();
			else if (m_diff.getOldInfo().getOther(m_printOldLine) == m_printNewLine)
				showSame();
			else
				showMove();
		}
		if (m_anyPrinted == true)
			m_diff.printLine(">>>> End of differences.");
		else
			m_diff.printLine(">>>> Files are identical.");
		return (! m_anyPrinted);
	}

	/*
	 * newConsume. Have run out of old file. 
	 * Print the rest of the new file, as inserts and/or moves.
	 */
	private void newConsume() {
		for( ; ;) {
			if (m_printNewLine > m_diff.getNewInfo().getMaxLine())
				break;	/* end of file */
			if (m_diff.getNewInfo().getOther(m_printNewLine) < 0)
				showInsert();
			else
				showMove();
		}
	}

	/**
	 * oldConsume. Have run out of new file.
	 * Process the rest of the old file, printing any
	 * parts which were deletes or moves.
	 */
	private void oldConsume() {
		for( ; ;) {
			if (m_printOldLine > m_diff.getOldInfo().getMaxLine())
				break;	   /* end of file */
			m_printNewLine = m_diff.getOldInfo().getOther(m_printOldLine);
			if (m_printNewLine < 0)
				showDelete();
			else if (m_diff.getBlocklen(m_printOldLine) < 0)
				skipOld();
			else
				showMove();
		}
	}

	/**
	 * showDelete
	 * Expects m_printOldLine is at a deletion.
	 */
	private void showDelete() {
		if (m_printStatus != STATUS_DELETE)
			m_diff.printLine( ">>>> DELETE AT " + m_printOldLine);
		m_printStatus = STATUS_DELETE;
		m_diff.printLine(
			m_diff.getOldInfo().getSymbol(m_printOldLine).getSymbol());
		m_anyPrinted = true;
		m_printOldLine++;
	}

	/*
	 * showInsert
	 * Expects m_printNewLine is at an insertion.
	 */
	private void showInsert() {
		if (m_printStatus == STATUS_CHANGE)
			m_diff.printLine(">>>>	 CHANGED TO" );
		else if (m_printStatus != STATUS_INSERT) 
			m_diff.printLine(">>>> INSERT BEFORE " + m_printOldLine);
		m_printStatus = STATUS_INSERT;
		m_diff.printLine(
			m_diff.getNewInfo().getSymbol(m_printNewLine).getSymbol());
		m_anyPrinted = true;
		m_printNewLine++;
	}

	/**
	 * showChange
	 * Expects m_printNewLine is an insertion.
	 *  Expects m_printOldLine is a deletion.
	 */
	private void showChange() {
		if (m_printStatus != STATUS_CHANGE) 
			m_diff.printLine( ">>>> " + m_printOldLine + " CHANGED FROM");
		m_printStatus = STATUS_CHANGE;
		m_diff.printLine(
			m_diff.getOldInfo().getSymbol(m_printOldLine).getSymbol());
		m_anyPrinted = true;
		m_printOldLine++;
	}

	/**
	 * skipOld
	 * Expects m_printOldLine at start of an old block that has
	 * already been announced as a move.
	 * Skips over the old block.
	 */
	private void skipOld() {
		m_printStatus = STATUS_IDLE;
		for ( ; ;) {
			if (++m_printOldLine > m_diff.getOldInfo().getMaxLine())
				break;	 /* end of file  */
			if (m_diff.getOldInfo().getOther(m_printOldLine) < 0)
				break;	/* end of block */
			if (m_diff.getBlocklen(m_printOldLine) != 0)
				break;	  /* start of another */
		}
	}

	/**
	 * skipNew
	 * Expects m_printNewLine is at start of a new block that has
	 * already been announced as a move.
	 * Skips over the new block.
	 */
	private void skipNew() {
		int oldline;
		m_printStatus = STATUS_IDLE;
		for ( ; ;) {
			if (++m_printNewLine > m_diff.getNewInfo().getMaxLine())
				break;	/* end of file  */
			oldline = m_diff.getNewInfo().getOther(m_printNewLine);
			if (oldline < 0)
				break;			 /* end of block */
			if (m_diff.getBlocklen(oldline) != 0)
				break;		  /* start of another */
		}
	}

	/**
	 * showSame
	 * Expects m_printNewLine and m_printOldLine at start of
	 * two blocks that aren't to be displayed.
	 */
	private void showSame() {
		int count;
		m_printStatus = STATUS_IDLE;
		if (m_diff.getNewInfo().getOther(m_printNewLine) != m_printOldLine) {
			System.err.println("BUG IN LINE REFERENCING");
			System.exit(1);
		}
		count = m_diff.getBlocklen(m_printOldLine);
		m_printOldLine += count;
		m_printNewLine += count;
	}

	/**
	 * showMove
	 * Expects m_printOldLine, m_printNewLine at start of
	 * two different blocks ( a move was done).
	 */
	private void showMove() {
		int oldblock = m_diff.getBlocklen(m_printOldLine);
		int newother = m_diff.getNewInfo().getOther(m_printNewLine);
		int newblock = m_diff.getBlocklen(newother);

		if (newblock < 0)
			skipNew();	 // already printed.
		else if (oldblock >= newblock) {	 // assume new's blk moved.
			m_diff.setBlocklen(newother, -1);	 // stamp block as "printed".
			m_diff.printLine(">>>> " + newother + 
					" THRU " + (newother + newblock - 1) + 
					" MOVED TO BEFORE " + m_printOldLine);
			for ( ; newblock > 0; newblock--, m_printNewLine++)
				m_diff.printLine(
					m_diff.getNewInfo().getSymbol(m_printNewLine).getSymbol());
			m_anyPrinted = true;
			m_printStatus = STATUS_IDLE;
		} else		/* assume old's block moved */
			skipOld();	  /* target line# not known, display later */
	}
}
