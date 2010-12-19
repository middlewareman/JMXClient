/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.util;

import java.io.IOException;
import java.util.*;

import javax.management.*;

import com.middlewareman.mbean.MBean;
import com.middlewareman.mbean.type.SimpleAttributeFilter;

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
		filter.setNoNullValue(true);
		filter.setBulk(true);
		load(mbean);
	}

	public MBeanIterator(MBean mbean, SimpleAttributeFilter filter)
			throws Exception {
		this.filter = filter;
		load(mbean);
	}

	private void load(MBean parent) throws InstanceNotFoundException,
			IntrospectionException, AttributeNotFoundException,
			ReflectionException, MBeanException, IOException {
		if (visited.add(parent.objectName) && parent.asBoolean()) {
			Map<String, ?> map = parent.home.getProperties(parent.objectName,
					filter);
			for (String name : map.keySet()) {
				// TODO logging
				Object value = map.get(name);
				if (value != null) {
					if (value instanceof MBean) {
						add((MBean) value);
					} else if (value instanceof MBean[]) {
						for (MBean mbean : (MBean[]) value)
							add(mbean);
					} else if (value instanceof List) {
						for (Object item : (List) value)
							if (item instanceof MBean)
								add((MBean) item);
					}
				} else {
					System.err.println("MBeanIterator UNEXPECTED "
							+ value.getClass().getName() + " " + value);
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
		if (mbean.asBoolean()) {
			try {
				load(mbean);
			} catch (Exception e) {
				e.fillInStackTrace();
				e.printStackTrace();
				// TODO logging
			}
		} else {
			// TODO logging
			System.err.println(getClass().getName()
					+ " next() returning false mbean " + mbean);
		}
		return mbean;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
