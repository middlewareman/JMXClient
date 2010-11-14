package com.middlewareman.mbean.platform

import com.middlewareman.mbean.MBeanServerConnectionFactory 
import java.lang.management.*
import javax.management.MBeanServerConnection 
import javax.management.ObjectName 

class ProxyPlatformHome extends PlatformHome implements IPlatformHome {
	
	private final MBeanServerConnectionFactory scf;
	
	ProxyPlatformHome(MBeanServerConnectionFactory connectionFactory) {
		this.scf = connectionFactory;
	}
	
	private MBeanServerConnection getConnection() {
		scf.getMBeanServerConnection();
	}
	
	ClassLoadingMXBean getClassLoading() {
		ManagementFactory.newPlatformMXBeanProxy(connection, classLoadingName, ClassLoadingMXBean.class)
	}	
	
	MemoryMXBean getMemory() {
		ManagementFactory.newPlatformMXBeanProxy(connection, memoryName, MemoryMXBean.class)
	}
	
	ThreadMXBean getThread() {
		ManagementFactory.newPlatformMXBeanProxy(connection, threadName, ThreadMXBean.class)
	}
	
	RuntimeMXBean getRuntime() {
		ManagementFactory.newPlatformMXBeanProxy(connection, runtimeName, RuntimeMXBean.class)
	}
	
	CompilationMXBean getCompilation() {
		ManagementFactory.newPlatformMXBeanProxy(connection, compilationName, CompilationMXBean.class)
	}
	
	OperatingSystemMXBean getOperatingSystem() {
		ManagementFactory.newPlatformMXBeanProxy(connection, operatingSystemName, OperatingSystemMXBean.class)
	}
	
	List<MemoryPoolMXBean> getMemoryPools() {
		connection.queryNames(new ObjectName(getMemoryPoolName('*')), null).collect { 
			ManagementFactory.newPlatformMXBeanProxy(connection, it.toString(), MemoryPoolMXBean.class)
		}
	}
	
	MemoryPoolMXBean getMemoryPool(String name) {
		ManagementFactory.newPlatformMXBeanProxy(connection, getMemoryPoolName(name), MemoryPoolMXBean.class)
	}
	
	List<MemoryManagerMXBean> getMemoryManagers() {
		connection.queryNames(new ObjectName(getMemoryManagerName('*')), null).collect { 
			ManagementFactory.newPlatformMXBeanProxy(connection, it.toString(), MemoryManagerMXBean.class)
		}
	}
	
	MemoryManagerMXBean getMemoryManager(String name) {
		ManagementFactory.newPlatformMXBeanProxy(connection, getMemoryManagerName(name), MemoryManagerMXBean.class)
	}
	
	List<GarbageCollectorMXBean> getGarbageCollectors() {
		connection.queryNames(new ObjectName(getGarbageCollectorName('*')), null).collect { 
			ManagementFactory.newPlatformMXBeanProxy(connection, it.toString(), GarbageCollectorMXBean.class)
		}
	}
	
	GarbageCollectorMXBean getGarbageCollector(String name) {
		ManagementFactory.newPlatformMXBeanProxy(connection, getGarbageCollectorName(name), GarbageCollectorMXBean.class)
	}
}
