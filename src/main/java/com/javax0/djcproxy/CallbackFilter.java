package com.javax0.djcproxy;

import java.lang.reflect.Method;

/**
 * A callback filter controls which methods are proxied directly without any intervention in the generated class and which methods are intercepted.
 * @author verhasp
 *
 */
public interface CallbackFilter {
	boolean accept(Method method);
}
