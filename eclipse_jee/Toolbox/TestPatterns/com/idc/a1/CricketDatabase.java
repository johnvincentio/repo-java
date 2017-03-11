package com.idc.a1;

import java.util.Calendar;
import java.util.Date;

public class CricketDatabase implements SportDatabase {
	private final static Player[] p = {
			new Player("12341", "Boeta Dippenaar", makeDate (77, 5, 14)),
			new Player("23432", "Gary Kirsten", makeDate (67, 10, 23)),
			new Player("23411", "Graeme Smith", makeDate (81, 1, 1)),
			new Player("55221", "Jonty Rhodes", makeDate (69, 6, 27)),
			new Player("61234", "Monde Zondeki", makeDate (82, 6, 25)),
			new Player("23415", "Paul Adams", makeDate (77, 0, 20)), };

	public Player[] getPlayers() {return p;}
	private static Date makeDate (int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set (year, month, day);
		long millis = calendar.getTimeInMillis();
		return new Date (millis);
	}
}
