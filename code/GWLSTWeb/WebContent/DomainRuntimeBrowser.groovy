import javax.management.ObjectName
import com.middlewareman.mbean.weblogic.DomainRuntimeServer

if (params.objectName) {
	def objectName = params.objectName
	def home = DomainRuntimeServer.localMBeanHome
	assert home
	mbean = home.getMBean(objectName)
} else {
	mbean = DomainRuntimeServer.localDomainRuntimeServer.domainRuntimeService
}
assert mbean
request.setAttribute 'MBean', mbean
forward 'DumpMBean.groovy'
