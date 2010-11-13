package com.middlewareman.mbean;

import javax.management.MBeanServerConnection;

public class ConnectedMBeanHome extends MBeanHome {

	private final MBeanServerConnection sc;
	
	public ConnectedMBeanHome(Object url, MBeanServerConnection connection) {
		super(url);
		this.sc = connection;
	}

	public MBeanServerConnection getMBeanServerConnection() {
		return sc;
	}

	public void close() {
	}

}
