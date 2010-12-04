/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.platform

import java.lang.management.*

/**
 * Java Platform MXBeans factory returning local JVM MXBeans.
 * Primarily used for testing.
 * 
 * @author Andreas Nyberg
 */
class LocalPlatformHome implements IProxyPlatformHome {
	
	ClassLoadingMXBean getClassLoading() {
		ManagementFactory.classLoadingMXBean
	}
	
	MemoryMXBean getMemory() {
		ManagementFactory.memoryMXBean
	}
	
	ThreadMXBean getThread() {
		ManagementFactory.threadMXBean
	}
	
	RuntimeMXBean getRuntime() {
		ManagementFactory.runtimeMXBean
	}
	
	CompilationMXBean getCompilation() {
		ManagementFactory.compilationMXBean
	}
	
	OperatingSystemMXBean getOperatingSystem() {
		ManagementFactory.operatingSystemMXBean
	}
	
	Collection<MemoryPoolMXBean> getMemoryPools() {
		ManagementFactory.memoryPoolMXBeans
	}
	
	MemoryPoolMXBean getMemoryPool(String name) {
		ManagementFactory.memoryPoolMXBeans.find { it.name == name }
	}
	
	Collection<MemoryManagerMXBean> getMemoryManagers() {
		ManagementFactory.memoryManagerMXBeans
	}
	
	MemoryManagerMXBean getMemoryManager(String name) {
		ManagementFactory.memoryManagerMXBeans.find { it.name == name }
	}
	
	Collection<GarbageCollectorMXBean> getGarbageCollectors() {
		ManagementFactory.garbageCollectorMXBeans
	}
	
	GarbageCollectorMXBean getGarbageCollector(String name) {
		ManagementFactory.garbageCollectorMXBeans.find { it.name == name }
	}
}
