package com.middlewareman.mbean.weblogic

import groovy.util.GroovyTestCase

class DomainRuntimeServerTest extends GroovyTestCase {
	
	final DomainRuntimeServer home
	
	DomainRuntimeServerTest() {
		home = new DomainRuntimeServer(
				new DefaultMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1'))
	}
	
	void testDomainRuntimeService() {
		def drs = home.domainRuntimeService
		for (sr in drs.ServerRuntimes) {
			println "Server $sr.Name is $sr.State"
		}
		println "domainRuntimeService $drs.properties"
	}
	
	void testTypeService() {
		def bean = home.typeService
		assert bean.getMBeanInfo('weblogic.management.configuration.DomainMBean')
	}
	
}