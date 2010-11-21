package com.middlewareman.mbean.weblogic;

import groovy.util.GroovyTestCase;

class EditServerTest extends GroovyTestCase {

	final EditServer editServer = new EditServer(WebLogicMBeanHomeFactory.default)
	
	void testEditService() {
		def es = editServer.editService
		assert es
		println "editService"
		es.properties.each { k, v -> println "  $k:\t$v" }
	}
	
}
