/* $Id$ */
/**
 * Report server instances by associated machine.
 * 
 * @author Andreas Nyberg
 */
def domain = domainRuntimeService.DomainConfiguration
def domainName = domain.Name
for (server in domain.Servers) {
    println "$domainName\t$server.Name\t$server.SelfTuningThreadPoolSizeMax\t${server.Machine?.Name}"
}