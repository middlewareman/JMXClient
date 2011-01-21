/* $Id$ */
/**
 * Report transaction participants by server.
 * 
 * @author Andreas Nyberg
 */
import java.text.SimpleDateFormat

long sleep = 500

def serverNames = args
println "Server names: $serverNames"

Map server2xid2tx = [:]
Map server2resourceGroups = [:]

def df = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
println status()
def jtaRuntimes = serverNames ?
		serverNames.collect { domainRuntimeService.lookupServerRuntime(it).JTARuntime } :
		domainRuntimeService.ServerRuntimes.JTARuntime
while(true) {
	def timestamp = df.format(new Date())
	print 'Ping '
	println timestamp
	for (jta in jtaRuntimes) {
		def serverName = jta.@objectName.getKeyProperty('ServerRuntime')
		assert serverName
		def resourceGroups = server2resourceGroups.get(serverName)
		if (!resourceGroups) {
			resourceGroups = new LinkedHashSet()
			server2resourceGroups.put serverName, resourceGroups
		}
		def xid2tx = server2xid2tx.get(serverName)
		if (!xid2tx) {
			xid2tx = [:]
			server2xid2tx.put serverName, xid2tx
		}
		
		def txs = jta.JTATransactions.findAll { it.resourceNamesAndStatus }
		for (goneid in xid2tx.keySet() - (txs*.xid as Set)) {
			/* Transaction is gone: use report last known state and remove from map. */
			def gone = xid2tx[goneid]
			def resourceNames = gone.resourceNamesAndStatus.keySet()
			if (resourceGroups.add(resourceNames)) {
				/* New group */
				println "SERVER JTA\t$serverName"
				print '  NEW    \t'
				println resourceNames
				println '  CURRENT'
				resourceGroups.sort { it.size() }.each {
					print '\t'
					println it
				}
				def onlyOnes = resourceGroups.findAll {	one ->
					one.size() == 1 && !resourceGroups.any { more ->
						more.size() > 1 && more.containsAll(one)
					}
				}
				print '  SINGLES\t'
				println onlyOnes
				println()
			}
			xid2tx.remove goneid
		}
		for (tx in txs) {
			/* Update map with current (new and known) transactions. */
			xid2tx.put tx.xid, tx
		}
	}
	Thread.sleep sleep
}