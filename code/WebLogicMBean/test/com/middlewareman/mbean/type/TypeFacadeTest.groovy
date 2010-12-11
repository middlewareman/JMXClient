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
import javax.management.MBeanAttributeInfo 
import javax.management.ObjectName;


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
				def tf = create(attr)
				def desc = tf.toString()
				if (descriptions.add(desc)) {
					println "$attr.name in $mbean\n\t$attr.type\n\t$tf.longString \t$tf.shortString\n\t$tf.stats"
				}
			}
		}
	}
	
	protected TypeFacade create(MBeanAttributeInfo ai) {
		new TypeFacade(ai)
	}
	
}
