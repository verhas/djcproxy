package com.javax0.djcproxy;

import java.lang.reflect.Method;

public interface MethodInterceptor {
	/**
	 * This method is called by the proxy object whenever a method is invoked.
	 * 
	 * @param obj the original object
	 * @param method the method that was invoked in the proxy object
	 * @param args the arguments that were passed to the method call on the proxy object
	 * @return should return an object that is compatible with the type returned by the original method
	 * @throws Exception
	 */
	Object intercept(Object obj, Method method, Object[] args) throws Exception;
}
