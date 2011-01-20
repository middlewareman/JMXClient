/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.weblogic.shell.GWLSTBindings

class EditorTest extends GroovyTestCase {

	Map<String,?> gwlst = GWLSTBindings.bind(WebLogicMBeanHomeFactory.default, [:])

	def goodScript = { it.Notes += '!' }
	def badScript = {
		it.Notes += '?'
		it.SomethingWrong = 'bad'
	}

	void testValidateOnly() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		Editor.validateOnly.editDomain(gwlst.editService, goodScript)
		assert config.Notes == confignotes
		assert pending.Notes == pendingnotes
	}

	void testValidateOnlyBad() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		try {
			Editor.validateOnly.editDomain(gwlst.editService, badScript)
			fail()
		}
		catch(Exception) {
		}
		assert config.Notes == confignotes
		assert pending.Notes == pendingnotes
	}

	void testSaveOnly() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		Editor.saveOnly.editDomain(gwlst.editService, goodScript)
		assert config.Notes == confignotes
		assert pending.Notes == pendingnotes + '!'
	}

	void testSaveOnlyBad() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		try {
			Editor.saveOnly.editDomain(gwlst.editService, badScript)
			fail()
		}
		catch(Exception) {
		}
		assert config.Notes == confignotes
		assert pending.Notes == pendingnotes
	}

	void testEditSave() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		gwlst.editSave goodScript
		assert pending.Notes == pendingnotes + '!'
	}

	void testEditBad() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		try {
			gwlst.edit badScript
			fail()
		}
		catch(Exception) {
		}
		assert config.Notes == confignotes
		assert pending.Notes == pendingnotes
	}

	void testActivateWait() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		Editor.activateWait.editDomain(gwlst.editService, goodScript)
		assert config.Notes == pendingnotes + '!'
	}

	void testActivateWaitBadScript() {
		def config = gwlst.domainRuntimeService.DomainConfiguration
		def pending = gwlst.domainRuntimeService.DomainPending
		def confignotes = config.Notes
		def pendingnotes = pending.Notes
		println "$name $confignotes $pendingnotes"
		try {
			Editor.activateWait.editDomain(gwlst.editService, badScript)
			fail()
		}
		catch(Exception) {
		}
		assert config.Notes == confignotes
		assert pending.Notes == pendingnotes
	}

	// TODO testActivateBadActivation
}
