/** 
 * Groovy Servlet to dump an MBean.
 * Expects request attribute 'MBean'
 */

import com.middlewareman.mbean.MBean
import com.middlewareman.mbean.weblogic.*

void render(Object value) {
	if (value == null) {
		html.i 'null'
	} else {
		switch(value) {
			case Object[]:
			case MBean[]:
				renderArray(value)
				break
			case Map:
				renderMap(value)
				break
			case Exception:
				html.i value.message
				break
			case MBean:
				html.a(href:"?objectName=${value.@objectName}") { pre value.@objectName }
				break
			default:
				html.pre value
		}
	}
}

void renderArray(Object[] array) {
	if (array) {
		html.table(border:'1') {
			for (i in 0..<array.length) {
				def v = array[i]
				tr {
					td { pre i }
					td { render v }
				}
			}
		}
	} else {
		html.pre '[]'
	}
}

void renderMap(Map map) {
	if (map) {
		html.table(border:'1') {
			map.each { mapKey, mapVal ->
				tr {
					td { pre mapKey }
					td { render mapVal }
				}
			}
		}
	} else {
		pre '[:]'
	}
}

def home = request.getAttribute('MBeanHome')
if (!home) home = RuntimeServer.localMBeanHome


def objectName = params.objectName
if (objectName) {
}
def mbean = request.getAttribute('MBean')
if (!mbean) mbean = RuntimeServer.localRuntimeServer.runtimeService

assert mbean

def attributes = mbean.info.attributes // .findAll{ !it['deprecated'] }

html.html {
	head { title 'MBean Browser' }
	body {
		h2 'MBeanHome'
		pre mbean.@home
		h2 'ObjectName'
		pre mbean.@objectName
		h2 'Attributes'
		table(border:'1') {
			tr {
				th 'Attribute'
				th 'Type'
				th 'Value'
			}
			// attributes.sort{it.name}
			for (attr in attributes) {
				def name = attr.name
				def val
				try {
					val = mbean.getProperty(name)
				} catch(Exception e) {
					val = e
				}
				tr {
					td {  pre name }
					td {  pre attr.type }
					td { render val }
				}
			}
		}
		h2 'Done.'
	}
}

