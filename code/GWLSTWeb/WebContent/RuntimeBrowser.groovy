/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.weblogic.access.HttpServletRequestAdapter
import com.middlewareman.mbean.weblogic.builder.HtmlExporter 

new ExceptionHandler(binding).wrap {
	
	def adapter = new HttpServletRequestAdapter(request)
	def runtimeServer = adapter.runtimeServer
	
	if (params.objectName) {
		def home = runtimeServer.home
		assert home
		def mbean = home.getMBean(params.objectName)
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
		
		html.html {
			head { title 'GWLST RuntimeMBeanServer Browser' }
			body {
				h1 'GWLST RuntimeMBeanServer Browser'
				h2 'MBeanHome'
				pre runtimeServer.home
				h2 'WebLogic Services'
				for (name in [
					'runtimeService',
					'typeService'
				]) {
					def mbean = runtimeServer."$name"
					def objectName = mbean.@objectName
					h3 name
					a(href:"?objectName=$objectName") { pre objectName }
				}
				
				h2 'Java Platform MXBeans'
				def used = null
				def enabled = null
				try {
					def jmx = runtimeServer.runtimeService.DomainConfiguration.JMX
					enabled = jmx.PlatformMBeanServerEnabled
					used = jmx.PlatformMBeanServerUsed	// Only from 10.3.3 ?
				}
				catch(Exception e) {
					context.log "Could not get DomainConfiguration.JMX.PlatformMBeanServer{Enabled,Used}: $e.message"
				}
				if (!enabled && !used) {
					i 'PlatformMBeanServer probably needs to be enabled in your domain configuration to see the beans below.'
				}
				
				def map = runtimeServer.getMBeanPlatformHome().properties.findAll { key, value ->
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