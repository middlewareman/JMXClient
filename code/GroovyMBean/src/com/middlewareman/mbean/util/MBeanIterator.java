/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.util;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;

import com.middlewareman.groovy.util.StackTraceCleaner;
import com.middlewareman.mbean.MBean;
import com.middlewareman.mbean.info.SimpleAttributeFilter;

/**
 * Iterates breadth-first over all children reachable from a parent MBean. Note
 * that the ObjectNames returned from the server are not checked, so it is
 * possible that {@link #next()} returns an {@link MBean} that is not registered
 * in the server.
 * 
 * @author Andreas Nyberg
 */
public class MBeanIterator implements Iterator<MBean> {

	private static final Logger logger = Logger.getLogger(MBeanIterator.class
			.getName());

	private final SimpleAttributeFilter filter;
	private Set<ObjectName> visited = new LinkedHashSet<ObjectName>();
	private LinkedList<MBean> queue = new LinkedList<MBean>();

	/**
	 * Creates an iterator with default filter (no deprecated references).
	 */
	public MBeanIterator(MBean mbean) {
		filter = new SimpleAttributeFilter();
		filter.setDeprecated(false);
		filter.setReadable(true);
		filter.setMbeans(true);
		filter.setNullValue(false);
		load(mbean);
	}

	/**
	 * Creates an iterator with custom filter.
	 */
	public MBeanIterator(MBean mbean, SimpleAttributeFilter filter) {
		this.filter = filter;
		load(mbean);
	}

	private void load(MBean parent) {
		if (visited.add(parent.objectName)) {
			Map<String, ?> map;
			try {
				map = MBeanProperties.get(parent.home, parent.objectName,
						filter);
			} catch (InstanceNotFoundException e) {
				logger.log(Level.FINE, "Could not find " + parent);
				return;
			} catch (Exception e) {
				StackTraceCleaner.getDefaultInstance().deepClean(e);
				logger.log(Level.WARNING, "Could not load " + parent, e);
				return;
			}
			for (String name : map.keySet()) {
				Object value = map.get(name);
				if (value != null) {
					if (value instanceof MBean) {
						add((MBean) value);
					} else if (value instanceof MBean[]) {
						for (MBean mbean : (MBean[]) value)
							add(mbean);
					} else if (value instanceof List) {
						for (Object item : (List<?>) value)
							if (item instanceof MBean)
								add((MBean) item);
					} else {
						if (logger.isLoggable(Level.FINE)) {
							logger.log(Level.FINE,
									"ignoring unexpected attribute type: "
											+ value.getClass().getName()
											+ " value:" + value);
						}
					}
				}
			}
		}
	}

	private void add(MBean mbean) {
		if (mbean != null && !visited.contains(mbean.objectName))
			queue.addLast(mbean);
	}

	/**
	 * @see Iterator#hasNext()
	 */
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	/**
	 * @see Iterator#next()
	 */
	public MBean next() {
		MBean mbean = queue.removeFirst();
		load(mbean);
		return mbean;
	}

	/**
	 * Not supported.
	 * 
	 * @see Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
