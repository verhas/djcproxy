package com.javax0.djcproxy;

import java.lang.reflect.Method;

import org.junit.Test;

public class ProxyFactoryTest {

	public class A {
		public void method() {
			System.out.println("A.method()");
		}
	}

	public class Interceptor implements MethodInterceptor {

		@Override
		public Object intercept(Object obj, Method method, Object[] args)
				throws Exception {
			System.out.println("interceptor()");
			method.invoke(obj, args);
			return null;
		}

	}

	@Test
	public void testGivenObjectWhenCreatingSourceThenGettingResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		A s = factory.create(a, null);
		System.out.println(s);

	}

}
