/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.weblogic.DomainRuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 

new ExceptionHandler(binding).wrap {
	
	if (params.url) {
		def hf = new WebLogicMBeanHomeFactory(url:params.url,username:params.username,password:params.password)
		def drs = new DomainRuntimeServer(hf)
		drs.home.ping()	// Provoke exception
		request.getSession(true).domainRuntimeServer = drs
		redirect 'DomainRuntimeBrowser.groovy'
	} else if (params.logout) {
		session.removeAttribute('domainRuntimeServer')
	}
	
	def currentRemote = session?.domainRuntimeServer
	html.html {
		head { title 'Remote DomainRuntimeServer Login' }
		body {
			h1 'Remote DomainRuntimeServer Login'
			h2 'Current remote connection'
			if (currentRemote) {
				pre currentRemote.home
				ul {
					li { a href:'?logout=yes', "Log out" }
					li { a href:'DomainRuntimeBrowser.groovy', "Proceed" }
				}
			} else {
				div 'Not logged in to a remote server'
				a(href:'DomainRuntimeBrowser.groovy', 'Proceed to local server')
			}
			h2 'Log in to a remote server'
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
						td(span:2) {  input(type:'submit')  }
					}
				}
			}
		}
	}
}
