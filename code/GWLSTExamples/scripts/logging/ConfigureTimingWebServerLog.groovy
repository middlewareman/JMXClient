/* $Id$ */
/**
 * Configures time-taken webserver (http) log.
 * 
 * @author Andreas Nyberg
 */
editSave { domain ->
	def domainRootDir = domain.RootDirectory
	for (server in domain.Servers) {
		def wslog = server.WebServer.WebServerLog
		wslog.FileName = "../../logs/servers/$server.Name/access.log" as String
		wslog.LogFileFormat = 'extended'
		wslog.ELFFields = 'date time cs-method cs-uri-stem time-taken cs-status bytes cs-uri-query'
	}
}