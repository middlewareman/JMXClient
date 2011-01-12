/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.weblogic.shell.GWLSTBindings

class EditorTest extends GroovyTestCase {

	Map<String,?> gwlst = GWLSTBindings.bind(WebLogicMBeanHomeFactory.default, [:])

	void testValidateOnly() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		Editor.validateOnly.editDomain(gwlst.editService) {  it.Notes += '!'	 }
		assert config.Notes == confignotes
		assert pending.Notes == pendingnotes
	}

	void testSaveOnly() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		Editor.saveOnly.editDomain(gwlst.editService) {  it.Notes += '!'	 }
		assert config.Notes == confignotes
		assert pending.Notes == pendingnotes + '!'
	}

	void testActivate() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		Editor.activate.editDomain(gwlst.editService) {  it.Notes += '!'	 }
		assert config.Notes == pendingnotes + '!'
	}
}
