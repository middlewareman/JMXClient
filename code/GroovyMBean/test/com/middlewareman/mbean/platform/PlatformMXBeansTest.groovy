/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.platform

import java.lang.management.*

import javax.management.MBeanServer
import javax.management.InstanceNotFoundException
import javax.management.RuntimeMBeanException 

import com.middlewareman.mbean.*
import com.middlewareman.mbean.type.* 

class PlatformMXBeansTest extends GroovyTestCase {
	
	MBeanHome home
	
	void setUp() {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer()
		assert server
		home = new LocalMBeanHome('platformMBeanServer', server)
		assert home
	}
	
	void tearDown() {
		home.close()
	}
	
	void compareRemoteLocal(MBean remote, Object local) {
		println name
		Map remoteProps = remote.properties
		//println "remote keys: ${remoteProps.keySet().sort()}"
		Map localProps = local.properties
		//println "local keys:  ${localProps.keySet().sort()}"
		println "remote-local: ${remoteProps.keySet() - localProps.keySet()}"
		println "local-remote: ${localProps.keySet() - remoteProps.keySet()}"
		assert localProps.keySet().containsAll(remoteProps.keySet())
		for (key in remoteProps.keySet()) {
			def rval = remoteProps.get(key)
			def lval = localProps.get(key)
			if (rval != lval) {
				println "    $key\tNOT EQUAL"
				println "\t  remote\t(${rval.getClass().getName()})\t$rval"
				println "\t  local \t(${lval.getClass().getName()})\t$lval"
			}
		}
		compareProps remoteProps, localProps
	}
	
	void compareProps(Map<String,?> remote, Map<String,?> local) {
		remote.each { rk, rv ->
			assert local.containsKey(rk)
			if (rv instanceof Map) {
				compareProps("$name.$rk", rv, local[rk])
			} else {
				def rval = remote[rk]
				def lval = local[rk]
				if (rval != lval) {
					println "    $rk\tNOT EQUAL"
					println "\t  remote\t(${rval.getClass().getName()})\t$rval"
					println "\t  local \t(${lval.getClass().getName()})\t$lval"
				}
			}
		}
	}
	
	void compareRemoteLocalNamedCollections(def remotes, def locals) {
		def rmap = [:]
		def lmap = [:]
		for (remote in remotes) rmap[remote.name] = remote
		for (local in locals) lmap[local.name] = local
		def commonKeys = rmap.keySet().intersect(lmap.keySet())
		println "name commonKeys $commonKeys"
		println rmap
		println lmap
		for (odd in rmap.keySet() - commonKeys) println "$name $odd only in remote"
		for (odd in lmap.keySet() - commonKeys) println "$name $odd only in local"
		for (key in commonKeys) {
			try {
				compareRemoteLocal rmap[key], lmap[key]
			} catch (RuntimeMBeanException e) {
				println "$name $key $e.message"
			}
		}
	}
	
	MBean getRemoteClassLoading() {
		home.getMBean ManagementFactory.CLASS_LOADING_MXBEAN_NAME
	}
	
	ClassLoadingMXBean getLocalClassLoading() {
		ManagementFactory.getClassLoadingMXBean()
	}
	
	void testClassLoading() {
		def m = remoteClassLoading
		def o = localClassLoading
		compareRemoteLocal m, o
		
		def mtlcc = m.totalLoadedClassCount
		def otlcc = o.totalLoadedClassCount
		assert mtlcc instanceof Long
		assert otlcc instanceof Long
		
		def mlcc = m.loadedClassCount
		def olcc = o.loadedClassCount
		assert mlcc instanceof Integer
		assert olcc instanceof Integer
		
		def mucc = m.unloadedClassCount
		def oucc = o.unloadedClassCount
		assert mucc instanceof Long
		assert oucc instanceof Long
		
		def mv = m.verbose
		def ov = o.verbose
		assert mv instanceof Boolean
		assert ov instanceof Boolean
		assert mv == ov
	}
	
	MBean getRemoteMemory() {
		home.getMBean ManagementFactory.MEMORY_MXBEAN_NAME
	}
	
	MemoryMXBean getLocalMemory() {
		ManagementFactory.getMemoryMXBean()
	}
	
