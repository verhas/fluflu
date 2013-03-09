package com.javax0.fluflu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FluClassLoader extends ClassLoader {
	final String bin;

	public FluClassLoader(ClassLoader parent, String bin) {
		super(parent);
		this.bin = bin;
	}

	@Override
	public Class<?> loadClass(String core) throws ClassNotFoundException {
		String fileName = bin + "/" + core.replaceAll("\\.", "/") + ".class";
		File file = new File(fileName);
		if (file.exists()) {
			final byte[] classBytes = new byte[(int) file.length()];
			try (InputStream is = new FileInputStream(file)) {
				is.read(classBytes);
			} catch (IOException e) {
				return super.loadClass(core);
			}
			Class<?> klass = defineClass(core, classBytes, 0, classBytes.length);
			if (klass != null) {
				resolveClass(klass);
				return klass;
			} else {
				return super.loadClass(core);
			}
		}
		return super.loadClass(core);
	}
}
