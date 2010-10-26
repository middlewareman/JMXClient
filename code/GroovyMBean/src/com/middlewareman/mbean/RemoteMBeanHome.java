package com.middlewareman.mbean;

import javax.management.MBeanServerConnection;

public class RemoteMBeanHome extends MBeanHome {

	public RemoteMBeanHome(Object url) {
		super(url);
	}

	@Override
	public MBeanServerConnection getMBeanServerConnection() {
		// TODO Auto-generated method stub
		return null;
	}

}
