/* $Id$ */
/**
 * Configure thread pool for servers.
 * 
 * @author Andreas Nyberg
 */
editSave { domain ->
	domain.Servers.each { server ->
		server.SelfTuningThreadPoolSizeMax = 50
	}
}