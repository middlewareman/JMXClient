/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.Closeable;
import java.io.IOException;

import javax.management.MBeanServerConnection;

/**
 * MBeanServerConnection factory implemented by {@link MBeanHome}.
 * 
 * @author Andreas Nyberg
 */
public interface MBeanServerConnectionFactory extends Closeable {

	Object getAddress();
	
	MBeanServerConnection getConnection() throws IOException;

}
