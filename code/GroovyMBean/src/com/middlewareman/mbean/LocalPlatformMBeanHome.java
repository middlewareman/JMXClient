package com.middlewareman.mbean;

import java.lang.management.ManagementFactory;


public class LocalPlatformMBeanHome extends ConnectedMBeanHome {

	public LocalPlatformMBeanHome() {
		super("PlatformMBeanServer", ManagementFactory.getPlatformMBeanServer());
	}

}
