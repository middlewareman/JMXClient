/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type;

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.util.MBeanIterator 
import com.middlewareman.mbean.weblogic.DomainRuntimeServer 
import com.middlewareman.mbean.weblogic.EditServer 
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 


class TypeFacadeTest extends GroovyTestCase {
	
	RuntimeServer getRuntimeServer() {
		new RuntimeServer(WebLogicMBeanHomeFactory.default)
	}
	
	DomainRuntimeServer getDomainRuntimeServer() {
		new DomainRuntimeServer(WebLogicMBeanHomeFactory.default)
	}
	
	EditServer getEditServer() {
		new EditServer(WebLogicMBeanHomeFactory.default)
	}
	
	void testIterateRuntime() {
		testIterate new MBeanIterator(runtimeServer.runtimeService)
	}
	
	void testIterateDomainRuntime() {
		testIterate new MBeanIterator(domainRuntimeServer.domainRuntimeService)
	}
	
	void testIterateEdit() {
		testIterate new MBeanIterator(editServer.editService)
	}
	
	void testIterate(Iterator<MBean> iter) {
		Set<String> descriptions = new HashSet<String>()
		for (MBean mbean in iter) {
			for (attr in mbean.info.attributes) {
				def tf = new TypeFacade(attr)
				def itf = new InterfaceTypeFacade(attr)
				//def key = tf.toString() //"$tf/$itf".toString()
				//if (descriptions.add(key)) {
					println "$attr.name type $attr.type in $mbean"
					println "\t$tf.longString \t$tf.shortString \t$tf.stats"
					println "\t$itf.longString \t$itf.shortString \t$itf.stats"
				//}
			}
		}
	}
}
