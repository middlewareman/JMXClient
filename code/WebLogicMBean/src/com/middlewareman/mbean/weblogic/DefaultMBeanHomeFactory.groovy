package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.MBeanHomeFactory 
import javax.management.remote.JMXConnectorFactory 
import javax.management.remote.JMXServiceURL 
import javax.naming.Context 

public class DefaultMBeanHomeFactory extends MBeanHomeFactory {

	// protocol packages etc
	
	String username
	String password
	String protocolProviderPackages
	
	JMXServiceURL surl(String path) throws MalformedURLException {
		new JMXServiceURL("service:jmx:${url}/jndi/${path}");
	}
	
	Map<String,?> env() {
		def map = super.env()
		map.put Context.SECURITY_PRINCIPAL, username ?: 'weblogic'
		map.put Context.SECURITY_CREDENTIALS, password ?: 'welcome1'
		map.put JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, protocolProviderPackages ?: 'weblogic.management.remote' 
		return map
	}
}
