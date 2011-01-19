/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.gwlst.jdbc

class SimpleDataSourceConfig extends DataSourceConfig {

	String user
	String password

	Map driverParams = [:]
	Map connectionPoolParams = [:]

	private void editDriverParams(driverParamsBean) {
		copyProperties driverParams, driverParamsBean
		driverParamsBean.Properties.createProperty('user').Value = user
		driverParamsBean.Password = password
	}

	void editResource(domain, resource) {
		super.editResource(domain,resource)
		editDriverParams(resource.JDBCDriverParams)
		copyProperties connectionPoolParams, resource.JDBCConnectionPoolParams
	}


	void setOracle() {
		connectionPoolParams.TestTableName = 'SQL SELECT 1 FROM DUAL'
	}

	void setOracleNonXA() {
		setOracle()
		driverName = 'oracle.jdbc.OracleDriver'
	}
	
	void setOracle2PC() {
		setOracle()
		driverName = 'oracle.jdbc.xa.client.OracleXADataSource'
		globalTransactionsProtocol = 'TwoPhaseCommit'
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
}
