/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

/**
 * RemoteMBeanHome that has an open MBeanServerConnection without the ability to
 * reconnect.
 * 
 * @author Andreas Nyberg
 */
public class ConnectedMBeanHome extends RemoteMBeanHome {

	private final Object server;
	private final JMXConnector connector;
	private final MBeanServerConnection connection;

	public ConnectedMBeanHome(JMXServiceURL url, Map<String, ?> env,
			Subject subject) throws IOException {
		this.server = url;
		connector = JMXConnectorFactory.connect(url, env);
		connection = connector.getMBeanServerConnection(subject);
	}

	public ConnectedMBeanHome(Object server, JMXConnector connector,
			MBeanServerConnection connection) {
		this.server = server;
		this.connector = connector;
		this.connection = connection;
	}

	public Object getServerId() {
		return server;
	}

	public JMXConnector getConnector() {
		return connector;
	}

	public MBeanServerConnection getMBeanServerConnection() {
		return connection;
	}

}
