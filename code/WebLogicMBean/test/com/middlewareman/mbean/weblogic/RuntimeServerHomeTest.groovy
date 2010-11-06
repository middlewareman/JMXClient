package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.MBean 
import groovy.util.GroovyTestCase
import javax.management.MBeanAttributeInfo 
import javax.management.MBeanInfo 
import javax.management.MBeanOperationInfo 
import javax.management.MBeanParameterInfo 
import javax.management.openmbean.CompositeData 
import javax.management.openmbean.CompositeType 

class RuntimeServerHomeTest extends GroovyTestCase {
	
	final RuntimeServerHome home
	
	RuntimeServerHomeTest() {
		home = new RuntimeServerHome(
				new DefaultMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1'))
		home.home.assertRegistered = true
	}
	
	void test(MBean mbean) {
		assert mbean
		println mbean
		describe mbean.@home.getInfo(mbean.@objectName)
	}
	
	void describe(MBeanInfo bean) {
		println '\n>>>'
		println bean.className
		for (MBeanAttributeInfo attr in bean.attributes) { 
			println "  ${attr.type}\t${attr.name}"
		}
		for (MBeanOperationInfo oper in bean.operations) {
			def sign = oper.signature.collect { MBeanParameterInfo pi -> "$pi.type $pi.name" }.join(',')
			println "  $oper.returnType $oper.name($sign)"
		}
	}
	
	void describe(CompositeData cd) {
		CompositeType ct = cd.getCompositeType()
		for (key in ct.keySet()) {
			
		}
	}
	
	void testGetRuntimeService() {
		def rs = home.runtimeService
		test rs
		println rs.Name
		println rs.Services
	}
	
	void testGetTypeService() {
		def bean = home.typeService
		test bean
		assert bean.getMBeanInfo('weblogic.management.configuration.DomainMBean')
	}
	
	void testGetClassLoadingMXMBean() {
		def bean = home.classLoadingMXMBean
		assert bean
		test bean
		def lcc = bean.LoadedClassCount
		println "classLoading.LoadedClassCount $lcc"
		assert lcc
	}
	
	void testGetMemoryMXBean() {
		def bean = home.memoryMXBean
		test bean
		println "memory.HeapMemoryUsage before $bean.HeapMemoryUsage"
		bean.gc()
		println "memory.HeapMemoryUsage after  $bean.HeapMemoryUsage"
	}
	
	void testGetThreadMXBean() {
		def bean = home.threadMXBean
		test bean
		println "thread.AllThreadIds $bean.AllThreadIds"
		println "thread.PeakThreadCount before $bean.PeakThreadCount"
		bean.resetPeakThreadCount()
		println "thread.PeakThreadCount after  $bean.PeakThreadCount"
	}
	
	void testGetGarbageCollectorMXBeans() {
		def beans = home.garbageCollectorMXBeans
		assert beans
		for (bean in beans) {
			test bean
			println "garbageCollector '$bean.Name' lastGcInfo $bean.LastGcInfo"
		}
	}
	
	void testGetGarbageCollectorMXBean() {
		def beans = home.garbageCollectorMXBeans
		assert beans
		for (bean1 in beans) {
			def Name = bean1.Name
			def name = bean1.@objectName.keyPropertyList.name
			assert Name == name
			def bean2 = home.getGarbageCollectorMXBean(name)
			assert bean1 == bean2
			assert bean2
		}
	}
	
	void testGetMemoryManagerMXBeans() {
		def beans = home.memoryManagerMXBeans
		assert beans
		for (bean in beans) {
			test bean
			println "memoryManager '$bean.Name' $bean.MemoryPoolNames $bean.Valid"
		}
	}
	
	void testGetMemoryManagerMXBean() {
		def beans = home.memoryManagerMXBeans
		assert beans
		for (bean1 in beans) {
			def Name = bean1.Name
			def name = bean1.@objectName.keyPropertyList.name
			assert Name == name
			def bean2 = home.getMemoryManagerMXBean(name)
			assert bean1 == bean2
			assert bean2
		}
	}
	
	void testGetMemoryPoolMXBeans() {
		def beans = home.memoryPoolMXBeans
		assert beans
		for (bean in beans) {
			test bean
			println "memoryPool.PeakUsage before $bean.PeakUsage"
			bean.resetPeakUsage()
			println "memoryPool.PeakUsage after  $bean.PeakUsage"
		}
	}
	
	void testGetMemoryPoolMXBean() {
		def beans = home.memoryPoolMXBeans
		assert beans
		for (bean1 in beans) {
			def Name = bean1.Name
			def name = bean1.@objectName.keyPropertyList.name
			assert Name == name
			def bean2 = home.getMemoryPoolMXBean(name)
			assert bean1 == bean2
			assert bean2
		}
	}
	
	void testGetOperatingSystemMXBean() {
		def bean = home.operatingSystemMXBean
		test bean
		println "operatingSystem $bean.Name $bean.Version $bean.Arch load=$bean.SystemLoadAverage"
	}
	
	void testGetCompilationMXBean() {
		def bean = home.compilationMXBean
		test bean
		println "compilation $bean.Name $bean.CompilationTimeMonitoringSupported $bean.TotalCompilationTime"
	}
	
	void testGetRuntimeMXBean() {
		def bean = home.runtimeMXBean
		test bean
		println "runtime StartTime ${new Date(bean.StartTime)}, Uptime $bean.Uptime"
	}
}