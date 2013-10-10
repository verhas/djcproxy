package com.javax0.djcproxy.interceptors;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javax0.djcproxy.MethodInterceptor;
import com.javax0.djcproxy.MethodProxy;

public class LoggingInterceptor implements MethodInterceptor {
	Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy mproxy) throws Throwable {
		LOG.trace("Method {}.{} started", method.getDeclaringClass()
				.getCanonicalName(), method.getName());
		Object retval = mproxy.invoke(obj, args);
		LOG.trace("Method {}.{} finished", method.getDeclaringClass()
				.getCanonicalName(), method.getName());
		return retval;
	}

}
