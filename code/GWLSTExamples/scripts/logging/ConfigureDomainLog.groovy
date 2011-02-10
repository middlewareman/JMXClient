/* $Id$ */
/**
 * Configures domain log to sharable location.
 * 
 * @author Andreas Nyberg
 */
editSave { domain ->
	def domainName = domain.Name
	for (server in domain.Servers) {
		def wslog = server.WebServer.Log
		wslog.FileName = "../../logs/domain/domain-$domainName.log" as String
		wslog.FileMinSize = 5<<10 // 5 MB instead of default 500 KB
	}
}