/* $Id$ */
editSave { domain ->
	for (jdbcsr in domain.JDBCSystemResources) {
		if (jdbcsr.Name ==~ /.*MultiDS/) {
			jdbcsr.JDBCResource.JDBCDataSourceParams.FailoverRequestIfBusy = true
			println jdbcsr
		}
	}
}
