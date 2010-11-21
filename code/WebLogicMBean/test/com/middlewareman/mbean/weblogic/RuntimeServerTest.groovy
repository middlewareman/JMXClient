package com.middlewareman.mbean.weblogic

import groovy.util.GroovyTestCase


class RuntimeServerTest extends GroovyTestCase {
	
	final RuntimeServer runtimeServer = new RuntimeServer(WebLogicMBeanHomeFactory.default)
	
	void testRuntimeService() {
		def rs = runtimeServer.runtimeService
		def healthState = rs.ServerRuntime.HealthState
		println healthState.dump()
		println healthState
		assert rs.DomainConfiguration.Type == 'Domain'
		println "$name: servers $rs.DomainConfiguration.servers.name"
		println "runtimeService $rs.properties"
	}
	
	void testTypeService() {
		def bean = runtimeServer.typeService
		assert bean.getMBeanInfo('weblogic.management.configuration.DomainMBean')
	}
}