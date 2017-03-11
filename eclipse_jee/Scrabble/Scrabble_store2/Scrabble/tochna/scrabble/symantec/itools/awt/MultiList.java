package symantec.itools.awt;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Scrollbar;
import java.awt.Panel;
import java.awt.Color;
import java.awt.Image;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Container;
import java.util.BitSet;
import java.util.Vector;


/**
 * Multi-column listbox component.
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */


public class MultiList
    extends Panel
{
    /**
     * Time to register a double click (in milliseconds).
     */
    public final static long CLICKTHRESHOLD = 250;

    /**
     * Left-justify constant.
     */
    public final static int LEFT = 0;

    /**
     * Center-justify constant.
     */
    public final static int CENTER = 1;

    /**
     * Right-justify constant.
     */
    public final static int RIGHT = 2;


    static CompareCells func = new CompareCells();

    Color           colorBg = Color.white;
    Color           colorFg = Color.black;
    Color           colorHBg = Color.blue;
    Color           colorHFg = Color.white;
    Matrix          cells = new Matrix();
    boolean         multiSelect = false; //allow multiple selections
    int             splitters[];
    String          headings[];
    int             align[];
    Image           cellIm[];
    Color           headingBg = Color.lightGray;
    Color           headingFg = Color.black;
    Font            headingFont;
    Font            cellFont;
    Scrollbar       sb;
    int             cursor = Frame.DEFAULT_CURSOR;
    int             colClick = -1;
    int             memoryClick = -1;
    long            clickTime;
    BitSet          highlightedRows = new BitSet();
    int             selectedRow = 0;
    int             topRow;
    int             dragColumn = -1;
    int             xDragLast = -1;
    boolean         isDragging;
    int             headingHeight = 0;
    int             cellHeight = 0;
    int             cellAscent = 0;
    int             cellDescent = 0;
    int             clickMargin = 5;
    int             currentCursor = Frame.DEFAULT_CURSOR;
    Image           im;
    Graphics        gg;
    int             height = -1;
    int             width = -1;
    int             sbPosition;
    long            scrollbarTimer;


    /**
     * Default constructor for MultiList.
     */
    public MultiList()
    {
        this(0, false, Color.white);
    }

    /**
     * Constructs a new MultiList with the specified number of columns.
     * @param cols the number of columns
     */
    public MultiList(int cols)
    {
        this(cols, false, Color.white);
    }

    /**
     * Constructs a new MultiList with the spcified number of columns
     * and whether multiple row selection allowed.
     * @param cols the number of columns
     * @param multi true for multiple row selection, false otherwise
     */
    public MultiList(int cols, boolean multi)
    {
        this(cols, multi, Color.white);
    }

    /**
     * Constructs a new MultiList with the spcified number of columns
     * and whether multiple row selection allowed, and background color.
     * @param cols the number of columns
     * @param multi true for multiple row selection, false otherwise
     * @param bg instance for background color
     */
    public MultiList(int cols, boolean multi, Color bg)
    {
        createColumns(cols);
        multiSelect = multi;

        setLayout(new BorderLayout());
        setHeadingFont(new Font("Helvetica", Font.PLAIN, 12));
        setCellFont(new Font("Helvetica", Font.PLAIN, 12));
        setBackground(colorBg = bg);
        sb = new Scrollbar(Scrollbar.VERTICAL);
        sb.hide();
//        add("East",sb);
    }

    /**
     * Set the number of columns.
     * @param i new number of columns
     * @see #getColumns
     */
    public void setColumns(int i)
    {
        createColumns(i);
    }

    /**
     * Get the current number of columns.
     * @return int current number of columns
     * @see #setColumns
     */
    public int getColumns()
    {
        return headings.length;
    }

    /**
     * Set heading text and width.
     * @param h string for heading text
     * @param i number of column
     * @param pixels width of column
     * @see #getHeading
     */
    public void setHeading(String h, int i, int pixels)
    {
        headings[i-1] = h;
        splitters[i] = pixels;
        redraw();
        repaint();
    }

    /**
     * Return heading text of specified column.
     * @see #setHeading
     */
    public String getHeading(int i)
    {
        return headings[i];
    }

    /**
     * Set heading text with default width.
     * @param hl array of heading strings
     * @see #getHeadings
     */
    public void setHeadings(String[] list)
    {
        if (list.length == 0) {
            createColumns(0);
            invalidate();
        } else {
            Dimension d = size();
            int w = 0, width = d.width / list.length;
            splitters[0] = width;
            createColumns(list.length);
            for (int i = 0; i < list.length; ++i)
                setHeading(list[i], i + 1, w += width);
        }
        redraw();
        repaint();
    }

    /**
     * Get the heading text.
     * @return String[] - current headings
     * @see #setHeadings
     */
    public String[] getHeadings()
    {
        return headings;
    }

    /**
     * Set the font of the heading text.
     * @param f font instance for heading text
     * @see #getHeadingFont
     */
    public void setHeadingFont(Font f)
    {
        headingFont = f;
        headingHeight = getFontMetrics(f).getHeight() + 4;
        redraw();
        repaint();
    }

    /**
     * Returns the font of the heading text
     * @see #setHeadingFont
     */
    public Font getHeadingFont()
    {
        return headingFont;
    }

    /**
     * Set the font of all cell text.
     * @param f font instance for cell text
     * @see #getCellFont
     */
    public void setCellFont(Font f)
    {
        cellFont = f;
        cellAscent = getFontMetrics(f).getAscent();
        cellDescent = getFontMetrics(f).getDescent();
        cellHeight = getFontMetrics(f).getHeight();
        redraw();
        repaint();
    }

    /**
     * Returns the current font setting for cell text.
     * @see #setCellFont
     */
    public Font getCellFont()
    {
        return cellFont;
    }

    /**
     * Set the heading foreground and background colors.
     * @param fg foreground Color for heading text
     * @param bg background Color for heading text
     * @see #setHeadingFg
     * @see #setHeadingBg
     */
    public void setHeadingColors(Color fg, Color bg)
    {
        headingFg = fg;
        headingBg = bg;
        redraw();
        repaint();
    }

    /**
     * Set the heading foreground color.
     * @param c foreground Color for heading text
     * @see #getHeadingFg
     * @see #setHeadingBg
     * @see #setHeadingColors
     */
    public void setHeadingFg(Color c)
    {
        headingFg = c;
        redraw();
        repaint();
    }

    /**
     * Set the heading background color.
     * @param c backgroun Color for heading text
     * @see #getHeadingBg
     * @see #setHeadingFg
     * @see #setHeadingColors
     */
    public void setHeadingBg(Color c)
    {
        headingBg = c;
        redraw();
        repaint();
    }

    /**
     * Returns the color of the heading foreground.
     * @return Color current heading foreground color
     * @see #setHeadingFg
     */
    public Color getHeadingFg()
    {
        return headingFg;
    }

    /**
     * Returns the color of the heading background.
     * @return Color current heading background color
     * @see #setHeadingBg
     */
    public Color getHeadingBg()
    {
        return headingBg;
    }

    /**
     * Set foreground and background colors of cell text.
     * @param fg foreground color of cell text
     * @param bg background color of cell text
     * @see #setCellFg
     * @see #setCellBg
     */
    public void setCellColors(Color fg, Color bg)
    {
        colorFg = fg;
        colorBg = bg;
        redraw();
        repaint();
    }

    /**
     * Set the foreground color of cell text.
     * @param c foreground Color of cell text
     * @see #getCellFg
     * @see #setCellBg
     * @see #setCellColors
     */
    public void setCellFg(Color c)
    {
        colorFg = c;
        redraw();
        repaint();
    }

    /**
     * Set the background color of cell text.
     * @param c background Color of cell text
     * @see #getCellBg
     * @see #setCellFg
     * @see #setCellColors
     */
    public void setCellBg(Color c)
    {
        colorBg = c;
        redraw();
        repaint();
    }

    /**
     * Get cell foreground color.
     * @return Color current cell foreground color
     * @see #setCellFg
     */
    public Color getCellFg()
    {
        return colorFg;
    }

    /**
     * Get cell background color.
     * @return Color current cell background color
     * @see #setCellBg
     */
    public Color getCellBg()
    {
        return colorBg;
    }

    /**
     * Get the column size in pixels for the specified column.
     * @param i number of column (zero-relative)
     */
    public int getColumnSize(int i)
    {
        return splitters[i];
    }

    /**
     * Set the justification of the text for the specified column.
     * @param i number of column (zero relative)
     * @param align one of the values LEFT, CENTER, or RIGHT
     */
    public void setColumnAlignment(int i, int align)
    {
        // align must be LEFT, CENTER, or RIGHT
        this.align[i] = align;
    }

    /**
     * Returns the current number of columns.
     */
    public int getNumberofCols()
    {
        return headings.length;
    }

    /**
     * Set specified row as the selected row.
     * @param r number of the row to select (zero relative)
     */
    public void setSelectedRow(int r) throws IllegalArgumentException
    {
        if (r > cells.rows())
        {
            throw new IllegalArgumentException(r + " is not a valid row number");
        }

//        selectedRow = r;
//        redraw();
//        repaint();
    }

    /**
     * Returns the index of the selected row (zero relative).
     */
    public int getSelectedRow()
    {
        return selectedRow;
    }

    /**
     * Returns an array of integers representing all highlighted rows.
     * May return null (no rows selected).
     */
    public int[] getSelectedRows()
    {

        int size = highlightedRows.size();
        int count = 0;

        for (int i = 0; i < size; i++)
        {
            if (highlightedRows.get(i))
            {
                count++;
            }
        }

        int[] selections = new int[count];
        count = 0;

        for (int i = 0; i < size; i++)
        {
            if (highlightedRows.get(i))
            {
                selections[count++] = i;
            }
        }

        return selections;
    }

    public boolean mouseDown(Event e, int x, int y)
    {
        if (y < headingHeight)
        {
            // It's a click on the heading area:
            // is it close enough to be a column size drag?
            for (int i=1; i<headings.length; i++)
            {
                if ((x < splitters[i] + clickMargin) &&
                    (x > splitters[i] - clickMargin))
                {
                    dragColumn = i;
                    isDragging = true;
                    mouseDrag(e, x, y); //draw drag line immediately
                    return true;
                }
            }

            // check for sort button push
            for (int i = 0; i < headings.length; i++)
            {
                int max = i == headings.length - 1 ? size().width : splitters[i+1];

                if ((x > splitters[i]) && (x < max))
                {
                   colClick = i;
//                   drawHeading(true);
//                   repaint();
                   return true;
                }
            }

        }

        //else it's a click in the row area
        if (x > width)
        {
            //woops, might have hit the scrollbar
            return false;
        }

        int oldRowSelect = selectedRow;  //save off the old selection

        // set new row selection
        changeSelection(((int) (y - headingHeight - 4)/cellHeight)+topRow, e.modifiers);

        // detect a double click
        if ((selectedRow == oldRowSelect) || (oldRowSelect < 0))
        { // if clicked on same row track time elapsed since last mouseDown
            long clickSpeed = e.when - clickTime;

            if (clickSpeed < CLICKTHRESHOLD)
            {
                //action event
//                postEvent(Event.ACTION_EVENT, selectedRow);
            }
        }

        clickTime  = e.when;

        if (e.modifiers == Event.META_MASK)
        {
            //right mouse click
        }

        return false;
    }

    public boolean mouseDrag(Event e, int x, int y)
    {
        Dimension s = size();

        if (!isDragging)
        {
            // check for sort button push outside of button
            int max;
            int min;

            if (colClick > -1)
            {
                if (colClick == 0)
                {
                    min = 0;
                }
                else
                {
                    min = splitters[colClick];
                }

                if (colClick == headings.length - 1)
                {
                    max = s.width;
                }
                else
                {
                    max = splitters[colClick+1];
                }

                if ((x < min) || (x > max) || (y > headingHeight) || (y < 0))
                {
                   memoryClick = colClick;
                   colClick = -1;
                   drawHeading(false);
                   repaint();
                }
            }
            else if (memoryClick > -1)
            {
                if (memoryClick == 0)
                {
                    min = 0;
                }
                else
                {
                    min = splitters[memoryClick];
                }

                if (memoryClick == headings.length - 1)
                {
                    max = s.width;
                }
                else
                {
                    max = splitters[memoryClick+1];
                }

                if ((x > min) && (x < max) && (y < headingHeight) && (y > 0))
                {
                    colClick = memoryClick;
                    memoryClick = -1;
                    drawHeading(false);
                    repaint();
                }
            }

            return true;
        }

        //fix any drag that went off the left side
        if (x < 0)
        {
            x=0;
        }

        //erase prvious drag line and draw in new position
        gg.setColor(colorBg);
        gg.setXORMode(Color.black);
        gg.drawLine(xDragLast,0,xDragLast,s.height);
        gg.drawLine(x, 0, x, s.height);
        gg.setColor(Color.black);
        gg.setPaintMode();

        //save x position of drag line
        xDragLast = x;
        repaint();

        return true;
    }

    public boolean mouseUp(Event e, int x, int y)
    {
        if (isDragging)
        {
            //fix any drag that went off the left side
            if (x < 0)
            {
                x = 0;
            }

            //erase drag line
            gg.setColor(colorBg);
            gg.setXORMode(Color.black);
            gg.drawLine(xDragLast, 0, xDragLast, size().height);
            gg.setColor(Color.black);
            gg.setPaintMode();
            //turn off dragging
            xDragLast = -1;
            isDragging = false;

            //set column width to dragged position
            splitters[dragColumn] = x;
            redraw();
            repaint();

            return true;
        }

        if (colClick != -1)
        {
            // it was a sorting request
            highlightedRows = new BitSet();
            selectedRow = -1;
//            cells.sort(func, colClick);
            colClick = -1;
//            redraw();
//            repaint();     

            return true;
        }

        return false;
    }

    public boolean mouseMove(Event ev, int x, int y)
    {
        boolean isCloseEnough = false;

        // Use resize cursor ?
        if (y < headingHeight)
        {
            // Moving mouse around in header area
            // is it close enough to be a column size drag?
            for (int i=1; i<headings.length; i++)
            {
                if ((x < splitters[i] + clickMargin) &&
                    (x > splitters[i] - clickMargin))
                {
                    isCloseEnough = true;
                }
            }
        }

        int newCursor = (isCloseEnough? Frame.W_RESIZE_CURSOR : Frame.DEFAULT_CURSOR);

        if (newCursor != currentCursor)
        {
            Frame f = frame();
            if (f != null)
                f.setCursor(currentCursor = newCursor);
        }

        return false;
    }

    public boolean keyDown(Event ev, int key)
    {

        switch (key)
        {
            case Event.DOWN:
            {
                if (selectedRow < cells.rows() - 1)
                {
//                    changeSelection(selectedRow + 1, ev.modifiers);
                }

                break;
            }

            case Event.PGDN:
            {
//                changeSelection(selectedRow + getPageSize(), ev.modifiers);
                break;
            }

            case Event.UP:
            {
                if (selectedRow > 0)
                {
//                    changeSelection(selectedRow - 1, ev.modifiers);
                }

                break;
            }

            case Event.PGUP:
            {
//                changeSelection(selectedRow - getPageSize(), ev.modifiers);
                break;
            }

            case '\n':
            {
                if (selectedRow > -1)
                {
//                    postEvent(Event.ACTION_EVENT, selectedRow);
                }
                break;
            }
        }

        return false;
    }

    public boolean handleEvent(Event e)
    {
        if (e.target == sb && e.arg != null)
        {
            java.util.Date d = new java.util.Date();
            e.when = d.getTime();

            if (((e.when - scrollbarTimer) < 300) && (e.id ==Event.SCROLL_ABSOLUTE))
            {
                return false;
            }

            scrollbarTimer = e.when;

            if (topRow != sb.getValue())
            {
                topRow = sb.getValue();
                sbPosition = topRow;
                redraw();
                repaint();
            }
        }

        return super.handleEvent(e);
    }

    /**
     * Create specified number of columns for MultiList.
     * @param i number of columns for MulitList
     */
    public void createColumns(int i)
    {
        headings = new String[i];
        align = new int[i];
        splitters = new int[i+1];
        cellIm = new Image[i];
    }

    /**
     * Remove all rows from the MulitList.
     */
    public void clear()
    {
        cells.removeAllElements();
        xDragLast = -1;
        isDragging = false;
        selectedRow = 0;
        highlightedRows = new BitSet();
        topRow = 0;
    }

    /**
     * Adds or changes the text of a row and column position.
     * @param r index of row
     * @param c index of column
     * @param s String text for cell
     */
    public void addTextCell(int r, int c, String s)
    {
        Cell cell = new Cell(this, s);
        cells.updateElement(r, c, cell);

        repaint();
    }

    /**
     * Adds or changes the image of a row and column position.
     * @param r index of row
     * @param c index of column
     * @param i image instance for cell
     */
    public void addImageCell(int r, int c, Image i)
    {
        Cell cell = new Cell(this, i);
        cells.updateElement(r, c, cell);

        repaint();
    }

    /**
     * Add/change contents of a cell, both text and image.
     * @param r index of row
     * @param c index of column
     * @parma s string text for cell
     * @param i image instance for cell
     */
    public void addCell(int r, int c, String s, Image i)
    {
        Cell cell = new Cell(this, s, i);
        cells.updateElement(r, c, cell);

        repaint();
    }

    /**
     * Returns string text of specified cell.
     * @param r index of row
     * @param c index of column
     */
    public String getCellText(int r, int c)
    {
        Cell cell = (Cell)cells.elementAt(r,c);

        if (cell != null)
        {
            return cell.text;
        }
        else
        {
            return null;
        }
    }

    /**
     * Adds the string array to the list.  An empty
     * string starts filling the next column.
     * @param items items to add to the list
     * @see #getListItems
     */
    public void setListItems(String[] items)
    {
        clear();
        for (int row = 0; row < items.length; row++)
        {
            String s = items[row];
            int len = s.length();
            int start, end, col;

            for (end = col = start = 0; end <= len && col < headings.length; ++end)
            {
                if (end == len || s.charAt(end) == ';')
                {
                    addCell(row, col++, s.substring(start, end), null);
                    start = end + 1;
                }
           }
           while (col < headings.length)
               addCell(row, col++, "", null);
        }

        redraw();
        repaint();
    }

    /**
     * Returns the current list as a array.
     * @return String[] current list
     * @see #setListItems
     */
    public String[] getListItems()
    {

        Vector v = new Vector();
        String s;
        boolean lastNull = false;
        int row = 0, col = 0;
        while (true)
        {
            if ((s = getCellText(row++, col)) == null)
            {
                if (lastNull)
                {
                    break;
                }
                lastNull = true;
                row = 0;
                ++col;
                s = "";
            }
            else
            {
                lastNull = false;
            }

            if (col==0)
                v.addElement(s);
            else {
                String old=(String)v.elementAt(row);
                old += ";" + s;
                v.setElementAt(old,row);
            }

        }

        int len = v.size();
        String[] items = new String[len];
        for (int i = 0; i < len; ++i)
        {
            items[i] = (String)v.elementAt(i);
        }

        return items;
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    void checkClipping(Graphics g)
    {
        Rectangle r = g.getClipRect();
        Dimension d = size();

        if (r.x != 0 || r.y != 0 ||
            r.width != d.width ||
            r.height != d.height)
        {
            repaint();
        }
    }

    public void paint(Graphics g)
    {
        checkClipping(g);
        Font f = g.getFont();
        Dimension s;

        s = size();

        if (f == null)
        {
            f = headingFont;
            setFont(f);
            return;
        }

        if (width != s.width || height != s.height || symantec.beans.Beans.isDesignTime())
            redraw();

        if (im != null) g.drawImage(im, 0, 0, this);
    }

    public void redraw()
    {
        Dimension s = size();

        if (s.width == 0 || s.height == 0)
            return;

        if (width != s.width || height != s.height)
        {
            im = createImage((width = s.width), height = s.height);
            if (gg != null)
                gg.dispose();
            gg = im.getGraphics();
        }

        gg.setColor(colorBg);
        gg.fillRect(0,0,width,height);

        if (cellHeight > 0 && (cells.rows() * cellHeight) > height-headingHeight)
        {
            sb.setValues(sbPosition, (height/cellHeight), 0, cells.rows()-1);
            sb.setPageIncrement(1);
//          sb.show();
            getParent().paintAll(getParent().getGraphics());
        }
        else
        {
            topRow = 0;
//            sb.hide();
        }

        gg.setFont(headingFont);
        drawHeading(false);

        int count = topRow + (cellHeight > 0 ? (int)((height-3)/cellHeight) - 1 : 0);

        if (count > cells.rows())
        {
            count = cells.rows();
        }

        gg.setFont(cellFont);
        drawRows(0, count, topRow);
    }

    void drawRows(int r, int count, int cellRow)
    {
        MatrixEnumeration e = cells.elements();
        Cell              c = null;
        int     x, w;
        int     cols = headings.length;

        if (cellRow > 0)
        {
            c  = (Cell)e.advanceTo(cellRow);
        }

        //iterate the rows and paint each one
        while (e.hasMoreElements() || c != null)
        {
            //iterate the cols of the current row
            for (int i=0; i<cols; i++)
            {
                if (i < cols-1)
                {
                    w = splitters[i+1] - splitters[i];
                }
                else
                {
                    w = size().width - splitters[i]-4;
                }

                x = i==0 ?1  :splitters[i]-3;
                gg.setColor(highlightedRows.get(cellRow)
                            ?colorHBg
                            :colorBg);
                gg.fillRect(x, headingHeight + r*cellHeight+4, w+4, cellHeight);
                gg.setColor(highlightedRows.get(cellRow)?colorHFg :colorFg);

                if (c == null)
                {
                    c = (Cell)e.nextElement();
                }

                if (c != null && e.currRow() == cellRow && e.currCol() == i)
                {
                    c.drawCell(gg, align[i], splitters[i]+3,
                              headingHeight + r*cellHeight + 4, w, cellHeight, cellAscent);
                    c = null;
                }

                if (c != null && e.currRow() < cellRow)
                {
                    c = null;
                }
            }

            if (--count == 0)
            {
                break;
            }

            cellRow++;
            r++;
        }
    }

    boolean postEvent(int id, int num)
    {
        return postEvent(new Event(this, id, new Integer(num)));
    }

    int getPageSize()
    {
        return ((int)(size().height/cellHeight));
    }

    void draw3DBox(Rectangle r, boolean up)
    {
        int x0 = r.x;
        int y0 = r.y;
        int x1 = r.x + r.width;
        int y1 = r.y + r.height;

        gg.setColor(up?Color.black: Color.white);
        gg.drawLine(x1,   y0, x1,   y1); //right
        gg.drawLine(x1+1, y0, x1+1, y1); //right

        gg.drawLine(x0,y1,x1,y1);        //bottom
        gg.drawLine(x0,y1+1, x1, y1+1);  //bottom

        gg.setColor(up?Color.white:Color.gray);
        gg.drawLine(x0,y0,x1-2,y0);     //top        
        gg.drawLine(x0,y0,x0,y1-1);     //left
    }

    void drawHeading(boolean down)
    {
            Dimension s;
        int x0,y0,x1,y1;
        x0 = 1;
        y0 = 1;
        int xx;
        Font f = getFont();
        gg.setFont(headingFont);
        FontMetrics fm = gg.getFontMetrics();

        s = size();
        
        for (int i=0; i<headings.length; i++)
        {
            x0 = splitters[i];

            if (i < headings.length - 1)
            {
                xx = splitters[i+1];
                x1 = xx - x0;
            }
            else
            {
                //width of last column expands to extreme right
                xx = s.width;
                x1 = xx - x0;
            }

            Rectangle r = new Rectangle(x0,y0,x1-2, headingHeight);

            draw3DBox(r, (colClick==i)?false:true);
            gg.setColor(headingBg);
            gg.fillRect(r.x+1, r.y+1, x1-3, headingHeight-1);
            gg.setColor(headingFg);

            if (headings[i] == null)
                continue;

            int sw = fm.stringWidth(headings[i]);
            int w = x1-3;
            int offset = 0;
            int shift = down && colClick == i ? 1 : 0;

            switch (align[i])
            {
                case MultiList.LEFT:
                {
                    gg.drawString(headings[i], x0+8+shift, headingHeight-3+shift);
                    break;
                }

                case MultiList.CENTER:
                {
                    if (sw>w)
                    {
                        gg.drawString(headings[i], x0+8+shift, headingHeight-3+shift);
                    }
                    else
                    {
                        gg.drawString(headings[i], x0 + (w-sw)/2+shift, headingHeight-3+shift);
                    }

                    break;
                }

                case MultiList.RIGHT:
                {
                    if (sw > w)
                    {
                        gg.drawString(headings[i], x0+8+shift, headingHeight-3+shift);
                    }
                    else
                    {
                        gg.drawString(headings[i], x0+w-sw-6+shift, headingHeight-3+shift);
                    }

                    break;
                }
            }

            if (colClick == i)
            {
                gg.drawLine(r.x+1, headingHeight+2, xx-3,  headingHeight+2);
            }
        }

        //draw an outside border
        gg.setColor(Color.black);
//        gg.drawRect(0,0, s.width-1, s.height-1);
//        gg.drawRect(-1,0, s.width+1, s.height+1);        
        gg.drawLine(0,0,s.width,0);
        gg.drawLine(0,0,0,headingHeight);
        gg.setFont(f);
    }

    Frame frame()
    {
        Container c = this;

        while (c != null && !(c instanceof Frame))
        {
            c = c.getParent();
        }

        return (Frame)c;
    }

    void changeSelection(int newSelection, int meta)
    {
        if (multiSelect)
        {
            switch (meta)
            {
                case Event.CTRL_MASK:
                {
                    if (highlightedRows.get(newSelection))
                    {
                        highlightedRows.clear(newSelection);
                    }
                    else
                    {
                        highlightedRows.set(newSelection);
                    }
                    break;
                }
                case Event.SHIFT_MASK:
                {
                    for (int i=Math.min(selectedRow, newSelection);
                         i<=Math.max(selectedRow, newSelection);
                         i++)
                    {
                        if (i>=0)
                        {
                            highlightedRows.set(i);
                        }
                    }
                    break;
                }
                default:
                {
                    highlightedRows = new BitSet();
                    highlightedRows.set(newSelection);
                }
            }
        }
        else
        {
            if (selectedRow >= 0)
            {
                highlightedRows.clear(selectedRow);
            }
            highlightedRows.set(newSelection);
        }

        int rc = cells.rows();

        if (newSelection > rc-1)
        {
            newSelection = rc-1;
        }
        else if (newSelection < 0)
        {
            newSelection = 0;
        }

        // scroll a page UPWARD
        if (newSelection < topRow)
        {
            topRow = newSelection -1;

            if (topRow < 0)
            {
                topRow = 0;
            }

            sbPosition = topRow;
            sb.setValue(sbPosition);
        }

        // scroll a page DOWNWARD
        if (newSelection >=  -1 + topRow + getPageSize())
        {
            topRow = topRow + 1;

            if (topRow > rc-1)
            {
                topRow = newSelection;
            }

            sbPosition = topRow;
            sb.setValue(sbPosition);
        }


        if (selectedRow != newSelection)
        {
            if (meta > 0)
            {
                //generate a list deselection event
                postEvent(Event.LIST_DESELECT, selectedRow);
            }
        }

        //redraw tree
        selectedRow = newSelection;
        redraw();
        repaint();

        //generate a list selection event here!!!
        postEvent(Event.LIST_SELECT, newSelection);
    }

    public synchronized Dimension preferredSize()
    {
        return new Dimension(175, 125);
    }

    public synchronized Dimension minimumSize()
    {
        return new Dimension(50, 50);
    }
}



class  CompareCells implements CompareFunc
{
    public boolean lessThan(Object o1, Object o2)
    {
        if (!(o1 instanceof Cell && o2 instanceof Cell))
        {
            throw new IllegalArgumentException("Objects to compare must " +
                                 "be Cell instances");
        }

        Cell c1 = (Cell)o1;
        Cell c2 = (Cell)o2;

        return c1.text.compareTo(c2.text) < 0;
    }
}


class Cell implements java.awt.image.ImageObserver
{
    Image       im;
    MultiList   list;
    static String empty = "";
    String      text = empty;

    public Cell(MultiList l, String s)
    {
        list = l;
        text = s;
    }

    public Cell(MultiList l, String s, Image i)
    {
        list = l;
        text = s;
        im = i;
    }

    public Cell(MultiList l, Image i)
    {
        list = l;
        im = i;
    }

    public void drawCell(Graphics g, int align, int x, int y, int w, int h, int asc)
    {
        FontMetrics fm = g.getFontMetrics();
        int sw = fm.stringWidth(text);
        int offset = 0;

        switch(align)
        {
            case MultiList.LEFT:
            {
                if (im != null)
                {
                    g.drawImage(im, x, y, this);
                    offset = im.getWidth(this) + 2;
                }

                g.drawString(text, x + offset, y+asc);
                break;
            }

            case MultiList.CENTER:
            {
                if (sw > w)
                {
                    g.drawString(text, x, y+asc);
                }
                else
                {
                    g.drawString(text, x + (w-sw)/2, y+asc);
                }

                break;
            }

            case MultiList.RIGHT:
            {
                if (sw > w)
                {
                    g.drawString(text, x, y+asc);
                }
                else
                {
                    g.drawString(text, x+w-sw-6, y+asc);
                }

                break;
            }
        }
    }

    /**
     * Repaints the list when the cell's image has changed.
     * @return true if image has changed; false otherwise.
     */
    public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
    {
        if ((flags & (ALLBITS|ABORT)) != 0)
        {
            list.redraw();
            list.repaint();
            return false;
        }
        else
        {
            return true;
        }
    }

    public String toString()
    {
        return text;
    }
}

