package com.javax0.djcproxy;

import com.javax0.djcproxy.compiler.Compiler;

public class ProxyFactory<Proxy> {

	private CallbackFilter callbackFilter = null;

	public void setCallbackFilter(CallbackFilter callBackFilter) {
		this.callbackFilter = callBackFilter;
	}

	/**
	 * Create a new proxy object.
	 * 
	 * @param originalObject
	 *            the object to be proxied
	 * @return the new proxy object
	 * @throws Exception 
	 */
	public Proxy create(Proxy originalObject, MethodInterceptor interceptor) throws Exception {
		ProxySourceFactory<Proxy> sourceFactory = new ProxySourceFactory<>(callbackFilter);
		String sourceCode = sourceFactory.create(originalObject);
		Compiler compiler = new Compiler();
		compiler.setClassLoader(originalObject.getClass().getClassLoader());
		Class<?> proxyClass = compiler.compile(sourceCode, sourceFactory.getGeneratedClassName());
		ProxySetter proxy = (ProxySetter) proxyClass.newInstance();
		proxy.setPROXY$OBJECT(originalObject);
		proxy.setPROXY$INTERCEPTOR(interceptor);
		return (Proxy)proxy;
	}

}
