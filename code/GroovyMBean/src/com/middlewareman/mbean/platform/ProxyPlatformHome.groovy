/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.platform

import java.lang.management.*

import javax.management.MBeanServerConnection
import javax.management.ObjectName

import com.middlewareman.mbean.MBeanServerConnectionFactory

/**
 * Java Platform MXBeans factory returning type-safe MBean proxies.
 * 
 * @author Andreas Nyberg
 */
class ProxyPlatformHome implements IProxyPlatformHome {

	private final MBeanServerConnectionFactory scf
	private final PlatformNames names
	boolean check = true

	ProxyPlatformHome(MBeanServerConnectionFactory connectionFactory, PlatformNames names = PlatformNames.DEFAULT) {
		this.scf = connectionFactory;
		this.names = names;
	}

	private MBeanServerConnection getConnection() {
		scf.getConnection()
	}

	private Object newPlatformMXBeanProxy(String name, Class clazz) {
		MBeanServerConnection connection = scf.getConnection()
		if (check && !connection.isRegistered(new ObjectName(name))) null
		else ManagementFactory.newPlatformMXBeanProxy(connection, name, clazz)
	}

	ClassLoadingMXBean getClassLoading() {
		newPlatformMXBeanProxy(names.classLoading, ClassLoadingMXBean.class)
	}

	MemoryMXBean getMemory() {
		newPlatformMXBeanProxy(names.memory, MemoryMXBean.class)
	}

	ThreadMXBean getThread() {
		newPlatformMXBeanProxy(names.thread, ThreadMXBean.class)
	}

	RuntimeMXBean getRuntime() {
		newPlatformMXBeanProxy(names.runtime, RuntimeMXBean.class)
	}

	CompilationMXBean getCompilation() {
		newPlatformMXBeanProxy(names.compilation, CompilationMXBean.class)
	}

	OperatingSystemMXBean getOperatingSystem() {
		newPlatformMXBeanProxy(names.operatingSystem, OperatingSystemMXBean.class)
	}

	Collection<MemoryPoolMXBean> getMemoryPools() {
		connection.queryNames(new ObjectName(names.getMemoryPool('*')), null).collect {
			newPlatformMXBeanProxy(it.toString(), MemoryPoolMXBean.class)
		}
	}

	MemoryPoolMXBean getMemoryPool(String name) {
		newPlatformMXBeanProxy(names.getMemoryPool(name), MemoryPoolMXBean.class)
	}

	Collection<MemoryManagerMXBean> getMemoryManagers() {
		connection.queryNames(new ObjectName(names.getMemoryManager('*')), null).collect {
			newPlatformMXBeanProxy(it.toString(), MemoryManagerMXBean.class)
		}
	}

	MemoryManagerMXBean getMemoryManager(String name) {
		newPlatformMXBeanProxy(names.getMemoryManager(name), MemoryManagerMXBean.class)
	}

	Collection<GarbageCollectorMXBean> getGarbageCollectors() {
		connection.queryNames(new ObjectName(names.getGarbageCollector('*')), null).collect {
			newPlatformMXBeanProxy(it.toString(), GarbageCollectorMXBean.class)
		}
	}

	GarbageCollectorMXBean getGarbageCollector(String name) {
		newPlatformMXBeanProxy(names.getGarbageCollector(name), GarbageCollectorMXBean.class)
	}
}
