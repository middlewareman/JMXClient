package com.middlewareman.mbean;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.security.auth.Subject;

public abstract class MBeanHomeFactory {

	public String url;
	public Subject subject;
	public boolean reconnecting = true;

	public MBeanHome createMBeanHome(String urlPart) throws IOException {
		//JMXServiceURL surl = surl(urlPart);
		//System.err.println(surl);
		MBeanHome home = new ConnectingMBeanHome(surl(urlPart), env(), subject);
		if (!reconnecting)
			home = new ConnectedMBeanHome(url, home.getMBeanServerConnection());
		// TODO Settings on home?
		return home;
	}

	public abstract JMXServiceURL surl(String path);

	public Map<String, ?> env() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (url != null)
			map.put(Context.PROVIDER_URL, url);
		return map;
	}

}
