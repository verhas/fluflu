package com.javax0.fluflu;

import java.io.Console;

public class Out {
	private Out() {
		RuntimeException up = new RuntimeException(
				"Out is a utility class, should not be instantiated. "
						+ "Not even through reflection.");
		throw up;
	}

	private static final Console console = System.console();

	private static void out(String prefix, String s) {
		if (console != null) {
			console.printf("[%s] %s\n", prefix, s);
		}
	}

	public static void info(String s) {
		out("INFO", s);
	}

	public static void warn(String s) {
		out("WARN", s);
	}

	public static void error(String s) {
		out("ERRR", s);
	}

}
