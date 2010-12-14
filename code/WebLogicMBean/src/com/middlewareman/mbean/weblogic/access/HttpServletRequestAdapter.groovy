/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.access

import com.middlewareman.mbean.MBeanHomeFactory 
import com.middlewareman.mbean.weblogic.DomainRuntimeServer 
import com.middlewareman.mbean.weblogic.EditServer 
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 
import javax.servlet.http.HttpServletRequest 

/**
 * Accesses references to MBean Servers in an HttpSession and handles connecting and disconnecting.
 * 
 * @author Andreas Nyberg
 */
class HttpServletRequestAdapter {
	
	static final String runtimeServerName = 'runtimeServer'
	static final String domainRuntimeServerName = 'domainRuntimeServer'
	static final String editServerName = 'editServer'
	
	private HttpServletRequest request
	Boolean reconnect = true
	Long timeout = 10000
	
	HttpServletRequestAdapter(HttpServletRequest request) {
		assert request
		this.request = request
	}
	
	private MBeanHomeFactory getHomeFactory() {
		String value = request.getParameter('url')
		if (!value) return null
		def hf = new WebLogicMBeanHomeFactory()
		hf.url = value
		if ((value = request.getParameter('username'))) 
			hf.username = value
		if ((value = request.getParameter('password')))
			hf.password = value
		hf.reconnect = reconnect
		hf.timeout = timeout
		return hf
	}
	
	private get(String name) {
		request.getSession(false)?.getAttribute name
	}
	
	private void set(String name, value) {
		request.getSession(true).setAttribute name, value
	}
	
	private void remove(String name) {
		request.getSession(false)?.removeAttribute name
	}
	
	RuntimeServer getRemoteRuntimeServer() {
		def rs = get(runtimeServerName)
		if (rs) return rs
		if (rs != null)	// broken
			remove runtimeServerName
		return null
	}
	
	RuntimeServer getRuntimeServer() {
		def rs = remoteRuntimeServer
		if (rs) return rs
		rs = RuntimeServer.localRuntimeServer
		assert rs
		return rs
	}
	
	void logoutRuntimeServer() {
		if (request.getParameter('logout'))
			remove runtimeServerName
	}
	
	RuntimeServer loginRuntimeServer() {
		def hf = homeFactory
		if (hf) {
			def rs = new RuntimeServer(hf)
			if (rs) {
				set runtimeServerName, rs
				return rs
			}
		}
		return null
	}
	
	DomainRuntimeServer getRemoteDomainRuntimeServer() {
		def drs = get(domainRuntimeServerName)
		if (drs) return drs
		if (drs != null)	// broken
			remove domainRuntimeServerName
		return null
	}
	
	DomainRuntimeServer getDomainRuntimeServer() {
		def drs = remoteDomainRuntimeServer
		if (drs) return drs
		drs = DomainRuntimeServer.localDomainRuntimeServer
		if (drs) return drs
		def runtimeService = runtimeServer.runtimeService
		def adminUrl = runtimeService.ServerRuntime.AdministrationURL
		def mbhf = new WebLogicMBeanHomeFactory(url:adminUrl, reconnect:reconnect)
		drs = new DomainRuntimeServer(mbhf)
		assert drs
		set domainRuntimeServerName, drs
		return drs
	}
	
	void logoutDomainRuntimeServer() {
		if (request.getParameter('logout'))
			remove domainRuntimeServerName
	}
	
	DomainRuntimeServer loginDomainRuntimeServer() {
		def hf = homeFactory
		if (hf) {
			def drs = new DomainRuntimeServer(hf)
			if (drs) {
				set domainRuntimeServerName, drs
				return drs
			}
		}
		return null
	}
	
	EditServer getRemoteEditServer() {
		def es = get(editServerName)
		if (es) return es
		if (es != null)	// broken
			remove editServerName
		return null
	}
	
	EditServer getEditServer() {
		def es = remoteEditServer
		if (es) return es
		def runtimeService = runtimeServer.runtimeService
		def adminUrl = runtimeService.ServerRuntime.AdministrationURL
		def mbhf = new WebLogicMBeanHomeFactory(url:adminUrl, reconnect:reconnect)
		es = new EditServer(mbhf)
		assert es
		set editServerName, es
		return es
	}
	
	void logoutEditServer() {
		if (request.getParameter('logout'))
			remove editServerName
	}
	
	EditServer loginEditServer() {
		def hf = homeFactory
		if (hf) {
			def es = new EditServer(hf)
			if (es) {
				set editServerName, es
				return es
			}
		}
		return null
	}
	
	void logoutAll() {
		logoutRuntimeServer()
		logoutDomainRuntimeServer()
		logoutEditServer()
	}
	
	void loginAll() {
		loginRuntimeServer()
		loginDomainRuntimeServer()
		loginEditServer()
	}
}
