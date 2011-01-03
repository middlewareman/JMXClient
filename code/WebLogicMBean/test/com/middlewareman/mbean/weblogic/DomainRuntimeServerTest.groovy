/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import groovy.util.GroovyTestCase

class DomainRuntimeServerTest extends GroovyTestCase {
	
	final DomainRuntimeServer domainRuntimeServer = new DomainRuntimeServer(WebLogicMBeanHomeFactory.default)
	
	void testDomainRuntimeService() {
		def drs = domainRuntimeServer.domainRuntimeService
		for (sr in drs.ServerRuntimes) {
			println "Server $sr.Name is $sr.State"
		}
		println "domainRuntimeService $drs.properties"
	}
	
	void testTypeService() {
		def bean = domainRuntimeServer.typeService
		assert bean.getMBeanInfo('weblogic.management.configuration.DomainMBean')
	}
}