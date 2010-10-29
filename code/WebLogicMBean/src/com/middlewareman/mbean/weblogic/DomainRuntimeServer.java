package com.middlewareman.mbean.weblogic;

public class DomainRuntimeServer {

	public static String localJndiName = "java:comp/env/jmx/domainRuntime";
	public static String remoteJndiName = "weblogic.management.mbeanservers.domainruntime";
	public static String domainRuntimeServiceObjectName = "com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean";
	
}
