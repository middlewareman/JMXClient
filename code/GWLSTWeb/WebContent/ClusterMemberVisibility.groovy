/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */ 

import com.middlewareman.mbean.MBean
import com.middlewareman.mbean.weblogic.access.HttpServletRequestAdapter 
import com.middlewareman.mbean.weblogic.builder.HtmlExporter 

def desc = """Cluster members maintain their dynamic view of the \
cluster by receiving heartbeat messages from other cluster members. \
Because these messages are broadcast (whether with multicast or unicast), \
the failure in receiving these messages is not readily detected by the sender. \
This monitoring utility compares the view of the cluster from each cluster member \
to that which would be expected based on a list of running cluster members retrieved \
from another source."""

def show(collection) {
	collection ? collection.sort().join(' ') : ''
}

void browse(MBean mbean, text) {
	if (mbean) html.a(href:"DomainRuntimeBrowser.groovy?objectName=${mbean.@objectName}", text)
}

new ExceptionHandler(binding).wrap {
	
	def adapter = new HttpServletRequestAdapter(request)
	def domainRuntimeServer = adapter.domainRuntimeServer
	
	def drs = domainRuntimeServer.domainRuntimeService
	
	Set allServerConfigNames = drs.domainConfiguration.servers.name as Set
	def allServerRuntimes = drs.serverRuntimes
	Set allServerRuntimeNames = allServerRuntimes.name as Set
	
	html.html {
		head { title 'GWLST Cluster Member Visibility' }
		body {
			HtmlExporter.notice delegate
			h1 'Cluster Member Visibility'
			h2 'Description'
			p desc
			for (clusterConfig in drs.domainConfiguration.clusters) {
				h2 "Cluster $clusterConfig.name"
				Map clusterMemberConfigMap = [:]
				for (serverConfig in clusterConfig.servers)
					clusterMemberConfigMap[serverConfig.name] = serverConfig
				Set clusterMemberConfigNames = clusterMemberConfigMap.keySet()
				Set clusterMemberRuntimeNames = allServerRuntimeNames.intersect(clusterMemberConfigNames)
				h3 'Overview'
				table(border:1) {
					tr {
						td 'Configured members'
						td show(clusterMemberConfigNames)
					}
					tr {
						td 'Running members'
						td show(clusterMemberRuntimeNames)
					}
					tr {
						td 'Not running members'
						td show(clusterMemberConfigNames - clusterMemberRuntimeNames)
					}
				}
				
				def dodgyServerNames = new HashSet()
				h3 'Per configured cluster member'
				table(border:1) {
					tr {
						th 'Cluster member'
						th 'State'
						th 'Sees'
						th 'Should also see'
					}
					for (name in clusterMemberConfigNames?.sort()) {
						def clusterMemberRuntime = drs.lookupServerRuntime(name)
						if (clusterMemberRuntime) {
							def clusterRuntime = clusterMemberRuntime.clusterRuntime
							Set sees = clusterRuntime.serverNames as Set
							Set shouldAlsoSee = clusterMemberRuntimeNames - sees
							dodgyServerNames += shouldAlsoSee
							tr {
								td name
								td clusterMemberRuntime.state
								td show(sees)
								td { strong show(shouldAlsoSee) }
							}
						} else {
							tr {
								td name
								td { i 'unavailable' }
								td()
								td()
							}
						}
					}
				}
				
				if (dodgyServerNames) {
					h3 'Dodgy servers'
					a href:"DomainRuntimeBrowser.groovy?objectName=$clusterConfig.@objectname", 'Cluster configuration'
					for (name in dodgyServerNames?.sort()) {
						def serverConfig = drs.findServerConfiguration(name)
						def serverRuntime = drs.lookupServerRuntime(name)
						def clusterRuntime = serverRuntime?.clusterRuntime
						h5 name
						ul {
							li { browse serverConfig, "$name config" }
							li { browse serverRuntime, "$name runtime" }
							li { browse clusterRuntime, "$name's cluster runtime" }
						}
					}
				}
			}
			h2 'Done'
		}
	}
}
	