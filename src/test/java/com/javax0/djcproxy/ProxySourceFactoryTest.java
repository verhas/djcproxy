package com.javax0.djcproxy;

import org.junit.Test;

import com.javax0.djcproxy.exceptions.FinalCanNotBeExtendedException;
import com.javax0.djcproxy.filters.NonObject;

public class ProxySourceFactoryTest {

	@Test
	public void given_Object_when_CreatingSource_then_GettingResult()
			throws FinalCanNotBeExtendedException {

		ProxySourceFactory<ProxySourceFactoryTest> factory = new ProxySourceFactory<>(
				new NonObject());
		String s = factory.create(Object.class);
		
		System.out.println(s);

	}

}
