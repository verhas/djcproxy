package com.javax0.djcproxy;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class InMemoryOutputJavaFileManager extends
		ForwardingJavaFileManager<StandardJavaFileManager> {
	private MemoryFileObject classFile;

	protected InMemoryOutputJavaFileManager(StandardJavaFileManager fileManager) {
		super(fileManager);
	}

	public MemoryFileObject getClassFile() {
		return classFile;
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		return super.getClassLoader(location);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException {
		classFile = new MemoryFileObject(className);
		return classFile;
	}

}
