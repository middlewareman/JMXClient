/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.info;

import javax.management.MBeanFeatureInfo;

public interface DescriptorFilter {

	boolean accept(MBeanFeatureInfo fi, String fieldName);
}
