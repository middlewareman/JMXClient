package com.middlewareman.mbean.weblogic;

public class RuntimeServer {

	public static final String localJndiName = "java:comp/env/jmx/runtime";
	public static final String remoteJndiName = "weblogic.management.mbeanservers.runtime";
	public static final String runtimeServiceObjectName = "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean";

}
