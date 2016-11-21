package com.javax0.djcproxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.javax0.djcproxy.exceptions.ProxyClassCompilerError;
import com.javax0.jscc.Compiler;

/**
 * 
 * Factory to create new proxy object. To create a proxy object you need a
 * factory that is able to create new class that proxies the original object and
 * also can compile the generated Java source and load the compiled class.
 * <p>
 * Usually there are many object created for the same class and for this reason
 * a factory keeps track of the already generated classes and when a class can
 * be reused it will not be generated again, but the already loaded class will
 * be used.
 * <p>
 * 
 * 
 * @author Peter Verhas
 * 
 * @param <ClassToBeProxied>
 */
public class ProxyFactory<ClassToBeProxied> {

	private ProxyClassCache<ClassToBeProxied> cache = new ProxyClassCache<>();

	private CallbackFilter callbackFilter = new CallbackFilter() {
		@Override
		public boolean accept(Method method) {
			return true;
		}

	};

	/**
	 * Set a valid callback filter.
	 * 
	 * @param callbackFilter
	 *            is the callback filter object and can not be null. When null
	 *            argument is passed {@link IllegalArgumentException} is thrown.
	 */
	public void setCallbackFilter(CallbackFilter callbackFilter) {
		if (callbackFilter == null) {
			throw new IllegalArgumentException(
					"callback filter can not be null");
		}
		this.callbackFilter = callbackFilter;
	}

	private ClassLoader classLoader = null;

	/**
	 * By the default the classloader used to load the original class is used to
	 * load the proxy class. In some cases this does not work, when the original
	 * class loader can not load the class {@see ProxySetter} and/or {@see
	 * MethodInterceptor} classes. This is typically in the weird case when the
	 * you want to proxy some of the system classes. In that case you can set
	 * the classloader calling this method. For example you can
	 * 
	 * <pre>
	 * ProxyFactory&lt;Object&gt; factory = new ProxyFactory&lt;&gt;();
	 * factory.setClassLoader(this.getClass().getClassLoader());
	 * </pre>
	 * 
	 * @param classLoader
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private String source;

	/**
	 * Get the source code that was generated by the factory last time. Since a
	 * factory can be used to generate more than one source class there is no
	 * guarantee that the returned source is the source of the last object
	 * returned. This method is to help debugging and learning how the proxy
	 * factory works. To ensure that the source is the one the caller is
	 * interested in create a new ProxyFactory and use it only once to generate
	 * a proxy object.
	 * 
	 * @return
	 */
	public String getGeneratedSource() {
		return source;
	}

	private String generatedClassName;

	/**
	 * Get the name of the class that was generated last time. The same caveats
	 * hold as in the method {@link #getGeneratedSource()}.
	 * 
	 * @return
	 */
	public String getGeneratedClassName() {
		return generatedClassName;
	}

	private String compilerErrorOutput;

	public String getCompilerErrorOutput() {
		return compilerErrorOutput;
	}

	/**
	 * Create a new proxy object.
	 * 
	 * @param originalObject
	 *            the object to be proxied
	 * @return the new proxy object
	 * @throws Exception
	 *             when the proxy can not be created. This can be for several
	 *             reasons. Identified reasons:
	 * 
	 *             <ul>
	 *             <li>The original object is an instance of a final class
	 *             <li>The original object is an inner class that is private and
	 *             thus can not be extended.
	 *             </ul>
	 */
	public ClassToBeProxied create(ClassToBeProxied originalObject,
			MethodInterceptor interceptor) throws Exception {
		final Class<?> originalClass = originalObject.getClass();
		final ClassLoader classLoader = calculateClassLoader(originalClass);
		Class<?> proxyClass = cache.get(originalObject.getClass(),
				callbackFilter, classLoader);
		if (proxyClass == null) {
			proxyClass = createClass(originalObject.getClass());
			cache.put(originalClass, callbackFilter, classLoader, proxyClass);
		}
		ProxySetter proxy = instantiateProxy(proxyClass);
		proxy.setPROXY$OBJECT(originalObject);
		proxy.setPROXY$INTERCEPTOR(interceptor);
		return cast(proxy);
	}

	private ClassLoader calculateClassLoader(Class<?> originalClass) {
		return classLoader == null ? originalClass.getClassLoader()
				: classLoader;
	}

	/**
	 * Create a new proxy class that is capable proxying an object that is an
	 * instance of the class.
	 * 
	 * @param originalObject
	 * @param interceptor
	 * @return
	 * @throws Exception
	 */
	public Class<?> createClass(Class<?> originalClass) throws Exception {
		ProxySourceFactory<ClassToBeProxied> sourceFactory = new ProxySourceFactory<>(
				callbackFilter);
		source = sourceFactory.create(originalClass);
		generatedClassName = sourceFactory.getGeneratedClassName();
		Compiler compiler = new Compiler();
		compiler.setClassLoader(calculateClassLoader(originalClass));

		String packagePrefix =
			sourceFactory.getGeneratedPackageName() == null ? "" : sourceFactory.getGeneratedPackageName() + ".";
		String classFQN = packagePrefix + sourceFactory.getGeneratedClassName();
		Class<?> proxyClass = compiler.compile(source, classFQN);
		compilerErrorOutput = compiler.getCompilerErrorOutput();
		if (proxyClass == null) {
			throw new ProxyClassCompilerError(compiler.getCompilerErrorOutput());
		}
		return proxyClass;
	}

	/**
	 * Instantiate a proxy object from a proxyClass. Note that the created
	 * object is not initialized properly.
	 * 
	 * @param proxyClass
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public ProxySetter instantiateProxy(Class<?> proxyClass) throws Exception {
		ProxySetter proxy;
		sun.misc.Unsafe unsafe;
		if (proxyClass == null) {
			throw new IllegalArgumentException("proxyClass can not be null");
		}
		try {
			Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			unsafe = (sun.misc.Unsafe) f.get(null);
		} catch (Exception e) {
			unsafe = null;
		}
		// unsafe = null;
		if (unsafe == null) {
			Constructor<?> constructor = proxyClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			proxy = (ProxySetter) constructor.newInstance(new Object[0]);
		} else {
			proxy = (ProxySetter) unsafe.allocateInstance(proxyClass);
		}
		return proxy;
	}

	@SuppressWarnings("unchecked")
	private ClassToBeProxied cast(Object proxy) {
		return (ClassToBeProxied) proxy;
	}
}
