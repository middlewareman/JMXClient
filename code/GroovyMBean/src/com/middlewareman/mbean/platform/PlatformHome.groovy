package com.middlewareman.mbean.platform

import java.lang.management.ManagementFactory 
import javax.management.ObjectName 

class PlatformHome {
	
	protected String getClassLoadingName() {
		ManagementFactory.CLASS_LOADING_MXBEAN_NAME
	}
	
	protected String getMemoryName() {
		ManagementFactory.MEMORY_MXBEAN_NAME
	}
	
	protected String getThreadName() {
		ManagementFactory.THREAD_MXBEAN_NAME
	}
	
	protected String getRuntimeName() {
		ManagementFactory.RUNTIME_MXBEAN_NAME
	}
	
	protected String getCompilationName() {
		ManagementFactory.COMPILATION_MXBEAN_NAME
	}
	
	protected String getOperatingSystemName() {
		ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME
	}
	
	protected String getMemoryPoolsName() {
		ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE
	}
	
	protected String getMemoryPoolName(String name) {
		"$memoryPoolsName,name=$name"
	}
	
	protected String getMemoryManagersName() {
		ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE
	}
	
	protected String getMemoryManagerName(String name) {
		"$memoryManagersName,name=$name"
	}
	
	protected String getGarbageCollectorsName() {
		ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE
	}
	
	protected String getGarbageCollectorName(String name) {
		"$garbageCollectorsName,name=$name"
	}
}
