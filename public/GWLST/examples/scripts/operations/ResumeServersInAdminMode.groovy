/* $Id$ */
for (sr in domainRuntimeService.ServerRuntimes) {
	if (sr.State == "ADMIN") {
		println "Resuming $sr.Name"
		sr.resume()
	}
}