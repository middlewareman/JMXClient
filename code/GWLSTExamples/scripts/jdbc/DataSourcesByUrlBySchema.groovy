final version = '$Id$'

def ipw = new com.middlewareman.groovy.util.IndentPrintWriter()

ipw.println version
ipw.println new Date()
ipw.println domainRuntimeServer.home
ipw.println()

def jdbcsList = domainRuntimeService.DomainConfiguration.JDBCSystemResources as List
def jdbcsByUrl = jdbcsList.groupBy {
	it.JDBCResource.JDBCDriverParams.Url
}

jdbcsByUrl.each { url, jdbcsPerUrl ->
	ipw.indent("\nURL $url") {
		def jdbcsByUser = jdbcsPerUrl.groupBy {
			it.JDBCResource.JDBCDriverParams.Properties.Properties.find { it.Name == 'user' }.Value
		}
		jdbcsByUser.each { user, jdbcsPerUser ->
			ipw.indent("\nUSER $user") {
				for (jdbc in jdbcsPerUser) {
					def jdbcName = jdbc.Name
					def initial = jdbc.JDBCResource.JDBCConnectionPoolParams.InitialCapacity
					def max = jdbc.JDBCResource.JDBCConnectionPoolParams.MaxCapacity
					def jndiNames = jdbc.JDBCResource.JDBCDataSourceParams.JNDINames
					ipw.indent("\nJDBC $jdbcName\t$initial\t$max\t$jndiNames") {
						for (target in jdbc.Targets) {
							def name = target.@objectName.getKeyProperty('Name')
							switch(target.@objectName.getKeyProperty('Type')) {
								case 'Server':
									ipw.indent("\nSERVER $name") {
										//def datasourceRuntime = domainRuntimeServer.home.getMBean("com.bea:Name=$jdbcName,ServerRuntime=$name,Location=$name,Type=JDBCDataSourceRuntime")
										def serverRuntime = domainRuntimeService.lookupServerRuntime(name)
										if (serverRuntime) {
											def jdbcServiceRuntime = serverRuntime.JDBCServiceRuntime
											def dsRuntime = jdbcServiceRuntime.JDBCDataSourceRuntimeMBeans.find { it.Name == jdbcName }
											if (dsRuntime) {
												ipw.println "ActiveHigh \t$dsRuntime.ActiveConnectionsHighCount"
												ipw.println "CurrentHigh\t$dsRuntime.CurrCapacityHighCount"
												ipw.println "WaitSecHigh\t$dsRuntime.WaitSecondsHighCount"
												ipw.println "WaitingHigh\t$dsRuntime.WaitingForConnectionHighCount"
											} else {
												ipw.println "No JDBCDataSourceRuntime"
											}
										} else {
											ipw.println "No ServerRuntime"
										}
									}
									break
								case 'Cluster':
									ipw.println "\nCLUSTER $name"
									break
								default:
									assert false, target
							}
						}
					}
				}
			}
		}
	}
}

null