	void testMemory() {
		assert remoteMemory.objectPendingFinalizationCount instanceof Integer
		assert localMemory.objectPendingFinalizationCount instanceof Integer
		
		def rhmu = remoteMemory.heapMemoryUsage
		def lhmu = localMemory.heapMemoryUsage
		assert rhmu instanceof CompositeDataWrapper
		assert rhmu.init > 0
		assert rhmu.init == lhmu.init
		assert lhmu.max > lhmu.used
		assert rhmu.max > rhmu.used
		
		compareRemoteLocal remoteMemory, localMemory
	}
	
	MBean getRemoteThread() {
		home.getMBean ManagementFactory.THREAD_MXBEAN_NAME
	}
	
	ThreadMXBean getLocalThread() {
		ManagementFactory.getThreadMXBean()
	}
	
	void testThread() {
		compareRemoteLocal remoteThread, localThread
	}
	
	MBean getRemoteRuntime() {
		home.getMBean ManagementFactory.RUNTIME_MXBEAN_NAME
	}
	
	def getLocalRuntime() {
		ManagementFactory.getRuntimeMXBean()
	}
	
	void testRuntime() {
		compareRemoteLocal remoteRuntime, localRuntime
	}
	
	MBean getRemoteCompilation() {
		home.getMBean ManagementFactory.COMPILATION_MXBEAN_NAME
	}
	
	CompilationMXBean getLocalCompilation() {
		ManagementFactory.getCompilationMXBean()
	}
	
	void testCompilation() {
		compareRemoteLocal remoteCompilation, localCompilation
	}
	
	MBean getRemoteOperatingSystem() {
		home.getMBean ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME
	}
	
	OperatingSystemMXBean getLocalOperatingSystem() {
		ManagementFactory.getOperatingSystemMXBean()
	}
	
	void testOperatingSystem() {
		compareRemoteLocal remoteOperatingSystem, localOperatingSystem
	}
	
	Set<MBean> getRemoteMemoryPools() {
		home.getMBeans "${ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE},*"
	}
	
	List<MemoryPoolMXBean> getLocalMemoryPools() {
		ManagementFactory.getMemoryPoolMXBeans()
	}
	
	void testMemoryPools() {
		compareRemoteLocalNamedCollections remoteMemoryPools, localMemoryPools
	}
	
	MBean getRemoteMemoryPool(String key) {
		home.getMBean "${ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE},name=${key}"
	}
	
	MemoryPoolMXBean getLocalMemoryPool(String key) {
		getLocalMemoryPools().find { it.name == key }
	}
	
	void testMemoryPool() {
		for (mps in localMemoryPools) {
			def key = mps.name
			println getRemoteMemoryPool(key).collectionUsage
			println getLocalMemoryPool(key).collectionUsage
		}
	}
	
	Set<MBean> getRemoteMemoryManagers() {
		home.getMBeans("${ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE},*")
	}
	
	List<MemoryManagerMXBean> getLocalMemoryManagers() {
		ManagementFactory.getMemoryManagerMXBeans()
	}
	
	void testMemoryManagers() {
		compareRemoteLocalNamedCollections remoteMemoryManagers, localMemoryManagers
	}
	
	MBean getRemoteMemoryManager(String key) {
		home.getMBean "${ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE},name=${key}"
	}
	
	MemoryManagerMXBean getLocalMemoryManager(String key) {
		getLocalMemoryManagers().find { it.name == key }
	}
	
	void testMemoryManager() {
		for (mms in localMemoryManagers) {
			def key = mms.name
			def remote = getRemoteMemoryManager(key)
			def local = getLocalMemoryManager(key)
			if (!local || !remote) {
				println "$name $key ONE DOES NOT EXIST\n  local  $local\n  remote $remote"
			} else {
				compareRemoteLocal remote, local
			}
		}
	}
	
	Set<MBean> getRemoteGarbageCollectors() {
		home.getMBeans "${ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE},*"
	}
	
	List<GarbageCollectorMXBean> getLocalGarbageCollectors() {
		ManagementFactory.getGarbageCollectorMXBeans()
	}
	
	void testGarbageCollectors() {
		compareRemoteLocalNamedCollections remoteGarbageCollectors, localGarbageCollectors
	}
	
	MBean getRemoteGarbageCollector(String name) {
		home.getMBean "${ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE},name=${name}"
	}
	
	GarbageCollectorMXBean getLocalGarbageCollector(String key) {
		getLocalGarbageCollectors().find { it.name == key }
	}
	
	void testGarbageCollector() {
		for (gcs in getLocalGarbageCollectors()) {
			def key = gcs.name
			compareRemoteLocal getRemoteGarbageCollector(key), getLocalGarbageCollector(key)
		}
	}
}
