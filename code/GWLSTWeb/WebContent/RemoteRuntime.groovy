/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 

new ExceptionHandler(binding).wrap {
	
	if (params.url) {
		def hf = new WebLogicMBeanHomeFactory(url:params.url,username:params.username,password:params.password)
		def rs = new RuntimeServer(hf)
		rs.home.ping()	// Provoke exception
		request.getSession(true).runtimeServer = rs
		redirect 'RuntimeBrowser.groovy'
	} else if (params.logout) {
		session.removeAttribute('runtimeServer')
	}
	
	def currentRemote = session?.runtimeServer
	html.html {
		head { title 'Remote Runtime Login' }
		body {
			h1 'Remote RuntimeServer Login'
			h2 'Current remote connection'
			if (currentRemote) {
				pre currentRemote.home
				a(href:'?logout=yes', "Log out of $currentRemote.home.serverId")
			} else {
				div 'Not logged in to a remote server'
				a(href:'RuntimeBrowser.groovy', 'Proceed to local server')
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
