package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;

import com.javax0.djcproxy.CallbackFilter;
/**
 * Callback filter that accepts all methods.
 * 
 * @author Peter Verhas
 *
 */
public class All implements CallbackFilter{

	@Override
	public boolean accept(Method method) {
		return true;
	}

}
