/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.weblogic.access.HttpServletRequestAdapter
import com.middlewareman.util.ThreadDumpAnalyzer

new ExceptionHandler(binding).wrap {

	def adapter = new HttpServletRequestAdapter(request)
	def domainRuntimeServer = adapter.domainRuntimeServer

	def serverName = params.serverName

	if (serverName) {

		def serverRuntime = domainRuntimeServer.domainRuntimeService.lookupServerRuntime(serverName)
		assert serverRuntime
		def jvm = serverRuntime.JVMRuntime
		String text = jvm.ThreadStackDump
		def tda = new ThreadDumpAnalyzer()
		tda.parse text
		response.contentType = 'text/plain'
		out.println serverRuntime
		out.println serverRuntime.@home
		out.println new Date()
		out.println "FreeCur\tFree%\tSizeCur\tSizeMax"
		out.println "${jvm.HeapFreeCurrent>>20} MB\t${jvm.HeapFreePercent}%\t${jvm.HeapSizeCurrent>>20} MB\t${jvm.HeapSizeMax>>20} MB"
		out.println()
		tda.report out
		out << '\n'
		out << '*' * 80
		out << '\n\n'
		out << text
	} else {

		def server = domainRuntimeServer
		def service = server.domainRuntimeService
		def adminServerName = service.DomainConfiguration.AdminServerName
		def serverNames = service.ServerRuntimes.Name
		html.html {
			head { title 'GWLST DomainRuntimeMBeanServer ThreadStackDump' }
			body {
				h1 'GWLST DomainRuntimeMBeanServer ThreadStackDump'
				h2 'MBeanHome'
				pre domainRuntimeServer.home

				h2 "Select server runtime"
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
						for (name in serverNames) {
							option name
						}
					}
					input type:'submit'
				}
			}
		}
	}
}