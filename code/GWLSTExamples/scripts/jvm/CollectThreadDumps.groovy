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
		def jvm = sr.JVMRuntime
		String text = jvm.ThreadStackDump
		boolean stuck = text.contains('STUCK')
		def name = "${sr.Name}-${timestamp}-${thread.threadCount}"
		if (stuck) name += "-STUCK"
		new File(root,name + '.txt').write(text)
		try {
			def tda = new com.middlewareman.util.ThreadDumpAnalyzer()
			tda.parse text
			new File(root,name + '.analysed.txt').withPrintWriter { out ->
				out.println sr
				out.println "FreeCur\tFree%\tSizeCur\tSizeMax"
				out.println "${jvm.HeapFreeCurrent>>20} MB\t${jvm.HeapFreePercent}%\t${jvm.HeapSizeCurrent>>20} MB\t${jvm.HeapSizeMax>>20} MB"
				out.println()
				tda.report out
			}
		} catch(StackOverflowError e) {
			println "StackOverflowError for $serverName"
		}
	}
	println 'Sleeping...'
	Thread.sleep 30000
}