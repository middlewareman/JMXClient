package com.middlewareman.mbean.weblogic

import groovy.util.GroovyTestCase


class RuntimeServerTest extends GroovyTestCase {
	
	final RuntimeServer home
	
	RuntimeServerTest() {
		home = new RuntimeServer(WebLogicMBeanHomeFactory.default)
	}
	
	void testRuntimeService() {
		def rs = home.runtimeService
		def healthState = rs.ServerRuntime.HealthState
		println healthState.dump()
		println healthState
		assert rs.DomainConfiguration.Type == 'Domain'
		println "$name: servers $rs.DomainConfiguration.servers.name"
		println "runtimeService $rs.properties"
	}
	
	void testTypeService() {
		def bean = home.typeService
		assert bean.getMBeanInfo('weblogic.management.configuration.DomainMBean')
	}
}