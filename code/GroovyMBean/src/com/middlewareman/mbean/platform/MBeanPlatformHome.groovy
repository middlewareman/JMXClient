package com.middlewareman.mbean.platform

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 
import java.lang.management.*

class MBeanPlatformHome extends PlatformHome {
	
	private final MBeanHome home
	
	MBeanPlatformHome(MBeanHome home) {
		this.home = home
	}
	
	MBean getClassLoading() {
		home.getMBean classLoadingName
	}	
	
	MBean getMemory() {
		home.getMBean memoryName
	}
	
	MBean getThread() {
		home.getMBean threadName
	}
	
	MBean getRuntime() {
		home.getMBean runtimeName
	}
	
	MBean getCompilation() {
		home.getMBean compilationName
	}
	
	MBean getOperatingSystem() {
		home.getMBean operatingSystemName
	}
	
	Collection<MBean> getMemoryPools() {
		home.getMBeans getMemoryPoolName('*')
	}
	
	MBean getMemoryPool(String name) {
		home.getMBean getMemoryPoolName(name)
	}
	
	Collection<MBean> getMemoryManagers() {
		home.getMBeans getMemoryManagerName('*')
	}
	
	MBean getMemoryManager(String name) {
		home.getMBean getMemoryManagerName(name)
	}
	
	Collection<MBean> getGarbageCollectors() {
		home.getMBeans getGarbageCollectorName('*')
	}
	
	MBean getGarbageCollector(String name) {
		home.getMBean getGarbageCollectorName(name)
	}
}
