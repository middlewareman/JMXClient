/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

/**
 * Serializable RemoteMBeanHome that maintains parameters to connect lazily,
 * monitors any open connection and reconnect on demand.
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

	public ConnectingMBeanHome(JMXServiceURL url, Map<String, ?> env,
			Subject subject) {
		this.url = url;
		this.env = env;
		this.subject = subject;
	}

	public Object getServerId() {
		return url;
	}

	public JMXConnector getConnector() {
		return connector;
	}

	public synchronized MBeanServerConnection getMBeanServerConnection()
			throws IOException {
		if (connection != null)
			return connection;
		if (connector == null) {
			connector = JMXConnectorFactory.connect(url, env);
			connector.addConnectionNotificationListener(this, null, null);
		}
		return connector.getMBeanServerConnection(subject);
	}

	public void handleNotification(Notification notification, Object handback) {
		if (notification instanceof JMXConnectionNotification) {
			JMXConnectionNotification jmxn = (JMXConnectionNotification) notification;
			String type = jmxn.getType();
			if (JMXConnectionNotification.CLOSED.equals(type)
					|| JMXConnectionNotification.FAILED.equals(type))
				reset();
		}
	}

	private synchronized void reset() {
		connector = null;
		connection = null;
	}
}
