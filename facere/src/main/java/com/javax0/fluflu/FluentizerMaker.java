package com.javax0.fluflu;

import static com.javax0.fluflu.PackagePrefixCalculator.packagePrefix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
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
	final private static String fluentClassTemplate = loadTemplate("fluentClassTemplate.java");

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
				"#methodname#", edge.method.getName(),//
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

	public String generateStateClassMethod(TransitionEdge edge) {
		StringBuilder arglist = new StringBuilder();
		StringBuilder paramlist = new StringBuilder();
		String sep = "";
		int i = 0;
		for (Class<?> paramClass : edge.method.getParameterTypes()) {
			String paramClassCanonicalName = paramClass.getCanonicalName();
			if (edge.method.isVarArgs()
					&& i == edge.method.getParameterTypes().length - 1) {
				paramClassCanonicalName = paramClassCanonicalName.replaceAll(
						"\\[\\]", "...");
			}
			if (paramClassCanonicalName.startsWith("java.lang.")) {
				paramClassCanonicalName = paramClassCanonicalName
						.substring("java.lang.".length());
			}
			arglist.append(sep).append(paramClassCanonicalName).append(" ")
					.append(createArgumentName(i));
			paramlist.append(sep).append(createArgumentName(i));
			sep = ", ";
			i++;
		}
		return replaceAllMethodParams(
				edge.targetState == null ? endMethodTemplate : methodTemplate,
				edge, arglist, paramlist);
	}

	public String generateStateClassFooter() {
		return "\n}";
	}

	public String generateFluentClass(String startState, String startMethod) {
		return fillInHeaderPlaceholders(fluentClassTemplate)//
				.replaceAll("#startState#", startState)//
				.replaceAll("#startMethod#", startMethod)//
		;
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
