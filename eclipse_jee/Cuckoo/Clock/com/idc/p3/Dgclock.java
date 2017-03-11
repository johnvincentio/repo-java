package com.idc.p3;

import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;
import java.util.*;

//////////////////////////////////////////////////////////
// Description: a simple digital clock in Java (1.0.2 applet)
// Author: Muhammad A Muquit
// email:  ma_muquit@fccc.edu
// URL:    http://www.fccc.edu/users/muquit/Dgclock.html
//
// 12/05/1995           lets play with Java
// 01/29/96             some clean up
// 02/13/96             added ornamental frame
//                      added 24 hr time format (TimeFormat)
// 02/15/96             GMT+-hhmm to display time of any place (TZ)
//                      date (ShowDate)
// 03/13/96             using MediaTrcaker to stop incremental display
//                      Suggested by Jim Graham
// 03/16/96             display clock without frame.
//                      shows version info while mouse is moved on the appl.
//                      stop clock by clicking mouse
// 10/29/1996           jdk 1.1 on DEC OSF1 found a syntax error
// 11/02/1996           Version 2.2
// 11/02/1996           uses an image strip instead of individual digit images
//                      loads much faster.
// 11/05/1996           Version 2.3
//                      change fg & bg color on the fly
//
// Copyright:
// This program is in the public domain. Do anything you like with it.
// It would be nice if you give me credit for it tho.
//////////////////////////////////////////////////////////

/*

 Example of calling convention:

 The default 24 hr format:

 <applet code="Dgclock.class" width=100 height=30></applet>

 To display time in 12 hr format:

 <applet code="Dgclock.class" width=100 height=30>
 <param name="TimeFormat" value="%I">
 </applet>

 To display NewYork time:

 <applet code="Dgclock.class" width=100 height=30>
 <param name="TZ" value="GMT-0500">
 </applet>

 12 or 24 hr format
 <param name="TimeFormat" value="%H">    (24 hr format - default)
 <param name="TimeFormat" value="%I">    (12 hr format)

 the format for value is GMT+-hhmm, for example

 <param name="TZ" value="GMT+0600">    (6 hours west of Greenwich meridian)
 <param name="TZ" value="GMT+0430">    (4 hours, 30 minutes
 west of Greenwich meridian)
 <param name="TZ" value="GMT+0000">    (Greenwich)
 <param name="TZ" value="GMT-0500">    (5 hours east of Greenwich meridian)

 <param name="ShowDate" value="yes">   (show date with time)
 <param name="ShowDate" value="no">   (don't show date - default)

 <applet code="Dgclock.class" width=89 height=20>
 <param name="ShowFrame" value="no">   (do not wrap clock with frame)

 <param name="ShowVersion" value="no"> (do not show version info when the
 mouse cursor is moved on the applet)

 <param name="fg" value="color">       (change foreground color, color can be
 any of red,green,blue,cyan,magenta,
 yellow,orange,pink,black,white or 
 a 6 digit hex number like ff0000)

 <param name="bg" value="color">       (change background color)

 */

public class Dgclock extends Applet implements Runnable {
	private static final long serialVersionUID = 1;
	MediaTracker tracker;

	Thread timer = null;

	boolean suspended = false;

	Image big_image, buf_image;

	int bdigit_width = 15;

	int bdigit_height = 20;

	int sdigit_width = 7;

	int sdigit_height = 9;

	int base_width = 100;

	int base_height = 30;

	int offset = 4;

	String TimeFormat = "%H";

	long TimeDiff = 0L;

	boolean ShowDate = false;

	boolean ShowStatus = true;

	boolean use_frame = true;

	Color bg_color = Color.black;

	/**
	 * Cache of colors indexed by name or color-spec.
	 */
	static Hashtable colorCache = new Hashtable();

	/**
	 * Initialize the color hash table.
	 */
	static {
		colorCache.put("red", Color.red);
		colorCache.put("green", Color.green);
		colorCache.put("blue", Color.blue);
		colorCache.put("cyan", Color.cyan);
		colorCache.put("magenta", Color.magenta);
		colorCache.put("yellow", Color.yellow);
		colorCache.put("orange", Color.orange);
		colorCache.put("pink", Color.pink);
		colorCache.put("white", Color.white);
		colorCache.put("black", Color.black);
	}

