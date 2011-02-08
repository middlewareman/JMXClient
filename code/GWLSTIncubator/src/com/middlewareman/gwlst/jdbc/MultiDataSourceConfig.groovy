/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.gwlst.jdbc

/**
 * MultiDataSource is a DataSource that merely provides a list of other 
 * datasources and settings to choose from these.
 * 
 * @author Andreas Nyberg
 */
class MultiDataSourceConfig extends DataSourceConfig {

	List<DataSourceConfig> memberConfigs

	void setAlgorithmType(String value) {
		dataSourceParams.AlgorithmType = value
	}

	void configureResource(domain, resource) {
		assert domain.Type == 'Domain'
		def members = memberConfigs.collect { it.configureDomain(domain) }
		assert members.every()
		dataSourceParams.DataSourceList = members.Name.join(',')
		super.configureResource(domain, resource)
	}
}
