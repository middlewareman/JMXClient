/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.*

class EditServer extends WebLogicMBeanServer {
	
	private static final remoteJndiName = 'weblogic.management.mbeanservers.edit'
	
	public static final editServiceName = 'com.bea:Name=EditService,Type=weblogic.management.mbeanservers.edit.EditServiceMBean'
	public static final typeServiceName = 'com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService'
	
	EditServer(MBeanHomeFactory homeFactory) {
		super(homeFactory.createMBeanHome(remoteJndiName))
	}
	
	MBean getEditService() {
		home.getMBean editServiceName
	}
	
	MBean getTypeService() {
		home.getMBean typeServiceName
	}
}
