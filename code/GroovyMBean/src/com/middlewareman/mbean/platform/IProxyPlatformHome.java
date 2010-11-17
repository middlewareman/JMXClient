package com.middlewareman.mbean.platform;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collection;

public interface IProxyPlatformHome extends IPlatformHome {

	ClassLoadingMXBean getClassLoading();

	MemoryMXBean getMemory();

	ThreadMXBean getThread();

	RuntimeMXBean getRuntime();

	CompilationMXBean getCompilation();

	OperatingSystemMXBean getOperatingSystem();

	Collection<MemoryPoolMXBean> getMemoryPools();

	MemoryPoolMXBean getMemoryPool(String name);

	Collection<MemoryManagerMXBean> getMemoryManagers();

	MemoryManagerMXBean getMemoryManager(String name);

	Collection<GarbageCollectorMXBean> getGarbageCollectors();

	GarbageCollectorMXBean getGarbageCollector(String name);

}
