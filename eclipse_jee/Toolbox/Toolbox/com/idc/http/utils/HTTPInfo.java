
package com.idc.http.utils;

import java.io.Serializable;
import javax.servlet.http.HttpSession;

public class HTTPInfo implements Serializable {
	private static final long serialVersionUID = 1;
	protected transient HttpSession session;
	
	protected HTTPInfo (HttpSession session) {this.session = session;}
	public HTTPInfo() {}
}
