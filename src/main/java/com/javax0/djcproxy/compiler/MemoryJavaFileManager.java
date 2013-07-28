package com.javax0.djcproxy.compiler;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class MemoryJavaFileManager extends
		ForwardingJavaFileManager<StandardJavaFileManager> {
	private MemoryFileObject classFile;

	protected MemoryJavaFileManager(final StandardJavaFileManager fileManager) {
		super(fileManager);
	}

	public MemoryFileObject getClassFile() {
		return classFile;
	}

	@Override
	public ClassLoader getClassLoader(final Location location) {
		return super.getClassLoader(location);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(final Location location,
			final String className, final Kind kind, final FileObject sibling) throws IOException {
		classFile = new MemoryFileObject(className);
		return classFile;
	}

}
