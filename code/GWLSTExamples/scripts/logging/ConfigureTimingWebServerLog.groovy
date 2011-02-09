/* $Id$ */
/**
 * Configures time-taken webserver (http) log.
 * 
 * @author Andreas Nyberg
 */
editSave { domain ->
	for (server in domain.Servers) {
		def wslog = server.WebServer.WebServerLog
		wslog.FileName = "../../logs/servers/$server.Name/access.log" as String
		wslog.FileMinSize = 5<<10 // 5 MB instead of default 500 KB
		wslog.LogFileFormat = 'extended'
		wslog.ELFFields = 'date time cs-method cs-uri-stem time-taken cs-status bytes cs-uri-query'
	}
}