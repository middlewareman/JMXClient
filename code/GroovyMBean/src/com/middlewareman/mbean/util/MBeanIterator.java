/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.util;

import java.io.IOException;
import java.util.*;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.middlewareman.mbean.MBean;
import com.middlewareman.mbean.type.SimpleAttributeFilter;
import com.middlewareman.mbean.type.AttributeFilter.OnException;

/**
 * Iterates breadth-first over all children reachable from a parent MBean.
 * 
 * @author Andreas Nyberg
 */
public class MBeanIterator implements Iterator<MBean> {

	public final SimpleAttributeFilter filter;
	private Set<ObjectName> visited = new LinkedHashSet<ObjectName>();
	private LinkedList<MBean> queue = new LinkedList<MBean>();

	public MBeanIterator(MBean mbean) throws Exception {
		filter = new SimpleAttributeFilter();
		filter.setNoDeprecated(true);
		filter.setOnlyReadable(true);
		filter.setOnlyMBeans(true);
		filter.setOnException(OnException.RETURN);
		load(mbean);
	}

	public MBeanIterator(MBean mbean, SimpleAttributeFilter filter)
			throws Exception {
		this.filter = filter;
		load(mbean);
	}

	private void load(MBean parent) throws InstanceNotFoundException,
			IntrospectionException, ReflectionException, IOException {
		if (visited.add(parent.objectName)) {
			Map<String, ?> map = parent.home.getProperties(parent.objectName,
					filter);
			for (String name : map.keySet()) {
				// TODO logging
				Object value = map.get(name);
				if (value != null) {
					if (value instanceof MBean) {
						add((MBean) value);
					} else if (value instanceof MBean[]) {
						for (MBean mbean : (MBean[]) value) {
							add(mbean);
						}
					} else {
						System.out.println("UNEXPECTED "
								+ value.getClass().getName());
					}
				}
			}
		}
	}

	void add(MBean mbean) {
		if (!visited.contains(mbean.objectName))
			queue.addLast(mbean);
	}

	public boolean hasNext() {
		return !queue.isEmpty();
	}

	public MBean next() {
		MBean mbean = queue.removeFirst();
		try {
			load(mbean);
		} catch (Exception e) {
			// TODO logging
			e.printStackTrace();
		}
		return mbean;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
