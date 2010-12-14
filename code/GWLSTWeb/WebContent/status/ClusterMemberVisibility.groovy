/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */ 

import com.middlewareman.mbean.weblogic.access.HttpServletRequestAdapter
import com.middlewareman.mbean.weblogic.builder.HtmlExporter

final desc = """Cluster members maintain their dynamic view of the \
cluster by receiving heartbeat messages from other cluster members. \
Because these messages are broadcast (whether with multicast or unicast), \
the failure in receiving these messages is not readily detected by the sender. \
This monitoring utility compares the view of the cluster from each cluster member \
to that which would be expected based on a list of running cluster members retrieved \
from another source."""

new ExceptionHandler(binding).wrap {
	
	def adapter = new HttpServletRequestAdapter(request)
	def domainRuntimeServer = adapter.domainRuntimeServer
	
	def drs = domainRuntimeServer.domainRuntimeService
	
	def allServerConfigNames = drs.domainConfiguration.servers.name
	def allServerRuntimes = drs.serverRuntimes
	def allServerRuntimeNames = allServerRuntimes.name
	
	html.html {
		head { title '$Id$' }
		body {
			HtmlExporter.notice delegate
			h1 'Cluster Member Visibility'
			h2 'Description'
			p desc
			for (clusterConfig in drs.domainConfiguration.clusters) {
				h2 "Cluster $clusterConfig.name"
				def clusterMemberConfigMap = [:]
				for (serverConfig in clusterConfig.servers)
					clusterMemberConfigMap[serverConfig.name] = serverConfig
				def clusterMemberConfigNames = clusterMemberConfigMap.keySet().sort()
				table(border:1) {
					tr {
						th 'Server Name'
						th 'State'
						th 'Sees'
						th 'Does not see'
					}
					for (name in clusterMemberConfigNames) {
						def clusterMemberRuntime = drs.lookupServerRuntime(name)
						def clusterRuntime = clusterMemberRuntime.clusterRuntime
						def sees = clusterRuntime.serverNames
						tr {
							td name
							td clusterMemberRuntime?.state
							td sees.join(' ')
							td '?'
						}
					}
				}
			}
			h2 'Done.'
		}
	}
}