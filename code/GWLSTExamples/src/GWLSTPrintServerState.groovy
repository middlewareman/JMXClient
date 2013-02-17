/* $Id$ */
/**
 * GWLST version of Oracle's PrintServerState.
 * 
 * @author Andreas Nyberg
 */
for (sr in domainRuntimeServer.domainRuntimeService.ServerRuntimes) {
	println "Server name:$sr.Name state:$sr.State"
}