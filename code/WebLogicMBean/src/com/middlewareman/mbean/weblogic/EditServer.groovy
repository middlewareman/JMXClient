package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 
import com.middlewareman.mbean.MBeanHomeFactory 

class EditServer {
	
	private static final remoteJndiName = 'weblogic.management.mbeanservers.edit'
	
	public static final editServiceName = 'com.bea:Name=EditService,Type=weblogic.management.mbeanservers.edit.EditServiceMBean'
	public static final typeServiceName = 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'
	
	final MBeanHome home
	
	EditServer(MBeanHome home) {
		this.home = home
	}
	
	EditServer(MBeanHomeFactory homeFactory) {
		this.home = homeFactory.createMBeanHome(remoteJndiName)
	}
	
	MBean getEditService() {
		home.getMBean editServiceName
	}
	
	MBean getTypeService() {
		home.getMBean typeServiceName
	}
}
