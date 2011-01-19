/*
* $Id$
* Copyright (c) 2011 Middlewareman Limited. All rights reserved.
*/
package com.middlewareman.gwlst.jdbc

class DataSourceConfig {

	void setOracle() {
		connectionPoolParams.TestTableName = 'SQL SELECT 1 FROM DUAL'
	}

	void setOracleNonXA() {
		setOracle()
		driverName = 'oracle.jdbc.OracleDriver'
	}

	void setOracleEmulated() {
		setOracleNonXA()
		globalTransactionsProtocol = 'EmulateTwoPhaseCommit'
	}


	void setUrl(String value) {
		driverParams.Url = value
	}

	void setDriverName(String value) {
		driverParams.DriverName = value
	}

	void setGlobalTransactionsProtocol(String value) {
		dataSourceParams.GlobalTransactionsProtocol = value
	}

	void setRowPrefetch(boolean value) {
		dataSourceParams.RowPrefetch = value
	}

	void setMaxCapacity(int value) {
		connectionPoolParams.MaxCapacity = value
	}

	void setTestConnectionsOnReserve(boolean value) {
		connectionPoolParams.TestConnectionsOnReserve = value
	}

	String name

	String user
	String password

	Map connectionPoolParams = [:]
	Map dataSourceParams = [:]
	Map driverParams = [:]

	def configure(domain) {
		assert domain.Type == 'Domain'

		def systemResource = domain.createJDBCSystemResource(name)
		def resource = systemResource.JDBCResource
		resource.Name = name

		def jdbcDriverParams = resource.JDBCDriverParams
		for (param in driverParams)
			jdbcDriverParams."$param.key" = param.value
		jdbcDriverParams.Properties.createProperty('user').Value = user
		jdbcDriverParams.Password = password

		def jdbcConnectionPoolParams = resource.JDBCConnectionPoolParams
		for (param in connectionPoolParams)
			jdbcConnectionPoolParams."$param.key" = param.value


		def jdbcDataSourceParams = resource.JDBCDataSourceParams
		for (param in dataSourceParams)
			jdbcDataSourceParams."$param.key" = param.value
		
		return systemResource
	}
}

