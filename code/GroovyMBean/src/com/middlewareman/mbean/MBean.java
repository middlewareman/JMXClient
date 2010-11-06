package com.middlewareman.mbean;

import java.io.IOException;

import javax.management.ObjectName;

public class MBean {

	public final MBeanHome home;
	public final ObjectName objectName;

	public MBean(MBeanHome home, ObjectName objectName) {
		this.home = home;
		this.objectName = objectName;
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + objectName + ")";
	}

	public int hashCode() {
		return objectName.hashCode();
	}

	public boolean equals(Object other) {
		if (other instanceof MBean) {
			MBean mother = (MBean) other;
			return objectName.equals(mother.objectName)
					&& home.equals(mother.home);
		} else
			return false;
	}

	boolean asBoolean() {
		try {
			return home.getMBeanServerConnection().isRegistered(objectName);
		} catch (IOException e) {
			// TODO Log error?
			return false;
		}
	}

}
