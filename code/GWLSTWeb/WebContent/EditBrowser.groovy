import com.bea.common.security.xacml.context.Request;

import javax.management.ObjectName
import com.middlewareman.mbean.weblogic.*

assert request.getSession(true)

def getEditServer() {
	def rs = RuntimeServer.localRuntimeServer.runtimeService
	def adminUrl = rs.ServerRuntime.AdministrationURL
	def mf = new WebLogicMBeanHomeFactory(url:adminUrl)
	new EditServer(mf)
}

def getHome() {
	def h = session?.editServerHome
	if (!h) {
		h = editServer.home
		setHome h
	}
	return h
}

void setHome(def newHome) {
	request.getSession(true).editServerHome = newHome
}

if (params.objectName) {
	assert home
	request.MBean = home.getMBean(params.objectName)
} else { 
	def mbean = editServer.editService
	assert mbean
	home = mbean.@home
	request.MBean = mbean
}
assert request.getAttribute('MBean')
forward 'DumpMBean.groovy'