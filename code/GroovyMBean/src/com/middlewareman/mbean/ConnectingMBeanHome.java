/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;

import javax.management.*;
import javax.management.remote.*;
import javax.security.auth.Subject;

/**
 * Serializable RemoteMBeanHome that maintains parameters to connect lazily,
 * monitors any open connection and reconnect on demand. This offers a resilient
 * and distributable solution when it is acceptable for credentials to persist.
 * 
 * @author Andreas Nyberg
 */
public class ConnectingMBeanHome extends RemoteMBeanHome implements
		Serializable, NotificationListener {

	private static final long serialVersionUID = 1L;

	private JMXServiceURL url;
	private Map<String, ?> env;
	private Subject subject;
	private transient JMXConnector connector;
	private transient MBeanServerConnection connection;

	/** For serialization only. */
	protected ConnectingMBeanHome() {
		logger.log(Level.INFO, "Default contructor for serialization");
	}

	public ConnectingMBeanHome(JMXServiceURL url, Map<String, ?> env,
			Subject subject) {
		this.url = url;
		this.env = env;
		this.subject = subject;
	}

	public Object getAddress() {
		return url;
	}

	protected JMXConnector getConnector() {
		return connector;
	}

	/**
	 * Returns a connector that is either existing and verified or newly
	 * created.
	 */
	protected JMXConnector createConnector() throws IOException {
		String id = "(connector not available)";
		if (connector != null) {
			try {
				id = connector.getConnectionId();
				logger.log(Level.FINER, "verified connector {0} -> return", id);
				return connector;
			} catch (IOException e) {
				logger.log(Level.INFO, "disqualifying connector", e);
				connector = null;
			}
		}
		if (connector == null) {
			logger.log(Level.FINER, "connector is null");
			connector = createConnector(url, env);
			id = connector.getConnectionId();
			logger.log(Level.FINE, "opened connector {0}", id);
			connector.addConnectionNotificationListener(this, null, connector);
		}
		return connector;
	}

	public synchronized MBeanServerConnection getConnection()
			throws IOException {
		if (connection != null) {
			logger.log(Level.FINER, "connection already open");
			return connection;
		}
		try {
			createConnector();
			String id = connector.getConnectionId();
			connection = createConnection(connector, subject);
			logger.log(Level.FINE, "opened connection on {0}", id);
			return connection;
		} catch (IOException e) {
			reset();
			createConnector();
			String id = connector.getConnectionId();
			connection = createConnection(connector, subject);
			logger.log(Level.FINE, "reopened connection on {0}", id);
			return connection;
		}
	}

	public synchronized void handleNotification(Notification notification,
			Object handback) {
		if (notification instanceof JMXConnectionNotification) {
			JMXConnectionNotification jmxn = (JMXConnectionNotification) notification;
			String type = jmxn.getType();
			logger.log(Level.INFO, "type {0}", type);
			if (JMXConnectionNotification.CLOSED.equals(type)
					|| JMXConnectionNotification.FAILED.equals(type)) {
				if (handback != null && handback instanceof JMXConnector
						&& ((JMXConnector) handback).equals(getConnector()))
					reset();
			}
		}
	}

	public synchronized void close() throws IOException {
		daemonClose();
		reset();
	}

	private synchronized void reset() {
		logger.log(Level.FINE, "reset");
		connector = null;
		connection = null;
	}
}
