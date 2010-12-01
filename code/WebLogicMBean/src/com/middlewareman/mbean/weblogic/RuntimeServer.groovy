package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.LocalMBeanHome
import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 
import com.middlewareman.mbean.MBeanHomeFactory 
import com.middlewareman.mbean.platform.IPlatformHome 
import com.middlewareman.mbean.platform.MBeanPlatformHome 
import com.middlewareman.mbean.platform.ProxyPlatformHome 
import javax.naming.InitialContext 

class RuntimeServer {
	
	private static final localJndiName = 'java:comp/env/jmx/runtime'
	private static final remoteJndiName = 'weblogic.management.mbeanservers.runtime'
	
	static LocalMBeanHome getLocalMBeanHome() {
		def ic = new InitialContext()
		def server = ic.lookup(localJndiName)
		new LocalMBeanHome(localJndiName, server)
	}
	
	static RuntimeServer getLocalRuntimeServer() {
		new RuntimeServer(getLocalMBeanHome())
	}
	
	final MBeanHome home
	
	RuntimeServer(MBeanHome home) {
		this.home = home
	}
	
	RuntimeServer(MBeanHomeFactory homeFactory) {
		this.home = homeFactory.createMBeanHome(remoteJndiName)
	}
	
	MBean getRuntimeService() {
		home.getMBean 'com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean'
	}
	
	MBean getTypeService() {
		home.getMBean 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'
	}
	
	ProxyPlatformHome getProxyPlatformHome() {
		new ProxyPlatformHome(home)
	}
	
	MBeanPlatformHome getMBeanPlatformHome() {
		new MBeanPlatformHome(home)
	}
}
