/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.weblogic.DomainRuntimeServer 
import com.middlewareman.mbean.weblogic.builder.HtmlExporter 

/* Pick up RuntimeServer from session or use local. */
// TODO Cache instance within page?
DomainRuntimeServer getDomainRuntimeServer() {
	def session = request.getSession(false)
	session?.domainRuntimeServer ?: DomainRuntimeServer.localRuntimeServer
}

if (params.objectName) {
	def objectName = params.objectName
	def home = domainRuntimeServer.home
	assert home
	def mbean = home.getMBean(objectName)
	assert mbean
	
	response.bufferSize = 350000
	def htmlExporter = new HtmlExporter(response.writer)
	
	// TODO any additional parameters or preferences
	
	def timestamp = new Date()
	def extras = [
				'URL':request.requestURL,
				'Timestamp':timestamp,
				'user':request.remoteUser, 
				'principal':request.userPrincipal]
	htmlExporter.mbean mbean, extras
	
} else {
	
	def server = domainRuntimeServer
	def service = server.domainRuntimeService
	def adminServerName = service.DomainConfiguration.AdminServerName
	def serverNames = service.ServerRuntimes.Name 
	html.html {
		head { title 'GWLST RuntimeMBeanServer Browser' }
		body {
			h1 'GWLST RuntimeMBeanServer Browser'
			h2 'MBeanHome'
			pre domainRuntimeServer.home
			h2 'WebLogic Services'
			for (name in [
				'domainRuntimeService',
				'typeService'
			]) {
				def mbean = server."$name"
				def objectName = mbean.@objectName
				h3 name
				a(href:"?objectName=$objectName") { pre objectName }
			}
			
			def selectedServerName = params.serverName ?: adminServerName
			h2 "Java Platform MXBeans ($selectedServerName)"
			def used = null
			def enabled = null
			try {
				def jmx = runtimeServer.runtimeService.DomainConfiguration.JMX
				enabled = jmx.PlatformMBeanServerEnabled
				used = jmx.PlatformMBeanServerUsed	// Only from 10.3.3 ?
			}
			catch(Exception e) {
				context.log "Could not get DomainConfiguration.JMX.PlatformMBeanServer{Enabled,Used}", e
			}
			if (!enabled && !used) {
				i 'PlatformMBeanServer probably needs to be enabled in your domain configuration to see the beans below.'
			}
			
			form {
				select(name:'serverName') {
					for (serverName in serverNames) {
						if (serverName == selectedServerName) {
							option selected:serverName, serverName
						} else {
							option serverName
						}
					}
				}
				input type:'submit'
			}
			def map = server.getMBeanPlatformHome(selectedServerName).properties.findAll { key, value ->
				value instanceof MBean || value instanceof Collection<MBean>
			}
			map.each { name, mbeans -> 
				h3 name
				if (mbeans instanceof MBean) {
					def objectName = mbeans.@objectName
					a(href:"?objectName=$objectName") { pre objectName }
				} else if (mbeans instanceof Collection<MBean>) {
					ul {
						mbeans.each { mbean ->
							def objectName = mbean.@objectName
							li {
								a(href:"?objectName=$objectName") { pre objectName }
							}
						}
					}
				}
			}
		}
	}
}