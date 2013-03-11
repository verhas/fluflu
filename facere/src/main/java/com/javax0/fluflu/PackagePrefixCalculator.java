package com.javax0.fluflu;

public class PackagePrefixCalculator {
	private PackagePrefixCalculator() {
		RuntimeException up = new RuntimeException(
				"PackagePrefixCalculator is a utility class, should not be instantiated. "
						+ "Not even through reflection.");
		throw up;
	}
	
	public static String packagePrefix(String packageName) {
		return packageName == null ? "" : packageName + ".";
	}
}
