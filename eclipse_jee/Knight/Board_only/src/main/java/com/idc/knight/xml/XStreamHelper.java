package com.idc.knight.xml;

import com.idc.knight.Board;
import com.idc.knight.Pair;
import com.idc.knight.Square;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamHelper {
	public static void main(String[] args) {
		Scenario scenario = new Scenario (new Pair (7, 3), new Pair(0, 0));
		classToXml (scenario);
	}

	public static String classToXml (Scenario scenario) {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("scenario", Scenario.class);
		String xml = xstream.toXML(scenario);
//		System.out.println(xml);
		return xml;
	}

	public static String classToXml (Solution solution) {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("solution", Solution.class);
		xstream.alias("board", Board.class);
		xstream.alias("square", Square.class);

		xstream.omitField (Board.class, "m_count_countExits");
		xstream.omitField (Board.class, "m_cntr_countExits");
		xstream.omitField (Board.class, "m_cntr_isConnected");

		xstream.omitField (Board.class, "m_one_exit_count_isPossible");
		xstream.omitField (Board.class, "m_count_exits_isPossible");
		xstream.omitField (Board.class, "m_x_isPossible");
		xstream.omitField (Board.class, "m_y_isPossible");
		xstream.omitField (Board.class, "m_cntr_getNextValidMove");
		xstream.omitField (Board.class, "m_toSquare_moveBackward");
		xstream.omitField (Board.class, "m_fromSquare_moveForward");
		xstream.omitField (Board.class, "m_toSquare_moveForward");
		xstream.omitField (Board.class, "m_moveCounter_moveForward");

		String xml = xstream.toXML(solution);
//		System.out.println(xml);
		return xml;
	}

	public static Scenario xmlToScenario (String xml) {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("scenario", Scenario.class);
		return (Scenario) xstream.fromXML(xml);
	}

	public static Solution xmlToSolution (String xml) {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("solution", Solution.class);
		xstream.alias("board", Board.class);
		xstream.alias("square", Square.class);
		return (Solution) xstream.fromXML(xml);
	}
}

/*
		Person joe = new Person("Joe", "Walnes");
		joe.setPhone(new PhoneNumber(123, "1234-456"));
		joe.setFax(new PhoneNumber(123, "9999-999"));
		classToXml (joe);

	public static void classToXml (Person person) throws Exception {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("person", Person.class);
		xstream.alias("phonenumber", PhoneNumber.class);
		String xml = xstream.toXML(person);
		System.out.println(xml);

		Person p1 = (Person) xstream.fromXML(xml);
		System.out.println("p1 "+p1);
	}
*/
