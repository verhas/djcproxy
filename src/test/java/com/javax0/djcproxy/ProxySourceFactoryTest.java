package com.javax0.djcproxy;

import org.junit.Test;

import com.javax0.djcproxy.exceptions.FinalCanNotBeExtendedException;

public class ProxySourceFactoryTest {

	@Test
	public void testGivenObjectWhenCreatingSourceThenGettingResult()
			throws FinalCanNotBeExtendedException {

		ProxySourceFactory<ProxySourceFactoryTest> factory = new ProxySourceFactory<>(
				null);
		String s = factory.create(this);
		System.out.println(s);

	}

}
