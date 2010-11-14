package com.middlewareman.mbean.platform

import com.middlewareman.mbean.LocalPlatformMBeanHome 

class MBeanPlatformHomeTest extends PlatformHomeTest {

	final ph = new MBeanPlatformHome(new LocalPlatformMBeanHome())

}
