package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.LocalMBeanHome
import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 
import com.middlewareman.mbean.MBeanHomeFactory 
import java.lang.management.ManagementFactory 
import javax.naming.InitialContext 

class RuntimeServerHome {
	
	private static final localJndiName = 'java:comp/env/jmx/runtime'
	private static final remoteJndiName = 'weblogic.management.mbeanservers.runtime'
	
	static LocalMBeanHome getLocalMBeanHome() {
		def ic = new InitialContext()
		def server = ic.lookup(localJndiName)
		new LocalMBeanHome(localJndiName, server)
	}
	
	static RuntimeServerHome getLocalMBeanServerHome() {
		new RuntimeServerHome(getLocalMBeanHome())
	}
	
	final MBeanHome home
	
	RuntimeServerHome(MBeanHome home) {
		this.home = home
	}
	
	RuntimeServerHome(MBeanHomeFactory homeFactory) {
		this.home = homeFactory.createMBeanHome(remoteJndiName)
	}
	
	MBean getRuntimeService() {
		home.getMBean 'com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean'
	}
	
	MBean getTypeService() {
		home.getMBean 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'
	}
	
	MBean getClassLoadingMXMBean() {
		home.getMBean ManagementFactory.CLASS_LOADING_MXBEAN_NAME
	}
	
	MBean getMemoryMXBean() {
		home.getMBean ManagementFactory.MEMORY_MXBEAN_NAME
	}
	
	MBean getThreadMXBean() {
		home.getMBean ManagementFactory.THREAD_MXBEAN_NAME
	}
	
	MBean getRuntimeMXBean() {
		home.getMBean ManagementFactory.RUNTIME_MXBEAN_NAME
	}
	
	MBean getCompilationMXBean() {
		home.getMBean ManagementFactory.COMPILATION_MXBEAN_NAME
	}
	
	MBean getOperatingSystemMXBean() {
		home.getMBean ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME
	}
	
	Set<MBean> getMemoryPoolMXBeans() {
		home.getMBeans "${ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE},*"
	}
	
	MBean getMemoryPoolMXBean(String name) {
		home.getMBean "${ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE},name=${name}"
	}
	
	Set<MBean> getMemoryManagerMXBeans() {
		home.getMBeans("${ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE},*")
	}
	
	MBean getMemoryManagerMXBean(String name) {
		home.getMBean "${ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE},name=${name}"
	}
	
	Set<MBean> getGarbageCollectorMXBeans() {
		home.getMBeans "${ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE},*"
	}
	
	MBean getGarbageCollectorMXBean(String name) {
		home.getMBean "${ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE},name=${name}"
	}
}
