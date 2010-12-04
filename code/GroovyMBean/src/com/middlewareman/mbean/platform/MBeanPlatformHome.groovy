/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.platform

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 

import java.lang.management.*

/**
 * Java Platform MXBeans factory returning {@link MBean MBeans}.
 * 
 * @author Andreas Nyberg
 */
class MBeanPlatformHome implements IPlatformHome {
	
	private final MBeanHome home
	private final PlatformNames names
	
	MBeanPlatformHome(MBeanHome home, PlatformNames names = PlatformNames.DEFAULT) {
		this.home = home
		this.names = names
	}
	
	MBean getClassLoading() {
		home.getMBean names.classLoading
	}	
	
	MBean getMemory() {
		home.getMBean names.memory
	}
	
	MBean getThread() {
		home.getMBean names.thread
	}
	
	MBean getRuntime() {
		home.getMBean names.runtime
	}
	
	MBean getCompilation() {
		home.getMBean names.compilation
	}
	
	MBean getOperatingSystem() {
		home.getMBean names.operatingSystem
	}
	
	Collection<MBean> getMemoryPools() {
		home.getMBeans names.getMemoryPool('*')
	}
	
	MBean getMemoryPool(String name) {
		home.getMBean names.getMemoryPool(name)
	}
	
	Collection<MBean> getMemoryManagers() {
		home.getMBeans names.getMemoryManager('*')
	}
	
	MBean getMemoryManager(String name) {
		home.getMBean names.getMemoryManager(name)
	}
	
	Collection<MBean> getGarbageCollectors() {
		home.getMBeans names.getGarbageCollector('*')
	}
	
	MBean getGarbageCollector(String name) {
		home.getMBean names.getGarbageCollector(name)
	}
}
