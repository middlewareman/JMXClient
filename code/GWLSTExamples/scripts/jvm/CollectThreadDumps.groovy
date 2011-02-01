/* $Id$ */
def df = new java.text.SimpleDateFormat('yyyyMMdd-HHmmss')
def domainName = domainRuntimeService.DomainConfiguration.Name
while(true) {
	def timestamp = df.format(new Date())
	println timestamp
	def root = new File("${domainName}/${timestamp}")
	root.mkdirs()
	for (sr in domainRuntimeService.ServerRuntimes) {
		def serverName = sr.Name
		println serverName
		def ph = domainRuntimeServer.getProxyPlatformHome(serverName)
		def thread = ph.thread
		String text = sr.JVMRuntime.ThreadStackDump
		boolean stuck = text.contains('STUCK')
		def name = "${sr.Name}-${thread.threadCount}"
		if (stuck) name += "-STUCK"
		new File(root,name + '.txt').write(text)
	}
	Thread.sleep 30000
}