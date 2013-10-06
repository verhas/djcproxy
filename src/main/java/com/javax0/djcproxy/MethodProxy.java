package com.javax0.djcproxy;

public interface MethodProxy {
	Object invoke(Object obj, Object[] args) throws Throwable;
}
