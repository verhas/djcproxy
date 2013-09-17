package com.javax0.djcproxy;

import java.lang.reflect.Method;

/**
 * A callback filter controls which methods are intercepted by the proxy.
 * <p>
 * Note that a callback filter object should be time invariant. If the method
 * {@see #accept(Method)} return X for a method once then it should return X on
 * subsequent calls. This is required because {@see ProxyFactory} objects cache
 * the proxy classes and the callback filter may not be consulted on subsequent
 * calls. If the filter is not time invariant the invariance may not have
 * effect.
 * 
 * @author Peter Verhas
 * 
 */
public interface CallbackFilter {
	/**
	 * Implementation should return true if the method has to be intercepted by
	 * the proxy. If the method returns false then the method will be
	 * transparently proxied and not affected by an interceptor.
	 * 
	 * @param method
	 * @return
	 */
	boolean accept(Method method);
}
