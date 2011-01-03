/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.lang.management.ManagementFactory;


public class LocalPlatformMBeanHome extends LocalMBeanHome {

	public LocalPlatformMBeanHome() {
		super("PlatformMBeanServer", ManagementFactory.getPlatformMBeanServer());
	}

}
