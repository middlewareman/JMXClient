/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.shell

import com.middlewareman.mbean.weblogic.*

/**
 * Create standard bindings for GWLST script.
 * <p>Beans:
 * <ul>
 * <li><code>runtimeServer</code></li>
 * <li><code>runtimeService</code> (short for 
 * <code>runtimeServer.runtimeService</code>)</li>
 * <li><code>domainRuntimeServer</code></li>
 * <li><code>domainRuntimeService</code> (short for 
 * <code>domainRuntimeServer.domainRuntimeService</code>)</li>
 * <li><code>editServer</code></li>
 * <li><code>editService</code> (short for 
 * <code>editServer.editService</code>)</li>
 * </ul>
 * </p>
 * <p>Closures:
 * <ul>
 * <li><code>String status()</code></li>
 * <li><code>edit(Closure script)</code> that passes the pending domain MBean 
 * as the parameter to the script</li>
 * </ul>
 * </p>
 * 
 * @author Andreas Nyberg
 */
class GWLSTBindings {

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
				def address = serverRuntime.@home.serverId
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
		target.domainRuntimeServer = null
		target.domainRuntimeService = null
		target.editServer = null
		target.editService = null
		if (isAdminServer) {
			def newDomainRuntimeServer = new DomainRuntimeServer(hf)
			def newDomainRuntimeService = newDomainRuntimeServer.domainRuntimeService
			if (newDomainRuntimeService) {
				target.domainRuntimeServer = newDomainRuntimeServer
				target.domainRuntimeService = newDomainRuntimeService
			} else {
				// TODO Warning?
			}
			def newEditServer = new EditServer(hf)
			def newEditService = newEditServer.editService
			if (newEditService) {
				target.editServer = newEditServer
				target.editService = newEditService
			} else {
				// TODO Warning?
			}
		}
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

