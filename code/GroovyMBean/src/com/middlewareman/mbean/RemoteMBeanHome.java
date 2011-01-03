/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

/**
 * MBeanHome with remote MBeanServer over MBeanServerConnection with
 * {@link #close()}.
 * 
 * @author Andreas Nyberg
 */
public abstract class RemoteMBeanHome extends MBeanHome {

	private class Closer extends Thread {
		final JMXConnector connector;

		Closer(JMXConnector connector) {
			this.connector = connector;
		}

		public void run() {
			String id = "(connector not available)";
			try {
				id = connector.getConnectionId();
				connector.close();
				logger.log(Level.FINE, "daemonClose closed {0}", id);
			} catch (IOException e) {
				logger.log(Level.INFO, "daemonClose FAILED closing " + id, e);
			}
		}
	}

	/** Returns any existing connector but does not create or open anything. */
	protected abstract JMXConnector getConnector();

	/** Returns a verified existing or new connector but never null. */
	protected abstract JMXConnector createConnector() throws IOException;
	
	/** Returns a new connector. */
	protected JMXConnector createConnector(JMXServiceURL url, Map<String, ?> env)
			throws IOException {
		logger.log(Level.FINER, "connecting to {0}", url);
		return JMXConnectorFactory.connect(url, env);
	}

	/** Returns a new connection. */
	protected MBeanServerConnection createConnection(JMXConnector connector,
			Subject subject) throws IOException {
		String id = connector.getConnectionId();
		logger.log(Level.FINER, "connecting to {0} for {1}", new Object[] { id,
				subject });
		return connector.getMBeanServerConnection(subject);
	}

	/**
	 * Spawns a daemon thread to close any current connection.
	 * 
	 * @see JMXConnector#close()
	 */
	public synchronized void daemonClose() throws IOException {
		JMXConnector connector = getConnector();
		if (connector == null) {
			logger.log(Level.FINER, "connector null -> nothing to close");
			return;
		}
		String id;
		try {
			id = connector.getConnectionId();
			logger.log(Level.FINER, "connector {0}", id);
		} catch (IOException e) {
			id = e.getMessage();
		}
		String name = getClass().getSimpleName() + " Closer for " + id;
		Thread thread = new Closer(getConnector());
		thread.setName(name);
		thread.setDaemon(true);
		logger.log(Level.FINER, "spawning {0}", name);
		thread.start();
	}

	/**
	 * Crudely close any connection.
	 * 
	 * @see JMXConnector#close()
	 */
	public void close() throws IOException {
		JMXConnector connector = getConnector();
		if (connector == null) {
			logger.log(Level.FINER, "connector null -> nothing to close");
		} else {
			String id = connector.getConnectionId();
			logger.log(Level.FINER, "closing {0}", id);
			connector.close();
			logger.log(Level.FINE, "closed {0}", id);
		}
	}

	/**
	 * Returns true if the connector is valid or can be made valid by opening
	 * it.
	 */
	public boolean asBoolean() {
		try {
			JMXConnector connector = createConnector();
			if (connector == null) {
				logger.log(Level.FINER, "connector null -> false");
				return false;
			}
			String id = connector.getConnectionId();
			logger.log(Level.FINER, "connector {0} -> true", id);
			return true;
		} catch (IOException e) {
			logger.log(Level.FINE, "exception -> false", e);
			return false;
		}
	}

	public String toString() {
		JMXConnector connector = getConnector();
		String status = null;
		if (connector != null) {
			try {
				status = connector.getConnectionId();
			} catch (IOException e) {
				status = e.getMessage();
			}
		}
		return getClass().getSimpleName() + "(" + getServerId() + ", " + status
				+ ")";
	}

}
