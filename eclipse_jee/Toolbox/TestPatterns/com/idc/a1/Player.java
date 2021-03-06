package com.idc.a1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Player {
	private final Key key;

	private final String name;

	private final Date dateOfBirth;

	public Player(String id, String name, Date dob) {
		this.key = new Key(id);
		this.name = name;
		this.dateOfBirth = dob;
	}

	public Key getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	private final static DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

	public String toString() {
		return name + " born on " + df.format(dateOfBirth);
	}

	public static final class Key {
		private final String id;

		public Key(String id) {
			if (id == null) {
				throw new IllegalArgumentException();
			}
			this.id = id;
		}

		public int hashCode() {
			return id.hashCode();
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof Key))
				return false;
			return id.equals(((Key) obj).id);
		}
	}
}
