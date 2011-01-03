/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.platform

import com.middlewareman.mbean.LocalPlatformMBeanHome 
import com.middlewareman.mbean.MBeanHome 

class LocalPlatformHomeTest extends PlatformHomeTestAbstract {
	
	final ph = new LocalPlatformHome()
}

class ProxyPlatformHomeTest extends PlatformHomeTestAbstract {
	
	final ph = new ProxyPlatformHome(new LocalPlatformMBeanHome())
}

class MBeanPlatformHomeTest extends PlatformHomeTestAbstract {
	
	final ph = new MBeanPlatformHome(new LocalPlatformMBeanHome())
}

class CachingMBeanPlatformHomeTest extends PlatformHomeTestAbstract {
	
	final MBeanHome mbh
	final ph
	
	CachingMBeanPlatformHomeTest() {
		mbh = new LocalPlatformMBeanHome()
		mbh.enableMBeanCache()
		mbh.enableMBeanInfoCache 0
		ph = new MBeanPlatformHome(mbh)
	}
	
	void tearDown() {
		println "after $name:\n\t$mbh.mbeanFactory\n\t$mbh.mbeanInfoFactory"
		super.tearDown()
	}
}

abstract class PlatformHomeTestAbstract extends GroovyTestCase {
	
	abstract getPh();
	
	private describe(def bean) {
		println "\n$name"
		bean.properties.each { k, v -> println "  $k:\t$v" }
	}
	
	void testClassLoading() {
		def xx = ph.classLoading
		assert xx.equals(ph.classLoading)
		def i = xx.loadedClassCount
		assert i
		assert i instanceof Integer
		describe xx
	}
	
	void testMemory() {
		def xx = ph.memory
		assert xx.equals(ph.memory)
		def used = xx.heapMemoryUsage.used
		assert used
		assert used instanceof Long
		describe xx
	}
	
	void testThread() {
		def xx = ph.thread
		assert xx.equals(ph.thread)
		def x = xx.totalStartedThreadCount
		assert x
		assert x instanceof Long
		describe xx
	}
	
	void testRuntime() {
		def xx = ph.runtime
		assert xx.equals(ph.runtime)
		def cp = xx.classPath
		assert cp
		assert cp instanceof String
		assert cp == ph.runtime.systemProperties.'java.class.path'
		def args = ph.runtime.inputArguments
		assert args instanceof List<String> || args instanceof String[], args.dump()
		describe xx
	}
	
	void testCompilation() {
		def xx = ph.compilation
		assert xx.equals(ph.compilation)
		def x = xx.compilationTimeMonitoringSupported
		assert x instanceof Boolean
		if (x) {
			def y = xx.totalCompilationTime
			assert y
			assert y instanceof Long
		}
		describe xx
	}
	
	protected void testGarbageCollector(def gc) {
		def x = gc.collectionTime
		assert x instanceof Long
		def y = gc.memoryPoolNames
		assert y
		assert y instanceof String[], gc.dump()
		describe gc
	}
	
	void testGarbageCollector() {
		def xx = ph.garbageCollectors
		assert xx.equals(ph.garbageCollectors)
		assert xx
		for (gc0 in ph.garbageCollectors) {
			def gc1 = ph.getGarbageCollector(gc0.name)
			assert gc0 == gc1
			testGarbageCollector gc0
		}
	}
	
	void testGarbageCollectors() {
		def x = ph.garbageCollectors
		assert x.equals(ph.garbageCollectors)
		assert x
		x.each { testGarbageCollector it }
	}
	
	protected testMemoryManager(def mm) {
		def x = mm.valid
		assert x instanceof Boolean
		assert mm.memoryPoolNames
		describe mm
	} 
	
	void testMemoryManager() {
		assert ph.memoryManagers
		for (mm0 in ph.memoryManagers) {
			def mm1 = ph.getMemoryManager(mm0.name)
			assert mm0.equals(mm1)
			testMemoryManager mm0
		}
	}
	
	void testMemoryManagers(){
		def x = ph.memoryManagers
		assert x.equals(ph.memoryManagers)
		assert x
		x.each { testMemoryManager it }
	}
	
	protected void testMemoryPool(def mp) {
		def x = mp.valid
		assert x instanceof Boolean
		def max = mp.usage.max
		assert max instanceof Long
		assert max
		describe mp
	}
	
	void testMemoryPool() {
		assert ph.memoryPools
		for (mp0 in ph.memoryPools) {
			def mp1 = ph.getMemoryPool(mp0.name)
			assert mp0.equals(mp1)
			testMemoryPool mp0
		}
	}
	
	void testMemoryPools() {
		def x = ph.memoryPools
		assert x.equals(ph.memoryPools)
		assert x
		x.each { testMemoryPool it }
	}
	
	void testOperatingSystem() {
		def xx = ph.operatingSystem
		assert xx.equals(ph.operatingSystem)
		assert xx
		def procs = xx.availableProcessors
		assert procs instanceof Integer
		assert procs > 0
		assert xx.arch
		describe xx
	}
}