package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.type.CompositeDataWrapper 
import com.middlewareman.mbean.type.MBeanDescriber 
import groovy.util.GroovyTestCase
import groovy.util.IndentPrinter;

import javax.management.MBeanAttributeInfo 
import javax.management.MBeanInfo 
import javax.management.MBeanOperationInfo 
import javax.management.MBeanParameterInfo 

class RuntimeServerHomeTest extends GroovyTestCase {
	
	final RuntimeServerHome home
	MBeanDescriber d = new MBeanDescriber()
	
	RuntimeServerHomeTest() {
		home = new RuntimeServerHome(
				new DefaultMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1'))
	}
	
	void testRuntimeService() {
		def rs = home.runtimeService
		def healthState = rs.ServerRuntime.HealthState
		println healthState.dump()
		println healthState
		assert rs.DomainConfiguration.Type == 'Domain'
		println "$name: servers $rs.domainConfiguration.servers.name"
		println "runtimeService $rs.properties"
		println "Current domain attributes:"
		rs.DomainConfiguration.properties.each { k,v -> println "  $k\n    $v" }
	}
	
	void testTypeService() {
		def bean = home.typeService
		assert bean.getMBeanInfo('weblogic.management.configuration.DomainMBean')
	}
	
}