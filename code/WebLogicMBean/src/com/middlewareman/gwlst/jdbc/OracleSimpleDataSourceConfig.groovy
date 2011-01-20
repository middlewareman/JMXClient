/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.gwlst.jdbc

/**
 * Configurator for Oracle Thin driver.
 * 
 * @author Andreas Nyberg
 */
class OracleSimpleDataSourceConfig extends SimpleDataSourceConfig {

	OracleSimpleDataSourceConfig() {
		connectionPoolParams.TestTableName = 'SQL SELECT 1 FROM DUAL'
	}

	void setNonXADriver() {
		driverName = 'oracle.jdbc.OracleDriver'
	}

	void setXADriver() {
		driverName = 'oracle.jdbc.xa.client.OracleXADataSource'
	}
}
