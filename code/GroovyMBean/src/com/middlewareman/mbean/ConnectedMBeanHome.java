/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

/**
 * RemoteMBeanHome that has an open connection without the ability to reconnect.
 * 
 * @author Andreas Nyberg
 */
public class ConnectedMBeanHome extends RemoteMBeanHome implements
		NotificationListener {

	private final Object server;
	private final JMXConnector connector;
	private final MBeanServerConnection connection;

	public ConnectedMBeanHome(JMXServiceURL url, Map<String, ?> env,
			Subject subject) throws IOException {
		this.server = url;
		connector = createConnector(url, env);
		connector.addConnectionNotificationListener(this, null, connector);
		connection = createConnection(connector, subject);
	}

	public ConnectedMBeanHome(Object server, JMXConnector connector,
			MBeanServerConnection connection) {
		this.server = server;
		this.connector = connector;
		connector.addConnectionNotificationListener(this, null, connector);
		this.connection = connection;
	}

	public Object getServerId() {
		return server;
	}

	protected JMXConnector getConnector() {
		return connector;
	}

	protected JMXConnector createConnector() throws IOException {
		assert connector != null;
		String id = connector.getConnectionId();
		logger.log(Level.FINER, "verified connector {0} -> return", id);
		return connector;
	}

	public MBeanServerConnection getMBeanServerConnection() {
		return connection;
	}

	public synchronized void handleNotification(Notification notification,
			Object handback) {
		if (notification instanceof JMXConnectionNotification) {
			JMXConnectionNotification jmxn = (JMXConnectionNotification) notification;
			String type = jmxn.getType();
			if (JMXConnectionNotification.CLOSED.equals(type)
					|| JMXConnectionNotification.FAILED.equals(type)) {
				if (handback != null && handback instanceof JMXConnector
						&& ((JMXConnector) handback).equals(getConnector()))
					logger.log(Level.WARNING, type);
			}
		}
	}

}
