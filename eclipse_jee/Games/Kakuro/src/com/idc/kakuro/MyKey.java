package com.idc.kakuro;

public class MyKey {
	private int squares;
	private int total;
	public MyKey (int squares, int total) {
		this.squares = squares;
		this.total = total;
	}

	public int getSquares() {
		return squares;
	}
	public int getTotal() {
		return total;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + squares;
		result = prime * result + total;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyKey other = (MyKey) obj;
		if (squares != other.squares)
			return false;
		if (total != other.total)
			return false;
		return true;
	}
}
