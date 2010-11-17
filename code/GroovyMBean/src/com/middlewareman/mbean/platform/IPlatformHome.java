package com.middlewareman.mbean.platform;

import java.util.Collection;

public interface IPlatformHome {

	Object getClassLoading();

	Object getMemory();

	Object getThread();

	Object getRuntime();

	Object getCompilation();

	Object getOperatingSystem();

	Collection<?> getMemoryPools();

	Object getMemoryPool(String name);

	Collection<?> getMemoryManagers();

	Object getMemoryManager(String name);

	Collection<?> getGarbageCollectors();

	Object getGarbageCollector(String name);
}
