package com.idc.queen;

import java.io.*;

public class queen
{
	private static int solutions_found = 0;
	private static long positions_tested = 0;
	private static boolean bOutFile = false;
	public static void main (String[] args)
	{
		System.out.println ("before test");
		String strFile;
		if (args.length > 0)
		{
			strFile = args[0];
			try
			{
				System.setOut (new PrintStream 
					(new java.io.FileOutputStream (strFile)));
			}
			catch (java.io.IOException ioe) {System.err.println (ioe);}
			bOutFile = true;
		}
		Add ();
   		System.out.println ("Positions tested " + positions_tested);
	}
	private static void Add ()
	{
		int row, c;
		boolean[] column_free = new boolean[8];
		boolean[] up_free = new boolean[15];
		boolean[] down_free = new boolean[15];
		int[] col = new int[8];

		for (c=0; c<8; c++)
		{
			column_free[c] = true;
			col[c] = 0;
		}
		for (c=0; c<15; c++)
			up_free[c] = down_free[c] = true;

		for (row = 0;;)
		{
			while (col[row]<8)
			{
				c = col[row];
				if (column_free[c] && up_free[row+c] && down_free[row-c+7])
				{
					positions_tested += 1;
					column_free[c] = false;
					up_free[row+c] = false;
					down_free[row-c+7] = false;
					if (row == 7)             /* terminating condition */
					{
						PrintBoard (col);
						column_free[c] = true;
						up_free[row+c] = true;
						down_free[row-c+7] = true;
						break;
					}
					else
					{
						row++;
						col[row] = 0;
						continue;
					}
				}
				col[row]++;
			}
			col[row] = 0;
			row--;
		
			c = col[row];
			column_free[c] = true;
			up_free[row+c] = true;
			down_free[row-c+7] = true;
		
			col[row]++;
		
			if (row == 0 && col[0] == 8)
				break;
		}
	}
	private static void PrintBoard (int[] column)
	{
		char [][] board = new char[8][8];
		int i, j, c;

		solutions_found += 1;
		System.out.print   ("Solution number " + solutions_found);
		System.out.println ("   Positions tested " + positions_tested);
		for (i=0; i<8; i++) {for (j=0; j<8; j++) board[i][j] = '.';}
		for (i=0; i<8; i++) {board[7-column [i]][i] = 'Q';}
		for (i=0; i<8; i++)
		{
			for (j=0; j<8; j++) System.out.print (board[i][j]);
			System.out.println ("");
		}
		
		System.out.println ("");
		for (i=0; i<8; i++)
			System.out.print ("(" + (i+1) + "," + (column[i]+1) + ")  ");
		System.out.println ("");
		
		if (! bOutFile)
		{
			try
			{
				while ((c=System.in.read()) != -1)
				{
					char ch = (char) c;
					if (ch == '\n') break;
				}
			}
			catch (java.io.IOException ioe) {System.err.println (ioe);}
		}
	}
}

