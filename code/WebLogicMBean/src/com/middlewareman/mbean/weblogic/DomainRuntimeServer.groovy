/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import javax.naming.InitialContext

import com.middlewareman.mbean.*
import com.middlewareman.mbean.platform.MBeanPlatformHome
import com.middlewareman.mbean.platform.ProxyPlatformHome

class DomainRuntimeServer extends WebLogicMBeanServer {
	
	private static final localJndiName = 'java:comp/env/jmx/domainRuntime'
	private static final remoteJndiName = 'weblogic.management.mbeanservers.domainruntime'
	
	public static final domainRuntimeServiceName = 'com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean'
	public static final typeServiceName = 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'
	
	static LocalMBeanHome getLocalMBeanHome() {
		def ic = new InitialContext()
		def server = ic.lookup(localJndiName)
		new LocalMBeanHome(localJndiName, server)
	}
	
	static DomainRuntimeServer getLocalDomainRuntimeServer() {
		new DomainRuntimeServer(getLocalMBeanHome())
	}
	
	private DomainRuntimeServer(MBeanHome home) {
		super(home)
	}
	
	DomainRuntimeServer(MBeanHomeFactory homeFactory) {
		super(homeFactory.createMBeanHome(remoteJndiName))
	}
	
	MBean getDomainRuntimeService() {
		home.getMBean domainRuntimeServiceName
	}
	
	MBean getTypeService() {
		home.getMBean typeServiceName
	}
	
	ProxyPlatformHome getProxyPlatformHome(String serverName) {
		new ProxyPlatformHome(home, new ManagedServerPlatformNames(serverName))
	}
	
	MBeanPlatformHome getMBeanPlatformHome(String serverName) {
		new MBeanPlatformHome(home, new ManagedServerPlatformNames(serverName))
	}
}
