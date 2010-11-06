package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 
import com.middlewareman.mbean.MBeanHomeFactory 

class EditServerHome {
	
	private static final remoteJndiName = 'weblogic.management.mbeanservers.edit'
	
	final MBeanHome home
	
	EditServerHome(MBeanHome home) {
		this.home = home
	}
	
	EditServerHome(MBeanHomeFactory homeFactory) {
		this.home = homeFactory.createMBeanHome(remoteJndiName)
	}
	
	MBean getRuntimeService() {
		home.getMBean 'com.bea:Name=EditService,Type=weblogic.management.mbeanservers.edit.EditServiceMBean'
	}
	
	MBean getTypeService() {
		home.getMBean 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'
	}
}
