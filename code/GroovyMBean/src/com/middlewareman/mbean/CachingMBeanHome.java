package com.middlewareman.mbean;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public abstract class CachingMBeanHome extends MBeanHome {

	private static class MBeanInfoWrapper {

		final MBeanInfo info;
		final Long timeout; // null is no timeout

		MBeanInfoWrapper(MBeanInfo info, Long timeout) {
			this.info = info;
			this.timeout = timeout;
		}

		MBeanInfoWrapper(MBeanInfo info) {
			this(info, null);
		}

	}

	private Map<ObjectName, WeakReference<MBean>> mbeanCache;
	private Map<ObjectName, MBeanInfoWrapper> infoCache;

	public CachingMBeanHome(Object url, boolean mbeanCache, boolean infoCache) {
		super(url);
		setMBeanCache(mbeanCache);
		setInfoCache(infoCache);
	}
	
	public boolean isMBeanCache() {
		return mbeanCache != null;
	}

	public void setMBeanCache(boolean flag) {
		if (flag)
			mbeanCache = new WeakHashMap<ObjectName, WeakReference<MBean>>();
		else
			mbeanCache = null;
	}

	public boolean isInfoCache() {
		return infoCache != null;
	}

	public void setInfoCache(boolean flag) {
		if (flag)
			infoCache = new WeakHashMap<ObjectName, MBeanInfoWrapper>();
		else
			infoCache = null;
	}



	public MBean getMBean(ObjectName objectName)
			throws InstanceNotFoundException, IOException {
		if (mbeanCache != null) {
			synchronized (mbeanCache) {
				WeakReference<MBean> ref = mbeanCache.get(objectName);
				if (ref != null) {
					MBean mbean = ref.get();
					if (mbean != null)
						return mbean;
				}
				MBean mbean = super.getMBean(objectName);
				mbeanCache.put(objectName, new WeakReference<MBean>(mbean));
				return mbean;
			}
		} else
			return super.getMBean(objectName);
	}

	public MBeanInfo getInfo(ObjectName objectName)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException {
		if (infoCache != null) {
			/* If found, return it or remove it. */
			synchronized (infoCache) {
				MBeanInfoWrapper iw = infoCache.get(objectName);
				if (iw != null) {
					if (iw.timeout == null
							|| iw.timeout < System.currentTimeMillis())
						return iw.info;
					infoCache.remove(objectName);
				}
			}
			/* It's not in the cache or has expired. */
			MBeanInfo info = super.getInfo(objectName);
			Boolean immutable = immutable(info);
			Long infoTimeout = infoTimeout(info);
			if (immutable != null && immutable) {
				/* Explicitly immutable: cache forever. */
				synchronized (infoCache) {
					infoCache.put(objectName, new MBeanInfoWrapper(info));
				}
			} else if (infoTimeout != null && infoTimeout > 0L) {
				/* Timeout is specified. */
				Long timeout = infoTimeout + System.currentTimeMillis();
				synchronized (infoCache) {
					/* We don't care if we overwrite because ours is newer. */
					infoCache.put(objectName, new MBeanInfoWrapper(info,
							timeout));
				}
			}
			return info;
		} else
			return super.getInfo(objectName);
	}

	private Boolean immutable(MBeanInfo info) {
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