/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.gwlst.jdbc

import java.util.List

class DataSourceConfig {

	String name
	List<String> jndiNames

	List<String> targetServerNames
	List<String> targetClusterNames

	Map dataSourceParams = [:]

	def configDomain(domain) {
		assert domain.Type == 'Domain'

		def systemResource = domain.createJDBCSystemResource(name)
		def resource = systemResource.JDBCResource
		resource.Name = name

		editResource domain, resource

		for (serverName in targetServerNames) {
			def target = domain.lookupServer(serverName)
			assert target
			systemResource.addTarget target
		}
		for (clusterName in targetClusterNames) {
			def target = domain.lookupCluster(clusterName)
			assert target
			systemResource.addTarget target
		}

		return systemResource
	}

	protected void copyProperties(Map map, GroovyObject bean) {
		for (Map.Entry param in map)
			bean.setProperty(param.key, param.value)
	}

	void editResource(domain, resource) {
		editDataSource resource.JDBCDataSourceParams
	}

	private void editDataSource(dataSourceParamsBean) {
		copyProperties dataSourceParams, dataSourceParamsBean
		jndiNames.each { dataSourceParamsBean.addTarget it }
	}
}

