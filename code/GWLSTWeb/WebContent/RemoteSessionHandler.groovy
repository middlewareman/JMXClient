/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.weblogic.access.HttpServletRequestAdapter 

import groovy.xml.MarkupBuilder

new ExceptionHandler(binding).wrap {
	response.bufferSize = 32<<10
	def html = new MarkupBuilder(response.writer)
	
	def adapter = new HttpServletRequestAdapter(request)
	adapter.logoutAll()
	adapter.loginAll()
	html.html {
		head { title 'Remote Session Handler' }
		body {
			h1 'Remote Session Handler'
			h2 'Current remote connections'
			table(border:1) {
				tr {
					th 'Service'
					th 'Status'
				}
				tr {
					td 'runtimeServer'
					td adapter.remoteRuntimeServer?.home
				}
				tr {
					td 'domainRuntimeServer'
					td adapter.remoteDomainRuntimeServer?.home
				}
				tr {
					td 'editServer'
					td adapter.remoteEditServer?.home
				}
			}
			h2 'Log out'
			a href:'?logout=please', 'Log out of all'
			h2 'Log in'
			form {
				table {
					tr {
						td 'URL'
						td {
							input(name:'url', type:'text')
						}
					}
					tr {
						td 'Username'
						td {
							input(name:'username', type:'text')
						}
					}
					tr {
						td 'Password'
						td {
							input(name:'password', type:'password')
						}
					}
					tr {
						td(colspan:2) {  input(type:'submit') }
					}
				}
			}
			h2 'Proceed'
			ul {
				li { a href:'RuntimeBrowser.groovy', 'RuntimeServer' }
				li { a href:'DomainRuntimeBrowser.groovy', 'DomainRuntimeServer' }
				li { a href:'EditBrowser.groovy', 'EditServer' }
			}
		}
	}
}