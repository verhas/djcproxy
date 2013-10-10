package com.javax0.djcproxy.interceptors;

import java.lang.reflect.Method;

import com.javax0.djcproxy.MethodInterceptor;
import com.javax0.djcproxy.MethodProxy;

public class RuntimeExceptionInterceptor implements MethodInterceptor {
	private static final RuntimeExceptionInterceptor INSTANCE = new RuntimeExceptionInterceptor();

	public static RuntimeExceptionInterceptor getInstance() {
		return INSTANCE;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy mproxy) throws Throwable {
		throw new RuntimeException();
	}

}
