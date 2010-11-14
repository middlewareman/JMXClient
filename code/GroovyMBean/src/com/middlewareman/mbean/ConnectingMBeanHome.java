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

public class ConnectingMBeanHome extends MBeanHome implements Serializable,
		NotificationListener {

	private static final long serialVersionUID = 1L;

	private JMXServiceURL url;
	private Map<String, ?> env;
	private Subject subject;
	private transient JMXConnector connector;
	private transient MBeanServerConnection connection;

	protected ConnectingMBeanHome() {
		/* Serialization */
	}

	public ConnectingMBeanHome(JMXServiceURL url, Map<String, ?> env,
			Subject subject) {
		super(url);
		this.url = url;
		this.env = env;
		this.subject = subject;
	}

	public synchronized MBeanServerConnection getMBeanServerConnection()
			throws IOException {
		if (connection != null)
			return connection;
		if (connector == null) {
			connector = JMXConnectorFactory.connect(url, env);
			connector.addConnectionNotificationListener(this, null, null);
			addShutdownHook(connector);
		}
		return connector.getMBeanServerConnection(subject);
	}

	public void close() throws IOException {
		connector.close();
	}

	private void addShutdownHook(final JMXConnector connector) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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

	public synchronized void reset() {
		connector = null;
		connection = null;
	}
}
