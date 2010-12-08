/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;

public interface MBeanFactory {

	public MBean createMBean(ObjectName objectName)
			throws InstanceNotFoundException, IOException;
}
