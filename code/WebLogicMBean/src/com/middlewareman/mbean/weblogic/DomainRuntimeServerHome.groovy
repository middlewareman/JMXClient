package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.LocalMBeanHome;
import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 
import com.middlewareman.mbean.MBeanHomeFactory 

import java.lang.management.ManagementFactory 
import javax.naming.InitialContext 

class DomainRuntimeServerHome {
	
	private static final localJndiName = 'java:comp/env/jmx/domainRuntime'
	private static final remoteJndiName = 'weblogic.management.mbeanservers.domainruntime'
	
	static LocalMBeanHome getLocalMBeanHome() {
		def ic = new InitialContext()
		def server = ic.lookup(localJndiName)
		new LocalMBeanHome(localJndiName, server)
	}
	
	static DomainRuntimeServerHome getLocalMBeanServerHome() {
		new DomainRuntimeServerHome(getLocalMBeanHome())
	}
	
	final MBeanHome home
	
	DomainRuntimeServerHome(MBeanHome home) {
		this.home = home
	}
	
	DomainRuntimeServerHome(MBeanHomeFactory homeFactory) {
		this.home = homeFactory.createMBeanHome(remoteJndiName)
	}
	
	MBean getRuntimeService() {
		home.getMBean 'com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean'
	}
	
	MBean getTypeService() {
		home.getMBean 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'
	}
	
	MBean getClassLoadingMXMBean(String serverName) {
		home.getMBean "${ManagementFactory.CLASS_LOADING_MXBEAN_NAME},Location=${serverName}"
	}
	
	MBean getMemoryMXBean(String serverName) {
		home.getMBean "${ManagementFactory.MEMORY_MXBEAN_NAME},Location=${serverName}"
	}
	
	MBean getThreadMXBean(String serverName) {
		home.getMBean "${ManagementFactory.THREAD_MXBEAN_NAME},Location=${serverName}"
	}
	
	MBean getRuntimeMXBean(String serverName) {
		home.getMBean "${ManagementFactory.RUNTIME_MXBEAN_NAME},Location=${serverName}"
	}
	
	MBean getCompilationMXBean(String serverName) {
		home.getMBean "${ManagementFactory.COMPILATION_MXBEAN_NAME},Location=${serverName}"
	}
	
	MBean getOperatingSystemMXBean(String serverName) {
		home.getMBean "${ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME},Location=${serverName}"
	}
	
	Set<MBean> getMemoryPoolMXBeans(String serverName) {
		home.getMBeans "${ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE},Location=${serverName}"
	}
	
	MBean getMemoryPoolMXBean(String name, String serverName) {
		home.getMBean "${ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE},name=${name},Location=${serverName}"
	}
	
	Set<MBean> getMemoryManagerMXBeans(String serverName) {
		home.getMBeans "${ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE},Location=${serverName}"
	}
	
	MBean getMemoryManagerMXBean(String name, String serverName) {
		home.getMBean "${ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE},name=${name},Location=${serverName}"
	}
	
	Set<MBean> getGarbageCollectorMXBeans(String serverName) {
		home.getMBeans "${ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE},Location=${serverName}"
	}
	
	MBean getGarbageCollectorMXBean(String name, String serverName) {
		home.getMBean "${ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE},name=${name},Location=${serverName}"
	}
}
