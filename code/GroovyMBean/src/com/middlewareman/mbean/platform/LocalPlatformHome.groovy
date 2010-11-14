package com.middlewareman.mbean.platform

import java.lang.management.*

class LocalPlatformHome implements IPlatformHome {
	
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
	
	List<MemoryPoolMXBean> getMemoryPools() {
		ManagementFactory.memoryPoolMXBeans
	}
	
	MemoryPoolMXBean getMemoryPool(String name) {
		ManagementFactory.memoryPoolMXBeans.find { it.name == name }
	}
	
	List<MemoryManagerMXBean> getMemoryManagers() {
		ManagementFactory.memoryManagerMXBeans
	}
	
	MemoryManagerMXBean getMemoryManager(String name) {
		ManagementFactory.memoryManagerMXBeans.find { it.name == name }
	}
	
	List<GarbageCollectorMXBean> getGarbageCollectors() {
		ManagementFactory.garbageCollectorMXBeans
	}
	
	GarbageCollectorMXBean getGarbageCollector(String name) {
		ManagementFactory.garbageCollectorMXBeans.find { it.name == name }
	}
}
