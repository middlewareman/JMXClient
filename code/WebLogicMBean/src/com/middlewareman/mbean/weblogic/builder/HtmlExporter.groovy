/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.builder

import groovy.xml.MarkupBuilder;

import java.util.Map;

import javax.management.*;

import com.middlewareman.mbean.MBean;
import com.middlewareman.mbean.type.AttributeFilter;
import com.middlewareman.mbean.type.CompositeDataWrapper 
import com.middlewareman.mbean.type.DescriptorFilter 
import com.middlewareman.mbean.type.InterfaceTypeFacade 
import com.middlewareman.mbean.type.MBeanInfoCategory 
import com.middlewareman.mbean.type.SimpleAttributeFilter 
import com.middlewareman.mbean.type.SimpleDescriptorFilter 
import com.middlewareman.mbean.type.TabularDataWrapper 
import com.middlewareman.mbean.type.TypeFacade 

/**
 * Generates an HTML report from an MBean or MBeanInfo.
 * 
 * @author Andreas Nyberg
 */
class HtmlExporter {
	
	static {	// TODO
		MBeanFeatureInfo.mixin MBeanInfoCategory
	}
	
	final MarkupBuilder html
	
	HtmlExporter() {
		html = new MarkupBuilder()
	}
	
	HtmlExporter(PrintWriter pw) {
		html = new MarkupBuilder(pw)
	}
	
	HtmlExporter(MarkupBuilder mb) {
		html = mb
	}
	
	AttributeFilter attributeFilter = SimpleAttributeFilter.verbose
	DescriptorFilter descriptorFilter = new SimpleDescriptorFilter()	// TODO
	
	boolean operationInfoFilter(MBeanOperationInfo oi) {
		true
	}
	
	boolean constructorInfoFilter(MBeanConstructorInfo ci) {
		true
	}
	
	boolean notificationInfoFilter(MBeanNotificationInfo ni) {
		true
	}
	
	static void notice(delegate) {
		delegate.p {
			mkp.yield 'This page is generated using '
			a(href:'http://www.middlewareman.com/gwlst', title:'Groovy WebLogic Scripting Tool', 'GWLST')
			mkp.yield '.'
		}
	}
	
	void mbean(MBean mbean, Map extras = null) {
		long time0 = System.currentTimeMillis()
		def info = mbean.info
		html.html {
			head { title 'GWLST MBean Browser' }
			body {
				notice delegate
				h1 'GWLST MBean Browser'
				if (extras) {
					table(border:'1') {
						extras.each { key, value ->
							tr {
								td key
								td { pre value }
							}
						}
					}
				}
				h2 'MBeanHome'
				pre mbean.@home
				h2 'ObjectName'
				pre mbean.@objectName
				h2 'Description'
				blockquote {
					mkp.yieldUnescaped(info.description)	// TODO dangerous?
				}
				h2 'Attributes'
				long timeBeforeAttr = System.currentTimeMillis()
				attributes mbean, info, delegate
				long timeAfterAttr = System.currentTimeMillis()
				h2 'Operations'
				operations info, delegate
				h2 'Constructors'
				constructors info, delegate
				h2 'Notifications'
				notifications info, delegate
				h2 'Done'
				long timeEnd = System.currentTimeMillis()
				div "Done in ${timeEnd - time0} ms"
			}
		}
	}
	
	void mbean(MBeanInfo info) {
		long time0 = System.currentTimeMillis()
		html.html {
			head { title 'GWLST MBeanInfo Browser' }
			a(href:'http://www.middlewareman.com/gwlst', 'Groovy WebLogic Scripting Tool (GWLST)')
			body {
				h2 'Description'
				blockquote {
					mkp.yieldUnescaped(info.description)	// TODO dangerous?
				}
				h2 'Attributes'
				attributes info, delegate
				h2 'Constructors'
				constructors info, delegate
				h2 'Operations'
				operations info, delegate
				h2 'Notifications'
				notifications info, delegate
				h2 'Done'
				div "Done in ${System.currentTimeMillis() - time0} ms"
			}
		}
	}
	
	void attributes(MBean mbean, MBeanInfo info, delegate) {
		def ais = info.attributes.findAll { attributeFilter.acceptAttribute it }.sort { it.name }
		delegate.table(border:'1') {
			tr {
				th 'Name'
				th 'Descriptor'
				th 'Type'
				th 'ActualType'
				th 'Value'
			}
			for (ai in ais) {
				def val
				try {
					val = mbean.getProperty(ai.name)
					if (!attributeFilter.acceptAttribute(ai,val)) {
						println "skipping attribute $ai.name"
						continue
					}
				} catch(Exception e) {
					val = e
				}
				tr {
					td { name ai, delegate }
					td { descriptor ai, delegate }
					td { type ai, delegate }
					td {
						if (val != null) 
							type val.getClass(), delegate
					}
					td { value val, delegate }
				}
			}
		}
	}
	
