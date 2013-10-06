package com.javax0.djcproxy;

import java.lang.reflect.Method;

public interface MethodInterceptor {
	/**
	 * This method is called by the proxy object whenever a method is invoked.
	 * 
	 * @param obj
	 *            the original object
	 * @param method
	 *            the method that was invoked in the proxy object
	 * @param args
	 *            the arguments that were passed to the method call on the proxy
	 *            object
	 * @param mproxy
	 *            the method proxy that can be used to call the method on the
	 *            original object or on just any other object of the same type
	 * @return should return an object that is compatible with the type returned
	 *         by the original method
	 * @throws Exception
	 */
	Object intercept(Object obj, Method method, Object[] args,
			MethodProxy mproxy) throws Throwable;
}
