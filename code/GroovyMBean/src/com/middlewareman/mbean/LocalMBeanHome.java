package com.middlewareman.mbean;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;

public class LocalMBeanHome extends CachingMBeanHome {

	private final MBeanServer mbeanServer;

	public LocalMBeanHome(Object url, MBeanServer mbeanServer) {
		super(url, true, false);
		this.mbeanServer = mbeanServer;
	}

	public MBeanServer getMBeanServer() {
		return mbeanServer;
	}

	public MBeanServerConnection getMBeanServerConnection() {
		return getMBeanServer();
	}
	
	public void close() {
	}

}
