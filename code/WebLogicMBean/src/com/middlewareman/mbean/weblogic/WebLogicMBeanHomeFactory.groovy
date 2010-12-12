/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import java.io.IOException;

import com.middlewareman.mbean.MBeanHome;
import com.middlewareman.mbean.MBeanHomeFactory 
import javax.management.remote.JMXConnectorFactory 
import javax.management.remote.JMXServiceURL 
import javax.naming.Context 

public class WebLogicMBeanHomeFactory extends MBeanHomeFactory {
	
	static WebLogicMBeanHomeFactory getDefault() {
		def hf = new WebLogicMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		hf.loadEnvironmentProperties()
		hf.loadSystemProperties()
		return hf
	}
	
	String username
	String password
	String protocolProviderPackages = 'weblogic.management.remote'
	Long timeout
	
	void loadEnvironmentProperties() {
		String value
		value = System.getenv('GWLST_URL')
		if (value) url = value
		value = System.getenv('GWLST_USERNAME')
		if (value) username = value
		value = System.getenv('GWLST_PASSWORD')
		if (value) password = value
		value = System.getenv('GWLST_TIMEOUT')
		if (value) timeout = value as Long
		// TODO GWLST_ENV_*
	}
	
	void loadSystemProperties() {
		String value
		value = System.getProperty('gwlst.url')
		if (value) url = value
		value = System.getProperty('gwlst.username')
		if (value) username = value
		value = System.getProperty('gwlst.password')
		if (value) password = value
		value = System.getProperty('gwlst.timeout')
		if (value) timeout = value as Long
		// TODO gwlst.env.*
	}
	
	String[] loadArguments(String[] args) {
		def escape = new ArrayList<String>(args.length)
		def iter = args.iterator()
		while (iter) {
			String arg = iter.next()
			switch (arg) {
				case '-url':
					assert iter, "Missing argument for $arg"
					url = iter.next()
					break
				case '-username':
					assert iter, "Missing argument for $arg"
					username = iter.next()
					break
				case '-password':
					assert iter, "Missing argument for $arg"
					password = iter.next
					break
				default:
					escape.add arg
			}
		}
		return escape as String[]
	}
	
	JMXServiceURL surl(String path) throws MalformedURLException {
		new JMXServiceURL("service:jmx:${url}/jndi/${path}");
	}
	
	MBeanHome createMBeanHome(String urlPart) throws IOException {
		MBeanHome home = super.createMBeanHome(urlPart)
		home.enableMBeanCache()
		home.enableMBeanInfoCache null
		return home
	}
	
	Map<String,?> env() {
		def map = super.env()
		map.put Context.SECURITY_PRINCIPAL, username
		map.put Context.SECURITY_CREDENTIALS, password
		map.put 'jmx.remote.x.request.waiting.timeout', timeout
		map.put JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, protocolProviderPackages 
		return map
	}
}
