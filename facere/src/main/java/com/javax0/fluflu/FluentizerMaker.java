package com.javax0.fluflu;

import static com.javax0.fluflu.PackagePrefixCalculator.packagePrefix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

public class FluentizerMaker {
	final String className;
	final String src;
	final String packge;
	final String core;

	public FluentizerMaker(String packge, String className, String src,
			String core) throws IOException {
		this.className = className;
		this.src = src;
		this.packge = packge;
		this.core = core;
		constructFileName();
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

	private String fileName = null;

	private String getFullClassName() {
		return packagePrefix(packge) + className;
	}

	private void constructFileName() {
		fileName = src + "/" + getFullClassName().replaceAll("\\.", "/")
				+ ".java";
	}

	private String replace(String s, String... arg) {
		if (arg.length % 2 != 0)
			throw new RuntimeException(
					"replace was called with odd number of strings");
		String result = s;
		for (int i = 0; i < arg.length; i += 2) {
			result = result.replaceAll(arg[i], arg[i + 1]);
		}
		return result;
	}

	/**
	 * Throw an error if the file exists, not empty and was created manually or
	 * was modified since it was created by fluflu.
	 * 
	 * @throws IOException
	 */
	public boolean fileIsIntactOrNewOrEmpty() throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(
					new FileReader(file))) {
				String firstLine = reader.readLine();
				if (firstLine != null) {
					return fileIsIntact(reader, firstLine, fileName);
				}
			}
		}
		return true;
	}

	/**
	 * Assert that the file can be safely overwritten.
	 * 
	 * A file can be safely overwritten if it was created by the user for the
	 * very purpose or if it was generated and not edited. If the file starts
	 * with a single line Java comment containing the text 'replace it' then it
	 * was created by the user to be replaced. If the comment contains an SHA
	 * checksum converted to base64 of the rest of the lines then it was not
	 * modified since it was changed. If the checksum fails or is missing or the
	 * first line is not a comment then the file should not be overwritten. In
	 * that case the user has to delete the file manually and that is their
	 * responsibility.
	 * 
	 * @param reader
	 *            to read the rest of the lines.
	 * @param firstLine
	 *            the first line of the file that was already read
	 */
	private boolean fileIsIntact(BufferedReader reader, String firstLine,
			String fileName) {
		try (Scanner scanner = new Scanner(reader)) {
			String fileContent = scanner.useDelimiter(
					"something that does not happen ever in the files").next();
			if (!firstLine.equals("//" + calculateHash(fileContent))) {
				Out.error("The file '"
						+ fileName
						+ "' was modified since it was generated. I will not overwrite it.");
				return false;
			}
			return true;
		}
	}

	private String fillInHeaderPlaceholders(String s) {
		String filled = replace(s,//
				"#className#", className,//
				"#Core#", core,//
				"#timestamp#", new Date().toString());
		if (packge == null) {
			filled = filled.replaceAll("package\\s+packge;\n", "");
		} else {
			filled = filled.replaceAll("#packge#", packge);
		}
		return filled;
	}

	public String generateStateClassHeader() {
		return fillInHeaderPlaceholders(stateClassHeaderTemplate);
	}

	private String replaceAllMethodParams(String s, TransitionEdge edge,
			StringBuilder arglist, StringBuilder paramlist) {
		return replace(s,//
				"#methodName#", edge.method.getName(),//
				"#arglist#", arglist.toString(),//
				"#paramlist#", paramlist.toString(),//
				"#toState#", edge.targetState);
	}

	private static final String alphas = "abcdefghjkmnopqrstzwxy";

	private String createArgumentName(int i) {
		if (i < alphas.length()) {
			return alphas.substring(i, i + 1);
		} else {
			return "p" + i;
		}
	}

	private boolean thisIsTheLastParameter(Method method, int i) {
		return i == method.getParameterTypes().length - 1;
	}

	private String getClassDeclarationName(Class<?> klass) {
		String declaration = klass.getCanonicalName();
		if (declaration.startsWith("java.lang.")) {
			declaration = declaration.substring("java.lang.".length());
		}
		return declaration;
	}

	private String getMethodParameterClassDeclarationString(Method method, int i) {
		String declaration = getClassDeclarationName(method.getParameterTypes()[i]);
		if (method.isVarArgs() && thisIsTheLastParameter(method, i)) {
			declaration = declaration.replaceAll("\\[\\]", "...");
		}
		return declaration;
	}

	private StringBuilder createArgList(Method method) {
		StringBuilder arglist = new StringBuilder();
		String sep = "";
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			final String declaration = getMethodParameterClassDeclarationString(
					method, i);
			arglist.append(sep).append(declaration).append(" ")
					.append(createArgumentName(i));
			sep = ", ";
		}
		return arglist;
	}

	private StringBuilder createParamList(Method method) {
		StringBuilder paramlist = new StringBuilder();
		String sep = "";
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			paramlist.append(sep).append(createArgumentName(i));
			sep = ", ";
		}
		return paramlist;
	}

	public String generateStateClassMethod(TransitionEdge edge) {
		StringBuilder arglist = createArgList(edge.method);
		StringBuilder paramlist = createParamList(edge.method);
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
		return replace(startMethodTemplate,//
				"#startState#", startState,//
				"#startMethod#", startMethod,//
				"#className#", className);
	}

	public String generateFluentClassFooter() {
		return "\n}";
	}

	private String generateFluentMethod(Method method) {
		String returnType = getClassDeclarationName(method.getReturnType());
		String methodName = method.getName();
		String arglist = createArgList(method).toString();
		StringBuilder setterBody = new StringBuilder();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		int i = 0;
		for (Annotation[] annotations : parameterAnnotations) {
			String par = createArgumentName(i);
			i++;
			for (Annotation annotation : annotations) {
				if (annotation instanceof AssignTo) {
					String fieldName = ((AssignTo) annotation).value();
					setterBody.append("    ").append("core.").append(fieldName)
							.append(" = ").append(par).append(";\n");
				} else if (annotation instanceof AddTo) {
					String fieldName = ((AddTo) annotation).value();
					setterBody.append("    ").append("core.").append(fieldName)
							.append(".add(").append(par).append(");\n");
				}
			}
		}
		return replace(fluentMethodTemplate,//
				"#Core#", className,//
				"#returnType#", returnType,//
				"#methodName#", methodName,//
				"#arglist#", arglist,//
				"#setterBody#", setterBody.toString()//
		);
	}

	public StringBuilder generateFluentClassMethods(Class<?> klass) {
		Method[] methods = klass.getDeclaredMethods();
		StringBuilder body = new StringBuilder();
		for (Method method : methods) {
			if (Modifier.isAbstract(method.getModifiers())) {
				body.append(generateFluentMethod(method));
			}
		}
		return body;
	}

	/**
	 * Normalize the string removing the characters that are not treated as
	 * modification. Since we do this specifically for the generated state
	 * files, that do not contain any string literal all spaces are irrelevant
	 * to check modification.
	 * 
	 * @param s
	 * @return
	 */
	private String normalizeString(String s) {
		return s.replaceAll("\\s", "");
	}

	private static final String DIGEST_ALGORITHM = "SHA-512";

	private String calculateHash(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
			byte[] buffer = normalizeString(s).getBytes();
			md.update(buffer, 0, buffer.length);
			return Base64.encodeBase64String(md.digest());
		} catch (NoSuchAlgorithmException e) {
			Out.error("The algorithm '" + DIGEST_ALGORITHM
					+ "' is not available on this platform");
			return Math.random() + "";
		}
	}

	public void overwrite(String stateJavaFile, String body) throws IOException {
		try (Writer writer = new FileWriter(new File(stateJavaFile))) {
			writer.write("//" + calculateHash(body) + "\n");
			writer.write(body);
		}

	}

}
