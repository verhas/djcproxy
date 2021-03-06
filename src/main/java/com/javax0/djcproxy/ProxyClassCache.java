package com.javax0.djcproxy;

import java.util.Map;
import java.util.WeakHashMap;

public class ProxyClassCache<ClassToBeProxied> {

	private static class Key {
		ClassLoader classLoader;
		CallbackFilter filter;
		Class<?> originalClass;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((classLoader == null) ? 0 : classLoader.hashCode());
			result = prime * result
					+ ((filter == null) ? 0 : filter.hashCode());
			result = prime * result
					+ ((originalClass == null) ? 0 : originalClass.hashCode());
			return result;
		}

		private boolean safeEquals(Object a, Object b) {
			if (a == null && b == null)
				return true;
			if (a == null)
				return false;
			return a.equals(b);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Key other = (Key) obj;
			if (!safeEquals(classLoader, other.classLoader))
				return false;
			if (!safeEquals(filter, other.filter))
				return false;
			if (!safeEquals(originalClass, other.originalClass))
				return false;
			return true;
		}
	}

	private final Map<Key, Class<?>> cache = new WeakHashMap<>();

	public Class<?> get(final Class<?> originalClass,
			final CallbackFilter filter, final ClassLoader classLoader) {
		final Key key = new Key();
		key.originalClass = originalClass;
		key.filter = filter;
		key.classLoader = classLoader;
		final Class<?> proxyClass = cache.get(key);
		return proxyClass;
	}

	public void put(final Class<?> originalClass, final CallbackFilter filter,
			final ClassLoader classLoader, final Class<?> proxyClass) {
		final Key key = new Key();
		key.originalClass = originalClass;
		key.filter = filter;
		key.classLoader = classLoader;
		cache.put(key, proxyClass);
	}
}
