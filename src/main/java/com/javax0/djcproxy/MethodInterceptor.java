package com.javax0.djcproxy;

import java.lang.reflect.Method;

public interface MethodInterceptor {
	Object intercept(Object obj, Method method, Object[] args) throws Exception;
}
