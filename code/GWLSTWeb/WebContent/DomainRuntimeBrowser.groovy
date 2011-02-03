/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.MBean
import com.middlewareman.mbean.weblogic.access.HttpServletRequestAdapter
import com.middlewareman.mbean.weblogic.builder.HtmlExporter

new ExceptionHandler(binding).wrap {

	def adapter = new HttpServletRequestAdapter(request)
	def domainRuntimeServer = adapter.domainRuntimeServer

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

	} else if (params.interfaceClassName) {

		def typeService = domainRuntimeServer.typeService
		assert typeService
		def interfaceClassName = params.interfaceClassName
		def info = typeService.getMBeanInfo(interfaceClassName)
		assert info
		def subtypes = typeService.getSubtypes(interfaceClassName)

		response.bufferSize = 350000
		def htmlExporter = new HtmlExporter(response.writer)

		// TODO any additional parameters or preferences

		htmlExporter.mbean interfaceClassName, info, subtypes

	} else {

		def server = domainRuntimeServer
		def service = server.domainRuntimeService
		def adminServerName = service.DomainConfiguration.AdminServerName
		def serverNames = service.ServerRuntimes.Name
		html.html {
			head { title 'GWLST DomainRuntimeMBeanServer Browser' }
			body {
				h1 'GWLST DomainRuntimeMBeanServer Browser'
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
					def jmx = domainRuntimeServer.domainRuntimeService.DomainConfiguration.JMX
					enabled = jmx.PlatformMBeanServerEnabled
					used = jmx.PlatformMBeanServerUsed	// Only from 10.3.3 ?
				}
				catch(Exception e) {
					context.log "Could not get DomainConfiguration.JMX.PlatformMBeanServer{Enabled,Used}: e.message"
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
						if (mbeans) {
							a(href:"?objectName=$objectName") { pre objectName }
						} else {
							pre objectName
						}
					} else if (mbeans instanceof Collection<MBean>) {
						ul {
							mbeans.each { mbean ->
								def objectName = mbean.@objectName
								li {
									if (mbean) {
										a(href:"?objectName=$objectName") { pre objectName }
									} else {
										pre objectName
									}
								}
							}
						}
					}
				}
			}
		}
	}
}