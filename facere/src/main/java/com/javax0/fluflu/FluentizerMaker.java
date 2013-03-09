package com.javax0.fluflu;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluentizerMaker {
	final String className;
	final String src;
	final String packge;
	final String core;
	private static Logger log = LoggerFactory.getLogger(FluentizerMaker.class);

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
			log.error(
					"Resource '{}' can not be loaded. This is an internal error.",
					name);
			return null;
		}

	}

	final private static String defaultStateClassTemplate = loadTemplate("defaultStateClassTemplate.java");
	final private static String stateClassHeaderTemplate = loadTemplate("stateClassHeaderTemplate.java");
	final private static String methodTemplate = loadTemplate("methodTemplate.java");
	final private static String endMethodTemplate = loadTemplate("endMethodTemplate.java");
	final private static String defaultFirstLine = loadTemplate("defaultFirstLine.txt");

	private String fileName = null;

	private String getFullClassName() {
		final String fullClassName;
		if (packge == null) {
			fullClassName = className;
		} else {
			fullClassName = packge + "." + className;
		}
		return fullClassName;
	}

	private void constructFileName() {
		fileName = src + "/" + getFullClassName().replaceAll("\\.", "/")
				+ ".java";
	}

	/**
	 * Throw an error if the file exists, not empty and was created manually or
	 * was modified since it was created by fluflu.
	 * 
	 * @throws IOException
	 */
	public void assertFileIsIntactOrNewOrEmpty() throws IOException,
			FileModifiedException {
		File file = new File(fileName);
		if (file.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String firstLine = reader.readLine();
			if (firstLine != null) {
				assertFileIsIntact(reader, firstLine);
			}
		}
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
	private void assertFileIsIntact(BufferedReader reader, String firstLine)
			throws FileModifiedException {
		if (!firstLine.equals(defaultFirstLine)) {
			try (Scanner scanner = new Scanner(reader)) {
				String fileContent = scanner.useDelimiter(
						"somethind that does not happen even in the files")
						.next();
				if (!firstLine.equals("//" + calculateHash(fileContent))) {
					throw new FileModifiedException();
				}
			}
		}
	}

	public void createDefaultClass() throws IOException {
		try {
			assertFileIsIntactOrNewOrEmpty();
		} catch (Exception e) {
			log.error("File '{}' cannot be created.", fileName, e);
			return;
		}
		log.debug("Creating default class {}.{} to fluentize {}", packge,
				className, core);
		File file = new File(fileName);
		Writer writer = new FileWriter(file);
		writer.write(defaultFirstLine);
		writer.write("\n");
		writer.write(fillInHeaderPlaceholders(defaultStateClassTemplate));
		writer.close();
	}

	private String fillInHeaderPlaceholders(String s) {
		String filled = s.replaceAll("className", className)//
				.replaceAll("Core", core)//
				.replaceAll("timestamp", new Date().toString());
		if (packge == null) {
			filled = filled.replaceAll("package\\s+packge;\n", "");
		} else {
			filled = filled.replaceAll("packge", packge);
		}
		return filled;
	}

	public String generateStateClassHeader() {
		return fillInHeaderPlaceholders(stateClassHeaderTemplate);
	}

	public String generateStateClassMethod(TransitionEdge edge) {
		StringBuilder arglist = new StringBuilder();
		StringBuilder paramlist = new StringBuilder();
		String sep = "";
		int i = 0;
		for (Class<?> paramClass : edge.method.getParameterTypes()) {
			arglist.append(sep).append(paramClass.getCanonicalName()).append(" ")
					.append("par" + i);
			paramlist.append(sep).append("par" + i);
			sep = ", ";
			i++;
		}
		final String methodBody;
		if (edge.targetState == null) {
			methodBody = endMethodTemplate
					.replaceAll("methodname", edge.method.getName())
					.replaceAll("arglist", arglist.toString())
					.replaceAll("paramlist", paramlist.toString());
		} else {
			methodBody = methodTemplate
					.replaceAll("toState", edge.targetState.getCanonicalName())
					.replaceAll("methodname", edge.method.getName())
					.replaceAll("arglist", arglist.toString())
					.replaceAll("paramlist", paramlist.toString());
		}
		return methodBody;
	}

	public String generateStateClassFooter() {
		return "\n}";
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
			log.error("The algorithm '" + DIGEST_ALGORITHM
					+ "' is not available on this platform");
			return "";
		}
	}

	public void overwrite(String stateJavaFile, String body) throws IOException {
		try (Writer writer = new FileWriter(new File(stateJavaFile))) {
			writer.write("//" + calculateHash(body) + "\n");
			writer.write(body);
		}

	}
}
