/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.security.auth.Subject;

/**
 * Factory for MBeanHome implementations for remote MBeanServer.
 * 
 * @author Andreas Nyberg
 */
public abstract class MBeanHomeFactory {

	/**
	 * Identifies the MBean server for equality and logging, but subclasses may
	 * choose to use it for actually addressing the server as well.
	 */
	public String url;

	/** Subject used when creating an MBeanServerConnection. */
	public Subject subject;

	/**
	 * If true, use {@link ConnectingMBeanHome}. If false, use
	 * {@link ConnectedMBeanHome}, which is the default, more secure but also
	 * less resilient.
	 */
	public boolean reconnect = false;

	public abstract JMXServiceURL surl(String path);

	public MBeanHome createMBeanHome(String urlPart) throws IOException {
		if (reconnect)
			return new ConnectingMBeanHome(surl(urlPart), env(), subject);
		else
			return new ConnectedMBeanHome(surl(urlPart), env(), subject);
	}

	/**
	 * Returns the created environment passed to
	 * {@link JMXConnectorFactory#connect(JMXServiceURL, Map)}.
	 */
	public Map<String, ?> env() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (url != null)
			map.put(Context.PROVIDER_URL, url);
		return map;
	}

}
