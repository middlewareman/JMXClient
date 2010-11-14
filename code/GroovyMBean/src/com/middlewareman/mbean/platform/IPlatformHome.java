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
import java.util.List;

public interface IPlatformHome {

	ClassLoadingMXBean getClassLoading();

	MemoryMXBean getMemory();

	ThreadMXBean getThread();

	RuntimeMXBean getRuntime();

	CompilationMXBean getCompilation();

	OperatingSystemMXBean getOperatingSystem();

	List<MemoryPoolMXBean> getMemoryPools();

	MemoryPoolMXBean getMemoryPool(String name);

	List<MemoryManagerMXBean> getMemoryManagers();

	MemoryManagerMXBean getMemoryManager(String name);

	List<GarbageCollectorMXBean> getGarbageCollectors();

	GarbageCollectorMXBean getGarbageCollector(String name);

}
