package com.javax0.djcproxy;

import java.io.IOException;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class StringFileManager extends
		ForwardingJavaFileManager<JavaFileManager> {
	private final JavaFileManager extendedFileManager;

	protected StringFileManager(JavaFileManager fileManager) {
		super(fileManager);
		extendedFileManager = fileManager;
	}

	public JavaFileObject getJavaFileForInput(
			JavaFileManager.Location location, String className,
			JavaFileObject.Kind kind) throws IOException {
		final JavaFileObject returnValue;

		if (JavaFileObject.Kind.SOURCE.equals(kind)
				&& "__DUMMY__".equals(className)) {
			returnValue = extendedFileManager.getJavaFileForInput(location,
					className, kind);
		} else {
			returnValue = extendedFileManager.getJavaFileForInput(location,
					className, kind);
		}
		return returnValue;
	}

}