	void attributes(MBeanInfo info, delegate) {
		def ais = info.attributes.findAll { attributeFilter.acceptAttribute(it) }.sort { it.name }
		delegate.table(border:'1') {
			tr {
				th 'Name'
				th 'Descriptor'
				th 'Type'
			}
			for (ai in ais) {
				if (!attributeInfoFilter(ai))
					continue
				tr {
					td { name ai, delegate }
					td { descriptor ai, delegate }
					td { type ai, delegate }
				}
			}
		}
	}
	
	void operations(MBeanInfo info, delegate) {
		delegate.table(border:'1') {
			tr {
				th 'ReturnType'
				th 'Name'
				th 'Descriptor'
				th 'Parameters'
			}
			for (op in info.operations) {
				tr {
					td { type op.returnType, delegate }		// TODO
					td { name op, delegate }
					td { descriptor op, delegate }
					td { signature op.signature, delegate }
				}
			}
		}
	}
	
	void constructors(MBeanInfo info, delegate) {
		delegate.table(border:'1') {
			tr {
				th 'Name'
				th 'Parameters'
			}
			for (con in info.constructors) {
				if (!constructorInfoFilter(con)) 
					continue
				tr {
					td { name con, delegate }
					td { signature con.signature, delegate }
				}
			}
		}
	}
	
	void signature(MBeanParameterInfo[] pis, delegate) {
		if (pis) {
			delegate.table(border:'1') {
				for (pi in pis) {
					tr {
						td { type pi, delegate }
						td { descriptor pi, delegate }
						td { name pi, delegate }
					}
				}
			}
		}
	}
	
	void notifications(MBeanInfo info, delegate) {
		delegate.table(border:'1') {
			tr {
				th 'Name'
				th 'NotificationTypes'
			}
			for (ni in info.notifications) {
				if (!notificationInfoFilter(ni))
					continue
				tr {
					td { name ni, delegate }
					td { descriptor ni, delegate }
					td {
						for (nt in ni.notifTypes) pre nt
					}
				}
			}
		}
	}
	
	void name(MBeanFeatureInfo info, delegate) {
		def deprecated = info.descriptor.getFieldValue('deprecated')
		if (deprecated) {
			delegate.del {
				pre title:info.description, info.name
			}
		} else {
			delegate.pre title:info.description, info.name
		}
	}
	
	void descriptor(MBeanFeatureInfo info, delegate) {
		for (fieldName in info.descriptor.fieldNames) {
			if (!descriptorFilter || descriptorFilter.accept(info,fieldName))
				delegate.div title:info[fieldName].inspect(), fieldName
		}
	}
	
	void value(value, delegate) {
		if (value == null) {
			delegate.i 'null'
		} else {
			switch(value) {
				case Object[]:
					array value, delegate
					break
				case Map:
					map value, delegate
					break
				case Exception:
					exception value, delegate
					break
				case MBean:
					mbean value, delegate
					break
				case CompositeDataWrapper:
					map value.properties, delegate
					break
				case TabularDataWrapper:
					map value.properties, delegate
					break
				default:
					delegate.pre title:value.getClass().getName(), value
			}
		}
	}
	
	void array(Object[] array, delegate) {
		if (array) {
			delegate.table(border:'1') {
				for (i in 0..<array.length) {
					def v = array[i]
					tr {
						td { pre i }
						td { value v, delegate }
					}
				}
			}
		} else {
			delegate.pre '[]'
		}
	}
	
	void map(Map map, delegate) {
		if (map) {
			delegate.table(border:'1') {
				map.each { mapKey, mapVal ->
					tr {
						td { pre mapKey }
						td { value mapVal, delegate }
					}
				}
			}
		} else {
			delegate.pre '[:]'
		}
	}
	
	void exception(Exception e, delegate) {
		def sw = new StringWriter()
		sw.withPrintWriter { e.printStackTrace it }
		delegate.i title:sw, e.message
	}
	
	void mbean(MBean mbean, delegate) {
		delegate.a(href:"?objectName=${mbean.@objectName}") { pre mbean.@objectName }
	}
	
	// TODO
	void type(String name, delegate) {
		def tf = new TypeFacade(name)
		delegate.pre title:name, tf.shortString
	}
	
	void type(MBeanFeatureInfo fi, delegate) {
		def tf = new InterfaceTypeFacade(fi)
		delegate.pre title:tf.originalTypeName, tf.shortString
	}
	
	void type(Class clazz, delegate) {
		delegate.pre title:clazz.name, clazz.simpleName
	}
}
