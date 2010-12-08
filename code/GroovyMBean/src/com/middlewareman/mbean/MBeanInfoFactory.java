/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;

import javax.management.*;

public interface MBeanInfoFactory {

	MBeanInfo createMBeanInfo(ObjectName objectName) throws InstanceNotFoundException,
			IntrospectionException, ReflectionException, IOException;
}
