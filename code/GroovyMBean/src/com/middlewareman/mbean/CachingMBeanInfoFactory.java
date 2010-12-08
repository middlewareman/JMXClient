/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.util.Map;

import javax.management.*;

import org.apache.commons.collections.map.ReferenceMap;

/**
 * MbeanInfoFactory proxy that maintains a memory-sensitive cache of
 * {@link MBeanInfo} instances. These instances themselves may specify caching
 * behaviour in {@link Descriptor} as follows: <blockquote>
 * <table border="1" cellpadding="5">
 * <tr>
 * <th>Name</th>
 * <th>Meaning</th>
 * </tr>
 * <tr>
 * <td><a name="immutableInfo"><i>immutableInfo</i></a>
 * <td>The string {@code "true"} or {@code "false"} according as this MBean's
 * MBeanInfo is <em>immutable</em>. When this field is true, the MBeanInfo for
 * the given MBean is guaranteed not to change over the lifetime of the MBean.
 * Hence, a client can read it once and cache the read value. When this field is
 * false or absent, there is no such guarantee, although that does not mean that
 * the MBeanInfo will necessarily change.</td>
 * <tr>
 * <td>infoTimeout</td>
 * <td>The time in milli-seconds that the MBeanInfo can reasonably be expected
 * to be unchanged. The value can be a {@code Long} or a decimal string. This
 * provides a hint from a DynamicMBean or any MBean that does not define
 * {@code immutableInfo} as {@code true} that the MBeanInfo is not likely to
 * change within this period and therefore can be cached. When this field is
 * missing or has the value zero, it is not recommended to cache the MBeanInfo
 * unless it has the {@code immutableInfo} set to {@code true}.</td>
 * </tr>
 * </table>
 * </blockquote> The specification above states that it is not recommended to
 * cache unless explicitly immutable or a positive timeout is specified. This is
 * overly restrictive in most cases, and it is therefore possible to specify a
 * default timeout that is used when both descriptor fields are absent.
 * <table border="1" cellpadding="5">
 * <tr>
 * <th>defaultTimeout</th>
 * <th>Meaning</th>
 * <tr>
 * <td><code>== null</code></td>
 * <td>cache forever</td>
 * </tr>
 * <tr>
 * <td><code>== 0L</code></td>
 * <td>don't cache at all (default according to specification)</td>
 * </tr>
 * <tr>
 * <td><code>&gt; 0L</code></td>
 * <td>cache for specified time in milliseconds (time to live)</td>
 * </tr>
 * </table>
 * Overriding the default <code>defaultTimeout == 0L</code> may provide
 * performance gains in some cases.
 * 
 * @author Andreas Nyberg
 */
public class CachingMBeanInfoFactory implements MBeanInfoFactory {

	private static class CacheEntry {

		final MBeanInfo info;
		final Long expiry;

		CacheEntry(MBeanInfo info) {
			this.info = info;
			this.expiry = null;
		}

		CacheEntry(MBeanInfo info, long timeToLive) {
			this.info = info;
			this.expiry = System.currentTimeMillis() + timeToLive;
		}

	}

	private final MBeanInfoFactory delegate;

	private int hits, misses, discards;

	@SuppressWarnings("unchecked")
	private Map<ObjectName, CacheEntry> cache = new ReferenceMap();

	private Long defaultTimeout = 0L;

	public CachingMBeanInfoFactory(MBeanInfoFactory delegate) {
		this(delegate, 0L);
	}

	public CachingMBeanInfoFactory(MBeanInfoFactory delegate,
			Long defaultTimeout) {
		this.delegate = delegate;
		this.defaultTimeout = defaultTimeout;
	}

	public synchronized String toString() {
		return getClass().getSimpleName() + "(size=" + cache.size() + ", hits="
				+ hits + ", misses=" + misses + ", discards=" + discards + ")";
	}

	public synchronized void clear() {
		cache.clear();
		hits = misses = discards = 0;
	}

	public MBeanInfo createMBeanInfo(ObjectName objectName)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException {
		/* If found, return if fresh or remove if stale. */
		synchronized (this) {
			CacheEntry entry = cache.get(objectName);
			if (entry != null) {
				if (entry.expiry == null
						|| entry.expiry < System.currentTimeMillis()) {
					++hits;
					return entry.info;
				} else {
					++discards;
					cache.remove(objectName);
				}
			} else {
				++misses;
			}
		}
		/* Retrieve fresh. */
		MBeanInfo info = delegate.createMBeanInfo(objectName);
		/* Figure how to cache. */
		Boolean immutableInfo = immutableInfo(info);
		Long infoTimeout = infoTimeout(info);
		CacheEntry entry = null;
		if (immutableInfo == null && infoTimeout == null) {
			/* Unspecified in MBeanInfo: use our default. */
			if (defaultTimeout != null) {
				entry = new CacheEntry(info, System.currentTimeMillis()
						+ defaultTimeout);
			} else {
				entry = new CacheEntry(info);
			}
		} else if (immutableInfo != null && immutableInfo) {
			/* Explicitly immutable: cache forever. */
			entry = new CacheEntry(info);
		} else if (infoTimeout != null && infoTimeout > 0L) {
			entry = new CacheEntry(info, System.currentTimeMillis()
					+ infoTimeout);
		}
		if (entry != null) {
			synchronized (this) {
				/* We don't care if we overwrite because ours is probably newer. */
				cache.put(objectName, entry);
			}
		} else {
			// TODO logging
			System.err
					.println("CachingMBeanInfoFactory not caching immutableInfo="
							+ immutableInfo
							+ ", infoTimeout="
							+ infoTimeout
							+ ", defaultTimeout="
							+ defaultTimeout
							+ " for "
							+ objectName);
		}
		return info;
	}

	private Boolean immutableInfo(MBeanInfo info) {
		String immutableInfo = (String) info.getDescriptor().getFieldValue(
				"immutableInfo");
		if (immutableInfo == null)
			return null;
		return Boolean.parseBoolean(immutableInfo);
	}

	private Long infoTimeout(MBeanInfo info) {
		Object infoTimeout = info.getDescriptor().getFieldValue("infoTimeout");
		if (infoTimeout == null)
			return null;
		if (infoTimeout instanceof Long)
			return ((Long) infoTimeout);
		if (infoTimeout instanceof String)
			return Long.parseLong((String) infoTimeout);
		throw new IllegalArgumentException(infoTimeout.toString());
	}

}
