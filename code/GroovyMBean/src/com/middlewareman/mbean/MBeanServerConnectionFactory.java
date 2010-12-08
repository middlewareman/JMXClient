/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
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

	Object getServerId();

	MBeanServerConnection getMBeanServerConnection() throws IOException;

}
