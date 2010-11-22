package com.middlewareman.mbean.weblogic.shell

import com.middlewareman.mbean.weblogic.DomainRuntimeServer 
import com.middlewareman.mbean.weblogic.EditServer 
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory;

class GWLSTBindings {
	
	static bind(WebLogicMBeanHomeFactory hf, def target) {
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
		// TODO convenience closures
		target.status = {
			if (target.runtimeServer) {
				try {
					def serverRuntime = target.runtimeService.ServerRuntime
					def serverName = target.runtimeService.ServerName
					def serverType = serverRuntime.AdminServer ? 'admin' : 'managed'
					def serverState = serverRuntime.State
					def address = serverRuntime.@home.url
					return "Connected to $serverName ($serverType) $serverState on $address"
				} catch (Exception e) {
					return e.message
				}
			} else {
				return 'Not connected'
			}
		}
	}
}
