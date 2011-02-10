/* $Id$ */
/**
 * Configures server log to sharable location.
 * 
 * @author Andreas Nyberg
 */
editSave { domain ->
	for (server in domain.Servers) {
		def serverName = server.Name
		def log = server.Log
		log.FileName = "../../logs/servers/${serverName}/server-${serverName}.log" as String
		log.FileMinSize = 5<<10 // 5 MB instead of default 500 KB
	}
}