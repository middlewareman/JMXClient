/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import javax.management.MBeanServerConnection;

/**
 * (Remote)MBeanHome that maintains an open MBeanServerConnection only without the ability to reconnect.
 * 
 * @author Andreas Nyberg
 */
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