	int checkTZ(String TZ) {
		int d;

		int hr, min;

		int counter = 0;

		int diff = 0;

		hr = 0;
		min = 0;
		if (TZ != null) {
			int length = TZ.length();
			if (length == 8) {
				// Print("Length= " + length);
				int c = TZ.charAt(3);
				if ((c == '-') || (c == '+')) {
					String tzval = TZ.substring(4, 8);
					// Print("TZ value= " + tzval);

					for (int i = 0; i < tzval.length(); i++) {
						d = tzval.charAt(i);

						if ((d >= '0') && d <= '9') {
							if (counter < 2)
								hr = hr * 10 + (d - '0');
							else
								min = min * 10 + (d - '0');
							counter++;
						}
					}
					// Print("Hour: " + hr + " Min: " + min);
					diff = (hr * 60) + min;
					if (c == '-')
						diff = (-diff);
				}
			}
		}

		return diff;
	}

	public void init() {
		int x, y;

		Image rgb_image = null;

		String fg, bg;

		fg = bg = null;
		// check if TimeFormat is supplied
		TimeFormat = getParameter("TimeFormat");
		if (TimeFormat != null) {
			if ((!TimeFormat.equals("%I")) && (!TimeFormat.equals("%H")))
				TimeFormat = "%H";
		} else
			TimeFormat = "%H";

		String gp = getParameter("ShowDate");
		if (gp != null) {
			if (gp.equals("yes"))
				ShowDate = true;
		}

		gp = getParameter("ShowVersion");
		if (gp != null) {
			if (gp.equals("no"))
				ShowStatus = false;
		}

		// check if frame need to be displayed
		gp = getParameter("ShowFrame");
		if (gp != null) {
			if (gp.equals("no")) {
				use_frame = false;
				/*
				 * offset=0; base_width=89; base_height=20;
				 */
			}
		}

		// check if fg and bg parameters are supplied
		fg = getParameter("fg");
		bg = getParameter("bg");

		// Print("TImeForamt= " + TimeFormat);

		tracker = new MediaTracker(this);
		try {
			// load the big image
			if ((fg == null) && (bg == null)) {
				// Print("fg and bg null");
				big_image = getImage(getCodeBase(), "./dgstrip.gif");
				tracker.addImage(big_image, 0);
			} else {
				rgb_image = getImage(getCodeBase(), "./dgstrip.gif");
				tracker.addImage(rgb_image, 0);
			}
		} catch (Exception e) {
			;
		}

		if ((fg != null) || (bg != null)) {
			// Print ("fg or bg is not null");
			Color fgc, bgc;

			if (fg != null)
				fgc = lookupColor(true, fg);
			else
				fgc = Color.cyan;

			if (bg != null)
				bgc = lookupColor(false, bg);
			else
				bgc = Color.black;

			bg_color = bgc;
			ImageFilter f = new ColorFilter(fgc, bgc);
			ImageProducer producer = new FilteredImageSource(rgb_image
					.getSource(), f);
			big_image = this.createImage(producer);
		}

		// check for TZ
		int diff = 0;
		String timezone = getParameter("TZ");
		if (timezone != null) {
			if (timezone.equals("LOCAL"))
				TimeDiff = 0L;
			else {
				Date date = new Date();
				diff = checkTZ(timezone);
				TimeDiff = ((long) date.getTimezoneOffset()) * 60000 + diff
						* 60000;
			}

		}

		try {
			buf_image = createImage(base_width, base_height);
		} catch (Exception e) {
			;
		} // jdk 1.1 on osf found a problem
	}

