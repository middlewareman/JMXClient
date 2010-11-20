package com.middlewareman.mbean.weblogic

import groovy.util.GroovyTestCase

class DomainRuntimeServerTest extends GroovyTestCase {
	
	final DomainRuntimeServer home
	
	DomainRuntimeServerTest() {
		home = new DomainRuntimeServer(WebLogicMBeanHomeFactory.default)
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