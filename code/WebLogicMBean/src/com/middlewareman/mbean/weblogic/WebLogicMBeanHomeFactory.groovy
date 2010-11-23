package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.MBeanHomeFactory 
import javax.management.remote.JMXConnectorFactory 
import javax.management.remote.JMXServiceURL 
import javax.naming.Context 

public class WebLogicMBeanHomeFactory extends MBeanHomeFactory {
	
	static WebLogicMBeanHomeFactory getDefault() {
		def hf = new WebLogicMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		hf.loadSystemProperties()
		return hf
	}
	
	String username
	String password
	String protocolProviderPackages = 'weblogic.management.remote'
	Long timeout
	
	void loadSystemProperties() {
		def props = System.getProperties() // TODO optimise: gwlst* only
		def config = new ConfigSlurper().parse(props).gwlst
		loadProperties config
	}
	
	void loadProperties(def map) {
		if (map.url) url = map.url
		if (map.username) username = map.username
		if (map.password) password = map.password
		if (map.timeout) timeout = map.timeout
		// TODO use gwlst.env.*
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
	
	Map<String,?> env() {
		def map = super.env()
		map.put Context.SECURITY_PRINCIPAL, username
		map.put Context.SECURITY_CREDENTIALS, password
		map.put 'jmx.remote.x.request.waiting.timeout', timeout
		map.put JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, protocolProviderPackages 
		return map
	}
}
