/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.builder

import groovy.xml.MarkupBuilder;

import java.util.Map;
import java.util.regex.Matcher 

import javax.management.*;

import com.middlewareman.mbean.MBean;
import com.middlewareman.mbean.type.AttributeFilter;
import com.middlewareman.mbean.type.CompositeDataWrapper 
import com.middlewareman.mbean.type.SimpleAttributeFilter 
import com.middlewareman.mbean.type.TabularDataWrapper 

/**
 * Generates an HTML report from an MBean or MBeanInfo.
 * 
 * @author Andreas Nyberg
 */
class HtmlExporter {
	
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
	
	boolean operationInfoFilter(MBeanOperationInfo oi) {
		true
	}
	
	boolean constructorInfoFilter(MBeanConstructorInfo ci) {
		true
	}
	
	boolean notificationInfoFilter(MBeanNotificationInfo ni) {
		true
	}
	
	boolean descriptorFieldNameFilter(String name) {
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
		long time1 = System.currentTimeMillis()
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
				div mkp.yieldUnescaped(info.description)	// TODO dangerous?
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
				table(border:1) {
					tr {
						td "info"
						td align:'right', time1-time0
					}
					tr {
						td 'attributes'
						td align:'right', timeAfterAttr - timeBeforeAttr
					}
					tr {
						td 'all'
						td align:'right', timeEnd - time0
					}
				}
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
				div mkp.yieldUnescaped(info.description)	// TODO dangerous?
				h2 'Attributes'
				attributes info, delegate
				h2 'Constructors'
				constructors info, delegate
				h2 'Operations'
				operations info, delegate
				h2 'Notifications'
				notifications info, delegate
				h2 'Done'
				div "Total time: ${System.currentTimeMillis() - time0} ms"
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
					td { type ai.type, delegate }
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
					td { type ai.type, delegate }
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
					td { type op.returnType, delegate }
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
						td { type pi.type, delegate }
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
		def names = info.descriptor.fieldNames
		def values = info.descriptor.getFieldValues(names)
		assert names.size() == values.size()
		for (i in 0..<names.length) {
			if (descriptorFieldNameFilter(names[i]))
				delegate.div title:values[i], names[i]
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
	
	def primitiveRE = ~/^(boolean|byte|char|double|float|int|long|short|void)((\[\])*)$/
	def binaryClassRE = ~/^(\[*)L(.*);$/
	
	void type(String name, delegate) {
		Matcher matcher
		if (name ==~ primitiveRE) {
			delegate.pre name
		} else if ((matcher = name =~ binaryClassRE)) {
			assert matcher.hasGroup()
			def dim = matcher[0][1].size()
			String className = matcher[0][2]
			className = className.substring(className.lastIndexOf('.')+1)
			if (dim) className += '[]' * dim
			delegate.pre title:name, className
		} else {
			String className = name.substring(name.lastIndexOf('.')+1)
			delegate.pre title:name, className
		}
	}
	
	void type(Class clazz, delegate) {
		delegate.pre title:clazz.name, clazz.simpleName
	}
}
