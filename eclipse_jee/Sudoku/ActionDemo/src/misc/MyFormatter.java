package misc;

import java.text.ParseException;
import javax.swing.text.DefaultFormatter;

public class MyFormatter extends DefaultFormatter {

	public MyFormatter() {
		super();
	}

	// This method reads the text from model class to view class
	public String valueToString(Object obj) throws ParseException {
		System.out.println(">>> MyFormatter::valueToString");
		if (obj == null) {
			System.out.println("object is null");
			return "";
		}
		try {
			Number number = (Number) obj;
			System.out.println("number "+number.longValue());
			String str = super.valueToString(number);
			System.out.println("<<< MyFormatter::valueToString; string :"+str+":");
			return str;
		}
		catch (NumberFormatException e) {
			return "";
		}
	}

	// This method reads the text from view class to modal class and does the validations
	public Object stringToValue(String s) throws ParseException {
		System.out.println(">>> MyFormatter::stringToValue; string :"+s+":");
		if (s == null) {
			System.out.println("string is null");
			return "";
		}
		if (s.length() < 1) {
			System.out.println("string is empty");
			return "";
		}
		try {
			int value = Integer.parseInt(s);
			System.out.println("value "+value);
			if (value > 9) throw new ParseException(s, 0);
			System.out.println("<<< MyFormatter::stringToValue; value :"+value+":");
			return value;
		}
		catch (Exception e) {
			System.out.println("Exception; ex "+e.getMessage());
			throw new ParseException(s, 0);
		}
	}
}
