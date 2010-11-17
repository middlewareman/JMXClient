package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformNames

class ManagedServerPlatformNames extends PlatformNames {
	
	private final appendix;
	
	ManagedServerPlatformNames(String serverName) {
		appendix = ",Location=$serverName"
	}
	
	String getClassLoading() {
		super.classLoading + appendix
	}
	
	String getMemory() {
		super.memory + appendix
	}
	
	String getThread() {
		super.thread + appendix
	}
	
	String getRuntime() {
		super.runtime + appendix
	}
	
	String getCompilation() {
		super.compilation + appendix
	}
	
	String getOperatingSystem() {
		super.operatingSystem + appendix
	}
	
	String getMemoryPools() {
		super.memoryPools + appendix
	}
	
	String getMemoryManagers() {
		super.memoryManagers + appendix
	}
	
	String getGarbageCollectors() {
		super.garbageCollectors + appendix
	}
}
