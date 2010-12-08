/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;

import org.apache.commons.collections.map.ReferenceMap;

/**
 * MBeanFactory proxy that maintains known {@link MBean} instances in a
 * memory-sensitive cache.
 * 
 * @author Andreas Nyberg
 */
public class CachingMBeanFactory implements MBeanFactory {

	private final MBeanFactory delegate;

	@SuppressWarnings("unchecked")
	private final Map<ObjectName, MBean> cache = new ReferenceMap();

	private int hits, misses;

	public CachingMBeanFactory(MBeanFactory delegate) {
		this.delegate = delegate;
	}

	public synchronized String toString() {
		return getClass().getSimpleName() + "(size=" + cache.size() + ", hits="
				+ hits + ", misses=" + misses + ")";
	}

	public synchronized void clear() {
		cache.clear();
		hits = misses = 0;
	}

	public MBean createMBean(ObjectName objectName)
			throws InstanceNotFoundException, IOException {
		synchronized (this) {
			MBean mbean = cache.get(objectName);
			if (mbean != null) {
				++hits;
				return mbean;
			} else {
				++misses;
			}
		}
		MBean mbean = delegate.createMBean(objectName);
		synchronized (this) {
			cache.put(objectName, mbean);
		}
		return mbean;
	}

}
