package com.idc.five.game;

public class SizingItemInfo {
	private static final int MIN_CELL_SIZE = 30;		// minimum cell size in pixels
	private static final int EXTRA_HEIGHT = 163;		// extra height for the top and bottom panels
	private static final int MIN_BOARD_WIDTH = 550;		// minimum board width
	private static final int MIN_FRAME_HEIGHT = MIN_BOARD_WIDTH + EXTRA_HEIGHT;		// minimum frame height

	private int rows;			// rows are rendered vertically, thus affect height
	private int columns;		// columns are rendered horizontally, thus affect width

	private int m_calculatedBoardWidth = MIN_BOARD_WIDTH;		// calculated board width
	private int m_calculatedBoardHeight = MIN_BOARD_WIDTH;		// calculated board height

	private int m_calculatedCellSize = MIN_CELL_SIZE;			// calculated cell size

	private int m_calculatedOffsetWidth = 0;					// distance from frame start to board start

	private int m_calculatedWindowWidth = MIN_BOARD_WIDTH;		// calculated frame width
	private int m_calculatedWindowHeight = MIN_FRAME_HEIGHT;	// calculated frame height

	public SizingItemInfo (int rows, int columns) {
		this.rows = rows;		
		this.columns = columns;
		calculateSizes();			// perform sizing calculations
	}

	/**
	 * Calculate the row, col of this pixel position.
	 * 
	 * @param x		x axis distance from origin of component (width or columns)
	 * @param y		y axis distance from origin of component (height or rows)
	 * @return		Coordinate object with row, col of this x, y pixel position
	 */
	public Coordinate calculateCoordinate (int x, int y) {
//		System.out.println(">>> SizingItemInfo::calculateCoordinate; x "+x+" y "+y);
		int xcnt;
		int xpos = x - (m_calculatedOffsetWidth / 2);
		if (xpos < 0 || xpos > m_calculatedBoardWidth)
			xcnt = -1;
		else
			xcnt = xpos / m_calculatedCellSize;
//		System.out.println("xpos "+xpos+" xcnt "+xcnt);

		int ycnt;
		int ypos = y;
		if (ypos > m_calculatedBoardHeight)
			ycnt = -1;
		else
			ycnt = ypos / m_calculatedCellSize;
//		System.out.println("ypos "+ypos+" ycnt "+ycnt);

		Coordinate coord = new Coordinate (ycnt, xcnt);
//		System.out.println("<<< SizingItemInfo::calculateCoordinate; coord "+coord);
		return coord;
	}

	private void calculateSizes() {
//		System.out.println(">>> SizingItemInfo::calculateSizes");
		int longestSide = rows > columns ? rows : columns;
//		System.out.println("longestSide "+longestSide+" rows "+rows+" columns "+columns);

		m_calculatedBoardWidth = MIN_BOARD_WIDTH;
		if (longestSide * MIN_CELL_SIZE <= MIN_BOARD_WIDTH) {
			m_calculatedCellSize = MIN_BOARD_WIDTH / longestSide;
		}
		else if (longestSide * MIN_CELL_SIZE > MIN_BOARD_WIDTH) {
			m_calculatedCellSize = MIN_BOARD_WIDTH / longestSide;
			if (m_calculatedCellSize < MIN_CELL_SIZE) {
				m_calculatedCellSize = MIN_CELL_SIZE;
			}
		}
		m_calculatedBoardWidth = columns * m_calculatedCellSize;
		m_calculatedBoardHeight = rows * m_calculatedCellSize;

		if (m_calculatedBoardWidth < MIN_BOARD_WIDTH) {
			m_calculatedOffsetWidth = MIN_BOARD_WIDTH - m_calculatedBoardWidth + 1;
		}

		m_calculatedWindowWidth = m_calculatedBoardWidth + m_calculatedOffsetWidth;
		m_calculatedWindowHeight = m_calculatedBoardHeight + EXTRA_HEIGHT;

//		System.out.println("<<< SizingItemInfo::calculateSizes; item "+toString());
	}

	public int getRows() {return rows;}
	public int getColumns() {return columns;}
	public int getCalculatedBoardWidth() {return m_calculatedBoardWidth;}
	public int getCalculatedBoardHeight() {return m_calculatedBoardHeight;}
	public int getCalculatedCellSize() {return m_calculatedCellSize;}
	public int getCalculatedOffsetWidth() {return m_calculatedOffsetWidth;}
	public int getCalculatedWindowWidth() {return m_calculatedWindowWidth;}
	public int getCalculatedWindowHeight() {return m_calculatedWindowHeight;}

	@Override
	public String toString() {
		return "SizingItemInfo [rows=" + rows + ", columns=" + columns
				+ ", m_calculatedBoardWidth=" + m_calculatedBoardWidth
				+ ", m_calculatedBoardHeight=" + m_calculatedBoardHeight
				+ ", m_calculatedCellSize=" + m_calculatedCellSize
				+ ", m_calculatedOffsetWidth=" + m_calculatedOffsetWidth
				+ ", m_calculatedWindowWidth=" + m_calculatedWindowWidth
				+ ", m_calculatedWindowHeight=" + m_calculatedWindowHeight
				+ "]";
	}
}
