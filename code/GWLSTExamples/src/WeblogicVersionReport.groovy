/* $Id$ */
/**
 * Reports the WebLogic version reported by running servers in a domain
 * grouped by version.
 * 
 * @author Andreas Nyberg
 */

println status()

def drs = domainRuntimeServer.domainRuntimeService

def map = [:]
for (serverRuntime in drs.serverRuntimes) 
	map[serverRuntime.name] = serverRuntime.weblogicVersion

def reverseMap = map.groupBy { name, version -> version } 

def domainLevels = reverseMap.size()
if (domainLevels > 1)
	println "WARNING: There are $domainLevels different versions/patchlevels in your DOMAIN."
for (version in reverseMap.keySet()) {
	def nameSet = reverseMap[version].keySet().sort()
	println "\n$nameSet:"
	println version
}
