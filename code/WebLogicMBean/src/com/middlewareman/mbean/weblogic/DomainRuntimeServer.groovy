package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.LocalMBeanHome;
import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 
import com.middlewareman.mbean.MBeanHomeFactory 
import com.middlewareman.mbean.platform.MBeanPlatformHome;
import com.middlewareman.mbean.platform.ProxyPlatformHome;

import java.lang.management.ManagementFactory 
import javax.naming.InitialContext 

class DomainRuntimeServer {
	
	private static final localJndiName = 'java:comp/env/jmx/domainRuntime'
	private static final remoteJndiName = 'weblogic.management.mbeanservers.domainruntime'
	
	static LocalMBeanHome getLocalMBeanHome() {
		def ic = new InitialContext()
		def server = ic.lookup(localJndiName)
		new LocalMBeanHome(localJndiName, server)
	}
	
	static DomainRuntimeServer getLocalDomainRuntimeServer() {
		new DomainRuntimeServer(getLocalMBeanHome())
	}
	
	final MBeanHome home
	
	DomainRuntimeServer(MBeanHome home) {
		this.home = home
	}
	
	DomainRuntimeServer(MBeanHomeFactory homeFactory) {
		this.home = homeFactory.createMBeanHome(remoteJndiName)
	}
	
	MBean getDomainRuntimeService() {
		home.getMBean 'com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean'
	}
	
	MBean getTypeService() {
		home.getMBean 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'
	}
	
	ProxyPlatformHome getProxyPlatformHome(String serverName) {
		new ProxyPlatformHome(home, new ManagedServerPlatformNames(serverName))
	}
	
	MBeanPlatformHome getMBeanPlatformHome(String serverName) {
		new MBeanPlatformHome(home, new ManagedServerPlatformNames(serverName))
	}
}
