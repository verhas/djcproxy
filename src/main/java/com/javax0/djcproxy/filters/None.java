package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;

import com.javax0.djcproxy.CallbackFilter;

/**
 * Callback filter that does not accept any method. There may be some use of
 * this class.
 * 
 * @author Peter Verhas
 * 
 */
public class None implements CallbackFilter {

	@Override
	public boolean accept(Method method) {
		return false;
	}

}
