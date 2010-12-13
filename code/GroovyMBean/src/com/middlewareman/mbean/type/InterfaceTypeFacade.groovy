/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type

import javax.management.JMX 
import javax.management.MBeanAttributeInfo

/**
 * TypeFacade that also considers any {@link JMX#INTERFACE_CLASS_NAME_FIELD interfaceClassName} 
 * field of the {@link Descriptor}.
 * 
 * @author Andreas Nyberg
 */
class InterfaceTypeFacade extends TypeFacade {
	
	final String interfaceName
	
	InterfaceTypeFacade(MBeanAttributeInfo ai) {
		this(ai.type, ai.descriptor.getFieldValue(JMX.INTERFACE_CLASS_NAME_FIELD))
	}
	
	/**
	 * @param typeName Class name of attribute value as returned by {@link MBeanAttributeInfo#getType()}.
	 * @param interfaceName Interface name of attribute as returned by
	 * {@link JMX#INTERFACE_CLASS_NAME_FIELD interfaceClassName} field of {@link Descriptor}.
	 */
	InterfaceTypeFacade(String typespec, String interfaceName) {
		super(typespec)
		if (interfaceName) {
			this.interfaceName = interfaceName
			TypeFacade interfaceType = new TypeFacade(interfaceName)
			assert dim == interfaceType.dim
			if (isMaybeMBean() && interfaceType.isMaybeMBean()) 
				this.type = TypeFacade.Type.MBEAN;	// TODO Check guess!
			this.typeName = interfaceType.typeName;
		}
	}
	
	String getLongName() {
		isCertainlyMBean() ? "MBean<$typeName>" : typeName
	}
	
	String getShortName() {
		def shortname = typeName.substring(typeName.lastIndexOf('.')+1) 
		isCertainlyMBean() ? "MBean<$shortname>" : shortname
	}
}
