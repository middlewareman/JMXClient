/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import javax.naming.InitialContext

import com.middlewareman.mbean.*
import com.middlewareman.mbean.platform.MBeanPlatformHome
import com.middlewareman.mbean.platform.ProxyPlatformHome

class RuntimeServer extends WebLogicMBeanServer {

	private static final localJndiName = 'java:comp/env/jmx/runtime'
	private static final remoteJndiName = 'weblogic.management.mbeanservers.runtime'

	public static final runtimeServiceName = 'com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean'
	public static final typeServiceName = 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'

	static LocalMBeanHome getLocalMBeanHome() {
		def ic = new InitialContext()
		def server = ic.lookup(localJndiName)
		new LocalMBeanHome(localJndiName, server)
	}

	static RuntimeServer getLocalRuntimeServer() {
		new RuntimeServer(getLocalMBeanHome())
	}

	private RuntimeServer(MBeanHome home) {
		super(home)
	}
	
	RuntimeServer(MBeanHomeFactory homeFactory) {
		super(homeFactory.createMBeanHome(remoteJndiName))
	}

	MBean getRuntimeService() {
		home.getMBean runtimeServiceName
	}

	MBean getTypeService() {
		home.getMBean typeServiceName
	}

	ProxyPlatformHome getProxyPlatformHome() {
		new ProxyPlatformHome(home)
	}

	MBeanPlatformHome getMBeanPlatformHome() {
		new MBeanPlatformHome(home)
	}
}
