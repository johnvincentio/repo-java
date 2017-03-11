
package com.idc.xml;

import java.io.Writer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;

import org.w3c.dom.Document;

import com.idc.trace.LogHelper;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;

public class XMLTransform {
	private TransformerFactory m_tFactory;
	public XMLTransform() {
		m_tFactory = TransformerFactory.newInstance();
	}

	public void doTransform (InputStream xslInput, InputStream xmlInput, Writer writer) {
		LogHelper.info(">>> doTransform (XML file)");
		try {
			Transformer transformer = m_tFactory.newTransformer(new StreamSource(xslInput));
			transformer.transform(new StreamSource(xmlInput), new StreamResult(writer));
		}
		catch (Exception e) {
			LogHelper.error(e.toString());
			e.printStackTrace();
		}
		LogHelper.info("<<< doTransform (XML file");     
	}
	public void doTransform (InputStream xslInput, InputStream xmlInput, OutputStream output) {
		LogHelper.info(">>> doTransform (XML file to OutputStream) ");
		try {
			Transformer transformer = m_tFactory.newTransformer(new StreamSource(xslInput));
			transformer.transform(new StreamSource(xmlInput), new StreamResult(output));
		}
		catch (Exception e) {
			LogHelper.error(e.toString());
			e.printStackTrace();
		}
		LogHelper.info("<<< doTransform (XML file to OutputStream)");     
	}
	public void doTransform (InputStream xslInput, StringBuffer buf, Writer writer) {
		LogHelper.info(">>> doTransform (StringBuffer)");
		try {
			Transformer transformer = m_tFactory.newTransformer(new StreamSource(xslInput));
			ByteArrayInputStream bais = new ByteArrayInputStream(buf.toString().getBytes());
			transformer.transform(new StreamSource(bais), new StreamResult(writer));
		}
		catch (Exception e) {
			LogHelper.error(e.toString());
			e.printStackTrace();
		}
		LogHelper.info("<<< doTransform (StringBuffer)");     
	}
	public void doTransform (InputStream xslInput, Document doc, Writer writer) {
		LogHelper.info(">>> doTransform(Document)");
		try {
			Transformer transformer = m_tFactory.newTransformer(new StreamSource(xslInput));
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
		}
		catch (Exception e) {
			LogHelper.error(e.toString());
			e.printStackTrace();
		}
		LogHelper.info("<<< doTransform(Document)");     
	}
}
