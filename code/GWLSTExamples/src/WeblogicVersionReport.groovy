/* $Id$ */
/**
 * Reports the WebLogic version reported by running servers in a domain
 * grouped by version.
 * Note that version can only be queried from running servers.
 * 
 * @author Andreas Nyberg
 */

println status()

def drs = domainRuntimeServer.domainRuntimeService

println "\nDOMAIN $drs.domainConfiguration.name"

def domainMap = [:]
for (serverRuntime in drs.serverRuntimes) 
	domainMap[serverRuntime.name] = serverRuntime.weblogicVersion

def domainReverseMap = domainMap.groupBy { name, version -> version } 

def domainLevels = domainReverseMap.size()
if (domainLevels > 1)
	println "WARNING: There are $domainLevels different versions/patchlevels in your DOMAIN."
for (version in domainReverseMap.keySet()) {
	def nameSet = domainReverseMap[version].keySet().sort()
	println "\nServer $nameSet:"
	println version
}

for (cluster in drs.domainConfiguration.clusters) {
	def clusterName = cluster.name
	println "\nCLUSTER $clusterName"
	def configNames = cluster.servers?.name as Set
	def runtimeNames = domainMap.keySet()
	def unavailableNames = configNames - runtimeNames
	if (unavailableNames) 
		println "Warning: cannot check cluster members not running: $unavailableNames"
	def clusterMap = domainMap.findAll { key, value -> key in runtimeNames }
	def clusterReverseMap = clusterMap.groupBy { name, version -> version }
	def clusterLevels = domainReverseMap.size()
	if (clusterLevels > 1) {
		println "WARNING: There are $clusterLevels different versions/patchlevels in cluster $clusterName."
		for (version in clusterReverseMap.keySet()) {
			def nameSet = domainReverseMap[version].keySet().sort()
			println "\nServer $nameSet:"
			println version
		}
	}
}