	/**
	 * taken from AlphaBullet applet by Jim Graham
	 */
	private synchronized Color lookupColor(boolean bb, String colorname) {
		/**
		 * boolean bb is true when looking color for foreground we need this
		 * flag, so if color lookup fails, we can choose contrasting colors for
		 * fg and bg
		 */

		String lowername = colorname.toLowerCase();
		Color newcolor = (Color) colorCache.get(lowername);
		if (newcolor != null)
			return newcolor;

		int colorval;

		try {
			colorval = Integer.parseInt(lowername, 16);
		} catch (NumberFormatException e) {
			if (bb == true)
				return (Color.cyan);
			else
				return (Color.black);
		}

		int r = (colorval >> 16) & 0xff;
		int g = (colorval >> 8) & 0xff;
		int b = (colorval & 0xff);

		newcolor = new Color(r, g, b);
		colorCache.put(lowername, newcolor);

		return (newcolor);
	}

	public void start() {
		if (timer == null) {
			timer = new Thread(this, "Dgclock");
			timer.start();
		}
	}

	public void run() {

		while (timer != null) {
			try {
				timer.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
			repaint();
		}
		timer = null;
	}

	public void paintApplet() {
		int i, j, k, x;

		Date now = new Date();

		int hour, minute, second;

		String hr, min, sec, today;

		Graphics gc;

		if (TimeDiff != 0L) {
			now.setTime(now.getTime() + TimeDiff);
		}
		hr = formatDate(TimeFormat, now);
		min = formatDate("%M", now);
		sec = formatDate("%S", now);

		if (ShowDate == true) {
			today = formatDate("%d", now);
		} else
			today = "";

		if (tracker.statusID(0, true) == MediaTracker.COMPLETE) {

			// if (offset == 4)
			if (use_frame == true) {
				gc = buf_image.getGraphics();
				// draw the frame
				gc.clipRect(0, 0, base_width, base_height);
				gc.drawImage(big_image, 0, 0, this);
			}

			// draw the hour digit images
			for (i = 0; i < hr.length(); i++) {
				gc = buf_image.getGraphics();
				x = offset + (i * bdigit_width);
				gc.clipRect(x, offset, 15, 20);
				x = x - 100 - (hr.charAt(i) - '0') * bdigit_width;
				gc.drawImage(big_image, x, offset, null);
			}

			// draw the colon image
			gc = buf_image.getGraphics();
			x = offset + i * bdigit_width;
			gc.clipRect(x, offset, bdigit_width, bdigit_height);
			x = x - 250;
			gc.drawImage(big_image, x, offset, null);
			i++;

			// draw the minute images
			for (j = 0; j < min.length(); j++) {
				gc = buf_image.getGraphics();
				x = offset + (i * bdigit_width);
				gc.clipRect(x, offset, 15, 20);
				x = x - 100 - (min.charAt(j) - '0') * bdigit_width;
				gc.drawImage(big_image, x, offset, null);
				i++;
			}

			// draw the second images
			for (j = 0; j < sec.length(); j++) {
				gc = buf_image.getGraphics();
				x = offset + (i * bdigit_width) + (j * sdigit_width);
				gc.clipRect(x, offset, sdigit_width, sdigit_height);
				x = x - 100 - (sec.charAt(j) - '0') * sdigit_width;
				gc.drawImage(big_image, x, offset - bdigit_height, null);
			}

			if (ShowDate == true) {
				for (k = 0; k < today.length(); k++) {
					gc = buf_image.getGraphics();
					x = offset + (i * bdigit_width) + (k * sdigit_width);
					gc.clipRect(x, offset + sdigit_height + 1, sdigit_width,
							sdigit_height);
					x = x - 100 - (today.charAt(k) - '0') * sdigit_width;
					gc.drawImage(big_image, x, offset + sdigit_height
							- bdigit_height, null);
				}
			}
		}
	}

	public void update(Graphics g) {
		if (tracker.statusID(0, true) == MediaTracker.COMPLETE) {
			if (buf_image != null) {
				if (use_frame == false) {
					Graphics gc = buf_image.getGraphics();
					gc.setColor(bg_color);
					gc.fillRect(0, 0, base_width, base_height);
				}
				paintApplet();
				g.drawImage(buf_image, 0, 0, this);
			} else {
				g.clearRect(0, 0, base_width, base_height);
			}
		} else {
			g.drawString("Loading..", 10, 15);
		}
	}

	// Left Pad a number
	// taken from Clock2 by Per Reedtz Thomsen/Rachel Gollub
	private String padElement(int expr, char padChar) {
		String result = "";
		// I'm just padding 2 digit numbers
		if (expr < 10)
			result = result.concat(String.valueOf(padChar));
		result = result.concat(String.valueOf(expr));
		return (result);
	}

	// Format a date according to the formatting string.
	// taken from Clock2 by Per Reedtz Thomsen/Rachel Gollub
	private String formatDate(String fmt, Date d) {
		String formattedDate = "";

		// Retrieve the specific date information
		int hour = d.getHours();
		int minute = d.getMinutes();
		int second = d.getSeconds();
		int monthDay = d.getDate();

		int US_Hour = hour < 13 ? hour : hour - 12;

		// Loop through the format string
		for (int i = 0; i < fmt.length(); i++) {
			if (fmt.charAt(i) == '%') // We've hit a formatting command...
			{
				i++; // Move past the '%' sign
				// Figure out the format.
				switch (fmt.charAt(i)) {
				case 'H': // Hour -- 00 to 23
					formattedDate = formattedDate.concat(padElement(hour, '0'));
					break;
				case 'I': // Hour -- 01 to 12
					formattedDate = formattedDate.concat(padElement(US_Hour,
							'0'));
					// formattedDate=formattedDate.concat(String.valueOf(US_Hour));
					break;
				case 'M': // Minutes -- 00 to 59
					formattedDate = formattedDate
							.concat(padElement(minute, '0'));
					break;
				case 'd': // 2 digit month number
					formattedDate = formattedDate.concat(padElement(monthDay,
							'0'));
					break;
				case 'S': // Second -- 00 to 61 (leap seconds)
					formattedDate = formattedDate
							.concat(padElement(second, '0'));
					break;
				default:
					formattedDate = formattedDate.concat("??");
					break;
				}
			} else // A regular character
			{
				formattedDate = formattedDate.concat(String.valueOf(fmt
						.charAt(i)));
			}
		} // end for

		return (formattedDate);
	}

	public void stop() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
	}

