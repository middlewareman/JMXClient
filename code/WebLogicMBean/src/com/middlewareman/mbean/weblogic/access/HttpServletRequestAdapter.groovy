/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.access

import java.util.logging.Level
import java.util.logging.Logger

import javax.servlet.http.HttpServletRequest

import com.middlewareman.mbean.MBeanHomeFactory
import com.middlewareman.mbean.weblogic.*


/**
 * Accesses references to MBean Servers in an HttpSession and handles connecting and disconnecting.
 * 
 * @author Andreas Nyberg
 */
class HttpServletRequestAdapter {

	static final Logger logger = Logger.getLogger(HttpServletRequestAdapter.class.name)

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

	private getattr(String name) {
		request.getSession(false)?.getAttribute name
	}

	private void setattr(String name, value) {
		request.getSession(true).setAttribute name, value
	}

	private void removeattr(String name) {
		request.getSession(false)?.removeAttribute name
	}

	RuntimeServer getRemoteRuntimeServer() {
		try {
			def rs = getattr(runtimeServerName)
			if (rs) return rs
			if (rs != null)	// broken
				removeattr runtimeServerName
		} catch(Exception e) {
			//new StackTraceCleaner().deepClean e
			def session = request.getSession(false)?.id
			logger.logp(Level.WARNING, getClass().getName(), 'getRemoteRuntimeServer', "Could not get from $session", e);
			removeattr runtimeServerName
		}
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
			removeattr runtimeServerName
	}

	RuntimeServer loginRuntimeServer() {
		def hf = homeFactory
		if (hf) {
			def rs = new RuntimeServer(hf)
			if (rs) {
				setattr runtimeServerName, rs
				return rs
			}
		}
		return null
	}

	DomainRuntimeServer getRemoteDomainRuntimeServer() {
		try {
			def drs = getattr(domainRuntimeServerName)
			if (drs) return drs
			if (drs != null)	// broken
				removeattr domainRuntimeServerName
		} catch(Exception e) {
			//new StackTraceCleaner().deepClean e
			def session = request.getSession(false)?.id
			logger.logp(Level.WARNING, getClass().getName(), 'getRemoteDomainRuntimeServer', "Could not get from $session", e);
			removeattr runtimeServerName
		}
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
		setattr domainRuntimeServerName, drs
		return drs
	}

	void logoutDomainRuntimeServer() {
		if (request.getParameter('logout'))
			removeattr domainRuntimeServerName
	}

	DomainRuntimeServer loginDomainRuntimeServer() {
		def hf = homeFactory
		if (hf) {
			def drs = new DomainRuntimeServer(hf)
			if (drs) {
				setattr domainRuntimeServerName, drs
				return drs
			}
		}
		return null
	}

	EditServer getRemoteEditServer() {
		EditServer es
		try {
			es = getattr(editServerName)
			if (es) return es
			if (es != null)	// broken
				removeattr editServerName
		} catch(Exception e) {
			//new StackTraceCleaner().deepClean e
			def session = request.getSession(false)?.id
			logger.logp(Level.WARNING, this.getClass().getName(), 'getRemoteEditServer', "Could not from $session", e);
			removeattr runtimeServerName
		}
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
		setattr editServerName, es
		return es
	}

	void logoutEditServer() {
		if (request.getParameter('logout'))
			removeattr editServerName
	}

	EditServer loginEditServer() {
		def hf = homeFactory
		if (hf) {
			def es = new EditServer(hf)
			if (es) {
				setattr editServerName, es
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
