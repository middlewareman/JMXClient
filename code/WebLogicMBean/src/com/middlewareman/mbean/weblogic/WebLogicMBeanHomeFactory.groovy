package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.MBeanHomeFactory 
import javax.management.remote.JMXConnectorFactory 
import javax.management.remote.JMXServiceURL 
import javax.naming.Context 

public class WebLogicMBeanHomeFactory extends MBeanHomeFactory {
	
	static WebLogicMBeanHomeFactory getDefault() {
		new WebLogicMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
	}
	
	String username
	String password
	String protocolProviderPackages = 'weblogic.management.remote' 
	
	JMXServiceURL surl(String path) throws MalformedURLException {
		new JMXServiceURL("service:jmx:${url}/jndi/${path}");
	}
	
	Map<String,?> env() {
		def map = super.env()
		map.put Context.SECURITY_PRINCIPAL, username
		map.put Context.SECURITY_CREDENTIALS, password
		map.put JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, protocolProviderPackages 
		return map
	}
}
