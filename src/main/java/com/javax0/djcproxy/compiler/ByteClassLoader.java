package com.javax0.djcproxy.compiler;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class loader that loads a class from a byte array. This loader can load one
 * single class for which the binary class file is passed as constructor
 * argument.
 * 
 * @author Peter Verhas
 * 
 */
public class ByteClassLoader extends URLClassLoader {
	private byte[] classFile;

	/**
	 * @param urls
	 *            passed to the super constructor.
	 * @param parent
	 *            passed to the super constructor. For more information see
	 *            {@link URLClassLoader#URLClassLoader(URL[], ClassLoader)}.
	 * @param classFile
	 *            the byte array that contains the compiled binary class file.
	 */
	public ByteClassLoader(URL[] urls, ClassLoader parent,
			final byte[] classFile) {
		super(urls, parent);
		this.classFile = classFile;
	}

	@Override
	protected Class<?> findClass(final String name)
			throws ClassNotFoundException {
		if (classFile != null) {
			Class<?> klass = defineClass(name, classFile, 0, classFile.length);
			releaseClassFile();
			return klass;
		}
		return super.findClass(name);
	}

	/**
	 * The class loader remains in memory so long as long the loaded class
	 * remains in memory but the source byte array that was used to load the
	 * code of the class is not needed anymore.
	 */
	private void releaseClassFile() {
		classFile = null;
	}

}
