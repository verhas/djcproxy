package com.javax0.djcproxy;

import java.lang.reflect.Method;

public interface CallbackFilter {
	boolean accept(Method method);
}
