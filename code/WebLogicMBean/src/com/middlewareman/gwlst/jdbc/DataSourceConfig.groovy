/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.gwlst.jdbc

/**
 * A datasource is either SimpleDataSource or MultiDataSource.
 * It has a name, JNDI names and targets.
 * 
 * @author Andreas Nyberg
 */
class DataSourceConfig {

	String name
	Collection<String> jndiNames = new LinkedHashSet()
	Collection<String> targetServerNames = new LinkedHashSet()
	Collection<String> targetClusterNames = new LinkedHashSet()

	Map dataSourceParams = [:]

	/**
	 * Create a new JDBCSystemResource in the domain.
	 * @param domain
	 * @return the new JDBCSystemResource
	 */
	def configureDomain(domain) {
		assert domain.Type == 'Domain'

		def systemResource = domain.createJDBCSystemResource(name)
		def resource = systemResource.JDBCResource
		resource.Name = name

		configureResource domain, resource

		for (serverName in targetServerNames) {
			def target = domain.lookupServer(serverName)
			assert target, "target server $serverName not found"
			systemResource.addTarget target
		}
		for (clusterName in targetClusterNames) {
			def target = domain.lookupCluster(clusterName)
			assert target, "target cluster $clusterName not found"
			systemResource.addTarget target
		}

		return systemResource
	}

	/**
	 * Configure an existing JDBCResource (in JDBCSystemResource). 
	 * @param domain
	 * @param resource JDBCSystemResource
	 */
	void configureResource(domain, resource) {
		configureDataSource resource.JDBCDataSourceParams
	}

	private void configureDataSource(dataSourceParamsBean) {
		copyProperties dataSourceParams, dataSourceParamsBean
		jndiNames.each { dataSourceParamsBean.addJNDIName it }
	}

	protected void copyProperties(Map map, GroovyObject bean) {
		for (Map.Entry param in map)
			bean.setProperty(param.key, param.value)
	}
}

