for (sr in domainRuntimeService.ServerRuntimes) {
	println "Server name:$sr.Name state:$sr.State"
}