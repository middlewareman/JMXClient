/*
 * $Id$
 * Copyright � 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.weblogic.EditServer 
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 
import com.middlewareman.mbean.weblogic.builder.HtmlExporter 


assert request.getSession(true)

def getEditServer() {
	def es = session?.editServer
	if (!es) {
		def rs = RuntimeServer.localRuntimeServer.runtimeService
		def adminUrl = rs.ServerRuntime.AdministrationURL
		def mf = new WebLogicMBeanHomeFactory(url:adminUrl)
		es = new EditServer(mf)
		assert es
		request.getSession(true).editServer = es
	}
	return es
}

if (params.objectName) {
	
	assert editServer
	def mbean = editServer.home.getMBean(params.objectName)
	assert mbean
	
	def htmlExporter = new HtmlExporter(html:html)
	// TODO any additional parameters or preferences
	
	def timestamp = new Date()
	def extras = [
				'URL':request.requestURL,
				'Timestamp':timestamp,
				'user':request.remoteUser,
				'principal':request.userPrincipal]
	htmlExporter.mbean mbean, extras
	
} else { 
	
	html.html {
		head { title 'WebLogic EditMBeanServer Browser' }
		body {
			h1 'WebLogic EditMBeanServer Browser'
			h2 'WebLogic Services'
			for (name in [
				'editService',
				'typeService'
			]) {
				def mbean = editServer."$name"
				def objectName = mbean.@objectName
				h3 name
				a(href:"?objectName=$objectName") { pre objectName }
			}
		}
	}
}