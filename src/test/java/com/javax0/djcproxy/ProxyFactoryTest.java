package com.javax0.djcproxy;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

public class ProxyFactoryTest {

	protected static class A {
		public A() {
		}

		public int method() {
			return 1;
		}
	}

	private class Interceptor implements MethodInterceptor {

		@Override
		public Object intercept(Object obj, Method method, Object[] args)
				throws Exception {
			return 0;
		}

	}

	@Test
	public void given_Object_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		A s = factory.create(a, new Interceptor());
		String genSource = factory.getGeneratedSource();
		System.out.println(genSource);
		System.out.println(s);
		Assert.assertEquals(0, s.method());
	}

	@Test
	public void given_Object_when_CreatingSourceWithFilter_then_GettingOriginalResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		factory.setCallbackFilter(new CallbackFilter() {

			@Override
			public boolean accept(Method method) {
				return false;
			}
		});
		A s = factory.create(a, new Interceptor());
		String genSource = factory.getGeneratedSource();
		System.out.println(genSource);
		System.out.println(s);
		Assert.assertEquals(1, s.method());
	}
}
