/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.shell

import com.middlewareman.mbean.weblogic.*

/**
 * Create standard bindings for GWLST script.
 * 
 * @author Andreas Nyberg
 */
class GWLSTBindings {

	static help = """\
Bindings for any server:
  status()
  runtimeServer
  runtimeService                       (runtimeServer.RuntimeService)
  serverConfig                         (runtimeService.ServerConfiguration)
  serverRuntime                        (runtimeService.ServerRuntime)

Bindings for admin server:
  domainRuntimeServer
  domainRuntimeService                 (domainRuntimeServer.domainRuntimeService)
  domainConfig                         (domainRuntimeService.DomainConfiguration)
  domainRuntime                        (domainRuntimeService.DomainRuntime)
  editServer
  editService                          (short for editServer.editService)
  editValidate { domain -> }           Configure domain and validate only
  editSave { domain -> }               Configure domain and save
  editActivateWait { domain -> }       Configure domain and activate synchronously
  editActivateNoWait { domain -> }     Configure domain and activate asynchronously
"""
	/**
	 * Returns a string describing the runtimeService.
	 */
	static String status(def runtimeService) {
		if (runtimeService) {
			try {
				def serverRuntime = runtimeService.ServerRuntime
				def serverName = runtimeService.ServerName
				def serverType = serverRuntime.AdminServer ? 'admin' : 'managed'
				def serverState = serverRuntime.State
				def address = serverRuntime.@home.address
				return "Connected to $serverName ($serverType) $serverState on $address"
			} catch (Exception e) {
				return e.message
			}
		} else {
			return 'Not connected'
		}
	}

	/**
	 * Create standard bindings for GWLST script. 
	 * 
	 * @param hf
	 * @param target anything that accepts having properties assigned to it
	 * such as a {@link Bindings} or a {@link Map}.
	 * @return the same target.
	 */
	static bind(WebLogicMBeanHomeFactory hf, target) {
		def newRuntimeServer = new RuntimeServer(hf)
		newRuntimeServer.home.ping()
		def newRuntimeService = newRuntimeServer.runtimeService
		def isAdminServer = newRuntimeService.ServerRuntime.AdminServer
		/* If we got this far we have managed to connect! */
		target.runtimeServer = newRuntimeServer
		target.runtimeService = newRuntimeService
		target.serverConfig = newRuntimeService.ServerConfiguration
		target.serverRuntime = newRuntimeService.ServerRuntime
		if (isAdminServer) {
			def newDomainRuntimeServer = new DomainRuntimeServer(hf)
			def newDomainRuntimeService = newDomainRuntimeServer.domainRuntimeService
			if (newDomainRuntimeService) {
				target.domainRuntimeServer = newDomainRuntimeServer
				target.domainRuntimeService = newDomainRuntimeService
				target.domainConfig = newDomainRuntimeService.DomainConfiguration
				target.domainRuntime = newDomainRuntimeService.DomainRuntime
			} else {
				// TODO Warning?
				target.domainRuntimeServer = null
				target.domainRuntimeService = null
				target.domainConfig = null
				target.domainRuntime = null
			}
			def newEditServer = new EditServer(hf)
			def newEditService = newEditServer.editService
			if (newEditService) {
				target.editServer = newEditServer
				target.editService = newEditService
			} else {
				// TODO Warning?
				target.editServer = null
				target.editService = null
			}
		}
		target.help = help
		target.status = { status(target.runtimeService) }
		target.editValidate = { script ->
			Editor.validateOnly.editDomain(target.editService, script)
		}
		target.editSave = { script ->
			Editor.saveOnly.editDomain(target.editService, script)
		}
		target.editActivateWait = { script ->
			Editor.activateWait.editDomain(target.editService, script)
		}
		target.editActivateNoWait = { script ->
			Editor.activateNoWait.editDomain(target.editService, script)
		}

		return target
	}
}

