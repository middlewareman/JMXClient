/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.platform;

import java.lang.management.*;
import java.util.Collection;

/**
 * Duck-typed Java Platform MXBeans factory.
 * 
 * @author Andreas Nyberg
 */
public interface IPlatformHome {

	/** @see ClassLoadingMXBean */
	Object getClassLoading();

	/** @see MemoryMXBean */
	Object getMemory();

	/** @see ThreadMXBean */
	Object getThread();

	/** @see RuntimeMXBean */
	Object getRuntime();

	/** @see CompilationMXBean */
	Object getCompilation();

	/** @see OperatingSystemMXBean */
	Object getOperatingSystem();

	/** @see MemoryPoolMXBean */
	Collection<?> getMemoryPools();

	/** @see MemoryPoolMXBean */
	Object getMemoryPool(String name);

	/** @see MemoryManagerMXBean */
	Collection<?> getMemoryManagers();

	/** @see MemoryManagerMXBean */
	Object getMemoryManager(String name);

	/** @see GarbageCollectorMXBean */
	Collection<?> getGarbageCollectors();

	/** @see GarbageCollectorMXBean */
	Object getGarbageCollector(String name);
}
