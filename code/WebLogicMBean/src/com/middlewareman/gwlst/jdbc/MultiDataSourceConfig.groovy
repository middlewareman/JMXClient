/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.gwlst.jdbc

class MultiDataSourceConfig {

	String name
	List<String> targetServerNames = []
	String algorithmType

	List<DataSourceConfig> memberConfigs

	def configure(domain) {
		assert domain.Type == 'Domain'

		println "Creating members $memberConfigs"
		def members = memberConfigs.collect { it.configure(domain) }
		println "Created members $members"
		assert members.every()

		def systemResource = domain.createJDBCSystemResource(name)
		def resource = systemResource.JDBCResource
		resource.Name = name

		def jdbcDataSourceParams = resource.JDBCDataSourceParams
		if (algorithmType) jdbcDataSourceParams.AlgorithmType = algorithmType
		jdbcDataSourceParams.DataSourceList = members.Name.join(',')

		def targets = targetServerNames.collect { domain.lookupServer it }
		println "Targeting $members to $targets"
		members.each { member ->
			targets.each { target ->  member.addTarget target }
		}
		
		return systemResource
	}
}
