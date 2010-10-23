package com.middlewareman.mbean;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;

public class LocalMBeanHome extends MBeanHome {

	@Override
	public MBeanServerConnection getMBeanServerConnection() {
		return getMBeanServer();
	}

	public MBeanServer getMBeanServer() {
		return null; // TODO
	}

}
