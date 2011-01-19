/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.gwlst.jdbc

class MultiDataSourceConfig extends DataSourceConfig {

	void setAlgorithmType(String value) {
		dataSourceParams.AlgorithmType = value
	}

	List<DataSourceConfig> memberConfigs

	void editResource(domain, resource) {
		assert domain.Type == 'Domain'

		println "Creating members $memberConfigs"
		def members = memberConfigs.collect { it.configDomain(domain) }
		println "Created members $members"
		assert members.every()

		dataSourceParams.DataSourceList = members.Name.join(',')

		super.editResource(domain, resource)
	}
}
