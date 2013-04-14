package com.javax0.fluflu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.javax0.aptools.FromThe;
import com.javax0.aptools.InThe;
import com.javax0.aptools.The;

public class FluentizerMaker {
	final String className;
	final String packageName;
	final String core;
	final String toBeFluentized;

	/**
	 * Create a new fluentize maker to create a fluent helper class named
	 * `className`.
	 * 
	 * @param packageName
	 *            is the package in which the classes are generated.
	 * @param className
	 *            the name of the class to generate
	 * @param core
	 *            is the name of the builder class. This is either the original
	 *            class that contains the annotation or the class that extends
	 *            the original class if the class is abstract.
	 * @throws IOException
	 */
	public FluentizerMaker(String packageName, String className, String core,
			String toBeFluentized) throws IOException {
		this.className = className;
		this.packageName = packageName;
		this.core = core;
		this.toBeFluentized = toBeFluentized;
	}

	private static String loadTemplate(String name) {
		try (InputStream is = FluentizerMaker.class.getClassLoader()
				.getResourceAsStream(name);
				Scanner scanner = new Scanner(is, "utf-8")) {
			return scanner.useDelimiter(
					"somethind that does not happen even in the files").next();
		} catch (IOException ioe) {
			Out.error("Resource '" + name
					+ "' can not be loaded. This is an internal error.");
			return null;
		}

	}

	final private static String stateClassHeaderTemplate = loadTemplate("stateClassHeaderTemplate.java");
	final private static String methodTemplate = loadTemplate("methodTemplate.java");
	final private static String endMethodTemplate = loadTemplate("endMethodTemplate.java");
	final private static String fluentClassHeaderTemplate = loadTemplate("fluentClassHeaderTemplate.java");
	final private static String startMethodTemplate = loadTemplate("startMethodTemplate.java");
	final private static String fluentMethodTemplate = loadTemplate("fluentMethodTemplate.java");

	private String fillInHeaderPlaceholders(String s) {
		String filled = InThe.string(s).replace(
		//
				"#className#", className,//
				"#Core#", core,//
				"#toBeFluentized#", toBeFluentized, //
				"#timestamp#", new Date().toString());
		if (packageName == null) {
			filled = filled.replaceAll("package\\s+packageName;\n", "");
		} else {
			filled = filled.replaceAll("#packageName#", packageName);
		}
		return filled;
	}

	public String generateStateClassHeader() {
		return fillInHeaderPlaceholders(stateClassHeaderTemplate);
	}

	private String replaceAllMethodParams(String s, TransitionEdge edge,
			String arglist, String paramlist) {
		return InThe.string(s).replace("#methodName#",
				edge.method.getSimpleName().toString(),//
				"#arglist#", arglist,//
				"#paramlist#", paramlist,//
				"#toState#", edge.targetState);
	}

	public String generateStateClassMethod(TransitionEdge edge) {
		String arglist = FromThe.method(edge.method).createArgList();
		String paramlist = FromThe.method(edge.method).createParamList();
		return replaceAllMethodParams(
				edge.targetState == null ? endMethodTemplate : methodTemplate,
				edge, arglist, paramlist);
	}

	public String generateStateClassFooter() {
		return "\n}";
	}

	public String generateFluentClassHeader(String startState,
			String startMethod) {
		return fillInHeaderPlaceholders(fluentClassHeaderTemplate);
	}

	public String generateStartMethod(String startState, String startMethod) {
		return InThe.string(startMethodTemplate).replace(//
				"#startState#", startState,//
				"#startMethod#", startMethod,//
				"#className#", className);
	}

	public String generateFluentClassFooter() {
		return "\n}";
	}

	private String generateFluentMethod(ExecutableElement methodElement) {
		String returnType = FromThe.method(methodElement).getReturnType();
		String methodName = FromThe.method(methodElement).getName();
		String arglist = FromThe.method(methodElement).createArgList();
		StringBuilder setterBody = new StringBuilder();

		List<? extends AnnotationMirror>[] parameterAnnotations = FromThe
				.method(methodElement).getParameterAnnotations();
		int i = 0;
		for (List<? extends AnnotationMirror> annotations : parameterAnnotations) {
			String par = FromThe.method(methodElement).getArgumentName(i);
			i++;
			for (AnnotationMirror annotation : annotations) {

				String fieldName = FromThe.annotation(annotation)
						.getStringValue();
				if (annotation.getAnnotationType().toString()
						.equals(AssignTo.class.getCanonicalName())) {
					setterBody.append("    ").append("core.").append(fieldName)
							.append(" = ").append(par).append(";\n");
				} else if (annotation.getAnnotationType().toString()
						.equals(AddTo.class.getCanonicalName())) {
					setterBody.append("    ").append("core.").append(fieldName)
							.append(".add(").append(par).append(");\n");
				}
				System.out.println("Annotation class= "
						+ annotation.getAnnotationType());
			}
			System.out.println("setter body " + setterBody.toString());
		}
		return InThe.string(fluentMethodTemplate).replace(//
				"#Core#", className,//
				"#returnType#", returnType,//
				"#methodName#", methodName,//
				"#arglist#", arglist,//
				"#setterBody#", setterBody.toString()//
				);
	}

	public StringBuilder generateFluentClassMethods(Element classElement) {
		StringBuilder body = new StringBuilder();
		List<ExecutableElement> methodElements = FromThe.element(classElement)
				.getMethods();
		for (ExecutableElement methodElement : methodElements) {
			if (The.method(methodElement).isAbstract()) {
				body.append(generateFluentMethod(methodElement));
			}
		}
		return body;
	}

}
