package com.idc.http.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.net.URL;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.jms.QueueConnectionFactory;
import javax.jms.Queue;
import javax.jms.TopicConnectionFactory;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

//import java.rmi.Remote.*;	// web services
//import javax.xml.rpc.*;	// web services

public class JVServiceLocator {
	private InitialContext m_ic;
	private Map<String, Object> m_cache;
//	private Map m_cache;
	private static JVServiceLocator m_serviceLocator;
  
	static {
		try {
			m_serviceLocator = new JVServiceLocator();
		}
		catch (JVServiceLocatorException ex) {
			System.err.println(ex);
			ex.printStackTrace(System.err);
		}
	}
	static public JVServiceLocator getInstance() {return m_serviceLocator;}	
	private JVServiceLocator() throws JVServiceLocatorException {
		try {
			m_ic = new InitialContext();
			m_cache = Collections.synchronizedMap(new HashMap<String, Object>());
		} catch (NamingException ne) {
			throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
	}
//
// java:comp/env/ejb/TheConverter
//
	public EJBLocalHome getLocalHome(String jndiName) throws JVServiceLocatorException {
		EJBLocalHome home = null;
		try {
			if (m_cache.containsKey(jndiName)) {
				home = (EJBLocalHome) m_cache.get(jndiName);
			}
			else {
				home = (EJBLocalHome) m_ic.lookup(jndiName);
				m_cache.put(jndiName,home);
			}
		}
		catch(NamingException ex) {
			throw new JVServiceLocatorException(ex);
		}
		catch(Exception ex) {
			throw new JVServiceLocatorException(ex);
		}
		return home;
	}

	public EJBHome getRemoteHome(String jndiName, Class<?> className) throws JVServiceLocatorException {
		EJBHome home = null;
		try {
			if (m_cache.containsKey(jndiName)) {
				home = (EJBHome) m_cache.get(jndiName);
			}
			else {
				Object objref = m_ic.lookup(jndiName);
				Object obj = PortableRemoteObject.narrow(objref, className);
				home = (EJBHome) obj;
				m_cache.put(jndiName,home);
			}
		}
		catch(NamingException ex) {
			throw new JVServiceLocatorException(ex);
		}
		catch(Exception ex) {
			throw new JVServiceLocatorException(ex);
		}
		return home;
	}

	public QueueConnectionFactory getQueueConnectionFactory(String qConnFactoryName)
				throws JVServiceLocatorException {
		QueueConnectionFactory factory = null;
		try {
			if (m_cache.containsKey(qConnFactoryName)) {
				factory = (QueueConnectionFactory) m_cache.get(qConnFactoryName);
			} else {
				factory = (QueueConnectionFactory) m_ic.lookup(qConnFactoryName);
				m_cache.put(qConnFactoryName, factory);
			}
		} catch (NamingException ne) {
			throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
		return factory;
	}

	public Queue getQueue(String queueName) throws JVServiceLocatorException {
		Queue queue = null;
		try {
			if (m_cache.containsKey(queueName)) {
				queue = (Queue) m_cache.get(queueName);
			} else {
				queue =(Queue)m_ic.lookup(queueName);
				m_cache.put(queueName, queue);
			}
		} catch (NamingException ne) {
				throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
		return queue;
	}

	public TopicConnectionFactory getTopicConnectionFactory(String topicConnFactoryName)
					throws JVServiceLocatorException {
		TopicConnectionFactory factory = null;
		try {
			if (m_cache.containsKey(topicConnFactoryName)) {
				factory = (TopicConnectionFactory) m_cache.get(topicConnFactoryName);
			} else {
				factory = (TopicConnectionFactory) m_ic.lookup(topicConnFactoryName);
				m_cache.put(topicConnFactoryName, factory);
			}
		} catch (NamingException ne) {
			throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
		return factory;
	}

	public Topic getTopic(String topicName) throws JVServiceLocatorException {
		Topic topic = null;
		try {
			if (m_cache.containsKey(topicName)) {
				topic = (Topic) m_cache.get(topicName);
			} else {
				topic = (Topic)m_ic.lookup(topicName);
				m_cache.put(topicName, topic);
			}
		} catch (NamingException ne) {
			throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
		return topic;
	}
//
// java:comp/env/jdbc/JVServletDB
//
	public DataSource getDataSource(String dataSourceName) throws JVServiceLocatorException {
		DataSource dataSource = null;
		try {
			if (m_cache.containsKey(dataSourceName)) {
				dataSource = (DataSource) m_cache.get(dataSourceName);
			} else {
				dataSource = (DataSource)m_ic.lookup(dataSourceName);
				m_cache.put(dataSourceName, dataSource );
			}
		} catch (NamingException ne) {
			throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
		return dataSource;
	}

	public URL getUrl(String envName) throws JVServiceLocatorException {
		URL url = null;
		try {
			url = (URL)m_ic.lookup(envName);
		} catch (NamingException ne) {
			throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
		return url;
	}

	public boolean getBoolean(String envName) throws JVServiceLocatorException {
		Boolean bool = null;
		try {
			bool = (Boolean)m_ic.lookup(envName);
		} catch (NamingException ne) {
			throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
		return bool.booleanValue();
	}
//
// java:comp/env/database_user
//
	public String getString(String envName) throws JVServiceLocatorException {
		String envEntry = null;
		try {
			envEntry = (String)m_ic.lookup(envName);
		} catch (NamingException ne) {
			throw new JVServiceLocatorException(ne);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
		return envEntry ;
	}
}
/*
	public Remote getServicePort(String jndiHomeName, Class className)
					throws JVServiceLocatorException {
		try {
			Service service = (Service) m_ic.lookup(jndiHomeName);
			return service.getPort(className);
		} catch (Exception e) {
			throw new JVServiceLocatorException(e);
		}
	} 
*/
	
