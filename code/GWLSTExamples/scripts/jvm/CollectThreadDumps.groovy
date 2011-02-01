/* $Id$ */
def df = new java.text.SimpleDateFormat('yyyyMMdd-HHmmss')
def domainName = domainRuntimeService.DomainConfiguration.Name
def timestamp = df.format(new Date())
def root = new File("${domainName}/${timestamp}")
root.mkdirs()
for (sr in domainRuntimeService.ServerRuntimes) {
	def serverName = sr.Name
	println serverName
	String text = sr.JVMRuntime.ThreadStackDump
	new File(root,"${sr.Name}.txt").write(text)
}