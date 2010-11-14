package com.middlewareman.mbean.platform

import com.middlewareman.mbean.LocalPlatformMBeanHome;

class ProxyPlatformHomeTest extends PlatformHomeTest {
	
	final ph = new ProxyPlatformHome(new LocalPlatformMBeanHome())
}
