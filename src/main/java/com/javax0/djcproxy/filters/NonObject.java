package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;

import com.javax0.djcproxy.CallbackFilter;

/**
 * A callback filter that accepts all methods that are implemented above the
 * Object level.
 * 
 * @author Peter Verhas
 * 
 */
public class NonObject implements CallbackFilter {

	public boolean accept(Method method) {
		return method.getDeclaringClass() != Object.class;
	}

}
