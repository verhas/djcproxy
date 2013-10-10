package com.javax0.djcproxy.interceptors;

import java.lang.reflect.Method;

import com.javax0.djcproxy.MethodInterceptor;
import com.javax0.djcproxy.MethodProxy;

public class IgnoringInterceptor implements MethodInterceptor {

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy mproxy) throws Throwable {
		return null;
	}

}
