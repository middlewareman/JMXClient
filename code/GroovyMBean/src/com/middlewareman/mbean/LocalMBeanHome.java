/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;

/**
 * MBeanHome for a local MBeanServer. (Any future support for
 * {@link MBeanServer}-specific functionality such as instantiating and
 * registering MBeans will go here.)
 * 
 * @author Andreas Nyberg
 */
public class LocalMBeanHome extends MBeanHome {

	private final Object address;
	private final MBeanServer mbeanServer;

	public LocalMBeanHome(Object address, MBeanServer mbeanServer) {
		this.address = address;
		this.mbeanServer = mbeanServer;
	}

	public Object getAddress() {
		return address;
	}

	public MBeanServer getMBeanServer() {
		return mbeanServer;
	}

	public MBeanServerConnection getConnection() {
		return mbeanServer;
	}

	public void close() throws IOException {
	}

}
