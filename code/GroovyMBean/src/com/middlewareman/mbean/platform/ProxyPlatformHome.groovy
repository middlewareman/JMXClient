package com.middlewareman.mbean.platform

import com.middlewareman.mbean.MBeanServerConnectionFactory 
import java.lang.management.ClassLoadingMXBean 
import java.lang.management.CompilationMXBean 
import java.lang.management.GarbageCollectorMXBean 
import java.lang.management.ManagementFactory 
import java.lang.management.MemoryMXBean 
import java.lang.management.MemoryManagerMXBean 
import java.lang.management.MemoryPoolMXBean 
import java.lang.management.OperatingSystemMXBean 
import java.lang.management.RuntimeMXBean 
import java.lang.management.ThreadMXBean 
import javax.management.MBeanServerConnection 
import javax.management.ObjectName 


class ProxyPlatformHome implements IProxyPlatformHome {
	
	private final MBeanServerConnectionFactory scf;
	private final PlatformNames names;
	
	ProxyPlatformHome(MBeanServerConnectionFactory connectionFactory, PlatformNames names = PlatformNames.DEFAULT) {
		this.scf = connectionFactory;
		this.names = names;
	}
	
	private MBeanServerConnection getConnection() {
		scf.getMBeanServerConnection();
	}
	
	ClassLoadingMXBean getClassLoading() {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.classLoading, ClassLoadingMXBean.class)
	}	
	
	MemoryMXBean getMemory() {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.memory, MemoryMXBean.class)
	}
	
	ThreadMXBean getThread() {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.thread, ThreadMXBean.class)
	}
	
	RuntimeMXBean getRuntime() {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.runtime, RuntimeMXBean.class)
	}
	
	CompilationMXBean getCompilation() {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.compilation, CompilationMXBean.class)
	}
	
	OperatingSystemMXBean getOperatingSystem() {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.operatingSystem, OperatingSystemMXBean.class)
	}
	
	Collection<MemoryPoolMXBean> getMemoryPools() {
		connection.queryNames(new ObjectName(names.getMemoryPool('*')), null).collect { 
			ManagementFactory.newPlatformMXBeanProxy(connection, it.toString(), MemoryPoolMXBean.class)
		}
	}
	
	MemoryPoolMXBean getMemoryPool(String name) {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.getMemoryPool(name), MemoryPoolMXBean.class)
	}
	
	Collection<MemoryManagerMXBean> getMemoryManagers() {
		connection.queryNames(new ObjectName(names.getMemoryManager('*')), null).collect { 
			ManagementFactory.newPlatformMXBeanProxy(connection, it.toString(), MemoryManagerMXBean.class)
		}
	}
	
	MemoryManagerMXBean getMemoryManager(String name) {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.getMemoryManager(name), MemoryManagerMXBean.class)
	}
	
	Collection<GarbageCollectorMXBean> getGarbageCollectors() {
		connection.queryNames(new ObjectName(names.getGarbageCollector('*')), null).collect { 
			ManagementFactory.newPlatformMXBeanProxy(connection, it.toString(), GarbageCollectorMXBean.class)
		}
	}
	
	GarbageCollectorMXBean getGarbageCollector(String name) {
		ManagementFactory.newPlatformMXBeanProxy(connection, names.getGarbageCollector(name), GarbageCollectorMXBean.class)
	}
}
