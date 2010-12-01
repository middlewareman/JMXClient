import javax.management.ObjectName
import com.middlewareman.mbean.weblogic.RuntimeServer

if (params.objectName) {
	def objectName = params.objectName
	def home = RuntimeServer.localMBeanHome
	assert home
	mbean = home.getMBean(objectName)
} else {
	mbean = RuntimeServer.localRuntimeServer.runtimeService
}
assert mbean
request.setAttribute 'MBean', mbean
forward 'DumpMBean.groovy'
