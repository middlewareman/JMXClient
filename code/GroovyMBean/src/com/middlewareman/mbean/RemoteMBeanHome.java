/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;

import javax.management.remote.JMXConnector;

/**
 * MBeanHome with remote MBeanServer over MBeanServerConnection with
 * {@link #close()}.
 * 
 * @author Andreas Nyberg
 */
public abstract class RemoteMBeanHome extends MBeanHome {

	private class Closer extends Thread {
		public void run() {
			JMXConnector connector = getConnector();
			if (connector != null) {
				try {
					connector.close();
				} catch (IOException e) {
					// TODO logging
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns the connector that opened any connection. Used only to close, so
	 * no point in creating or opening if not already so.
	 */
	public abstract JMXConnector getConnector();

	/**
	 * Attempt to diligently close any connections. Runs in a daemon thread as
	 * this can be a slow operation.
	 * 
	 * @see JMXConnector#close()
	 */
	public void close() throws IOException {
		Thread thread = new Closer();
		thread.setName(getClass().getName() + " " + getServerId().toString());
		thread.setDaemon(true);
		thread.start();
	}

}
