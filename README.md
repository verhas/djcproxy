Dynamic Java Proxy Generator
============================

Using this library you can create proxy objects to an already existing object during run time. The use of this library is very similar to the use of cglib with the following differences:

* Using this library you can create a proxy to an already existing object, while cglib helps you to create a new object along with the extending proxy object.
* This library creates Java source on the file and does not do JVM byte manipulation.

An example use of this library is to create immutable objects from an existing object. The immutable version will call the original method transparently so long as long the 


Aí proxy object is an object that is an instance of a class that extends the class of the original object and can be used in the place of the original object, however instead calling the original methods of the original object the proxy object calls an interceptor method. The interceptor method can implement any functionality and can even call the original method.

To get a proxy object the caller can create a `ProxyFactory` and use the factory to generate proxy objects.

```
ProxyFactory<A> factory = new ProxyFactory<>();
A s = factory.create(a, new Interceptor());
```

The first argument to the factory method `create()` is the original object, the second is an interceptor. The interceptor object implements the interface `MethodInterceptor` that has a single method, `intercept()`. This method is called whenever any of the original objects method is executed. The `MethodInterceptor` interface is very simple:

```
public interface MethodInterceptor {
	Object intercept(Object obj, Method method, Object[] args) throws Exception;
}
```



In the following example the test method extends the class `A`. The class interceptor is a SAM (single abstract method) that can be defined as a closure in Java 8. The interceptor is invoked by the proxy object and the interceptor method can call the original method zero or more times and perform other tasks as well at the discretion of the interceptor.

```
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
			if (method.getName().equals("toString")) {
				return "interceptedToString";
			}
			return 0;
		}

	}

	@Test
	public void given_Object_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		A s = factory.create(a, new Interceptor());
		Assert.assertEquals("interceptedToString", s.toString());
		Assert.assertEquals(0, s.method());
	}
```

The sample above is from the unit test that documents the usage of the 

