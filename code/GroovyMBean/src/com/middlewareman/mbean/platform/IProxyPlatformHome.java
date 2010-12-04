/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.platform;

import java.lang.management.*;
import java.util.Collection;

/**
 * Type-safe Java Platform MXBeans factory.
 * 
 * @author Andreas Nyberg
 */
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
