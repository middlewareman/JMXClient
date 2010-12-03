/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.MBean
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.builder.HtmlExporter 

if (params.objectName) {
	
	def objectName = params.objectName
	def home = RuntimeServer.localMBeanHome
	assert home
	def mbean = home.getMBean(objectName)
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
	
	def runtimeServer = RuntimeServer.localRuntimeServer
	html.html {
		head { title 'WebLogic RuntimeMBeanServer Browser' }
		body {
			h1 'WebLogic RuntimeMBeanServer Browser'
			
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
			def map = runtimeServer.getMBeanPlatformHome().properties.findAll { key, value ->
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