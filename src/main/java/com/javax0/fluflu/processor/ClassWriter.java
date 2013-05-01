package com.javax0.fluflu.processor;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import com.javax0.aptools.FromThe;

/**
 * Instance of this class can be used to write the string content into a
 * generated source file.
 * 
 * @author Peter Verhas
 * 
 */
public class ClassWriter {
	private final ProcessingEnvironment processingEnv;

	/**
	 * Create a new instance of the class for a specific processing environment.
	 * The class is usually instantiated from an annotation processor that
	 * `extends javax.annotation.processing.AbstractProcessor` and this way the
	 * variable `processingEnv` is available and has to passed as parameter to
	 * this constructor.
	 * 
	 * @param processingEnv
	 */
	public ClassWriter(final ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	/**
	 * Write a generated source file into the directory where the compiler
	 * thinks it is appropriate
	 * 
	 * @param packageName
	 * @param className
	 * @param body
	 *            the string body of the generated class. At this stage the
	 *            string should contain the final text of the class, no further
	 *            template handling is performed.
	 * @throws IOException
	 */
	public void writeSource(final String packageName, final String className,
			final String body) throws IOException {
		final String name = FromThe.string(packageName).makePrefix() + className;
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
				name, (Element[]) null);
		final Writer writer = jfo.openWriter();
		try {
			writer.write(body);
		} finally {
			writer.close();
		}
	}
}
