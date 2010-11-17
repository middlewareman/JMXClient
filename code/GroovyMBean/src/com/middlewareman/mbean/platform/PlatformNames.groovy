package com.middlewareman.mbean.platform

import java.lang.management.ManagementFactory 

class PlatformNames {
	
	static final PlatformNames DEFAULT = new PlatformNames()
	
	String getClassLoading() {
		ManagementFactory.CLASS_LOADING_MXBEAN_NAME
	}
	
	String getMemory() {
		ManagementFactory.MEMORY_MXBEAN_NAME
	}
	
	String getThread() {
		ManagementFactory.THREAD_MXBEAN_NAME
	}
	
	String getRuntime() {
		ManagementFactory.RUNTIME_MXBEAN_NAME
	}
	
	String getCompilation() {
		ManagementFactory.COMPILATION_MXBEAN_NAME
	}
	
	String getOperatingSystem() {
		ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME
	}
	
	String getMemoryPools() {
		ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE
	}
	
	String getMemoryPool(String name) {
		"$memoryPools,name=$name"
	}
	
	String getMemoryManagers() {
		ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE
	}
	
	String getMemoryManager(String name) {
		"$memoryManagers,name=$name"
	}
	
	String getGarbageCollectors() {
		ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE
	}
	
	String getGarbageCollector(String name) {
		"$garbageCollectors,name=$name"
	}
}