	public boolean handleEvent(Event evt) {
		switch (evt.id) {
		case Event.MOUSE_DOWN: {
			if (suspended) {
				timer.resume();
			} else {
				// Print ("Suspending..");
				timer.suspend();
			}
			suspended = !suspended;
			break;
		}

		case Event.MOUSE_MOVE: {
			if (tracker.statusID(0, true) != MediaTracker.COMPLETE) {
				showStatus("Loading image...please wait!");
			} else {
				if (suspended == false) {
					if (ShowStatus == true)
						showStatus("Dgclock 2.3 by ma_muquit@fccc.edu");
					else {
						String tz = getParameter("TZ");
						if (tz != null)
							showStatus(tz);
						else
							showStatus("your time");
					}
				}
			}
			break;
		}
		}
		return true;
	}

	/*
	 * public void Print(String str) { System.out.println(str); }
	 */

}

// change a color of the image
class ColorFilter extends RGBImageFilter {
	Color fgc, bgc;

	public ColorFilter(Color fg, Color bg) {
		fgc = fg;
		bgc = bg;
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {

		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb >> 0) & 0xff;

		int fg_r = fgc.getRed();
		int fg_g = fgc.getGreen();
		int fg_b = fgc.getBlue();

		int bg_r = bgc.getRed();
		int bg_g = bgc.getGreen();
		int bg_b = bgc.getBlue();

		// System.out.println("bg r,g,b=" + bg_r + "," + bg_g + "," + bg_b);

		// change foreground
		if ((r == 0) && (g == 255) && (b == 255)) {
			r = fg_r;
			g = fg_g;
			b = fg_b;
			if ((r == 0) && (g == 0) && (b == 0)) {
				r = g = b = 1;
			}
			// System.out.println("fg r,g,b=" + r + "," + g + "," + b);
		}

		// change background
		if ((r == 0) && (g == 0) && (b == 0)) {
			r = bg_r;
			g = bg_g;
			b = bg_b;
		}

		return (rgb & 0xff000000) | (r << 16) | (g << 8) | (b << 0);
	}

}
