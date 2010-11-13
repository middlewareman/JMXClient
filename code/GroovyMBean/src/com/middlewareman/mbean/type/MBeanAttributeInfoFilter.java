package com.middlewareman.mbean.type;

import javax.management.MBeanAttributeInfo;

public interface MBeanAttributeInfoFilter {

	boolean accept(MBeanAttributeInfo attributeInfo);

}
