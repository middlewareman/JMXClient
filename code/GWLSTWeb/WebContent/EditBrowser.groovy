/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.weblogic.access.HttpServletRequestAdapter
import com.middlewareman.mbean.weblogic.builder.HtmlExporter 

new ExceptionHandler(binding).wrap {
	
	def adapter = new HttpServletRequestAdapter(request)
	def editServer = adapter.editServer
	
	if (params.objectName) {
		def mbean = editServer.home.getMBean(params.objectName)
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
			head { title 'GWLST EditMBeanServer Browser' }
			body {
				h1 'GWLST EditMBeanServer Browser'
				h2 'MBeanHome'
				pre editServer.home
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
}