/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

class EditServerTest extends GroovyTestCase {

	final EditServer editServer = new EditServer(WebLogicMBeanHomeFactory.default)
	
	void testEditService() {
		def es = editServer.editService
		assert es
		println "editService"
		es.properties.each { k, v -> println "  $k:\t$v" }
	}
	
}
