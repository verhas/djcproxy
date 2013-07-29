package com.javax0.djcproxy;

import java.lang.reflect.Method;

import org.junit.Test;

public class ProxyFactoryTest {


	public static class A {
		public int method() {
			System.out.println("A.method()");
			return 1;
		}
	}

	public class Interceptor implements MethodInterceptor {

		@Override
		public Object intercept(Object obj, Method method, Object[] args)
				throws Exception {
			System.out.println("interceptor() "+method.getName());
			return method.invoke(obj, args);
		}

	}

	@Test
	public void testGivenObjectWhenCreatingSourceThenGettingResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		A s = factory.create(a, new Interceptor());
		System.out.println(s);
		s.method();

	}

}
