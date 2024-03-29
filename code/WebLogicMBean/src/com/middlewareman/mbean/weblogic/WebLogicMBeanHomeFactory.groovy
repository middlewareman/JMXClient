/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import java.io.IOException

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import javax.naming.Context

import com.middlewareman.mbean.MBeanHome
import com.middlewareman.mbean.MBeanHomeFactory
import com.middlewareman.mbean.info.SimpleAttributeFilter

/**
 * Factory of RemoteMBeanHome to a WebLogic Server with support for picking up parameters from environment, 
 * system properties and command line parameters.
 * 
 * @author Andreas Nyberg
 */
public class WebLogicMBeanHomeFactory extends MBeanHomeFactory {

	static WebLogicMBeanHomeFactory getDefaults() {
		def hf = new WebLogicMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		hf.loadEnvironmentProperties()
		hf.loadSystemProperties()
		return hf
	}

	@Deprecated
	static WebLogicMBeanHomeFactory getDefault() {
		getDefaults()
	}

	static WebLogicMBeanHomeFactory getLoaded() {
		def hf = new WebLogicMBeanHomeFactory()
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
					password = iter.next()
					break
				case '-timeout':
					assert iter, "Missing argument for $arg"
					timeout = iter.next() as Long
					break
				default:
					escape.add arg
			}
		}
		return escape as String[]
	}

	void promptConsole() {
		Console console = System.console()
		if (!console) {
			// LOG
			return
		}
		final urlPattern = ~/\w+:\/\/\w+(\.\w+)*:\d+/
		while (!url || !(url ==~ urlPattern)) {
			if (url) console.printf "Invalid URL format: $url\n"
			url = console.readLine('GWLST URL:      ')
		}
		while (!username) username = console.readLine('GWLST username: ')
		while (!password) password = new String(console.readPassword('GWLST password: '))
	}

	void promptPopup() {
		//System.err.println "${this.class.name} promptPopup() not yet implemented"
		promptConsole()
	}

	JMXServiceURL surl(String path) throws MalformedURLException {
		new JMXServiceURL("service:jmx:${url}/jndi/${path}");
	}

	MBeanHome createMBeanHome(String urlPart) throws IOException {
		MBeanHome home = super.createMBeanHome(urlPart)
		home.enableMBeanCache()
		home.enableMBeanInfoCache null
		home.defaultPropertiesFilter = new SimpleAttributeFilter(deprecated:false,readable:true)
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
