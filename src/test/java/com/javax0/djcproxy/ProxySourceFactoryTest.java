package com.javax0.djcproxy;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.djcproxy.exceptions.FinalCanNotBeExtendedException;
import com.javax0.djcproxy.filters.NonObject;

public class ProxySourceFactoryTest {

	@Test
	public void given_ObjectAndCallbackFilter_when_CreatingSource_then_GettingSomeResult()
			throws FinalCanNotBeExtendedException {

		ProxySourceFactory<ProxySourceFactoryTest> factory = new ProxySourceFactory<>(
				new NonObject());
		String s = factory.create(Object.class);
		Assert.assertNotNull(s);
		String className = factory.getGeneratedClassName();
		Assert.assertNotNull(className);
	}

	@Test
	public void given_Object_when_CreatingSource_then_GettingSomeResult()
			throws FinalCanNotBeExtendedException {

		ProxySourceFactory<ProxySourceFactoryTest> factory = new ProxySourceFactory<>(
				null);
		String s = factory.create(Object.class);
		Assert.assertNotNull(s);
		String className = factory.getGeneratedClassName();
		Assert.assertNotNull(className);
	}

	public class C {
	}

	@Test
	public void given_ObjectC_when_CreatingSource_then_GettingSomeResult()
			throws FinalCanNotBeExtendedException {

		ProxySourceFactory<ProxySourceFactoryTest> factory = new ProxySourceFactory<>(
				null);
		String s = factory.create(C.class);
		Assert.assertNotNull(s);
		String className = factory.getGeneratedClassName();
		Assert.assertNotNull(className);
		String generatedPackageName = factory.getGeneratedPackageName();
		Assert.assertEquals("com.javax0.djcproxy", generatedPackageName);
	}

	@Test(expected = FinalCanNotBeExtendedException.class)
	public void given_FinalObject_when_CreatingSource_then_ThrowsException()
			throws FinalCanNotBeExtendedException {

		ProxySourceFactory<ProxySourceFactoryTest> factory = new ProxySourceFactory<>(
				new NonObject());
		String s = factory.create(Integer.class);
	}

}
