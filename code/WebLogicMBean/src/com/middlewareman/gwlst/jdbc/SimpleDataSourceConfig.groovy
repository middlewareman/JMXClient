/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.gwlst.jdbc

/**
 * SimpleDataSource (as opposed to MultiDataSource) has a driver and connection
 * pool. A subclass for each database vendor or major configuration provides
 * settings such as drivers and test table.
 * 
 * @author Andreas Nyberg
 */
abstract class SimpleDataSourceConfig extends DataSourceConfig {

	String user
	String password

	Map driverParams = [:]
	Map connectionPoolParams = [:]

	private void configureDriverParams(driverParamsBean) {
		copyProperties driverParams, driverParamsBean
		driverParamsBean.Properties.createProperty('user').Value = user
		driverParamsBean.Password = password
	}

	void configureResource(domain, resource) {
		super.configureResource(domain,resource)
		configureDriverParams(resource.JDBCDriverParams)
		copyProperties connectionPoolParams, resource.JDBCConnectionPoolParams
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

	abstract void setNonXADriver();
	abstract void setXADriver();
	
	void setTwoPhaseCommit() {
		setXADriver()
		globalTransactionsProtocol = 'TwoPhaseCommit'
	}

	void setLoggingLastResource() {
		setNonXADriver()
		globalTransactionsProtocol = 'LoggingLastResource'
	}

	void setEmulateTwoPhaseCommit() {
		setNonXADriver()
		globalTransactionsProtocol = 'EmulateTwoPhaseCommit'
	}

	void setOracleOnePhaseCommit() {
		setNonXADriver()
		globalTransactionsProtocol = 'OnePhaseCommit'
	}

	void setNone() {
		setNonXADriver()
		globalTransactionsProtocol = 'None'
	}
}
